package com.lanhai.lanaicodemother.controller;

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
import com.lanhai.lanaicodemother.services.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户 控制层。
 *
 * @author <a href="https://gitee.com/hhzalh">致爱蓝海</a>
 */
@Slf4j
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
        long userId = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(userId);
    }

    /**
     * 用户注册 - 发送验证码
     *
     * @param email 邮箱地址
     * @return 是否发送成功
     */
    @PostMapping("/send/register/code")
    public BaseResponse<Boolean> sendRegisterEmailCode(String email) {
        ThrowUtils.throwIf(email == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.sendRegisterEmailCode(email);
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
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest, request);
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
    @PostMapping("/send/reset-password/code")
    public BaseResponse<Boolean> findPassword(@RequestBody FindPasswordRequest findPasswordRequest) {
        ThrowUtils.throwIf(findPasswordRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = findPasswordRequest.getUserAccount();
        String email = findPasswordRequest.getEmail();
        boolean result = userService.sendPasswordResetEmailCode(userAccount, email);
        return ResultUtils.success(result);
    }

    /**
     * 重置密码
     *
     * @param resetPasswordRequest 重置密码请求（包含账号、邮箱、验证码、新密码）
     * @return 是否重置成功
     */
    @PostMapping("/reset/password")
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
    @PostMapping("/change/password")
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
        User loginUser = userService.getLoginUser(request);
        boolean result = userService.updateUserInfo(
                loginUser.getId(),
                userUpdateRequest.getUserName(),
                userUpdateRequest.getUserProfile()
        );
        return ResultUtils.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = userService.createUserWithDefaultPassword(userAddRequest);
        return ResultUtils.success(userId);
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
        ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.updateUser(userUpdateRequest);
        return ResultUtils.success(result);
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
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return ResultUtils.success(userVOPage);
    }

}
