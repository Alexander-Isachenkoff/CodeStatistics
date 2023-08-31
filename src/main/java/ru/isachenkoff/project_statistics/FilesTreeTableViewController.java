package ru.isachenkoff.project_statistics;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import ru.isachenkoff.project_statistics.model.StatFile;

import java.io.File;

public class FilesTreeTableViewController {

    @FXML
    private TreeTableView<StatFile> treeTableView;
    @FXML
    private TreeTableColumn<StatFile, StatFile> fileColumn;
    @FXML
    private TreeTableColumn<StatFile, String> totalLinesColumn;
    @FXML
    private TreeTableColumn<StatFile, String> notEmptyLinesColumn;

    @FXML
    private void initialize() {
        treeTableView.setRowFactory(param -> {
            return new TreeTableRow<StatFile>() {
                @Override
                protected void updateItem(StatFile item, boolean empty) {
                    super.updateItem(item, empty);
                    setContextMenu(createContextMenu(item));
                }

                private ContextMenu createContextMenu(StatFile item) {
                    if (item != null && item.isDirectory()) {
                        MenuItem analysisMenuItem = new MenuItem("Анализ");
                        analysisMenuItem.setOnAction(event -> {
                            MainController.getInstance().showNewAnalysis(new File(item.getFilePath()));
                        });
                        return new ContextMenu(analysisMenuItem);
                    } else {
                        return null;
                    }
                }
            };
        });

        fileColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        fileColumn.setCellFactory(param -> new FileTableCell());

        totalLinesColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getTotalLinesInfo()));
        notEmptyLinesColumn.setCellValueFactory(param -> {
            StatFile statFile = param.getValue().getValue();
            return new SimpleStringProperty(statFile.getNotEmptyLinesInfo());
        });
    }

    static class FileTableCell extends TreeTableCell<StatFile, StatFile> {

        @Override
        protected void updateItem(StatFile statFile, boolean empty) {
            super.updateItem(statFile, empty);
            if (!empty && statFile != null) {
                setGraphic(createCellContent(statFile));
            } else {
                setGraphic(null);
            }
        }

        private HBox createCellContent(StatFile statFile) {
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            int size = 16;
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setImage(statFile.getImage());
            VBox vBox = new VBox(imageView);
            vBox.setAlignment(Pos.CENTER);
            vBox.setMinSize(size, size);
            Text text = new Text(statFile.getFileName());
            text.setFontSmoothingType(FontSmoothingType.LCD);
            HBox hBox = new HBox(4, vBox, text);
            hBox.setAlignment(Pos.CENTER_LEFT);
            return hBox;
        }

    }

}
