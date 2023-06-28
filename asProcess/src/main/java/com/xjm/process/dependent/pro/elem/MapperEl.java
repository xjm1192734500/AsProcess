package com.xjm.process.dependent.pro.elem;

import org.mapstruct.ap.internal.model.common.Type;

import java.util.SortedSet;

/**
 * title: MapperEl
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/20 10:01
 **/
public class MapperEl {
    private String implName;
    private boolean customName;
    private String implPackage;
    private boolean customPackage;

    private  String name;

    private Type mapperDefinitionType;

    /**
     * imported
     */
    SortedSet<Type> extraImportedTypes;


}
