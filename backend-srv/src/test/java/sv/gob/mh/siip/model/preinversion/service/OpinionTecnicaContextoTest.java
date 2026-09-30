package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AccionesOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.CamposEditablesOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;

/** Pruebas unitarias de {@link OpinionTecnicaContexto} (CU-PRE-26, RN02–RN06, RN10, RN 12). */
class OpinionTecnicaContextoTest {

    private static OpinionTecnicaContexto contexto(RolUsuario rol, EstadoProyecto estado, OpinionTecnica gestion) {
        return new OpinionTecnicaContexto(Usuario.builder().id(1L).rol(rol).build(),
                Proyecto.builder().id(2L).estado(estado).build(), gestion, false);
    }

    private static OpinionTecnica enCurso() {
        return OpinionTecnica.builder().id(3L).build();
    }

    private static OpinionTecnica observada() {
        return OpinionTecnica.builder().id(3L).resultado(ResultadoOpinionTecnica.OBSERVADO)
                .fechaEmision(LocalDateTime.now()).build();
    }

    @Test
    void laDgicpRevisaUnaGestionEnCursoConElProyectoViableOElegible() {
        AccionesOpinionTecnicaDto acciones = contexto(RolUsuario.TECNICO_PRE, EstadoProyecto.VIABLE, enCurso())
                .acciones();

        assertThat(acciones.getEnviarComentarios().getHabilitada()).isTrue();
        assertThat(acciones.getGuardar().getHabilitada()).isTrue();
        assertThat(acciones.getOtFavorable().getVisible()).isTrue();
        assertThat(acciones.getOtFavorable().getHabilitada()).isFalse();
        assertThat(acciones.getVistoBuenoOt().getVisible()).isFalse();
        assertThat(acciones.getEnviarAjustes().getVisible()).isFalse();
        assertThat(acciones.getSolicitarOt().getVisible()).isFalse();

        // RN05: fuera de "Proyecto viable" / "Proyecto elegible" el botón no se activa.
        assertThat(contexto(RolUsuario.TECNICO_PRE, EstadoProyecto.EN_VIABILIDAD, enCurso()).acciones()
                .getEnviarComentarios().getHabilitada()).isFalse();
    }

    @Test
    void elVistoBuenoRequiereConclusionesYLuegoHabilitaLaOtFavorable() {
        OpinionTecnica gestion = enCurso();
        assertThat(contexto(RolUsuario.COORDINADOR_PRE, EstadoProyecto.ELEGIBLE, gestion).acciones()
                .getVistoBuenoOt().getHabilitada()).isFalse();

        gestion.getRevisionConclusiones().registrar("Cumple");
        assertThat(contexto(RolUsuario.COORDINADOR_PRE, EstadoProyecto.ELEGIBLE, gestion).acciones()
                .getVistoBuenoOt().getHabilitada()).isTrue();

        gestion.getRevisionConclusiones().darVistoBueno(null, LocalDateTime.now());
        assertThat(contexto(RolUsuario.COORDINADOR_PRE, EstadoProyecto.ELEGIBLE, gestion).acciones()
                .getVistoBuenoOt().getHabilitada()).isFalse();
        assertThat(contexto(RolUsuario.TECNICO_PRE, EstadoProyecto.ELEGIBLE, gestion).acciones()
                .getOtFavorable().getHabilitada()).isTrue();
    }

    @Test
    void elTecnicoUrpJustificaSoloConLaGestionObservadaYElProyectoObservado() {
        OpinionTecnicaContexto urp = contexto(RolUsuario.TECNICO_URP, EstadoProyecto.OBSERVADO, observada());

        assertThat(urp.camposEditables().getJustificacionInstitucion()).isTrue();
        assertThat(urp.camposEditables().getComentarioDgicp()).isFalse();
        assertThat(urp.acciones().getEnviarAjustes().getHabilitada()).isTrue();
        assertThat(contexto(RolUsuario.VIABILIZADOR, EstadoProyecto.OBSERVADO, observada()).camposEditables()
                .getJustificacionInstitucion()).isFalse();
        assertThat(contexto(RolUsuario.TECNICO_URP, EstadoProyecto.EN_VIABILIDAD, observada()).justifica()).isFalse();
    }

    @Test
    void laSeccionElegibilidadSoloSeEditaEnLaPrimeraGestion() {
        OpinionTecnica posterior = enCurso();
        posterior.setPrimeraGestion(false);

        CamposEditablesOpinionTecnicaDto campos = contexto(RolUsuario.TECNICO_PRE, EstadoProyecto.VIABLE, posterior)
                .camposEditables();

        assertThat(campos.getComentarioDgicp()).isTrue();
        assertThat(campos.getComentarioDgicpElegibilidad()).isFalse();
    }

