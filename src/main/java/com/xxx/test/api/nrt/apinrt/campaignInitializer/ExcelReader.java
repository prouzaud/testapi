package com.xxx.test.api.nrt.apinrt.campaignInitializer;

import com.xxx.test.api.nrt.apinrt.campaignInitializer.exceptions.TestReaderException;
import com.xxx.test.api.nrt.apinrt.model.configuration.CallSettings;
import com.xxx.test.api.nrt.apinrt.model.configuration.Test;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Component
public class ExcelReader {

    public List<Test> createTestsFromExcel(File excelFile) {
        List<Test> tests = new ArrayList<>();
        String excelFilePath = excelFile.getAbsolutePath();
        try (InputStream inp = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(inp)) {

            int numberOfSheets = workbook.getNumberOfSheets();
            for(int i=0; i < numberOfSheets; i++) {
                tests.addAll(createTestsFromSheet(workbook, excelFilePath, i));
            }
        } catch (IOException e) {
            throw new TestReaderException("Unable to read excel file " + excelFilePath, e);
        }
        return tests;
    }

    private List<Test> createTestsFromSheet(Workbook workbook, String filePath, int sheetNumber) {

        List<Test> tests = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(sheetNumber);
        int rowNumber = 1;

        for (Row row : sheet) {
            tests.add(createTestFromRow(row, rowNumber++, filePath));
            System.out.println();
        }
        return tests;
    }

    private Test createTestFromRow(Row row, int rowNumber, String filePath) {

        CallSettings callSettings = createCallSettings(row);
        String expectedStatus = readString(row.getCell(4));
        String expectedResult = readString(row.getCell(5));
        String sheetName = row.getSheet().getSheetName();
        String[] data = rowToStringArray(row);
        return new Test(filePath, rowNumber, data, callSettings, expectedStatus, expectedResult, sheetName);
    }

    private CallSettings createCallSettings(Row row) {

        String url = readString(row.getCell(0)) + readString(row.getCell(1));
        String verb = readString(row.getCell(2));
        String body = readString(row.getCell(3));
        return new CallSettings(url, verb, body);
    }

    private String[] rowToStringArray(Row row) {
        String[] data = new String[6];
        for (int i = 0; i < row.getLastCellNum(); i++) {
            data[i] = readString(row.getCell(i));
        }
        return data;
    }

    private int readInteger(Cell cell) {
        if (!cell.getCellType().equals(CellType.NUMERIC)) {
            throw new TestReaderException(formatCellPath(cell) + ": Integer value expected, the found value is: " + cell.getStringCellValue());
        }
        double doubleValue = cell.getNumericCellValue();
        return (int) doubleValue;
    }

    private String readString(Cell cell) {
        return cell.getStringCellValue();
    }

    private String formatCellPath(Cell cell) {
        return cell.getSheet().getSheetName()+", row: "+cell.getRow().getRowNum()+", column: "+cell.getColumnIndex();
    }
}