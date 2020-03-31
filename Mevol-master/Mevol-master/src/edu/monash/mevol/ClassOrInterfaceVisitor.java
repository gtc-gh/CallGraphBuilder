package edu.monash.mevol;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * To represent an API in a way that is consistent between MethodExtractor and Mining4U projects.
 * 	1) Parameters
 * 	2) Comments inside method signatures
 * 
 * @author li.li
 *
 */
public class ClassOrInterfaceVisitor extends VoidVisitorAdapter<Object> 
{
	private String pkgName;
	private String clsName;
	private Set<MethodSig> methodSigSet = new HashSet<MethodSig>();
	
	public ClassOrInterfaceVisitor(String pkgName)
	{
		this.pkgName = pkgName;
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration c, Object arg) 
	{
		this.clsName = c.getNameAsString();
		
		String clsComment = "";
		boolean isDeprecated = false;
		boolean isHidden = false;
		
		Optional<Comment> optional = c.getComment();
		if (! optional.isEmpty())
		{
			clsComment = optional.get().toString();
			
			if (clsComment.contains("@hide"))
			{
				isHidden = true;
			}
			else if (clsComment.contains("@deprecated"))
			{
				isDeprecated = true;
			}
		}

		for (AnnotationExpr ae : c.getAnnotations())
		{
			String annotationName = ae.getNameAsString();
			
			if ("Deprecated".equals(annotationName))
			{
				isDeprecated = true;
			}
			else if ("RequiresPermission".contentEquals(annotationName))
			{
				//NOTHING AT THE MOMENT
			}
		}
		
		for (Node node : c.getChildNodes())
		{
			if (node instanceof ClassOrInterfaceDeclaration)
			{
				ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) node;
				
				ClassOrInterfaceVisitor clsVisitor = new ClassOrInterfaceVisitor(pkgName + "." + clsName);
				
				clsVisitor.visit(cid, arg);
				methodSigSet.addAll(clsVisitor.getMethodSigSet());
				
				continue;
			}
			
			MethodSig methodSig = new MethodSig();
			
			if (pkgName.contains(".internal"))
			{
				methodSig.isInternal = true;
			}
			if (isDeprecated)
			{
				methodSig.isDeprecated = true;
			}
			if (isHidden)
			{
				methodSig.isHidden = true;
			}
			
			if (node instanceof MethodDeclaration)
			{
    			MethodDeclaration md = (MethodDeclaration) node;
    			methodSig.definition = toMethodSignature(md);
    			methodSig.fullName = toMethodFullName(md);

				// get the annotations
				if (md.getAnnotations().isNonEmpty()) {
					methodSig.rawAnnotations = md.getAnnotations().toString();
				}
    			
    			Optional<BlockStmt> optionalBody = md.getBody();
    			if (! optionalBody.isEmpty())
    			{
    				methodSig.body = optionalBody.get().toString();
    			}
    			
    			EnumSet<com.github.javaparser.ast.Modifier> modifiers = md.getModifiers();
    			
    			for (com.github.javaparser.ast.Modifier m : modifiers)
    			{
    				String modifierStr = m.asString();
    				methodSig = checkModifiers(methodSig, modifierStr);
    			}

    			if (isNoneOfModifiers(methodSig)) {
    				methodSig.isDefault = true;
    			}

    			Optional<Comment> optionalComment = md.getComment();
    			if (! optionalComment.isEmpty())
    			{
    				
    				methodSig = checkAndAssignComment(methodSig, optionalComment.get());
    			}
    			else
    			{
    				methodSig.comment = "";
    			}
    			
    			for (AnnotationExpr ae : md.getAnnotations())
    			{
    				String annotationName = ae.getNameAsString();
    				
    				
    				if ("Deprecated".equals(annotationName))
    				{
    					if (methodSig.isDeprecated == false)
    					{
    						methodSig.isDeprecated = true;
    						
    						//There are some inconsistencies within the Android framework code base
    						//System.out.println(methodSig.definition + methodSig.comment);
    					}
    				}
    				else if ("RequiresPermission".contentEquals(annotationName))
    				{
    					if (ae instanceof SingleMemberAnnotationExpr)
    					{
    						SingleMemberAnnotationExpr smae = (SingleMemberAnnotationExpr) ae;
    						
    						methodSig.annotations.put("RequiresPermission", smae.getMemberValue().toString());
    					}
    					if (ae instanceof NormalAnnotationExpr)
    					{
    						NormalAnnotationExpr nae = (NormalAnnotationExpr) ae;

    						if (nae.getPairs().size() == 1)
    						{
    							methodSig.annotations.put("RequiresPermission", nae.getPairs().get(0).toString());
    						}
    						else
    						{
    							String permStr = "";
    							
    							for (MemberValuePair mvp : nae.getPairs())
    							{
    								if (mvp.toString().contains("conditional"))
    								{
    									permStr = "conditional" + permStr;
    								}
    								else
    								{
    									permStr = permStr + mvp.toString();
    								}
    							}
    							
    							methodSig.annotations.put("RequiresPermission", permStr);
    						}
    					}
    				}
    			}
    		}
    		else if (node instanceof ConstructorDeclaration)
    		{

    			ConstructorDeclaration cd = (ConstructorDeclaration) node;
    			methodSig.definition = toConstructorSignature(cd);
    			methodSig.fullName = pkgName + "." + clsName + ".<init>";


				// get annotations
				if (cd.getAnnotations().isNonEmpty()) {
					methodSig.rawAnnotations = cd.getAnnotations().toString();
				}


    			EnumSet<com.github.javaparser.ast.Modifier> modifiers = cd.getModifiers();
    			for (com.github.javaparser.ast.Modifier m : modifiers)
    			{
    				String modifierStr = m.asString();
    				methodSig = checkModifiers(methodSig, modifierStr);
    			}

    			if (isNoneOfModifiers(methodSig)) {
    				methodSig.isDefault = true;
    			}

    			Optional<Comment> optionalComment = cd.getComment();
    			if (! optionalComment.isEmpty())
    			{
    				
    				methodSig = checkAndAssignComment(methodSig, optionalComment.get());
    			}
    			else
    			{
    				methodSig.comment = "";
    			}
    		}
			
			if (! methodSig.definition.isEmpty())
			{
				methodSigSet.add(methodSig);
			}
		}
	}
	
	public String toMethodFullName(MethodDeclaration md)
	{
		return pkgName + "." + clsName + "." + md.getName();
	}
	
	public String toMethodSignature(MethodDeclaration md)
	{		
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(pkgName + "." + clsName);
		sb.append(": ");
		sb.append(simplify(md.getType().toString()));
		sb.append(" ");
		sb.append(md.getName());
		
		String params = "(";
		for (Parameter p : md.getParameters())
		{
			if ("(".equals(params))
			{
				params = params + simplify(p.getType().toString());
			}
			else
			{
				params = params + "," + simplify(p.getType().toString());
			}
		}
		params = params + ")";
		
		//String mdStr = md.getDeclarationAsString();
		//int paramPos = mdStr.lastIndexOf('(');
		//String params = mdStr.substring(paramPos, 1+mdStr.lastIndexOf(')'));
		
		sb.append(params);
		sb.append(">");
		
		return sb.toString();
	}
	
	public String toConstructorSignature(ConstructorDeclaration cd)
	{		
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(pkgName + "." + clsName);
		sb.append(": ");
		sb.append("void");
		sb.append(" ");
		sb.append("<init>");
		
		String params = "(";
		for (Parameter p : cd.getParameters())
		{
			if ("(".equals(params))
			{
				params = params + simplify(p.getType().toString());
			}
			else
			{
				params = params + "," + simplify(p.getType().toString());
			}
		}
		params = params + ")";
		
		sb.append(params);
		sb.append(">");
		
		return sb.toString();
	}
	
	public MethodSig checkModifiers(MethodSig mf, String modifierStr)
	{
		if (modifierStr.contains("public"))
		{
			mf.isPublic = true;
		}
		else if (modifierStr.contains("protected"))
		{
			mf.isProtected = true;
		}
		else if (modifierStr.contains("private"))
		{
			mf.isPrivate = true;
		}
		else
		{
			if (!mf.isPublic && !mf.isProtected && !mf.isPrivate)
			{
				mf.isDefault = true;
			}
			else
			{
				mf.isDefault = false;
			}
			
		}
		
		if (modifierStr.contains("static"))
		{
			mf.isStatic = true;
		}
		
		if (modifierStr.contains("abstract"))
		{
			mf.isAbstract = true;
		}
		
		if (modifierStr.contains("final"))
		{
			mf.isFinal = true;
		}
		
		if (modifierStr.contains("native"))
		{
			mf.isNative = true;
		}
		
		return mf;
	}

	public static String simplify(String type)
	{
		String typeStr = type;
		String REGEX = "(/\\*{1,2}[\\s\\S]*?\\*/)([\\f\\r\\t\\v]*?)([\\n])";
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(type);
		if (m.find()) {
			typeStr = m.replaceAll("");
		}
		String[] strs = typeStr.split("\\.");
		return strs[strs.length-1];
	}
	
	public MethodSig checkAndAssignComment(MethodSig mf, Comment comment)
	{
		if (null != comment)
		{
			mf.comment = comment.toString();
			
			if (mf.comment.contains("@hide"))
			{
				mf.isHidden = true;
			}
			
			if (mf.comment.contains("@deprecated"))
			{
				mf.isDeprecated = true;
			}
		}
		
		return mf;
	}
	
	public Set<MethodSig> getMethodSigSet() {
		return methodSigSet;
	}
	
	public void setMethodSigSet(Set<MethodSig> methodSigSet) {
		this.methodSigSet = methodSigSet;
	}

	public static boolean isNoneOfModifiers(MethodSig sig) {
		boolean isNone = true;
		if (sig.isPrivate || sig.isDefault || sig.isPublic || sig.isProtected) {
			isNone = false;
		}
		return isNone;
	}
}
