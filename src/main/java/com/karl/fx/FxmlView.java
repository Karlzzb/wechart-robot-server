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
            return getStringFromResourceBundle("/fxml/login.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/RawLogin.fxml";
        }
    },
    MENU {
        @Override
        String getTitle() {
            return getStringFromResourceBundle("/fxml/login.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/Menu.fxml";
        }
    },
    PLAYER {
        @Override
        String getTitle() {
            return getStringFromResourceBundle("/fxml/login.title");
        }

        @Override
        String getFxmlFile() {
            return "/fxml/Player.fxml";
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
            return "/fxml/ConsoleTab.fxml";
        }
    };

    abstract String getTitle();

    abstract String getFxmlFile();

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
