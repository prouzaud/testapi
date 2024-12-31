package com.xxx.test.api.nrt.apinrt.model.context;

import com.xxx.test.api.nrt.apinrt.model.configuration.Test;

public class TestContext {

    private final TestGroupContext testGroupContext;
    private final Test test;
    private boolean status = true;
    private int httpStatusCode;
    private long durationInMilliseconds;

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

    public long getDurationInMilliseconds() {
        return durationInMilliseconds;
    }

    public void setDurationInMilliseconds(long durationInMilliseconds) {
        this.durationInMilliseconds = durationInMilliseconds;
    }
}
