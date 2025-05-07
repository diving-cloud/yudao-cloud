package cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 红包分片 DO
 *
 * @author leaf
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedPacketShard extends BaseDO {
    /**
     * 分片ID
     */
    private Long id;
    
    /**
     * 红包ID
     */
    private Long redPacketId;
    
    /**
     * 分片序号
     */
    private Integer shardId;
    
    /**
     * 分片金额
     */
    private BigDecimal amount;
    
    /**
     * 分片状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketShardStatus}
     */
    private Integer status;
    
    /**
     * 抢夺用户ID
     */
    private Long grabUserId;
    
    /**
     * 抢夺时间
     */
    private LocalDateTime grabTime;
}