package com.xxx.test.api.nrt.apinrt.model.configuration;

public record Test(
    String filePath,
    int line,
    String[] csvData,
    CallSettings callSettings,
    int expectedStatus,
    String expectedResult
) {}
