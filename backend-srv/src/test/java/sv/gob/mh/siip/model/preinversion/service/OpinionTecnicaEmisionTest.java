package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/** Pruebas unitarias de {@link OpinionTecnicaEmision} (CU-PRE-26, FA01, RN07 e, RN10, RN11). */
class OpinionTecnicaEmisionTest {

    private OpinionTecnicaRepository opiniones;
    private BandejaOpinionTecnica bandeja;
    private EtapasOpinionTecnica etapas;
    private static final String NUMERO = "MH.DGICP.DGI/001.070/2026";

    private NotaEmisionOpinionTecnica nota;
    private DestinatariosOpinionTecnica destinatarios;
    private NotificacionService notificaciones;
    private OpinionTecnicaEmision emision;

    private final Usuario tecnicoPre = Usuario.builder().id(1L).rol(RolUsuario.TECNICO_PRE).build();
    private final Usuario coordinador = Usuario.builder().id(2L).rol(RolUsuario.COORDINADOR_PRE).build();
    private Proyecto proyecto;
    private OpinionTecnica gestion;

    @BeforeEach
    void setUp() {
        opiniones = mock(OpinionTecnicaRepository.class);
        bandeja = mock(BandejaOpinionTecnica.class);
        etapas = mock(EtapasOpinionTecnica.class);
        nota = mock(NotaEmisionOpinionTecnica.class);
        destinatarios = mock(DestinatariosOpinionTecnica.class);
        notificaciones = mock(NotificacionService.class);
        emision = new OpinionTecnicaEmision(opiniones, mock(ProyectoRepository.class), bandeja, etapas, nota,
                destinatarios, notificaciones);
        proyecto = Proyecto.builder().id(3L).estado(EstadoProyecto.ELEGIBLE).build();
        gestion = OpinionTecnica.builder().id(4L).etapaActual(TipoEtapaPreinversion.DISENO)
                .etapaFutura(TipoEtapaPreinversion.EJECUCION).build();
        when(destinatarios.tecnicosPre(gestion)).thenReturn(List.of(tecnicoPre));
    }

    private OpinionTecnicaContexto contexto(Usuario actor) {
        return new OpinionTecnicaContexto(actor, proyecto, gestion, false);
    }

    @Test
    void elVistoBuenoRequiereConclusionesYSeDaUnaSolaVez() {
        OpinionTecnicaContexto delCoordinador = contexto(coordinador);
        assertThatThrownBy(() -> emision.darVistoBueno(delCoordinador))
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaEmision.CONCLUSIONES_NO_REGISTRADAS);

        gestion.getRevisionConclusiones().registrar("Cumple");
        emision.darVistoBueno(contexto(coordinador));
        emision.darVistoBueno(contexto(coordinador));

        assertThat(gestion.getRevisionConclusiones().tieneVistoBueno()).isTrue();
        assertThat(gestion.getRevisionConclusiones().getCoordinadorVistoBueno()).isSameAs(coordinador);
        verify(opiniones).save(gestion);
        verify(notificaciones).notificarVistoBuenoOpinionTecnica(proyecto, tecnicoPre);
    }

    @Test
    void sinVistoBuenoNoSeEmite() {
        OpinionTecnicaContexto delTecnicoPre = contexto(tecnicoPre);
        assertThatThrownBy(() -> emision.emitirFavorable(delTecnicoPre, null, NUMERO))
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaEmision.VISTO_BUENO_OT_PENDIENTE);
    }

    @Test
    void emiteLaOtFavorableConSuNotaYHabilitaLaEjecucion() {
        gestion.getRevisionConclusiones().registrar("Cumple");
        gestion.getRevisionConclusiones().darVistoBueno(null, LocalDateTime.now());
        MockMultipartFile archivo = new MockMultipartFile("notaOt", "nota.pdf", "application/pdf",
                "x".getBytes(StandardCharsets.UTF_8));

        boolean disponibleEnCaptura = emision.emitirFavorable(contexto(tecnicoPre), archivo, NUMERO);

        assertThat(disponibleEnCaptura).isTrue();
        assertThat(gestion.getResultado()).isEqualTo(ResultadoOpinionTecnica.FAVORABLE);
        assertThat(gestion.getObservaciones()).isEqualTo("Cumple");
        assertThat(gestion.getTecnicoResponsable()).isSameAs(tecnicoPre);
        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.PROYECTO_CON_OT);
        verify(nota).registrar(gestion, archivo, NUMERO, tecnicoPre);
        verify(etapas).marcarEmitida(3L, TipoEtapaPreinversion.DISENO);
        verify(bandeja).aprobar(gestion);
        verify(notificaciones).notificarEmisionOpinionTecnica(any(), any());
    }

    @Test
    void soloElTecnicoPreAsignadoEmiteYConservaLaAsignacion() {
        Usuario asignado = Usuario.builder().id(9L).rol(RolUsuario.TECNICO_PRE).build();
        gestion.setTecnicoResponsable(asignado);
        gestion.getRevisionConclusiones().darVistoBueno(null, LocalDateTime.now());
        gestion.setEtapaFutura(TipoEtapaPreinversion.DISENO);
        gestion.setEtapaActual(TipoEtapaPreinversion.PERFIL);
        OpinionTecnicaContexto deOtroTecnico = contexto(tecnicoPre);

        assertThatThrownBy(() -> emision.emitirFavorable(deOtroTecnico, null, NUMERO))
                .isInstanceOf(AccesoDenegadoException.class);
        verify(nota, never()).registrar(any(), any(), any(), any());

        boolean disponibleEnCaptura = emision.emitirFavorable(contexto(asignado), null, NUMERO);

        assertThat(disponibleEnCaptura).isFalse();
        assertThat(gestion.getTecnicoResponsable()).isSameAs(asignado);
    }
}
