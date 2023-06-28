package com.xjm.processService.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * title: Version
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/14 16:02
 **/
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface Version {


    String version() default  "v1";

}
