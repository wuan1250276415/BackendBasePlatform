package pro.wuan.core.token;

import org.springframework.data.jpa.repository.Query;
import pro.wuan.core.base.repository.BaseRepository;
import pro.wuan.feignapi.userapi.entity.Token;

import java.util.List;
import java.util.Optional;
public interface TokenRepository extends BaseRepository<Token, Integer> {

  @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
  List<Token> findAllValidTokenByUser(Integer id);

  Optional<Token> findByToken(String token);
}
