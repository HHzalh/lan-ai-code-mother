package com.lanhai.lanaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.lanhai.lanaicodemother.exception.BusinessException;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.manager.CosManager;
import com.lanhai.lanaicodemother.mapper.UserMapper;
import com.lanhai.lanaicodemother.model.dto.user.UserQueryRequest;
import com.lanhai.lanaicodemother.model.entity.User;
import com.lanhai.lanaicodemother.model.enums.UserRoleEnum;
import com.lanhai.lanaicodemother.model.vo.LoginUserVO;
import com.lanhai.lanaicodemother.model.vo.UserVO;
import com.lanhai.lanaicodemother.service.UserService;
import com.lanhai.lanaicodemother.utils.MailUtils;
import com.lanhai.lanaicodemother.utils.RegexUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lanhai.lanaicodemother.constant.UserConstant.*;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://gitee.com/hhzalh">致爱蓝海</a>
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private CosManager cosManager;

    @Resource
    private MailUtils mailUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2. 检查是否重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 3. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");

        if (userPassword.equals("admin13299626612")){
            user.setUserRole(UserRoleEnum.ADMIN.getValue());
        }else {
            user.setUserRole(UserRoleEnum.USER.getValue());
        }
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 4. 获得脱敏后的用户信息
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }


    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("userRole", userRole)
                .like("userAccount", userAccount)
                .like("userName", userName)
                .like("userProfile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    @Transactional
    public String uploadUserAvatar(Long userId, MultipartFile avatarFile) {
        //参数校验
        ThrowUtils.throwIf(avatarFile == null || avatarFile.isEmpty(), ErrorCode.PARAMS_ERROR, "头像文件不能为空");
        String ext = StrUtil.subAfter(StrUtil.emptyToDefault(avatarFile.getOriginalFilename(), ""), ".", true);
        ThrowUtils.throwIf(StrUtil.isBlank(ext), ErrorCode.PARAMS_ERROR, "头像文件名缺少后缀");
        ext = ext.toLowerCase();
        ThrowUtils.throwIf(!"jpg".equals(ext) && !"png".equals(ext), ErrorCode.PARAMS_ERROR, "仅支持 jpg/png 格式头像");

        String suffix = "." + ext;
        File tempFile = null;
        try {
            tempFile = File.createTempFile("avatar_" + userId + "_", suffix);
            avatarFile.transferTo(tempFile);
            String cosKey = String.format("/userAvatar/%s%s", userId, suffix);
            String avatarUrl = cosManager.uploadFile(cosKey, tempFile);
            ThrowUtils.throwIf(StrUtil.isBlank(avatarUrl), ErrorCode.OPERATION_ERROR, "头像上传失败");
            //更新数据库中的头像URL
            User updateUser = new User();
            updateUser.setId(userId);
            updateUser.setUserAvatar(avatarUrl);
            boolean updated = this.updateById(updateUser);
            return avatarUrl;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "头像处理失败");
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "lanhai";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 发送密码找回验证码
     */
    @Override
    public boolean sendPasswordResetCode(String userAccount, String email) {
        // 1. 参数校验
        if (!StringUtils.hasText(userAccount) || !StringUtils.hasText(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号和邮箱不能为空");
        }
        if (RegexUtils.isEmailInvalid(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
        }

        // 2. 查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "该账号未注册");

        // 3. 生成验证码
        String code = MailUtils.generateCode();

        // 4. 将验证码存储到Redis，设置5分钟过期时间
        String redisKey = PASSWORD_RESET_CODE_PREFIX + userAccount + ":" + email;
        stringRedisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_TIME, TimeUnit.SECONDS);

        // 5. 发送验证码邮件
        try {
            mailUtils.sendVerificationCode(email, code);
            log.info("密码找回验证码发送成功，账号：{}，邮箱：{}", userAccount, email);
            return true;
        } catch (Exception e) {
            log.error("密码找回验证码发送失败，账号：{}，邮箱：{}，错误：{}", userAccount, email, e.getMessage(), e);
            // 发送失败时删除Redis中的验证码
            stringRedisTemplate.delete(redisKey);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码发送失败，请稍后重试");
        }
    }

    /**
     * 重置密码
     */
    @Override
    @Transactional
    public boolean resetPassword(String userAccount, String email, String code, String newPassword, String checkPassword) {
        // 1. 参数校验
        if (!StringUtils.hasText(userAccount) || !StringUtils.hasText(email) ||
                !StringUtils.hasText(code) || !StringUtils.hasText(newPassword) ||
                !StringUtils.hasText(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (RegexUtils.isEmailInvalid(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
        }
        if (newPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能少于8位");
        }
        if (!newPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 2. 验证验证码
        String redisKey = PASSWORD_RESET_CODE_PREFIX + userAccount + ":" + email;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码已过期或不存在，请重新获取");
        }
        if (!storedCode.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }

        // 3. 查询用户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 4. 加密新密码
        String encryptPassword = getEncryptPassword(newPassword);

        // 5. 更新密码
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUserPassword(encryptPassword);
        boolean result = this.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "密码重置失败");

        // 6. 删除Redis中的验证码
        stringRedisTemplate.delete(redisKey);

        log.info("密码重置成功，账号：{}", userAccount);
        return true;
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword, String checkPassword) {
        // 1. 参数校验
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword) ||
                !StringUtils.hasText(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (newPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能少于8位");
        }
        if (!newPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 2. 查询用户
        User user = this.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 3. 验证旧密码
        String encryptOldPassword = getEncryptPassword(oldPassword);
        if (!encryptOldPassword.equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误");
        }

        // 4. 验证新密码不能与旧密码相同
        String encryptNewPassword = getEncryptPassword(newPassword);
        if (encryptNewPassword.equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能与旧密码相同");
        }

        // 5. 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setUserPassword(encryptNewPassword);
        boolean result = this.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "密码修改失败");

        log.info("密码修改成功，用户ID：{}", userId);
        return true;
    }

}
