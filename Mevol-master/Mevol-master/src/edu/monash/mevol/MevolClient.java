package edu.monash.mevol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MevolClient 
{
	public static Set<String> identicalMethods = new HashSet<String>();
	public static Set<String> newMethods = new HashSet<String>();
	public static Set<String> deletedMethods = new HashSet<String>();

	//updatedMethods is different from others
	//The item of updatedMethods is methodName:ChangeLevel
	public static Set<String> updatedMethods = new HashSet<String>();
	public static Map<String, Integer> updatedMethods2ChangeLevel = new HashMap<String, Integer>();

	public static Map<String, MethodSig> methodsRepo = new HashMap<String, MethodSig>();
	public static Map<String, MethodSig> methodsRepo2 = new HashMap<String, MethodSig>();

	public static void main(String[] args)
	{
		System.out.println(args.length);
		for (int i = 0; i < args.length; i ++) {
			System.out.println(args[i]);
		}

		Config.repoPath = args[0];
		Config.repoPath2 = args[1];

		try
		{
			JavaRepoComparison repoComp = new JavaRepoComparison();
			repoComp.compare(Config.repoPath, Config.repoPath2);

			for (String javaFilePath : repoComp.deletedFiles)
			{
				Set<MethodSig> methodSigs = JavaFileMethodExtractor.extract(Config.repoPath + javaFilePath);
				Set<String> methods = MethodSigHelper.getMethodSigNames(methodSigs);
				deletedMethods.addAll(methods);

				methodsRepo.putAll(MethodSigHelper.getMethodSigMap(methodSigs));
			}

			for (String javaFilePath : repoComp.newFiles)
			{
				Set<MethodSig> methodSigs = JavaFileMethodExtractor.extract(Config.repoPath2 + javaFilePath);
				Set<String> methods = MethodSigHelper.getMethodSigNames(methodSigs);
				newMethods.addAll(methods);

				methodsRepo2.putAll(MethodSigHelper.getMethodSigMap(methodSigs));
			}

			for (String javaFilePath : repoComp.similarFiles) {
				JavaFileComparison fileComp = new JavaFileComparison();
				fileComp.compare(Config.repoPath, Config.repoPath2, javaFilePath);

				identicalMethods.addAll(fileComp.identicalMethods);
				deletedMethods.addAll(fileComp.deletedMethods);
				newMethods.addAll(fileComp.newMethods);

				updatedMethods.addAll(fileComp.updatedMethods);
				updatedMethods2ChangeLevel.putAll(fileComp.updatedMethods2ChangeLevel);

				methodsRepo.putAll(fileComp.methodsRepo);
				methodsRepo2.putAll(fileComp.methodsRepo2);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	
}
