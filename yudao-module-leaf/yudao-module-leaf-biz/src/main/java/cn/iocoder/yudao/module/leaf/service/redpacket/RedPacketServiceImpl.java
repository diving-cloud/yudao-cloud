package cn.iocoder.yudao.module.leaf.service.redpacket;

import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacket;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketRecord;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketShard;
import cn.iocoder.yudao.module.leaf.dal.mysql.redpacket.RedPacketMapper;
import cn.iocoder.yudao.module.leaf.dal.mysql.redpacket.RedPacketRecordMapper;
import cn.iocoder.yudao.module.leaf.dal.mysql.redpacket.RedPacketShardMapper;
import cn.iocoder.yudao.module.leaf.dto.redpacket.RedPacketShardDTO;
import cn.iocoder.yudao.module.leaf.dto.redpacket.RedPacketTokenDTO;
import cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketRecordStatusEnum;
import cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketShardStatusEnum;
import cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketStatusEnum;
import cn.iocoder.yudao.module.leaf.request.RedPacketCreateRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class RedPacketServiceImpl implements RedPacketService {

    // TODO: 临时设置
    private static final long TOKEN_EXPIRE = 10;
    private static final Long TOKEN_SECRET = 10L;
    @Autowired
    private RedPacketConfig redPacketConfig;
    @Autowired
    private RedPacketMapper redPacketMapper;

    @Autowired
    private RedPacketShardMapper redPacketShardMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedPacketRecordMapper redPacketRecordMapper;

    @Autowired
    private RedPacketStrategyFactory strategyFactory;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional
    public RedPacket createRedPacket(RedPacketCreateRequest request) {
        RedPacketStrategyFactory.StrategyType strategyType = determineStrategy(request); // 根据业务判断策略类型
        RedPacketGenerationStrategy strategy = strategyFactory.getStrategy(strategyType);
        List<RedPacketShardDTO> redPacketShardDTOS = strategy.generateShards(request.getTotalAmount(), request.getTotalCount());
        // 创建红包主体
        RedPacket redPacket = new RedPacket();
        redPacket.setCreatorId(getCreatorId());
        redPacket.setTotalAmount(request.getTotalAmount());
        redPacket.setTotalCount(request.getTotalCount());
        redPacket.setRemainingCount(request.getTotalCount());
        redPacket.setFollowersTarget(request.getFollowersTarget());
        redPacket.setCurrentFollowers(0L);
        redPacket.setStatus(RedPacketStatusEnum.NOT_STARTED.getCode());
        redPacket.setStartTime(request.getStartTime());
        redPacket.setEndTime(request.getEndTime());
        redPacket.setCreateTime(LocalDateTime.now());
        redPacket.setUpdateTime(LocalDateTime.now());
        // 插入红包主体
        redPacketMapper.insert(redPacket);

        // 插入成功后通过数据库自增ID获取完整对象
        Long newRedPacketId = redPacket.getId(); // 假设使用自增主键，插入后会设置id
        // 保存红包分片
        for (RedPacketShardDTO shardDTO : redPacketShardDTOS) {
            RedPacketShard shard = new RedPacketShard();
            shard.setRedPacketId(newRedPacketId);
            shard.setShardId(shardDTO.getShardId());
            shard.setAmount(shardDTO.getAmount());
            shard.setStatus(RedPacketShardStatusEnum.UNRECEIVED.getCode());
            shard.setCreateTime(LocalDateTime.now());
            shard.setUpdateTime(LocalDateTime.now());
            redPacketShardMapper.insert(shard);

            // 将红包分片数据存入Redis
            String redisKey = "red_packet:" + newRedPacketId + ":shard:" + shard.getShardId();
            redisTemplate.opsForValue().set(redisKey, shard.getAmount().toString(), 24, TimeUnit.HOURS);
        }
        return redPacket;
    }

    /**
     * 根据配置决定使用哪种红包策略
     */
    public RedPacketStrategyFactory.StrategyType determineStrategy(RedPacketCreateRequest request) {
        String strategyTypeStr = redPacketConfig.getType().toUpperCase();
        try {
            return RedPacketStrategyFactory.StrategyType.valueOf(strategyTypeStr);
        } catch (IllegalArgumentException e) {
            // 默认返回 FIXED 类型
            return RedPacketStrategyFactory.StrategyType.FIXED;
        }
    }

    @Override
    public RedPacketTokenDTO generateToken(Long redPacketId, Long userId) {
        // 查询红包信息
        RedPacket redPacket = redPacketMapper.selectById(redPacketId);
        if (redPacket == null) {
            throw new RuntimeException("红包不存在");
        }

        // 检查红包状态
        if (!Objects.equals(redPacket.getStatus(), RedPacketStatusEnum.IN_PROGRESS.getCode())) {
            throw new RuntimeException("红包不在进行中状态");
        }

        // 检查用户是否已经抢过红包
        RedPacketRecord redPacketRecord = redPacketRecordMapper.selectOne(new QueryWrapper<RedPacketRecord>()
                .eq("red_packet_id", redPacketId)
                .eq("user_id", userId));

        if (redPacketRecord != null) {
            throw new RuntimeException("您已经抢过该红包了");
        }

        // 生成token
        RedPacketTokenDTO tokenDTO = new RedPacketTokenDTO();
        tokenDTO.setRedPacketId(redPacketId);
        tokenDTO.setUserId(userId);
        tokenDTO.setTimestamp(System.currentTimeMillis());

        // 根据用户等级、活跃度等设置金额范围
        tokenDTO.setMinAmount(new BigDecimal("0.01"));
        tokenDTO.setMaxAmount(new BigDecimal("10.00"));

        // 根据剩余红包数量决定是否允许抢红包
        tokenDTO.setCanGrab(redPacket.getRemainingCount() > 0);

        // 生成签名
        String signStr = redPacketId + ":" + userId + ":" + tokenDTO.getTimestamp() + ":" + TOKEN_SECRET;
        tokenDTO.setSign(DigestUtils.md5DigestAsHex(signStr.getBytes()));

        // 将token存入Redis
        String redisKey = "red_packet_token:" + redPacketId + ":" + userId;
        redisTemplate.opsForValue().set(redisKey, tokenDTO, TOKEN_EXPIRE, TimeUnit.SECONDS);
        return tokenDTO;
    }

    public BigDecimal grabRedPacket(RedPacketTokenDTO tokenDTO) {
        // 验证token
        String redisKey = "red_packet_token:" + tokenDTO.getRedPacketId() + ":" + tokenDTO.getUserId();
        RedPacketTokenDTO storedToken = (RedPacketTokenDTO) redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null) {
            throw new RuntimeException("Token已过期");
        }

        // 验证签名
        String signStr = tokenDTO.getRedPacketId() + ":" + tokenDTO.getUserId() + ":" + tokenDTO.getTimestamp() + ":" + TOKEN_SECRET;
        String expectedSign = DigestUtils.md5DigestAsHex(signStr.getBytes());
        if (!expectedSign.equals(tokenDTO.getSign())) {
            throw new RuntimeException("无效的token签名");
        }

        // 检查是否可以抢红包
        if (!tokenDTO.getCanGrab()) {
            throw new RuntimeException("不可抢红包");
        }

        // 使用分布式锁防止一个用户并发抢多个红包
        String lockKey = "red_packet_lock:" + tokenDTO.getRedPacketId() + ":" + tokenDTO.getUserId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);

        if (locked == null || !locked) {
            throw new RuntimeException("操作过于频繁，请稍后重试");
        }

        try {
            // 获取红包信息
            RedPacket redPacket = redPacketMapper.selectById(tokenDTO.getRedPacketId());
            if (redPacket == null) {
                throw new RuntimeException("红包不存在");
            }

            // 检查红包状态
            if (!Objects.equals(redPacket.getStatus(), RedPacketStatusEnum.IN_PROGRESS.getCode())) {
                throw new RuntimeException("红包不在进行中状态");
            }

            // 检查红包是否已抢光
            if (redPacket.getRemainingCount() <= 0) {
                throw new RuntimeException("红包已抢光");
            }

            // 检查用户是否已经抢过红包
            RedPacketRecord existingRecord =
                    redPacketRecordMapper.selectOne(new QueryWrapper<RedPacketRecord>()
                            .eq("red_packet_id", tokenDTO.getRedPacketId())
                            .eq("user_id", tokenDTO.getUserId()));

            if (existingRecord != null) {
                throw new RuntimeException("您已经抢过该红包了");
            }

            // 获取可用的分片
            Integer shardId = getAvailableShardId(tokenDTO.getRedPacketId());
            if (shardId == null) {
                throw new RuntimeException("红包已抢光");
            }

            // 获取分片金额
            String shardKey = "red_packet:" + tokenDTO.getRedPacketId() + ":shard:" + shardId;
            String amountStr = (String) redisTemplate.opsForValue().get(shardKey);

            if (amountStr == null) {
                throw new RuntimeException("红包分片不存在");
            }

            BigDecimal amount = new BigDecimal(amountStr);

            // 删除Redis中的分片，防止重复抢
            redisTemplate.delete(shardKey);

            // 更新红包主体信息
            redPacket.setRemainingCount(redPacket.getRemainingCount() - 1);
            if (redPacket.getRemainingCount() == 0) {
                redPacket.setStatus(RedPacketStatusEnum.FINISHED.getCode());
            }
            redPacketMapper.insert(redPacket);

            // 创建抢红包记录
            RedPacketRecord record = new RedPacketRecord();
            record.setRedPacketId(tokenDTO.getRedPacketId());
            record.setUserId(tokenDTO.getUserId());
            record.setShardId(shardId);
            record.setAmount(amount);
            record.setStatus(RedPacketRecordStatusEnum.PENDING_SETTLEMENT.getCode());
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            redPacketRecordMapper.insert(record);
            // 发送MQ消息，异步处理结算
            rocketMQTemplate.convertAndSend("red-packet-settlement", record);
            return amount;
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    // 获取可用分片ID
    private Integer getAvailableShardId(Long redPacketId) {
        // 从Redis获取可用分片
        String redisKey = "red_packet:available_shards:" + redPacketId;
        Set<ZSetOperations.TypedTuple<Object>> shardWithCounts =
                redisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0, 0);

        if (shardWithCounts != null && !shardWithCounts.isEmpty()) {
            ZSetOperations.TypedTuple<Object> tuple = shardWithCounts.iterator().next();
            Integer shardId = Integer.valueOf(tuple.getValue().toString());
            Double remainingCount = tuple.getScore();
            // 如果该分片还有剩余，则分配该分片
            if (remainingCount > 0) {
                // 原子性递减该分片剩余数量
                redisTemplate.opsForZSet().incrementScore(redisKey, shardId, -1);
                return shardId;
            }
        }
        return null;
    }

    @Override
    public List<RedPacketRecord> getRedPacketRecords(Long redPacketId) {
        return List.of();
    }

    @Override
    public void updateFollowers(Long redPacketId, Integer newFollowers) {

    }

    private long getCreatorId() {
        return 1L;
    }
}
