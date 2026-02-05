package com.lanhai.lanaicodemother.task;

import com.lanhai.lanaicodemother.utils.WebScreenshotUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 应用截图定时任务
 * 每天凌晨2点执行，为已部署但封面为空的应用自动生成封面截图
 */
@Component
@Slf4j
public class cleanupTempScheduledTask {

    /**
     * 每天凌晨3点执行，清理过期的临时截图文件
     * 0 0 3 * * ? 表示每天凌晨2点0分0秒执行
     * Cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupTempScreenshots() {
        log.info("定时清理临时截图文件任务开始");
        WebScreenshotUtils.cleanupTempFiles();
    }

}
