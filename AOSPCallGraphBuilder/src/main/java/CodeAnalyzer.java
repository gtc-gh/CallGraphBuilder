import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class CodeAnalyzer {

    public static void main(String[] args) throws IOException {
        String projectPath = "C:\\Users\\gtc\\Desktop\\AOSPScourceCode\\level-28-9.0.0_r1";
        outputCSVPath = "C:\\Users\\gtc\\Desktop\\AOSPCallGraphBuilder\\cg_results\\28_calling_pairs_signature.csv";
        outputCSVPathField = "C:\\Users\\gtc\\Desktop\\AOSPCallGraphBuilder\\cg_results\\28_calling_pairs_field.csv";
        analyzeProject(projectPath);
        System.out.println("The number of unsolved API is: " + unsolvedCount);
    }
    public static String outputCSVPath = "";
    public static String outputCSVPathField = "";
    // write to a csv file
    public static Workbook wb = new XSSFWorkbook();
    public static Sheet sheet1 = wb.createSheet("signature calling pairs");
    public static Sheet sheet2 = wb.createSheet("signature to fields calling pairs");
    public static String[] columnNames = {"Caller", "Callee"};
    public static int rowNumber = 0;
    public static int sigFieldRowNumber = 0;
    public static Row row1 = sheet1.createRow(rowNumber);
    public static Row row2 = sheet2.createRow(sigFieldRowNumber);
    private static final Map<String, Set<String>> callPair = new HashMap<>();
    private static final Map<String, Set<String>> sigFieldCallPair = new HashMap<>();
    private static Set<String> importFiles = new HashSet<>();
    private static final Set<String> allFileDir = new HashSet<>();
    private static final List<String> allClassesInCurrentJavaFile = new ArrayList<>();
    private static int unsolvedCount = 0;
    public static String currentFileName = "";
    private static final int MAX_RECURSION_DEPTH = 5;
    private static final int MAX_SHEET_ROW_NUMBER = 100;

    private static void analyzeProject(String filePath) throws IOException {

        // write the first row which is column names
        for (int i = 0; i < columnNames.length; i ++) {
            WriteToCSV.writeToWorkbook(row1, i, columnNames[i]);
            WriteToCSV.writeToWorkbook(row2, i, columnNames[i]);
        }
        sigFieldRowNumber ++;
        rowNumber ++;

        // traverse each file in the folder
        Set<String> javaFiles = JavaFileExtractor.extractJavaFiles(filePath);

        int totalJavaFileNumber = javaFiles.size();

//        for (String javaFile : javaFiles) {
//            int lastDotIndex = javaFile.lastIndexOf("\\");
//            allFileDir.add(javaFile.substring(0, lastDotIndex));
//        }

        // Set up the JavaSymbolSolver with a ReflectionTypeSolver
        ReflectionTypeSolver reflectionTypeSolver = new ReflectionTypeSolver();

        // Set up a CombinedTypeSolver to combine multiple TypeSolvers if needed
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(reflectionTypeSolver);
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(filePath)));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        int currentFileNumber = 0;

        boolean runAgain = false;

        for (String file : javaFiles) {

            currentFileNumber ++;

            if (!file.equals("C:\\Users\\gtc\\Desktop\\AOSPScourceCode\\level-28-9.0.0_r1\\com\\android\\internal\\util\\function\\pooled\\PooledLambda.java")
                    && !runAgain)
                continue;
            else
                runAgain = true;

            System.out.println(file);
            System.out.println("File process: " + currentFileNumber + "/" + totalJavaFileNumber + " Sig row number: "
            + rowNumber + " Field row number: " + sigFieldRowNumber);
            currentFileName = file;

            // Parse the file
            try {
//                // 1. Get the imported files.
//                FileInputStream in_pre = new FileInputStream(file);
//                CompilationUnit cu_pre = StaticJavaParser.parse(in_pre);
////                String packageName = cu.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse("");
////                String className = cu.findFirst(ClassOrInterfaceDeclaration.class).map(NodeWithSimpleName::getNameAsString).orElse("");
//
//                // Traverse all imported files, if it is in the allFileDirectory, store it(the directory, not the java file).
//                importFiles = cu_pre.getImports().stream()
//                        .parallel()
//                        .map(NodeWithName::getNameAsString)
//                        .filter(importedClass -> importedClass.startsWith("android") || importedClass.startsWith("com"))
//                        .map(importedClass -> {
//                            int lastDotIndex = importedClass.lastIndexOf(".");
//                            return importedClass.substring(0, lastDotIndex).replace(".", "\\");
//                        })
//                        .map(importedClass -> filePath + "\\" + importedClass)
//                        .filter(allFileDir::contains)
//                        .collect(Collectors.toSet());



//                for (String importFile : importFiles) {
//                    combinedTypeSolver.add(new JavaParserTypeSolver(new File(importFile)));  // The path to directories.
//                    // System.out.println(importFile);
//                }

                // Parse the input file again with the search space limited into imported files.
                FileInputStream in = new FileInputStream(file);
                CompilationUnit cu = StaticJavaParser.parse(in);

                // Store all classes in current java file
                // Create a visitor to extract class information
//                VoidVisitor<Void> classVisitor = new ClassVisitor();
//                classVisitor.visit(cu, null);

                // Visit and print the call chain for each method
                new CodeVisitor().visit(cu, null);

                in.close();

            } catch (ParseProblemException e) {
                System.err.println("Parser error");
            }

            // print the call chain
            for (Map.Entry<String, Set<String>> entry : callPair.entrySet()) {
                for (String value : entry.getValue()) {
                    Row ithRow = sheet1.createRow(rowNumber);
                    WriteToCSV.writeToWorkbook(ithRow, 0, entry.getKey());
                    WriteToCSV.writeToWorkbook(ithRow, 1, value);
                    rowNumber ++;
                }
            }

            for (Map.Entry<String, Set<String>> entry : sigFieldCallPair.entrySet()) {
                for (String value : entry.getValue()) {
                    Row ithRow = sheet2.createRow(sigFieldRowNumber);
                    WriteToCSV.writeToWorkbook(ithRow, 0, entry.getKey());
                    WriteToCSV.writeToWorkbook(ithRow, 1, value);
                    sigFieldRowNumber ++;
                }
            }
            importFiles.clear();
            allClassesInCurrentJavaFile.clear();
            callPair.clear();
            sigFieldCallPair.clear();

            if (rowNumber >= MAX_SHEET_ROW_NUMBER || sigFieldRowNumber >= MAX_SHEET_ROW_NUMBER
            || currentFileNumber == totalJavaFileNumber) {
                // Write to CSV from workbook
                try {
                    WriteToCSV.workbookWriteToCSVAndClose(wb, outputCSVPath, 0);
                    WriteToCSV.workbookWriteToCSVAndClose(wb, outputCSVPathField, 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                wb.removeSheetAt(0);
                wb.removeSheetAt(0);
                sheet1 = wb.createSheet();
                sheet2 = wb.createSheet();
                rowNumber = 0;
                sigFieldRowNumber = 0;
            }
        }
    }

    // Get all classes in current file
    private static class ClassVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            // Process the class information as needed
            String className = n.getNameAsString();
            allClassesInCurrentJavaFile.add(className);
            super.visit(n, arg);
        }
    }


    // Visit all codes in the current file
    private static class CodeVisitor extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            String packageName = n.findCompilationUnit().map(cu -> cu.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse("")).orElse("");
            String className = n.findCompilationUnit().map(cu -> cu.findFirst(ClassOrInterfaceDeclaration.class).map(NodeWithSimpleName::getNameAsString).orElse("")).orElse("");
            String currentMethodName = packageName + "." + className + "." + n.getSignature();

            Set<String> callChain = new HashSet<>();
            Set<String> fieldsCallChain = new HashSet<>();
            MethodCallVisitor methodCallVisitor = new MethodCallVisitor(callChain, fieldsCallChain, new HashSet<>());
            methodCallVisitor.depth = 0;

            // System.out.println("Current method name is: " + currentMethodName);

            n.accept(methodCallVisitor, null);

            // Store the call chain for the method
            callPair.put(currentMethodName, callChain);
            sigFieldCallPair.put(currentMethodName, fieldsCallChain);
        }
    }


    // Visitor implementation for visiting method calls within a method
    private static class MethodCallVisitor extends VoidVisitorAdapter<List<String>> {
        private final Set<String> callChain;

        private final Set<String> fieldsCallChain;

        private final Set<MethodCallExpr> visitedNodes;  // Record the visited nodes.

        public int depth;

        public MethodCallVisitor(Set<String> callChain, Set<String> fieldsCallChain, Set<MethodCallExpr> visitedNodes) {
            this.callChain = callChain;
            this.fieldsCallChain = fieldsCallChain;
            this.visitedNodes = visitedNodes;
        }

        @Override
        public void visit(MethodCallExpr n, List<String> arg) {
            // Stop recursion if the maximum depth is reached
//            if (depth >= MAX_RECURSION_DEPTH)
//                return;
            // depth ++;
            // Log: v d i w e wtf -> stack overflow error
            String methodName = n.getNameAsString();
            if (methodName.equals("v") || methodName.equals("d") || methodName.equals("i") || methodName.equals("w")
            || methodName.equals("e") || methodName.equals("wtf"))
                return;
            if (methodName.equals("acquire") && (currentFileName.endsWith("PooledLambdaImpl.java") || currentFileName.endsWith("PooledLambda.java")))
                return;
            if (methodName.equals("acquireConstSupplier") && (currentFileName.endsWith("PooledLambda.java")))
                return;

            if (visitedNodes.contains(n))
                return; // Skip already visited nodes to avoid infinite recursion

            visitedNodes.add(n);

            // System.out.println("method name: " + n.getNameAsString());

            String methodSignature = "";
            try {
                String subMethodClass = n.resolve().declaringType().getQualifiedName();
                // Get the method signature without values
                methodSignature = getMethodSignatureWithoutValues(n);

                // System.out.println("method sig: " + methodSignature);

                callChain.add(subMethodClass + "." + methodSignature);
                // if it is in the imported classes
//                if (importMap.containsKey(subMethodClass)) {
//                    String callingMethod = subMethodClass + "." + methodSignature;  // imported signature path
//                    callChain.add(callingMethod);
//                } else {
//                    // current package and class
//                    callChain.add(packageClassName + methodSignature);
//                }
                // Continue visiting nested method calls
                super.visit(n, arg);
            } catch (Exception e) {
                // Handle the case where type resolution fails
                // Log or print an error message, or take other appropriate actions
                System.err.println("Error resolving type for method signature");
                unsolvedCount ++;
            }
        }

        @Override
        public void visit(FieldAccessExpr n, List<String> arg) {

            // String subMethodClass = n.resolve().declaringType().getQualifiedName();

            String fieldName = n.getNameAsString();
            String packageName = n.findCompilationUnit().map(cu -> cu.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse("")).orElse("");
            String className = n.findCompilationUnit().map(cu -> cu.findFirst(ClassOrInterfaceDeclaration.class).map(NodeWithSimpleName::getNameAsString).orElse("")).orElse("");
            String fieldSignature = packageName + "." + className + "." + fieldName;

            // Include the field in the call chain
            fieldsCallChain.add(fieldSignature);
            // Continue visiting nested field accesses
            super.visit(n, arg);
        }

        private String getMethodSignatureWithoutValues(MethodCallExpr methodCall) {
            StringBuilder signature = new StringBuilder();
            signature.append(methodCall.getName()).append("(");

            // Append parameters without values
            // methodCall.getArguments().forEach(arg -> signature.append(arg.calculateResolvedType().describe()).append(", "));

            // Handle exceptions that may occur during type resolution
            try {
                NodeList<Expression> arguments = methodCall.getArguments();

                for (Expression arg : arguments) {

                    try {
                        String argType = arg.calculateResolvedType().describe();
                        signature.append(argType).append(", ");
                    } catch (UnsolvedSymbolException e) {
                        // Handle the case where the type of the argument cannot be resolved
                        // You can log or print an error message, or take other appropriate actions
                        System.err.println("Error resolving type for argument: " + arg);
                    }
                }
                // Remove the trailing comma and space if arguments exist
                if (!arguments.isEmpty()) {
                    signature.delete(signature.length() - 2, signature.length());
                }
            } catch (UnsolvedSymbolException e) {
                // Handle the case where the type of the method itself cannot be resolved
                // You can log or print an error message, or take other appropriate actions
                System.err.println("Error resolving type for method call: " + methodCall);
            }

            signature.append(")");

            return signature.toString();
        }
    }
}
