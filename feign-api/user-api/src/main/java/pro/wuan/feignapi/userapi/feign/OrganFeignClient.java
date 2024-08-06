package pro.wuan.feignapi.userapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.fallback.OrganFeignClientFallback;
import pro.wuan.feignapi.userapi.vo.GetAllUnitAndDeptsDto;
import pro.wuan.feignapi.userapi.vo.GetUnitDto;
import pro.wuan.feignapi.userapi.vo.OrganTreeVO;
import pro.wuan.feignapi.userapi.vo.UnitTreeVO;

import java.util.List;

/**
 * @author: liumin
 * @date: 2021/11/19 10:05
 */
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = OrganFeignClientFallback.class,
        path = "/feign/org/organ"
)
public interface OrganFeignClient extends IBaseFeignQuerySerivce<OrgOrganModel> {

    /**
     * 根据单位id获取该单位部门及直接下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    @GetMapping(value = "/getSubDeptAndDirectUnitListByUnitId",produces = { "application/json;charset=UTF-8"})
    List<OrgOrganModel> getSubDeptAndDirectUnitListByUnitId(@RequestParam("unitId") Long unitId);

    /**
     * 根据单位id获取该单位及直接下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    @GetMapping(value = "/getDirectUnitListByUnitId",produces = { "application/json;charset=UTF-8"})
    List<OrgOrganModel> getDirectUnitListByUnitId(@RequestParam("unitId") Long unitId);
    /**
     * 根据单位id获取该单位以及该单位及直接下属单位列表
     *
     * @param getUnitDto
     * @return 组织机构列表
     */
    @RequestMapping(value = "/getMyAndDirectUnitListByUnitId",produces = { "application/json;charset=UTF-8"})
    List<OrgOrganModel> getMyAndDirectUnitListByUnitId(@RequestBody GetUnitDto getUnitDto);

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @param unitId   单位id，值为空时根据当前登录用户获取单位树形结构
     * @param typeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 单位树形结构
     */
    @GetMapping(value = "/getUnitTree",produces = { "application/json;charset=UTF-8"})
    List<UnitTreeVO> getUnitTree(@RequestParam(name = "unitId", required = false) Long unitId,
                                 @RequestParam(name = "typeList", required = false) List<String> typeList);

    /**
     * 获取组织机构树形结构
     *
     * @param organId 组织机构id，值为空时根据当前登录用户获取从单位开始的组织机构树形结构
     * @return 组织机构树形结构
     */
    @GetMapping("/getOrganTree")
    List<OrganTreeVO> getOrganTree(@RequestParam("organId") Long organId);

    /**
     * 根据单位编号获取机构业务类型
     *
     * @param uniteCreditCode 单位编号
     * @return 机构业务类型
     */
    @GetMapping("/getOrganBusinessType")
    String getOrganBusinessType(@RequestParam("uniteCreditCode") String uniteCreditCode);

    /**
     * 根据单位编号获取组织机构信息
     *
     * @param uniteCreditCode 单位编号
     * @return 组织机构信息
     */
    @GetMapping("/getOrganByUnitCode")
    OrgOrganModel getOrganByUnitCode(@RequestParam("uniteCreditCode") String uniteCreditCode);

    /**
     * 获取所有顶级单位列表
     *
     * @return 单位列表
     */
    @GetMapping("/getTopUnitList")
    List<OrgOrganModel> getTopUnitList();

    /**
     * 获取所有单位列表
     *
     * @return 单位列表
     */
    @GetMapping("/getAllUnitList")
    List<OrgOrganModel> getAllUnitList();

    /**
     * 获取所有单位列表以及部门
     *
     * @return 单位列表
     */
    @RequestMapping(value = "/getAllUnitListAndDepts",produces = { "application/json;charset=UTF-8"})
    List<OrgOrganModel> getAllUnitListAndDepts(@RequestBody GetAllUnitAndDeptsDto getAllUnitAndDeptsDto);

    /**
     * 根据组织机构id获取该组织机构下的组织机构列表
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位
     * @return 组织机构列表
     */
    @GetMapping("/getSubOrganListByOrganId")
    List<OrgOrganModel> getSubOrganListByOrganId(@RequestParam("organId") Long organId, boolean containSubUnit);

    /**
     * 获取上级单位到最顶级
     *
     * @param organId
     * @return
     */
    @GetMapping("/getParentUnits")
    List<OrgOrganModel> getParentUnits(@RequestParam("organId") Long organId);

}
