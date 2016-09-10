package com.karl;

import java.io.IOException;

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
    public void init() throws IOException {
        springContext = bootstapSpringApplicationContext();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stageManager = springContext.getBean(StageManager.class, stage);
        displayINitialScene();
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
        stageManager.switchScene(FxmlView.MAIN);
    }

    private ConfigurableApplicationContext bootstapSpringApplicationContext() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        String[] args = getParameters().getRaw().toArray(new String[] {});
        builder.headless(false);
        return builder.run(args);
    }
}
