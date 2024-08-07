package pro.wuan.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import feign.BusFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.wuan.common.auth.util.JwtUtils;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.utils.ConvertUtil;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.common.mq.constant.GroupEnum;
import pro.wuan.common.mq.constant.TagEnum;
import pro.wuan.common.mq.model.MqMessage;
import pro.wuan.common.mq.service.MqProducerService;
import pro.wuan.constant.UserOnlineStatus;
import pro.wuan.feignapi.appapi.OrgAppModel;
import pro.wuan.feignapi.dictapi.constant.SendWayEnum;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.service.IOrgAppService;
import pro.wuan.user.entity.OrgUserLoginStatusModel;
import pro.wuan.user.mapper.OrgUserLoginStatusMapper;
import pro.wuan.user.service.IOrgUserLoginStatusService;
import pro.wuan.user.service.IOrgUserService;
import pro.wuan.utils.UserAgentUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线用户
 * @program: tellhowcloud
 * @author huangtianji
 * @create 2022-10-11 09:35:17
 */
@Service("orgUserLoginStatusService")
public class OrgUserLoginStatusServiceImpl extends BaseServiceImpl<OrgUserLoginStatusMapper, OrgUserLoginStatusModel> implements IOrgUserLoginStatusService {

    @Lazy
    @Resource
    private BusFeignClient busFeignClient;

    @Autowired
    private IOrgUserService orgUserService;

    @Autowired
    private MqProducerService mqProducerService;

    @Autowired
    private IOrgAppService orgAppService;

    @Value("${spring.application.name}")
    private String appCode;

    /**
     * 更新在线用户的信息
     * */
    @Override
    @Transactional
    public void updateUserStatus() {
        //每次查询列表更新在线用户信息
        Result<List<String>> userResult = busFeignClient.getAllUser();
        if(userResult.isSuccess()){
            List<String> onlineUserIds = userResult.getResult();
            if(onlineUserIds != null && onlineUserIds.size() > 0){
                List<Long> userIds = onlineUserIds.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                OrgUserLoginStatusModel userLoginStatusModel = new OrgUserLoginStatusModel();
                userLoginStatusModel.setLoginStatus(UserOnlineStatus.ONLINE);
                LambdaQueryWrapper<OrgUserLoginStatusModel> queryWrapper = new LambdaQueryWrapper();
                queryWrapper.in(OrgUserLoginStatusModel::getUserId,userIds);
                this.update(userLoginStatusModel,queryWrapper);
                OrgUserLoginStatusModel userLoginStatusModel1 = new OrgUserLoginStatusModel();
                userLoginStatusModel1.setLoginStatus(UserOnlineStatus.NOT_ONLINE);
                LambdaQueryWrapper<OrgUserLoginStatusModel> queryWrapper1 = new LambdaQueryWrapper();
                queryWrapper1.notIn(OrgUserLoginStatusModel::getUserId,userIds);
                this.update(userLoginStatusModel1,queryWrapper1);
            }else {
                OrgUserLoginStatusModel userLoginStatusModel = new OrgUserLoginStatusModel();
                userLoginStatusModel.setLoginStatus(UserOnlineStatus.NOT_ONLINE);
                LambdaQueryWrapper<OrgUserLoginStatusModel> queryWrapper = new LambdaQueryWrapper();
                this.update(userLoginStatusModel,queryWrapper);
            }
        }
    }

