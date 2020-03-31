package edu.monash.mevol;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;

public class WriteToCSV {

    // Write to workbook.
    public static void writeToWorkbook(Row row, int column, String content) {
        Cell c = row.createCell(column);
        c.setCellValue(content);
    }

    // Write the content from a workbook to a csv file.
    public static void workbookWriteToCSVAndClose(Workbook workbook, String filePath) throws IOException {
        BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(filePath));
        workbook.write(fileOutput);
        fileOutput.close();
        workbook.close();
    }

}
