package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaRegistroRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;

/**
 * Costo y fechas de una tabla "Registro de Etapas" de CU-PRE-03.5 (Anexo A.1), para
 * {@link SeleccionEtapasRegistro#actualizar}: copia lo enviado a cada etapa acumulando las fechas
 * con formato inválido (RN04) y valida el orden cronológico de la ruta (RN23).
 */
final class RegistroFechasEtapas {

    /** RN04: mismo formato dd/mm/aaaa ya validado a nivel de DTO (`@Pattern`) para fechaInicio/fechaFin. */
    private static final DateTimeFormatter FORMATO_FECHA_ETAPA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<ErrorDetalleDto> fechasInvalidas = new ArrayList<>();

    /** Copia costo y fechas del item a la etapa; una fecha inválida queda en {@code null} y se anota. */
    void aplicar(EtapaPreinversion etapa, EtapaRegistroRequestDto item) {
        TipoEtapaPreinversion tipoEtapa = etapa.getTipoEtapa();

        // RN05/RN11: el costo de EJECUCION lo fija el Sistema desde el presupuesto de inversion
        // (ver CostoEtapaEjecucion); cualquier valor enviado por el cliente se ignora.
        if (tipoEtapa != TipoEtapaPreinversion.EJECUCION) {
            etapa.setCosto(item.getCosto());
        }
        LocalDate fechaInicio = parsearFecha(tipoEtapa, item.getFechaInicio());
        LocalDate fechaFin = parsearFecha(tipoEtapa, item.getFechaFin());
        etapa.setFechaInicio(fechaInicio);
        etapa.setFechaFin(fechaFin);
        if (fechaInicio != null && fechaFin != null) {
            etapa.setHabilitadoParaRegistro(true);
        }
    }

    /**
     * RN04: rechaza con 400 si alguna de las fechas aplicadas no era una fecha calendario válida.
     *
     * @throws ValidacionNegocioException con código {@code FECHA_INVALIDA}
     */
    void exigirFechasValidas() {
        if (!fechasInvalidas.isEmpty()) {
            throw new ValidacionNegocioException("FECHA_INVALIDA",
                    "Alguna de las fechas indicadas no es una fecha calendario válida.", fechasInvalidas);
        }
    }

    /**
     * RN23: las etapas PERFIL/PREFACTIBILIDAD/FACTIBILIDAD/DISENO/EJECUCION deben respetar ese
     * orden cronológico entre sí: ninguna etapa puede iniciar antes de que termine la etapa previa
     * (en orden de ruta) que también tenga fechas completas, ni terminar antes de su propio inicio.
     * Solo se comparan etapas con fechaInicio y fechaFin completos: las incompletas son
     * responsabilidad visual del cliente (RN19), no entran en esta validación. Rechaza con 400,
     * igual que RN04 (formato de fecha), del cual esta regla es una extensión natural.
     */
    static void validarConsistencia(List<EtapaPreinversion> etapas) {
        List<EtapaPreinversion> conFechas = etapas.stream()
                .filter((EtapaPreinversion etapa) -> etapa.getFechaInicio() != null && etapa.getFechaFin() != null)
                .sorted(Comparator.comparing(EtapaPreinversion::getTipoEtapa))
                .toList();
        List<ErrorDetalleDto> detalles = new ArrayList<>();

        for (var i = 0; i < conFechas.size(); i++) {
            EtapaPreinversion actual = conFechas.get(i);
            if (actual.getFechaInicio().isAfter(actual.getFechaFin())) {
                detalles.add(new ErrorDetalleDto().campo(actual.getTipoEtapa().name())
                        .mensaje("La fecha de inicio no puede ser posterior a la fecha de finalización"
                                + " de la misma etapa."));
            }
            if (i > 0 && actual.getFechaInicio().isBefore(conFechas.get(i - 1).getFechaFin())) {
                detalles.add(new ErrorDetalleDto().campo(actual.getTipoEtapa().name())
                        .mensaje("No puede iniciar antes de que finalice la etapa "
                                + conFechas.get(i - 1).getTipoEtapa() + "."));
            }
        }

        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException("FECHAS_ETAPAS_INCONSISTENTES",
                    "Las fechas de las etapas no son consistentes con el orden de la ruta.", detalles);
        }
    }

    private LocalDate parsearFecha(TipoEtapaPreinversion tipoEtapa, String fecha) {
        // Una fecha vacía equivale a "sin fecha": RN19 solo la marca en rojo, no impide guardar.
        if (fecha == null || fecha.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha, FORMATO_FECHA_ETAPA);
        } catch (DateTimeParseException ex) {
            fechasInvalidas.add(new ErrorDetalleDto().campo(tipoEtapa.name()).mensaje("Fecha inválida."));
            return null;
        }
    }
}
