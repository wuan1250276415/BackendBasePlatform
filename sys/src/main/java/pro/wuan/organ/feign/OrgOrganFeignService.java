package pro.wuan.organ.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.vo.OrganTreeVO;
import pro.wuan.feignapi.userapi.vo.UnitTreeVO;
import pro.wuan.organ.dto.GetAllUnitAndDeptsDto;
import pro.wuan.organ.dto.GetUnitDto;
import pro.wuan.organ.mapper.OrgOrganMapper;
import pro.wuan.organ.service.IOrgOrganService;

import java.util.List;

/**
 * 组织机构
 *
 * @author: liumin
 * @date: 2021-08-30 11:21:11
 */
@Slf4j
@RestController
@RequestMapping("/feign/org/organ")
public class OrgOrganFeignService extends QueryFeignService<IOrgOrganService, OrgOrganMapper, OrgOrganModel> {

    @Autowired
    private IOrgOrganService organService;

    /**
     * 根据单位id获取该单位部门及直接下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    @GetMapping("/getSubDeptAndDirectUnitListByUnitId")
    public List<OrgOrganModel> getSubDeptAndDirectUnitListByUnitId(@RequestParam("unitId") Long unitId) {
        try {
            return organService.getSubDeptAndDirectUnitListByUnitId(unitId);
        } catch (Exception e) {
            log.error("organ getSubDeptAndDirectUnitListByUnitId error:" + e);
            return null;
        }
    }

    /**
     * 根据单位id获取该单位及直接下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    @GetMapping("/getDirectUnitListByUnitId")
    public List<OrgOrganModel> getDirectUnitListByUnitId(@RequestParam("unitId") Long unitId) {
        try {
            return organService.getDirectUnitListByUnitId(unitId);
        } catch (Exception e) {
            log.error("organ getDirectUnitListByUnitId error:" + e);
            return null;
        }
    }

    /**
     * 根据单位id获取该单位以及该单位及直接下属单位列表
     *
     * @param getUnitDto
     * @return 组织机构列表
     */
    @RequestMapping("/getMyAndDirectUnitListByUnitId")
    public List<OrgOrganModel> getMyAndDirectUnitListByUnitId(@RequestBody GetUnitDto getUnitDto) {
        try {
            return organService.getMyAndDirectUnitListByUnitId(getUnitDto);
        } catch (Exception e) {
            log.error("organ getMyAndDirectUnitListByUnitId error:" + e);
            return null;
        }
    }

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @param unitId   单位id，值为空时根据当前登录用户获取单位树形结构
     * @param typeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 单位树形结构
     */
    @GetMapping("/getUnitTree")
    public List<UnitTreeVO> getUnitTree(@RequestParam(name = "unitId", required = false) Long unitId,
                                        @RequestParam(name = "typeList", required = false) List<String> typeList) {
        try {
            return organService.getUnitTree(unitId, typeList);
        } catch (Exception e) {
            log.error("organ getUnitTree error:" + e);
            return null;
        }
    }

    /**
     * 获取组织机构树形结构
     *
     * @param organId 组织机构id，值为空时根据当前登录用户获取从单位开始的组织机构树形结构
     * @return 组织机构树形结构
     */
    @GetMapping("/getOrganTree")
    public List<OrganTreeVO> getOrganTree(@RequestParam("organId") Long organId) {
        try {
            return organService.getOrganTree(organId, false);
        } catch (Exception e) {
            log.error("organ getOrganTree error:" + e);
            return null;
        }
    }

    /**
     * 根据单位编号获取机构业务类型
     *
     * @param uniteCreditCode 单位编号
     * @return 机构业务类型
     */
    @GetMapping("/getOrganBusinessType")
    public String getOrganBusinessType(@RequestParam("uniteCreditCode") String uniteCreditCode) {
        try {
            return organService.getOrganBusinessType(uniteCreditCode);
        } catch (Exception e) {
            log.error("organ getOrganBusinessType error:" + e);
            return null;
        }
    }

    /**
     * 根据单位编号获取组织机构信息
     *
     * @param uniteCreditCode 单位编号
     * @return 组织机构信息
     */
    @GetMapping("/getOrganByUnitCode")
    public OrgOrganModel getOrganByUnitCode(@RequestParam("uniteCreditCode") String uniteCreditCode) {
        try {
            return organService.getOrganByUnitCode(uniteCreditCode);
        } catch (Exception e) {
            log.error("organ getOrganByUnitCode error:" + e);
            return null;
        }
    }

    /**
     * 根据单位类型获取单位列表
     *
     * @param organType 单位类型
     * @return
     */
    @GetMapping("/getUnitListByType")
    public List<OrgOrganModel> getUnitListByType(@RequestParam("organType") String organType) {
        try {
            return organService.getUnitListByType(organType);
        } catch (Exception e) {
            log.error("organ getUnitListByType error:" + e);
            return null;
        }
    }

    /**
     * 获取所有顶级单位列表
     *
     * @return 单位列表
     */
    @GetMapping("/getTopUnitList")
    public List<OrgOrganModel> getTopUnitList() {
        try {
            return organService.getTopUnitList();
        } catch (Exception e) {
            log.error("organ getTopUnitList error:" + e);
            return null;
        }
    }

    /**
     * 获取所有单位列表
     *
     * @return 单位列表
     */
    @GetMapping("/getAllUnitList")
    public List<OrgOrganModel> getAllUnitList() {
        try {
            return organService.getAllUnitList();
        } catch (Exception e) {
            log.error("organ getAllUnitList error:" + e);
            return null;
        }
    }
    /**
     * 获取所有单位列表以及部门
     *
     * @return 单位列表
     */
    @GetMapping("/getAllUnitListAndDepts")
    public List<OrgOrganModel> getAllUnitListAndDepts(@RequestBody GetAllUnitAndDeptsDto getAllUnitAndDeptsDto) {
        try {
            return organService.getAllUnitListAndDepts(getAllUnitAndDeptsDto.getOrganTypes());
        } catch (Exception e) {
            log.error("organ getAllUnitList error:" + e);
            return null;
        }
    }

    /**
     * 根据组织机构id获取该组织机构下的组织机构列表
     *
     * @param organId 组织机构id
     * @param containSubUnit 是否包含下级单位
     * @return 组织机构列表
     */
    @GetMapping("/getSubOrganListByOrganId")
    public List<OrgOrganModel> getSubOrganListByOrganId(@RequestParam("organId") Long organId, boolean containSubUnit) {
        try {
            return organService.getSubOrganListByOrganId(organId, containSubUnit);
        } catch (Exception e) {
            log.error("organ getSubOrganListByOrganId error:" + e);
            return null;
        }
    }

    /**
     * 获取上级单位到最顶级
     *
     * @param organId
     * @return
     */
    @GetMapping("/getParentUnits")
    public List<OrgOrganModel> getParentUnits(@RequestParam("organId")Long organId){
        try {
            return organService.getParentUnits(organId);
        } catch (Exception e) {
            log.error("organ getParentUnits error:" + e);
            return null;
        }
    }
    /**
     * 根据单位id获取所有单位
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/getUnitsByUnitIds")
    public List<OrgOrganModel> getUnitsByUnitIds(List<Long> unitIds){
        try {
            return organService.getUnitsByUnitIds(unitIds);
        } catch (Exception e) {
            log.error("organ getParentUnits error:" + e);
            return null;
        }
    }

}
