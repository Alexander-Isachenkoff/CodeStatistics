package ru.isachenkoff.project_statistics.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileTypeStat {
    private FileType fileType;
    private int filesCount;
    private int linesCount;
    private int notEmptyLinesCount;

    public String getNotEmptyLinesInfo() {
        float notEmptyRatio;
        if (linesCount > 0) {
            notEmptyRatio = notEmptyLinesCount / (float) linesCount * 100;
        } else {
            notEmptyRatio = 0;
        }
        return String.format("%d (%.0f%%)", notEmptyLinesCount, notEmptyRatio);
    }
}
