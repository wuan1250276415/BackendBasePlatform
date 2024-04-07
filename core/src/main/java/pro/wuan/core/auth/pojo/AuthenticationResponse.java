package pro.wuan.core.auth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the response object for the authentication requests.
 * @author wuan1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  /**
   * The access token.
   */
  @JsonProperty("access_token")
  private String accessToken;

  /**
   * The refresh token.
   */
  @JsonProperty("refresh_token")
  private String refreshToken;
}
