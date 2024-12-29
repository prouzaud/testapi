package com.xxx.test.api.nrt.apinrt.campaignExecutor;

import com.xxx.test.api.nrt.apinrt.campaignExecutor.model.*;
import org.springframework.stereotype.Component;

@Component
public interface Reporter {

    void campaignStarted(CampaignContext campaignContext);
    void campaignFinished(CampaignContext campaignContext);

    void testGroupStarted(TestGroupContext testGroupContext);
    void testGroupFinished(TestGroupContext testGroupContext);

    void testStarted(TestContext testContext);
    void testFinished(TestContext testContext);

    void apiCallStarted(TestContext testContext);
    void apiCallDone(TestContext testContext);
    void apiCallInvalid(TestContext testContext, String message);
    void apiCallFails(TestContext testContext, Exception e);

    void expectedStatusOk(TestContext testContext);
    void expectedStatusKo(TestContext testContext);
    void expectedBodyOk(TestContext testContext, String body);
    void expectedBodyKo(TestContext testContext, String body);
}
