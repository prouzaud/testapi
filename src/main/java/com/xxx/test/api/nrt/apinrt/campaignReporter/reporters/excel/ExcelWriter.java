package com.xxx.test.api.nrt.apinrt.campaignReporter.reporters.excel;

import com.xxx.test.api.nrt.apinrt.campaignReporter.exceptions.ReportException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ExcelWriter {

    @Value("${apiNrt.reportPath}")
    private String reportsRootPath;
    @Value("${apiNrt.rootPath}")
    private String inputsRootPath;
    private Workbook workbook;
    private Sheet sheet;
    private int currentLine;

    public void initializeWorkbook()  {
        this.workbook = new XSSFWorkbook();
    }

    public void closeWorkbook(String excelInputPath) {
        String excelFilePath = determineReportPath(excelInputPath);
        new File(excelFilePath).getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(excelFilePath)){
            this.workbook.write(fos);
        } catch (FileNotFoundException e) {
            throw new ReportException("Unable to create the Excel file " + excelFilePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write the Excel file: " + excelFilePath, e);
        }
    }

    public void createSheet(String sheetName) {
        this.sheet = workbook.createSheet(sheetName);
        currentLine = 0;
        writeHeader();
    }

    private void writeHeader() {
        writeRow("Line number",
                "URL Prefix",
                "Url suffix",
                "HTTP Verb",
                "Body (input)",
                "Expected HTTP status",
                "Expected HTTP response",
                "Comment",
                "Test Status",
                "HTTP code",
                "call duration",
                "HTTP body"
                );
    }

    public void writeRow(Object... values) {
        Row row = sheet.createRow(currentLine);
        for (int numCell = 0; numCell < values.length; numCell++) {
            writeCell(row, numCell, values[numCell]);
        }
    }

    private void writeCell(Row row, int numCell, Object value) {
        Cell cell = row.createCell(numCell);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private String determineReportPath(String csvInputPath) {
      File csvInputFile = new File(csvInputPath);
      String inputFileName = csvInputFile.getName();
      String inputAbstractPath = determineInputAbstractPath(csvInputPath, csvInputFile);
      Path targetDir = Paths.get("target");
      return targetDir.toAbsolutePath() + "/" + reportsRootPath + inputAbstractPath + "/report-" + inputFileName;
    }

    private String determineInputAbstractPath(String csvInputPath, File csvInputFile) {
        String inputFilePath = csvInputFile.getParentFile().getAbsolutePath();
        Path resourceDir = Paths.get("src", "test", "resources");
        String rootExamples =resourceDir.toAbsolutePath() +"/"+inputsRootPath;
        return inputFilePath.substring(rootExamples.length());
    }
}
