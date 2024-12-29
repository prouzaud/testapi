package com.xxx.test.api.nrt.apinrt.campaignExecutor;

import com.xxx.test.api.nrt.apinrt.model.Campaign;
import com.xxx.test.api.nrt.apinrt.model.Test;
import com.xxx.test.api.nrt.apinrt.model.TestGroup;
import org.springframework.stereotype.Component;
import com.xxx.test.api.nrt.apinrt.campaignExecutor.model.*;

@Component
public class CampaignExecutor {

    final Reporter reporter;
    final ApiCaller apiCaller;

    public CampaignExecutor(Reporter reporter, ApiCaller apiCaller) {
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
        apiCaller.call(testContext);
        reporter.testFinished(testContext);
        return testContext;
    }
}
