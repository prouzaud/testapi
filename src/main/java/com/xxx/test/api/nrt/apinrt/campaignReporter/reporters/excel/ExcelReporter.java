package com.xxx.test.api.nrt.apinrt.campaignReporter.reporters.excel;

import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.ReportPlugin;
import com.xxx.test.api.nrt.apinrt.model.configuration.TestGroupType;
import com.xxx.test.api.nrt.apinrt.model.context.TestContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestGroupContext;
import org.springframework.stereotype.Component;

//Not Thread safe.
@Component
public class ExcelReporter implements ReportPlugin {

    private final ExcelWriter excelWriter;
    private String lastSheetName = null;

    public ExcelReporter(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
    }

    @Override
    public void testGroupStarted(TestGroupContext testGroupContext) {
        if (testGroupContext.getTestGroup().type() == TestGroupType.EXCEL) {
            excelWriter.initializeWorkbook();
        }
    }

    @Override
    public void testGroupFinished(TestGroupContext testGroupContext) {
        if (testGroupContext.getTestGroup().type() == TestGroupType.EXCEL) {
            excelWriter.closeWorkbook(testGroupContext.getTestGroup().filePath());
            lastSheetName = null;
        }
    }

    @Override
    public void testFinished(TestContext testContext, String body) {
        if (testContext.getTestGroupContext().getTestGroup().type() == TestGroupType.EXCEL) {
            changeSheetNameIfNeeded(testContext.getTest().sheetName());
            writeLine(testContext, body);
        }
    }

    private void changeSheetNameIfNeeded(String sheetName) {
        if (!sheetName.equals(lastSheetName)) {
            excelWriter.createSheet(sheetName);
            lastSheetName = sheetName;
        }
    }

    private void writeLine(TestContext testContext, String body) {
        var inputData = testContext.getTest().csvData();
        int numberOfInputs = inputData.length;
        Object[] values = new Object[numberOfInputs + 4];
        System.arraycopy(inputData, 0, values, 0, inputData.length);
        values[numberOfInputs] = testContext.getHttpStatusCode();
        values[numberOfInputs + 1] = testContext.getHttpStatusCode();
        values[numberOfInputs + 2] = testContext.getDurationInMilliseconds();
        values[numberOfInputs + 3] = body;
        excelWriter.writeRow(values);
    }
}
