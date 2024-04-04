package pro.wuan.core.token;

import org.springframework.data.jpa.repository.Query;
import pro.wuan.core.base.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends BaseRepository<Token, Integer> {

  @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
  List<pro.wuan.core.token.Token> findAllValidTokenByUser(Integer id);

  Optional<pro.wuan.core.token.Token> findByToken(String token);
}
