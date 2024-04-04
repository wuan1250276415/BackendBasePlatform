package pro.wuan.core.role;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.core.base.entity.BaseEntity;
import pro.wuan.core.entity.Application;
import pro.wuan.core.user.User;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "role")
@Entity
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "role_application",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "application_id"))
    private Set<Application> applications = new LinkedHashSet<>();

}