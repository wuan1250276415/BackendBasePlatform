package pro.wuan.element.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.utils.ConvertUtil;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.element.mapper.OrgElementMapper;
import pro.wuan.element.service.IOrgElementService;
import pro.wuan.element.vo.ElementQueryVO;
import pro.wuan.feignapi.userapi.entity.OrgElementModel;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;

import java.util.List;

/**
 * 元素
 *
 * @author: liumin
 * @date: 2021-08-30 09:49:57
 */
@RestController
@RequestMapping("/org/element")
public class OrgElementController extends CURDController<IOrgElementService, OrgElementMapper, OrgElementModel> {

    /**
     * 获取直接下级元素列表（动态加载）
     *
     * @param elementId 元素id，不传值时返回顶级元素列表
     * @param leafElementType 叶子元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色
     * @return 直接下级元素列表
     */
    @AspectLog(description = "获取直接下级元素列表（动态加载）", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getDirectSubElementList")
    public Result<List<OrgElementModel>> getDirectSubElementList(Long elementId, String leafElementType) {
        try {
            return Result.success(service.getDirectSubElementList(elementId, leafElementType));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 查询元素列表。包括参数：elementName（元素名称），elementType（元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色）
     *
     * @param searchParam 查询参数不能为空
     * @return 元素列表
     */
    @AspectLog(description = "查询元素列表", type = OperationType.OPERATION_QUERY)
    @PostMapping("/queryElementList")
    public Result<List<OrgElementModel>> queryElementList(@RequestBody BaseSearchParam searchParam) {
        try {
            if (searchParam == null || CollectionUtil.isEmpty(searchParam.getQueryPrams())) {
                return Result.failure("查询参数不能为空");
            }
            ElementQueryVO elementQueryVO = ConvertUtil.queryMapToObject(searchParam.getQueryPrams(), ElementQueryVO.class);
            return Result.success(service.queryElementList(elementQueryVO.getElementName(), elementQueryVO.getElementType(), null));
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 根据当前用户获取直接下级元素列表（动态加载）
     *
     * @param elementId 元素id，不传值时返回当前用户的顶级元素列表
     * @param leafElementType 叶子元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色
     * @return 直接下级元素列表
     */
    @AspectLog(description = "根据当前用户获取直接下级元素列表（动态加载）", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getDirectSubElementListByCurrentUser")
    public Result<List<OrgElementModel>> getDirectSubElementListByCurrentUser(Long elementId, String leafElementType) {
        try {
            if (elementId == null) {
                UserDetails userDetails = UserContext.getCurrentUser();
                OrgUserModel user = userDetails.getUser();
                if (user == null) {
                    return Result.failure("当前用户信息为空");
                }
                if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
                    return this.getDirectSubElementList(elementId, leafElementType);
                } else {
                    OrgOrganModel unit = userDetails.getUnit();
                    if (unit == null) {
                        return Result.failure("当前用户所属单位为空");
                    }
                    return this.getDirectSubElementList(unit.getId(), leafElementType);
                }
            } else {
                return this.getDirectSubElementList(elementId, leafElementType);
            }
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 根据当前用户查询元素列表。包括参数：elementName（元素名称），elementType（元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色）
     *
     * @param searchParam 查询参数不能为空
     * @return 元素列表
     */
    @AspectLog(description = "根据当前用户查询元素列表", type = OperationType.OPERATION_QUERY)
    @PostMapping("/queryElementListByCurrentUser")
    public Result<List<OrgElementModel>> queryElementListByCurrentUser(@RequestBody BaseSearchParam searchParam) {
        try {
            if (searchParam == null || CollectionUtil.isEmpty(searchParam.getQueryPrams())) {
                return Result.failure("查询参数不能为空");
            }
            ElementQueryVO elementQueryVO = ConvertUtil.queryMapToObject(searchParam.getQueryPrams(), ElementQueryVO.class);
            UserDetails userDetails = UserContext.getCurrentUser();
            OrgUserModel user = userDetails.getUser();
            if (user == null) {
                return Result.failure("当前用户信息为空");
            }
            if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
                return Result.success(service.queryElementList(elementQueryVO.getElementName(), elementQueryVO.getElementType(), null));
            } else {
                OrgOrganModel unit = userDetails.getUnit();
                if (unit == null) {
                    return Result.failure("当前用户所属单位为空");
                }
                return Result.success(service.queryElementList(elementQueryVO.getElementName(), elementQueryVO.getElementType(), unit.getId()));
            }
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

}
