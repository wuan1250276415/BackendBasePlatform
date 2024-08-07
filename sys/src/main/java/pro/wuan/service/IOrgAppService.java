package pro.wuan.service;


import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.mapper.OrgAppMapper;

import java.util.List;

/**
 * 应用
 *
 * @author: liumin
 * @date: 2021-08-30 11:22:48
 */
public interface IOrgAppService extends IBaseService<OrgAppMapper, OrgAppModel> {

    /**
     * 编码是否存在
     *
     * @param id 应用id
     * @param code 编码
     * @return true/false
     */
    public boolean isExistCode(Long id, String code);

    /**
     * 根据应用id获取应用
     *
     * @param id 应用id
     * @return 应用
     */
    public OrgAppModel getAppById(Long id);

    /**
     * 根据编码获取应用
     *
     * @param code 编码
     * @return 应用
     */
    public OrgAppModel getAppByCode(String code);

    /**
     * 根据应用id列表获取应用列表
     *
     * @param idList 应用id列表
     * @return 应用列表
     */
    public List<OrgAppModel> getAppListByIdList(List<Long> idList);

    /**
     * 根据用户id获取应用列表
     *
     * @param userId
     * @return
     */
    public List<OrgAppModel> getAppListByUserId(Long userId);

}