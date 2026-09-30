package sv.gob.mh.api.controller.catalogo;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.api.dto.catalogo.CatalogChildResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogCreateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogExistenceResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordCreateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordFieldValuesResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogSummaryResponseDto;
import sv.gob.mh.api.dto.catalogo.InactivationRequestDto;

/**
 * CU-ADM-01 (Administración de Catálogos), tag AdministracionCatalogos. Las rutas las declara la
 * interfaz generada del contrato; aquí se montan bajo {@value #BASE}. Cada operación exige un
 * token (401 NO_AUTENTICADO) con un rol de administración de catálogos (403 SIN_PERMISOS); los
 * errores los traduce {@code CatalogosManejadorErrores}.
 *
 * <p>Las operaciones las atienden {@link EscrituraCatalogos}, {@link EscrituraRegistros} y
 * {@link LecturaCatalogos}, que traducen el contrato a los handlers y queries (CQRS de la
 * plantilla).</p>
 */
@RestController
@RequestMapping(CatalogosAdministracionController.BASE)
@PreAuthorize(CatalogosAdministracionController.ADMINISTRA_CATALOGOS)
public class CatalogosAdministracionController implements AdministracionCatalogosApi {

    public static final String BASE = "/api/v1";

    /**
     * x-roles del contrato: ADMINISTRADOR_DEL_SISTEMA, que en Keycloak es el rol de realm
     * ADMINISTRADOR. Se admite también ADMINISTRADOR_DE_CATALOGOS, el rol que usa el menú del front.
     */
    public static final String ADMINISTRA_CATALOGOS = "hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_DE_CATALOGOS')";

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
    public ResponseEntity<CatalogResponseDto> crearCatalogo(CatalogCreateRequestDto catalogCreateRequestDto) {
        return catalogos.crear(catalogCreateRequestDto);
    }

    @Override
    public ResponseEntity<List<CatalogSummaryResponseDto>> listarCatalogos() {
        return lectura.listarCatalogos();
    }

    @Override
    public ResponseEntity<CatalogExistenceResponseDto> verificarExistenciaCatalogo(String name) {
        return lectura.verificarExistencia(name);
    }

    @Override
    public ResponseEntity<CatalogResponseDto> consultarCatalogoPorCodigo(String code) {
        return lectura.consultarCatalogo(code);
    }

    @Override
    public ResponseEntity<CatalogResponseDto> actualizarDescriptoresCatalogo(String code,
            CatalogDescriptorsUpdateRequestDto catalogDescriptorsUpdateRequestDto) {
        return catalogos.actualizarDescriptores(code, catalogDescriptorsUpdateRequestDto);
    }

    @Override
    public ResponseEntity<Void> eliminarCatalogo(String code) {
        return catalogos.eliminar(code);
    }

    @Override
    public ResponseEntity<CatalogResponseDto> actualizarCamposCatalogo(String code,
            CatalogFieldsUpdateRequestDto catalogFieldsUpdateRequestDto) {
        return catalogos.actualizarCampos(code, catalogFieldsUpdateRequestDto);
    }

    @Override
    public ResponseEntity<CatalogResponseDto> inactivarCatalogo(String code,
            InactivationRequestDto inactivationRequestDto) {
        return catalogos.inactivar(code, inactivationRequestDto);
    }

    @Override
    public ResponseEntity<List<CatalogChildResponseDto>> consultarCatalogosHijos(String code) {
        return lectura.consultarCatalogosHijos(code);
    }

    // ---------- Registros ----------

    @Override
    public ResponseEntity<CatalogRecordResponseDto> crearRegistro(String code,
            CatalogRecordCreateRequestDto catalogRecordCreateRequestDto) {
        return registros.crear(code, catalogRecordCreateRequestDto);
    }

    @Override
    public ResponseEntity<List<CatalogRecordFieldValuesResponseDto>> buscarListaRegistros(String code,
            List<String> fields) {
        return lectura.buscarRegistros(code, fields);
    }

    @Override
    public ResponseEntity<CatalogRecordFieldValuesResponseDto> buscarRegistroPorClave(String code, String keyValue,
            List<String> fields) {
        return lectura.buscarRegistroPorClave(code, keyValue, fields);
    }

    @Override
    public ResponseEntity<CatalogRecordResponseDto> actualizarRegistro(String code, String keyValue,
            CatalogRecordUpdateRequestDto catalogRecordUpdateRequestDto) {
        return registros.actualizar(code, keyValue, catalogRecordUpdateRequestDto);
    }

    @Override
    public ResponseEntity<Void> eliminarRegistro(String code, String keyValue) {
        return registros.eliminar(code, keyValue);
    }

    @Override
    public ResponseEntity<CatalogRecordResponseDto> inactivarRegistro(String code, String keyValue,
            InactivationRequestDto inactivationRequestDto) {
        return registros.inactivar(code, keyValue, inactivationRequestDto);
    }

    @Override
    public ResponseEntity<List<CatalogRecordResponseDto>> consultarRegistrosHijos(String code, String keyValue) {
        return lectura.consultarRegistrosHijos(code, keyValue);
    }
}
