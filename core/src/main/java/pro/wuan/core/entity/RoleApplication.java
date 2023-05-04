//package pro.wuan.core.entity;
//
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import pro.wuan.common.auth.user.Application;
//import pro.wuan.common.auth.user.Role;
//
//@Getter
//@Setter
//@Table(name = "role_application")
//@Entity
//public class RoleApplication {
//    @EmbeddedId
//    private RoleApplicationId id;
//
//    @MapsId("roleId")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "role_id", nullable = false)
//    private Role role;
//
//    @MapsId("applicationId")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "application_id", nullable = false)
//    private Application application;
//
//}