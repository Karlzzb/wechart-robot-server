package com.karl.fx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.domain.RuntimeDomain;
import com.karl.fx.StageManager;
import com.karl.service.WebWechat;

@Component
public abstract class FxmlController {

    @Autowired
    @Lazy
    public StageManager stageManager;

    @Autowired
    public WebWechat webWechat;

    @Autowired
    public RuntimeDomain runtimeDomain;

    public void initialize() {
    };
}
