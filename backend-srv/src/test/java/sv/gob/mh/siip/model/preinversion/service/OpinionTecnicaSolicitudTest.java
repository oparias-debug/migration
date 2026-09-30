package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.ApartadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitudOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;

/** Pruebas unitarias de {@link OpinionTecnicaSolicitud} (CU-PRE-26, RN04, FA04, RN07 a y b). */
class OpinionTecnicaSolicitudTest {

    private static final Long ID_PROYECTO = 1L;
    private static final EtapasOpinionTecnica.Etapas PERFIL = new EtapasOpinionTecnica.Etapas(
            TipoEtapaPreinversion.PERFIL, TipoEtapaPreinversion.PREFACTIBILIDAD);

    private OpinionTecnicaRepository opiniones;
    private BandejaOpinionTecnica bandeja;
    private UsuarioRepository usuarios;
    private OpinionTecnicaAcceso acceso;
    private EtapasOpinionTecnica etapas;
    private DocumentosOpinionTecnica documentos;
    private DestinatariosOpinionTecnica destinatarios;
    private NotificacionService notificaciones;
    private OpinionTecnicaSolicitud solicitud;

    private final Usuario urp = Usuario.builder().id(10L).nombreUsuario("urp").rol(RolUsuario.TECNICO_URP).build();
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        opiniones = mock(OpinionTecnicaRepository.class);
        bandeja = mock(BandejaOpinionTecnica.class);
        usuarios = mock(UsuarioRepository.class);
        acceso = mock(OpinionTecnicaAcceso.class);
        etapas = mock(EtapasOpinionTecnica.class);
        documentos = mock(DocumentosOpinionTecnica.class);
        destinatarios = new DestinatariosOpinionTecnica(usuarios, mock(RevisionViabilidadRepository.class));
        notificaciones = mock(NotificacionService.class);
        solicitud = new OpinionTecnicaSolicitud(opiniones, bandeja, acceso, etapas, documentos,
                destinatarios, notificaciones);
        proyecto = Proyecto.builder().id(ID_PROYECTO).estado(EstadoProyecto.PROYECTO_CON_OT).build();
        when(opiniones.save(any())).thenAnswer(i -> i.getArgument(0));
        when(acceso.impedimentoSolicitud(proyecto)).thenReturn(Optional.empty());
        when(etapas.paraOpinionTecnica(ID_PROYECTO)).thenReturn(PERFIL);
        when(etapas.paraActualizacion(ID_PROYECTO)).thenReturn(Optional.of(PERFIL));
        when(opiniones.existsByProyectoIdAndResultado(ID_PROYECTO, ResultadoOpinionTecnica.FAVORABLE)).thenReturn(true);
    }

    private static MockMultipartFile nota() {
        return new MockMultipartFile("notaSolicitudOt", "nota.pdf", "application/pdf",
                "contenido".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void solicitarAbreLaGestionConSuNotaYAvisaAlCoordinadorPre() {
        OpinionTecnica gestion = solicitud.solicitar(urp, proyecto, nota());

        assertThat(gestion.getTipoSolicitud()).isEqualTo(TipoSolicitudOpinionTecnica.OPINION_TECNICA);
        assertThat(gestion.getPrimeraGestion()).isTrue();
        assertThat(gestion.getEtapaActual()).isEqualTo(TipoEtapaPreinversion.PERFIL);
        assertThat(gestion.getSolicitante()).isSameAs(urp);
        verify(documentos).cargar(eq(gestion), eq(TipoDocumentoOpinionTecnica.NOTA_SOLICITUD_OT), any(), eq(urp));
        verify(notificaciones).notificarSolicitudOpinionTecnica(any(), any());
    }

    @Test
    void sinNotaDeSolicitudOConUnImpedimentoNoSeAbreLaGestion() {
        assertThatThrownBy(() -> solicitud.solicitar(urp, proyecto, null))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo(OpinionTecnicaSolicitud.NOTA_SOLICITUD_OT_REQUERIDA);

        when(acceso.impedimentoSolicitud(proyecto)).thenReturn(Optional.of(new ConflictoEstadoException(
                OpinionTecnicaAcceso.OPINION_TECNICA_EN_CURSO, "En curso")));
        MockMultipartFile nota = nota();
        assertThatThrownBy(() -> solicitud.solicitar(urp, proyecto, nota))
                .isInstanceOf(ConflictoEstadoException.class);
        verifyNoInteractions(bandeja, documentos, notificaciones);
    }

    @Test
    void laActualizacionRequiereUnaOtFavorableSinGestionAbiertaNiEjecucionIniciada() {
        assertThat(solicitud.actualizacionHabilitada(proyecto)).isTrue();

        when(acceso.gestionEnCurso(ID_PROYECTO)).thenReturn(true);
        assertThat(solicitud.actualizacionHabilitada(proyecto)).isFalse();
        assertThatThrownBy(() -> solicitud.solicitarActualizacion(urp, proyecto)).hasMessageContaining("en curso");
        when(acceso.gestionEnCurso(ID_PROYECTO)).thenReturn(false);

        proyecto.setEstado(EstadoProyecto.EN_EJECUCION);
        assertThat(solicitud.actualizacionHabilitada(proyecto)).isFalse();
        assertThatThrownBy(() -> solicitud.solicitarActualizacion(urp, proyecto))
                .hasMessageContaining("ejecución del proyecto ya inició");
        proyecto.setEstado(EstadoProyecto.FINALIZADO);
        assertThat(solicitud.actualizacionHabilitada(proyecto)).isFalse();
        proyecto.setEstado(EstadoProyecto.PROYECTO_CON_OT);

        when(etapas.paraActualizacion(ID_PROYECTO)).thenReturn(Optional.empty());
        assertThat(solicitud.actualizacionHabilitada(proyecto)).isFalse();
        assertThatThrownBy(() -> solicitud.solicitarActualizacion(urp, proyecto))
                .hasMessageContaining("previa para la etapa");

        when(opiniones.existsByProyectoIdAndResultado(ID_PROYECTO, ResultadoOpinionTecnica.FAVORABLE)).thenReturn(false);
        assertThat(solicitud.actualizacionHabilitada(proyecto)).isFalse();
        assertThatThrownBy(() -> solicitud.solicitarActualizacion(urp, proyecto))
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaSolicitud.ACTUALIZACION_OT_NO_DISPONIBLE);
        assertThatThrownBy(() -> solicitud.solicitarActualizacion(urp, proyecto))
                .hasMessageContaining("favorable previa");
    }

    @Test
    void laActualizacionAbreUnaGestionQueNoEsLaPrimera() {
        when(opiniones.existsByProyectoIdAndFechaArchivoIsNull(ID_PROYECTO)).thenReturn(true);

        OpinionTecnica gestion = solicitud.solicitarActualizacion(urp, proyecto);

        assertThat(gestion.getTipoSolicitud()).isEqualTo(TipoSolicitudOpinionTecnica.ACTUALIZACION_OT);
        assertThat(gestion.getPrimeraGestion()).isFalse();
        assertThat(solicitud.opinionTecnicaHabilitada(proyecto)).isTrue();
        assertThat(ApartadoOpinionTecnica.casosDeUso(false)).contains("CU-PRE-04", "CU-PRE-23");

        assertThat(ApartadoOpinionTecnica.casosDeUso(true)).containsExactly("CU-PRE-03.5");
    }

    @Test
    void asignaSoloAUnTecnicoPreActivoMientrasLaGestionEstaEnCurso() {
        Usuario coordinador = Usuario.builder().id(20L).rol(RolUsuario.COORDINADOR_PRE).build();
        Usuario tecnicoPre = Usuario.builder().id(30L).rol(RolUsuario.TECNICO_PRE).activo(true).build();
        Usuario inactivo = Usuario.builder().id(31L).rol(RolUsuario.TECNICO_PRE).activo(false).build();
        when(usuarios.findById(30L)).thenReturn(Optional.of(tecnicoPre));
        when(usuarios.findById(31L)).thenReturn(Optional.of(inactivo));
        when(usuarios.findById(20L)).thenReturn(Optional.of(coordinador));
        OpinionTecnica gestion = OpinionTecnica.builder().id(5L).build();
        OpinionTecnicaContexto contexto = new OpinionTecnicaContexto(coordinador, proyecto, gestion, false);

        assertThatThrownBy(() -> solicitud.asignar(contexto, null)).isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> solicitud.asignar(contexto, 0L)).isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> solicitud.asignar(contexto, 31L)).isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> solicitud.asignar(contexto, 20L)).isInstanceOf(RecursoNoEncontradoException.class);

        assertThat(solicitud.asignar(contexto, 30L).getTecnicoResponsable()).isSameAs(tecnicoPre);
        verify(bandeja).asignar(gestion);
        verify(notificaciones).notificarAsignacionOpinionTecnica(proyecto, tecnicoPre);

        gestion.setResultado(ResultadoOpinionTecnica.OBSERVADO);
        assertThatThrownBy(() -> solicitud.asignar(contexto, 30L)).isInstanceOf(ConflictoEstadoException.class);
    }

    @Test
    void laOpcionOpinionTecnicaSeDesactivaDuranteUnaActualizacion() {
        when(acceso.actualizacionEnCurso(ID_PROYECTO)).thenReturn(true);

        assertThat(solicitud.opinionTecnicaHabilitada(proyecto)).isFalse();
        assertThat(List.of(TipoSolicitudOpinionTecnica.values())).hasSize(2);
    }
}
