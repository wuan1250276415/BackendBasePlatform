/**
 * This class represents the Department entity in the application.
 * It extends the BaseEntity class which provides common fields like id, created_at, and updated_at.
 * It uses JPA's @Entity annotation to indicate that it's a JPA entity.
 * It uses JPA's @Table annotation to specify the table name in the database.
 */
package pro.wuan.feignapi.userapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name = "department")
@Entity
public class Department extends BaseEntity {

    /**
     * The name of the department.
     * It uses the @NotNull and @Size annotations to validate that the name is not null and its size is up to 255 characters.
     */
    @ToString.Include
    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The description of the department.
     */
    @ToString.Include
    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    /**
     * The identifier of the parent department.
     */
    @ToString.Include
    @Column(name = "parent_department_id")
    private Integer parentDepartmentId;

    /**
     * The identifier of the unit that the department belongs to.
     */
    @ToString.Include
    @Column(name = "unit_id")
    private Integer unitId;

    /**
     * The identifier of the leader of the department.
     */
    @ToString.Include
    @Column(name = "leader_id")
    private Integer leaderId;

    /**
     * The status of the department.
     * It uses the @NotNull annotation to validate that the status is not null.
     */
    @ToString.Include
    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;
}