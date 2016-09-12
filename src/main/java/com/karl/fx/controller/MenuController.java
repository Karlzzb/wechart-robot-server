/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karl.fx.controller;

import static org.slf4j.LoggerFactory.getLogger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.karl.fx.FxmlView;

@Component
public class MenuController extends FxmlController {

    private static final Logger LOGGER = getLogger(MenuController.class);

    @FXML
    private Button close;
    @FXML
    private Button maximize;
    @FXML
    private Button minimize;
    @FXML
    private Button resize;
    @FXML
    private Button fullscreen;
    @FXML
    private Label title;
    @FXML
    private ListView<String> listMenu;
    @FXML
    private AnchorPane paneData;
    @FXML
    private Button btnLogout;

    @Override
    public void initialize() {
        listMenu.getItems().addAll("  工作台", "  日志", "  用户列表");
        maximize.getStyleClass().add("decoration-button-restore");
        resize.setVisible(false);
        listMenu.getSelectionModel().select(0);
        listMenu.requestFocus();
        stageManager.loadAnchorPaneMemu(paneData, FxmlView.MAIN);
    }

    @FXML
    private void aksiResize(ActionEvent event) {
    }

    @FXML
    private void aksiClose(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void aksiKlikListMenu(MouseEvent event) {
        switch (listMenu.getSelectionModel().getSelectedIndex()) {
        case 0: {
            stageManager.loadAnchorPaneMemu(paneData, FxmlView.MAIN);
        }
            break;
        case 1: {
            stageManager.loadAnchorPaneMemu(paneData, FxmlView.CONSOLE);
        }
            break;
        case 2: {
            stageManager.loadAnchorPaneMemu(paneData, FxmlView.PLAYER);
        }
            break;
        }
    }

}
