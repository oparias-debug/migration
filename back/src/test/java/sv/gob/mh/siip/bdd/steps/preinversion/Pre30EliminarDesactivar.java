package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-30-eliminar-desactivar.feature (RN-C, RN-D). */
public class Pre30EliminarDesactivar {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;

    private enum Accion {
        FUENTE, ETAPA, ESTUDIO
    }

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProgramacionFinancieraPapService service;

    private Proyecto proyecto;
    private EtapaPreinversion etapa;
    private FuenteFinanciamientoEtapaPap fuente;
    private Accion accionPendiente;
    private ConflictoEstadoException excepcionCapturada;

    public Pre30EliminarDesactivar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            ProgramacionFinancieraPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Cuando("el Técnico URP intenta eliminar una fuente de financiamiento")
    public void el_tecnico_urp_intenta_eliminar_una_fuente() {
        crearEstudioConFuente(false);
        accionPendiente = Accion.FUENTE;
    }

    @Entonces("el sistema pregunta \"¿Está seguro de eliminar la fuente de financiamiento seleccionada?\"")
    public void el_sistema_pregunta_si_esta_seguro_de_eliminar_la_fuente() {
        // Confirmacion de UI pura (Anexo A.5): sin estado de backend que verificar.
    }

    @Cuando("el Técnico URP confirma")
    public void el_tecnico_urp_confirma() {
        switch (accionPendiente) {
            case FUENTE -> service.eliminarFuenteFinanciamiento(proyecto.getCup(),
                    NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()), fuente.getId(), ANIO);
            case ETAPA -> service.eliminarEtapaProgramacion(proyecto.getCup(),
                    NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()), ANIO);
            case ESTUDIO -> service.desactivarEstudio(proyecto.getCup(), ANIO);
        }
    }

    @Entonces("el sistema elimina la fuente de financiamiento \\(RN-C)")
    public void el_sistema_elimina_la_fuente_de_financiamiento() {
        assertThat(fuenteRepository.findById(fuente.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP intenta eliminar una etapa")
    public void el_tecnico_urp_intenta_eliminar_una_etapa() {
        crearEstudioConFuente(false);
        accionPendiente = Accion.ETAPA;
    }

    @Entonces("el sistema pregunta \"¿Está seguro de eliminar la etapa seleccionada?\"")
    public void el_sistema_pregunta_si_esta_seguro_de_eliminar_la_etapa() {
        // Confirmacion de UI pura, sin mockup propio (ver Observaciones del CU): sin estado de
        // backend que verificar.
    }

    @Entonces("el sistema elimina la etapa \\(RN-C)")
    public void el_sistema_elimina_la_etapa() {
        assertThat(fuenteRepository.findByEtapaPreinversionId(etapa.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que la etapa o fuente de financiamiento seleccionada tiene ejecución de años anteriores relacionada para ese CUP")
    public void que_la_etapa_o_fuente_tiene_ejecucion_de_anios_anteriores() {
        crearEstudioConFuente(true);
    }

    @Cuando("el Técnico URP intenta eliminarla")
    public void el_tecnico_urp_intenta_eliminarla() {
        String cup = proyecto.getCup();
        NombreEtapaDto nombreEtapa = NombreEtapaDto.valueOf(etapa.getTipoEtapa().name());
        Long idFuente = fuente.getId();
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> service.eliminarFuenteFinanciamiento(cup, nombreEtapa, idFuente, ANIO));
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("EJECUCION_ANIOS_ANTERIORES");
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un estudio no iniciado y el período de elaboración del PAP vigente")
    public void un_estudio_no_iniciado_y_el_periodo_vigente() {
        crearEstudioConFuente(false);
        accionPendiente = Accion.ESTUDIO;
    }

    @Cuando("el Técnico URP intenta eliminarlo de la tabla de programación")
    public void el_tecnico_urp_intenta_eliminarlo_de_la_tabla_de_programacion() {
        // La confirmacion real ocurre en "el Técnico URP confirma" (paso compartido de esta clase).
    }

    @Entonces("el sistema pregunta \"¿está seguro de desactivar el proyecto con código XXX?\" \\(RN-D)")
    public void el_sistema_pregunta_si_esta_seguro_de_desactivar() {
        // Confirmacion de UI pura (RN-D, sin mockup propio): sin estado de backend que verificar.
    }

    @Entonces("el sistema desactiva el estudio")
    public void el_sistema_desactiva_el_estudio() {
        assertThat(fuenteRepository.findByEtapaPreinversionProyectoId(proyecto.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un estudio de arrastre que no cumplió el 100% de lo programado física o financieramente en periodos anteriores")
    public void un_estudio_de_arrastre_con_programacion_incompleta() {
        crearEstudioConFuente(true);
    }

    @Cuando("el Técnico URP intenta eliminarlo")
    public void el_tecnico_urp_intenta_eliminarlo() {
        String cup = proyecto.getCup();
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> service.desactivarEstudio(cup, ANIO));
    }

    @Entonces("el sistema no permite la eliminación \\(RN-D)")
    public void el_sistema_no_permite_la_eliminacion() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("ESTUDIO_ARRASTRE_INCOMPLETO");
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearEstudioConFuente(boolean conEjecucionAnioAnterior) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-30E2-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-30E2-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M30E2" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S30E2" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-30E2-" + sufijo, "Eje tematico de prueba"));

        String nombreUsuario = "urp.30e2." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 10000.0);
        fuente = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder().etapaPreinversion(etapa).build());

        if (conEjecucionAnioAnterior) {
            progRepository.save(ProgCuatrimestralFinanciera.builder()
                    .fuente(fuente)
                    .anio(ANIO - 1)
                    .montoCuatrimestre1(BigDecimal.valueOf(3000))
                    .montoCuatrimestre2(BigDecimal.ZERO)
                    .montoCuatrimestre3(BigDecimal.ZERO)
                    .build());
        }

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
