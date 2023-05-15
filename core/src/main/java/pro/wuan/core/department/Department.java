package pro.wuan.core.department;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pro.wuan.common.db.entity.BaseEntity;

@Getter
@Setter
@Table(name = "department")
@Entity
@Document(indexName = "department")
public class Department extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @org.springframework.data.annotation.Id
    @Field(name = "id",type = FieldType.Integer,analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    @Field(name = "部门", analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
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

    private String similar;

}