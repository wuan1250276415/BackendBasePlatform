package pro.wuan.organmodelsetting.service;


import pro.wuan.common.core.service.IBaseService;
import pro.wuan.organmodelsetting.mapper.OrgOrganModelSettingMapper;
import pro.wuan.organmodelsetting.model.OrgOrganModelSettingModel;

import java.util.List;

/**
 * 查询模式数据配置
 *
 * @program: tellhowcloud
 * @author libra
 * @create 2024-01-22 11:48:48
 */
public interface IOrgOrganModelSettingService extends IBaseService<OrgOrganModelSettingMapper, OrgOrganModelSettingModel> {

    /**
     * 根据单位id获取该单位所属数据库模式
     *
     */
    String getOrganDabaseModel(Long organId);

    /**
     * 获取所有配置的数据库模式
     *
     */
    List<String> getAllDabaseMode();

}