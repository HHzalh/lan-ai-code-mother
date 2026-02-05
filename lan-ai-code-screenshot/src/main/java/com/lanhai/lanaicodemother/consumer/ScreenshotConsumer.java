package com.lanhai.lanaicodemother.consumer;

import com.lanhai.lanaicodemother.model.mq.ScreenshotMessage;
import com.lanhai.lanaicodemother.services.ScreenshotService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 截图生成消息消费者
 * 监听 app 服务发送的截图任务，完成后将结果返回给 app 服务
 *
 * @author hhzalh
 * @since 2026-02-06
 */
@Slf4j
@Service
public class ScreenshotConsumer {

    @Resource
    private ScreenshotService screenshotService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 消费截图任务
     * 从 screenshot.queue 队列接收任务
     *
     * @param task 截图任务消息
     */
    @RabbitListener(queues = "screenshot.queue")
    public void handleScreenshotTask(ScreenshotMessage.TaskMessage task) {
        Long appId = task.getAppId();
        String screenshotUrl = null;
        boolean success = false;
        String errorMessage = null;

        try {
            log.info("开始处理截图任务：appId={}, url={}", appId, task.getDeployUrl());

            // 调用截图服务生成截图并上传到 COS
            screenshotUrl = screenshotService.generateAndUploadScreenshot(task.getDeployUrl());

            // 判断是否成功
            if (screenshotUrl != null && !screenshotUrl.trim().isEmpty()) {
                success = true;
                log.info("截图生成成功：appId={}, url={}", appId, screenshotUrl);
            } else {
                errorMessage = "截图生成失败，返回 URL 为空";
                log.warn("截图生成失败：appId={}, 原因={}", appId, errorMessage);
            }

        } catch (Exception e) {
            log.error("截图任务处理异常：appId={}", appId, e);
            success = false;
            errorMessage = e.getMessage();

        } finally {
            // ✅ 关键：无论成功失败，都发送结果消息到 app 服务
            sendScreenshotResult(appId, screenshotUrl, success, errorMessage);
        }
    }

    /**
     * 发送截图结果消息到 app 服务
     * 将结果发送到 screenshot.result.queue
     *
     * @param appId         应用 ID
     * @param screenshotUrl 截图 URL
     * @param success       是否成功
     * @param errorMessage  错误信息
     */
    private void sendScreenshotResult(Long appId, String screenshotUrl,
                                      boolean success, String errorMessage) {
        try {
            // 构建结果消息
            ScreenshotMessage.ResultMessage resultMessage =
                    ScreenshotMessage.ResultMessage.builder()
                            .appId(appId)
                            .screenshotUrl(screenshotUrl)
                            .success(success)
                            .errorMessage(errorMessage)
                            .timestamp(System.currentTimeMillis())
                            .build();

            // 发送结果消息到结果队列
            rabbitTemplate.convertAndSend(
                    "screenshot.exchange",
                    "screenshot.result.key",
                    resultMessage
            );

            log.info("截图结果已发送：appId={}, success={}", appId, success);

        } catch (Exception e) {
            log.error("发送截图结果失败：appId={}", appId, e);
        }
    }
}
