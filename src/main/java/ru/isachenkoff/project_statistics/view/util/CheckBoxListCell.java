package ru.isachenkoff.project_statistics.view.util;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;

public abstract class CheckBoxListCell<T> extends ListCell<T> {

    private final CheckBox checkBox = new CheckBox();

    public CheckBoxListCell() {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                getListView().getSelectionModel().select(getIndex());
            } else {
                getListView().getSelectionModel().clearSelection(getIndex());
            }
        });

        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (getListView().getSelectionModel().getSelectedIndices().contains(getIndex())) {
                getListView().getSelectionModel().clearSelection(getIndex());
            } else {
                getListView().getSelectionModel().select(getIndex());
            }
            getListView().requestFocus();
            e.consume();
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            checkBox.setSelected(isSelected());
            checkBox.setGraphic(createCheckBoxGraphic(item));
            setGraphic(checkBox);
        } else {
            setGraphic(null);
        }
    }

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
        checkBox.setSelected(selected);
    }

    public abstract Node createCheckBoxGraphic(T item);

}
