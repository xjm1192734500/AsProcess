package com.xjm.spring.common.config;


import com.xjm.processService.annotation.UseVersion;
import com.xjm.processService.annotation.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * title: ServiceBeanAliveConfig
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/14 16:17
 **/
@Component
public class ServiceBeanAliveConfig implements BeanPostProcessor, BeanFactoryAware, Ordered {


    @Value("${versionFlag:true}")
    private Boolean versionFlag;

    protected final Log logger = LogFactory.getLog(getClass());

    private int order = Ordered.LOWEST_PRECEDENCE;

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);


    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    private final Map<String, Set<YanShiAnnotationElement>> yanshiMetadtaMap = new ConcurrentHashMap<>(100);


    private ConfigurableListableBeanFactory beanFactory;

    public ServiceBeanAliveConfig() {
        this.autowiredAnnotationTypes.add(UseVersion.class);
    }


    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        checkMap(clazz, bean, beanName);
        InjectionMetadata metadata = this.injectionMetadataCache.get(beanName);
        //需要刷新或者为空
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            //获取元素
            InjectionMetadata findMetadata = findMetadata(clazz);
            //筛选
            this.injectionMetadataCache.compute(beanName, (k, old) -> {
                if (InjectionMetadata.EMPTY.equals(findMetadata)) {
                    return null;
                }
                return findMetadata;
            });
        }
        return bean;
    }

    private void checkMap(Class<?> beanClass, Object bean, String beanName) {
        Version annotation = AnnotationUtils.findAnnotation(beanClass, Version.class);
        if (annotation != null) {
            yanshiMetadtaMap.compute(annotation.version(), (key, old) -> {
                if (old == null) {
                    Set<YanShiAnnotationElement> elements = new HashSet<>(10);
                    elements.add(new YanShiAnnotationElement(annotation.version(), bean, beanName, beanClass));
                    return elements;
                } else {
                    old.add(new YanShiAnnotationElement(annotation.version(), bean, beanName, beanClass));
                    return old;
                }
            });
        }
    }

    /**
     * 该阶段bean已经被实例化
     */
    private InjectionMetadata findMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, Version.class)) {
            return InjectionMetadata.EMPTY;
        }
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;
        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(clazz, field -> {
                MergedAnnotation<?> annotation = findAnnotation(field);
                if (annotation != null) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        currElements.add(new YangShiFileElement(field, annotation));
                    }
                }
            });
            if (currElements.size() > 0) {
                elements.addAll(0, currElements);
            }
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);

        return InjectionMetadata.forElements(elements, clazz);

    }

    /**
     * 发现注解元素
     */
    private MergedAnnotation<?> findAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from(ao);
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            MergedAnnotation<?> annotation = annotations.get(type);
            if (annotation.isPresent() && (annotations.get(Autowired.class).isPresent() || annotations.get(Resource.class).isPresent())) {
                return annotation;
            }
        }
        return null;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Map<String, Set<YanShiAnnotationElement>> yanshiMetadtaMap = this.yanshiMetadtaMap;
        Map<String, InjectionMetadata> injectionMetadataCaches = this.injectionMetadataCache;
        if (injectionMetadataCaches.size() > 0 && yanshiMetadtaMap.size() > 0) {
            InjectionMetadata metadata = this.injectionMetadataCache.get(beanName);
            try {
                metadata.inject(bean, beanName, null);
            } catch (Throwable e) {
                logger.error("执行版本切换失败e===>[{}]，自动注入主类！！！", e);
            }
        }

        return bean;
    }

    /**
     * 设置bean 工厂
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }


    private class YangShiFileElement extends InjectionMetadata.InjectedElement {


        private MergedAnnotation<?> mergedAnnotation;


        protected YangShiFileElement(Field field) {
            super(field, null);
        }

        protected YangShiFileElement(Field field, MergedAnnotation<?> mergedAnnotation) {
            super(field, null);
            this.mergedAnnotation = mergedAnnotation;
        }


        @Override
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;
            MergedAnnotation<?> mergedAnnotation = this.mergedAnnotation;
            Object value = null;
            String getVersion = mergedAnnotation.getValue("getVersion", String.class).orElse("NULL");
            Set<YanShiAnnotationElement> yanShiAnnotationElements = yanshiMetadtaMap.get(getVersion);
            if (yanShiAnnotationElements != null && yanShiAnnotationElements.size() > 0) {
                for (YanShiAnnotationElement element : yanShiAnnotationElements) {
                    if (getVersion.equals(element.version) && beanFactory.isTypeMatch(element.getBeanName(), field.getType())) {
                        if (value != null) {
                            value = null;
                            logger.error("同一个字段有两个注册的版本，不知道用那个版本，默认系统！！");
                            break;
                        }
                        value = element.getBean();
                    }
                }
            }
            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(target, value);
            }
        }
    }

    private static class YanShiAnnotationElement {

        private String version;

        private Object bean;

        private String beanName;

        private Class<?> targetClass;


        public YanShiAnnotationElement(String version, Object bean, String beanName, Class<?> targetClass) {
            this.version = version;
            this.bean = bean;
            this.beanName = beanName;
            this.targetClass = targetClass;
        }

        public String getVersion() {
            return version;
        }

        public Object getBean() {
            return bean;
        }

        public String getBeanName() {
            return beanName;
        }

        public Class<?> getTargetClass() {
            return targetClass;
        }
    }

}
