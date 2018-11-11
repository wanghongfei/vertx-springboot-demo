package cn.fh.vertxboot.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "vertx")
@Data
public class VertxProps {
    /**
     * vertx http server配置
     */
    private Server server;

    /**
     * 请求路由配置
     */
    private List<HandlerMapping> handlerMappings;

    @Data
    public static class HandlerMapping {
        /**
         * 请求路径
         */
        private String path;
        /**
         * 请求方法, 如GET, POST, PUT
         */
        private String method;
        /**
         * 对应Spring里的bean名
         */
        private String beanName;
    }

    @Data
    public static class Server {
        /**
         * vertx http server监听端口
         */
        private int port = 8080;

        /**
         * NIO线程数, 默认为CPU逻辑核心数量
         */
        private int nioThreadCount = Runtime.getRuntime().availableProcessors();
        /**
         * worker线程池大小
         */
        private int workerThreadCount = Runtime.getRuntime().availableProcessors();
        /**
         * 部署的http verticle数量
         */
        private int verticleCount = Runtime.getRuntime().availableProcessors();
    }
}
