package com.karl.fx.controller;

import static org.slf4j.LoggerFactory.getLogger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.karl.fx.animations.FadeInLeftTransition;
import com.karl.fx.animations.FadeInRightTransition;
import com.karl.fx.animations.FadeInTransition;
import com.karl.utils.AppUtils;

@Component
public class RetryLoginController extends FxmlController {

	private static final Logger LOGGER = getLogger(RetryLoginController.class);

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

	@FXML
	private ImageView qrImgeView;
	
	@Autowired
	private MainDeskController mainDeskController;

	@Override
	public void initialize() {
		longStart();
		lblClose.setOnMouseClicked((MouseEvent event) -> {
			try {
				Stage stage = (Stage) root.getScene().getWindow();
				if (stage != null) {
					stage.close();
				}
			} catch (Exception e) {
				LOGGER.error("retry login failed!",e);
			}
		});
	}

	private void longStart() {
		Service<Integer> service = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				return new Task<Integer>() {
					@Override
					public Integer call() throws InterruptedException {
						String uuid = webWechatSentor.getUUID();
						if (null == uuid || uuid.isEmpty()) {
							LOGGER.info("[*] uuid获取失败");
							return 1;
						}
						LOGGER.info("[*] 获取到uuid为 [{}]",
								sentorDomain.getUuid());
						webWechatSentor.showQrCode();
						String path = sentorDomain.getQrCodeFile().toURI()
								.toString();
						LOGGER.info("Longin image path :{}", path);
						Image qrImge = new Image(path);
						qrImgeView.setImage(qrImge);
						Thread.sleep(AppUtils.LOGIN_WAITING_TIME);
						while (!"200".equals(webWechatSentor.waitForLogin())) {
							updateProgress(10, 100);
							Thread.sleep(AppUtils.LOGIN_WAITING_TIME);
						}
						if (!webWechatSentor.login()) {
							LOGGER.info("微信登录失败");
						}
						LOGGER.info("[*] 微信登录成功");
						sentorDomain.clearGroupMap();
						webWechatSentor.buildWechat();
						webWechatSentor.setStopRequested(Boolean.TRUE);
						return 0;
					}
				};
			}
		};
		service.start();
		service.setOnRunning((WorkerStateEvent event) -> {
			new FadeInLeftTransition(lblWelcome).play();
			new FadeInRightTransition(lblRudy).play();
			new FadeInTransition(vboxBottom).play();
		});
		service.setOnSucceeded((WorkerStateEvent event) -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("登陆提示");
			alert.setContentText("信息发送账号登陆完成！");
			alert.show();
			
			try {
				mainDeskController.fillUpGroupBoxSentor();
				Stage stage = (Stage) root.getScene().getWindow();
				if (stage != null) {
					stage.close();
				}
			} catch (Exception e) {
				LOGGER.error("retry login failed!",e);
			}
		});
	}
}
