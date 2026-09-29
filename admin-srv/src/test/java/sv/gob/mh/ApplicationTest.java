package sv.gob.mh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

/**
 * Cubre el punto de entrada de la aplicacion.
 *
 * <p>El arranque real de Spring ya lo ejercitan las pruebas anotadas con
 * {@code @SpringBootTest}; aqui solo se comprueba que {@code main} delega en
 * {@link SpringApplication}, sin levantar un segundo contexto.</p>
 */
class ApplicationTest {

    @Test
    @DisplayName("main delega el arranque en SpringApplication")
    void mainDelegaEnSpringApplication() {
        String[] args = {"--server.port=0"};

        try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
            Application.main(args);
            spring.verify(() -> SpringApplication.run(Application.class, args));
        }
    }

    @Test
    @DisplayName("La clase de arranque es instanciable")
    void laClaseDeArranqueEsInstanciable() {
        assertThat(new Application()).isNotNull();
    }
}