    /**
     * 在线用户-批量下线用户
     * @param userIds
     * */
    @Override
    @Transactional
    public void shutdownBatchIds(List<Long> userIds) {
        OrgUserLoginStatusModel userLoginStatusModel = new OrgUserLoginStatusModel();
        userLoginStatusModel.setLoginStatus(UserOnlineStatus.NOT_ONLINE);
        LambdaQueryWrapper<OrgUserLoginStatusModel> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(OrgUserLoginStatusModel::getUserId,userIds);
        this.update(userLoginStatusModel,queryWrapper);
        //禁用用户
        OrgUserModel orgUserModel = new OrgUserModel();
        orgUserModel.setStatus(0);
        LambdaQueryWrapper<OrgUserModel> queryWrapper1 = new LambdaQueryWrapper();
        queryWrapper1.in(OrgUserModel::getId,userIds);
        orgUserService.update(orgUserModel,queryWrapper1);
        //删除token
        //发送MQ消息
        OrgAppModel orgAppModel = orgAppService.getAppByCode(appCode);
        List<OrgUserModel> orgUserModels = orgUserService.selectBatchIds(userIds);
        for(OrgUserModel userModel : orgUserModels){
            JwtUtils.setTokenInvalid(userModel.getLoginName());
            if(orgAppModel != null){
                MqMessage mqMessage = new MqMessage();
                mqMessage.setKey(userModel.getId().toString());
                mqMessage.setToId(userModel.getId().toString());
                mqMessage.setTitle("用户登录异常");
                mqMessage.setContent("用户登录异常管理员强制登出");
                mqMessage.setAppId(String.valueOf(orgAppModel.getId()));
                mqMessage.setType("tzxx");
                mqMessage.setNoticeType(SendWayEnum.WEB.getCode());
                mqMessage.setDetailUrl("kickout");
                mqMessage.setStatus("0");
                mqMessage.setSendTime(new Date());
                mqMessage.setGroupEnum(GroupEnum.SYSTOBUSGROUP);
                mqMessage.setTagEnum(TagEnum.NOTICE);
                mqProducerService.asynSendMsg(mqMessage);
                //发送ws给前端
                busFeignClient.sendMessageToUser(userModel.getId().toString(), ConvertUtil.objectToString(mqMessage));
            }
        }
    }

    /**
     * 用户登录
     * @param request
     * @param userId
     * */
    @Transactional
    @Override
    public void loginOrgUserLoginStatus(HttpServletRequest request, Long userId) {
        String browserName = UserAgentUtils.browserName(request);
        String osName = UserAgentUtils.osName(request);
        String ipAddress = UserAgentUtils.getIpAddress(request);
        String address = UserAgentUtils.getCityInfo(ipAddress);
        OrgUserLoginStatusModel model = this.getOrgUserLoginStatusModelByUserId(userId);
        OrgUserLoginStatusModel userLoginStatusModel = new OrgUserLoginStatusModel();
        OrgUserModel orgUserModel = orgUserService.selectById(userId);
        if(model == null){
            userLoginStatusModel.setLoginName(orgUserModel.getLoginName());
            userLoginStatusModel.setUserId(orgUserModel.getId());
            userLoginStatusModel.setUserName(orgUserModel.getUserName());
            userLoginStatusModel.setLoginStatus(UserOnlineStatus.ONLINE);
            userLoginStatusModel.setLoginIp(ipAddress);
            userLoginStatusModel.setLoginAddress(address);
            userLoginStatusModel.setLoginBrowser(browserName);
            userLoginStatusModel.setLoginOs(osName);
            userLoginStatusModel.setLoginTime(new Date());
            userLoginStatusModel.setTenantId(orgUserModel.getTenantId());
            this.insert(userLoginStatusModel);
        }else {
            userLoginStatusModel.setLoginStatus(UserOnlineStatus.ONLINE);
            userLoginStatusModel.setLoginIp(ipAddress);
            userLoginStatusModel.setLoginBrowser(browserName);
            userLoginStatusModel.setLoginOs(osName);
            userLoginStatusModel.setLoginAddress(address);
            userLoginStatusModel.setLoginTime(new Date());
            userLoginStatusModel.setTenantId(orgUserModel.getTenantId());
            LambdaQueryWrapper<OrgUserLoginStatusModel> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(OrgUserLoginStatusModel::getUserId,userId);
            this.update(userLoginStatusModel,queryWrapper);
        }

    }

    /**
     * 用户登出
     * @param request
     * @param userId
     * */
    @Transactional
    @Override
    public void loginOutOrgUserLoginStatus(HttpServletRequest request, Long userId) {
        OrgUserLoginStatusModel userLoginStatusModel = new OrgUserLoginStatusModel();
        userLoginStatusModel.setLoginStatus(UserOnlineStatus.NOT_ONLINE);
        LambdaQueryWrapper<OrgUserLoginStatusModel> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(OrgUserLoginStatusModel::getUserId,userId);
        this.update(userLoginStatusModel,queryWrapper);
    }

    /**
     * 获取登录信息
     * @param userId
     * */
    @Override
    public OrgUserLoginStatusModel getOrgUserLoginStatusModelByUserId(Long userId) {
        LambdaQueryWrapper<OrgUserLoginStatusModel> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByDesc(OrgUserLoginStatusModel::getLoginTime);
        queryWrapper.eq(OrgUserLoginStatusModel::getUserId,userId);
        queryWrapper.last("LIMIT 1");
        return this.selectOne(queryWrapper);
    }


}
