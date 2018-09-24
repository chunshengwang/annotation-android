# bean-annotation
annotation processor generate java bean get() set() toString()

///注解处理器为java bean 生成get() set() toString() 方法

# example

### 1.How to use @bean.
```
package com.lofiwang.beanannotation;

import com.lofiwang.beanannotation.compiler.bean;

@bean
public class Person {
    private String name;
    private String age;
    private String height;
    private String weight;
}
```

### 2.what will be generated automatically.
```
package com.lofiwang.beanannotation;

import java.lang.Override;
import java.lang.String;

public class PersonBean {
  private String name;

  private String weight;

  private String age;

  private String height;

  public void setName(String name) {
    this.name=name;
  }

  public String getName() {
    return name;
  }

  public void setWeight(String weight) {
    this.weight=weight;
  }

  public String getWeight() {
    return weight;
  }

  public void setAge(String age) {
    this.age=age;
  }

  public String getAge() {
    return age;
  }

  public void setHeight(String height) {
    this.height=height;
  }

  public String getHeight() {
    return height;
  }

  @Override
  public String toString() {
    return "PersonBean{" + "name:" + name + "weight:" + weight + "age:" + age + "height:" + height + "}";
  }
}
```
