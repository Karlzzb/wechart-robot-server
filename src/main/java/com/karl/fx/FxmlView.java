package com.karl.fx;

import java.util.ResourceBundle;

import javafx.stage.StageStyle;

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
    }, LUCKTABLE{
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("luckinfo.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/LuckInfo.fxml";
        }    	
    }, BLUELOGIN{
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("bluelogin.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/BlueLogin.fxml";
        }
        
        @Override
        public StageStyle getStageStyle() {
        	return StageStyle.UNDECORATED;
        }
    }, STATS{
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("stats.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/GameStatsBoard.fxml";
        }
    };

    public abstract String getTitle();

    public abstract String getFxmlFile();
    
    public StageStyle getStageStyle() {
    	return StageStyle.DECORATED;
    }

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
