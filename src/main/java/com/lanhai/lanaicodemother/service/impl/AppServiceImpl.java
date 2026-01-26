package com.lanhai.lanaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.lanhai.lanaicodemother.ai.AiCodeGenTypeRoutingService;
import com.lanhai.lanaicodemother.ai.AiCodeGenTypeRoutingServiceFactory;
import com.lanhai.lanaicodemother.constant.AppConstant;
import com.lanhai.lanaicodemother.core.AiCodeGeneratorFacade;
import com.lanhai.lanaicodemother.core.builder.VueProjectBuilder;
import com.lanhai.lanaicodemother.core.handler.StreamHandlerExecutor;
import com.lanhai.lanaicodemother.exception.BusinessException;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.mapper.AppMapper;
import com.lanhai.lanaicodemother.model.dto.app.AppAddRequest;
import com.lanhai.lanaicodemother.model.dto.app.AppQueryRequest;
import com.lanhai.lanaicodemother.model.entity.App;
import com.lanhai.lanaicodemother.model.entity.User;
import com.lanhai.lanaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.lanhai.lanaicodemother.model.enums.CodeGenTypeEnum;
import com.lanhai.lanaicodemother.model.vo.AppVO;
import com.lanhai.lanaicodemother.model.vo.UserVO;
import com.lanhai.lanaicodemother.rabbitmq.producer.ScreenshotProducer;
import com.lanhai.lanaicodemother.service.*;
import com.lanhai.lanaicodemother.utils.GoodAppCacheUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://gitee.com/hhzalh">致爱蓝海</a>
 */
