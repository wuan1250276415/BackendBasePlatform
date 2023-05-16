package pro.wuan.core.department;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.common.db.entity.BaseEntity;

@Getter
@Setter
@Table(name = "department")
@Entity
public class Department extends BaseEntity {
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

    @Column(name = "parent_department_id")
    private Integer parentDepartmentId;

    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "leader_id")
    private Integer leaderId;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;
}