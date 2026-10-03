package sv.gob.mh.siip.config.devseed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

class ProyectoDevSeederTest {

    private static final String CODIGO_SECTOR = "Desarrollo Social::Educación y cultura";
    private static final String CODIGO_EJE = "Infraestructura Educativa (Construcción y Mejoramiento)";
    private static final String NOMBRE_ASIGNADO = "Proyecto de prueba (Asignado a Técnico PRE)";

    private ProyectoRepository proyectoRepository;
    private SolicitudPreinversionRepository solicitudRepository;
    private InstitucionRepository institucionRepository;
    private UnidadEjecutoraRepository unidadEjecutoraRepository;
    private SectorActividadRepository sectorActividadRepository;
    private EjeTematicoRepository ejeTematicoRepository;
    private UsuarioRepository usuarioRepository;
    private ProyectoDevSeeder seeder;

    private final Institucion institucion = Institucion.builder().id(1L).codigo("MH-DGICP").build();
    private final Institucion institucion2 = Institucion.builder().id(5L).codigo("MINED").build();
    private final UnidadEjecutora unidadEjecutora = UnidadEjecutora.builder().id(2L).codigo("URP-01").build();
    private final UnidadEjecutora unidadEjecutora2 = UnidadEjecutora.builder().id(6L).codigo("URP-02").build();
    private final Usuario tecnicoPre = Usuario.builder().id(7L).nombreUsuario("tecnico.pre").build();

