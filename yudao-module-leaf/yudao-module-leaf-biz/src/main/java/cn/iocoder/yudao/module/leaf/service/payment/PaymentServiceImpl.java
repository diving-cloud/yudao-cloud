package cn.iocoder.yudao.module.leaf.service.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付服务实现类
 */
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Override
    public boolean transferMoney(Long userId, BigDecimal amount) {
        // 实际项目中，这里应该调用支付系统的接口进行转账
        // 这里简单模拟成功
        log.info("[transferMoney][向用户({})转账金额({})]", userId, amount);
        return true;
    }
}