package sv.gob.mh.siip.model.preinversion.service;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionRequestDto;

/**
 * CU-PRE-22.1 "Programación Financiera de Preinversión": validaciones de la solicitud (períodos, etapas y
 * montos) y del alcance del actor, ejecutadas ANTES de escribir nada.
 */
final class ProgramacionFinancieraPreinversionValidaciones {

    private ProgramacionFinancieraPreinversionValidaciones() {
    }

    /** Los períodos a programar son obligatorios y deben ser mayores que cero. */
    static int periodosValidos(Integer periodos) {
        if (periodos == null || periodos < 1) {
            throw validacion("Períodos a programar es obligatorio y debe ser mayor que cero.");
        }
        return periodos;
    }

    /**
     * Valida las filas recibidas: al menos una, cada etapa perteneciente a la ruta del proyecto y sin
     * repetirse, y montos completos, finitos, no negativos y con al menos uno mayor que cero.
     */
    static List<FilaProgramacionEtapaRequestDto> filasValidas(ProgramacionFinancieraPreinversionRequestDto request,
            Collection<EtapaPreinversion> etapasDisponibles, int periodos) {
        List<FilaProgramacionEtapaRequestDto> filas = filasRecibidas(request);
        if (filas.isEmpty()) {
            throw validacion("Debe registrar la programación de al menos una etapa.");
        }
        Set<TipoEtapaPreinversion> permitidas = EnumSet.noneOf(TipoEtapaPreinversion.class);
        for (EtapaPreinversion etapa : etapasDisponibles) {
            permitidas.add(etapa.getTipoEtapa());
        }
        Set<TipoEtapaPreinversion> recibidas = EnumSet.noneOf(TipoEtapaPreinversion.class);
        for (FilaProgramacionEtapaRequestDto fila : filas) {
            validarEtapa(fila, permitidas, recibidas);
            validarMontos(fila.getProgramacionPorPeriodo(), periodos);
        }
        return filas;
    }

    /** Un Técnico con Unidad Ejecutora solo puede operar proyectos de esa misma unidad. */
    static void exigirAlcanceUnidadEjecutora(Usuario usuario, Proyecto proyecto) {
        if (usuario.getUnidadEjecutora() != null && (proyecto.getUnidadEjecutora() == null
                || !usuario.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId()))) {
            throw new AccesoDenegadoException("El proyecto no pertenece a la Unidad Ejecutora del actor.");
        }
    }

    static ValidacionNegocioException validacion(String mensaje) {
        return new ValidacionNegocioException(mensaje, List.of());
    }

    private static List<FilaProgramacionEtapaRequestDto> filasRecibidas(
            ProgramacionFinancieraPreinversionRequestDto request) {
        if (request == null || request.getFilas() == null) {
            return List.of();
        }
        return request.getFilas();
    }

    private static void validarEtapa(FilaProgramacionEtapaRequestDto fila, Set<TipoEtapaPreinversion> permitidas,
            Set<TipoEtapaPreinversion> recibidas) {
        if (fila == null || fila.getEtapa() == null) {
            throw validacion("La etapa es obligatoria.");
        }
        var etapa = TipoEtapaPreinversion.valueOf(fila.getEtapa().name());
        if (!permitidas.contains(etapa)) {
            throw validacion("La etapa no pertenece a la ruta de preinversión del proyecto.");
        }
        if (!recibidas.add(etapa)) {
            throw validacion("No se puede repetir una etapa en la programación.");
        }
    }

    private static void validarMontos(List<Double> montos, int periodos) {
        if (montos == null || montos.size() != periodos) {
            throw validacion("La programación debe contener exactamente los períodos configurados.");
        }
        var tieneMonto = false;
        for (Double monto : montos) {
            validarMonto(monto);
            tieneMonto |= monto > 0D;
        }
        if (!tieneMonto) {
            throw validacion("La programación de la etapa debe contener al menos un monto mayor que cero.");
        }
    }

    private static void validarMonto(Double monto) {
        if (monto == null) {
            throw validacion("Cada período debe contener un monto.");
        }
        if (!Double.isFinite(monto)) {
            throw validacion("Cada período debe contener un monto finito.");
        }
        if (monto < 0D) {
            throw validacion("Los montos programados no pueden ser negativos.");
        }
    }
}
