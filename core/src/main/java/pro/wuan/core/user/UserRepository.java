package pro.wuan.core.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pro.wuan.core.base.repository.BaseRepository;
import pro.wuan.feignapi.userapi.entity.User;

import java.util.Optional;
@EnableJpaRepositories
public interface UserRepository extends BaseRepository<User, Integer> {
    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByUsername(String username);

}
