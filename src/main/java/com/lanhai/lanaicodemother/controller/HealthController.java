package com.lanhai.lanaicodemother.controller;

import com.lanhai.lanaicodemother.common.BaseResponse;
import com.lanhai.lanaicodemother.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务健康检查控制器，提供用于探活的轻量响应接口。
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("ok");
    }
}
