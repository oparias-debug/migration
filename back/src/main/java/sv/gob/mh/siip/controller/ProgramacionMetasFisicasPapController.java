package sv.gob.mh.siip.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinProgramacinDeMetasFsicasPapApi;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;

/** Expone la Programación Cuatrimestral de Metas Físicas de la Preinversión (CU-PRE-31). */
@RestController
public class ProgramacionMetasFisicasPapController implements PreinversinProgramacinDeMetasFsicasPapApi {

    private final ProgramacionMetasFisicasPapService service;

    public ProgramacionMetasFisicasPapController(ProgramacionMetasFisicasPapService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<ProgramacionMetasFisicasPAPResponseDto> obtenerProgramacionMetasFisicasPAP(
            @Nullable Long idUnidadEjecutora, @Nullable Integer anio, Integer pagina, Integer tamanio) {
        return ResponseEntity.ok(service.listar(idUnidadEjecutora, anio, pagina, tamanio));
    }

    @Override
    public ResponseEntity<EstudioProgramacionMetasDto> obtenerProgramacionMetasEstudio(String cup, Integer anio) {
        return ResponseEntity.ok(service.obtenerProgramacionMetasEstudio(cup, anio));
    }

    @Override
    public ResponseEntity<EstudioProgramacionMetasDto> guardarProgramacionMetasEstudio(String cup, Integer anio,
            GuardarProgramacionMetasEstudioRequestDto guardarProgramacionMetasEstudioRequestDto) {
        return ResponseEntity
                .ok(service.guardarProgramacionMetasEstudio(cup, anio, guardarProgramacionMetasEstudioRequestDto));
    }

    @Override
    public ResponseEntity<RevisionProgramacionPAPDto> enviarProgramacionARevisionDgicp(
            EnviarProgramacionARevisionDgicpRequestDto enviarProgramacionARevisionDgicpRequestDto) {
        return ResponseEntity.ok(service.enviarProgramacionARevisionDgicp(enviarProgramacionARevisionDgicpRequestDto));
    }

    @Override
    public ResponseEntity<RevisionProgramacionPAPDto> registrarObservacionesDgicp(
            RegistrarObservacionesDgicpRequestDto registrarObservacionesDgicpRequestDto) {
        return ResponseEntity.ok(service.registrarObservacionesDgicp(registrarObservacionesDgicpRequestDto));
    }

    @Override
    public ResponseEntity<RevisionProgramacionPAPDto> enviarObservacionesDgicp(
            EnviarProgramacionARevisionDgicpRequestDto enviarProgramacionARevisionDgicpRequestDto) {
        return ResponseEntity.ok(service.enviarObservacionesDgicp(enviarProgramacionARevisionDgicpRequestDto));
    }

    @Override
    public ResponseEntity<RevisionProgramacionPAPDto> registrarRespuestaInstitucion(
            RegistrarRespuestaInstitucionRequestDto registrarRespuestaInstitucionRequestDto) {
        return ResponseEntity.ok(service.registrarRespuestaInstitucion(registrarRespuestaInstitucionRequestDto));
    }

    @Override
    public ResponseEntity<RevisionProgramacionPAPDto> enviarRespuestaInstitucion(
            EnviarProgramacionARevisionDgicpRequestDto enviarProgramacionARevisionDgicpRequestDto) {
        return ResponseEntity.ok(service.enviarRespuestaInstitucion(enviarProgramacionARevisionDgicpRequestDto));
    }

    @Override
    public ResponseEntity<RevisionProgramacionPAPDto> finalizarRevision(
            FinalizarRevisionRequestDto finalizarRevisionRequestDto) {
        return ResponseEntity.ok(service.finalizarRevision(finalizarRevisionRequestDto));
    }

    @Override
    public ResponseEntity<Resource> generarReporteMetasFisicas(Long idUnidadEjecutora, Integer anio, String formato) {
        Resource reporte = service.generarReporteMetasFisicas(idUnidadEjecutora, anio, formato);
        MediaType tipoContenido = "PDF".equalsIgnoreCase(formato)
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return ResponseEntity.ok().contentType(tipoContenido).body(reporte);
    }

    @Override
    public ResponseEntity<Void> habilitarModificacionesMetasFueraPlazo(
            EnviarProgramacionARevisionDgicpRequestDto enviarProgramacionARevisionDgicpRequestDto) {
        service.habilitarModificacionesMetasFueraPlazo(enviarProgramacionARevisionDgicpRequestDto);
        return ResponseEntity.ok().build();
    }
}
