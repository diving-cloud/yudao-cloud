package cn.iocoder.yudao.module.leaf.service.redpacket;

import cn.iocoder.yudao.module.leaf.dto.redpacket.RedPacketShardDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 红包分片策略接口
 */
public interface RedPacketGenerationStrategy {

    /**
     * 生成红包分片
     *
     * @param totalAmount 总金额
     * @param totalCount  总数量
     * @return 分片列表
     */
    List<RedPacketShardDTO> generateShards(BigDecimal totalAmount, int totalCount);
}
