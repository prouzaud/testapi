package com.xxx.test.api.nrt.apinrt.campaignExecutor.model;

import com.xxx.test.api.nrt.apinrt.model.TestGroup;
import java.util.ArrayList;
import java.util.List;

public class TestGroupContext {

    private final CampaignContext campaignContext;
    private final TestGroup testGroup;
    private final List<TestContext> testContexts = new ArrayList<TestContext>();

    public TestGroupContext(CampaignContext campaignContext, TestGroup testGroup) {
        this.campaignContext = campaignContext;
        this.testGroup = testGroup;
    }

    public CampaignContext getCampaignContext() {
        return campaignContext;
    }

    public TestGroup getTestGroup() {
        return testGroup;
    }

    public List<TestContext> getTestContexts() {
        return testContexts;
    }

    public boolean isStatus() {
        return testContexts.stream().allMatch(TestContext::isStatus);
    }

    public List<TestContext> getTestsFailed() {
        return  testContexts.stream().filter(testContext -> !testContext.isStatus()).toList();
    }
}
