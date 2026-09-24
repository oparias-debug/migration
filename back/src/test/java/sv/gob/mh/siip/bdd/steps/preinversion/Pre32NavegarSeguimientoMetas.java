package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceFinancieroPapService;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;

/**
 * CU-PRE-32-navegar-seguimiento-metas.feature (SF-2). "Siguiente" es pura navegación (decisión
 * funcional v1.2, sin endpoint propio); se verifica que la pantalla de destino (CU-PRE-33) responde
 * para la misma Unidad Ejecutora.
 */
public class Pre32NavegarSeguimientoMetas {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2028;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvanceFinancieroPapService avanceFinancieroService;
    private final AvanceMetasFisicasPapService avanceMetasService;

    private UnidadEjecutora unidadEjecutora;

    public Pre32NavegarSeguimientoMetas(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            AvanceFinancieroPapService avanceFinancieroService, AvanceMetasFisicasPapService avanceMetasService) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.avanceFinancieroService = avanceFinancieroService;
        this.avanceMetasService = avanceMetasService;
    }

    @Dado("que el Técnico URP se encuentra en la pantalla \"Avance Financiero Cuatrimestral del PAP\" avance-financiero")
    public void que_se_encuentra_en_la_pantalla() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-32F-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-32F-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.32f." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThat(avanceFinancieroService.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20))
                .isNotNull();
    }

    @Cuando("hace clic en el botón \"Siguiente\" avance-financiero")
    public void hace_clic_en_siguiente() {
        // Pura navegación (SF-2): no dispara ninguna acción propia de CU-PRE-32.
    }

    @Entonces("el sistema muestra el Anexo A.1 de CU-PRE-33 \"Avance Cuatrimestral por Metas Físicas del PAP\"")
    public void el_sistema_muestra_el_anexo_a1_de_cu_pre_33() {
        assertThat(avanceMetasService.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20))
                .isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }
}