    @Test
    void unaOtEmitidaNoAdmiteCambios() {
        OpinionTecnica favorable = OpinionTecnica.builder().id(3L).resultado(ResultadoOpinionTecnica.FAVORABLE)
                .fechaEmision(LocalDateTime.now()).build();
        OpinionTecnicaContexto pre = contexto(RolUsuario.TECNICO_PRE, EstadoProyecto.PROYECTO_CON_OT, favorable);

        assertThat(pre.acciones().getGuardar().getHabilitada()).isFalse();
        assertThatThrownBy(pre::exigirRevisable)
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaContexto.OPINION_TECNICA_YA_EMITIDA);
        assertThatThrownBy(contexto(RolUsuario.TECNICO_URP, EstadoProyecto.PROYECTO_CON_OT, favorable)
                ::exigirJustificable)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaContexto.OPINION_TECNICA_YA_EMITIDA);
    }

    @Test
    void cadaConflictoTieneSuCodigo() {
        OpinionTecnica archivada = observada();
        archivada.setFechaArchivo(LocalDateTime.now());

        assertThatThrownBy(contexto(RolUsuario.TECNICO_URP, EstadoProyecto.EN_FORMULACION, archivada)
                ::exigirJustificable)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaContexto.SOLICITUD_OT_ARCHIVADA);
        assertThatThrownBy(contexto(RolUsuario.TECNICO_PRE, EstadoProyecto.OBSERVADO, observada())::exigirRevisable)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaContexto.COMENTARIOS_DGICP_YA_ENVIADOS);
        assertThatThrownBy(contexto(RolUsuario.TECNICO_PRE, EstadoProyecto.EN_VIABILIDAD, enCurso())::exigirRevisable)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaContexto.ESTADO_PROYECTO_NO_PERMITE_ENVIO_COMENTARIOS);
        assertThatThrownBy(contexto(RolUsuario.TECNICO_URP, EstadoProyecto.ELEGIBLE, enCurso())::exigirJustificable)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(OpinionTecnicaContexto.ESTADO_PROYECTO_NO_OBSERVADO);
    }

    @Test
    void asignadaLaGestionSoloSuTecnicoPreLaRevisaYElCoordinadorConservaElAcceso() {
        OpinionTecnica gestion = enCurso();
        gestion.setTecnicoResponsable(Usuario.builder().id(9L).rol(RolUsuario.TECNICO_PRE).build());

        OpinionTecnicaContexto otroTecnico = contexto(RolUsuario.TECNICO_PRE, EstadoProyecto.VIABLE, gestion);
        assertThat(otroTecnico.esResponsable()).isFalse();
        assertThat(otroTecnico.revisa()).isFalse();
        assertThatThrownBy(otroTecnico::exigirRevisable).isInstanceOf(AccesoDenegadoException.class);

        OpinionTecnicaContexto asignado = new OpinionTecnicaContexto(gestion.getTecnicoResponsable(),
                Proyecto.builder().id(2L).estado(EstadoProyecto.VIABLE).build(), gestion, false);
        assertThat(asignado.revisa()).isTrue();
        assertThat(contexto(RolUsuario.COORDINADOR_PRE, EstadoProyecto.VIABLE, gestion).revisa()).isTrue();
    }

    @Test
    void elViabilizadorRespondeSoloLaElegibilidadDeLaPrimeraGestionAntesDeReemitirla() {
        OpinionTecnicaContexto observado = contexto(RolUsuario.VIABILIZADOR, EstadoProyecto.OBSERVADO, observada());
        assertThat(observado.justificaElegibilidad()).isTrue();
        assertThat(observado.justificaProyecto()).isFalse();
        CamposEditablesOpinionTecnicaDto campos = observado.camposEditables();
        assertThat(campos.getJustificacionInstitucion()).isFalse();
        assertThat(campos.getJustificacionInstitucionElegibilidad()).isTrue();
        assertThat(observado.acciones().getGuardar().getHabilitada()).isTrue();

        assertThat(contexto(RolUsuario.VIABILIZADOR, EstadoProyecto.EN_VIABILIDAD, observada())
                .justificaElegibilidad()).isTrue();
        assertThat(contexto(RolUsuario.VIABILIZADOR, EstadoProyecto.ELEGIBLE, observada())
                .justificaElegibilidad()).isFalse();
        OpinionTecnica posterior = observada();
        posterior.setPrimeraGestion(false);
        assertThat(contexto(RolUsuario.VIABILIZADOR, EstadoProyecto.OBSERVADO, posterior).justificaElegibilidad())
                .isFalse();
        assertThat(contexto(RolUsuario.VIABILIZADOR, EstadoProyecto.OBSERVADO, enCurso()).justificaElegibilidad())
                .isFalse();
        assertThat(contexto(RolUsuario.TECNICO_URP, EstadoProyecto.OBSERVADO, observada()).justificaElegibilidad())
                .isFalse();
    }
}
