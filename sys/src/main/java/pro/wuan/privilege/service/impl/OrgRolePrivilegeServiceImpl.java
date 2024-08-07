package pro.wuan.privilege.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.*;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.privilege.mapper.OrgBusinessResourceMapper;
import pro.wuan.privilege.mapper.OrgRolePrivilegeMapper;
import pro.wuan.privilege.service.IOrgBusinessResourceService;
import pro.wuan.privilege.service.IOrgDataSchemeService;
import pro.wuan.privilege.service.IOrgListColumnService;
import pro.wuan.privilege.service.IOrgRolePrivilegeService;
import pro.wuan.privilege.vo.ResourceTreeVO;
import pro.wuan.privilege.vo.RolePrivilegeQueryVO;
import pro.wuan.privilege.vo.RolePrivilegeResultVO;
import pro.wuan.privilege.vo.RolePrivilegeSaveVO;
import pro.wuan.role.mapper.OrgRoleMapper;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色权限关联表
 *
 * @author: liumin
 * @create 2021-08-30 11:15:00
 */
@Validated
@Service("orgRolePrivilegeService")
public class OrgRolePrivilegeServiceImpl extends BaseServiceImpl<OrgRolePrivilegeMapper, OrgRolePrivilegeModel> implements IOrgRolePrivilegeService {

//    @Resource
//    private IOrgRoleService roleService;
    @Resource
    private OrgRoleMapper roleMapper;

    @Resource
    private IOrgBusinessResourceService businessResourceService;

    @Resource
    private OrgBusinessResourceMapper businessResourceMapper;

    @Resource
    private IOrgListColumnService listColumnService;

    @Resource
    private IOrgDataSchemeService dataSchemeService;

    /**
     * 获取角色授权时的菜单树形结构
     *
     * @param roleId 角色id
     * @return 角色权限返回结果VO
     */
    public Result<RolePrivilegeResultVO> getMenuTreeByRole(@NotNull(message = "roleId不能为空") Long roleId) {
        OrgUserModel user = this.getCurrentUser();
        OrgRoleModel role = this.getRole(roleId);
        // 获取当前用户可授权的菜单树形结构
        List<ResourceTreeVO> menuTreeVOList = this.getMenuTreeByCurrentUser(role.getAppId(), user);
        // 获取当前角色已授权的菜单权限id列表
        List<Long> privilegeIdList = this.getPrivilegeIdListByRoleAndType(roleId, CommonConstant.PRIVILEGE_TYPE.MENU.getValue());
        // 获取选中权限id列表
        List<Long> selectedIdList = this.getSelectedIdList(menuTreeVOList, privilegeIdList);
        // 组装数据
        RolePrivilegeResultVO resultVO = new RolePrivilegeResultVO();
        resultVO.setTreeVoList(menuTreeVOList);
        resultVO.setSelectedIdList(selectedIdList);
        return Result.success(resultVO);
    }

