package ru.isachenkoff.project_statistics.model;

import javafx.beans.property.SimpleBooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileTypeStat {
    private FileType fileType;
    private int filesCount;
    private int linesCount;
    private int notEmptyLinesCount;
    private final SimpleBooleanProperty visibleProperty = new SimpleBooleanProperty(true);

    public String getNotEmptyLinesInfo() {
        float notEmptyRatio;
        if (linesCount > 0) {
            notEmptyRatio = notEmptyLinesCount / (float) linesCount * 100;
        } else {
            notEmptyRatio = 0;
        }
        return String.format("%d (%.0f%%)", notEmptyLinesCount, notEmptyRatio);
    }

    public boolean isVisible() {
        return visibleProperty.get();
    }

}
