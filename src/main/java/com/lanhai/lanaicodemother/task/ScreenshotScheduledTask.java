package com.lanhai.lanaicodemother.task;

import com.lanhai.lanaicodemother.model.entity.App;
import com.lanhai.lanaicodemother.service.AppService;
import com.lanhai.lanaicodemother.service.ScreenshotService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用截图定时任务
 * 每天凌晨2点执行，为已部署但封面为空的应用自动生成封面截图
 *
 * @author AI Assistant
 */
@Component
@Slf4j
public class ScreenshotScheduledTask {

    @Resource
    private AppService appService;

    @Resource
    private ScreenshotService screenshotService;

    @Value("${code.deploy-host:http://localhost}")
    private String deployHost;


    /**
     *  * 0 0 2 * * ? 表示每天凌晨2点0分0秒执行
     * Cron表达式：秒 分 时 日 月 周
     *
     */
    @Scheduled(cron = "* 0 0 2 * * ?")
    public void generateMissingScreenshots() {
        long startTime = System.currentTimeMillis();
        log.info("========== 开始执行应用截图定时任务 ==========");

        try {
            // 1. 查询已部署但封面为空的应用
            List<App> apps = queryAppsWithoutCover();
            int total = apps.size();

            if (total == 0) {
                log.info("没有需要处理的应用，任务结束");
                return;
            }

            log.info("查询到 {} 个需要截图的应用", total);

            // 2. 统计计数器
            int successCount = 0;
            int failCount = 0;

            // 3. 顺序处理每个应用
            for (App app : apps) {
                try {
                    processAppScreenshot(app);
                    successCount++;
                    log.info("应用截图成功：appId={}, appName={}", app.getId(), app.getAppName());
                } catch (Exception e) {
                    failCount++;
                    log.error("应用截图失败：appId={}, appName={}, error={}",
                        app.getId(), app.getAppName(), e.getMessage(), e);
                }
            }

            // 4. 记录执行统计
            long duration = System.currentTimeMillis() - startTime;
            log.info("========== 应用截图定时任务执行完成 ==========");
            log.info("总数：{}, 成功：{}, 失败：{}, 耗时：{}ms", total, successCount, failCount, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("应用截图定时任务执行异常，耗时：{}ms", duration, e);
        }
    }

    /**
     * 查询已部署但封面为空的应用
     *
     * @return 应用列表
     */
    private List<App> queryAppsWithoutCover() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(App::getDeployKey).isNotNull()
            .and(App::getDeployKey).isNotNull()
            .and(App::getCover).isNull().or(App::getCover).eq("")
            .and(App::getIsDelete).eq(0)
            .orderBy(App::getCreateTime, false);
        return appService.list(queryWrapper);
    }

    /**
     * 处理单个应用的截图
     *
     * @param app 应用实体
     */
    private void processAppScreenshot(App app) {
        // 1. 构建应用访问URL
        String appUrl = buildAppUrl(app.getDeployKey());
        log.info("开始截图：appId={}, url={}", app.getId(), appUrl);

        // 2. 调用截图服务生成截图并上传到COS
        String screenshotUrl = screenshotService.generateAndUploadScreenshot(appUrl);

        if (screenshotUrl == null || screenshotUrl.isEmpty()) {
            throw new RuntimeException("截图生成失败");
        }

        // 3. 更新应用封面字段
        App updateApp = new App();
        updateApp.setId(app.getId());
        updateApp.setCover(screenshotUrl);
        boolean updated = appService.updateById(updateApp);

        if (!updated) {
            throw new RuntimeException("更新应用封面失败");
        }
    }

    /**
     * 构建应用访问URL
     *
     * @param deployKey 部署标识
     * @return 应用访问URL
     */
    private String buildAppUrl(String deployKey) {
        return String.format("%s/%s/", deployHost, deployKey);
    }
}
