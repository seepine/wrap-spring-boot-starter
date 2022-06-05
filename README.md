# spring-boot-starter-wrap

easy wrap for springboot

- 依赖引入后，所有接口返回都会被封装成{code:0,data:{},msg:''}的形式
- 当接口抛出WrapException，将会封装成{code:1,msg:''}
- 当接口不想被封装时，只需要在方法或类上加@NotWrap注解即可

## 集成

### 引入依赖

- Latest
  Version: [![Maven Central](https://img.shields.io/maven-central/v/com.seepine/wrap-spring-boot-starter.svg)](https://search.maven.org/search?q=g:com.seepine%20a:wrap-spring-boot-starter)
- Maven:

```xml

<dependency>
    <groupId>com.seepine</groupId>
    <artifactId>wrap-spring-boot-starter</artifactId>
    <version>Latest Version</version>
</dependency>
```

### 指定拦截包路径

不指定则默认拦截所有。若有集成Swagger，必须指定，否则可能导致`Unable to infer base url.`

```yaml
wrap:
  #多个路径逗号隔开 com.example.controller1,com.example.controller2
  scan-packages: com.example.controller
```

## 例子

### 1 返回字符串类型

controller:

```java
class Controller {
    @RequestMapping("hello")
    public String hello() {
        return "hello world";
    }
}
```

response:
`需要注意，个别请求库(例如flutter的dio)接收到的可能是jsonString，需要转为对象JSON.parse(jsonString)`

```json
{
  "code": 0,
  "data": "hello world"
}
```

### 2 返回对象类型

entity:

```java
class User {
    Long id;
    String fullName;
    Integer age;

    public User(Long id, String fullName, Integer age) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
    }
}
```

controller:

```java
class Controller {
    @RequestMapping("user")
    public User user() {
        return new User(1L, "jackson", 24);
    }
}
```

response:

```json
{
  "code": 0,
  "data": {
    "id": 1,
    "fullName": "jackson",
    "age": 24
  }
}
```

### 3 带错误信息的异常处理

controller:

```java
class Controller {
    @RequestMapping("sum")
    public String sum() {
        // ...
        throw new WrapException("错误信息");
    }
}
```

response:

```json
{
  "code": 1,
  "msg": "错误信息"
}
```

### 4 带数据的异常处理

controller:

```java
class Controller {
    @RequestMapping("del")
    public String del() {
        //...
        throw new WrapException(new Object(), "错误信息2");
    }
}
```

response:

```json
{
  "code": -1,
  "data": {
    //..
  },
  "msg": "错误信息2"
}
```