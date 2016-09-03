package com.karl.fx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import org.springframework.stereotype.Component;

@Component
public class ConsoleController {

    @FXML
    private VBox vBox;

    @FXML
    private TextArea logArea;

    public TextArea getLogArea() {
        return logArea;
    }

    public void setLogArea(TextArea logArea) {
        this.logArea = logArea;
    }

    public VBox getvBox() {
        return vBox;
    }

    public void setvBox(VBox vBox) {
        this.vBox = vBox;
    }

}
