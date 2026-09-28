package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;

class ProyectoReglasTest {

    private static final UnidadEjecutora UNIDAD = UnidadEjecutora.builder().id(10L).build();

    @Test
    void nuevoEnRegistro_adscribeElProyectoALaUnidadDelActor() {
        Institucion institucion = Institucion.builder().id(1L).build();
        Usuario actor = Usuario.builder().id(1L).unidadEjecutora(UNIDAD).institucion(institucion).build();

        Proyecto entidad = ProyectoReglas.nuevoEnRegistro(actor);

        assertThat(entidad.getUnidadEjecutora()).isSameAs(UNIDAD);
        assertThat(entidad.getInstitucion()).isSameAs(institucion);
        assertThat(entidad.getEstado()).isEqualTo(EstadoProyecto.EN_REGISTRO);
        assertThat(entidad.getFechaIngreso()).isNotNull();
        assertThat(entidad.getActivo()).isTrue();
    }

    @Test
    void exigirEstadoEditable_aceptaEnRegistroYObservado_yRechazaElResto() {
        Proyecto enRegistro = conEstado(EstadoProyecto.EN_REGISTRO);
        Proyecto observado = conEstado(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
        Proyecto enviado = conEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);

        assertThatCode(() -> ProyectoReglas.exigirEstadoEditable(enRegistro)).doesNotThrowAnyException();
        assertThatCode(() -> ProyectoReglas.exigirEstadoEditable(observado)).doesNotThrowAnyException();
        assertThatThrownBy(() -> ProyectoReglas.exigirEstadoEditable(enviado))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessageContaining("ENVIADO_DGICP_REGISTRO");
    }

    @Test
    void validarReglaEmergencia_noExigeNada_cuandoNoEsEmergencia() {
        Proyecto sinIndicador = Proyecto.builder().build();
        Proyecto noEmergencia = Proyecto.builder().build();
        noEmergencia.setEsProyectoEmergencia(false);

        assertThatCode(() -> ProyectoReglas.validarReglaEmergencia(sinIndicador)).doesNotThrowAnyException();
        assertThatCode(() -> ProyectoReglas.validarReglaEmergencia(noEmergencia)).doesNotThrowAnyException();
    }

    @Test
    void validarReglaEmergencia_exigeTipoEventoYDecreto_cuandoEsEmergencia() {
        Proyecto entidad = Proyecto.builder().build();
        entidad.setEsProyectoEmergencia(true);
        entidad.setTipoEvento(" ");

        assertThatThrownBy(() -> ProyectoReglas.validarReglaEmergencia(entidad))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                        .extracting(ErrorDetalleDto::getCampo)
                        .containsExactly("tipoEvento", "numeroDecretoLegislativo"));
    }

    @Test
    void validarReglaEmergencia_aceptaEmergenciaCompleta() {
        Proyecto entidad = Proyecto.builder().build();
        entidad.setEsProyectoEmergencia(true);
        entidad.setTipoEvento("Terremoto");
        entidad.setNumeroDecretoLegislativo("DL-1");

        assertThatCode(() -> ProyectoReglas.validarReglaEmergencia(entidad)).doesNotThrowAnyException();
    }

    @Test
    void exigirRespuestaObservacionHabilitada_rechazaProyectoNoObservado() {
        Proyecto entidad = conEstado(EstadoProyecto.EN_REGISTRO);
        RespuestaObservacionRequestDto request = new RespuestaObservacionRequestDto().respuesta("Respuesta");

        assertThatThrownBy(() -> ProyectoReglas.exigirRespuestaObservacionHabilitada(entidad, request))
                .isInstanceOf(ConflictoEstadoException.class);
    }

    @Test
    void exigirRespuestaObservacionHabilitada_rechazaRespuestaEnBlanco() {
        Proyecto entidad = conEstado(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
        RespuestaObservacionRequestDto request = new RespuestaObservacionRequestDto().respuesta("  ");

        assertThatThrownBy(() -> ProyectoReglas.exigirRespuestaObservacionHabilitada(entidad, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                        .extracting(ErrorDetalleDto::getCampo)
                        .containsExactly("respuesta"));
    }

    @Test
    void exigirRespuestaObservacionHabilitada_aceptaRespuestaConProyectoObservado() {
        Proyecto entidad = conEstado(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
        RespuestaObservacionRequestDto request = new RespuestaObservacionRequestDto().respuesta("Respuesta");

        assertThatCode(() -> ProyectoReglas.exigirRespuestaObservacionHabilitada(entidad, request))
                .doesNotThrowAnyException();
    }

    @Test
    void exigirAlcanceUnidadEjecutora_soloAcotaAlActorConUnidadEjecutora() {
        Proyecto entidad = Proyecto.builder().unidadEjecutora(UNIDAD).build();
        Usuario sinUnidad = Usuario.builder().id(1L).build();
        Usuario mismaUnidad = Usuario.builder().id(2L).unidadEjecutora(UNIDAD).build();
        Usuario otraUnidad = Usuario.builder().id(3L).unidadEjecutora(UnidadEjecutora.builder().id(99L).build())
                .build();

        assertThatCode(() -> ProyectoReglas.exigirAlcanceUnidadEjecutora(sinUnidad, entidad))
                .doesNotThrowAnyException();
        assertThatCode(() -> ProyectoReglas.exigirAlcanceUnidadEjecutora(mismaUnidad, entidad))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> ProyectoReglas.exigirAlcanceUnidadEjecutora(otraUnidad, entidad))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    private static Proyecto conEstado(EstadoProyecto estado) {
        return Proyecto.builder().estado(estado).build();
    }
}
