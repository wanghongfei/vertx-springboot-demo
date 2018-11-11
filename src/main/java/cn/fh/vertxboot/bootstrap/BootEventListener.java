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

/**
 * 通过此监听器将vertx启动逻辑嵌入到Spring生命周期中
 */
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

    /**
     * 此方法只是打印一下路由信息
     */
    private void logMappings(ApplicationContext ctx, List<VertxProps.HandlerMapping> mappings) {
        for (VertxProps.HandlerMapping hm : mappings) {
            log.info("mapping {} {} to {}", hm.getMethod(), hm.getPath(),
                    ctx.getBean(hm.getBeanName()).getClass().getName()
            );
        }

    }

    /**
     * 部署http verticle
     *
     * @param context
     * @param config
     */
    private void deploy(ApplicationContext context, VertxProps.Server config) {
        // 创建vertx
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(config.getNioThreadCount())
                .setWorkerPoolSize(config.getWorkerThreadCount());

        Vertx vertx = Vertx.vertx(vertxOptions);

        DeploymentOptions depOptions = new DeploymentOptions();
        depOptions.setInstances(config.getVerticleCount());

        // 给verticle中的spring上下文引用赋值
        VertxHttpServerVerticle.springContext = context;

        // latch用于等待部署完成
        CountDownLatch latch = new CountDownLatch(1);
        vertx.deployVerticle(VertxHttpServerVerticle.class, depOptions, ar -> {
            // 部署失败
            if (!ar.succeeded()) {
                // 记日志
                log.error("err:", ar.cause());
                // 将异常设置到成员变量中
                this.bootError = ar.cause();
                latch.countDown();

                return;
            }

            log.info("http verticle deployed successfully at {}", config.getPort());
            latch.countDown();
        });

        try {
            latch.await();

            // 如果bootError不为空, 说明部署失败
            if (null != bootError) {
                // 关闭vertx
                vertx.close();
                // 抛出异常, 触发spring启动失败
                throw bootError;
            }

        } catch (Throwable e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
