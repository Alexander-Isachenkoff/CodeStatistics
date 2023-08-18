package ru.isachenkoff.project_statistics;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;
import ru.isachenkoff.project_statistics.model.StatFile;
import ru.isachenkoff.project_statistics.model.StatFileRoot;
import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private CheckBox emptyDirsCheck;
    @FXML
    private CheckBox textFilesOnlyCheck;
    @FXML
    private ListView<Pair<String, SimpleBooleanProperty>> fileTypeListView;
    @FXML
    private Button analysisBtn;
    @FXML
    private TextField pathField;
    @FXML
    private TreeTableView<StatFile> table;

    private File directory;
    private StatFileRoot statFileRoot;

    public static TreeItem<StatFile> buildTree(StatFile statFile) {
        TreeItem<StatFile> treeItem = new TreeItem<>(statFile);
        treeItem.setExpanded(true);
        if (statFile.getFile().isDirectory()) {
            List<TreeItem<StatFile>> collect = statFile.getChildren().stream()
                    .filter(StatFile::isVisible)
                    .map(MainController::buildTree)
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
                        long count = statFileRoot.flatFiles().stream()
                                .filter(statFile -> FileUtils.getExtension(statFile.getFileName()).equals(item.getKey()))
                                .count();
                        String text = String.format("%s (%d)", item.getKey(), count);
                        CheckBox checkBox = new CheckBox(text);
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
        statFileRoot = new StatFileRoot(directory);

        List<Pair<String, SimpleBooleanProperty>> fileTypesBoolPairs =
                statFileRoot.getAllFileTypes().stream()
                        .map(ext -> new Pair<>(ext, new SimpleBooleanProperty(true)))
                        .collect(Collectors.toList());

        fileTypesBoolPairs.stream()
                .map(Pair::getValue)
                .forEach(prop -> prop.addListener((observable, oldValue, newValue) -> {
                    statFileRoot.setExtFilter(getSelectedFileTypes());
                    rebuildTable(statFileRoot);
                }));

        fileTypeListView.getItems().setAll(fileTypesBoolPairs);

        statFileRoot.setExtFilter(getSelectedFileTypes());
        emptyDirsCheck.selectedProperty().bindBidirectional(statFileRoot.emptyDirsProperty());
        textFilesOnlyCheck.selectedProperty().bindBidirectional(statFileRoot.textFilesOnlyProperty());

        rebuildTable(statFileRoot);
    }

    private List<String> getSelectedFileTypes() {
        return fileTypeListView.getItems().stream()
                .filter(pair -> pair.getValue().get())
                .map(Pair::getKey)
                .collect(Collectors.toList());
    }

    private void rebuildTable(StatFile statFile) {
        TreeItem<StatFile> tree = buildTree(statFile);
        table.setRoot(tree);
    }

    @FXML
    private void onEmptyDirs() {
        if (statFileRoot != null) {
            rebuildTable(statFileRoot);
        }
    }

    @FXML
    private void onTextFiles() {
        if (statFileRoot != null) {
            rebuildTable(statFileRoot);
        }
    }

}
