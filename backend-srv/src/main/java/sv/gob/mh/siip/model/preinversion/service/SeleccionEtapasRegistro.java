package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaRegistroRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.mapper.SeleccionYRegistroDeEtapasMapper;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;

/**
 * Tabla "Registro de Etapas" de CU-PRE-03.5 (Anexo A.1): alta de las filas de
 * {@link EtapaPreinversion} según la ruta vigente y registro de costo/fechas de cada etapa.
 */
@Component
public class SeleccionEtapasRegistro {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    /** RN07/RN08/RN09: PERFIL+EJECUCION, habilitadas desde el inicio para cualquier iniciativa. */
    private static final List<TipoEtapaPreinversion> ETAPAS_INICIALES = List.of(TipoEtapaPreinversion.PERFIL,
            TipoEtapaPreinversion.EJECUCION);

    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository metaFisicaPapRepository;
    private final FuenteFinanciamientoEtapaPapRepository fuenteFinanciamientoPapRepository;
    private final EtapasOpinionTecnica etapasOpinionTecnica;
    private final SeleccionEtapasProyectos proyectos;
    private final SeleccionYRegistroDeEtapasMapper mapper;

    public SeleccionEtapasRegistro(EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository metaFisicaPapRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteFinanciamientoPapRepository,
            EtapasOpinionTecnica etapasOpinionTecnica,
            SeleccionEtapasProyectos proyectos, SeleccionYRegistroDeEtapasMapper mapper) {
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.metaFisicaPapRepository = metaFisicaPapRepository;
        this.fuenteFinanciamientoPapRepository = fuenteFinanciamientoPapRepository;
        this.etapasOpinionTecnica = etapasOpinionTecnica;
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
        return mapper.toDtoList(etapasConIniciales(proyectos.buscar(idProyecto)));
    }

