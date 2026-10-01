package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionRequestDto;

class ProgramacionFinancieraPreinversionValidacionesTest {

    private static final String SIN_FILAS = "Debe registrar la programación de al menos una etapa.";
    private static final String SIN_ETAPA = "La etapa es obligatoria.";
    private static final String PERIODOS = "La programación debe contener exactamente los períodos configurados.";

    private static final List<EtapaPreinversion> ETAPAS = List.of(
            EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build(),
            EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.DISENO).build());

    @ParameterizedTest
    @MethodSource("solicitudesInvalidas")
    void rechazaSolicitudesInvalidasConElMensajeDeLaRegla(ProgramacionFinancieraPreinversionRequestDto request,
            String mensaje) {
        assertThatThrownBy(() -> ProgramacionFinancieraPreinversionValidaciones.filasValidas(request, ETAPAS, 2))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessage(mensaje);
    }

    static Stream<Arguments> solicitudesInvalidas() {
        FilaProgramacionEtapaRequestDto perfil = fila(NombreEtapaDto.PERFIL, List.of(1D, 2D));
        return Stream.of(
                arguments(null, SIN_FILAS),
                arguments(new ProgramacionFinancieraPreinversionRequestDto().filas(null), SIN_FILAS),
                arguments(new ProgramacionFinancieraPreinversionRequestDto().filas(List.of()), SIN_FILAS),
                arguments(solicitud(Collections.singletonList(null)), SIN_ETAPA),
                arguments(solicitud(List.of(fila(null, List.of(1D, 2D)))), SIN_ETAPA),
                arguments(solicitud(List.of(fila(NombreEtapaDto.FACTIBILIDAD, List.of(1D, 2D)))),
                        "La etapa no pertenece a la ruta de preinversión del proyecto."),
                arguments(solicitud(List.of(perfil, perfil)), "No se puede repetir una etapa en la programación."),
                arguments(solicitud(List.of(fila(NombreEtapaDto.PERFIL, null))), PERIODOS),
                arguments(solicitud(List.of(fila(NombreEtapaDto.PERFIL, List.of(1D)))), PERIODOS),
                arguments(solicitud(List.of(fila(NombreEtapaDto.PERFIL, Arrays.asList(1D, null)))),
                        "Cada período debe contener un monto."),
                arguments(solicitud(List.of(fila(NombreEtapaDto.PERFIL, List.of(1D, Double.NaN)))),
                        "Cada período debe contener un monto finito."),
                arguments(solicitud(List.of(fila(NombreEtapaDto.PERFIL, List.of(Double.POSITIVE_INFINITY, 1D)))),
                        "Cada período debe contener un monto finito."),
                arguments(solicitud(List.of(fila(NombreEtapaDto.PERFIL, List.of(1D, -0.5D)))),
                        "Los montos programados no pueden ser negativos."),
                arguments(solicitud(List.of(fila(NombreEtapaDto.PERFIL, List.of(0D, 0D)))),
                        "La programación de la etapa debe contener al menos un monto mayor que cero."));
    }

    @Test
    void aceptaVariasEtapasDistintasConAlMenosUnMontoPositivoCadaUna() {
        List<FilaProgramacionEtapaRequestDto> filas = List.of(fila(NombreEtapaDto.PERFIL, List.of(0D, 5D)),
                fila(NombreEtapaDto.DISENO, List.of(3D, 0D)));

        assertThat(ProgramacionFinancieraPreinversionValidaciones.filasValidas(solicitud(filas), ETAPAS, 2))
                .isSameAs(filas);
    }

    @ParameterizedTest
    @MethodSource("periodosInvalidos")
    void rechazaPeriodosAusentesOMenoresQueUno(Integer periodos) {
        assertThatThrownBy(() -> ProgramacionFinancieraPreinversionValidaciones.periodosValidos(periodos))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessage("Períodos a programar es obligatorio y debe ser mayor que cero.");
    }

    static Stream<Integer> periodosInvalidos() {
        return Stream.of(null, 0, -3);
    }

    @Test
    void aceptaPeriodosMayoresQueCero() {
        assertThat(ProgramacionFinancieraPreinversionValidaciones.periodosValidos(3)).isEqualTo(3);
    }

    @ParameterizedTest
    @MethodSource("alcancesPermitidos")
    void permiteActorSinUnidadOConLaMismaUnidadDelProyecto(Usuario usuario, Proyecto proyecto) {
        assertThatCode(() -> ProgramacionFinancieraPreinversionValidaciones.exigirAlcanceUnidadEjecutora(usuario,
                proyecto)).doesNotThrowAnyException();
    }

    static Stream<Arguments> alcancesPermitidos() {
        return Stream.of(
                arguments(Usuario.builder().build(), Proyecto.builder().build()),
                arguments(Usuario.builder().unidadEjecutora(unidad(1L)).build(),
                        Proyecto.builder().unidadEjecutora(unidad(1L)).build()));
    }

    @ParameterizedTest
    @MethodSource("alcancesDenegados")
    void deniegaProyectoSinUnidadODeOtraUnidad(Usuario usuario, Proyecto proyecto) {
        assertThatThrownBy(() -> ProgramacionFinancieraPreinversionValidaciones.exigirAlcanceUnidadEjecutora(usuario,
                proyecto)).isInstanceOf(AccesoDenegadoException.class);
    }

    static Stream<Arguments> alcancesDenegados() {
        Usuario usuario = Usuario.builder().unidadEjecutora(unidad(1L)).build();
        return Stream.of(
                arguments(usuario, Proyecto.builder().build()),
                arguments(usuario, Proyecto.builder().unidadEjecutora(unidad(2L)).build()));
    }

    private static UnidadEjecutora unidad(Long id) {
        return UnidadEjecutora.builder().id(id).build();
    }

    private static FilaProgramacionEtapaRequestDto fila(NombreEtapaDto etapa, List<Double> montos) {
        return new FilaProgramacionEtapaRequestDto().etapa(etapa).programacionPorPeriodo(montos);
    }

    private static ProgramacionFinancieraPreinversionRequestDto solicitud(List<FilaProgramacionEtapaRequestDto> filas) {
        return new ProgramacionFinancieraPreinversionRequestDto().filas(filas);
    }
}
