package com.lanhai.lanaicodemother.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到记录 实体类。
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("point_sign_in_record")
public class PointSignInRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 用户ID
     */
    @Column("user_id")
    private Long userId;

    /**
     * 签到日期
     */
    @Column("sign_date")
    private LocalDate signDate;

    /**
     * 本次签到时的连续天数
     */
    @Column("days_count")
    private Integer daysCount;

    /**
     * 本次获得的积分
     */
    @Column("points")
    private Long points;

    /**
     * 是否额外奖励(如连续7天等)
     */
    @Column("is_bonus")
    private Integer isBonus;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

}

