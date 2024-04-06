package pro.wuan.feignapi.messageapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import pro.wuan.feignapi.userapi.entity.BaseEntity;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message extends BaseEntity {

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

}