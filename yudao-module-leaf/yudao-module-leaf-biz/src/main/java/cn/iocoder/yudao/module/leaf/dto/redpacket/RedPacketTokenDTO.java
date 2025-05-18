package cn.iocoder.yudao.module.leaf.dto.redpacket;

import lombok.Data;
import lombok.experimental.Accessors; // For chainable setters
import java.math.BigDecimal;

@Data
public class RedPacketTokenDTO {
    private Long redPacketId;
    private Long userId;
    private Long timestamp;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Boolean canGrab;
    private String sign;
}