package com.lanhai.lanaicodemother.model.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 截图相关的消息类
 * 包含任务消息和结果消息
 *
 * @author hhzalh
 * @since 2026-02-06
 */
public class ScreenshotMessage {

    /**
     * 截图任务消息
     * app 服务发送到 screenshot 服务
     */
    @Data
    @Builder
    public static class TaskMessage {
        /**
         * 应用 ID
         */
        private Long appId;

        /**
         * 部署后的 URL
         */
        private String deployUrl;

        /**
         * 时间戳
         */
        private Long timestamp;
    }

    /**
     * 截图结果消息
     * screenshot 服务返回给 app 服务
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultMessage {
        /**
         * 应用 ID
         */
        private Long appId;

        /**
         * 截图 URL（上传到 COS 后的访问地址）
         */
        private String screenshotUrl;

        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 错误信息（失败时填写）
         */
        private String errorMessage;

        /**
         * 完成时间戳
         */
        private Long timestamp;
    }
}
