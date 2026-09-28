package sv.gob.mh.siip.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinProgramacinFinancieraPapApi;
import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.HabilitarModificacionesFueraPlazoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapAjusteService;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;

/** Expone la Programación Financiera Cuatrimestral del PAP (CU-PRE-30). */
@RestController
public class ProgramacionFinancieraPapController implements PreinversinProgramacinFinancieraPapApi {

    private final ProgramacionFinancieraPapService service;
    private final ProgramacionFinancieraPapAjusteService ajusteService;

    public ProgramacionFinancieraPapController(ProgramacionFinancieraPapService service,
            ProgramacionFinancieraPapAjusteService ajusteService) {
        this.service = service;
        this.ajusteService = ajusteService;
    }

    @Override
    public ResponseEntity<ProgramacionFinancieraPAPResponseDto> obtenerProgramacionFinancieraPAP(
            @Nullable Long idUnidadEjecutora, @Nullable Integer anio, @Nullable String busqueda, Integer pagina,
            Integer tamanio) {
        return ResponseEntity.ok(service.listar(idUnidadEjecutora, anio, busqueda, pagina, tamanio));
    }

    @Override
    public ResponseEntity<EstudioProgramacionPAPDto> agregarEstudio(AgregarEstudioRequestDto agregarEstudioRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarEstudio(agregarEstudioRequestDto));
    }

    @Override
    public ResponseEntity<EstudioProgramacionPAPDto> obtenerProgramacionEstudio(String cup, Integer anio) {
        return ResponseEntity.ok(service.obtenerProgramacionEstudio(cup, anio));
    }

    @Override
    public ResponseEntity<EstudioProgramacionPAPDto> guardarProgramacionEstudio(String cup, Integer anio,
            GuardarProgramacionEstudioRequestDto guardarProgramacionEstudioRequestDto) {
        return ResponseEntity.ok(service.guardarProgramacionEstudio(cup, anio, guardarProgramacionEstudioRequestDto));
    }

    @Override
    public ResponseEntity<Void> desactivarEstudio(String cup, Integer anio) {
        ajusteService.desactivarEstudio(cup, anio);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> eliminarEtapaProgramacion(String cup, NombreEtapaDto etapa, Integer anio) {
        ajusteService.eliminarEtapaProgramacion(cup, etapa, anio);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> eliminarFuenteFinanciamiento(String cup, NombreEtapaDto etapa, Long idFuente,
            Integer anio) {
        ajusteService.eliminarFuenteFinanciamiento(cup, etapa, idFuente, anio);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> habilitarModificacionesFueraPlazo(
            HabilitarModificacionesFueraPlazoRequestDto habilitarModificacionesFueraPlazoRequestDto) {
        ajusteService.habilitarModificacionesFueraPlazo(habilitarModificacionesFueraPlazoRequestDto);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Resource> generarReporteProgramacionPAP(Long idUnidadEjecutora, Integer anio,
            String formato) {
        Resource reporte = service.generarReporte(idUnidadEjecutora, anio, formato);
        MediaType tipoContenido = "PDF".equalsIgnoreCase(formato)
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return ResponseEntity.ok().contentType(tipoContenido).body(reporte);
    }
}
