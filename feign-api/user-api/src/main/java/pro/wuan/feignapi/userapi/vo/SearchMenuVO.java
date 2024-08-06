package pro.wuan.feignapi.userapi.vo;

import lombok.Data;
import pro.wuan.common.core.annotation.EnumFiled;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.IDModel;

/**
 * 搜索菜单VO
 *
 * @author: liumin
 * @date: 2021/9/7 15:58
 */
@Data
public class SearchMenuVO extends IDModel<SearchMenuVO> {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 菜单类型
     */
    private String type;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 地址
     */
    private String url;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 应用地址
     */
    private String appUrl;

    /**
     * 应用备注
     */
    private String appRemark;

    /**
     * 应用启用状态。枚举：0-停用，1-启用
     */
    @EnumFiled(type = CommonConstant.USE_STATUS.class, targetProperty = "appStatusName")
    private Integer appStatus;

    private String appStatusName;

}
