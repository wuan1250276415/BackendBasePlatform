package pro.wuan.common.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;


@Getter
@Setter
public abstract class BaseElasticEntity implements Serializable {
    private Instant createdAt;

    private Integer createdBy;

    private Instant updatedAt;

    private Integer updatedBy;

    private boolean deleted = false;

    private Integer tenantId;

}