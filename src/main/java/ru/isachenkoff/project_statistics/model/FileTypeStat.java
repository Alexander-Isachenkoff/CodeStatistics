package ru.isachenkoff.project_statistics.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileTypeStat {
    private String fileType;
    private int count;
}
