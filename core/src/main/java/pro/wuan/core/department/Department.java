/**
 * This class represents the Department entity in the application.
 * It extends the BaseEntity class which provides common fields like id, created_at, and updated_at.
 * It uses JPA's @Entity annotation to indicate that it's a JPA entity.
 * It uses JPA's @Table annotation to specify the table name in the database.
 */
package pro.wuan.core.department;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.core.base.entity.BaseEntity;

@Getter
@Setter
@Table(name = "department")
@Entity
public class Department extends BaseEntity {

    /**
     * The unique identifier of the department.
     * It uses JPA's @Id and @GeneratedValue annotations to indicate that it's the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * The name of the department.
     * It uses the @NotNull and @Size annotations to validate that the name is not null and its size is up to 255 characters.
     */
    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The description of the department.
     */
    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    /**
     * The identifier of the parent department.
     */
    @Column(name = "parent_department_id")
    private Integer parentDepartmentId;

    /**
     * The identifier of the unit that the department belongs to.
     */
    @Column(name = "unit_id")
    private Integer unitId;

    /**
     * The identifier of the leader of the department.
     */
    @Column(name = "leader_id")
    private Integer leaderId;

    /**
     * The status of the department.
     * It uses the @NotNull annotation to validate that the status is not null.
     */
    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;
}