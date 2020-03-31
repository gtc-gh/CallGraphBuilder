package edu.monash.mevol;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JavaRepoMethodExtractor 
{
	public static Map<String, MethodSig> extractMethods(String repoPath)
	{
		Map<String, MethodSig> methodsRepo = new HashMap<String, MethodSig>();
		
		try
		{
			Set<String> files = JavaFileExtractor.extractJavaFiles(repoPath);
			for (String javaFilePath : files)
			{
				Set<MethodSig> methodSigs = JavaFileMethodExtractor.extract(repoPath + File.separator + javaFilePath);
				methodsRepo.putAll(MethodSigHelper.getMethodSigMap(methodSigs));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return methodsRepo;
	}
}
