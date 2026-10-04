package sv.gob.mh.api.controller.catalogo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.catalogo.CambioEstadoDto;
import sv.gob.mh.api.dto.catalogo.CampoDefinicionDto;
import sv.gob.mh.api.dto.catalogo.CatalogoCreacionDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDescriptoresDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDto;
import sv.gob.mh.api.mapper.CatalogoApiMapper;
import sv.gob.mh.application.command.catalogo.ActualizarCamposCatalogoCommand;
import sv.gob.mh.application.command.catalogo.ActualizarDescriptoresCatalogoCommand;
import sv.gob.mh.application.command.catalogo.CambiarEstadoCatalogoCommand;
import sv.gob.mh.application.command.catalogo.EliminarCatalogoCommand;
import sv.gob.mh.application.handler.catalogo.ActualizarCamposCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.ActualizarDescriptoresCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.CambiarEstadoCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.CrearCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.EliminarCatalogoHandler;

/**
 * Operaciones de escritura sobre catálogos de CU-ADM-01 (HU-ADM-01-01, 02 y 05 a 08): traduce el
 * contrato a commands y arma las respuestas de {@link CatalogosAdministracionController}.
 */
@Component
public class EscrituraCatalogos {

    private final CrearCatalogoHandler crearCatalogo;
    private final ActualizarDescriptoresCatalogoHandler actualizarDescriptores;
    private final ActualizarCamposCatalogoHandler actualizarCampos;
    private final CambiarEstadoCatalogoHandler cambiarEstado;
    private final EliminarCatalogoHandler eliminarCatalogo;

    public EscrituraCatalogos(CrearCatalogoHandler crearCatalogo,
            ActualizarDescriptoresCatalogoHandler actualizarDescriptores,
            ActualizarCamposCatalogoHandler actualizarCampos,
            CambiarEstadoCatalogoHandler cambiarEstado,
            EliminarCatalogoHandler eliminarCatalogo) {
        this.crearCatalogo = crearCatalogo;
        this.actualizarDescriptores = actualizarDescriptores;
        this.actualizarCampos = actualizarCampos;
        this.cambiarEstado = cambiarEstado;
        this.eliminarCatalogo = eliminarCatalogo;
    }

    public ResponseEntity<CatalogoDto> crear(CatalogoCreacionDto request) {
        CatalogoDto creado = CatalogoApiMapper.aCatalogo(crearCatalogo.handle(CatalogoApiMapper.aCommand(request)));
        return ResponseEntity.created(Ubicaciones.catalogo(creado.getCodigo())).body(creado);
    }

    public ResponseEntity<CatalogoDto> actualizarDescriptores(String codigo, CatalogoDescriptoresDto request) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogo(actualizarDescriptores.handle(
                new ActualizarDescriptoresCatalogoCommand(codigo, CatalogoApiMapper.aCambioDescriptores(request)))));
    }

    /** RN-13 (E-24): el handler siempre rechaza la eliminación con 405. */
    public ResponseEntity<Void> eliminar(String codigo) {
        eliminarCatalogo.handle(new EliminarCatalogoCommand(codigo));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    public ResponseEntity<CatalogoDto> actualizarCampos(String codigo, List<CampoDefinicionDto> campos) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogo(actualizarCampos.handle(
                new ActualizarCamposCatalogoCommand(codigo, CatalogoApiMapper.aNuevosCampos(campos)))));
    }

    public ResponseEntity<CatalogoDto> cambiarEstado(String codigo, CambioEstadoDto request) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogo(cambiarEstado.handle(new CambiarEstadoCatalogoCommand(
                codigo, CatalogoApiMapper.estado(request), CatalogoApiMapper.fechaHasta(request)))));
    }
}
