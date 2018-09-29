package com.lofiwang.beansdk;


import com.lofiwang.beanannotation.compiler.ann.Bean;
import com.lofiwang.beanannotation.compiler.ann.Parcelable;

@Bean
@Parcelable
public class Person {
    private String name;
    private String age;
}
