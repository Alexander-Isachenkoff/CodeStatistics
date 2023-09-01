package ru.isachenkoff.project_statistics.view.controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import ru.isachenkoff.project_statistics.model.FileTypeStat;
import ru.isachenkoff.project_statistics.model.StatFile;
import ru.isachenkoff.project_statistics.model.StatFileRoot;
import ru.isachenkoff.project_statistics.util.SystemClipboard;
import ru.isachenkoff.project_statistics.view.util.SelectableListCellFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnalysisController {

    public VBox analysisPane;
    public ProgressIndicator progress;
    @FXML
    private TreeTableView<StatFile> filesTreeTableView;
    @FXML
    private CheckBox selectAllCheck;
    @FXML
    private CheckBox emptyDirsCheck;
    @FXML
    private CheckBox textFilesOnlyCheck;
    @FXML
    private ListView<FileTypeStat> fileTypeListView;
    @FXML
    private Button analysisBtn;
    @FXML
    private TextField pathField;
    @FXML
    private TableView<FileTypeStat> fileTypesTable;
    @FXML
    private PieChart pieChart;

    private File directory;
    private StatFileRoot statFileRoot;

    public static TreeItem<StatFile> buildTree(StatFile statFile) {
        TreeItem<StatFile> treeItem = new TreeItem<>(statFile);
        treeItem.setExpanded(true);
        if (statFile.isDirectory()) {
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
        fileTypeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileTypeListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<FileTypeStat>) c -> {
            List<FileTypeStat> stats = new ArrayList<>(c.getList());
            List<String> extensions = stats.stream()
                    .map(stat -> stat.getFileType().getExtension())
                    .collect(Collectors.toList());
            statFileRoot.setExtFilter(extensions);
            updateViews(stats);
        });
        fileTypeListView.setCellFactory(new SelectableListCellFactory<FileTypeStat>() {
            @Override
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
        });
    }

    @FXML
    private void onChooseDir() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(filesTreeTableView.getScene().getWindow());
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
        analysisPane.setDisable(true);
        progress.setVisible(true);

        new Thread(() -> {
            analysisInner();
            analysisPane.setDisable(false);
            progress.setVisible(false);
        }).start();
    }

    private void analysisInner() {
        statFileRoot = new StatFileRoot(directory);
        statFileRoot.setExtFilter(statFileRoot.getAllFileTypes());

        selectAllCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                fileTypeListView.getSelectionModel().selectAll();
            } else {
                fileTypeListView.getSelectionModel().clearSelection();
            }
        });
        emptyDirsCheck.selectedProperty().bindBidirectional(statFileRoot.emptyDirsProperty());
        textFilesOnlyCheck.selectedProperty().bindBidirectional(statFileRoot.textFilesOnlyProperty());

        List<FileTypeStat> fileTypesStatistics = statFileRoot.getFileTypesStatistics();

        Platform.runLater(() -> {
            selectAllCheck.setText(String.format("Выбрать все (%d)", statFileRoot.flatFiles().size()));
            fileTypeListView.getItems().setAll(fileTypesStatistics);
            selectAllCheck.setSelected(true);
            fileTypeListView.getSelectionModel().selectAll();
        });
    }

    private void updateViews(List<FileTypeStat> fileTypeStats) {
        System.out.println("updateViews");
        rebuildFilesTreeTable();
        updateFileTypesTable(fileTypeStats);
        updatePieChart(fileTypeStats);
    }

    private void updateFileTypesTable(List<FileTypeStat> fileTypeStats) {
        fileTypesTable.getItems().setAll(fileTypeStats);
    }

    public void setDirectory(File dir) {
        this.directory = dir;
        pathField.setText(this.directory.getAbsolutePath());
        analysisBtn.setDisable(false);
    }

    private void rebuildFilesTreeTable() {
        TreeItem<StatFile> tree = buildTree(statFileRoot);
        Platform.runLater(() -> filesTreeTableView.setRoot(tree));
    }

    private void updatePieChart(List<FileTypeStat> fileTypeStats) {
        List<PieChart.Data> data = fileTypeStats.stream()
                .filter(stat -> stat.getLinesCount() > 0)
                .sorted((e1, e2) -> -Integer.compare(e1.getLinesCount(), e2.getLinesCount()))
                .map(stat -> new PieChart.Data(stat.getFileType().getTypeName() + " (" + stat.getLinesCount() + ")", stat.getLinesCount()))
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            pieChart.getData().setAll(data);
            pieChart.setTitle(directory.getAbsolutePath());
        });
    }

    @FXML
    private void onEmptyDirs() {
        if (statFileRoot != null) {
            rebuildFilesTreeTable();
        }
    }

    @FXML
    private void onTextFiles() {
        if (statFileRoot != null) {
            rebuildFilesTreeTable();
        }
    }

    @FXML
    private void onCopy() {
        int width = (int) pieChart.getWidth();
        int height = (int) pieChart.getHeight();
        WritableImage image = new WritableImage(width, height);
        pieChart.snapshot(new SnapshotParameters(), image);
        SystemClipboard.setContent(SwingFXUtils.fromFXImage(image, new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)));

        Tooltip tooltip = new Tooltip("Скопировано в буфер обмена");
        double prefWidth = 160;
        tooltip.setPrefWidth(prefWidth);
        double xPos = pieChart.localToScene(pieChart.getLayoutBounds()).getMinX() + pieChart.getScene().getWindow().getX() + pieChart.getWidth() / 2 - prefWidth / 2;
        double yPos = pieChart.localToScene(pieChart.getLayoutBounds()).getMinY() + pieChart.getScene().getWindow().getY() + pieChart.getHeight() / 2;
        tooltip.show(pieChart, xPos, yPos);
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(tooltip::hide);
        }).start();
    }

}
