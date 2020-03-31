package edu.monash.mevol;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SignificanceLevel;

public class Config 
{
	public static FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
	public static String repoPath = "";
	public static String repoPath2 = "";
	
	public static int updateSignificanceLevel = SignificanceLevel.LOW.ordinal();

	public static String HelloWorldGtc = "Hello World!";
}
