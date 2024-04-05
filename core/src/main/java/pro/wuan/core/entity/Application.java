package pro.wuan.core.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.core.base.entity.BaseEntity;
import pro.wuan.core.role.Role;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "application")
@Entity
public class Application extends BaseEntity {

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 255)
    @Column(name = "icon")
    private String icon;

    @Size(max = 50)
    @Column(name = "app_type", length = 50)
    private String appType;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;

    @ManyToMany
    @JoinTable(name = "role_application",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

}