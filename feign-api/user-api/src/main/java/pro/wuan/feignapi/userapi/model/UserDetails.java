package pro.wuan.feignapi.userapi.model;

import lombok.Data;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.userapi.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @description: 用户详细信息
 * @author: oldone
 * @date: 2020/7/29 14:52
 */
@Data
public class UserDetails {

    /**
     * 用户名
     */
    private String account;

    /**
     * 令牌信息
     */
    private String token;

    /**
     * 用户信息
     */
    private OrgUserModel user;

    /**
     * 所属部门
     */
    private OrgOrganModel department;

    /**
     * 所属单位
     */
    private OrgOrganModel unit;

    /**
     * 应用列表
     */
    private List<OrgAppModel> appList;

    /**
     * 岗位列表
     */
    private List<OrgPostModel> postList;

    /**
     * 角色列表
     */
    private List<OrgRoleModel> roleList;

    /**
     * 菜单/目录列表
     */
    private List<OrgBusinessResourceModel> menuList;

    /**
     * 功能集合（例如：按钮）
     */
    private List<OrgBusinessResourceModel> functionList;

    /**
     * 列表字段集合
     */
    private List<OrgListColumnModel> listColumnList;

    /**
     * 数据方案集合
     */
    private List<OrgDataSchemeModel> dataSchemeList;

    /**
     * 可访问路径集合
     */
    private Set<String> urls;


    /**
     * 用户设置信息
     */
    private UserSet userSet;


    /**
     * 系统nginx地址，用于拼接用户头像地址显示
     */
    private String nginxPath;

    /**
     * 直接下属
     */
    private List<OrgUserModel>  directUnderlingUsers = new ArrayList<>();

    /**
     * 直接下属ids
     */
    private List<Long> directUnderlingUserIds = new ArrayList<>();

    /**
     * 间接下属
     */
    private List<OrgUserModel> indirectUnderlingUsers = new ArrayList<>();

    /**
     * 间接下属ids
     */
    private List<Long> indirectUnderlingUserIds = new ArrayList<>();

}
