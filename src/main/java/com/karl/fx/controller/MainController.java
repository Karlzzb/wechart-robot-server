package com.karl.fx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import org.springframework.stereotype.Component;

@Component
public class MainController extends FxmlController {

    @FXML
    private ConsoleController consoleController;

    public TextArea getVisualConsole() {
        return consoleController.getLogArea();
    }

    @Override
    public void initialize() {
    }
}
