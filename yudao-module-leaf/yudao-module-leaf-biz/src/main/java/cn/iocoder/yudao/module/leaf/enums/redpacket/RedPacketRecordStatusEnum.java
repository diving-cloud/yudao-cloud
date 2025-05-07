package cn.iocoder.yudao.module.leaf.enums.redpacket;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 红包领取记录状态枚举
 */
@Getter
@AllArgsConstructor
public enum RedPacketRecordStatusEnum {

    PENDING_SETTLEMENT(0, "未结算"),
    SETTLED(1, "已结算"),
    SETTLEMENT_FAILED(2, "结算失败");

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 状态描述
     */
    private final String description;

    public static RedPacketRecordStatusEnum findByCode(Integer code) {
        for (RedPacketRecordStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}