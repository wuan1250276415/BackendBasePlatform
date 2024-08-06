package pro.wuan.common.web.constant;

import org.springframework.util.Assert;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息全局变量
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2022-01-04 13:24
 **/
public class ConstantUser {
    public static Map<String, Object> getSystemParam() {
        //获取系统参数
        Map<String, Object> systemParams = new HashMap<>();
        // 获取当前登录用户信息
        UserDetails userDetails = UserContext.getCurrentUser();
        Assert.notNull(userDetails.getUser(), "当前用户未登录或登录信息已失效，请重新登录");
        systemParams.put(CommonConstant.DATA_FILTER_SYSTEM_PARAMS_USER_DETAILS, userDetails); // 设置用户详情
        systemParams.put(CommonConstant.DATA_FILTER_SYSTEM_PARAMS_USER_ID, userDetails.getUser().getId()); // 设置用户id
        return systemParams;
    }
}
