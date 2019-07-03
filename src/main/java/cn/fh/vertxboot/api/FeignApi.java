package cn.fh.vertxboot.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by wanghongfei on 2019-07-02.
 */
@FeignClient("engine-service")
public interface FeignApi {
    @GetMapping("/info")
    String user();
}
