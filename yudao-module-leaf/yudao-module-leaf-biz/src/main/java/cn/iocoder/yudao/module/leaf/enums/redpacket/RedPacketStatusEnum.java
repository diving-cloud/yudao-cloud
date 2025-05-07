package cn.iocoder.yudao.module.leaf.enums.redpacket;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 红包状态枚举
 */
@Getter
@AllArgsConstructor
public enum RedPacketStatusEnum {

    NOT_STARTED(0, "未开始"),
    IN_PROGRESS(1, "进行中"),
    FINISHED(2, "已结束");
    // TODO 可以根据实际业务需求增加更多状态，例如：已暂停、已取消等

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 状态描述
     */
    private final String description;

    public static RedPacketStatusEnum findByCode(Integer code) {
        for (RedPacketStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null; // 或者抛出异常，表示未找到对应的枚举
    }
}