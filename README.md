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
