package sv.gob.mh.siip.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

class ActorContextoTest {

    private ActorContexto actorContexto;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        actorContexto = new ActorContexto(mock(UsuarioRepository.class));
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void nombreUsuarioActual_conHeader_devuelveElNombre() {
        request.addHeader("X-Usuario", "tecnico.urp");

        assertThat(actorContexto.nombreUsuarioActual()).contains("tecnico.urp");
    }

    @Test
    void nombreUsuarioActual_conHeaderEnBlanco_devuelveVacio() {
        request.addHeader("X-Usuario", "   ");

        assertThat(actorContexto.nombreUsuarioActual()).isEmpty();
    }

    @Test
    void nombreUsuarioActual_sinHeader_devuelveVacio() {
        assertThat(actorContexto.nombreUsuarioActual()).isEmpty();
    }

    @Test
    void nombreUsuarioActual_sinRequestActiva_devuelveVacio() {
        RequestContextHolder.resetRequestAttributes();

        assertThat(actorContexto.nombreUsuarioActual()).isEmpty();
    }
}
