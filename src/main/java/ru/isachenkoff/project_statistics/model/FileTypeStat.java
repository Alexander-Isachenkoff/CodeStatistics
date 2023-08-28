package ru.isachenkoff.project_statistics.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileTypeStat {
    private FileType fileType;
    private int filesCount;
    private int linesCount;
}
