package ru.isachenkoff.project_statistics.model;

import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatFile {

    private final List<StatFile> children = new ArrayList<>();
    private final File file;
    private final boolean isDirectory;
    private boolean isTextFile;
    private StatFile parent;
    private int totalLines;
    private int notEmptyLines;

    StatFile(File file) {
        this.file = file;
        isDirectory = file.isDirectory();
        init();
    }

    public static List<StatFile> flatFiles(StatFile statFile) {
        if (!statFile.isDirectory) {
            return Collections.singletonList(statFile);
        } else {
            return statFile.children.parallelStream()
                    .flatMap(child -> flatFiles(child).stream())
                    .collect(Collectors.toList());
        }
    }

    public List<String> getAllFileTypes() {
        long l = System.currentTimeMillis();
        List<String> strings = flatFiles().stream()
                .filter(StatFile::isFile)
                .map(StatFile::getFileName)
                .map(FileUtils::getExtension)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        System.out.printf("getAllFileTypes:\t%d мс%n", System.currentTimeMillis() - l);
        return strings;
    }

    public StatFileRoot getRoot() {
        StatFile parent = this;
        while (parent.parent != null) {
            parent = parent.parent;
        }
        return (StatFileRoot) parent;
    }

    public List<StatFile> flatFiles() {
        return flatFiles(this);
    }

    private void init() {
        if (isDirectory) {
            File[] files = Optional.ofNullable(file.listFiles()).orElse(new File[0]);
            Stream.of(files).parallel()
                    .map(StatFile::new)
                    .forEach(statFile -> {
                        children.add(statFile);
                        statFile.parent = this;
                    });
            children.sort((f1, f2) -> {
                if (f1.isFile() && f2.isDirectory) {
                    return 1;
                }
                if (f1.isDirectory && f2.isFile()) {
                    return -1;
                }
                return f1.getFileName().compareTo(f2.getFileName());
            });
        } else {
            countLines();
        }
    }

    public boolean isVisible() {
        if (isFile()) {
            return getRoot().getExtFilter().contains(FileUtils.getExtension(file)) && (!getRoot().isTextFilesOnly() || isTextFile);
        } else if (!getRoot().isEmptyDirs()) {
            return flatFiles().stream().anyMatch(StatFile::isVisible);
        }
        return true;
    }

    public int getTotalLines() {
        if (isDirectory) {
            return flatFiles().stream()
                    .filter(StatFile::isVisible)
                    .filter(StatFile::isTextFile)
                    .mapToInt(statFile -> statFile.totalLines)
                    .sum();
        } else {
            return totalLines;
        }
    }

    public int getNotEmptyLines() {
        if (isDirectory) {
            return flatFiles().stream()
                    .filter(StatFile::isVisible)
                    .filter(StatFile::isTextFile)
                    .mapToInt(statFile -> statFile.notEmptyLines)
                    .sum();
        } else {
            return notEmptyLines;
        }
    }

    private void countLines() {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            isTextFile = false;
            return;
        }
        isTextFile = true;
        totalLines = lines.size();
        notEmptyLines = (int) lines.stream().filter(s -> !s.trim().isEmpty()).count();
    }

    public String getTotalLinesInfo() {
        if (isTextFile || isDirectory) {
            return String.valueOf(getTotalLines());
        } else {
            return "";
        }
    }

    public String getNotEmptyLinesInfo() {
        if (isTextFile || isDirectory) {
            int totalLines = getTotalLines();
            int notEmptyLines = getNotEmptyLines();
            float notEmptyRatio;
            if (totalLines > 0) {
                notEmptyRatio = notEmptyLines / (float) totalLines * 100;
            } else {
                notEmptyRatio = 0;
            }
            return String.format("%d (%.0f%%)", notEmptyLines, notEmptyRatio);
        } else {
            return "";
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

    public boolean isTextFile() {
        return isTextFile;
    }
}
