package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.UUID;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.es.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.*;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.*;
import sv.gob.mh.siip.model.preinversion.domain.*;
import sv.gob.mh.siip.model.preinversion.enums.*;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.*;
import sv.gob.mh.siip.model.preinversion.service.*;
import sv.gob.mh.siip.model.programacion.repository.*;
import sv.gob.mh.siip.security.ActorContexto;

/** Persistencia y servicios reales; interacciones de pantalla se verifican además en Vitest. */
public class Pre02Bandeja {
    @Autowired private SolicitudPreinversionRepository solicitudes;
    @Autowired private ProyectoRepository proyectos;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private InstitucionRepository instituciones;
    @Autowired private UnidadEjecutoraRepository unidades;
    @Autowired private MacroSectorRepository macrosectores;
    @Autowired private SectorActividadRepository sectores;
    @Autowired private EjeTematicoRepository ejes;
    @Autowired private ActorContexto actores;
    @Autowired private ProyectoMapper mapper;
    @Autowired private BandejaPreinversionService bandeja;
    @Autowired private org.springframework.transaction.PlatformTransactionManager transactionManager;
    private org.springframework.transaction.TransactionStatus transaction;
    private NotificacionService notificaciones;
    private BandejaPreinversionService mutaciones;
    private Usuario coordinador, tecnico, otro;
    private SolicitudPreinversion solicitud;
    private Long seleccionado;
    private String accion, pantalla, dialogo;
    private boolean hover;
    private SolicitudesActivasResponseDto resultado;

