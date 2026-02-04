package com.lanhai.lanaicodemother.model.dto.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 签到响应
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointSignInResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 本次获得的积分
     */
    private Long points;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 是否有额外奖励
     */
    private Boolean isBonus;

    /**
     * 当前可用积分
     */
    private Long availablePoints;

}

