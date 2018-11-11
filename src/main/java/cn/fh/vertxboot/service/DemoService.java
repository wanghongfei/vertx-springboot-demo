package cn.fh.vertxboot.service;

import org.springframework.stereotype.Service;

@Service
public class DemoService {
    public String hello(String name) {
        return "hello, " + name;
    }
}
