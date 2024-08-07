package pro.wuan.utils;


import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;

/**
 * @author: liumin
 * @date: 2021/9/29 10:04
 */
public class TestUserInfo {

    /**
     * // TODO 前端未实现token，先写死当前用户信息为admin
     *
     * @return
     */
    public static OrgUserModel getUser() {
//        OrgUserModel user = UserContext.getCurrentUser().getUser();
        OrgUserModel user = new OrgUserModel();
        user.setId(1L);
        user.setLoginName("admin");
        user.setType(CommonConstant.USER_TYPE.SUPER_ADMIN.getValue());
        return user;
    }

}
