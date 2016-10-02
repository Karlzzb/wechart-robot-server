package com.karl.fx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class MessageController extends FxmlController {

    @FXML
    private TextArea messageBoard;

    @FXML
    private Button sentOutBut;
    
    
	@Override
	public void initialize() {
		messageBoard.setText(runtimeDomain.getSentOutMessage());
	}
    
    @FXML
    private void sentOut(ActionEvent event) {
    	webWechat.webwxsendmsg(messageBoard.getText());
//    	Stage stage = (Stage)sentOutBut.getScene().getWindow();
//    	runtimeDomain.setMessageBoardCount(0);
//    	stage.close();
    }
    
    public void changeMessage() {
    	messageBoard.setText(runtimeDomain.getSentOutMessage());
    }

}
