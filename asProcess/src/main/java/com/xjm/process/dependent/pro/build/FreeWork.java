package com.xjm.process.dependent.pro.build;

import com.xjm.process.dependent.pro.elem.AnnotationEl;
import com.xjm.process.dependent.pro.elem.FileEl;
import com.xjm.process.dependent.pro.elem.MethodEl;

import java.util.List;
import java.util.Set;

/**
 * title: FreeWorkTest
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/20 10:58
 **/
public class FreeWork {


    private List<AnnotationEl> annotationEls;




    private String name;


    private String packageName;

    private String accessibility;

    private Set<String> importSets;

    private List<MethodEl> methodEls;


    private List<FileEl>  fileEls;

    private boolean idHavaFiles(){
        return fileEls!=null && fileEls.size()>0;
    }

    public List<FileEl> getFileEls() {
        return fileEls;
    }

    public void setFileEls(List<FileEl> fileEls) {
        this.fileEls = fileEls;
    }

    private boolean haveDecorator;

    private String decoratorClassName;


    private boolean haveImpl;

    private String implClassName;

    public boolean isHaveImpl(){
        return haveImpl;
    }


    public String getDecoratorClassName() {
        return decoratorClassName;
    }

    public String getImplClassName() {
        return implClassName;
    }

    public void setHaveDecorator(boolean haveDecorator) {
        this.haveDecorator = haveDecorator;
    }

    public void setDecoratorClassName(String decoratorClassName) {
        this.decoratorClassName = decoratorClassName;
    }

    public void setHaveImpl(boolean haveImpl) {
        this.haveImpl = haveImpl;
    }

    public void setImplClassName(String implClassName) {
        this.implClassName = implClassName;
    }

    public List<MethodEl> getMethodEls() {
        return methodEls;
    }

    public void setMethodEls(List<MethodEl> methodEls) {
        this.methodEls = methodEls;
    }

    /**
     * 是否是有方法
     * @return
     */
    public boolean isHaveMethods(){
        return methodEls!=null && methodEls.size()>0;
    }

    public boolean isHaveDecorator(){
        return haveDecorator;
    }

    public Set<String> getImportSets() {
        return importSets;
    }

    public void setImportSets(Set<String> importSets) {
        this.importSets = importSets;
    }

    public String getAccessibility() {
        return accessibility;
    }

      public  Set<String> getImportTypeNames(){
        return  this.importSets;
     }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasPackageName(){
        return true;
    }


    public boolean isHaveAnnotations(){
        return annotationEls!=null && annotationEls.size()>0;
    }

    public List<AnnotationEl> getAnnotationEls() {
        return annotationEls;
    }

    public void setAnnotationEls(List<AnnotationEl> annotationEls) {
        this.annotationEls = annotationEls;
    }
}
