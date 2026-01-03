package com.lanhai.lanaicodemother.controller;

import cn.hutool.core.bean.BeanUtil;
import com.lanhai.lanaicodemother.annotation.AuthCheck;
import com.lanhai.lanaicodemother.common.BaseResponse;
import com.lanhai.lanaicodemother.common.DeleteRequest;
import com.lanhai.lanaicodemother.common.ResultUtils;
import com.lanhai.lanaicodemother.constant.UserConstant;
import com.lanhai.lanaicodemother.exception.BusinessException;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.model.dto.user.*;
import com.lanhai.lanaicodemother.model.entity.User;
import com.lanhai.lanaicodemother.model.vo.LoginUserVO;
import com.lanhai.lanaicodemother.model.vo.UserVO;
import com.lanhai.lanaicodemother.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author <a href="https://gitee.com/hhzalh">致爱蓝海</a>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 上传用户头像
     *
     * @param file    头像文件（仅支持 jpg/png）
     * @param request 请求
     * @return 头像访问地址
     */
    @PostMapping("/avatar/upload")
    public BaseResponse<String> uploadUserAvatar(MultipartFile file, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String avatarUrl = userService.uploadUserAvatar(loginUser.getId(), file);
        return ResultUtils.success(avatarUrl);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return 当前登录用户信息
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 找回密码 - 发送验证码
     *
     * @param findPasswordRequest 找回密码请求（包含账号和邮箱）
     * @return 是否发送成功
     */
    @PostMapping("/password/find")
    public BaseResponse<Boolean> findPassword(@RequestBody FindPasswordRequest findPasswordRequest) {
        ThrowUtils.throwIf(findPasswordRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = findPasswordRequest.getUserAccount();
        String email = findPasswordRequest.getEmail();
        boolean result = userService.sendPasswordResetCode(userAccount, email);
        return ResultUtils.success(result);
    }

    /**
     * 重置密码
     *
     * @param resetPasswordRequest 重置密码请求（包含账号、邮箱、验证码、新密码）
     * @return 是否重置成功
     */
    @PostMapping("/password/reset")
    public BaseResponse<Boolean> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        ThrowUtils.throwIf(resetPasswordRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = resetPasswordRequest.getUserAccount();
        String email = resetPasswordRequest.getEmail();
        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();
        String checkPassword = resetPasswordRequest.getCheckPassword();
        boolean result = userService.resetPassword(userAccount, email, code, newPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 修改密码
     *
     * @param changePasswordRequest 修改密码请求（包含旧密码、新密码、确认新密码）
     * @param request               请求
     * @return 是否修改成功
     */
    @PostMapping("/password/change")
    public BaseResponse<Boolean> changePassword(@RequestBody UserChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(changePasswordRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String checkPassword = changePasswordRequest.getCheckPassword();
        boolean result = userService.changePassword(loginUser.getId(), oldPassword, newPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 更新用户个人信息
     *
     * @param userUpdateRequest 用户更新请求（仅可更新 userName 和 userProfile）
     * @param request           请求
     * @return 是否更新成功
     */
    @PostMapping("/update/info")
    public BaseResponse<Boolean> updateUserInfo(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 创建更新对象，只更新 userName 和 userProfile
        User updateUser = new User();
        updateUser.setId(loginUser.getId());
        // 只更新请求中提供的字段
        if (userUpdateRequest.getUserName() != null) {
            updateUser.setUserName(userUpdateRequest.getUserName());
        }
        if (userUpdateRequest.getUserProfile() != null) {
            updateUser.setUserProfile(userUpdateRequest.getUserProfile());
        }
        // 执行更新
        boolean result = userService.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = userQueryRequest.getPageNum();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(Page.of(pageNum, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        // 数据脱敏
        Page<UserVO> userVOPage = new Page<>(pageNum, pageSize, userPage.getTotalRow());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

}
