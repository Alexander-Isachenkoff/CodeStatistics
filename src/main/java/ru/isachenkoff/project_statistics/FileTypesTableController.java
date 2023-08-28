package ru.isachenkoff.project_statistics;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.isachenkoff.project_statistics.model.FileTypeStat;

public class FileTypesTableController {

    @FXML
    private TableColumn<FileTypeStat, String> fileTypeColumn;
    @FXML
    private TableColumn<FileTypeStat, Integer> filesCountColumn;
    @FXML
    private TableColumn<FileTypeStat, Integer> totalLinesColumn;
    @FXML
    private TableColumn<FileTypeStat, String> notEmptyLinesColumn;

    @FXML
    private void initialize() {
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().getTypeName()));
        filesCountColumn.setCellValueFactory(new PropertyValueFactory<>("filesCount"));
        totalLinesColumn.setCellValueFactory(new PropertyValueFactory<>("linesCount"));
        notEmptyLinesColumn.setCellValueFactory(new PropertyValueFactory<>("notEmptyLinesInfo"));
    }

}
