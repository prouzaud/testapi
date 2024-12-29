package com.xxx.test.api.nrt.apinrt.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public record TestGroup (
    String filePath,
    String name,
    List<Test> tests
){

    public TestGroup(File csvFile) {
        this(csvFile.getAbsolutePath(), csvFile.getName(), new ArrayList<>());
    }
}
