package com.xxx.test.api.nrt.apinrt.campaignExecutor;

import com.xxx.test.api.nrt.apinrt.campaignExecutor.model.TestContext;
import com.xxx.test.api.nrt.apinrt.model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Component
public class ApiCaller {

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private Reporter reporter;

    public ApiCaller(Reporter reporter) {
        this.reporter = reporter;
    }

    public void call(TestContext testContext) {
        Test test = testContext.getTest();
        var settings = test.callSettings();
        HttpMethod method = getMethod(settings.verb());
        if (method == null) {
            reporter.apiCallInvalid(testContext, "The given method is \""+settings.verb()+"\": Must be GET, POST, PUT, PATCH, HEAD, OPTIONS or DELETE (case not sensitive).");
        } else {
            var result = performsCall(testContext, method);
            performAssertions(testContext, result);
        }
    }

    private ResponseEntity<String> performsCall(TestContext testContext, HttpMethod method) {
        Test test = testContext.getTest();
        reporter.apiCallStarted(testContext);
        var settings = test.callSettings();
        ResponseEntity<String> result = null;
        try {
            var beginInstant = LocalDateTime.now();
            result = restTemplate.exchange(
                settings.url(),
                method,
                buildBody(settings.body()),
                String.class);
            var duration = ChronoUnit.MILLIS.between(LocalDateTime.now(), beginInstant);
            reporter.apiCallDone(testContext);
        } catch (Exception exception) {
            reporter.apiCallFails(testContext, exception);
        }
        return result;
    }

    private HttpEntity<String> buildBody(String body) {
        return new HttpEntity<>(body);
    }

    private HttpMethod getMethod(String method) {
        return switch (method.toUpperCase()) {
            case "GET" -> HttpMethod.GET;
            case "POST" -> HttpMethod.POST;
            case "PUT" -> HttpMethod.PUT;
            case "DELETE" -> HttpMethod.DELETE;
            case "PATCH" -> HttpMethod.PATCH;
            case "HEAD" -> HttpMethod.HEAD;
            case "OPTIONS" -> HttpMethod.OPTIONS;
            default -> null;
        };
    }

    private void performAssertions(TestContext testContext, ResponseEntity<String> responseEntity) {

        testContext.setHttpStatusCode(responseEntity.getStatusCode().value());
        if (testContext.getTest().expectedStatus() == testContext.getHttpStatusCode()) {
            reporter.expectedStatusOk(testContext);
        } else {
            reporter.expectedStatusKo(testContext);
            testContext.setStatus(false);
        }
        if (testContext.getTest().expectedResult().equals(responseEntity.getBody())) {
            reporter.expectedBodyOk(testContext, responseEntity.getBody());
        } else {
            reporter.expectedBodyKo(testContext, responseEntity.getBody());
            testContext.setStatus(false);
        }
    }
}
