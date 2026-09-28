package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Viabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ViabilidadRepository;

/** Pruebas unitarias de {@link ViabilidadCierre} (CU-PRE-24, HU-PRE-24-02 y HU-PRE-24-03). */
class ViabilidadCierreTest {

    private static final Long ID_PROYECTO = 7L;

    private ProyectoRepository proyectos;
    private RevisionViabilidadRepository revisiones;
    private ViabilidadRepository viabilidades;
    private NotificacionService notificaciones;
    private FiltrosPosterioresViabilidad filtros;
    private ViabilidadCierre cierre;

    private Usuario solicitante;
    private Usuario viabilizador;
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        proyectos = mock(ProyectoRepository.class);
        revisiones = mock(RevisionViabilidadRepository.class);
        viabilidades = mock(ViabilidadRepository.class);
        notificaciones = mock(NotificacionService.class);
        filtros = mock(FiltrosPosterioresViabilidad.class);
        cierre = new ViabilidadCierre(proyectos, revisiones, viabilidades, notificaciones, filtros);

        solicitante = Usuario.builder().id(10L).rol(RolUsuario.TECNICO_URP).build();
        viabilizador = Usuario.builder().id(20L).rol(RolUsuario.VIABILIZADOR).build();
        proyecto = Proyecto.builder().id(ID_PROYECTO).estado(EstadoProyecto.EN_VIABILIDAD).build();
    }

    private RevisionViabilidad enCurso(String observaciones) {
        return RevisionViabilidad.builder().numero(1).estado(EstadoRevisionViabilidad.EN_CURSO)
                .solicitante(solicitante).observacionesGenerales(observaciones).build();
    }

    private ViabilidadContexto contexto(RevisionViabilidad revision) {
        return new ViabilidadContexto(viabilizador, proyecto, revision, false, true);
    }

    private Viabilidad resultadoRegistrado() {
        ArgumentCaptor<Viabilidad> captor = ArgumentCaptor.forClass(Viabilidad.class);
        verify(viabilidades).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void devolverCierraLaRevisionRegistraObservadoYNotificaAlSolicitante() {
        RevisionViabilidad revision = enCurso("Ajustar");

        cierre.devolver(contexto(revision));

        assertThat(revision.getEstado()).isEqualTo(EstadoRevisionViabilidad.DEVUELTA);
        assertThat(revision.getViabilizador()).isSameAs(viabilizador);
        assertThat(revision.getFechaCierre()).isNotNull();
        verify(revisiones).save(revision);
        Viabilidad resultado = resultadoRegistrado();
        assertThat(resultado.getResultado()).isEqualTo(ResultadoViabilidad.OBSERVADO);
        assertThat(resultado.getFechaEvaluacion()).isEqualTo(revision.getFechaCierre());
        assertThat(resultado.getObservaciones()).isEqualTo("Ajustar");
        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.OBSERVADO);
        verify(proyectos).save(proyecto);
        verify(notificaciones).notificarComentariosViabilidad(proyecto, solicitante);
    }

    @Test
    void devolverSinRevisionEnCursoSeRechaza() {
        ViabilidadContexto sinRevision = contexto(null);

        assertThatThrownBy(() -> cierre.devolver(sinRevision))
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(ViabilidadContexto.SOLICITUD_VIABILIDAD_NO_VIGENTE);
        verifyNoInteractions(revisiones, viabilidades, proyectos, notificaciones);
    }

    @Test
    void emitirPorPrimeraVezHabilitaElegibilidad() {
        RevisionViabilidad revision = enCurso("Cumple");

        boolean primeraVez = cierre.emitir(contexto(revision));

        assertThat(primeraVez).isTrue();
        assertThat(revision.getHabilitaElegibilidad()).isTrue();
        assertThat(revision.getEstado()).isEqualTo(EstadoRevisionViabilidad.EMITIDA);
        assertThat(resultadoRegistrado().getResultado()).isEqualTo(ResultadoViabilidad.VIABLE);
        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.VIABLE);
        verify(notificaciones).notificarEmisionViabilidad(proyecto, solicitante);
    }

    @Test
    void emitirTrasPasarPorElegibilidadSeSaltaALaOt() {
        when(filtros.yaPasoPorElegibilidad(ID_PROYECTO)).thenReturn(true);
        RevisionViabilidad revision = enCurso("Cumple");

        assertThat(cierre.emitir(contexto(revision))).isFalse();
        assertThat(revision.getHabilitaElegibilidad()).isFalse();
    }

    @Test
    void emitirSinJustificacionSeRechaza() {
        ViabilidadContexto sinJustificacion = contexto(enCurso("  "));

        assertThatThrownBy(() -> cierre.emitir(sinJustificacion))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo(ViabilidadCierre.JUSTIFICACION_VIABILIDAD_REQUERIDA);
        verifyNoInteractions(filtros, viabilidades, proyectos, notificaciones);
    }
}
