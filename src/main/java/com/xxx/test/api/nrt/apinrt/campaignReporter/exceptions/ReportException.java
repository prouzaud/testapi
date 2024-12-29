package com.xxx.test.api.nrt.apinrt.campaignReporter.exceptions;

public class ReportException extends RuntimeException {

    public ReportException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ReportException(String message) {
        super(message);
    }
}
