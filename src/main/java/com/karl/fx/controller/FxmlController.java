package com.karl.fx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.karl.domain.RuntimeDomain;
import com.karl.fx.StageManager;
import com.karl.service.WebWechat;

@Component
public abstract class FxmlController {

    public StageManager stageManager;

    public abstract void initialize();

    @Autowired
    @Lazy(value = true)
    public WebWechat webWechat;

    @Autowired
    @Lazy(value = true)
    public RuntimeDomain runtimeDomain;

}
