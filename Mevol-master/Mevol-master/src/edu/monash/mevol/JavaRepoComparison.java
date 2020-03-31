package edu.monash.mevol;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ParseException;

public class JavaRepoComparison 
{
	public Set<String> similarFiles = new HashSet<String>();
	public Set<String> newFiles = new HashSet<String>();
	public Set<String> deletedFiles = new HashSet<String>();
	
	public void compare(String repoPath1, String repoPath2) throws FileNotFoundException, ParseException 
	{
		Set<String> files1 = JavaFileExtractor.extractJavaFiles(repoPath1);
		Set<String> files2 = JavaFileExtractor.extractJavaFiles(repoPath2);
		
		for (String file : files1)
		{
			if (files2.contains(file))
			{
				similarFiles.add(file);
			}
			else
			{
				deletedFiles.add(file);
			}
		}
		
		files2.removeAll(files1);
		newFiles.addAll(files2);
	}

	
}
