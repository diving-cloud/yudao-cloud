package cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket;

import cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketRecordStatusEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

import java.math.BigDecimal;

/**
 * 红包领取记录 DO
 *
 * @author leaf
 */
@TableName("red_packet_record") // 假设表名为 red_packet_record
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketRecordDO extends BaseDO {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 红包ID
     *
     * 关联 {@link RedPacketDO#getId()}
     */
    private Long redPacketId;
    /**
     * 用户ID
     *
     * 关联 AdminUserDO 的 id 字段 // TODO 确认用户表和字段
     */
    private Long userId;
    /**
     * 红包分片ID
     *
     * 关联 {@link RedPacketShardDO#getId()}
     */
    private Long shardId;
    /**
     * 领取金额
     */
    private BigDecimal amount;
    /**
     * 状态
     *
     * 枚举 {@link RedPacketRecordStatusEnum} // TODO 定义枚举 0-未结算 1-已结算 2-结算失败
     */
    private Integer status;

}