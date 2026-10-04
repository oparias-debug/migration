package sv.gob.mh.bdd.support;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Vigencia;

import sv.gob.mh.infrastructure.persistence.entity.catalogo.CampoDefinicionEntity;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CatalogoEntity;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.RegistroEntity;
import sv.gob.mh.infrastructure.persistence.repository.catalogo.CatalogoJpaRepository;
import sv.gob.mh.infrastructure.persistence.repository.catalogo.RegistroJpaRepository;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Precondiciones y verificaciones de los steps BDD de CU-ADM-01 contra la base de datos, sin
 * pasar por la API, para que cada escenario solo ejercite por HTTP la operación bajo prueba.
 * Cada método corre en su propia transacción (los steps no tienen sesión de Hibernate abierta) y
 * expone datos planos, no colecciones lazy. Los escenarios usan códigos fijos ("PAIS"), así que
 * {@link #limpiar()} vacía el catalogMaster antes de cada uno.
 *
 * <p>Los estados que se verifican son los guardados: así se comprueba que las cascadas (RN-06,
 * RN-14) y la evaluación diaria (SF-14) realmente persistieron el cambio.</p>
 */
@Component
@Transactional
public class CatalogoFixtures {

    /** Definición de un campo como la describen las tablas de los .feature; {@code tipo} vacío es STRING{80}. */
    public record Campo(String nombre, boolean esKey, String tipo, Integer posicion) {

        public TipoBdd tipoBdd() {
            return TipoBdd.de(tipo);
        }

        public String calificador() {
            return esKey ? "KEY" : "FIELD";
        }
    }

    private final CatalogoJpaRepository catalogoRepository;
    private final RegistroJpaRepository registroRepository;

    public CatalogoFixtures(CatalogoJpaRepository catalogoRepository, RegistroJpaRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    /**
     * Campos de un catálogo que el escenario no describe: el KEY que usan los .feature para ese
     * código (codPais, codDepto, ...) y un nombre.
     */
    public static List<Campo> camposPorDefecto(String codigo) {
        String key = switch (codigo) {
            case "PAIS" -> "codPais";
            case "DEPTO" -> "codDepto";
            case "MUNICIPIO" -> "codMun";
            default -> "codigo";
        };
        String tipoKey = key.equals("codigo") ? "STRING{10}" : "STRING{3}";
        return List.of(new Campo(key, true, tipoKey, 1), new Campo("nombre", false, "STRING{60}", 2));
    }

    /** Borra todos los registros y catálogos, primero sus enlaces al padre. */
    public void limpiar() {
        List<RegistroEntity> registros = registroRepository.findAll();
        registros.forEach(registro -> registro.setRegistroPadre(null));
        registroRepository.flush();
        registroRepository.deleteAll(registros);
        List<CatalogoEntity> catalogos = catalogoRepository.findAll();
        catalogos.forEach(catalogo -> catalogo.setCatalogoPadre(null));
        catalogoRepository.flush();
        catalogoRepository.deleteAll(catalogos);
    }

    // ---------- Catálogos ----------

    /** Crea el catálogo; si se indica padre y no existe, lo crea con los campos por defecto. */
    public void crearCatalogo(String codigo, String nombre, String padre, EstadoVigencia estado, LocalDate desde,
            LocalDate hasta, List<Campo> campos) {
        if (padre != null) {
            asegurarCatalogo(padre);
        }
        CatalogoEntity catalogo = new CatalogoEntity();
        catalogo.setCodigo(codigo);
        catalogo.setNombre(nombre != null ? nombre : "Catálogo " + codigo);
        catalogo.setCatalogoPadre(padre == null ? null : catalogo(padre));
        catalogo.setEstado(estado != null ? estado : EstadoVigencia.ACTIVE);
        catalogo.setFechaDesde(desde);
        catalogo.setFechaHasta(hasta);
        catalogo.reemplazarCampos(entidades(catalogo, campos));
        catalogoRepository.save(catalogo);
    }

    private static List<CampoDefinicionEntity> entidades(CatalogoEntity catalogo, List<Campo> campos) {
        List<CampoDefinicionEntity> entidades = new ArrayList<>();
        for (Campo campo : campos) {
            CampoDefinicionEntity entidad = new CampoDefinicionEntity();
            entidad.setCatalogo(catalogo);
            entidad.setNombre(campo.nombre());
            entidad.setEsKey(campo.esKey());
            entidad.setPosicion(campo.posicion());
            campo.tipoBdd().aplicar(entidad);
            entidades.add(entidad);
        }
        return entidades;
    }

    /** Si no existe, crea el catálogo plano, ACTIVE y con {@link #camposPorDefecto}. */
    public void asegurarCatalogo(String codigo) {
        if (!catalogoRepository.existsByCodigo(codigo)) {
            crearCatalogo(codigo, null, null, EstadoVigencia.ACTIVE, null, null, camposPorDefecto(codigo));
        }
    }

    public boolean existeCatalogo(String codigo) {
        return catalogoRepository.existsByCodigo(codigo);
    }

    /** {@code padre} de {@code hijo}, creando los que falten. */
    public void fijarPadre(String hijo, String padre) {
        asegurarCatalogo(padre);
        asegurarCatalogo(hijo);
        catalogo(hijo).setCatalogoPadre(catalogo(padre));
    }

    public void fijarNombre(String codigo, String nombre) {
        catalogo(codigo).setNombre(nombre);
    }

    /** Estado y TO DATE guardados del catálogo, creándolo si falta. */
    public void fijarEstadoCatalogo(String codigo, EstadoVigencia estado, LocalDate hasta) {
        asegurarCatalogo(codigo);
        CatalogoEntity catalogo = catalogo(codigo);
        catalogo.setEstado(estado);
        catalogo.setFechaHasta(hasta);
    }

    /** Reemplaza los campos de un catálogo sin registros. */
    public void fijarCampos(String codigo, List<Campo> campos) {
        CatalogoEntity catalogo = catalogo(codigo);
        catalogo.reemplazarCampos(entidades(catalogo, campos));
    }

    public CatalogoEntity catalogo(String codigo) {
        return catalogoRepository.findByCodigo(codigo).orElseThrow();
    }

    @Transactional(readOnly = true)
    public String catalogoNombre(String codigo) {
        return catalogo(codigo).getNombre();
    }

    @Transactional(readOnly = true)
    public EstadoVigencia estadoCatalogo(String codigo) {
        return catalogo(codigo).getEstado();
    }

    @Transactional(readOnly = true)
    public LocalDate fechaDesdeCatalogo(String codigo) {
        return catalogo(codigo).getFechaDesde();
    }

    @Transactional(readOnly = true)
    public LocalDate fechaHastaCatalogo(String codigo) {
        return catalogo(codigo).getFechaHasta();
    }

    /** Código del catálogo padre guardado (el padre es una relación lazy). */
    @Transactional(readOnly = true)
    public String codigoPadre(String codigo) {
        return catalogo(codigo).getCatalogoPadreCodigo();
    }

    /** Código del catálogo cuyo padre es {@code codigo}, o {@code null}. */
    @Transactional(readOnly = true)
    public String codigoHijo(String codigo) {
        return catalogoRepository.findFirstByCatalogoPadre_CodigoOrderByCodigoAsc(codigo)
                .map(CatalogoEntity::getCodigo)
                .orElse(null);
    }

    /** Campos del catálogo en orden de posición. */
    @Transactional(readOnly = true)
    public List<Campo> campos(String codigo) {
        return catalogo(codigo).getCampos().stream()
                .sorted(Comparator.comparing(CampoDefinicionEntity::getPosicion))
                .map(campo -> new Campo(campo.getNombre(), campo.isEsKey(), TipoBdd.de(campo).notacion(),
                        campo.getPosicion()))
                .toList();
    }

    @Transactional(readOnly = true)
    public String nombreCampoKey(String codigo) {
        return campos(codigo).stream().filter(Campo::esKey).findFirst().orElseThrow().nombre();
    }

    // ---------- Registros ----------

    /**
     * Valores válidos para un registro de {@code clave}: los dados y, para los campos que no
     * vienen, uno que su tipo admite.
     */
    @Transactional(readOnly = true)
    public Map<String, String> valoresValidos(String codigoCatalogo, String clave, Map<String, String> dados) {
        Map<String, String> valores = new LinkedHashMap<>();
        for (Campo campo : campos(codigoCatalogo)) {
            valores.put(campo.nombre(), dados.containsKey(campo.nombre()) ? dados.get(campo.nombre())
                    : campo.tipoBdd().valorValido(clave, campo.esKey()));
        }
        return valores;
    }

    /**
     * Persiste un registro con los valores dados, completando los que faltan con valores válidos.
     * Si se indica {@code clavePadre}, lo enlaza al registro de esa clave del catálogo padre.
     */
    public void crearRegistro(String codigoCatalogo, Map<String, String> valores, String clavePadre,
            EstadoVigencia estado, LocalDate hasta) {
        asegurarCatalogo(codigoCatalogo);
        CatalogoEntity catalogo = catalogo(codigoCatalogo);
        String nombreKey = nombreCampoKey(codigoCatalogo);
        String clave = valores.get(nombreKey);
        RegistroEntity registro = new RegistroEntity();
        registro.setCatalogo(catalogo);
        registro.setClave(clave);
        registro.setEstado(estado != null ? estado : EstadoVigencia.ACTIVE);
        registro.setFechaHasta(hasta);
        registro.setRegistroPadre(clavePadre == null ? null : registro(catalogo.getCatalogoPadreCodigo(), clavePadre));
        registro.reemplazarValores(valoresValidos(codigoCatalogo, clave, valores));
        registroRepository.save(registro);
    }

    /** Registro ACTIVE cuyo KEY es {@code clave}, con valores válidos para el resto de campos. */
    public void crearRegistro(String codigoCatalogo, String clave, String clavePadre) {
        asegurarCatalogo(codigoCatalogo);
        crearRegistro(codigoCatalogo, Map.of(nombreCampoKey(codigoCatalogo), clave), clavePadre, null, null);
    }

    /**
     * Un registro activo en el catálogo; si tiene padre, enlazado al primer registro del catálogo
     * padre (creándolo si no hay).
     */
    public String crearRegistroActivo(String codigoCatalogo, String clave) {
        asegurarCatalogo(codigoCatalogo);
        String codigoPadre = codigoPadre(codigoCatalogo);
        String clavePadre = null;
        if (codigoPadre != null) {
            List<RegistroEntity> delPadre = registroRepository.findByCatalogo_CodigoOrderByIdAsc(codigoPadre);
            if (delPadre.isEmpty()) {
                clavePadre = crearRegistroActivo(codigoPadre, "P" + clave);
            } else {
                clavePadre = delPadre.get(0).getClave();
            }
        }
        crearRegistro(codigoCatalogo, clave, clavePadre);
        return clave;
    }

    public void fijarEstadoRegistro(String codigoCatalogo, String clave, EstadoVigencia estado, LocalDate hasta) {
        RegistroEntity registro = registro(codigoCatalogo, clave);
        registro.setEstado(estado);
        registro.setFechaHasta(hasta);
    }

    /** Inactiva (guardado) el registro y, recursivamente, sus registros hijos, a la fecha actual. */
    public void inactivarConDescendientes(String codigoCatalogo, String clave) {
        List<RegistroEntity> pendientes = new ArrayList<>(List.of(registro(codigoCatalogo, clave)));
        while (!pendientes.isEmpty()) {
            RegistroEntity registro = pendientes.remove(0);
            registro.setEstado(EstadoVigencia.INACTIVE);
            registro.setFechaHasta(Vigencia.hoy());
            pendientes.addAll(registroRepository.findByRegistroPadre_IdOrderByIdAsc(registro.getId()));
        }
    }

    /** Inactiva (guardado) todos los registros del catálogo a la fecha actual. */
    public void inactivarRegistros(String codigoCatalogo) {
        registroRepository.findByCatalogo_CodigoOrderByIdAsc(codigoCatalogo).forEach(registro -> {
            registro.setEstado(EstadoVigencia.INACTIVE);
            registro.setFechaHasta(Vigencia.hoy());
        });
    }

    public RegistroEntity registro(String codigoCatalogo, String clave) {
        return registroRepository.findByCatalogo_CodigoAndClave(codigoCatalogo, clave).orElseThrow();
    }

    /** Catálogo del registro de esa clave: los .feature usan claves distintas en cada catálogo. */
    @Transactional(readOnly = true)
    public String catalogoDelRegistro(String clave) {
        return registroRepository.findAll().stream()
                .filter(registro -> registro.getClave().equals(clave))
                .map(registro -> registro.getCatalogo().getCodigo())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay un registro con la clave " + clave));
    }

    /**
     * Catálogo donde buscar un registro que puede no existir (p.ej. E-22): el del registro de esa
     * clave o, si no hay, el único catálogo del escenario.
     */
    @Transactional(readOnly = true)
    public String catalogoDelRegistroOUnico(String clave) {
        if (registroRepository.findAll().stream().anyMatch(registro -> registro.getClave().equals(clave))) {
            return catalogoDelRegistro(clave);
        }
        List<CatalogoEntity> catalogos = catalogoRepository.findAll();
        if (catalogos.size() != 1) {
            throw new IllegalStateException("No hay un registro con la clave " + clave + " ni un único catálogo");
        }
        return catalogos.get(0).getCodigo();
    }

    @Transactional(readOnly = true)
    public boolean existeRegistro(String codigoCatalogo, String clave) {
        return registroRepository.existsByCatalogo_CodigoAndClave(codigoCatalogo, clave);
    }

    @Transactional(readOnly = true)
    public long contarRegistros(String codigoCatalogo) {
        return registroRepository.findByCatalogo_CodigoOrderByIdAsc(codigoCatalogo).size();
    }

    @Transactional(readOnly = true)
    public List<String> claves(String codigoCatalogo) {
        return registroRepository.findByCatalogo_CodigoOrderByIdAsc(codigoCatalogo).stream()
                .map(RegistroEntity::getClave)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstadoVigencia estadoRegistro(String codigoCatalogo, String clave) {
        return registro(codigoCatalogo, clave).getEstado();
    }

    @Transactional(readOnly = true)
    public LocalDate fechaHastaRegistro(String codigoCatalogo, String clave) {
        return registro(codigoCatalogo, clave).getFechaHasta();
    }

    /** Valores del registro por nombre de campo (copia, sin proxies). */
    @Transactional(readOnly = true)
    public Map<String, String> valores(String codigoCatalogo, String clave) {
        return Map.copyOf(registro(codigoCatalogo, clave).getValores());
    }

    /** KEY del registro padre, o {@code null} si el registro no está enlazado. */
    @Transactional(readOnly = true)
    public String clavePadre(String codigoCatalogo, String clave) {
        RegistroEntity padre = registro(codigoCatalogo, clave).getRegistroPadre();
        return padre == null ? null : padre.getClave();
    }
}
