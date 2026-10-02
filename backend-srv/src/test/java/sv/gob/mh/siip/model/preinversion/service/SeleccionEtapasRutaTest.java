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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RutaPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.ComplejidadProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.TamanioProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCapitalDto;
import sv.gob.mh.siip.model.preinversion.enums.ComplejidadProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TamanioProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoCapital;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.RutaPreinversionRepository;

class SeleccionEtapasRutaTest {

    private static final Long ID_PROYECTO = 20L;

    private final RutaPreinversionRepository rutaRepository = mock(RutaPreinversionRepository.class);
    private final SeleccionEtapasProyectos proyectos = mock(SeleccionEtapasProyectos.class);
    private final SeleccionEtapasRegistro registro = mock(SeleccionEtapasRegistro.class);
    private final SeleccionEtapasRuta ruta = new SeleccionEtapasRuta(rutaRepository, proyectos, registro);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .iniciativaInversion(IniciativaInversion.PROYECTO).build();

    @BeforeEach
    void setUp() {
        when(proyectos.buscar(ID_PROYECTO)).thenReturn(proyecto);
        when(rutaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        when(registro.enOrdenDeRuta(ID_PROYECTO)).thenReturn(List.of());
    }

    @Test
    void obtener_sinRuta_devuelveEtapasAceptadasSinModificacionNiCriterios() {
        when(registro.enOrdenDeRuta(ID_PROYECTO)).thenReturn(List.of(
                EtapaPreinversion.builder().id(1L).tipoEtapa(TipoEtapaPreinversion.PERFIL).build(),
                EtapaPreinversion.builder().id(2L).tipoEtapa(TipoEtapaPreinversion.EJECUCION).build()));

        RutaPreinversionDto dto = ruta.obtener(ID_PROYECTO);

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getEtapasAceptadas()).containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.EJECUCION);
        assertThat(dto.getFueModificada()).isFalse();
        assertThat(dto.getCriterios()).isNull();
        assertThat(dto.getJustificacionUltimaModificacion()).isNull();
    }

    @Test
    void obtener_rutaConCriteriosCompletos_devuelveCriteriosYJustificacion() {
        RutaPreinversion guardada = RutaPreinversion.builder().proyecto(proyecto).fueModificada(true)
                .justificacionUltimaModificacion("Ajuste").tipoCapital(TipoCapital.CAPITAL_FISICO)
                .tamanioProyecto(TamanioProyecto.GRANDE).complejidad(ComplejidadProyecto.ALTA).build();
        when(rutaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(guardada));

        RutaPreinversionDto dto = ruta.obtener(ID_PROYECTO);

        assertThat(dto.getFueModificada()).isTrue();
        assertThat(dto.getJustificacionUltimaModificacion()).isEqualTo("Ajuste");
        assertThat(dto.getCriterios().getTipoCapital()).isEqualTo(TipoCapitalDto.CAPITAL_FISICO);
        assertThat(dto.getCriterios().getTamanioProyecto()).isEqualTo(TamanioProyectoDto.GRANDE);
        assertThat(dto.getCriterios().getComplejidad()).isEqualTo(ComplejidadProyectoDto.ALTA);
    }

    @Test
    void obtener_rutaConCriteriosIncompletos_noDevuelveCriterios() {
        RutaPreinversion sinTamanio = RutaPreinversion.builder().tipoCapital(TipoCapital.CAPITAL_FISICO)
                .complejidad(ComplejidadProyecto.ALTA).build();
        RutaPreinversion sinComplejidad = RutaPreinversion.builder().tipoCapital(TipoCapital.CAPITAL_FISICO)
                .tamanioProyecto(TamanioProyecto.GRANDE).build();
        RutaPreinversion sinTipoCapital = RutaPreinversion.builder().tamanioProyecto(TamanioProyecto.GRANDE)
                .complejidad(ComplejidadProyecto.ALTA).build();
        when(rutaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(sinTamanio),
                Optional.of(sinComplejidad), Optional.of(sinTipoCapital));

        assertThat(ruta.obtener(ID_PROYECTO).getCriterios()).isNull();
        assertThat(ruta.obtener(ID_PROYECTO).getCriterios()).isNull();
        assertThat(ruta.obtener(ID_PROYECTO).getCriterios()).isNull();
    }

    @Test
    void generar_iniciativaNoProyecto_lanzaConflicto() {
        proyecto.setIniciativaInversion(IniciativaInversion.PROGRAMA);
        CriteriosCalificacionDto criterios = criterios(TipoCapitalDto.CAPITAL_FISICO, TamanioProyectoDto.GRANDE,
                ComplejidadProyectoDto.ALTA);

        assertThatThrownBy(() -> ruta.generar(ID_PROYECTO, criterios)).isInstanceOf(ConflictoEstadoException.class);
    }

    @Test
    void generar_capitalNoFisico_sugierePerfilYEjecucion() {
        RutaPreinversionSugeridaDto dto = ruta.generar(ID_PROYECTO,
                criterios(TipoCapitalDto.CAPITAL_HUMANO, TamanioProyectoDto.GRANDE, ComplejidadProyectoDto.ALTA));

        assertThat(dto.getEtapasSugeridas()).containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.EJECUCION);
    }

    @Test
    void generar_capitalFisicoPequenio_sugiereRutaConDiseno() {
        RutaPreinversionSugeridaDto dto = ruta.generar(ID_PROYECTO,
                criterios(TipoCapitalDto.CAPITAL_FISICO, TamanioProyectoDto.PEQUENIO, ComplejidadProyectoDto.ALTA));

        assertThat(dto.getEtapasSugeridas())
                .containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.DISENO, NombreEtapaDto.EJECUCION);
    }

    @Test
    void generar_capitalFisicoMedianoBaja_sugiereRutaConDiseno() {
        CriteriosCalificacionDto criterios = criterios(TipoCapitalDto.CAPITAL_FISICO, TamanioProyectoDto.MEDIANO,
                ComplejidadProyectoDto.BAJA);

        RutaPreinversionSugeridaDto dto = ruta.generar(ID_PROYECTO, criterios);

        assertThat(dto.getCriterios()).isSameAs(criterios);
        assertThat(dto.getEtapasSugeridas())
                .containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.DISENO, NombreEtapaDto.EJECUCION);
    }

    @Test
    void generar_capitalFisicoMedianoAlta_sugiereRutaCompleta() {
        RutaPreinversionSugeridaDto dto = ruta.generar(ID_PROYECTO,
                criterios(TipoCapitalDto.CAPITAL_FISICO, TamanioProyectoDto.MEDIANO, ComplejidadProyectoDto.ALTA));

        assertThat(dto.getEtapasSugeridas()).containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.PREFACTIBILIDAD,
                NombreEtapaDto.FACTIBILIDAD, NombreEtapaDto.DISENO, NombreEtapaDto.EJECUCION);
    }

    @Test
    void aceptar_iniciativaProyecto_guardaCriteriosYSincronizaEtapasSugeridas() {
        RutaPreinversion existente = RutaPreinversion.builder().proyecto(proyecto).fueModificada(true)
                .justificacionUltimaModificacion("Anterior").build();
        when(rutaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(existente));

        ruta.aceptar(ID_PROYECTO,
                criterios(TipoCapitalDto.CAPITAL_FISICO, TamanioProyectoDto.GRANDE, ComplejidadProyectoDto.ALTA));

        verify(rutaRepository).save(existente);
        assertThat(existente.getTipoCapital()).isEqualTo(TipoCapital.CAPITAL_FISICO);
        assertThat(existente.getTamanioProyecto()).isEqualTo(TamanioProyecto.GRANDE);
        assertThat(existente.getComplejidad()).isEqualTo(ComplejidadProyecto.ALTA);
        assertThat(existente.getFueModificada()).isFalse();
        assertThat(existente.getJustificacionUltimaModificacion()).isNull();
        verify(registro).reemplazarSeleccion(proyecto, List.of(TipoEtapaPreinversion.PERFIL,
                TipoEtapaPreinversion.PREFACTIBILIDAD, TipoEtapaPreinversion.FACTIBILIDAD,
                TipoEtapaPreinversion.DISENO, TipoEtapaPreinversion.EJECUCION));
    }

    @Test
    void aceptar_iniciativaEstudioGeneral_creaRutaSinCriteriosConPerfilYEjecucion() {
        proyecto.setIniciativaInversion(IniciativaInversion.ESTUDIO_GENERAL);

        ruta.aceptar(ID_PROYECTO, new CriteriosCalificacionDto());

        ArgumentCaptor<RutaPreinversion> guardada = ArgumentCaptor.forClass(RutaPreinversion.class);
        verify(rutaRepository).save(guardada.capture());
        assertThat(guardada.getValue().getProyecto()).isSameAs(proyecto);
        assertThat(guardada.getValue().getTipoCapital()).isNull();
        verify(registro).reemplazarSeleccion(proyecto,
                List.of(TipoEtapaPreinversion.PERFIL, TipoEtapaPreinversion.EJECUCION));
    }

    @Test
    void modificar_marcaRutaModificadaYReemplazaLaSeleccion() {
        ModificarRutaPreinversionRequestDto request = new ModificarRutaPreinversionRequestDto()
                .justificacion("Cambio de alcance")
                .etapas(List.of(NombreEtapaDto.PERFIL, NombreEtapaDto.EJECUCION));

        ruta.modificar(ID_PROYECTO, request);

        ArgumentCaptor<RutaPreinversion> guardada = ArgumentCaptor.forClass(RutaPreinversion.class);
        verify(rutaRepository).save(guardada.capture());
        assertThat(guardada.getValue().getFueModificada()).isTrue();
        assertThat(guardada.getValue().getJustificacionUltimaModificacion()).isEqualTo("Cambio de alcance");
        verify(registro).reemplazarSeleccion(proyecto,
                List.of(TipoEtapaPreinversion.PERFIL, TipoEtapaPreinversion.EJECUCION));
    }

    @Test
    void generar_proyectoDeEmergencia_lanzaConflicto() {
        proyecto.setEsProyectoEmergencia(true);
        CriteriosCalificacionDto criterios =
                criterios(TipoCapitalDto.CAPITAL_FISICO, TamanioProyectoDto.GRANDE, ComplejidadProyectoDto.ALTA);

        assertThatThrownBy(() -> ruta.generar(ID_PROYECTO, criterios))
                .isInstanceOfSatisfying(ConflictoEstadoException.class, (ConflictoEstadoException ex) ->
                        assertThat(ex.getCodigo()).isEqualTo("PROYECTO_EMERGENCIA_SIN_RUTA"));
    }

    @Test
    void aceptar_proyectoDeEmergencia_lanzaConflictoSinGuardar() {
        proyecto.setEsProyectoEmergencia(true);
        CriteriosCalificacionDto criterios =
                criterios(TipoCapitalDto.CAPITAL_FISICO, TamanioProyectoDto.GRANDE, ComplejidadProyectoDto.ALTA);

        assertThatThrownBy(() -> ruta.aceptar(ID_PROYECTO, criterios))
                .isInstanceOfSatisfying(ConflictoEstadoException.class, (ConflictoEstadoException ex) ->
                        assertThat(ex.getCodigo()).isEqualTo("PROYECTO_EMERGENCIA_SIN_RUTA"));
        verify(rutaRepository, never()).save(any());
        verify(registro, never()).reemplazarSeleccion(any(), any());
    }

    @Test
    void modificar_proyectoDeEmergencia_lanzaConflictoSinGuardar() {
        proyecto.setEsProyectoEmergencia(true);
        ModificarRutaPreinversionRequestDto request = modificacion("Cambio",
                NombreEtapaDto.PERFIL, NombreEtapaDto.EJECUCION);

        assertThatThrownBy(() -> ruta.modificar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ConflictoEstadoException.class, (ConflictoEstadoException ex) ->
                        assertThat(ex.getCodigo()).isEqualTo("PROYECTO_EMERGENCIA_SIN_RUTA"));
        verify(rutaRepository, never()).save(any());
        verify(registro, never()).reemplazarSeleccion(any(), any());
    }

    @Test
    void modificar_iniciativaPrograma_lanzaConflictoSinGuardar() {
        proyecto.setIniciativaInversion(IniciativaInversion.PROGRAMA);
        ModificarRutaPreinversionRequestDto request = modificacion("Agregar diseño",
                NombreEtapaDto.PERFIL, NombreEtapaDto.DISENO, NombreEtapaDto.EJECUCION);

        assertThatThrownBy(() -> ruta.modificar(ID_PROYECTO, request))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessageContaining("RN07/RN08");
        verify(rutaRepository, never()).save(any());
        verify(registro, never()).reemplazarSeleccion(any(), any());
    }

    @Test
    void modificar_justificacionEnBlanco_lanzaValidacionSinGuardar() {
        ModificarRutaPreinversionRequestDto request = modificacion("   ",
                NombreEtapaDto.PERFIL, NombreEtapaDto.EJECUCION);

        assertThatThrownBy(() -> ruta.modificar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, (ValidacionNegocioException ex) -> {
                    assertThat(ex.getCodigo()).isEqualTo("RUTA_MODIFICADA_INVALIDA");
                    assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo).containsExactly("justificacion");
                });
        verify(rutaRepository, never()).save(any());
        verify(registro, never()).reemplazarSeleccion(any(), any());
    }

    @Test
    void modificar_sinPerfilOEjecucion_lanzaValidacionSinGuardar() {
        ModificarRutaPreinversionRequestDto sinEjecucion = modificacion("Cambio",
                NombreEtapaDto.PERFIL, NombreEtapaDto.DISENO);
        ModificarRutaPreinversionRequestDto vacia = modificacion("Cambio");

        for (ModificarRutaPreinversionRequestDto request : List.of(sinEjecucion, vacia)) {
            assertThatThrownBy(() -> ruta.modificar(ID_PROYECTO, request))
                    .isInstanceOfSatisfying(ValidacionNegocioException.class, (ValidacionNegocioException ex) ->
                            assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo)
                                    .containsExactly("etapas"));
        }
        verify(rutaRepository, never()).save(any());
        verify(registro, never()).reemplazarSeleccion(any(), any());
    }

    private static ModificarRutaPreinversionRequestDto modificacion(String justificacion, NombreEtapaDto... etapas) {
        return new ModificarRutaPreinversionRequestDto().justificacion(justificacion).etapas(List.of(etapas));
    }

    private static CriteriosCalificacionDto criterios(TipoCapitalDto tipo, TamanioProyectoDto tamanio,
            ComplejidadProyectoDto complejidad) {
        return new CriteriosCalificacionDto().tipoCapital(tipo).tamanioProyecto(tamanio).complejidad(complejidad);
    }
}
