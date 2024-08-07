package pro.wuan.user.feign;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.constant.HttpStatus;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.userapi.entity.*;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.feignapi.userapi.vo.UnitUserNumVo;
import pro.wuan.feignapi.userapi.vo.UpdateUserOrganAndRoleVo;
import pro.wuan.feignapi.userapi.vo.UserBaseInfoUpdateVo;
import pro.wuan.organmodelsetting.service.IOrgOrganModelSettingService;
import pro.wuan.role.service.IOrgRoleService;
import pro.wuan.user.mapper.OrgUserMapper;
import pro.wuan.user.service.IOrgUserRoleService;
import pro.wuan.user.service.IOrgUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: tellhowcloud
 * @description: 用户信息微服务接口实现类
 * @author: HawkWang
 * @create: 2021-08-27 11:21
 **/
@Slf4j
@RestController
@RequestMapping("/feign/user")
public class OrgUserFeignService extends QueryFeignService<IOrgUserService, OrgUserMapper, OrgUserModel> {

    @Autowired
    private IOrgUserService userService;

    @Autowired
    private IOrgRoleService roleService;

    @Autowired
    private IOrgUserRoleService userRoleService;

    @Autowired
    private IOrgOrganModelSettingService orgOrganModelSettingService;

    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/save")
    public Boolean save(@RequestBody OrgUserModel orgUser) {
        if (Objects.isNull(orgUser)) {
            return false;
        }
        try {
            Result<OrgUserModel> result = userService.saveUser(orgUser);
            if (HttpStatus.OK.value() == result.getCode()) {
                return true;
            }
        } catch (Exception e) {
            log.error("user feign save error:" + ExceptionUtils.getMessage(e));
        }
        return false;
    }

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return
     */
    @PostMapping("/saveUser")
    public OrgUserModel saveUser(@RequestBody OrgUserModel user) {
        OrgUserModel newUser = new OrgUserModel();
        if (Objects.isNull(user)) {
            return newUser;
        }
        try {
            Result<OrgUserModel> result = userService.saveUser(user);
            if (HttpStatus.OK.value() == result.getCode()) {
                return result.getResult();
            } else {
                return newUser;
            }
        } catch (Exception e) {
            log.error("user feign saveUser error:" + ExceptionUtils.getMessage(e));
            return newUser;
        }
    }

