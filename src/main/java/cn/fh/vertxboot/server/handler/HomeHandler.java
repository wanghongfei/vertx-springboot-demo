package cn.fh.vertxboot.server.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HomeHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext route) {
        log.info("invoke homeHandler");
        route.next();
//        route.response()
//                .end(route.request().path());
    }
}
