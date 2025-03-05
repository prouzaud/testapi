package com.xxx.test.api.nrt.apinrt.campaignReporter.reporters.csv;

import com.xxx.test.api.nrt.apinrt.campaignReporter.exceptions.ReportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class CsvWriter {
    @Value("${apiNrt.csvReports.csvSeparator}")
    private String csvSeparator;
    @Value("${apiNrt.reportPath}")
    private String reportsRootPath;
    @Value("${apiNrt.rootPath}")
    private String inputsRootPath;

    BufferedWriter writer = null;

    public void initializeWriter(String csvTestPath)  {
        String path = determineReportPath(csvTestPath);
        new File(path).getParentFile().mkdirs();
        try {
            writer = new BufferedWriter(new FileWriter(path));
        } catch (IOException e) {
            throw new ReportException("unable to create the CSV report (for "+csvTestPath+"): " + path, e);
        }
        writeLine(formatHeader());
    }

  private String determineReportPath(String csvInputPath) {
      File csvInputFile = new File(csvInputPath);
      String inputFileName = csvInputFile.getName();
      String inputAbstractPath = determineInputAbstractPath(csvInputPath, csvInputFile);
      Path targetDir = Paths.get("target");
      return targetDir.toAbsolutePath() + "/" + reportsRootPath + /*(inputAbstractPath.isEmpty() ?"":"/") +*/ inputAbstractPath + "/report-" + inputFileName;
  }

  private String determineInputAbstractPath(String csvInputPath, File csvInputFile) {
        String inputFilePath = csvInputFile.getParentFile().getAbsolutePath();
        Path resourceDir = Paths.get("src", "test", "resources");
        String rootExamples =resourceDir.toAbsolutePath() +"/"+inputsRootPath;
        return inputFilePath.substring(rootExamples.length());
  }

  private String formatHeader() {
        return  "Line number" + csvSeparator+
                "URL Prefix"+ csvSeparator +
                "URL Suffix," + csvSeparator +
                "HTTP verb"+ csvSeparator +
                "HTTP Body"+ csvSeparator +
                "Expected HTTP status"+ csvSeparator +
                "Expected HTTP response"+ csvSeparator +
                "Comment"+ csvSeparator +
                "Test Status"+ csvSeparator +
                "HTTP code"+ csvSeparator +
                "call duration"+ csvSeparator +
                "HTTP body";
  }

  public void closeCsv() {
      try {
          if (writer != null) {
              writer.close();
          }
      } catch (IOException e) {
          throw new ReportException("Error closing CSV writer", e);
      }
  }

  public void writeLine(String csvLine) {
        try {
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            throw new ReportException("Error writing line to CSV", e);
        }
    }
}
