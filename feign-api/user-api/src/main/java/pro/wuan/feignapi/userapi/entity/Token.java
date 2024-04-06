package pro.wuan.feignapi.userapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Table(name = "\"token\"")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

  @Id
  @GeneratedValue
  public Integer id;

  @Column(unique = true)
  public String token;

  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  public boolean revoked;

  public boolean expired;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @ToString.Exclude
  public User user;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Token token = (Token) o;
    return getId() != null && Objects.equals(getId(), token.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
