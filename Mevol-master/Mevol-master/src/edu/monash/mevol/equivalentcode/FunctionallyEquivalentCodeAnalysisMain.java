package edu.monash.mevol.equivalentcode;

import com.github.javaparser.ParseException;
import edu.monash.mevol.MevolClient;
import edu.monash.mevol.runtimepermission.RuntimePermissionMapping;
import edu.monash.mevol.CommonUtils;
import edu.monash.mevol.JavaRepoMethodExtractor;

import java.io.IOException;


/**
 * Github Java Repository Rank: https://github.com/search?l=Java&q=stars%3A%3E1&s=stars&type=Repositories
 *
 * For collecting functionally equivalent code
 */
public class FunctionallyEquivalentCodeAnalysisMain {
    public static void main(String[] args) throws IOException, ParseException {
        // Mevol
        MevolClient.main(new String[] {args[0], args[1]});

        String outputPath = "C:\\Users\\gtc\\Desktop\\test\\output.csv";

        FunctionallyEquivalentCodeAnalysis fecAnalysis = new FunctionallyEquivalentCodeAnalysis();
        fecAnalysis.analyse(MevolClient.methodsRepo, MevolClient.methodsRepo2,
                MevolClient.updatedMethods, outputPath, args[0], args[1]);

    }
}
