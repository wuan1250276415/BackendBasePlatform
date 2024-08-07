package pro.wuan.user.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.auth.util.JwtUtils;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.constant.HttpStatus;
import pro.wuan.common.core.constant.SearchOptConstant;
import pro.wuan.common.core.model.BaseQueryValue;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.model.UserAuthInfo;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.common.db.service.impl.DispatcherService;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.dict.service.ISysDictionaryService;
import pro.wuan.element.mapper.OrgElementMapper;
import pro.wuan.element.service.IOrgElementService;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.ossapi.entity.SysAttMainEntity;
import pro.wuan.feignapi.ossapi.fegin.SysAttMainService;
import pro.wuan.feignapi.ossapi.model.SysAttMainVo;
import pro.wuan.feignapi.userapi.entity.*;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.feignapi.userapi.vo.UnitUserNumVo;
import pro.wuan.feignapi.userapi.vo.UpdateUserOrganAndRoleVo;
import pro.wuan.feignapi.userapi.vo.UserBaseInfoUpdateVo;
import pro.wuan.organ.service.IOrgOrganService;
import pro.wuan.post.service.IOrgPostService;
import pro.wuan.privilege.service.IOrgBusinessResourceService;
import pro.wuan.privilege.service.IOrgDataSchemeService;
import pro.wuan.privilege.service.IOrgListColumnService;
import pro.wuan.role.service.IOrgRoleService;
import pro.wuan.service.IOrgAppService;
import pro.wuan.user.dto.ImportUserDto;
import pro.wuan.user.dto.UserPasswordDto;
import pro.wuan.user.dto.excelImport.ExcelImportResultDto;
import pro.wuan.user.dto.excelImport.ExcelUserDto;
import pro.wuan.user.entity.PasswordSecuritySet;
import pro.wuan.user.mapper.OrgUserMapper;
import pro.wuan.user.service.*;
import pro.wuan.utils.MockMultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Slf4j
@Service("orgUserService")
public class OrgUserServiceImpl extends BaseServiceImpl<OrgUserMapper, OrgUserModel> implements IOrgUserService {

    @Autowired
    private IPasswordSecuritySetService passwordSecuritySetService;

    @Autowired
    private JedisUtil jedisUtil;

    @Resource
    private IOrgOrganService organService;

    @Resource
    private IOrgPostService postService;

    @Resource
    private IOrgRoleService roleService;

    @Resource
    private IOrgBusinessResourceService businessResourceService;

    @Resource
    private IOrgListColumnService listColumnService;

    @Resource
    private IOrgDataSchemeService dataSchemeService;

    @Resource
    private IOrgUserRoleService userRoleService;

    @Resource
    private IOrgUserPostService userPostService;

    @Resource
    private IOrgAppService appService;

    @Resource
    private IUserSetService userSetService;

    @Resource
    private IOrgElementService elementService;

    @Resource
    private OrgElementMapper elementMapper;

    @Resource
    @Lazy
    private SysAttMainService attMainService;

    @Value("${tellhow.nginxPath}")
    private String nginxPath;

    @Resource
    private ISysDictionaryService sysDictionaryService;

    @Autowired
    private DispatcherService dispatcher;


    /**
     * 用户登录
     *
     * @param loginName
     * @param password
     * @return
     */
    public Result<UserDetails> login(String loginName, String password) {
        //根据用户登录名获取用户信息
        OrgUserModel user = this.selectOne(new LambdaQueryWrapper<OrgUserModel>().eq(OrgUserModel::getLoginName, loginName));
        if (user == null) {
            return Result.failure("用户不存在！");
        }
        //是否启用
        if (CommonConstant.USE_STATUS.USE.getValue() != user.getStatus()) {
            return Result.failure("用户未启用，请联系管理员！");
        }
        if (!CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 不是超级管理员
            if (user.getOrganId() == null) {
                return Result.failure("用户未关联组织机构！");
            }
            OrgOrganModel organ = organService.getOrganById(user.getOrganId());
            if (organ == null) {
                return Result.failure("用户所属组织机构不存在！");
            }
            if (CommonConstant.USE_STATUS.USE.getValue() != organ.getStatus()) {
                return Result.failure("用户所属组织机构未启用！");
            }
            OrgOrganModel unit = organService.getDirectParentUnit(user.getOrganId());
            if (unit == null) {
                return Result.failure("用户所属单位不存在！");
            }
            if (CommonConstant.USE_STATUS.USE.getValue() != unit.getStatus()) {
                return Result.failure("用户所属单位未启用！");
            }
        }

        //获取系统密码策略,判断密码失败次数
        PasswordSecuritySet passwordSecuritySet = passwordSecuritySetService.selectOne(null);
        boolean flag = checkLogin(user, passwordSecuritySet);
        if (!flag) {
            return Result.failure("登录失败，账号被锁定，请稍后再试或联系管理员！");
        }
        //String md5Password = MD5Encoder.getMD5String(password);
        //检查登录密码是否一致
        if (!password.equals(user.getPassword())) {
            //更新当前用户登入失败次数
            updateFailureCount(user, passwordSecuritySet);
            return Result.failure("登录失败，密码错误!");
        }
        //如果都成功
        user.setFailureCount(0);//重置登录失败次数
        user.setLockTime(null);//重置锁定时间
        this.updateById(user);
        //生成token
        long millTimes = System.currentTimeMillis();
        String strToken = JwtUtils.genToken(loginName, millTimes);
        UserDetails userDetails = new UserDetails();
        userDetails.setUser(user);
        userDetails.setNginxPath(nginxPath);
        userDetails.setToken(strToken);
        userDetails.setAccount(loginName);
        // 获取用户组织机构、岗位信息
        this.getOrganAndPostOfUser(userDetails);
        // 根据用户id获取应用列表
        List<OrgAppModel> appList = appService.getAppListByUserId(user.getId());
        if (CollectionUtil.isEmpty(appList)) {
            return Result.failure("用户无应用访问权限");
        }
        userDetails.setAppList(appList);
        //获取用户设置信息
        UserSet userSet = userSetService.getByUserId(user.getId());
        userDetails.setUserSet(userSet);
        //设置用户信息线程上下文
        UserContext.setCurrentUser(userDetails);
        //设置缓存信息
        jedisUtil.set(CommonConstant.JEDIS_USER_DETAILS_PREFIX + userDetails.getAccount(), userDetails);
        return Result.success(userDetails);
    }


    /**
     * 人脸识别登录
     *
     * @return 返回用户详情
     */
    @Override
    public Result<UserDetails> faceLogin(MultipartFile file) {
        //获取系统中所有可用用户
        List<OrgUserModel> userList = this.selectList(null);
        if (userList != null && userList.size() > 0) {
            for (OrgUserModel orgUserModel : userList) {
                if (StringUtils.isEmpty(orgUserModel.getImageId())) {
                    continue;
                }
                // 判断是否是本人
                //生成token
                long millTimes = System.currentTimeMillis();
                String strToken = JwtUtils.genToken(orgUserModel.getLoginName(), millTimes);
                UserDetails userDetails = new UserDetails();
                userDetails.setUser(orgUserModel);
                userDetails.setNginxPath(nginxPath);
                userDetails.setToken(strToken);
                userDetails.setAccount(orgUserModel.getLoginName());
                // 获取用户组织机构、岗位信息
                this.getOrganAndPostOfUser(userDetails);
                // 根据用户id获取应用列表
                List<OrgAppModel> appList = appService.getAppListByUserId(orgUserModel.getId());
                if (CollectionUtil.isEmpty(appList)) {
                    return Result.failure("用户无应用访问权限");
                }
                userDetails.setAppList(appList);
                //获取用户设置信息
                UserSet userSet = userSetService.getByUserId(orgUserModel.getId());
                userDetails.setUserSet(userSet);
                //设置用户信息线程上下文
                UserContext.setCurrentUser(userDetails);
                //设置缓存信息
                jedisUtil.set(CommonConstant.JEDIS_USER_DETAILS_PREFIX + userDetails.getAccount(), userDetails);
                return Result.success(userDetails);
            }
        }
        return Result.failure("系统暂无你的人脸信息，请先录入!");
    }


