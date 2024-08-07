package pro.wuan.privilege.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.convertor.db2page.DbConvert2Page;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;
import pro.wuan.feignapi.userapi.vo.SearchMenuVO;
import pro.wuan.privilege.mapper.OrgBusinessResourceMapper;
import pro.wuan.privilege.mapper.OrgListColumnMapper;
import pro.wuan.privilege.service.IOrgBusinessResourceService;
import pro.wuan.privilege.service.IOrgDataSchemeColumnService;
import pro.wuan.privilege.service.IOrgDataSchemeService;
import pro.wuan.privilege.vo.BusinessResourceListQueryParamVO;
import pro.wuan.privilege.vo.MenuTreeListQueryParamVO;
import pro.wuan.privilege.vo.ResourceTreeVO;

import javax.annotation.Resource;
import java.util.*;

/**
 * 业务资源
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Validated
@Service("orgBusinessResourceService")
public class OrgBusinessResourceServiceImpl extends BaseServiceImpl<OrgBusinessResourceMapper, OrgBusinessResourceModel> implements IOrgBusinessResourceService {

    @Resource
    private OrgListColumnMapper listColumnMapper;

    @Autowired
    private IOrgDataSchemeService dataSchemeService;

    @Autowired
    private IOrgDataSchemeColumnService dataSchemeColumnService;

    /**
     * 查询菜单树形列表
     *
     * @param queryParamVO 菜单树形列表查询参数VO
     * @return 菜单树形列表
     */
    public List<OrgBusinessResourceModel> selectMenuTreeList(MenuTreeListQueryParamVO queryParamVO) {
        // 查询所有菜单/目录资源列表
        List<String> typeList = this.getMenuResourceTypeList();
        BusinessResourceListQueryParamVO listQueryParamVO = new BusinessResourceListQueryParamVO(typeList, queryParamVO.getAppId(), queryParamVO.getName(), queryParamVO.getStatus(), null);
        List<OrgBusinessResourceModel> menuList = this.getAllBusinessResourceList(listQueryParamVO);
        if (CollectionUtil.isEmpty(menuList)) {
            return new ArrayList<>();
        }
        DbConvert2Page dbConvert2Page = new DbConvert2Page();
        dbConvert2Page.executeDbToPage4List(menuList);
        // 查询条件不为空时，返回菜单列表
        if (StrUtil.isNotEmpty(queryParamVO.getName()) || queryParamVO.getStatus() != null) {
            return menuList;
        }
        // 根据菜单列表组装菜单树形结构
        return this.getMenuTree(menuList);
    }

    /**
     * 根据应用id获取关联的菜单树形结构
     *
     * @param appId 应用id
     * @return 菜单树形结构
     */
    public List<OrgBusinessResourceModel> getMenuTreeByAppId(@NotNull(message = "appId不能为空") Long appId) {
        // 查询所有可见菜单/目录资源列表
        List<String> typeList = this.getMenuResourceTypeList();
        BusinessResourceListQueryParamVO queryParamVO = new BusinessResourceListQueryParamVO(typeList, appId, null, null, null);
        List<OrgBusinessResourceModel> menuList = this.getAllBusinessResourceList(queryParamVO);
        if (CollectionUtil.isEmpty(menuList)) {
            return new ArrayList<>();
        }
        DbConvert2Page dbConvert2Page = new DbConvert2Page();
        dbConvert2Page.executeDbToPage4List(menuList);
        // 根据菜单列表组装菜单树形结构
        return this.getMenuTree(menuList);
    }

    /**
     * 根据菜单列表组装菜单树形结构
     *
     * @param menuList 菜单列表
     * @return 菜单树形结构
     */
    public List<OrgBusinessResourceModel> getMenuTree(List<OrgBusinessResourceModel> menuList) {
        // 组装树形列表下级结点map
        Map<Long, List<OrgBusinessResourceModel>> menuTreeListMap = this.getResourceTreeListMap(menuList);
        // 获取树形列表顶级结点
        List<OrgBusinessResourceModel> topList = menuTreeListMap.get(CommonConstant.TREE_ROOT_ID);
        if (CollectionUtil.isEmpty(topList)) {
            return new ArrayList<>();
        }
        // 递归生成菜单树形列表下级结点
        for (OrgBusinessResourceModel top : topList) {
            this.getChildResourceTreeList(top, menuTreeListMap);
        }
        return topList;
    }

    /**
     * 组装树形列表下级结点map。其中key为parentId，value为对应的下级结点列表
     *
     * @param resourceList
     * @return
     */
    private Map<Long, List<OrgBusinessResourceModel>> getResourceTreeListMap(List<OrgBusinessResourceModel> resourceList) {
        Map<Long, List<OrgBusinessResourceModel>> resourceMap = new HashMap<>();
        Long parentId = null;
        for (OrgBusinessResourceModel resource : resourceList) {
            parentId = resource.getParentId();
            if (resourceMap.containsKey(parentId)) {
                resourceMap.get(parentId).add(resource);
            } else {
                List<OrgBusinessResourceModel> list = new ArrayList<>();
                list.add(resource);
                resourceMap.put(parentId, list);
            }
        }
        return resourceMap;
    }

    /**
     * 递归生成树形列表下级结点
     *
     * @param resourceModel
     * @param resourceMap
     */
    private void getChildResourceTreeList(OrgBusinessResourceModel resourceModel, Map<Long, List<OrgBusinessResourceModel>> resourceMap) {
        List<OrgBusinessResourceModel> children = resourceMap.get(resourceModel.getId());
        resourceModel.setChildren(children);
        if (CollectionUtil.isNotEmpty(children)) {
            for (OrgBusinessResourceModel child : children) {
                this.getChildResourceTreeList(child, resourceMap);
            }
        }
    }

    /**
     * 根据菜单树形结构获取菜单列表
     *
     * @param menuTree 菜单树形结构
     * @return 菜单列表
     */
    public List<OrgBusinessResourceModel> getMenuList(List<OrgBusinessResourceModel> menuTree) {
        List<OrgBusinessResourceModel> menuList = new ArrayList<>();
        this.getMenuList(menuTree, menuList);
        return menuList;
    }

    private void getMenuList(List<OrgBusinessResourceModel> menuTree, List<OrgBusinessResourceModel> menuList) {
        if (CollectionUtil.isEmpty(menuTree)) {
            return;
        }
        menuTree.stream().forEach(menu -> {
            menuList.add(menu);
            this.getMenuList(menu.getChildren(), menuList);
        });
    }

    /**
     * 获取菜单树形结构（用于菜单编辑页面选择上级菜单）
     *
     * @param appId 应用id
     * @return 菜单树形结构
     */
    public ResourceTreeVO getMenuTree(@NotNull(message = "appId不能为空") Long appId) {
        // 增加顶级结点
        ResourceTreeVO top = new ResourceTreeVO();
        top.setId(CommonConstant.TREE_ROOT_ID);
        top.setName(CommonConstant.TREE_ROOT_NAME);
        top.setType(CommonConstant.PRIVILEGE_TYPE.MENU.getValue());
        // 查询所有菜单/目录资源列表
        List<String> typeList = this.getMenuResourceTypeList();
        BusinessResourceListQueryParamVO queryParamVO = new BusinessResourceListQueryParamVO(typeList, appId, null, null, null);
        List<OrgBusinessResourceModel> menuList = this.getAllBusinessResourceList(queryParamVO);
        if (CollectionUtil.isEmpty(menuList)) {
            top.setChildren(null);
            return top;
        }
        // 组装树形下级结点map
        Map<Long, List<ResourceTreeVO>> menuMap = this.getResourceTreeVOMap(menuList, CommonConstant.PRIVILEGE_TYPE.MENU.getValue());
        // 递归生成菜单树形结构下级结点
        this.getChildResourceTree(top, menuMap);
        return top;
    }

    /**
     * 组装树形下级结点map。其中key为parentId，value为对应的下级结点列表
     *
     * @param resourceList
     * @param type
     * @return
     */
    private Map<Long, List<ResourceTreeVO>> getResourceTreeVOMap(List<OrgBusinessResourceModel> resourceList, String type) {
        Map<Long, List<ResourceTreeVO>> resourceMap = new HashMap<>();
        Long parentId = null;
        for (OrgBusinessResourceModel resource : resourceList) {
            ResourceTreeVO treeVO = new ResourceTreeVO();
            treeVO.setId(resource.getId());
            treeVO.setName(resource.getName());
            treeVO.setType(type);
            parentId = resource.getParentId();
            if (resourceMap.containsKey(parentId)) {
                resourceMap.get(parentId).add(treeVO);
            } else {
                List<ResourceTreeVO> list = new ArrayList<>();
                list.add(treeVO);
                resourceMap.put(parentId, list);
            }
        }
        return resourceMap;
    }

    /**
     * 递归生成树形结构下级结点
     *
     * @param treeVO
     * @param resourceMap
     */
    private void getChildResourceTree(ResourceTreeVO treeVO, Map<Long, List<ResourceTreeVO>> resourceMap) {
        List<ResourceTreeVO> children = resourceMap.get(treeVO.getId());
        treeVO.setChildren(children);
        if (CollectionUtil.isNotEmpty(children)) {
            for (ResourceTreeVO child : children) {
                this.getChildResourceTree(child, resourceMap);
            }
        }
    }

    /**
     * 获取菜单资源类型列表
     *
     * @return
     */
    private List<String> getMenuResourceTypeList() {
        return Arrays.asList(CommonConstant.BUSINESS_RESOURCE_TYPE.CATALOG.getValue(), CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue());
    }

    /**
     * 查询所有业务资源列表
     *
     * @param queryParamVO
     * @return
     */
    private List<OrgBusinessResourceModel> getAllBusinessResourceList(BusinessResourceListQueryParamVO queryParamVO) {
        LambdaQueryWrapper<OrgBusinessResourceModel> wrapper = new LambdaQueryWrapper<>();
        if (CollectionUtil.isNotEmpty(queryParamVO.getTypeList())) {
            wrapper.in(OrgBusinessResourceModel::getType, queryParamVO.getTypeList());
        }
        if (queryParamVO.getAppId() != null) {
            wrapper.eq(OrgBusinessResourceModel::getAppId, queryParamVO.getAppId());
        }
        if (StrUtil.isNotEmpty(queryParamVO.getName())) {
            wrapper.like(OrgBusinessResourceModel::getName, queryParamVO.getName());
        }
        if (queryParamVO.getStatus() != null) {
            wrapper.eq(OrgBusinessResourceModel::getStatus, queryParamVO.getStatus());
        }
        if (queryParamVO.getMenuResourceId() != null) {
            wrapper.eq(OrgBusinessResourceModel::getMenuResourceId, queryParamVO.getMenuResourceId());
        }
        wrapper.orderByAsc(OrgBusinessResourceModel::getSort);
        return this.selectList(wrapper);
    }

    /**
     * 更新菜单/目录
     * 为控制关联的功能、列表字段的显示状态信息预留该方法
     *
     * @param resourceModel 业务资源实体
     * @return 业务资源实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgBusinessResourceModel> updateMenu(OrgBusinessResourceModel resourceModel) {
        if (this.updateById(resourceModel) == 1) {
            return Result.success(resourceModel);
        } else {
            return Result.failure(resourceModel);
        }
    }

    /**
     * 删除菜单/目录
     *
     * @param id 业务资源id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteMenu(@NotNull(message = "id不能为空") Long id) {
        OrgBusinessResourceModel resourceModel = this.selectById(id);
        if (resourceModel == null) {
            return Result.failure("对应的数据不存在");
        }
        // 查询关联的子菜单
        LambdaQueryWrapper<OrgBusinessResourceModel> brWrapper = new LambdaQueryWrapper<>();
        brWrapper.eq(OrgBusinessResourceModel::getParentId, id);
        brWrapper.in(OrgBusinessResourceModel::getType, this.getMenuResourceTypeList());
        Long count = this.selectCount(brWrapper);
        if (count > 0) {
            return Result.failure("存在关联的下级菜单");
        }
        // 批量删除关联的功能
        LambdaQueryWrapper<OrgBusinessResourceModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgBusinessResourceModel::getMenuResourceId, id);
        this.delete(wrapper);
        // 批量删除关联的列表字段
        LambdaQueryWrapper<OrgListColumnModel> listWrapper = new LambdaQueryWrapper<>();
        listWrapper.eq(OrgListColumnModel::getMenuResourceId, id);
        listColumnMapper.delete(listWrapper);
        // 批量删除关联的数据方案
        dataSchemeService.batchDeleteDataScheme(id);
        // 批量删除关联的数据方案字段及条件符号关联表
        dataSchemeColumnService.batchDeleteSchemeColumnAndSymbol(id);

        // 删除菜单/目录
        if (this.deleteById(id) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 编码是否存在
     *
     * @param id id
     * @param code 编码
     * @return true/false
     */
    public boolean isExistCode(Long id, @NotBlank(message = "code不能为空") String code) {
        LambdaQueryWrapper<OrgBusinessResourceModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgBusinessResourceModel::getCode, code);
        if (id != null) {
            wrapper.ne(OrgBusinessResourceModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return (count != null && count > 0) ? true : false;
    }

    /**
     * 按钮编码是否存在
     *
     * @param id id
     * @param code 编码
     * @return true/false
     */
    @Override
    public boolean isExistCodeFunction(Long id, @NotBlank(message = "code不能为空") String code,Long menuResourceId) {
        LambdaQueryWrapper<OrgBusinessResourceModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgBusinessResourceModel::getCode, code);
        wrapper.eq(OrgBusinessResourceModel::getMenuResourceId, menuResourceId);
        if (id != null) {
            wrapper.ne(OrgBusinessResourceModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return (count != null && count > 0) ? true : false;
    }

    /**
     * 查询功能树形列表
     *
     * @param menuResourceId 菜单资源id
     * @return 功能树形列表
     */
    public List<OrgBusinessResourceModel> selectFunctionTreeList(@NotNull(message = "menuResourceId不能为空") Long menuResourceId) {
        // 查询所有功能资源列表
        List<String> typeList = Arrays.asList(CommonConstant.BUSINESS_RESOURCE_TYPE.FUNCTION.getValue());
        BusinessResourceListQueryParamVO queryParamVO = new BusinessResourceListQueryParamVO(typeList, null, null, null, menuResourceId);
        List<OrgBusinessResourceModel> functionList = this.getAllBusinessResourceList(queryParamVO);
        if (CollectionUtil.isEmpty(functionList)) {
            return new ArrayList<>();
        }
        DbConvert2Page dbConvert2Page = new DbConvert2Page();
        dbConvert2Page.executeDbToPage4List(functionList);
        // 组装树形列表下级结点map
        Map<Long, List<OrgBusinessResourceModel>> functionTreeListMap = this.getResourceTreeListMap(functionList);
        // 获取树形列表顶级结点
        List<OrgBusinessResourceModel> topList = functionTreeListMap.get(menuResourceId);
        if (CollectionUtil.isEmpty(topList)) {
            return new ArrayList<>();
        }
        // 递归生成功能树形列表下级结点
        for (OrgBusinessResourceModel top : topList) {
            this.getChildResourceTreeList(top, functionTreeListMap);
        }
        return topList;
    }

    /**
     * 获取功能树形结构（按钮，用于菜单编辑页面选择上级菜单）
     *
     * @param menuResourceId 菜单资源id
     * @return 功能树形结构（按钮）
     */
    public ResourceTreeVO getFunctionTree(@NotNull(message = "menuResourceId不能为空") Long menuResourceId) {
        // 增加顶级结点
        ResourceTreeVO top = new ResourceTreeVO();
        top.setId(menuResourceId); // 顶级结点的id设置为菜单id
        top.setName(CommonConstant.TREE_ROOT_NAME);
        top.setType(CommonConstant.PRIVILEGE_TYPE.FUNCTION.getValue());
        // 查询所有功能资源列表
        List<String> typeList = Arrays.asList(CommonConstant.BUSINESS_RESOURCE_TYPE.FUNCTION.getValue());
        BusinessResourceListQueryParamVO queryParamVO = new BusinessResourceListQueryParamVO(typeList, null, null, null, menuResourceId);
        List<OrgBusinessResourceModel> functionList = this.getAllBusinessResourceList(queryParamVO);
        if (CollectionUtil.isEmpty(functionList)) {
            top.setChildren(null);
            return top;
        }
        // 组装树形下级结点map
        Map<Long, List<ResourceTreeVO>> functionMap = this.getResourceTreeVOMap(functionList, CommonConstant.PRIVILEGE_TYPE.FUNCTION.getValue());
        // 递归生成功能树形结构下级结点
        this.getChildResourceTree(top, functionMap);
        return top;
    }

    /**
     * 删除功能（按钮）
     *
     * @param id 业务资源id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteFunction(@NotNull(message = "id不能为空") Long id) {
        OrgBusinessResourceModel resourceModel = this.selectById(id);
        if (resourceModel == null) {
            return Result.failure("对应的数据不存在");
        }
        // 查询关联的子菜单
        LambdaQueryWrapper<OrgBusinessResourceModel> brWrapper = new LambdaQueryWrapper<>();
        brWrapper.eq(OrgBusinessResourceModel::getParentId, id);
        brWrapper.eq(OrgBusinessResourceModel::getType, CommonConstant.BUSINESS_RESOURCE_TYPE.FUNCTION.getValue());
        Long count = this.selectCount(brWrapper);
        if (count > 0) {
            return Result.failure("存在关联的下级按钮");
        }
        // 递归删除下级功能
        this.recursionDeleteChildFunction(id);
        // 删除功能（按钮）
        if (this.deleteById(id) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 递归删除下级功能
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void recursionDeleteChildFunction(Long id) {
        LambdaQueryWrapper<OrgBusinessResourceModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgBusinessResourceModel::getParentId, id);
        List<OrgBusinessResourceModel> children = this.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(children)) {
            for (OrgBusinessResourceModel child : children) {
                this.recursionDeleteChildFunction(child.getId());
            }
            // 批量删除直接下级功能
            this.delete(wrapper);
        }
    }

    /**
     * 根据用户id，应用id获取关联的菜单列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 菜单列表
     */
    public List<OrgBusinessResourceModel> getMenuListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, @NotNull(message = "appId不能为空") Long appId) {
        return dao.getMenuListByUserAndApp(userId, appId);
    }

    /**
     * 根据应用id获取关联的功能列表
     *
     * @param appId 应用id
     * @return 功能列表
     */
    public List<OrgBusinessResourceModel> getFunctionListByAppId(@NotNull(message = "appId不能为空") Long appId) {
        List<String> typeList = Arrays.asList(CommonConstant.BUSINESS_RESOURCE_TYPE.FUNCTION.getValue());
        BusinessResourceListQueryParamVO queryParamVO = new BusinessResourceListQueryParamVO(typeList, appId, null, null, null);
        List<OrgBusinessResourceModel> functionList = this.getAllBusinessResourceList(queryParamVO);
        if (CollectionUtil.isEmpty(functionList)) {
            return new ArrayList<>();
        }
        return functionList;
    }

    /**
     * 根据用户id，应用id获取关联的功能列表
     *
     * @param userId 用户id
     * @param appId 应用id
     * @return 功能列表
     */
    public List<OrgBusinessResourceModel> getFunctionListByUserAndApp(@NotNull(message = "userId不能为空") Long userId, @NotNull(message = "appId不能为空") Long appId) {
        return dao.getFunctionListByUserAndApp(userId, appId);
    }

    /**
     * 根据角色id列表获取关联的业务资源列表
     *
     * @param roleIdList 角色id列表
     * @return 业务资源列表
     */
    public List<OrgBusinessResourceModel> getBusinessResourceListByRoleIdList(@NotEmpty(message = "roleIdList不能为空") List<Long> roleIdList) {
        return dao.getBusinessResourceListByRoleIdList(roleIdList);
    }

    /**
     * 获取所有搜索菜单列表
     *
     * @return 搜索菜单列表
     */
    public List<SearchMenuVO> getAllSearchMenuList() {
        List<SearchMenuVO> list = dao.getAllSearchMenuList();
        if (CollectionUtil.isNotEmpty(list)) {
            DbConvert2Page dbConvert2Page = new DbConvert2Page();
            dbConvert2Page.executeDbToPage4List(list);
        }
        return list;
    }

}
