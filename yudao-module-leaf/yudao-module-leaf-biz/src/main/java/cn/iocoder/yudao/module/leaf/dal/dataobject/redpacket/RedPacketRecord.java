package cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 红包记录 DO
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedPacketRecord extends BaseDO {
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 红包ID
     */
    private Long redPacketId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 分片ID
     */
    private Integer shardId;
    
    /**
     * 抢到的金额
     */
    private BigDecimal amount;
    
    /**
     * 抢红包时间
     */
    private LocalDateTime grabTime;
    
    /**
     * 结算状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketRecordStatus}
     */
    private Integer status;
}