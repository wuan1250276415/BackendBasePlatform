package pro.wuan.element.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.db.convertor.db2page.DbConvert2Page;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.element.mapper.OrgElementMapper;
import pro.wuan.element.service.IOrgElementService;
import pro.wuan.feignapi.userapi.entity.OrgElementModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.user.mapper.OrgUserMapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 元素
 *
 * @author: liumin
 * @date: 2021-08-30 09:49:57
 */
@Validated
@Service("orgElementService")
public class OrgElementServiceImpl extends BaseServiceImpl<OrgElementMapper, OrgElementModel> implements IOrgElementService {

    @Resource
    private OrgUserMapper userMapper;

    /**
     * 获取直接下级元素列表（动态加载）
     *
     * @param elementId       元素id列表，不传值时返回顶级元素列表
     * @param leafElementType 叶子元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色
     * @return 直接下级元素列表
     */
    public List<OrgElementModel> getDirectSubElementList(Long elementId, @NotBlank(message = "叶子元素类型不能为空") String leafElementType) {
        CommonConstant.ELEMENT_TYPE leafElementTypeEnum = CommonConstant.ELEMENT_TYPE.parse(leafElementType);
        OrgElementModel element;
        if (elementId == null) {
            elementId = CommonConstant.TREE_ROOT_ID;
            element = null;
        } else {
            element = this.selectById(elementId);
            this.convertElementModel(element);
        }
        // 获取直接下级元素类型列表
        List<String> elementTypeList = this.getDirectSubElementTypeList(element, elementId, leafElementTypeEnum);
        // 获取直接下级元素列表
        List<OrgElementModel> subElementList = this.getDirectSubElementList(elementId, elementTypeList);
        if (CollectionUtil.isEmpty(subElementList)) {
            return new ArrayList<>();
        }
        this.convertElementList(subElementList);
        // 循环设置直接下级元素是否存在下级元素
        for (OrgElementModel subElement : subElementList) {
            List<String> subElementTypeList = this.getDirectSubElementTypeList(subElement, subElement.getId(), leafElementTypeEnum);
            // 判断元素是否存在下级元素
            boolean haveChild = this.haveChild(subElement.getId(), subElementTypeList);
            subElement.setHaveChild(haveChild);
        }
        DbConvert2Page dbConvert2Page = new DbConvert2Page();
        dbConvert2Page.executeDbToPage4List(subElementList);
        return subElementList;
    }

    private void convertElementModel(OrgElementModel element) {
        if (element == null) {
            return;
        }
        if (!CommonConstant.ELEMENT_TYPE.USER.getValue().equals(element.getType())) {
            return;
        }
        OrgUserModel user = userMapper.selectById(element.getId());
        if (user != null) {
            element.setUserType(user.getAttrString05());
            element.setZw(user.getAttrString03());
            element.setJx(user.getAttrString04());
            element.setAttrString02(user.getAttrString02());
            //todo 为什么org_element的id会是org_user的id
            element.setSex(user.getSex());
            element.setSexName(user.getSexName());
            element.setIdCardNo(user.getIdCardNo());
            element.setHeadImageUrl(user.getHeadImageUrl());
            element.setTel(user.getTel());
            element.setOrganId(user.getOrganId());
            element.setOrganName(user.getOrganName());
        }
    }

    private void convertElementList(List<OrgElementModel> elements) {
        if (CollectionUtil.isEmpty(elements)) {
            return;
        }

        List<Long> ids = elements.stream().filter(element -> CommonConstant.ELEMENT_TYPE.USER.getValue().equals(element.getType()))
                .map(m -> m.getId()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(ids)) {
            List<OrgUserModel> users = userMapper.selectBatchIds(ids);
            if (CollectionUtil.isNotEmpty(users)) {
                elements.stream().forEach(y -> {
                    OrgUserModel user = users.stream().filter(f -> f.getId().equals(y.getId())).findFirst().orElse(null);
                    if (user != null) {
                        y.setUserType(user.getAttrString05());
                        y.setZw(user.getAttrString03());
                        y.setJx(user.getAttrString04());
                        y.setAttrString02(user.getAttrString02());
                        //todo 为什么org_element的id会是org_user的id
                        y.setSex(user.getSex());
                        y.setSexName(user.getSexName());
                        y.setIdCardNo(user.getIdCardNo());
                        y.setHeadImageUrl(user.getHeadImageUrl());
                        y.setTel(user.getTel());
                        y.setOrganId(user.getOrganId());
                        y.setOrganName(user.getOrganName());
                    }
                });
            }
        }
    }


