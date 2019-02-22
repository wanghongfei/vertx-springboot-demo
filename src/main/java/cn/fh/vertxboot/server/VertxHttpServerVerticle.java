package cn.fh.vertxboot.server;

import cn.fh.vertxboot.server.meta.BlockedHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Slf4j
public class VertxHttpServerVerticle extends AbstractVerticle {
    public static ApplicationContext springContext;

    @Override
    public void start() {
        VertxProps config = springContext.getBean(VertxProps.class);

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        addHandlers(router, config.getHandlerMappings());

        server.requestHandler(router::accept)
                .listen(config.getServer().getPort());
    }

    private void addHandlers(Router router, List<VertxProps.HandlerMapping> handlerMappings) {
        for (VertxProps.HandlerMapping hm : handlerMappings) {
            for (String beanName : hm.getBeanNames()) {
                Handler<RoutingContext> handler = (Handler<RoutingContext>) springContext.getBean(beanName);

                Route route = router.route(hm.getPath());
                if (isBlocked(handler)) {
                    route.blockingHandler(handler);
                } else {
                    route.handler(handler);
                }

            }
        }
    }

    private boolean isBlocked(Handler<?> handler) {
        BlockedHandler an = handler.getClass().getAnnotation(BlockedHandler.class);
        return an != null;
    }
}
