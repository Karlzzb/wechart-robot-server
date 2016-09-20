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
            return "/fxml/MainDesk.fxml";
        }
    },
    LOGIN {
        @Override
        String getTitle() {
            return getStringFromResourceBundle("login.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/RawLogin.fxml";
        }
    },
    MENU {
        @Override
        String getTitle() {
            return getStringFromResourceBundle("menu.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/Menu.fxml";
        }
    },
    CONSOLE {
        @Override
        String getTitle() {
            return getStringFromResourceBundle("console.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/ConsoleTab.fxml";
        }
    }, CONFIG{
        @Override
        String getTitle() {
            return getStringFromResourceBundle("config.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/Configuration.fxml";
        }
    };

    abstract String getTitle();

    abstract String getFxmlFile();

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
