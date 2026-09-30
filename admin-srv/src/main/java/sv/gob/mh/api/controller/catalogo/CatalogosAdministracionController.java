package sv.gob.mh.api.controller.catalogo;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

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
import sv.gob.mh.api.mapper.CatalogoApiMapper;
import sv.gob.mh.application.command.catalogo.ActualizarCamposCatalogoCommand;
import sv.gob.mh.application.command.catalogo.ActualizarDescriptoresCatalogoCommand;
import sv.gob.mh.application.command.catalogo.ActualizarRegistroCommand;
import sv.gob.mh.application.command.catalogo.CrearRegistroCommand;
import sv.gob.mh.application.command.catalogo.EliminarCatalogoCommand;
import sv.gob.mh.application.command.catalogo.EliminarRegistroCommand;
import sv.gob.mh.application.command.catalogo.InactivarCatalogoCommand;
import sv.gob.mh.application.command.catalogo.InactivarRegistroCommand;
import sv.gob.mh.application.handler.catalogo.ActualizarCamposCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.ActualizarDescriptoresCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.ActualizarRegistroHandler;
import sv.gob.mh.application.handler.catalogo.CrearCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.CrearRegistroHandler;
import sv.gob.mh.application.handler.catalogo.EliminarCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.EliminarRegistroHandler;
import sv.gob.mh.application.handler.catalogo.InactivarCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.InactivarRegistroHandler;
import sv.gob.mh.application.query.catalogo.BuscarRegistroPorClaveQuery;
import sv.gob.mh.application.query.catalogo.BuscarRegistrosQuery;
import sv.gob.mh.application.query.catalogo.ConsultarCatalogoQuery;
import sv.gob.mh.application.query.catalogo.ConsultarCatalogosHijosQuery;
import sv.gob.mh.application.query.catalogo.ConsultarRegistrosHijosQuery;
import sv.gob.mh.application.query.catalogo.ListarCatalogosQuery;
import sv.gob.mh.application.query.catalogo.VerificarExistenciaCatalogoQuery;
import sv.gob.mh.domain.model.catalogo.Registro;

