package cn.fh.vertxboot.server.handler;

import cn.fh.vertxboot.service.DemoService;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemoHandler implements Handler<RoutingContext> {
    @Autowired
    private DemoService demoService;

    @Override
    public void handle(RoutingContext route) {
        route.response()
                .end(
                        demoService.hello(route.request().host())
                );
    }
}
