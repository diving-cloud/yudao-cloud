package cn.iocoder.yudao.module.leaf.service.redpacket;

import cn.iocoder.yudao.module.leaf.request.RedPacketCreateRequest;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacket;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketRecord;
import cn.iocoder.yudao.module.leaf.dto.redpacket.RedPacketTokenDTO;

import java.math.BigDecimal;
import java.util.List;


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