package com.xxx.test.api.nrt.apinrt.util.repositoryComparator;

import com.xxx.test.api.nrt.apinrt.util.repositoryComparator.model.CoupleOfFile;
import com.xxx.test.api.nrt.apinrt.util.repositoryComparator.model.FileType;

import java.io.File;
import java.util.*;

import static com.xxx.test.api.nrt.apinrt.util.repositoryComparator.model.FileType.REPOSITORY;

public class RepositoryComparator {

    private List<String> errors = new ArrayList<>();

    public void assertDirectoriesMatch(File expected, File testResult) {
        //compareRepository(new CoupleOfFile(expected, testResult, REPOSITORY));
    }

    private void compareDirectory(CoupleOfFile coupleOfFile) {
        if (!coupleOfFile.expected().isDirectory()) {
            System.out.println(coupleOfFile.generated().getAbsolutePath() + " is not a directory.");
        }

    }
}
