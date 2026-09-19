package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogExistenceResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.enums.TipoCampo;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class CatalogoServiceImpl implements CatalogoService {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    /** Orden de despliegue de un CatalogField (Reglas 4 y 5): por `posicion`, nulls al final. */
    static final Comparator<CampoDefinicion> POR_POSICION = Comparator
            .comparing(CampoDefinicion::getPosicion, Comparator.nullsLast(Comparator.naturalOrder()));

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final ActorContexto actorContexto;

    public CatalogoServiceImpl(CatalogoRepository catalogoRepository, RegistroRepository registroRepository,
            ActorContexto actorContexto) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    public CatalogDto crear(CatalogCreateRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);

        List<CatalogFieldDto> camposSolicitados = request.getFields();
        if (camposSolicitados == null || camposSolicitados.isEmpty()) {
            throw new ValidacionNegocioException("CAMPOS_REQUERIDOS", "Debe existir al menos un campo definido.", null);
        }
        validarCampoKeyYNombresUnicos(camposSolicitados);
        exigirCatalogoPadreExistente(request.getParent());

        LocalDate fechaDesde = request.getFromDate();
        LocalDate fechaHasta = request.getToDate();

        Catalogo catalogo = Catalogo.builder()
                .codigo(request.getCode())
                .nombre(request.getName())
                .catalogoPadreCodigo(request.getParent())
                .estado(estadoSolicitadoOCalculado(request.getActive(), fechaHasta))
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .build();
        reemplazarCampos(catalogo, camposSolicitados);

        return aCatalogDto(catalogoRepository.save(catalogo));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogDto> listar(Pageable pageable) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        return catalogoRepository.findAll(pageable).map(CatalogoServiceImpl::aCatalogDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogExistenceResponseDto verificarExistencia(String nombre) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        return new CatalogExistenceResponseDto().name(nombre).exists(catalogoRepository.existsByNombreIgnoreCase(nombre));
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogDto consultar(String codigoCatalogo) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        return aCatalogDto(obtenerPorCodigo(codigoCatalogo));
    }

    @Override
    public CatalogDto actualizarDescriptores(String codigoCatalogo, CatalogDescriptorsUpdateRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerPorCodigo(codigoCatalogo);

        if (request.getName() == null && request.getParent() == null && request.getActive() == null
                && request.getFromDate() == null && request.getToDate() == null) {
            throw new ValidacionNegocioException("DESCRIPTOR_REQUERIDO", "Debe indicar al menos un descriptor a actualizar.",
                    null);
        }
        if (request.getName() != null) {
            catalogo.setNombre(request.getName());
        }
        if (request.getParent() != null) {
            exigirCatalogoPadreExistente(request.getParent());
            catalogo.setCatalogoPadreCodigo(request.getParent());
        }
        if (request.getActive() != null) {
            catalogo.setEstado(EstadoVigencia.valueOf(request.getActive().getValue()));
        }
        if (request.getFromDate() != null) {
            catalogo.setFechaDesde(request.getFromDate());
        }
        if (request.getToDate() != null) {
            catalogo.setFechaHasta(request.getToDate());
        }

        return aCatalogDto(catalogoRepository.save(catalogo));
    }

    @Override
    public void eliminar(String codigoCatalogo) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        throw new ValidacionNegocioException("ELIMINACION_NO_PERMITIDA",
                "Un catálogo no puede eliminarse, solo inactivarse. Use POST /catalogos/{code}/inactivacion.", null);
    }

    @Override
    public CatalogDto actualizarCampos(String codigoCatalogo, CatalogFieldsUpdateRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerPorCodigo(codigoCatalogo);

        if (registroRepository.existsByCatalogo_Codigo(codigoCatalogo)) {
            throw new ConflictoEstadoException("No se pueden modificar los campos de un catálogo que ya tiene registros.");
        }
        validarCampoKeyYNombresUnicos(request.getFields());
        reemplazarCampos(catalogo, request.getFields());

        return aCatalogDto(catalogoRepository.save(catalogo));
    }

    @Override
    public CatalogDto inactivar(String codigoCatalogo, InactivationRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerPorCodigo(codigoCatalogo);

        catalogo.setEstado(EstadoVigencia.INACTIVE);
        catalogo.setFechaHasta(resolverToDate(request != null ? request.getToDate() : null));

        return aCatalogDto(catalogoRepository.save(catalogo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogSummaryDto> consultarHijos(String codigoCatalogo) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        obtenerPorCodigo(codigoCatalogo);
        return catalogoRepository.findByCatalogoPadreCodigo(codigoCatalogo).stream()
                .map(hijo -> new CatalogSummaryDto().code(hijo.getCodigo()).name(hijo.getNombre()))
                .toList();
    }

    Catalogo obtenerPorCodigo(String codigoCatalogo) {
        return catalogoRepository.findByCodigo(codigoCatalogo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El catálogo indicado no existe."));
    }

    private void exigirCatalogoPadreExistente(String catalogoPadreCodigo) {
        if (catalogoPadreCodigo != null && !catalogoPadreCodigo.isBlank()
                && catalogoRepository.findByCodigo(catalogoPadreCodigo).isEmpty()) {
            throw new RecursoNoEncontradoException("El catálogo padre indicado no existe.");
        }
    }

    /** Reglas 2 (al menos un KEY) y 3 (nombres unicos), compartidas por crear y actualizarCampos. */
    private static void validarCampoKeyYNombresUnicos(List<CatalogFieldDto> campos) {
        boolean tieneKey = campos.stream().anyMatch(campo -> campo.getQualifier() == FieldQualifierDto.KEY);
        if (!tieneKey) {
            throw new ValidacionNegocioException("CAMPO_KEY_REQUERIDO", "Debe existir al menos un campo con calificador KEY.",
                    null);
        }
        long nombresUnicos = campos.stream().map(CatalogFieldDto::getName).distinct().count();
        if (nombresUnicos < campos.size()) {
            throw new ValidacionNegocioException("NOMBRES_CAMPO_REPETIDOS", "Los nombres de los campos no deben repetirse.",
                    null);
        }
    }

    private static void reemplazarCampos(Catalogo catalogo, List<CatalogFieldDto> camposSolicitados) {
        catalogo.getCampos().clear();
        for (int posicion = 0; posicion < camposSolicitados.size(); posicion++) {
            catalogo.getCampos().add(aCampoDefinicion(camposSolicitados.get(posicion), catalogo, posicion));
        }
    }

    /**
     * Regla 9b/14: sin toDate se asigna la fecha actual (9a); con toDate, debe ser
     * actual o pasada.
     */
    static LocalDate resolverToDate(LocalDate toDate) {
        LocalDate hoy = LocalDate.now(ZONA_EL_SALVADOR);
        if (toDate == null) {
            return hoy;
        }
        if (toDate.isAfter(hoy)) {
            throw new ValidacionNegocioException("TO_DATE_FUTURA",
                    "La fecha toDate provista es futura y no corresponde a una inactivación.", null);
        }
        return toDate;
    }

    /** Regla 13: sin active explicito, ACTIVE salvo que fromDate/toDate ya lo dejen en el pasado. */
    private static EstadoVigencia estadoSolicitadoOCalculado(ActiveStatusDto activeSolicitado, LocalDate fechaHasta) {
        return activeSolicitado != null ? EstadoVigencia.valueOf(activeSolicitado.getValue())
                : calcularEstadoVigencia(fechaHasta);
    }

    static EstadoVigencia calcularEstadoVigencia(LocalDate fechaHasta) {
        return (fechaHasta != null && fechaHasta.isBefore(LocalDate.now(ZONA_EL_SALVADOR))) ? EstadoVigencia.INACTIVE
                : EstadoVigencia.ACTIVE;
    }

    private static CampoDefinicion aCampoDefinicion(CatalogFieldDto dto, Catalogo catalogo, int posicionPorDefecto) {
        return CampoDefinicion.builder()
                .catalogo(catalogo)
                .nombre(dto.getName())
                .tipo(TipoCampo.STRING)
                .esKey(dto.getQualifier() == FieldQualifierDto.KEY)
                .posicion(dto.getPosition() != null ? dto.getPosition() : posicionPorDefecto)
                .build();
    }

    private static CatalogFieldDto aCatalogFieldDto(CampoDefinicion campo) {
        return new CatalogFieldDto()
                .name(campo.getNombre())
                .qualifier(campo.isEsKey() ? FieldQualifierDto.KEY : FieldQualifierDto.FIELD)
                .position(campo.getPosicion());
    }

    static CatalogDto aCatalogDto(Catalogo catalogo) {
        CatalogDto dto = new CatalogDto()
                .code(catalogo.getCodigo())
                .name(catalogo.getNombre())
                .parent(catalogo.getCatalogoPadreCodigo())
                .active(ActiveStatusDto.fromValue(catalogo.getEstado().name()))
                .fromDate(catalogo.getFechaDesde())
                .toDate(catalogo.getFechaHasta());
        catalogo.getCampos().stream().sorted(POR_POSICION).forEach(campo -> dto.addFieldsItem(aCatalogFieldDto(campo)));
        return dto;
    }
}
