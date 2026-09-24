package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinBancoDeProyectosApi;
import sv.gob.mh.siip.model.preinversion.dto.BancoProyectosResponseDto;
import sv.gob.mh.siip.model.preinversion.service.BancoProyectosService;

/** Expone la consulta del Banco de Proyectos (CU-PRE-29). */
@RestController
public class BancoProyectosController implements PreinversinBancoDeProyectosApi {

    private final BancoProyectosService bancoProyectosService;

    public BancoProyectosController(BancoProyectosService bancoProyectosService) {
        this.bancoProyectosService = bancoProyectosService;
    }

    @Override
        public ResponseEntity<BancoProyectosResponseDto> listarBancoProyectos(@Nullable Long idUnidadEjecutora,
            @Nullable String busqueda, Integer pagina, Integer tamanio) {
        return ResponseEntity.ok(bancoProyectosService.listar(idUnidadEjecutora, busqueda, pagina, tamanio));
    }
}
