package com.xxx.test.api.nrt.apinrt.util;

import com.xxx.test.api.nrt.apinrt.CampaignOrchestrator;
import com.xxx.test.api.nrt.apinrt.util.repositoryComparator.RepositoryComparator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=57575"})
@ActiveProfiles("test")
@ComponentScan(basePackages = {"com.xxx.test.api",  "com.xxx.test.api.nrt.apinrt.util"})    // src/test/java
public class ApiTestItTest {

    @Autowired
    private CampaignOrchestrator campaignOrchestrator;

    @Autowired
    private RepositoryComparator repositoryComparator;

    @Value("${apiNrt.rootPath}")
    private String rootPath;
    @Value("${apiNrt.reportPath}")
    private String reportPath;

    @Test
    void integrationTest() {
        campaignOrchestrator.playCampaign();
        List<String> errors = repositoryComparator.assertDirectoriesMatch(rootPath, reportPath);
        Assertions.assertThat(errors).isEmpty();
    }
}
