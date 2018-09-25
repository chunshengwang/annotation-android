# bean-annotation
annotation processor generate java bean get() set() toString()
///注解处理器为java bean 生成get() set() toString() 方法


# Question
### 1.How to resolve AndroidStudio prompt underline on JavaPoet Modifier.PUBLIC.
resolve: We can new Android Library Module, in which dependencies annotationProcessor project(':xxx-compiler') instead of app module.
```
MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addStatement("return " + fieldName);
```
我们在写注解处理器时，Modifier.PUBLIC 会被Android Studio用红色下划线提示，原因是在app module里直接依赖了，新建一个Android Library 来依赖注解处理器可以解决红线提示问题。依个人理解，注解处理器一般用来动态生成一些代码，这些代码生成在Library Module里作为其它模块的依赖，项目模块更整洁独立，使用注解处理器更应该往模块独立化上思考。

# example

### 1.How to use @bean.
```
package com.lofiwang.beanannotation;

import com.lofiwang.beanannotation.compiler.bean;

@bean
public class Person {
    private String name;
    private String age;
}
```

### 2.What will be generated automatically.
```
package com.lofiwang.beanannotation;

import java.lang.Override;
import java.lang.String;

public class PersonBean {
  private String name;

  private String age;

  public void setName(String name) {
    this.name=name;
  }

  public String getName() {
    return name;
  }

  public void setAge(String age) {
    this.age=age;
  }

  public String getAge() {
    return age;
  }

  @Override
  public String toString() {
    return "PersonBean{" + "name:" + name + "age:" + age + "}";
  }
}
```
