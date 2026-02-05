package com.lanhai.lanaicodemother.task;

import com.lanhai.lanaicodemother.model.entity.App;
import com.lanhai.lanaicodemother.rabbitmq.producer.ScreenshotProducer;
import com.lanhai.lanaicodemother.service.AppService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用截图定时任务
 * 每天凌晨2点执行，为已部署但封面为空的应用自动生成封面截图
 */
@Component
@Slf4j
public class ScreenshotScheduledTask {

    @Resource
    private AppService appService;

    @Value("${code.deploy-host:http://localhost}")
    private String deployHost;

    @Resource
    private ScreenshotProducer screenshotProducer;


    /**
     * 每天凌晨2点执行，为已部署但封面为空的应用自动生成封面截图
     * 0 0 2 * * ? 表示每天凌晨2点0分0秒执行
     * Cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateMissingScreenshots() {
        List<App> apps = queryAppsWithoutCover();
        if (!apps.isEmpty()) {
            for (App app : apps) {
                processAppScreenshot(app);
            }
        }
    }

    // 功能已迁移，不再在此处执行
//    /**
//     * 每天凌晨3点执行，清理过期的临时截图文件
//     * 0 0 3 * * ? 表示每天凌晨2点0分0秒执行
//     * Cron表达式：秒 分 时 日 月 周
//     * <p>
//     * 注意：此功能已移至 screenshot 服务
//     */
//    @Scheduled(cron = "0 0 3 * * ?")
//    public void cleanupTempScreenshots() {
//        // 功能已迁移，不再在此处执行
//        // 原实现：
//        // WebScreenshotUtils.cleanupTempFiles();
//    }

    /**
     * 查询已部署但封面为空的应用
     *
     * @return 应用列表
     */
    private List<App> queryAppsWithoutCover() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(App::getDeployKey).isNotNull()
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
        //log.info("开始截图：appId={}, url={}", app.getId(), appUrl);

        // 2. 调用截图服务生成截图并上传到COS
        //String screenshotUrl = screenshotService.generateAndUploadScreenshot(appUrl);

        // 2. 发送截图任务到rabbitMQ
        screenshotProducer.sendScreenshotTask(app.getId(), appUrl);
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
