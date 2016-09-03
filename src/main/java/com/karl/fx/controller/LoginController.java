package com.karl.fx.controller;

import static org.slf4j.LoggerFactory.getLogger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.domain.RuntimeDomain;
import com.karl.fx.FxmlView;
import com.karl.fx.StageManager;
import com.karl.service.WebWechat;
import com.karl.utils.AppUtils;

@Component
public class LoginController extends FxmlController {

    private static final Logger LOGGER = getLogger(LoginController.class);

    @FXML
    private ImageView loginImageView;

    @FXML
    private Button btn1;

    private WebWechat webWechat;

    private RuntimeDomain runtimeDomain;

    @Autowired
    @Lazy(value = true)
    public LoginController(WebWechat webWechat, RuntimeDomain runtimeDomain,
            StageManager stageManager) {
        super();
        this.webWechat = webWechat;
        this.runtimeDomain = runtimeDomain;
        this.stageManager = stageManager;
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
        LOGGER.debug("Longin image path :{}", path);
        Image loginImage = new Image(path);
        loginImageView.setImage(loginImage);
    }

    public void btn1Action() throws InterruptedException {
        while (!"200".equals(webWechat.waitForLogin())) {
            Thread.sleep(AppUtils.LOGIN_WAITING_TIME);
        }
        if (!webWechat.login()) {
            LOGGER.info("微信登录失败");
            return;
        }
        LOGGER.info("[*] 微信登录成功");
        stageManager.switchScene(FxmlView.MAIN);
    }
}
