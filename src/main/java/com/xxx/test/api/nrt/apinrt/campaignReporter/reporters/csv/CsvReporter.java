package com.xxx.test.api.nrt.apinrt.campaignReporter.reporters.csv;

import com.xxx.test.api.nrt.apinrt.model.context.TestContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestGroupContext;
import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.ReportPlugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CsvReporter implements ReportPlugin {

    @Value("${apiNrt.csvReports.csvSeparator}")
    private String csvSeparator;

    private final CsvWriter csvWriter;

    public CsvReporter(CsvWriter csvWriter) {
        this.csvWriter = csvWriter;
    }

    @Override
    public void testGroupStarted(TestGroupContext testGroupContext) {
        csvWriter.initializeWriter(testGroupContext.getTestGroup().filePath());
    }

    @Override
    public void testGroupFinished(TestGroupContext testGroupContext) {
        csvWriter.closeCsv();
    }

    @Override
    public void testFinished(TestContext testContext, String body) {
        String line = buildCsvReportLine(testContext, body);
        csvWriter.writeLine(line);
    }

    private String buildCsvReportLine(TestContext testContext, String body) {
        return testContext.getTest().line() +
                csvSeparator +
                Arrays.stream(testContext.getTest().csvData()).collect(Collectors.joining(csvSeparator)) +
                csvSeparator +
                (testContext.isStatus() ? "SUCCESS" : "FAILURE") +
                csvSeparator +
                testContext.getHttpStatusCode() +
                csvSeparator +
                testContext.getDurationInMilliseconds() +
                csvSeparator +
                body.replaceAll(Pattern.quote("\n"), " ");
    }

}
