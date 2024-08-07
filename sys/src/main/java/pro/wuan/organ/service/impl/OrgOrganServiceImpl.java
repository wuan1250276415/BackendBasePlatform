package pro.wuan.organ.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.constant.HttpStatus;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.dict.service.ISysDictionaryService;
import pro.wuan.element.mapper.OrgElementMapper;
import pro.wuan.element.service.IOrgElementService;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.userapi.entity.OrgElementModel;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.feignapi.userapi.vo.OrganTreeVO;
import pro.wuan.feignapi.userapi.vo.UnitTreeVO;
import pro.wuan.organ.dto.GetUnitDto;
import pro.wuan.organ.mapper.OrgOrganMapper;
import pro.wuan.organ.service.IOrgOrganService;
import pro.wuan.post.mapper.OrgPostMapper;
import pro.wuan.role.mapper.OrgRoleMapper;
import pro.wuan.service.IOrgAppService;
import pro.wuan.user.dto.excelImport.ExcelImportResultDto;
import pro.wuan.user.dto.excelImport.ExcelUnitDto;
import pro.wuan.user.service.IOrgUserService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 组织机构
 *
 * @author: liumin
 * @date: 2021-08-30 11:21:11
 */
@Slf4j
@Service("orgOrganService")
public class OrgOrganServiceImpl extends BaseServiceImpl<OrgOrganMapper, OrgOrganModel> implements IOrgOrganService {

    @Autowired
    private IOrgAppService appService;

    @Autowired
    private IOrgUserService userService;

    @Resource
    private IOrgElementService elementService;

    @Resource
    private OrgElementMapper elementMapper;

    @Resource
    private OrgPostMapper postMapper;

    @Resource
    private OrgRoleMapper roleMapper;
    @Resource
    private ISysDictionaryService sysDictionaryService;

    @Override
    public OrgOrganModel selectById(Long id) {
        OrgOrganModel orgOrganModel = dao.selectById(id);
        if (orgOrganModel != null) {
            orgOrganModel.setPathName(this.getPathName(orgOrganModel));
        }
        return orgOrganModel;
    }

    @Override
    public List<OrgOrganModel> selectBatchIds(List<Long> ids) {
        List<OrgOrganModel> organModels = dao.selectBatchIds(ids);
        organModels.stream().forEach(y -> {
            y.setPathName(this.getPathName(y));
        });
        return organModels;
    }

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @param id                    单位id，值为空时根据当前登录用户获取单位树形结构
     * @param organBusinessTypeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 单位树形结构
     */
    public List<UnitTreeVO> getUnitTree(Long id, List<String> organBusinessTypeList) {
        return this.getUnitTreeWithoutRoot(id, organBusinessTypeList);
    }

    /**
     * 获取单位树形结构，只显示单位结点
     *
     * @param id       组织机构id，值为空时根据当前登录用户获取单位树形结构
     * @param showRoot 是否显示顶级结点
     * @return 单位树形结构
     */
    public List<UnitTreeVO> getUnitTree(Long id, boolean showRoot) {
        if (showRoot) { // 显示顶级结点
            return this.getUnitTreeWithRoot(id, null);
        } else { // 不显示顶级结点
            return this.getUnitTreeWithoutRoot(id, null);
        }
    }

    /**
     * 获取所有单位树形结构，只显示单位结点
     *
     * @return 单位树形结构
     */
    public List<UnitTreeVO> getAllUnitTree() {
        List<UnitTreeVO> unitTreeVOList = new ArrayList<>();
        UnitTreeVO root = new UnitTreeVO();
        root.setId(CommonConstant.TREE_ROOT_ID);
        root.setCode(CommonConstant.STRING_BLANK);
        root.setName(CommonConstant.TREE_ROOT_NAME);
        unitTreeVOList.add(root);
        this.getChildUnitTree(root, null);
        return unitTreeVOList;

    }

