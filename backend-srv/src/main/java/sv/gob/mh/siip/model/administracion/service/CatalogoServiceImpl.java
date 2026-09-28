package sv.gob.mh.siip.model.administracion.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogExistenceResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-ADM-01 (Gestion de Catalogos): gestion de catalogos, restringida a ADMINISTRADOR_DE_CATALOGOS. La
 * logica vive en {@link CatalogoAltas} (alta y definicion de campos), {@link CatalogoEdicion}
 * (descriptores, inactivacion y eliminacion) y {@link CatalogoConsultas}; las reglas compartidas con los
 * registros estan en {@link CatalogoReglas}. Esta clase mantiene el control transaccional.
 */
@Service
@Transactional
public class CatalogoServiceImpl implements CatalogoService {

    private final CatalogoAltas altas;
    private final CatalogoEdicion edicion;
    private final CatalogoConsultas consultas;

    public CatalogoServiceImpl(CatalogoRepository catalogoRepository, RegistroRepository registroRepository,
            ActorContexto actorContexto) {
        CatalogoBusqueda busqueda = new CatalogoBusqueda(catalogoRepository);
        this.altas = new CatalogoAltas(actorContexto, catalogoRepository, registroRepository, busqueda);
        this.edicion = new CatalogoEdicion(actorContexto, catalogoRepository, busqueda);
        this.consultas = new CatalogoConsultas(actorContexto, catalogoRepository, busqueda);
    }

    @Override
    public CatalogDto crear(CatalogCreateRequestDto request) {
        return altas.crear(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogDto> listar(Pageable pageable) {
        return consultas.listar(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogExistenceResponseDto verificarExistencia(String nombre) {
        return consultas.verificarExistencia(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogDto consultar(String codigoCatalogo) {
        return consultas.consultar(codigoCatalogo);
    }

    @Override
    public CatalogDto actualizarDescriptores(String codigoCatalogo, CatalogDescriptorsUpdateRequestDto request) {
        return edicion.actualizarDescriptores(codigoCatalogo, request);
    }

    @Override
    public void eliminar(String codigoCatalogo) {
        edicion.eliminar();
    }

    @Override
    public CatalogDto actualizarCampos(String codigoCatalogo, CatalogFieldsUpdateRequestDto request) {
        return altas.actualizarCampos(codigoCatalogo, request);
    }

    @Override
    public CatalogDto inactivar(String codigoCatalogo, InactivationRequestDto request) {
        return edicion.inactivar(codigoCatalogo, request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogSummaryDto> consultarHijos(String codigoCatalogo) {
        return consultas.consultarHijos(codigoCatalogo);
    }
}
