package com.xjm.processService.annotation;

import java.lang.annotation.*;

/**
 * title: YangShiServiceVersion
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/21 16:24
 **/
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface ServiceVersion {


    String getVersion();

    Class<?> impl() ;
}
