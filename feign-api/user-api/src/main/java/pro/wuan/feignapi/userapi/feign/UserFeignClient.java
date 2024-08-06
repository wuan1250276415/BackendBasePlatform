package pro.wuan.feignapi.userapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;
import pro.wuan.feignapi.userapi.entity.*;
import pro.wuan.feignapi.userapi.fallback.UserFeignClientFallback;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.feignapi.userapi.vo.UnitUserNumVo;
import pro.wuan.feignapi.userapi.vo.UpdateUserOrganAndRoleVo;
import pro.wuan.feignapi.userapi.vo.UserBaseInfoUpdateVo;

import java.util.List;

/**
 * @description:
 * @author: zf
 * @date: 2021-9-6 15:16
 */
@FeignClient(
        value = AppConstant.APPLICATION_SYS_NAME,
        fallback = UserFeignClientFallback.class,
        path = "/feign/user"
)
public interface UserFeignClient extends IBaseFeignQuerySerivce<OrgUserModel> {

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return
     */
    @PostMapping("/saveUser")
    OrgUserModel saveUser(@RequestBody OrgUserModel user);

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return
     */
    @PostMapping("/saveUserTset")
    OrgUserTsetModel saveUserTset(@RequestBody OrgUserTsetModel user);

    /**
     * 保存用户信息并且返回userid
     *
     * @param user 用户信息
     * @return
     */
    @PostMapping("/saveUserTsetReturnId")
    String saveUserTsetReturnId(@RequestBody OrgUserTsetModel user);

    /**
     * 更新用户基本信息
     *
     * @param userBaseInfo 用户基本信息更新vo
     * @return
     */
    @PostMapping("/updateUserBaseInfo")
    OrgUserModel updateUserBaseInfo(@RequestBody UserBaseInfoUpdateVo userBaseInfo);

    /**
     * 更新用户基本信息
     *
     * @param userBaseInfo 用户基本信息更新vo
     * @return
     */
    @PostMapping("/updateUserBaseInfoTest")
    boolean updateUserBaseInfoTest(@RequestBody UserBaseInfoUpdateVo userBaseInfo);

    /**
     * 更新用户所属的组织机构信息及关联的角色
     *
     * @param vo 更新用户所属的组织机构信息及关联的角色vo
     * @return
     */
    @PostMapping("/updateUserOrganAndRole")
    boolean updateUserOrganAndRole(@RequestBody UpdateUserOrganAndRoleVo vo);

    /**
     * 根据用户名获取用户信息
     *
     * @param loginName 用户名
     * @return
     */
    @GetMapping("/selectByLoginName")
    UserDetails selectByLoginName(@RequestParam(value = "loginName") String loginName);

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return
     */
    @GetMapping("/deleteUser")
    int deleteUser(@RequestParam("id") Long id);

    /**
     * 根据id获取用户信息
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/getUserById")
    OrgUserModel getUserById(@RequestParam("id") Long id);

    /**
     * 根据应用编码获取用户列表（应用-角色-用户）
     *
     * @param appCode 应用编码
     * @return 用户列表
     */
    @GetMapping("/getUserListByApp")
    List<OrgUserModel> getUserListByApp(@RequestParam("appCode") String appCode);

    /**
     * 根据应用编码获取角色列表
     *
     * @param appCode 应用编码
     * @return 角色列表
     */
    @GetMapping("/getRoleListByApp")
    List<OrgRoleModel> getRoleListByApp(@RequestParam("appCode") String appCode);

    /**
     * 根据元素列表获取关联的用户列表
     *
     * @param elementList 元素列表
     * @return 用户列表
     */
    @PostMapping("/getUserListByElementList")
    List<OrgUserModel> getUserListByElementList(@RequestBody List<OrgElementModel> elementList);

    /**
     * 根据用户id列表获取用户列表
     *
     * @param userIdList 用户id列表
     * @return 用户列表
     */
    @PostMapping("/getUserListByUserIdList")
    List<OrgUserModel> getUserListByUserIdList(@RequestBody List<Long> userIdList);

    /**
     * 根据角色id列表获取角色关联的用户列表
     *
     * @param roleIdList 角色id列表
     * @return 用户列表
     */
    @PostMapping("/getUserListByRoleIdList")
    List<OrgUserModel> getUserListByRoleIdList(@RequestBody List<Long> roleIdList);

    /**
     * 根据岗位id列表获取岗位关联的用户列表
     *
     * @param postIdList 岗位id列表
     * @return 用户列表
     */
    @PostMapping("/getUserListByPostIdList")
    List<OrgUserModel> getUserListByPostIdList(@RequestBody List<Long> postIdList);

    /**
     * 根据当前登录用户获取所属部门负责人
     *
     * @return 所属部门负责人
     */
    @GetMapping("/getDepartmentHeadByCurrentUser")
    OrgUserModel getDepartmentHeadByCurrentUser();

    /**
     * 根据当前登录用户获取直属上级
     *
     * @return 直属上级
     */
    @GetMapping("/getDirectLeaderByCurrentUser")
    OrgUserModel getDirectLeaderByCurrentUser();

