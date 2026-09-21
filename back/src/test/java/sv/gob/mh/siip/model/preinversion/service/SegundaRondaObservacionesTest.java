package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01-responder-observaciones.feature deja explicitamente sin escenario el ciclo
 * devolver-responder repetido ("puede repetirse tantas veces... no se genera un escenario
 * adicional para ello"): esta prueba cubre esa segunda vuelta contra el servicio real (no mocks),
 * ya que devolverSolicitudCup/responderObservacionCup dependen de releer SolicitudPreinversion
 * vigente desde la base en cada llamada.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SegundaRondaObservacionesTest {

    @Autowired
    ProyectoService proyectoService;
    @Autowired
    InstitucionRepository institucionRepository;
    @Autowired
    UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ProyectoRepository proyectoRepository;
    @Autowired
    SolicitudPreinversionRepository solicitudRepository;
    @Autowired
    MacroSectorRepository macroSectorRepository;
    @Autowired
    SectorActividadRepository sectorActividadRepository;
    @Autowired
    EjeTematicoRepository ejeTematicoRepository;

    @Test
    void devolverDosVecesConRespuestaDelUrpEnMedio_mantieneObservado() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("INS-" + sufijo, "I"));
        UnidadEjecutora ue = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-" + sufijo, "UE", institucion));
        MacroSector macro = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "M"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "S", macro));
        EjeTematico eje = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-" + sufijo, "E"));

        Usuario urp = usuarioRepository.save(Usuario.builder().nombreUsuario("urp." + sufijo)
                .nombreCompleto("URP").rol(RolUsuario.TECNICO_URP).unidadEjecutora(ue).institucion(institucion)
                .activo(true).build());
        Usuario pre = usuarioRepository.save(Usuario.builder().nombreUsuario("pre." + sufijo)
                .nombreCompleto("PRE").rol(RolUsuario.TECNICO_PRE).unidadEjecutora(ue).institucion(institucion)
                .activo(true).build());

        autenticarComo(urp.getNombreUsuario());
        Proyecto proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto repro",
                EstadoProyecto.ENVIADO_DGICP_REGISTRO, ue, institucion, sector, eje));
        solicitudRepository.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP)
                .estado(EstadoSolicitud.ASIGNADA)
                .fechaSolicitud(LocalDateTime.now().minusDays(1))
                .tecnicoAsignado(pre)
                .fechaAsignacion(LocalDateTime.now())
                .build());

        autenticarComo(pre.getNombreUsuario());
        proyectoService.devolverSolicitudCup(proyecto.getId(),
                new DevolucionSolicitudRequestDto().comentario("Primera observacion"));

        autenticarComo(urp.getNombreUsuario());
        proyectoService.responderObservacionCup(proyecto.getId(),
                new RespuestaObservacionRequestDto().respuesta("Respuesta a la primera"));

        autenticarComo(pre.getNombreUsuario());
        proyectoService.devolverSolicitudCup(proyecto.getId(),
                new DevolucionSolicitudRequestDto().comentario("Segunda observacion distinta"));

        Proyecto recargado = proyectoRepository.findById(proyecto.getId()).orElseThrow();
        assertThat(recargado.getEstado()).isEqualTo(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Usuario", nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
