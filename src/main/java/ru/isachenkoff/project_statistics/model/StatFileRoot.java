package ru.isachenkoff.project_statistics.model;

import javafx.beans.property.SimpleBooleanProperty;
import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class StatFileRoot extends StatFile {

    private List<String> extFilter;
    private final SimpleBooleanProperty emptyDirs = new SimpleBooleanProperty();
    private final SimpleBooleanProperty textFilesOnly = new SimpleBooleanProperty();

    public StatFileRoot(File file) {
        super(file);
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
