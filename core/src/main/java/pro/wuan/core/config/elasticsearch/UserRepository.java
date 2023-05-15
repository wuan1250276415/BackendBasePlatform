package pro.wuan.core.config.elasticsearch;

import org.springframework.data.jpa.repository.EntityGraph;
import pro.wuan.common.db.repository.BaseRepository;
import pro.wuan.core.user.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Integer> {
    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByUsername(String username);

}
