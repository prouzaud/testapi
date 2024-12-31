package com.xxx.test.api.nrt.apinrt.campaignExecutor;

import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.ReporterNotifier;
import com.xxx.test.api.nrt.apinrt.model.context.TestContext;
import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.Reporter;
import com.xxx.test.api.nrt.apinrt.model.configuration.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Component
public class ApiCaller {

    RestTemplate restTemplate = new RestTemplate();

    final private ReporterNotifier reporter;

    public ApiCaller(ReporterNotifier reporter) {
        this.reporter = reporter;
    }

    public String call(TestContext testContext) {
        Test test = testContext.getTest();
        var settings = test.callSettings();
        HttpMethod method = getMethod(settings.verb());
        if (method == null) {
            reporter.apiCallInvalid(testContext, "The given method is \""+settings.verb()+"\": Must be GET, POST, PUT, PATCH, HEAD, OPTIONS or DELETE (case not sensitive).");
            return "";
        } else {
            var result = performsCall(testContext, method);
            if (result != null) {
                performAssertions(testContext, result);
                return result.getBody();
            } else {
                updateContextOnError(testContext);
                return "<no response>";
            }

        }
    }

    private void updateContextOnError(TestContext testContext) {
        testContext.setHttpStatusCode(0);
        testContext.setStatus(false);
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
            var duration = ChronoUnit.MILLIS.between(beginInstant, LocalDateTime.now());
            testContext.setDurationInMilliseconds(duration);
            reporter.apiCallDone(testContext);
        } catch (Exception exception) {
            reporter.apiCallFails(testContext, exception);
        }
        return result;
    }

    private HttpEntity<String> buildBody(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }


    private HttpMethod getMethod(String method) {
        return switch (method.toUpperCase()) {
            case "GET" -> HttpMethod.GET;
            case "POST" -> HttpMethod.POST;
            case "PUT" -> HttpMethod.PUT;
            case "DELETE" -> HttpMethod.DELETE;
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
