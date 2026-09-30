package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link ViabilidadAcceso} (CU-PRE-24, RN01). */
class ViabilidadAccesoTest {

    private static final Long ID_PROYECTO = 7L;

    private ActorContexto actorContexto;
    private ProyectoRepository proyectos;
    private RevisionViabilidadRepository revisiones;
    private DocumentosViabilidad documentos;
    private FiltrosPosterioresViabilidad filtros;
    private ViabilidadAcceso acceso;

    private UnidadEjecutora unidad;
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        actorContexto = mock(ActorContexto.class);
        proyectos = mock(ProyectoRepository.class);
        revisiones = mock(RevisionViabilidadRepository.class);
        documentos = mock(DocumentosViabilidad.class);
        filtros = mock(FiltrosPosterioresViabilidad.class);
        acceso = new ViabilidadAcceso(actorContexto, proyectos, revisiones, documentos, filtros);

        unidad = UnidadEjecutora.builder().id(3L).build();
        proyecto = Proyecto.builder().id(ID_PROYECTO).unidadEjecutora(unidad).build();
        when(proyectos.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(revisiones.findFirstByProyectoIdOrderByNumeroDesc(ID_PROYECTO)).thenReturn(Optional.empty());
    }

    private static Usuario usuario(RolUsuario rol, UnidadEjecutora unidadEjecutora) {
        return Usuario.builder().id(10L).rol(rol).unidadEjecutora(unidadEjecutora).build();
    }

    private RevisionViabilidad emitida() {
        RevisionViabilidad revision = RevisionViabilidad.builder().estado(EstadoRevisionViabilidad.EMITIDA)
                .fechaCierre(LocalDateTime.now()).build();
        when(revisiones.findFirstByProyectoIdOrderByNumeroDesc(ID_PROYECTO)).thenReturn(Optional.of(revision));
        return revision;
    }

    @Test
    void cadaOperacionExigeElRolQueLeCorresponde() {
        Usuario tecnico = usuario(RolUsuario.TECNICO_URP, unidad);
        Usuario viabilizador = usuario(RolUsuario.VIABILIZADOR, null);
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.VIABILIZADOR)).thenReturn(viabilizador);
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnico);
        when(actorContexto.exigirRol(RolUsuario.VIABILIZADOR)).thenReturn(viabilizador);
        when(documentos.tieneDocumentoPreinversion(ID_PROYECTO)).thenReturn(true);

        ViabilidadContexto consulta = acceso.paraConsulta(ID_PROYECTO);

        assertThat(consulta.actor()).isSameAs(viabilizador);
        assertThat(consulta.proyecto()).isSameAs(proyecto);
        assertThat(consulta.ultima()).isNull();
        assertThat(consulta.deshabilitada()).isFalse();
        assertThat(consulta.documentoPreinversionCargado()).isTrue();
        assertThat(acceso.paraTecnicoUrp(ID_PROYECTO).actor()).isSameAs(tecnico);
        assertThat(acceso.paraViabilizador(ID_PROYECTO).actor()).isSameAs(viabilizador);
    }

    @Test
    void unaEmisionVigenteDeshabilitaLaFichaSalvoQueLaOtHayaDevueltoElProyecto() {
        when(actorContexto.exigirRol(RolUsuario.VIABILIZADOR)).thenReturn(usuario(RolUsuario.VIABILIZADOR, null));
        RevisionViabilidad revision = emitida();

        ViabilidadContexto vigente = acceso.paraViabilizador(ID_PROYECTO);
        when(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, revision.getFechaCierre())).thenReturn(true);
        ViabilidadContexto devuelta = acceso.actualizar(vigente);

        assertThat(vigente.deshabilitada()).isTrue();
        assertThat(devuelta.deshabilitada()).isFalse();
        assertThat(devuelta.ultima()).isSameAs(revision);
        assertThat(devuelta.actor()).isSameAs(vigente.actor());
    }

    @Test
    void proyectoInexistenteRespondeProyectoNoEncontrado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(usuario(RolUsuario.TECNICO_URP, unidad));
        when(proyectos.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> acceso.paraTecnicoUrp(ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining(String.valueOf(ID_PROYECTO))
                .extracting(e -> ((RecursoNoEncontradoException) e).getCodigo())
                .isEqualTo(ViabilidadAcceso.PROYECTO_NO_ENCONTRADO);
    }

    @Test
    void unActorDeOtraUnidadEjecutoraNoAccede() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(usuario(RolUsuario.TECNICO_URP, UnidadEjecutora.builder().id(99L).build()));

        assertThatThrownBy(() -> acceso.paraTecnicoUrp(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void unProyectoSinUnidadEjecutoraSoloEsAccesibleParaActoresSinUnidad() {
        proyecto.setUnidadEjecutora(null);
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(usuario(RolUsuario.TECNICO_URP, unidad));
        when(actorContexto.exigirRol(RolUsuario.VIABILIZADOR)).thenReturn(usuario(RolUsuario.VIABILIZADOR, null));

        assertThatThrownBy(() -> acceso.paraTecnicoUrp(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
        assertThat(acceso.paraViabilizador(ID_PROYECTO).proyecto()).isSameAs(proyecto);
    }
}
