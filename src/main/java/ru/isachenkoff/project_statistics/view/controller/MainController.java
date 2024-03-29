package ru.isachenkoff.project_statistics.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.DirectoryChooser;
import ru.isachenkoff.project_statistics.Main;

import java.io.File;
import java.io.IOException;

public class MainController {

    private static MainController instance;

    @FXML
    private TabPane tabPane;

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }

    @FXML
    private void onNew() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(tabPane.getScene().getWindow());
        if (dir != null) {
            showNewAnalysis(dir);
        }
    }

    public void showNewAnalysis(File dir) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/analysis.fxml"));
        Parent load;
        AnalysisController controller;
        try {
            load = fxmlLoader.load();
            controller = fxmlLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newTab(dir.getAbsolutePath(), load);
        controller.setDirectory(dir);
        controller.analysis();
    }

    private void newTab(String title, Parent load) {
        Tab signInTab = new Tab(title);
        signInTab.setContent(load);
        tabPane.getTabs().add(signInTab);
        tabPane.getSelectionModel().select(signInTab);
    }

    public Tab getSelectedTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

}