/**
 * CU-ADM-01 (Administración de Catálogos), tag AdministracionCatalogos. Las rutas las declara la
 * interfaz generada del contrato; aquí se montan bajo {@value #BASE}. Cada operación exige un
 * token (401 NO_AUTENTICADO) con un rol de administración de catálogos (403 SIN_PERMISOS); los
 * errores los traduce {@code CatalogosManejadorErrores}.
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
    static final String ADMINISTRA_CATALOGOS = "hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_DE_CATALOGOS')";

    private final CrearCatalogoHandler crearCatalogo;
    private final ActualizarDescriptoresCatalogoHandler actualizarDescriptores;
    private final ActualizarCamposCatalogoHandler actualizarCampos;
    private final InactivarCatalogoHandler inactivarCatalogo;
    private final EliminarCatalogoHandler eliminarCatalogo;
    private final CrearRegistroHandler crearRegistro;
    private final ActualizarRegistroHandler actualizarRegistro;
    private final InactivarRegistroHandler inactivarRegistro;
    private final EliminarRegistroHandler eliminarRegistro;
    private final ListarCatalogosQuery listarCatalogos;
    private final VerificarExistenciaCatalogoQuery verificarExistencia;
    private final ConsultarCatalogoQuery consultarCatalogo;
    private final ConsultarCatalogosHijosQuery consultarCatalogosHijos;
    private final BuscarRegistrosQuery buscarRegistros;
    private final BuscarRegistroPorClaveQuery buscarRegistroPorClave;
    private final ConsultarRegistrosHijosQuery consultarRegistrosHijos;

    @SuppressWarnings("java:S107") // Un handler o query por operación del contrato (CQRS de la plantilla).
    public CatalogosAdministracionController(CrearCatalogoHandler crearCatalogo,
            ActualizarDescriptoresCatalogoHandler actualizarDescriptores, ActualizarCamposCatalogoHandler actualizarCampos,
            InactivarCatalogoHandler inactivarCatalogo, EliminarCatalogoHandler eliminarCatalogo,
            CrearRegistroHandler crearRegistro, ActualizarRegistroHandler actualizarRegistro,
            InactivarRegistroHandler inactivarRegistro, EliminarRegistroHandler eliminarRegistro,
            ListarCatalogosQuery listarCatalogos, VerificarExistenciaCatalogoQuery verificarExistencia,
            ConsultarCatalogoQuery consultarCatalogo, ConsultarCatalogosHijosQuery consultarCatalogosHijos,
            BuscarRegistrosQuery buscarRegistros, BuscarRegistroPorClaveQuery buscarRegistroPorClave,
            ConsultarRegistrosHijosQuery consultarRegistrosHijos) {
        this.crearCatalogo = crearCatalogo;
        this.actualizarDescriptores = actualizarDescriptores;
        this.actualizarCampos = actualizarCampos;
        this.inactivarCatalogo = inactivarCatalogo;
        this.eliminarCatalogo = eliminarCatalogo;
        this.crearRegistro = crearRegistro;
        this.actualizarRegistro = actualizarRegistro;
        this.inactivarRegistro = inactivarRegistro;
        this.eliminarRegistro = eliminarRegistro;
        this.listarCatalogos = listarCatalogos;
        this.verificarExistencia = verificarExistencia;
        this.consultarCatalogo = consultarCatalogo;
        this.consultarCatalogosHijos = consultarCatalogosHijos;
        this.buscarRegistros = buscarRegistros;
        this.buscarRegistroPorClave = buscarRegistroPorClave;
        this.consultarRegistrosHijos = consultarRegistrosHijos;
    }

    // ---------- Catálogos ----------

    @Override
    public ResponseEntity<CatalogResponseDto> crearCatalogo(CatalogCreateRequestDto catalogCreateRequestDto) {
        CatalogResponseDto creado = CatalogoApiMapper.aCatalogResponse(
                crearCatalogo.handle(CatalogoApiMapper.aCommand(catalogCreateRequestDto)));
        return ResponseEntity.created(uri(PATH_CONSULTAR_CATALOGO_POR_CODIGO, creado.getCode())).body(creado);
    }

    @Override
    public ResponseEntity<List<CatalogSummaryResponseDto>> listarCatalogos() {
        return ResponseEntity.ok(listarCatalogos.ejecutar().stream().map(CatalogoApiMapper::aCatalogSummary).toList());
    }

    @Override
    public ResponseEntity<CatalogExistenceResponseDto> verificarExistenciaCatalogo(String name) {
        return ResponseEntity.ok(new CatalogExistenceResponseDto(name, verificarExistencia.ejecutar(name)));
    }

    @Override
    public ResponseEntity<CatalogResponseDto> consultarCatalogoPorCodigo(String code) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogResponse(consultarCatalogo.ejecutar(code)));
    }

    @Override
    public ResponseEntity<CatalogResponseDto> actualizarDescriptoresCatalogo(String code,
            CatalogDescriptorsUpdateRequestDto catalogDescriptorsUpdateRequestDto) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogResponse(actualizarDescriptores.handle(
                new ActualizarDescriptoresCatalogoCommand(code,
                        CatalogoApiMapper.aCambioDescriptores(catalogDescriptorsUpdateRequestDto)))));
    }

    @Override
    public ResponseEntity<Void> eliminarCatalogo(String code) {
        eliminarCatalogo.handle(new EliminarCatalogoCommand(code));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @Override
    public ResponseEntity<CatalogResponseDto> actualizarCamposCatalogo(String code,
            CatalogFieldsUpdateRequestDto catalogFieldsUpdateRequestDto) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogResponse(actualizarCampos.handle(
                new ActualizarCamposCatalogoCommand(code,
                        CatalogoApiMapper.aNuevosCampos(catalogFieldsUpdateRequestDto.getFields())))));
    }

    @Override
    public ResponseEntity<CatalogResponseDto> inactivarCatalogo(String code,
            InactivationRequestDto inactivationRequestDto) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogResponse(
                inactivarCatalogo.handle(new InactivarCatalogoCommand(code, fechaHasta(inactivationRequestDto)))));
    }

    @Override
    public ResponseEntity<List<CatalogChildResponseDto>> consultarCatalogosHijos(String code) {
        return ResponseEntity.ok(
                consultarCatalogosHijos.ejecutar(code).stream().map(CatalogoApiMapper::aCatalogChild).toList());
    }

    // ---------- Registros ----------

    @Override
    public ResponseEntity<CatalogRecordResponseDto> crearRegistro(String code,
            CatalogRecordCreateRequestDto catalogRecordCreateRequestDto) {
        Registro creado = crearRegistro.handle(new CrearRegistroCommand(code,
                CatalogoApiMapper.aValores(catalogRecordCreateRequestDto.getValues()),
                catalogRecordCreateRequestDto.getParentRecord(), catalogRecordCreateRequestDto.getFromDate(),
                catalogRecordCreateRequestDto.getToDate()));
        return ResponseEntity.created(uri(PATH_BUSCAR_REGISTRO_POR_CLAVE, code, creado.getClave()))
                .body(CatalogoApiMapper.aCatalogRecordResponse(creado));
    }

    @Override
    public ResponseEntity<List<CatalogRecordFieldValuesResponseDto>> buscarListaRegistros(String code,
            List<String> fields) {
        return ResponseEntity.ok(
                buscarRegistros.ejecutar(code, fields).stream().map(CatalogoApiMapper::aFieldValues).toList());
    }

    @Override
    public ResponseEntity<CatalogRecordFieldValuesResponseDto> buscarRegistroPorClave(String code, String keyValue,
            List<String> fields) {
        return ResponseEntity.ok(CatalogoApiMapper.aFieldValues(buscarRegistroPorClave.ejecutar(code, keyValue, fields)));
    }

    @Override
    public ResponseEntity<CatalogRecordResponseDto> actualizarRegistro(String code, String keyValue,
            CatalogRecordUpdateRequestDto catalogRecordUpdateRequestDto) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogRecordResponse(actualizarRegistro.handle(
                new ActualizarRegistroCommand(code, keyValue,
                        CatalogoApiMapper.aValores(catalogRecordUpdateRequestDto.getValues())))));
    }

    @Override
    public ResponseEntity<Void> eliminarRegistro(String code, String keyValue) {
        eliminarRegistro.handle(new EliminarRegistroCommand(code, keyValue));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @Override
    public ResponseEntity<CatalogRecordResponseDto> inactivarRegistro(String code, String keyValue,
            InactivationRequestDto inactivationRequestDto) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogRecordResponse(inactivarRegistro.handle(
                new InactivarRegistroCommand(code, keyValue, fechaHasta(inactivationRequestDto)))));
    }

    @Override
    public ResponseEntity<List<CatalogRecordResponseDto>> consultarRegistrosHijos(String code, String keyValue) {
        return ResponseEntity.ok(consultarRegistrosHijos.ejecutar(code, keyValue).stream()
                .map(CatalogoApiMapper::aCatalogRecordResponse)
                .toList());
    }

    /** El cuerpo de las inactivaciones es opcional. */
    private static LocalDate fechaHasta(InactivationRequestDto request) {
        return request != null ? request.getToDate() : null;
    }

    private static URI uri(String plantilla, Object... variables) {
        return UriComponentsBuilder.fromPath(BASE + plantilla).buildAndExpand(variables).encode().toUri();
    }
}
