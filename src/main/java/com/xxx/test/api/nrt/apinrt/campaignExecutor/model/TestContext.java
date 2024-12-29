package com.xxx.test.api.nrt.apinrt.campaignExecutor.model;

import com.xxx.test.api.nrt.apinrt.model.Test;

public class TestContext {

    private final TestGroupContext testGroupContext;
    private final Test test;
    private boolean status = true;
    private int httpStatusCode;
    private int durationInMilliseconds;

    public TestContext(TestGroupContext testGroupContext, Test test) {
        this.testGroupContext = testGroupContext;
        this.test = test;
    }

    public TestGroupContext getTestGroupContext() {
        return testGroupContext;
    }

    public Test getTest() {
        return test;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public int getDurationInMilliseconds() {
        return durationInMilliseconds;
    }

    public void setDurationInMilliseconds(int durationInMilliseconds) {
        this.durationInMilliseconds = durationInMilliseconds;
    }
}
