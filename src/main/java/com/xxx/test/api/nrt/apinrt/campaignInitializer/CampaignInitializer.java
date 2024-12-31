package com.xxx.test.api.nrt.apinrt.campaignInitializer;

import com.xxx.test.api.nrt.apinrt.campaignInitializer.exceptions.TestReaderException;
import com.xxx.test.api.nrt.apinrt.model.configuration.Campaign;
import com.xxx.test.api.nrt.apinrt.model.configuration.Test;
import com.xxx.test.api.nrt.apinrt.model.configuration.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
public class CampaignInitializer {

    @Autowired
    public CampaignInitializer(CsvReader csvReader){
        this.csvReader = csvReader;
    }

    private final CsvReader csvReader;

    @Value("${apiNrt.rootPath}")
    private String rootDirPath;

    public Campaign createCampaignFromTestRepository() {
        File rootDir = getRootFile(rootDirPath);
        Campaign campaign = new Campaign(rootDir.getPath());
        processFolder(rootDir, campaign);
        return campaign;
    }

    private File getRootFile(String rootDirPath) {
        File rootDir = new File(getTestResourcesPath() + rootDirPath);
        if (!rootDir.isDirectory()) {
            throw new TestReaderException("the property \"apiNrt.rootPath\" must be a readable directory, directly contained in src/test/resources (don't start by \"\\\" ou \"/\")");
        }
        return rootDir;
    }

    private String getTestResourcesPath() {
        Path resourceDir = Paths.get("src", "test", "resources");
        return resourceDir.toAbsolutePath() + "/";
    }

    private void processFolder(File currentDirectory, Campaign campaign) {
        List<File> csvFiles = detectCsvFiles(currentDirectory);
        for (File csvFile : csvFiles) {
            addCsvFile(campaign, csvFile);
        }
        List<File> subDirectories = detectSubDirectory(currentDirectory);
        for (File subDirectory : subDirectories) {
            processFolder(subDirectory, campaign);
        }
    }

    private List<File> detectCsvFiles(File currentDirectory) {
        File[] children = currentDirectory.listFiles();
        assert children != null;
        return Arrays.stream(children)
            .filter(File::isFile)
            .filter(file -> file.getName().endsWith(".csv"))
            .toList();
    }

    private List<File> detectSubDirectory(File currentDirectory) {
        File[] children = currentDirectory.listFiles();
        assert children != null;
        return Arrays.stream(children)
            .filter(File::isDirectory)
            .toList();
    }

    private void addCsvFile(Campaign campaign, File csvFile) {
        var testGroup = new TestGroup(csvFile);
        campaign.testGroups().add(testGroup);

        List<Test> tests = csvReader.createTestsFromCsv(csvFile);
        testGroup.tests().addAll(tests);
    }
}
