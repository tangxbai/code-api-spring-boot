

### Code Api

[![dictionary-map](https://img.shields.io/badge/plugin-code--api--boot--starter-green?style=flat-square)](https://github.com/tangxbai/dictionary-map) [![license](https://img.shields.io/badge/license-Apache%202-blue?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)



### 项目简介

提供基于 springboot 的状态码收集器，省去了关于状态码的描述传输。比如你系统中有哪些状态码，分别代表什么意思？此时你不仅需要在程序中添加一个状态码，而且还需要在文档中记录下这个状态码的信息，然后才能交给使用者。这个过程其实重复且多余，虽然是很小的一部分，几行代码就可以搞定，但是不方便啊，每次都需要写或者拷贝，此插件就是为了解决这个问题而提供出来的，有了此插件以后，你仅需在项目中引入 Maven 坐标，然后即可无脑的添加你的各种状态码，也不用关心你写了哪些状态码，直接将访问 URL 交给使用者即可，提供的网页中也可以快速查找状态码对应的中文含义，岂不快哉？赶紧用起来吧。

这里有一点要说明一下，不管大家的项目状态码是如何定义的，这里**推荐使用枚举的方式进行定义**，好处这里就不做过多的阐述了，如果不是通过枚举来定义的话，后面可能会稍微麻烦一些。

*注意：此项目是一款完全开源的项目，您可以在任何适用的场景使用它，商用或者学习都可以，如果您有任何项目上的疑问，可以在issue上提出您问题，我会在第一时间回复您，如果您觉得它对您有些许帮助，希望能留下一个您的星星（★），谢谢。*

------

此项目遵照 [Apache 2.0 License]( http://www.apache.org/licenses/LICENSE-2.0.txt ) 开源许可 



### 核心亮点

- **零代码**：仅需导入 Maven 坐标，即可参与工作；
- **自动扫描**：程序启动过程中会将所有符合目标的状态码收集起来，提供查阅；
- **快捷查询**：程序启动完成后会自动生成查询链接，提供查询功能；
- **状态分组**：可以通过 @CodeDescriptor 进行分组信息设置；
- **支持颜色标注**：可以通过设置不同的颜色值来区分不同的状态码；
- **支持导出**：可以导出状态码列表到本地（如果加入了 `poi-lite` 坐标的话，还支持导出到 Excel，无需任何配置）；



### 快速开始

```xml
<dependency>
    <groupId>com.viiyue.plugins</groupId>
    <artifactId>code-api-spring-boot-starter</artifactId>
    <version>[VERSION]</version>
</dependency>
```

如何获取最新版本？[点击这里获取最新版本](https://search.maven.org/search?q=g:com.viiyue.plugins%20AND%20a:code-api-spring-boot-starter&core=gav)



### 核心组件

<table>
    <thead>
    	<tr>
            <th align="left">名称</th>
        	<th align="left">类型</th>
            <th align="left">描述</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>@CodeDescriptor(...)</td>
        	<td>注解</td>
            <td>状态码描述配置</td>
        </tr>
        <tr>
            <td>@EnableCodeApi</td>
            <td>注解</td>
            <td>通过 @EnableCodeApi 开启插件支持</td>
        </tr>
        <tr>
            <td>CodeExporter</td>
            <td>接口</td>
            <td>导出状态码信息</td>
        </tr>
    </tbody>
</table>



### 偏好配置

<table>
    <thead>
    	<tr>
            <th width="20%" align="left">属性</th>
            <th width="45%" align="left">描述</th>
            <th width="15%" align="left">类型</th>
            <th width="20%" align="left">默认</th>
        </tr>
    </thead>
    <tbody>
    	<tr>
            <td>codeapi.title</td>
            <td>提供的网页标题</td>
            <td>String</td>
            <td>CodeApi</td>
        </tr>
        <tr>
            <td>codeapi.path</td>
            <td>生成的页面地址</td>
            <td>String</td>
            <td>/code-api</td>
        </tr>
        <tr>
            <td>codeapi.basePackage</td>
            <td>指定程序扫描的基础包</td>
            <td>String</td>
            <td>启动类所在的包路径</td>
        </tr>
        <tr>
            <td>codeapi.exportable</td>
            <td>是否启用导出功能（默认没有开启）</td>
            <td>Boolean</td>
            <td>false</td>
        </tr>
    </tbody>
</table>



### 如何使用？

1、在启动类上启用

```java
@EnableCodeApi
@SpringBootApplication
public class Application {
    
    public static void main( String [] args ) {
        SpringApplication.run( Application.class, args );
    }
    
}
```

2、枚举类继承自 CodeExporter 接口，程序启动后会自动扫描所有继承于此类的子类。

```java
@Getter
@AllArgsConstructor
@CodeDescriptor( value = "通用状态码（1000以下）", theme = "#EC26BD" )
public enum StatusCode implements CodeExporter {
    
    OK( 200, "请求成功" ),
    EMPTY( 210, "暂无更多数据" ),
    BAD_PARAMETERS( 400, "提交的参数内容或者格式有问题" ),
    FAILURE( 500, "系统异常" );
    
    private int code;
    private String message;
    
}
```

3、默认关闭了导出功能，如需导出功能，按照下面配置打开限制

```properties
codeapi.exportable = true
```

4、默认提供纯文本的方式导出，另外如果在项目中添加了 [poi-lite](https://search.maven.org/search?q=g:com.viiyue.plugins%20AND%20a:poi-lite&core=gav) 坐标的话，则会自动切换为 Excel 导出，如果都不满足的话，甚至可以重写 `CodeDownloader` 完成自己的下载器逻辑，然后定制你自己的下载逻辑。

```java
/**
 * 注册到 Spring 容器中
 *
 * @author tangxbai
 * @since 1.0.0
 */
@Configuration
public class CodeApiConfiguration {
    
    @Bean
    public CodeDownloader codeDownloader() {
        return new MyCodeDownloader();
    }
    
}

/**
 * 编写你自己的下载器
 *
 * @author tangxbai
 * @since 1.0.0
 */
public class MyCodeDownloader implements CodeDownloader {
    
    @Override
    public String getExtension() {
        return "ext";
    }

    @Override
    public void download( OutputStream out, CodeApiProperties props, List<CodeBean> codes ) throws IOException {
        // ...
    }
    
}
```



### 访问状态码页面

```java
http://<ip>:<port>/<path>/code-api
```



### 关于作者

- 邮箱：tangxbai@hotmail.com
- 掘金： https://juejin.im/user/5da5621ce51d4524f007f35f
- 简书： https://www.jianshu.com/u/e62f4302c51f
- Issuse：https://github.com/tangxbai/code-api-spring-boot-starter/issues
