package com.xxx.test.api.nrt.apinrt.campaignReporter.reporters.globalStatus;

import com.xxx.test.api.nrt.apinrt.model.context.CampaignContext;
import com.xxx.test.api.nrt.apinrt.campaignReporter.exceptions.ReportException;
import com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine.ReportPlugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class GlobalStatusReporter implements ReportPlugin {

    @Value("${apiNrt.reportPath}")
    private String reportsRootPath;

    @Override
    public void campaignFinished(CampaignContext campaignContext) {
        String path = getGlobalStatusPath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(campaignContext.isStatus()?"SUCCESS":"FAILED");
        } catch (IOException e) {
            throw new ReportException("unable to create the status report: " + path, e);
        }
    }

    private String getGlobalStatusPath() {
        Path resourceDir = Paths.get("target");
        return resourceDir.toAbsolutePath() + "/" + reportsRootPath + "/status.txt";
    }
}
