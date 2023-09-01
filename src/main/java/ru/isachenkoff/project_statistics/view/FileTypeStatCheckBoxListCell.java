package ru.isachenkoff.project_statistics.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import ru.isachenkoff.project_statistics.model.FileTypeStat;

public abstract class FileTypeStatCheckBoxListCell extends CheckBoxListCell<FileTypeStat> {

    public FileTypeStatCheckBoxListCell() {
        setSelectedStateCallback(FileTypeStat::getVisibleProperty);
    }

    @Override
    public void updateItem(FileTypeStat item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setText("");
            CheckBox checkBox = (CheckBox) getGraphic();
            checkBox.setOnAction(event -> onCheckBoxAction(item));
            checkBox.setGraphic(createCheckBoxGraphic(item));
        }
    }

    public Node createCheckBoxGraphic(FileTypeStat item) {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        int size = 20;
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setImage(item.getFileType().getImage());
        VBox vBox = new VBox(imageView);
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinSize(size, size);

        String text = String.format("%s (%d)", item.getFileType().getTypeName(), item.getFilesCount());
        Text textNode = new Text(text);
        textNode.setFontSmoothingType(FontSmoothingType.LCD);

        HBox hBox = new HBox(4, vBox, textNode);
        hBox.setAlignment(Pos.CENTER_LEFT);

        return hBox;
    }

    public abstract void onCheckBoxAction(FileTypeStat fileTypeStat);

}
