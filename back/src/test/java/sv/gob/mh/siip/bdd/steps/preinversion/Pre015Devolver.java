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
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01.5-devolver.feature. Las Antecedentes ("el Técnico PRE ingresó..."/"el proyecto se
 * encuentra en estado...") y los pasos "el sistema cambia el estado del proyecto a {string}",
 * "el sistema informa al Técnico URP..." y "el sistema pasa a la pantalla {string}" tienen texto
 * identico en CU-PRE-01.5-emitir-cup.feature (Cucumber exige una unica definicion por texto): se
 * definen una unica vez aqui y Pre015EmitirCup los reutiliza via {@link ContextoProyectoBdd} y
 * recargando el Proyecto por id. El clic en "Devolver" comparte texto con otros botones ("Guardar",
 * "Enviar") y su no-op ya esta definido en Pre01ResponderObservaciones; la accion real se dispara en
 * el primer paso propio que sigue ("el sistema cambia el estado del proyecto a {string}").
 */
public class Pre015Devolver {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String ESTADO_OBSERVADO_UI = "Observado DGICP (Registro)";

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

    private UnidadEjecutora unidadEjecutora;
    private Institucion institucion;
    private Usuario tecnicoPre;
    private Usuario tecnicoUrp;
    private String textoComentario;

    public Pre015Devolver(InstitucionRepository institucionRepository,
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

    @Dado("que el Técnico PRE ingresó a la pantalla {string} desde el caso asignado en la Bandeja Preinversión \\(CU-PRE-02)")
    public void que_el_tecnico_pre_ingreso_a_la_pantalla_desde_el_caso_asignado(String pantalla) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-PRE-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioUrp = "tecnico.urp.bdd.pre." + sufijo;
        tecnicoUrp = usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioUrp)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioUrp + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        String nombreUsuarioPre = "tecnico.pre.bdd." + sufijo;
        tecnicoPre = usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioPre)
                .nombreCompleto("Tecnico PRE (BDD)")
                .correo(nombreUsuarioPre + "@example.com")
                .rol(RolUsuario.TECNICO_PRE)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        autenticarComo(nombreUsuarioPre);
    }

    @Dado("el proyecto se encuentra en estado {string}")
    public void el_proyecto_se_encuentra_en_estado(String estadoUi) {
        if (!"Enviado a DGICP (Registro)".equals(estadoUi)) {
            throw new IllegalArgumentException(
                    "Estado no soportado en las Antecedentes de CU-PRE-01.5: " + estadoUi);
        }

        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE-" + sufijo, "Eje temático de prueba"));

        // El proyecto lo registro el Técnico URP (CU-PRE-01): se autentica momentaneamente como el
        // para que la auditoria (usuarioCreacion) apunte a el. Anexo A.3.2/A.3.4 resuelven asi al
        // destinatario URP, ya que Proyecto no guarda un "responsable URP" propio.
        autenticarComo(tecnicoUrp.getNombreUsuario());
        Proyecto proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto en revisión PRE",
                EstadoProyecto.ENVIADO_DGICP_REGISTRO, unidadEjecutora, institucion, sector, ejeTematico));
        autenticarComo(tecnicoPre.getNombreUsuario());

        // "el caso asignado en la Bandeja Preinversión (CU-PRE-02)": la asignacion en si es de otro
        // CU, fuera de este fragmento; se modela aqui el efecto que CU-PRE-01.5 necesita
        // (tecnicoAsignado) para exigir que la solicitud pertenezca al Técnico PRE autenticado.
        SolicitudPreinversion solicitud = solicitudRepository.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP)
                .estado(EstadoSolicitud.ASIGNADA)
                .fechaSolicitud(LocalDateTime.now().minusDays(1))
                .tecnicoAsignado(tecnicoPre)
                .fechaAsignacion(LocalDateTime.now())
                .build());
        assertThat(solicitud.getId()).isNotNull();

        contextoProyecto.setProyectoActual(proyecto);
    }

    @Cuando("el Técnico PRE digita observaciones en el campo {string} de la sección {string}")
    public void el_tecnico_pre_digita_observaciones_en_el_campo_de_la_seccion(String campo, String seccion) {
        textoComentario = "Falta justificar el monto estimado de inversión.";
    }

    @Entonces("el sistema cambia el estado del proyecto a {string}")
    public void el_sistema_cambia_el_estado_del_proyecto_a(String estadoUi) {
        Long idProyecto = contextoProyecto.getProyectoActual().getId();
        if (ESTADO_OBSERVADO_UI.equals(estadoUi)) {
            // Unico punto donde CU-PRE-01.5-devolver.feature dispara la accion real: el clic en
            // "Devolver" (paso anterior) es el no-op generico compartido con otros botones.
            proyectoService.devolverSolicitudCup(idProyecto,
                    new DevolucionSolicitudRequestDto().comentario(textoComentario));
        }

        Proyecto recargado = proyectoRepository.findById(idProyecto).orElseThrow();
        EstadoProyecto esperado = ESTADO_OBSERVADO_UI.equals(estadoUi)
                ? EstadoProyecto.OBSERVADO_DGICP_REGISTRO
                : EstadoProyecto.CUP_ASIGNADO;
        assertThat(recargado.getEstado()).isEqualTo(esperado);
    }

    @Entonces("el sistema informa al Técnico URP por correo electrónico según el modelo del Anexo A.{double}")
    public void el_sistema_informa_al_tecnico_urp_por_correo_electronico_segun_el_modelo_del_anexo_a(Double anexo) {
        // El numero de Anexo (3.2 aqui, 3.4 en emitir-cup) solo referencia el modelo de correo; el
        // tipo {double} de Cucumber Expressions es sensible al locale de la JVM para el separador
        // decimal, asi que no se compara (mismo criterio que en Pre01SolicitarCup). Lo verificable
        // es el efecto real: la accion de revision/emision ya se aplico sobre el proyecto.
        Proyecto recargado = proyectoRepository.findById(contextoProyecto.getProyectoActual().getId()).orElseThrow();
        assertThat(recargado.getEstado())
                .isIn(EstadoProyecto.OBSERVADO_DGICP_REGISTRO, EstadoProyecto.CUP_ASIGNADO);
    }

    @Entonces("el sistema pasa a la pantalla {string}")
    public void el_sistema_pasa_a_la_pantalla(String pantalla) {
        // Navegacion de UI pura, sin efecto propio que verificar en el backend.
    }

    @Entonces("habilita, para el Técnico URP, el campo {string} en CU-PRE-01")
    public void habilita_para_el_tecnico_urp_el_campo_en_cu_pre_01(String campo) {
        // RN 6 (CU-PRE-01): el campo "Respuesta" solo se habilita cuando el proyecto esta en este
        // estado; ya verificado por el metodo responderObservacionCup en Pre01ResponderObservaciones.
        Proyecto recargado = proyectoRepository.findById(contextoProyecto.getProyectoActual().getId()).orElseThrow();
        assertThat(recargado.getEstado()).isEqualTo(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
        RequestContextHolder.resetRequestAttributes();
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
