package ru.isachenkoff.project_statistics.model;

import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.util.List;

public class StatFileRoot extends StatFile {

    private final SimpleBooleanProperty emptyDirs = new SimpleBooleanProperty();
    private final SimpleBooleanProperty textFilesOnly = new SimpleBooleanProperty();
    private List<String> extFilter;

    public StatFileRoot(File file) {
        super(file);
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
