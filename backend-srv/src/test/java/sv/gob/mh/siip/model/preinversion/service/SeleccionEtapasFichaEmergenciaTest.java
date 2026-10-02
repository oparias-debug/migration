package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ComponenteCostoEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ComponenteCostoDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.FichaEmergenciaRepository;

class SeleccionEtapasFichaEmergenciaTest {

    private static final Long ID_PROYECTO = 40L;

    private final SeleccionEtapasProyectos proyectos = mock(SeleccionEtapasProyectos.class);
    private final FichaEmergenciaRepository fichaRepository = mock(FichaEmergenciaRepository.class);
    private final FichaEmergenciaEnsamblador ensamblador = mock(FichaEmergenciaEnsamblador.class);
    private final SeleccionEtapasFichaEmergencia fichaEmergencia =
            new SeleccionEtapasFichaEmergencia(proyectos, fichaRepository, ensamblador);

    private final Proyecto proyecto = proyectoEmergencia();

    private static Proyecto proyectoEmergencia() {
        Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO).estado(EstadoProyecto.CUP_ASIGNADO).build();
        proyecto.setEsProyectoEmergencia(true);
        return proyecto;
    }
    private final FichaEmergenciaDto dto = new FichaEmergenciaDto();

    @BeforeEach
    void setUp() {
        when(proyectos.buscarDeEmergencia(ID_PROYECTO)).thenReturn(proyecto);
        when(ensamblador.construir(any(), any())).thenReturn(dto);
    }

    @Test
    void obtener_sinFichaGuardada_ensamblaSoloConElProyecto() {
        when(fichaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThat(fichaEmergencia.obtener(ID_PROYECTO)).isSameAs(dto);
        verify(ensamblador).construir(proyecto, null);
    }

    @Test
    void obtener_conFichaGuardada_laEnsambla() {
        FichaEmergencia ficha = FichaEmergencia.builder().id(1L).proyecto(proyecto).build();
        when(fichaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(ficha));

        fichaEmergencia.obtener(ID_PROYECTO);

        verify(ensamblador).construir(proyecto, ficha);
    }

    @Test
    void registrar_camposObligatoriosVacios_lanzaValidacionConUnDetallePorCampo() {
        FichaEmergenciaRequestDto request = new FichaEmergenciaRequestDto().planteamientoProblema(" ")
                .distrito(null).poblacionObjetivo("");
        request.setProductos(List.of());

        assertThatThrownBy(() -> fichaEmergencia.registrar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, (ValidacionNegocioException ex) -> {
                    assertThat(ex.getMessage()).isEqualTo("Existen campos sin diligenciar");
                    assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo).containsExactly(
                            "planteamientoProblema", "productos", "distrito", "poblacionObjetivo");
                    assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getMensaje)
                            .containsOnly("*Campo obligatorio");
                });
        verify(fichaRepository, never()).save(any());
        verify(proyectos, never()).guardar(any());
    }

    @Test
    void registrar_sinProductos_lanzaValidacion() {
        FichaEmergenciaRequestDto request = requestCompleto();
        request.setProductos(List.of());

        assertThatThrownBy(() -> fichaEmergencia.registrar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, (ValidacionNegocioException ex) ->
                        assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo)
                                .containsExactly("productos"));
    }

    @ParameterizedTest
    @EnumSource(value = EstadoProyecto.class, names = {"EN_VIABILIDAD", "VIABLE", "EN_EJECUCION", "EN_REGISTRO"})
    void registrar_enEstadoNoRegistrable_lanzaConflictoSinGuardarNiCambiarElEstado(EstadoProyecto estado) {
        proyecto.setEstado(estado);

        assertThatThrownBy(() -> fichaEmergencia.registrar(ID_PROYECTO, requestCompleto()))
                .isInstanceOfSatisfying(ConflictoEstadoException.class, (ConflictoEstadoException ex) ->
                        assertThat(ex.getCodigo()).isEqualTo("FICHA_EMERGENCIA_NO_EDITABLE"));
        assertThat(proyecto.getEstado()).isEqualTo(estado);
        verify(fichaRepository, never()).save(any());
        verify(proyectos, never()).guardar(any());
    }

    @ParameterizedTest
    @EnumSource(value = EstadoProyecto.class, names = {"CUP_ASIGNADO", "EN_FORMULACION", "PROYECTO_FORMULADO",
        "OBSERVADO"})
    void registrar_enEstadoDeFormulacion_remiteAViabilidad(EstadoProyecto estado) {
        proyecto.setEstado(estado);
        when(fichaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        fichaEmergencia.registrar(ID_PROYECTO, requestCompleto());

        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.EN_VIABILIDAD);
        verify(proyectos).guardar(proyecto);
    }

    @Test
    void registrar_fichaNueva_copiaLosDatosYRemiteElProyectoAViabilidad() {
        when(fichaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        FichaEmergenciaDto resultado = fichaEmergencia.registrar(ID_PROYECTO, requestCompleto());

        assertThat(resultado).isSameAs(dto);
        ArgumentCaptor<FichaEmergencia> guardada = ArgumentCaptor.forClass(FichaEmergencia.class);
        verify(fichaRepository).save(guardada.capture());
        FichaEmergencia ficha = guardada.getValue();
        assertThat(ficha.getProyecto()).isSameAs(proyecto);
        assertThat(ficha.getPlanteamientoProblema()).isEqualTo("Inundación");
        assertThat(ficha.getObjetivoGeneral()).isEqualTo("Reconstruir");
        assertThat(ficha.getDescripcionProyecto()).isEqualTo("Descripción");
        assertThat(ficha.getProductos()).containsExactly("P-01");
        assertThat(ficha.getDistrito()).isEqualTo("San Salvador");
        assertThat(ficha.getLatitud()).isEqualTo(13.7d);
        assertThat(ficha.getLongitud()).isEqualTo(-89.2d);
        assertThat(ficha.getDireccionEspecifica()).isEqualTo("Calle 1");
        assertThat(ficha.getPoblacionObjetivo()).isEqualTo("Familias");
        assertThat(ficha.getInversionEstimada()).isEqualTo(1000d);
        assertThat(ficha.getArchivoPresupuestoUrl()).isEqualTo("presupuesto.pdf");
        assertThat(ficha.getComponentesCosto()).extracting(ComponenteCostoEmergencia::getTipoCosto,
                ComponenteCostoEmergencia::getCosto).containsExactly(tuple("Obra", 600d), tuple("Supervisión", 400d));
        assertThat(ficha.getCostosOperacion()).isEqualTo(10d);
        assertThat(ficha.getCostosMantenimiento()).isEqualTo(20d);
        assertThat(ficha.getFuentesFinanciamiento()).containsExactly(FuenteFinanciamiento.FONDO_GENERAL);
        assertThat(ficha.getFuenteRecursos()).isEqualTo("GOES");
        assertThat(ficha.getArchivoProgramacionUrl()).isEqualTo("programacion.pdf");
        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.EN_VIABILIDAD);
        verify(proyectos).guardar(proyecto);
        verify(ensamblador).construir(proyecto, ficha);
    }

    @Test
    void registrar_fichaExistente_laActualiza() {
        FichaEmergencia existente = FichaEmergencia.builder().id(5L).proyecto(proyecto).distrito("Anterior").build();
        when(fichaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(existente));

        fichaEmergencia.registrar(ID_PROYECTO, requestCompleto());

        assertThat(existente.getDistrito()).isEqualTo("San Salvador");
        verify(fichaRepository).save(existente);
    }

    @Test
    void registrar_fichaExistente_sinArchivosEnElRequest_conservaLosGuardados() {
        FichaEmergencia existente = FichaEmergencia.builder().id(5L).proyecto(proyecto)
                .archivoPresupuestoUrl("presupuesto-v1.xlsx").archivoProgramacionUrl("programacion-v1.pdf").build();
        when(fichaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(existente));
        FichaEmergenciaRequestDto request = requestCompleto().archivoPresupuestoUrl(null).archivoProgramacionUrl(null);

        fichaEmergencia.registrar(ID_PROYECTO, request);

        assertThat(existente.getArchivoPresupuestoUrl()).isEqualTo("presupuesto-v1.xlsx");
        assertThat(existente.getArchivoProgramacionUrl()).isEqualTo("programacion-v1.pdf");
    }

    @Test
    void registrar_totalDeComponentesDistintoDeLaInversion_lanzaValidacionSinGuardar() {
        FichaEmergenciaRequestDto request = requestCompleto().inversionEstimada(1200d);

        assertThatThrownBy(() -> fichaEmergencia.registrar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, (ValidacionNegocioException ex) -> {
                    assertThat(ex.getCodigo()).isEqualTo("TOTAL_COMPONENTES_DISTINTO_INVERSION");
                    assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo)
                            .containsExactly("componentesCosto");
                });
        verify(fichaRepository, never()).save(any());
        verify(proyectos, never()).guardar(any());
    }

    @Test
    void registrar_componentesSinInversionEstimada_lanzaValidacion() {
        FichaEmergenciaRequestDto request = requestCompleto().inversionEstimada(null);

        assertThatThrownBy(() -> fichaEmergencia.registrar(ID_PROYECTO, request))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void registrar_sinComponentes_noExigeTotal() {
        when(fichaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        FichaEmergenciaRequestDto request = requestCompleto().inversionEstimada(null).componentesCosto(List.of());

        fichaEmergencia.registrar(ID_PROYECTO, request);

        verify(fichaRepository).save(any());
    }

    private static FichaEmergenciaRequestDto requestCompleto() {
        return new FichaEmergenciaRequestDto()
                .planteamientoProblema("Inundación")
                .objetivoGeneral("Reconstruir")
                .descripcionProyecto("Descripción")
                .productos(List.of(new ProductoSeleccionadoDto().codigoProducto("P-01")))
                .distrito("San Salvador")
                .latitud(13.7d)
                .longitud(-89.2d)
                .direccionEspecifica("Calle 1")
                .poblacionObjetivo("Familias")
                .inversionEstimada(1000d)
                .archivoPresupuestoUrl("presupuesto.pdf")
                .componentesCosto(List.of(new ComponenteCostoDto().tipoCosto("Obra").costo(600d),
                        new ComponenteCostoDto().tipoCosto("Supervisión").costo(400d)))
                .costosOperacion(10d)
                .costosMantenimiento(20d)
                .fuentesFinanciamiento(List.of(FuenteFinanciamientoDto.FONDO_GENERAL))
                .fuenteRecursos("GOES")
                .archivoProgramacionUrl("programacion.pdf");
    }
}
