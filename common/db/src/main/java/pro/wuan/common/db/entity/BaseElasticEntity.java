package pro.wuan.common.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.Instant;


@Getter
@Setter
public abstract class BaseElasticEntity implements Serializable {
    @Field(type = FieldType.Date, format = {}, pattern = "epoch_millis")
    private Instant createdAt;

    private Integer createdBy;

    @Field(type = FieldType.Date, format = {}, pattern = "epoch_millis")
    private Instant updatedAt;

    private Integer updatedBy;

    private boolean deleted = false;

    private Integer tenantId;

}