    /**
     * 获取当前用户可授权的菜单树形结构
     *
     * @param appId 应用id
     * @param user 用户
     * @return 菜单树形结构
     */
    private List<ResourceTreeVO> getMenuTreeByCurrentUser(Long appId, OrgUserModel user) {
        // 查询当前用户可授权的菜单/目录资源列表
        List<OrgBusinessResourceModel> menuList = null;
        List<String> typeList = Arrays.asList(CommonConstant.BUSINESS_RESOURCE_TYPE.CATALOG.getValue(), CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue());
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
            menuList = this.getBusinessResourceList(typeList, appId);
        } else {
            menuList = businessResourceService.getMenuListByUserAndApp(user.getId(), appId);
        }
        return this.getMenuTreeByMenuList(menuList);
    }

    /**
     * 根据菜单资源列表生成菜单树形结构
     *
     * @param menuList 菜单资源列表
     * @return 菜单树形结构
     */
    private List<ResourceTreeVO> getMenuTreeByMenuList(List<OrgBusinessResourceModel> menuList) {
        if (CollectionUtil.isEmpty(menuList)) {
            return new ArrayList<>();
        }
        // 组装树形下级结点map
        Map<Long, List<ResourceTreeVO>> menuMap = new HashMap<>();
        Long parentId = null;
        for (OrgBusinessResourceModel menu : menuList) {
            ResourceTreeVO treeVO = new ResourceTreeVO();
            treeVO.setId(menu.getId());
            treeVO.setName(menu.getName());
            treeVO.setParentId(menu.getParentId());
            treeVO.setType(CommonConstant.PRIVILEGE_TYPE.MENU.getValue());
            parentId = menu.getParentId();
            if (menuMap.containsKey(parentId)) {
                menuMap.get(parentId).add(treeVO);
            } else {
                List<ResourceTreeVO> list = new ArrayList<>();
                list.add(treeVO);
                menuMap.put(parentId, list);
            }
        }
        // 获取树形结构顶级结点
        List<ResourceTreeVO> topTreeVOList = menuMap.get(CommonConstant.TREE_ROOT_ID);
        if (CollectionUtil.isEmpty(topTreeVOList)) {
            return new ArrayList<>();
        }
        // 递归生成菜单树形结构下级结点
        for (ResourceTreeVO topTreeVO : topTreeVOList) {
            this.getChildMenuTree(topTreeVO, menuMap);
        }
        return topTreeVOList;
    }

    /**
     * 递归生成菜单树形结构下级结点
     *
     * @param treeVO
     * @param menuMap
     */
    private void getChildMenuTree(ResourceTreeVO treeVO, Map<Long, List<ResourceTreeVO>> menuMap) {
        List<ResourceTreeVO> children = menuMap.get(treeVO.getId());
        treeVO.setChildren(children);
        if (CollectionUtil.isNotEmpty(children)) {
            for (ResourceTreeVO child : children) {
                this.getChildMenuTree(child, menuMap);
            }
        }
    }

    /**
     * 查询业务资源列表
     *
     * @param typeList
     * @param appId
     * @return
     */
    private List<OrgBusinessResourceModel> getBusinessResourceList(List<String> typeList, Long appId) {
        LambdaQueryWrapper<OrgBusinessResourceModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgBusinessResourceModel::getAppId, appId);
        wrapper.in(OrgBusinessResourceModel::getType, typeList);
        wrapper.orderByAsc(OrgBusinessResourceModel::getSort);
        return businessResourceMapper.selectList(wrapper);
    }

    /**
     * 获取选中权限id列表
     *
     * @param resourceTreeVOList
     * @param privilegeIdList
     * @return
     */
    private List<Long> getSelectedIdList(List<ResourceTreeVO> resourceTreeVOList, List<Long> privilegeIdList) {
        // 获取权限树形中的叶子权限id集合
        Set<Long> leafResourceIdSet = new HashSet<>();
        if (CollectionUtil.isNotEmpty(resourceTreeVOList)) {
            this.getLeafResourceIdSet(resourceTreeVOList, leafResourceIdSet);
        }
        List<Long> selectedIdList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(privilegeIdList)) {
            selectedIdList = privilegeIdList.stream().filter(privilegeId -> leafResourceIdSet.contains(privilegeId)).collect(Collectors.toList());
        }
        return selectedIdList;
    }

    /**
     * 获取资源树形中的叶子资源id集合
     *
     * @param resourceTreeVOList
     * @param leafResourceIdSet
     */
    private void getLeafResourceIdSet(List<ResourceTreeVO> resourceTreeVOList, Set<Long> leafResourceIdSet) {
        resourceTreeVOList.stream().forEach(resourceTreeVO -> {
            if (CollectionUtil.isEmpty(resourceTreeVO.getChildren())) {
                leafResourceIdSet.add(resourceTreeVO.getId());
            } else {
                this.getLeafResourceIdSet(resourceTreeVO.getChildren(), leafResourceIdSet);
            }
        });
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    private OrgUserModel getCurrentUser() {
        OrgUserModel user = UserContext.getCurrentUser().getUser();
        Assert.notNull(user, "当前用户未登录或登录信息已失效，请重新登录");
        return user;
    }

    /**
     * 获取角色
     *
     * @param roleId
     * @return
     */
    private OrgRoleModel getRole(Long roleId) {
        OrgRoleModel role = roleMapper.selectById(roleId);
        Assert.notNull(role, "角色不存在");
        return role;
    }

    /**
     * 获取角色授权时的按钮树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    public Result<RolePrivilegeResultVO> getButtonTreeByRole(RolePrivilegeQueryVO queryVO) {
        OrgUserModel user = this.getCurrentUser();
        OrgRoleModel role = this.getRole(queryVO.getRoleId());
        // 获取当前用户可授权的按钮树形结构
        List<ResourceTreeVO> buttonTreeVOList = this.getButtonTreeByCurrentUser(role.getAppId(), user, queryVO.getMenuIdList());
        // 获取当前角色已授权的按钮权限id列表
        List<Long> privilegeIdList = this.getPrivilegeIdListByRoleAndType(queryVO.getRoleId(), CommonConstant.PRIVILEGE_TYPE.FUNCTION.getValue());
        // 获取选中权限id列表
        List<Long> selectedIdList = this.getSelectedIdList(buttonTreeVOList, privilegeIdList);
        // 组装数据
        RolePrivilegeResultVO resultVO = new RolePrivilegeResultVO();
        resultVO.setTreeVoList(buttonTreeVOList);
        resultVO.setSelectedIdList(selectedIdList);
        return Result.success(resultVO);
    }

    /**
     * 获取当前用户可授权的按钮树形结构
     *
     * @param appId 应用id
     * @param user 用户
     * @param menuIdList 可授权菜单id列表
     * @return 按钮树形结构
     */
    private List<ResourceTreeVO> getButtonTreeByCurrentUser(Long appId, OrgUserModel user, List<Long> menuIdList) {
        if (CollectionUtil.isEmpty(menuIdList)) {
            return new ArrayList<>();
        }
        // 获取当前用户可授权的菜单树形结构
        List<ResourceTreeVO> menuTreeVOList = this.getMenuTreeByMenuIdList(menuIdList, appId);
        if (CollectionUtil.isEmpty(menuTreeVOList)) {
            return new ArrayList<>();
        }
        // 查询当前用户可授权的功能资源列表
        List<OrgBusinessResourceModel> functionList = null;
        List<String> typeList = Arrays.asList(CommonConstant.BUSINESS_RESOURCE_TYPE.FUNCTION.getValue());
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
            functionList = this.getBusinessResourceList(typeList, appId);
        } else {
            functionList = businessResourceService.getFunctionListByUserAndApp(user.getId(), appId);
        }
        if (CollectionUtil.isEmpty(functionList)) {
            return menuTreeVOList;
        }
        // 组装树形下级结点map
        Map<Long, List<ResourceTreeVO>> functionMap = new HashMap<>();
        Long parentId = null;
        for (OrgBusinessResourceModel function : functionList) {
            ResourceTreeVO treeVO = new ResourceTreeVO();
            treeVO.setId(function.getId());
            treeVO.setName(function.getName());
            treeVO.setParentId(function.getParentId());
            treeVO.setType(CommonConstant.PRIVILEGE_TYPE.FUNCTION.getValue());
            parentId = function.getParentId();
            if (functionMap.containsKey(parentId)) {
                functionMap.get(parentId).add(treeVO);
            } else {
                List<ResourceTreeVO> list = new ArrayList<>();
                list.add(treeVO);
                functionMap.put(parentId, list);
            }
        }
        // 递归生成功能树形结构下级结点
        for (ResourceTreeVO menuTreeVO : menuTreeVOList) {
            this.getChildFunctionTree(menuTreeVO, functionMap);
        }
        return menuTreeVOList;
    }

    /**
     * 获取菜单树形结构
     *
     * @param menuIdList 菜单id列表
     * @param appId 应用id
     * @return 菜单树形结构
     */
    private List<ResourceTreeVO> getMenuTreeByMenuIdList(List<Long> menuIdList, Long appId) {
        // 查询菜单/目录资源列表
        LambdaQueryWrapper<OrgBusinessResourceModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrgBusinessResourceModel::getId, menuIdList);
        wrapper.eq(OrgBusinessResourceModel::getAppId, appId);
        wrapper.orderByAsc(OrgBusinessResourceModel::getSort);
        List<OrgBusinessResourceModel> menuList =  businessResourceMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(menuList)) {
            return new ArrayList<>();
        }
        return this.getMenuTreeByMenuList(menuList);
    }

    /**
     * 递归生成功能树形结构下级结点
     *
     * @param treeVO
     * @param functionMap
     */
    private void getChildFunctionTree(ResourceTreeVO treeVO, Map<Long, List<ResourceTreeVO>> functionMap) {
        List<ResourceTreeVO> childFunction = functionMap.get(treeVO.getId());
        if (CollectionUtil.isNotEmpty(childFunction)) {
            if (CollectionUtil.isNotEmpty(treeVO.getChildren())) {
                treeVO.getChildren().addAll(childFunction);
            } else {
                treeVO.setChildren(childFunction);
            }
        }
        List<ResourceTreeVO> children = treeVO.getChildren();
        if (CollectionUtil.isNotEmpty(children)) {
            for (ResourceTreeVO child : children) {
                this.getChildFunctionTree(child, functionMap);
            }
        }
    }

    /**
     * 获取角色授权时的列表树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    public Result<RolePrivilegeResultVO> getListTreeByRole(RolePrivilegeQueryVO queryVO) {
        OrgUserModel user = this.getCurrentUser();
        OrgRoleModel role = this.getRole(queryVO.getRoleId());
        // 获取当前用户可授权的列表树形结构
        List<ResourceTreeVO> listTreeVOList = this.getListTreeByCurrentUser(role.getAppId(), user, queryVO.getMenuIdList());
        // 获取当前角色已授权的列表权限id列表
        List<Long> privilegeIdList = this.getPrivilegeIdListByRoleAndType(queryVO.getRoleId(), CommonConstant.PRIVILEGE_TYPE.LIST.getValue());
        // 组装数据
        RolePrivilegeResultVO resultVO = new RolePrivilegeResultVO();
        resultVO.setTreeVoList(listTreeVOList);
        resultVO.setSelectedIdList(privilegeIdList);
        return Result.success(resultVO);
    }

    /**
     * 获取当前用户可授权的列表树形结构
     *
     * @param appId 应用id
     * @return 列表树形结构
     */
    private List<ResourceTreeVO> getListTreeByCurrentUser(Long appId, OrgUserModel user, List<Long> menuIdList) {
        if (CollectionUtil.isEmpty(menuIdList)) {
            return new ArrayList<>();
        }
        // 获取当前用户可授权的菜单树形结构
        List<ResourceTreeVO> menuTreeVOList = this.getMenuTreeByMenuIdList(menuIdList, appId);
        if (CollectionUtil.isEmpty(menuTreeVOList)) {
            return new ArrayList<>();
        }
        // 查询当前用户可授权的列表字段列表
        List<OrgListColumnModel> listColumnList = null;
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
            LambdaQueryWrapper<OrgListColumnModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(OrgListColumnModel::getMenuResourceId, menuIdList);
            wrapper.orderByAsc(OrgListColumnModel::getMenuResourceId);
            listColumnList = listColumnService.selectList(wrapper);
        } else {
            listColumnList = listColumnService.getListColumnListByUserAndApp(user.getId(), appId);
        }
        if (CollectionUtil.isEmpty(listColumnList)) {
            return menuTreeVOList;
        }
        // 组装树形下级结点map
        Map<Long, List<ResourceTreeVO>> listColumnMap = new HashMap<>();
        Long parentId = null;
        for (OrgListColumnModel listColumn : listColumnList) {
            ResourceTreeVO treeVO = new ResourceTreeVO();
            treeVO.setId(listColumn.getId());
            treeVO.setName(listColumn.getColumnName());
            treeVO.setParentId(listColumn.getMenuResourceId());
            treeVO.setType(CommonConstant.PRIVILEGE_TYPE.LIST.getValue());
            parentId = listColumn.getMenuResourceId();
            if (listColumnMap.containsKey(parentId)) {
                listColumnMap.get(parentId).add(treeVO);
            } else {
                List<ResourceTreeVO> list = new ArrayList<>();
                list.add(treeVO);
                listColumnMap.put(parentId, list);
            }
        }
        // 递归生成列表树形结构下级结点
        for (ResourceTreeVO menuTreeVO : menuTreeVOList) {
            this.getChildResourceTree(menuTreeVO, listColumnMap);
        }
        return menuTreeVOList;
    }

    /**
     * 递归生成资源树形结构下级结点
     *
     * @param treeVO
     * @param resourceTreeMap
     */
    private void getChildResourceTree(ResourceTreeVO treeVO, Map<Long, List<ResourceTreeVO>> resourceTreeMap) {
        List<ResourceTreeVO> childResourceTreeVO = resourceTreeMap.get(treeVO.getId());
        if (CollectionUtil.isNotEmpty(childResourceTreeVO)) {
            if (CollectionUtil.isNotEmpty(treeVO.getChildren())) {
                treeVO.getChildren().addAll(childResourceTreeVO);
            } else {
                treeVO.setChildren(childResourceTreeVO);
            }
        }
        List<ResourceTreeVO> children = treeVO.getChildren();
        if (CollectionUtil.isNotEmpty(children)) {
            for (ResourceTreeVO child : children) {
                this.getChildResourceTree(child, resourceTreeMap);
            }
        }
    }

    /**
     * 获取角色授权时的数据方案树形结构
     *
     * @param queryVO 角色权限查询VO
     * @return 角色权限返回结果VO
     */
    public Result<RolePrivilegeResultVO> getDataSchemeTreeByRole(RolePrivilegeQueryVO queryVO) {
        OrgUserModel user = this.getCurrentUser();
        OrgRoleModel role = this.getRole(queryVO.getRoleId());
        // 获取当前用户可授权的数据方案树形结构
        List<ResourceTreeVO> dataSchemeTreeVOList = this.getDataSchemeTreeByCurrentUser(role.getAppId(), user, queryVO.getMenuIdList());
        // 获取当前角色已授权的数据方案权限id列表
        List<Long> privilegeIdList = this.getPrivilegeIdListByRoleAndType(queryVO.getRoleId(), CommonConstant.PRIVILEGE_TYPE.DATA.getValue());
        // 组装数据
        RolePrivilegeResultVO resultVO = new RolePrivilegeResultVO();
        resultVO.setTreeVoList(dataSchemeTreeVOList);
        resultVO.setSelectedIdList(privilegeIdList);
        return Result.success(resultVO);
    }

    /**
     * 获取当前用户可授权的数据方案树形结构
     *
     * @param appId 应用id
     * @return 数据方案树形结构
     */
    private List<ResourceTreeVO> getDataSchemeTreeByCurrentUser(Long appId, OrgUserModel user, List<Long> menuIdList) {
        if (CollectionUtil.isEmpty(menuIdList)) {
            return new ArrayList<>();
        }
        // 获取当前用户可授权的菜单树形结构
        List<ResourceTreeVO> menuTreeVOList = this.getMenuTreeByMenuIdList(menuIdList, appId);
        if (CollectionUtil.isEmpty(menuTreeVOList)) {
            return new ArrayList<>();
        }
        // 查询当前用户可授权的数据方案列表
        List<OrgDataSchemeModel> dataSchemeList = null;
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
            LambdaQueryWrapper<OrgDataSchemeModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(OrgDataSchemeModel::getMenuResourceId, menuIdList);
            wrapper.orderByAsc(OrgDataSchemeModel::getMenuResourceId);
            dataSchemeList = dataSchemeService.selectList(wrapper);
        } else {
            dataSchemeList = dataSchemeService.getDataSchemeListByUserAndApp(user.getId(), appId);
        }
        if (CollectionUtil.isEmpty(dataSchemeList)) {
            return menuTreeVOList;
        }
        // 组装树形下级结点map
        Map<Long, List<ResourceTreeVO>> dataSchemeMap = new HashMap<>();
        Long parentId = null;
        for (OrgDataSchemeModel dataScheme : dataSchemeList) {
            ResourceTreeVO treeVO = new ResourceTreeVO();
            treeVO.setId(dataScheme.getId());
            treeVO.setName(dataScheme.getName());
            treeVO.setParentId(dataScheme.getMenuResourceId());
            treeVO.setType(CommonConstant.PRIVILEGE_TYPE.DATA.getValue());
            parentId = dataScheme.getMenuResourceId();
            if (dataSchemeMap.containsKey(parentId)) {
                dataSchemeMap.get(parentId).add(treeVO);
            } else {
                List<ResourceTreeVO> list = new ArrayList<>();
                list.add(treeVO);
                dataSchemeMap.put(parentId, list);
            }
        }
        // 递归生成数据方案树形结构下级结点
        for (ResourceTreeVO menuTreeVO : menuTreeVOList) {
            this.getChildResourceTree(menuTreeVO, dataSchemeMap);
        }
        return menuTreeVOList;
    }

    /**
     * 保存角色数据权限
     *
     * @param saveVO 角色权限保存VO
     * @return 保存成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveRolePrivilege(RolePrivilegeSaveVO saveVO) {
        OrgUserModel user = this.getCurrentUser();
        if (!CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 不是超级管理员
            Assert.isTrue(!CommonConstant.ROLE_ID_UNIT_ADMIN.equals(saveVO.getRoleId()), "无权操作单位管理员角色");
        }
        this.saveRoleMenu(saveVO);
        this.saveRoleButton(saveVO);
        this.saveRoleList(saveVO);
        this.saveRoleData(saveVO);
        return Result.success("保存成功");
    }

    /**
     * 保存角色菜单权限
     *
     * @param saveVO 角色权限保存VO
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleMenu(RolePrivilegeSaveVO saveVO) {
        Long roleId = saveVO.getRoleId();
        // 批量删除角色权限关联信息
        List<String> typeList = Arrays.asList(CommonConstant.PRIVILEGE_TYPE.MENU.getValue());
        this.batchDeleteRolePrivilege(roleId, typeList);
        // 去除重复的id
        List<Long> menuIds = this.removeRepeatId(saveVO.getMenuIdList());
        if (CollectionUtil.isEmpty(menuIds)) {
            return;
        }
        // 批量新增角色权限关联信息
        List<OrgRolePrivilegeModel> rolePrivilegeList = new ArrayList<>();
        for (Long menuId : menuIds) {
            OrgRolePrivilegeModel rolePrivilege = new OrgRolePrivilegeModel();
            rolePrivilege.setRoleId(roleId);
            rolePrivilege.setPrivilegeId(menuId);
            rolePrivilege.setType(CommonConstant.PRIVILEGE_TYPE.MENU.getValue());
            rolePrivilegeList.add(rolePrivilege);
        }
        this.batchInsertNoCascade(rolePrivilegeList);
    }

    /**
     * 保存角色按钮权限
     *
     * @param saveVO 角色权限保存VO
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleButton(RolePrivilegeSaveVO saveVO) {
        Long roleId = saveVO.getRoleId();
        // 批量删除角色权限关联信息
        List<String> typeList = Arrays.asList(CommonConstant.PRIVILEGE_TYPE.FUNCTION.getValue());
        this.batchDeleteRolePrivilege(roleId, typeList);
        // 去除重复的id
        List<Long> buttonIds = this.removeRepeatId(saveVO.getFunctionIdList());
        if (CollectionUtil.isEmpty(buttonIds)) {
            return;
        }
        // 批量新增角色权限关联信息
        List<OrgRolePrivilegeModel> rolePrivilegeList = new ArrayList<>();
        for (Long buttonId : buttonIds) {
            OrgRolePrivilegeModel rolePrivilege = new OrgRolePrivilegeModel();
            rolePrivilege.setRoleId(roleId);
            rolePrivilege.setPrivilegeId(buttonId);
            rolePrivilege.setType(CommonConstant.PRIVILEGE_TYPE.FUNCTION.getValue());
            rolePrivilegeList.add(rolePrivilege);
        }
        this.batchInsertNoCascade(rolePrivilegeList);
    }

    /**
     * 保存角色列表权限
     *
     * @param saveVO 角色权限保存VO
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleList(RolePrivilegeSaveVO saveVO) {
        Long roleId = saveVO.getRoleId();
        // 批量删除角色权限关联信息
        List<String> typeList = Arrays.asList(CommonConstant.PRIVILEGE_TYPE.LIST.getValue());
        this.batchDeleteRolePrivilege(roleId, typeList);
        // 去除重复的id
        List<Long> listColumnIds = this.removeRepeatId(saveVO.getListIdList());
        if (CollectionUtil.isEmpty(listColumnIds)) {
            return;
        }
        // 批量新增角色权限关联信息
        List<OrgRolePrivilegeModel> rolePrivilegeList = new ArrayList<>();
        for (Long listColumnId : listColumnIds) {
            OrgRolePrivilegeModel rolePrivilege = new OrgRolePrivilegeModel();
            rolePrivilege.setRoleId(roleId);
            rolePrivilege.setPrivilegeId(listColumnId);
            rolePrivilege.setType(CommonConstant.PRIVILEGE_TYPE.LIST.getValue());
            rolePrivilegeList.add(rolePrivilege);
        }
        this.batchInsertNoCascade(rolePrivilegeList);
    }

    /**
     * 保存角色数据权限
     *
     * @param saveVO 角色权限保存VO
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleData(RolePrivilegeSaveVO saveVO) {
        Long roleId = saveVO.getRoleId();
        // 批量删除角色权限关联信息
        List<String> typeList = Arrays.asList(CommonConstant.PRIVILEGE_TYPE.DATA.getValue());
        this.batchDeleteRolePrivilege(roleId, typeList);
        // 去除重复的id
        List<Long> dataSchemeIds = this.removeRepeatId(saveVO.getDataIdList());
        if (CollectionUtil.isEmpty(dataSchemeIds)) {
            return;
        }
        // 批量新增角色权限关联信息
        List<OrgRolePrivilegeModel> rolePrivilegeList = new ArrayList<>();
        for (Long dataSchemeId : dataSchemeIds) {
            OrgRolePrivilegeModel rolePrivilege = new OrgRolePrivilegeModel();
            rolePrivilege.setRoleId(roleId);
            rolePrivilege.setPrivilegeId(dataSchemeId);
            rolePrivilege.setType(CommonConstant.PRIVILEGE_TYPE.DATA.getValue());
            rolePrivilegeList.add(rolePrivilege);
        }
        this.batchInsertNoCascade(rolePrivilegeList);
    }

    /**
     * 去除重复的id
     *
     * @param ids
     * @return
     */
    private List<Long> removeRepeatId(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        // 去除重复的id
        Set<Long> idSet = new HashSet<>();
        for (Long id : ids) {
            if (id != null) {
                idSet.add(id);
            }
        }
        if (CollectionUtil.isEmpty(idSet)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(idSet);
    }

    /**
     * 根据角色id、权限类型查询角色权限关联信息列表
     *
     * @param roleId
     * @param type
     * @return
     */
    public List<OrgRolePrivilegeModel> getRolePrivilegeByRoleAndType(Long roleId, String type) {
        LambdaQueryWrapper<OrgRolePrivilegeModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgRolePrivilegeModel::getRoleId, roleId);
        wrapper.eq(OrgRolePrivilegeModel::getType, type);
        return this.selectList(wrapper);
    }

    /**
     * 根据角色id、权限类型查询角色权限关联的权限id信息列表
     *
     * @param roleId
     * @param type
     * @return
     */
    public List<Long> getPrivilegeIdListByRoleAndType(Long roleId, String type) {
        List<OrgRolePrivilegeModel> rolePrivilegeList = this.getRolePrivilegeByRoleAndType(roleId, type);
        Set<Long> privilegeIdSet = new HashSet<>();
        if (CollectionUtil.isNotEmpty(rolePrivilegeList)) {
            for (OrgRolePrivilegeModel rolePrivilege : rolePrivilegeList) {
                privilegeIdSet.add(rolePrivilege.getPrivilegeId());
            }
        }
        return new ArrayList<>(privilegeIdSet);
    }

    /**
     * 批量删除角色权限关联信息
     *
     * @param roleId
     * @param typeList
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRolePrivilege(Long roleId, List<String> typeList) {
        LambdaQueryWrapper<OrgRolePrivilegeModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgRolePrivilegeModel::getRoleId, roleId);
        wrapper.in(OrgRolePrivilegeModel::getType, typeList);
        this.delete(wrapper);
    }

}
