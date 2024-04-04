package pro.wuan.core.user;

import org.springframework.data.jpa.repository.EntityGraph;
import pro.wuan.core.base.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Integer> {
    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByUsername(String username);

}
