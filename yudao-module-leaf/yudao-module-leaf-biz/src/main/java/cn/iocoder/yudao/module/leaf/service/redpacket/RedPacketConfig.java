package cn.iocoder.yudao.module.leaf.service.redpacket;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redpacket.strategy")
public class RedPacketConfig {

    /**
     * 策略类型：FIXED / RANDOM
     */
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
