package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaRegistroRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.mapper.SeleccionYRegistroDeEtapasMapper;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;

/**
 * Tabla "Registro de Etapas" de CU-PRE-03.5 (Anexo A.1): alta de las filas de
 * {@link EtapaPreinversion} según la ruta vigente y registro de costo/fechas de cada etapa.
 */
@Component
public class SeleccionEtapasRegistro {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    /** RN04: mismo formato dd/mm/aaaa ya validado a nivel de DTO (`@Pattern`) para fechaInicio/fechaFin. */
    private static final DateTimeFormatter FORMATO_FECHA_ETAPA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** RN07/RN08/RN09: PERFIL+EJECUCION, habilitadas desde el inicio para cualquier iniciativa. */
    private static final List<TipoEtapaPreinversion> ETAPAS_INICIALES = List.of(TipoEtapaPreinversion.PERFIL,
            TipoEtapaPreinversion.EJECUCION);

    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final SeleccionEtapasProyectos proyectos;
    private final SeleccionYRegistroDeEtapasMapper mapper;

    public SeleccionEtapasRegistro(EtapaPreinversionRepository etapaPreinversionRepository,
            SeleccionEtapasProyectos proyectos, SeleccionYRegistroDeEtapasMapper mapper) {
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.proyectos = proyectos;
        this.mapper = mapper;
    }

