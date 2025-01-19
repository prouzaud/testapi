package com.xxx.test.api.nrt.apinrt.model.configuration;

import java.util.Arrays;

public record Test(
    String filePath,
    int line,
    String[] csvData,
    CallSettings callSettings,
    String expectedStatus,
    String expectedResult
) {

    @Override
    public String toString() {
        return "Test: [line "+line+"]: {" +
                ", csvData=" + Arrays.toString(csvData) +
                ", callSettings=" + callSettings +
                ", expectedStatus='" + expectedStatus + '\'' +
                ", expectedResult='" + expectedResult + '\'' +
                '}';
    }
}
