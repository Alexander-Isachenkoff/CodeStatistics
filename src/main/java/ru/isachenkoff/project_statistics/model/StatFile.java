package ru.isachenkoff.project_statistics.model;

import javafx.scene.image.Image;
import lombok.Getter;
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

public class StatFile implements Comparable<StatFile> {

    @Getter
    private final List<StatFile> children = new ArrayList<>();
    @Getter
    private final String fileName;
    @Getter
    private final String filePath;
    @Getter
    private final boolean isDirectory;
    @Getter
    private boolean isTextFile;
    private StatFile parent;
    private int totalLines;
    private int notEmptyLines;

    StatFile(File file) {
        fileName = file.getName();
        filePath = file.getAbsolutePath();
        isDirectory = file.isDirectory();
        init(file);
    }

    public List<StatFile> flatFiles() {
        if (isFile()) {
            return Collections.singletonList(this);
        } else {
            return children.parallelStream()
                    .flatMap(child -> child.flatFiles().stream())
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

    private void init(File file) {
        if (isDirectory()) {
            File[] files = Optional.ofNullable(file.listFiles()).orElse(new File[0]);
            Stream.of(files).parallel()
                    .map(StatFile::new)
                    .sorted()
                    .forEachOrdered(statFile -> {
                        children.add(statFile);
                        statFile.parent = this;
                    });
        } else {
            countLines(file);
        }
    }

    public boolean isVisible() {
        if (isFile()) {
            return getRoot().getExtFilter().contains(FileUtils.getExtension(getFileName())) && (!getRoot().isTextFilesOnly() || isTextFile);
        } else if (!getRoot().isEmptyDirs()) {
            return flatFiles().stream().anyMatch(StatFile::isVisible);
        }
        return true;
    }

    public int getTotalLines() {
        if (isDirectory()) {
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
        if (isDirectory()) {
            return flatFiles().stream()
                    .filter(StatFile::isVisible)
                    .filter(StatFile::isTextFile)
                    .mapToInt(statFile -> statFile.notEmptyLines)
                    .sum();
        } else {
            return notEmptyLines;
        }
    }

    private void countLines(File file) {
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
        if (isTextFile || isDirectory()) {
            return String.valueOf(getTotalLines());
        } else {
            return "";
        }
    }

    public String getNotEmptyLinesInfo() {
        if (isTextFile || isDirectory()) {
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

    public List<FileTypeStat> getFileTypesStatistics() {
        List<StatFile> allFiles = flatFiles();
        long l = System.currentTimeMillis();

        List<FileTypeStat> fileTypeStats = allFiles.stream()
                .collect(Collectors.groupingBy(statFile -> FileUtils.getExtension(statFile.getFileName())))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<StatFile> filesByExt = entry.getValue();
                    int filesCount = filesByExt.size();
                    int linesCount = filesByExt.stream()
                            .mapToInt(StatFile::getTotalLines)
                            .sum();
                    int notEmptyLinesCount = filesByExt.stream()
                            .mapToInt(StatFile::getNotEmptyLines)
                            .sum();
                    return new FileTypeStat(FileType.of(entry.getKey()), filesCount, linesCount, notEmptyLinesCount);
                })
                .collect(Collectors.toList());

        System.out.printf("getFileTypesStatistics:\t\t%d мс%n", System.currentTimeMillis() - l);
        return fileTypeStats;
    }

    public Image getImage() {
        if (isFile()) {
            return FileType.of(FileUtils.getExtension(getFileName())).getImage();
        } else {
            return FileType.DIRECTORY_IMAGE;
        }
    }

    @Override
    public int compareTo(StatFile statFile) {
        if (this.isFile() && statFile.isDirectory()) {
            return 1;
        }
        if (this.isDirectory() && statFile.isFile()) {
            return -1;
        }
        return this.getFileName().compareTo(statFile.getFileName());
    }

    public boolean isFile() {
        return !isDirectory();
    }

}
