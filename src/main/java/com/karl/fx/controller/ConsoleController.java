package com.karl.fx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsoleController extends FxmlController {

    @FXML
    private VBox consoleTab;

    @FXML
    private TextArea logArea;

    @Autowired
    private MainController mainViewController;

    public TextArea getLogArea() {
        return logArea;
    }

    public void setLogArea(TextArea logArea) {
        this.logArea = logArea;
    }

    public void injectMainViewController(MainController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize() {
        webWechat.buildWechat(logArea);

    }

}
