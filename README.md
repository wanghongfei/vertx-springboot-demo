# vertx-springboot结合demo

此项目为SpringBoot与Vert.x结合使用的工程示例。二者结合的优势如下：

- web层可充分利用vert.x异步特性，轻松支持高并发
- 抛弃笨重的Tomcat(Servlet), 大大降低资源消耗
- 保留SpringBoot好用的特性，如`application.yaml`配置，一键打包，可执行jar，依赖注入等



vertx通过使用`BootEventListener`事件监听器将自身的启动逻辑嵌入到Spring生命周期中，vertx相关配置可通过`application.yaml`传入。

