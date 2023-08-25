package ru.isachenkoff.project_statistics;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;
import ru.isachenkoff.project_statistics.model.FileType;
import ru.isachenkoff.project_statistics.model.FileTypeStat;
import ru.isachenkoff.project_statistics.model.StatFile;
import ru.isachenkoff.project_statistics.model.StatFileRoot;
import ru.isachenkoff.project_statistics.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AnalysisController {

    @FXML
    private CheckBox selectAllCheck;
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
    private boolean selectAllCheckChange;

    public static TreeItem<StatFile> buildTree(StatFile statFile) {
        TreeItem<StatFile> treeItem = new TreeItem<>(statFile);
        treeItem.setExpanded(true);
        if (statFile.getFile().isDirectory()) {
            List<TreeItem<StatFile>> collect = statFile.getChildren().parallelStream()
                    .filter(StatFile::isVisible)
                    .map(AnalysisController::buildTree)
                    .collect(Collectors.toList());
            treeItem.getChildren().setAll(collect);
        }
        return treeItem;
    }

    @FXML
    private void initialize() {
        table.getColumns().clear();

        TreeTableColumn<StatFile, String> column;

        column = new TreeTableColumn<>("Путь файла");
        column.setPrefWidth(300);
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getFile().getName()));
        column.setCellFactory(param -> {
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
        table.getColumns().add(column);

        column = new TreeTableColumn<>("Всего строк");
        column.setPrefWidth(120);
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getTotalLinesInfo()));
        column.setStyle("-fx-alignment: CENTER_RIGHT");
        table.getColumns().add(column);

        column = new TreeTableColumn<>("Непустых строк");
        column.setPrefWidth(120);
        column.setCellValueFactory(param -> {
            StatFile statFile = param.getValue().getValue();
            return new SimpleStringProperty(statFile.getNotEmptyLinesInfo());
        });
        column.setStyle("-fx-alignment: CENTER_RIGHT");
        table.getColumns().add(column);

        fileTypeListView.setCellFactory(param -> {
            return new ListCell<Pair<FileTypeStat, SimpleBooleanProperty>>() {
                @Override
                protected void updateItem(Pair<FileTypeStat, SimpleBooleanProperty> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        String text = String.format("%s (%d)", item.getKey().getFileType().getTypeName(), item.getKey().getCount());
                        CheckBox checkBox = new CheckBox();
                        checkBox.selectedProperty().bindBidirectional(item.getValue());
                        ImageView imageView = new ImageView();
                        imageView.setPreserveRatio(true);
                        int size = 20;
                        imageView.setFitWidth(size);
                        imageView.setFitHeight(size);
                        imageView.setImage(item.getKey().getFileType().getImage());
                        VBox vBox = new VBox(imageView);
                        vBox.setAlignment(Pos.CENTER);
                        vBox.setMinSize(size, size);
                        Text textNode = new Text(text);
                        textNode.setFontSmoothingType(FontSmoothingType.LCD);
                        HBox hBox = new HBox(4, vBox, textNode);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        checkBox.setGraphic(hBox);
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
        selectAllCheck.setText(String.format("Выбрать все (%d)", statFileRoot.flatFiles().size()));

        List<Pair<FileTypeStat, SimpleBooleanProperty>> fileTypesBoolPairs =
                statFileRoot.getFileTypesStatistics().stream()
                        .map(fileTypeStat -> new Pair<>(fileTypeStat, new SimpleBooleanProperty(true)))
                        .collect(Collectors.toList());

        fileTypesBoolPairs.stream()
                .map(Pair::getValue)
                .forEach(prop -> prop.addListener((observable, oldValue, newValue) -> {
                    if (!selectAllCheckChange) {
                        statFileRoot.setExtFilter(getSelectedFileTypes());
                        rebuildTable(statFileRoot);
                    }
                }));

        fileTypeListView.getItems().setAll(fileTypesBoolPairs);

        statFileRoot.setExtFilter(getSelectedFileTypes());
        selectAllCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            selectAllCheckChange = true;
            for (Pair<FileTypeStat, SimpleBooleanProperty> item : fileTypeListView.getItems()) {
                item.getValue().set(newValue);
            }
            selectAllCheckChange = false;
            statFileRoot.setExtFilter(getSelectedFileTypes());
            rebuildTable(statFileRoot);
        });
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
                .map(pair -> pair.getKey().getFileType().getExtension())
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
