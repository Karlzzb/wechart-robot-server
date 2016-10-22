package com.karl.fx.controller;

import static org.slf4j.LoggerFactory.getLogger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.fx.FxmlView;
import com.karl.fx.StageManager;
import com.karl.utils.AppUtils;

@Component
public class LoginController extends FxmlController {

	private static final Logger LOGGER = getLogger(LoginController.class);

	@FXML
	private ImageView loginImageView;

	@FXML
	private ProgressBar taskBar;

	private ConsoleController consoleController;

	@Autowired
	@Lazy(value = true)
	public LoginController(StageManager stageManager,
			ConsoleController consoleController) {
		super();
		this.stageManager = stageManager;
		this.consoleController = consoleController;
	}

	@Override
	public void initialize() {
		String uuid = webWechat.getUUID();
		if (null == uuid || uuid.isEmpty()) {
			LOGGER.info("[*] uuid获取失败");
			return;
		}

		LOGGER.info("[*] 获取到uuid为 [{}]", runtimeDomain.getUuid());
		webWechat.showQrCode();
		String path = runtimeDomain.getQrCodeFile().toURI().toString();
		LOGGER.info("Longin image path :{}", path);
		Image loginImage = new Image(path);
		loginImageView.setImage(loginImage);
		waitLoginTask();
	}

	private void waitLoginTask() {
		Service<Integer> service = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				return new Task<Integer>() {
					@Override
					public Integer call() throws InterruptedException {
						Thread.sleep(AppUtils.LOGIN_WAITING_TIME);
						while (!"200".equals(webWechat.waitForLogin())) {
							updateProgress(10, 100);
							Thread.sleep(AppUtils.LOGIN_WAITING_TIME);
						}
						if (!webWechat.login()) {
							LOGGER.info("微信登录失败");
						}
						LOGGER.info("[*] 微信登录成功");
						webWechat.buildWechat();
						webWechat.listenMsgMode(consoleController);
						return null;
					}
				};
			}
		};
        service.start();
//        taskBar.progressProperty().bind(service.progressProperty());
        service.setOnRunning((WorkerStateEvent event) -> {
    		taskBar.setProgress(-1.0f);
        });
        service.setOnSucceeded((WorkerStateEvent event) -> {
			stageManager.switchScene(FxmlView.MENU);
        });
	}
}
