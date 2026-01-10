package com.lanhai.lanaicodemother.model.dto.point;

import com.lanhai.lanaicodemother.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户积分账户查询请求
 *
 * @author 积分系统
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAccountQueryRequest extends PageRequest implements Serializable {

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
     * 最小可用积分
     */
    private Long minAvailablePoints;

    /**
     * 最大可用积分
     */
    private Long maxAvailablePoints;

    /**
     * 最小累计积分
     */
    private Long minTotalPoints;

    /**
     * 最大累计积分
     */
    private Long maxTotalPoints;

    /**
     * 用户昵称（模糊查询）
     */
    private String userNickname;

}

