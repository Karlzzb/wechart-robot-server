package com.karl;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;

import com.karl.fx.FxmlView;
import com.karl.fx.StageManager;
import com.karl.service.WebWechat;

@SpringBootApplication
@Lazy
public class Main extends Application {
	private ConfigurableApplicationContext springContext;

	private StageManager stageManager;

	public static void main(String[] args) {
		Application.launch(args);
	}

	private void buildWelcomePage(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(
				"/fxml/splash.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Stage loadingStage = new Stage();
//		buildWelcomePage(loadingStage);
		bootstapSpringApplicationContext(stage, loadingStage);
	}

	@Override
	public void stop() {
		WebWechat webWechat = springContext.getBean(WebWechat.class);
		if (webWechat != null) {
			webWechat.stopListen();
		}
		springContext.close();
	}

	protected void displayINitialScene() {
		stageManager.switchScene(FxmlView.LOGIN);
		// stageManager.switchScene(FxmlView.MENU);
	}

	private void bootstapSpringApplicationContext(Stage stage,
			Stage loadingStage) {
		try {
			SpringApplicationBuilder builder = new SpringApplicationBuilder(
					Main.class);
			builder.web(false);
			String[] args = getParameters().getRaw().toArray(new String[] {});
			builder.headless(false);
			springContext = builder.run(args);
			stageManager = springContext.getBean(StageManager.class, stage);
			displayINitialScene();
			loadingStage.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
