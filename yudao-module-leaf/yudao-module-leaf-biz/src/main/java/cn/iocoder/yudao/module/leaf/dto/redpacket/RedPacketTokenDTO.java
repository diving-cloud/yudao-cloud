package cn.iocoder.yudao.module.leaf.dto.redpacket;

import lombok.Data;
import lombok.experimental.Accessors; // For chainable setters
import java.math.BigDecimal;

@Data
public class RedPacketTokenDTO {
    private Long redPacketId;
    private Long userId;
    private Long timestamp;
    private BigDecimal minAmount; // Example field
    private BigDecimal maxAmount; // Example field
    private Boolean canGrab;
    private String sign;
}