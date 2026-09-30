package sv.gob.mh.bdd.support;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.infrastructure.persistence.entity.catalogo.CampoDefinicionEntity;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CatalogoEntity;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.RegistroEntity;
import sv.gob.mh.infrastructure.persistence.repository.catalogo.CatalogoJpaRepository;
import sv.gob.mh.infrastructure.persistence.repository.catalogo.RegistroJpaRepository;
import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.enums.TipoCampo;

/**
 * Precondiciones y verificaciones de los steps BDD de CU-ADM-01 contra la base de datos, sin
 * pasar por la API, para que cada escenario solo ejercite por HTTP la operación bajo prueba.
 * Cada método corre en su propia transacción (los steps no tienen sesión de Hibernate abierta) y
 * expone datos planos, no colecciones lazy. Los escenarios usan códigos fijos ("CAT-A"), así que
 * {@link #limpiar()} vacía el catalogMaster antes de cada uno.
 */
@Component
@Transactional
public class CatalogoFixtures {

    /** Campos de un catálogo que el escenario no describe: los que usan las historias de CU-ADM-01. */
    public static final List<Campo> CAMPOS_POR_DEFECTO = List.of(new Campo("codigo", true),
            new Campo("descripcion", false), new Campo("sigla", false));

    public record Campo(String nombre, boolean esKey) {

        /** {@code "nombre:KEY"} / {@code "nombre:FIELD"}, para comparar estructuras. */
        public String firma() {
            return nombre + ":" + (esKey ? "KEY" : "FIELD");
        }
    }

    private final CatalogoJpaRepository catalogoRepository;
    private final RegistroJpaRepository registroRepository;

    public CatalogoFixtures(CatalogoJpaRepository catalogoRepository, RegistroJpaRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    /** Borra todos los registros (primero sus enlaces al padre, Regla 23) y catálogos. */
    public void limpiar() {
        List<RegistroEntity> registros = registroRepository.findAll();
        registros.forEach(registro -> registro.setRegistroPadre(null));
        registroRepository.flush();
        registroRepository.deleteAll(registros);
        catalogoRepository.deleteAll();
    }

    // ---------- Catálogos ----------

    public void crearCatalogo(String codigo, String nombre, String padre, List<Campo> campos) {
        CatalogoEntity catalogo = new CatalogoEntity();
        catalogo.setCodigo(codigo);
        catalogo.setNombre(nombre != null ? nombre : "Catálogo " + codigo);
        catalogo.setCatalogoPadreCodigo(padre);
        catalogo.setEstado(EstadoVigencia.ACTIVE);
        for (int i = 0; i < campos.size(); i++) {
            CampoDefinicionEntity campo = new CampoDefinicionEntity();
            campo.setCatalogo(catalogo);
            campo.setNombre(campos.get(i).nombre());
            campo.setTipo(TipoCampo.STRING);
            campo.setEsKey(campos.get(i).esKey());
            campo.setPosicion(i + 1);
            catalogo.getCampos().add(campo);
        }
        catalogoRepository.save(catalogo);
    }

    public void crearCatalogo(String codigo, String padre, List<Campo> campos) {
        crearCatalogo(codigo, null, padre, campos);
    }

    /** Si no existe, crea el catálogo sin padre y con {@link #CAMPOS_POR_DEFECTO}. */
    public void asegurarCatalogo(String codigo) {
        if (!catalogoRepository.existsByCodigo(codigo)) {
            crearCatalogo(codigo, null, CAMPOS_POR_DEFECTO);
        }
    }

    public void fijarEstadoCatalogo(String codigo, EstadoVigencia estado) {
        asegurarCatalogo(codigo);
        catalogo(codigo).setEstado(estado);
    }

    @Transactional(readOnly = true)
    public CatalogoEntity catalogo(String codigo) {
        return catalogoRepository.findByCodigo(codigo).orElseThrow();
    }

    /** Campos del catálogo en orden de posición. */
    @Transactional(readOnly = true)
    public List<Campo> campos(String codigo) {
        return catalogo(codigo).getCampos().stream()
                .sorted(Comparator.comparing(CampoDefinicionEntity::getPosicion))
                .map(campo -> new Campo(campo.getNombre(), campo.isEsKey()))
                .toList();
    }

    @Transactional(readOnly = true)
    public String nombreCampoKey(String codigo) {
        return campos(codigo).stream().filter(Campo::esKey).findFirst().orElseThrow().nombre();
    }

    // ---------- Registros ----------

    /**
     * Persiste un registro con los valores dados; los campos que no vienen se completan con un
     * texto de relleno. Si se indica {@code clavePadre}, lo enlaza al registro de esa clave del
     * catálogo padre.
     */
    public void crearRegistro(String codigoCatalogo, Map<String, String> valores, String clavePadre) {
        asegurarCatalogo(codigoCatalogo);
        CatalogoEntity catalogo = catalogo(codigoCatalogo);
        String clave = valores.get(nombreCampoKey(codigoCatalogo));
        Map<String, String> completos = new LinkedHashMap<>();
        catalogo.getCampos().forEach(campo -> completos.put(campo.getNombre(),
                valores.getOrDefault(campo.getNombre(), campo.getNombre() + " " + clave)));

        RegistroEntity registro = new RegistroEntity();
        registro.setCatalogo(catalogo);
        registro.setClave(clave);
        registro.setEstado(EstadoVigencia.ACTIVE);
        registro.setRegistroPadre(clavePadre == null ? null : registro(catalogo.getCatalogoPadreCodigo(), clavePadre));
        registro.getValores().putAll(completos);
        registroRepository.save(registro);
    }

    /** Registro cuyo KEY es {@code clave}, con valores de relleno para el resto de campos. */
    public void crearRegistro(String codigoCatalogo, String clave) {
        asegurarCatalogo(codigoCatalogo);
        crearRegistro(codigoCatalogo, Map.of(nombreCampoKey(codigoCatalogo), clave), null);
    }

    public void fijarEstadoRegistro(String codigoCatalogo, String clave, EstadoVigencia estado) {
        registro(codigoCatalogo, clave).setEstado(estado);
    }

    @Transactional(readOnly = true)
    public RegistroEntity registro(String codigoCatalogo, String clave) {
        return registroRepository.findByCatalogo_CodigoAndClave(codigoCatalogo, clave).orElseThrow();
    }

    /** Valores del registro por nombre de campo (copia, sin proxies). */
    @Transactional(readOnly = true)
    public Map<String, String> valores(String codigoCatalogo, String clave) {
        return Map.copyOf(registro(codigoCatalogo, clave).getValores());
    }

    /** {@code [clave del padre, catálogo del padre]}, o {@code null} si el registro no está enlazado. */
    @Transactional(readOnly = true)
    public List<String> registroPadre(String codigoCatalogo, String clave) {
        RegistroEntity padre = registro(codigoCatalogo, clave).getRegistroPadre();
        return padre == null ? null : List.of(padre.getClave(), padre.getCatalogo().getCodigo());
    }
}
