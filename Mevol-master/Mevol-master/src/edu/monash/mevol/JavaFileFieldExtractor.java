package edu.monash.mevol;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaFileFieldExtractor {

    public static Workbook getFields(Workbook workbook, String p1, String p2) {
        // Get the similar files. p1, p2 means the input path
        Config.repoPath = p1;
        Config.repoPath2 = p2;
        JavaRepoComparison repoComp = new JavaRepoComparison();
        try {
            repoComp.compare(Config.repoPath, Config.repoPath2);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Sheet sheet3 = workbook.createSheet("VersionAFields");
        Sheet sheet4 = workbook.createSheet("VersionBFields");

        int row = 0;  // record the current row number

        Row row3 = sheet3.createRow(row);
        Row row4 = sheet4.createRow(row);

        String[] firstRow= {"Variable", "Annotation", "Modifier", "Comment"};
        for (int i = 0; i < firstRow.length; i ++) {
            WriteToCSV.writeToWorkbook(row3, i, firstRow[i]);
            WriteToCSV.writeToWorkbook(row4, i, firstRow[i]);
        }

        row++;

        // input path 1
        for (String javaFilePath : repoComp.similarFiles) {
            // Get the fields from these similar files.
            try {
                FileInputStream in = new FileInputStream(p1 + javaFilePath);
                CompilationUnit cu = null;
                cu = JavaParser.parse(in);
                AtomicInteger finalRow = new AtomicInteger(row);
                cu.findAll(FieldDeclaration.class).forEach(field -> {
                    Row row3i = sheet3.createRow(finalRow.get());
                    WriteToCSV.writeToWorkbook(row3i, 0, field.getVariables().toString());
                    WriteToCSV.writeToWorkbook(row3i, 1, field.getAnnotations().toString());
                    WriteToCSV.writeToWorkbook(row3i, 2, field.getModifiers().toString());
                    WriteToCSV.writeToWorkbook(row3i, 3, field.getComment().toString());
                    finalRow.getAndIncrement();
                });
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // input path 2
        for (String javaFilePath : repoComp.similarFiles) {
            // Get the fields from these similar files.
            try {
                FileInputStream in = new FileInputStream(p2 + javaFilePath);
                CompilationUnit cu = null;
                cu = JavaParser.parse(in);
                AtomicInteger finalRow1 = new AtomicInteger(row);
                cu.findAll(FieldDeclaration.class).forEach(field -> {
                    Row row4i = sheet4.createRow(finalRow1.get());
                    WriteToCSV.writeToWorkbook(row4i, 0, field.getVariables().toString());
                    WriteToCSV.writeToWorkbook(row4i, 1, field.getAnnotations().toString());
                    WriteToCSV.writeToWorkbook(row4i, 2, field.getModifiers().toString());
                    WriteToCSV.writeToWorkbook(row4i, 3, field.getComment().toString());
                    finalRow1.getAndIncrement();
                });
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            row ++;
        }

        return workbook;
    }



}
