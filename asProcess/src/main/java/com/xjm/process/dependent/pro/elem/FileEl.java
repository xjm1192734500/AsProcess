package com.xjm.process.dependent.pro.elem;

import java.util.List;

/**
 * title: FileEl
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/21 13:51
 **/
public class FileEl {


    private List<AnnotationEl> annotationEls;


    private String className;

    private String name;

    private String  accessibility;




    public List<AnnotationEl> getAnnotationEls() {
        return annotationEls;
    }

    public void setAnnotationEls(List<AnnotationEl> annotationEls) {
        this.annotationEls = annotationEls;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }
}
