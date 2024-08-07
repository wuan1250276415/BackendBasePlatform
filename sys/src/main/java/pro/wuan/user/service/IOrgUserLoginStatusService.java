package pro.wuan.user.service;


import pro.wuan.common.core.service.IBaseService;
import pro.wuan.user.entity.OrgUserLoginStatusModel;
import pro.wuan.user.mapper.OrgUserLoginStatusMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 在线用户
 *
 * @program: tellhowcloud
 * @author huangtianji
 * @create 2022-10-11 09:35:17
 */
public interface IOrgUserLoginStatusService extends IBaseService<OrgUserLoginStatusMapper, OrgUserLoginStatusModel> {

    /**
     * 更新在线用户的信息
     * */
    void updateUserStatus();

    /**
     * 在线用户-批量下线用户
     * @param userIds
     * */
    void shutdownBatchIds(List<Long> userIds);

    /**
     * 用户登录
     * @param request
     * @param userId
     * */
    void loginOrgUserLoginStatus(HttpServletRequest request,Long userId);

    /**
     * 用户登出
     * @param request
     * @param userId
     * */
    void loginOutOrgUserLoginStatus(HttpServletRequest request,Long userId);

    /**
     * 获取登录信息
     * @param userId
     * */
    OrgUserLoginStatusModel getOrgUserLoginStatusModelByUserId(Long userId);
}