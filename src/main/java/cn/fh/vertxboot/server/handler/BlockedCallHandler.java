package cn.fh.vertxboot.server.handler;

import cn.fh.vertxboot.server.meta.BlockedHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

/**
 * Created by wanghongfei on 2019-02-22.
 */
@Component
@BlockedHandler
public class BlockedCallHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext event) {
        // 会block线程的调用
    }
}
