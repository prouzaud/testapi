package com.xxx.test.api.nrt.apinrt.campaignExecutor.model;

import com.xxx.test.api.nrt.apinrt.model.Campaign;
import java.util.ArrayList;
import java.util.List;

public class CampaignContext {

    private final Campaign campaign;
    private final List<TestGroupContext> testGroupContexts = new ArrayList<>();

    public CampaignContext(Campaign campaign) {
        this.campaign = campaign;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public List<TestGroupContext> getTestGroupContexts() {
        return testGroupContexts;
    }

    public boolean isStatus() {
        return testGroupContexts.stream().allMatch(TestGroupContext::isStatus);
    }
}
