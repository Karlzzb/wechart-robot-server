/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karl.fx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import org.springframework.stereotype.Component;

import com.karl.fx.FxmlView;

@Component
public class MenuController extends FxmlController {

	@FXML
	private Label title;
	@FXML
	private ListView<String> listMenu;
	@FXML
	private AnchorPane paneData;
	@FXML
	private Button btnLogout;
	@FXML
	private Button loginButton;

	@Override
	public void initialize() {
		listMenu.getItems().addAll("  工作台", "  系统配置",  "  游戏设置", "  统计");
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
			stageManager.loadAnchorPaneMemu(paneData, FxmlView.CONFIG);
		}
			break;
		case 2: {
			stageManager.loadAnchorPaneMemu(paneData, FxmlView.CONFIGALL);
			break;
		}
		case 3: {
			stageManager.loadAnchorPaneMemu(paneData, FxmlView.STATS);
			break;
		}default :
			break;
		}
	
	}

	@FXML
	private void wechatLogin(ActionEvent event) {
//		stageManager.retryLogin(FxmlView.RETRYLOGIN);
	}

}
