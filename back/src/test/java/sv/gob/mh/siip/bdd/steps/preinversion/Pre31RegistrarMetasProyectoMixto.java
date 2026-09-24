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

/**
 * CU-PRE-31-registrar-metas-proyecto-mixto.feature (SF-1 + SF-2 en un mismo CUP, RN-B literales
 * a.1 y a.2). Perfil ya tuvo 40% programado en el año anterior (arrastre: pendiente 60%);
 * Prefactibilidad no tiene programación previa (nueva: límite 100%). La clasificación, y por lo
 * tanto el código de error de RN-B.a, debe resolverse por etapa y no por proyecto.
 */
public class Pre31RegistrarMetasProyectoMixto {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;
    private static final double EJECUTADO_PERFIL = 40d;

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
    private EtapaMetaFisicaPap metaPerfil;
    private EtapaPreinversion etapaPrefactibilidad;
    private EstudioProgramacionMetasDto estudioConsultado;
    private EstudioProgramacionMetasDto estudioGuardado;
    private ValidacionNegocioException excepcionCapturada;

    public Pre31RegistrarMetasProyectoMixto(InstitucionRepository institucionRepository,
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

    @Dado("que existe un proyecto con la etapa \"Perfil\" de arrastre y la etapa \"Prefactibilidad\" nueva")
    public void que_existe_un_proyecto_mixto() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31M-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31M-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M31M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S31M" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-31M-" + sufijo, "Eje tematico de prueba"));

        String nombreUsuario = "urp.31m." + sufijo;
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
        etapaPrefactibilidad = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto,
                TipoEtapaPreinversion.PREFACTIBILIDAD, 20000.0);
        metaPerfil = Pre31Fixtures.nuevaEtapaMetaFisicaConHistorico(etapaMetaRepository, progRepository, etapaPerfil,
                Entregable.ESTUDIO_DE_PERFIL, ANIO - 1, EJECUTADO_PERFIL);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Cuando("el Técnico URP hace clic en el CUP del proyecto mixto")
    public void hace_clic_en_el_cup_del_proyecto_mixto() {
        estudioConsultado = service.obtenerProgramacionMetasEstudio(proyecto.getCup(), ANIO);
        assertThat(estudioConsultado.getEtapas()).hasSize(2);
    }

    @Entonces("la etapa \"Perfil\" se muestra como de arrastre con \"Entregable\" y \"Ejecutado años Anteriores\" precargados")
    public void la_etapa_perfil_se_muestra_como_arrastre() {
        EtapaMetaFisicaDto perfil = etapa(estudioConsultado, NombreEtapaDto.PERFIL);
        assertThat(perfil.getEsArrastre()).isTrue();
        assertThat(perfil.getEntregable()).isEqualTo(EntregableDto.ESTUDIO_DE_PERFIL);
        assertThat(perfil.getEjecutadoAniosAnteriores()).isEqualTo(EJECUTADO_PERFIL);
    }

    @Y("la etapa \"Prefactibilidad\" se muestra como nueva con los campos vacíos")
    public void la_etapa_prefactibilidad_se_muestra_como_nueva() {
        EtapaMetaFisicaDto prefactibilidad = etapa(estudioConsultado, NombreEtapaDto.PREFACTIBILIDAD);
        assertThat(prefactibilidad.getEsArrastre()).isFalse();
        assertThat(prefactibilidad.getEntregable()).isNull();
        assertThat(prefactibilidad.getEjecutadoAniosAnteriores()).isNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP guarda la etapa \"Perfil\" dentro de su pendiente y la etapa \"Prefactibilidad\" por encima del 100%")
    public void guarda_perfil_dentro_y_prefactibilidad_por_encima() {
        // Perfil: 60% = exactamente su pendiente (100% - 40%). Prefactibilidad: 60% + 50% = 110%.
        excepcionCapturada = capturarValidacion(request(30, 30, 60, 50));
    }

    @Cuando("el Técnico URP guarda la etapa \"Perfil\" por encima de su pendiente y la etapa \"Prefactibilidad\" dentro del 100%")
    public void guarda_perfil_por_encima_y_prefactibilidad_dentro() {
        // Perfil: 70% > pendiente de 60%. Prefactibilidad: 80% <= 100%.
        excepcionCapturada = capturarValidacion(request(40, 30, 50, 30));
    }

    @Entonces("el sistema rechaza el guardado del proyecto mixto con el código {string}")
    public void el_sistema_rechaza_el_guardado_con_el_codigo(String codigo) {
        assertThat(excepcionCapturada.getCodigo()).isEqualTo(codigo);
        // Se mantiene en el Anexo A.4: no se persistió programación de ninguna de las dos etapas.
        assertThat(progRepository.findByEtapaMetaFisicaIdAndAnio(metaPerfil.getId(), ANIO)).isEmpty();
        assertThat(etapaMetaRepository.findByEtapaPreinversionId(etapaPrefactibilidad.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP guarda ambas etapas del proyecto mixto dentro de sus límites")
    public void guarda_ambas_etapas_dentro_de_sus_limites() {
        // Perfil: 60% (= pendiente). Prefactibilidad: 100%.
        estudioGuardado = service.guardarProgramacionMetasEstudio(proyecto.getCup(), ANIO, request(30, 30, 50, 50));
    }

    @Entonces("el sistema guarda la programación de ambas etapas del proyecto mixto")
    public void el_sistema_guarda_ambas_etapas() {
        EtapaMetaFisicaDto perfil = etapa(estudioGuardado, NombreEtapaDto.PERFIL);
        EtapaMetaFisicaDto prefactibilidad = etapa(estudioGuardado, NombreEtapaDto.PREFACTIBILIDAD);
        assertThat(perfil.getTotalAnio()).isEqualTo(60d);
        // SF-1 paso 2: el "Entregable" de la etapa de arrastre no cambia aunque no venga en el request.
        assertThat(perfil.getEntregable()).isEqualTo(EntregableDto.ESTUDIO_DE_PERFIL);
        assertThat(prefactibilidad.getTotalAnio()).isEqualTo(100d);
        assertThat(prefactibilidad.getEntregable()).isEqualTo(EntregableDto.ESTUDIO_DE_PREFACTIBILIDAD);
        RequestContextHolder.resetRequestAttributes();
    }

    private static EtapaMetaFisicaDto etapa(EstudioProgramacionMetasDto estudio, NombreEtapaDto nombre) {
        return estudio.getEtapas().stream()
                .filter(e -> e.getEtapa() == nombre)
                .findFirst()
                .orElseThrow();
    }

    /** Perfil (arrastre) sin "Entregable": el campo está deshabilitado y se conserva el ya registrado. */
    private static GuardarProgramacionMetasEstudioRequestDto request(double perfilC1, double perfilC2,
            double prefactibilidadC1, double prefactibilidadC2) {
        return new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL)
                        .montoCuatrimestre1(perfilC1).montoCuatrimestre2(perfilC2).montoCuatrimestre3(0d))
                .addEtapasItem(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PREFACTIBILIDAD)
                        .entregable(EntregableDto.ESTUDIO_DE_PREFACTIBILIDAD)
                        .montoCuatrimestre1(prefactibilidadC1).montoCuatrimestre2(prefactibilidadC2)
                        .montoCuatrimestre3(0d));
    }

    private ValidacionNegocioException capturarValidacion(GuardarProgramacionMetasEstudioRequestDto request) {
        return org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio(proyecto.getCup(), ANIO, request));
    }
}
