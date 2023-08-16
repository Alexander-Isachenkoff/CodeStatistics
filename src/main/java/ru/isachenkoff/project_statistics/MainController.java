package ru.isachenkoff.project_statistics;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;
import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private ListView<Pair<String, SimpleBooleanProperty>> fileTypeListView;
    @FXML
    private Button analysisBtn;
    @FXML
    private TextField pathField;
    @FXML
    private TreeTableView<StatFile> table;

    private File directory;

    public static TreeItem<StatFile> toTree(StatFile statFile) {
        TreeItem<StatFile> treeItem = new TreeItem<>(statFile);
        treeItem.setExpanded(true);
        if (statFile.getFile().isDirectory()) {
            List<TreeItem<StatFile>> collect = statFile.getChildren().stream()
                    .filter(StatFile::isFiltered)
                    .map(MainController::toTree)
                    .collect(Collectors.toList());
            treeItem.getChildren().setAll(collect);
        }
        return treeItem;
    }

    @FXML
    private void initialize() {
        TreeTableColumn<StatFile, String> column;

        column = new TreeTableColumn<>("Путь файла");
        column.setPrefWidth(300);
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getFile().getName()));
        table.getColumns().add(column);

        column = new TreeTableColumn<>("Всего строк");
        column.setPrefWidth(120);
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getTotalLinesInfo()));
        table.getColumns().add(column);

        column = new TreeTableColumn<>("Непустых строк");
        column.setPrefWidth(120);
        column.setCellValueFactory(param -> {
            StatFile statFile = param.getValue().getValue();
            return new SimpleStringProperty(statFile.getNotEmptyLinesInfo());
        });
        table.getColumns().add(column);

        fileTypeListView.setCellFactory(param -> {
            return new ListCell<Pair<String, SimpleBooleanProperty>>() {
                @Override
                protected void updateItem(Pair<String, SimpleBooleanProperty> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        CheckBox checkBox = new CheckBox(item.getKey());
                        checkBox.selectedProperty().bindBidirectional(item.getValue());
                        setGraphic(checkBox);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });
    }

    @FXML
    private void onChooseDir() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(table.getScene().getWindow());
        if (dir != null) {
            directory = dir;
            pathField.setText(directory.getAbsolutePath());
            analysisBtn.setDisable(false);
        }
    }

    @FXML
    private void onAnalysis() {
        StatFile statFile = new StatFile(directory, new ArrayList<>());
        List<StatFile> allFiles = statFile.flatFiles();
        List<Pair<String, SimpleBooleanProperty>> extensions = allFiles.stream()
                .filter(StatFile::isFile)
                .map(StatFile::getFileName)
                .map(FileUtils::getExtension)
                .distinct()
                .sorted()
                .map(ext -> new Pair<>(ext, new SimpleBooleanProperty(true)))
                .collect(Collectors.toList());

        extensions.stream()
                .map(Pair::getValue)
                .forEach(prop -> prop.addListener((observable, oldValue, newValue) -> {
                    statFile.setExtFilter(getSelectedExts());
                    updateTable(statFile);
                }));

        fileTypeListView.getItems().setAll(extensions);

        statFile.setExtFilter(getSelectedExts());
        updateTable(statFile);
    }

    private List<String> getSelectedExts() {
        List<String> selectedExts = fileTypeListView.getItems().stream()
                .filter(pair -> pair.getValue().getValue())
                .map(Pair::getKey)
                .collect(Collectors.toList());
        return selectedExts;
    }

    private void updateTable(StatFile statFile) {
        TreeItem<StatFile> tree = toTree(statFile);
        table.setRoot(tree);
    }

}
