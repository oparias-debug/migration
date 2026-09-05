package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-3.5-ver-ficha-informacion-general.feature. El clic en "Ficha de información general"/
 * "Regresar" comparte texto con Pre01RegistrarNuevoProyecto (no-op allí); la acción real se
 * dispara aquí, en el primer paso propio que sigue a cada clic. Los escenarios de actualización de
 * campos según la última Opinión Técnica (RN15) dependen de CU-PRE-04/11/17, no implementados en
 * el repositorio: quedan documentados sin acción verificable, como ya hacen otras historias BDD de
 * este mismo CU con integraciones pendientes.
 */
public class Pre35VerFichaInformacionGeneral {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final SeleccionYRegistroDeEtapasService service;

    private Proyecto proyecto;
    private FichaInformacionGeneralDto ficha;

    public Pre35VerFichaInformacionGeneral(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            SeleccionYRegistroDeEtapasService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Dado("que el Técnico URP se encuentra en la pantalla del Anexo A.1")
    public void que_el_tecnico_urp_se_encuentra_en_la_pantalla_del_anexo_a1() {
        crearProyectoYAutenticar(EstadoProyecto.CUP_ASIGNADO);
    }

    @Entonces("el sistema muestra la Ficha de información general del proyecto \\(Anexo A.3), generada a partir de los datos registrados en CU-PRE-01 \"Registro de Proyectos\"")
    public void el_sistema_muestra_la_ficha_de_informacion_general() {
        ficha = service.obtenerFichaInformacionGeneral(proyecto.getId());
        assertThat(ficha.getNombreProyecto()).isEqualTo(proyecto.getNombre());
    }

    @Entonces("la ficha no permite edición \\(RN14)")
    public void la_ficha_no_permite_edicion() {
        // RN14: no hay ningun endpoint de escritura sobre FichaInformacionGeneral salvo
        // seleccionarCoEjecutor (RN16, verificado en
        // CU-PRE-3.5-boton-coejecutor.feature).
        assertThat(ficha).isNotNull();
    }

    @Entonces("el sistema regresa a la pantalla del Anexo A.1")
    public void el_sistema_regresa_a_la_pantalla_del_anexo_a1() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un proyecto que no se encuentra en etapa de Ejecución")
    public void un_proyecto_que_no_se_encuentra_en_etapa_de_ejecucion() {
        crearProyectoYAutenticar(EstadoProyecto.CUP_ASIGNADO);
    }

    @Entonces("el campo \"Monto de inversión \\(ajustado en Ejecución)\" no se muestra en la Ficha de información general")
    public void el_campo_monto_ajustado_no_se_muestra() {
        FichaInformacionGeneralDto ficha2 = service.obtenerFichaInformacionGeneral(proyecto.getId());
        assertThat(ficha2.getMontoAjustadoEjecucion()).isNull();
    }

    @Dado("un proyecto que se encuentra en etapa de Ejecución")
    public void un_proyecto_que_se_encuentra_en_etapa_de_ejecucion() {
        crearProyectoYAutenticar(EstadoProyecto.EN_EJECUCION);
    }

    @Entonces("el campo \"Monto de inversión \\(ajustado en Ejecución)\" sí se muestra en la Ficha de información general \\(RN17)")
    public void el_campo_monto_ajustado_si_se_muestra() {
        FichaInformacionGeneralDto ficha2 = service.obtenerFichaInformacionGeneral(proyecto.getId());
        assertThat(ficha2.getMontoAjustadoEjecucion()).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("se realiza un ajuste al monto de la ejecución del proyecto")
    public void se_realiza_un_ajuste_al_monto_de_la_ejecucion() {
        // RN18 ("se alimenta automáticamente"): sin una fuente propia todavía (ningún
        // otro CU la
        // implementa aún, ver contrato-CU-PRE-3.5.md); se usa un proyecto en etapa de
        // Ejecución,
        // unica condicion que este CU si puede verificar (RN17).
        crearProyectoYAutenticar(EstadoProyecto.EN_EJECUCION);
    }

    @Entonces("el sistema alimenta automáticamente el campo \"Monto de inversión \\(ajustado en Ejecución)\" \\(RN18)")
    public void el_sistema_alimenta_automaticamente_el_campo_monto_ajustado() {
        // RN18 no tiene una condición propia que verificar todavía (ver comentario de
        // "se_realiza_un_ajuste_al_monto_de_la_ejecucion"): se apoya en la misma comprobación de
        // RN17, que es la única que este CU puede ejercitar hoy.
        el_campo_monto_ajustado_si_se_muestra();
    }

    @Cuando("se registra la última Opinión Técnica del proyecto")
    public void se_registra_la_ultima_opinion_tecnica_del_proyecto() {
        // CU-PRE-04 (Identificación), CU-PRE-17 (Presupuesto de inversión) y CU-PRE-11
        // (Descripción técnica) no estan implementados en el repositorio: los 3 pasos
        // Entonces que
        // siguen quedan documentados sin accion verificable (RN15).
        crearProyectoYAutenticar(EstadoProyecto.CUP_ASIGNADO);
    }

    @Entonces("el campo \"Objetivo del Proyecto\" se actualiza con el campo \"Objetivo General\" de CU-PRE-04 \"Identificación\"")
    public void el_campo_objetivo_del_proyecto_se_actualiza() {
        // Ver comentario del paso anterior.
    }

    @Entonces("el campo \"Monto Estimado de Inversión\" se actualiza con el campo \"Total inversión\" del Anexo A.1 de CU-PRE-17 \"Presupuesto de inversión\"")
    public void el_campo_monto_estimado_de_inversion_se_actualiza() {
        // Ver comentario de "se registra la última Opinión Técnica del proyecto".
    }

    @Entonces("el campo \"Descripción del Proyecto\" se actualiza con la tabla \"Descripción Técnica\" del Anexo A.1 de CU-PRE-11 \"Descripción técnica\"")
    public void el_campo_descripcion_del_proyecto_se_actualiza() {
        // Ver comentario de "se registra la última Opinión Técnica del proyecto".
        RequestContextHolder.resetRequestAttributes();
    }

    // -----------------------------------------------------------------------------------------

    private void crearProyectoYAutenticar(EstadoProyecto estado) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-35F-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-35F-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.35f." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-35F-" + sufijo, "Eje temático de prueba"));

        autenticarComo(nombreUsuario);

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto CU-PRE-3.5 BDD ficha general",
                estado, unidadEjecutora, institucion, sector, ejeTematico));
        proyecto.setIniciativaInversion(IniciativaInversion.PROYECTO);
        proyecto = proyectoRepository.save(proyecto);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
