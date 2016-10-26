/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karl.fx.controller;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import org.springframework.stereotype.Component;

import com.karl.fx.FxmlView;
import com.karl.fx.animations.FadeInLeftTransition;
import com.karl.fx.animations.FadeInLeftTransition1;
import com.karl.fx.animations.FadeInRightTransition;
import com.karl.utils.DateUtils;

@Component
public class CertificateConroller extends FxmlController  implements Initializable {
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Text lblWelcome;
    @FXML
    private Text lblUsername;
    @FXML
    private Text lblPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Text lblRudyCom;
    @FXML 
    private Label lblClose;        
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            new FadeInLeftTransition(lblWelcome).play();
            new FadeInLeftTransition1(lblPassword).play();
            new FadeInLeftTransition1(lblUsername).play();
            new FadeInLeftTransition1(txtUsername).play();
            new FadeInLeftTransition1(txtPassword).play();
            new FadeInRightTransition(btnLogin).play();
            lblClose.setOnMouseClicked((MouseEvent event) -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }    

    @FXML
    private void aksiLogin(ActionEvent event) {
    	if(DateUtils.formatDate(new Date()).equals("2016-10-31")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("错误");
			alert.setContentText("账号已过期，请重新向管理员申请！");
			alert.showAndWait();
			return;
    	}
        if (txtUsername.getText().equals("hopeless") && txtPassword.getText().equals("qwer4321#")) {
        	stageManager.switchSceneLogin(FxmlView.BLUELOGIN);
        }else{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("错误");
			alert.setContentText("用户名/密码错误");
			alert.showAndWait();
			return;
        }
    }
    
}
