package cn.iocoder.yudao.module.leaf.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import cn.iocoder.yudao.module.leaf.mq.message.RedPacketSettlementMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 红包消息生产者
 */
@Component
@Slf4j
public class RedPacketProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送红包结算消息
     *
     * @param recordId 红包记录ID
     * @param userId 用户ID
     * @param amount 金额
     * @param redPacketId 红包ID
     */
    public void sendSettlementMessage(Long recordId, Long userId, BigDecimal amount, Long redPacketId) {
        RedPacketSettlementMessage message = new RedPacketSettlementMessage()
                .setRecordId(recordId)
                .setUserId(userId)
                .setAmount(amount)
                .setRedPacketId(redPacketId);
        
        redisMQTemplate.send(message);
        log.info("[sendSettlementMessage][发送红包({})记录({})的结算消息，用户({})金额({})]", 
                redPacketId, recordId, userId, amount);
    }
}