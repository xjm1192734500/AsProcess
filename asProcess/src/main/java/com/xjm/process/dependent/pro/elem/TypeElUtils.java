package com.xjm.process.dependent.pro.elem;

import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.NativeTypes;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;

/**
 * title: TypeElUtils
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/19 17:48
 **/
public class TypeElUtils {

    public static TypeEl getType(TypeMirror mirror, Elements elementUtils) {
        boolean isEnumType;
        boolean isInterface;
        String name;
        String packageName;
        String qualifiedName;
        TypeElement typeElement;
        Type componentType;
        Boolean toBeImported = null;
        if (mirror != null) {
            if (TypeKind.DECLARED.equals(mirror.getKind())) {
                DeclaredType declaredType = (DeclaredType) mirror;
                isEnumType = declaredType.asElement().getKind() == ElementKind.ENUM;
                isInterface = declaredType.asElement().getKind() == ElementKind.INTERFACE;
                name = declaredType.asElement().getSimpleName().toString();

                typeElement = (TypeElement) declaredType.asElement();
                if (typeElement != null) {
                    packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                    qualifiedName = typeElement.getQualifiedName().toString();
                } else {
                    packageName = null;
                    qualifiedName = name;
                }

            } else {
                isEnumType = false;
                isInterface = false;
                // When the component type is primitive and is annotated with ElementType.TYPE_USE then
                // the typeMirror#toString returns (@CustomAnnotation :: byte) for the javac compiler
                if (mirror.getKind().isPrimitive()) {
                    name = NativeTypes.getName(mirror.getKind());
                }
                // When the component type is type var and is annotated with ElementType.TYPE_USE then
                // the typeMirror#toString returns (@CustomAnnotation T) for the errorprone javac compiler
                else if (mirror.getKind() == TypeKind.TYPEVAR) {
                    name = ((TypeVariable) mirror).asElement().getSimpleName().toString();
                } else {
                    name = mirror.toString();
                }
                packageName = null;
                qualifiedName = name;
                typeElement = null;
                componentType = null;
                toBeImported = false;
            }
            return new TypeEl(isEnumType,isInterface,name,packageName,qualifiedName,typeElement,toBeImported);

        }
        return null;

    }

}
