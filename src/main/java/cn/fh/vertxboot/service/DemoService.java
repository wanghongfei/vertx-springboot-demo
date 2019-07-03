package cn.fh.vertxboot.service;

import cn.fh.vertxboot.api.FeignApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DemoService {
    @Autowired
    private FeignApi api;

    public String hello(String name) {
        return "hello, " + name;
    }

    public String blockingLogic(int i) {
        log.info("execute blocking logic {}", i);

        return i + " done";
    }

    public String remoteCall() {
        String response = api.user();
        log.info("feign response: {}", response);

        return response;
    }
}
