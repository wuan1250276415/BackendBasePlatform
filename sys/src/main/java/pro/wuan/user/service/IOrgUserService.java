package pro.wuan.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.feignapi.userapi.entity.OrgElementModel;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.feignapi.userapi.vo.UnitUserNumVo;
import pro.wuan.feignapi.userapi.vo.UpdateUserOrganAndRoleVo;
import pro.wuan.feignapi.userapi.vo.UserBaseInfoUpdateVo;
import pro.wuan.user.dto.UserPasswordDto;
import pro.wuan.user.mapper.OrgUserMapper;

import java.util.List;

/**
 * 用户
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
public interface IOrgUserService extends IBaseService<OrgUserMapper, OrgUserModel> {

    //用户登录
    Result<UserDetails> login(String loginName, String password);

    /**
     * 选择应用
     *
     * @param appCode 应用编码
     * @return 用户详情
     */
    public Result<UserDetails> selectApp(@NotBlank(message = "appCode不能为空") String appCode);

    //根据登录名查找用户信息
    UserDetails selectByLoginName(String loginName);


    //修改密码
    Result updatePassword(UserPasswordDto userPasswordDto);

    //重置密码
    Result restPassword(Long userId, String password);

    //解锁
    Result unlock(String password);

    /**
     * 用户登录名是否存在
     *
     * @param id        用户id
     * @param loginName 登录名
     * @return true/false
     */
    public boolean isExistLoginName(Long id, String loginName);

    /**
     * 保存单位管理员账号
     *
     * @param account
     * @param unit
     */
    public void saveUnitAdmin(String account, OrgOrganModel unit);

    /**
     * 保存用户
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    public Result<OrgUserModel> saveUser(OrgUserModel userModel);

    /**
     * 保存用户 密码不加密，依赖前端加密策略
     *
     * @param userModel 用户实体
     * @return 用户实体 userModel.get
     */
    Result<OrgUserModel> saveUserTset(OrgUserModel userModel);

    /**
     * 更新用户
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    public Result<OrgUserModel> updateUser(OrgUserModel userModel);

    /**
     * feign调用-更新用户角色信息
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    Result updateUserRole(OrgUserModel userModel);

    /**
     * 通过用户id获取角色id列表
     *
     * @param id 用户id
     * @return
     */
    Result getRoleIdListByUserId(Long id);

    /**
     * 更新用户基本信息
     *
     * @param userBaseInfo 用户基本信息更新vo
     * @return
     */
    public boolean updateUserBaseInfo(UserBaseInfoUpdateVo userBaseInfo);

    /**
     * 更新用户所属的组织机构信息及关联的角色
     *
     * @param vo 更新用户所属的组织机构信息及关联的角色vo
     * @return
     */
    public boolean updateUserOrganAndRole(UpdateUserOrganAndRoleVo vo);

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 删除成功/失败
     */
    public int deleteUser(@NotNull(message = "id不能为空") Long id);

    /**
     * 批量导入用户
     *
     * @param file
     * @param organId
     * @param roleIdList
     * @return
     */
    public Result batchImportUser(MultipartFile file, Long organId, List<Long> roleIdList);

    /**
     * 批量导入单位、用户、角色
     *
     * @param file
     * @param appId
     * @return
     */
    public Result batchImportUnitUserRole(MultipartFile file, Long appId);

    /**
     * 分页查询角色关联的用户列表
     *
     * @param pageSearchParam 分页查询内容
     * @return 用户分页列表
     */
    public Result<IPage<OrgUserModel>> selectUserListPageByRole(PageSearchParam pageSearchParam);

    /**
     * 获取组织机构下当前用户可授权的角色列表
     *
     * @param organId 组织机构id
     * @return 角色实体列表
     */
    public List<OrgRoleModel> getRoleListByOrganAndCurrentUser(@NotNull(message = "organId不能为空") Long organId);

    /**
     * 获取组织机构所属单位下的用户列表
     *
     * @param organId 组织机构id
     * @return 用户实体列表
     */
    public List<OrgUserModel> getUserListByOrgan(@NotNull(message = "organId不能为空") Long organId);

    /**
     * 获取组织机构下的用户列表
     *
     * @param organId 组织机构id
     * @return 用户实体列表
     */
    public List<OrgUserModel> getUserListByOrganId(@NotNull(message = "organId不能为空") Long organId);

    /**
     * 获取组织机构所属单位下的用户列表（排除userId对应的用户）
     *
     * @param organId 组织机构id
     * @param userId  用户id
     * @return 用户实体列表
     */
    public List<OrgUserModel> getUserListByUnit(@NotNull(message = "organId不能为空") Long organId, Long userId);

    /**
     * 根据id获取用户信息
     *
     * @param id 主键
     * @return
     */
    public OrgUserModel getUserById(Long id);

    /**
     * 根据应用编码获取用户列表（应用-角色-用户）
     *
     * @param appCode 应用编码
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByApp(String appCode);

    /**
     * 根据元素列表获取关联的用户列表
     *
     * @param elementList 元素列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByElementList(List<OrgElementModel> elementList);

    /**
     * 根据用户id列表获取用户列表
     *
     * @param userIdList 用户id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByUserIdList(List<Long> userIdList);

    /**
     * 根据角色id列表获取角色关联的用户列表
     *
     * @param roleIdList 角色id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByRoleIdList(List<Long> roleIdList);

    /**
     * 根据岗位id列表获取岗位关联的用户列表
     *
     * @param postIdList 岗位id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByPostIdList(List<Long> postIdList);

    /**
     * 根据当前登录用户获取所属部门负责人
     *
     * @return 所属部门负责人
     */
    public OrgUserModel getDepartmentHeadByCurrentUser();

    /**
     * 根据当前登录用户获取直属上级
     *
     * @return 直属上级
     */
    public OrgUserModel getDirectLeaderByCurrentUser();

    /**
     * 根据当前登录用户获取连续多级主管列表
     * 例如：参数为2，返回当前登录用户的直属上级，直属上级的直属上级
     *
     * @param level 级别
     * @return 连续多级主管列表
     */
    public List<OrgUserModel> getLeaderListByCurrentUser(int level);

    /**
     * 统计有效用户数量
     *
     * @return 有效用户数量
     */
    public Long getValidUserNumber();

    /**
     * 分页查询有效用户
     *
     * @param pageSearchParam 分页查询内容
     * @return 有效用户实体分页
     */
    public List<OrgUserModel> selectUserPage(PageSearchParam pageSearchParam);

    /**
     * 查询组织机构下的所有有效用户
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    public List<OrgUserModel> getUserListByOrganId(Long organId, boolean containSubUnit);

    /**
     * 查询组织机构下的所有有效用户-提出自动创建的单位管理员和超管
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    List<OrgUserModel> getUserListByOrganIdRemoveUnitAdmin(Long organId, boolean containSubUnit);

    /**
     * 锁屏
     *
     * @return
     */
    Result lockScreen();


    /**
     * 人脸识别登录
     *
     * @return 返回用户详情
     */
    Result<UserDetails> faceLogin(MultipartFile file);

    /**
     * 获取指定单位用户数
     *
     * @param unitIds 指定单位用户数量
     * @return
     */
    List<UnitUserNumVo> getUnitUserNum(List<Long> unitIds);

    /**
     * 查询组织机构下的所有有效用户-不包含超管和单位管理员
     *
     * @param unitIds 指定单位用户数量
     * @return
     */
    Integer getAllUnitUserNum(List<Long> unitIds);

    /**
     * 更新同步样本状态
     *1
     * @param unitId
     * @param state
     * @return
     */

    Boolean updateThStateByUnitId(Long unitId, String state);

    /**
     * 根据登入名查找用户信息
     *
     * @param loginName
     * @return
     */
    OrgUserModel loadLoginName(String loginName);

    /**
     * 根据军人证件号获取用户信息
     *
     * @param attrString02 军人证件号
     * @return
     */
    public OrgUserModel getUserByAttrString02(String attrString02);

}