package com.karl;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.karl.fx.FxmlView;
import com.karl.fx.StageManager;
import com.karl.service.WebWechat;

@SpringBootApplication
public class Main extends Application {
	private ConfigurableApplicationContext springContext;

	private StageManager stageManager;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		bootstapSpringApplicationContext(stage);
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
//		stageManager.switchSceneLogin(FxmlView.CERTIFICATE);
		stageManager.switchSceneLogin(FxmlView.BLUELOGIN);
//		 stageManager.switchSceneLogin(FxmlView.LOGIN);
//		 stageManager.switchScene(FxmlView.MENU);
	}

	private void bootstapSpringApplicationContext(Stage stage) {
		try {
			SpringApplicationBuilder builder = new SpringApplicationBuilder(
					Main.class);
			builder.web(false);
			String[] args = getParameters().getRaw().toArray(new String[] {});
			builder.headless(false);
			springContext = builder.run(args);
			stageManager = springContext.getBean(StageManager.class, stage);
			displayINitialScene();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
