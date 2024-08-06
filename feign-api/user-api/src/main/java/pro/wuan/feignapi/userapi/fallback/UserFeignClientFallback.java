package pro.wuan.feignapi.userapi.fallback;


import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.feignapi.userapi.entity.*;
import pro.wuan.feignapi.userapi.feign.UserFeignClient;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.feignapi.userapi.vo.UnitUserNumVo;
import pro.wuan.feignapi.userapi.vo.UpdateUserOrganAndRoleVo;
import pro.wuan.feignapi.userapi.vo.UserBaseInfoUpdateVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zf
 * @date: 2021-9-6 15:17
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public OrgUserModel selectById(Long id) {
        return null;
    }

    @Override
    public List<OrgUserModel> selectBatchByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return
     */
    public OrgUserModel saveUser(@RequestBody OrgUserModel user) {
        return null;
    }

    /**
     * 保存用户信息test
     *
     * @param user 用户信息
     * @return
     */
    public OrgUserTsetModel saveUserTset(@RequestBody OrgUserTsetModel user) {
        return null;
    }

    /**
     * 保存用户信息test
     *
     * @param user 用户信息
     * @return
     */
    public String saveUserTsetReturnId(@RequestBody OrgUserTsetModel user) {
        return null;
    }

    /**
     * 更新用户基本信息
     *
     * @param userBaseInfo 用户基本信息更新vo
     * @return
     */
    public OrgUserModel updateUserBaseInfo(@RequestBody UserBaseInfoUpdateVo userBaseInfo) {
        return null;
    }

    /**
     * 更新用户基本信息
     *
     * @param userBaseInfo 用户基本信息更新vo
     * @return
     */
    public boolean updateUserBaseInfoTest(@RequestBody UserBaseInfoUpdateVo userBaseInfo) {
        return false;
    }

    /**
     * 更新用户所属的组织机构信息及关联的角色
     *
     * @param vo 更新用户所属的组织机构信息及关联的角色vo
     * @return
     */
    public boolean updateUserOrganAndRole(@RequestBody UpdateUserOrganAndRoleVo vo) {
        return false;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param loginName 用户名
     * @return
     */
    public UserDetails selectByLoginName(@RequestParam(value = "loginName") String loginName) {
        return null;
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return
     */
    public int deleteUser(@RequestParam("id") Long id) {
        return 0;
    }

    /**
     * 根据id获取用户信息
     *
     * @param id 主键
     * @return
     */
    public OrgUserModel getUserById(@RequestParam("id") Long id) {
        return null;
    }

    /**
     * 根据应用编码获取用户列表（应用-角色-用户）
     *
     * @param appCode 应用编码
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByApp(@RequestParam("appCode") String appCode) {
        return new ArrayList<>();
    }

    /**
     * 根据应用编码获取角色列表
     *
     * @param appCode 应用编码
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByApp(@RequestParam("appCode") String appCode) {
        return new ArrayList<>();
    }

    /**
     * 根据元素列表获取关联的用户列表
     *
     * @param elementList 元素列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByElementList(@RequestBody List<OrgElementModel> elementList) {
        return new ArrayList<>();
    }

    /**
     * 根据用户id列表获取用户列表
     *
     * @param userIdList 用户id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByUserIdList(@RequestBody List<Long> userIdList) {
        return new ArrayList<>();
    }

    /**
     * 根据角色id列表获取角色关联的用户列表
     *
     * @param roleIdList 角色id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByRoleIdList(@RequestBody List<Long> roleIdList) {
        return new ArrayList<>();
    }

    /**
     * 根据岗位id列表获取岗位关联的用户列表
     *
     * @param postIdList 岗位id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByPostIdList(@RequestBody List<Long> postIdList) {
        return new ArrayList<>();
    }

    /**
     * 根据当前登录用户获取所属部门负责人
     *
     * @return 所属部门负责人
     */
    public OrgUserModel getDepartmentHeadByCurrentUser() {
        return null;
    }

    /**
     * 根据当前登录用户获取直属上级
     *
     * @return 直属上级
     */
    public OrgUserModel getDirectLeaderByCurrentUser() {
        return null;
    }

    /**
     * 根据当前登录用户获取连续多级主管列表
     * 例如：参数为2，返回当前登录用户的直属上级，直属上级的直属上级
     *
     * @param level 级别
     * @return 连续多级主管列表
     */
    public List<OrgUserModel> getLeaderListByCurrentUser(@RequestParam("level") int level) {
        return new ArrayList<>();
    }

    /**
     * 统计有效用户数量
     *
     * @return 有效用户数量
     */
    public Integer getValidUserNumber() {
        return null;
    }

    /**
     * 分页查询有效用户
     *
     * @param pageSearchParam 分页查询内容
     * @return 有效用户实体分页
     */
    public List<OrgUserModel> selectUserPage(@RequestBody PageSearchParam pageSearchParam) {
        return new ArrayList<>();
    }

    /**
     * 查询组织机构下的所有有效用户-不包含超管和单位管理员
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    public List<OrgUserModel> getUserListByOrganId(@RequestParam("organId") Long organId, @RequestParam("containSubUnit") boolean containSubUnit) {
        return new ArrayList<>();
    }

    /**
     * 查询组织机构下的所有有效用户-提出自动创建的单位管理员和超管
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    @Override
    public List<OrgUserModel> getUserListByOrganIdRemoveUnitAdmin(@RequestParam("organId") Long organId, @RequestParam("containSubUnit") boolean containSubUnit) {
        return new ArrayList<>();
    }

    @Override
    public List<UnitUserNumVo> getUnitUserNum(List<Long> unitIds) {
        return new ArrayList<>();
    }

    @Override
    public Integer getAllUnitUserNum(List<Long> unitIds) {
        return 0;
    }

    @Override
    public List<OrgUserRoleModel> getUserRoleList(List<Long> organIds, List<String> roleNameList) {
        return new ArrayList<>();
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    @Override
    public Boolean updateUserInfo(@RequestBody OrgUserModel user) {
        return false;
    }

    /**
     * 获取上级单位到最顶级
     *
     * @param organId
     * @return
     */
    @GetMapping("/getParentUnits")
    public List<OrgOrganModel> getParentUnits(@RequestParam("organId") Long organId) {
        return new ArrayList<>();
    }

    /**
     * 根据角色获取用户列表
     *
     * @param organIds     机构id集合
     * @param roleNameList 角色名称集合
     * @return
     */
    @Override
    public List<OrgUserModel> getUserListByRole(@RequestParam("organIds") List<Long> organIds, @RequestParam("roleNameList") List<String> roleNameList) {
        return new ArrayList<>();
    }

    /**
     * 更新同步样本状态
     *
     * @param unitId
     * @param state
     * @return
     */
    @Override
    public Boolean updateThStateByUnitId(@RequestParam("unitId") Long unitId, @RequestParam("state") String state) {
        return false;
    }

    /**
     * 批量更新用户信息
     *
     * @param users 用户信息
     * @return
     */
    @Override
    public boolean bacthUpdate(@RequestBody List<OrgUserModel> users){
        return false;
    }

    /**
     * 根据军人证件号获取用户信息
     *
     * @param attrString02 军人证件号
     * @return
     */
    @Override
    public OrgUserModel getUserByAttrString02(@RequestParam("attrString02") String attrString02) {
        return null;
    }


    /**
     * feign调用-更新用户角色信息
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    @Override
    public Result updateUserRole(@RequestBody OrgUserModel userModel) {
       return null;
    }

    /**
     * 通过用户id获取角色id列表
     *
     * @param id 用户id
     * @return
     */
    @Override
    public Result getRoleIdListByUserId(@RequestParam("id") Long id) {
        return null;
    }

    /**
     * 获取用户所属数据库模式
     *
     * @param organId 用户单位id
     * @return
     */
    @Override
    public String getOrganDabaseModel(Long organId) {
        return null;
    }

    /**
     * 获取所有配置的数据库模式
     *
     * @return
     */
    @Override
    public List<String> getAllDabaseMode() {
        return null;
    }


}
