package cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket;

import cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketStatusEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 红包 DO
 *
 * @author Your Name
 */
@TableName("red_packet") // 假设表名为 red_packet
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketDO extends BaseDO {

    /**
     * 红包ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 创建者ID
     */
    private Long creatorId;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 总数量
     */
    private Integer totalCount;
    /**
     * 剩余数量
     */
    private Integer remainingCount;
    /**
     * 涨粉目标
     */
    private Integer followersTarget;
    /**
     * 当前涨粉数
     */
    private Integer currentFollowers;
    /**
     * 状态
     * <p>
     * 枚举 {@link RedPacketStatusEnum} // TODO 定义枚举
     */
    private Integer status;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;

}