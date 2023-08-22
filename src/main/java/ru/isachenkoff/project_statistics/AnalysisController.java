package ru.isachenkoff.project_statistics;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;
import ru.isachenkoff.project_statistics.model.FileTypeStat;
import ru.isachenkoff.project_statistics.model.StatFile;
import ru.isachenkoff.project_statistics.model.StatFileRoot;
import ru.isachenkoff.project_statistics.util.FileTypesIcons;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AnalysisController {

    @FXML
    private CheckBox emptyDirsCheck;
    @FXML
    private CheckBox textFilesOnlyCheck;
    @FXML
    private ListView<Pair<FileTypeStat, SimpleBooleanProperty>> fileTypeListView;
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
                    .map(AnalysisController::buildTree)
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
            return new ListCell<Pair<FileTypeStat, SimpleBooleanProperty>>() {
                @Override
                protected void updateItem(Pair<FileTypeStat, SimpleBooleanProperty> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        String text = String.format("%s (%d)", item.getKey().getFileType(), item.getKey().getCount());
                        CheckBox checkBox = new CheckBox();
                        checkBox.selectedProperty().bindBidirectional(item.getValue());
                        ImageView imageView = new ImageView();
                        imageView.setPreserveRatio(true);
                        imageView.setFitWidth(16);
                        imageView.setFitHeight(16);
                        imageView.setImage(FileTypesIcons.getIconForType(item.getKey().getFileType()));
                        setGraphic(new HBox(checkBox, new HBox(4, imageView, new Label(text))));
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
            setDirectory(directory);
            MainController.getInstance().getSelectedTab().setText(directory.getAbsolutePath());
        }
    }

    @FXML
    private void onAnalysis() {
        analysis();
    }

    public void analysis() {
        statFileRoot = new StatFileRoot(directory);

        List<Pair<FileTypeStat, SimpleBooleanProperty>> fileTypesBoolPairs =
                statFileRoot.getFileTypesStatistics().stream()
                        .map(fileTypeStat -> new Pair<>(fileTypeStat, new SimpleBooleanProperty(true)))
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

    public void setDirectory(File dir) {
        this.directory = dir;
        pathField.setText(this.directory.getAbsolutePath());
        analysisBtn.setDisable(false);
    }

    private List<String> getSelectedFileTypes() {
        return fileTypeListView.getItems().stream()
                .filter(pair -> pair.getValue().get())
                .map(pair -> pair.getKey().getFileType())
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
