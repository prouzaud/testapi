package com.xxx.test.api.nrt.apinrt.campaignExecutor.exceptions;

public class ExecutionException extends RuntimeException {

    public ExecutionException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ExecutionException(String message) {
        super(message);
    }
}
