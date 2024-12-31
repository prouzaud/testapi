package com.xxx.test.api.nrt.apinrt.campaignExecutor;

import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.ReporterNotifier;
import com.xxx.test.api.nrt.apinrt.model.configuration.Campaign;
import com.xxx.test.api.nrt.apinrt.model.configuration.Test;
import com.xxx.test.api.nrt.apinrt.model.configuration.TestGroup;
import com.xxx.test.api.nrt.apinrt.model.context.CampaignContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestGroupContext;
import org.springframework.stereotype.Component;

@Component
public class CampaignExecutor {

    final ReporterNotifier reporter;
    final ApiCaller apiCaller;

    public CampaignExecutor(ReporterNotifier reporter, ApiCaller apiCaller) {
        this.reporter = reporter;
        this.apiCaller = apiCaller;
    }

    public void executeCampaign(Campaign campaign) {
        CampaignContext campaignContext = new CampaignContext(campaign);
        reporter.campaignStarted(campaignContext);
        campaign.testGroups().forEach(testGroup->campaignContext.getTestGroupContexts().add(executeTestGroup(campaignContext, testGroup)));
        reporter.campaignFinished(campaignContext);
    }

    private TestGroupContext executeTestGroup(CampaignContext campaignContext, TestGroup testGroup) {
        TestGroupContext testGroupContext = new TestGroupContext(campaignContext, testGroup);
        reporter.testGroupStarted(testGroupContext);
        testGroup.tests().forEach(test->testGroupContext.getTestContexts().add(executeTest(testGroupContext, test)));
        reporter.testGroupFinished(testGroupContext);
        return testGroupContext;
    }

    private TestContext executeTest(TestGroupContext testGroupContext, Test test) {
        TestContext testContext = new TestContext(testGroupContext, test);
        reporter.testStarted(testContext);
        String body = apiCaller.call(testContext);
        reporter.testFinished(testContext, body);
        return testContext;
    }
}
