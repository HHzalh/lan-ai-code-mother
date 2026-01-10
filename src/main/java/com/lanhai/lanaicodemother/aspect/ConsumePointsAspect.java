package com.lanhai.lanaicodemother.aspect;

import com.lanhai.lanaicodemother.annotation.ConsumePoints;
import com.lanhai.lanaicodemother.exception.BusinessException;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.mapper.PointLogMapper;
import com.lanhai.lanaicodemother.model.entity.User;
import com.lanhai.lanaicodemother.service.PointRuleService;
import com.lanhai.lanaicodemother.service.UserAccountService;
import com.lanhai.lanaicodemother.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 积分消耗切面
 * 拦截标注了 @ConsumePoints 注解的方法，自动处理积分扣减
 *
 * @author 积分系统
 */
@Slf4j
@Aspect
@Component
public class ConsumePointsAspect {

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private PointRuleService pointRuleService;

    @Resource
    private PointLogMapper pointLogMapper;

    @Resource
    private UserService userService;

    @Around("@annotation(com.lanhai.lanaicodemother.annotation.ConsumePoints)")
    @Transactional(rollbackFor = Exception.class)
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取方法信息和注解
        Method method = getMethod(joinPoint);
        ConsumePoints annotation = method.getAnnotation(ConsumePoints.class);

