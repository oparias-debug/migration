package sv.gob.mh.siip.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class ApiGatewayApplicationTest {

  @Test
  void main_delegaEnSpringApplicationConLosArgumentos() {
    String[] args = {"--server.port=0"};
    try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
      ApiGatewayApplication.main(args);

      spring.verify(() -> SpringApplication.run(ApiGatewayApplication.class, args));
    }
  }

  @Test
  void constructor_creaInstancia() {
    assertThat(new ApiGatewayApplication()).isNotNull();
  }
}
