package com.xjm.process.dependent.pro;

import com.google.auto.service.AutoService;
import com.xjm.process.dependent.pro.build.FreeWork;
import com.xjm.process.dependent.pro.elem.*;
import com.xjm.process.dependent.pro.template.CreateClassTemplate;
import org.mapstruct.ap.internal.util.Executables;
import org.mapstruct.ap.shaded.org.mapstruct.tools.gem.GemValue;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.ElementKindVisitor6;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * title: TestProcessIm
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/18 10:37
 **/
@AutoService(Processor.class)
public class ProcessServiceVersionProcessIm extends AbstractProcessor {

    private Elements delegate;
    private Types mTypeUtils;
    private Elements elementUtils;
    private Filer mFiler;
    private Messager mMessager;


    private Set<DeferredMapper> deferredMappers = new HashSet<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        this.delegate = processingEnv.getElementUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> ann = new HashSet<>();
        ann.add("com.xjm.processService.annotation.ServiceVersion");
        return ann;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {

            Set<TypeElement> mapperTypes = new HashSet<>();
            //处理注解
            Map<String, AnnotationValue> decoratedValueMap = new HashMap<>();
            Map<String, GemValue<TypeMirror>> typeMirrorGemValueMap = new HashMap<>();
            Set<String> importStr = new HashSet<>();
            for (TypeElement annotation : annotations) {


                if (annotation.getKind() != ElementKind.ANNOTATION_TYPE) {
                    continue;
                }

                //获取注解
                Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : elementsAnnotatedWith) {
                    AnnotationMirror annotationMirror = AnnotationElUtil.checkGetAnnotationMirror(element, "com.xjm.processService.annotation.ServiceVersion");
                    if (annotationMirror == null) {
                        return true;
                    }
                    Map<String, AnnotationValue> annotationValueMap = AnnotationElUtil.getAnnotationValueMap(annotationMirror, (def, va) -> {
                        AnnotationValue annotationValue = va.get("impl");
                        AnnotationValue annotationValue1 = def.get("impl");
                        if (annotationValue != null) {
                            decoratedValueMap.put("impl", annotationValue);
                            typeMirrorGemValueMap.put("impl", GemValue.create(annotationValue, annotationValue1, TypeMirror.class));
                        }
                        if (va.get("getVersion") != null) {
                            typeMirrorGemValueMap.put("getVersion", GemValue.create(va.get("getVersion"), def.get("getVersion"), TypeMirror.class));
                        }
                    });

                }
                //获取该注解下的类信息
                Set<? extends Element> annotatedMapper = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : annotatedMapper) {
                    TypeElement mapperTypeElement = asTypeElement(element);
                    if (mapperTypeElement != null) {
                        mapperTypes.add(mapperTypeElement);
                    }
                }
            }
            //收集的方法
            List<ExecutableElement> alreadyCollected = new ArrayList<>();
            List<MethodEl> methodEls = new ArrayList<>();
            FreeWork freeWork = new FreeWork();
            freeWork.setFileEls(new ArrayList<>());
            freeWork.setMethodEls(new ArrayList<>());
            for (TypeElement decoratorElement : mapperTypes) {

                //获取筛选的方法 获取没有重写的方法 获取指定的装饰类
                TypeElement mapperElement = (TypeElement) mTypeUtils.asElement(typeMirrorGemValueMap.get("impl").get());


                //获取实现的接口类
                List<? extends Element> mapperElements = mapperElement.getEnclosedElements();

                //筛选方法
                List<ExecutableElement> executableElements = ElementFilter.methodsIn(mapperElements);
                for (ExecutableElement toAdd : executableElements) {
                    //除去私有 Object
                    if (isNotPrivate(toAdd) && isNotObjectEquals(toAdd) && methodWasNotYetOverridden(alreadyCollected, toAdd, mapperElement)) {
                        alreadyCollected.add(0, toAdd);
                    }
                }


                //获取没有被覆盖的方法
                List<ExecutableElement> mappingMethods = new ArrayList<>();
                //接口类的方法属性
                for (ExecutableElement executableElement : alreadyCollected) {
                    boolean implementationRequired = true;
                    //获取实现类的属性
                    for (ExecutableElement method : ElementFilter.methodsIn(decoratorElement.getEnclosedElements())) {
                        if (elementUtils.overrides(method, executableElement, decoratorElement)) {
                            implementationRequired = false;
                            break;
                        }
                    }
                    //校验方法
                    if (implementationRequired && !(Executables.isDefaultMethod(executableElement) ||
                            executableElement.getModifiers().contains(Modifier.STATIC))) {
                        mappingMethods.add(executableElement);
                    }
                }

                //获取重写方法的类
                for (ExecutableElement mappingMethod : mappingMethods) {
                    List<? extends VariableElement> parameters = mappingMethod.getParameters();
                    for (VariableElement parameter : parameters) {

                        Set<String> importTypes = getImportTypes(parameter.asType());
                        if (importTypes.size() > 0) {
                            importStr.addAll(importTypes);
                        }
                    }
                }
                //导入
                freeWork.setImportSets(importStr);
                //当前类
                TypeEl decoratorEl = TypeElUtils.getType(decoratorElement.asType(), elementUtils);
                //包地址
                String packageName = decoratorEl.getPackageName();
                String className = decoratorEl.getName();
                freeWork.getImportTypeNames().add(decoratorEl.getQualifiedName());
                freeWork.setDecoratorClassName(decoratorEl.getName());
                freeWork.setHaveDecorator(true);
                //实现类
                TypeMirror service = typeMirrorGemValueMap.get("impl").getValue();
                if (service != null) {
                    Set<String> importTypes = getImportTypes(service);
                    TypeEl type = TypeElUtils.getType(service, elementUtils);
                    if (importTypes.size() > 0) {
                        freeWork.getImportSets().addAll(importTypes);
                        freeWork.setHaveImpl(true);
                        freeWork.setImplClassName(type.getName());

                        //也是该实现的字段
                        FileEl fileEl = new FileEl();
                        fileEl.setAccessibility("private");
                        fileEl.setClassName(type.getName());
                        fileEl.setName("service");

                        //添加注解
                        fileEl.setAnnotationEls(Collections.singletonList(new AnnotationEl("@Autowired")));
                        freeWork.getImportTypeNames().add("org.springframework.beans.factory.annotation.Autowired");
                        freeWork.getFileEls().add(fileEl);
                    }
                }

                //获取方法 alreadyCollected
                //获取方法的参数 编写没有被重写的类
                for (ExecutableElement method : mappingMethods) {
                    MethodEl methodEl = new MethodEl();
                    methodEl.setParameterElList(new ArrayList<>());
                    //javaType 获取当前类指定方法的数据类型
                    DeclaredType containing = (DeclaredType) mapperElement.asType();
                    TypeMirror asMemberOf = mTypeUtils.asMemberOf(containing, method);
                    ExecutableType methodType = (ExecutableType) asMemberOf;

                    //获取入参
                    List<? extends TypeMirror> parameterTypes = methodType.getParameterTypes();
                    List<? extends VariableElement> parameters = method.getParameters();
                    List<? extends TypeMirror> thrownTypes = method.getThrownTypes();

                    //返回名称
                    TypeMirror returnType = method.getReturnType();
                    //判断返回是不是void
                    if (returnType.getKind() == TypeKind.VOID) {
                        methodEl.setVoid(true);
                    }
                    String returnName = returnType.toString();
                    methodEl.setReturnType(returnName);

                    //方法名字
                    String methodName = method.getSimpleName().toString();
                    methodEl.setMethodName(methodName);


                    //迭代器
                    Iterator<? extends VariableElement> varIt = parameters.iterator();
                    Iterator<? extends TypeMirror> typesIt = parameterTypes.iterator();

                    while (varIt.hasNext()) {
                        VariableElement parameter = varIt.next();
                        TypeMirror parameterType = typesIt.next();

                        if (parameterType.getKind() == TypeKind.DECLARED) {
                            DeclaredType declaredType = (DeclaredType) parameterType;
                            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                            //判断泛形
                            for (TypeMirror typeArgument : typeArguments) {
                                if (typeArgument.getKind() == TypeKind.TYPEVAR) {
                                    mMessager.printMessage(Diagnostic.Kind.ERROR, "class " + packageName + "." + freeWork.getImplClassName() + " method.filed " + typeArgument.toString() + " Can't generate mapping method for a generic type variable target.");
                                    return false;
                                }
                            }
                        }

                        //排除返回泛型
                        if (returnType.getKind() == TypeKind.TYPEVAR) {
                            mMessager.printMessage(Diagnostic.Kind.ERROR, "class " + packageName + "." + freeWork.getImplClassName() + " method " + method.getSimpleName().toString() + " Can't generate mapping method for a generic type variable target.");
                            return false;
                        }

                        //直接获取
                        ParameterEl parameterEl = new ParameterEl(parameterType.toString(), parameter.getSimpleName().toString());
                        methodEl.getParameterElList().add(parameterEl);


                        // if the method has varargs and this is the last parameter
                        // we know that this parameter should be used as varargs
                        boolean isVarArgs = !varIt.hasNext() && method.isVarArgs();
                    }

                    if (thrownTypes.size() > 0) {
                        List<String> throwNames = new ArrayList<>(thrownTypes.size());

                        for (TypeMirror thrownType : thrownTypes) {
                            String thrownTypesName = thrownType.toString();
                            throwNames.add(thrownTypesName);
                        }
                        methodEl.setThrownTypes(throwNames);
                    }
                    methodEl.setAccessibility("public");

                    methodEls.add(methodEl);
                }

                freeWork.setMethodEls(methodEls);

                //当前类添加注解
                TypeElement componentTypeElement = elementUtils.getTypeElement("org.springframework.stereotype.Component");
                Object getVersion = typeMirrorGemValueMap.get("getVersion").getAnnotationValue().getValue();
                freeWork.setAnnotationEls(new ArrayList<>(Arrays.asList(new AnnotationEl("@Component"),
                        new AnnotationEl("@Version(version  = \"" + getVersion.toString() + "\")"))));
                freeWork.getImportTypeNames().add("org.springframework.stereotype.Component");
                freeWork.getImportTypeNames().add("com.xjm.processService.annotation.Version");


                freeWork.setName(className + "Impl___");
                freeWork.setAccessibility("public");
                freeWork.setPackageName(packageName);

                CreateClassTemplate.createCLass(mFiler, packageName, freeWork);
            }


        }

        return false;
    }


    /**
     * 获取qualifiedName;
     *
     * @param typeMirror
     * @return
     */
    public Set<String> getImportTypes(TypeMirror typeMirror) {
        Set<String> result = new HashSet<>();

        if (typeMirror.getKind() == TypeKind.DECLARED) {
            TypeEl type = TypeElUtils.getType(typeMirror, elementUtils);
            String qualifiedName = type.getQualifiedName();
            if (qualifiedName != null) {
                result.add(qualifiedName);
            }

        }

        return result;
    }

    private TypeMirror getComponentType(TypeMirror mirror) {
        if (mirror.getKind() != TypeKind.ARRAY) {
            return null;
        }

        ArrayType arrayType = (ArrayType) mirror;
        return arrayType.getComponentType();
    }


    private boolean isNotPrivate(ExecutableElement executable) {
        return !executable.getModifiers().contains(Modifier.PRIVATE);
    }

    private boolean isNotObjectEquals(ExecutableElement executable) {
        if (executable.getSimpleName().contentEquals("equals") && executable.getParameters().size() == 1 && asTypeElement(executable.getParameters().get(0).asType()).getQualifiedName().contentEquals("java.lang.Object")) {
            return false;
        }
        return true;
    }

    /**
     * @param alreadyCollected the list of already collected methods of one type hierarchy (order is from sub-types to
     *                         super-types)
     * @param executable       the method to check
     * @param parentType       the type for which elements are collected
     * @return {@code true}, iff the given executable was not yet overridden by a method in the given list.
     */
    private boolean methodWasNotYetOverridden(List<ExecutableElement> alreadyCollected, ExecutableElement executable, TypeElement parentType) {
        for (ListIterator<ExecutableElement> it = alreadyCollected.listIterator(); it.hasNext(); ) {
            ExecutableElement executableInSubtype = it.next();
            if (executableInSubtype == null) {
                continue;
            }
            if (delegate.overrides(executableInSubtype, executable, parentType)) {
                return false;
            } else if (delegate.overrides(executable, executableInSubtype, parentType)) {
                // remove the method from another interface hierarchy that is overridden by the executable to add
                it.remove();
                return true;
            }
        }

        return true;
    }


    /**
     * @param mirror the type positionHint
     * @return the corresponding type element
     */
    private TypeElement asTypeElement(TypeMirror mirror) {
        return (TypeElement) ((DeclaredType) mirror).asElement();
    }


    private TypeElement asTypeElement(Element element) {
        return element.accept(new ElementKindVisitor6<TypeElement, Void>() {
            @Override
            public TypeElement visitTypeAsInterface(TypeElement e, Void p) {
                return e;
            }

            @Override
            public TypeElement visitTypeAsClass(TypeElement e, Void p) {
                return e;
            }

        }, null);
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private Set<TypeElement> getAndResetDeferredMappers() {
        Set<TypeElement> deferred = new HashSet<>(deferredMappers.size());

        for (DeferredMapper deferredMapper : deferredMappers) {
            TypeElement element = deferredMapper.deferredMapperElement;
            deferred.add(processingEnv.getElementUtils().getTypeElement(element.getQualifiedName()));
        }

        deferredMappers.clear();
        return deferred;
    }


    private static class DeferredMapper {

        private final TypeElement deferredMapperElement;
        private final Element erroneousElement;

        private DeferredMapper(TypeElement deferredMapperElement, Element erroneousElement) {
            this.deferredMapperElement = deferredMapperElement;
            this.erroneousElement = erroneousElement;
        }
    }
}
