package com.lanhai.lanaicodemother.model.vo.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邀请码 VO
 *
 * @author 积分系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationCodeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 邀请链接
     */
    private String inviteUrl;

    /**
     * 邀请人数
     */
    private Integer invitationCount;

    /**
     * 累计获得邀请奖励积分
     */
    private Long totalInvitePoints;

}

