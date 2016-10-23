package com.karl.fx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
		Font font = Font.font ("微软雅黑", 14);
		messageBoard.setFont(font);
		String[] content  = runtimeDomain.getSentOutMessage();
//		messageBoard.setMinHeight(JavaFXUtile.computeTextHeight(messageBoard.getFont(), content,messageBoard.getWidth()));
		messageBoard.setText(content[0]);
	}
    
    @FXML
    private void sentOut(ActionEvent event) {
    	webWechat.webwxsendmsg(messageBoard.getText());
    	if(runtimeDomain.getSentOutMessage().length > 1) {
    		webWechat.webwxsendmsg(runtimeDomain.getSentOutMessage()[1]);
    	}
    	Stage stage = (Stage)sentOutBut.getScene().getWindow();
    	runtimeDomain.setMessageBoardCount(0);
    	stage.hide();
    }
    
    public void changeMessage() {
    	messageBoard.setText(runtimeDomain.getSentOutMessage()[0]);
    }

}
