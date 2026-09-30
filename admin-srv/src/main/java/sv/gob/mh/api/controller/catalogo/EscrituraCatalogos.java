package sv.gob.mh.api.controller.catalogo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.catalogo.CatalogCreateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogResponseDto;
import sv.gob.mh.api.dto.catalogo.InactivationRequestDto;
import sv.gob.mh.api.mapper.CatalogoApiMapper;
import sv.gob.mh.api.mapper.DescriptoresApiMapper;
import sv.gob.mh.application.command.catalogo.ActualizarCamposCatalogoCommand;
import sv.gob.mh.application.command.catalogo.ActualizarDescriptoresCatalogoCommand;
import sv.gob.mh.application.command.catalogo.EliminarCatalogoCommand;
import sv.gob.mh.application.command.catalogo.InactivarCatalogoCommand;
import sv.gob.mh.application.handler.catalogo.ActualizarCamposCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.ActualizarDescriptoresCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.CrearCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.EliminarCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.InactivarCatalogoHandler;

/**
 * Operaciones de escritura sobre catálogos de CU-ADM-01 (HU-ADM-01-01, 06, 07 y 08): traduce el
 * contrato a commands y arma las respuestas de {@link CatalogosAdministracionController}.
 */
@Component
public class EscrituraCatalogos {

    private final CrearCatalogoHandler crearCatalogo;
    private final ActualizarDescriptoresCatalogoHandler actualizarDescriptores;
    private final ActualizarCamposCatalogoHandler actualizarCampos;
    private final InactivarCatalogoHandler inactivarCatalogo;
    private final EliminarCatalogoHandler eliminarCatalogo;

    public EscrituraCatalogos(CrearCatalogoHandler crearCatalogo,
            ActualizarDescriptoresCatalogoHandler actualizarDescriptores,
            ActualizarCamposCatalogoHandler actualizarCampos,
            InactivarCatalogoHandler inactivarCatalogo,
            EliminarCatalogoHandler eliminarCatalogo) {
        this.crearCatalogo = crearCatalogo;
        this.actualizarDescriptores = actualizarDescriptores;
        this.actualizarCampos = actualizarCampos;
        this.inactivarCatalogo = inactivarCatalogo;
        this.eliminarCatalogo = eliminarCatalogo;
    }

    public ResponseEntity<CatalogResponseDto> crear(CatalogCreateRequestDto request) {
        CatalogResponseDto creado = CatalogoApiMapper.aCatalogResponse(
                crearCatalogo.handle(CatalogoApiMapper.aCommand(request)));
        return ResponseEntity.created(Ubicaciones.catalogo(creado.getCode())).body(creado);
    }

    public ResponseEntity<CatalogResponseDto> actualizarDescriptores(String code,
            CatalogDescriptorsUpdateRequestDto request) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogResponse(actualizarDescriptores.handle(
                new ActualizarDescriptoresCatalogoCommand(code, DescriptoresApiMapper.aCambioDescriptores(request)))));
    }

    /** Reglas 10 y E7: el handler siempre rechaza la eliminación con 405. */
    public ResponseEntity<Void> eliminar(String code) {
        eliminarCatalogo.handle(new EliminarCatalogoCommand(code));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    public ResponseEntity<CatalogResponseDto> actualizarCampos(String code, CatalogFieldsUpdateRequestDto request) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogResponse(actualizarCampos.handle(
                new ActualizarCamposCatalogoCommand(code, CatalogoApiMapper.aNuevosCampos(request.getFields())))));
    }

    public ResponseEntity<CatalogResponseDto> inactivar(String code, InactivationRequestDto request) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogResponse(inactivarCatalogo.handle(
                new InactivarCatalogoCommand(code, CatalogoApiMapper.fechaHasta(request)))));
    }
}
