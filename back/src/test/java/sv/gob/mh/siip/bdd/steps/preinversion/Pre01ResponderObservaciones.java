package sv.gob.mh.siip.bdd.steps.preinversion;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioSolicitudRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01-responder-observaciones.feature. "un proyecto en estado {string}" tambien lo usa
 * CU-PRE-01-solicitar-cup.feature (Cucumber exige una unica definicion por texto); "hace clic en
 * el botón {string}" es el paso generico compartido con CU-PRE-01-registrar-nuevo-proyecto.feature
 * (aqui solo hace no-op). La accion real de responder la observacion se dispara en el primer paso
 * propio de esta clase que sigue a ese clic ("el sistema notifica al Técnico PRE..."), ya que el
 * clic en si mismo no tiene un texto propio en el que enganchar la llamada al servicio.
 */
public class Pre01ResponderObservaciones {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final SolicitudPreinversionRepository solicitudRepository;
    private final ComentarioSolicitudRepository comentarioRepository;
    private final ProyectoService proyectoService;
    private final ContextoProyectoBdd contextoProyecto;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyecto;
    private String textoRespuesta;

    public Pre01ResponderObservaciones(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            SolicitudPreinversionRepository solicitudRepository,
            ComentarioSolicitudRepository comentarioRepository,
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
        this.comentarioRepository = comentarioRepository;
        this.proyectoService = proyectoService;
        this.contextoProyecto = contextoProyecto;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    // "un proyecto en estado {string}" también es usado por CU-PRE-01-solicitar-cup.feature;
    // se define una única vez aquí para no generar un step duplicado/ambiguo.
    @Dado("un proyecto en estado {string}")
    public void un_proyecto_en_estado(String estadoEtiqueta) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-OBS-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-OBS-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.bdd.obs." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioTecnico + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        EstadoProyecto estado = mapearEstado(estadoEtiqueta);
        autenticarComo(nombreUsuarioTecnico);

        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-OBS-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto " + estadoEtiqueta, estado,
                unidadEjecutora, institucion, sector, ejeTematico));
        contextoProyecto.setProyectoActual(proyecto);

        if (estado == EstadoProyecto.OBSERVADO_DGICP_REGISTRO) {
            // Simula que CU-PRE-01.5 (Revision y Emision de CUP, Tecnico PRE) ya devolvio el
            // expediente con una observacion; ese flujo no forma parte de este fragmento OpenAPI.
            String nombreUsuarioTecnicoPre = "tecnico.pre.bdd.obs." + sufijo;
            Usuario tecnicoPre = usuarioRepository.save(Usuario.builder()
                    .nombreUsuario(nombreUsuarioTecnicoPre)
                    .nombreCompleto("Tecnico PRE (BDD)")
                    .correo(nombreUsuarioTecnicoPre + "@example.com")
                    .rol(RolUsuario.TECNICO_PRE)
                    .unidadEjecutora(unidadEjecutora)
                    .institucion(institucion)
                    .activo(true)
                    .build());

            SolicitudPreinversion solicitud = solicitudRepository.save(SolicitudPreinversion.builder()
                    .proyecto(proyecto)
                    .tipoSolicitud(TipoSolicitud.CUP)
                    .estado(EstadoSolicitud.OBSERVADA)
                    .fechaSolicitud(LocalDateTime.now().minusDays(2))
                    .build());

            comentarioRepository.save(ComentarioSolicitud.builder()
                    .solicitud(solicitud)
                    .autor(tecnicoPre)
                    .texto("Falta justificar el monto estimado de inversión.")
                    .fechaComentario(LocalDateTime.now().minusDays(1))
                    .build());
        }
    }

    @Cuando("el sistema permite al Técnico URP visualizar los comentarios del Técnico PRE en la sección {string}")
    public void el_sistema_permite_al_tecnico_urp_visualizar_los_comentarios_del_tecnico_pre_en_la_seccion(
            String seccion) {
        ProyectoDto detalle = proyectoService.obtener(proyecto.getId());
        assertThat(detalle.getRevisionPre()).isNotEmpty();
    }

    @Cuando("habilita el campo {string}")
    public void habilita_el_campo(String campo) {
        // RN 6: el campo "Respuesta" solo se habilita cuando el proyecto esta en este estado.
        Proyecto recargado = proyectoRepository.findById(proyecto.getId()).orElseThrow();
        assertThat(recargado.getEstado()).isEqualTo(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
    }

    @Cuando("el Técnico URP ajusta los campos correspondientes en la pantalla {string} y\\/o digita comentarios justificativos en el campo {string}")
    public void el_tecnico_urp_ajusta_los_campos_y_o_digita_comentarios_justificativos(String pantalla,
            String campoRespuesta) {
        textoRespuesta = "Se corrige el monto estimado de inversión según lo solicitado.";
    }

    // Ver comentario de la clase: el clic en "Enviar" comparte texto con "Guardar"
    // (CU-PRE-01-registrar-nuevo-proyecto.feature) y solo hace no-op.
    @Cuando("hace clic en el botón {string}")
    public void hace_clic_en_el_boton(String boton) {
        // No-op intencional: ver comentario de la clase.
    }

    @Entonces("el sistema notifica al Técnico PRE por correo electrónico según el modelo del Anexo A.{double}")
    public void el_sistema_notifica_al_tecnico_pre_por_correo_electronico_segun_el_modelo_del_anexo_a(
            Double anexo) {
        // El numero de Anexo solo referencia el modelo de correo (Anexo A.3.3); no tiene
        // significado de negocio que verificar aqui, y el tipo {double} de Cucumber Expressions
        // es sensible al locale de la JVM para el separador decimal, asi que no se compara.
        ProyectoDto actualizado = proyectoService.responderObservacionCup(proyecto.getId(),
                new RespuestaObservacionRequestDto().respuesta(textoRespuesta));
        assertThat(actualizado.getEstado()).isEqualTo(EstadoProyectoDto.ENVIADO_DGICP_REGISTRO);
    }

    @Entonces("guarda los datos registrados")
    public void guarda_los_datos_registrados() {
        ProyectoDto recargado = proyectoService.obtener(proyecto.getId());
        assertThat(recargado.getRevisionPre()).anyMatch(c -> textoRespuesta.equals(c.getTexto()));
    }

    @Entonces("se pasa a la pantalla {string}")
    public void se_pasa_a_la_pantalla(String pantalla) {
        RequestContextHolder.resetRequestAttributes();
    }

    private EstadoProyecto mapearEstado(String etiqueta) {
        return switch (etiqueta) {
            case "Observado DGICP (Registro)" -> EstadoProyecto.OBSERVADO_DGICP_REGISTRO;
            case "Enviado a DGICP (Registro)" -> EstadoProyecto.ENVIADO_DGICP_REGISTRO;
            case "En Elaboración" -> EstadoProyecto.EN_REGISTRO;
            default -> throw new IllegalArgumentException("Estado no reconocido: " + etiqueta);
        };
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
