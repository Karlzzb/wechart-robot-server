package com.karl.fx;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringFXMLLoader {

    private final ResourceBundle resourceBundle;
    private final ApplicationContext context;

    @Autowired
    public SpringFXMLLoader(ResourceBundle resourceBundle, ApplicationContext context) {
        this.resourceBundle = resourceBundle;
        this.context = context;
    }

    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        // Spring now FXML Controller Factory
        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
            public Object call(Class<?> aClass) {
                return context.getBean(aClass);
            }
        });
        fxmlLoader.setResources(resourceBundle);
        fxmlLoader.setLocation(getClass().getResource(fxmlPath));

        Parent node = (Parent) fxmlLoader.load();

        return node;
    }
}
