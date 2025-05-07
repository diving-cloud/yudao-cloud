package cn.iocoder.yudao.module.leaf.mq.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 红包结算消息
 */
@Data
@Accessors(chain = true)
public class RedPacketSettlementMessage {

    /**
     * 红包记录ID
     */
    private Long recordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 红包ID
     */
    private Long redPacketId;


}