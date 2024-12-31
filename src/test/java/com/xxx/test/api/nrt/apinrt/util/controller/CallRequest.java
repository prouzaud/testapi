package com.xxx.test.api.nrt.apinrt.util.controller;

public class CallRequest {

    private long expectedDurationInMilliseconds;
    private String expectedPayload;
    private int expectedHttpStatus;

    public long getExpectedDuration() {
        return expectedDurationInMilliseconds;
    }

    public void setExpectedDuration(long expectedDurationInMilliseconds) {
        this.expectedDurationInMilliseconds = expectedDurationInMilliseconds;
    }

    public String getExpectedPayload() {
        return expectedPayload;
    }

    public void setExpectedPayload(String expectedPayload) {
        this.expectedPayload = expectedPayload;
    }

    public int getExpectedHttpStatus() {
        return expectedHttpStatus;
    }

    public void setExpectedHttpStatus(int expectedHttpStatus) {
        this.expectedHttpStatus = expectedHttpStatus;
    }
}
