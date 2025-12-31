package com.lanhai.lanaicodemother.service;

import com.lanhai.lanaicodemother.model.dto.user.UserQueryRequest;
import com.lanhai.lanaicodemother.model.entity.User;
import com.lanhai.lanaicodemother.model.vo.LoginUserVO;
import com.lanhai.lanaicodemother.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author <a href="https://gitee.com/hhzalh">致爱蓝海</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return 退出登录是否成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息(分页)
     *
     * @param userList 用户列表
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 根据查询条件构造数据查询参数
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 加密
     *
     * @param userPassword 用户密码
     * @return 加密后的用户密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 上传并更新用户头像
     *
     * @param userId     用户ID
     * @param avatarFile 头像文件
     * @return 头像访问地址
     */
    String uploadUserAvatar(Long userId, MultipartFile avatarFile);

    /**
     * 发送密码找回验证码
     *
     * @param userAccount 账号
     * @param email       邮箱地址
     * @return 是否发送成功
     */
    boolean sendPasswordResetCode(String userAccount, String email);

    /**
     * 重置密码
     *
     * @param userAccount   账号
     * @param email         邮箱地址
     * @param code          验证码
     * @param newPassword   新密码
     * @param checkPassword 确认新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String userAccount, String email, String code, String newPassword, String checkPassword);
}
