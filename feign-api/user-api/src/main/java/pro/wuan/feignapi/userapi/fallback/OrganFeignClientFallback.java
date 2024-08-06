package pro.wuan.feignapi.userapi.fallback;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.feign.OrganFeignClient;
import pro.wuan.feignapi.userapi.vo.GetAllUnitAndDeptsDto;
import pro.wuan.feignapi.userapi.vo.GetUnitDto;
import pro.wuan.feignapi.userapi.vo.OrganTreeVO;
import pro.wuan.feignapi.userapi.vo.UnitTreeVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: liumin
 * @date: 2021/11/19 10:07
 */
@Component
public class OrganFeignClientFallback implements OrganFeignClient {

    @Override
    public OrgOrganModel selectById(Long id) {
        return null;
    }

    @Override
    public List<OrgOrganModel> selectBatchByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    /**
     * 根据单位id获取该单位部门及直接下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    @Override
    public List<OrgOrganModel> getSubDeptAndDirectUnitListByUnitId(@RequestParam("unitId") Long unitId) {
        return new ArrayList<>();
    }

    @Override
    public List<OrgOrganModel> getDirectUnitListByUnitId(Long unitId) {
        return new ArrayList<>();
    }

    @Override
    public List<OrgOrganModel> getMyAndDirectUnitListByUnitId(@RequestBody GetUnitDto getUnitDto) {
        return new ArrayList<>();
    }

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @param unitId   单位id，值为空时根据当前登录用户获取单位树形结构
     * @param typeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 单位树形结构
     */
    public List<UnitTreeVO> getUnitTree(@RequestParam(name = "unitId", required = false) Long unitId,
                                        @RequestParam(name = "typeList", required = false) List<String> typeList) {
        return new ArrayList<>();
    }

    /**
     * 获取组织机构树形结构
     *
     * @param organId 组织机构id，值为空时根据当前登录用户获取从单位开始的组织机构树形结构
     * @return 组织机构树形结构
     */
    public List<OrganTreeVO> getOrganTree(@RequestParam("organId") Long organId) {
        return new ArrayList<>();
    }

    /**
     * 根据单位编号获取机构业务类型
     *
     * @param uniteCreditCode 单位编号
     * @return 机构业务类型
     */
    public String getOrganBusinessType(@RequestParam("uniteCreditCode") String uniteCreditCode) {
        return new String();
    }

    /**
     * 根据单位编号获取组织机构信息
     *
     * @param uniteCreditCode 单位编号
     * @return 组织机构信息
     */
    public OrgOrganModel getOrganByUnitCode(@RequestParam("uniteCreditCode") String uniteCreditCode) {
        return null;
    }

    /**
     * 获取所有顶级单位列表
     *
     * @return 单位列表
     */
    public List<OrgOrganModel> getTopUnitList() {
        return new ArrayList<>();
    }

    /**
     * 获取所有单位列表
     *
     * @return 单位列表
     */
    public List<OrgOrganModel> getAllUnitList() {
        return new ArrayList<>();
    }

    /**
     * 获取所有单位列表以及部门
     *
     * @return 单位列表
     */
    @Override
    public List<OrgOrganModel> getAllUnitListAndDepts(GetAllUnitAndDeptsDto getAllUnitAndDeptsDto) {
        return new ArrayList<>();
    }

    /**
     * 根据组织机构id获取该组织机构下的组织机构列表
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位
     * @return 组织机构列表
     */
    public List<OrgOrganModel> getSubOrganListByOrganId(Long organId, boolean containSubUnit) {
        return new ArrayList<>();
    }

    /**
     * 获取上级单位到最顶级
     *
     * @param organId
     * @return
     */
    public List<OrgOrganModel> getParentUnits(@RequestParam("organId") Long organId) {
        return new ArrayList<>();
    }

}
