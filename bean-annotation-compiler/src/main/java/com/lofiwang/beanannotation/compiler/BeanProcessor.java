package com.lofiwang.beanannotation.compiler;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
public class BeanProcessor extends AbstractProcessor {
    private static final String SUFFIX = "Bean";
    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(bean.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element annElement : roundEnvironment.getElementsAnnotatedWith(bean.class)) {
            TypeElement classElement = (TypeElement) annElement;
            try {
                generateCode(classElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args));
    }


    private void info(String msg, Object... args) {
        mMessager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args));
    }

    public void generateCode(TypeElement classElement) throws IOException {

        HashMap<String, TypeName> fieldMap = new HashMap<>();

        for (Element encloseElement : classElement.getEnclosedElements()) {
            if (encloseElement.getKind() == ElementKind.FIELD) {
                String fieldName = encloseElement.getSimpleName().toString();
                TypeName fieldTypeName = TypeName.get(encloseElement.asType());
                fieldMap.put(fieldName, fieldTypeName);
            }
        }

        info("generateCode " + fieldMap.toString());

        String beanName = classElement.getSimpleName().toString() + SUFFIX;
        PackageElement pkg = mElementUtils.getPackageOf(classElement);
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();
        if (packageName == null) {
            error(classElement.getQualifiedName() + " has no pkg name.");
            return;
        }
        TypeSpec.Builder typeSpecB = TypeSpec.classBuilder(beanName);

        typeSpecB.addModifiers(Modifier.PUBLIC);
//                .addMethod(createSetMethod(elementUtils, superClassName))
//                .addMethod(newCompareIdMethod())

        for (String field : fieldMap.keySet()) {
            typeSpecB.addField(fieldMap.get(field), field, Modifier.PRIVATE);
            typeSpecB.addMethod(createSetMethod(field, fieldMap.get(field)));
            typeSpecB.addMethod(createGetMethod(field, fieldMap.get(field)));
        }
        typeSpecB.addMethod(createToStrMethod(beanName, fieldMap));
        TypeSpec typeSpec = typeSpecB.build();
        // Write file
        JavaFile.builder(packageName, typeSpec).build().writeTo(mFiler);
    }

    private MethodSpec createSetMethod(String fieldName, TypeName fieldType) {

        String methodName = "set" + upperCase(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName)
                .addStatement("this." + fieldName + "=" + fieldName);

        return method.build();
    }

    private MethodSpec createGetMethod(String fieldName, TypeName fieldType) {
        String methodName = "get" + upperCase(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addStatement("return " + fieldName);
        return method.build();
    }

    private MethodSpec createToStrMethod(String clazzName, HashMap<String, TypeName> fieldMap) {
        String methodName = "toString";
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(String.class);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"")
                .append(clazzName)
                .append("{\"");
        for (String field : fieldMap.keySet()) {
            stringBuilder.append(" + \"").append(field).append(":\" + ").append(field);
        }
        stringBuilder.append(" + \"}\"");
        method.addStatement("return " + stringBuilder.toString());
        return method.build();
    }


    public String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
