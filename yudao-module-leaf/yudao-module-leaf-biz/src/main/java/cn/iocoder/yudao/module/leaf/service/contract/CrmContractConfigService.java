package cn.iocoder.yudao.module.leaf.service.contract;

import cn.iocoder.yudao.module.leaf.controller.admin.contract.vo.config.CrmContractConfigSaveReqVO;
import cn.iocoder.yudao.module.leaf.dal.dataobject.contract.CrmContractConfigDO;
import jakarta.validation.Valid;

/**
 * 合同配置 Service 接口
 *
 * @author 芋道源码
 */
public interface CrmContractConfigService {

    /**
     * 获得合同配置
     *
     * @return 合同配置
     */
    CrmContractConfigDO getContractConfig();

    /**
     * 保存合同配置
     *
     * @param saveReqVO 更新信息
     */
    void saveContractConfig(@Valid CrmContractConfigSaveReqVO saveReqVO);

}
