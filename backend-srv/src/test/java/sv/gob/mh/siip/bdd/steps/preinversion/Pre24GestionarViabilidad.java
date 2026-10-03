package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.mock.web.MockMultipartFile;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AccionesDisponiblesViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.CampoFichaViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioCampoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.DocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;

/**
 * CU-PRE-24-gestionar-viabilidad.feature (feature "de pantalla" del front). Reutiliza el proyecto,
 * los actores y el {@code ViabilidadServiceImpl} (con notificaciones mockeadas) que arma el
 * {@code @Before("@CU-PRE-24")} de {@link Pre24Viabilidad}, y sus operaciones del flujo.
 *
 * <p>El aviso de confirmación es del cliente: el paso que pide la acción la deja pendiente y
 * "confirma la acción" la ejecuta contra el servicio; cancelar la descarta sin llamar al servidor.
 */
public class Pre24GestionarViabilidad implements PantallaFront {

    private static final String FEATURE = "CU-PRE-24-gestionar-viabilidad.feature";
    private static final String PANTALLA = "Viabilidad del Proyecto";
    private static final String CARGAR_DOCUMENTO = "Cargar documento";
    private static final String COMENTARIO_OBJETIVO = "Precisar el objetivo general";
    private static final String COMENTARIO_PRODUCTOS = "Falta detalle de los productos";
    private static final String JUSTIFICACION = "Se requieren ajustes antes de emitir.";

    private final PantallasFrontComun comun;
    private final Pre24Viabilidad base;

    private FichaViabilidadResponseDto ficha;
    private FichaViabilidadResponseDto fichaAntes;
    private MockMultipartFile archivo;
    private GuardarComentariosViabilidadRequestDto borrador;
    private Runnable accionPendiente;
    private EmitirViabilidadResponseDto emision;
    private RuntimeException error;

    public Pre24GestionarViabilidad(PantallasFrontComun comun, Pre24Viabilidad base) {
        this.comun = comun;
        this.base = base;
    }

    @Before
    public void activar(Scenario scenario) {
        comun.activarSi(scenario, FEATURE, this);
    }

    // ------------------------------------------------------------------ pasos compartidos

    @Override
    public void haceClic(String boton) {
        switch (boton) {
            case CARGAR_DOCUMENTO -> capturar(() -> cargar(archivo));
            case "Guardar" -> capturar(() -> base.guardar(borrador));
            default -> throw new IllegalArgumentException("Botón no reconocido: " + boton);
        }
    }

    @Override
    public void tecnicoUrpHaceClic(String boton) {
        assertThat(boton).isEqualTo("Solicitar viabilidad");
        accionPendiente = base::solicitar;
    }

    @Override
    public void confirmaAccion() {
        assertThat(accionPendiente).as("No hay ninguna acción pendiente de confirmar").isNotNull();
        capturar(accionPendiente);
        accionPendiente = null;
    }

    @Override
    public void consultaFalla() {
        Pre24Viabilidad.autenticar(base.tecnicoUrp());
        Long inexistente = -base.proyecto().getId();
        ficha = null;
        capturar(() -> ficha = base.enTransaccion(() -> base.service().consultarFicha(inexistente)));
    }

    // ------------------------------------------------------------------ Antecedentes

    @Dado("que el proyecto está formulado y tiene CUP")
    public void proyectoFormuladoConCup() {
        base.sembrarFicha();
        assertThat(base.proyectoActual().getEstado()).isEqualTo(EstadoProyecto.EN_FORMULACION);
        assertThat(base.proyectoActual().getCup()).isNotBlank();
    }

    @Dado("el actor se encuentra en la pantalla {string} \\(Anexo A.1)")
    public void actorEnPantalla(String pantalla) {
        assertThat(pantalla).isEqualTo(PANTALLA);
        ficha = base.fichaComo(base.tecnicoUrp());
        assertThat(ficha.getProyectoId()).isEqualTo(base.proyecto().getId());
    }

    // ------------------------------------------------------------------ Ficha del proyecto

