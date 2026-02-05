package com.lanhai.lanaicodemother.rabbitmq.producer;

import com.lanhai.lanaicodemother.model.mq.ScreenshotMessage;
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
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送截图生成任务
     */
    public void sendScreenshotTask(Long appId, String deployUrl) {
        ScreenshotMessage.TaskMessage message = ScreenshotMessage.TaskMessage.builder()
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
}