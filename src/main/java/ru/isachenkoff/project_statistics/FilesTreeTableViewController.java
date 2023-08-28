package ru.isachenkoff.project_statistics;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import ru.isachenkoff.project_statistics.model.FileType;
import ru.isachenkoff.project_statistics.model.StatFile;
import ru.isachenkoff.project_statistics.util.FileUtils;

public class FilesTreeTableViewController {

    @FXML
    private TreeTableColumn<StatFile, String> fileColumn;
    @FXML
    private TreeTableColumn<StatFile, String> totalLinesColumn;
    @FXML
    private TreeTableColumn<StatFile, String> notEmptyLinesColumn;

    @FXML
    private void initialize() {
        fileColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getFile().getName()));
        fileColumn.setCellFactory(param -> {
            return new TreeTableCell<StatFile, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        ImageView imageView = new ImageView();
                        imageView.setPreserveRatio(true);
                        int size = 16;
                        imageView.setFitWidth(size);
                        imageView.setFitHeight(size);
                        Image image;
                        if (getTreeTableRow().getTreeItem() != null) {
                            if (getTreeTableRow().getTreeItem().getValue().isFile()) {
                                image = FileType.of(FileUtils.getExtension(item)).getImage();
                            } else {
                                image = FileType.DIRECTORY_IMAGE;
                            }
                            imageView.setImage(image);
                        }
                        VBox vBox = new VBox(imageView);
                        vBox.setAlignment(Pos.CENTER);
                        vBox.setMinSize(size, size);
                        Text text = new Text(item);
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

        totalLinesColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getTotalLinesInfo()));
        notEmptyLinesColumn.setCellValueFactory(param -> {
            StatFile statFile = param.getValue().getValue();
            return new SimpleStringProperty(statFile.getNotEmptyLinesInfo());
        });
    }
}
