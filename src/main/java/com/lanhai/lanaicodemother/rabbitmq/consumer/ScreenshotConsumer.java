package com.lanhai.lanaicodemother.rabbitmq.consumer;

import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.model.entity.App;
import com.lanhai.lanaicodemother.rabbitmq.producer.ScreenshotProducer;
import com.lanhai.lanaicodemother.service.AppService;
import com.lanhai.lanaicodemother.service.ScreenshotService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 截图生成消息消费者
 *
 * @author hhzalh
 * @since 2026-01-26
 */
@Slf4j
@Service
public class ScreenshotConsumer {

    @Resource
    private  ScreenshotService screenshotService;

    @Resource
    private  AppService appService;

    /**
     * 消费截图任务
     */
    @RabbitListener(queues = "screenshot.queue")
    public void handleScreenshotTask(ScreenshotProducer.ScreenshotTaskMessage task) {
        try {
            log.info("开始处理截图任务：appId={}, url={}", task.getAppId(), task.getDeployUrl());

            // 1. 生成截图
            String screenshotUrl = screenshotService.generateAndUploadScreenshot(task.getDeployUrl());

            // 2. 更新应用截图信息
            // 更新应用封面字段
            App updateApp = new App();
            updateApp.setId(task.getAppId());
            updateApp.setCover(screenshotUrl);
            boolean updated = appService.updateById(updateApp);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新应用封面字段失败");

            log.info("截图任务处理完成：appId={}, screenshotUrl={}", task.getAppId(), screenshotUrl);


        } catch (Exception e) {
            log.error("截图任务处理失败：appId={}", task.getAppId(), e);
            // 抛出异常会导致消息被拒绝并重新入队
            throw new RuntimeException("截图任务处理失败", e);
        }
    }
}