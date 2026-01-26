package com.lanhai.lanaicodemother.rabbitmq.producer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 截图生成消息生产者
 *
 * @author hhzalh
 * @since 2026-01-26
 */
@Slf4j
@Service
public class ScreenshotProducer {

    @Resource
    private  RabbitTemplate rabbitTemplate;

    /**
     * 发送截图生成任务
     */
    public void sendScreenshotTask(Long appId, String deployUrl) {
        ScreenshotTaskMessage message = ScreenshotTaskMessage.builder()
                .appId(appId)
                .deployUrl(deployUrl)
                .timestamp(System.currentTimeMillis())
                .build();

        rabbitTemplate.convertAndSend(
                "screenshot.exchange",
                "screenshot.key",
                message
        );

        log.info("截图任务已发送到队列：appId={}, url={}", appId, deployUrl);
    }

    /**
     * 截图任务消息
     */
    @lombok.Data
    @lombok.Builder
    public static class ScreenshotTaskMessage {
        private Long appId;
        private String deployUrl;
        private Long timestamp;
    }
}