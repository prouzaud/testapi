package com.xxx.test.api.nrt.apinrt.campaignReporter.reporters.log;

import com.xxx.test.api.nrt.apinrt.campaignReporter.exceptions.ReportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ReportLogger {

    private final static String CHAR_SEPARATOR = " | ";
    private final static String CHAR_NEW_LINE = "\n";
    private final static String CHAR_TYPE_MESSAGE = "MESSAGE";
    private final static String CHAR_TYPE_ERROR = "ERROR  ";
    private final static String CHAR_INDENT = "\t";

    @Value("${apiNrt.reportPath}")
    private String reportsRootPath;

    private BufferedWriter writer = null;
    private File reportPath = null;

    public void initializeWriter() {
        this.reportPath = new File(getReportsRootPath());
        if (!reportPath.getParentFile().exists() &&!reportPath.getParentFile().mkdirs()) {
            throw new ReportException("Unable to create directories for the log file (" + reportPath.getAbsolutePath() + ")");
        }
        if (reportPath.exists() && !reportPath.delete()) {
            throw new ReportException("The log file (" + reportPath.getAbsolutePath() + ") already exists and can't be deleted.");
        }
        try {
            writer = new BufferedWriter(new FileWriter(reportPath, false));
        } catch (IOException e) {
            throw new ReportException("Unable to create the log file ("+reportPath.getAbsolutePath()+")", e);
        }
    }

    private String getReportsRootPath() {
        Path resourceDir = Paths.get("target");
        return resourceDir.toAbsolutePath() + "/" + reportsRootPath + "/general-report.log";
    }

    public void logMessage(int indent, String message) {
        log(indent, message, CHAR_TYPE_MESSAGE);
    }

    public void logError(int indent, String message) {
        log(indent, message, CHAR_TYPE_ERROR);
    }

    private void log(int indent, String message, String logType) {
        String[] lines = message.split("\n");
        for (String line : lines) {
            logLine(indent, line, logType);
        }
    }

    private void logLine(int indent, String message, String logType) {
        write(formatCurrentDate());
        write(CHAR_SEPARATOR);
        write(logType);
        write(CHAR_SEPARATOR);
        write(buildIndent(indent));
        write(message);
        write(CHAR_NEW_LINE);
    }

    private String formatCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private void write(String message) {
        try {
            writer.write(message);
        } catch (Exception e) {
            throw new ReportException("Error when writing in the log file ("+reportPath.getAbsolutePath()+") the String ("+message+")", e);
        }
    }

    private String buildIndent(int indent) {
        return CHAR_INDENT.repeat(indent);
    }

    public void closeLog() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new ReportException("Unable to close the log file ("+reportPath.getAbsolutePath()+")", e);
        }
    }
}
