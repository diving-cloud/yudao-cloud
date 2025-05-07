package cn.iocoder.yudao.module.leaf.service.payment;

import java.math.BigDecimal;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 转账给用户
     *
     * @param userId 用户ID
     * @param amount 金额
     * @return 是否成功
     */
    boolean transferMoney(Long userId, BigDecimal amount);
}