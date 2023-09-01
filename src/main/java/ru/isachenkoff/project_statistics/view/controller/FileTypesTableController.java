package ru.isachenkoff.project_statistics.view.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import ru.isachenkoff.project_statistics.model.FileTypeStat;

public class FileTypesTableController {

    @FXML
    private TableColumn<FileTypeStat, FileTypeStat> fileTypeColumn;
    @FXML
    private TableColumn<FileTypeStat, Integer> filesCountColumn;
    @FXML
    private TableColumn<FileTypeStat, Integer> totalLinesColumn;
    @FXML
    private TableColumn<FileTypeStat, String> notEmptyLinesColumn;

    @FXML
    private void initialize() {
        fileTypeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));

        fileTypeColumn.setCellFactory(param -> {
            return new TableCell<FileTypeStat, FileTypeStat>() {
                @Override
                protected void updateItem(FileTypeStat item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        ImageView imageView = new ImageView();
                        imageView.setPreserveRatio(true);
                        int size = 20;
                        imageView.setFitWidth(size);
                        imageView.setFitHeight(size);
                        Image image = item.getFileType().getImage();
                        imageView.setImage(image);
                        VBox vBox = new VBox(imageView);
                        vBox.setAlignment(Pos.CENTER);
                        vBox.setMinSize(size, size);
                        Text text = new Text(item.getFileType().getTypeName());
                        text.setFontSmoothingType(FontSmoothingType.LCD);
                        HBox hBox = new HBox(4, vBox, text);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });

        filesCountColumn.setCellValueFactory(new PropertyValueFactory<>("filesCount"));
        totalLinesColumn.setCellValueFactory(new PropertyValueFactory<>("linesCount"));
        notEmptyLinesColumn.setCellValueFactory(new PropertyValueFactory<>("notEmptyLinesInfo"));
    }

}
