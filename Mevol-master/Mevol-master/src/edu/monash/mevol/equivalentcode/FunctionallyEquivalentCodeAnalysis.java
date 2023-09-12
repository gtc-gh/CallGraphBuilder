package edu.monash.mevol.equivalentcode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ParseException;
import edu.monash.mevol.*;
import edu.monash.mevol.JavaRepoComparison;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FunctionallyEquivalentCodeAnalysis
{
    // Check whether it could be analysed. The signature is primary key and output all the other relevant information.
    public static boolean checkAnalysed(MethodSig ms1) {

        // null check
        if (ms1 == null)
            return false;

        // unallowable
        if (ms1.isPrivate || ms1.isInternal || ms1.isHidden || ms1.isAbstract || ms1.isNative) {
            return false;
        }

        return true;
    }

    // check if it is a callback function
    public static boolean checkCallback(MethodSig ms) {
        String s = ms.getFullName().substring(ms.getFullName().lastIndexOf(".") + 1);
        if (s.startsWith("On") || s.endsWith("Listeners") || s.endsWith("Callback") || s.endsWith("Callbacks"))
            return true;
        return false;
    }

    public static void methodSignatureWrite (Row row, MethodSig ms) {
        // signature, body, full name, class, comment, annotation, definition, callback
        WriteToCSV.writeToWorkbook(row, 0, ms.getSignature());
        WriteToCSV.writeToWorkbook(row, 1, ms.body);
        WriteToCSV.writeToWorkbook(row, 2, ms.getFullName());
        WriteToCSV.writeToWorkbook(row, 3, ms.getDeclaredClass());
        WriteToCSV.writeToWorkbook(row, 4, ms.comment);
        WriteToCSV.writeToWorkbook(row, 5, ms.rawAnnotations);
        if (checkCallback(ms))
            WriteToCSV.writeToWorkbook(row, 6, "True");
        else
            WriteToCSV.writeToWorkbook(row, 6, "False");
    }


    // analyse
    // method_signature1, method_signature2, the set of methods whose bodies are changed
    public static void analyse(Set<String> allMethods, Map<String, MethodSig> methodRepo,
                               String inputPath1, String outputPath1, String outputPath2)
            throws IOException, ParseException {
        // 1. Create a workbook.
        Workbook workbook = new XSSFWorkbook();

        // 2. Create two sheets
        Sheet sheet1 = workbook.createSheet("Signature");

        // Position for current csv file.
        String[] firstRow = {"Method Signature", "Method Body", "Method Full Name", "Method Class",
                "Method Comment", "Method Annotation", "Callback"};
        int row = 0;

        Row row1 = sheet1.createRow(row);

        for (int col = 0; col < firstRow.length; col ++)
            WriteToCSV.writeToWorkbook(row1, col, firstRow[col]);

        row ++;

        // 3. Output all necessary information
        for (String method : allMethods)
        {
            MethodSig ms1 = methodRepo.get(method);

            if (!checkAnalysed(ms1))
                continue;

            Row sheet1Row_i = sheet1.createRow(row);

            // retrieve all relevant information
            methodSignatureWrite(sheet1Row_i, ms1);

            row ++;  // move to next row
        }

        // 4. Write to CSV file from workbook
        WriteToCSV.workbookWriteToCSVAndClose(workbook, outputPath1);

        // create the other workbook to store the fields
        Workbook workbook2 = new XSSFWorkbook();
        workbook2 = JavaFileFieldExtractor.getFields(workbook2, inputPath1);
        WriteToCSV.workbookWriteToCSVAndClose(workbook2, outputPath2);
    }

}