    @Entonces("el sistema muestra el objetivo general, la descripción, los productos y la población objetivo")
    public void muestraDatosGenerales() {
        for (String campo : List.of("Objetivo General", "Descripción", "Productos", "Población objetivo")) {
            base.verificarCampo(ficha, campo);
        }
    }

    @Entonces("muestra la inversión estimada, el costo de operación, el costo de mantenimiento "
            + "y los indicadores de evaluación")
    public void muestraDatosEconomicos() {
        for (String campo : List.of("Inversión estimada", "Costo de operación", "Costo de mantenimiento",
                "Indicadores de evaluación")) {
            base.verificarCampo(ficha, campo);
        }
    }

    @Entonces("todos esos datos provienen de los capítulos ya registrados y no se editan aquí")
    public void datosNoEditables() {
        // La única escritura de la ficha ("Guardar") solo lleva comentarios y la justificación.
        assertThat(Arrays.stream(GuardarComentariosViabilidadRequestDto.class.getDeclaredFields())
                .map(Field::getName))
                .containsExactlyInAnyOrder("comentariosViabilizador", "observacionesGeneralesJustificacion");
        assertThat(ficha.getAccionesDisponibles().getGuardarComentarios()).isFalse();
    }

    // ------------------------------------------------------------------ Documento de Preinversión

    @Cuando("el Técnico URP elige el tipo de documento y el archivo")
    public void eligeTipoYArchivo() {
        archivo = new MockMultipartFile("archivo", "preinversion.pdf", "application/pdf",
                "%PDF-1.4 documento".getBytes(StandardCharsets.UTF_8));
    }

