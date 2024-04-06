package pro.wuan.common.mq.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pro.wuan.feignapi.messageapi.entity.Message;

import java.util.List;

@EnableJpaRepositories
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("select m from Message m where m.status = ?1")
    List<Message> findMessagesByStatus(String status);

    @Modifying
    @Query("update Message m set m.status = ?2 where m.id = ?1")
    void updateMessageStatus(Integer id, String status);
}
