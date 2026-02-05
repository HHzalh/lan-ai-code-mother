package com.lanhai.lanaicodemother.rabbitmq.consumer;

import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.model.entity.App;
import com.lanhai.lanaicodemother.model.mq.ScreenshotMessage;
import com.lanhai.lanaicodemother.service.AppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 截图结果消费者
 * 监听 screenshot 服务返回的结果消息
 *
 * @author hhzalh
 * @since 2026-02-06
 */
@Slf4j
@Service
public class ScreenshotResultConsumer {

    @Resource
    private AppService appService;

    /**
     * 监听截图结果队列
     * 当 screenshot 服务完成截图生成后，会发送结果消息到此队列
     *
     * @param resultMessage 截图结果消息
     */
    @RabbitListener(queues = "screenshot.result.queue")
    public void handleScreenshotResult(ScreenshotMessage.ResultMessage resultMessage) {
        try {
            log.info("接收到截图结果：appId={}, success={}, screenshotUrl={}",
                    resultMessage.getAppId(),
                    resultMessage.getSuccess(),
                    resultMessage.getScreenshotUrl());

            // 检查是否成功
            if (Boolean.FALSE.equals(resultMessage.getSuccess())) {
                log.error("截图生成失败：appId={}, error={}",
                        resultMessage.getAppId(),
                        resultMessage.getErrorMessage());
                // 失败时不更新数据库，或者可以记录失败状态
                return;
            }

            // 成功则更新应用截图
            updateAppScreenshot(resultMessage.getAppId(), resultMessage.getScreenshotUrl());

        } catch (Exception e) {
            log.error("处理截图结果失败：appId={}", resultMessage.getAppId(), e);
            // 不抛出异常，避免消息重试导致无限循环
        }
    }

    /**
     * 更新应用截图 URL
     *
     * @param appId         应用 ID
     * @param screenshotUrl 截图 URL
     */
    private void updateAppScreenshot(Long appId, String screenshotUrl) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        ThrowUtils.throwIf(screenshotUrl == null || screenshotUrl.trim().isEmpty(),
                ErrorCode.PARAMS_ERROR, "截图URL不能为空");

        log.info("开始更新应用截图：appId={}, screenshotUrl={}", appId, screenshotUrl);

        // 查询应用是否存在
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 更新截图 URL
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setCover(screenshotUrl);
        boolean updated = appService.updateById(updateApp);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新应用截图失败");

        log.info("应用截图更新成功：appId={}, screenshotUrl={}", appId, screenshotUrl);
    }
}
