package com.lofiwang.beanannotation.compiler.util;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;

import javax.lang.model.element.Modifier;

public class ParcelableUtil {

    /**
     *
     */
    public static TypeSpec createParcelable(TypeName typeName, TypeSpec.Builder typeSpecBuilder, HashMap<String, TypeName> fieldMap) {
        ClassName parcelableType = ClassName.get("android.os", "Parcelable");
        ClassName parcelType = ClassName.get("android.os", "Parcel");
        ClassName parcelableCreator = ClassName.get("android.os", "Parcelable", "Creator");
        TypeName creatorFieldType = ParameterizedTypeName.get(parcelableCreator, typeName);

        //0. implements Parcelable
        typeSpecBuilder.addSuperinterface(parcelableType);

        //1. construct
        typeSpecBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC).build());

        //2. describeContents
        MethodSpec.Builder method = MethodSpec.methodBuilder("describeContents")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(int.class)
                .addStatement("return 0");

        //3. writeToParcel
        MethodSpec.Builder method1 = MethodSpec.methodBuilder("writeToParcel")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parcelType, "dest")
                .addParameter(int.class, "flags");
        for (String field : fieldMap.keySet()) {
            method1.addStatement("dest.writeValue($L)", "this." + field);
        }

        //4. construct(Parcel in)
        MethodSpec.Builder method2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parcelType, "in")
                .addStatement("$T classLoader = this.getClass().getClassLoader()", ClassLoader.class);
        for (String field : fieldMap.keySet()) {
            method2.addStatement("this.$L = ($T)in.readValue(classLoader)", field, fieldMap.get(field));
        }

        //5. CREATOR
        ArrayTypeName newTypeArray = ArrayTypeName.of(typeName);
        TypeSpec parcelableCreatorType = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(creatorFieldType)
                .addMethod(MethodSpec.methodBuilder("createFromParcel")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parcelType, "source")
                        .returns(typeName)
                        .addStatement("return new $T($N)", typeName, "source")
                        .build())
                .addMethod(MethodSpec.methodBuilder("newArray")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(int.class, "size")
                        .returns(newTypeArray)
                        .addStatement("return new $T[$N]", typeName, "size")
                        .build())
                .build();
        FieldSpec.Builder creatorField = FieldSpec.builder(creatorFieldType, "CREATOR")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", parcelableCreatorType);


        typeSpecBuilder.addMethod(method.build());
        typeSpecBuilder.addMethod(method1.build());
        typeSpecBuilder.addMethod(method2.build());
        typeSpecBuilder.addField(creatorField.build());

        return typeSpecBuilder.build();
    }
}
