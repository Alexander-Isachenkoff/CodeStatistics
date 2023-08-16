package ru.isachenkoff.project_statistics;

import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatFile {

    private final List<StatFile> children = new ArrayList<>();
    private final File file;
    private final boolean isDirectory;
    private int totalLines;
    private int notEmptyLines;
    private final List<String> extFilter;

    public StatFile(File file, List<String> extensions) {
        this.file = file;
        isDirectory = file.isDirectory();
        extFilter = extensions;
        init();
    }

    public static List<StatFile> flatFiles(StatFile statFile) {
        if (!statFile.isDirectory) {
            return Collections.singletonList(statFile);
        } else {
            List<StatFile> flatChildren = new ArrayList<>();
            for (StatFile child : statFile.children) {
                flatChildren.addAll(flatFiles(child));
            }
            return flatChildren;
        }
    }

    public void setExtFilter(List<String> extFilter) {
        this.extFilter.clear();
        this.extFilter.addAll(extFilter);
    }

    public List<StatFile> flatFiles() {
        return flatFiles(this);
    }

    private void init() {
        if (isDirectory) {
            for (File file1 : file.listFiles()) {
                children.add(new StatFile(file1, extFilter));
            }
        } else {
            countLines();
        }
    }

    public boolean isFiltered() {
        return isDirectory || extFilter.contains(FileUtils.getExtension(file));
    }

    public int getTotalLines() {
        if (isDirectory) {
            return flatFiles().stream()
                    .filter(StatFile::isFiltered)
                    .mapToInt(statFile -> statFile.totalLines)
                    .filter(i -> i > -1)
                    .sum();
        } else {
            return totalLines;
        }
    }

    public int getNotEmptyLines() {
        if (isDirectory) {
            return flatFiles().stream()
                    .filter(StatFile::isFiltered)
                    .mapToInt(statFile -> statFile.notEmptyLines)
                    .filter(i -> i > -1)
                    .sum();
        } else {
            return notEmptyLines;
        }
    }

    private void countLines() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            totalLines = lines.size();
            notEmptyLines = (int) lines.stream().filter(s -> !s.trim().isEmpty()).count();
        } catch (IOException e) {
            totalLines = -1;
            notEmptyLines = -1;
        }
    }

    public String getTotalLinesInfo() {
        int totalLines = getTotalLines();
        if (totalLines == -1) {
            return "";
        } else {
            return String.valueOf(totalLines);
        }
    }

    public String getNotEmptyLinesInfo() {
        int totalLines = getTotalLines();
        if (totalLines == -1) {
            return "";
        } else {
            int notEmptyLines = getNotEmptyLines();
            float notEmptyRatio;
            if (totalLines > 0) {
                notEmptyRatio = notEmptyLines / (float) totalLines * 100;
            } else {
                notEmptyRatio = 0;
            }
            return String.format("%d (%.0f%%)", notEmptyLines, notEmptyRatio);
        }
    }

    public List<StatFile> getChildren() {
        return children;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return file.getName();
    }

    public boolean isFile() {
        return !isDirectory;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
