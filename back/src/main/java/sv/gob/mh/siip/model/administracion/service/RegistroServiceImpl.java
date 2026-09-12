package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActualizarRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoVigenciaDto;
import sv.gob.mh.siip.model.administracion.dto.InactivacionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ListaRegistrosResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroValoresResponseDto;
import sv.gob.mh.siip.model.administracion.dto.VigenciaDto;
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
    public RegistroResponseDto crear(String codigoCatalogo, CrearRegistroRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);

        Map<String, Object> valoresSolicitados = request.getValores();
        for (CampoDefinicion campo : catalogo.getCampos()) {
            if (!valoresSolicitados.containsKey(campo.getNombre())) {
                throw new ValidacionNegocioException("VALOR_CAMPO_REQUERIDO",
                        "Debe proveer un valor para cada campo definido en el catálogo, incluyendo el campo KEY.",
                        null);
            }
        }
        String nombreCampoKey = nombreCampoKey(catalogo);
        String clave = String.valueOf(valoresSolicitados.get(nombreCampoKey));
        if (registroRepository.existsByCatalogo_CodigoAndClave(codigoCatalogo, clave)) {
            throw new ConflictoEstadoException("El valor del campo KEY provisto ya existe en el catálogo.");
        }

        VigenciaDto vigencia = request.getVigencia();
        LocalDate fechaDesde = vigencia != null ? vigencia.getFechaDesde() : null;
        LocalDate fechaHasta = vigencia != null ? vigencia.getFechaHasta() : null;

        Registro registro = Registro.builder()
                .catalogo(catalogo)
                .clave(clave)
                .estado(CatalogoServiceImpl.calcularEstadoVigencia(fechaHasta))
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .build();
        valoresSolicitados.forEach((nombre, valor) -> registro.getValores().put(nombre, aTexto(valor)));

        Registro registroCreado = registroRepository.save(registro);
        return aRegistroResponse(registroCreado);
    }

    @Override
    @Transactional(readOnly = true)
    public ListaRegistrosResponseDto buscarLista(String codigoCatalogo, List<String> campos) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);
        List<Registro> registros = registroRepository.findByCatalogo_Codigo(codigoCatalogo);

        ListaRegistrosResponseDto response = new ListaRegistrosResponseDto()
                .catalogoCodigo(codigoCatalogo)
                .total(registros.size());
        registros.forEach(registro -> response.addRegistrosItem(aRegistroValoresResponse(catalogo, registro, campos)));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroValoresResponseDto buscarPorClave(String codigoCatalogo, String key, List<String> campos) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);
        Registro registro = obtenerRegistro(catalogo, key);
        validarCamposSolicitados(catalogo, campos);
        return aRegistroValoresResponse(catalogo, registro, campos);
    }

    @Override
    public RegistroResponseDto actualizar(String codigoCatalogo, String key, ActualizarRegistroRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);
        Registro registro = obtenerRegistro(catalogo, key);

        String nombreCampoKey = nombreCampoKey(catalogo);
        Map<String, Object> valoresSolicitados = request.getValores();
        if (valoresSolicitados.containsKey(nombreCampoKey)) {
            throw new ValidacionNegocioException("CAMPO_KEY_INMUTABLE",
                    "No se puede modificar el valor del campo KEY.", null);
        }
        valoresSolicitados.forEach((nombre, valor) -> registro.getValores().put(nombre, aTexto(valor)));

        Registro registroActualizado = registroRepository.save(registro);
        return aRegistroResponse(registroActualizado);
    }

    @Override
    public RegistroResponseDto inactivar(String codigoCatalogo, String key, InactivacionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = obtenerCatalogo(codigoCatalogo);
        Registro registro = obtenerRegistro(catalogo, key);

        LocalDate toDate = CatalogoServiceImpl.resolverToDate(request != null ? request.getToDate() : null);
        registro.setEstado(EstadoVigencia.INACTIVE);
        registro.setFechaHasta(toDate);

        registro = registroRepository.save(registro);
        return aRegistroResponse(registro);
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

    private static String aTexto(Object valor) {
        return valor == null ? null : String.valueOf(valor);
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
                .findFirst()
                .map(CampoDefinicion::getNombre)
                .orElse(null);
    }

    private static void validarCamposSolicitados(Catalogo catalogo, List<String> campos) {
        if (campos == null || campos.isEmpty()) {
            return;
        }
        List<String> nombresDefinidos = catalogo.getCampos().stream().map(CampoDefinicion::getNombre).toList();
        for (String campo : campos) {
            if (!nombresDefinidos.contains(campo)) {
                throw new ValidacionNegocioException("CAMPO_NO_EXISTE",
                        "Alguno de los nombres de campo solicitados no existe en el catálogo.", null);
            }
        }
    }

    private static RegistroResponseDto aRegistroResponse(Registro registro) {
        RegistroResponseDto response = new RegistroResponseDto()
                .catalogoCodigo(registro.getCatalogo().getCodigo())
                .key(registro.getClave())
                .estado(EstadoVigenciaDto.fromValue(registro.getEstado().name()))
                .vigencia(new VigenciaDto().fechaDesde(registro.getFechaDesde()).fechaHasta(registro.getFechaHasta()));
        registro.getValores().forEach(response::putValoresItem);
        return response;
    }

    /** Regla 11: si el catalogo esta INACTIVE, el registro se retorna siempre como INACTIVO. */
    private static RegistroValoresResponseDto aRegistroValoresResponse(Catalogo catalogo, Registro registro,
            List<String> campos) {
        EstadoVigenciaDto estadoEfectivo = catalogo.getEstado() == EstadoVigencia.INACTIVE
                ? EstadoVigenciaDto.INACTIVE
                : EstadoVigenciaDto.fromValue(registro.getEstado().name());

        RegistroValoresResponseDto response = new RegistroValoresResponseDto()
                .catalogoCodigo(catalogo.getCodigo())
                .key(registro.getClave())
                .estado(estadoEfectivo);

        if (campos == null || campos.isEmpty()) {
            String primerNoKey = primerCampoNoKeyNombre(catalogo);
            if (primerNoKey != null) {
                response.putValoresItem(primerNoKey, registro.getValores().get(primerNoKey));
            }
        } else {
            campos.forEach(campo -> response.putValoresItem(campo, registro.getValores().get(campo)));
        }
        return response;
    }
}
