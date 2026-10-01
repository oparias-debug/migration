package sv.gob.mh.siip.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
  private String username;
  // Fuera del toString() de @Data: que no llegue a ningún log.
  @ToString.Exclude
  private String password;
}
