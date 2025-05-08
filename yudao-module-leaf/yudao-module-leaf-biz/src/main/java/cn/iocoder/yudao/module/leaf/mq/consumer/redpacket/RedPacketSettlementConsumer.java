package cn.iocoder.yudao.module.leaf.mq.consumer.redpacket;

import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketRecord;
import cn.iocoder.yudao.module.leaf.dal.mysql.redpacket.RedPacketRecordMapper;
import cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketRecordStatusEnum;
import cn.iocoder.yudao.module.leaf.mq.message.RedPacketSettlementMessage;
import cn.iocoder.yudao.module.leaf.service.payment.PaymentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 红包结算消费者
 */
@Service
@RocketMQMessageListener(topic = "red-packet-settlement", consumerGroup = "red-packet-consumer")
@Slf4j
public class RedPacketSettlementConsumer implements RocketMQListener<RedPacketSettlementMessage> {

    @Resource
    private RedPacketRecordMapper redPacketRecordMapper;
    
    @Resource
    private PaymentService paymentService;

    @Override
    @Transactional
    public void onMessage(RedPacketSettlementMessage message) {
        try {
            Long recordId = message.getRecordId();
            Long userId = message.getUserId();
            
            // 查询最新的记录状态，防止重复结算
            RedPacketRecord record = redPacketRecordMapper.selectById(recordId);
            if (record == null) {
                log.error("[onMessage][红包记录({})不存在]", recordId);
                return;
            }
            
            if (!RedPacketRecordStatusEnum.PENDING_SETTLEMENT.getCode().equals(record.getStatus())) {
                log.info("[onMessage][红包记录({})状态为({})，跳过结算]", 
                        recordId, record.getStatus());
                return;
            }
            
            // 执行支付逻辑
            boolean success = paymentService.transferMoney(userId, message.getAmount());
            
            // 更新结算状态
            int updated = redPacketRecordMapper.updateStatus(
                    recordId, 
                    success ? RedPacketRecordStatusEnum.SETTLED.getCode() : RedPacketRecordStatusEnum.SETTLEMENT_FAILED.getCode(),
                    RedPacketRecordStatusEnum.PENDING_SETTLEMENT.getCode());
            
            if (updated > 0) {
                log.info("[onMessage][红包记录({})结算完成，状态：{}]", 
                        recordId, success ? "成功" : "失败");
            } else {
                log.warn("[onMessage][红包记录({})状态更新失败，可能已被其他线程处理]", recordId);
            }
        } catch (Exception e) {
            log.error("[onMessage][红包结算失败: {}]", e.getMessage(), e);
        }
    }
}