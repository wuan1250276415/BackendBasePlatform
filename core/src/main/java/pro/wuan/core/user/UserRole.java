package pro.wuan.core.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.core.entity.Role;

@Getter
@Setter
@Table(name = "user_role")
@Entity
public class UserRole {
    @EmbeddedId
    private UserRoleId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}