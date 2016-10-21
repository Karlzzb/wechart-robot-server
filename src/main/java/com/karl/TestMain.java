package com.karl;

import javafx.application.Application;
import javafx.stage.Stage;

import org.jboss.jandex.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.karl.fx.FxmlView;
import com.karl.fx.StageManager;

@SpringBootApplication
public class TestMain  {
    public static void main(String[] args) {
      Application.launch(args);
    }
    
	public static void start(Stage stage) {
		try {
		ConfigurableApplicationContext springContext = bootstapSpringApplicationContext();
		StageManager stageManager = springContext.getBean(StageManager.class,
				stage);
		stageManager.switchScene(FxmlView.LOGIN);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

    private static ConfigurableApplicationContext bootstapSpringApplicationContext() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        builder.web(false);
        builder.headless(false);
        return builder.run("");
    }
}