    /**
     * 获取单位树形结构，只显示单位结点，并且显示顶级结点
     *
     * @param id                    组织机构id，值为空时根据当前登录用户获取单位树形结构
     * @param organBusinessTypeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 单位树形结构
     */
    private List<UnitTreeVO> getUnitTreeWithRoot(Long id, List<String> organBusinessTypeList) {
        List<UnitTreeVO> unitTreeVOList = new ArrayList<>();
        UnitTreeVO root = new UnitTreeVO();
        root.setId(CommonConstant.TREE_ROOT_ID);
        root.setCode(CommonConstant.STRING_BLANK);
        root.setName(CommonConstant.TREE_ROOT_NAME);
        unitTreeVOList.add(root);
        if (id != null) {
            OrgOrganModel self = this.getOrganById(id);
            return this.getUnitTreeVOList(root, self, unitTreeVOList, organBusinessTypeList);
        } else { // 根据当前登录用户获取单位树形结构
            UserDetails userDetails = UserContext.getCurrentUser();
            if (userDetails == null || userDetails.getUser() == null) {
                return unitTreeVOList;
            }
            if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(userDetails.getUser().getType())) { // 超级管理员
                this.getChildUnitTree(root, organBusinessTypeList);
                return unitTreeVOList;
            } else {
                OrgOrganModel self = userDetails.getUnit();
                return this.getUnitTreeVOList(root, self, unitTreeVOList, organBusinessTypeList);
            }
        }
    }

    /**
     * 获取单位树形结构，只显示单位结点，并且不显示顶级结点
     *
     * @param id                    组织机构id，值为空时根据当前登录用户获取单位树形结构
     * @param organBusinessTypeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 单位树形结构
     */
    private List<UnitTreeVO> getUnitTreeWithoutRoot(Long id, List<String> organBusinessTypeList) {
        List<UnitTreeVO> unitTreeVOList = new ArrayList<>();
        if (id != null) {
            OrgOrganModel self = this.getOrganById(id);
            return this.getUnitTreeVOList(self, unitTreeVOList, organBusinessTypeList);
        } else { // 根据当前登录用户获取单位树形结构
            UserDetails userDetails = UserContext.getCurrentUser();
            if (userDetails == null || userDetails.getUser() == null) {
                return unitTreeVOList;
            }
            if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(userDetails.getUser().getType())) { // 超级管理员
                List<OrgOrganModel> unitList = this.getDirectChildOrgan(CommonConstant.TREE_ROOT_ID, CommonConstant.ORGAN_TYPE.UNIT, organBusinessTypeList);
                if (CollectionUtil.isNotEmpty(unitList)) {
                    for (OrgOrganModel unit : unitList) {
                        UnitTreeVO unitVO = this.getUnitTreeVO(unit, organBusinessTypeList);
                        unitTreeVOList.add(unitVO);
                    }
                }
                return unitTreeVOList;
            } else {
                OrgOrganModel self = userDetails.getUnit();
                return this.getUnitTreeVOList(self, unitTreeVOList, organBusinessTypeList);
            }
        }
    }

    /**
     * 获取单位树形VO列表
     *
     * @param root
     * @param self
     * @param unitTreeVOList
     * @param organBusinessTypeList
     * @return
     */
    private List<UnitTreeVO> getUnitTreeVOList(UnitTreeVO root, OrgOrganModel self, List<UnitTreeVO> unitTreeVOList, List<String> organBusinessTypeList) {
        if (self == null) {
            return unitTreeVOList;
        }
        if (!CommonConstant.ORGAN_TYPE.UNIT.getValue().equals(self.getType())) {
            return unitTreeVOList;
        }
        UnitTreeVO selfTreeVO = this.getUnitTreeVO(self, organBusinessTypeList);
        List<UnitTreeVO> rootChildren = new ArrayList<>();
        rootChildren.add(selfTreeVO);
        root.setChildren(rootChildren);
        return unitTreeVOList;
    }

    /**
     * 获取单位树形VO列表
     *
     * @param self
     * @param unitTreeVOList
     * @param organBusinessTypeList
     * @return
     */
    private List<UnitTreeVO> getUnitTreeVOList(OrgOrganModel self, List<UnitTreeVO> unitTreeVOList, List<String> organBusinessTypeList) {
        if (self == null) {
            return unitTreeVOList;
        }
        if (!CommonConstant.ORGAN_TYPE.UNIT.getValue().equals(self.getType())) {
            return unitTreeVOList;
        }
        if (CollectionUtil.isNotEmpty(organBusinessTypeList) &&
                (StrUtil.isEmpty(self.getOrganBusinessType()) || !organBusinessTypeList.contains(self.getOrganBusinessType()))) {
            return unitTreeVOList;
        }
        UnitTreeVO selfTreeVO = this.getUnitTreeVO(self, organBusinessTypeList);
        unitTreeVOList.add(selfTreeVO);
        return unitTreeVOList;
    }

    /**
     * 获取单位树形VO
     *
     * @param unit
     * @param organBusinessTypeList
     * @return
     */
    private UnitTreeVO getUnitTreeVO(OrgOrganModel unit, List<String> organBusinessTypeList) {
        UnitTreeVO unitTreeVO = new UnitTreeVO();
        unitTreeVO.setId(unit.getId());
        unitTreeVO.setParentId(unit.getParentId());
        unitTreeVO.setCode(StrUtil.isNotEmpty(unit.getUniteCreditCode()) ? unit.getUniteCreditCode() : CommonConstant.STRING_BLANK); // 编码使用统一社会信用代码
        unitTreeVO.setName(unit.getName());
        unitTreeVO.setRemark(unit.getRemark());
        this.getChildUnitTree(unitTreeVO, organBusinessTypeList);
        return unitTreeVO;
    }

    /**
     * 递归生成单位树形结构下级结点
     *
     * @param unitTreeVO
     * @param organBusinessTypeList
     */
    private void getChildUnitTree(UnitTreeVO unitTreeVO, List<String> organBusinessTypeList) {
        List<OrgOrganModel> children = this.getDirectChildOrgan(unitTreeVO.getId(), CommonConstant.ORGAN_TYPE.UNIT, organBusinessTypeList);
        if (CollectionUtil.isNotEmpty(children)) {
            List<UnitTreeVO> childVOList = new ArrayList<>();
            for (OrgOrganModel child : children) {
                UnitTreeVO childVO = new UnitTreeVO();
                childVO.setId(child.getId());
                childVO.setParentId(child.getParentId());
                childVO.setCode(StrUtil.isNotEmpty(child.getUniteCreditCode()) ? child.getUniteCreditCode() : CommonConstant.STRING_BLANK); // 编码使用统一社会信用代码
                childVO.setName(child.getName());
                childVO.setRemark(child.getRemark());
                this.getChildUnitTree(childVO, organBusinessTypeList);
                childVOList.add(childVO);
            }
            unitTreeVO.setChildren(childVOList);
        }
    }

    /**
     * 获取组织机构实体
     *
     * @param organId 组织机构id
     * @return 组织机构实体
     */
    public OrgOrganModel getOrganById(Long organId) {
        if (organId == null) {
            return null;
        }
        return dao.selectById(organId);
    }

    /**
     * 获取直接下级组织机构
     *
     * @param organId               组织机构id
     * @param organType             组织机构类型枚举。值为unit时返回下级单位，值为dept时返回下级部门，值为null时返回下级组织机构（单位+部门）
     * @param organBusinessTypeList 机构业务类型列表
     * @return 直接下级组织机构
     */
    private List<OrgOrganModel> getDirectChildOrgan(Long organId, CommonConstant.ORGAN_TYPE organType, List<String> organBusinessTypeList) {
        if (organId == null) {
            return null;
        }
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgOrganModel::getParentId, organId);
        if (organType != null) {
            wrapper.eq(OrgOrganModel::getType, organType.getValue());
        }
        if (CollectionUtil.isNotEmpty(organBusinessTypeList)) {
            wrapper.in(OrgOrganModel::getOrganBusinessType, organBusinessTypeList);
        }
        wrapper.orderByAsc(OrgOrganModel::getSort);
        return this.selectList(wrapper);
    }

    /**
     * 获取直属上级单位
     * 根据组织机构id获取组织机构信息，判断组织机构是否为单位，是则返回组织机构信息；不是则根据其上级组织机构id继续递归查找
     *
     * @param organId 组织机构id
     * @return 上级单位
     */
    public OrgOrganModel getDirectParentUnit(Long organId) {
        if (organId == null) {
            return null;
        }
        OrgOrganModel organ = dao.selectById(organId);
        if (organ == null) {
            return null;
        }
        if (CommonConstant.ORGAN_TYPE.UNIT.getValue().equals(organ.getType())) {
            return organ;
        } else {
            return this.getDirectParentUnit(organ.getParentId());
        }
    }

    /**
     * 获取树行结构Ids
     *
     * @param organModel
     * @return
     */
    private String getTreeIds(OrgOrganModel organModel) {
        if (organModel == null || organModel.getId() == null) {
            return null;
        }
        Stack<Long> stack = new Stack<>();
        // 设置上级组织机构树形堆栈
        this.setOrganTreeStack(organModel.getId(), stack);
        // 生成树行结构Ids
        StringBuilder sb = new StringBuilder();
        Long organId = null;
        while (!stack.empty()) {
            organId = stack.pop();
            if (organId != null) {
                sb.append(CommonConstant.SEPARATOR_COMMA).append(organId);
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * 设置上级组织机构树形堆栈
     *
     * @param organId 组织机构id
     * @param stack   上级组织机构树形堆栈
     * @return
     */
    private void setOrganTreeStack(Long organId, Stack<Long> stack) {
        if (organId == null) {
            return;
        }
        OrgOrganModel organ = dao.selectById(organId);
        if (organ == null) {
            return;
        }
        stack.push(organ.getId());
        this.setOrganTreeStack(organ.getParentId(), stack);
    }

    /**
     * 根据组织机构名称获取id
     *
     * @param name
     * @param organNameAndId
     * @return
     */
    private Long getOrganIdByName(String name, Map<String, Long> organNameAndId) {
        if (StrUtil.isEmpty(name)) {
            return CommonConstant.TREE_ROOT_ID;
        }
        Long organId = organNameAndId.get(name);
        Assert.notNull(organId, "单位名称[" + name + "]对应的单位信息不存在");
        return organId;
    }

    /**
     * 保存单位
     *
     * @param unit 单位实体
     * @return 单位实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgOrganModel> saveUnit(OrgOrganModel unit) {
        Assert.hasLength(unit.getUnitAdminAccount(), "单位管理员账号不能为空");
        Assert.isTrue(!this.isExistUnitCode(unit.getId(), unit.getUniteCreditCode()), "单位编号已经存在，请重新输入");
        this.checkParentId(unit.getParentId());

        unit.setType(CommonConstant.ORGAN_TYPE.UNIT.getValue());
        if (this.insert(unit) <= 0) {
            return Result.failure(unit);
        }
        // 更新树行结构Ids
        unit.setTreeIds(this.getTreeIds(unit));
        unit.setTenantId(unit.getId()); // 保存单位时需要设置租户id
        this.updateById(unit);
        // 保存单位管理员账号
        userService.saveUnitAdmin(unit.getUnitAdminAccount(), unit);
        // 保存元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(unit.getId());
        elementModel.setName(unit.getName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.UNIT.getValue());
        elementModel.setSort(unit.getSort());
        elementModel.setParentId(unit.getParentId());
        elementModel.setTreeIds(unit.getTreeIds());
        elementModel.setStatus(unit.getStatus());
        elementModel.setTenantId(unit.getTenantId()); // 保存单位对应的元素时需要设置租户id
        elementService.insert(elementModel);
        return Result.success(unit);
    }

    /**
     * 更新单位
     *
     * @param unit 单位实体
     * @return 单位实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgOrganModel> updateUnit(OrgOrganModel unit) {
        OrgOrganModel organ = this.getOrganById(unit.getId());
        Assert.notNull(organ, "单位信息不存在");
        Assert.isTrue(!this.isExistUnitCode(unit.getId(), unit.getUniteCreditCode()), "单位编号已经存在，请重新输入");
        Assert.isTrue(CommonConstant.ORGAN_TYPE.UNIT.getValue().equals(organ.getType()), "该组织机构不是单位");
        Assert.isTrue(organ.getParentId().equals(unit.getParentId()), "上级不允许修改");
        Assert.isTrue(organ.getUnitAdminAccount().equals(unit.getUnitAdminAccount()), "单位管理员账号不允许修改");
        this.checkParentId(unit.getParentId());
        // 更新元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(unit.getId());
        elementModel.setName(unit.getName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.UNIT.getValue());
        elementModel.setSort(unit.getSort());
        elementModel.setStatus(unit.getStatus());
        elementModel.setTenantId(unit.getId()); // 保存单位对应的元素时需要设置租户id
        elementService.updateById(elementModel);

        unit.setTenantId(unit.getId()); // 保存单位时需要设置租户id
        if (this.updateById(unit) == 1) {
            return Result.success(unit);
        } else {
            return Result.failure(unit);
        }
    }

    /**
     * 校验上级组织机构id
     *
     * @param parentId
     */
    private void checkParentId(Long parentId) {
        OrgUserModel user = UserContext.getCurrentUser().getUser();
        if (!CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 不是超级管理员
            Assert.isTrue(!CommonConstant.TREE_ROOT_ID.equals(parentId), "上级单位不能为顶级结点");
        }
    }

    /**
     * 删除单位
     *
     * @param id 单位id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteUnit(Long id) {
        OrgOrganModel organModel = this.getOrganById(id);
        Assert.notNull(organModel, "单位信息不存在");
        Assert.isTrue(CommonConstant.ORGAN_TYPE.UNIT.getValue().equals(organModel.getType()), "该组织机构不是单位");
        this.checkParentId(organModel.getParentId());
        OrgUserModel currentUser = UserContext.getCurrentUser().getUser();
        Assert.isTrue(CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(currentUser.getType()), "没有权限删除单位信息");
        // 单位下关联的用户
        Long userCount = this.getUserCount(id);
        Assert.isTrue(userCount <= 0, "该单位存在关联的用户信息，请先删除用户信息");
        // 查询关联的元素
        LambdaQueryWrapper<OrgElementModel> eqw = new LambdaQueryWrapper<>();
        eqw.like(OrgElementModel::getTreeIds, id);
        List<OrgElementModel> elementList = elementMapper.selectList(eqw);
        Assert.notEmpty(elementList, "关联的元素信息不存在");
        List<Long> organIdList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();
        List<Long> roleIdList = new ArrayList<>();
        elementList.stream().forEach(element -> {
            if (CommonConstant.ELEMENT_TYPE.UNIT.getValue().equals(element.getType())) {
                organIdList.add(element.getId());
            } else if (CommonConstant.ELEMENT_TYPE.DEPT.getValue().equals(element.getType())) {
                organIdList.add(element.getId());
            } else if (CommonConstant.ELEMENT_TYPE.POST.getValue().equals(element.getType())) {
                postIdList.add(element.getId());
            } else if (CommonConstant.ELEMENT_TYPE.ROLE.getValue().equals(element.getType())) {
                roleIdList.add(element.getId());
            }
        });
        // 删除关联的元素
        elementMapper.delete(eqw);
        // 删除关联的角色
        if (CollectionUtil.isNotEmpty(roleIdList)) {
            roleMapper.deleteBatchIds(roleIdList);
        }
        // 删除关联的岗位
        if (CollectionUtil.isNotEmpty(postIdList)) {
            postMapper.deleteBatchIds(postIdList);
        }
        // 删除关联的单位、部门
        if (CollectionUtil.isNotEmpty(organIdList)) {
            this.deleteBatchIds(organIdList);
        }
        return Result.success("删除成功！");
    }

    /**
     * 启用单位
     *
     * @param id 单位id
     * @return 单位实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgOrganModel> useUnit(Long id) {
        return updateUnitStatus(id, CommonConstant.USE_STATUS.USE);
    }

    /**
     * 停用单位
     *
     * @param id 单位id
     * @return 单位实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgOrganModel> nonuseUnit(Long id) {
        return updateUnitStatus(id, CommonConstant.USE_STATUS.NONUSE);
    }

    /**
     * 更新单位启用状态
     *
     * @param id
     * @param useStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgOrganModel> updateUnitStatus(Long id, CommonConstant.USE_STATUS useStatus) {
        OrgOrganModel organModel = this.getOrganById(id);
        Assert.notNull(organModel, "单位信息不存在");
        Integer newStatus = useStatus.getValue();
        if (newStatus == organModel.getStatus()) {
            return Result.success(organModel);
        }
        // 更新元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(id);
        elementModel.setStatus(newStatus);
        elementService.updateById(elementModel);

        organModel.setStatus(newStatus);
        if (this.updateById(organModel) == 1) {
            return Result.success(organModel);
        } else {
            return Result.failure(organModel);
        }
    }

    /**
     * 获取组织机构树形结构
     *
     * @param id       组织机构id，值为空时根据当前登录用户获取从单位开始的组织机构树形结构
     * @param showRoot 是否显示顶级结点
     * @return 组织机构树形结构
     */
    public List<OrganTreeVO> getOrganTree(Long id, boolean showRoot) {
        UserDetails userDetails = UserContext.getCurrentUser();
        Assert.isTrue(userDetails.getUser() != null, "当前用户未登录或登录信息已失效，请重新登录");
        CommonConstant.ORGAN_TYPE organType = null;
        if (!CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(userDetails.getUser().getType())) { // 不是超级管理员
            organType = CommonConstant.ORGAN_TYPE.DEPT;
        }
        if (showRoot) { // 显示顶级结点
            return this.getOrganTreeWithRoot(id, organType, userDetails, null);
        } else { // 不显示顶级结点
            return this.getOrganTreeWithoutRoot(id, organType, userDetails, null);
        }
    }

    /**
     * 获取组织机构树形结构，并且显示顶级结点
     *
     * @param id                    组织机构id，值为空时根据当前登录用户获取从单位开始的组织机构树形结构
     * @param organType             组织机构类型
     * @param userDetails           用户详细信息
     * @param organBusinessTypeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 组织机构树形结构
     */
    private List<OrganTreeVO> getOrganTreeWithRoot(Long id, CommonConstant.ORGAN_TYPE organType, UserDetails userDetails, List<String> organBusinessTypeList) {
        List<OrganTreeVO> organTreeVOList = new ArrayList<>();
        OrganTreeVO root = new OrganTreeVO();
        root.setId(CommonConstant.TREE_ROOT_ID);
        root.setCode(CommonConstant.STRING_BLANK);
        root.setName(CommonConstant.TREE_ROOT_NAME);
        root.setUnit(true);
        organTreeVOList.add(root);
        if (id != null) {
            OrgOrganModel self = this.getOrganById(id);
            return this.getOrganTreeVOList(root, self, organTreeVOList, organType, organBusinessTypeList);
        } else { // 根据当前登录用户获取从单位开始的组织机构树形结构
            if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(userDetails.getUser().getType())) { // 超级管理员
                this.getChildOrganTree(root, organType, organBusinessTypeList);
                return organTreeVOList;
            } else {
                OrgOrganModel self = userDetails.getUnit();
                return this.getOrganTreeVOList(root, self, organTreeVOList, organType, organBusinessTypeList);
            }
        }
    }

    /**
     * 获取组织机构树形结构，并且不显示顶级结点
     *
     * @param id                    组织机构id，值为空时根据当前登录用户获取从单位开始的组织机构树形结构
     * @param organType             组织机构类型
     * @param userDetails           用户详细信息
     * @param organBusinessTypeList 机构业务类型列表，值为空时忽略该参数。详见：字典管理->基础架构平台->机构业务类型cloud_organBusinessType
     * @return 组织机构树形结构
     */
    private List<OrganTreeVO> getOrganTreeWithoutRoot(Long id, CommonConstant.ORGAN_TYPE organType, UserDetails userDetails, List<String> organBusinessTypeList) {
        List<OrganTreeVO> organTreeVOList = new ArrayList<>();
        if (id != null) {
            OrgOrganModel self = this.getOrganById(id);
            return this.getOrganTreeVOList(self, organTreeVOList, organType, organBusinessTypeList);
        } else { // 根据当前登录用户获取从单位开始的组织机构树形结构
            if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(userDetails.getUser().getType())) { // 超级管理员
                List<OrgOrganModel> organList = this.getDirectChildOrgan(CommonConstant.TREE_ROOT_ID, organType, organBusinessTypeList);
                if (CollectionUtil.isNotEmpty(organList)) {
                    for (OrgOrganModel organ : organList) {
                        OrganTreeVO organVO = this.getOrganTreeVO(organ, organType, organBusinessTypeList);
                        organTreeVOList.add(organVO);
                    }
                }
                return organTreeVOList;
            } else {
                OrgOrganModel self = userDetails.getUnit();
                return this.getOrganTreeVOList(self, organTreeVOList, organType, organBusinessTypeList);
            }
        }
    }

    /**
     * 获取组织机构树形VO列表
     *
     * @param root
     * @param self
     * @param organTreeVOList
     * @param organType
     * @param organBusinessTypeList
     * @return
     */
    private List<OrganTreeVO> getOrganTreeVOList(OrganTreeVO root, OrgOrganModel self, List<OrganTreeVO> organTreeVOList,
                                                 CommonConstant.ORGAN_TYPE organType, List<String> organBusinessTypeList) {
        if (self == null) {
            return organTreeVOList;
        }
        OrganTreeVO selfTreeVO = this.getOrganTreeVO(self, organType, organBusinessTypeList);
        List<OrganTreeVO> rootChildren = new ArrayList<>();
        rootChildren.add(selfTreeVO);
        root.setChildren(rootChildren);
        return organTreeVOList;
    }

    /**
     * 获取组织机构树形VO列表
     *
     * @param self
     * @param organTreeVOList
     * @param organType
     * @param organBusinessTypeList
     * @return
     */
    private List<OrganTreeVO> getOrganTreeVOList(OrgOrganModel self, List<OrganTreeVO> organTreeVOList, CommonConstant.ORGAN_TYPE organType,
                                                 List<String> organBusinessTypeList) {
        if (self == null) {
            return organTreeVOList;
        }
        OrganTreeVO selfTreeVO = this.getOrganTreeVO(self, organType, organBusinessTypeList);
        organTreeVOList.add(selfTreeVO);
        return organTreeVOList;
    }

    /**
     * 获取组织机构树形VO
     *
     * @param organ
     * @param organType
     * @param organBusinessTypeList
     * @return
     */
    private OrganTreeVO getOrganTreeVO(OrgOrganModel organ, CommonConstant.ORGAN_TYPE organType, List<String> organBusinessTypeList) {
        OrganTreeVO organTreeVO = new OrganTreeVO();
        organTreeVO.setId(organ.getId());
        organTreeVO.setParentId(organ.getParentId());
        organTreeVO.setCode(StrUtil.isNotEmpty(organ.getUniteCreditCode()) ? organ.getUniteCreditCode() : CommonConstant.STRING_BLANK); // 编码使用统一社会信用代码
        organTreeVO.setName(organ.getName());
        organTreeVO.setUnit(this.isUnit(organ.getType()));
        this.getChildOrganTree(organTreeVO, organType, organBusinessTypeList);
        return organTreeVO;
    }

    /**
     * 递归生成组织机构树形结构下级结点
     *
     * @param organTreeVO
     * @param organType
     * @param organBusinessTypeList
     */
    private void getChildOrganTree(OrganTreeVO organTreeVO, CommonConstant.ORGAN_TYPE organType, List<String> organBusinessTypeList) {
        List<OrgOrganModel> children = this.getDirectChildOrgan(organTreeVO.getId(), organType, organBusinessTypeList);
        if (CollectionUtil.isNotEmpty(children)) {
            List<OrganTreeVO> childVOList = new ArrayList<>();
            for (OrgOrganModel child : children) {
                OrganTreeVO childVO = new OrganTreeVO();
                childVO.setId(child.getId());
                childVO.setParentId(child.getParentId());
                childVO.setCode(StrUtil.isNotEmpty(child.getUniteCreditCode()) ? child.getUniteCreditCode() : CommonConstant.STRING_BLANK); // 编码使用统一社会信用代码
                childVO.setName(child.getName());
                childVO.setUnit(this.isUnit(child.getType()));
                this.getChildOrganTree(childVO, organType, organBusinessTypeList);
                childVOList.add(childVO);
            }
            organTreeVO.setChildren(childVOList);
        }
    }

    /**
     * 是否单位
     *
     * @param type 组织机构类型
     * @return
     */
    private boolean isUnit(String type) {
        return CommonConstant.ORGAN_TYPE.UNIT.getValue().equals(type);
    }

    /**
     * 保存部门
     *
     * @param department 部门实体
     * @return 部门实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgOrganModel> saveDept(OrgOrganModel department) {
        department.setType(CommonConstant.ORGAN_TYPE.DEPT.getValue());
        if (this.insert(department) <= 0) {
            return Result.failure(department);
        }
        // 更新树行结构Ids
        department.setTreeIds(this.getTreeIds(department));
        this.updateById(department);
        // 保存元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(department.getId());
        elementModel.setName(department.getName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.DEPT.getValue());
        elementModel.setParentId(department.getParentId());
        elementModel.setTreeIds(department.getTreeIds());
        elementModel.setStatus(CommonConstant.USE_STATUS.USE.getValue());
        elementService.insert(elementModel);
        return Result.success(department);
    }

    /**
     * 更新部门
     *
     * @param department 部门实体
     * @return 部门实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgOrganModel> updateDept(OrgOrganModel department) {
        OrgOrganModel organ = this.getOrganById(department.getId());
        Assert.notNull(organ, "部门信息不存在");
        Assert.isTrue(organ.getParentId().equals(department.getParentId()), "上级不允许修改");
        // 更新元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(department.getId());
        elementModel.setName(department.getName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.DEPT.getValue());
        elementService.updateById(elementModel);

        if (this.updateById(department) == 1) {
            return Result.success(department);
        } else {
            return Result.failure(department);
        }
    }

    /**
     * 删除部门
     *
     * @param id 部门id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteDept(Long id) {
        OrgOrganModel organModel = this.getOrganById(id);
        Assert.notNull(organModel, "部门信息不存在");
        // 部门下关联的用户
        Long userCount = this.getUserCount(id);
        Assert.isTrue(userCount <= 0, "该部门存在关联的用户信息，请先删除用户信息");
        // 查询关联的元素
        LambdaQueryWrapper<OrgElementModel> eqw = new LambdaQueryWrapper<>();
        eqw.like(OrgElementModel::getTreeIds, id);
        List<OrgElementModel> elementList = elementMapper.selectList(eqw);
        Assert.notEmpty(elementList, "关联的元素信息不存在");
        List<Long> deptIdList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();
        elementList.stream().forEach(element -> {
            if (CommonConstant.ELEMENT_TYPE.DEPT.getValue().equals(element.getType())) {
                deptIdList.add(element.getId());
            } else if (CommonConstant.ELEMENT_TYPE.POST.getValue().equals(element.getType())) {
                postIdList.add(element.getId());
            }
        });
        // 删除关联的元素
        elementMapper.delete(eqw);
        // 删除关联的岗位
        if (CollectionUtil.isNotEmpty(postIdList)) {
            postMapper.deleteBatchIds(postIdList);
        }
        // 删除关联的部门
        if (CollectionUtil.isNotEmpty(deptIdList)) {
            this.deleteBatchIds(deptIdList);
        }
        return Result.success("删除成功！");
    }

    /**
     * 查询关联的用户数量
     *
     * @param id
     */
    private Long getUserCount(Long id) {
        LambdaQueryWrapper<OrgElementModel> uqw = new LambdaQueryWrapper<>();
        uqw.eq(OrgElementModel::getType, CommonConstant.ELEMENT_TYPE.USER.getValue());
        uqw.like(OrgElementModel::getTreeIds, id);
        return elementMapper.selectCount(uqw);
    }

    /**
     * 根据单位id获取该单位部门及直接下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    public List<OrgOrganModel> getSubDeptAndDirectUnitListByUnitId(Long unitId) {
        if (unitId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<OrgElementModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgElementModel::getType, CommonConstant.ELEMENT_TYPE.DEPT.getValue());
        wrapper.like(OrgElementModel::getTreeIds, unitId);
        wrapper.or();
        wrapper.eq(OrgElementModel::getType, CommonConstant.ELEMENT_TYPE.UNIT.getValue());
        wrapper.eq(OrgElementModel::getParentId, unitId);
        List<OrgElementModel> elementList = elementMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(elementList)) {
            return new ArrayList<>();
        }
        List<Long> organIdList = new ArrayList<>();
        for (OrgElementModel element : elementList) {
            organIdList.add(element.getId());
        }
        return dao.selectBatchIds(organIdList);
    }

    /**
     * 根据单位id获取该单位及所有下属单位列表
     *
     * @param unitId 单位id
     * @return 组织机构列表
     */
    @Override
    public List<OrgOrganModel> getDirectUnitListByUnitId(Long unitId) {
        if (unitId == null) {
            //拿到当前用户，判断是否为超级管理员
            OrgUserModel orgUserModel = UserContext.getCurrentUser().getUser();
            if (ObjectUtil.isEmpty(orgUserModel)) {
                return new ArrayList<>();
            }
            if (ObjectUtil.equal(CommonConstant.USER_TYPE.SUPER_ADMIN.getValue(), orgUserModel.getType())) {
                //超管的处理逻辑,获取所有单位列表
                return this.getAllUnitList();
            }
        }
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(OrgOrganModel::getId, unitId);
        wrapper.eq(OrgOrganModel::getType, CommonConstant.ELEMENT_TYPE.UNIT.getValue());
        wrapper.like(OrgOrganModel::getTreeIds, unitId);
        wrapper.orderByAsc(OrgOrganModel::getSort);
        return this.selectList(wrapper);
    }

    /**
     * 根据单位id获取该单位以及该单位及直接下属单位列表
     *
     * @return 组织机构列表
     */
    @Override
        public List<OrgOrganModel> getMyAndDirectUnitListByUnitId(GetUnitDto getUnitDto) {
        //组织机构类型
        if (CollectionUtil.isEmpty(getUnitDto.getOrganTypes())) {
            //默认查询单位
            List<String> types = Lists.newArrayList(CommonConstant.ELEMENT_TYPE.UNIT.getValue());
            getUnitDto.setOrganTypes(types);
        }
        if (getUnitDto.getUnitId() == null) {
            //拿到当前用户，判断是否为超级管理员
            OrgUserModel orgUserModel = UserContext.getCurrentUser().getUser();
            if (ObjectUtil.isEmpty(orgUserModel)) {
                return new ArrayList<>();
            }
            if (ObjectUtil.equal(CommonConstant.USER_TYPE.SUPER_ADMIN.getValue(), orgUserModel.getType())) {
                //超管的处理逻辑,获取所有单位列表
                return this.getAllUnitListAndDepts(getUnitDto.getOrganTypes());
            }
        }
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrgOrganModel::getType, getUnitDto.getOrganTypes());
        wrapper.like(OrgOrganModel::getTreeIds, getUnitDto.getUnitId());
        wrapper.orderByAsc(OrgOrganModel::getSort);
        return this.selectList(wrapper);
    }

    /**
     * 根据单位编号获取机构业务类型
     *
     * @param uniteCreditCode 单位编号
     * @return 机构业务类型
     */
    public String getOrganBusinessType(String uniteCreditCode) {
        OrgOrganModel organ = this.getOrganByUnitCode(uniteCreditCode);
        return (organ != null && StrUtil.isNotEmpty(organ.getOrganBusinessType())) ? organ.getOrganBusinessType() : CommonConstant.STRING_BLANK;
    }

    /**
     * 根据单位编号获取组织机构信息
     *
     * @param uniteCreditCode 单位编号
     * @return 组织机构信息
     */
    public OrgOrganModel getOrganByUnitCode(String uniteCreditCode) {
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgOrganModel::getUniteCreditCode, uniteCreditCode);
        wrapper.orderByDesc(OrgOrganModel::getCreateTime);
        wrapper.last("limit 1");
        return this.selectOne(wrapper);
    }

    /**
     * 根据名称获取组织机构信息
     *
     * @param name 名称
     * @return 组织机构信息
     */
    public OrgOrganModel getOrganByName(String name) {
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgOrganModel::getName, name);
        wrapper.orderByDesc(OrgOrganModel::getCreateTime);
        wrapper.last("limit 1");
        return this.selectOne(wrapper);
    }

    /**
     * 单位编码是否存在
     *
     * @param id       id
     * @param unitCode 单位编号
     * @return true/false
     */
    private boolean isExistUnitCode(Long id, String unitCode) {
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(unitCode)) {
            wrapper.eq(OrgOrganModel::getUniteCreditCode, unitCode);
        } else {
            return false;
        }
        if (id != null) {
            wrapper.ne(OrgOrganModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return (count != null && count > 0) ? true : false;
    }

    /**
     * 名称是否存在
     *
     * @param id   id
     * @param name 名称
     * @return true/false
     */
    private boolean isExistName(Long id, String name) {
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(name)) {
            wrapper.eq(OrgOrganModel::getName, name);
        } else {
            return false;
        }
        if (id != null) {
            wrapper.ne(OrgOrganModel::getId, id);
        }
        Long count = dao.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 根据单位类型获取单位列表
     *
     * @param organType 单位类型
     * @return
     */
    public List<OrgOrganModel> getUnitListByType(String organType) {
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgOrganModel::getOrganBusinessType, organType);
        wrapper.eq(OrgOrganModel::getType, CommonConstant.ORGAN_TYPE.UNIT.getValue());
        return dao.selectList(wrapper);
    }


    /**
     * 获取当前节点到顶级父节之间路径的单位名称,例：父节点子节点孙节点
     *
     * @param orgOrgan 当前节点单位信息
     * @return
     */
    private String getPathName(OrgOrganModel orgOrgan) {
        if (orgOrgan == null) {
            return Strings.EMPTY;
        }
        if (orgOrgan.getTreeIds() == null) {
            return orgOrgan.getName();
        }
        List<String> treeIds = Arrays.asList(orgOrgan.getTreeIds().split(","));
        List<Long> ids = treeIds.stream().map(y -> Long.valueOf(y)).collect(Collectors.toList());
        QueryWrapper<OrgOrganModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        String idsStr = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        queryWrapper.orderByAsc("strpos('" + idsStr + "', id::text)");


        //获取该路径下的所有单位列表
        List<OrgOrganModel> organModels = this.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(organModels)) {
            return orgOrgan.getName();
        }
        String pathName = Strings.EMPTY;
        if (organModels.size() == 1) {
            return orgOrgan.getName();
        }
        int i = 0;
        for (OrgOrganModel org : organModels) {
            if (i > 0) {
                pathName += org.getName();
            }
            i++;
        }
        return pathName;
    }


    /**
     * 获取上级单位到最顶级
     *
     * @param organId
     * @return
     */
    public List<OrgOrganModel> getParentUnits(Long organId) {
        List<OrgOrganModel> units = new ArrayList<>();
        if (organId == null) {
            return units;
        }
        OrgOrganModel org = this.selectById(organId);
        if (org == null) {
            return units;
        }
        List<String> treeIds = Arrays.asList(org.getTreeIds().split(","));
        List<Long> ids = treeIds.stream().map(y -> Long.valueOf(y)).collect(Collectors.toList());
        QueryWrapper<OrgOrganModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        String idsStr = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        queryWrapper.orderByAsc("strpos('" + idsStr + "', id::text)");
        return this.selectList(queryWrapper);
    }

    /**
     * 获取所有顶级单位列表
     *
     * @return 单位列表
     */
    public List<OrgOrganModel> getTopUnitList() {
        return this.getDirectChildOrgan(CommonConstant.TREE_ROOT_ID, CommonConstant.ORGAN_TYPE.UNIT, null);
    }

    /**
     * 获取所有单位列表
     *
     * @return 单位列表
     */
    public List<OrgOrganModel> getAllUnitList() {
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgOrganModel::getType, CommonConstant.ORGAN_TYPE.UNIT.getValue());
        wrapper.orderByAsc(OrgOrganModel::getName);
        return dao.selectList(wrapper);
    }

    /**
     * 获取所有单位列表以及部门
     *
     * @return 单位列表
     */
    @Override
    public List<OrgOrganModel> getAllUnitListAndDepts(List<String> organTypes) {
        if(CollectionUtil.isEmpty(organTypes)){
            organTypes.add(CommonConstant.ORGAN_TYPE.UNIT.getValue());
            organTypes.add(CommonConstant.ORGAN_TYPE.DEPT.getValue());
        }
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrgOrganModel::getType, organTypes);
        wrapper.orderByAsc(OrgOrganModel::getName);
        return dao.selectList(wrapper);
    }

    /**
     * 根据组织机构id获取该组织机构下的组织机构列表
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位
     * @return 组织机构列表
     */
    public List<OrgOrganModel> getSubOrganListByOrganId(Long organId, boolean containSubUnit) {
        LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(OrgOrganModel::getId, organId);
        if (!containSubUnit) {
            wrapper.eq(OrgOrganModel::getType, CommonConstant.ELEMENT_TYPE.DEPT.getValue());
        }
        wrapper.like(OrgOrganModel::getTreeIds, organId);
        wrapper.orderByAsc(OrgOrganModel::getName);
        return this.selectList(wrapper);
    }


    /**
     * 根据单位id获取所有单位
     *
     * @return 单位列表
     */
    @Override
    public List<OrgOrganModel> getUnitsByUnitIds(List<Long> unitIds) {
        List<OrgOrganModel> list = Lists.newArrayList();
        if(CollectionUtil.isNotEmpty(unitIds)){
            LambdaQueryWrapper<OrgOrganModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(OrgOrganModel::getId, unitIds);
            wrapper.orderByAsc(OrgOrganModel::getName);
            list = dao.selectList(wrapper);
        }
        return list;
    }

    /**
     * 批量导入单位表格解析获取接口
     * @param: [file, appId]
     * @return: pro.wuan.core.model.Result
     */
    @Override
    public Result importExcelUnit(MultipartFile file, Long appId) {
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
        List<ExcelUnitDto> unitDtoList;
        try {
            //从EXCEL表读取表格列数据信息
            importParams.setStartSheetIndex(0);
            unitDtoList = ExcelImportUtil.importExcel(file.getInputStream(), ExcelUnitDto.class, importParams);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("导入失败，读取文件出错：" + e.getMessage());
        }
        //创建拼接消息
        StringBuffer errorMassage = new StringBuffer();
        ExcelImportResultDto unitResult = this.batchImportUnit(unitDtoList);
        errorMassage.append(unitResult.getMessage());
        List<Long> userIdList = (List<Long>) unitResult.getResult();
        if (unitResult.isSuccess()) {
            return Result.success(HttpStatus.OK.value(), errorMassage.toString(), userIdList);
        } else {
            return Result.failure(errorMassage.toString());
        }
    }

    /**
     * 批量导入单位具体方法
     *
     * @param unitDtoList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportResultDto batchImportUnit(List<ExcelUnitDto> unitDtoList) {
        if (CollectionUtil.isEmpty(unitDtoList)) {
            return new ExcelImportResultDto(false, "单位导入失败。未获取到单位信息。", null);
        }
        int importCount = 0;
        StringBuffer errorMassage = new StringBuffer();
        //表格第一行是表头，行号从第2行开始
        AtomicInteger atomicInteger = new AtomicInteger(1);
        for (ExcelUnitDto dto : unitDtoList) {
            atomicInteger.incrementAndGet();
            String lineNum = "第" + atomicInteger.get() + "行，";
            String unitName = StrUtil.cleanBlank(dto.getName());
            try {
                //判断单位名称
                OrgOrganModel organModel = getOrganByName(unitName);
                if (ObjectUtil.isNotEmpty(organModel)) {
                    errorMassage.append("[" + lineNum + "单位名称：" + unitName + "]导入失败，单位已经存在。");
                    continue;
                }

                //默认上级ID
                Long parentId = 0L;
                if(StrUtil.isNotBlank(dto.getParentName())){
                    //查找上级单位是否存在，存在就关联
                    OrgOrganModel parentOrgan = getOrganByName(StrUtil.cleanBlank(dto.getParentName()));
                    if(ObjectUtil.isNotEmpty(parentOrgan)){
                        parentId = parentOrgan.getId();
                    }
                }

                OrgOrganModel unit = new OrgOrganModel();
                unit.setName(unitName);
                unit.setType(CommonConstant.ORGAN_TYPE.UNIT.getValue());
                String organType =sysDictionaryService.getDictValByNameAndCode("cloud_organBusinessType",dto.getOrganBusinessType());
                if(StrUtil.isNotBlank(organType)){
                    unit.setOrganBusinessType(organType);
                }else {
                    unit.setOrganBusinessType("8"); // 数据字典：默认连级
                }
                unit.setUnitAdminAccount(dto.getUnitAdminAccount());
                unit.setParentId(parentId);
                unit.setDirectUnitId(parentId);
                unit.setRemark(dto.getRemark());
                unit.setStatus(CommonConstant.USE_STATUS.USE.getValue());
                Result<OrgOrganModel> result = this.saveUnit(unit);
                if (HttpStatus.OK.value() != result.getCode()) {
                    errorMassage.append("[" + lineNum + "单位名称：" + unitName + "]导入失败");
                } else {
                    importCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + lineNum + "单位名称：" + unitName + "]导入失败，出错信息：" + e.getMessage() + "。");
                errorMassage.append("[" + lineNum + "单位名称：" + unitName + "]导入失败，出错信息：" + e.getMessage() + "。");
            }
        }
        if (importCount > 0) {
            if (importCount < unitDtoList.size()) {
                return new ExcelImportResultDto(true, "单位导入成功。" + errorMassage.toString(), null);
            } else {
                return new ExcelImportResultDto(true, "单位导入成功。", null);
            }
        } else {
            return new ExcelImportResultDto(false, "单位导入失败。" + errorMassage.toString(), null);
        }
    }

}
