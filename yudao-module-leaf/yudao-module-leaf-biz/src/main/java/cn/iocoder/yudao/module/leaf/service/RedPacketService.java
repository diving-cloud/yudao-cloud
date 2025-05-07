package cn.iocoder.yudao.module.leaf.service;


import cn.iocoder.yudao.module.leaf.request.RedPacketCreateRequest;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacket;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketRecord;
import cn.iocoder.yudao.module.leaf.dto.redpacket.RedPacketTokenDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 红包服务接口
 *
 * @author 芋道源码
 */
public interface RedPacketService {

    /**
     * 创建红包
     *
     * @param request 创建请求
     * @return 创建的红包实体
     */
    RedPacket createRedPacket(RedPacketCreateRequest request);

    /**
     * 生成抢红包令牌
     *
     * @param redPacketId 红包ID
     * @param userId      用户ID
     * @return 红包令牌 DTO
     */
    RedPacketTokenDTO generateToken(Long redPacketId, Long userId);

    /**
     * 抢红包
     *
     * @param tokenDTO 红包令牌 DTO
     * @return 抢到的金额
     */
    BigDecimal grabRedPacket(RedPacketTokenDTO tokenDTO);

    /**
     * 获取红包的抢夺记录
     *
     * @param redPacketId 红包ID
     * @return 抢夺记录列表
     */
    List<RedPacketRecord> getRedPacketRecords(Long redPacketId);

    /**
     * 更新红包的当前关注者数量
     *
     * @param redPacketId  红包ID
     * @param newFollowers 新的关注者数量
     */
    void updateFollowers(Long redPacketId, Integer newFollowers);

}