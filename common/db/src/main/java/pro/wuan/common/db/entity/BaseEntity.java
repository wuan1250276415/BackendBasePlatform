package pro.wuan.common.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public abstract class BaseEntity implements Serializable {
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "created_by")
    @CreatedBy
    private Integer createdBy;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

    @Column(name = "updated_by")
    @LastModifiedBy
    private Integer updatedBy;

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "tenant_id")
    private Integer tenantId;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @PreRemove
    protected void onRemove() {
        this.deleted = true;
    }

    // Getter and Setter methods
}