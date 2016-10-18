package com.karl.fx;

import java.io.IOException;
import java.util.Objects;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.karl.service.PcClient;
import com.karl.service.WebWechat;

public class StageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageManager.class);
    private final Stage primaryStage;
    private final SpringFXMLLoader springFXMLLoader;

    public StageManager(Stage primaryStage, SpringFXMLLoader springFXMLLoader, final WebWechat webWechat, final PcClient pcClient) {
        this.primaryStage = primaryStage;
        this.springFXMLLoader = springFXMLLoader;
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	webWechat.setStopRequested(Boolean.FALSE);
            	if(pcClient != null) {
            		pcClient.destory();
            	}
            	System.exit(0);
            }
        });
    }

    public void switchScene(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
//        show(viewRootNodeHierarchy, view.getTitle());
        show(viewRootNodeHierarchy, "翱翔出品");
    }
    
    public void popupWindow(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        popup(viewRootNodeHierarchy, view.getTitle());
    }
    
    private void popup(final Parent rootnode, String title) {
        Scene scene = new Scene(rootnode);
        Stage newStage = new Stage();
        newStage.setTitle(title);
        newStage.initModality(Modality.NONE);
        newStage.initOwner(primaryStage);
        newStage.setScene(scene);
        newStage.sizeToScene();
        newStage.centerOnScreen();
        try {
        	newStage.show();
        } catch (Exception e) {
            LOGGER.error("Uable to show scene for title " + title, e);
        }
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

    public Parent loadViewNodeHierarchy(String fxmlFile) {
        Parent rootNode = null;
        try {
            rootNode = springFXMLLoader.load(fxmlFile);
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception e) {
            LOGGER.error("Uable to load FXML view " + fxmlFile, e);
        }
        return rootNode;
    }

    public void loadAnchorPaneMemu(AnchorPane ap, final FxmlView view) {
        try {
            AnchorPane p = (AnchorPane) springFXMLLoader.load(view.getFxmlFile());
            ap.getChildren().setAll(p);
        } catch (IOException e) {
            LOGGER.error("Load menu failed!", e);
        }
    }

	public Stage getPrimaryStage() {
		return primaryStage;
	}
}
