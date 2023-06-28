package com.xjm.processService.service;

import com.xjm.processService.annotation.ServiceVersion;

@ServiceVersion(impl = Iservice.class, getVersion = "v1")
public abstract class ProService implements Iservice {


    @Override
    public void getName() {
        System.out.println("XJM");
    }


}