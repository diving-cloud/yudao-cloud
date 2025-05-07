# Java高并发抢红包系统 - 技术笔记

## 目录

1. [项目概述](#1-项目概述)
2. [系统架构](#2-系统架构)
3. [核心模块实现](#3-核心模块实现)
4. [高频面试问题与解答](#4-高频面试问题与解答)
5. [项目优化方案](#5-项目优化方案)
6. [面试要点总结](#6-面试要点总结)

## 1. 项目概述

### 背景介绍

联合红包项目是一个高并发系统，允许主播在运营后台创建红包，设置一定的金额和涨粉目标。红包以挂件形式出现在直播间，观众点击并关注主播后可参与抢红包活动。

### 业务价值

- **主播视角**：增加粉丝量，提升直播间活跃度
- **用户视角**：获得奖励，提升参与感
- **平台视角**：提高整体付费率，促进用户留存

### 核心流程

1. **红包创建**：运营后台创建红包，设定金额和涨粉目标
2. **红包拆分**：系统将大红包拆分为多个小红包分片
3. **红包投放**：将红包推送至目标直播间
4. **用户抢红包**：用户请求token后参与抢红包
5. **结算发放**：异步结算并发放红包金额

## 2. 系统架构

### 技术栈选型

- **开发框架**：Spring Boot + Spring Cloud
- **数据存储**：MySQL + Redis
- **消息队列**：RocketMQ
- **缓存**：Redis + 本地缓存
- **监控**：Prometheus + Grafana

### 系统架构图

```
┌──────────────────┐     ┌─────────────────┐     ┌───────────────────┐
│  运营管理后台     │     │  红包创建服务    │     │   MySQL集群       │
│                  │────▶│                 │────▶│                   │
└──────────────────┘     └─────────────────┘     └───────────────────┘
                                 │                         ▲
                                 │                         │
                                 ▼                         │
┌──────────────────┐     ┌─────────────────┐     ┌───────────────────┐
│  直播间客户端     │     │  Token服务      │     │   Redis集群       │
│                  │◀───▶│                 │◀───▶│                   │
└──────────────────┘     └─────────────────┘     └───────────────────┘
         │                        │                         ▲
         │                        │                         │
         ▼                        ▼                         │
┌──────────────────┐     ┌─────────────────┐     ┌───────────────────┐
│  用户抢红包      │     │  抢红包服务      │────▶│   RocketMQ        │
│                  │────▶│                 │     │                   │
└──────────────────┘     └─────────────────┘     └───────────────────┘
                                                          │
                                                          │
                                                          ▼
                                              ┌───────────────────┐
                                              │  结算服务         │
                                              │                   │
                                              └───────────────────┘
```

### 关键技术点

- **分布式架构**：微服务化设计，高可用部署
- **高并发处理**：Redis分片与预生成token机制控制并发
- **异步处理**：RocketMQ实现异步结算
- **数据分片**：红包分片存储，避免热点问题
- **缓存设计**：多级缓存减轻DB压力

## 3. 核心模块实现

### 3.1 核心实体类设计

```java
// 红包主体
@Data
@Entity
@Table(name = "red_packet")
public class RedPacket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long creatorId;             // 创建者ID
    private BigDecimal totalAmount;     // 总金额
    private Integer totalCount;         // 总数量
    private Integer remainingCount;     // 剩余数量
    private Integer followersTarget;    // 涨粉目标
    private Integer currentFollowers;   // 当前涨粉数
    private Integer status;             // 状态 0-未开始 1-进行中 2-已结束
    private LocalDateTime startTime;    // 开始时间
    private LocalDateTime endTime;      // 结束时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

// 红包分片
@Data
@Entity
@Table(name = "red_packet_shard")
public class RedPacketShard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long redPacketId;           // 红包ID
    private Integer shardId;            // 分片ID
    private BigDecimal amount;          // 金额
    private Integer status;             // 状态 0-未领取 1-已领取
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

// 红包领取记录
@Data
@Entity
@Table(name = "red_packet_record")
public class RedPacketRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long redPacketId;           // 红包ID
    private Long userId;                // 用户ID
    private Integer shardId;            // 分片ID
    private BigDecimal amount;          // 金额
    private Integer status;             // 状态 0-未结算 1-已结算 2-结算失败
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 3.2 红包服务接口

```java
public interface RedPacketService {
    // 创建红包
    RedPacket createRedPacket(RedPacketCreateRequest request);
    
    // 生成Token
    RedPacketTokenDTO generateToken(Long redPacketId, Long userId);
    
    // 抢红包
    BigDecimal grabRedPacket(RedPacketTokenDTO tokenDTO);
    
    // 获取红包领取记录
    List<RedPacketRecord> getRedPacketRecords(Long redPacketId);
    
    // 更新粉丝数
    void updateFollowers(Long redPacketId, Integer newFollowers);
}
```

### 3.3 红包创建实现

```java
@Service
@Slf4j
public class RedPacketServiceImpl implements RedPacketService {

    @Autowired
    private RedPacketRepository redPacketRepository;
    
    @Autowired
    private RedPacketShardRepository redPacketShardRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public RedPacket createRedPacket(RedPacketCreateRequest request) {
        // 创建红包主体
        RedPacket redPacket = new RedPacket();
        redPacket.setCreatorId(request.getCreatorId());
        redPacket.setTotalAmount(request.getTotalAmount());
        redPacket.setTotalCount(request.getTotalCount());
        redPacket.setRemainingCount(request.getTotalCount());
        redPacket.setFollowersTarget(request.getFollowersTarget());
        redPacket.setCurrentFollowers(0);
        redPacket.setStatus(RedPacketStatus.NOT_STARTED);
        redPacket.setStartTime(request.getStartTime());
        redPacket.setEndTime(request.getEndTime());
        redPacket.setCreateTime(LocalDateTime.now());
        redPacket.setUpdateTime(LocalDateTime.now());
        
        RedPacket savedRedPacket = redPacketRepository.save(redPacket);
        
        // 保存红包分片
        for (RedPacketShardDTO shardDTO : request.getShards()) {
            RedPacketShard shard = new RedPacketShard();
            shard.setRedPacketId(savedRedPacket.getId());
            shard.setShardId(shardDTO.getShardId());
            shard.setAmount(shardDTO.getAmount());
            shard.setStatus(RedPacketShardStatus.NOT_GRABBED);
            shard.setCreateTime(LocalDateTime.now());
            shard.setUpdateTime(LocalDateTime.now());
            redPacketShardRepository.save(shard);
            
            // 将红包分片数据存入Redis
            String redisKey = "red_packet:" + savedRedPacket.getId() + ":shard:" + shard.getShardId();
            redisTemplate.opsForValue().set(redisKey, shard.getAmount().toString(), 24, TimeUnit.HOURS);
        }
        
        return savedRedPacket;
    }
}
```

### 3.4 Token生成实现

```java
@Override
public RedPacketTokenDTO generateToken(Long redPacketId, Long userId) {
    // 查询红包信息
    RedPacket redPacket = redPacketRepository.findById(redPacketId)
        .orElseThrow(() -> new RuntimeException("红包不存在"));
        
    // 检查红包状态
    if (redPacket.getStatus() != RedPacketStatus.IN_PROGRESS) {
        throw new RuntimeException("红包不在进行中状态");
    }
    
    // 检查用户是否已经抢过红包
    Optional<RedPacketRecord> existingRecord = redPacketRecordRepository.findByRedPacketIdAndUserId(redPacketId, userId);
    if (existingRecord.isPresent()) {
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
```

### 3.5 抢红包实现

```java
@Override
@Transactional
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
        RedPacket redPacket = redPacketRepository.findById(tokenDTO.getRedPacketId())
            .orElseThrow(() -> new RuntimeException("红包不存在"));
        
        // 检查红包状态
        if (redPacket.getStatus() != RedPacketStatus.IN_PROGRESS) {
            throw new RuntimeException("红包不在进行中状态");
        }
        
        // 检查红包是否已抢光
        if (redPacket.getRemainingCount() <= 0) {
            throw new RuntimeException("红包已抢光");
        }
        
        // 检查用户是否已经抢过红包
        Optional<RedPacketRecord> existingRecord = redPacketRecordRepository.findByRedPacketIdAndUserId(
                tokenDTO.getRedPacketId(), tokenDTO.getUserId());
        if (existingRecord.isPresent()) {
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
            redPacket.setStatus(RedPacketStatus.FINISHED);
        }
        redPacketRepository.save(redPacket);
        
        // 创建抢红包记录
        RedPacketRecord record = new RedPacketRecord();
        record.setRedPacketId(tokenDTO.getRedPacketId());
        record.setUserId(tokenDTO.getUserId());
        record.setShardId(shardId);
        record.setAmount(amount);
        record.setStatus(RedPacketRecordStatus.PENDING);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        redPacketRecordRepository.save(record);
        
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
```

### 3.6 结算消费者实现

```java
@Service
@RocketMQMessageListener(topic = "red-packet-settlement", consumerGroup = "red-packet-consumer")
@Slf4j
public class RedPacketSettlementConsumer implements RocketMQListener<RedPacketRecord> {

    @Autowired
    private RedPacketRecordRepository redPacketRecordRepository;
    
    @Autowired
    private PaymentService paymentService;

    @Override
    @Transactional
    public void onMessage(RedPacketRecord record) {
        try {
            // 查询最新的记录状态，防止重复结算
            RedPacketRecord latestRecord = redPacketRecordRepository.findById(record.getId())
                .orElseThrow(() -> new RuntimeException("记录不存在"));
            
            if (latestRecord.getStatus() != RedPacketRecordStatus.PENDING) {
                log.info("红包记录 {} 状态为 {}，跳过结算", latestRecord.getId(), latestRecord.getStatus());
                return;
            }
            
            // 执行支付逻辑
            boolean success = paymentService.transferMoney(record.getUserId(), record.getAmount());
            
            // 更新结算状态
            latestRecord.setStatus(success ? RedPacketRecordStatus.SETTLED : RedPacketRecordStatus.FAILED);
            latestRecord.setUpdateTime(LocalDateTime.now());
            redPacketRecordRepository.save(latestRecord);
            
            log.info("红包记录 {} 结算完成，状态：{}", latestRecord.getId(), latestRecord.getStatus());
        } catch (Exception e) {
            log.error("红包结算失败: " + e.getMessage(), e);
        }
    }
}
```

### 3.7 Controller 实现

```java
@RestController
@RequestMapping("/api/red-packet")
public class RedPacketController {

    @Autowired
    private RedPacketService redPacketService;
    
    @PostMapping
    public ResponseEntity<RedPacket> createRedPacket(@RequestBody RedPacketCreateRequest request) {
        RedPacket createdRedPacket = redPacketService.createRedPacket(request);
        return ResponseEntity.ok(createdRedPacket);
    }
    
    @GetMapping("/{redPacketId}/token")
    public ResponseEntity<RedPacketTokenDTO> getToken(
            @PathVariable Long redPacketId,
            @RequestParam Long userId) {
        RedPacketTokenDTO tokenDTO = redPacketService.generateToken(redPacketId, userId);
        return ResponseEntity.ok(tokenDTO);
    }
    
    @PostMapping("/grab")
    public ResponseEntity<GrabRedPacketResponse> grabRedPacket(@RequestBody RedPacketTokenDTO tokenDTO) {
        BigDecimal amount = redPacketService.grabRedPacket(tokenDTO);
        
        GrabRedPacketResponse response = new GrabRedPacketResponse();
        response.setRedPacketId(tokenDTO.getRedPacketId());
        response.setUserId(tokenDTO.getUserId());
        response.setAmount(amount);
        response.setGrabTime(LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{redPacketId}/records")
    public ResponseEntity<List<RedPacketRecord>> getRedPacketRecords(@PathVariable Long redPacketId) {
        List<RedPacketRecord> records = redPacketService.getRedPacketRecords(redPacketId);
        return ResponseEntity.ok(records);
    }
    
    @PutMapping("/{redPacketId}/followers")
    public ResponseEntity<Void> updateFollowers(
            @PathVariable Long redPacketId,
            @RequestParam Integer followers) {
        redPacketService.updateFollowers(redPacketId, followers);
        return ResponseEntity.ok().build();
    }
}
```

