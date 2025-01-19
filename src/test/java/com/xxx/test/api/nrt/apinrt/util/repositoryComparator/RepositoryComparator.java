package com.xxx.test.api.nrt.apinrt.util.repositoryComparator;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class RepositoryComparator {

    public static final String STATUS_FILE_NAME = "status.txt";
    private final List<String> errors = new ArrayList<>();

    public List<String> assertDirectoriesMatch(String expected, String testResult) {
        compareDirectory(Paths.get("src/test/resources/it/expected").toFile(), Paths.get("target/"+testResult).toFile() );
        return errors;
    }

    private void compareDirectory(File expectedRepository, File generatedRepository) {

        compareDirectoryStructure(expectedRepository, generatedRepository);
        var generatedChildren = generatedRepository.listFiles();
        var expectedChildren = expectedRepository.listFiles();
        assert expectedChildren != null : "expectedChildren is null";
        for (File expectedChild : expectedChildren) {
            assert generatedChildren != null : "generatedChildren is null";
            File generatedChild = getMatchingChild(expectedChild, generatedChildren);
            if (generatedChild == null) return;
            if (expectedChild.isDirectory()) {
                compareDirectory(expectedChild, generatedChild);
            } else if (expectedChild.isFile()) {
                try {
                    if (!filesMatch(expectedChild, generatedChild)) {
                        errors.add("Unable to compare text files (expected="+expectedChild+", generated="+generatedChild+")");
                    }

                } catch (IOException e) {
                    throw new RuntimeException("Unable to compare text files (expected="+expectedChild+", generated="+generatedChild+")", e);
                }
            }
        }
    }

    private File getMatchingChild(File expectedChild, File[] generatedChildren) {
        File generatedChild = Arrays.stream(generatedChildren).filter(gen -> gen.getName().equals(expectedChild.getName())).findAny().orElse(null);
        if (generatedChild == null) {
            errors.add(expectedChild.getName());
            return null;
        }
        return generatedChild;
    }

    private void compareDirectoryStructure(File expectedRepository, File generatedRepository) {
        if (expectedRepository == null) {
            errors.add("the expected repository can't be null - shouldn't raise!");
            return;
        }
        if (generatedRepository == null) {
            errors.add("the generated repository can't be null - related expected repository:" + expectedRepository.getAbsolutePath());
            return;
        }
        if (!generatedRepository.exists()) {
            errors.add(generatedRepository.getAbsolutePath() + " directory doesn't exist.");
            return;
        }
        if (!generatedRepository.isDirectory()) {
            errors.add(generatedRepository.getAbsolutePath() + " is not a directory.");
            return;
        }
        if (Objects.requireNonNull(expectedRepository.list()).length != Objects.requireNonNull(generatedRepository.list()).length) {
            errors.add("Expected and generated folders have not the same number of files (expected=" + expectedRepository.getAbsolutePath() + ", generated=" + generatedRepository.getAbsolutePath() + ").");
        }
    }

    private boolean filesMatch(File expectedFile, File generatedFile) throws IOException {

        Path pathExpected = Paths.get(expectedFile.getAbsolutePath());
        Path pathGenerated = Paths.get(generatedFile.getAbsolutePath());

        List<String> linesExpected = Files.readAllLines(pathExpected);
        List<String> linesGenerated = Files.readAllLines(pathGenerated);

        if(linesExpected.size()!=linesGenerated.size()) {
            errors.add("the two files have not the same number of lines (expected=" + expectedFile.getAbsolutePath() + ", generated=" + generatedFile.getAbsolutePath() + ")");
            return false;
        }

        for( int i = 0; i<linesExpected.size();i++) {
            if (!linesMatch(linesExpected.get(i), linesGenerated.get(i))) {
                errors.add("the two lines dismatch: expected= " + linesExpected.get(i) + " and generated=" + linesGenerated.get(i));
                return false;
            }
        }
        return true;
    }

    public static boolean linesMatch(String expected, String generated) {
        if (expected.equals(generated)) {
            return true;
        }
        if (!expected.contains("######")) {
            return false;
        }
        String[] segments = expected.split("######", -1);

        int currentPos = 0;
        for (int i = 0; i < segments.length; i++) {
            String seg = segments[i];
            if (seg.isEmpty()) {
                continue;
            }
            int foundIndex = generated.indexOf(seg, currentPos);
            if (foundIndex < 0) {
                return false;
            }
            currentPos = foundIndex + seg.length();
        }
        String lastSegment = segments[segments.length - 1];
        return lastSegment.isEmpty() || currentPos == generated.length();
    }

}