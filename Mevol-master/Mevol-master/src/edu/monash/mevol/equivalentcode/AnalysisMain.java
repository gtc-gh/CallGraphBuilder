package edu.monash.mevol.equivalentcode;

import com.github.javaparser.ParseException;
import edu.monash.mevol.GTCClient;

import java.io.IOException;


/**
 * Github Java Repository Rank: https://github.com/search?l=Java&q=stars%3A%3E1&s=stars&type=Repositories
 *
 * For collecting functionally equivalent code
 */
public class AnalysisMain {
    public static void main(String[] args) throws IOException, ParseException {
        // Mevol
        //MevolClient.main(new String[] {args[0], args[1]});
        GTCClient.main(new String[] {args[0]});

        String outputPath1 = "C:\\Users\\gtc\\Desktop\\test\\outputMethods.csv";
        String outputPath2 = "C:\\Users\\gtc\\Desktop\\test\\outputFields.csv";

        FunctionallyEquivalentCodeAnalysis fecAnalysis = new FunctionallyEquivalentCodeAnalysis();
        fecAnalysis.analyse(GTCClient.allMethods, GTCClient.methodsRepo, args[0], outputPath1, outputPath2);

    }
}