    /**
     * 保存用户信息
     *
     * @param orgUserTsetModel 用户信息
     * @return
     */
    @PostMapping(value = "/saveUserTset", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public OrgUserTsetModel saveUserTset(@RequestBody OrgUserTsetModel orgUserTsetModel) {
        OrgUserTsetModel newUser = new OrgUserTsetModel();
        String error;
        if (Objects.isNull(orgUserTsetModel)) {
            error = "接收的用户信息为空！";
            newUser.setError(error);
            return newUser;
        }
        try {
            //复制属性
            OrgUserModel newUser1 = JSONObject.parseObject(JSONObject.toJSONString(orgUserTsetModel), new TypeReference<OrgUserModel>() {
            });
            Result<OrgUserModel> result = null;
            try {
                result = userService.saveUserTset(newUser1);
                if (HttpStatus.OK.value() == result.getCode()) {
                    OrgUserModel userModels = (OrgUserModel) result.getResult();
                    //复制属性
                    OrgUserTsetModel test = JSONObject.parseObject(JSONObject.toJSONString(userModels), new TypeReference<OrgUserTsetModel>() {
                    });
                    return test;
                } else {
                    error = result.getMessage();
                    newUser.setError(error);
                    return newUser;
                }
            } catch (Exception e) {
                error = e.getMessage();
                newUser.setError(error);
            }

        } catch (Exception e) {
            log.error("user feign saveUser error:" + ExceptionUtils.getMessage(e));
            return newUser;
        }
        return newUser;
    }

    /**
     * 更新用户基本信息
     *
     * @param userBaseInfo 用户基本信息更新vo
     * @return
     */
    @PostMapping("/updateUserBaseInfo")
    public boolean updateUserBaseInfo(@RequestBody UserBaseInfoUpdateVo userBaseInfo) {
        try {
            return userService.updateUserBaseInfo(userBaseInfo);
        } catch (Exception e) {
            log.error("user updateUserBaseInfo error:" + e);
            return false;
        }
    }

    /**
     * 更新用户基本信息
     *
     * @param userBaseInfo 用户基本信息更新vo
     * @return
     */
    @PostMapping("/updateUserBaseInfoTest")
    public boolean updateUserBaseInfoTest(@RequestBody UserBaseInfoUpdateVo userBaseInfo) {
        try {
            return userService.updateUserBaseInfo(userBaseInfo);
        } catch (Exception e) {
            log.error("user updateUserBaseInfoTest error:" + e);
            return false;
        }
    }

    /**
     * 更新用户所属的组织机构信息及关联的角色
     *
     * @param vo 更新用户所属的组织机构信息及关联的角色vo
     * @return
     */
    @PostMapping("/updateUserOrganAndRole")
    public boolean updateUserOrganAndRole(@RequestBody UpdateUserOrganAndRoleVo vo) {
        try {
            return userService.updateUserOrganAndRole(vo);
        } catch (Exception e) {
            log.error("user updateUserOrganAndRole error:" + e);
            return false;
        }
    }

    /**
     * 根据登入名查找用户信息
     *
     * @param loginName 登录名称
     * @return
     */
    @GetMapping("/selectByLoginName")
    public UserDetails selectByLoginName(@RequestParam(value = "loginName") String loginName) {
        try {
            return userService.selectByLoginName(loginName);
        } catch (Exception e) {
            log.error("user selectByLoginName error:" + e);
            return null;
        }
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return
     */
    @GetMapping("/deleteUser")
    public int deleteUser(@RequestParam("id") Long id) {
        try {
            return userService.deleteUser(id);
        } catch (Exception e) {
            log.error("user deleteUser error:" + e);
            return 0;
        }
    }

    /**
     * 根据id获取用户信息
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/getUserById")
    public OrgUserModel getUserById(@RequestParam("id") Long id) {
        try {
            return userService.getUserById(id);
        } catch (Exception e) {
            log.error("user getUserById error:" + e);
            return null;
        }
    }

    /**
     * 根据应用编码获取用户列表（应用-角色-用户）
     *
     * @param appCode 应用编码
     * @return 用户列表
     */
    @GetMapping("/getUserListByApp")
    public List<OrgUserModel> getUserListByApp(@RequestParam("appCode") String appCode) {
        try {
            return userService.getUserListByApp(appCode);
        } catch (Exception e) {
            log.error("user getUserListByApp error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据应用编码获取角色列表
     *
     * @param appCode 应用编码
     * @return 角色列表
     */
    @GetMapping("/getRoleListByApp")
    public List<OrgRoleModel> getRoleListByApp(@RequestParam("appCode") String appCode) {
        try {
            return roleService.getRoleListByAppCode(appCode);
        } catch (Exception e) {
            log.error("user getRoleListByApp error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据元素列表获取关联的用户列表
     *
     * @param elementList 元素列表
     * @return 用户列表
     */
    @PostMapping("/getUserListByElementList")
    public List<OrgUserModel> getUserListByElementList(@RequestBody List<OrgElementModel> elementList) {
        try {
            return userService.getUserListByElementList(elementList);
        } catch (Exception e) {
            log.error("user getUserListByElementList error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据用户id列表获取用户列表
     *
     * @param userIdList 用户id列表
     * @return 用户列表
     */
    @PostMapping("/getUserListByUserIdList")
    public List<OrgUserModel> getUserListByUserIdList(@RequestBody List<Long> userIdList) {
        try {
            return userService.getUserListByUserIdList(userIdList);
        } catch (Exception e) {
            log.error("user getUserListByUserIdList error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据角色id列表获取角色关联的用户列表
     *
     * @param roleIdList 角色id列表
     * @return 用户列表
     */
    @PostMapping("/getUserListByRoleIdList")
    public List<OrgUserModel> getUserListByRoleIdList(@RequestBody List<Long> roleIdList) {
        try {
            return userService.getUserListByRoleIdList(roleIdList);
        } catch (Exception e) {
            log.error("user getUserListByRoleIdList error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据岗位id列表获取岗位关联的用户列表
     *
     * @param postIdList 岗位id列表
     * @return 用户列表
     */
    @PostMapping("/getUserListByPostIdList")
    public List<OrgUserModel> getUserListByPostIdList(@RequestBody List<Long> postIdList) {
        try {
            return userService.getUserListByPostIdList(postIdList);
        } catch (Exception e) {
            log.error("user getUserListByPostIdList error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据当前登录用户获取所属部门负责人
     *
     * @return 所属部门负责人
     */
    @GetMapping("/getDepartmentHeadByCurrentUser")
    public OrgUserModel getDepartmentHeadByCurrentUser() {
        try {
            return userService.getDepartmentHeadByCurrentUser();
        } catch (Exception e) {
            log.error("user getDepartmentHeadByCurrentUser error:" + e);
            return null;
        }
    }

    /**
     * 根据当前登录用户获取直属上级
     *
     * @return 直属上级
     */
    @GetMapping("/getDirectLeaderByCurrentUser")
    public OrgUserModel getDirectLeaderByCurrentUser() {
        try {
            return userService.getDirectLeaderByCurrentUser();
        } catch (Exception e) {
            log.error("user getDirectLeaderByCurrentUser error:" + e);
            return null;
        }
    }

    /**
     * 根据当前登录用户获取连续多级主管列表
     * 例如：参数为2，返回当前登录用户的直属上级，直属上级的直属上级
     *
     * @param level 级别
     * @return 连续多级主管列表
     */
    @GetMapping("/getLeaderListByCurrentUser")
    public List<OrgUserModel> getLeaderListByCurrentUser(@RequestParam("level") int level) {
        try {
            return userService.getLeaderListByCurrentUser(level);
        } catch (Exception e) {
            log.error("user getLeaderListByCurrentUser error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 统计有效用户数量
     *
     * @return 有效用户数量
     */
    @GetMapping("/getValidUserNumber")
    public Long getValidUserNumber() {
        try {
            return userService.getValidUserNumber();
        } catch (Exception e) {
            log.error("user getValidUserNumber error:" + e);
            return null;
        }
    }

    /**
     * 分页查询有效用户
     *
     * @param pageSearchParam 分页查询内容
     * @return 有效用户实体分页
     */
    @PostMapping(value = "/selectUserPage")
    public List<OrgUserModel> selectUserPage(@RequestBody PageSearchParam pageSearchParam) {
        try {
            return userService.selectUserPage(pageSearchParam);
        } catch (Exception e) {
            log.error("user selectUserPage error:" + e);
            return null;
        }
    }

    /**
     * 查询组织机构下的所有有效用户
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    @GetMapping(value = "/getUserListByOrganId")
    public List<OrgUserModel> getUserListByOrganId(@RequestParam("organId") Long organId, @RequestParam("containSubUnit") boolean containSubUnit) {
        try {
            return userService.getUserListByOrganId(organId, containSubUnit);
        } catch (Exception e) {
            log.error("user getUserListByOrganId error:" + e);
            return null;
        }
    }

    /**
     * 查询组织机构下的所有有效用户-提出自动创建的单位管理员和超管
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    @GetMapping(value = "/getUserListByOrganIdRemoveUnitAdmin")
    public List<OrgUserModel> getUserListByOrganIdRemoveUnitAdmin(@RequestParam("organId") Long organId, @RequestParam("containSubUnit") boolean containSubUnit) {
        try {
            return userService.getUserListByOrganIdRemoveUnitAdmin(organId, containSubUnit);
        } catch (Exception e) {
            log.error("user getUserListByOrganId error:" + e);
            return null;
        }
    }

    /**
     * 查询各单位有效用户数量
     *
     * @return 有效用户实体列表
     */
    @GetMapping(value = "/getUnitUserNum")
    public List<UnitUserNumVo> getUnitUserNum(@RequestParam("unitIds") List<Long> unitIds) {
        try {
            return userService.getUnitUserNum(unitIds);
        } catch (Exception e) {
            log.error("user getUnitUserNum error:" + e);
            return null;
        }
    }

    /**
     * 查询组织机构下的所有有效用户-不包含超管和单位管理员
     *
     * @return 有效用户实体列表
     */
    @GetMapping(value = "/getAllUnitUserNum")
    public Integer getAllUnitUserNum(@RequestParam("unitIds") List<Long> unitIds) {
        try {
            return userService.getAllUnitUserNum(unitIds);
        } catch (Exception e) {
            log.error("user getUnitUserNum error:" + e);
            return null;
        }
    }


    /**
     * 根据单位id集合和角色名称集合获取用户角色列表
     *
     * @param organIds     机构id集合
     * @param roleNameList 角色名称集合
     * @return
     */
    @GetMapping("/getUserRoleList")
    public List<OrgUserRoleModel> getUserRoleList(@RequestParam("organIds") List<Long> organIds, @RequestParam("roleNameList") List<String> roleNameList) {
        try {
            return userRoleService.getUserRoleList(organIds, roleNameList);
        } catch (Exception e) {
            log.error("user getUserListByRoleIdList error:" + e);
            return new ArrayList<>();
        }
    }


    /**
     * 更新用户信息
     *
     * @param user
     */
    @PostMapping("/updateUserInfo")
    public Boolean updateUserInfo(@RequestBody OrgUserModel user) {
        return userService.updateById(user) > 0;
    }

    /**
     * 根据角色获取用户列表
     *
     * @param organIds     机构id集合
     * @param roleNameList 角色名称集合
     * @return
     */
    @GetMapping("/getUserListByRole")
    public List<OrgUserModel> getUserListByRole(@RequestParam("organIds") List<Long> organIds, @RequestParam("roleNameList") List<String> roleNameList) {
        try {
            List<Long> roleIdList = userRoleService.getRoleIdList(organIds, roleNameList);
            List<OrgUserModel> orgUserModels = userService.getUserListByRoleIdList(roleIdList);
            if(CollectionUtil.isEmpty(orgUserModels)){
                orgUserModels = Lists.newArrayList();
            }
            orgUserModels = orgUserModels.stream().filter(en->ObjectUtil.equal(en.getType(), CommonConstant.USER_TYPE.COMMON_USER.getValue())).collect(Collectors.toList());
            return orgUserModels;
        } catch (Exception e) {
            log.error("user getUserListByRole error:" + e);
            return new ArrayList<>();
        }
    }

    /**
     * 更新同步样本状态
     *
     * @param unitId
     * @param state
     * @return
     */
    @GetMapping("/updateThState")
    public Boolean updateThStateByUnitId(@RequestParam("unitId") Long unitId, @RequestParam("state") String state) {
        try {
            return userService.updateThStateByUnitId(unitId, state);
        } catch (Exception e) {
            log.error("user updateThState error:" + e);
            return false;
        }
    }

    /**
     * 批量更新用户信息
     *
     * @param users 用户信息
     * @return
     */
    @PostMapping("/bacthUpdate")
    public boolean bacthUpdate(@RequestBody List<OrgUserModel> users) {
        try {
            return userService.batchUpdateNoCascade(users);
        } catch (Exception e) {
            log.error("user bacthUpdate error:" + e);
            return false;
        }
    }

    /**
     * 根据军人证件号获取用户信息
     *
     * @param attrString02 军人证件号
     * @return
     */
    @GetMapping("/getUserByAttrString02")
    public OrgUserModel getUserByAttrString02(@RequestParam("attrString02") String attrString02) {
        try {
            return userService.getUserByAttrString02(attrString02);
        } catch (Exception e) {
            log.error("user getUserByAttrString02 error:" + e);
            return null;
        }
    }

    /**
     * feign调用-更新用户角色信息
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    @PostMapping("/updateUserRole")
    public Result updateUserRole(@RequestBody OrgUserModel userModel) {
        try {
            return userService.updateUserRole(userModel);
        } catch (Exception e) {
            log.error("user updateUserRole error:" + e);
            return Result.failure(e);
        }
    }

    /**
     * 通过用户id获取角色id列表
     *
     * @param id 用户id
     * @return
     */
    @GetMapping("/getRoleIdList")
    public Result getRoleIdListByUserId(@RequestParam("id") Long id){
        try {
            return userService.getRoleIdListByUserId(id);
        }catch (Exception e){
            log.error("user getRoleIdList error:" + e);
            return Result.failure(e);
        }
    }

    /**
     * 获取用户所属数据库模式
     *
     * @param organId 用户单位id
     * @return
     */
    @GetMapping("/getOrganDabaseModel")
    public String selectUserDatabaseModl(@RequestParam(value = "organId") Long organId){
        try {
            return orgOrganModelSettingService.getOrganDabaseModel(organId);
        }catch (Exception e){
            log.error("user selectUserDatabaseModl error:" + e);
            return null;
        }
    }

    /**
     * 获取所有配置的数据库模式
     *
     * @return
     */
    @GetMapping("/getAllDabaseMode")
    public List<String> getAllDabaseMode(){
        try {
            return orgOrganModelSettingService.getAllDabaseMode();
        }catch (Exception e){
            log.error("user getAllnDabaseModel error:" + e);
            return null;
        }
    }

}
