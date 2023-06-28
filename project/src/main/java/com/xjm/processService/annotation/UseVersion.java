package com.xjm.processService.annotation;

import java.lang.annotation.*;

/**
 * title: UseVersion
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/14 17:41
 **/
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UseVersion {

    String getVersion() default  "v1";
}
