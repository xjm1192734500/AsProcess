package com.xjm.processService.service;

import com.xjm.processService.annotation.TestProcessorV1;

@TestProcessorV1(value = "Method", impl = Iservice.class)
public abstract class ProService implements Iservice {


    @Override
    public void getName() {
        System.out.println("XJM");
    }


}