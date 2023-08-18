package ru.isachenkoff.project_statistics.model;

import javafx.beans.property.SimpleBooleanProperty;
import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
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
        List<FileTypeStat> fileTypeStats = new ArrayList<>();
        List<String> fileTypes = getAllFileTypes();
        List<StatFile> allFiles = flatFiles();
        for (String fileType : fileTypes) {
            int count = (int) allFiles.stream()
                    .filter(statFile -> FileUtils.getExtension(statFile.getFileName()).equals(fileType))
                    .count();
            fileTypeStats.add(new FileTypeStat(fileType, count));
        }
        return fileTypeStats;
    }

    public List<String> getAllFileTypes() {
        return flatFiles().stream()
                .filter(StatFile::isFile)
                .map(StatFile::getFileName)
                .map(FileUtils::getExtension)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
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