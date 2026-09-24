package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-31-registrar-metas-nuevo-estudio.feature (SF-2, RN-B literal a.2, RN-E, Anexo B.1). */
public class Pre31RegistrarMetasNuevoEstudio {

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
    private final ProgramacionMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;
    private Institucion institucion;
    private Proyecto proyecto;
    private EtapaPreinversion etapaPerfil;
    private String nombreUsuarioUrp;
    private EstudioProgramacionMetasDto estudioConsultado;
    private EstudioProgramacionMetasDto estudioGuardado;
    private ValidacionNegocioException excepcionCapturada;

    public Pre31RegistrarMetasNuevoEstudio(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            ProgramacionMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Dado("que el Técnico URP se encuentra en la pantalla \"Programación por Meta Física Cuatrimestral del PAP\" \\(Anexo A.1) nuevo-estudio")
    public void que_se_encuentra_en_la_pantalla_nuevo_estudio() {
        crearInsumosYAutenticarComoUrp();
    }

    @Cuando("el Técnico URP selecciona el código de un proyecto nuevo cargado desde CU-PRE-30")
    public void selecciona_el_codigo_de_un_proyecto_nuevo() {
        estudioConsultado = service.obtenerProgramacionMetasEstudio(proyecto.getCup(), ANIO);
        assertThat(estudioConsultado.getEsArrastre()).isFalse();
    }

    @Entonces("el sistema muestra el Anexo A.4 con los campos vacíos, salvo \"CUP\"")
    public void el_sistema_muestra_el_anexo_a4_vacio() {
        assertThat(estudioConsultado.getCup()).isEqualTo(proyecto.getCup());
        assertThat(estudioConsultado.getEtapas().get(0).getEntregable()).isNull();
        assertThat(estudioConsultado.getEtapas().get(0).getTotalAnio()).isZero();
    }

    @Cuando("el Técnico URP registra la programación cuatrimestral en los campos habilitados")
    public void registra_la_programacion_cuatrimestral() {
        // El envío real ocurre al hacer clic en "Guardar" (paso genérico compartido de CU-PRE-31);
        // la acción se dispara en el primer paso propio de esta clase que le sigue.
    }

    @Entonces("el sistema valida los datos según RN-B literal a.2")
    public void el_sistema_valida_los_datos_segun_rn_b_a2() {
        estudioGuardado = guardarMontos(40, 30, 30);
        assertThat(estudioGuardado.getEtapas().get(0).getTotalAnio()).isEqualTo(100d);
    }

    @Y("traslada automáticamente el registro a la tabla del Anexo A.1")
    public void traslada_la_informacion_a_la_tabla_del_anexo_a1() {
        assertThat(service.listar(unidadEjecutora.getId(), ANIO, 0, 20).getContenido()).isNotEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que la programación de metas físicas ya fue registrada y guardada")
    public void que_la_programacion_ya_fue_registrada_y_guardada() {
        guardarMontos(40, 30, 30);
    }

    @Entonces("el sistema notifica al Técnico PRE que la programación PAP, tanto financiera como de metas físicas, fue enviada para su revisión")
    public void el_sistema_notifica_al_tecnico_pre() {
        RevisionProgramacionPAPDto revision = enviarARevision();
        assertThat(revision.getEstadoPap()).isEqualTo(EstadoPAPDto.ENVIADO_A_REVISION_DGICP);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el botón \"ENVIAR A REVISIÓN DGICP\" es visible solo para el Técnico URP")
    public void el_boton_enviar_a_revision_es_visible_solo_para_tecnico_urp() {
        String nombreUsuarioPre = "pre.31b." + UUID.randomUUID().toString().substring(0, 8);
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioPre)
                .nombreCompleto("Tecnico PRE (BDD)")
                .correo(nombreUsuarioPre + "@example.com")
                .rol(RolUsuario.TECNICO_PRE)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());
        autenticarComo(nombreUsuarioPre);

        assertThatThrownBy(this::enviarARevision).isInstanceOf(AccesoDenegadoException.class);
    }

