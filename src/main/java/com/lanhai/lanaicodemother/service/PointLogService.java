package com.lanhai.lanaicodemother.service;

import com.lanhai.lanaicodemother.model.dto.point.PointLogQueryRequest;
import com.lanhai.lanaicodemother.model.entity.PointLog;
import com.lanhai.lanaicodemother.model.vo.point.PointLogVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 积分流水 服务层。
 *
 * @author 积分系统
 */
public interface PointLogService extends IService<PointLog> {

    /**
     * 记录积分流水
     *
     * @param userId       用户ID
     * @param businessType 业务类型
     * @param businessId   业务ID
     * @param pointType    积分类型
     * @param pointChange  积分变动数
     * @param beforePoints 变动前积分
     * @param afterPoints  变动后积分
     * @param remark       备注
     * @return 是否成功
     */
    boolean recordLog(Long userId, String businessType, String businessId, String pointType,
                      Long pointChange, Long beforePoints, Long afterPoints, String remark);

    /**
     * 分页查询积分流水
     *
     * @param userId       用户ID（如果是null则从queryRequest中获取，用于管理员查询指定用户）
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    Page<PointLogVO> pageLogs(Long userId, PointLogQueryRequest queryRequest);

}

