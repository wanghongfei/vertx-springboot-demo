package cn.fh.vertxboot.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "vertx")
@Data
public class VertxProps {
    private Server server;

    private List<HandlerMapping> handlerMappings;

    @Data
    public static class HandlerMapping {
        private String path;
        private String method;
        private String beanName;
    }

    @Data
    public static class Server {
        private int port = 8080;

        private int nioThreadCount = Runtime.getRuntime().availableProcessors();
        private int workerThreadCount = Runtime.getRuntime().availableProcessors();
        private int verticleCount = Runtime.getRuntime().availableProcessors();
    }
}
