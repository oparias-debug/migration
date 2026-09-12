package sv.gob.mh.siip.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.bandeja.api.PreinversinBandejaPreinversinApi;
import sv.gob.mh.siip.model.administracion.api.CatlogosBandejaPreinversinApi;
import sv.gob.mh.siip.model.administracion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.service.BandejaPreinversionService;

@RestController
public class BandejaPreinversionController implements PreinversinBandejaPreinversinApi, CatlogosBandejaPreinversinApi {
    private final BandejaPreinversionService service;
    public BandejaPreinversionController(BandejaPreinversionService service) { this.service = service; }

    @Override
    public ResponseEntity<SolicitudesActivasResponseDto> listarSolicitudesActivas(TipoSolicitudDto tipoSolicitud,
            Integer pagina, Integer tamanio) {
        return ResponseEntity.ok(service.activas(tipoSolicitud, pagina, tamanio));
    }

    @Override
    public ResponseEntity<SolicitudesArchivadasResponseDto> listarSolicitudesArchivadas(TipoSolicitudDto tipoSolicitud,
            Integer pagina, Integer tamanio) {
        return ResponseEntity.ok(service.archivadas(tipoSolicitud, pagina, tamanio));
    }

    @Override
    public ResponseEntity<SolicitudActivaItemDto> asignarTecnicoPre(Long idSolicitud, AsignacionTecnicoPreRequestDto request) {
        return ResponseEntity.ok(service.asignar(idSolicitud, request));
    }

    @Override
    public ResponseEntity<SolicitudArchivadaItemDto> archivarSolicitud(Long idSolicitud) {
        return ResponseEntity.ok(service.archivar(idSolicitud));
    }

    @Override
    public ResponseEntity<List<UsuarioResumenDto>> listarTecnicosPre() {
        return ResponseEntity.ok(service.tecnicos());
    }
}
