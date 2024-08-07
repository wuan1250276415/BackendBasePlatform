package pro.wuan.element.service;

import jakarta.validation.constraints.NotBlank;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.element.mapper.OrgElementMapper;
import pro.wuan.feignapi.userapi.entity.OrgElementModel;

import java.util.List;

/**
 * 元素
 *
 * @author: liumin
 * @date: 2021-08-30 09:49:57
 */
public interface IOrgElementService extends IBaseService<OrgElementMapper, OrgElementModel> {

    /**
     * 获取直接下级元素列表（动态加载）
     *
     * @param elementId 元素id，不传值时返回顶级元素列表
     * @param leafElementType 叶子元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色
     * @return 直接下级元素列表
     */
    public List<OrgElementModel> getDirectSubElementList(Long elementId, @NotBlank(message = "叶子元素类型不能为空") String leafElementType);

    /**
     * 查询元素列表
     *
     * @param elementName 元素名称
     * @param elementType 元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色
     * @param elementId 元素id，不传值时忽略该参数
     * @return 元素列表
     */
    public List<OrgElementModel> queryElementList(@NotBlank(message = "元素名称不能为空") String elementName,
                                                  @NotBlank(message = "元素类型不能为空") String elementType, Long elementId);

    /**
     * 删除元素
     *
     * @param elementId 元素id
     * @return
     */
    public int deleteElement(Long elementId);

}