package com.lofiwang.beanannotation.compiler;


import com.google.auto.service.AutoService;
import com.lofiwang.beanannotation.compiler.ann.Bean;
import com.lofiwang.beanannotation.compiler.ann.Builder;
import com.lofiwang.beanannotation.compiler.ann.Parcelable;
import com.lofiwang.beanannotation.compiler.gen.GenBeanUtil;
import com.lofiwang.beanannotation.compiler.gen.GenBuilderUtil;
import com.lofiwang.beanannotation.compiler.gen.GenParcelableUtil;

import java.io.IOException;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


@AutoService(Processor.class)
public class GenProcessor extends AbstractProcessor {

    private Types types;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        types = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Bean.class.getCanonicalName());
        annotations.add(Builder.class.getCanonicalName());
        annotations.add(Parcelable.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (handleBean(set, roundEnvironment)) {
//            return true;
        }
        if (handleBuilder(set, roundEnvironment)) {
//            return true;
        }

        if (handleParcelable(set, roundEnvironment)) {
//            return true;
        }
        return true;
    }


    private boolean handleBean(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element annElement : roundEnvironment.getElementsAnnotatedWith(Bean.class)) {
            TypeElement classElement = (TypeElement) annElement;
            try {
                GenBeanUtil.createBeanFile(messager, elementUtils, classElement, filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean handleBuilder(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element annElement : roundEnvironment.getElementsAnnotatedWith(Builder.class)) {
            TypeElement classElement = (TypeElement) annElement;
            try {
                GenBuilderUtil.createBuilderFile(messager, elementUtils, classElement, filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean handleParcelable(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element annElement : roundEnvironment.getElementsAnnotatedWith(Parcelable.class)) {
            TypeElement classElement = (TypeElement) annElement;
            try {
                GenParcelableUtil.createParcelableFile(messager, elementUtils, classElement, filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
