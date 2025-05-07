package cn.iocoder.yudao.module.leaf.service.redpacket;

import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketShardDO;

import java.util.List;

/**
 * 红包分片 Service 接口
 *
 * @author Your Name
 */
public interface RedPacketShardService {

    /**
     * 批量创建红包分片
     *
     * @param redPacketId 红包ID
     * @param shards      分片列表
     */
    void createRedPacketShards(Long redPacketId, List<RedPacketShardDO> shards);

    /**
     * 获取一个可用的红包分片（核心高并发操作）
     *
     * @param redPacketId 红包ID
     * @return 可用的红包分片，如果无则返回 null
     */
    RedPacketShardDO grabRedPacketShard(Long redPacketId, Long userId);

    // TODO: 添加其他必要的接口方法
    // 例如：根据红包ID查询所有分片等

}