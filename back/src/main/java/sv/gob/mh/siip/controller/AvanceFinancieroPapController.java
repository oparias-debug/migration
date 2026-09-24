package sv.gob.mh.siip.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinAvanceFinancieroPapApi;
import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceFinancieroPapService;

/** Expone el Avance Financiero Cuatrimestral del PAP (CU-PRE-32). */
@RestController
public class AvanceFinancieroPapController implements PreinversinAvanceFinancieroPapApi {

    private final AvanceFinancieroPapService service;

    public AvanceFinancieroPapController(AvanceFinancieroPapService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<AvanceFinancieroPAPResponseDto> obtenerAvanceFinancieroPAP(
            @Nullable Long idUnidadEjecutora, @Nullable Integer anio, @Nullable CuatrimestreDto periodo,
            Integer pagina, Integer tamanio) {
        return ResponseEntity.ok(service.listar(idUnidadEjecutora, anio, periodo, pagina, tamanio));
    }

    @Override
    public ResponseEntity<AvanceEstudioDto> obtenerAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodo) {
        return ResponseEntity.ok(service.obtenerAvanceEstudio(cup, anio, periodo));
    }

    @Override
    public ResponseEntity<AvanceEstudioDto> guardarAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodo,
            GuardarAvanceEstudioRequestDto guardarAvanceEstudioRequestDto) {
        return ResponseEntity.ok(service.guardarAvanceEstudio(cup, anio, periodo, guardarAvanceEstudioRequestDto));
    }

    @Override
    public ResponseEntity<Resource> generarReporteAvanceFinanciero(Long idUnidadEjecutora, Integer anio,
            CuatrimestreDto periodo, String formato) {
        Resource reporte = service.generarReporte(idUnidadEjecutora, anio, periodo, formato);
        MediaType tipoContenido = "PDF".equalsIgnoreCase(formato)
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return ResponseEntity.ok().contentType(tipoContenido).body(reporte);
    }
}
