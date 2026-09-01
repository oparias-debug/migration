package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01-solicitar-cup.feature. "el Técnico URP hace clic en el botón {string}" es el paso
 * generico compartido definido en Pre01RegistrarNuevoProyecto (no-op aqui). "un proyecto en
 * estado {string}" vive en Pre01ResponderObservaciones; como ese Dado crea el Proyecto en otra
 * instancia de step class, se lee via {@link ContextoProyectoBdd} (bean de escenario compartido).
 */
public class Pre01SolicitarCup {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final SolicitudPreinversionRepository solicitudRepository;
    private final ProyectoService proyectoService;
    private final ContextoProyectoBdd contextoProyecto;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyecto;
    private ProyectoDto proyectoActualizado;
    private ProyectoDto proyectoConsultado;
    private ValidacionNegocioException excepcionValidacion;
    private SectorActividad sector;
    private EjeTematico ejeTematico;

    public Pre01SolicitarCup(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            SolicitudPreinversionRepository solicitudRepository,
            ProyectoService proyectoService,
            ContextoProyectoBdd contextoProyecto,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.solicitudRepository = solicitudRepository;
        this.proyectoService = proyectoService;
        this.contextoProyecto = contextoProyecto;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("que el Técnico URP se encuentra en la pantalla {string} con la información del proyecto registrada")
    public void que_el_tecnico_urp_se_encuentra_en_la_pantalla_con_la_informacion_del_proyecto_registrada(
            String pantalla) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-CUP-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-CUP-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.bdd.cup." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioTecnico + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        autenticarComo(nombreUsuarioTecnico);

        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-CUP-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto registrado", EstadoProyecto.EN_REGISTRO,
                unidadEjecutora, institucion, sector, ejeTematico));
        contextoProyecto.setProyectoActual(proyecto);
    }

    @Cuando("el sistema realiza las validaciones descritas en el Anexo B.{int}")
    public void el_sistema_realiza_las_validaciones_descritas_en_el_anexo_b(Integer anexo) {
        // El Anexo B.2 no describe un mecanismo propio: se materializa en las reglas que
        // "no existen inconsistencias"/"existen inconsistencias" ejercitan a continuacion.
    }

    @Cuando("no existen inconsistencias")
    public void no_existen_inconsistencias() {
        proyectoActualizado = proyectoService.solicitarCup(proyecto.getId());
    }

    @Cuando("existen inconsistencias")
    public void existen_inconsistencias() {
        // Unica regla de Anexo B.2 que el servicio valida mas alla de Bean Validation en el
        // request (ya cubierta por los campos obligatorios de "Guardar"): la condicional de
        // proyecto de emergencia (RN: tipoEvento y N. de DL obligatorios si esProyectoEmergencia).
        proyecto.setEsProyectoEmergencia(true);
        proyecto.setTipoEvento(null);
        proyecto.setNumeroDecretoLegislativo(null);
        proyecto = proyectoRepository.save(proyecto);

        excepcionValidacion = null;
        try {
            proyectoService.solicitarCup(proyecto.getId());
        } catch (ValidacionNegocioException ex) {
            excepcionValidacion = ex;
        }
    }

    @Entonces("el sistema deshabilita la edición de todos los campos de la pantalla {string}")
    public void el_sistema_deshabilita_la_edicion_de_todos_los_campos_de_la_pantalla(String pantalla) {
        // Ya no esta en un estado editable: actualizar() debe rechazarlo (RN 1.c/2.2.b).
        Long idProyecto = proyecto.getId();
        ProyectoRequestDto valido = requestValido();
        assertThatThrownBy(() -> proyectoService.actualizar(idProyecto, valido))
                .isInstanceOf(ConflictoEstadoException.class);
    }

    @Entonces("el proyecto pasa al estado {string}")
    public void el_proyecto_pasa_al_estado(String estadoUi) {
        assertThat(proyectoActualizado.getEstado()).isEqualTo(EstadoProyectoDto.ENVIADO_DGICP_REGISTRO);
        assertThat(estadoUi).isEqualTo("Enviado a DGICP (Registro)");
    }

    @Entonces("el sistema envía alerta al Coordinador PRE por correo electrónico según el modelo del Anexo A.{double}")
    public void el_sistema_envia_alerta_al_coordinador_pre_por_correo_electronico_segun_el_modelo_del_anexo_a(
            Double anexo) {
        // Igual que en Pre01ResponderObservaciones: no se compara el numero de Anexo (sensible al
        // locale via {double}). Lo verificable es el efecto real: se registro la solicitud de CUP.
        assertThat(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(proyecto.getId(),
                sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud.CUP)).isPresent();
    }

    @Entonces("el sistema regresa a la pantalla {string}")
    public void el_sistema_regresa_a_la_pantalla(String pantalla) {
        // Navegacion de UI pura, compartida con "Regresar sin guardar y cancelar"
        // (CU-PRE-01-registrar-nuevo-proyecto.feature): nada mas que verificar aqui.
    }

    @Entonces("el proyecto aparece en la Bandeja de Preinversión \\(CU-PRE-{int}) con estado {string}")
    public void el_proyecto_aparece_en_la_bandeja_de_preinversion_cu_pre_con_estado(Integer numeroCu, String estadoUi) {
        // CU-PRE-02 (Bandeja de Preinversion) es un caso de uso propio, fuera de este fragmento
        // OpenAPI; se verifica el equivalente disponible: el listado paginado por UE y estado.
        boolean visible = proyectoRepository
                .findByActivoTrueAndUnidadEjecutoraIdAndEstado(proyecto.getUnidadEjecutora().getId(),
                        EstadoProyecto.ENVIADO_DGICP_REGISTRO, PageRequest.of(0, 20))
                .getContent()
                .stream()
                .anyMatch(p -> p.getId().equals(proyecto.getId()));
        assertThat(visible).isTrue();
        assertThat(estadoUi).isEqualTo("Enviado a DGICP (Registro)");
    }

    @Entonces("el sistema sombrea en rojo el contorno de cada campo con inconsistencia")
    public void el_sistema_sombrea_en_rojo_el_contorno_de_cada_campo_con_inconsistencia() {
        assertThat(excepcionValidacion).isNotNull();
        assertThat(excepcionValidacion.getDetalles()).hasSize(2);
    }

    @Entonces("el sistema muestra en cada campo los mensajes descritos en el Anexo B.{int}")
    public void el_sistema_muestra_en_cada_campo_los_mensajes_descritos_en_el_anexo_b(Integer anexo) {
        assertThat(excepcionValidacion.getDetalles()).allSatisfy(detalle -> {
            assertThat(detalle.getCampo()).isNotBlank();
            assertThat(detalle.getMensaje()).isNotBlank();
        });
    }

    @Entonces("se cancela la acción de solicitar CUP")
    public void se_cancela_la_accion_de_solicitar_cup() {
        Proyecto recargado = proyectoRepository.findById(proyecto.getId()).orElseThrow();
        assertThat(recargado.getEstado()).isEqualTo(EstadoProyecto.EN_REGISTRO);
    }

    @Entonces("retorna a la pantalla {string}")
    public void retorna_a_la_pantalla(String pantalla) {
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP intenta acceder al registro")
    public void el_tecnico_urp_intenta_acceder_al_registro() {
        proyecto = contextoProyecto.getProyectoActual();
        proyectoConsultado = proyectoService.obtener(proyecto.getId());
    }

    @Entonces("el sistema solo permite consultar el registro")
    public void el_sistema_solo_permite_consultar_el_registro() {
        assertThat(proyectoConsultado).isNotNull();
        assertThat(proyectoConsultado.getIdProyecto()).isEqualTo(proyecto.getId());
    }

    @Entonces("no permite editarlo")
    public void no_permite_editarlo() {
        Long idProyecto = proyecto.getId();
        ProyectoRequestDto valido = requestValido();
        assertThatThrownBy(() -> proyectoService.actualizar(idProyecto, valido))
                .isInstanceOf(ConflictoEstadoException.class);
        RequestContextHolder.resetRequestAttributes();
    }

    private ProyectoRequestDto requestValido() {
        return new ProyectoRequestDto()
                .iniciativaInversion(sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto.PROYECTO)
                .nombre("Proyecto de prueba BDD")
                .montoEstimadoInversion(1000.0)
                .idSector(sector.getId())
                .idEjeTematico(ejeTematico.getId())
                .descripcionProyecto("Descripcion de prueba BDD");
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
