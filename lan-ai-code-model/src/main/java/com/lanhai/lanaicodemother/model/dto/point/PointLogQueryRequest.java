package com.lanhai.lanaicodemother.model.dto.point;

import com.lanhai.lanaicodemother.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分流水查询请求
 *
 * @author 积分系统
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PointLogQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 积分类型
     */
    private String pointType;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 用户ID（管理员可以查询指定用户的流水）
     */
    private Long userId;

}

