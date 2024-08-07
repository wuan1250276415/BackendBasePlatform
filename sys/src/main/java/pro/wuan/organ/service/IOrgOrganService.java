package pro.wuan.organ.service;

import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.vo.OrganTreeVO;
import pro.wuan.feignapi.userapi.vo.UnitTreeVO;
import pro.wuan.organ.dto.GetUnitDto;
import pro.wuan.organ.mapper.OrgOrganMapper;
import pro.wuan.user.dto.excelImport.ExcelImportResultDto;
import pro.wuan.user.dto.excelImport.ExcelUnitDto;

import java.util.List;

/**
 * 组织机构
 *
 * @author: liumin
 * @date: 2021-08-30 11:21:11
 */
public interface IOrgOrganService extends IBaseService<OrgOrganMapper, OrgOrganModel> {

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @param id                    单位id，值为空时根据当前登录用户获取单位树形结构
     * @param organBusinessTypeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 单位树形结构
     */
    public List<UnitTreeVO> getUnitTree(Long id, List<String> organBusinessTypeList);

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @param id       单位id，值为空时根据当前登录用户获取单位树形结构
     * @param showRoot 是否显示顶级结点
     * @return 单位树形结构
     */
    public List<UnitTreeVO> getUnitTree(Long id, boolean showRoot);

    /**
     * 获取所有单位树形结构，只显示单位结点
     *
     * @return 单位树形结构
     */
    public List<UnitTreeVO> getAllUnitTree();
    /**
     * 获取组织机构实体
     *
     * @param organId 组织机构id
     * @return 组织机构实体
     */
    public OrgOrganModel getOrganById(Long organId);

    /**
     * 获取直属上级单位
     * 根据组织机构id获取组织机构信息，判断组织机构是否为单位，是则返回组织机构信息；不是则根据其上级组织机构id继续递归查找
     *
     * @param organId 组织机构id
     * @return 直属上级单位
     */
    public OrgOrganModel getDirectParentUnit(Long organId);

    /**
     * 保存单位
     *
     * @param unit 单位实体
     * @return 单位实体
     */
    public Result<OrgOrganModel> saveUnit(OrgOrganModel unit);

    /**
     * 更新单位
     *
     * @param unit 单位实体
     * @return 单位实体
     */
    public Result<OrgOrganModel> updateUnit(OrgOrganModel unit);

    /**
     * 删除单位
     *
     * @param id 单位id
     * @return 删除成功/失败
     */
    public Result<String> deleteUnit(Long id);

    /**
     * 启用单位
     *
     * @param id 单位id
     * @return 单位实体
     */
    public Result<OrgOrganModel> useUnit(Long id);

    /**
     * 停用单位
     *
     * @param id 单位id
     * @return 单位实体
     */
    public Result<OrgOrganModel> nonuseUnit(Long id);

    /**
     * 获取组织机构树形结构
     *
     * @param id       组织机构id，值为空时根据当前登录用户获取从单位开始的组织机构树形结构
     * @param showRoot 是否显示顶级结点
     * @return 组织机构树形结构
     */
    public List<OrganTreeVO> getOrganTree(Long id, boolean showRoot);

    /**
     * 保存部门
     *
     * @param department 部门实体
     * @return 部门实体
     */
    public Result<OrgOrganModel> saveDept(OrgOrganModel department);

    /**
     * 更新部门
     *
     * @param department 部门实体
     * @return 部门实体
     */
    public Result<OrgOrganModel> updateDept(OrgOrganModel department);

    /**
     * 删除部门
     *
     * @param id 部门id
     * @return 删除成功/失败
     */
    public Result<String> deleteDept(Long id);

    /**
     * 根据单位id获取该单位部门及直接下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    public List<OrgOrganModel> getSubDeptAndDirectUnitListByUnitId(Long unitId);

    /**
     * 根据单位id获取该单位及直接下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    public List<OrgOrganModel> getDirectUnitListByUnitId(Long unitId);
    /**
     * 根据单位id获取该单位以及该单位及直接下属单位列表
     *
     * @return 组织机构列表
     */
    List<OrgOrganModel> getMyAndDirectUnitListByUnitId(GetUnitDto getUnitDto);

    /**
     * 根据单位编号获取机构业务类型
     *
     * @param uniteCreditCode 单位编号
     * @return 机构业务类型
     */
    public String getOrganBusinessType(String uniteCreditCode);

    /**
     * 根据单位编号获取组织机构信息
     *
     * @param uniteCreditCode 单位编号
     * @return 组织机构信息
     */
    public OrgOrganModel getOrganByUnitCode(String uniteCreditCode);

    /**
     * 根据名称获取组织机构信息
     *
     * @param name 名称
     * @return 组织机构信息
     */
    public OrgOrganModel getOrganByName(String name);

    /**
     * 根据单位类型获取单位列表
     *
     * @param organType 单位类型
     * @return
     */
    public List<OrgOrganModel> getUnitListByType(String organType);

    /**
     * 获取上级单位到最顶级
     *
     * @param organId
     * @return
     */
    List<OrgOrganModel> getParentUnits(Long organId);

    /**
     * 获取所有顶级单位列表
     *
     * @return 单位列表
     */
    public List<OrgOrganModel> getTopUnitList();

    /**
     * 获取所有单位列表
     *
     * @return 单位列表
     */
    public List<OrgOrganModel> getAllUnitList();
    /**
     * 获取所有单位列表以及部门
     *
     * @return 单位列表
     */
    public List<OrgOrganModel> getAllUnitListAndDepts(List<String> organTypes);

    /**
     * 根据组织机构id获取该组织机构下的组织机构列表
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位
     * @return 组织机构列表
     */
    public List<OrgOrganModel> getSubOrganListByOrganId(Long organId, boolean containSubUnit);

    /**
     * 根据单位id获取所有单位
     *
     * @param unitIds        组织机构id
     * @return 组织机构列表
     */
    List<OrgOrganModel> getUnitsByUnitIds(List<Long> unitIds);

    /**
     * 批量导入单位表格解析获取接口
     * @author: KevinYu
     * @date: 2023/1/13 9:50
     * @param: [file, appId]
     * @return: pro.wuan.core.model.Result
     */
    Result importExcelUnit(MultipartFile file, Long appId);

    /**
     * 批量导入单位具体方法
     *
     * @param unitDtoList
     * @return
     */
    public ExcelImportResultDto batchImportUnit(List<ExcelUnitDto> unitDtoList);

}
