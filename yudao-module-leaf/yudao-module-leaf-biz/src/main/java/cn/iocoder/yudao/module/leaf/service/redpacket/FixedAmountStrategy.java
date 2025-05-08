package cn.iocoder.yudao.module.leaf.service.redpacket;

import cn.iocoder.yudao.module.leaf.dto.redpacket.RedPacketShardDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class FixedAmountStrategy implements RedPacketGenerationStrategy {

    @Override
    public List<RedPacketShardDTO> generateShards(BigDecimal totalAmount, int totalCount) {
        BigDecimal amountPerShard = totalAmount.divide(BigDecimal.valueOf(totalCount), BigDecimal.ROUND_DOWN);
        BigDecimal remainder = totalAmount.subtract(amountPerShard.multiply(BigDecimal.valueOf(totalCount)));

        List<RedPacketShardDTO> shards = new ArrayList<>();
        int shard = 0;
        for (int i = 0; i < totalCount; i++) {
            BigDecimal currentAmount = amountPerShard;
            if (i == 0 && remainder.compareTo(BigDecimal.ZERO) > 0) {
                currentAmount = currentAmount.add(remainder);
            }
            shards.add(new RedPacketShardDTO(shard++, currentAmount));
        }
        return shards;
    }
}
