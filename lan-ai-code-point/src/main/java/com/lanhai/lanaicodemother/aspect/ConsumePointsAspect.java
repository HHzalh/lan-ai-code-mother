package com.lanhai.lanaicodemother.aspect;


import com.lanhai.lanaicodemother.annotation.ConsumePoints;
import com.lanhai.lanaicodemother.exception.BusinessException;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.innerservice.InnerUserService;
import com.lanhai.lanaicodemother.mapper.PointLogMapper;
import com.lanhai.lanaicodemother.model.entity.PointLog;
import com.lanhai.lanaicodemother.model.entity.User;
import com.lanhai.lanaicodemother.service.PointRuleService;
import com.lanhai.lanaicodemother.service.PointService;
import com.lanhai.lanaicodemother.service.UserAccountService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;

/**
 * 积分消耗切面
 * 拦截标注了 @ConsumePoints 注解的方法，自动处理积分扣减
 * 优先级：@Order(2) - 在限流检查通过后执行
 *
 * @author 积分系统
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class ConsumePointsAspect {

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private PointRuleService pointRuleService;

    @Resource
    private PointLogMapper pointLogMapper;

    @Resource
    private PointService pointService;

    /**
     * 积分扣减切面（使用 @Around 实现异常回滚）
     * 执行顺序：限流检查 → 积分扣减 → 目标方法
     */
    @Around("@annotation(com.lanhai.lanaicodemother.annotation.ConsumePoints)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取方法信息和注解
        Method method = getMethod(joinPoint);
        ConsumePoints annotation = method.getAnnotation(ConsumePoints.class);

        // 2. 从方法参数中提取用户ID
        Long userId = extractUserId(joinPoint, method, annotation.userIdParam());
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "无法获取用户ID，请确保已登录或方法参数中包含userId或HttpServletRequest");
        }

        // 3. 判断业务ID是从参数获取还是从返回值获取
        String businessId;
        String tempBusinessId = null; // 用于记录临时ID，以便后续更新
        boolean needUpdateBusinessId = false;

        if (annotation.businessIdFromReturnValue()) {
            // 3.1 从返回值获取业务ID
            // 生成临时 businessId
            tempBusinessId = "TEMP_" + UUID.randomUUID().toString().replace("-", "");
            businessId = tempBusinessId;
            needUpdateBusinessId = true;
            log.info("使用临时业务ID进行积分扣减，临时ID：{}，用户ID：{}", businessId, userId);
        } else {
            // 3.2 从参数获取业务ID（默认行为）
            businessId = extractBusinessId(joinPoint, method, annotation.businessIdParam());
            if (businessId == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法获取业务ID，请确保方法参数中包含" + annotation.businessIdParam());
            }
        }

        // 4. 检查是否首次操作（如果配置了 once）
        if (annotation.once() && !needUpdateBusinessId) {
            // 仅当不需要更新 businessId 时才检查是否已消费
            if (hasConsumedBefore(userId, annotation.businessType().getValue(), businessId)) {
                log.info("该业务已扣费过，不再扣减，直接执行原方法，用户ID：{}，业务类型：{}，业务ID：{}",
                        userId, annotation.businessType().getText(), businessId);
                return joinPoint.proceed();  // 已扣费，直接执行原方法
            }
        }

        // 5. 获取需要扣减的积分数
        Long points = pointRuleService.getRuleValue(annotation.ruleKey());

        // 6. 检查积分是否足够
        boolean enough = checkPointsEnough(userId, points);
        if (!enough) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
        }

        // 7. 扣减积分
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

        // 8. 执行原方法并处理异常
        Object result;
        try {
            result = joinPoint.proceed();  // 执行目标方法
        } catch (Throwable throwable) {
            // 8.1 方法执行失败，需要退回积分
            log.error("业务执行失败，开始退回积分，用户ID：{}，业务类型：{}，业务ID：{}，积分数：{}，异常信息：{}",
                    userId, annotation.businessType().getText(), businessId, points, throwable.getMessage());

            // 8.2 生成退回备注
            String refundRemark = String.format("%s失败，退回%d积分", annotation.businessType().getText(), points);

            // 8.3 退回积分
            try {
                pointService.grantPoints(userId, points, refundRemark);
                log.info("积分退回成功，用户ID：{}，业务类型：{}，业务ID：{}，退回积分：{}，备注：{}",
                        userId, annotation.businessType().getText(), businessId, points, refundRemark);
            } catch (Exception refundException) {
                // 退回积分失败，记录错误日志
                log.error("积分退回失败，用户ID：{}，业务类型：{}，业务ID：{}，应退回积分：{}，备注：{}",
                        userId, annotation.businessType().getText(), businessId, points, refundRemark, refundException);
                // 退回失败不应该阻止原异常的抛出，继续抛出原异常
            }

            // 8.4 重新抛出原异常
            throw throwable;
        }

        // 9. 如果需要更新业务ID（从返回值中提取真实ID）
        if (needUpdateBusinessId && tempBusinessId != null) {
            String realBusinessId = extractBusinessIdFromReturnValue(result);
            if (realBusinessId != null) {
                updateBusinessIdInPointLog(userId, annotation.businessType().getValue(),
                        tempBusinessId, realBusinessId);
                log.info("业务ID更新成功，用户ID：{}，临时ID：{}，真实ID：{}", userId, tempBusinessId, realBusinessId);
            } else {
                log.warn("无法从返回值中提取业务ID，保留临时ID：{}，返回值类型：{}",
                        tempBusinessId, result != null ? result.getClass().getName() : "null");
            }
        }

        return result;
    }

    /**
     * 获取方法对象
     * 使用签名信息获取方法，而不是通过参数类型推断，避免运行时类型与声明类型不匹配的问题
     */
    private Method getMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    /**
     * 提取用户ID
     * 优先从方法参数中提取userId，如果没有则从HttpServletRequest中获取
     */
    private Long extractUserId(JoinPoint joinPoint, Method method, String userIdParamName) {
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
            // 使用 Stream 从所有参数中查找 HttpServletRequest
            Object[] args = joinPoint.getArgs();

            request = java.util.Arrays.stream(args)
                    .filter(arg -> arg instanceof HttpServletRequest)
                    .map(arg -> (HttpServletRequest) arg)
                    .findFirst()
                    .orElse(null);
        }

        // 4. 如果找到了HttpServletRequest，从中获取用户ID
        if (request != null) {
            try {
                User loginUser = InnerUserService.getLoginUser(request);
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
     * 使用 for-each 遍历（纯遍历操作，无复杂逻辑）
     */
    private <T> T extractParameter(JoinPoint joinPoint, Method method, String paramName, Class<T> type) {
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        // 使用 for-each 优化纯遍历操作
        int index = 0;
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(paramName) && type.isInstance(args[index])) {
                return type.cast(args[index]);
            }
            index++;
        }
        return null;
    }

    /**
     * 提取业务ID
     * 支持从方法参数中提取，也支持从对象属性中提取（如 appDeployRequest.getAppId()）
     */
    private String extractBusinessId(JoinPoint joinPoint, Method method, String businessIdParam) {
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

        // 3. 尝试从所有参数中查找路径变量或请求参数（使用 for-each 优化纯遍历）
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        int index = 0;
        for (Parameter param : parameters) {
            // 检查是否是 @PathVariable 或 @RequestParam 标注的参数
            if (param.getName().equals(businessIdParam) || param.getName().equals("appId")) {
                Object arg = args[index];
                if (arg != null) {
                    return String.valueOf(arg);
                }
            }
            index++;
        }

        return null;
    }

    /**
     * 从方法参数中提取原始值
     * 使用 for-each 遍历（纯遍历操作，无复杂逻辑）
     */
    private Object extractParameterRaw(JoinPoint joinPoint, Method method, String paramName) {
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        // 使用 for-each 优化纯遍历操作
        int index = 0;
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(paramName)) {
                return args[index];
            }
            index++;
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

    /**
     * 从返回值中提取业务ID
     * 支持 Long、String、BaseResponse 等多种返回类型
     */
    private String extractBusinessIdFromReturnValue(Object returnValue) {
        if (returnValue == null) {
            return null;
        }

        // 1. 如果返回值是 Long 或 Integer，直接转换
        if (returnValue instanceof Long || returnValue instanceof Integer) {
            return String.valueOf(returnValue);
        }

        // 2. 如果返回值是 String，直接返回
        if (returnValue instanceof String) {
            return (String) returnValue;
        }

        try {
            java.lang.reflect.Method getDataMethod = returnValue.getClass().getMethod("getData");
            Object data = getDataMethod.invoke(returnValue);
            if (data != null) {
                return extractBusinessIdFromReturnValue(data); // 递归处理
            }
        } catch (Exception e) {
            log.debug("无法从 BaseResponse 中提取 data：{}", e.getMessage());
        }

        log.warn("无法从返回值中提取业务ID，返回值类型：{}", returnValue.getClass().getName());
        return null;
    }

    /**
     * 更新积分流水中的业务ID
     * 将临时 businessId 更新为真实的 businessId
     */
    private void updateBusinessIdInPointLog(Long userId, String businessType,
                                            String tempBusinessId, String realBusinessId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("business_type", businessType);
        queryWrapper.eq("business_id", tempBusinessId);

        // 查询使用临时 businessId 的积分流水记录
        PointLog pointLog = pointLogMapper.selectOneByQuery(queryWrapper);
        if (pointLog != null) {
            pointLog.setBusinessId(realBusinessId);
            pointLogMapper.update(pointLog);
            log.info("积分流水业务ID更新成功，流水ID：{}，原临时ID：{}，新真实ID：{}",
                    pointLog.getId(), tempBusinessId, realBusinessId);
        } else {
            log.error("未找到使用临时 businessId 的积分流水记录，用户ID：{}，业务类型：{}，临时ID：{}",
                    userId, businessType, tempBusinessId);
        }
    }
}

