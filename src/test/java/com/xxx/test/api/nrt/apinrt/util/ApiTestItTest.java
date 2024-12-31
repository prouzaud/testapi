package com.xxx.test.api.nrt.apinrt.util;

import com.xxx.test.api.nrt.apinrt.CampaignOrchestrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=57575"})
@ActiveProfiles("test")
@ComponentScan(basePackages = {"com.xxx.test.api",  "com.xxx.test.api.nrt.apinrt.util"})    // src/test/java
public class ApiTestItTest {

    @Autowired
    private CampaignOrchestrator campaignOrchestrator;

    @Test
    void integrationTest() {
        campaignOrchestrator.playCampaign();
    }
}
