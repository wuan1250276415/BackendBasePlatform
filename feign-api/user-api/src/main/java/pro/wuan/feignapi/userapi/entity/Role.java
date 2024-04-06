package pro.wuan.feignapi.userapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Role entity class.
 * This class represents a role that can be assigned to a user.
 * Each role can have multiple users and applications associated with it.
 */
@Getter
@Setter
@Table(name = "role")
@Entity
public class Role extends BaseEntity {


    /**
     * The name of the role.
     */
    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * A description of the role.
     */
    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    /**
     * The status of the role. True if the role is active, false otherwise.
     */
    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;

    /**
     * The set of users associated with this role.
     */
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new LinkedHashSet<>();

    /**
     * The set of applications associated with this role.
     */
    @ManyToMany
    @JoinTable(name = "role_application",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "application_id"))
    private Set<Application> applications = new LinkedHashSet<>();
}