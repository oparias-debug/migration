package sv.gob.mh.api.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.api.dto.catalogo.CatalogChildResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogCreateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogFieldRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogFieldResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogSummaryResponseDto;
import sv.gob.mh.api.dto.catalogo.FieldQualifierDto;
import sv.gob.mh.api.dto.catalogo.InactivationRequestDto;
import sv.gob.mh.application.command.catalogo.CrearCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.NuevoCampo;

/**
 * Traducción de los catálogos del contrato CU-ADM-01 ↔ commands y modelo de dominio. Vive en la capa
 * API (y no en {@code shared/mapper}) porque conoce los DTO generados, y {@code shared} no puede
 * depender de otras capas (regla R3). Los registros los traduce {@link RegistroApiMapper} y la
 * actualización de descriptores {@link DescriptoresApiMapper}.
 */
public final class CatalogoApiMapper {

    private CatalogoApiMapper() {
    }

    // ---------- Solicitudes ----------

    public static CrearCatalogoCommand aCommand(CatalogCreateRequestDto request) {
        return new CrearCatalogoCommand(request.getCode(), request.getName(), request.getParent(),
                EnumeradosApi.aEstado(request.getActive()), request.getFromDate(), request.getToDate(),
                aNuevosCampos(request.getFields()));
    }

    public static List<NuevoCampo> aNuevosCampos(List<CatalogFieldRequestDto> campos) {
        return campos == null ? null
                : campos.stream()
                        .map((CatalogFieldRequestDto campo) -> new NuevoCampo(campo.getName(),
                                campo.getQualifier() == FieldQualifierDto.KEY))
                        .toList();
    }

    /** El cuerpo de las inactivaciones es opcional. */
    public static LocalDate fechaHasta(InactivationRequestDto request) {
        return request != null ? request.getToDate() : null;
    }

    // ---------- Respuestas ----------

    public static CatalogResponseDto aCatalogResponse(Catalogo catalogo) {
        CatalogResponseDto dto = new CatalogResponseDto()
                .code(catalogo.getCodigo())
                .name(catalogo.getNombre())
                .parent(catalogo.getCatalogoPadreCodigo())
                .active(EnumeradosApi.aActiveStatus(catalogo.estadoEfectivo()))
                .fromDate(catalogo.getFechaDesde())
                .toDate(catalogo.getFechaHasta())
                .fields(new ArrayList<>());
        catalogo.camposOrdenados().forEach((CampoDefinicion campo) -> dto.addFieldsItem(new CatalogFieldResponseDto()
                .name(campo.getNombre())
                .qualifier(EnumeradosApi.calificador(campo))
                .posicion(campo.getPosicion())));
        return dto;
    }

    public static CatalogSummaryResponseDto aCatalogSummary(Catalogo catalogo) {
        return new CatalogSummaryResponseDto()
                .code(catalogo.getCodigo())
                .name(catalogo.getNombre())
                .parent(catalogo.getCatalogoPadreCodigo())
                .active(EnumeradosApi.aActiveStatus(catalogo.estadoEfectivo()))
                .fromDate(catalogo.getFechaDesde())
                .toDate(catalogo.getFechaHasta());
    }

    public static CatalogChildResponseDto aCatalogChild(Catalogo catalogo) {
        return new CatalogChildResponseDto(catalogo.getCodigo(), catalogo.getNombre());
    }
}
