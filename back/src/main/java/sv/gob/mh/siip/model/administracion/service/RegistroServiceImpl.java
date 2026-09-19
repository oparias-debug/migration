package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class RegistroServiceImpl implements RegistroService {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final ActorContexto actorContexto;

    public RegistroServiceImpl(CatalogoRepository catalogoRepository, RegistroRepository registroRepository,
            ActorContexto actorContexto) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    public CatalogRecordDto crear(String codigoCatalogo, CatalogRecordCreateRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);

        Map<String, String> valoresSolicitados = request.getValues();
        for (CampoDefinicion campo : catalogo.getCampos()) {
            if (!valoresSolicitados.containsKey(campo.getNombre())) {
                throw new ValidacionNegocioException("VALOR_CAMPO_REQUERIDO",
                        "Debe proveer un valor para cada campo definido en el catálogo, incluyendo el campo KEY.", null);
            }
        }
        String clave = valoresSolicitados.get(nombreCampoKey(catalogo));
        if (registroRepository.existsByCatalogo_CodigoAndClave(codigoCatalogo, clave)) {
            throw new ValidacionNegocioException("CLAVE_DUPLICADA",
                    "El valor del campo KEY provisto ya existe en el catálogo.", null);
        }

        LocalDate fechaDesde = request.getFromDate();
        LocalDate fechaHasta = request.getToDate();
        Registro registro = Registro.builder()
                .catalogo(catalogo)
                .clave(clave)
                .estado(request.getActive() != null ? EstadoVigencia.valueOf(request.getActive().getValue())
                        : CatalogoServiceImpl.calcularEstadoVigencia(fechaHasta))
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .build();
        registro.getValores().putAll(valoresSolicitados);

        return aCatalogRecordDtoCompleto(registroRepository.save(registro));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogRecordDto> buscarLista(String codigoCatalogo, List<String> campos, Pageable pageable) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);
        return registroRepository.findByCatalogo_Codigo(codigoCatalogo, pageable)
                .map(registro -> aCatalogRecordDtoFiltrado(catalogo, registro, campos));
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogRecordDto buscarPorClave(String codigoCatalogo, String key, List<String> campos) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);
        Registro registro = obtenerRegistro(catalogo, key);
        validarCamposSolicitados(catalogo, campos);
        return aCatalogRecordDtoFiltrado(catalogo, registro, campos);
    }

    @Override
    public CatalogRecordDto actualizar(String codigoCatalogo, String key, CatalogRecordUpdateRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);
        Registro registro = obtenerRegistro(catalogo, key);

        String nombreCampoKey = nombreCampoKey(catalogo);
        Map<String, String> valoresSolicitados = request.getValues();
        if (valoresSolicitados.containsKey(nombreCampoKey)) {
            throw new ValidacionNegocioException("CAMPO_KEY_INMUTABLE", "No se puede modificar el valor del campo KEY.",
                    null);
        }
        registro.getValores().putAll(valoresSolicitados);

        return aCatalogRecordDtoCompleto(registroRepository.save(registro));
    }

    @Override
    public void eliminar(String codigoCatalogo, String key) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        throw new ValidacionNegocioException("ELIMINACION_NO_PERMITIDA",
                "Un registro no puede eliminarse, solo inactivarse. Use POST /catalogos/{code}/registros/{key}/inactivacion.",
                null);
    }

    @Override
    public CatalogRecordDto inactivar(String codigoCatalogo, String key, InactivationRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);
        Registro registro = obtenerRegistro(catalogo, key);

        registro.setEstado(EstadoVigencia.INACTIVE);
        registro.setFechaHasta(CatalogoServiceImpl.resolverToDate(request != null ? request.getToDate() : null));

        return aCatalogRecordDtoCompleto(registroRepository.save(registro));
    }

    private Catalogo obtenerCatalogo(String codigoCatalogo) {
        return catalogoRepository.findByCodigo(codigoCatalogo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El catálogo indicado no existe."));
    }

    private Registro obtenerRegistro(Catalogo catalogo, String key) {
        return registroRepository.findByCatalogo_CodigoAndClave(catalogo.getCodigo(), key)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe ningún registro con la clave (KEY) provista en el catálogo."));
    }

    private static String nombreCampoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream()
                .filter(CampoDefinicion::isEsKey)
                .findFirst()
                .map(CampoDefinicion::getNombre)
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo " + catalogo.getCodigo() + " no tiene definido un campo KEY."));
    }

    private static String primerCampoNoKeyNombre(Catalogo catalogo) {
        return catalogo.getCampos().stream()
                .filter(campo -> !campo.isEsKey())
                .sorted(CatalogoServiceImpl.POR_POSICION)
                .findFirst()
                .map(CampoDefinicion::getNombre)
                .orElse(null);
    }

    /** Regla 4: cada nombre de campo solicitado debe existir en el catálogo, o 404. */
    private static void validarCamposSolicitados(Catalogo catalogo, List<String> campos) {
        if (campos == null || campos.isEmpty()) {
            return;
        }
        List<String> nombresDefinidos = catalogo.getCampos().stream().map(CampoDefinicion::getNombre).toList();
        for (String campo : campos) {
            if (!nombresDefinidos.contains(campo)) {
                throw new RecursoNoEncontradoException("Alguno de los nombres de campo solicitados no existe en el catálogo.");
            }
        }
    }

    private static CatalogRecordDto aCatalogRecordDtoCompleto(Registro registro) {
        CatalogRecordDto dto = new CatalogRecordDto()
                .key(registro.getClave())
                .active(ActiveStatusDto.fromValue(registro.getEstado().name()))
                .fromDate(registro.getFechaDesde())
                .toDate(registro.getFechaHasta());
        registro.getValores().forEach(dto::putValuesItem);
        return dto;
    }

    /** Regla 12: si el catálogo esta INACTIVE, el registro se retorna siempre como INACTIVE. */
    private static CatalogRecordDto aCatalogRecordDtoFiltrado(Catalogo catalogo, Registro registro, List<String> campos) {
        ActiveStatusDto estadoEfectivo = catalogo.getEstado() == EstadoVigencia.INACTIVE ? ActiveStatusDto.INACTIVE
                : ActiveStatusDto.fromValue(registro.getEstado().name());

        CatalogRecordDto dto = new CatalogRecordDto()
                .key(registro.getClave())
                .active(estadoEfectivo)
                .fromDate(registro.getFechaDesde())
                .toDate(registro.getFechaHasta());

        if (campos == null || campos.isEmpty()) {
            String primerNoKey = primerCampoNoKeyNombre(catalogo);
            if (primerNoKey != null) {
                dto.putValuesItem(primerNoKey, registro.getValores().get(primerNoKey));
            }
        } else {
            campos.forEach(campo -> dto.putValuesItem(campo, registro.getValores().get(campo)));
        }
        return dto;
    }
}
