package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.programacion.api.PreinversinProgramacinFinancieraApi;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ConfigurarPeriodosProgramacionPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPreinversionService;

/** Adaptador HTTP de CU-PRE-22.1; delega la lógica al servicio. */
@RestController
public class ProgramacionFinancieraPreinversionController implements PreinversinProgramacinFinancieraApi {

    private final ProgramacionFinancieraPreinversionService service;

    public ProgramacionFinancieraPreinversionController(ProgramacionFinancieraPreinversionService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<ProgramacionFinancieraPreinversionDto> obtenerProgramacionFinancieraPreinversion(
            Long idProyecto) {
        return ResponseEntity.ok(service.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<ProgramacionFinancieraPreinversionDto> configurarPeriodosProgramacionPreinversion(
            Long idProyecto, ConfigurarPeriodosProgramacionPreinversionRequestDto request) {
        return ResponseEntity.ok(service.configurarPeriodos(idProyecto, request));
    }

    @Override
    public ResponseEntity<ProgramacionFinancieraPreinversionDto> guardarProgramacionFinancieraPreinversion(
            Long idProyecto, ProgramacionFinancieraPreinversionRequestDto request) {
        return ResponseEntity.ok(service.guardar(idProyecto, request));
    }
}