    /**
     * 获取用户组织机构、岗位信息
     *
     * @param userDetails
     */
    private void getOrganAndPostOfUser(UserDetails userDetails) {
        OrgUserModel user = userDetails.getUser();
        if (user == null) {
            return;
        }
        if (user.getOrganId() != null) {
            // 所属部门
            OrgOrganModel department = organService.getOrganById(user.getOrganId());
            userDetails.setDepartment(department);
            // 所属单位
            OrgOrganModel directUnit = organService.getDirectParentUnit(department.getId());
            userDetails.setUnit(directUnit);
        }
        // 岗位列表
        List<OrgPostModel> postList = postService.getPostListByUserId(user.getId());
        userDetails.setPostList(postList);


        {
            ////一级下属
            List<Long> directUnderlingUserIds = new ArrayList<>();
            LambdaQueryWrapper<OrgUserModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(OrgUserModel::getLeaderUserId, user.getId());
            List<OrgUserModel> userModels = dao.selectList(wrapper);
            if (userModels.size() > 0) {
                userDetails.setDirectUnderlingUsers(userModels);
                userModels.forEach(item -> {
                    directUnderlingUserIds.add(item.getId());
                });
                userDetails.setDirectUnderlingUserIds(directUnderlingUserIds);
                userDetails.setIndirectUnderlingUsers(userModels);
            }

            //二级下属
            List<Long> inDirectUnderlingUserIds = new ArrayList<>();
            if (userModels.size() > 0) {
                //添加一级下属的ID
                inDirectUnderlingUserIds.addAll(directUnderlingUserIds);

                LambdaQueryWrapper<OrgUserModel> inDirectwrapper = new LambdaQueryWrapper<>();
                inDirectwrapper.in(OrgUserModel::getLeaderUserId, directUnderlingUserIds);
                List<OrgUserModel> inDirectUserModels = dao.selectList(wrapper);
                userDetails.getIndirectUnderlingUsers().addAll(inDirectUserModels);
                inDirectUserModels.forEach(item -> {
                    inDirectUnderlingUserIds.add(item.getId());
                });
                userDetails.setIndirectUnderlingUserIds(inDirectUnderlingUserIds);
            }
        }
    }

    /**
     * 选择应用
     *
     * @param appCode 应用编码
     * @return 用户详情
     */
    public Result<UserDetails> selectApp(@NotBlank(message = "appCode不能为空") String appCode) {
        OrgAppModel app = appService.getAppByCode(appCode);
        if (app == null) {
            return Result.failure("应用不存在");
        }
        if (!CommonConstant.USE_STATUS.USE.getValue().equals(app.getStatus())) {
            return Result.failure("应用已经停用");
        }
        // 获取用户信息线程上下文
        UserDetails userDetails = UserContext.getCurrentUser();
        //获取用户关联信息
        this.getUserDetail(userDetails, app.getId());
        //设置用户信息线程上下文
        UserContext.setCurrentUser(userDetails);
        //设置缓存信息
        jedisUtil.set(CommonConstant.JEDIS_USER_DETAILS_PREFIX + userDetails.getAccount(), userDetails);

        UserAuthInfo userAuthInfo = new UserAuthInfo();
        userAuthInfo.setAccount(userDetails.getAccount());
        userAuthInfo.setUserType(userDetails.getUser().getType());
        userAuthInfo.setUrls(userDetails.getUrls());
        jedisUtil.set(CommonConstant.JEDIS_USER_AUTH_PREFIX + userDetails.getAccount(), userAuthInfo);
        return Result.success(userDetails);
    }

