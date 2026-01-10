package com.lanhai.lanaicodemother.service;

import com.lanhai.lanaicodemother.model.dto.point.PointSignInResponse;
import com.lanhai.lanaicodemother.model.vo.point.PointSignInRecordVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 签到 服务层。
 *
 * @author 积分系统
 */
public interface PointSignInService {

    /**
     * 用户签到
     *
     * @param userId 用户ID
     * @return 签到响应
     */
    PointSignInResponse signIn(Long userId);

    /**
     * 获取今日签到状态
     *
     * @param userId 用户ID
     * @return 是否已签到
     */
    Boolean getTodaySignInStatus(Long userId);

    /**
     * 获取签到日历
     *
     * @param userId    用户ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 签到记录列表
     */
    List<PointSignInRecordVO> getSignInCalendar(Long userId, LocalDate startDate, LocalDate endDate);

}

