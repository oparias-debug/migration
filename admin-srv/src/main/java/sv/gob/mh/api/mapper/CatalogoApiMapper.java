package sv.gob.mh.api.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import sv.gob.mh.api.dto.catalogo.CalificadorDto;
import sv.gob.mh.api.dto.catalogo.CambioEstadoDto;
import sv.gob.mh.api.dto.catalogo.CampoDefinicionDto;
import sv.gob.mh.api.dto.catalogo.CampoDto;
import sv.gob.mh.api.dto.catalogo.CatalogoCreacionDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDescriptoresDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDto;
import sv.gob.mh.api.dto.catalogo.CatalogoResumenDto;
import sv.gob.mh.api.dto.catalogo.ChildCatalogResultDto;
import sv.gob.mh.api.dto.catalogo.VigenciaDto;
import sv.gob.mh.application.command.catalogo.CrearCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores;
import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.DefinicionTipo;
import sv.gob.mh.domain.model.catalogo.NuevoCampo;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Traducción de los catálogos del contrato CU-ADM-01 ↔ commands y modelo de dominio. Vive en la capa
 * API (y no en {@code shared/mapper}) porque conoce los DTO generados, y {@code shared} no puede
 * depender de otras capas. Los registros y resultados de búsqueda los traduce
 * {@link RegistroApiMapper}.
 */
public final class CatalogoApiMapper {

    private CatalogoApiMapper() {
    }

    // ---------- Solicitudes ----------

    public static CrearCatalogoCommand aCommand(CatalogoCreacionDto request) {
        return new CrearCatalogoCommand(request.getCodigo(), request.getNombre(), request.getPadre(),
                EnumeradosApi.aEstado(request.getEstado()), desde(request.getVigencia()),
                hasta(request.getVigencia()), aNuevosCampos(request.getCampos()));
    }

    public static List<NuevoCampo> aNuevosCampos(List<CampoDefinicionDto> campos) {
        return campos == null ? null : campos.stream().map(CatalogoApiMapper::aNuevoCampo).toList();
    }

    /** RN-04, S-09: sin tipo, el campo es STRING{80} y se ignoran los demás parámetros de tipo. */
    private static NuevoCampo aNuevoCampo(CampoDefinicionDto campo) {
        DefinicionTipo definicion = campo.getTipo() == null ? DefinicionTipo.porDefecto()
                : new DefinicionTipo(EnumeradosApi.aTipoCampo(campo.getTipo()), campo.getMinimo(), campo.getMaximo(),
                        campo.getLongitudMaxima(), campo.getFechaDesde(), campo.getFechaHasta(), campo.getValores());
        return new NuevoCampo(campo.getNombre(), campo.getCalificador() == CalificadorDto.KEY, campo.getPosicion(),
                definicion);
    }

    /** SF-04 / SF-15: los descriptores reemplazan a los actuales; {@code padre} ausente o nulo deja el catálogo plano. */
    public static CambioDescriptores aCambioDescriptores(CatalogoDescriptoresDto request) {
        return new CambioDescriptores(request.getCodigo(), request.getNombre(), request.getPadre(),
                EnumeradosApi.aEstado(request.getEstado()), desde(request.getVigencia()),
                hasta(request.getVigencia()), Boolean.TRUE.equals(request.getConfirmarCambioPadre()),
                request.getRegistrosPadre());
    }

    /** SF-05, SF-06, SF-09: estado pedido en {@code CambioEstado}; {@code null} si solo cambia la TO DATE. */
    public static EstadoVigencia estado(CambioEstadoDto request) {
        return request == null ? null : EnumeradosApi.aEstado(request.getEstado());
    }

    /** Nueva TO DATE de {@code CambioEstado}; {@code null} la deja vacía. */
    public static LocalDate fechaHasta(CambioEstadoDto request) {
        return request == null ? null : request.getHasta();
    }

    static LocalDate desde(VigenciaDto vigencia) {
        return vigencia == null ? null : vigencia.getDesde();
    }

    static LocalDate hasta(VigenciaDto vigencia) {
        return vigencia == null ? null : vigencia.getHasta();
    }

    // ---------- Respuestas ----------

    /** SF-02: definición completa, con los campos ordenados por posición y el estado efectivo. */
    public static CatalogoDto aCatalogo(Catalogo catalogo) {
        CatalogoDto dto = new CatalogoDto()
                .codigo(catalogo.getCodigo())
                .nombre(catalogo.getNombre())
                .padre(catalogo.getCatalogoPadreCodigo())
                .hijo(catalogo.getCatalogoHijoCodigo())
                .estado(EnumeradosApi.aEstadoDto(catalogo.estadoEfectivo()))
                .vigencia(aVigencia(catalogo.getFechaDesde(), catalogo.getFechaHasta()))
                .campos(new ArrayList<>());
        catalogo.camposOrdenados().forEach(campo -> dto.addCamposItem(aCampo(campo)));
        return dto;
    }

    /** Tipo efectivo: un campo definido sin tipo ya se guardó como STRING{80}. */
    private static CampoDto aCampo(CampoDefinicion campo) {
        DefinicionTipo definicion = campo.getDefinicion();
        return new CampoDto()
                .nombre(campo.getNombre())
                .calificador(EnumeradosApi.calificador(campo))
                .posicion(campo.getPosicion())
                .tipo(EnumeradosApi.aTipoCampoDto(definicion.tipo()))
                .minimo(definicion.minimo())
                .maximo(definicion.maximo())
                .longitudMaxima(definicion.longitudMaxima())
                .fechaDesde(definicion.fechaMinima())
                .fechaHasta(definicion.fechaMaxima())
                .valores(new LinkedHashSet<>(definicion.valoresEnum()));
    }

    public static CatalogoResumenDto aCatalogoResumen(Catalogo catalogo) {
        return new CatalogoResumenDto()
                .codigo(catalogo.getCodigo())
                .nombre(catalogo.getNombre())
                .padre(catalogo.getCatalogoPadreCodigo())
                .hijo(catalogo.getCatalogoHijoCodigo())
                .estado(EnumeradosApi.aEstadoDto(catalogo.estadoEfectivo()))
                .vigencia(aVigencia(catalogo.getFechaDesde(), catalogo.getFechaHasta()));
    }

    static VigenciaDto aVigencia(LocalDate desde, LocalDate hasta) {
        return new VigenciaDto().desde(desde).hasta(hasta);
    }

    /** SF-12: {@code {codigo, nombre}} del catálogo hijo. */
    public static ChildCatalogResultDto aChildCatalogResult(Catalogo hijo) {
        return new ChildCatalogResultDto(hijo.getCodigo(), hijo.getNombre());
    }
}
