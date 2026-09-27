package sv.gob.mh.siip;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class SiipApplicationTest {

    @Test
    void main_delegaEnSpringApplicationConLosArgumentos() {
        String[] args = {"--spring.profiles.active=test"};
        try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
            SiipApplication.main(args);

            spring.verify(() -> SpringApplication.run(SiipApplication.class, args));
        }
    }
}
