package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.ComponenteCostoEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RutaPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ComplejidadProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ComponenteCostoDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaRegistroRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.TamanioProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCapitalDto;
import sv.gob.mh.siip.model.preinversion.enums.ComplejidadProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TamanioProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoCapital;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.mapper.SeleccionYRegistroDeEtapasMapper;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FichaEmergenciaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RutaPreinversionRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class SeleccionYRegistroDeEtapasServiceImpl implements SeleccionYRegistroDeEtapasService {

    private static final String CAMPO_OBLIGATORIO = "*Campo obligatorio";

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    /** Ruta con Diseño, sin Prefactibilidad/Factibilidad (Anexo B.2: "Perfil con Diseño Básico"/"Perfil + Diseño"). */
    private static final List<TipoEtapaPreinversion> RUTA_PERFIL_DISENO = List.of(TipoEtapaPreinversion.PERFIL,
            TipoEtapaPreinversion.DISENO, TipoEtapaPreinversion.EJECUCION);

    /** Ruta completa (Anexo B.2: "Perfil + Prefactibilidad + Factibilidad + Diseño"). */
    private static final List<TipoEtapaPreinversion> RUTA_COMPLETA = List.of(TipoEtapaPreinversion.PERFIL,
            TipoEtapaPreinversion.PREFACTIBILIDAD, TipoEtapaPreinversion.FACTIBILIDAD,
            TipoEtapaPreinversion.DISENO, TipoEtapaPreinversion.EJECUCION);

    /** RN07/RN08: Programa/Estudio General usan siempre PERFIL+EJECUCION, preseleccionadas, sin criterios. */
    private static final List<TipoEtapaPreinversion> RUTA_PROGRAMA_ESTUDIO = List.of(TipoEtapaPreinversion.PERFIL,
            TipoEtapaPreinversion.EJECUCION);

    private final ProyectoRepository proyectoRepository;
    private final RutaPreinversionRepository rutaPreinversionRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final FichaEmergenciaRepository fichaEmergenciaRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository;
    private final ActorContexto actorContexto;
    private final ProyectoMapper proyectoMapper;
    private final SeleccionYRegistroDeEtapasMapper mapper;

    public SeleccionYRegistroDeEtapasServiceImpl(ProyectoRepository proyectoRepository,
            RutaPreinversionRepository rutaPreinversionRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            FichaEmergenciaRepository fichaEmergenciaRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository,
            ActorContexto actorContexto, ProyectoMapper proyectoMapper,
            SeleccionYRegistroDeEtapasMapper mapper) {
        this.proyectoRepository = proyectoRepository;
        this.rutaPreinversionRepository = rutaPreinversionRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.fichaEmergenciaRepository = fichaEmergenciaRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.productoIndicadorCatalogoRepository = productoIndicadorCatalogoRepository;
        this.actorContexto = actorContexto;
        this.proyectoMapper = proyectoMapper;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public RutaPreinversionDto obtenerRutaPreinversion(Long idProyecto) {
        actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        return construirRutaDto(proyecto);
    }

    @Override
    @Transactional(readOnly = true)
    public RutaPreinversionSugeridaDto generarRutaPreinversion(Long idProyecto, CriteriosCalificacionDto criterios) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        if (proyecto.getIniciativaInversion() != IniciativaInversion.PROYECTO) {
            throw new ConflictoEstadoException(
                    "El botón \"Ruta de Preinversión\" está desactivado para proyectos que no son de iniciativa PROYECTO (RN07/RN08).");
        }
        List<TipoEtapaPreinversion> sugeridas = calcularEtapasSugeridas(criterios);
        return new RutaPreinversionSugeridaDto()
                .criterios(criterios)
                .etapasSugeridas(sugeridas.stream().map(this::aNombreEtapaDto).toList());
    }

    @Override
    public RutaPreinversionDto aceptarRutaPreinversion(Long idProyecto, CriteriosCalificacionDto criterios) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);

        List<TipoEtapaPreinversion> etapas = proyecto.getIniciativaInversion() == IniciativaInversion.PROYECTO
                ? calcularEtapasSugeridas(criterios)
                : RUTA_PROGRAMA_ESTUDIO;

        RutaPreinversion ruta = obtenerOCrearRuta(proyecto);
        if (proyecto.getIniciativaInversion() == IniciativaInversion.PROYECTO) {
            ruta.setTipoCapital(TipoCapital.valueOf(criterios.getTipoCapital().name()));
            ruta.setTamanioProyecto(TamanioProyecto.valueOf(criterios.getTamanioProyecto().name()));
            ruta.setComplejidad(ComplejidadProyecto.valueOf(criterios.getComplejidad().name()));
        }
        ruta.setFueModificada(false);
        ruta.setJustificacionUltimaModificacion(null);
        rutaPreinversionRepository.save(ruta);

        sincronizarEtapas(proyecto, etapas, false);

        return construirRutaDto(proyecto);
    }

    @Override
    public RutaPreinversionDto modificarRutaPreinversion(Long idProyecto, ModificarRutaPreinversionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);

        List<TipoEtapaPreinversion> nuevaSeleccion = request.getEtapas().stream()
                .map(dto -> TipoEtapaPreinversion.valueOf(dto.name()))
                .toList();

        RutaPreinversion ruta = obtenerOCrearRuta(proyecto);
        ruta.setFueModificada(true);
        ruta.setJustificacionUltimaModificacion(request.getJustificacion());
        rutaPreinversionRepository.save(ruta);

        // RN13: una etapa ya emitida (tieneOpinionTecnica) que queda fuera de la nueva seleccion no
        // se elimina ni pierde su informacion; se marca bloqueadaPorModificacion en vez de rechazar
        // la operacion completa.
        sincronizarEtapas(proyecto, nuevaSeleccion, true);

        return construirRutaDto(proyecto);
    }

    @Override
    public List<EtapaDto> listarEtapas(Long idProyecto) {
        actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        List<EtapaPreinversion> etapas = etapaPreinversionRepository.findByProyectoIdOrderByTipoEtapaAsc(idProyecto);

        // RN09: PERFIL y EJECUCION estan habilitadas desde el inicio, para cualquier iniciativa,
        // sin esperar a generarRutaPreinversion/aceptarRutaPreinversion (RN07/RN08: para Programa/
        // Estudio General esas dos son ademas las unicas etapas de toda la ruta). Excepcion: un
        // proyecto de emergencia solo muestra PERFIL en Registro de Etapas (EJECUCION es su
        // "etapaFutura" tras pasar por Viabilidad, ver FichaEmergenciaDto/obtenerFichaEmergencia).
        if (etapas.isEmpty()) {
            List<TipoEtapaPreinversion> etapasIniciales = Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia())
                    ? List.of(TipoEtapaPreinversion.PERFIL)
                    : RUTA_PROGRAMA_ESTUDIO;
            sincronizarEtapas(proyecto, etapasIniciales, false);
            etapas = etapaPreinversionRepository.findByProyectoIdOrderByTipoEtapaAsc(idProyecto);
        }
        return mapper.toDtoList(etapas);
    }

    @Override
    public List<EtapaDto> actualizarEtapas(Long idProyecto, ActualizarEtapasRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);

        for (EtapaRegistroRequestDto item : request.getEtapas()) {
            TipoEtapaPreinversion tipoEtapa = TipoEtapaPreinversion.valueOf(item.getNombreEtapa().name());
            EtapaPreinversion etapa = etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(idProyecto, tipoEtapa)
                    .orElseGet(() -> nuevaEtapa(proyecto, tipoEtapa));

            // RN05/RN11: el costo de EJECUCION lo fija el Sistema (Presupuesto de inversion u
            // Opinion Tecnica mas reciente); cualquier valor enviado por el cliente se ignora.
            if (tipoEtapa != TipoEtapaPreinversion.EJECUCION) {
                etapa.setCosto(item.getCosto());
            }
            etapa.setFechaInicio(item.getFechaInicio());
            etapa.setFechaFin(item.getFechaFin());
            if (item.getFechaInicio() != null && item.getFechaFin() != null) {
                etapa.setHabilitadoParaRegistro(true);
            }
            etapaPreinversionRepository.save(etapa);
        }

        return mapper.toDtoList(etapaPreinversionRepository.findByProyectoIdOrderByTipoEtapaAsc(idProyecto));
    }

    @Override
    @Transactional(readOnly = true)
    public FichaInformacionGeneralDto obtenerFichaInformacionGeneral(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.COORDINADOR_SYMP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        return construirFichaInformacionGeneral(proyecto);
    }

    @Override
    public FichaInformacionGeneralDto seleccionarCoEjecutor(Long idProyecto, SeleccionCoEjecutorRequestDto request) {
        actorContexto.exigirRol(RolUsuario.COORDINADOR_SYMP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        UnidadEjecutora coEjecutor = unidadEjecutoraRepository.findById(request.getIdUnidadEjecutoraCoEjecutora())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La Unidad Ejecutora " + request.getIdUnidadEjecutoraCoEjecutora() + " no existe."));
        proyecto.setUnidadEjecutoraCoEjecutora(coEjecutor);
        proyectoRepository.save(proyecto);
        return construirFichaInformacionGeneral(proyecto);
    }

    @Override
    @Transactional(readOnly = true)
    public FichaEmergenciaDto obtenerFichaEmergencia(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyectoDeEmergencia(idProyecto);
        FichaEmergencia ficha = fichaEmergenciaRepository.findByProyectoId(idProyecto).orElse(null);
        return construirFichaEmergenciaDto(proyecto, ficha);
    }

    @Override
    public FichaEmergenciaDto registrarFichaEmergencia(Long idProyecto, FichaEmergenciaRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyectoDeEmergencia(idProyecto);

        List<ErrorDetalleDto> detalles = new ArrayList<>();
        if (request.getPlanteamientoProblema() == null || request.getPlanteamientoProblema().isBlank()) {
            detalles.add(new ErrorDetalleDto().campo("planteamientoProblema").mensaje(CAMPO_OBLIGATORIO));
        }
        if (request.getProductos() == null || request.getProductos().isEmpty()) {
            detalles.add(new ErrorDetalleDto().campo("productos").mensaje(CAMPO_OBLIGATORIO));
        }
        if (request.getDistrito() == null || request.getDistrito().isBlank()) {
            detalles.add(new ErrorDetalleDto().campo("distrito").mensaje(CAMPO_OBLIGATORIO));
        }
        if (request.getPoblacionObjetivo() == null || request.getPoblacionObjetivo().isBlank()) {
            detalles.add(new ErrorDetalleDto().campo("poblacionObjetivo").mensaje(CAMPO_OBLIGATORIO));
        }
        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException("Existen campos sin diligenciar", detalles);
        }

        FichaEmergencia ficha = fichaEmergenciaRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> FichaEmergencia.builder().proyecto(proyecto).build());

        ficha.setPlanteamientoProblema(request.getPlanteamientoProblema());
        ficha.setObjetivoGeneral(request.getObjetivoGeneral());
        ficha.setDescripcionProyecto(request.getDescripcionProyecto());
        ficha.setProductos(request.getProductos().stream().map(ProductoSeleccionadoDto::getCodigoProducto).toList());
        ficha.setDistrito(request.getDistrito());
        ficha.setLatitud(request.getLatitud());
        ficha.setLongitud(request.getLongitud());
        ficha.setDireccionEspecifica(request.getDireccionEspecifica());
        ficha.setPoblacionObjetivo(request.getPoblacionObjetivo());
        ficha.setInversionEstimada(request.getInversionEstimada());
        ficha.setArchivoPresupuestoUrl(request.getArchivoPresupuestoUrl());
        ficha.setComponentesCosto(request.getComponentesCosto().stream()
                .map(c -> new ComponenteCostoEmergencia(c.getTipoCosto(), c.getCosto()))
                .toList());
        ficha.setCostosOperacion(request.getCostosOperacion());
        ficha.setCostosMantenimiento(request.getCostosMantenimiento());
        ficha.setFuentesFinanciamiento(
                request.getFuentesFinanciamiento().stream().map(f -> FuenteFinanciamiento.valueOf(f.name())).toList());
        ficha.setFuenteRecursos(request.getFuenteRecursos());
        ficha.setArchivoProgramacionUrl(request.getArchivoProgramacionUrl());
        fichaEmergenciaRepository.save(ficha);

        // FA-05, paso 5.5: al guardar con exito, el proyecto se remite a Viabilidad (CU-PRE-24).
        proyecto.setEstado(EstadoProyecto.EN_VIABILIDAD);
        proyectoRepository.save(proyecto);

        return construirFichaEmergenciaDto(proyecto, ficha);
    }

    // -----------------------------------------------------------------------------------------

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    private Proyecto buscarProyectoDeEmergencia(Long idProyecto) {
        Proyecto proyecto = buscarProyecto(idProyecto);
        if (!Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia())) {
            throw new RecursoNoEncontradoException(
                    "El proyecto " + idProyecto + " no existe o no está categorizado como de emergencia.");
        }
        return proyecto;
    }

    private RutaPreinversion obtenerOCrearRuta(Proyecto proyecto) {
        return rutaPreinversionRepository.findByProyectoId(proyecto.getId())
                .orElseGet(() -> RutaPreinversion.builder().proyecto(proyecto).build());
    }

    /**
     * RN10 (Anexo B.2, matriz confirmada v1.3): solo el "Tipo de capital" y, cuando es Capital
     * Físico, el "Tamaño" y la "Complejidad" alteran la ruta sugerida — Capital Humano/
     * Institucional/Otros capitales siempre sugieren únicamente Perfil, sin importar Tamaño o
     * Complejidad. Ejecución no aparece en la matriz porque, junto con Perfil, está siempre
     * habilitada por defecto (RN09); se agrega aquí para que la sugerencia refleje la ruta
     * completa que terminará en Registro de Etapas.
     */
    private List<TipoEtapaPreinversion> calcularEtapasSugeridas(CriteriosCalificacionDto criterios) {
        if (criterios.getTipoCapital() != TipoCapitalDto.CAPITAL_FISICO) {
            return RUTA_PROGRAMA_ESTUDIO;
        }
        if (criterios.getTamanioProyecto() == TamanioProyectoDto.PEQUENIO) {
            return RUTA_PERFIL_DISENO;
        }
        if (criterios.getTamanioProyecto() == TamanioProyectoDto.MEDIANO
                && criterios.getComplejidad() == ComplejidadProyectoDto.BAJA) {
            return RUTA_PERFIL_DISENO;
        }
        return RUTA_COMPLETA;
    }

    /** Crea/actualiza las filas de EtapaPreinversion para reflejar la seleccion vigente. */
    private void sincronizarEtapas(Proyecto proyecto, List<TipoEtapaPreinversion> seleccion,
            boolean bloquearEmitidasFueraDeSeleccion) {
        List<EtapaPreinversion> existentes = etapaPreinversionRepository.findByProyectoIdOrderByTipoEtapaAsc(proyecto.getId());

        if (bloquearEmitidasFueraDeSeleccion) {
            for (EtapaPreinversion existente : existentes) {
                if (!seleccion.contains(existente.getTipoEtapa()) && Boolean.TRUE.equals(existente.getTieneOpinionTecnica())) {
                    existente.setBloqueadaPorModificacion(true);
                    etapaPreinversionRepository.save(existente);
                }
            }
        }

        for (TipoEtapaPreinversion tipoEtapa : seleccion) {
            if (etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(proyecto.getId(), tipoEtapa).isEmpty()) {
                etapaPreinversionRepository.save(nuevaEtapa(proyecto, tipoEtapa));
            }
        }
    }

    private EtapaPreinversion nuevaEtapa(Proyecto proyecto, TipoEtapaPreinversion tipoEtapa) {
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

    private RutaPreinversionDto construirRutaDto(Proyecto proyecto) {
        RutaPreinversion ruta = rutaPreinversionRepository.findByProyectoId(proyecto.getId()).orElse(null);
        List<NombreEtapaDto> etapasAceptadas = etapaPreinversionRepository
                .findByProyectoIdOrderByTipoEtapaAsc(proyecto.getId()).stream()
                .map(e -> aNombreEtapaDto(e.getTipoEtapa()))
                .toList();

        RutaPreinversionDto dto = new RutaPreinversionDto()
                .idProyecto(proyecto.getId())
                .etapasAceptadas(etapasAceptadas)
                .fueModificada(ruta != null && Boolean.TRUE.equals(ruta.getFueModificada()));

        if (ruta != null) {
            dto.setJustificacionUltimaModificacion(ruta.getJustificacionUltimaModificacion());
            if (ruta.getTipoCapital() != null && ruta.getTamanioProyecto() != null && ruta.getComplejidad() != null) {
                dto.setCriterios(new CriteriosCalificacionDto()
                        .tipoCapital(TipoCapitalDto.valueOf(ruta.getTipoCapital().name()))
                        .tamanioProyecto(TamanioProyectoDto.valueOf(ruta.getTamanioProyecto().name()))
                        .complejidad(ComplejidadProyectoDto.valueOf(ruta.getComplejidad().name())));
            }
        }
        return dto;
    }

    private FichaInformacionGeneralDto construirFichaInformacionGeneral(Proyecto proyecto) {
        boolean esGrdGrcAcc = !proyecto.getMedidasGrd().isEmpty() || !proyecto.getMedidasGrc().isEmpty()
                || !proyecto.getMedidasAcc().isEmpty();

        FichaInformacionGeneralDto dto = new FichaInformacionGeneralDto()
                .idProyecto(proyecto.getId())
                .institucion(proyectoMapper.toResumen(proyecto.getInstitucion()))
                .unidadEjecutora(proyectoMapper.toResumen(proyecto.getUnidadEjecutora()))
                .iniciativaInversion(IniciativaInversionDto.valueOf(proyecto.getIniciativaInversion().name()))
                .nombreProyecto(proyecto.getNombre())
                .montoEstimadoInversion(proyecto.getMontoEstimadoInversion())
                .sector(proyectoMapper.toResumen(proyecto.getSector()))
                .ejeTematico(proyectoMapper.toResumen(proyecto.getEjeTematico()))
                .esProyectoGrdGrcAcc(esGrdGrcAcc)
                .esProyectoEmergencia(Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia()))
                .tipoEvento(proyecto.getTipoEvento())
                .numeroDecretoLegislativo(proyecto.getNumeroDecretoLegislativo())
                .descripcionProyecto(proyecto.getDescripcionProyecto());

        if (proyecto.getUnidadEjecutoraCoEjecutora() != null) {
            dto.setCoEjecutor(proyectoMapper.toResumen(proyecto.getUnidadEjecutoraCoEjecutora()));
        }
        if (proyecto.getEjePlanGobierno() != null) {
            dto.setEjePlanGobierno(proyectoMapper.toResumen(proyecto.getEjePlanGobierno()));
        }
        if (proyecto.getPlanSectorialRegional() != null) {
            dto.setPlanSectorialRegional(proyectoMapper.toResumen(proyecto.getPlanSectorialRegional()));
        }
        // RN17: solo no nulo en etapa de Ejecucion. RN18 ("se alimenta automaticamente") no tiene
        // todavia una fuente propia distinta de este mismo monto (ningun otro CU la implementa
        // aun); se proyecta el monto estimado como valor provisional mientras eso se define.
        if (proyecto.getEstado() == EstadoProyecto.EN_EJECUCION) {
            dto.setMontoAjustadoEjecucion(proyecto.getMontoEstimadoInversion());
        }
        // RN15: objetivoProyecto/montoEstimadoInversion/descripcionProyecto se actualizarian segun
        // la ultima Opinion Tecnica (CU-PRE-04/17/11); ninguno de esos CU esta implementado todavia,
        // asi que se devuelven los valores originales de CU-PRE-01 (objetivoProyecto queda nulo).
        return dto;
    }

    private FichaEmergenciaDto construirFichaEmergenciaDto(Proyecto proyecto, FichaEmergencia ficha) {
        FichaEmergenciaDto dto = new FichaEmergenciaDto()
                .cup(proyecto.getCup())
                .nombreProyecto(proyecto.getNombre())
                .etapaActual(NombreEtapaDto.PERFIL)
                .etapaFutura(NombreEtapaDto.EJECUCION)
                .numeroDecretoLegislativo(proyecto.getNumeroDecretoLegislativo())
                .tipoEvento(proyecto.getTipoEvento());

        if (ficha == null) {
            return dto;
        }

        double totalComponentesCosto = ficha.getComponentesCosto().stream()
                .mapToDouble(ComponenteCostoEmergencia::getCosto)
                .sum();

        return dto
                .planteamientoProblema(ficha.getPlanteamientoProblema())
                .objetivoGeneral(ficha.getObjetivoGeneral())
                .descripcionProyecto(ficha.getDescripcionProyecto())
                .productos(resolverProductos(ficha.getProductos()))
                .departamento(null)
                .distrito(ficha.getDistrito())
                .latitud(ficha.getLatitud())
                .longitud(ficha.getLongitud())
                .direccionEspecifica(ficha.getDireccionEspecifica())
                .poblacionObjetivo(ficha.getPoblacionObjetivo())
                .inversionEstimada(ficha.getInversionEstimada())
                .archivoPresupuestoUrl(ficha.getArchivoPresupuestoUrl())
                .componentesCosto(ficha.getComponentesCosto().stream()
                        .map(c -> new ComponenteCostoDto().tipoCosto(c.getTipoCosto()).costo(c.getCosto()))
                        .toList())
                .totalComponentesCosto(ficha.getComponentesCosto().isEmpty() ? null : totalComponentesCosto)
                .costosOperacion(ficha.getCostosOperacion())
                .costosMantenimiento(ficha.getCostosMantenimiento())
                .fuentesFinanciamiento(ficha.getFuentesFinanciamiento().stream()
                        .map(f -> FuenteFinanciamientoDto.valueOf(f.name())).toList())
                .fuenteRecursos(ficha.getFuenteRecursos())
                .archivoProgramacionUrl(ficha.getArchivoProgramacionUrl());
    }

    private List<ProductoSeleccionadoDto> resolverProductos(List<String> codigosProducto) {
        if (codigosProducto.isEmpty()) {
            return List.of();
        }
        var catalogo = productoIndicadorCatalogoRepository.findByCodigoProductoIn(codigosProducto);
        return codigosProducto.stream()
                .map(codigo -> {
                    ProductoSeleccionadoDto dto = new ProductoSeleccionadoDto().codigoProducto(codigo);
                    catalogo.stream().filter(p -> p.getCodigoProducto().equals(codigo)).findFirst()
                            .ifPresent(p -> dto.setProducto(p.getProducto()));
                    return dto;
                })
                .toList();
    }

    private NombreEtapaDto aNombreEtapaDto(TipoEtapaPreinversion tipoEtapa) {
        return NombreEtapaDto.valueOf(tipoEtapa.name());
    }
}
