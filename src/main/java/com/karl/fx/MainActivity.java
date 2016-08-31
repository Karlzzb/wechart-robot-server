package com.karl.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import blade.kit.StringKit;
import blade.kit.json.JSONObject;

import com.karl.service.WebWechat;

public class MainActivity extends Application {

    private WebWechat myapp;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();

        assembleGroupBox(root);

        Scene scene = new Scene(root, 300, 275);
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }

    private void assembleGroupBox(StackPane root) {

        ChoiceBox<String> cb = new ChoiceBox<String>(getGroupBox());

        root.getChildren().add(cb);

    }

    private ObservableList<String> getGroupBox() {

        Map<String, JSONObject> allGroups = null;

        List<String> groupNames = new ArrayList<String>();
        final Map<String, String> groupIds = new HashMap<String, String>();

        String name = "unknown";
        for (String groupId : allGroups.keySet()) {
            if (StringKit.isNotBlank(allGroups.get(groupId).getString("RemarkName"))) {
                name = allGroups.get(groupId).getString("RemarkName");
            } else {
                name = allGroups.get(groupId).getString("NickName");
            }
            groupNames.add(name);
            groupIds.put(name, groupId);
        }

        return FXCollections.observableArrayList();

    }

    public static void main(String[] args) {
        Application.launch(MainActivity.class, args);
    }

    public WebWechat getMyapp() {
        return myapp;
    }

    public void setMyapp(WebWechat myapp) {
        this.myapp = myapp;
    }

}
