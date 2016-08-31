package com.karl;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Value;

import com.karl.service.WebWechat;

public class App extends Application {

    @Value("${app.ui.title}")
    private String windowTitle;

    private Stage primarySate;

    private static BorderPane mainLayout;

    // @Autowired
    private WebWechat webWechat;

    @Override
    public void start(Stage primarySate) throws Exception {
        this.primarySate = primarySate;
        this.primarySate.setTitle(windowTitle);
        // this.webWechat = new WebWechat(new RuntimeDomain());
        showMainView();
    }

    private void showMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/MainView.fxml"));
        mainLayout = (BorderPane) loader.load();
        Scene scene = new Scene(mainLayout);
        primarySate.setScene(scene);
        primarySate.show();
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

    // public static void showConsole() throws IOException {
    // FXMLLoader loader = new FXMLLoader();
    // loader.setLocation(App.class.getResource("view/MainView.fxml"));
    // BorderPane mainLayout = (BorderPane) loader.load();
    // mainLayout.setCenter(mainLayout);
    // }

    public static void main(String[] args) {
        launch(args);
    }
}
