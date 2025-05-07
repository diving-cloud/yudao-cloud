package cn.iocoder.yudao.module.leaf.enums.redpacket;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 红包分片状态枚举
 */
@Getter
@AllArgsConstructor
public enum RedPacketShardStatusEnum {

    UNRECEIVED(0, "未领取"),
    RECEIVED(1, "已领取");

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 状态描述
     */
    private final String description;

    public static RedPacketShardStatusEnum findByCode(Integer code) {
        for (RedPacketShardStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}