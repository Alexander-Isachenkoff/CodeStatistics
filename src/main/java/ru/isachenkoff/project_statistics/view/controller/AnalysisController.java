package ru.isachenkoff.project_statistics.view.controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.isachenkoff.project_statistics.model.FileTypeStat;
import ru.isachenkoff.project_statistics.model.StatFile;
import ru.isachenkoff.project_statistics.model.StatFileRoot;
import ru.isachenkoff.project_statistics.util.SystemClipboard;
import ru.isachenkoff.project_statistics.view.FileTypeStatCheckBoxListCell;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalysisController {

    @FXML
    private VBox analysisPane;
    @FXML
    private ProgressIndicator progress;
    @FXML
    private ComboBox<ChartType> chartTypeCmb;
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
        fileTypeListView.setCellFactory(param -> new FileTypeStatCheckBoxListCell() {
            @Override
            public void onCheckBoxAction(FileTypeStat fileTypeStat) {
                update();
            }
        });

        chartTypeCmb.getItems().setAll(ChartType.values());
        chartTypeCmb.getSelectionModel().select(ChartType.LINES);
        chartTypeCmb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePieChart(getSelectedFileTypes()));
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

        selectAllCheck.setOnAction(event -> {
            for (FileTypeStat fileTypeStat : fileTypeListView.getItems()) {
                fileTypeStat.getVisibleProperty().set(selectAllCheck.isSelected());
            }
            update();
        });

        List<FileTypeStat> fileTypesStatistics = statFileRoot.getFileTypesStatistics();

        Platform.runLater(() -> {
            selectAllCheck.setText(String.format("Выбрать все (%d)", statFileRoot.flatFiles().size()));
            fileTypeListView.getItems().setAll(fileTypesStatistics);
            update();
        });
    }

    private void update() {
        updateViews(getSelectedFileTypes());
    }

    private List<FileTypeStat> getSelectedFileTypes() {
        return fileTypeListView.getItems().stream()
                .filter(FileTypeStat::isVisible)
                .collect(Collectors.toList());
    }

    private void updateViews(List<FileTypeStat> fileTypeStats) {
        System.out.println("updateViews");
        rebuildFilesTreeTable(fileTypeStats);
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

    private void rebuildFilesTreeTable(List<FileTypeStat> fileTypeStats) {
        List<String> extensions = fileTypeStats.stream()
                .map(stat -> stat.getFileType().getExtension())
                .collect(Collectors.toList());
        statFileRoot.setExtFilter(extensions);
        statFileRoot.setEmptyDirs(emptyDirsCheck.isSelected());
        statFileRoot.setTextFilesOnly(textFilesOnlyCheck.isSelected());
        TreeItem<StatFile> tree = buildTree(statFileRoot);
        Platform.runLater(() -> filesTreeTableView.setRoot(tree));
    }

    private void updatePieChart(List<FileTypeStat> fileTypeStats) {
        ChartType chartType = chartTypeCmb.getSelectionModel().getSelectedItem();
        Function<FileTypeStat, Integer> viewFunction = chartType.getViewFunction();

        List<PieChart.Data> data = fileTypeStats.stream()
                .filter(stat -> stat.getLinesCount() > 0)
                .sorted((e1, e2) -> -Integer.compare(viewFunction.apply(e1), viewFunction.apply(e2)))
                .map(stat -> new PieChart.Data(stat.getFileType().getTypeName() + " (" + viewFunction.apply(stat) + ")", viewFunction.apply(stat)))
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            pieChart.getData().setAll(data);
            pieChart.setTitle(directory.getAbsolutePath());
        });
    }

    @FXML
    private void onEmptyDirs() {
        if (statFileRoot != null) {
            rebuildFilesTreeTable(getSelectedFileTypes());
        }
    }

    @FXML
    private void onTextFiles() {
        if (statFileRoot != null) {
            rebuildFilesTreeTable(getSelectedFileTypes());
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

    @AllArgsConstructor
    @Getter
    private enum ChartType {
        FILES("Файлы", FileTypeStat::getFilesCount),
        LINES("Строки", FileTypeStat::getLinesCount),
        NOT_EMPTY_LINES("Непустые строки", FileTypeStat::getNotEmptyLinesCount);

        private final String name;
        private final Function<FileTypeStat, Integer> viewFunction;

        @Override
        public String toString() {
            return name;
        }
    }

}
