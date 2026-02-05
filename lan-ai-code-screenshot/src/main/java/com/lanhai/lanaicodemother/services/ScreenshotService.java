package com.lanhai.lanaicodemother.services;

public interface ScreenshotService {

    /**
     * 生成并上传网页截图
     *
     * @param webUrl
     * @return
     */
    String generateAndUploadScreenshot(String webUrl);
}
