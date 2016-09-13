package com.karl.fx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import org.springframework.stereotype.Component;

import com.karl.utils.StringUtils;

@Component
public class ConsoleController extends FxmlController {

    @FXML
    private AnchorPane consoleTab;

    @FXML
    private TextArea logArea;

    @FXML
    private Button cleanButton;

    public TextArea getLogArea() {
        return logArea;
    }

    public void setLogArea(TextArea logArea) {
        this.logArea = logArea;
    }

    @Override
    public void initialize() {
        // webWechat.buildWechat();
        logArea.setWrapText(true);
        logArea.setEditable(false);
        // webWechat.listenMsgMode(this);
    }

    public void writeLog(String message) {
        if (logArea != null) {
            if (logArea.getText().length() == 0) {
                logArea.setText(message);
            } else {
                logArea.selectEnd();
                logArea.insertText(logArea.getText().length(), StringUtils.replaceHtml(message)
                        + "\n");
            }
        }
    }

    @FXML
    public void clearLog() {
        logArea.clear();

    }

}
