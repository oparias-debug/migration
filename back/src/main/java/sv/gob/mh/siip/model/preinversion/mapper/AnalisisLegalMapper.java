package sv.gob.mh.siip.model.preinversion.mapper;

import org.springframework.stereotype.Component;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisLegal;
import sv.gob.mh.siip.model.preinversion.domain.AnalsisGestionesLegalesRequeridas;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisLegalRequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para transformar entre entidades de dominio y DTOs del módulo de Análisis Legal (CU-PRE-16).
 *
 * @author Luis Medrano
 * @since 2026-09
 */
@Component
public class AnalisisLegalMapper {

    /**
     * Mapea una entidad AnalisisLegal y su lista de filas hacia el AnalisisLegalDto de lectura,
     * calculando server-side el total de los costos de los entregables.
     *
     * @param entity Entidad de dominio AnalisisLegal.
     * @return DTO con la estructura completa para la respuesta HTTP.
     * @author Luis Medrano
     */
    public AnalisisLegalDto toDto(AnalisisLegal entity) {
        if (entity == null) {
            return null;
        }

        List<FilaAnalisisLegalRequestDto> filasDto = new ArrayList<>();
        if (entity.getFilas() != null) {
            filasDto = entity.getFilas().stream()
                    .map(this::toFilaDto)
                    .collect(Collectors.toList());
        }

        // Cálculo automático del total de costos de entregables por el servidor
        double totalCosto = entity.getFilas().stream()
                .mapToDouble(f -> f.getCostoEntregable() != null ? f.getCostoEntregable() : 0.0)
                .sum();

        AnalisisLegalDto dto = new AnalisisLegalDto();
        dto.setIdProyecto(entity.getProyecto() != null ? entity.getProyecto().getId() : null);
        dto.setRequiereAnalisisLegal(entity.getRequiereAnalisisLegal());
        dto.setFilas(filasDto);
        dto.setTotalCostoEntregables(totalCosto);

        return dto;
    }

    /**
     * Mapea una entidad hija de detalle hacia su DTO de fila.
     *
     * @param entidadDetalle Entidad AnalsisGestionesLegalesRequeridas.
     * @return DTO de la fila de análisis legal.
     * @author Luis Medrano
     */
    public FilaAnalisisLegalRequestDto toFilaDto(AnalsisGestionesLegalesRequeridas entidadDetalle) {
        if (entidadDetalle == null) {
            return null;
        }

        FilaAnalisisLegalRequestDto dto = new FilaAnalisisLegalRequestDto();
        dto.setAnalisisGestionLegalRequerida(entidadDetalle.getAnalisisGestionLegalRequerida());
        dto.setEntregable(entidadDetalle.getEntregable());
        dto.setCostoEntregable(entidadDetalle.getCostoEntregable());
        return dto;
    }

    /**
     * Mapea un DTO de solicitud hacia una nueva entidad hija.
     *
     * @param filaDto DTO de la fila proveniente del request.
     * @param analisisLegal Entidad padre a la que pertenecerá el detalle.
     * @return Entidad AnalsisGestionesLegalesRequeridas construida.
     * @author Luis Medrano
     */
    public AnalsisGestionesLegalesRequeridas toEntity(FilaAnalisisLegalRequestDto filaDto, AnalisisLegal analisisLegal) {
        if (filaDto == null) {
            return null;
        }

        return AnalsisGestionesLegalesRequeridas.builder()
                .analisisLegal(analisisLegal)
                .analisisGestionLegalRequerida(filaDto.getAnalisisGestionLegalRequerida())
                .entregable(filaDto.getEntregable())
                .costoEntregable(filaDto.getCostoEntregable())
                .build();
    }
}