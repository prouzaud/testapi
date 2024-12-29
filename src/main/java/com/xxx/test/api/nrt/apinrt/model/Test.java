package com.xxx.test.api.nrt.apinrt.model;

public record Test(
    String filePath,
    int line,
    CallSettings callSettings,
    int expectedStatus,
    String expectedResult
) {}
