# vertx-springcloud结合demo

此项目为SpringBoot与Vert.x结合使用的工程示例。二者结合的优势如下：

- web层可充分利用vert.x异步特性，轻松支持高并发
- 抛弃笨重的Tomcat(Servlet), 大大降低资源消耗
- 保留SpringBoot好用的特性，如`application.yaml`配置，一键打包，可执行jar，依赖注入等
- 保留Spring Cloud服务治理，支持注册中心和声明式Feign调用



vertx通过使用`BootEventListener`事件监听器将自身的启动逻辑嵌入到Spring生命周期中，vertx相关配置可通过`application.yaml`传入。



一个需要发起两次blocking调用的请求处理代码示例：

```java
@Component
@Slf4j
public class DemoHandler implements Handler<RoutingContext> {
    @Autowired
    private DemoService demoService;

    @Override
    public void handle(RoutingContext route) {
        log.info(route.request().path());

        Future<String> fut1 = Future.future();
        Future<String> fut2 = Future.future();

        // 执行block调用
        route.vertx()
                .executeBlocking(
                        fut -> {
                            String result = demoService.blockingLogic(1);
                            fut.complete(result);
                        },
                        fut1.completer()
                );

        // 执行block调用
        route.vertx()
                .executeBlocking(
                        fut -> {
                            String result = demoService.blockingLogic(2);
                            fut.complete(result);
                        },
                        fut2.completer()
                );

        // 组合结果
        CompositeFuture.all(fut1, fut2).setHandler(ar -> {
            if (!ar.succeeded()) {
                log.error("", ar.cause());
                route.response().end("error");
                return;
            }

            List<String> resultList = ar.result().list();
            route.response().end(resultList.toString());
        });
    }
}
```

