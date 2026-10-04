package sv.gob.mh.infrastructure.persistence.repository.catalogo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.DefinicionTipo;
import sv.gob.mh.domain.model.catalogo.Periodo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.RegistroPadre;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CampoDefinicionEntity;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CatalogoEntity;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.RegistroEntity;

/**
 * Traducción entidad JPA ↔ modelo de dominio de CU-ADM-01. Se invoca dentro de la transacción del
 * adaptador: el modelo sale completo (campos incluidos) y no arrastra proxies lazy fuera de ella.
 */
final class CatalogoPersistenceMapper {

    private CatalogoPersistenceMapper() {
    }

    static Catalogo aModelo(CatalogoEntity entidad) {
        List<CampoDefinicion> campos = entidad.getCampos().stream()
                .map(campo -> new CampoDefinicion(campo.getId(), campo.getNombre(), campo.isEsKey(),
                        campo.getPosicion(), definicion(campo)))
                .toList();
        return new Catalogo(entidad.getId(), entidad.getCodigo(), entidad.getNombre(), entidad.getCatalogoPadreCodigo(),
                entidad.getCatalogoHijoCodigo(),
                new Periodo(entidad.getEstado(), entidad.getFechaDesde(), entidad.getFechaHasta()), campos);
    }

    private static DefinicionTipo definicion(CampoDefinicionEntity campo) {
        return new DefinicionTipo(campo.getTipo(), campo.getValorMinimo(), campo.getValorMaximo(),
                campo.getLongitudMaxima(), campo.getFechaMinima(), campo.getFechaMaxima(), campo.getValoresEnum());
    }

    /**
     * Copia el modelo sobre la entidad. Los campos se sincronizan por id: los que conservan su id
     * se actualizan, los nuevos se agregan y el resto se elimina (orphanRemoval). {@code padre} es
     * la entidad del catálogo padre del modelo, o {@code null}.
     */
    static void copiar(Catalogo modelo, CatalogoEntity entidad, CatalogoEntity padre) {
        entidad.setCodigo(modelo.getCodigo());
        entidad.setNombre(modelo.getNombre());
        entidad.setCatalogoPadre(padre);
        entidad.setEstado(modelo.getEstado());
        entidad.setFechaDesde(modelo.getFechaDesde());
        entidad.setFechaHasta(modelo.getFechaHasta());

        Map<Long, CampoDefinicionEntity> existentes = new HashMap<>();
        entidad.getCampos().forEach(campo -> existentes.put(campo.getId(), campo));
        List<CampoDefinicionEntity> nuevos = new ArrayList<>();
        for (CampoDefinicion campo : modelo.getCampos()) {
            CampoDefinicionEntity destino = campo.getId() != null && existentes.containsKey(campo.getId())
                    ? existentes.get(campo.getId())
                    : new CampoDefinicionEntity();
            DefinicionTipo definicion = campo.getDefinicion();
            destino.setCatalogo(entidad);
            destino.setNombre(campo.getNombre());
            destino.setTipo(definicion.tipo());
            destino.setEsKey(campo.isEsKey());
            destino.setPosicion(campo.getPosicion());
            destino.setValorMinimo(definicion.minimo());
            destino.setValorMaximo(definicion.maximo());
            destino.setLongitudMaxima(definicion.longitudMaxima());
            destino.setFechaMinima(definicion.fechaMinima());
            destino.setFechaMaxima(definicion.fechaMaxima());
            destino.reemplazarValoresEnum(definicion.valoresEnum());
            nuevos.add(destino);
        }
        entidad.reemplazarCampos(nuevos);
    }

    /** Registros con su catálogo traducido una sola vez por catálogo distinto. */
    static List<Registro> aModelos(List<RegistroEntity> entidades) {
        Map<Long, Catalogo> catalogos = new HashMap<>();
        return entidades.stream()
                .map(entidad -> aModelo(entidad,
                        catalogo -> catalogos.computeIfAbsent(catalogo.getId(), id -> aModelo(catalogo))))
                .toList();
    }

    static Registro aModelo(RegistroEntity entidad) {
        return aModelo(entidad, CatalogoPersistenceMapper::aModelo);
    }

    private static Registro aModelo(RegistroEntity entidad, Function<CatalogoEntity, Catalogo> catalogo) {
        RegistroEntity padre = entidad.getRegistroPadre();
        RegistroPadre registroPadre = padre == null ? null
                : new RegistroPadre(padre.getId(), padre.getClave(), padre.getCatalogo().getCodigo());
        return new Registro(entidad.getId(), catalogo.apply(entidad.getCatalogo()), entidad.getClave(), registroPadre,
                new Periodo(entidad.getEstado(), entidad.getFechaDesde(), entidad.getFechaHasta()),
                entidad.getValores());
    }
}
