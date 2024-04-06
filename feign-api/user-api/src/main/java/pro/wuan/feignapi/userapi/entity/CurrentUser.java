package pro.wuan.feignapi.userapi.entity;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * The User entity class.
 * This class represents a user in the application.
 * Each user can have multiple roles and applications associated with it.
 */
public class CurrentUser {
    private static final String CURRENT_USER = "CURRENT_USER";

    private CurrentUser() {
    }

    public static User get() {
        return (User) RequestContextHolder.currentRequestAttributes().getAttribute(CURRENT_USER, RequestAttributes.SCOPE_REQUEST);
    }

    public static void set(User user) {
        RequestContextHolder.currentRequestAttributes().setAttribute(CURRENT_USER, user, RequestAttributes.SCOPE_REQUEST);
    }

    public static void clear() {
        RequestContextHolder.currentRequestAttributes().removeAttribute(CURRENT_USER, RequestAttributes.SCOPE_REQUEST);
    }

}
