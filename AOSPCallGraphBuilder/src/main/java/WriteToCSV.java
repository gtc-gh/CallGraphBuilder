import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.opencsv.CSVWriter;

import java.io.*;

public class WriteToCSV {

    // Write to workbook.
    public static void writeToWorkbook(Row row, int column, String content) {
        Cell c = row.createCell(column);
        c.setCellValue(content);
    }

    // Write the content from a workbook to a csv file.
    public static void workbookWriteToCSVAndClose(Workbook workbook, String filePath, int sheetNumber) throws IOException {

        FileWriter filewriter = new FileWriter(filePath, true);
        CSVWriter csvWriter = new CSVWriter(filewriter);
        Sheet sheet = workbook.getSheetAt(sheetNumber);
        for (Row row : sheet) {
            String[] csvData = new String[row.getLastCellNum()];
            int cellNum = 0;
            for (Cell cell : row)
                csvData[cellNum++] = cell.toString();
            csvWriter.writeNext(csvData);
        }
        csvWriter.close();
        filewriter.close();
        System.out.println("Successfully Write!");
    }
}
