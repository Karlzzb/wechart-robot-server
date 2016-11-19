package com.karl.fx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.domain.RuntimeDomain;
import com.karl.fx.StageManager;
import com.karl.service.GameService;
import com.karl.service.WebWechat;

@Component
public class FxmlController{

    @Autowired
    @Lazy
    public StageManager stageManager;

    @Autowired
    public WebWechat webWechat;
    
//    @Autowired
//    public WebWechatSentor webWechatSentor;

    @Autowired
    public RuntimeDomain runtimeDomain;
    
//    @Autowired
//    public SentorDomain sentorDomain;
    
    @Autowired
    public GameService gameService;

    public void initialize() {
    }
}