    @Y("se habilita únicamente durante el período de ingreso de información o cuando se presenten modificaciones al PAP \\(RN-E)")
    public void se_habilita_unicamente_durante_el_periodo() {
        autenticarComo(nombreUsuarioUrp);
        assertThat(enviarARevision()).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el \"Total Programado Año\" supera el 100%")
    public void que_el_total_programado_supera_el_100() {
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> guardarMontos(50, 30, 30));
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("MONTO_SUPERA_100_NUEVO");
    }

    @Y("se mantiene en la pantalla del Anexo A.4 nuevo-estudio")
    public void se_mantiene_en_la_pantalla_del_anexo_a4() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(etapaMetaRepository.findByEtapaPreinversionId(etapaPerfil.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios metas-fisicas-nuevo-estudio")
    public void el_sistema_regresa_sin_guardar_los_cambios() {
        assertThat(etapaMetaRepository.findByEtapaPreinversionId(etapaPerfil.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    // Anexo B.1 (Anexo A.4): mismo criterio que el escenario RN-B.a.2 — el clic en "Guardar" es el
    // paso genérico (no-op); el intento de guardado se dispara en el "Dado" y se captura el error.

    @Dado("que el Técnico URP no seleccionó el \"Entregable\" de la etapa")
    public void que_no_selecciono_el_entregable() {
        excepcionCapturada = capturarValidacion(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL)
                .montoCuatrimestre1(40d).montoCuatrimestre2(30d).montoCuatrimestre3(30d));
    }

    @Dado("que el Técnico URP no registró porcentaje en ningún cuatrimestre de la etapa")
    public void que_no_registro_porcentaje_en_ningun_cuatrimestre() {
        excepcionCapturada = capturarValidacion(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL)
                .entregable(EntregableDto.ESTUDIO_DE_PERFIL)
                .montoCuatrimestre1(0d).montoCuatrimestre2(0d).montoCuatrimestre3(0d));
    }

    @Dado("que el Técnico URP registró un cuatrimestre con un porcentaje mayor a 100%")
    public void que_registro_un_cuatrimestre_mayor_a_100() {
        excepcionCapturada = capturarValidacion(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL)
                .entregable(EntregableDto.ESTUDIO_DE_PERFIL)
                .montoCuatrimestre1(150d).montoCuatrimestre2(0d).montoCuatrimestre3(0d));
    }

    @Entonces("el sistema indica que el campo {string} de la etapa es obligatorio")
    public void el_sistema_indica_que_el_campo_es_obligatorio(String campo) {
        verificarDetalleDeCampo(campo);
    }

    @Entonces("el sistema indica que el campo {string} de la etapa está fuera del rango permitido")
    public void el_sistema_indica_que_el_campo_esta_fuera_de_rango(String campo) {
        verificarDetalleDeCampo(campo);
    }

    private ValidacionNegocioException capturarValidacion(EtapaMetaFisicaRequestDto etapa) {
        GuardarProgramacionMetasEstudioRequestDto request = new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(etapa);
        return org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio(proyecto.getCup(), ANIO, request));
    }

    private void verificarDetalleDeCampo(String campo) {
        // CU-ADM-03: código genérico VALIDACION_NEGOCIO (codigo nulo en la excepción) + ErrorDetalle por campo.
        assertThat(excepcionCapturada.getCodigo()).isNull();
        assertThat(excepcionCapturada.getDetalles()).extracting(ErrorDetalleDto::getCampo)
                .containsExactly("PERFIL." + campo);
    }

    private RevisionProgramacionPAPDto enviarARevision() {
        return service.enviarProgramacionARevisionDgicp(
                new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), ANIO));
    }

    private EstudioProgramacionMetasDto guardarMontos(double c1, double c2, double c3) {
        GuardarProgramacionMetasEstudioRequestDto request = new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL)
                        .entregable(EntregableDto.ESTUDIO_DE_PERFIL)
                        .montoCuatrimestre1(c1).montoCuatrimestre2(c2).montoCuatrimestre3(c3));
        return service.guardarProgramacionMetasEstudio(proyecto.getCup(), ANIO, request);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private void crearInsumosYAutenticarComoUrp() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("INS-31B-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31B-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M31B" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S31B" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-31B-" + sufijo, "Eje tematico de prueba"));

        nombreUsuarioUrp = "urp.31b." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioUrp)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioUrp + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        etapaPerfil = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 10000.0);

        autenticarComo(nombreUsuarioUrp);
    }
}
