package cn.iocoder.yudao.module.leaf.dal.mysql.redpacket;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketShard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 红包分片 Mapper 接口
 */
@Mapper
public interface RedPacketShardMapper extends BaseMapperX<RedPacketShard> {

    /**
     * 批量插入红包分片
     *
     * @param shards 红包分片列表
     * @return 插入行数
     */
    int insertBatch(@Param("shards") List<RedPacketShard> shards);

    /**
     * 根据红包ID和分片ID查询分片
     *
     * @param redPacketId 红包ID
     * @param shardId 分片ID
     * @return 红包分片
     */
    RedPacketShard selectByPacketIdAndShardId(@Param("redPacketId") Long redPacketId, 
                                             @Param("shardId") Integer shardId);

    /**
     * 抢夺红包分片
     *
     * @param redPacketId 红包ID
     * @param shardId 分片ID
     * @param userId 用户ID
     * @param grabTime 抢夺时间
     * @return 更新行数
     */
    int grabShard(@Param("redPacketId") Long redPacketId, 
                 @Param("shardId") Integer shardId, 
                 @Param("userId") Long userId, 
                 @Param("grabTime") LocalDateTime grabTime);
}