    @BeforeEach
    void setUp() {
        proyectoRepository = mock(ProyectoRepository.class);
        solicitudRepository = mock(SolicitudPreinversionRepository.class);
        institucionRepository = mock(InstitucionRepository.class);
        unidadEjecutoraRepository = mock(UnidadEjecutoraRepository.class);
        sectorActividadRepository = mock(SectorActividadRepository.class);
        ejeTematicoRepository = mock(EjeTematicoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        seeder = new ProyectoDevSeeder(proyectoRepository, solicitudRepository, institucionRepository,
                unidadEjecutoraRepository, sectorActividadRepository, ejeTematicoRepository, usuarioRepository);

        when(institucionRepository.findByCodigo("MH-DGICP")).thenReturn(Optional.of(institucion));
        when(institucionRepository.findByCodigo("MINED")).thenReturn(Optional.of(institucion2));
        when(unidadEjecutoraRepository.findByCodigo("URP-01")).thenReturn(Optional.of(unidadEjecutora));
        when(unidadEjecutoraRepository.findByCodigo("URP-02")).thenReturn(Optional.of(unidadEjecutora2));
        when(sectorActividadRepository.findByCodigo(CODIGO_SECTOR))
                .thenReturn(Optional.of(SectorActividad.builder().id(3L).codigo(CODIGO_SECTOR).build()));
        when(ejeTematicoRepository.findByCodigo(CODIGO_EJE))
                .thenReturn(Optional.of(EjeTematico.builder().id(4L).codigo(CODIGO_EJE).build()));
        when(usuarioRepository.findByNombreUsuario("tecnico.pre")).thenReturn(Optional.of(tecnicoPre));
        when(proyectoRepository.findByNombreContainingIgnoreCase(anyString())).thenReturn(List.of());
        when(proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()).thenReturn(Optional.empty());
        AtomicLong secuencia = new AtomicLong(100L);
        when(proyectoRepository.save(any())).thenAnswer(inv -> {
            Proyecto proyecto = inv.getArgument(0);
            proyecto.setId(secuencia.incrementAndGet());
            return proyecto;
        });
    }

    @Test
    void seed_creaLosCincoProyectosDePrueba_cuandoNoExistenAun() {
        seeder.seed();

        ArgumentCaptor<Proyecto> proyectos = ArgumentCaptor.forClass(Proyecto.class);
        verify(proyectoRepository, times(5)).save(proyectos.capture());
        assertThat(proyectos.getAllValues())
                .extracting(Proyecto::getNombre, Proyecto::getEstado)
                .containsExactly(
                        tuple("Proyecto de prueba (En Elaboración)",
                                EstadoProyecto.EN_REGISTRO),
                        tuple("Proyecto de prueba (Enviado DGICP Registro)",
                                EstadoProyecto.ENVIADO_DGICP_REGISTRO),
                        tuple(NOMBRE_ASIGNADO, EstadoProyecto.ENVIADO_DGICP_REGISTRO),
                        tuple("Proyecto de prueba (CUP Asignado)",
                                EstadoProyecto.CUP_ASIGNADO),
                        tuple("Proyecto de prueba (Otra Unidad Ejecutora)",
                                EstadoProyecto.EN_REGISTRO));

        Proyecto conCup = proyectos.getAllValues().get(3);
        assertThat(conCup.getCup()).isEqualTo("10000");
        assertThat(conCup.getFechaCupAsignado()).isNotNull();
        Proyecto otraUnidad = proyectos.getAllValues().get(4);
        assertThat(otraUnidad.getUnidadEjecutora()).isSameAs(unidadEjecutora2);
        assertThat(otraUnidad.getInstitucion()).isSameAs(institucion2);
        assertThat(proyectos.getAllValues().get(0).getUnidadEjecutora()).isSameAs(unidadEjecutora);
    }

    @Test
    void seed_registraLasSolicitudesDeCupSegunElEstadoDeCadaProyecto() {
        seeder.seed();

        ArgumentCaptor<SolicitudPreinversion> solicitudes = ArgumentCaptor.forClass(SolicitudPreinversion.class);
        verify(solicitudRepository, times(3)).save(solicitudes.capture());
        List<SolicitudPreinversion> guardadas = solicitudes.getAllValues();
        assertThat(guardadas).extracting(SolicitudPreinversion::getTipoSolicitud).containsOnly(TipoSolicitud.CUP);
        assertThat(guardadas).extracting(SolicitudPreinversion::getEstado)
                .containsExactly(EstadoSolicitud.REGISTRADA, EstadoSolicitud.REGISTRADA, EstadoSolicitud.APROBADA);

        SolicitudPreinversion sinAsignar = guardadas.get(0);
        assertThat(sinAsignar.getTecnicoAsignado()).isNull();
        assertThat(sinAsignar.getFechaAsignacion()).isNull();
        assertThat(sinAsignar.getProyecto().getId()).isEqualTo(102L);

        assertThat(guardadas.get(1).getTecnicoAsignado()).isSameAs(tecnicoPre);
        assertThat(guardadas.get(1).getFechaAsignacion()).isNotNull();
        assertThat(guardadas.get(2).getTecnicoAsignado()).isSameAs(tecnicoPre);
        assertThat(guardadas.get(2).getProyecto().getCup()).isEqualTo("10000");
    }

    @Test
    void seed_esIdempotente_cuandoLosProyectosYaExisten() {
        when(proyectoRepository.findByNombreContainingIgnoreCase(anyString()))
                .thenReturn(List.of(mock(Proyecto.class)));

        seeder.seed();

        verify(proyectoRepository, never()).save(any());
        verify(solicitudRepository, never()).save(any());
        verify(usuarioRepository, never()).findByNombreUsuario(anyString());
    }

    @Test
    void seed_fallaSinTocarNada_cuandoFaltaLaInstitucionPrincipal() {
        when(institucionRepository.findByCodigo("MH-DGICP")).thenReturn(Optional.empty());

        assertThatThrownBy(seeder::seed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MH-DGICP");
        verify(proyectoRepository, never()).save(any());
    }

    @Test
    void seed_falla_cuandoFaltaLaUnidadEjecutoraPrincipal() {
        when(unidadEjecutoraRepository.findByCodigo("URP-01")).thenReturn(Optional.empty());

        assertThatThrownBy(seeder::seed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("URP-01");
    }

    @Test
    void seed_falla_cuandoFaltaElSector() {
        when(sectorActividadRepository.findByCodigo(CODIGO_SECTOR)).thenReturn(Optional.empty());

        assertThatThrownBy(seeder::seed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Educación y cultura");
    }

    @Test
    void seed_falla_cuandoFaltaElEjeTematico() {
        when(ejeTematicoRepository.findByCodigo(CODIGO_EJE)).thenReturn(Optional.empty());

        assertThatThrownBy(seeder::seed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Infraestructura Educativa");
    }

    @Test
    void seed_falla_cuandoFaltaLaInstitucionDeLaSegundaUnidad() {
        when(institucionRepository.findByCodigo("MINED")).thenReturn(Optional.empty());

        assertThatThrownBy(seeder::seed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MINED");
        verify(proyectoRepository, times(4)).save(any());
    }

    @Test
    void seed_falla_cuandoFaltaLaSegundaUnidadEjecutora() {
        when(unidadEjecutoraRepository.findByCodigo("URP-02")).thenReturn(Optional.empty());

        assertThatThrownBy(seeder::seed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("URP-02");
    }

    @Test
    void seed_falla_cuandoFaltaElTecnicoPreParaElProyectoAsignado() {
        when(usuarioRepository.findByNombreUsuario("tecnico.pre")).thenReturn(Optional.empty());

        assertThatThrownBy(seeder::seed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("tecnico.pre");
        verify(proyectoRepository, times(2)).save(any());
    }

    @Test
    void seed_falla_cuandoFaltaElTecnicoPreParaElProyectoConCup() {
        when(usuarioRepository.findByNombreUsuario("tecnico.pre")).thenReturn(Optional.empty());
        when(proyectoRepository.findByNombreContainingIgnoreCase(NOMBRE_ASIGNADO))
                .thenReturn(List.of(mock(Proyecto.class)));

        assertThatThrownBy(seeder::seed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("tecnico.pre");
        verify(proyectoRepository, times(2)).save(any());
        verify(proyectoRepository, never()).findFirstByCupIsNotNullOrderByCupDesc();
    }
}