    /** Etapas del proyecto en orden de ruta; si aún no tiene ninguna, crea antes las iniciales. */
    private List<EtapaPreinversion> etapasConIniciales(Proyecto proyecto) {
        List<EtapaPreinversion> etapas = enOrdenDeRuta(proyecto.getId());
        if (!etapas.isEmpty()) {
            return etapas;
        }
        List<TipoEtapaPreinversion> etapasIniciales = Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia())
                ? List.of(TipoEtapaPreinversion.PERFIL)
                : ETAPAS_INICIALES;
        sincronizar(proyecto, etapasIniciales);
        return enOrdenDeRuta(proyecto.getId());
    }

    /**
     * Registra costo y fechas de las etapas (botón único "Guardar"). RN04: fechas con formato de
     * calendario válido; RN23: fechas consistentes con el orden de la ruta.
     *
     * <p>Solo admite etapas de la ruta vigente. Una etapa bloqueada por RN13 es de solo lectura: sus
     * valores se ignoran (igual que el costo de EJECUCION) para que el cliente pueda reenviar la
     * tabla completa.
     *
     * @throws ConflictoEstadoException si alguna etapa no forma parte de la ruta del proyecto
     */
    public List<EtapaDto> actualizar(Long idProyecto, ActualizarEtapasRequestDto request) {
        Proyecto proyecto = proyectos.buscar(idProyecto);
        List<EtapaPreinversion> etapasDeLaRuta = etapasConIniciales(proyecto);

        RegistroFechasEtapas registro = new RegistroFechasEtapas();
        List<EtapaPreinversion> etapasTocadas = new ArrayList<>();
        for (EtapaRegistroRequestDto item : request.getEtapas()) {
            EtapaPreinversion etapa = etapaDeLaRuta(etapasDeLaRuta, item);
            if (!Boolean.TRUE.equals(etapa.getBloqueadaPorModificacion())) {
                registro.aplicar(etapa, item);
                etapasTocadas.add(etapa);
            }
        }
        registro.exigirFechasValidas();
        etapasTocadas.forEach(etapaPreinversionRepository::save);

        List<EtapaPreinversion> etapasActualizadas = enOrdenDeRuta(idProyecto);
        RegistroFechasEtapas.validarConsistencia(etapasActualizadas);
        return mapper.toDtoList(etapasActualizadas);
    }

    /**
     * Etapas del proyecto en el orden PERFIL/PREFACTIBILIDAD/FACTIBILIDAD/DISENO/EJECUCION de la
     * ruta. No se puede usar un {@code ORDER BY} sobre la columna TIPO_ETAPA en el repositorio: al
     * ser {@code @Enumerated(EnumType.STRING)}, eso ordenaría alfabéticamente (DISENO, EJECUCION,
     * FACTIBILIDAD, PERFIL, PREFACTIBILIDAD), no en el orden real de la ruta. Se ordena en memoria
     * por el ordinal del enum, que sí refleja ese orden de forma estable.
     *
     * <p>No incluye las etapas que una modificación dejó fuera de la ruta ({@code fueraDeRuta}).
     */
    public List<EtapaPreinversion> enOrdenDeRuta(Long idProyecto) {
        return etapaPreinversionRepository.findByProyectoId(idProyecto).stream()
                .filter((EtapaPreinversion etapa) -> !Boolean.TRUE.equals(etapa.getFueraDeRuta()))
                .sorted(Comparator.comparing(EtapaPreinversion::getTipoEtapa))
                .toList();
    }

    /**
     * Crea las filas de {@link EtapaPreinversion} que falten para reflejar la selección vigente.
     */
    public void sincronizar(Proyecto proyecto, Iterable<TipoEtapaPreinversion> seleccion) {
        for (TipoEtapaPreinversion tipoEtapa : seleccion) {
            if (etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(proyecto.getId(), tipoEtapa).isEmpty()) {
                etapaPreinversionRepository.save(nuevaEtapa(proyecto, tipoEtapa));
            }
        }
    }

    /**
     * Deja en Registro de Etapas exactamente la selección vigente, al aceptar o modificar la ruta.
     * <ul>
     * <li>Crea las etapas que falten. Una etapa que estaba fuera de la ruta y se vuelve a seleccionar
     * regresa con su información.</li>
     * <li>Una etapa que queda fuera se elimina, salvo que tenga Opinión Técnica o la espere por RN13:
     * entonces se marca {@code fueraDeRuta} y conserva su información.</li>
     * <li>Si una etapa que queda fuera no tiene Opinión Técnica pero sí programación cuatrimestral
     * (CU-PRE-30/31), se rechaza la operación completa: eliminarla borraría esa programación.</li>
     * <li>Por último aplica RN13 sobre la ruta resultante (ver
     * {@link EtapasOpinionTecnica#recalcularBloqueos}).</li>
     * </ul>
     *
     * @throws ConflictoEstadoException si una etapa que queda fuera tiene programación cuatrimestral
     */
    public void reemplazarSeleccion(Proyecto proyecto, Collection<TipoEtapaPreinversion> seleccion) {
        List<EtapaPreinversion> existentes = etapaPreinversionRepository.findByProyectoId(proyecto.getId());
        List<EtapaPreinversion> salenDeLaRuta = existentes.stream()
                .filter((EtapaPreinversion etapa) -> !seleccion.contains(etapa.getTipoEtapa()))
                .filter((EtapaPreinversion etapa) -> !Boolean.TRUE.equals(etapa.getFueraDeRuta()))
                .toList();

        List<String> conProgramacion = salenDeLaRuta.stream()
                .filter((EtapaPreinversion etapa) -> !seConserva(etapa))
                .filter(this::tieneProgramacionCuatrimestral)
                .map((EtapaPreinversion etapa) -> etapa.getTipoEtapa().name())
                .toList();
        if (!conProgramacion.isEmpty()) {
            throw new ConflictoEstadoException("ETAPA_CON_PROGRAMACION",
                    "No se puede quitar de la ruta las etapas " + String.join(", ", conProgramacion)
                            + " porque tienen programación cuatrimestral registrada (CU-PRE-30/31).");
        }

        for (EtapaPreinversion etapa : salenDeLaRuta) {
            if (seConserva(etapa)) {
                etapa.setFueraDeRuta(true);
                etapaPreinversionRepository.save(etapa);
            } else {
                etapaPreinversionRepository.delete(etapa);
            }
        }
        for (EtapaPreinversion etapa : existentes) {
            if (seleccion.contains(etapa.getTipoEtapa()) && Boolean.TRUE.equals(etapa.getFueraDeRuta())) {
                etapa.setFueraDeRuta(false);
                etapaPreinversionRepository.save(etapa);
            }
        }
        sincronizar(proyecto, seleccion);
        etapasOpinionTecnica.recalcularBloqueos(proyecto.getId());
    }

    /** Etapa que no se elimina al quedar fuera de la ruta: tiene OT o la espera por RN13. */
    private static boolean seConserva(EtapaPreinversion etapa) {
        return Boolean.TRUE.equals(etapa.getTieneOpinionTecnica())
                || Boolean.TRUE.equals(etapa.getBloqueadaPorModificacion());
    }

    private boolean tieneProgramacionCuatrimestral(EtapaPreinversion etapa) {
        return metaFisicaPapRepository.findByEtapaPreinversionId(etapa.getId()).isPresent()
                || !fuenteFinanciamientoPapRepository.findByEtapaPreinversionId(etapa.getId()).isEmpty();
    }

    private static EtapaPreinversion etapaDeLaRuta(List<EtapaPreinversion> etapasDeLaRuta,
            EtapaRegistroRequestDto item) {
        TipoEtapaPreinversion tipoEtapa = TipoEtapaPreinversion.valueOf(item.getNombreEtapa().name());
        return etapasDeLaRuta.stream()
                .filter((EtapaPreinversion etapa) -> etapa.getTipoEtapa() == tipoEtapa)
                .findFirst()
                .orElseThrow(() -> new ConflictoEstadoException("ETAPA_FUERA_DE_RUTA",
                        "La etapa " + tipoEtapa + " no forma parte de la Ruta de Preinversión del proyecto."));
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