    @Entonces("el documento aparece en la lista con su tipo y su fecha de carga")
    public void documentoEnLaLista() {
        assertThat(error).isNull();
        List<DocumentoViabilidadDto> documentos = base.fichaComo(base.tecnicoUrp()).getDocumentos();
        assertThat(documentos).singleElement().satisfies((DocumentoViabilidadDto d) -> {
            assertThat(d.getTipoDocumento()).isEqualTo(TipoDocumentoViabilidadDto.DOCUMENTO_PREINVERSION);
            assertThat(d.getNombreArchivo()).isEqualTo("preinversion.pdf");
            assertThat(d.getFechaCarga()).isNotNull();
        });
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber elegido archivo")
    public void cargarSinArchivo(String boton) {
        assertThat(boton).isEqualTo(CARGAR_DOCUMENTO);
        // El cliente no llega a llamar al servicio; si la petición llegara sin archivo, el servidor la rechaza.
        MockMultipartFile vacio = new MockMultipartFile("archivo", new byte[0]);
        capturar(() -> cargar(vacio));
    }

    @Entonces("el sistema lo indica y no llama al servicio")
    public void indicaFaltaDeArchivo() {
        assertThat(error).isInstanceOf(ValidacionNegocioException.class);
        assertThat(base.fichaComo(base.tecnicoUrp()).getDocumentos()).isEmpty();
    }

    // ------------------------------------------------------------------ Solicitar la viabilidad

    @Dado("que el proyecto tiene cargado el Documento de Preinversión y no hay una solicitud en curso")
    public void documentoCargadoSinSolicitud() {
        base.cargarDocumentoPreinversion();
        AccionesDisponiblesViabilidadDto acciones = base.fichaComo(base.tecnicoUrp()).getAccionesDisponibles();
        assertThat(acciones.getSolicitarViabilidad()).isTrue();
        assertThat(base.fichaComo(base.viabilizador()).getAccionesDisponibles().getGuardarComentarios()).isFalse();
    }

    @Entonces("el sistema registra la solicitud y la ficha refleja el nuevo estado")
    public void solicitudRegistrada() {
        assertThat(error).isNull();
        assertThat(base.revisionActual().getEstado()).isEqualTo(EstadoRevisionViabilidad.EN_CURSO);
        FichaViabilidadResponseDto actual = base.fichaComo(base.tecnicoUrp());
        assertThat(actual.getEstadoProyecto()).isEqualTo(EstadoProyecto.EN_VIABILIDAD.getEtiquetaUi());
        assertThat(actual.getAccionesDisponibles().getSolicitarViabilidad()).isFalse();
    }

    // ------------------------------------------------------------------ Comentarios del Viabilizador

    @Dado("que quien revisa tiene la ficha habilitada")
    public void revisorConFichaHabilitada() {
        solicitudVigente();
        assertThat(base.fichaComo(base.viabilizador()).getAccionesDisponibles().getGuardarComentarios()).isTrue();
    }

    @Cuando("escribe un comentario en uno o varios campos y la justificación general")
    public void escribeComentarios() {
        borrador = Pre24Viabilidad.borrador(COMENTARIO_OBJETIVO, COMENTARIO_PRODUCTOS, JUSTIFICACION);
        // Un campo que se deja en blanco: no debe guardarse.
        borrador.addComentariosViabilizadorItem(new ComentarioCampoViabilidadDto(CampoFichaViabilidadDto.DESCRIPCION,
                "   "));
    }

    @Entonces("el sistema guarda únicamente los campos con comentario")
    public void guardaSoloCamposConComentario() {
        assertThat(error).isNull();
        assertThat(base.fichaComo(base.viabilizador()).getComentariosViabilizador())
                .extracting(ComentarioCampoViabilidadDto::getCampo)
                .containsExactlyInAnyOrder(CampoFichaViabilidadDto.OBJETIVO_GENERAL, CampoFichaViabilidadDto.PRODUCTOS);
    }

    @Entonces("al volver a abrir la ficha los comentarios siguen ahí")
    public void comentariosPersistentes() {
        FichaViabilidadResponseDto reabierta = base.fichaComo(base.viabilizador());
        assertThat(reabierta.getComentariosViabilizador()).extracting(ComentarioCampoViabilidadDto::getComentario)
                .containsExactlyInAnyOrder(COMENTARIO_OBJETIVO, COMENTARIO_PRODUCTOS);
        assertThat(reabierta.getObservacionesGeneralesJustificacion()).isEqualTo(JUSTIFICACION);
    }

    @Dado("que hay una solicitud de viabilidad vigente")
    public void solicitudVigente() {
        base.cargarDocumentoPreinversion();
        base.solicitar();
        assertThat(base.revisionActual().getEstado()).isEqualTo(EstadoRevisionViabilidad.EN_CURSO);
    }

    @Cuando("quien revisa hace clic en {string}")
    public void revisorHaceClic(String boton) {
        switch (boton) {
            case "Enviar comentarios" -> {
                base.guardar(Pre24Viabilidad.borrador(COMENTARIO_OBJETIVO, null, JUSTIFICACION));
                accionPendiente = base::enviar;
            }
            case "Emitir viabilidad" -> {
                // "Quien revisa" presupone una solicitud vigente con la justificación ya guardada.
                solicitudVigente();
                base.guardar(Pre24Viabilidad.borrador(null, null, "El proyecto cumple los criterios."));
                accionPendiente = () -> emision = base.emitir();
            }
            default -> throw new IllegalArgumentException("Botón no reconocido: " + boton);
        }
    }

    @Entonces("el sistema envía los comentarios y el proyecto queda observado")
    public void comentariosEnviados() {
        assertThat(error).isNull();
        assertThat(base.proyectoActual().getEstado()).isEqualTo(EstadoProyecto.OBSERVADO);
        assertThat(base.revisionActual().getEstado()).isEqualTo(EstadoRevisionViabilidad.DEVUELTA);
        Long idProyecto = base.proyecto().getId();
        Long idTecnico = base.tecnicoUrp().getId();
        verify(base.notificaciones()).notificarComentariosViabilidad(
                argThat((Proyecto p) -> p.getId().equals(idProyecto)),
                argThat((Usuario u) -> u.getId().equals(idTecnico)));
    }

    // ------------------------------------------------------------------ Emitir la viabilidad

    @Entonces("el proyecto queda viable")
    public void proyectoViable() {
        assertThat(error).isNull();
        assertThat(base.proyectoActual().getEstado()).isEqualTo(EstadoProyecto.VIABLE);
        assertThat(base.revisionActual().getEstado()).isEqualTo(EstadoRevisionViabilidad.EMITIDA);
    }

    @Entonces("la ficha deja de admitir cambios")
    public void fichaDeshabilitada() {
        AccionesDisponiblesViabilidadDto acciones = base.fichaComo(base.viabilizador()).getAccionesDisponibles();
        assertThat(acciones.getGuardarComentarios()).isFalse();
        assertThat(acciones.getEnviarComentarios()).isFalse();
        assertThat(acciones.getEmitirViabilidad()).isFalse();
        assertThat(base.fichaComo(base.tecnicoUrp()).getAccionesDisponibles().getSolicitarViabilidad()).isFalse();
        capturar(() -> base.guardar(Pre24Viabilidad.borrador(null, null, "Cambio posterior")));
        assertThat(error).isInstanceOf(ConflictoEstadoException.class);
        assertThat(((ConflictoEstadoException) error).getCodigo()).isEqualTo("FICHA_VIABILIDAD_DESHABILITADA");
    }

    @Entonces("se ofrece continuar a Elegibilidad")
    public void ofreceElegibilidad() {
        assertThat(emision.getElegibilidadHabilitada()).isTrue();
        assertThat(base.fichaComo(base.viabilizador()).getAccionesDisponibles().getIrAElegibilidad()).isTrue();
    }

    // ------------------------------------------------------------------ Cancelar / sólo lectura / error

    @Cuando("el actor pide una acción y cancela en el aviso de confirmación")
    public void pideYCancela() {
        base.cargarDocumentoPreinversion();
        fichaAntes = base.fichaComo(base.tecnicoUrp());
        assertThat(fichaAntes.getAccionesDisponibles().getSolicitarViabilidad()).isTrue();
        tecnicoUrpHaceClic("Solicitar viabilidad");
        // Cancelar cierra el aviso en el cliente: la acción pendiente se descarta sin llamar al servidor.
        accionPendiente = null;
    }

    @Entonces("no se ejecuta nada y la ficha queda como estaba")
    public void nadaEjecutado() {
        assertThat(base.fichaComo(base.tecnicoUrp())).isEqualTo(fichaAntes);
        assertThat(base.proyectoActual().getEstado()).isEqualTo(EstadoProyecto.EN_FORMULACION);
    }

    @Dado("que el actor no tiene ninguna acción habilitada para el estado actual")
    public void actorSinAcciones() {
        // El Viabilizador, antes de que el Técnico URP solicite la viabilidad.
        ficha = base.fichaComo(base.viabilizador());
    }

    @Entonces("la ficha se muestra en sólo lectura, sin botones de acción ni campos de comentario")
    public void fichaSoloLectura() {
        AccionesDisponiblesViabilidadDto acciones = ficha.getAccionesDisponibles();
        assertThat(List.of(acciones.getSolicitarViabilidad(), acciones.getGuardarComentarios(),
                acciones.getEnviarComentarios(), acciones.getEmitirViabilidad(), acciones.getIrAElegibilidad()))
                .containsOnly(false);
        capturar(() -> base.guardar(Pre24Viabilidad.borrador(COMENTARIO_OBJETIVO, null, null)));
        assertThat(error).isInstanceOf(ConflictoEstadoException.class);
    }

    @Entonces("el sistema muestra el error en vez de una ficha vacía")
    public void errorEnVezDeFichaVacia() {
        assertThat(error).isInstanceOf(RecursoNoEncontradoException.class);
        assertThat(ficha).isNull();
    }

    // ------------------------------------------------------------------ helpers

    private void cargar(MockMultipartFile documento) {
        Pre24Viabilidad.autenticar(base.tecnicoUrp());
        Long idProyecto = base.proyecto().getId();
        base.enTransaccion(() -> base.service().cargarDocumento(idProyecto,
                TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION, documento));
    }

    private void capturar(Runnable accion) {
        error = null;
        try {
            accion.run();
        } catch (RuntimeException ex) {
            error = ex;
        }
    }
}
