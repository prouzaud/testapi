package com.xxx.test.api.nrt.apinrt.model;

import java.util.ArrayList;
import java.util.List;

public record Campaign(
    String rootPath,
    List<TestGroup> testGroups
) {

    public Campaign(String rootPath) {
        this(rootPath, new ArrayList<>());
    }
}
