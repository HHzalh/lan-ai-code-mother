package com.lanhai.lanaicodemother.innerservice;

/**
 * 内部截图服务
 */
public interface InnerScreenshotService {

    /**
     * 生成并上传网页截图
     *
     * @param webUrl 网页URL（如：http://localhost/app/deploy-key/）
     * @return 截图访问URL（腾讯云 COS 地址）
     */
    String generateAndUploadScreenshot(String webUrl);
}