    /**
     * 获取直接下级元素类型列表
     *
     * @param element
     * @param elementId
     * @param leafElementTypeEnum
     * @return
     */
    private List<String> getDirectSubElementTypeList(OrgElementModel element, Long elementId, CommonConstant.ELEMENT_TYPE leafElementTypeEnum) {
        if (elementId == CommonConstant.TREE_ROOT_ID) {
            return Arrays.asList(CommonConstant.ELEMENT_TYPE.UNIT.getValue());
        }
        if (element == null) {
            return null;
        }
        CommonConstant.ELEMENT_TYPE elementType = CommonConstant.ELEMENT_TYPE.parse(element.getType());
        switch (elementType) {
            case UNIT:
                return new ArrayList<String>() {{
                    add(CommonConstant.ELEMENT_TYPE.UNIT.getValue());
                    switch (leafElementTypeEnum) {
                        case UNIT:
                            break;
                        case DEPT:
                            add(CommonConstant.ELEMENT_TYPE.DEPT.getValue());
                            break;
                        case POST:
                            add(CommonConstant.ELEMENT_TYPE.DEPT.getValue());
                            add(CommonConstant.ELEMENT_TYPE.POST.getValue());
                            break;
                        case USER:
                            add(CommonConstant.ELEMENT_TYPE.DEPT.getValue());
                            add(CommonConstant.ELEMENT_TYPE.USER.getValue());
                            break;
                        case ROLE:
                            add(CommonConstant.ELEMENT_TYPE.DEPT.getValue());
                            add(CommonConstant.ELEMENT_TYPE.ROLE.getValue());
                            break;
                    }
                }};
            case DEPT:
                return new ArrayList<String>() {{
                    add(CommonConstant.ELEMENT_TYPE.DEPT.getValue());
                    switch (leafElementTypeEnum) {
                        case UNIT:
                            break;
                        case DEPT:
                            break;
                        case POST:
                            add(CommonConstant.ELEMENT_TYPE.POST.getValue());
                            break;
                        case USER:
                            add(CommonConstant.ELEMENT_TYPE.USER.getValue());
                            break;
                        case ROLE:
                            add(CommonConstant.ELEMENT_TYPE.ROLE.getValue());
                            break;
                    }
                }};
            default:
                return null;
        }
    }

    /**
     * 获取直接下级元素列表
     *
     * @param elementId
     * @param elementTypeList
     * @return
     */
    private List<OrgElementModel> getDirectSubElementList(Long elementId, List<String> elementTypeList) {
        if (CollectionUtil.isEmpty(elementTypeList)) {
            return null;
        }
        LambdaQueryWrapper<OrgElementModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgElementModel::getParentId, elementId);
        wrapper.in(OrgElementModel::getType, elementTypeList);
        wrapper.eq(OrgElementModel::getStatus, CommonConstant.USE_STATUS.USE.getValue());
        wrapper.orderByAsc(OrgElementModel::getSort);
        return this.selectList(wrapper);
    }

    /**
     * 判断元素是否存在下级元素
     *
     * @param elementId
     * @param elementTypeList
     * @return
     */
    private boolean haveChild(Long elementId, List<String> elementTypeList) {
        if (CollectionUtil.isEmpty(elementTypeList)) {
            return false;
        }
        LambdaQueryWrapper<OrgElementModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgElementModel::getParentId, elementId);
        wrapper.in(OrgElementModel::getType, elementTypeList);
        wrapper.eq(OrgElementModel::getStatus, CommonConstant.USE_STATUS.USE.getValue());
        Long count = this.selectCount(wrapper);
        return count > 0;
    }

    /**
     * 查询元素列表
     *
     * @param elementName 元素名称
     * @param elementType 元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色
     * @param elementId   元素id，不传值时忽略该参数
     * @return 元素列表
     */
    public List<OrgElementModel> queryElementList(@NotBlank(message = "元素名称不能为空") String elementName,
                                                  @NotBlank(message = "元素类型不能为空") String elementType, Long elementId) {
        LambdaQueryWrapper<OrgElementModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(OrgElementModel::getName, elementName);
        wrapper.eq(OrgElementModel::getType, elementType);
        wrapper.eq(OrgElementModel::getStatus, CommonConstant.USE_STATUS.USE.getValue());
        if (elementId != null) {
            wrapper.like(OrgElementModel::getTreeIds, elementId);
        }
        wrapper.orderByAsc(OrgElementModel::getName, OrgElementModel::getParentId);
        List<OrgElementModel> elementList = this.selectList(wrapper);
        if (CollectionUtil.isEmpty(elementList)) {
            return new ArrayList<>();
        }
        // 设置父id集合
        Set<Long> parentIdSet = new HashSet<>();
        for (OrgElementModel element : elementList) {
            parentIdSet.add(element.getParentId());
        }
        // 组装数据
        LambdaQueryWrapper<OrgElementModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrgElementModel::getId, parentIdSet);
        List<OrgElementModel> parentElementList = this.selectList(queryWrapper);
        for (OrgElementModel element : elementList) {
            element.setParentName(this.getParentName(element.getParentId(), parentElementList));
        }
        this.convertElementList(elementList);
        DbConvert2Page dbConvert2Page = new DbConvert2Page();
        dbConvert2Page.executeDbToPage4List(elementList);
        return elementList;
    }

    /**
     * 获取父名称
     *
     * @param parentId
     * @param parentElementList
     * @return
     */
    private String getParentName(Long parentId, List<OrgElementModel> parentElementList) {
        String blank = CommonConstant.STRING_BLANK;
        if (parentId == null) {
            return blank;
        }
        if (parentId.equals(0)) {
            return CommonConstant.TREE_ROOT_NAME;
        }
        if (CollectionUtil.isEmpty(parentElementList)) {
            return blank;
        }
        for (OrgElementModel parentElement : parentElementList) {
            if (parentId.equals(parentElement.getId())) {
                return parentElement.getName();
            }
        }
        return blank;
    }

    /**
     * 删除元素
     *
     * @param elementId 元素id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteElement(Long elementId) {
        LambdaQueryWrapper<OrgElementModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgElementModel::getId, elementId);
        return this.delete(wrapper);
    }

}
