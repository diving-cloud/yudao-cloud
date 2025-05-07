package cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket;

import cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketShardStatusEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

import java.math.BigDecimal;

/**
 * 红包分片 DO
 *
 * @author Your Name
 */
@TableName("red_packet_shard") // 假设表名为 red_packet_shard
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketShardDO extends BaseDO {

    /**
     * 分片ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 红包ID
     * <p>
     * 关联 {@link RedPacketDO#getId()}
     */
    private Long redPacketId;
    /**
     * 分片序号
     * 用于标识同一个红包下的不同分片，方便查询和管理
     */
    private Integer shardIndex; // 注意：文档中使用 shardId，这里改为 shardIndex 避免与主键 id 混淆
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 状态
     * <p>
     * 枚举 {@link RedPacketShardStatusEnum} // TODO 定义枚举 0-未领取 1-已领取
     */
    private Integer status;
    /**
     * 领取用户ID
     * <p>
     * 关联 AdminUserDO 的 id 字段 // TODO 确认用户表和字段
     */
    private Long userId;
    /**
     * 领取时间
     */
    private java.time.LocalDateTime receiveTime;

}