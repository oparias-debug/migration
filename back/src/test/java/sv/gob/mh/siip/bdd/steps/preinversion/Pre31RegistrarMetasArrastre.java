package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.Pre31Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
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
import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-31-registrar-metas-arrastre.feature (SF-1, RN-B literal a.1). */
public class Pre31RegistrarMetasArrastre {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;
    private static final double EJECUTADO_ANIO_ANTERIOR = 40d;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProgramacionMetasFisicasPapService service;

    private Proyecto proyecto;
    private EtapaMetaFisicaPap etapaMetaArrastre;
    private EstudioProgramacionMetasDto estudioConsultado;
    private EstudioProgramacionMetasDto estudioGuardado;
    private ValidacionNegocioException excepcionCapturada;

    public Pre31RegistrarMetasArrastre(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository, ProgCuatrimestralMetaFisicaRepository progRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, ProgramacionMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Dado("que el Técnico URP se encuentra en la pantalla \"Programación por Meta Física Cuatrimestral del PAP\" \\(Anexo A.1) arrastre")
    public void que_se_encuentra_en_la_pantalla_arrastre() {
        crearEstudioDeArrastreYAutenticar();
    }

    @Cuando("el Técnico URP hace clic en el CUP de un proyecto de arrastre")
    public void hace_clic_en_el_cup_de_un_proyecto_de_arrastre() {
        estudioConsultado = service.obtenerProgramacionMetasEstudio(proyecto.getCup(), ANIO);
        assertThat(estudioConsultado.getEsArrastre()).isTrue();
    }

    @Entonces("el sistema muestra el Anexo A.4 con \"CUP\", \"Etapa\", \"Meta\", \"Entregable\" y \"Ejecutado años Anteriores\" deshabilitados y precargados del ejercicio anterior")
    public void el_sistema_muestra_el_anexo_a4_precargado() {
        assertThat(estudioConsultado.getCup()).isEqualTo(proyecto.getCup());
        EtapaMetaFisicaDto fila = etapaDto();
        assertThat(fila.getEntregable()).isEqualTo(EntregableDto.ESTUDIO_DE_PERFIL);
        assertThat(fila.getEjecutadoAniosAnteriores()).isEqualTo(EJECUTADO_ANIO_ANTERIOR);
    }

    @Cuando("el Técnico URP registra la programación cuatrimestral para cada etapa")
    public void registra_la_programacion_cuatrimestral_para_cada_etapa() {
        // El envío real ocurre al hacer clic en "Guardar" (paso genérico compartido de CU-PRE-31);
        // la acción se dispara en el primer paso propio de esta clase que le sigue.
    }

    @Entonces("el sistema valida los datos según RN-B literal a.1")
    public void el_sistema_valida_los_datos_segun_rn_b_a1() {
        estudioGuardado = guardarMontos(20, 20, 20);
        assertThat(estudioGuardado.getEtapas().get(0).getTotalAnio()).isEqualTo(60d);
    }

    @Y("guarda automáticamente el registro en la tabla del Anexo A.1")
    public void guarda_automaticamente_el_registro_en_la_tabla_del_anexo_a1() {
        assertThat(progRepository.findByEtapaMetaFisicaIdAndAnio(etapaMetaArrastre.getId(), ANIO)).isPresent();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios metas-fisicas-arrastre")
    public void el_sistema_regresa_sin_guardar_los_cambios() {
        assertThat(progRepository.findByEtapaMetaFisicaId(etapaMetaArrastre.getId())).hasSize(1);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el \"Total Programado Año\" supera el porcentaje pendiente de ejecutar del estudio")
    public void que_el_total_programado_supera_el_pendiente() {
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> guardarMontos(40, 20, 10));
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("PORCENTAJE_SUPERA_100_ARRASTRE");
    }

    @Y("se mantiene en la pantalla del Anexo A.4 arrastre")
    public void se_mantiene_en_la_pantalla_del_anexo_a4() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(progRepository.findByEtapaMetaFisicaId(etapaMetaArrastre.getId())).hasSize(1);
        RequestContextHolder.resetRequestAttributes();
    }

    private EtapaMetaFisicaDto etapaDto() {
        return estudioConsultado.getEtapas().get(0);
    }

    private EstudioProgramacionMetasDto guardarMontos(double c1, double c2, double c3) {
        GuardarProgramacionMetasEstudioRequestDto request = new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL)
                        .entregable(EntregableDto.ESTUDIO_DE_PERFIL)
                        .montoCuatrimestre1(c1).montoCuatrimestre2(c2).montoCuatrimestre3(c3));
        return service.guardarProgramacionMetasEstudio(proyecto.getCup(), ANIO, request);
    }

    private void crearEstudioDeArrastreYAutenticar() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31A-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31A-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M31A" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S31A" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-31A-" + sufijo, "Eje tematico de prueba"));

        String nombreUsuario = "urp.31a." + sufijo;
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
        EtapaPreinversion etapaPerfil = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto,
                TipoEtapaPreinversion.PERFIL, 10000.0);
        etapaMetaArrastre = Pre31Fixtures.nuevaEtapaMetaFisicaConHistorico(etapaMetaRepository, progRepository,
                etapaPerfil, Entregable.ESTUDIO_DE_PERFIL, ANIO - 1, EJECUTADO_ANIO_ANTERIOR);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
