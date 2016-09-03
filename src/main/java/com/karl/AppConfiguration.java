package com.karl;

import java.util.ResourceBundle;

import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.karl.fx.SpringFXMLLoader;
import com.karl.fx.StageManager;

@Configuration
public class AppConfiguration {
    @Autowired
    SpringFXMLLoader springFXMLLoader;

    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("Bundle");
    }

    @Bean
    @Lazy(value = true)
    // stage only created after Spring context bootstrap
    public StageManager stageManager(Stage stage) {
        return new StageManager(stage, springFXMLLoader);
    }

}
