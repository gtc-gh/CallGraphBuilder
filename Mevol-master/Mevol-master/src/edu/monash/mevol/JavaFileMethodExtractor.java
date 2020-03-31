package edu.monash.mevol;

import java.util.HashSet;
import java.util.Set;

public class JavaFileMethodExtractor
{
	public static Set<MethodSig> extract(String javaFilePath)
	{
		Set<MethodSig> sigSet = new HashSet<MethodSig>();
		
		try 
		{
			sigSet = JavaSourceVisitor.visit(javaFilePath);
		}
		catch (Exception ex)
		{
			//System.out.println("==>Java Files With Exception:" + javaFilePath);
			ex.printStackTrace();
		}

		return sigSet;
	}
}
