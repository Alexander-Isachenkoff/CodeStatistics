package ru.isachenkoff.project_statistics.model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class StatFileRoot extends StatFile {

    private boolean emptyDirs;
    private boolean textFilesOnly;
    private List<String> extFilter = new ArrayList<>();

    public StatFileRoot(File file) {
        super(file);
    }

}
