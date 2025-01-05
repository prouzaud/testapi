package com.xxx.test.api.nrt.apinrt.campaignExecutor.checker;

import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.ReporterNotifier;
import com.xxx.test.api.nrt.apinrt.model.context.TestContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StatusCodeChecker {

    public boolean matches(ReporterNotifier reporterNotifier, TestContext testContext, String expectedStatus, int actualStauts) {
        List<Integer> possibleValues = decodeExpectedStatusList(reporterNotifier, testContext, expectedStatus);
        return possibleValues.stream().anyMatch(i->i.equals(actualStauts));
    }

    private static List<Integer> decodeExpectedStatusList(ReporterNotifier reporterNotifier, TestContext testContext, String expectedStatus) {
        List<Integer> possibleValues = new ArrayList<>();
        String[] expectedStatusCodes = expectedStatus.split(",");
        for (String expectedStatusCode : expectedStatusCodes) {
            try {
                int statusCode = Integer.parseInt(expectedStatusCode.trim());
                if (statusCode < 1 || statusCode > 599) {
                    reporterNotifier.notifySpecificationError(testContext, "ExpectedStatus must be an integer between 200 and 599 or some integers separed by a coma (example: 200,201,202). " + expectedStatusCode + " is not an integer.");
                }
                possibleValues.add(statusCode);
            } catch (NumberFormatException e) {
                reporterNotifier.notifySpecificationError(testContext, "ExpectedStatus must be an integer between 200 and 599 or some integers separed by a coma (example: 200,201,202). " + expectedStatusCode + " must be between 1 and 599 (included).");
            }
        }
        return possibleValues;
    }
}
