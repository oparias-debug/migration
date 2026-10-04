package sv.gob.mh.api.controller.catalogo;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.api.dto.catalogo.CambioEstadoDto;
import sv.gob.mh.api.dto.catalogo.CampoDefinicionDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordsResultDto;
import sv.gob.mh.api.dto.catalogo.CatalogoCreacionDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDescriptoresDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDto;
import sv.gob.mh.api.dto.catalogo.CatalogoResumenDto;
import sv.gob.mh.api.dto.catalogo.ChildCatalogResultDto;
import sv.gob.mh.api.dto.catalogo.ChildRecordsResultDto;
import sv.gob.mh.api.dto.catalogo.KeySearchResultDto;
import sv.gob.mh.api.dto.catalogo.RegistroCreacionDto;
import sv.gob.mh.api.dto.catalogo.RegistroDto;

/**
 * CU-ADM-01 (Administración de Catálogos), tags {@code catalogos} y {@code registros}. Las rutas las
 * declaran las interfaces generadas del contrato; aquí se montan bajo {@value #BASE}. Toda
 * operación exige un token (401, filtro de seguridad). Las consultas (GET) quedan abiertas a
 * cualquier usuario autenticado y a los Sistemas Consumidores (RN-25, S-08); las demás exigen el
 * rol {@code ADMINISTRADOR_DE_CATALOGOS} (403, E-25). Los errores los traduce
 * {@code CatalogosManejadorErrores}.
 *
 * <p>Las operaciones las atienden {@link EscrituraCatalogos}, {@link EscrituraRegistros} y
 * {@link LecturaCatalogos}, que traducen el contrato a los handlers y queries (CQRS de la
 * plantilla).</p>
 */
@RestController
@RequestMapping(CatalogosAdministracionController.BASE)
public class CatalogosAdministracionController implements CatalogosApi, RegistrosApi {

    public static final String BASE = "/api/v1";

    /** Rol de las operaciones de mantenimiento (RN-25, S-08). */
    public static final String ADMINISTRA_CATALOGOS = "hasRole('ADMINISTRADOR_DE_CATALOGOS')";

    private final EscrituraCatalogos catalogos;
    private final EscrituraRegistros registros;
    private final LecturaCatalogos lectura;

    public CatalogosAdministracionController(EscrituraCatalogos catalogos, EscrituraRegistros registros,
            LecturaCatalogos lectura) {
        this.catalogos = catalogos;
        this.registros = registros;
        this.lectura = lectura;
    }

    // ---------- Catálogos ----------

    @Override
    public ResponseEntity<List<CatalogoResumenDto>> buscarListarCatalogos(String codigo, String nombre) {
        return lectura.buscarListarCatalogos(codigo, nombre);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<CatalogoDto> crearCatalogo(CatalogoCreacionDto catalogoCreacionDto) {
        return catalogos.crear(catalogoCreacionDto);
    }

    @Override
    public ResponseEntity<CatalogoDto> consultarCatalogo(String codigo) {
        return lectura.consultarCatalogo(codigo);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<CatalogoDto> actualizarDescriptores(String codigo,
            CatalogoDescriptoresDto catalogoDescriptoresDto) {
        return catalogos.actualizarDescriptores(codigo, catalogoDescriptoresDto);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<Void> eliminarCatalogo(String codigo) {
        return catalogos.eliminar(codigo);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<CatalogoDto> actualizarCampos(String codigo, List<CampoDefinicionDto> campoDefinicionDto) {
        return catalogos.actualizarCampos(codigo, campoDefinicionDto);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<CatalogoDto> cambiarEstadoCatalogo(String codigo, CambioEstadoDto cambioEstadoDto) {
        return catalogos.cambiarEstado(codigo, cambioEstadoDto);
    }

    @Override
    public ResponseEntity<ChildCatalogResultDto> consultarCatalogoHijo(String codigo) {
        return lectura.consultarCatalogoHijo(codigo);
    }

    // ---------- Registros ----------

    @Override
    public ResponseEntity<CatalogRecordsResultDto> listarRegistros(String codigo, List<String> campos) {
        return lectura.listarRegistros(codigo, campos);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<RegistroDto> crearRegistro(String codigo, RegistroCreacionDto registroCreacionDto) {
        return registros.crear(codigo, registroCreacionDto);
    }

    @Override
    public ResponseEntity<KeySearchResultDto> buscarRegistroPorLlave(String codigo, String llave,
            List<String> campos) {
        return lectura.buscarRegistroPorLlave(codigo, llave, campos);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<RegistroDto> actualizarRegistro(String codigo, String llave,
            Map<String, String> requestBody) {
        return registros.actualizar(codigo, llave, requestBody);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<Void> eliminarRegistro(String codigo, String llave) {
        return registros.eliminar(codigo, llave);
    }

    @Override
    @PreAuthorize(ADMINISTRA_CATALOGOS)
    public ResponseEntity<RegistroDto> cambiarEstadoRegistro(String codigo, String llave,
            CambioEstadoDto cambioEstadoDto) {
        return registros.cambiarEstado(codigo, llave, cambioEstadoDto);
    }

    @Override
    public ResponseEntity<ChildRecordsResultDto> buscarRegistrosHijos(String codigo, String llave,
            List<String> campos) {
        return lectura.buscarRegistrosHijos(codigo, llave, campos);
    }
}