@Slf4j
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Value("${code.deploy-host:http://localhost}")
    private String deployHost;

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Resource
    private ScreenshotService screenshotService;

    @Resource
    private AiCodeGenTypeRoutingServiceFactory aiCodeGenTypeRoutingServiceFactory;

    @Resource
    private GoodAppCacheUtils goodAppCacheUtils;

    @Resource
    private ScreenshotProducer screenshotProducer;

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .like("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        }
        // 5. 通过校验后，添加用户消息到对话历史
        // 注意：积分扣减在 Controller 层通过 AOP 自动处理
        chatHistoryService.addChatMessage(appId, message, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());
        // 7. 调用 AI 生成代码（流式）
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        // 8. 收集 AI 响应内容并在完成后记录到对话历史
        return streamHandlerExecutor.doExecute(codeStream, chatHistoryService, appId, loginUser, codeGenTypeEnum);
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限部署该应用，仅本人可以部署
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
        }
        // 4. 检查是否已有 deployKey
        // 注意：积分扣减在 Controller 层通过 AOP 自动处理
        String deployKey = app.getDeployKey();
        // 没有则生成 6 位 deployKey（大小写字母 + 数字）
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 6. 获取代码生成类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 7. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
        // 8. Vue 项目特殊处理：执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            // Vue 项目需要构建
            boolean buildSuccess = vueProjectBuilder.buildProject(sourceDirPath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请检查代码和依赖");
            // 检查 dist 目录是否存在
            File distDir = new File(sourceDirPath, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            // 将 dist 目录作为部署源
            sourceDir = distDir;
            log.info("Vue 项目构建成功，将部署 dist 目录: {}", distDir.getAbsolutePath());
        }
        // 9. 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }
        // 10. 更新应用的 deployKey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 11. 返回可访问的 URL
        String appDeployUrl = String.format("%s/%s/", deployHost, deployKey);
        //String appDeployUrl = String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);

        // 12. 异步生成截图并更新应用封面
        //generateAppScreenshotAsync(appId, appDeployUrl);

        //12. 发送截图任务到rabbitMQ
        screenshotProducer.sendScreenshotTask(appId,appDeployUrl);
        return appDeployUrl;
    }

    /**
     * 异步生成应用截图并更新封面
     *
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    @Override
    public void generateAppScreenshotAsync(Long appId, String appUrl) {
        // 使用虚拟线程异步执行
        Thread.startVirtualThread(() -> {
            // 调用截图服务生成截图并上传
            String screenshotUrl = screenshotService.generateAndUploadScreenshot(appUrl);
            // 更新应用封面字段
            App updateApp = new App();
            updateApp.setId(appId);
            updateApp.setCover(screenshotUrl);
            boolean updated = this.updateById(updateApp);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新应用封面字段失败");
        });
    }

    /**
     * 重写 updateById 方法，在更新后删除精选应用缓存
     *
     * @param entity 要更新的实体
     * @return 是否成功
     */
    @Override
    public boolean updateById(App entity) {
        if (entity == null || entity.getId() == null) {
            return false;
        }
        // 更新前获取旧数据，用于判断是否需要删除缓存
        App oldApp = this.getById(entity.getId());
        Integer oldPriority = oldApp != null ? oldApp.getPriority() : null;
        Integer newPriority = entity.getPriority();

        // 执行更新
        boolean result = super.updateById(entity);

        // 判断是否需要删除缓存：
        // 1. 修改前是精选应用（应用信息可能改变）
        // 2. 修改后是精选应用（新增或保持精选状态，但信息改变了）
        // 3. priority改变了（可能影响精选列表）
        if (result) {
            boolean wasGoodApp = AppConstant.GOOD_APP_PRIORITY.equals(oldPriority);
            boolean isNowGoodApp = AppConstant.GOOD_APP_PRIORITY.equals(newPriority);
            boolean priorityChanged = newPriority != null && oldPriority != null && !newPriority.equals(oldPriority);
            if (wasGoodApp || isNowGoodApp || priorityChanged) {
                try {
                    goodAppCacheUtils.evictAllGoodAppCacheByRedis();
                    log.debug("删除精选应用缓存成功，appId: {}", entity.getId());
                } catch (Exception e) {
                    // 记录日志但不阻止更新操作
                    log.error("删除精选应用缓存失败，appId: {}, 错误: {}", entity.getId(), e.getMessage(), e);
                }
            }
        }
        return result;
    }

    /**
     * 删除应用时关联删除对话历史、代码目录和部署目录
     *
     * @param id 应用ID
     * @return 是否成功
     */
    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        // 转换为 Long 类型
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        // 先查询应用信息，获取 codeGenType 和 deployKey（用于删除代码目录和部署目录）
        App app = this.getById(appId);
        // 记录是否是精选应用，用于删除缓存
        boolean isGoodApp = app != null && AppConstant.GOOD_APP_PRIORITY.equals(app.getPriority());
        if (app != null) {
            // 删除代码目录
            try {
                String codeGenType = app.getCodeGenType();
                if (StrUtil.isNotBlank(codeGenType)) {
                    String sourceDirName = codeGenType + "_" + appId;
                    String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
                    File sourceDir = new File(sourceDirPath);
                    // 如果目录存在，则删除
                    if (sourceDir.exists() && sourceDir.isDirectory()) {
                        FileUtil.del(sourceDir);
                        log.info("成功删除应用代码目录: {}", sourceDirPath);
                    }
                }
            } catch (Exception e) {
                // 记录日志但不阻止应用删除
                log.error("删除应用代码目录失败，appId: {}, 错误: {}", appId, e.getMessage(), e);
            }
            // 删除部署目录（如果应用已部署）
            try {
                String deployKey = app.getDeployKey();
                if (StrUtil.isNotBlank(deployKey)) {
                    String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
                    File deployDir = new File(deployDirPath);
                    // 如果部署目录存在，则删除
                    if (deployDir.exists() && deployDir.isDirectory()) {
                        FileUtil.del(deployDir);
                        log.info("成功删除应用部署目录: {}", deployDirPath);
                    }
                }
            } catch (Exception e) {
                // 记录日志但不阻止应用删除
                log.error("删除应用部署目录失败，appId: {}, deployKey: {}, 错误: {}", appId, app.getDeployKey(), e.getMessage(), e);
            }
        }
        // 删除关联的对话历史
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            // 记录日志但不阻止应用删除
            log.error("删除应用关联对话历史失败: {}", e.getMessage());
        }
        // 删除应用
        boolean result = super.removeById(id);
        // 如果删除的是精选应用，删除缓存
        if (isGoodApp && result) {
            try {
                goodAppCacheUtils.evictAllGoodAppCacheByRedis();
                log.debug("删除精选应用缓存成功，appId: {}", appId);
            } catch (Exception e) {
                // 记录日志但不阻止应用删除
                log.error("删除精选应用缓存失败，appId: {}, 错误: {}", appId, e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public Long createApp(AppAddRequest appAddRequest, User loginUser) {
        // 参数校验
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化 prompt 不能为空");

        // 构造入库对象
        App app = new App();
        BeanUtil.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());
        // 应用名称暂时为 initPrompt 前 12 位
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        // 使用 AI 智能选择代码生成类型（多例模式）
        AiCodeGenTypeRoutingService routingService = aiCodeGenTypeRoutingServiceFactory.createAiCodeGenTypeRoutingService();
        CodeGenTypeEnum selectedCodeGenType = routingService.routeCodeGenType(initPrompt);
        app.setCodeGenType(selectedCodeGenType.getValue());
        // 插入数据库
        boolean result = this.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        log.info("应用创建成功，ID: {}, 类型: {}", app.getId(), selectedCodeGenType.getValue());
        return app.getId();
    }


}
