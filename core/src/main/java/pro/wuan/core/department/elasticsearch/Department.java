package pro.wuan.core.department.elasticsearch;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import pro.wuan.common.db.entity.BaseElasticEntity;

@Getter
@Setter
@Document(indexName = "department")
public class Department extends BaseElasticEntity {
    @Id
    private Integer id;

    private String name;

    private String description;

    private Integer parentDepartmentId;

    private Integer unitId;

    private Integer leaderId;

    private Boolean status = false;
}