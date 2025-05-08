package cn.iocoder.yudao.module.leaf.dto.redpacket;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.annotations.ConstructorArgs;

import java.math.BigDecimal;

/**
 * 红包分片 DTO
 */
@Data
@AllArgsConstructor
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