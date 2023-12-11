import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class JavaFileExtractor {
    public static Set<String> extractJavaFiles(String repoPath) {
        return getAllJavaFiles(repoPath, repoPath);
    }

    private static Set<String> getAllJavaFiles(String repoPath, String dir) {
        Set<String> javaFiles = new HashSet<>();

        File root = new File(dir);

        for (File file : root.listFiles()) {
            if (file.isDirectory())
                javaFiles.addAll(getAllJavaFiles(repoPath, file.getAbsolutePath()));
            else {
                String path = file.getAbsolutePath();
                if (path.endsWith(".java")) {
                    path = path.substring(path.indexOf(repoPath));
                    javaFiles.add(path);
                }
            }
        }

        return javaFiles;
    }
}
