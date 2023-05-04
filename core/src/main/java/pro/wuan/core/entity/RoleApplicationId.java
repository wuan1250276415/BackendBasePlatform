//package pro.wuan.core.entity;
//
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Embeddable;
//import jakarta.validation.constraints.NotNull;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.io.Serial;
//import java.io.Serializable;
//import java.util.Objects;
//
//@Getter
//@Setter
//@Embeddable
//public class RoleApplicationId implements Serializable {
//    @Serial
//    private static final long serialVersionUID = -2238386682525984205L;
//    @NotNull
//    @Column(name = "role_id", nullable = false)
//    private Integer roleId;
//
//    @NotNull
//    @Column(name = "application_id", nullable = false)
//    private Integer applicationId;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || org.hibernate.Hibernate.getClass(this) != org.hibernate.Hibernate.getClass(o)) return false;
//        RoleApplicationId entity = (RoleApplicationId) o;
//        return Objects.equals(this.roleId, entity.roleId) &&
//                Objects.equals(this.applicationId, entity.applicationId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(roleId, applicationId);
//    }
//
//}