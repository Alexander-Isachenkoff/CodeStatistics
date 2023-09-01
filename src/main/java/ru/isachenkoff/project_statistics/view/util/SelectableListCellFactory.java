package ru.isachenkoff.project_statistics.view.util;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public abstract class SelectableListCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new CheckBoxListCell();
    }

    public abstract Node createCheckBoxGraphic(T item);

    private class CheckBoxListCell extends ListCell<T> {

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
    }
}
