package com.xjm.process.dependent.pro.elem;



import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * title: AnnotationElUtil
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/20 15:02
 **/
public class AnnotationElUtil {


    /**
     * 根据mapstruct 的源码学习得
     *
     * @param annotationMirror 注解
     * @param consumer         消费者
     * @return java.util.Map<java.lang.String, javax.lang.model.element.AnnotationValue>
     * version 1.0.0
     * time 2023/4/20 15:08
     */
    public static Map<String, AnnotationValue> getAnnotationValueMap(AnnotationMirror annotationMirror, BiConsumer<Map<String, AnnotationValue>, Map<String, AnnotationValue>> consumer) {


        //获取该注解所有的默认属性
        List<ExecutableElement> enclosed = ElementFilter.methodsIn(annotationMirror.getAnnotationType().asElement().getEnclosedElements());
        Map<String, AnnotationValue> defaultValues = new HashMap<>(enclosed.size());
        //获取所有的默认值
        enclosed.forEach(e -> defaultValues.put(e.getSimpleName().toString(), e.getDefaultValue()));
        //获取显示设置的注解值
        Map<String, AnnotationValue> values = new HashMap<>(enclosed.size());
        annotationMirror.getElementValues().entrySet().forEach(e -> values.put(e.getKey().getSimpleName().toString(), e.getValue()));

        //消费
        if (consumer != null) {
            consumer.accept(defaultValues, values);
        }

        return values;
    }


    /**
     * description
     *
     * @param element         元素
     * @param annotationClass 获取指定的注解
     * @return javax.lang.model.element.AnnotationMirror
     * version 1.0.0
     * time 2023/4/20 15:12
     */
    public static AnnotationMirror checkGetAnnotationMirror(Element element, String annotationClass) {
        return element.getAnnotationMirrors().stream()
                .filter(a -> annotationClass.contentEquals(((TypeElement) a.getAnnotationType().asElement()).getQualifiedName()))
                .findAny()
                .orElse(null);
    }


}
