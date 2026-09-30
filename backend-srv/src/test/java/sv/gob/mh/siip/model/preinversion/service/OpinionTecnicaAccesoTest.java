package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitudOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link OpinionTecnicaAcceso} (CU-PRE-26, RN01, RN04, Anexo A – RN2). */
class OpinionTecnicaAccesoTest {

    private static final Long ID_PROYECTO = 1L;

    private ActorContexto actorContexto;
    private ProyectoRepository proyectos;
    private OpinionTecnicaRepository opiniones;
    private FiltrosPosterioresViabilidad filtros;
    private OpinionTecnicaAcceso acceso;

    private final UnidadEjecutora unidad = UnidadEjecutora.builder().id(7L).build();
    private final Usuario tecnicoPre = Usuario.builder().id(2L).rol(RolUsuario.TECNICO_PRE).build();
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        actorContexto = mock(ActorContexto.class);
        proyectos = mock(ProyectoRepository.class);
        opiniones = mock(OpinionTecnicaRepository.class);
        filtros = mock(FiltrosPosterioresViabilidad.class);
        acceso = new OpinionTecnicaAcceso(actorContexto, proyectos, opiniones, filtros);
        proyecto = Proyecto.builder().id(ID_PROYECTO).estado(EstadoProyecto.ELEGIBLE).unidadEjecutora(unidad).build();
        when(proyectos.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(actorContexto.exigirRol(anySet())).thenReturn(tecnicoPre);
        when(filtros.yaPasoPorElegibilidad(ID_PROYECTO)).thenReturn(true);
    }

    @Test
    void abreLaGestionDelProyectoYDerivaSiSePuedeSolicitarOtraOt() {
        OpinionTecnica gestion = OpinionTecnica.builder().id(5L).proyecto(proyecto)
                .resultado(ResultadoOpinionTecnica.OBSERVADO).fechaEmision(LocalDateTime.now()).build();
        when(opiniones.findByIdAndProyectoId(5L, ID_PROYECTO)).thenReturn(Optional.of(gestion));

        OpinionTecnicaContexto contexto = acceso.gestionParaDgicp(ID_PROYECTO, 5L);

        assertThat(contexto.gestion()).isSameAs(gestion);
        assertThat(contexto.puedeSolicitarOt()).isTrue();
        assertThat(acceso.gestionParaConsulta(ID_PROYECTO, 5L).actor()).isSameAs(tecnicoPre);
        assertThat(acceso.proyectoParaConsulta(ID_PROYECTO).proyecto()).isSameAs(proyecto);
        assertThat(acceso.proyectoParaSolicitud(ID_PROYECTO).actor()).isSameAs(tecnicoPre);
    }

    @Test
    void rechazaProyectosYGestionesInexistentesOFueraDeLaUnidadEjecutora() {
        when(proyectos.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> acceso.proyectoParaConsulta(99L)).isInstanceOf(RecursoNoEncontradoException.class);

        when(opiniones.findByIdAndProyectoId(6L, ID_PROYECTO)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> acceso.gestionParaConsulta(ID_PROYECTO, 6L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        when(actorContexto.exigirRol(anySet())).thenReturn(Usuario.builder().id(3L).rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(UnidadEjecutora.builder().id(8L).build()).build());
        assertThatThrownBy(() -> acceso.proyectoParaSolicitud(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void cadaImpedimentoParaSolicitarTieneSuCodigo() {
        assertThat(acceso.impedimentoSolicitud(proyecto)).isEmpty();

        when(filtros.otPideElegibilidad(ID_PROYECTO)).thenReturn(true);
        assertThat(codigo()).isEqualTo(OpinionTecnicaAcceso.ESTADO_PROYECTO_NO_PERMITE_SOLICITAR_OT);
        when(filtros.otPideElegibilidad(ID_PROYECTO)).thenReturn(false);

        when(filtros.yaPasoPorElegibilidad(ID_PROYECTO)).thenReturn(false);
        assertThat(codigo()).isEqualTo(OpinionTecnicaAcceso.ESTADO_PROYECTO_NO_PERMITE_SOLICITAR_OT);
        when(filtros.yaPasoPorElegibilidad(ID_PROYECTO)).thenReturn(true);

        proyecto.setEstado(EstadoProyecto.OBSERVADO);
        assertThat(codigo()).isEqualTo(OpinionTecnicaAcceso.ESTADO_PROYECTO_NO_PERMITE_SOLICITAR_OT);
        proyecto.setEstado(EstadoProyecto.VIABLE);
        assertThat(acceso.impedimentoSolicitud(proyecto)).isEmpty();

        when(opiniones.findFirstByProyectoIdOrderByIdDesc(ID_PROYECTO))
                .thenReturn(Optional.of(OpinionTecnica.builder().id(5L).build()));
        assertThat(codigo()).isEqualTo(OpinionTecnicaAcceso.OPINION_TECNICA_EN_CURSO);

        when(opiniones.existsByProyectoIdAndTipoSolicitudAndResultadoIsNullAndFechaArchivoIsNull(ID_PROYECTO,
                TipoSolicitudOpinionTecnica.ACTUALIZACION_OT)).thenReturn(true);
        assertThat(codigo()).isEqualTo(OpinionTecnicaAcceso.ACTUALIZACION_OT_EN_CURSO);
    }

    private String codigo() {
        return acceso.impedimentoSolicitud(proyecto).map(ConflictoEstadoException::getCodigo).orElse(null);
    }
}
