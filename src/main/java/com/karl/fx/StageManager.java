package com.karl.fx;

import java.io.IOException;
import java.util.Objects;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.karl.domain.RuntimeDomain;
import com.karl.service.WebWechat;

public class StageManager {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(StageManager.class);
	private final Stage primaryStage;
	private Stage loginStage;
	private final SpringFXMLLoader springFXMLLoader;

	public StageManager(Stage primaryStage, SpringFXMLLoader springFXMLLoader,
			WebWechat webWechat) {
		this.primaryStage = primaryStage;
		this.springFXMLLoader = springFXMLLoader;
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				webWechat.setStopRequested(Boolean.FALSE);
				System.exit(0);
			}
		});
	}

	public void switchSceneLogin(final FxmlView view) {
		try {
			if (loginStage != null) {
				loginStage.close();
				loginStage = null;
			}
			Parent rootNode = springFXMLLoader.load(view.getFxmlFile());
			loginStage = new Stage();
			Scene scene = loginStage.getScene();
			if (scene == null) {
				scene = new Scene(rootNode);
			}
			scene.setRoot(rootNode);
			loginStage.setTitle(view.getTitle());
			loginStage.setScene(scene);
			loginStage.initStyle(view.getStageStyle());
			loginStage.sizeToScene();
			loginStage.centerOnScreen();
			loginStage.show();
		} catch (Exception e) {
			LOGGER.error("Uable to show scene for title " + view.getTitle(), e);
		}
	}
	
	public void retryLogin(final FxmlView view) {
		try {
			Parent rootNode = springFXMLLoader.load(view.getFxmlFile());
			Stage retryLogin = new Stage();
			Scene scene = retryLogin.getScene();
			if (scene == null) {
				scene = new Scene(rootNode);
			}
			scene.setRoot(rootNode);
			retryLogin.setTitle(view.getTitle());
			retryLogin.setScene(scene);
			retryLogin.initStyle(view.getStageStyle());
			retryLogin.sizeToScene();
			retryLogin.centerOnScreen();
			retryLogin.show();
		} catch (Exception e) {
			LOGGER.error("Uable to show scene for title " + view.getTitle(), e);
		}
	}

	public void switchScene(final FxmlView view) {
		if (loginStage != null) {
			loginStage.close();
			loginStage = null;
		}
		Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
		show(viewRootNodeHierarchy, view.getTitle(), view.getStageStyle());
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

	private void show(final Parent rootnode, String title, StageStyle stageStyle) {
		Scene scene = prepareScene(rootnode);
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.initStyle(stageStyle);
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
			Objects.requireNonNull(rootNode,
					"A Root FXML node must not be null");
		} catch (Exception e) {
			LOGGER.error("Uable to load FXML view " + fxmlFile, e);
		}
		return rootNode;
	}

	public void popMessageWindow(RuntimeDomain runtimeDomain) {
		Scene scene = new Scene(
				loadViewNodeHierarchy(FxmlView.MESSAGE.getFxmlFile()));
		Stage newStage = new Stage();
		// newStage.initStyle(StageStyle.UNIFIED);
		newStage.setTitle(FxmlView.MESSAGE.getTitle());
		newStage.initModality(Modality.NONE);
		newStage.initOwner(getPrimaryStage());
		newStage.setScene(scene);
		newStage.sizeToScene();
		newStage.centerOnScreen();
		try {
			newStage.show();
		} catch (Exception e) {
			LOGGER.error(
					"Uable to show scene for title "
							+ FxmlView.MESSAGE.getTitle(), e);
		}
		newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				runtimeDomain.setMessageBoardCount(0);
			}
		});
	}

	public void popLuckInfoWindow(RuntimeDomain runtimeDomain) {
		Scene scene = new Scene(
				loadViewNodeHierarchy(FxmlView.LUCKTABLE.getFxmlFile()));
		Stage luckInfoStage = new Stage();
		luckInfoStage.setTitle(FxmlView.LUCKTABLE.getTitle());
		luckInfoStage.initModality(Modality.NONE);
		luckInfoStage.initOwner(getPrimaryStage());
		luckInfoStage.setScene(scene);
		luckInfoStage.sizeToScene();
		luckInfoStage.centerOnScreen();
		try {
			luckInfoStage.show();
		} catch (Exception e) {
			LOGGER.error(
					"Uable to show scene for title "
							+ FxmlView.LUCKTABLE.getTitle(), e);
		}
//		luckInfoStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//			public void handle(WindowEvent we) {
//			}
//		});
	}

	public void loadAnchorPaneMemu(AnchorPane ap, final FxmlView view) {
		try {
			AnchorPane p = (AnchorPane) springFXMLLoader.load(view
					.getFxmlFile());
			ap.getChildren().setAll(p);
		} catch (IOException e) {
			LOGGER.error("Load menu failed!", e);
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}
}
