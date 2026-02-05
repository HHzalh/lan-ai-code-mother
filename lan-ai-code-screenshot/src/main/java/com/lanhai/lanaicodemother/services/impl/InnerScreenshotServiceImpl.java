package com.lanhai.lanaicodemother.services.impl;

import com.lanhai.lanaicodemother.innerservice.InnerScreenshotService;
import com.lanhai.lanaicodemother.services.ScreenshotService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 内部截图服务实现
 */
@Slf4j
@DubboService
public class InnerScreenshotServiceImpl implements InnerScreenshotService {

    @Resource
    private ScreenshotService screenshotService;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {

        return screenshotService.generateAndUploadScreenshot(webUrl);
    }
}
