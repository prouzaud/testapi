package com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine;

import com.xxx.test.api.nrt.apinrt.model.context.CampaignContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestGroupContext;
import org.springframework.stereotype.Component;

@Component
public interface Reporter  {

    default void campaignStarted(CampaignContext campaignContext) {}
    default void campaignFinished(CampaignContext campaignContext) {}

    default void testGroupStarted(TestGroupContext testGroupContext) {}
    default void testGroupFinished(TestGroupContext testGroupContext) {}

    default void testStarted(TestContext testContext) {}
    default void testFinished(TestContext testContext, String body) {}

    default void apiCallStarted(TestContext testContext) {}
    default void apiCallDone(TestContext testContext) {}
    default void apiCallInvalid(TestContext testContext, String message) {}
    default void apiCallFails(TestContext testContext, Exception e) {}

    default void expectedStatusOk(TestContext testContext) {}
    default void expectedStatusKo(TestContext testContext) {}
    default void expectedBodyOk(TestContext testContext, String body) {}
    default void expectedBodyKo(TestContext testContext, String body) {}

    default void notifySpecificationError(TestContext testContext, String message) {}
}
