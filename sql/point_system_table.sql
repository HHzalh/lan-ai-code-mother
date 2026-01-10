-- 积分签到与邀请码系统建表脚本
-- @author 积分系统
-- @date 2026-01-10

-- 使用数据库
use lan_ai_code_mother;

-- ============================================
-- 1. 用户积分账户表（包含邀请码）
-- ============================================
create table if not exists user_account
(
    id                  bigint auto_increment comment 'id' primary key,
    user_id             bigint                             not null comment '用户ID',
    invitation_code     varchar(32)                        null comment '邀请码(8位随机字符,每个用户唯一)',
    total_points        bigint   default 0                 not null comment '累计获得积分',
    available_points    bigint   default 0                 not null comment '可用积分',
    freeze_points       bigint   default 0                 not null comment '冻结积分(用于订单中)',
    total_consume       bigint   default 0                 not null comment '累计消耗积分',
    continuous_days     int      default 0                 not null comment '连续签到天数',
    last_sign_date      date                               null comment '最后签到日期',
    invitation_count    int      default 0                 not null comment '邀请人数',
    total_invite_points bigint   default 0                 not null comment '累计获得邀请奖励积分',
    version             int      default 0                 not null comment '乐观锁版本号',
    createTime          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime          datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete            tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userId (user_id),
    UNIQUE KEY uk_invitationCode (invitation_code),
    INDEX idx_available_points (available_points)
) comment '用户积分账户' collate = utf8mb4_unicode_ci;

-- ============================================
-- 2. 积分流水表（包含邀请记录）
-- ============================================
create table if not exists point_log
(
    id            bigint auto_increment comment 'id' primary key,
    user_id       bigint                             not null comment '用户ID',
    business_type varchar(64)                        not null comment '业务类型:SIGN_IN/SIGN_IN_BONUS/REGISTER_REWARD/INVITEE_BONUS/INVITER_BONUS/GENERATE/DEPLOY/DOWNLOAD/REFUND/SYSTEM_GRANT等',
    business_id   varchar(128)                       null comment '业务ID(如应用ID、邀请码、被邀请人ID等)',
    point_type    varchar(32)                        not null comment '积分类型:INCOME/EXPENSE',
    point_change  bigint                             not null comment '积分变动数(正数为增加,负数为减少)',
    before_points bigint                             not null comment '变动前积分',
    after_points  bigint                             not null comment '变动后积分',
    remark        varchar(512)                       null comment '备注',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    INDEX idx_userId (user_id),
    INDEX idx_businessId (business_id),
    INDEX idx_createTime (createTime),
    INDEX idx_businessType (business_type)
) comment '积分流水' collate = utf8mb4_unicode_ci;

-- ============================================
-- 3. 签到记录表
-- ============================================
create table if not exists point_sign_in_record
(
    id         bigint auto_increment comment 'id' primary key,
    user_id    bigint                             not null comment '用户ID',
    sign_date  date                               not null comment '签到日期',
    days_count int                                not null comment '本次签到时的连续天数',
    points     bigint                             not null comment '本次获得的积分',
    is_bonus   tinyint  default 0                 not null comment '是否额外奖励(如连续7天等)',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    UNIQUE KEY uk_userId_date (user_id, sign_date),
    INDEX idx_userId (user_id)
) comment '签到记录' collate = utf8mb4_unicode_ci;

-- ============================================
-- 4. 积分规则表
-- ============================================
create table if not exists point_rule
(
    id         bigint auto_increment comment 'id' primary key,
    rule_key   varchar(64)                        not null comment '规则键:SIGN_IN_BASE/SIGN_IN_CONTINUOUS_3/SIGN_IN_CONTINUOUS_7/REGISTER_REWARD/INVITE_NEW/INVITE_REWARD/DEPLOY_COST/GENERATE_COST',
    rule_value bigint                             not null comment '规则值(积分数)',
    rule_desc  varchar(512)                       null comment '规则描述',
    status     tinyint  default 1                 not null comment '状态:0-禁用 1-启用',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    UNIQUE KEY uk_ruleKey (rule_key)
) comment '积分规则' collate = utf8mb4_unicode_ci;

-- ============================================
-- 初始化数据
-- ============================================

-- 签到积分规则
INSERT INTO point_rule (rule_key, rule_value, rule_desc)
VALUES ('SIGN_IN_BASE', 10, '每日签到基础积分'),
       ('SIGN_IN_CONTINUOUS_3', 10, '连续3天额外奖励'),
       ('SIGN_IN_CONTINUOUS_7', 50, '连续7天额外奖励');

-- 注册奖励规则
INSERT INTO point_rule (rule_key, rule_value, rule_desc)
VALUES ('REGISTER_REWARD', 100, '注册奖励');

-- 邀请奖励规则
INSERT INTO point_rule (rule_key, rule_value, rule_desc)
VALUES ('INVITE_NEW', 50, '被邀请人注册奖励'),
       ('INVITE_REWARD', 30, '邀请人奖励');

-- 消耗积分规则
INSERT INTO point_rule (rule_key, rule_value, rule_desc)
VALUES ('DEPLOY_COST', 30, '部署应用消耗积分'),
       ('GENERATE_COST', 20, '生成应用消耗积分'),
       ('DOWNLOAD_COST', 30, '下载代码消耗积分');

-- ============================================
-- 为已有用户创建积分账户并生成邀请码
-- ============================================
INSERT INTO user_account (user_id, invitation_code, total_points, available_points, freeze_points,
                          total_consume, continuous_days, last_sign_date, invitation_count,
                          total_invite_points, version, createTime, updateTime, isDelete)
SELECT id    as user_id,
       -- 生成8位随机邀请码（排除易混淆字符 I,1,O,0）
       CONCAT(
               SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND() * 30) + 1, 1),
               SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND() * 30) + 1, 1),
               SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND() * 30) + 1, 1),
               SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND() * 30) + 1, 1),
               SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND() * 30) + 1, 1),
               SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND() * 30) + 1, 1),
               SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND() * 30) + 1, 1),
               SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND() * 30) + 1, 1)
       )     as invitation_code,
       0     as total_points,
       0     as available_points,
       0     as freeze_points,
       0     as total_consume,
       0     as continuous_days,
       NULL  as last_sign_date,
       0     as invitation_count,
       0     as total_invite_points,
       0     as version,
       NOW() as createTime,
       NOW() as updateTime,
       0     as isDelete
FROM `user`
WHERE isDelete = 0
  AND NOT EXISTS (SELECT 1
                  FROM user_account
                  WHERE user_account.user_id = `user`.id);
