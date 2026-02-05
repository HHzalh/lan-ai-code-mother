package com.lanhai.lanaicodemother.controller;

import com.lanhai.lanaicodemother.annotation.AuthCheck;
import com.lanhai.lanaicodemother.common.BaseResponse;
import com.lanhai.lanaicodemother.common.ResultUtils;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.innerservice.InnerUserService;
import com.lanhai.lanaicodemother.model.dto.point.PointLogQueryRequest;
import com.lanhai.lanaicodemother.model.dto.point.PointRuleUpdateRequest;
import com.lanhai.lanaicodemother.model.dto.point.PointSignInResponse;
import com.lanhai.lanaicodemother.model.dto.point.UserAccountQueryRequest;
import com.lanhai.lanaicodemother.model.entity.User;
import com.lanhai.lanaicodemother.model.vo.point.PointLogVO;
import com.lanhai.lanaicodemother.model.vo.point.PointRuleVO;
import com.lanhai.lanaicodemother.model.vo.point.UserAccountVO;
import com.lanhai.lanaicodemother.service.*;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分系统 控制层。
 *
 * @author 积分系统
 */
@RestController
@RequestMapping("/point")
public class PointController {

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private PointSignInService pointSignInService;

    @Resource
    private PointLogService pointLogService;

    @Resource
    private PointRuleService pointRuleService;

    @Resource
    private PointService pointService;

    /**
     * 获取我的积分账户
     *
     * @param request HTTP请求
     * @return 积分账户VO
     */
    @GetMapping("/account")
    public BaseResponse<UserAccountVO> getMyAccount(HttpServletRequest request) {
        User loginUser = InnerUserService.getLoginUser(request);
        UserAccountVO accountVO = userAccountService.getAccountVO(loginUser.getId());
        return ResultUtils.success(accountVO);
    }

    /**
     * 用户签到
     *
     * @param request HTTP请求
     * @return 签到结果
     */
    @PostMapping("/sign-in")
    public BaseResponse<PointSignInResponse> signIn(HttpServletRequest request) {
        User loginUser = InnerUserService.getLoginUser(request);
        PointSignInResponse response = pointSignInService.signIn(loginUser.getId());
        return ResultUtils.success(response);
    }

    /**
     * 获取今日签到状态
     *
     * @param request HTTP请求
     * @return 签到状态
     */
    @GetMapping("/sign-status")
    public BaseResponse<Boolean> getSignStatus(HttpServletRequest request) {
        User loginUser = InnerUserService.getLoginUser(request);
        Boolean signed = pointSignInService.getTodaySignInStatus(loginUser.getId());
        return ResultUtils.success(signed);
    }

    /**
     * 获取我的积分流水（普通用户专用）
     * 只能查询当前登录用户的流水，无需传递用户ID
     *
     * @param request      HTTP请求
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    @PostMapping("/my-logs")
    public BaseResponse<Page<PointLogVO>> getMyLogs(
            HttpServletRequest request,
            @RequestBody PointLogQueryRequest queryRequest) {
        User loginUser = InnerUserService.getLoginUser(request);
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "查询参数不能为空");

        // 强制使用当前登录用户的ID
        Page<PointLogVO> page = pointLogService.pageLogs(loginUser.getId(), queryRequest);
        return ResultUtils.success(page);
    }

    /**
     * 获取积分流水（管理员专用）
     * 可以查询指定用户的流水，也可以查询所有用户的流水
     *
     * @param request      HTTP请求
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    @PostMapping("/logs")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<PointLogVO>> getLogs(
            HttpServletRequest request,
            @RequestBody PointLogQueryRequest queryRequest) {
        User loginUser = InnerUserService.getLoginUser(request);
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "查询参数不能为空");

        // 管理员可以查询指定用户的流水，如果userId为空则查询所有用户
        Page<PointLogVO> page = pointLogService.pageLogs(queryRequest.getUserId(), queryRequest);
        return ResultUtils.success(page);
    }

    /**
     * 获取我的邀请码
     *
     * @param request HTTP请求
     * @return 邀请码信息
     */
    @GetMapping("/invitation/my-code")
    public BaseResponse<String> getMyInvitationCode(HttpServletRequest request) {
        User loginUser = InnerUserService.getLoginUser(request);
        String code = userAccountService.getOrCreateInvitationCode(loginUser.getId());
        return ResultUtils.success(code);
    }

    /**
     * 获取积分规则
     *
     * @return 规则列表
     */
    @GetMapping("/rules/all")
    public BaseResponse<List<PointRuleVO>> getAllRules() {
        List<PointRuleVO> rules = pointRuleService.getAllRules();
        return ResultUtils.success(rules);
    }

    /**
     * 获取用户积分账户列表（管理员）
     *
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    @PostMapping("/accounts")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<UserAccountVO>> listAccounts(@RequestBody UserAccountQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "查询参数不能为空");
        Page<UserAccountVO> page = userAccountService.pageAccounts(queryRequest);
        return ResultUtils.success(page);
    }

    /**
     * 更新积分规则（管理员）
     *
     * @param updateRequest 更新请求
     * @return 更新结果
     */
    @PutMapping("/rules")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateRule(@RequestBody PointRuleUpdateRequest updateRequest) {
        ThrowUtils.throwIf(updateRequest == null, ErrorCode.PARAMS_ERROR, "参数不能为空");
        boolean updated = pointRuleService.updateRule(updateRequest);
        return ResultUtils.success(updated);
    }


    /**
     * 管理员给用户发放积分
     *
     * @param userId 用户ID
     * @param points 积分数
     * @param remark 备注
     * @return 发放结果
     */
    @PostMapping("/rules/grant")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> grantPoints(
            @RequestParam Long userId,
            @RequestParam Long points,
            @RequestParam String remark) {
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        ThrowUtils.throwIf(points == null || points <= 0, ErrorCode.PARAMS_ERROR, "积分数必须大于0");
        ThrowUtils.throwIf(StringUtils.isBlank(remark), ErrorCode.PARAMS_ERROR, "备注不能为空");
        boolean granted = pointService.grantPoints(userId, points, remark);
        return ResultUtils.success(granted);
    }

    /**
     * 管理员给所有用户发放积分
     *
     * @param points 积分数
     * @param remark 备注
     * @return 发放结果
     */
    @PostMapping("/rules/grant-all")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Integer> grantPointsToAll(
            @RequestParam Long points,
            @RequestParam String remark) {
        ThrowUtils.throwIf(points == null || points <= 0, ErrorCode.PARAMS_ERROR, "积分数必须大于0");
        ThrowUtils.throwIf(StringUtils.isBlank(remark), ErrorCode.PARAMS_ERROR, "备注不能为空");
        int count = pointService.grantPointsToAll(points, remark);
        return ResultUtils.success(count);
    }

}

