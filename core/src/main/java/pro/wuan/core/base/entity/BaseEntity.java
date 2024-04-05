package pro.wuan.core.base.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.context.SecurityContextHolder;
import pro.wuan.core.user.User;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a base entity class that provides common fields and behaviors for all entities.
 * It implements Serializable for easy serialization and deserialization.
 * It uses the JPA @MappedSuperclass annotation, so its fields will be part of the child entities.
 */
@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public abstract class BaseEntity implements Serializable {

    /**
     * The unique identifier of the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * The timestamp when the entity was created.
     * It is not updatable after being set for the first time.
     */
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Date createdAt;

    /**
     * The ID of the user who created the entity.
     */
    @Column(name = "created_by")
    private Integer createdBy;

    /**
     * The timestamp when the entity was last updated.
     */
    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    /**
     * The ID of the user who last updated the entity.
     */
    @Column(name = "updated_by")
    private Integer updatedBy;

    /**
     * A flag indicating whether the entity has been deleted.
     */
    @Column(name = "deleted")
    private boolean deleted = false;

    /**
     * The ID of the tenant that the entity belongs to.
     */
    @Column(name = "tenant_id")
    private Integer tenantId;

    /**
     * A method that is called before the entity is persisted.
     * It sets the createdAt and createdBy fields.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        createdBy = getCurrentUserId();
    }

    /**
     * A method that is called before the entity is updated.
     * It sets the updatedAt and updatedBy fields.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
        updatedBy = getCurrentUserId();
    }

    /**
     * A method that is called before the entity is removed.
     * It sets the deleted flag to true.
     */
    @PreRemove
    protected void onRemove() {
        this.deleted = true;
    }

    /**
     * A helper method that retrieves the ID of the currently authenticated user.
     *
     * @return the ID of the currently authenticated user.
     */
    private Integer getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((User) principal).getId();
    }
}