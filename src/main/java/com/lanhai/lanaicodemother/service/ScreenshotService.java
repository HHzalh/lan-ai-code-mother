package com.lanhai.lanaicodemother.service;

/**
 * 网页截图服务，负责生成页面截图并返回上传后的访问地址。
 */
public interface ScreenshotService {

    /**
     * 生成并上传网页截图
     *
     * @param webUrl
     * @return
     */
    String generateAndUploadScreenshot(String webUrl);
}
