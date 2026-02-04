package com.lanhai.lanaicodemother.model.vo.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到记录 VO
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointSignInRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 签到记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 签到日期
     */
    private LocalDate signDate;

    /**
     * 本次签到时的连续天数
     */
    private Integer daysCount;

    /**
     * 本次获得的积分
     */
    private Long points;

    /**
     * 是否额外奖励
     */
    private Integer isBonus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}

