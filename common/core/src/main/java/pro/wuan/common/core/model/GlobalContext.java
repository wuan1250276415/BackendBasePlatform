package pro.wuan.common.core.model;

import org.apache.logging.log4j.util.Strings;
import pro.wuan.common.core.constant.CommonConstant;

/**
 * @author: oldone
 * @date: 2021/9/26 16:03
 */
public class GlobalContext implements AutoCloseable {
    static final ThreadLocal<BaseUser> current = new ThreadLocal<>();

    public GlobalContext(BaseUser user) {
        current.set(user);
    }


    public static void setBaseUser(BaseUser user) {
        current.set(user);
    }
    /**
     * @Description: 获取当前用户对应的租户ID
     * @Author: ivanchen
     * @Date: 2020/10/5
     * @Time: 4:42 下午
     * @Param:
     * @return:
     */
    public static Long getTenantId() {
        BaseUser user = current.get();
        if (user == null) {
            return CommonConstant.DEFAULT_PARENT_ID;
        }
        return user.getTenantId();
    }

    /*
     * @Description: 获取当前用户对应的部门ID
     * @Author: ivanchen
     * @Date: 2020/10/5
     * @Time: 4:42 下午
     * @Param:
     * @return:
     */
    public static Long getCurrentOrganId() {
        BaseUser user = current.get();
        if (user == null) {
            return CommonConstant.DEFAULT_PARENT_ID;
        }
        return user.getOrganId();
    }


    public static Long getCurrentUserId() {
        BaseUser user = current.get();
        if (user == null) {
            return CommonConstant.DEFAULT_PARENT_ID;
        }
        return user.getUserId();
    }

    public static String getCurrentUserName() {
        BaseUser user = current.get();
        if (user == null) {
            return Strings.EMPTY;
        }
        return user.getUserName();
    }


    /*
     * 判断是否为超管
     */
    public static Boolean getIsSuperAdmin() {
        BaseUser user = current.get();
        if (user == null) {
            return false;
        }
        return user.getIsSuperAdmin();
    }
    public static String getDatabaseModl() {
        BaseUser user = current.get();
        if (user == null) {
            return null;
        }
        return user.getDatabaseModl();
    }
    @Override
    public void close() {
        current.remove();
    }
}
