package edu.monash.mevol;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class JavaFileChangeDetector 
{
	public Set<String> updatedMethods = new HashSet<String>();
	public Map<String, Integer> updatedMethods2ChangeLevel = new HashMap<String, Integer>();
	
	public void inferUpdatedMethods(FileDistiller distiller, String repoPath1, String repoPath2, String javaFilePath)
	{
		File left = new File(repoPath1 +  javaFilePath);
		File right = new File(repoPath2 + javaFilePath);
		
		distiller.extractClassifiedSourceCodeChanges(left, right);
		
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		for (SourceCodeChange change : changes)
		{
			String rootEntity = change.getRootEntity().getUniqueName();
			
			StringBuilder sb = new StringBuilder();
			sb.append(javaFilePath);
			
			if (rootEntity.endsWith(")"))
			{
				int pos = rootEntity.indexOf('(');
				String method = rootEntity.substring(0, pos);

				updatedMethods.add(method);
				updatedMethods2ChangeLevel.put(method, change.getSignificanceLevel().ordinal());
			}
		}
	}
}