        // 2. 从方法参数中提取用户ID和业务ID
        Long userId = extractUserId(joinPoint, method, annotation.userIdParam());
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "无法获取用户ID，请确保已登录或方法参数中包含userId或HttpServletRequest");
        }
        
        String businessId = extractBusinessId(joinPoint, method, annotation.businessIdParam());
        if (businessId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法获取业务ID，请确保方法参数中包含" + annotation.businessIdParam());
        }

        // 3. 检查是否首次操作（如果配置了 once）
        if (annotation.once()) {
            if (hasConsumedBefore(userId, annotation.businessType().getValue(), businessId)) {
                log.info("该业务已扣费过，不再扣减，用户ID：{}，业务类型：{}，业务ID：{}",
                        userId, annotation.businessType().getText(), businessId);
                return joinPoint.proceed();
            }
        }

        // 4. 获取需要扣减的积分数
        Long points = pointRuleService.getRuleValue(annotation.ruleKey());

        // 5. 检查积分是否足够
        boolean enough = checkPointsEnough(userId, points);
        if (!enough) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
        }

        // 6. 扣减积分
        try {
            userAccountService.deductPoints(
                    userId,
                    points,
                    annotation.businessType().getValue(),
                    businessId,
                    annotation.businessType().getText()
            );
            log.info("积分扣减成功，用户ID：{}，业务类型：{}，业务ID：{}，积分：{}",
                    userId, annotation.businessType().getText(), businessId, points);
        } catch (Exception e) {
            log.error("积分扣减失败，用户ID：{}，业务类型：{}，业务ID：{}，积分：{}",
                    userId, annotation.businessType().getText(), businessId, points, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "积分扣减失败");
        }

        // 7. 执行原方法
        return joinPoint.proceed();
    }

    /**
     * 获取方法对象
     * 使用签名信息获取方法，而不是通过参数类型推断，避免运行时类型与声明类型不匹配的问题
     */
    private Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    /**
     * 提取用户ID
     * 优先从方法参数中提取userId，如果没有则从HttpServletRequest中获取
     */
    private Long extractUserId(ProceedingJoinPoint joinPoint, Method method, String userIdParamName) {
        // 1. 尝试从方法参数中提取userId（Long类型）
        Long userId = extractParameter(joinPoint, method, userIdParamName, Long.class);
        if (userId != null) {
            return userId;
        }

        // 2. 尝试从方法参数中提取User对象
        User user = extractParameter(joinPoint, method, userIdParamName, User.class);
        if (user != null && user.getId() != null) {
            return user.getId();
        }

        // 3. 尝试从方法参数中提取HttpServletRequest
        HttpServletRequest request = extractParameter(joinPoint, method, "request", HttpServletRequest.class);
        if (request == null) {
            // 尝试其他常见的参数名
            request = extractParameter(joinPoint, method, "httpServletRequest", HttpServletRequest.class);
        }
        if (request == null) {
            // 尝试从所有参数中查找HttpServletRequest类型
            Parameter[] parameters = method.getParameters();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < parameters.length; i++) {
                if (args[i] instanceof HttpServletRequest) {
                    request = (HttpServletRequest) args[i];
                    break;
                }
            }
        }

        // 4. 如果找到了HttpServletRequest，从中获取用户ID
        if (request != null) {
            try {
                User loginUser = userService.getLoginUser(request);
                return loginUser != null ? loginUser.getId() : null;
            } catch (Exception e) {
                log.warn("从HttpServletRequest中获取用户失败：{}", e.getMessage());
                return null;
            }
        }

        return null;
    }

    /**
     * 从方法参数中提取指定类型的值
     */
    private <T> T extractParameter(ProceedingJoinPoint joinPoint, Method method, String paramName, Class<T> type) {
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName) && type.isInstance(args[i])) {
                return type.cast(args[i]);
            }
        }
        return null;
    }

    /**
     * 提取业务ID
     * 支持从方法参数中提取，也支持从对象属性中提取（如 appDeployRequest.getAppId()）
     */
    private String extractBusinessId(ProceedingJoinPoint joinPoint, Method method, String businessIdParam) {
        // 1. 尝试直接提取参数（String 或 Long 类型）
        String businessId = extractParameter(joinPoint, method, businessIdParam, String.class);
        if (businessId != null) {
            return businessId;
        }

        Long businessIdLong = extractParameter(joinPoint, method, businessIdParam, Long.class);
        if (businessIdLong != null) {
            return String.valueOf(businessIdLong);
        }

        // 2. 尝试从对象中提取属性值（如 appDeployRequest.getAppId()）
        Object paramValue = extractParameterRaw(joinPoint, method, businessIdParam);
        if (paramValue != null) {
            // 如果是基本类型，直接转换
            if (paramValue instanceof String) {
                return (String) paramValue;
            }
            if (paramValue instanceof Long || paramValue instanceof Integer) {
                return String.valueOf(paramValue);
            }
            
            // 如果是对象，尝试通过反射获取 appId 属性
            try {
                java.lang.reflect.Method getterMethod = paramValue.getClass().getMethod("getAppId");
                Object appIdValue = getterMethod.invoke(paramValue);
                if (appIdValue != null) {
                    return String.valueOf(appIdValue);
                }
            } catch (Exception e) {
                log.debug("无法从对象 {} 中提取 appId 属性：{}", paramValue.getClass().getName(), e.getMessage());
            }
        }

        // 3. 尝试从所有参数中查找路径变量或请求参数
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            // 检查是否是 @PathVariable 或 @RequestParam 标注的参数
            if (param.getName().equals(businessIdParam) || 
                param.getName().equals("appId")) {
                Object arg = args[i];
                if (arg != null) {
                    return String.valueOf(arg);
                }
            }
        }

        return null;
    }

    /**
     * 从方法参数中提取原始值
     */
    private Object extractParameterRaw(ProceedingJoinPoint joinPoint, Method method, String paramName) {
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                return args[i];
            }
        }
        return null;
    }

    /**
     * 检查是否已经扣费过
     */
    private boolean hasConsumedBefore(Long userId, String businessType, String businessId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("business_type", businessType);
        queryWrapper.eq("business_id", businessId);
        long count = pointLogMapper.selectCountByQuery(queryWrapper);
        return count > 0;
    }

    /**
     * 检查积分是否足够
     */
    private boolean checkPointsEnough(Long userId, Long requiredPoints) {
        return userAccountService.getByUserId(userId).getAvailablePoints() >= requiredPoints;
    }
}

