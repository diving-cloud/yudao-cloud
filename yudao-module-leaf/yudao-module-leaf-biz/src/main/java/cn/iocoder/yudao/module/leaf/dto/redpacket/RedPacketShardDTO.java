package cn.iocoder.yudao.module.leaf.dto.redpacket;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 红包分片 DTO
 */
@Data
public class RedPacketShardDTO {
    
    /**
     * 分片ID
     */
    private Integer shardId;
    
    /**
     * 分片金额
     */
    private BigDecimal amount;
}