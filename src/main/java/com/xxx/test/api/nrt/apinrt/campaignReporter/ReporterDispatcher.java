package com.xxx.test.api.nrt.apinrt.campaignReporter;

import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.Reporter;
import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.ReporterNotifier;
import com.xxx.test.api.nrt.apinrt.model.context.CampaignContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestContext;
import com.xxx.test.api.nrt.apinrt.model.context.TestGroupContext;
import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.PluginsEngine;
import org.springframework.stereotype.Component;

@Component
public class ReporterDispatcher implements ReporterNotifier {

    PluginsEngine pluginsEngine;

    public ReporterDispatcher(PluginsEngine pluginsEngine) {
        this.pluginsEngine = pluginsEngine;
    }

    @Override
    public void campaignStarted(CampaignContext campaignContext) {
        pluginsEngine.dispatchCall("campaignStarted", campaignContext);
    }

    @Override
    public void campaignFinished(CampaignContext campaignContext) {
        pluginsEngine.dispatchCall("campaignFinished", campaignContext);

    }

    @Override
    public void testGroupStarted(TestGroupContext testGroupContext) {
        pluginsEngine.dispatchCall("testGroupStarted", testGroupContext);

    }

    @Override
    public void testGroupFinished(TestGroupContext testGroupContext) {
        pluginsEngine.dispatchCall("testGroupFinished", testGroupContext);
    }

    @Override
    public void testStarted(TestContext testContext) {
        pluginsEngine.dispatchCall("testStarted", testContext);
    }

    @Override
    public void testFinished(TestContext testContext, String body) {
        pluginsEngine.dispatchCall("testFinished", testContext, body);
    }

    @Override
    public void apiCallStarted(TestContext testContext) {
        pluginsEngine.dispatchCall("apiCallStarted", testContext);
    }

    @Override
    public void apiCallDone(TestContext testContext) {
        pluginsEngine.dispatchCall("apiCallDone", testContext);
    }

    @Override
    public void apiCallInvalid(TestContext testContext, String message) {
        pluginsEngine.dispatchCall("apiCallInvalid", testContext, message);
    }

    @Override
    public void apiCallFails(TestContext testContext, Exception e) {
        pluginsEngine.dispatchCall("apiCallFails", testContext, e);
    }

    @Override
    public void expectedStatusOk(TestContext testContext) {
        pluginsEngine.dispatchCall("expectedStatusOk", testContext);
    }

    @Override
    public void expectedStatusKo(TestContext testContext) {
        pluginsEngine.dispatchCall("expectedStatusKo", testContext);
    }

    @Override
    public void expectedBodyOk(TestContext testContext, String body) {
        pluginsEngine.dispatchCall("expectedBodyOk", testContext, body);
    }

    @Override
    public void expectedBodyKo(TestContext testContext, String body) {
        pluginsEngine.dispatchCall("expectedBodyKo", testContext, body);
    }
}