    /**
     * 获取用户详细信息
     *
     * @param userDetails
     * @param appId
     */
    private void getUserDetail(UserDetails userDetails, Long appId) {
        // 清除与切换应用之前的应用相关的用户数据
        this.clearUserDataRelateApp(userDetails);
        if (appId == null) {
            return;
        }
        // 获取用户信息
        OrgUserModel user = userDetails.getUser();
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
            this.getUserDetailBySuperAdmin(userDetails, appId);
            return;
        }
        // 角色列表
        List<OrgRoleModel> roleList = roleService.getRoleListByUserAndApp(user.getId(), appId);
        userDetails.setRoleList(roleList);
        if (CollectionUtil.isEmpty(roleList)) {
            return;
        }
        List<Long> roleIdList = new ArrayList<>();
        for (OrgRoleModel role : roleList) {
            roleIdList.add(role.getId());
        }
        Set<String> urlSet = new HashSet<>();
        // 业务资源列表
        this.setBusinessResource(userDetails, roleIdList, urlSet);
        // 列表字段列表
        this.setListColumn(userDetails, roleIdList);
        // 数据方案列表
        this.setDataScheme(userDetails, roleIdList, urlSet);
        // 可访问路径集合
        userDetails.setUrls(urlSet);
    }

    /**
     * 清除与应用相关的用户数据
     *
     * @param userDetails
     */
    private void clearUserDataRelateApp(UserDetails userDetails) {
        if (userDetails != null) {
            userDetails.setRoleList(new ArrayList<>());
            userDetails.setMenuList(new ArrayList<>());
            userDetails.setFunctionList(new ArrayList<>());
            userDetails.setListColumnList(new ArrayList<>());
            userDetails.setDataSchemeList(new ArrayList<>());
            userDetails.setUrls(new HashSet<>());
        }
    }

    /**
     * 获取用户详细信息-超级管理员
     *
     * @param userDetails
     * @param appId
     */
    public void getUserDetailBySuperAdmin(UserDetails userDetails, Long appId) {
        userDetails.setMenuList(businessResourceService.getMenuTreeByAppId(appId));
        userDetails.setFunctionList(businessResourceService.getFunctionListByAppId(appId));
        userDetails.setListColumnList(listColumnService.getListColumnListByAppId(appId));
    }

    /**
     * 设置业务资源相关数据
     *
     * @param userDetails
     * @param roleIdList
     * @param urlSet
     */
    private void setBusinessResource(UserDetails userDetails, List<Long> roleIdList, Set<String> urlSet) {
        List<OrgBusinessResourceModel> businessResourceList = businessResourceService.getBusinessResourceListByRoleIdList(roleIdList);
        if (CollectionUtil.isNotEmpty(businessResourceList)) {
            List<OrgBusinessResourceModel> menuList = new ArrayList<>();
            List<OrgBusinessResourceModel> functionList = new ArrayList<>();
            for (OrgBusinessResourceModel businessResource : businessResourceList) {
                if (CommonConstant.BUSINESS_RESOURCE_TYPE.CATALOG.getValue().equals(businessResource.getType())) { // 目录
                    menuList.add(businessResource);
                } else if (CommonConstant.BUSINESS_RESOURCE_TYPE.MENU.getValue().equals(businessResource.getType())) { // 菜单
                    menuList.add(businessResource);
                } else if (CommonConstant.BUSINESS_RESOURCE_TYPE.FUNCTION.getValue().equals(businessResource.getType())) { // 功能
                    functionList.add(businessResource);
                }
                // 将业务资源对应的url加入可访问路径集合
                if (StrUtil.isNotEmpty(businessResource.getUrl())) {
                    urlSet.add(businessResource.getUrl());
                }
            }
            userDetails.setMenuList(businessResourceService.getMenuTree(menuList));
            userDetails.setFunctionList(functionList);
        } else {
            userDetails.setMenuList(new ArrayList<>());
            userDetails.setFunctionList(new ArrayList<>());
        }
    }

    /**
     * 设置列表字段相关数据
     *
     * @param userDetails
     * @param roleIdList
     */
    private void setListColumn(UserDetails userDetails, List<Long> roleIdList) {
        List<OrgListColumnModel> listColumnList = listColumnService.getListColumnListByRoleIdList(roleIdList);
        if (CollectionUtil.isNotEmpty(listColumnList)) {
            userDetails.setListColumnList(listColumnList);
        } else {
            userDetails.setListColumnList(new ArrayList<>());
        }
    }


    /**
     * 设置数据方案相关数据
     *
     * @param userDetails
     * @param roleIdList
     * @param urlSet
     */
    private void setDataScheme(UserDetails userDetails, List<Long> roleIdList, Set<String> urlSet) {
        List<OrgDataSchemeModel> dataSchemeList = dataSchemeService.getDataSchemeListByRoleIdList(roleIdList);
        if (CollectionUtil.isNotEmpty(dataSchemeList)) {
            for (OrgDataSchemeModel dataScheme : dataSchemeList) {
                // 将数据方案对应的url加入可访问路径集合
                if (StrUtil.isNotEmpty(dataScheme.getUrl())) {
                    urlSet.add(dataScheme.getUrl());
                }
            }
            userDetails.setDataSchemeList(dataSchemeList);
        } else {
            userDetails.setDataSchemeList(new ArrayList<>());
        }
    }

    /**
     * 根据登入名查找用户信息
     *
     * @param loginName
     * @return
     */
    public UserDetails selectByLoginName(String loginName) {
        UserDetails userDetails = null;
        String key = CommonConstant.JEDIS_USER_DETAILS_PREFIX + loginName;
        if (jedisUtil.exists(key)) {
            userDetails = (UserDetails) jedisUtil.get(key);
        } else {
            userDetails = new UserDetails();
            OrgUserModel user = this.selectOne(new LambdaQueryWrapper<OrgUserModel>().eq(OrgUserModel::getLoginName, loginName));
            userDetails.setUser(user);
            userDetails.setAccount(loginName);
            this.getOrganAndPostOfUser(userDetails);
            this.getUserDetail(userDetails, null);
            //设置缓存信息
            jedisUtil.set(key, userDetails);
        }
        return userDetails;
    }


    /**
     * 修改密码
     *
     * @param userPasswordDto
     * @return
     */
    public Result updatePassword(UserPasswordDto userPasswordDto) {
        OrgUserModel orgUser = this.selectById(userPasswordDto.getUserId());
        if (orgUser == null) {
            return Result.failure("用户不存在");
        }
        String oldPassword = userPasswordDto.getOldPassword();
        if (!oldPassword.equals(orgUser.getPassword())) {
            return Result.failure("原密码输入错误");
        }
        String newPassword = userPasswordDto.getNewPassword();
        orgUser.setPassword(newPassword);
        orgUser.setModifyPasswordTime(new Date());
        return this.updateById(orgUser) > 0 ? Result.success("修改密码成功！") : Result.failure("修改密码失败！");
    }

    /**
     * 重置密码
     *
     * @param userId
     * @param password
     * @return
     */
    public Result restPassword(Long userId, String password) {
        OrgUserModel orgUser = this.selectById(userId);
        if (orgUser == null) {
            return Result.failure("用户不存在");
        }
        orgUser.setPassword(password);
        orgUser.setModifyPasswordTime(new Date());
        return this.updateById(orgUser) > 0 ? Result.success("重置密码成功!") : Result.failure("重置密码失败！");
    }


    /**
     * 解锁
     *
     * @param password
     * @return
     */
    public Result unlock(String password) {
        if (UserContext.getCurrentUser().getUser() != null) {
            if (!password.equals(UserContext.getCurrentUser().getUser().getPassword())) {
                return Result.failure("解锁失败，密码错误!");
            }
            Long userId = UserContext.getCurrentUserId();
            OrgUserModel user = this.selectById(userId);
            if (user != null) {
                user.setLockScreen(false);
                if (this.updateById(user) == 1) {
                    return Result.success();
                }
            }
        }
        return Result.failure();
    }

    /**
     * 判断用户是否可以登入
     *
     * @param user
     * @param passwordSecuritySet
     */
    public boolean checkLogin(OrgUserModel user, PasswordSecuritySet passwordSecuritySet) {
        boolean flag = true;
        //锁定时间
        if (user.getLockTime() == null) {
            return true;
        }
        //禁止登录期限
        if (passwordSecuritySet == null || passwordSecuritySet.getForbidTerm() <= 0) {
            return true;
        }
        Date lockTime = user.getLockTime();
        int forbidTerm = passwordSecuritySet.getForbidTerm();
        //是否自动解锁
        long time = lockTime.getTime() + forbidTerm * 60 * 60 * 1000;
        if (time > System.currentTimeMillis()) {
            flag = false;
        } else {
            //重置登录失败次数和置空锁定时间
            user.setFailureCount(0);
            user.setLockTime(null);
            this.updateById(user);
        }
        return flag;
    }

    /**
     * 更新登入失败次数
     *
     * @param user
     * @param passwordSecuritySet
     */
    public void updateFailureCount(OrgUserModel user, PasswordSecuritySet passwordSecuritySet) {
        //如果系统配置不为空
        if (passwordSecuritySet != null && passwordSecuritySet.getFailureCount() > 0) {
            int failureCount = user.getFailureCount();//用户已经登录失败次数
            failureCount += 1;
            user.setFailureCount(failureCount);
            //如果用户失败次数大于等于系统设定的失败次数
            if (passwordSecuritySet.getFailureCount() == failureCount) {
                user.setLockTime(new Date());
            }
            this.updateById(user);
        }
    }

    /**
     * 用户登录名是否存在
     *
     * @param id        用户id
     * @param loginName 登录名
     * @return true/false
     */
    public boolean isExistLoginName(Long id, String loginName) {
        LambdaQueryWrapper<OrgUserModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserModel::getLoginName, loginName);
        if (id != null) {
            wrapper.ne(OrgUserModel::getId, id);
        }
        Long count = this.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 保存单位管理员账号
     *
     * @param account
     * @param unit
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUnitAdmin(String account, OrgOrganModel unit) {
        Assert.hasLength(account, "单位管理员账号不能为空");
        Assert.notNull(unit, "单位信息不能为空");
        Assert.isTrue(!this.isExistLoginName(null, account), "单位管理员账号已经存在，请重新输入");
        OrgUserModel userAdmin = new OrgUserModel();
        userAdmin.setLoginName(account);
        userAdmin.setUserName(unit.getName() + "单位管理员");
        userAdmin.setPassword(this.getPassword());
        userAdmin.setSalt("");
        userAdmin.setFailureCount(0);
        // 用户类型设置为单位管理员
        userAdmin.setType(CommonConstant.USER_TYPE.UNIT_ADMIN.getValue());
        userAdmin.setRemark("单位管理员");
        userAdmin.setLeaderUserId(null); // 无直接上级
        userAdmin.setOrganId(unit.getId());
        userAdmin.setStatus(CommonConstant.USE_STATUS.USE.getValue());
        userAdmin.setTenantId(unit.getId()); // 保存用户时需要设置租户id
        this.save(userAdmin);
        // 保存用户角色关联信息
        OrgUserRoleModel userRoleModel = new OrgUserRoleModel();
        userRoleModel.setUserId(userAdmin.getId());
        userRoleModel.setRoleId(CommonConstant.ROLE_ID_UNIT_ADMIN); // 设置单位管理员角色id
        userRoleService.insert(userRoleModel);
        // 保存元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(userAdmin.getId());
        elementModel.setName(userAdmin.getUserName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.USER.getValue());
        elementModel.setParentId(userAdmin.getOrganId());
        elementModel.setTreeIds(unit.getTreeIds() + CommonConstant.SEPARATOR_COMMA + userAdmin.getId());
        elementModel.setStatus(userAdmin.getStatus());
        elementModel.setTenantId(userAdmin.getTenantId()); // 保存用户对应的元素时需要设置租户id
        elementService.insert(elementModel);
    }

    /**
     * 获取用户密码
     *
     * @return
     */
    private String getPassword() {
        // 获取系统密码安全策略
        PasswordSecuritySet passwordSecuritySet = passwordSecuritySetService.getSecuritySet();
        if (passwordSecuritySet != null) {
            return SecureUtil.md5(passwordSecuritySet.getInitPassword());
        } else {
            return SecureUtil.md5("666666");
        }
    }

    /**
     * 保存用户
     *
     * @param userModel 用户实体
     * @return 用户实体 userModel.get
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgUserModel> saveUser(OrgUserModel userModel) {
        Assert.isTrue(!this.isExistLoginName(null, userModel.getLoginName()), "登录账号已经存在，请重新输入");
        // 直属领导为空时设置为默认值-1
        if (ObjectUtil.isNull(userModel.getLeaderUserId())) {
            userModel.setLeaderUserId(CommonConstant.DEFAULT_ID);
        }
        Assert.notNull(userModel.getOrganId(), "所属部门不能为空");
        OrgOrganModel organ = organService.getOrganById(userModel.getOrganId());
        Assert.notNull(organ, "所属部门不存在");
        if (StrUtil.isEmpty(userModel.getPassword())) {
            userModel.setPassword(this.getPassword());
        }
        userModel.setFailureCount(0);
        // 用户类型设置为普通用户
        userModel.setType(CommonConstant.USER_TYPE.COMMON_USER.getValue());
        // 获取所属单位信息
        OrgOrganModel unit = organService.getDirectParentUnit(organ.getId());
        Assert.notNull(unit, "所属部门不属于任何单位");
        userModel.setTenantId(unit.getId()); // 保存用户时需要设置租户id
        if(StrUtil.isBlank(userModel.getAttrString02())){
            userModel.setAttrString02(StrUtil.uuid());
        }
        if (this.insert(userModel) <= 0) {
            return Result.failure(userModel);
        }
        if (ObjectUtil.isNotEmpty(userModel.getSjzxVo())) {
            //删除数据中心照片旧附件和使新附件生效
            SysAttMainVo sysAttMainSjzxVo = new SysAttMainVo();
            sysAttMainSjzxVo.setSysAttMainIds(userModel.getSjzxVo().getSysAttMainIds());
            sysAttMainSjzxVo.setModuleKey(userModel.getSjzxVo().getModuleKey());
            userModel.setSysAttMainVo(sysAttMainSjzxVo);
            dispatcher.dispatcherSave(userModel);
        }
        // 保存用户岗位关联信息
        userPostService.batchSaveUserPost(userModel.getId(), userModel.getPostIdList());
        // 保存用户角色关联信息
        userRoleService.batchSaveUserRole(userModel.getId(), userModel.getRoleIdList());
        // 保存元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(userModel.getId());
        elementModel.setName(userModel.getUserName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.USER.getValue());
        elementModel.setParentId(userModel.getOrganId());
        elementModel.setTreeIds(organ.getTreeIds() + CommonConstant.SEPARATOR_COMMA + userModel.getId());
        elementModel.setStatus(userModel.getStatus());
        elementModel.setTenantId(userModel.getTenantId()); // 保存用户对应的元素时需要设置租户id
        elementService.insert(elementModel);
        return Result.success(userModel);
    }


    /**
     * 保存用户 密码不加密，依赖前端加密策略
     *
     * @param userModel 用户实体
     * @return 用户实体 userModel.get
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<OrgUserModel> saveUserTset(OrgUserModel userModel) {
        Assert.isTrue(!this.isExistLoginName(null, userModel.getLoginName()), "登录账号已经存在，请重新输入");
        // 直属领导为空时设置为默认值-1
        if (ObjectUtil.isNull(userModel.getLeaderUserId())) {
            userModel.setLeaderUserId(CommonConstant.DEFAULT_ID);
        }
        Assert.notNull(userModel.getOrganId(), "所属部门不能为空");
        OrgOrganModel organ = organService.getOrganById(userModel.getOrganId());
        Assert.notNull(organ, "所属部门不存在");
        //默认开启不修改初始密码
        userModel.setModifyPasswordTime(new Date());
        userModel.setPassword(userModel.getPassword());
        userModel.setFailureCount(0);
        // 用户类型设置为普通用户
        userModel.setType(CommonConstant.USER_TYPE.COMMON_USER.getValue());
        // 获取所属单位信息
        OrgOrganModel unit = organService.getDirectParentUnit(organ.getId());
        if(StrUtil.isBlank(userModel.getAttrString02())){
            userModel.setAttrString02(StrUtil.uuid());
        }
        Assert.notNull(unit, "所属部门不属于任何单位");
        userModel.setTenantId(unit.getId()); // 保存用户时需要设置租户id
        if(ObjectUtil.isNotEmpty(userModel.getSysAttMainVo()) && ObjectUtil.isNotEmpty(userModel.getSysAttMainVo().getSysAttMainIds())){
            // 上传人脸样本时更新用户头像
            List<SysAttMainEntity> sysEntityList = attMainService.getFilesByIds(Lists.newArrayList(userModel.getSysAttMainVo().getSysAttMainIds()));
            if(CollectionUtil.isNotEmpty(sysEntityList)){
                List<SysAttMainEntity> collect = sysEntityList.stream().filter(sysEntity -> sysEntity.getStatus().equals(CommonConstant.FILE_STATUS_AVAILABLE)).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(collect)){
                    userModel.setHeadImageUrl(collect.get(0).getFilePath());
                }
            }
        }
        if (this.insert(userModel) <= 0) {
            return Result.failure(userModel);
        }
        // 保存用户岗位关联信息
        userPostService.batchSaveUserPost(userModel.getId(), userModel.getPostIdList());
        // 保存用户角色关联信息
        userRoleService.batchSaveUserRole(userModel.getId(), userModel.getRoleIdList());
        // 保存元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(userModel.getId());
        elementModel.setName(userModel.getUserName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.USER.getValue());
        elementModel.setParentId(userModel.getOrganId());
        elementModel.setTreeIds(organ.getTreeIds() + CommonConstant.SEPARATOR_COMMA + userModel.getId());
        elementModel.setStatus(userModel.getStatus());
        elementModel.setTenantId(userModel.getTenantId()); // 保存用户对应的元素时需要设置租户id
        elementService.insert(elementModel);
        return Result.success(userModel);
    }


    /**
     * 更新用户
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgUserModel> updateUser(OrgUserModel userModel) {
        Long userId = userModel.getId();
        OrgUserModel user = this.selectById(userId);
        Assert.notNull(user, "用户信息不存在");
        Assert.isTrue(user.getLoginName().equals(userModel.getLoginName()), "登录账号不允许修改");
        // 直属领导为空时设置为默认值-1
        if (ObjectUtil.isNull(userModel.getLeaderUserId())) {
            userModel.setLeaderUserId(CommonConstant.DEFAULT_ID);
        }
        Assert.isTrue(!userId.equals(userModel.getLeaderUserId()), "直属领导不能为用户本身");
        this.checkLeaderUser(userModel.getLeaderUserId(), userId);
        Assert.notNull(userModel.getOrganId(), "所属部门不能为空");
        OrgOrganModel organ = organService.getOrganById(userModel.getOrganId());
        Assert.notNull(organ, "所属部门不存在");
        // 获取所属单位信息
        OrgOrganModel unit = organService.getDirectParentUnit(organ.getId());
        Assert.notNull(unit, "所属部门不属于任何单位");
        userModel.setTenantId(unit.getId()); // 保存用户时需要设置租户id
        userModel.setFeature1(user.getFeature1());
        userModel.setFeature2(user.getFeature2());
        userModel.setAttrString01("123123");
        if (this.updateByIdWithCascade(userModel) < 1) {
            return Result.failure(userModel);
        }
        if (ObjectUtil.isNotEmpty(userModel.getSjzxVo())) {
            //删除数据中心照片旧附件和使新附件生效
            SysAttMainVo sysAttMainSjzxVo = new SysAttMainVo();
            sysAttMainSjzxVo.setSysAttMainIds(userModel.getSjzxVo().getSysAttMainIds());
            sysAttMainSjzxVo.setModuleKey(userModel.getSjzxVo().getModuleKey());
            userModel.setSysAttMainVo(sysAttMainSjzxVo);
            dispatcher.dispatcherUpdate(userModel);
        }

        // 批量删除用户岗位关联信息
        userPostService.batchDeleteUserPost(userId);
        // 批量保存用户岗位关联信息
        userPostService.batchSaveUserPost(userId, userModel.getPostIdList());
        // 批量删除用户角色关联信息
        userRoleService.batchDeleteUserRole(userId);
        // 批量保存用户角色关联信息
        userRoleService.batchSaveUserRole(userId, userModel.getRoleIdList());
        // 更新元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(userModel.getId());
        elementModel.setName(userModel.getUserName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.USER.getValue());
        elementModel.setParentId(userModel.getOrganId());
        elementModel.setTreeIds(organ.getTreeIds() + CommonConstant.SEPARATOR_COMMA + userModel.getId());
        elementModel.setStatus(userModel.getStatus());
        elementModel.setTenantId(userModel.getTenantId()); // 保存用户对应的元素时需要设置租户id
        elementService.updateById(elementModel);
        return Result.success(userModel);
    }


    /**
     * 更新用户角色
     *
     * @param userModel 用户实体
     * @return 用户实体
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result updateUserRole(OrgUserModel userModel) {
        Assert.notNull(userModel.getId(), "用户id不能为空");
        if(CollectionUtil.isNotEmpty(userModel.getRoleIdList())){
            Long userId = userModel.getId();
            OrgUserModel user = this.selectById(userId);
            Assert.notNull(user, "用户信息不存在");
            // 批量删除用户角色关联信息
            userRoleService.batchDeleteUserRole(userId);
            // 批量保存用户角色关联信息
            userRoleService.batchSaveUserRole(userId, userModel.getRoleIdList());
            Result.success(userModel);
        }
        return Result.failure("用户绑定的角色信息为空！");
    }


    /**
     * 通过用户id获取角色id列表
     *
     * @param id 用户id
     * @return
     */
    @Override
    public Result getRoleIdListByUserId(Long id){
        List<OrgUserRoleModel> userRoleInfo = userRoleService.getUserRoleListByUserId(id);
        List<Long> roleIdList = userRoleInfo.stream().map(y -> {
            return y.getRoleId();
        }).collect(Collectors.toList());
        return Result.success(roleIdList);
    }

    /**
     * 校验直属领导
     *
     * @param leaderUserId
     * @param userId
     */
    private void checkLeaderUser(Long leaderUserId, Long userId) {
        if (leaderUserId == null || CommonConstant.DEFAULT_ID.equals(leaderUserId)) {
            return;
        }
        Assert.isTrue(!userId.equals(leaderUserId), "当前用户属于选择的直属领导的上级，存在死循环");
        OrgUserModel leaderUser = this.selectById(leaderUserId);
        if (leaderUser != null) {
            this.checkLeaderUser(leaderUser.getLeaderUserId(), userId);
        }
    }

    /**
     * 更新用户基本信息
     *
     * @param userBaseInfo 用户基本信息更新vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserBaseInfo(UserBaseInfoUpdateVo userBaseInfo) {
        Long userId = userBaseInfo.getId();
        OrgUserModel user = this.selectById(userId);
        Assert.notNull(user, "用户信息不存在");
        OrgUserModel userModel = new OrgUserModel();
        BeanUtils.copyProperties(userBaseInfo, userModel);
        // 校验密码
        String oldPassword = userBaseInfo.getOldPassword();
        Assert.isTrue(user.getPassword().equals(oldPassword), "原密码输入错误");
        String newPassword = userBaseInfo.getNewPassword();
        // 获取系统密码安全策略
        userModel.setPassword(newPassword);
        userModel.setModifyPasswordTime(new Date());
        if (this.updateById(userModel) < 1) {
            return false;
        }
        // 更新元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(userBaseInfo.getId());
        elementModel.setName(userBaseInfo.getUserName());
        elementModel.setStatus(userBaseInfo.getStatus());
        int flag = elementService.updateById(elementModel);
        return flag > 0 ? true : false;
    }

    /**
     * 更新用户所属的组织机构信息及关联的角色
     *
     * @param vo 更新用户所属的组织机构信息及关联的角色vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserOrganAndRole(UpdateUserOrganAndRoleVo vo) {
        Long userId = vo.getUserId();
        Long organId = vo.getOrganId();
        Assert.notNull(userId, "用户id不能为空");
        Assert.notNull(organId, "组织机构id不能为空");
        OrgUserModel user = this.selectById(userId);
        Assert.notNull(user, "用户信息不存在");
        OrgUserModel userModel = new OrgUserModel();
        userModel.setId(userId);
        OrgOrganModel organ = organService.getOrganById(organId);
        Assert.notNull(organ, "所属部门不存在");
        // 获取所属单位信息
        OrgOrganModel unit = organService.getDirectParentUnit(organ.getId());
        Assert.notNull(unit, "所属部门不属于任何单位");
        userModel.setOrganId(organId);
        userModel.setBoundOrgan(true);
        userModel.setTenantId(unit.getId()); // 保存用户时需要设置租户id
        if (this.updateById(userModel) < 1) {
            return false;
        }
//        Long oldOrganId = user.getOrganId();
//        if (oldOrganId != null && !organId.equals(oldOrganId)) { // 变更了组织机构
//            List<OrgPostModel> postModelList = postService.getPostListByOrganId(oldOrganId);
//            if (CollectionUtil.isNotEmpty(postModelList)) {
//                List<Long> oldPostIdList = postModelList.stream().map(OrgPostModel::getId).collect(Collectors.toList());
//                // 批量删除用户岗位关联信息
//                userPostService.batchDeleteUserPost(userId, oldPostIdList);
//            }
//            List<OrgRoleModel> roleModelList = roleService.getRoleListByUnitId(oldOrganId, null);
//            if (CollectionUtil.isNotEmpty(roleModelList)) {
//                List<Long> oldRoleIdList = roleModelList.stream().map(OrgRoleModel::getId).collect(Collectors.toList());
//                // 批量删除用户角色关联信息
//                userRoleService.batchDeleteUserRole(userId, oldRoleIdList);
//            }
//        }
//        // 批量删除用户角色关联信息
//        userRoleService.batchDeleteUserRole(userId);
//        // 批量保存用户角色关联信息
//        if (CollectionUtil.isNotEmpty(roleIdList)) {
//            userRoleService.batchSaveUserRole(userId, roleIdList);
//        }
        // 更新元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(userId);
        elementModel.setParentId(organId);
        elementModel.setTreeIds(organ.getTreeIds() + CommonConstant.SEPARATOR_COMMA + userId);
        elementModel.setTenantId(userModel.getTenantId()); // 保存用户对应的元素时需要设置租户id
        int flag = elementService.updateById(elementModel);
        return flag > 0 ? true : false;
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteUser(@NotNull(message = "id不能为空") Long id) {
        OrgUserModel user = this.selectById(id);
        Assert.notNull(user, "用户信息不存在");
        OrgUserModel currentUser = UserContext.getCurrentUser().getUser();
        if (!CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(currentUser.getType())) { // 不是超级管理员
            Assert.isTrue(!CommonConstant.USER_TYPE.UNIT_ADMIN.getValue().equals(user.getType()), "没有权限删除单位管理员账号");
        }
        // 批量删除用户岗位关联信息
        userPostService.batchDeleteUserPost(id);
        // 批量删除用户角色关联信息
        userRoleService.batchDeleteUserRole(id);
        // 删除元素
        elementService.deleteElement(id);
        return this.deleteById(id);
    }

    /**
     * 批量导入用户
     *
     * @param file
     * @param organId
     * @param roleIdList
     * @return
     */
    public Result batchImportUser(MultipartFile file, Long organId, List<Long> roleIdList) {
        Assert.notNull(organId, "组织机构id不能为空");
        Assert.notEmpty(roleIdList, "角色不能为空");
        // 检查前台数据合法性
        if (null == file || file.isEmpty()) {
            return Result.failure("不能上传空文件!");
        }
        ZipSecureFile.setMinInflateRatio(0.01);
        OrgOrganModel organ = organService.getOrganById(organId);
        Assert.notNull(organ, "组织机构不存在");
        ImportParams importParams = new ImportParams();
        importParams.setTitleRows(1);
        importParams.setHeadRows(1);
        importParams.setNeedVerify(true);
        List<ImportUserDto> userDtos = null;
        try {
            userDtos = ExcelImportUtil.importExcel(file.getInputStream(), ImportUserDto.class, importParams);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("导入失败，读取文件出错：" + e.getMessage());
        }
        if (CollectionUtil.isEmpty(userDtos)) {
            return Result.failure("未读取到用户信息");
        }
        StringBuffer errorMassage = new StringBuffer();
        int importUserCount = 0;
        for (ImportUserDto dto : userDtos) {
            OrgUserModel userModel = new OrgUserModel();
            userModel.setUserName(dto.getName());
            userModel.setLoginName(dto.getLoginName());
            userModel.setSex(CommonConstant.SEX.MAN.getName().equals(dto.getSex()) ? CommonConstant.SEX.MAN.getValue() : CommonConstant.SEX.WOMAN.getValue());
            userModel.setIdCardNo(dto.getIdCardNo());
            userModel.setTel(dto.getTel());
            userModel.setBoundOrgan(false);
            if (StrUtil.isNotEmpty(dto.getTel())) { // 默认密码为手机号后6位，如果手机号信息少于6位，则使用默认密码
                String tel = dto.getTel().trim();
                if (tel.length() >= 6) {
                    userModel.setPassword(SecureUtil.md5(tel.substring(tel.length() - 6)));
                }
            }
            userModel.setRemark(dto.getRemark());
            userModel.setOrganId(organId);
            userModel.setRoleIdList(roleIdList);
            userModel.setStatus(CommonConstant.USE_STATUS.USE.getValue());
            if(StrUtil.isBlank(userModel.getAttrString02())){
                userModel.setAttrString02(StrUtil.uuid());
            }
            try {
                Result<OrgUserModel> result = this.saveUser(userModel);
                if (HttpStatus.OK.value() != result.getCode()) {
                    errorMassage.append("[" + dto.getName() + "]导入失败");
                } else {
                    importUserCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + dto.getName() + "]导入失败，出错信息：" + e.getMessage() + "。");
                errorMassage.append("[" + dto.getName() + "]导入失败，出错信息：" + e.getMessage() + "。");
            }
        }
        if (importUserCount > 0) {
            if (importUserCount < userDtos.size()) {
                return Result.success(HttpStatus.OK.value(), "导入成功。" + errorMassage.toString(), null);
            } else {
                return Result.success("导入成功");
            }
        } else {
            return Result.failure("导入失败。" + errorMassage.toString());
        }
    }

    /**
     * 批量导入用户
     *
     * @param file
     * @param appId
     * @return
     */
    public Result batchImportUnitUserRole(MultipartFile file, Long appId) {
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
        // 读取用户信息
        List<ExcelUserDto> userDtoList = null;
        try {
            importParams.setStartSheetIndex(0);
            userDtoList = ExcelImportUtil.importExcel(file.getInputStream(), ExcelUserDto.class, importParams);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("导入失败，读取文件出错：" + e.getMessage());
        }
        StringBuffer errorMassage = new StringBuffer();
        ExcelImportResultDto userResult = this.batchImportUser(userDtoList);
        errorMassage.append(userResult.getMessage());
        List<Long> userIdList = (List<Long>) userResult.getResult();
        if (userResult.isSuccess()) {
            return Result.success(HttpStatus.OK.value(), errorMassage.toString(), userIdList);
        } else {
            return Result.failure(errorMassage.toString());
        }
    }

    /**
     * 批量导入用户
     *
     * @param userDtoList
     * @return
     */
    public ExcelImportResultDto batchImportUser(List<ExcelUserDto> userDtoList) {
        if (CollectionUtil.isEmpty(userDtoList)) {
            return new ExcelImportResultDto(false, "用户导入失败。未获取到用户信息。", null);
        }
        int importCount = 0;
        StringBuffer errorMassage = new StringBuffer();
        Map<String, Long> organNameAndId = new HashMap<>();
        Map<String, Long> roleNameAndId = new HashMap<>();
        List<Long> userIdList = new ArrayList<>();
        for (ExcelUserDto dto : userDtoList) {
            String name = dto.getName();
            //去除姓名中的空格
            name = StrUtil.cleanBlank(name);
            try {
                OrgUserModel user = new OrgUserModel();
                user.setUserName(name);
                user.setOrganId(this.getOrganIdByName(dto.getUnitName(), organNameAndId));
                //通过姓名、单位判断用户是否存在，存在更新，不存在插入
                //todo 存在暂不变动
                OrgUserModel hasUser = this.getUserByNameOrganId(user.getUserName(),user.getOrganId());
                if(ObjectUtil.isNotEmpty(hasUser)){
                    continue;
                }
                user.setSex(CommonConstant.SEX.MAN.getName().equals(dto.getSex()) ? CommonConstant.SEX.MAN.getValue() : CommonConstant.SEX.WOMAN.getValue());
                if(StrUtil.isNotBlank(dto.getLoginName())){
                    user.setLoginName(dto.getLoginName());
                }else {
                    user.setLoginName(StrUtil.cleanBlank(PinyinUtil.getPinyin(name,"")));
                }
                user.setRoleIdList(this.getRoleIdListByUnitAndName(user.getOrganId(), dto.getRoleName(), roleNameAndId));
                user.setRemark(dto.getRemark());
                user.setIdCardNo(dto.getSfzh());
                user.setStatus(CommonConstant.USE_STATUS.USE.getValue());
                if(StrUtil.isBlank(dto.getJzh())){
                    user.setAttrString02(StrUtil.uuid());
                }else {
                    user.setAttrString02(dto.getJzh());
                }
                user.setAttrString03(sysDictionaryService.getDictValByNameAndCode("ryzw", dto.getZw()));
                user.setAttrString04(sysDictionaryService.getDictValByNameAndCode("bdjx", dto.getJx()));
                user.setAttrString05(sysDictionaryService.getDictValByNameAndCode("rylx", dto.getRylx()));

                Result<OrgUserModel> result = this.saveUser(user);
                if (HttpStatus.OK.value() != result.getCode()) {
                    errorMassage.append("[" + name + "]导入失败");
                } else {
                    importCount++;
                    if (StrUtil.isNotEmpty(dto.getImage())) {
                        FileInputStream fileInputStream = null;
                        try {
                            Long userId = user.getId();
                            File tmpFile = new File(dto.getImage());
                            fileInputStream = new FileInputStream(tmpFile);
                            //转换为 multipartFile 类
                            MultipartFile multipartFile = new MockMultipartFile("file", tmpFile.getName(), "text/plain",
                                    IOUtils.toByteArray(fileInputStream));
                            Result attResult = attMainService.uploadNew(multipartFile, userId, "UserImage", "rlzp", "2");
                            if (HttpStatus.OK.value() != attResult.getCode()) {
                                errorMassage.append("[" + name + "]人脸图像导入失败，出错信息：" + attResult.getMessage());
                            } else {
                                userIdList.add(userId);
                                LinkedHashMap result1 = (LinkedHashMap) attResult.getResult();
                                Object filePath =  result1.get("filePath");
                                if(filePath != null){
                                    user.setHeadImageUrl(filePath.toString());
                                    this.updateUser(user);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMassage.append("[" + name + "]人脸图像导入失败，出错信息：" + e.getMessage() + "。");
                        } finally {
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                fileInputStream = null;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + name + "]导入失败，出错信息：" + e.getMessage() + "。");
                errorMassage.append("[" + name + "]导入失败，出错信息：" + e.getMessage() + "。");
            }
        }
        if (importCount > 0) {
            if (importCount < userDtoList.size()) {
                return new ExcelImportResultDto(true, "用户导入成功。" + errorMassage.toString(), userIdList);
            } else {
                return new ExcelImportResultDto(true, "用户导入成功。", userIdList);
            }
        } else {
            return new ExcelImportResultDto(false, "用户导入失败。" + errorMassage.toString(), null);
        }
    }

    /**
     * 通过姓名、单位判断用户获取用户
     *
     * @param userName
     * @param organId
     * @return
     */
    private OrgUserModel getUserByNameOrganId(String userName, Long organId) {
        Assert.isTrue(StrUtil.isNotEmpty(userName), "姓名不能为空");
        Assert.notNull(organId, "organId不能为空");
        LambdaQueryWrapper<OrgUserModel> lam = new LambdaQueryWrapper<>();
        lam.eq(OrgUserModel::getUserName,userName).eq(OrgUserModel::getOrganId,organId)
                .last("limit 1");
        OrgUserModel orgUserModel = this.selectOne(lam);
        return orgUserModel;
    }


    /**
     * 根据组织机构名称获取id
     *
     * @param name
     * @param organNameAndId
     * @return
     */
    private Long getOrganIdByName(String name, Map<String, Long> organNameAndId) {
        Assert.isTrue(StrUtil.isNotEmpty(name), "所属单位名称不能为空");
        Long organId = organNameAndId.get(name);
        if (organId == null) {
            OrgOrganModel organ = organService.getOrganByName(name);
            Assert.notNull(organ, "所属单位名称[" + name + "]对应的单位信息不存在");
            organNameAndId.put(name, organ.getId());
            organId = organ.getId();
        }
        return organId;
    }

    /**
     * 根据角色名称获取id
     *
     * @param name
     * @param roleNameAndId
     * @return
     */
    private List<Long> getRoleIdListByUnitAndName(Long unitId, String name, Map<String, Long> roleNameAndId) {
        Assert.isTrue(StrUtil.isNotEmpty(name), "关联角色名称不能为空");
        String[] nameArray = name.split(CommonConstant.SEPARATOR_COMMA);
        List<Long> roleIdList = new ArrayList<>();
        for (String roleName : nameArray) {
            Long roleId = roleNameAndId.get(unitId + "-" + roleName);
            if (roleId == null) {
                OrgRoleModel role = roleService.getRoleByUnitIdAndName(unitId,name);
                Assert.notNull(role, "关联角色名称[" + name + "]对应的角色信息不存在");
                roleNameAndId.put(unitId + "-"+name, role.getId());
                roleId = role.getId();
            }
            roleIdList.add(roleId);
        }
        return roleIdList;
    }

    /**
     * 根据角色名称获取id
     *
     * @param name
     * @param roleNameAndId
     * @return
     */
    private List<Long> getRoleIdListByName(String name, Map<String, Long> roleNameAndId) {
        Assert.isTrue(StrUtil.isNotEmpty(name), "关联角色名称不能为空");
        String[] nameArray = name.split(CommonConstant.SEPARATOR_COMMA);
        List<Long> roleIdList = new ArrayList<>();
        for (String roleName : nameArray) {
            Long roleId = roleNameAndId.get(roleName);
            if (roleId == null) {
                OrgRoleModel role = roleService.getRoleByName(name);
                Assert.notNull(role, "关联角色名称[" + name + "]对应的角色信息不存在");
                roleNameAndId.put(name, role.getId());
                roleId = role.getId();
            }
            roleIdList.add(roleId);
        }
        return roleIdList;
    }

    /**
     * 分页查询角色关联的用户列表
     *
     * @param pageSearchParam 分页查询内容
     * @return 用户分页列表
     */
    public Result<IPage<OrgUserModel>> selectUserListPageByRole(PageSearchParam pageSearchParam) {
        Map<String, BaseQueryValue> queryPrams = pageSearchParam.getQueryPrams();
        String paramRoleId = "role_id";
        if (!queryPrams.containsKey(paramRoleId)) {
            return Result.failure(paramRoleId + "不能为空");
        }
        BaseQueryValue query_roleId = queryPrams.get(paramRoleId);
        Object[] values_roleId = query_roleId.getValues();
        if (values_roleId == null || values_roleId.length == 0) {
            return Result.failure(paramRoleId + "不能为空");
        }
        Long roleId = Long.parseLong(values_roleId[0].toString());
        String userName = null;
        if (queryPrams.containsKey("user_name")) {
            BaseQueryValue query_userName = queryPrams.get("user_name");
            Object[] values_userName = query_userName.getValues();
            if (values_userName != null && values_userName.length > 0) {
                userName = values_userName[0].toString();
            }
        }
        String sortField = pageSearchParam.getSortField();
        if (StrUtil.isNotEmpty(sortField)) {
            if (sortField.equals("createTime")) {
                pageSearchParam.setSortField("create_time");
            }
        }
        Page<OrgUserModel> mppage = new Page<>(pageSearchParam.getPage(), pageSearchParam.getLimit());
        IPage<OrgUserModel> iPage = dao.selectPageByRole(mppage, pageSearchParam, roleId, userName);
        return Result.success(iPage);
    }

    /**
     * 获取组织机构下当前用户可授权的角色列表
     *
     * @param organId 组织机构id
     * @return 角色实体列表
     */
    public List<OrgRoleModel> getRoleListByOrganAndCurrentUser(@NotNull(message = "organId不能为空") Long organId) {
        OrgUserModel user = UserContext.getCurrentUser().getUser();
        OrgOrganModel unit = organService.getDirectParentUnit(organId);
        if (unit == null) {
            return new ArrayList<>();
        }
        Long unitId = unit.getId();
        List<OrgRoleModel> roleList = new ArrayList<>();
        // 默认增加单位管理员角色
        //todo 需要初始化单位管理员角色
        OrgRoleModel unitRole = roleService.selectById(CommonConstant.ROLE_ID_UNIT_ADMIN);
        roleList.add(unitRole);
        if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
            roleList.addAll(roleService.getRoleListByUnitId(unitId, null));
            return roleList;
        } else {
            // 用户的角色列表
            List<OrgRoleModel> userRoleList = roleService.getRoleListByUserAndApp(user.getId(), null);
            if (CollectionUtil.isNotEmpty(userRoleList)) {
                for (OrgRoleModel haveRole : userRoleList) {
                    if (unitId.equals(haveRole.getOrganId())) {
                        roleList.add(haveRole);
                    }
                }
            }
            // 创建的角色列表
            List<OrgRoleModel> createRoleList = roleService.getRoleListByCreateUserAndApp(user.getId(), null);
            if (CollectionUtil.isNotEmpty(createRoleList)) {
                for (OrgRoleModel createRole : createRoleList) {
                    if (unitId.equals(createRole.getOrganId())) {
                        roleList.add(createRole);
                    }
                }
            }
            // 单位的角色列表
            if (CommonConstant.USER_TYPE.UNIT_ADMIN.getValue().equals(user.getType())) { // 单位管理员
                List<OrgRoleModel> unitRoleList = roleService.getRoleListByUnitId(unitId, null);
                if (CollectionUtil.isNotEmpty(unitRoleList)) {
                    roleList.addAll(unitRoleList);
                }
            }
            // 删除重复的角色
            roleList = roleList.stream().distinct().collect(Collectors.toList());
            return roleList;
        }
    }

    /**
     * 获取组织机构所属单位下的用户列表
     *
     * @param organId 组织机构id
     * @return 用户实体列表
     */
    public List<OrgUserModel> getUserListByOrgan(@NotNull(message = "organId不能为空") Long organId) {
        OrgOrganModel unit = organService.getDirectParentUnit(organId);
        if (unit == null) {
            return new ArrayList<>();
        }
        return this.getUserListByOrganId(unit.getId());
    }

    /**
     * 获取组织机构下的用户列表
     *
     * @param organId 组织机构id
     * @return 用户实体列表
     */
    public List<OrgUserModel> getUserListByOrganId(@NotNull(message = "organId不能为空") Long organId) {
        LambdaQueryWrapper<OrgElementModel> elementWrapper = new LambdaQueryWrapper<>();
        elementWrapper.like(OrgElementModel::getTreeIds, organId);
        elementWrapper.eq(OrgElementModel::getType, CommonConstant.ELEMENT_TYPE.USER.getValue());
        elementWrapper.orderByAsc(OrgElementModel::getName);
        List<OrgElementModel> elementList = elementMapper.selectList(elementWrapper);
        if (CollectionUtil.isEmpty(elementList)) {
            return new ArrayList<>();
        }
        List<Long> elementIdList = elementList.stream().map(OrgElementModel::getId).collect(Collectors.toList());
        LambdaQueryWrapper<OrgUserModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrgUserModel::getId, elementIdList);
        wrapper.orderByAsc(OrgUserModel::getUserName);
        return dao.selectList(wrapper);
    }

    /**
     * 获取组织机构所属单位下的用户列表（排除userId对应的用户）
     *
     * @param organId 组织机构id
     * @param userId  用户id
     * @return 用户实体列表
     */
    public List<OrgUserModel> getUserListByUnit(@NotNull(message = "organId不能为空") Long organId, Long userId) {
        OrgOrganModel unit = organService.getDirectParentUnit(organId);
        if (unit == null) {
            return new ArrayList<>();
        }
        List<OrgUserModel> userList = this.getUserListByOrganId(unit.getId(), false);
        // 排除userId对应的用户
        if (CollectionUtil.isNotEmpty(userList) && userId != null) {
            userList = userList.stream().filter(user -> !userId.equals(user.getId())).collect(Collectors.toList());
        }
        return userList;
    }

    /**
     * 根据id获取用户信息
     *
     * @param id 主键
     * @return
     */
    public OrgUserModel getUserById(Long id) {
        if (id == null) {
            return null;
        }
        return this.selectById(id);
    }

    /**
     * 根据应用编码获取用户列表（应用-角色-用户）
     *
     * @param appCode 应用编码
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByApp(String appCode) {
        if (StrUtil.isEmpty(appCode)) {
            return new ArrayList<>();
        }
        List<OrgRoleModel> roleList = roleService.getRoleListByAppCode(appCode);
        if (CollectionUtil.isEmpty(roleList)) {
            return new ArrayList<>();
        }
        List<Long> roleIdList = new ArrayList<>();
        for (OrgRoleModel role : roleList) {
            roleIdList.add(role.getId());
        }
        List<OrgUserRoleModel> userRoleList = userRoleService.getUserRoleListByRoleIdList(roleIdList);
        if (CollectionUtil.isEmpty(userRoleList)) {
            return new ArrayList<>();
        }
        List<Long> userIdList = new ArrayList<>();
        for (OrgUserRoleModel userRole : userRoleList) {
            userIdList.add(userRole.getUserId());
        }
        if (CollectionUtil.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<OrgUserModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrgUserModel::getId, userIdList);
        return dao.selectList(wrapper);
    }

    /**
     * 根据元素列表获取关联的用户列表
     *
     * @param elementList 元素列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByElementList(List<OrgElementModel> elementList) {
        if (CollectionUtil.isEmpty(elementList)) {
            return new ArrayList<>();
        }
        // 对元素进行分类
        Map<String, List<Long>> elementMap = new HashMap<>();
        for (OrgElementModel element : elementList) {
            if (StrUtil.isEmpty(element.getType())) {
                continue;
            }
            if (elementMap.containsKey(element.getType())) {
                elementMap.get(element.getType()).add(element.getId());
            } else {
                List<Long> elementIdList = new ArrayList<>();
                elementIdList.add(element.getId());
                elementMap.put(element.getType(), elementIdList);
            }
        }
        if (CollectionUtil.isEmpty(elementMap)) {
            return new ArrayList<>();
        }
        // 查询关联的用户列表
        List<OrgUserModel> oldUserList = new ArrayList<>();
        for (Map.Entry<String, List<Long>> entry : elementMap.entrySet()) {
            oldUserList.addAll(this.getUserListByElement(entry.getKey(), entry.getValue()));
        }
        // 去除重复的用户信息
        Set<Long> userIdSet = new HashSet<>();
        List<OrgUserModel> userList = new ArrayList<>();
        for (OrgUserModel user : oldUserList) {
            if (user == null) {
                continue;
            }
            if (!userIdSet.contains(user.getId())) {
                userList.add(user);
                userIdSet.add(user.getId());
            }
        }
        return userList;
    }

    private List<OrgUserModel> getUserListByElement(String elementType, List<Long> elementIdList) {
        if (CommonConstant.ELEMENT_TYPE.UNIT.getValue().equals(elementType)) {
            return this.getUserListByUnitIdList(elementIdList);
        } else if (CommonConstant.ELEMENT_TYPE.DEPT.getValue().equals(elementType)) {
            return this.getUserListByDeptIdList(elementIdList);
        } else if (CommonConstant.ELEMENT_TYPE.POST.getValue().equals(elementType)) {
            return this.getUserListByPostIdList(elementIdList);
        } else if (CommonConstant.ELEMENT_TYPE.USER.getValue().equals(elementType)) {
            return this.getUserListByUserIdList(elementIdList);
        } else if (CommonConstant.ELEMENT_TYPE.ROLE.getValue().equals(elementType)) {
            return this.getUserListByRoleIdList(elementIdList);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 根据单位id列表获取单位关联的用户列表
     *
     * @param unitIdList 单位id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByUnitIdList(List<Long> unitIdList) {
        if (CollectionUtil.isEmpty(unitIdList)) {
            return new ArrayList<>();
        }
        return dao.getUserListByOrganIdList(unitIdList);
    }

    /**
     * 根据部门id列表获取部门关联的用户列表
     *
     * @param deptIdList 部门id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByDeptIdList(List<Long> deptIdList) {
        if (CollectionUtil.isEmpty(deptIdList)) {
            return new ArrayList<>();
        }
        return dao.getUserListByOrganIdList(deptIdList);
    }

    /**
     * 根据角色id列表获取角色关联的用户列表
     *
     * @param roleIdList 角色id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByRoleIdList(List<Long> roleIdList) {
        if (CollectionUtil.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        List<OrgUserRoleModel> userRoleList = userRoleService.getUserRoleListByRoleIdList(roleIdList);
        if (CollectionUtil.isEmpty(userRoleList)) {
            return new ArrayList<>();
        }
        List<Long> userIdList = new ArrayList<>();
        for (OrgUserRoleModel userRole : userRoleList) {
            userIdList.add(userRole.getUserId());
        }
        return this.getUserListByUserIdList(userIdList);
    }

    /**
     * 根据岗位id列表获取岗位关联的用户列表
     *
     * @param postIdList 岗位id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByPostIdList(List<Long> postIdList) {
        if (CollectionUtil.isEmpty(postIdList)) {
            return new ArrayList<>();
        }
        List<OrgUserPostModel> userPostList = userPostService.getUserPostListByPostIdList(postIdList);
        if (CollectionUtil.isEmpty(userPostList)) {
            return new ArrayList<>();
        }
        List<Long> userIdList = new ArrayList<>();
        for (OrgUserPostModel userPost : userPostList) {
            userIdList.add(userPost.getUserId());
        }
        return this.getUserListByUserIdList(userIdList);
    }

    /**
     * 根据当前登录用户获取所属部门负责人
     *
     * @return 所属部门负责人
     */
    public OrgUserModel getDepartmentHeadByCurrentUser() {
        // 获取用户信息线程上下文
        UserDetails userDetails = UserContext.getCurrentUser();
        OrgUserModel user = userDetails.getUser();
        OrgOrganModel organ = organService.getOrganById(user.getOrganId());
        if (organ == null || organ.getDeptHeadId() == null) {
            return null;
        }
        return this.selectById(organ.getDeptHeadId());
    }

    /**
     * 根据当前登录用户获取直属上级
     *
     * @return 直属上级
     */
    public OrgUserModel getDirectLeaderByCurrentUser() {
        // 获取用户信息线程上下文
        UserDetails userDetails = UserContext.getCurrentUser();
        OrgUserModel user = userDetails.getUser();
        Long leaderUserId = user.getLeaderUserId();
        if (leaderUserId == null) {
            return null;
        }
        return this.selectById(leaderUserId);
    }

    /**
     * 根据当前登录用户获取连续多级主管列表
     * 例如：参数为2，返回当前登录用户的直属上级，直属上级的直属上级
     *
     * @param level 级别
     * @return 连续多级主管列表
     */
    public List<OrgUserModel> getLeaderListByCurrentUser(int level) {
        List<OrgUserModel> userList = new LinkedList<>();
        if (level <= 0) {
            return userList;
        }
        // 获取用户信息线程上下文
        UserDetails userDetails = UserContext.getCurrentUser();
        OrgUserModel user = userDetails.getUser();
        for (int i = 1; i <= level && user.getLeaderUserId() != null; i++) {
            user = this.selectById(user.getLeaderUserId());
            if (user == null) {
                break;
            }
            userList.add(user);
        }
        return userList;
    }

    /**
     * 统计有效用户数量
     *
     * @return 有效用户数量
     */
    public Long getValidUserNumber() {
        LambdaQueryWrapper<OrgUserModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserModel::getStatus, CommonConstant.USE_STATUS.USE.getValue());
        return this.selectCount(wrapper);
    }

    /**
     * 分页查询有效用户
     *
     * @param pageSearchParam 分页查询内容
     * @return 有效用户实体分页
     */
    public List<OrgUserModel> selectUserPage(PageSearchParam pageSearchParam) {
        if (pageSearchParam == null) {
            return new ArrayList<>();
        }
        BaseQueryValue queryStatus = new BaseQueryValue();
        queryStatus.setOpt(SearchOptConstant.SEARCH_EQUAL);
        queryStatus.setValues(new Object[]{CommonConstant.USE_STATUS.USE.getValue()});
        pageSearchParam.addQueryParams("status", queryStatus);
        Result<IPage<OrgUserModel>> result = this.selectPage(pageSearchParam);
        return result.getResult().getRecords();
    }

    /**
     * 查询组织机构下的所有有效用户
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    public List<OrgUserModel> getUserListByOrganId(Long organId, boolean containSubUnit) {
        if (organId == null) {
            return new ArrayList<>();
        }
        if (containSubUnit) { // 包含下级单位用户
            return dao.getUserListByOrganIdList(Arrays.asList(organId));
        }
        OrgOrganModel organModel = organService.getOrganById(organId);
        if (organModel == null) {
            return new ArrayList<>();
        }
        if (CommonConstant.ORGAN_TYPE.UNIT.getValue().equals(organModel.getType())) { // 组织机构为单位
            LambdaQueryWrapper<OrgUserModel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrgUserModel::getTenantId, organId);
            queryWrapper.eq(OrgUserModel::getStatus, CommonConstant.USE_STATUS.USE.getValue());
            queryWrapper.orderByAsc(OrgUserModel::getUserName);
            return dao.selectList(queryWrapper);
        } else { // 组织机构为部门
            return dao.getUserListByOrganIdList(Arrays.asList(organId));
        }
    }

    /**
     * 查询组织机构下的所有有效用户-剔除自动创建的单位管理员和超管
     *
     * @param organId        组织机构id
     * @param containSubUnit 是否包含下级单位用户
     * @return 有效用户实体列表
     */
    @Override
    public List<OrgUserModel> getUserListByOrganIdRemoveUnitAdmin(Long organId, boolean containSubUnit) {
        if (organId == null) {
            return new ArrayList<>();
        }
        if (containSubUnit) {
            // 包含下级单位用户
            return dao.getUserListByOrganIdListRemoveAdmin(Arrays.asList(organId));
        }
        OrgOrganModel organModel = organService.getOrganById(organId);
        if (organModel == null) {
            return new ArrayList<>();
        }
        if (CommonConstant.ORGAN_TYPE.UNIT.getValue().equals(organModel.getType())) {
            // 组织机构为单位
            LambdaQueryWrapper<OrgUserModel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrgUserModel::getTenantId, organId);
            queryWrapper.eq(OrgUserModel::getStatus, CommonConstant.USE_STATUS.USE.getValue());
            //剔除超管和单位管理员
            queryWrapper.notIn(OrgUserModel::getType, CommonConstant.USER_TYPE.UNIT_ADMIN.getValue(),CommonConstant.USER_TYPE.SUPER_ADMIN.getValue());
            queryWrapper.orderByAsc(OrgUserModel::getUserName);
            return dao.selectList(queryWrapper);
        } else { // 组织机构为部门
            return dao.getUserListByOrganIdListRemoveAdmin(Arrays.asList(organId));
        }
    }

    /**
     * 根据用户id列表获取用户列表
     *
     * @param userIdList 用户id列表
     * @return 用户列表
     */
    public List<OrgUserModel> getUserListByUserIdList(List<Long> userIdList) {
        if (CollectionUtil.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<OrgUserModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrgUserModel::getId, userIdList);
        return dao.selectList(wrapper);
    }

    /**
     * 锁屏
     *
     * @return
     */
    @Override
    public Result lockScreen() {
        if (UserContext.getCurrentUserId() != null) {
            Long userId = UserContext.getCurrentUserId();
            OrgUserModel user = this.selectById(userId);
            if (user != null) {
                user.setLockScreen(true);
                if (this.updateById(user) == 1) {
                    return Result.success();
                }
            }
            return Result.failure();
        }
        return Result.failure();
    }


    /**
     * 获取指定单位用户数
     *
     * @param unitIds 指定单位用户数量
     * @return
     */
    @Override
    public List<UnitUserNumVo> getUnitUserNum(List<Long> unitIds) {
        List<UnitUserNumVo> list = new ArrayList<>();
        //查询对应用户
        List<OrgUserModel> users = this.getAllUnitUser(unitIds);
        if (CollectionUtil.isNotEmpty(users)) {
            Map<Long, List<OrgUserModel>> userMap = users.stream().collect(Collectors.groupingBy(OrgUserModel::getTenantId, Collectors.toList()));
            for (Long key : userMap.keySet()) {
                UnitUserNumVo unitUserNumVo = new UnitUserNumVo();
                unitUserNumVo.setUnitId(key);
                unitUserNumVo.setUserNum(userMap.get(key).size());
                list.add(unitUserNumVo);
            }
        }
        return list;
    }

    /**
     * 查询组织机构下的所有有效用户-不包含超管和单位管理员
     *
     * @param unitIds 指定单位用户数量
     * @return
     */
    @Override
    public Integer getAllUnitUserNum(List<Long> unitIds) {
        List<OrgUserModel> users = this.getAllUnitUserRemoveAdmin(unitIds);
        return users.size();
    }

    /**
     * 获取指定某些单位用户
     *
     * @param unitIds 指定单位用户数量
     * @return
     */
    public List<OrgUserModel> getAllUnitUser(List<Long> unitIds) {
        LambdaQueryWrapper<OrgUserModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrgUserModel::getTenantId, unitIds);
        queryWrapper.eq(OrgUserModel::getStatus, CommonConstant.USE_STATUS.USE.getValue());
        //查询对应用户
        List<OrgUserModel> users = this.selectList(queryWrapper);
        return users;
    }


    /**
     * 获取指定某些单位用户-不包含超管和单位管理员
     *
     * @param unitIds 指定单位用户数量
     * @return
     */
    public List<OrgUserModel> getAllUnitUserRemoveAdmin(List<Long> unitIds) {
        LambdaQueryWrapper<OrgUserModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrgUserModel::getTenantId, unitIds);
        queryWrapper.eq(OrgUserModel::getStatus, CommonConstant.USE_STATUS.USE.getValue());
        //提出超管和单位管理员
        queryWrapper.notIn(OrgUserModel::getType, CommonConstant.USER_TYPE.UNIT_ADMIN.getValue(),CommonConstant.USER_TYPE.SUPER_ADMIN.getValue());
        //查询对应用户
        List<OrgUserModel> users = this.selectList(queryWrapper);
        return users;
    }


    /**
     * 更新同步样本状态
     *
     * @param unitId
     * @param state
     * @return
     */

    public Boolean updateThStateByUnitId(Long unitId, String state) {
        List<OrgUserModel> users = this.getUserListByOrganId(unitId, false);
        if (CollectionUtil.isNotEmpty(users)) {
            users = users.stream().filter(y -> Strings.isNotEmpty(y.getFeature1())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(users)) {
                users.stream().forEach(y -> {
                    y.setRockField(state);
                });
                return this.batchUpdateNoCascade(users);
            }
        }
        return true;
    }

    /**
     * 根据登入名查找用户信息
     *
     * @param loginName
     * @return
     */
    public OrgUserModel loadLoginName(String loginName) {
        return this.selectOne(new LambdaQueryWrapper<OrgUserModel>().eq(OrgUserModel::getLoginName, loginName));
    }

    /**
     * 根据军人证件号获取用户信息
     *
     * @param attrString02 军人证件号
     * @return
     */
    @Override
    public OrgUserModel getUserByAttrString02(String attrString02) {
        LambdaQueryWrapper<OrgUserModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserModel::getAttrString02, attrString02);
        wrapper.orderByDesc(OrgUserModel::getCreateTime);
        wrapper.last("limit 1");
        return this.selectOne(wrapper);
    }

}
