package cn.fh.vertxboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DemoService {
    public String hello(String name) {
        return "hello, " + name;
    }

    public String blockingLogic(int i) {
        log.info("execute blocking logic {}", i);

        return i + " done";
    }
}
