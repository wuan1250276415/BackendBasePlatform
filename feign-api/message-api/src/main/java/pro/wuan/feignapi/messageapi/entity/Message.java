package pro.wuan.feignapi.messageapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

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

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "tenant_id")
    private Integer tenantId;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    /**
     * 消息队列
     */
    @Size(max = 200)
    @Column(name = "queue", length = 200)
    private String queue;

}