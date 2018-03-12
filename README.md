algmarket-java
================

Java client 的api

## java client 调用例子

Algmarket java客户端发布到Maven central，可以通过以下方式添加为依赖项：

```xml
<dependency>
  <groupId>com.algmarket</groupId>
  <artifactId>algmarket-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

使用您的API密钥实例化客户端：

```java 数据调用
  AlgMarketClient client = AlgMarket.client(key);
```

Notes:

- 只有在算法集群上运行的算法进行调用时，才可以省略API密钥
- 使用版本范围`1.0.0`是因为它意味着使用最新的向后兼容的错误修正。


现在您已经准备好调用算法。

##调用算法

调用算法的以下示例按输入/输出的类型进行组织，这些算法之间会有所不同。

注意：单个算法可能有不同的输入和输出类型，或者接受多种类型的输入，因此请参考该算法特定的使用示例的算法描述。

###文本输入/输出

通过简单地将一个字符串传递给它的`pipe`方法来调用一个带有文本输入的算法。
如果算法输出是文本，则在响应中调用`asString`方法。

```java   
 String input = "我是测试人员";
        AlgMarketClient client = AlgMarket.client(key);
        Algm algm = client.algm("qihe/TestJava1/0.0.4");
        AlgmResponse result = algm.call(input);
        System.out.println(result.asJsonString());
```

### JSON input/output

使用JSON输入调用算法，只需传入可以序列化为JSON的类型，
包括大多数普通的旧Java对象和集合类型。
如果算法输出是JSON，则使用“TypeToken”对响应调用`as`方法
包含它应该被反序列化的类型：

```java
Algm algm = client.algo("algo://WebPredict/ListAnagrams/0.1.0");
List<String> words = Arrays.asList(("transformer", "terraforms", "retransform");
AlgmResponse result = algm.pipe(words);
// WebPredict/ListAnagrams returns an array of strings, so cast the result:
List<String> anagrams = result.as(new TypeToken<List<String>>(){});
// -> List("transformer", "retransform")
```

或者，您可以通过调用`pipeJson`来处理原始的JSON输入，
和通过在响应中调用`asJsonString`生成JSON输出：

```java
String jsonWords = "[\"transformer\", \"terraforms\", \"retransform\"]"
AlgmResponse result2 = algm.pipeJson(jsonWords);
String anagrams = result2.asJsonString();
// -> "[\"transformer\", \"retransform\"]"

Double durationInSeconds = response.getMetadata().duration;
```


### Binary input/output

通过传递一个`byte []`到`pipe`方法来调用二进制输入的算法。
如果算法响应是二进制数据，则在响应中调用`as`方法，使用`byte []``TypeToken`
获取原始字节数组。

```java
byte[] input = Files.readAllBytes(new File("/path/to/bender.jpg").toPath());
AlgmResponse result = client.algo("opencv/SmartThumbnail/0.1").pipe(input);
byte[] buffer = result.as(new TypeToken<byte[]>(){});
// -> [byte array]
```

### Error handling

API错误将导致调用`pipe`抛出`APIException`。
在算法执行期间发生的错误将导致在尝试读取响应时出现“算法异常”。

```java
Algm algo = client.algo('util/whoopsWrongAlgo')
try {
    AlgmResponse result = algo.pipe('Hello, world!');
    String output = result.asString();
} catch (APIException ex) {
    System.out.println("API Exception: " ex.getMessage());
} catch (AlgorithmException ex) {
    System.out.println("Algorithm Exception: " ex.getMessage());
    System.out.println(ex.stacktrace);
}
```

### Request options

客户端公开可以配置算法请求的选项。
这包括支持更改超时或指示API应在响应中包含stdout：

```java
Algm algo = client.algo("algo://demo/Hello/0.1.1")
                         .setTimeout(1, TimeUnit.MINUTES)
                         .setStdout(true);
AlgmResponse result = algo.pipe("HAL 9000");
Double stdout = response.getMetadata().stdout;
```

注意：如果您不能访问算法源，则会忽略`setStdout（true）`。
## Working with Data

AlgMarketClient Java客户端还提供了一种管理算法托管数据的方法
以及您连接到您的算法帐户的Dropbox或S3帐户的数据。

为客户提供数据操作api

### 创建数据集

Create directories by instantiating a `DataDirectory` object and calling `create()`:

```java
DataDirectory robots = client.dir("data://.my/robots");
robots.create();

DataDirectory dbxRobots = client.dir("dropbox://robots");
dbxRobots.create();
```

### 上传文件到服务器

```java
DataDirectory robots = client.dir("data://.my/robots");

// Upload local file
robots.putFile(new File("/path/to/Optimus_Prime.png"));
// Write a text file
robots.file("Optimus_Prime.txt").put("Leader of the Autobots");
// Write a binary file
robots.file("Optimus_Prime.key").put(new byte[] { (byte)0xe0, 0x4f, (byte)0xd0, 0x20 });
```

### 下载数据文件

```java
DataDirectory robots = client.dir("data://.my/robots");

// Download file and get the file handle
File t800File = robots.file("T-800.png").getFile();

// Get the file's contents as a string
String t800Text = robots.file("T-800.txt").getString();

// Get the file's contents as a byte array
byte[] t800Bytes = robots.file("T-800.png").getBytes();
```

### 删除数据文件和数据集合和是否强制删除


```java
client.file("data://.my/robots/C-3PO.txt").delete();
client.dir("data://.my/robots").delete(false);
client.dir("data://.my/robots").delete(true);
```

### 创建模型集
java--
ModelDirectory robots = client.dirModel("data://.my/robots");
robots.create();


### 上传模型文件
    AlgMarketClient client = AlgMarket.client(key);
    ModelDirectory robots = client.dirModel("model://qihe/TestModel");
    File file = new File("D:/2015123116200163949.jpg");
    robots.putFile(file);
    // Write a text file
    robots.file("Optimus_Prime.txt").put("Leader of the Autobots");
    // 二进制文件
    robots.file("zhang111.txt").put(new byte[] { (byte)0xe0, 0x4f, (byte)0xd0, 0x20 });


### 下载模型文件
     AlgMarketClient client = AlgMarket.client(key);
     ModelFile algo = client.modelfile("model://qihe/TestModel/2015123116200163949.jpg");
     File file = algo.getFile();

