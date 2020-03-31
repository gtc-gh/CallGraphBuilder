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
    public static boolean checkAnalysed(MethodSig ms1, MethodSig ms2) {

        // null check
        if ((ms1 == null) || (ms2 == null))
            return false;

        // primary key
        if (!ms1.getSignature().equals(ms2.getSignature()))
            return false;

        // unallowable
        if (ms1.isPrivate || ms1.isInternal || ms1.isHidden || ms1.isAbstract || ms1.isNative ||
                ms2.isPrivate || ms2.isInternal || ms2.isHidden || ms2.isAbstract || ms2.isNative) {
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
        WriteToCSV.writeToWorkbook(row, 3, String.valueOf(ms.getClass()));
        WriteToCSV.writeToWorkbook(row, 4, ms.comment);
        WriteToCSV.writeToWorkbook(row, 5, ms.rawAnnotations);
        if (checkCallback(ms))
            WriteToCSV.writeToWorkbook(row, 6, "True");
        else
            WriteToCSV.writeToWorkbook(row, 6, "False");
    }


    // analyse
    // method_signature1, method_signature2, the set of methods whose bodies are changed
    public static void analyse(Map<String, MethodSig> methodRepo, Map<String, MethodSig> methodRepo2,
                          Set<String> updatedMethods, String outputPath, String inputPath1, String inputPath2)
            throws IOException, ParseException {
        // 1. Create a workbook.
        Workbook workbook = new XSSFWorkbook();

        // 2. Create two sheets
        Sheet sheet1 = workbook.createSheet("VersionASignature");
        Sheet sheet2 = workbook.createSheet("VersionBSignature");

        // Position for current csv file.
        String[] firstRow = {"Signature", "Body", "Full Name", "Class", "Comment", "Annotation", "Callback"};
        int row = 0;

        Row row1 = sheet1.createRow(row);
        Row row2 = sheet2.createRow(row);

        for (int col = 0; col < firstRow.length; col ++) {
            WriteToCSV.writeToWorkbook(row1, col, firstRow[col]);
            WriteToCSV.writeToWorkbook(row2, col, firstRow[col]);
        }
        row ++;

        // 3. Output all necessary information
        for (String method : updatedMethods)
        {
            MethodSig ms1 = methodRepo.get(method);
            MethodSig ms2 = methodRepo2.get(method);

            if (!checkAnalysed(ms1, ms2))
                continue;

            Row sheet1Row_i = sheet1.createRow(row);
            Row sheet2Row_i = sheet2.createRow(row);

            // retrieve all relevant information
            methodSignatureWrite(sheet1Row_i, ms1);
            methodSignatureWrite(sheet2Row_i, ms2);

            row ++;  // move to next row
        }

        // create the other two sheets to store the fields
        workbook = JavaFileFieldExtractor.getFields(workbook, inputPath1, inputPath2);

        // 4. Write to CSV file from workbook
        WriteToCSV.workbookWriteToCSVAndClose(workbook, outputPath);
    }

}