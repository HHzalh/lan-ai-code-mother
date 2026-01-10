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
 * 用户积分账户 实体类。
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_account")
public class UserAccount implements Serializable {

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
     * 邀请码(8位随机字符,每个用户唯一)
     */
    @Column("invitation_code")
    private String invitationCode;

    /**
     * 累计获得积分
     */
    @Column("total_points")
    private Long totalPoints;

    /**
     * 可用积分
     */
    @Column("available_points")
    private Long availablePoints;

    /**
     * 冻结积分(用于订单中)
     */
    @Column("freeze_points")
    private Long freezePoints;

    /**
     * 累计消耗积分
     */
    @Column("total_consume")
    private Long totalConsume;

    /**
     * 连续签到天数
     */
    @Column("continuous_days")
    private Integer continuousDays;

    /**
     * 最后签到日期
     */
    @Column("last_sign_date")
    private LocalDate lastSignDate;

    /**
     * 邀请人数
     */
    @Column("invitation_count")
    private Integer invitationCount;

    /**
     * 累计获得邀请奖励积分
     */
    @Column("total_invite_points")
    private Long totalInvitePoints;

    /**
     * 乐观锁版本号
     */
    @Column("version")
    private Integer version;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

}

