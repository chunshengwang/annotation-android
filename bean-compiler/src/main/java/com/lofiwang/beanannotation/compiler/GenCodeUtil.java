package com.lofiwang.beanannotation.compiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by chunsheng.wang on 2018/9/25.
 */

public class GenCodeUtil {

    public static void createBeanFile(Messager messager, Elements mElementUtils, TypeElement classElement, Filer filer) throws IOException {
        HashMap<String, TypeName> fieldMap = new HashMap<>();

        for (Element encloseElement : classElement.getEnclosedElements()) {
            if (encloseElement.getKind() == ElementKind.FIELD) {
                String fieldName = encloseElement.getSimpleName().toString();
                TypeName fieldTypeName = TypeName.get(encloseElement.asType());
                fieldMap.put(fieldName, fieldTypeName);
            }
        }
        PrintUtil.info(messager, "createBeanFile " + fieldMap.toString());
        String beanName = classElement.getSimpleName().toString() + "Bean";
        PackageElement pkg = mElementUtils.getPackageOf(classElement);
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();
        if (packageName == null) {
            PrintUtil.error(messager, classElement.getQualifiedName() + " has no pkg name.");
            return;
        }

        TypeSpec.Builder typeSpecB = TypeSpec.classBuilder(beanName);
        typeSpecB.addModifiers(Modifier.PUBLIC);
        for (String field : fieldMap.keySet()) {
            typeSpecB.addField(fieldMap.get(field), field, Modifier.PRIVATE);
            typeSpecB.addMethod(GenCodeUtil.createSetMethod(field, fieldMap.get(field)));
            typeSpecB.addMethod(GenCodeUtil.createGetMethod(field, fieldMap.get(field)));
        }
        typeSpecB.addMethod(GenCodeUtil.createToStrMethod(beanName, fieldMap));
        TypeSpec typeSpec = typeSpecB.build();
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    }

    public static MethodSpec createSetMethod(String fieldName, TypeName fieldType) {
        String methodName = "set" + upperFirstChar(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName)
                .addStatement("this." + fieldName + "=" + fieldName);

        return method.build();
    }

    public static MethodSpec createGetMethod(String fieldName, TypeName fieldType) {
        String methodName = "get" + upperFirstChar(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addStatement("return " + fieldName);
        return method.build();
    }

    public static MethodSpec createToStrMethod(String clazzName, HashMap<String, TypeName> fieldMap) {
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

    public static String upperFirstChar(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
