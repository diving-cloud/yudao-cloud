package cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 红包 DO
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedPacket extends BaseDO {
    /**
     * 红包ID
     */
    private Long id;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 红包总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 红包总数量
     */
    private Integer totalCount;
    
    /**
     * 剩余数量
     */
    private Integer remainingCount;
    
    /**
     * 剩余金额
     */
    private BigDecimal remainingAmount;
    
    /**
     * 红包状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.leaf.enums.redpacket.RedPacketStatusEnum}
     */
    private Integer status;

    /**
     * 粉丝数目标
     */
    private Long followersTarget;

    /**
     * 当前粉丝数
     */
    private Long currentFollowers;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 版本号，用于乐观锁
     */
    private Integer version;
}