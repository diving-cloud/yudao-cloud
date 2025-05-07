package cn.iocoder.yudao.module.leaf.dal.mysql.redpacket;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 红包 Mapper 接口
 */
@Mapper
public interface RedPacketMapper extends BaseMapperX<RedPacket> {

    /**
     * 更新红包状态（仅当当前状态为进行中时）
     *
     * @param id         红包ID
     * @param newStatus  新状态
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int updateStatusIfInProgress(@Param("id") Long id,
                                 @Param("newStatus") Integer newStatus,
                                 @Param("updateTime") LocalDateTime updateTime);

    /**
     * 更新红包剩余数量和金额
     *
     * @param id      红包ID
     * @param amount  减少的金额
     * @param version 当前版本号（用于乐观锁）
     * @return 更新行数
     */
    int updateRemaining(@Param("id") Long id,
                        @Param("amount") BigDecimal amount,
                        @Param("version") Integer version);
}