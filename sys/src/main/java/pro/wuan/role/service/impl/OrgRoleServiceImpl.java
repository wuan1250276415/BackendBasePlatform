package pro.wuan.role.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.constant.HttpStatus;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.utils.IDUtil;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.element.service.IOrgElementService;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.userapi.entity.*;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.vo.UnitTreeVO;
import pro.wuan.mapper.OrgAppMapper;
import pro.wuan.organ.mapper.OrgOrganMapper;
import pro.wuan.organ.service.IOrgOrganService;
import pro.wuan.privilege.service.IOrgRolePrivilegeService;
import pro.wuan.privilege.vo.RolePrivilegeSaveVO;
import pro.wuan.role.dto.BindingRolePrivilegesDto;
import pro.wuan.role.dto.NewExcelRoleDto;
import pro.wuan.role.dto.OrgRoleDto;
import pro.wuan.role.dto.UnitAndRoleDto;
import pro.wuan.role.mapper.OrgRoleMapper;
import pro.wuan.role.service.IOrgRoleService;
import pro.wuan.role.vo.RoleSortVO;
import pro.wuan.role.vo.RoleUserSaveVO;
import pro.wuan.service.IOrgAppService;
import pro.wuan.user.dto.excelImport.ExcelImportResultDto;
import pro.wuan.user.dto.excelImport.ExcelRoleDto;
import pro.wuan.user.mapper.OrgUserRoleMapper;
import pro.wuan.user.service.IOrgUserRoleService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021-08-30 11:15:00
 */
@Slf4j
@Validated
@Service("orgRoleService")
public class OrgRoleServiceImpl extends BaseServiceImpl<OrgRoleMapper, OrgRoleModel> implements IOrgRoleService {

    @Resource
    private OrgOrganMapper organMapper;
    @Resource
    private OrgAppMapper appMapper;
    @Resource
    private OrgUserRoleMapper userRoleMapper;

    @Resource
    private IOrgUserRoleService userRoleService;
    @Resource
    private IOrgOrganService organService;
    @Resource
    private IOrgElementService elementService;
    @Resource
    private IOrgAppService appService;
    @Resource
    private IOrgRolePrivilegeService rolePrivilegeService;
    @Resource
    private IOrgOrganService iOrgOrganService;



    /**
     * 获取当前用户可维护的角色列表
     *
     * @param appId 应用id
     * @return 角色实体列表
     */
    @Override
    public List<RoleSortVO> getManageRoleList(@NotNull(message = "appId不能为空") Long appId) {
        List<OrgRoleModel> roleList = this.getRoleListByCurrentUser(appId);
        List<RoleSortVO> roleSortVOList = new ArrayList<>();
        if (CollectionUtil.isEmpty(roleList)) {
            return roleSortVOList;
        }
        //构建roleMap，key单位id，value角色list
        Map<Long, List<OrgRoleModel>> roleMap = new HashMap<>();
        for (OrgRoleModel role : roleList) {
            if (roleMap.containsKey(role.getOrganId())) {
                roleMap.get(role.getOrganId()).add(role);
            } else {
                List<OrgRoleModel> list = new ArrayList<>();
                list.add(role);
                roleMap.put(role.getOrganId(), list);
            }
        }
        for (Map.Entry<Long, List<OrgRoleModel>> entry : roleMap.entrySet()) {
            RoleSortVO roleSortVO = null;
            if (entry.getKey() != 0) {
                OrgOrganModel organModel = organMapper.selectById(entry.getKey());
                if (organModel != null) {
                    roleSortVO = new RoleSortVO();
                    roleSortVO.setId(organModel.getId());
                    roleSortVO.setName(organModel.getName());
                }
            } else {
                roleSortVO = new RoleSortVO();
                roleSortVO.setId(CommonConstant.TREE_ROOT_ID);
                roleSortVO.setName(CommonConstant.TREE_ROOT_NAME);
            }
            if (roleSortVO != null) {
                roleSortVO.setRoleList(entry.getValue());
                roleSortVOList.add(roleSortVO);
            }
        }
        //使返回的角色列表具有树形结构的排序
        Map<Long, List<RoleSortVO>> mapRoleTypes= roleSortVOList.stream().filter(en->ObjectUtil.isNotEmpty(en.getId())).collect(Collectors.groupingBy(RoleSortVO::getId));
        List<RoleSortVO> resRoleSortVOList = new ArrayList<>();
        //获取当前用户的所有单位树形结构顺序的单位集合
        List<UnitTreeVO> unitTreeOrderList = this.getOrderUnitListResult();
        //如果当前用户获取的所有单位树形结构顺序的单位集合为空则返回原来的逻辑
        if(CollectionUtil.isEmpty(unitTreeOrderList)){
            return roleSortVOList;
        }
        unitTreeOrderList.stream().forEach(en->{
            List<RoleSortVO> mapGet = mapRoleTypes.get(en.getId());
            if(CollectionUtil.isNotEmpty(mapGet) && mapGet.size()>0){
                resRoleSortVOList.add(mapGet.get(0));
            }
        });
        return resRoleSortVOList;
    }
    /**
     * 获取当前用户的所有单位树形结构顺序的单位集合
     *
     */
    public List<UnitTreeVO> getOrderUnitListResult() {
        List<UnitTreeVO> unitTreeVOS = iOrgOrganService.getUnitTree(null, false);
        //定义返回的集合
        List<UnitTreeVO> result = Lists.newArrayList();
        this.getOrderUnitList(unitTreeVOS,result);
        return result;
    }

