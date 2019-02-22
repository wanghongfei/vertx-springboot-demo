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
        log.info(route.request().path());

        Future<String> fut1 = Future.future();
        Future<String> fut2 = Future.future();

//        vertx().createHttpClient().get("url", ar -> {
//            // 回调方法
//        });

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
