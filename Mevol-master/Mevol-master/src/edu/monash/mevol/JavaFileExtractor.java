package edu.monash.mevol;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class JavaFileExtractor 
{
	public static Set<String> extractJavaFiles(String repoPath)
	{
		return getAllJavaFiles(repoPath, repoPath, "");
	}
	
	public static Set<String> extractJavaFiles(String repoPath, String prefix)
	{
		return getAllJavaFiles(repoPath, repoPath, prefix);
	}
	
	private static Set<String> getAllJavaFiles(String repoPath, String dir, String prefix)
	{
		Set<String> javaFiles = new HashSet<String>();
		
		File root = new File(dir);
		
		for (File file : root.listFiles())
		{
			if (file.isDirectory())
			{
				javaFiles.addAll(getAllJavaFiles(repoPath, file.getAbsolutePath(), prefix));
			}
			else
			{
				String path = file.getAbsolutePath();
				if (path.endsWith(".java"))
				{
					path = path.substring(path.indexOf(repoPath));
					String javaFilePath = path.replace(repoPath, "");
					
					if (prefix == null || prefix.isEmpty() || javaFilePath.startsWith(prefix))
					{
						javaFiles.add(javaFilePath);
					}
				}
			}
		}
		
		return javaFiles;
	}
}
