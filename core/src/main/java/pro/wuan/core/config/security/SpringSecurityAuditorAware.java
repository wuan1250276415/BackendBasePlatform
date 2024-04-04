package pro.wuan.core.config.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pro.wuan.core.user.User;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<Integer> {

    @Override
    @NonNull
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        // 请注意，这里假设你的 User 类具有名为 getId 的方法，用于返回用户 ID（整数）
        // 如果你的 User 类使用不同的名称或类型，请相应地进行调整
        return Optional.of(((User) authentication.getPrincipal()).getId());
    }
}

