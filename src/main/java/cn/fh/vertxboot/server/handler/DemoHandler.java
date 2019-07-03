package cn.fh.vertxboot.server.handler;

import cn.fh.vertxboot.service.DemoService;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DemoHandler implements Handler<RoutingContext> {
    @Autowired
    private DemoService demoService;

    @Override
    public void handle(RoutingContext route) {
        log.info("invoke DemoHandler, path: {}", route.request().path());

        // (0)
        Future<String> fut1 = Future.future();
        Future<String> fut2 = Future.future();

        // 执行block调用
        route.vertx() // (1)
                .executeBlocking(
                        fut -> {
                            String result = demoService.blockingLogic(1); // (3)
                            fut.complete(result); // (4)
                        },
                        fut1.completer()  // (5)
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
        CompositeFuture.all(fut1, fut2).setHandler(ar -> { // (6)
            if (!ar.succeeded()) {
                log.error("", ar.cause());
                route.response().end("error");
                return;
            }

            log.info("final step");

            List<String> resultList = ar.result().list(); // (7)
            route.response().end(resultList.toString());
        });
    }
}
