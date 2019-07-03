package cn.fh.vertxboot.test;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static io.vertx.core.VertxOptions.DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME;

public class HandlerTest {
    @Test
    public void test() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setMaxEventLoopExecuteTime(DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME * 100);

        Vertx vertx = Vertx.vertx(vertxOptions);
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultHost("www.baidu.com");
        HttpClient c = vertx.createHttpClient(options)
                .getNow("/a", ar -> {
                    System.out.println("aok");
                    latch.countDown();
                });
        c.getNow("/b", ar -> {
            System.out.println("bok");
            latch.countDown();
        });

        latch.await();
    }

}
