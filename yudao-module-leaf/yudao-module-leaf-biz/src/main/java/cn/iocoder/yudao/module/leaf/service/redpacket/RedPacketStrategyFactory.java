package cn.iocoder.yudao.module.leaf.service.redpacket;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class RedPacketStrategyFactory {

    public enum StrategyType {
        FIXED,
        RANDOM
    }

    private final Map<StrategyType, RedPacketGenerationStrategy> strategies = new EnumMap<>(StrategyType.class);

    @Autowired
    private FixedAmountStrategy fixedAmountStrategy;

    @Autowired
    private RandomAmountStrategy randomAmountStrategy;

    @PostConstruct
    public void init() {
        strategies.put(StrategyType.FIXED, fixedAmountStrategy);
        strategies.put(StrategyType.RANDOM, randomAmountStrategy);
    }

    public RedPacketGenerationStrategy getStrategy(StrategyType type) {
        return strategies.getOrDefault(type, fixedAmountStrategy);
    }
}
