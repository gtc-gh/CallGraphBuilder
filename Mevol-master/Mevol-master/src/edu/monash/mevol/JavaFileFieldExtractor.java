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

    public static Workbook getFields(Workbook workbook, String p1) {
        // Get the similar files. p1, p2 means the input path

        Sheet sheet2 = workbook.createSheet("Fields");

        int row = 0;  // record the current row number

        Row row3 = sheet2.createRow(row);

        String[] firstRow= {"Variable", "Annotation", "Modifier", "Comment"};
        for (int i = 0; i < firstRow.length; i ++)
            WriteToCSV.writeToWorkbook(row3, i, firstRow[i]);


        row++;

        // input path 1
        for (String javaFilePath : GTCClient.files) {
            // Get the fields from these similar files.
            try {
                FileInputStream in = new FileInputStream(p1 + javaFilePath);
                CompilationUnit cu = null;
                cu = JavaParser.parse(in);
                AtomicInteger finalRow = new AtomicInteger(row);
                cu.findAll(FieldDeclaration.class).forEach(field -> {
                    Row row3i = sheet2.createRow(finalRow.get());
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

        return workbook;
    }



}
