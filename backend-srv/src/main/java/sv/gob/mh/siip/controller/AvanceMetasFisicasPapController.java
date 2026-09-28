package sv.gob.mh.siip.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinAvanceDeMetasFsicasPapApi;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapRevisionService;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;

/** Expone el Avance Cuatrimestral por Metas Físicas del PAP (CU-PRE-33). */
@RestController
public class AvanceMetasFisicasPapController implements PreinversinAvanceDeMetasFsicasPapApi {

    private final AvanceMetasFisicasPapService service;
    private final AvanceMetasFisicasPapRevisionService revisionService;

    public AvanceMetasFisicasPapController(AvanceMetasFisicasPapService service,
            AvanceMetasFisicasPapRevisionService revisionService) {
        this.service = service;
        this.revisionService = revisionService;
    }

    @Override
    public ResponseEntity<AvanceMetasFisicasPAPResponseDto> obtenerAvanceMetasFisicasPAP(
            @Nullable Long idUnidadEjecutora, @Nullable Integer anio, @Nullable CuatrimestreDto periodo,
            Integer pagina, Integer tamanio) {
        return ResponseEntity.ok(service.listar(idUnidadEjecutora, anio, periodo, pagina, tamanio));
    }

    @Override
    public ResponseEntity<AvanceMetasEstudioDto> obtenerAvanceMetasEstudio(String cup, Integer anio,
            CuatrimestreDto periodo) {
        return ResponseEntity.ok(service.obtenerAvanceMetasEstudio(cup, anio, periodo));
    }

    @Override
    public ResponseEntity<AvanceMetasEstudioDto> guardarAvanceMetasEstudio(String cup, Integer anio,
            CuatrimestreDto periodo, GuardarAvanceMetasEstudioRequestDto guardarAvanceMetasEstudioRequestDto) {
        return ResponseEntity.ok(
                service.guardarAvanceMetasEstudio(cup, anio, periodo, guardarAvanceMetasEstudioRequestDto));
    }

    @Override
    public ResponseEntity<RevisionAvancePAPDto> registrarObservacionesAvanceDgicp(
            RegistrarObservacionesAvanceDgicpRequestDto registrarObservacionesAvanceDgicpRequestDto) {
        return ResponseEntity.ok(
                revisionService.registrarObservacionesAvanceDgicp(registrarObservacionesAvanceDgicpRequestDto));
    }

    @Override
    public ResponseEntity<RevisionAvancePAPDto> enviarObservacionesAvanceDgicp(
            EnviarObservacionesAvanceDgicpRequestDto enviarObservacionesAvanceDgicpRequestDto) {
        return ResponseEntity.ok(
                revisionService.enviarObservacionesAvanceDgicp(enviarObservacionesAvanceDgicpRequestDto));
    }

    @Override
    public ResponseEntity<RevisionAvancePAPDto> registrarRespuestaInstitucionAvance(
            RegistrarRespuestaInstitucionAvanceRequestDto registrarRespuestaInstitucionAvanceRequestDto) {
        return ResponseEntity.ok(
                revisionService.registrarRespuestaInstitucionAvance(registrarRespuestaInstitucionAvanceRequestDto));
    }

    @Override
    public ResponseEntity<RevisionAvancePAPDto> enviarRespuestaInstitucionAvance(
            EnviarObservacionesAvanceDgicpRequestDto enviarObservacionesAvanceDgicpRequestDto) {
        return ResponseEntity.ok(
                revisionService.enviarRespuestaInstitucionAvance(enviarObservacionesAvanceDgicpRequestDto));
    }

    @Override
    public ResponseEntity<RevisionAvancePAPDto> finalizarRevisionAvance(
            FinalizarRevisionAvanceRequestDto finalizarRevisionAvanceRequestDto) {
        return ResponseEntity.ok(revisionService.finalizarRevisionAvance(finalizarRevisionAvanceRequestDto));
    }

    @Override
    public ResponseEntity<Resource> generarReporteAvanceMetas(Long idUnidadEjecutora, Integer anio,
            CuatrimestreDto periodo, String formato) {
        Resource reporte = service.generarReporteAvanceMetas(idUnidadEjecutora, anio, periodo, formato);
        MediaType tipoContenido = "PDF".equalsIgnoreCase(formato)
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return ResponseEntity.ok().contentType(tipoContenido).body(reporte);
    }
}
