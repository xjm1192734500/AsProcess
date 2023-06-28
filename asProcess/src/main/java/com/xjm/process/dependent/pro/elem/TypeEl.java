package com.xjm.process.dependent.pro.elem;

import org.mapstruct.ap.internal.model.common.Accessibility;

import javax.lang.model.element.TypeElement;

/**
 * title: TypeEl
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/19 17:48
 **/
public class TypeEl {


    private boolean isEnumType;
    private boolean isInterface;

    private String name;
    private String packageName;
    private String qualifiedName;
    private TypeElement typeElement;

    private Boolean toBeImported;

    private Accessibility accessibility;


    public TypeEl(boolean isEnumType, boolean isInterface, String name, String packageName, String qualifiedName, TypeElement typeElement, Boolean toBeImported) {
        this.isEnumType = isEnumType;
        this.isInterface = isInterface;

        this.name = name;
        this.packageName = packageName;
        this.qualifiedName = qualifiedName;
        this.typeElement = typeElement;
        this.toBeImported = toBeImported;
    }

    public boolean isEnumType() {
        return isEnumType;
    }

    public void setEnumType(boolean enumType) {
        isEnumType = enumType;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public Boolean getToBeImported() {
        return toBeImported;
    }

    public void setToBeImported(Boolean toBeImported) {
        this.toBeImported = toBeImported;
    }
}