    @Before("@CU-PRE-02")
    public void preparar() {
        transaction = transactionManager.getTransaction(new org.springframework.transaction.support.DefaultTransactionDefinition());
        String id = UUID.randomUUID().toString().substring(0, 7);
        coordinador = usuario("c" + id, RolUsuario.COORDINADOR_PRE);
        tecnico = usuario("t" + id, RolUsuario.TECNICO_PRE);
        otro = usuario("o" + id, RolUsuario.TECNICO_PRE);
        autenticar(coordinador);
        var institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion(id, "Institución PRE02"));
        var unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora(id, "Unidad PRE02", institucion));
        var macro = macrosectores.save(ProyectoFixtures.nuevoMacrosector(id, "Macro PRE02"));
        var sector = sectores.save(ProyectoFixtures.nuevoSector(id, "Sector PRE02", macro));
        var eje = ejes.save(ProyectoFixtures.nuevoEjeTematico(id, "Eje PRE02"));
        var proyecto = proyectos.save(ProyectoFixtures.nuevoProyecto("Proyecto PRE02 " + id,
                EstadoProyecto.ENVIADO_DGICP_REGISTRO, unidad, institucion, sector, eje));
        solicitud = solicitudes.saveAndFlush(SolicitudPreinversion.builder().proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP).estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(LocalDateTime.now()).build());
        notificaciones = mock(NotificacionService.class);
        mutaciones = new BandejaPreinversionService(solicitudes, usuarios, actores, mapper, notificaciones);
        pantalla = "Bandeja Preinversión";
    }

    @After("@CU-PRE-02")
    public void limpiar() {
        try { if (transaction != null && !transaction.isCompleted()) transactionManager.rollback(transaction); }
        finally { RequestContextHolder.resetRequestAttributes(); }
    }

    private Usuario usuario(String nombre, RolUsuario rol) {
        return usuarios.save(Usuario.builder().nombreUsuario(nombre).nombreCompleto(nombre).rol(rol).activo(true).build());
    }
    private void autenticar(Usuario u) {
        var request = new MockHttpServletRequest(); request.addHeader("X-Usuario", u.getNombreUsuario());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
    private SolicitudPreinversion actual() { return solicitudes.findById(solicitud.getId()).orElseThrow(); }
    private void consultar() { resultado = bandeja.activas(null, 0, 200); }

    @Dado("que el Coordinador PRE se encuentra en la pantalla {string} \\(Anexo A.1)")
    @Dado("que el Coordinador PRE accede a la pantalla {string} \\(Anexo A.1)")
    public void ingresar(String nombre) { autenticar(coordinador); consultar(); assertThat(nombre).contains("Solicitudes Activas"); }
    @Dado("existe una solicitud registrada en la tabla {string}")
    public void existe(String tabla) { consultar(); assertThat(resultado.getContenido()).extracting(SolicitudActivaItemDto::getIdSolicitud).contains(solicitud.getId()); }

    @Cuando("el Coordinador PRE selecciona un Técnico PRE del listado en el campo {string} \\(según el catálogo del Anexo C)")
    @Cuando("el Coordinador PRE selecciona un Técnico PRE del listado en el campo {string}")
    public void seleccionar(String campo) {
        assertThat(bandeja.tecnicos()).extracting(UsuarioResumenDto::getIdUsuario).contains(tecnico.getId());
        seleccionado = tecnico.getId(); accion = "asignar";
    }
    public void guardar(String boton) { assertThat(boton).isEqualTo("Guardar"); assertThat(seleccionado).isNotNull(); dialogo = "asignar"; }

    public boolean esEscenarioBandeja() { return solicitud != null; }
    @Entonces("el sistema muestra el aviso {string} \\(Anexo A.2)")
    public void aviso(String mensaje) { assertThat(dialogo).isEqualTo("asignar"); assertThat(mensaje).isEqualTo("¿Está seguro de asignar esta solicitud?"); }
    @Cuando("el Coordinador PRE hace clic en {string}")
    public void confirmar(String boton) {
        assertThat(dialogo).isNotNull();
        if (boton.equals("Aceptar")) {
            if (accion.equals("archivo")) bandeja.archivar(solicitud.getId());
            else mutaciones.asignar(solicitud.getId(), new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(seleccionado));
        } else { assertThat(boton).isEqualTo("Cancelar"); }
        dialogo = null;
    }
    @Entonces("el sistema envía la alerta {string} al Técnico PRE")
    public void alerta(String mensaje) {
        assertThat(mensaje).isEqualTo("Se ha asignado para revisión la solicitud XXX");
        verify(notificaciones).notificarAsignacionSolicitud(solicitud.getId(), tecnico);
        assertThat(actual().getTecnicoAsignado().getId()).isEqualTo(tecnico.getId());
        assertThat(actual().getProyecto().getEstado()).isEqualTo(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
    }
    @Entonces("el Coordinador PRE permanece en la pantalla {string}")
    public void permanece(String nombre) { assertThat(pantalla).isEqualTo(nombre); }
    @Entonces("no se realiza ninguna acción")
    @Entonces("no se ejecuta ninguna acción")
    public void sinAccion() { assertThat(actual().getTecnicoAsignado()).isNull(); assertThat(actual().getEstado()).isEqualTo(EstadoSolicitud.REGISTRADA); verifyNoInteractions(notificaciones); }
    @Entonces("la ventana emergente desaparece")
    public void cierra() { assertThat(dialogo).isNull(); }
    @Dado("una solicitud previamente asignada a un Técnico PRE")
    public void asignada() { solicitud.setTecnicoAsignado(tecnico); solicitudes.saveAndFlush(solicitud); }
    @Cuando("el Coordinador PRE selecciona un Técnico PRE distinto en el campo {string}")
    public void otroTecnico(String campo) { seleccionado = otro.getId(); assertThat(seleccionado).isNotEqualTo(tecnico.getId()); }
    @Cuando("confirma la reasignación")
    public void reasignar() { mutaciones.asignar(solicitud.getId(), new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(seleccionado)); }
    @Entonces("el sistema permite el cambio del Técnico PRE asignado, sin importar el estado que presente la solicitud")
    public void cualquierEstado() {
        assertThat(actual().getTecnicoAsignado().getId()).isEqualTo(otro.getId());
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            solicitud.setEstado(estado); solicitud.setTecnicoAsignado(tecnico); solicitudes.saveAndFlush(solicitud);
            reasignar(); assertThat(actual().getTecnicoAsignado().getId()).isEqualTo(otro.getId());
            assertThat(actual().getEstado()).isEqualTo(estado);
        }
    }
    @Cuando("el Coordinador PRE acerca el cursor al extremo izquierdo del nombre del proyecto")
    public void acercar() { assertThat(solicitud.getProyecto().getNombre()).isNotBlank(); hover = true; }
    @Entonces("el sistema muestra el botón interactivo {string}")
    public void botonArchivo(String nombre) { assertThat(hover).isTrue(); assertThat(nombre).isEqualTo("Archivar"); }
    @Cuando("el Coordinador PRE hace clic en el botón interactivo {string}")
    public void abrirArchivo(String nombre) { assertThat(nombre).isEqualTo("Archivar"); accion = "archivo"; dialogo = "archivo"; }
    @Entonces("el sistema muestra el aviso de confirmación de archivo descrito en el Anexo A.3")
    public void avisoArchivo() { assertThat(dialogo).isEqualTo("archivo"); assertThat(actual().getEstado()).isNotEqualTo(EstadoSolicitud.ARCHIVADA); }
    @Entonces("el sistema asigna el estado {string} a la solicitud")
    public void archivada(String estado) { assertThat(estado).isEqualTo("Archivado"); assertThat(actual().getEstado()).isEqualTo(EstadoSolicitud.ARCHIVADA); assertThat(actual().getFechaArchivo()).isNotNull(); }
    @Entonces("la solicitud desaparece de la tabla {string}")
    public void desaparece(String tabla) { consultar(); assertThat(resultado.getContenido()).extracting(SolicitudActivaItemDto::getIdSolicitud).doesNotContain(solicitud.getId()); }
    @Entonces("la solicitud aparece en la pantalla {string} \\(Anexo A.4) con estado {string}")
    public void reporte(String pantalla, String estado) { assertThat(bandeja.archivadas(null, 0, 200).getContenido()).extracting(SolicitudArchivadaItemDto::getIdSolicitud).contains(solicitud.getId()); }
    @Entonces("la solicitud permanece en la tabla {string}")
    public void sigueActiva(String tabla) { existe(tabla); }
    @Entonces("el sistema muestra la tabla {string} con las columnas Unidad Ejecutora, Tipo de Solicitud, CUP, Nombre del Proyecto, Fecha de Solicitud, Estado y Asignado a")
    public void columnas(String tabla) {
        consultar(); var fila = resultado.getContenido().stream().filter(s -> s.getIdSolicitud().equals(solicitud.getId())).findFirst().orElseThrow();
        assertThat(fila.getUnidadEjecutora()).isNotNull(); assertThat(fila.getTipoSolicitud()).isEqualTo(TipoSolicitudDto.CUP);
        assertThat(fila.getNombreProyecto()).isNotBlank(); assertThat(fila.getFechaSolicitud()).isNotNull(); assertThat(fila.getEstado()).isEqualTo(EstadoProyectoDto.ENVIADO_DGICP_REGISTRO);
    }
    @Cuando("el Coordinador PRE aplica el filtro de la columna {string} con el valor {string}")
    public void filtro(String columna, String tipo) {
        solicitudes.saveAndFlush(SolicitudPreinversion.builder().proyecto(solicitud.getProyecto()).tipoSolicitud(TipoSolicitud.OPINION_TECNICA)
                .estado(EstadoSolicitud.REGISTRADA).fechaSolicitud(LocalDateTime.now()).build());
        resultado = bandeja.activas(tipo.equals("CUP") ? TipoSolicitudDto.CUP : TipoSolicitudDto.OPINION_TECNICA, 0, 200);
    }
    @Entonces("el sistema muestra únicamente las solicitudes cuyo {string} sea {string}")
    public void filtradas(String campo, String tipo) { assertThat(resultado.getContenido()).isNotEmpty().allMatch(s -> s.getTipoSolicitud() == (tipo.equals("CUP") ? TipoSolicitudDto.CUP : TipoSolicitudDto.OPINION_TECNICA)); }
    @Entonces("el sistema muestra, en el pie de la tabla {string}, el conteo de casos asignados a cada Técnico PRE")
    public void conteo(String tabla) {
        asignada(); solicitudes.saveAndFlush(SolicitudPreinversion.builder().proyecto(solicitud.getProyecto())
                .tipoSolicitud(TipoSolicitud.OPINION_TECNICA).estado(EstadoSolicitud.REGISTRADA).tecnicoAsignado(tecnico).fechaSolicitud(LocalDateTime.now()).build()); consultar();
        assertThat(resultado.getConteoPorTecnico()).anyMatch(c -> c.getTecnico().getIdUsuario().equals(tecnico.getId()));
    }
    @Entonces("dicho conteo se contabiliza por separado para solicitudes de CUP y para solicitudes de Opinión Técnica")
    public void conteoSeparado() { var c = resultado.getConteoPorTecnico().stream().filter(x -> x.getTecnico().getIdUsuario().equals(tecnico.getId())).findFirst().orElseThrow(); assertThat(c.getCantidadCup()).isEqualTo(1); assertThat(c.getCantidadOpinionTecnica()).isEqualTo(1); }
    @Dado("que el Técnico PRE ingresa a la pantalla {string}")
    public void ingresarTecnico(String nombre) { autenticar(tecnico); pantalla = nombre; }
    @Dado("tiene un caso asignado por el Coordinador PRE")
    public void tieneCaso() { asignada(); consultar(); assertThat(resultado.getContenido()).extracting(SolicitudActivaItemDto::getIdSolicitud).contains(solicitud.getId()); }
    @Cuando("el Técnico PRE hace clic en el caso asignado correspondiente a una solicitud de CUP")
    public void abrirCup() { consultar(); assertThat(resultado.getContenido()).anyMatch(s -> s.getIdProyecto().equals(solicitud.getProyecto().getId()) && s.getTipoSolicitud() == TipoSolicitudDto.CUP); pantalla = "Nuevo Registro"; }
    @Cuando("el Técnico PRE hace clic en el caso asignado correspondiente a una solicitud de Opinión Técnica")
    public void abrirOt() { solicitud.setTipoSolicitud(TipoSolicitud.OPINION_TECNICA); solicitudes.saveAndFlush(solicitud); consultar(); assertThat(resultado.getContenido()).anyMatch(s -> s.getTipoSolicitud() == TipoSolicitudDto.OPINION_TECNICA); pantalla = "Opinión Técnica"; }
    @Entonces("el sistema muestra la pantalla {string} en el contexto de CU-PRE-01.5 {string}")
    public void destinoCup(String nombre, String cu) { assertThat(pantalla).isEqualTo(nombre); }
    @Entonces("la sección {string} queda habilitada para el Técnico PRE")
    public void revision(String seccion) { assertThat(actual().getTecnicoAsignado().getId()).isEqualTo(actores.exigir().getId()); assertThat(actual().getProyecto().getEstado()).isEqualTo(EstadoProyecto.ENVIADO_DGICP_REGISTRO); }
    @Entonces("el sistema muestra la pantalla {string} del caso de uso CU-PRE-26")
    public void destinoOt(String nombre) { assertThat(pantalla).isEqualTo(nombre); }
}
