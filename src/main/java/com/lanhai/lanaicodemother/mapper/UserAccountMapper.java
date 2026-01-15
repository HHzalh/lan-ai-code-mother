package com.lanhai.lanaicodemother.mapper;

import com.lanhai.lanaicodemother.model.entity.UserAccount;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 批量增加积分（放弃乐观锁，用于管理员批量发放）
     *
     * @param accountIds 账户ID列表
     * @param points 积分数（所有用户相同）
     * @return 影响行数
     */
    int batchAddPoints(@Param("accountIds") List<Long> accountIds, @Param("points") Long points);

}

