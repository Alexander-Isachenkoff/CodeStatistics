package ru.isachenkoff.project_statistics.model;

import javafx.beans.property.SimpleBooleanProperty;
import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        long l = System.currentTimeMillis();
        for (String fileType : fileTypes) {
            int count = (int) allFiles.stream()
                    .filter(statFile -> FileUtils.getExtension(statFile.getFileName()).equals(fileType))
                    .count();
            fileTypeStats.add(new FileTypeStat(FileType.of(fileType), count));
        }
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
