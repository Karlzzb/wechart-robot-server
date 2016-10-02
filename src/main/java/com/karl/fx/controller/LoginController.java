package com.karl.fx.controller;

import static org.slf4j.LoggerFactory.getLogger;
import javafx.concurrent.Task;
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
    public LoginController(StageManager stageManager, ConsoleController consoleController) {
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
        Image loginImage =  new Image(path,true);
        loginImageView.setImage(loginImage);
//        taskBar.setProgress(-1.0f);
        waitLoginTask();
    }

    private void waitLoginTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
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
//                Stage stage = (Stage)loginImageView.getScene().getWindow();
//                stage.close();
                stageManager.switchScene(FxmlView.MENU);
                return null;
            }
        };
//        taskBar.progressProperty().bind(task.progressProperty());
//        Platform.setImplicitExit(false);
//        Platform.runLater(task);
		Thread t1 = new Thread(task);
		t1.setDaemon(Boolean.TRUE);
		t1.start();
    }
}
