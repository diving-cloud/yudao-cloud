package cn.iocoder.yudao.module.leaf.controller.app.redpacket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 抢红包响应 VO
 */
@Data
public class GrabRedPacketResponse {
    
    /**
     * 红包ID
     */
    private Long redPacketId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 抢到的金额
     */
    private BigDecimal amount;
    
    /**
     * 抢红包时间
     */
    private LocalDateTime grabTime;
}