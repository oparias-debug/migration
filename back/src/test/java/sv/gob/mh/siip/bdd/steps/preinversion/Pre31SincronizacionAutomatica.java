package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-31-sincronizacion-automatica.feature (SF-4/SF-5). Ejercita directamente
 * {@link ProgramacionFinancieraPapService} (CU-PRE-30, actor "Sistema", sin endpoint propio en
 * este documento) y verifica el efecto colateral en la Programación de Metas Físicas (CU-PRE-31),
 * mismo criterio que {@code Pre30EliminarDesactivar} usa para ejercitar esas mismas acciones desde
 * el lado de CU-PRE-30.
 */
public class Pre31SincronizacionAutomatica {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProgramacionFinancieraPapService financieraService;

    private Proyecto proyecto;
    private EtapaPreinversion etapa;

    public Pre31SincronizacionAutomatica(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            ProgramacionFinancieraPapService financieraService) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.financieraService = financieraService;
    }

    @Dado("que un código fue desactivado en la Programación Financiera CU-PRE-30")
    public void que_un_codigo_fue_desactivado() {
        crearEstudioConMetaFisicaYAutenticar();
        financieraService.desactivarEstudio(proyecto.getCup(), ANIO);
    }

    @Entonces("el sistema lo desactiva automáticamente en la programación de Metas Físicas")
    public void el_sistema_lo_desactiva_automaticamente() {
        verificarMetaFisicaDesactivada();
    }

    @Dado("que una etapa fue eliminada en la Programación Financiera CU-PRE-30")
    public void que_una_etapa_fue_eliminada() {
        crearEstudioConMetaFisicaYAutenticar();
        financieraService.eliminarEtapaProgramacion(proyecto.getCup(), NombreEtapaDto.PERFIL, ANIO);
    }

    @Entonces("el sistema la desactiva automáticamente en la programación de Metas Físicas")
    public void el_sistema_la_desactiva_automaticamente() {
        verificarMetaFisicaDesactivada();
    }

    /**
     * SF-4/SF-5 dicen "se desactivará": el registro se conserva marcado como inactivo (no se borra)
     * y deja de listarse en el Anexo A.1.
     */
    private void verificarMetaFisicaDesactivada() {
        assertThat(etapaMetaRepository.findByEtapaPreinversionId(etapa.getId()))
                .hasValueSatisfying(etapaMeta -> assertThat(etapaMeta.getActivo()).isFalse());
        assertThat(etapaMetaRepository
                .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(
                        proyecto.getUnidadEjecutora().getId()))
                .isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearEstudioConMetaFisicaYAutenticar() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31K-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31K-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M31K" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S31K" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-31K-" + sufijo, "Eje tematico de prueba"));

        String nombreUsuario = "urp.31k." + sufijo;
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
        etapaMetaRepository.save(EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