    /**
     * 根据当前登录用户获取连续多级主管列表
     * 例如：参数为2，返回当前登录用户的直属上级，直属上级的直属上级
     *
     * @param level 级别
     * @return 连续多级主管列表
     */
    @GetMapping("/getLeaderListByCurrentUser")
    List<OrgUserModel> getLeaderListByCurrentUser(@RequestParam("level") int level);

    /**
     * 统计有效用户数量
     *
     * @return 有效用户数量
     */
    @GetMapping("/getValidUserNumber")
    Integer getValidUserNumber();

    /**
     * 分页查询有效用户
     *
     * @param pageSearchParam 分页查询内容
     * @return 有效用户实体分页
     */
    @PostMapping(value = "/selectUserPage")
    List<OrgUserModel> selectUserPage(@RequestBody PageSearchParam pageSearchParam);

    /**
     * 查询组织机构下的所有有效用户-不包含超管和单位管理员
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    @GetMapping(value = "/getUserListByOrganId")
    List<OrgUserModel> getUserListByOrganId(@RequestParam("organId") Long organId, @RequestParam("containSubUnit") boolean containSubUnit);

    /**
     * 查询组织机构下的所有有效用户-剔除自动创建的单位管理员和超管
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    @GetMapping(value = "/getUserListByOrganIdRemoveUnitAdmin",produces = { "application/json;charset=UTF-8"})
    List<OrgUserModel> getUserListByOrganIdRemoveUnitAdmin(@RequestParam("organId") Long organId, @RequestParam("containSubUnit") boolean containSubUnit);

    /**
     * 查询各单位有效用户数量（每个单位id不包含下级人员）
     *
     * @return 有效用户实体列表
     */
    @GetMapping(value = "/getUnitUserNum")
    List<UnitUserNumVo> getUnitUserNum(@RequestParam("unitIds") List<Long> unitIds);
    /**
     * 查询各单位有效用户数量（每个单位id不包含下级人员）
     *
     * @return 有效用户实体列表
     */
    @GetMapping(value = "/getAllUnitUserNum")
    Integer getAllUnitUserNum(@RequestParam("unitIds") List<Long> unitIds);


    /**
     * 根据单位id集合和角色名称集合获取用户角色列表
     *
     * @param organIds     机构id集合
     * @param roleNameList 角色名称集合
     * @return
     */
    @GetMapping("/getUserRoleList")
    List<OrgUserRoleModel> getUserRoleList(@RequestParam("organIds") List<Long> organIds, @RequestParam("roleNameList") List<String> roleNameList);

    /**
     * 更新用户信息
     *
     * @param user
     */
    @PostMapping("/updateUserInfo")
    Boolean updateUserInfo(@RequestBody OrgUserModel user);

    /**
     * 获取上级单位到最顶级
     *
     * @param organId
     * @return
     */
    @GetMapping("/getParentUnits")
    List<OrgOrganModel> getParentUnits(@RequestParam("organId") Long organId);

    /**
     * 根据角色获取用户列表
     *
     * @param organIds     机构id集合
     * @param roleNameList 角色名称集合
     * @return
     */
    @GetMapping("/getUserListByRole")
    public List<OrgUserModel> getUserListByRole(@RequestParam("organIds") List<Long> organIds, @RequestParam("roleNameList") List<String> roleNameList);

    /**
     * 更新同步样本状态
     * @param unitId
     * @param state
     * @return
     */
    @GetMapping("/updateThState")
    public Boolean updateThStateByUnitId(@RequestParam("unitId") Long unitId,@RequestParam("state")String state);

    /**
     * 批量更新用户信息
     *
     * @param users 用户信息
     * @return
     */
    @PostMapping("/bacthUpdate")
    public boolean bacthUpdate(@RequestBody List<OrgUserModel> users);

    /**
     * 根据军人证件号获取用户信息
     *
     * @param attrString02 军人证件号
     * @return
     */
    @GetMapping("/getUserByAttrString02")
    OrgUserModel getUserByAttrString02(@RequestParam("attrString02") String attrString02);


    /**
     * feign调用-更新用户角色信息
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    @PostMapping(value = "/updateUserRole",produces = { "application/json;charset=UTF-8"})
    Result updateUserRole(@RequestBody OrgUserModel userModel);

    /**
     * 通过用户id获取角色id列表
     *
     * @param id 用户id
     * @return
     */
    @GetMapping("/getRoleIdList")
    public Result getRoleIdListByUserId(@RequestParam("id") Long id);

    /**
     * 获取用户所属数据库模式
     *
     * @param organId 用户单位id
     * @return
     */
    @GetMapping("/getOrganDabaseModel")
    String getOrganDabaseModel(@RequestParam(value = "organId") Long organId);

    /**
     * 获取所有配置的数据库模式
     *
     * @return
     */
    @GetMapping("/getAllDabaseMode")
    List<String> getAllDabaseMode();
}
