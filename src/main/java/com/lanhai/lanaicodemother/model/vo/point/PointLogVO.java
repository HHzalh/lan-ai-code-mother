package com.lanhai.lanaicodemother.model.vo.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分流水 VO
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointLogVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流水ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务类型文本
     */
    private String businessTypeText;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 积分类型
     */
    private String pointType;

    /**
     * 积分类型文本
     */
    private String pointTypeText;

    /**
     * 积分变动数
     */
    private Long pointChange;

    /**
     * 变动前积分
     */
    private Long beforePoints;

    /**
     * 变动后积分
     */
    private Long afterPoints;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}

