package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionArboles.TipoArbol;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link IdentificacionServiceImpl} (CU-PRE-04). */
class IdentificacionServiceImplTest {

    private static final Long ID_PROYECTO = 1L;
    private static final Long ID_UNIDAD = 5L;

    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final IdentificacionRepository identificacionRepository = mock(IdentificacionRepository.class);
    private final IdentificacionArboles arboles = mock(IdentificacionArboles.class);
    private final IdentificacionServiceImpl service = new IdentificacionServiceImpl(identificacionRepository,
            new IdentificacionAcceso(actorContexto, proyectoRepository), arboles,
            new IdentificacionEnsamblador(mock(ProyectoMapper.class)));

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO).nombre("Puente")
            .unidadEjecutora(UnidadEjecutora.builder().id(ID_UNIDAD).build()).build();

    private static Usuario actor(RolUsuario rol, Long idUnidad) {
        UnidadEjecutora unidad = idUnidad == null ? null : UnidadEjecutora.builder().id(idUnidad).build();
        return Usuario.builder().rol(rol).unidadEjecutora(unidad).build();
    }

    @Test
    void obtener_actorDeOtraUnidad_lanzaAccesoDenegado() {
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_URP, 99L));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void obtener_sinGuardadoPrevioYActorNoTecnicoUrp_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_PRE, null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("La informacion de identificacion del proyecto 1 todavia no se ha guardado.");
    }

    @Test
    void obtener_sinGuardadoPrevioYTecnicoUrp_devuelveSoloDatosDelProyecto() {
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_URP, ID_UNIDAD));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        IdentificacionDto dto = service.obtener(ID_PROYECTO);

        assertThat(dto.getNombreProyecto()).isEqualTo("Puente");
        assertThat(dto.getAntecedentes()).isNull();
    }

    @Test
    void obtener_conGuardadoPrevio_devuelveLaIdentificacion() {
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_PRE, null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(identificacionRepository.findByProyectoId(ID_PROYECTO))
                .thenReturn(Optional.of(Identificacion.builder().antecedentes("A").build()));

        assertThat(service.obtener(ID_PROYECTO).getAntecedentes()).isEqualTo("A");
    }

    @Test
    void guardar_sinRegistroPrevio_creaRegistroConLoCapturado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actor(RolUsuario.TECNICO_URP, ID_UNIDAD));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        when(identificacionRepository.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
        IdentificacionRequestDto request = new IdentificacionRequestDto().antecedentes("A")
                .objetivosEspecificos(List.of("uno"));

        IdentificacionDto dto = service.guardar(ID_PROYECTO, request);

        assertThat(dto.getAntecedentes()).isEqualTo("A");
        assertThat(dto.getObjetivosEspecificos()).containsExactly("uno");
        assertThat(dto.getFechaUltimoGuardado()).isNotNull();
    }

    @Test
    void guardar_conRegistroPrevio_actualizaElMismoRegistro() {
        Identificacion existente = Identificacion.builder().id(3L).proyecto(proyecto).antecedentes("viejo").build();
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actor(RolUsuario.TECNICO_URP, null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(existente));
        when(identificacionRepository.save(existente)).thenReturn(existente);

        service.guardar(ID_PROYECTO, new IdentificacionRequestDto().antecedentes("nuevo"));

        assertThat(existente.getAntecedentes()).isEqualTo("nuevo");
    }

    @Test
    void guardar_formulacionBloqueada_lanzaConflictoSinGuardar() {
        proyecto.setEstado(EstadoProyecto.EN_VIABILIDAD);
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actor(RolUsuario.TECNICO_URP, null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        IdentificacionRequestDto request = new IdentificacionRequestDto();

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO, request))
                .isInstanceOf(ConflictoEstadoException.class);
        verify(identificacionRepository, never()).save(any());
    }

    @Test
    void arbolDeProblemas_delegaEnArboles() {
        MockMultipartFile pdf = new MockMultipartFile("archivo", "p.pdf", "application/pdf", new byte[] {1});
        ArchivoAdjuntoResumenDto resumen = new ArchivoAdjuntoResumenDto();
        ArchivoDescargado descargado = new ArchivoDescargado(new ByteArrayResource(new byte[] {1}), "p.pdf");
        when(arboles.cargar(ID_PROYECTO, pdf, TipoArbol.PROBLEMAS)).thenReturn(resumen);
        when(arboles.descargar(ID_PROYECTO, TipoArbol.PROBLEMAS)).thenReturn(descargado);

        assertThat(service.cargarArbolProblemas(ID_PROYECTO, pdf)).isSameAs(resumen);
        assertThat(service.descargarArbolProblemas(ID_PROYECTO)).isSameAs(descargado);
        service.eliminarArbolProblemas(ID_PROYECTO);

        verify(arboles).eliminar(ID_PROYECTO, TipoArbol.PROBLEMAS);
    }

    @Test
    void arbolDeObjetivos_delegaEnArboles() {
        MockMultipartFile pdf = new MockMultipartFile("archivo", "o.pdf", "application/pdf", new byte[] {1});
        ArchivoAdjuntoResumenDto resumen = new ArchivoAdjuntoResumenDto();
        ArchivoDescargado descargado = new ArchivoDescargado(new ByteArrayResource(new byte[] {1}), "o.pdf");
        when(arboles.cargar(ID_PROYECTO, pdf, TipoArbol.OBJETIVOS)).thenReturn(resumen);
        when(arboles.descargar(ID_PROYECTO, TipoArbol.OBJETIVOS)).thenReturn(descargado);

        assertThat(service.cargarArbolObjetivos(ID_PROYECTO, pdf)).isSameAs(resumen);
        assertThat(service.descargarArbolObjetivos(ID_PROYECTO)).isSameAs(descargado);
        service.eliminarArbolObjetivos(ID_PROYECTO);

        verify(arboles).eliminar(ID_PROYECTO, TipoArbol.OBJETIVOS);
    }
}
