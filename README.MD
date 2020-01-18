# spi-groovy-integration



spi-groovy-integration(SGI), Java SPI与Groovy的相结合, 提供一种灵活的集成机制

核心思想, 借鉴Java SPI和Dubbo SPI, 提供SPI机制的Java接口, 接口实现类的代码文件(Groovy语言编写)可以在任何地方, 如数据库, 服务器上, 不再局限于固定目录, 动态加载

也可以用于开发测试环境的远程调试

&nbsp;

## Usage

```shell
cd sgi
mvn clean install
```

Maven dependency

```xml
        <dependency>
            <groupId>com.github.sgi</groupId>
            <artifactId>sgi</artifactId>
            <version>1.0.0</version>
        </dependency>
```

&nbsp;&nbsp;

使用 `@SGI`注解, 定义一个接口

```java
@SGI
public interface TravelStrategy {
    String travel(String destination);
}
```

实现接口, 并使用 `@Impl`注解

```java
@GImpl(value = "bike")
public class BikeTravelStrategy implements TravelStrategy {
    @Override
    public String travel(String destination) {
        return "go to " + destination +  " by bike ~";
    }
}
```
使用 `ExtensionLoader` , 查找对应的SGI接口实现类并加载, 然后根据名称调用相应方法

```java
ExtensionLoader<TravelStrategy> loader = ExtensionLoader.getExtensionLoader(TravelStrategy.class);
for (Class<?> clazz : PackageScanner.findGImplClass(TravelStrategy.class)) {
    loader.addExtension(clazz);
}
loader.getExtension("bike").travel("hangzhou")
```

&nbsp;


从外部文件中加载实现类

将如下内容保存为`NuoyafangzhuoTravelStrategy.groovy`

```groovy
package com.github.sgi.example.service

@GImpl
class NuoYaFangZhouTrave implements TravelStrategy {

    @Override
    String travel(String destination) {
        return "go to " + destination +  " by nuo ya fang zhou ~ .";
    }
}
```

读取文件, 并加载

```java
String path = "/root/file/NuoyafangzhuoTravelStrategy.groovy";
loader.addExtension(FileScanner.readFile(path));
loader.getExtension("bike").travel("hangzhou")
```

当然, 也可以开启一个定时任务刷新, 读取最新的文件内容

&nbsp;

也可以将实现类注册为spring bean

```java
@GImpl(isSpringBean = true, value = "flight")
// ....

// register
ExtensionSpringLoader.getExtensionSpringLoader(TravelStrategy.class).addExtensionSpringContext(script)
    
// get bean 
beanFactory.getBean("flight")
```

&nbsp;

完整示例请见: [ExampleController](sgi-example/src/main/java/com/github/sgi/example/usage/controller/ExampleController.java)
