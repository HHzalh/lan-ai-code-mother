package com.lanhai.lanaicodemother.model.vo.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户积分账户 VO
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 累计获得积分
     */
    private Long totalPoints;

    /**
     * 可用积分
     */
    private Long availablePoints;

    /**
     * 冻结积分
     */
    private Long freezePoints;

    /**
     * 累计消耗积分
     */
    private Long totalConsume;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 最后签到日期
     */
    private LocalDate lastSignDate;

    /**
     * 邀请人数
     */
    private Integer invitationCount;

    /**
     * 累计获得邀请奖励积分
     */
    private Long totalInvitePoints;

}

