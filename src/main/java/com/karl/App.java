package com.karl;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.karl.domain.RuntimeDomain;
import com.karl.service.WebWechat;

public class App extends Application {

    private String windowTitle;

    private static Stage primarySate;

    private static BorderPane mainLayout;

    private WebWechat webWechat;

    @Override
    public void start(Stage primarySate) throws Exception {
        App.setPrimarySate(primarySate);
        App.getPrimarySate().setTitle(windowTitle);
        this.webWechat = new WebWechat(new RuntimeDomain());
        showMainView();
    }

    private void showMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/MainView.fxml"));
        mainLayout = (BorderPane) loader.load();
        Scene scene = new Scene(mainLayout);
        getPrimarySate().setScene(scene);
        getPrimarySate().show();
    }

    public static void homeMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/MainView.fxml"));
        BorderPane consoleLayout = (BorderPane) loader.load();
        mainLayout.setCenter(consoleLayout);
    }

    public static void showConsole() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/ConsoleView.fxml"));
        BorderPane consoleLayout = (BorderPane) loader.load();
        mainLayout.setCenter(consoleLayout);
    }

    public static void showConsoleState() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/ConsoleView.fxml"));
        BorderPane consoleLayout = (BorderPane) loader.load();
        Stage consoleStage = new Stage();
        consoleStage.setTitle("The dialog console");
        consoleStage.initModality(Modality.NONE);
        consoleStage.initOwner(getPrimarySate());
        Scene scene = new Scene(consoleLayout);
        consoleStage.setScene(scene);
        consoleStage.showAndWait();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimarySate() {
        return primarySate;
    }

    public static void setPrimarySate(Stage primarySate) {
        App.primarySate = primarySate;
    }
}
