package cn.iocoder.yudao.module.leaf.dal.mysql.redpacket;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RedPacketRecordMapper extends BaseMapperX<RedPacketRecord> {

    RedPacketRecord selectByPacketIdAndUserId(@Param("redPacketId") Long redPacketId,
                                              @Param("userId") Long userId);

    List<RedPacketRecord> selectListByPacketId(@Param("redPacketId") Long redPacketId);

    // Update settlement status
    int updateStatus(@Param("id") Long id, @Param("newStatus") Integer newStatus, @Param("expectedStatus") Integer expectedStatus);
}