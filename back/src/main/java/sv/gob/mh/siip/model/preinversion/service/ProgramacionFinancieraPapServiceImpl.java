package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.HabilitacionModificacionPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaListaPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.HabilitarModificacionesFueraPlazoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión". Un {@code idFuente}
 * ({@link FuenteFinanciamientoEtapaPap}) persiste a través de los años; el monto programado por
 * cuatrimestre de cada año vive en {@link ProgCuatrimestralFinanciera} (RN-B.a). "Ejecutado años
 * anteriores" (RN-B.c) se calcula como la suma histórica de los años previos al consultado, no se
 * almacena: no existe todavía un módulo de ejecución financiera real que lo alimente.
 */
@Service
@Transactional
public class ProgramacionFinancieraPapServiceImpl implements ProgramacionFinancieraPapService {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private static final RolUsuario[] ROLES_CONSULTA = {
            RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE,
            RolUsuario.COORDINADOR_PROGRAMACION, RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI
    };

    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final HabilitacionModificacionPapRepository habilitacionRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaFisicaPapRepository;
    private final ActorContexto actorContexto;

    public ProgramacionFinancieraPapServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            HabilitacionModificacionPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository,
            EtapaMetaFisicaPapRepository etapaMetaFisicaPapRepository, ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.habilitacionRepository = habilitacionRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.etapaMetaFisicaPapRepository = etapaMetaFisicaPapRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramacionFinancieraPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, String busqueda,
            Integer pagina, Integer tamanio) {
        Usuario actor = actorContexto.exigirRol(ROLES_CONSULTA);
        Long unidadFiltro = actor.getRol() == RolUsuario.TECNICO_URP
                ? actor.getUnidadEjecutora().getId()
                : idUnidadEjecutora;
        int anioEfectivo = anio != null ? anio : Year.now(ZONA_EL_SALVADOR).getValue();
        String termino = (busqueda == null || busqueda.isBlank()) ? null : "%" + busqueda.trim().toLowerCase() + "%";

        Pageable pageable = PageRequest.of(
                pagina != null && pagina >= 0 ? pagina : 0,
                tamanio != null && tamanio > 0 ? tamanio : 20);
        Page<FuenteFinanciamientoEtapaPap> resultado = fuenteRepository.buscar(unidadFiltro, termino, pageable);

        List<EstudioFilaListaPAPDto> contenido = resultado.getContent().stream()
                .map(fuente -> construirFilaListaDto(fuente, anioEfectivo))
                .toList();

        PaginacionMetadataDto paginacion = new PaginacionMetadataDto()
                .pagina(resultado.getNumber())
                .tamanio(resultado.getSize())
                .totalElementos(resultado.getTotalElements())
                .totalPaginas(resultado.getTotalPages());

        return new ProgramacionFinancieraPAPResponseDto(unidadFiltro, anioEfectivo, contenido, paginacion);
    }

    @Override
    public EstudioProgramacionPAPDto agregarEstudio(AgregarEstudioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());
        Proyecto proyecto = proyectoRepository.findByCup(request.getCup())
                .orElseThrow(() -> new RecursoNoEncontradoException("El CUP indicado no existe."));
        return construirEstudioDto(proyecto, request.getAnio());
    }

    @Override
    @Transactional(readOnly = true)
    public EstudioProgramacionPAPDto obtenerProgramacionEstudio(String cup, Integer anio) {
        actorContexto.exigirRol(ROLES_CONSULTA);
        Proyecto proyecto = buscarEstudio(cup);
        return construirEstudioDto(proyecto, anio);
    }

    @Override
    public EstudioProgramacionPAPDto guardarProgramacionEstudio(String cup, Integer anio,
            GuardarProgramacionEstudioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarEstudio(cup);
        verificarPeriodoAbierto(proyecto.getUnidadEjecutora().getId(), anio);

        List<EtapaPreinversion> etapasProgramables = etapasOrdenadas(proyecto.getId()).stream()
                .filter(etapa -> !estaFinalizada(etapa, anio))
                .toList();

        Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa = construirMapaPorEtapa(request.getEtapas());
        Set<TipoEtapaPreinversion> conProgramacion = determinarEtapasConProgramacion(etapasProgramables, porEtapa);

        if (conProgramacion.isEmpty()) {
            throw new ValidacionNegocioException("SIN_NINGUNA_ETAPA_PROGRAMADA",
                    "Es obligatorio registrar la programación de por lo menos una etapa.", null);
        }
        validarRutaCompleta(etapasProgramables, conProgramacion);

        // Validar montos contra el costo/monto pendiente ANTES de escribir nada (RN-B.c).
        validarMontosContraCosto(etapasProgramables, porEtapa, anio);

        // Persistir.
        persistirProgramacion(etapasProgramables, porEtapa, anio);

        return construirEstudioDto(proyecto, anio);
    }

    private Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> construirMapaPorEtapa(
            List<EtapaProgramacionRequestDto> etapas) {
        Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa = new EnumMap<>(TipoEtapaPreinversion.class);
        for (EtapaProgramacionRequestDto item : nullSafe(etapas)) {
            porEtapa.put(TipoEtapaPreinversion.valueOf(item.getEtapa().name()), item);
        }
        return porEtapa;
    }

    private Set<TipoEtapaPreinversion> determinarEtapasConProgramacion(List<EtapaPreinversion> etapasProgramables,
            Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa) {
        Set<TipoEtapaPreinversion> conProgramacion = new HashSet<>();
        for (EtapaPreinversion etapa : etapasProgramables) {
            boolean enEstaLlamada = tieneMontoPositivo(porEtapa.get(etapa.getTipoEtapa()));
            boolean yaTeniaHistorico = tieneHistoricoPositivo(etapa);
            if (enEstaLlamada || yaTeniaHistorico) {
                conProgramacion.add(etapa.getTipoEtapa());
            }
        }
        return conProgramacion;
    }

    /** RN-B.b: no se permite registrar una etapa posterior sin haber programado una etapa anterior de la Ruta. */
    private void validarRutaCompleta(List<EtapaPreinversion> etapasProgramables,
            Set<TipoEtapaPreinversion> conProgramacion) {
        boolean etapaFaltante = false;
        for (EtapaPreinversion etapa : etapasProgramables) {
            if (!conProgramacion.contains(etapa.getTipoEtapa())) {
                etapaFaltante = true;
            } else if (etapaFaltante) {
                throw new ValidacionNegocioException("RUTA_PREINVERSION_SALTEADA",
                        "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda.",
                        null);
            }
        }
    }

    /**
     * RN-B.c (c.1 estudios nuevos, c.2 estudios de arrastre): el "Total programado Año" no puede
     * superar el "Costo de la etapa" menos lo "Ejecutado años anteriores". El costo es un único valor
     * por etapa, por lo que se valida el AGREGADO de todas sus fuentes de financiamiento (botón "+" del
     * Anexo A.2), no cada fuente por separado: las filas enviadas reemplazan lo ya guardado para este
     * año en su propia fuente (no se cuenta dos veces) y las fuentes de la etapa que no vienen en la
     * solicitud aportan lo que ya tienen programado para el año.
     */
    private void validarMontosContraCosto(List<EtapaPreinversion> etapasProgramables,
            Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa, Integer anio) {
        for (EtapaPreinversion etapa : etapasProgramables) {
            EtapaProgramacionRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            Double costoEtapa = item.getCostoEtapa() != null ? item.getCostoEtapa() : etapa.getCosto();
            List<FilaFuenteProgramacionRequestDto> filas = nullSafe(item.getFuentes());
            List<FuenteFinanciamientoEtapaPap> fuentesEtapa = fuenteRepository.findByEtapaPreinversionId(etapa.getId());
            Set<Long> idsEnSolicitud = new HashSet<>();
            for (FilaFuenteProgramacionRequestDto fila : filas) {
                if (fila.getIdFuente() != null) {
                    idsEnSolicitud.add(fila.getIdFuente());
                }
            }
            List<FuenteFinanciamientoEtapaPap> fuentesNoEnviadas = fuentesEtapa.stream()
                    .filter(fuente -> !idsEnSolicitud.contains(fuente.getId()))
                    .toList();

            BigDecimal programadoAnioEtapa = CostoEtapaPapSupport
                    .sumarPorEtapa(filas, ProgramacionFinancieraPapServiceImpl::suma)
                    .add(CostoEtapaPapSupport.sumarPorEtapa(fuentesNoEnviadas,
                            fuente -> programadoEnAnio(fuente.getId(), anio)));
            BigDecimal ejecutadoAnteriorEtapa = CostoEtapaPapSupport.sumarPorEtapa(fuentesEtapa,
                    fuente -> ejecutadoAniosAnteriores(fuente.getId(), anio));

            if (CostoEtapaPapSupport.superaCostoEtapa(costoEtapa, programadoAnioEtapa.add(ejecutadoAnteriorEtapa))) {
                throw new ValidacionNegocioException("MONTO_SUPERA_COSTO_ETAPA",
                        "Monto Programado supera el costo de la etapa.", null);
            }
        }
    }

    /** Monto ya guardado como "Total programado Año" de una fuente para el año indicado. */
    private BigDecimal programadoEnAnio(Long idFuente, Integer anio) {
        return progRepository.findByFuenteIdAndAnio(idFuente, anio)
                .map(ProgCuatrimestralFinanciera::totalProgramadoAnio)
                .orElse(BigDecimal.ZERO);
    }

    private void persistirProgramacion(List<EtapaPreinversion> etapasProgramables,
            Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa, Integer anio) {
        for (EtapaPreinversion etapa : etapasProgramables) {
            EtapaProgramacionRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            if (item.getCostoEtapa() != null) {
                etapa.setCosto(item.getCostoEtapa());
                etapaPreinversionRepository.save(etapa);
            }
            for (FilaFuenteProgramacionRequestDto fila : nullSafe(item.getFuentes())) {
                guardarFila(etapa, fila, anio);
            }
        }
    }

    @Override
    public void desactivarEstudio(String cup, Integer anio) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarEstudio(cup);
        verificarPeriodoAbierto(proyecto.getUnidadEjecutora().getId(), anio);

        if (esArrastre(proyecto.getId(), anio)) {
            // [SUPUESTO] RN-D: sin un módulo de ejecución financiera/física real, se asume que
            // cualquier estudio de arrastre tiene programación pendiente de completar.
            throw new ConflictoEstadoException("ESTUDIO_ARRASTRE_INCOMPLETO",
                    "El estudio es de arrastre y no completó el 100% de lo programado física o financieramente en periodos anteriores.");
        }

        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository.findByEtapaPreinversionProyectoId(proyecto.getId());
        eliminarFuentes(fuentes);
        // SF-4 (CU-PRE-31): al desactivar el código, se desactiva también en la Programación de
        // Metas Físicas.
        sincronizarDesactivacionMetasFisicas(proyecto.getId());
    }

    @Override
    public void eliminarEtapaProgramacion(String cup, NombreEtapaDto etapaDto, Integer anio) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarEstudio(cup);
        EtapaPreinversion etapa = etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(proyecto.getId(), TipoEtapaPreinversion.valueOf(etapaDto.name()))
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudio o la etapa no existen."));

        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository.findByEtapaPreinversionId(etapa.getId());
        rechazarSiTieneEjecucionAnterior(fuentes, anio);
        eliminarFuentes(fuentes);
        // SF-5 (CU-PRE-31): al eliminar la etapa, se desactiva también en la Programación de Metas
        // Físicas.
        sincronizarEliminacionEtapaMetasFisicas(etapa.getId());
    }

    /**
     * SF-4 (CU-PRE-31): sincroniza la desactivación de un código con la Programación de Metas
     * Físicas. El documento dice "automáticamente se desactivará": se marca cada meta física como
     * inactiva (se conserva el registro y su programación cuatrimestral), no se borra.
     */
    private void sincronizarDesactivacionMetasFisicas(Long idProyecto) {
        List<EtapaMetaFisicaPap> etapasMeta = etapaMetaFisicaPapRepository.findByEtapaPreinversionProyectoId(idProyecto);
        etapasMeta.forEach(etapaMeta -> etapaMeta.setActivo(Boolean.FALSE));
        etapaMetaFisicaPapRepository.saveAll(etapasMeta);
    }

    /**
     * SF-5 (CU-PRE-31): al eliminar la etapa en CU-PRE-30, "automáticamente se desactivará en la
     * programación por Metas Físicas" — mismo criterio que SF-4 (marca inactiva, sin borrar).
     */
    private void sincronizarEliminacionEtapaMetasFisicas(Long idEtapaPreinversion) {
        etapaMetaFisicaPapRepository.findByEtapaPreinversionId(idEtapaPreinversion).ifPresent(etapaMeta -> {
            etapaMeta.setActivo(Boolean.FALSE);
            etapaMetaFisicaPapRepository.save(etapaMeta);
        });
    }

    @Override
    public void eliminarFuenteFinanciamiento(String cup, NombreEtapaDto etapaDto, Long idFuente, Integer anio) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarEstudio(cup);
        EtapaPreinversion etapa = etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(proyecto.getId(), TipoEtapaPreinversion.valueOf(etapaDto.name()))
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudio, la etapa o la fuente no existen."));

        FuenteFinanciamientoEtapaPap fuente = fuenteRepository.findById(idFuente)
                .filter(f -> f.getEtapaPreinversion().getId().equals(etapa.getId()))
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudio, la etapa o la fuente no existen."));

        rechazarSiTieneEjecucionAnterior(List.of(fuente), anio);
        eliminarFuentes(List.of(fuente));
    }

    @Override
    public void habilitarModificacionesFueraPlazo(HabilitarModificacionesFueraPlazoRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        HabilitacionModificacionPap habilitacion = habilitacionRepository
                .findByIdUnidadEjecutoraAndAnio(request.getIdUnidadEjecutora(), request.getAnio())
                .orElseGet(() -> HabilitacionModificacionPap.builder()
                        .idUnidadEjecutora(request.getIdUnidadEjecutora())
                        .anio(request.getAnio())
                        .build());
        habilitacion.setFechaHabilitacion(LocalDateTime.now(ZONA_EL_SALVADOR));
        habilitacionRepository.save(habilitacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource generarReporte(Long idUnidadEjecutora, Integer anio, String formato) {
        actorContexto.exigirRol(ROLES_CONSULTA);
        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository
                .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdOrderByEtapaPreinversion_Proyecto_CupAsc(idUnidadEjecutora);
        List<EstudioFilaListaPAPDto> filas = fuentes.stream()
                .map(fuente -> construirFilaListaDto(fuente, anio))
                .toList();

        byte[] contenido = "PDF".equalsIgnoreCase(formato)
                ? ReporteProgramacionPapGenerator.generarPdf(idUnidadEjecutora, anio, filas)
                : ReporteProgramacionPapGenerator.generarExcel(idUnidadEjecutora, anio, filas);
        return new ByteArrayResource(contenido);
    }

    // -----------------------------------------------------------------------------------------

    private Proyecto buscarEstudio(String cup) {
        Proyecto proyecto = proyectoRepository.findByCup(cup)
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudio (CUP + año) no existe."));
        if (etapaPreinversionRepository.findByProyectoId(proyecto.getId()).isEmpty()) {
            throw new RecursoNoEncontradoException("El estudio (CUP + año) no existe.");
        }
        return proyecto;
    }

    private List<EtapaPreinversion> etapasOrdenadas(Long idProyecto) {
        return etapaPreinversionRepository.findByProyectoId(idProyecto).stream()
                .sorted(Comparator.comparingInt(etapa -> etapa.getTipoEtapa().ordinal()))
                .toList();
    }

    /**
     * RN-B.e: una etapa ya finalizada física y financieramente en años ANTERIORES no se muestra.
     * Solo cuenta programación de años previos al consultado/guardado: de lo contrario, una etapa
     * desaparecería de su propia respuesta en el mismo guardado que la completa (RN-B.c permite
     * financiar el 100% del costo de la etapa en un único año).
     */
    private boolean estaFinalizada(EtapaPreinversion etapa, Integer anio) {
        if (etapa.getCosto() == null || etapa.getCosto() <= 0) {
            return false;
        }
        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository.findByEtapaPreinversionId(etapa.getId());
        if (fuentes.isEmpty()) {
            return false;
        }
        BigDecimal totalHistorico = BigDecimal.ZERO;
        for (FuenteFinanciamientoEtapaPap fuente : fuentes) {
            for (ProgCuatrimestralFinanciera prog : progRepository.findByFuenteId(fuente.getId())) {
                if (prog.getAnio() < anio) {
                    totalHistorico = totalHistorico.add(prog.totalProgramadoAnio());
                }
            }
        }
        return totalHistorico.compareTo(BigDecimal.valueOf(etapa.getCosto())) >= 0;
    }

    private boolean tieneHistoricoPositivo(EtapaPreinversion etapa) {
        for (FuenteFinanciamientoEtapaPap fuente : fuenteRepository.findByEtapaPreinversionId(etapa.getId())) {
            for (ProgCuatrimestralFinanciera prog : progRepository.findByFuenteId(fuente.getId())) {
                if (prog.totalProgramadoAnio().compareTo(BigDecimal.ZERO) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tieneMontoPositivo(EtapaProgramacionRequestDto item) {
        if (item == null) {
            return false;
        }
        for (FilaFuenteProgramacionRequestDto fila : nullSafe(item.getFuentes())) {
            if (suma(fila).compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }
        }
        return false;
    }

    /** RN-A.a/FB paso 2: un estudio es "de arrastre" si ya tuvo programación en un año anterior al consultado. */
    private boolean esArrastre(Long idProyecto, Integer anio) {
        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository.findByEtapaPreinversionProyectoId(idProyecto);
        if (fuentes.isEmpty()) {
            return false;
        }
        List<Long> idsFuente = fuentes.stream().map(FuenteFinanciamientoEtapaPap::getId).toList();
        return progRepository.findByFuenteIdIn(idsFuente).stream().anyMatch(prog -> prog.getAnio() < anio);
    }

    /** RN-B.c: "Ejecutado años anteriores" = suma histórica de todos los años previos al consultado. */
    private BigDecimal ejecutadoAniosAnteriores(Long idFuente, Integer anio) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgCuatrimestralFinanciera prog : progRepository.findByFuenteId(idFuente)) {
            if (prog.getAnio() < anio) {
                total = total.add(prog.totalProgramadoAnio());
            }
        }
        return total;
    }

    private void guardarFila(EtapaPreinversion etapa, FilaFuenteProgramacionRequestDto fila, Integer anio) {
        FuenteFinanciamientoEtapaPap fuente;
        Long idFuente = fila.getIdFuente();
        if (idFuente != null) {
            fuente = fuenteRepository.findById(idFuente)
                    .orElseThrow(() -> new RecursoNoEncontradoException("La fuente de financiamiento indicada no existe."));
            boolean bloqueada = ejecutadoAniosAnteriores(fuente.getId(), anio).compareTo(BigDecimal.ZERO) > 0;
            if (!bloqueada) {
                aplicarDatosFuente(fuente, fila);
            }
            // RN-B.a: en filas ya existentes de un estudio de arrastre, se ignora cualquier cambio
            // a fuenteFinanciamiento/fuenteRecursos/convenios (sin código de error especifico).
        } else {
            fuente = FuenteFinanciamientoEtapaPap.builder().etapaPreinversion(etapa).build();
            aplicarDatosFuente(fuente, fila);
        }
        FuenteFinanciamientoEtapaPap fuenteGuardada = fuenteRepository.save(fuente);

        ProgCuatrimestralFinanciera prog = progRepository.findByFuenteIdAndAnio(fuenteGuardada.getId(), anio)
                .orElseGet(() -> ProgCuatrimestralFinanciera.builder().fuente(fuenteGuardada).anio(anio).build());
        prog.setMontoCuatrimestre1(bd(fila.getMontoCuatrimestre1()));
        prog.setMontoCuatrimestre2(bd(fila.getMontoCuatrimestre2()));
        prog.setMontoCuatrimestre3(bd(fila.getMontoCuatrimestre3()));
        progRepository.save(prog);
    }

    private void aplicarDatosFuente(FuenteFinanciamientoEtapaPap fuente, FilaFuenteProgramacionRequestDto fila) {
        FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
        fuente.setFuenteFinanciamiento(
                fuenteFinanciamiento != null ? FuenteFinanciamiento.valueOf(fuenteFinanciamiento.name()) : null);
        fuente.setFuenteRecursos(fila.getFuenteRecursos());
        fuente.setConvenios(fila.getConvenios() != null ? new ArrayList<>(fila.getConvenios()) : new ArrayList<>());
    }

    private void rechazarSiTieneEjecucionAnterior(List<FuenteFinanciamientoEtapaPap> fuentes, Integer anio) {
        for (FuenteFinanciamientoEtapaPap fuente : fuentes) {
            if (ejecutadoAniosAnteriores(fuente.getId(), anio).compareTo(BigDecimal.ZERO) > 0) {
                throw new ConflictoEstadoException("EJECUCION_ANIOS_ANTERIORES",
                        "Etapa y/o Fuente de Financiamiento no puede ser eliminado, existe ejecución en años anteriores.");
            }
        }
    }

    private void eliminarFuentes(List<FuenteFinanciamientoEtapaPap> fuentes) {
        for (FuenteFinanciamientoEtapaPap fuente : fuentes) {
            progRepository.deleteByFuenteId(fuente.getId());
        }
        fuenteRepository.deleteAll(fuentes);
    }

    /**
     * RN-A.b: fuera de la fecha del Calendario de Eventos del PAP no se permite ingresar ni ajustar
     * datos, salvo habilitación de modificaciones fuera de plazo. [SUPUESTO] La especificación no
     * define qué ocurre si NO hay evento PROGRAMACION_PAP configurado para el año: se asume período
     * abierto (mismo criterio que CU-PRE-31/32/33) para no bloquear la elaboración del PAP mientras el
     * Administrador aún no registra el calendario. Documentado por el escenario "Sin evento de
     * calendario configurado para el año" de CU-PRE-30-bloqueo-fuera-calendario.feature.
     */
    private void verificarPeriodoAbierto(Long idUnidadEjecutora, Integer anio) {
        boolean habilitado = habilitacionRepository.findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio).isPresent();
        if (habilitado) {
            return;
        }
        boolean abierto = calendarioEventoRepository
                .findByTipoEventoAndAnioAndCuatrimestreIsNull(TipoEventoCalendario.PROGRAMACION_PAP, anio)
                .map(evento -> evento.getEstado() == EstadoCalendarioEvento.ABIERTO)
                .orElse(true);
        if (!abierto) {
            throw new ConflictoEstadoException("PERIODO_CERRADO", "Periodo de ingreso de información ha finalizado.");
        }
    }

    private EstudioProgramacionPAPDto construirEstudioDto(Proyecto proyecto, Integer anio) {
        boolean esArrastreEstudio = esArrastre(proyecto.getId(), anio);
        List<EtapaProgramacionDto> etapas = etapasOrdenadas(proyecto.getId()).stream()
                .filter(etapa -> !estaFinalizada(etapa, anio))
                .map(etapa -> construirEtapaDto(etapa, anio))
                .toList();
        return new EstudioProgramacionPAPDto(proyecto.getCup(), proyecto.getNombre(), esArrastreEstudio, etapas);
    }

    private EtapaProgramacionDto construirEtapaDto(EtapaPreinversion etapa, Integer anio) {
        List<FilaFuenteProgramacionDto> fuentes = fuenteRepository.findByEtapaPreinversionId(etapa.getId()).stream()
                .map(fuente -> construirFilaDto(fuente, etapa.getCosto(), anio))
                .toList();
        return new EtapaProgramacionDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()), fuentes)
                .costoEtapa(etapa.getCosto());
    }

    private FilaFuenteProgramacionDto construirFilaDto(FuenteFinanciamientoEtapaPap fuente, Double costoEtapa,
            Integer anio) {
        BigDecimal ejecutadoAnterior = ejecutadoAniosAnteriores(fuente.getId(), anio);
        ProgCuatrimestralFinanciera actual = progRepository.findByFuenteIdAndAnio(fuente.getId(), anio).orElse(null);
        BigDecimal m1 = actual != null ? actual.getMontoCuatrimestre1() : BigDecimal.ZERO;
        BigDecimal m2 = actual != null ? actual.getMontoCuatrimestre2() : BigDecimal.ZERO;
        BigDecimal m3 = actual != null ? actual.getMontoCuatrimestre3() : BigDecimal.ZERO;
        BigDecimal total = m1.add(m2).add(m3);

        BigDecimal aniosPosteriores = costoEtapa != null
                ? BigDecimal.valueOf(costoEtapa).subtract(ejecutadoAnterior).subtract(total)
                : null;

        FilaFuenteProgramacionDto dto = new FilaFuenteProgramacionDto(fuente.getId(), m1.doubleValue(), m2.doubleValue(),
                m3.doubleValue(), total.doubleValue())
                .fuenteFinanciamiento(fuente.getFuenteFinanciamiento() != null
                        ? FuenteFinanciamientoDto.valueOf(fuente.getFuenteFinanciamiento().name())
                        : null)
                .fuenteRecursos(fuente.getFuenteRecursos())
                .convenios(new ArrayList<>(fuente.getConvenios()))
                .ejecutadoAniosAnteriores(
                        ejecutadoAnterior.compareTo(BigDecimal.ZERO) > 0 ? ejecutadoAnterior.doubleValue() : null)
                .aniosPosteriores(
                        aniosPosteriores != null && aniosPosteriores.compareTo(BigDecimal.ZERO) > 0
                                ? aniosPosteriores.doubleValue()
                                : null);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            dto.porcentajeCuatrimestre1(porcentaje(m1, total));
            dto.porcentajeCuatrimestre2(porcentaje(m2, total));
            dto.porcentajeCuatrimestre3(porcentaje(m3, total));
        } else {
            dto.porcentajeCuatrimestre1(0d).porcentajeCuatrimestre2(0d).porcentajeCuatrimestre3(0d);
        }
        return dto;
    }

    private EstudioFilaListaPAPDto construirFilaListaDto(FuenteFinanciamientoEtapaPap fuente, Integer anio) {
        EtapaPreinversion etapa = fuente.getEtapaPreinversion();
        Proyecto proyecto = etapa.getProyecto();
        BigDecimal ejecutadoAnterior = ejecutadoAniosAnteriores(fuente.getId(), anio);
        ProgCuatrimestralFinanciera actual = progRepository.findByFuenteIdAndAnio(fuente.getId(), anio).orElse(null);
        BigDecimal m1 = actual != null ? actual.getMontoCuatrimestre1() : BigDecimal.ZERO;
        BigDecimal m2 = actual != null ? actual.getMontoCuatrimestre2() : BigDecimal.ZERO;
        BigDecimal m3 = actual != null ? actual.getMontoCuatrimestre3() : BigDecimal.ZERO;
        BigDecimal total = m1.add(m2).add(m3);
        BigDecimal aniosPosteriores = etapa.getCosto() != null
                ? BigDecimal.valueOf(etapa.getCosto()).subtract(ejecutadoAnterior).subtract(total)
                : null;

        return new EstudioFilaListaPAPDto(proyecto.getCup(), proyecto.getNombre(),
                NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()))
                .fuenteFinanciamiento(fuente.getFuenteFinanciamiento() != null
                        ? FuenteFinanciamientoDto.valueOf(fuente.getFuenteFinanciamiento().name())
                        : null)
                .costoEtapa(etapa.getCosto())
                .ejecutadoAniosAnteriores(
                        ejecutadoAnterior.compareTo(BigDecimal.ZERO) > 0 ? ejecutadoAnterior.doubleValue() : null)
                // Anexo A.1: columnas "Programación I, II y III Cuatrimestre" (monto de cada cuatrimestre del Anexo A.2).
                .montoCuatrimestre1(m1.doubleValue())
                .montoCuatrimestre2(m2.doubleValue())
                .montoCuatrimestre3(m3.doubleValue())
                .totalProgramadoAnio(total.doubleValue())
                .aniosPosteriores(
                        aniosPosteriores != null && aniosPosteriores.compareTo(BigDecimal.ZERO) > 0
                                ? aniosPosteriores.doubleValue()
                                : null);
    }

    private static BigDecimal suma(FilaFuenteProgramacionRequestDto fila) {
        return bd(fila.getMontoCuatrimestre1()).add(bd(fila.getMontoCuatrimestre2())).add(bd(fila.getMontoCuatrimestre3()));
    }

    private static BigDecimal bd(Double valor) {
        return valor != null ? BigDecimal.valueOf(valor) : BigDecimal.ZERO;
    }

    private static double porcentaje(BigDecimal monto, BigDecimal total) {
        return monto.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP).doubleValue();
    }

    private static <T> List<T> nullSafe(List<T> lista) {
        return lista != null ? lista : List.of();
    }
}
