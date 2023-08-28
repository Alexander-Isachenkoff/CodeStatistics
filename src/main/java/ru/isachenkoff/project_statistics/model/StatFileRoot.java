package ru.isachenkoff.project_statistics.model;

import javafx.beans.property.SimpleBooleanProperty;
import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class StatFileRoot extends StatFile {

    private final SimpleBooleanProperty emptyDirs = new SimpleBooleanProperty();
    private final SimpleBooleanProperty textFilesOnly = new SimpleBooleanProperty();
    private List<String> extFilter;

    public StatFileRoot(File file) {
        super(file);
    }

    public List<FileTypeStat> getFileTypesStatistics() {
        List<String> fileTypes = getAllFileTypes();
        List<StatFile> allFiles = flatFiles();
        long l = System.currentTimeMillis();

        List<FileTypeStat> fileTypeStats = fileTypes.parallelStream().map(fileType -> {
                    List<StatFile> filesByExt = allFiles.stream()
                            .filter(statFile -> FileUtils.getExtension(statFile.getFileName()).equals(fileType))
                            .collect(Collectors.toList());
                    int filesCount = filesByExt.size();
                    int linesCount = filesByExt.stream()
                            .mapToInt(StatFile::getTotalLines)
                            .sum();
                    int notEmptyLinesCount = filesByExt.stream()
                            .mapToInt(StatFile::getNotEmptyLines)
                            .sum();
                    return new FileTypeStat(FileType.of(fileType), filesCount, linesCount, notEmptyLinesCount);
                })
                .collect(Collectors.toList());

        System.out.printf("getFileTypesStatistics:\t\t%d%n", System.currentTimeMillis() - l);
        return fileTypeStats;
    }

    public List<String> getExtFilter() {
        return extFilter;
    }

    public void setExtFilter(List<String> extFilter) {
        this.extFilter = extFilter;
    }

    public boolean isEmptyDirs() {
        return emptyDirs.get();
    }

    public boolean isTextFilesOnly() {
        return textFilesOnly.get();
    }

    public SimpleBooleanProperty emptyDirsProperty() {
        return emptyDirs;
    }

    public SimpleBooleanProperty textFilesOnlyProperty() {
        return textFilesOnly;
    }

}
