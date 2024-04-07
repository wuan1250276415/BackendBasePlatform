package pro.wuan.feignapi.messageapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message {

    /**
     * 消息内容
     */
    @Lob
    @Column(name = "content")
    private String content;

    /**
     * 消息状态
     */
    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    /**
     * 最后尝试时间
     */
    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    /**
     * 尝试次数
     */
    @ColumnDefault("0")
    @Column(name = "attempt_count")
    private Integer attemptCount;

    /**
     * 消息队列
     */
    @Size(max = 200)
    @Column(name = "queue", length = 200)
    private String queue;

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
    }

    /**
     * A method that is called before the entity is updated.
     * It sets the updatedAt and updatedBy fields.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    /**
     * A method that is called before the entity is removed.
     * It sets the deleted flag to true.
     */
    @PreRemove
    protected void onRemove() {
        this.deleted = true;
    }
}