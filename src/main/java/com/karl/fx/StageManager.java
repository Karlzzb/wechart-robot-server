package com.karl.fx;

import java.util.Objects;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageManager.class);
    private final Stage primaryStage;
    private final SpringFXMLLoader springFXMLLoader;

    public StageManager(Stage primaryStage, SpringFXMLLoader springFXMLLoader) {
        this.primaryStage = primaryStage;
        this.springFXMLLoader = springFXMLLoader;
    }

    public void switchScene(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        show(viewRootNodeHierarchy, view.getFxmlFile());
    }

    private void show(final Parent rootnode, String title) {
        Scene scene = prepareScene(rootnode);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        try {
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.error("Uable to show scene for title " + title, e);
        }
    }

    private Scene prepareScene(Parent rootNode) {
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(rootNode);
        }
        scene.setRoot(rootNode);

        return scene;
    }

    private Parent loadViewNodeHierarchy(String fxmlFile) {
        Parent rootNode = null;
        try {
            rootNode = springFXMLLoader.load(fxmlFile);
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception e) {
            LOGGER.error("Uable to load FXML view " + fxmlFile, e);
        }
        return rootNode;
    }
}