    /**
     * RN09: PERFIL y EJECUCION están habilitadas desde el inicio, para cualquier iniciativa, sin
     * esperar a generar/aceptar la ruta (RN07/RN08: para Programa/Estudio General esas dos son
     * además las únicas etapas de la ruta). Excepción: un proyecto de emergencia solo muestra
     * PERFIL en Registro de Etapas (EJECUCION es su "etapaFutura" tras pasar por Viabilidad).
     */
    public List<EtapaDto> listar(Long idProyecto) {
        Proyecto proyecto = proyectos.buscar(idProyecto);
        List<EtapaPreinversion> etapas = enOrdenDeRuta(idProyecto);
        if (etapas.isEmpty()) {
            List<TipoEtapaPreinversion> etapasIniciales = Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia())
                    ? List.of(TipoEtapaPreinversion.PERFIL)
                    : ETAPAS_INICIALES;
            sincronizar(proyecto, etapasIniciales);
            etapas = enOrdenDeRuta(idProyecto);
        }
        return mapper.toDtoList(etapas);
    }

    /**
     * Registra costo y fechas de las etapas (botón único "Guardar"). RN04: fechas con formato de
     * calendario válido; RN23: fechas consistentes con el orden de la ruta.
     */
    public List<EtapaDto> actualizar(Long idProyecto, ActualizarEtapasRequestDto request) {
        Proyecto proyecto = proyectos.buscar(idProyecto);

        List<ErrorDetalleDto> detalles = new ArrayList<>();
        List<EtapaPreinversion> etapasTocadas = new ArrayList<>();
        for (EtapaRegistroRequestDto item : request.getEtapas()) {
            etapasTocadas.add(aplicarRegistro(proyecto, item, detalles));
        }
        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException("FECHA_INVALIDA",
                    "Alguna de las fechas indicadas no es una fecha calendario válida.", detalles);
        }
        etapasTocadas.forEach(etapaPreinversionRepository::save);

        List<EtapaPreinversion> etapasActualizadas = enOrdenDeRuta(idProyecto);
        validarConsistenciaFechas(etapasActualizadas);
        return mapper.toDtoList(etapasActualizadas);
    }

    /**
     * Etapas del proyecto en el orden PERFIL/PREFACTIBILIDAD/FACTIBILIDAD/DISENO/EJECUCION de la
     * ruta. No se puede usar un {@code ORDER BY} sobre la columna TIPO_ETAPA en el repositorio: al
     * ser {@code @Enumerated(EnumType.STRING)}, eso ordenaría alfabéticamente (DISENO, EJECUCION,
     * FACTIBILIDAD, PERFIL, PREFACTIBILIDAD), no en el orden real de la ruta. Se ordena en memoria
     * por el ordinal del enum, que sí refleja ese orden de forma estable.
     */
    public List<EtapaPreinversion> enOrdenDeRuta(Long idProyecto) {
        return etapaPreinversionRepository.findByProyectoId(idProyecto).stream()
                .sorted(Comparator.comparing(EtapaPreinversion::getTipoEtapa))
                .toList();
    }

    /**
     * Crea las filas de {@link EtapaPreinversion} que falten para reflejar la selección vigente.
     */
    public void sincronizar(Proyecto proyecto, Collection<TipoEtapaPreinversion> seleccion) {
        for (TipoEtapaPreinversion tipoEtapa : seleccion) {
            if (etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(proyecto.getId(), tipoEtapa).isEmpty()) {
                etapaPreinversionRepository.save(nuevaEtapa(proyecto, tipoEtapa));
            }
        }
    }

    /**
     * Igual que {@link #sincronizar}, pero antes aplica RN13: una etapa ya emitida
     * (tieneOpinionTecnica) que queda fuera de la selección no se elimina ni pierde su
     * información: se marca bloqueadaPorModificacion.
     */
    public void sincronizarBloqueandoEmitidas(Proyecto proyecto, Collection<TipoEtapaPreinversion> seleccion) {
        for (EtapaPreinversion existente : etapaPreinversionRepository.findByProyectoId(proyecto.getId())) {
            if (!seleccion.contains(existente.getTipoEtapa())
                    && Boolean.TRUE.equals(existente.getTieneOpinionTecnica())) {
                existente.setBloqueadaPorModificacion(true);
                etapaPreinversionRepository.save(existente);
            }
        }
        sincronizar(proyecto, seleccion);
    }

    private EtapaPreinversion aplicarRegistro(Proyecto proyecto, EtapaRegistroRequestDto item,
            List<ErrorDetalleDto> detalles) {
        TipoEtapaPreinversion tipoEtapa = TipoEtapaPreinversion.valueOf(item.getNombreEtapa().name());
        EtapaPreinversion etapa = etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(proyecto.getId(), tipoEtapa)
                .orElseGet(() -> nuevaEtapa(proyecto, tipoEtapa));

        // RN05/RN11: el costo de EJECUCION lo fija el Sistema (Presupuesto de inversion u
        // Opinion Tecnica mas reciente); cualquier valor enviado por el cliente se ignora.
        if (tipoEtapa != TipoEtapaPreinversion.EJECUCION) {
            etapa.setCosto(item.getCosto());
        }
        LocalDate fechaInicio = parsearFecha(tipoEtapa, item.getFechaInicio(), detalles);
        LocalDate fechaFin = parsearFecha(tipoEtapa, item.getFechaFin(), detalles);
        etapa.setFechaInicio(fechaInicio);
        etapa.setFechaFin(fechaFin);
        if (fechaInicio != null && fechaFin != null) {
            etapa.setHabilitadoParaRegistro(true);
        }
        return etapa;
    }

    /**
     * RN23: las etapas PERFIL/PREFACTIBILIDAD/FACTIBILIDAD/DISENO/EJECUCION deben respetar ese
     * orden cronológico entre sí: ninguna etapa puede iniciar antes de que termine la etapa previa
     * (en orden de ruta) que también tenga fechas completas, ni terminar antes de su propio inicio.
     * Solo se comparan etapas con fechaInicio y fechaFin completos: las incompletas son
     * responsabilidad visual del cliente (RN19), no entran en esta validación. Rechaza con 400,
     * igual que RN04 (formato de fecha), del cual esta regla es una extensión natural.
     */
    private static void validarConsistenciaFechas(List<EtapaPreinversion> etapas) {
        List<EtapaPreinversion> conFechas = etapas.stream()
                .filter((EtapaPreinversion etapa) -> etapa.getFechaInicio() != null && etapa.getFechaFin() != null)
                .sorted(Comparator.comparing(EtapaPreinversion::getTipoEtapa))
                .toList();
        List<ErrorDetalleDto> detalles = new ArrayList<>();

        for (int i = 0; i < conFechas.size(); i++) {
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

    private static LocalDate parsearFecha(TipoEtapaPreinversion tipoEtapa, String fecha,
            List<ErrorDetalleDto> detalles) {
        if (fecha == null) {
            return null;
        }
        try {
            return LocalDate.parse(fecha, FORMATO_FECHA_ETAPA);
        } catch (DateTimeParseException ex) {
            detalles.add(new ErrorDetalleDto().campo(tipoEtapa.name()).mensaje("Fecha inválida."));
            return null;
        }
    }

    private static EtapaPreinversion nuevaEtapa(Proyecto proyecto, TipoEtapaPreinversion tipoEtapa) {
        // RN09: PERFIL y EJECUCION habilitadas por defecto desde su creacion.
        boolean habilitadaPorDefecto = tipoEtapa == TipoEtapaPreinversion.PERFIL
                || tipoEtapa == TipoEtapaPreinversion.EJECUCION;
        return EtapaPreinversion.builder()
                .proyecto(proyecto)
                .tipoEtapa(tipoEtapa)
                .fechaSeleccion(LocalDateTime.now(ZONA_EL_SALVADOR))
                .habilitadoParaRegistro(habilitadaPorDefecto)
                .build();
    }
}
