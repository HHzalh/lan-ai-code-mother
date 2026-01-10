package com.lanhai.lanaicodemother.mapper;

import com.lanhai.lanaicodemother.model.entity.UserAccount;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户积分账户 映射层。
 *
 * @author 积分系统
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    /**
     * 乐观锁增加积分
     */
    int addPointsWithVersion(@Param("account") UserAccount account, @Param("points") Long points);

    /**
     * 乐观锁扣减积分
     */
    int deductPointsWithVersion(@Param("account") UserAccount account, @Param("points") Long points);

    /**
     * 乐观锁更新连续天数和最后签到日期
     */
    int updateSignInStatusWithVersion(@Param("account") UserAccount account, @Param("continuousDays") Integer continuousDays);

    /**
     * 乐观锁更新邀请统计
     */
    int updateInviteStatsWithVersion(@Param("account") UserAccount account, @Param("inviterPoints") Long inviterPoints);

}

