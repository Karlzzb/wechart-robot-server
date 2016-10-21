/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karl.fx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import com.karl.fx.animations.FadeInLeftTransition;
import com.karl.fx.animations.FadeInRightTransition;
import com.karl.fx.animations.FadeInTransition;

/**
 * FXML Controller class
 *
 * @author Herudi
 */
public class ControllSplash implements Initializable {
	@FXML
	private Text lblWelcome;
	@FXML
	private Text lblRudy;
	@FXML
	private VBox vboxBottom;
	@FXML
	private Label lblClose;
	@FXML
	private ImageView imgLoading;

	@FXML
	private AnchorPane root;

	/**
	 * Initializes the controller class.
	 * 
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		longStart();
		lblClose.setOnMouseClicked((MouseEvent event) -> {
			Platform.exit();
			System.exit(0);
		});
	}

	private void longStart() {
		Task<Integer> mytask = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {
				return 0;
			}
		};
		Service<Integer> service = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				return mytask;
			}
		};
		service.setOnRunning((WorkerStateEvent event) -> {
			new FadeInLeftTransition(lblWelcome).play();
			new FadeInRightTransition(lblRudy).play();
			new FadeInTransition(vboxBottom).play();
		});
		service.setOnSucceeded((WorkerStateEvent event) -> {
		});
		service.start();
	}

}
