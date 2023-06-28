package com.xjm.process.dependent.pro.elem;

/**
 * title: ParameterEl
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/21 10:46
 **/
public class ParameterEl {


    private String className;

    private String parameterName;


    public ParameterEl(String className, String parameterName) {
        this.className = className;
        this.parameterName = parameterName;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }


    public String getStr(){
        return this.className + "  "+this.parameterName;
    }
}
