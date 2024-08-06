package pro.wuan.core.config.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * This class implements AuditorAware interface to provide the current auditor (user) for Spring Data auditing.
 * The auditor is determined from the Spring Security context.
 */
public class SpringSecurityAuditorAware implements AuditorAware<Integer> {

    /**
     * This method overrides the getCurrentAuditor method from the AuditorAware interface.
     * It retrieves the current authenticated user from the Spring Security context.
     * If there is no authenticated user, it returns an empty Optional.
     * Otherwise, it returns the ID of the authenticated user wrapped in an Optional.
     *
     * @return Optional containing the ID of the authenticated user, or an empty Optional if there is no authenticated user.
     */
    @Override
    @NonNull
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        // Please note that this assumes your User class has a method named getId that returns the user ID (integer)
        // If your User class uses a different name or type, adjust accordingly
        return Optional.of(((User) authentication.getPrincipal()).getId());
    }
}