package com.karl.fx;

import java.util.ResourceBundle;

public enum FxmlView {
    MAIN {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("main.app.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/MainDesk.fxml";
        }
    },
    LOGIN {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("login.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/RawLogin.fxml";
        }
    },
    MENU {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("menu.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Menu.fxml";
        }
    },
    CONSOLE {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("console.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ConsoleTab.fxml";
        }
    }, CONFIG{
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("config.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Configuration.fxml";
        }
    }, MESSAGE{
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("message.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Message.fxml";
        }
    };

    public abstract String getTitle();

    public abstract String getFxmlFile();

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
