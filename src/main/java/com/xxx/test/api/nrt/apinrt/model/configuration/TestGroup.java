package com.xxx.test.api.nrt.apinrt.model.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public record TestGroup (
    TestGroupType type,
    String filePath,
    String name,
    List<Test> tests
){
    public TestGroup(File csvFile, TestGroupType type) {
        this(type, csvFile.getAbsolutePath(), csvFile.getName(), new ArrayList<>());
    }
}
