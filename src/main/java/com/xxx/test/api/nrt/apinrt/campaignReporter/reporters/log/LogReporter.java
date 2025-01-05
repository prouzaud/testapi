package com.xxx.test.api.nrt.apinrt.campaignReporter.reporters.log;

import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.ReportPlugin;
import com.xxx.test.api.nrt.apinrt.model.configuration.Test;
import com.xxx.test.api.nrt.apinrt.model.configuration.TestGroup;
import com.xxx.test.api.nrt.apinrt.model.context.CampaignContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestGroupContext;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LogReporter implements ReportPlugin {
    
    private final ReportLogger logger;

    public LogReporter(ReportLogger logger) {
        this.logger = logger;
    }

    @Override
    public void campaignStarted(CampaignContext campaignContext) {
        logger.initializeWriter();
        logger.logMessage(0,"The campaign execution starts.");
    }

    @Override
    public void campaignFinished(CampaignContext campaignContext) {
        logger.logMessage(0,"The campaign execution is finished.");
        if (campaignContext.isStatus()) {
            logger.logMessage(0,"The campaign has succeed (0 failure))");
        } else {
            logger.logError(0,"The campaign has failed!");
            String failedCSVs = formatFailedCsvNames(campaignContext);
            logger.logError(0,"Failed CSV files: " + failedCSVs);
        }
        logger.closeLog();
    }

    private String formatFailedCsvNames(CampaignContext campaignContext) {
        return campaignContext.getTestGroupContexts().stream()
            .filter(tgc->!tgc.isStatus())
            .map(TestGroupContext::getTestGroup)
            .map(TestGroup::name)
            .collect(Collectors.joining(", "));
    }

    @Override
    public void testGroupStarted(TestGroupContext testGroupContext) {
        TestGroup testGroup = testGroupContext.getTestGroup();
        logger.logMessage(1, "A testGroup execution starts.");
        logger.logMessage(2,"Details: Test group name: " + testGroupContext.getTestGroup().name());
        logger.logMessage(2,"Details: related CSV file: " + testGroup.filePath());
    }

    @Override
    public void testGroupFinished(TestGroupContext testGroupContext) {
        TestGroup testGroup = testGroupContext.getTestGroup();
        logger.logMessage(2,"The testGroup execution is finished ("+testGroup.name()+").");
        if (testGroupContext.isStatus()) {
            logger.logMessage(2,"The CSV file processing has succeed (0 failure))");
        } else {
            logger.logError(2,"The CSV file processing has failed!");
            String failedCSVs = formatFailedCsvLines(testGroupContext);
            logger.logError(2,"Lines in failure: " + failedCSVs);
        }
        logger.logMessage(1,"==================================");
    }

    private String formatFailedCsvLines(TestGroupContext testGroupContext) {
        return testGroupContext.getTestContexts().stream()
            .filter(tc->!tc.isStatus())
            .map(TestContext::getTest)
            .map(Test::line)
            .map(i -> i+"")
            .collect(Collectors.joining(", "));
    }

    @Override
    public void testStarted(TestContext testContext) {
        Test test = testContext.getTest();
        logger.logMessage(3,"A test execution starts.");
        logger.logMessage(4,"Details: " + test);
    }

    @Override
    public void testFinished(TestContext testContext, String body) {
        logger.logMessage(4,"The test execution is finished.");
        logger.logMessage(4,"Its status is: " +formatStatus(testContext.isStatus()));
        logger.logMessage(2,"----------------------------------");
    }

    @Override
    public void apiCallStarted(TestContext testContext) {
        Test test = testContext.getTest();
        logger.logMessage(5,"An API call starts.");
        logger.logMessage(6,"Details: " + test.callSettings());
    }

    @Override
    public void apiCallDone(TestContext testContext) {
        logger.logMessage(6,"The API call has succeed.");
    }

    @Override
    public void apiCallInvalid(TestContext testContext, String message) {
        logger.logError(6,"The API call is impossible regarding its settings.");
    }

    @Override
    public void apiCallFails(TestContext testContext, Exception e) {
        logger.logError(6,"The API call has failed (unable to perform the fail - it is not related to the http status)." + e.toString());
    }

    @Override
    public void expectedStatusOk(TestContext testContext ) {
        logger.logMessage(6,"The status matches with the expected one: " + testContext.getHttpStatusCode());
    }

    @Override
    public void expectedStatusKo(TestContext testContext) {
        String expectedStatusCode = testContext.getTest().expectedStatus();
        int foundStatusCode = testContext.getHttpStatusCode();
        logger.logError(6,"The status doesn't match with the expected one: expected=" + expectedStatusCode + ", found="+foundStatusCode);
    }

    @Override
    public void expectedBodyOk(TestContext testContext, String body) {
        logger.logMessage(6,"The body matches with the expected one.");
    }

    @Override
    public void expectedBodyKo(TestContext testContext, String body) {
        logger.logError(6,"The body doesn't match with the expected one.");
        logger.logError(6,"Received body:");
        logger.logError(7,body);
    }

    private String formatStatus(boolean status) {
        return status ? "SUCCESS" : "FAILURE";
    }

    @Override
    public void notifySpecificationError(TestContext testContext, String message) {
        logger.logError(7,"[WARNING] the specified expected status code(s) can't be fully exploited: " + message);
    }
}
