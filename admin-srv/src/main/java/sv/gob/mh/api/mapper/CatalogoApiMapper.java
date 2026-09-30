package sv.gob.mh.api.mapper;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sv.gob.mh.api.dto.catalogo.ActiveStatusDto;
import sv.gob.mh.api.dto.catalogo.CatalogChildResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogCreateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogFieldRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogFieldResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordFieldValuesResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordValueRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordValueResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogSummaryResponseDto;
import sv.gob.mh.api.dto.catalogo.DescriptoresCatalogoInformados;
import sv.gob.mh.api.dto.catalogo.FieldQualifierDto;
import sv.gob.mh.application.command.catalogo.CrearCatalogoCommand;
import sv.gob.mh.application.query.catalogo.RegistroProyectado;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores.Descriptor;
import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.NuevoCampo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.ValorCampo;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Traducción DTO del contrato CU-ADM-01 ↔ commands y modelo de dominio. Vive en la capa API (y no
 * en {@code shared/mapper}) porque conoce los DTO generados, y {@code shared} no puede depender
 * de otras capas (regla R3).
 */
public final class CatalogoApiMapper {

    private static final Map<String, Descriptor> DESCRIPTORES = Map.of(
            DescriptoresCatalogoInformados.NAME, Descriptor.NOMBRE,
            DescriptoresCatalogoInformados.PARENT, Descriptor.PADRE,
            DescriptoresCatalogoInformados.ACTIVE, Descriptor.ESTADO,
            DescriptoresCatalogoInformados.FROM_DATE, Descriptor.FECHA_DESDE,
            DescriptoresCatalogoInformados.TO_DATE, Descriptor.FECHA_HASTA);

    private CatalogoApiMapper() {
    }

    // ---------- Solicitudes ----------

    public static CrearCatalogoCommand aCommand(CatalogCreateRequestDto request) {
        return new CrearCatalogoCommand(request.getCode(), request.getName(), request.getParent(),
                aEstado(request.getActive()), request.getFromDate(), request.getToDate(),
                aNuevosCampos(request.getFields()));
    }

    public static CambioDescriptores aCambioDescriptores(CatalogDescriptorsUpdateRequestDto request) {
        Set<Descriptor> informados = EnumSet.noneOf(Descriptor.class);
        DescriptoresCatalogoInformados.informadas(request).forEach(nombre -> informados.add(DESCRIPTORES.get(nombre)));
        return new CambioDescriptores(informados, request.getName(), request.getParent(), aEstado(request.getActive()),
                request.getFromDate(), request.getToDate());
    }

    public static List<NuevoCampo> aNuevosCampos(List<CatalogFieldRequestDto> campos) {
        return campos == null ? null
                : campos.stream().map(campo -> new NuevoCampo(campo.getName(), campo.getQualifier() == FieldQualifierDto.KEY))
                        .toList();
    }

    public static List<ValorCampo> aValores(List<CatalogRecordValueRequestDto> valores) {
        return valores.stream().map(valor -> new ValorCampo(valor.getField(), valor.getValor())).toList();
    }

    private static EstadoVigencia aEstado(ActiveStatusDto activo) {
        return activo == null ? null : EstadoVigencia.valueOf(activo.name());
    }

    // ---------- Respuestas ----------

    public static CatalogResponseDto aCatalogResponse(Catalogo catalogo) {
        CatalogResponseDto dto = new CatalogResponseDto()
                .code(catalogo.getCodigo())
                .name(catalogo.getNombre())
                .parent(catalogo.getCatalogoPadreCodigo())
                .active(aActiveStatus(catalogo.estadoEfectivo()))
                .fromDate(catalogo.getFechaDesde())
                .toDate(catalogo.getFechaHasta())
                .fields(new ArrayList<>());
        catalogo.camposOrdenados().forEach(campo -> dto.addFieldsItem(new CatalogFieldResponseDto()
                .name(campo.getNombre())
                .qualifier(calificador(campo))
                .posicion(campo.getPosicion())));
        return dto;
    }

    public static CatalogSummaryResponseDto aCatalogSummary(Catalogo catalogo) {
        return new CatalogSummaryResponseDto()
                .code(catalogo.getCodigo())
                .name(catalogo.getNombre())
                .parent(catalogo.getCatalogoPadreCodigo())
                .active(aActiveStatus(catalogo.estadoEfectivo()))
                .fromDate(catalogo.getFechaDesde())
                .toDate(catalogo.getFechaHasta());
    }

    public static CatalogChildResponseDto aCatalogChild(Catalogo catalogo) {
        return new CatalogChildResponseDto(catalogo.getCodigo(), catalogo.getNombre());
    }

    /** Registro completo: todos sus valores en orden de posición, con su estado efectivo (Regla 12). */
    public static CatalogRecordResponseDto aCatalogRecordResponse(Registro registro) {
        Catalogo catalogo = registro.getCatalogo();
        CatalogRecordResponseDto dto = new CatalogRecordResponseDto()
                .catalog(catalogo.getCodigo())
                .parentRecord(registro.getRegistroPadre() != null ? registro.getRegistroPadre().clave() : null)
                .active(aActiveStatus(registro.estadoEfectivo()))
                .fromDate(registro.getFechaDesde())
                .toDate(registro.getFechaHasta())
                .values(new ArrayList<>());
        catalogo.camposOrdenados().forEach(campo -> dto.addValuesItem(aValor(campo, registro)));
        return dto;
    }

    /** Registro reducido a los campos pedidos (Reglas 4 y 5). */
    public static CatalogRecordFieldValuesResponseDto aFieldValues(RegistroProyectado proyectado) {
        Registro registro = proyectado.registro();
        return new CatalogRecordFieldValuesResponseDto()
                .keyValue(registro.getClave())
                .active(aActiveStatus(registro.estadoEfectivo()))
                .values(proyectado.campos().stream().map(campo -> aValor(campo, registro)).toList());
    }

    private static CatalogRecordValueResponseDto aValor(CampoDefinicion campo, Registro registro) {
        return new CatalogRecordValueResponseDto()
                .field(campo.getNombre())
                .qualifier(calificador(campo))
                .valor(registro.valor(campo));
    }

    private static ActiveStatusDto aActiveStatus(EstadoVigencia estado) {
        return ActiveStatusDto.valueOf(estado.name());
    }

    private static FieldQualifierDto calificador(CampoDefinicion campo) {
        return campo.isEsKey() ? FieldQualifierDto.KEY : FieldQualifierDto.FIELD;
    }
}
