package edu.monash.mevol;

import java.util.HashMap;
import java.util.Map;

public class MethodSig 
{
	public boolean isPublic;
	public boolean isProtected;
	public boolean isDefault;
	public boolean isPrivate;
	
	public boolean isStatic;
	public boolean isFinal;
	public boolean isAbstract;
	public boolean isNative;
	
	public boolean isDeprecated;
	public boolean isHidden;
	public boolean isInternal;
	
	
	public String definition = "";
	public String fullName;
	
	public String comment = "";
	public String body = "";
	
	public Map<String, String> annotations = new HashMap<String, String>();

	public String rawAnnotations = "";
	
	@Override
	public String toString() {
		return "MethodSig [isPublic=" + isPublic + ", isProtected="
				+ isProtected + ", isDefault=" + isDefault + ", isPrivate="
				+ isPrivate + ", isStatic=" + isStatic + ", isFinal=" + isFinal
				+ ", isAbstract=" + isAbstract + ", isNative=" + isNative
				+ ", isDeprecated=" + isDeprecated
				+ ", isHidden=" + isHidden + ", isInternal=" + isInternal
				+ ", definition=" + definition + ", %" + comment + "%]";
	}

	public String getSignature()
	{
		return definition;
	}
	
	public void setSignature(String signature)
	{
		this.definition = signature;
	}

	public String getFullName() {
		return fullName;
	}
	
	public String getDeclaredClass()
	{
		return definition.substring(1, definition.indexOf(':'));
	}

	public String getRawAnnotations() {return rawAnnotations;}
}
