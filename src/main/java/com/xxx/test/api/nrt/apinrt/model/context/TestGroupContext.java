package com.xxx.test.api.nrt.apinrt.model.context;

import com.xxx.test.api.nrt.apinrt.model.configuration.TestGroup;
import java.util.ArrayList;
import java.util.List;

public class TestGroupContext {

    private final CampaignContext campaignContext;
    private final TestGroup testGroup;
    private final List<TestContext> testContexts = new ArrayList<>();

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
}
