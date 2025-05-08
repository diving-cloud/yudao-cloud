package cn.iocoder.yudao.module.leaf.service.redpacket;

import cn.iocoder.yudao.module.leaf.dto.redpacket.RedPacketShardDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class RandomAmountStrategy implements RedPacketGenerationStrategy {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("0.01");

    @Override
    public List<RedPacketShardDTO> generateShards(BigDecimal totalAmount, int totalCount) {
        List<BigDecimal> amounts = new ArrayList<>();
        BigDecimal remaining = totalAmount;
        Random random = new Random();

        for (int i = 0; i < totalCount - 1; i++) {
            // 最小值为 0.01，最大为剩余金额减去最小值乘以剩余个数
            BigDecimal max = remaining.subtract(MIN_AMOUNT.multiply(BigDecimal.valueOf(totalCount - i - 1)));
            BigDecimal nextAmount = MIN_AMOUNT.add(max.subtract(MIN_AMOUNT).multiply(BigDecimal.valueOf(random.nextDouble())));
            amounts.add(nextAmount.setScale(2, BigDecimal.ROUND_DOWN));
            remaining = remaining.subtract(nextAmount);
        }

        amounts.add(remaining); // 最后一份全部放入
        Collections.shuffle(amounts); // 打乱顺序避免固定模式

        List<RedPacketShardDTO> shards = new ArrayList<>();
        int shardId = 0;
        for (BigDecimal amount : amounts) {
            shards.add(new RedPacketShardDTO(shardId++, amount));
        }
        return shards;
    }
}
