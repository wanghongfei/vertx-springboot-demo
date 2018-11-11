package cn.fh.vertxboot.bootstrap;

import cn.fh.vertxboot.server.VertxHttpServerVerticle;
import cn.fh.vertxboot.server.VertxProps;
import com.alibaba.fastjson.JSON;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class BootEventListener implements ApplicationListener<ApplicationStartedEvent> {
    private volatile Throwable bootError;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ApplicationContext context = event.getApplicationContext();

        VertxProps props = context.getBean(VertxProps.class);
        log.info("vertx config: {}", JSON.toJSONString(props));
        logMappings(context, props.getHandlerMappings());

        deploy(context, props.getServer());
    }

    private void logMappings(ApplicationContext ctx, List<VertxProps.HandlerMapping> mappings) {
        for (VertxProps.HandlerMapping hm : mappings) {
            log.info("mapping {} {} to {}", hm.getMethod(), hm.getPath(),
                    ctx.getBean(hm.getBeanName()).getClass().getName()
            );
        }

    }

    private void deploy(ApplicationContext context, VertxProps.Server config) {
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(config.getNioThreadCount())
                .setWorkerPoolSize(config.getWorkerThreadCount());

        Vertx vertx = Vertx.vertx(vertxOptions);

        DeploymentOptions depOptions = new DeploymentOptions();
        depOptions.setInstances(config.getVerticleCount());

        VertxHttpServerVerticle.springContext = context;

        CountDownLatch latch = new CountDownLatch(1);
        vertx.deployVerticle(VertxHttpServerVerticle.class, depOptions, ar -> {
            if (!ar.succeeded()) {
                log.error("err:", ar.cause());
                this.bootError = ar.cause();
                latch.countDown();

                return;
            }

            log.info("http verticle deployed successfully at {}", config.getPort());
            latch.countDown();
        });

        try {
            latch.await();

            if (null != bootError) {
                vertx.close();
                throw bootError;
            }

        } catch (Throwable e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
