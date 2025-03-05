package com.xxx.test.api.nrt.apinrt.campaignInitializer;

import com.xxx.test.api.nrt.apinrt.campaignInitializer.exceptions.TestReaderException;
import com.xxx.test.api.nrt.apinrt.model.configuration.CallSettings;
import com.xxx.test.api.nrt.apinrt.model.configuration.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvReader {

    @Value("${apiNrt.inputs.csvSeparator}")
    private String csvSeparator;

    public List<Test> createTestsFromCsv(File csvFile) {

        List<Test> tests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;

            skipLine(reader, csvFile);

            int lineNumber = 2;
            while ((line = readline(reader, csvFile, lineNumber)) != null) {
                createTestFromCsvLine(csvFile, line, lineNumber, tests);
                lineNumber++;
            }
        } catch (IOException e) {
            throw new TestReaderException("Unable to open the CSV file " + csvFile.getAbsolutePath() + ": ", e);
        }
        return tests;
    }

    private void createTestFromCsvLine(File csvFile, String line, int lineNumber, List<Test> tests) {
        String[] data = line.split(csvSeparator);
        Test test = buildTest(data, csvFile, lineNumber);
        tests.add(test);
    }

    private String readline(BufferedReader reader, File csvFile, int lineNumber) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new TestReaderException("Unable to read " + csvFile.getAbsolutePath() + "(line "+ lineNumber +"): ", e);
        }
    }

    private void skipLine(BufferedReader reader, File csvFile) {
        readline(reader, csvFile, 1);
    }

    private Test buildTest(String[] data, File csvFile, int lineNumber) {
        checkLine(data, csvFile, lineNumber);
        CallSettings callSettings = new CallSettings(data[0]+data[1], data[2], data[3]);
        return new Test(csvFile.getAbsolutePath(), lineNumber, data, callSettings, data[4], data[5], "(default)");
    }

    private void checkLine(String[] data, File csvFile, int lineNumber) {
        check(data.length == 7, csvFile, lineNumber, "Csv lines must have 7 cells ("+data.length+" cells for the current one)." );
    }

    private void check(boolean failureCondition, File csvFile, int lineNumber, String message) {
        if (!failureCondition) {
            throw new TestReaderException("File " + csvFile.getAbsolutePath() + " line " + lineNumber + ": " + message);
        }
    }
}
