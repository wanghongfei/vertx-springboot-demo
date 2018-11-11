package cn.fh.vertxboot.server.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class DemoHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext route) {
        route.response()
                .end(route.request().path());
    }
}
