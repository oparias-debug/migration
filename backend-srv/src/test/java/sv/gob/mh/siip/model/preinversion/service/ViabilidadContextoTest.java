package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.AccionesDisponiblesViabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;

/** Pruebas unitarias de {@link ViabilidadContexto} (CU-PRE-24, Anexo A.1). */
class ViabilidadContextoTest {

    private static final Usuario TECNICO = Usuario.builder().id(1L).rol(RolUsuario.TECNICO_URP).build();
    private static final Usuario VIABILIZADOR = Usuario.builder().id(2L).rol(RolUsuario.VIABILIZADOR).build();
    private static final Usuario OTRO_ROL = Usuario.builder().id(3L).rol(RolUsuario.TECNICO_PRE).build();

    private static RevisionViabilidad revision(EstadoRevisionViabilidad estado, String observaciones,
            Boolean habilitaElegibilidad) {
        return RevisionViabilidad.builder().estado(estado).observacionesGenerales(observaciones)
                .habilitaElegibilidad(habilitaElegibilidad).build();
    }

    private static ViabilidadContexto contexto(Usuario actor, RevisionViabilidad ultima, boolean deshabilitada,
            boolean documento) {
        return new ViabilidadContexto(actor, Proyecto.builder().id(9L).build(), ultima, deshabilitada, documento);
    }

    @Test
    void sinRevisionesNoHayObservacionesNiElegibilidad() {
        ViabilidadContexto contexto = contexto(TECNICO, null, false, false);

        assertThat(contexto.enCurso()).isFalse();
        assertThat(contexto.observacionesGenerales()).isNull();
        assertThat(contexto.habilitoElegibilidad()).isFalse();
    }

    @Test
    void elTecnicoSoloSolicitaConDocumentoYSinRevisionEnCursoNiEmision() {
        assertThat(contexto(TECNICO, null, false, true).acciones().getSolicitarViabilidad()).isTrue();
        assertThat(contexto(TECNICO, null, false, false).acciones().getSolicitarViabilidad()).isFalse();
        assertThat(contexto(TECNICO, revision(EstadoRevisionViabilidad.EN_CURSO, null, null), false, true)
                .acciones().getSolicitarViabilidad()).isFalse();
        assertThat(contexto(TECNICO, revision(EstadoRevisionViabilidad.EMITIDA, "Ok", true), true, true)
                .acciones().getSolicitarViabilidad()).isFalse();
    }

    @Test
    void elViabilizadorRevisaLaSolicitudEnCursoYEmiteSoloConJustificacion() {
        AccionesDisponiblesViabilidadDto sinJustificacion = contexto(VIABILIZADOR,
                revision(EstadoRevisionViabilidad.EN_CURSO, "  ", null), false, true).acciones();
        AccionesDisponiblesViabilidadDto conJustificacion = contexto(VIABILIZADOR,
                revision(EstadoRevisionViabilidad.EN_CURSO, "Cumple", null), false, true).acciones();

        assertThat(sinJustificacion.getSolicitarViabilidad()).isFalse();
        assertThat(sinJustificacion.getGuardarComentarios()).isTrue();
        assertThat(sinJustificacion.getEnviarComentarios()).isTrue();
        assertThat(sinJustificacion.getEmitirViabilidad()).isFalse();
        assertThat(conJustificacion.getEmitirViabilidad()).isTrue();
    }

    @Test
    void irAElegibilidadRequiereViabilizadorEmisionVigenteYHabilitacion() {
        RevisionViabilidad habilitada = revision(EstadoRevisionViabilidad.EMITIDA, "Ok", true);
        RevisionViabilidad noHabilitada = revision(EstadoRevisionViabilidad.EMITIDA, "Ok", false);

        assertThat(contexto(VIABILIZADOR, habilitada, true, true).acciones().getIrAElegibilidad()).isTrue();
        assertThat(contexto(VIABILIZADOR, noHabilitada, true, true).acciones().getIrAElegibilidad()).isFalse();
        assertThat(contexto(VIABILIZADOR, habilitada, false, true).acciones().getIrAElegibilidad()).isFalse();
        assertThat(contexto(TECNICO, habilitada, true, true).acciones().getIrAElegibilidad()).isFalse();
    }

    @Test
    void otroRolNoTieneAccionesDisponibles() {
        AccionesDisponiblesViabilidadDto acciones = contexto(OTRO_ROL,
                revision(EstadoRevisionViabilidad.EN_CURSO, "Cumple", true), false, true).acciones();

        assertThat(acciones.getSolicitarViabilidad()).isFalse();
        assertThat(acciones.getGuardarComentarios()).isFalse();
        assertThat(acciones.getIrAElegibilidad()).isFalse();
    }

    @Test
    void exigirHabilitadaParaSolicitudRechazaLaFichaEmitidaYLaSolicitudEnCurso() {
        ViabilidadContexto emitida = contexto(TECNICO, revision(EstadoRevisionViabilidad.EMITIDA, "Ok", true), true,
                true);
        ViabilidadContexto enCurso = contexto(TECNICO, revision(EstadoRevisionViabilidad.EN_CURSO, null, null),
                false, true);
        ViabilidadContexto devuelta = contexto(TECNICO, revision(EstadoRevisionViabilidad.DEVUELTA, null, null),
                false, true);

        assertThatThrownBy(emitida::exigirHabilitadaParaSolicitud)
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(ViabilidadContexto.FICHA_VIABILIDAD_DESHABILITADA);
        assertThatThrownBy(enCurso::exigirHabilitadaParaSolicitud)
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(ViabilidadContexto.SOLICITUD_VIABILIDAD_EN_CURSO);
        assertThatCode(devuelta::exigirHabilitadaParaSolicitud).doesNotThrowAnyException();
    }

    @Test
    void exigirRevisionEnCursoDevuelveLaUltimaORechaza() {
        RevisionViabilidad enCurso = revision(EstadoRevisionViabilidad.EN_CURSO, null, null);
        ViabilidadContexto emitida = contexto(VIABILIZADOR, revision(EstadoRevisionViabilidad.EMITIDA, "Ok", true),
                true, true);
        ViabilidadContexto sinRevision = contexto(VIABILIZADOR, null, false, true);

        assertThat(contexto(VIABILIZADOR, enCurso, false, true).exigirRevisionEnCurso()).isSameAs(enCurso);
        assertThatThrownBy(emitida::exigirRevisionEnCurso)
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(ViabilidadContexto.FICHA_VIABILIDAD_DESHABILITADA);
        assertThatThrownBy(sinRevision::exigirRevisionEnCurso)
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(ViabilidadContexto.SOLICITUD_VIABILIDAD_NO_VIGENTE);
    }

    @Test
    void esVacioConsideraNuloYEspacios() {
        assertThat(ViabilidadContexto.esVacio(null)).isTrue();
        assertThat(ViabilidadContexto.esVacio("   ")).isTrue();
        assertThat(ViabilidadContexto.esVacio("x")).isFalse();
    }
}
