package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActualizarCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CampoDefinicionDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogoHijoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoVigenciaDto;
import sv.gob.mh.siip.model.administracion.dto.ExistenciaCatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.InactivacionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCampoDto;
import sv.gob.mh.siip.model.administracion.dto.VigenciaDto;
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
    public CatalogoResponseDto crear(CrearCatalogoRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);

        List<CampoDefinicionDto> camposSolicitados = request.getCampos();
        if (camposSolicitados.isEmpty()) {
            throw new ValidacionNegocioException("CAMPOS_REQUERIDOS", "Debe existir al menos un campo definido.", null);
        }
        boolean tieneKey = camposSolicitados.stream().anyMatch(campo -> Boolean.TRUE.equals(campo.getEsKey()));
        if (!tieneKey) {
            throw new ValidacionNegocioException("CAMPO_KEY_REQUERIDO",
                    "Debe existir al menos un campo marcado como KEY.", null);
        }
        long nombresUnicos = camposSolicitados.stream().map(CampoDefinicionDto::getNombre).distinct().count();
        if (nombresUnicos < camposSolicitados.size()) {
            throw new ValidacionNegocioException("NOMBRES_CAMPO_REPETIDOS",
                    "Los nombres de los campos no deben repetirse.", null);
        }
        String catalogoPadreCodigo = request.getCatalogoPadreCodigo();
        exigirCatalogoPadreExistente(catalogoPadreCodigo);

        VigenciaDto vigencia = request.getVigencia();
        LocalDate fechaDesde = vigencia != null ? vigencia.getFechaDesde() : null;
        LocalDate fechaHasta = vigencia != null ? vigencia.getFechaHasta() : null;

        Catalogo catalogo = Catalogo.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .catalogoPadreCodigo(catalogoPadreCodigo)
                .estado(calcularEstadoVigencia(fechaHasta))
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .build();
        camposSolicitados.forEach(campoDto -> catalogo.getCampos().add(aCampoDefinicion(campoDto, catalogo)));

        Catalogo catalogoCreado = catalogoRepository.save(catalogo);
        return aCatalogoResponse(catalogoCreado, false);
    }

    @Override
    @Transactional(readOnly = true)
    public ExistenciaCatalogoResponseDto buscarPorNombre(String nombre) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        boolean existe = catalogoRepository.existsByNombre(nombre);
        return new ExistenciaCatalogoResponseDto(nombre, existe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoHijoResponseDto> buscarHijos(String codigoCatalogo) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        return catalogoRepository.findByCatalogoPadreCodigo(codigoCatalogo).stream()
                .map(hijo -> new CatalogoHijoResponseDto(hijo.getCodigo(), hijo.getNombre()))
                .toList();
    }

    @Override
    public CatalogoResponseDto actualizar(String codigoCatalogo, ActualizarCatalogoRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerPorCodigo(codigoCatalogo);

        List<CampoDefinicionDto> camposSolicitados = request.getCampos();
        if (camposSolicitados != null && !camposSolicitados.isEmpty()) {
            if (registroRepository.existsByCatalogo_Codigo(codigoCatalogo)) {
                throw new ConflictoEstadoException(
                        "No se pueden modificar los campos de un catálogo que ya tiene registros.");
            }
            catalogo.getCampos().clear();
            camposSolicitados.forEach(campoDto -> catalogo.getCampos().add(aCampoDefinicion(campoDto, catalogo)));
        }
        if (request.getNombre() != null) {
            catalogo.setNombre(request.getNombre());
        }
        if (request.getCatalogoPadreCodigo() != null) {
            exigirCatalogoPadreExistente(request.getCatalogoPadreCodigo());
            catalogo.setCatalogoPadreCodigo(request.getCatalogoPadreCodigo());
        }
        EstadoVigenciaDto estado = request.getEstado();
        if (estado != null) {
            catalogo.setEstado(EstadoVigencia.valueOf(estado.getValue()));
        }
        VigenciaDto vigencia = request.getVigencia();
        if (vigencia != null) {
            catalogo.setFechaDesde(vigencia.getFechaDesde());
            catalogo.setFechaHasta(vigencia.getFechaHasta());
        }

        Catalogo catalogoActualizado = catalogoRepository.save(catalogo);
        return aCatalogoResponse(catalogoActualizado, registroRepository.existsByCatalogo_Codigo(codigoCatalogo));
    }

    @Override
    public CatalogoResponseDto inactivar(String codigoCatalogo, InactivacionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerPorCodigo(codigoCatalogo);

        LocalDate toDate = resolverToDate(request != null ? request.getToDate() : null);
        catalogo.setEstado(EstadoVigencia.INACTIVE);
        catalogo.setFechaHasta(toDate);

        Catalogo catalogoInactivado = catalogoRepository.save(catalogo);
        return aCatalogoResponse(catalogoInactivado, registroRepository.existsByCatalogo_Codigo(codigoCatalogo));
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

    /**
     * Regla 8b: sin toDate se asigna la fecha actual (8a); con toDate, debe ser
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

    /**
     * Regla 13: TO DATE anterior a la fecha actual deja el catalogo/registro
     * INACTIVE desde su creacion.
     */
    static EstadoVigencia calcularEstadoVigencia(LocalDate fechaHasta) {
        return (fechaHasta != null && fechaHasta.isBefore(LocalDate.now(ZONA_EL_SALVADOR))) ? EstadoVigencia.INACTIVE
                : EstadoVigencia.ACTIVE;
    }

    private static CampoDefinicion aCampoDefinicion(CampoDefinicionDto dto, Catalogo catalogo) {
        CampoDefinicion campo = CampoDefinicion.builder()
                .catalogo(catalogo)
                .nombre(dto.getNombre())
                .tipo(TipoCampo.valueOf(dto.getTipo().getValue()))
                .esKey(Boolean.TRUE.equals(dto.getEsKey()))
                .build();
        if (dto.getValoresEnum() != null) {
            campo.getValoresEnum().addAll(dto.getValoresEnum());
        }
        return campo;
    }

    private static CampoDefinicionDto aCampoDefinicionDto(CampoDefinicion campo) {
        CampoDefinicionDto dto = new CampoDefinicionDto()
                .nombre(campo.getNombre())
                .tipo(TipoCampoDto.fromValue(campo.getTipo().name()))
                .esKey(campo.isEsKey());
        dto.setValoresEnum(new ArrayList<>(campo.getValoresEnum()));
        return dto;
    }

    private static CatalogoResponseDto aCatalogoResponse(Catalogo catalogo, boolean tieneRegistros) {
        CatalogoResponseDto response = new CatalogoResponseDto()
                .codigo(catalogo.getCodigo())
                .nombre(catalogo.getNombre())
                .catalogoPadreCodigo(catalogo.getCatalogoPadreCodigo())
                .estado(EstadoVigenciaDto.fromValue(catalogo.getEstado().name()))
                .vigencia(new VigenciaDto().fechaDesde(catalogo.getFechaDesde()).fechaHasta(catalogo.getFechaHasta()))
                .tieneRegistros(tieneRegistros);
        catalogo.getCampos().forEach(campo -> response.addCamposItem(aCampoDefinicionDto(campo)));
        return response;
    }
}
