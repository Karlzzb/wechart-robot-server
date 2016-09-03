package com.karl.fx;

import java.util.ResourceBundle;

public enum FxmlView {

    MAIN {
        @Override
        String getTitle() {
            return getStringFromResourceBundle("main.app.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/Main.fxml";
        }
    },
    LOGIN {
        @Override
        String getTitle() {
            return getStringFromResourceBundle("/fxml/login.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/Login.fxml";
        }
    },
    CONSOLE {
        @Override
        String getTitle() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        String getFxmlFile() {
            // TODO Auto-generated method stub
            return null;
        }
    };

    abstract String getTitle();

    abstract String getFxmlFile();

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