    /**
     * 递归获取所有的有序单位
     *
     */
    public List<UnitTreeVO> getOrderUnitList(List<UnitTreeVO> unitTreeVOS,List<UnitTreeVO> result) {
        //遍历获取有序的单位集合
        if(CollectionUtil.isNotEmpty(unitTreeVOS)){
            unitTreeVOS.stream().forEach(en->{
                result.add(en);
                if(CollectionUtil.isNotEmpty(en.getChildren()) && en.getChildren().size()>0){
                    this.getOrderUnitList(en.getChildren(),result);
                }
            });

        }

        return result;
    }



    private List<OrgRoleModel> getRoleListByCurrentUser(Long appId) {
        OrgUserModel user = UserContext.getCurrentUser().getUser();
        if(CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
            return this.getRoleListByAppId(appId);
        }
        List<OrgRoleModel> roleList = new ArrayList<>();
        // 用户的角色
        List<OrgRoleModel> userRoleList = this.getRoleListByUserAndApp(user.getId(), appId);
        if (CollectionUtil.isNotEmpty(userRoleList)) {
            // 过滤单位管理员角色
            userRoleList = userRoleList.stream().filter(role -> !CommonConstant.ROLE_ID_UNIT_ADMIN.equals(role.getId())).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(userRoleList)) {
            roleList.addAll(userRoleList);
        }
        // 创建的角色
        List<OrgRoleModel> createRoleList = this.getRoleListByCreateUserAndApp(user.getId(), appId);
        if (CollectionUtil.isNotEmpty(createRoleList)) {
            roleList.addAll(createRoleList);
        }
        // 单位的角色
        if(CommonConstant.USER_TYPE.UNIT_ADMIN.getValue().equals(user.getType())) { // 单位管理员
            List<OrgRoleModel> unitRoleList = new ArrayList<>();
            OrgOrganModel unit = organService.getDirectParentUnit(user.getOrganId());
            if (unit != null) {
                unitRoleList = this.getRoleListByUnitId(unit.getId(), appId);
            }
            if (CollectionUtil.isNotEmpty(unitRoleList)) {
                roleList.addAll(unitRoleList);
            }
        }
        // 删除重复的角色
        roleList = roleList.stream().distinct().collect(Collectors.toList());
        // 对角色列表进行排序
        if (CollectionUtil.isNotEmpty(roleList)) {
            Collections.sort(roleList, (x, y) -> {
                if (x.getSort() != null && y.getSort() != null) {
                    return (x.getSort() < y.getSort()) ? -1 : ((x.getSort() == y.getSort()) ? 0 : 1);
                } else if (x.getSort() != null) {
                    return -1;
                } else if (y.getSort() != null) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
        return roleList;
    }

    /**
     * 批量导入角色
     *
     * @param roleDtoList
     * @param appId
     * @return
     */
    public ExcelImportResultDto batchImportRole(List<ExcelRoleDto> roleDtoList, Long appId) {
        if (CollectionUtil.isEmpty(roleDtoList)) {
            return new ExcelImportResultDto(false, "角色导入失败。未获取到角色信息。", null);
        }
        int importCount = 0;
        StringBuffer errorMassage = new StringBuffer();
        Map<String, Long> organNameAndId = new HashMap<>();
        for (ExcelRoleDto dto : roleDtoList) {
            String roleName = dto.getName();
            try {
                Assert.isTrue(!this.isExistName(null, roleName), "角色名称[" + roleName + "]已经存在");
                OrgRoleModel role = new OrgRoleModel();
                role.setName(roleName);
                role.setType(CommonConstant.ROLE_TYPE.BUSINESS.getValue());
                role.setOrganId(this.getOrganIdByName(dto.getUnitName(), organNameAndId));
                role.setRemark(dto.getRemark());
                role.setAppId(appId);
                Result<OrgRoleModel> result = this.saveRole(role);
                if (HttpStatus.OK.value() != result.getCode()) {
                    errorMassage.append("[").append(roleName).append("]导入失败");
                } else {
                    importCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + roleName + "]导入失败，出错信息：" + e.getMessage() + "。");
                errorMassage.append("[").append(roleName).append("]导入失败，出错信息：").append(e.getMessage()).append("。");
            }
        }
        if (importCount > 0) {
            if (importCount < roleDtoList.size()) {
                return new ExcelImportResultDto(true, "角色导入成功。" + errorMassage.toString(), null);
            } else {
                return new ExcelImportResultDto(true, "角色导入成功。", null);
            }
        } else {
            return new ExcelImportResultDto(false, "角色导入失败。" + errorMassage.toString(), null);
        }
    }

    /**
     * 根据组织机构名称获取id
     *
     * @param name
     * @param organNameAndId
     * @return
     */
    private Long getOrganIdByName(String name, Map<String, Long> organNameAndId) {
        Assert.isTrue(StrUtil.isNotEmpty(name), "所属单位名称不能为空");
        Long organId = organNameAndId.get(name);
        if (organId == null) {
            OrgOrganModel organ = organService.getOrganByName(name);
            Assert.notNull(organ, "所属单位名称[" + name + "]对应的单位信息不存在");
            organNameAndId.put(name, organ.getId());
            organId = organ.getId();
        }
        return organId;
    }

    /**
     * 保存角色
     *
     * @param roleModel 角色实体
     * @return 角色实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgRoleModel> saveRole(OrgRoleModel roleModel) {
        OrgOrganModel unit = organService.getOrganById(roleModel.getOrganId());
        if (unit == null) {
            return Result.failure("所属单位不存在");
        }
        if (StrUtil.isNotEmpty(roleModel.getCode())) {
            Assert.isTrue(!this.isExistCode(roleModel.getId(), roleModel.getCode()), "编码已经存在，请重新输入");
        }
        roleModel.setType(CommonConstant.ROLE_TYPE.BUSINESS.getValue());
        if (this.insert(roleModel) <= 0) {
            return Result.failure(roleModel);
        }
        // 保存元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(roleModel.getId());
        elementModel.setName(roleModel.getName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.ROLE.getValue());
        elementModel.setSort(roleModel.getSort());
        elementModel.setParentId(roleModel.getOrganId());
        elementModel.setTreeIds(unit.getTreeIds() + CommonConstant.SEPARATOR_COMMA + roleModel.getId());
        elementModel.setStatus(CommonConstant.USE_STATUS.USE.getValue());
        elementService.insert(elementModel);
        return Result.success(roleModel);
    }

    /**
     * 更新角色
     *
     * @param roleModel 角色实体
     * @return 角色实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgRoleModel> updateRole(OrgRoleModel roleModel) {
        this.checkUnitAdminRole(roleModel.getId());
        OrgRoleModel role = this.selectById(roleModel.getId());
        if (role == null) {
            return Result.failure("角色信息不存在");
        }
        if (StrUtil.isNotEmpty(roleModel.getCode())) {
            Assert.isTrue(!this.isExistCode(roleModel.getId(), roleModel.getCode()), "编码已经存在，请重新输入");
        }
        OrgOrganModel unit = organService.getOrganById(roleModel.getOrganId());
        if (unit == null) {
            return Result.failure("所属单位不存在");
        }
        // 更新元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(roleModel.getId());
        elementModel.setName(roleModel.getName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.ROLE.getValue());
        elementModel.setSort(roleModel.getSort());
        elementModel.setParentId(roleModel.getOrganId());
        elementModel.setTreeIds(unit.getTreeIds() + CommonConstant.SEPARATOR_COMMA + roleModel.getId());
        elementService.updateById(elementModel);

        if (this.updateById(roleModel) == 1) {
            return Result.success(roleModel);
        } else {
            return Result.failure(roleModel);
        }
    }

    /**
     * 删除角色
     *
     * @param id 角色id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteRole(@NotNull(message = "id不能为空") Long id) {
        Assert.isTrue(!CommonConstant.ROLE_ID_UNIT_ADMIN.equals(id), "禁止删除单位管理员角色");
        OrgRoleModel role = this.selectById(id);
        if (role == null) {
            return Result.failure("角色信息不存在");
        }
        // 校验用户角色关联
        LambdaQueryWrapper<OrgUserRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserRoleModel::getRoleId, id);
        Long userRoleCount = userRoleMapper.selectCount(wrapper);
        if (userRoleCount > 0) {
            return Result.failure("角色存在关联的用户");
        }
        // 删除角色权限关联
        LambdaQueryWrapper<OrgRolePrivilegeModel> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(OrgRolePrivilegeModel::getRoleId, id);
        rolePrivilegeService.delete(rpWrapper);
        // 删除元素
        elementService.deleteElement(id);

        if (this.deleteById(id) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    private void checkUnitAdminRole(Long roleId) {
        OrgUserModel user = UserContext.getCurrentUser().getUser();
        if (!CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 不是超级管理员
            Assert.isTrue(!CommonConstant.ROLE_ID_UNIT_ADMIN.equals(roleId), "无权操作单位管理员角色");
        }
    }

    /**
     * 保存用户角色关联信息
     *
     * @param roleUserSaveVO 角色用户关联保存VO
     * @return 保存成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveUserRoleList(RoleUserSaveVO roleUserSaveVO) {
        Long roleId = roleUserSaveVO.getRoleId();
        List<Long> userIds = roleUserSaveVO.getUserIds();
        return userRoleService.batchSaveRoleUser(roleId, userIds);
    }

    /**
     * 删除用户角色关联信息
     *
     * @param roleId 角色id
     * @param userId 用户id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteUserRole(@NotNull(message = "roleId不能为空") Long roleId, @NotNull(message = "userId不能为空") Long userId) {
        LambdaQueryWrapper<OrgUserRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserRoleModel::getRoleId, roleId);
        wrapper.eq(OrgUserRoleModel::getUserId, userId);
        if (userRoleMapper.delete(wrapper) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 根据用户id，应用id获取关联的角色列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, Long appId) {
        return dao.getRoleListByUserId(userId, appId);
    }

    /**
     * 根据创建用户id，应用id获取关联的角色列表
     *
     * @param createUserId 创建用户id
     * @param appId 应用id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByCreateUserAndApp(@NotNull(message = "createUserId不能为空") Long createUserId, Long appId) {
        LambdaQueryWrapper<OrgRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgRoleModel::getCreateUserId, createUserId);
        if (appId != null) {
            wrapper.eq(OrgRoleModel::getAppId, appId);
        }
        wrapper.orderByAsc(OrgRoleModel::getSort);
        return dao.selectList(wrapper);
    }

    /**
     * 根据单位id获取关联的角色列表
     *
     * @param unitId 单位id
     * @param appId 应用id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByUnitId(@NotNull(message = "unitId不能为空") Long unitId, Long appId) {
        LambdaQueryWrapper<OrgRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgRoleModel::getOrganId, unitId);
        if (appId != null) {
            wrapper.eq(OrgRoleModel::getAppId, appId);
        }
        wrapper.orderByAsc(OrgRoleModel::getSort);
        return this.selectList(wrapper);
    }

    /**
     * 根据应用id获取关联的角色列表
     *
     * @param appId 应用id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByAppId(@NotNull(message = "appId不能为空") Long appId) {
        LambdaQueryWrapper<OrgRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgRoleModel::getAppId, appId);
        wrapper.orderByAsc(OrgRoleModel::getSort);
        return this.selectList(wrapper);
    }

    /**
     * 根据应用编码获取关联的角色列表
     *
     * @param appCode 应用编码
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByAppCode(@NotBlank(message = "appCode不能为空") String appCode) {
        LambdaQueryWrapper<OrgAppModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgAppModel::getCode, appCode);
        List<OrgAppModel> appList = appMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        return this.getRoleListByAppId(appList.get(0).getId());
    }

    /**
     * 根据角色名称获取角色信息
     *
     * @param name 角色名称
     * @return 角色信息
     */
    public OrgRoleModel getRoleByName(String name) {
        LambdaQueryWrapper<OrgRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgRoleModel::getName, name);
        wrapper.orderByDesc(OrgRoleModel::getCreateTime);
        wrapper.last("limit 1");
        return this.selectOne(wrapper);
    }

    /**
     * 编码是否存在
     *
     * @param id id
     * @param code 编码
     * @return true/false
     */
    public boolean isExistCode(Long id, String code) {
        LambdaQueryWrapper<OrgRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgRoleModel::getCode, code);
        if (id != null) {
            wrapper.ne(OrgRoleModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 名称是否存在
     *
     * @param id id
     * @param name 名称
     * @return true/false
     */
    private boolean isExistName(Long id, String name) {
        LambdaQueryWrapper<OrgRoleModel> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(name)) {
            wrapper.eq(OrgRoleModel::getName, name);
        } else {
            return false;
        }
        if (id != null) {
            wrapper.ne(OrgRoleModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 根据角色名称获取角色信息
     *
     * @param unitId 单位id
     * @param name 角色名称
     * @return 角色信息
     */
    public OrgRoleModel getRoleByUnitIdAndName(Long unitId,String name){
        LambdaQueryWrapper<OrgRoleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgRoleModel::getOrganId,unitId);
        wrapper.eq(OrgRoleModel::getName, name);
        wrapper.orderByDesc(OrgRoleModel::getCreateTime);
        wrapper.last("limit 1");
        return this.selectOne(wrapper);
    }

    /**
     * （新）批量导入角色
     * @param: [file, appId]
     * @return: pro.wuan.core.model.Result
     */
    @Override
    public Result importRole(MultipartFile file, Long appId) {
        Assert.notNull(appId, "应用id不能为空");
        OrgAppModel app = appService.getAppById(appId);
        Assert.notNull(app, "应用不存在");
        // 检查前台数据合法性
        if (null == file || file.isEmpty()) {
            return Result.failure("不能上传空文件!");
        }
        ImportParams importParams = new ImportParams();
        importParams.setTitleRows(0);
        importParams.setHeadRows(1);
        importParams.setNeedVerify(true);
        importParams.setSheetNum(1);
        //读取角色信息
        List<NewExcelRoleDto> roleDtoList = Lists.newArrayList();
        try {
            // 读取角色信息
            importParams.setStartSheetIndex(0);
            roleDtoList = ExcelImportUtil.importExcel(file.getInputStream(), NewExcelRoleDto.class, importParams);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("导入失败，读取文件出错：" + e.getMessage());
        }
        StringBuffer errorMassage = new StringBuffer();
        ExcelImportResultDto roleResult = this.newBatchImportRole(roleDtoList, appId);
        errorMassage.append(roleResult.getMessage());
//        List<Long> roleIdList = (List<Long>) roleResult.getResult();
        if (roleResult.isSuccess()) {
            return Result.success(HttpStatus.OK.value(), errorMassage.toString(), null);
        } else {
            return Result.failure(errorMassage.toString());
        }
    }

    /**
     * 批量导入角色
     *
     * @param roleDtoList
     * @param appId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportResultDto newBatchImportRole(List<NewExcelRoleDto> roleDtoList, Long appId) {
        if (CollectionUtil.isEmpty(roleDtoList)) {
            return new ExcelImportResultDto(false, "角色导入失败。未获取到角色信息。", null);
        }
        //获取单位数据集合
        List<OrgOrganModel> unitList = organService.selectList(null);
        //获取角色数据集合
        List<OrgRoleModel> roleList = this.selectList(null);
        StringBuffer errorMassage = new StringBuffer();
        AtomicInteger atomicInteger = new AtomicInteger(1);
        List<OrgRoleModel> batchRoleList = Lists.newArrayList();
        for (NewExcelRoleDto dto : roleDtoList) {
            atomicInteger.incrementAndGet();
            int sort = atomicInteger.get();
            String msg = "第"+ sort +"行，";
            if(StringUtils.isBlank(dto.getName())){
                errorMassage.append("[" + msg + "角色名为空]导入失败");
                continue;
            }
            if(StringUtils.isBlank(dto.getUnitName())){
                errorMassage.append("[" + msg + "所属单位为空]导入失败");
                continue;
            }
            //去除空格
            String roleName = StrUtil.cleanBlank(dto.getName());
            String unitName = StrUtil.cleanBlank(dto.getUnitName());
            Long organId = null;
            //绑定单位
            for (OrgOrganModel model : unitList) {
                if(unitName.equals(model.getName())){
                    organId = model.getId();
                    break;
                }
            }
            //所属单位是否存在
            if(organId == null){
                errorMassage.append("[" + msg + "所属单位：" + unitName + "未找到]导入失败");
                continue;
            }
            boolean flag = false;
            //单位下是否存在该角色
            for (OrgRoleModel model : roleList) {
                if(model.getOrganId().equals(organId) && model.getName().equals(roleName)){
                    flag = true;
                    break;
                }
            }
            if(flag){
                errorMassage.append("[" + msg + "角色名：" + roleName + "已存在]导入失败");
                continue;
            }
            OrgRoleModel role = new OrgRoleModel();
            role.setName(roleName);
            role.setType(CommonConstant.ROLE_TYPE.BUSINESS.getValue());
            role.setOrganId(organId);
            role.setSort(sort);
            role.setRemark(dto.getRemark());
            role.setAppId(appId);
            //插入需批量保存数据
            batchRoleList.add(role);
        }
        //批量插入数据
        int batchInsertNum = this.batchInsertNoCascade(batchRoleList);
        if (batchInsertNum > 0) {
            if (batchInsertNum < roleDtoList.size()) {
                return new ExcelImportResultDto(true, "角色导入成功，成功：" + batchInsertNum + "条。" + errorMassage.toString(), null);
            } else {
                return new ExcelImportResultDto(true, "角色导入成功，成功：" + batchInsertNum + "条。", null);
            }
        } else {
            return new ExcelImportResultDto(false, "角色导入失败。" + errorMassage.toString(), null);
        }
    }

    /**
     * 绑定多单位角色权限（菜单、按钮、列表、数据权限）
     *
     * @param dto 角色实体
     * @return 角色实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgRoleModel> bindingUnitsRolePrivileges(BindingRolePrivilegesDto dto) {
        //获取页面上的单位ID串，转换为单位ID集合
        String[] organArr = dto.getOrganIds().split(",");
        List<String> organIdList = Arrays.asList(organArr);
        //获取角色名称，去除空格
        String roleName = StrUtil.cleanBlank(dto.getName());
        //获取所有角色信息
        List<OrgRoleModel> allRoleList = this.selectList(null);
        //获取所有单位信息
        List<OrgOrganModel> allOrganList = organService.selectList(null);
        //1、保存单位下的角色信息
        List<OrgRoleModel> orgRoleList = Lists.newArrayList();
        List<OrgElementModel> orgElementList = Lists.newArrayList();

        try {
            //查找现有角色单位下是否有相同角色
            for (String organIdStr : organIdList) {
                boolean isExist = false;
                Long organId = Long.parseLong(organIdStr);
                //查找现有角色中单位下是否有相同角色
                for (OrgRoleModel roleModel : allRoleList) {
                    if(roleModel.getOrganId().equals(organId) && roleModel.getName().equals(roleName)){
                        if(roleModel.getAppId().equals(dto.getAppId())){
                            isExist = true;
                            break;
                        }
                    }
                }
                //判断该单位下是否存在相同角色
                if(!isExist){
                    //获取单位下的组织结构数ID串
                    String treeIds = "";
                    for (OrgOrganModel organModel : allOrganList) {
                        if(organModel.getId().equals(organId)){
                            treeIds = organModel.getTreeIds();
                            break;
                        }
                    }

                    //插入批量生成角色信息
                    OrgRoleModel roleModel = new OrgRoleModel();
                    roleModel.setId(IDUtil.nextId());
                    roleModel.setName(roleName);
                    roleModel.setOrganId(organId);
                    roleModel.setType(CommonConstant.ROLE_TYPE.BUSINESS.getValue());
                    roleModel.setSort(dto.getSort());
                    roleModel.setRemark(dto.getRemark());
                    roleModel.setAppId(dto.getAppId());
                    orgRoleList.add(roleModel);

                    //插入批量生成角色元素信息
                    OrgElementModel elementModel = new OrgElementModel();
                    elementModel.setId(roleModel.getId());
                    elementModel.setName(roleModel.getName());
                    elementModel.setType(CommonConstant.ELEMENT_TYPE.ROLE.getValue());
                    elementModel.setSort(roleModel.getSort());
                    elementModel.setParentId(roleModel.getOrganId());
                    elementModel.setTreeIds(treeIds + CommonConstant.SEPARATOR_COMMA + roleModel.getId());
                    elementModel.setStatus(CommonConstant.USE_STATUS.USE.getValue());
                    orgElementList.add(elementModel);
                }
            }

            //批量保存角色信息
            if(CollectionUtil.isNotEmpty(orgRoleList)){
                int batchInsertNum = this.batchInsertNoCascade(orgRoleList);
                if(batchInsertNum <= 0){
                    return Result.failure("角色信息数据未获取，保存失败！");
                }
            }
            //批量保存角色元素信息
            if(CollectionUtil.isNotEmpty(orgElementList)){
                int batchInsertNum = elementService.batchInsertNoCascade(orgElementList);
                if(batchInsertNum <= 0){
                    return Result.failure("角色元素信息数据未获取，保存失败！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("出错信息：" + e.getMessage());
        }

        //2、获取所有单位下这个角色的信息
        List<OrgRoleModel> unitsRoleList = getUnitsRoleList(organIdList, roleName);
        //3、循环操作角色权限绑定（先删除原有权限绑定，再进行数据重新绑定）
        this.saveUnitsRolePrivilege(dto, unitsRoleList);
        return Result.success(dto);
    }

    /**
     * 获取多单位下指定角色数据
     */
    public List<OrgRoleModel> getUnitsRoleList(List<String> organIds, String roleName) {
        LambdaQueryWrapper<OrgRoleModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrgRoleModel::getName, roleName);
        queryWrapper.in(OrgRoleModel::getOrganId, organIds);
        List<OrgRoleModel> list = this.selectList(queryWrapper);
        return list;
    }

    /**
     * 多单位下指定角色绑定权限数据
     */
    public void saveUnitsRolePrivilege(BindingRolePrivilegesDto dto, List<OrgRoleModel> roleList) {
        for (OrgRoleModel roleModel : roleList) {
            RolePrivilegeSaveVO rolePrivilegeSaveVO = new RolePrivilegeSaveVO();
            BeanUtils.copyProperties(dto, rolePrivilegeSaveVO);
            rolePrivilegeSaveVO.setRoleId(roleModel.getId());
            rolePrivilegeService.saveRolePrivilege(rolePrivilegeSaveVO);
        }
    }

    /**
     * 批量多单位保存多角色   废弃
     *
     * @param dto 角色实体
     * @return 角色实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgRoleModel> saveUnitsBindingRoles(OrgRoleDto dto) {
        Long appId = dto.getAppId();

        //获取角色名称集合
        List<String> roleNameList = dto.getRoleNameList();

        //获取页面选择的单位数据
        String organIds = dto.getOrganIds();
        String[] idArr = organIds.split(",");
        List<String> organIdList = Arrays.asList(idArr);

        //组织机构、角色接值
        List<UnitAndRoleDto> dtoList = Lists.newArrayList();
        for (String organId : organIdList) {
            for (String roleName : roleNameList) {
                UnitAndRoleDto unitAndRoleDto = new UnitAndRoleDto();
                unitAndRoleDto.setOrganId(Long.parseLong(organId));
                unitAndRoleDto.setName(StrUtil.cleanBlank(roleName));
                dtoList.add(unitAndRoleDto);
            }
        }

        //获取所有角色数据
        List<OrgRoleModel> roleList = this.selectList(null);
        for (UnitAndRoleDto roleDto : dtoList) {
            for (OrgRoleModel role : roleList) {
                //该单位下有重复角色 标记  批量保存不保存
                if(roleDto.getOrganId().equals(role.getOrganId()) && roleDto.getName().equals(role.getName())){
                    roleDto.setSaveStatus("N");
                    break;
                }
            }
        }

        //获取所有单位数据
        List<OrgOrganModel> organList = organService.selectList(null);

        //筛选出需要同步的数据
        List<UnitAndRoleDto> roleDtoList = dtoList.stream().filter(p -> "N".equals(p.getSaveStatus())).collect(Collectors.toList());
        List<OrgRoleModel> roleModelList = Lists.newArrayList();
        AtomicInteger atomicInteger = new AtomicInteger();
        for (UnitAndRoleDto roleDto : roleDtoList) {
            //获取单位treeIds
            for (OrgOrganModel organModel : organList) {
                if(organModel.getId().equals(roleDto.getOrganId())){
                   roleDto.setUnitTreeIds(organModel.getTreeIds());
                    break;
                }
            }
            OrgRoleModel roleModel = new OrgRoleModel();
            BeanUtils.copyProperties(roleDto, roleModel);
            roleModel.setType(CommonConstant.ROLE_TYPE.BUSINESS.getValue());
            atomicInteger.incrementAndGet();
            roleModel.setSort(atomicInteger.get());
            roleModel.setAppId(appId);
            roleModelList.add(roleModel);
        }

        //批量保存角色数据
        if(CollectionUtil.isNotEmpty(roleModelList)){
            if (this.batchInsertNoCascade(roleModelList) <= 0) {
                return Result.failure(roleModelList);
            }
        }else{
            return Result.failure(roleModelList);
        }

        //批量同步元素
        List<OrgElementModel> orgElementModelList = Lists.newArrayList();
        for (OrgRoleModel roleModel : roleModelList) {
            if(StrUtil.isNotBlank(roleModel.getUnitTreeIds())){
                // 保存元素
                OrgElementModel elementModel = new OrgElementModel();
                elementModel.setId(roleModel.getId());
                elementModel.setName(roleModel.getName());
                elementModel.setType(CommonConstant.ELEMENT_TYPE.ROLE.getValue());
                elementModel.setSort(roleModel.getSort());
                elementModel.setParentId(roleModel.getOrganId());
                elementModel.setTreeIds(roleModel.getUnitTreeIds() + CommonConstant.SEPARATOR_COMMA + roleModel.getId());
                elementModel.setStatus(CommonConstant.USE_STATUS.USE.getValue());

                orgElementModelList.add(elementModel);
            }
        }
        if(CollectionUtil.isNotEmpty(orgElementModelList)){
            elementService.batchInsertNoCascade(orgElementModelList);
        }
        return Result.success(roleModelList);
    }

}
