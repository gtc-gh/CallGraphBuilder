package edu.monash.mevol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GTCClient {
    public static Set<String> allMethods = new HashSet<String>();
    public static Map<String, MethodSig> methodsRepo = new HashMap<String, MethodSig>();

    public static Set<String> files = new HashSet<String>();

    public static String repoPath = "";

    public static void main(String[] args) {
        repoPath = args[0];

        // get all the java file paths
        files = JavaFileExtractor.extractJavaFiles(repoPath);

        // get all the methods
        for (String javaFilePath : files)
        {
            Set<MethodSig> methodSigs = JavaFileMethodExtractor.extract(repoPath + javaFilePath);
            Set<String> methods = MethodSigHelper.getMethodSigNames(methodSigs);
            allMethods.addAll(methods);  // store the methods

            methodsRepo.putAll(MethodSigHelper.getMethodSigMap(methodSigs));  // get all methods' information
        }
    }
}
