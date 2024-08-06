package pro.wuan.feignapi.userapi.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import pro.wuan.common.core.constant.CommonConstant;

import java.util.Objects;

/**
 * @description: 线程变量存储用户信息
 * @author: oldone
 * @date: 2020/7/29 14:29
 */
public class UserContext implements AutoCloseable {
    static final ThreadLocal<UserDetails> current = new ThreadLocal<>();

    public UserContext(UserDetails user) {
        current.set(user);
    }

    public static UserDetails getCurrentUser() {
        return current.get() == null ? new UserDetails() : current.get();
    }


    /**
     * @Description: 获取当前用户对应的租户ID
     * @Author: ivanchen
     * @Date: 2020/10/5
     * @Time: 4:42 下午
     * @Param:
     * @return:
     */
    public static Long getCurrentUserTenantId() {
        UserDetails userDetails = current.get();
        //TODO 需要替换 by ivanchen
        Long defaultId = 222222l;
        if (userDetails == null || userDetails.getUser() == null) {
            return defaultId;
        }
        return userDetails.getUser().getTenantId();
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
        UserDetails userDetails = current.get();
        Long defaultId = -1l;
        if (userDetails == null || userDetails.getUser() == null) {
            return defaultId;
        }
        return userDetails.getUser().getOrganId();
    }


    public static void setCurrentUser(UserDetails user) {
        current.set(user);
    }


    public static Long getCurrentUserId() {
        return getCurrentUser() == null ? 222222l : (getCurrentUser().getUser() == null ? 222222l : getCurrentUser().getUser().getId());
    }

    public static String getCurrentUserName() {
        return getCurrentUser() == null ? StringUtils.EMPTY : (getCurrentUser().getUser() == null ? StringUtils.EMPTY : getCurrentUser().getUser().getUserName());
    }

    public void close() {
        current.remove();
    }

    public static Boolean isExists(String account) {
        UserDetails user = current.get();
        if (StringUtils.isNotEmpty(account) && Objects.nonNull(user) && account.equals(user.getAccount())) {
            return true;
        }

        return false;
    }

    /**
     * 判断是否超管
     *
     * @return
     */
    public static Boolean isSuperAdmin() {
        UserDetails user = current.get();
        //判断当前人是否超管
        if (user != null && user.getUser() != null && CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getUser().getType())) {
            return true;
        }
        return false;
    }

    /**
     * 获取统一社会信用码或单位编码
     *
     * @return
     */
    public static String getCreditCode() {
        UserDetails user = current.get();
        //判断当前人是否超管
        if (user != null && user.getDepartment() != null) {
            return user.getDepartment().getUniteCreditCode();
        }
        return Strings.EMPTY;
    }
}
