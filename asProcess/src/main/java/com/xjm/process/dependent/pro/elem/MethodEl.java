package com.xjm.process.dependent.pro.elem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * title: MethodEl
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/21 10:41
 **/
public class MethodEl {


    private String methodName;
    private String accessibility;

    private String returnType;

    private boolean isVoid;

    private List<String> thrownTypes;


    private List<ParameterEl> parameterElList;


    public MethodEl() {
    }

    public List<ParameterEl> getParameterElList() {
        return parameterElList;
    }

    public void setParameterElList(List<ParameterEl> parameterElList) {
        this.parameterElList = parameterElList;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }


    public List<String> getThrownTypes() {
        return thrownTypes;
    }

    public void setThrownTypes(List<String> thrownTypes) {
        this.thrownTypes = thrownTypes;
    }


    private String methodStr;

    public String getMethodStr() {
        return methodStr;
    }

    public void setMethodStr(String methodStr) {
        this.methodStr = methodStr;
    }

    public boolean isCreateStr() {
        this.methodStr = this.accessibility + "  " + this.returnType + "  " + this.methodName + "(" + this.parameterElList.stream().map(ParameterEl::getStr).collect(Collectors.joining(",")) + ")" + (this.thrownTypes != null && this.thrownTypes.size() > 0 ? "throws  " + String.join(",", thrownTypes) : "");
        return true;
    }


    private String returStr;

    public String getReturStr() {
        return returStr;
    }

    public void setReturStr(String returStr) {
        this.returStr = returStr;
    }

    public boolean isHaveDecoratorReturnStr() {
        if (Boolean.TRUE != this.isVoid && this.getParameterElList() == null || this.getParameterElList().size() == 0) {
            return false;
        }
        this.returStr = "(" + this.parameterElList.stream().map(ParameterEl::getParameterName).collect(Collectors.joining(",")) + ");";
        return true;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public void setVoid(boolean aVoid) {
        isVoid = aVoid;
    }
}
