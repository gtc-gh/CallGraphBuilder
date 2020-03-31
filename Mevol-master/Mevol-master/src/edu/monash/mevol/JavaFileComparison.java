package edu.monash.mevol;

import com.github.javaparser.ParseException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JavaFileComparison 
{
	public Set<String> identicalMethods = new HashSet<String>();
	public Set<String> newMethods = new HashSet<String>();
	public Set<String> deletedMethods = new HashSet<String>();

	public Map<String, MethodSig> methodsRepo = new HashMap<String, MethodSig>();
	public Map<String, MethodSig> methodsRepo2 = new HashMap<String, MethodSig>();
	
	public Set<String> updatedMethods = new HashSet<String>();
	public Map<String, Integer> updatedMethods2ChangeLevel = new HashMap<String, Integer>();

	public void compare(String repoPath1, String repoPath2, String javaFilePath) throws FileNotFoundException, ParseException 
	{
		Set<MethodSig> methodSig1 = JavaFileMethodExtractor.extract(repoPath1 + javaFilePath);
		Set<MethodSig> methodSig2 = JavaFileMethodExtractor.extract(repoPath2 + javaFilePath);

		Set<String> methods1 = MethodSigHelper.getMethodSigNames(methodSig1);
		Set<String> methods2 = MethodSigHelper.getMethodSigNames(methodSig2);

		methodsRepo = MethodSigHelper.getMethodSigMap(methodSig1);
		methodsRepo2 = MethodSigHelper.getMethodSigMap(methodSig2);

		JavaFileChangeDetector updateDetector = new JavaFileChangeDetector();
		updateDetector.inferUpdatedMethods(Config.distiller, repoPath1, repoPath2, javaFilePath);
		updatedMethods.addAll(updateDetector.updatedMethods);
		updatedMethods2ChangeLevel.putAll(updateDetector.updatedMethods2ChangeLevel);
		
		for (String method : methods1)
		{
			if (methods2.contains(method))
			{
				if (! updatedMethods.contains(method))
					identicalMethods.add(method);
			}
			else
			{
				deletedMethods.add(method);
			}
		}
		
		methods2.removeAll(methods1);
		newMethods.addAll(methods2);
	}
}
