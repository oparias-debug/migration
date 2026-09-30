package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import sv.gob.mh.domain.model.catalogo.CambioDescriptores.Descriptor;
import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * Catálogo del catalogMaster (CU-ADM-01): código, nombre, padre opcional (por código de negocio),
 * vigencia y sus campos (FIELD/KEY). Raíz del agregado: sus campos solo cambian a través de él.
 */
public class Catalogo {

    private static final String CAMPO_INEXISTENTE = "CAMPO_INEXISTENTE";
    private static final String MENSAJE_CAMPO_INEXISTENTE = "El campo solicitado no existe en el catálogo.";
    private static final String REGISTRO_PADRE_INEXISTENTE = "REGISTRO_PADRE_INEXISTENTE";

    private final Long id;
    private final String codigo;
    private String nombre;
    private String catalogoPadreCodigo;
    private EstadoVigencia estado;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private final List<CampoDefinicion> campos;

    /** Reconstituye un catálogo ya persistido. */
    public Catalogo(Long id, String codigo, String nombre, String catalogoPadreCodigo, EstadoVigencia estado,
            LocalDate fechaDesde, LocalDate fechaHasta, List<CampoDefinicion> campos) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.catalogoPadreCodigo = catalogoPadreCodigo;
        this.estado = estado;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.campos = new ArrayList<>(campos);
    }

    /** HU-ADM-01-01: la existencia del padre y la unicidad del código las verifica quien lo crea. */
    public static Catalogo nuevo(String codigo, String nombre, String catalogoPadreCodigo,
            EstadoVigencia estadoSolicitado, LocalDate fechaDesde, LocalDate fechaHasta, List<NuevoCampo> campos) {
        Catalogo catalogo = new Catalogo(null, codigo, nombre, catalogoPadreCodigo,
                Vigencia.estadoInicial(estadoSolicitado, fechaHasta), fechaDesde, fechaHasta, List.of());
        catalogo.definirCampos(campos);
        return catalogo;
    }

    /** Reglas 18 (al menos un campo), 2 (al menos un KEY) y 3 (nombres únicos). */
    public static void validarCampos(List<NuevoCampo> campos) {
        if (campos == null || campos.isEmpty()) {
            throw ErrorCatalogoException.reglaNegocio("CATALOGO_SIN_CAMPOS", "Un catálogo debe tener al menos un campo.");
        }
        if (campos.stream().noneMatch(NuevoCampo::esKey)) {
            throw ErrorCatalogoException.reglaNegocio("CATALOGO_SIN_CAMPO_KEY",
                    "Un catálogo debe tener al menos un campo KEY.");
        }
        Set<String> nombres = new HashSet<>();
        for (int i = 0; i < campos.size(); i++) {
            String nombreCampo = campos.get(i).nombre();
            if (!nombres.add(nombreCampo)) {
                throw ErrorCatalogoException.reglaNegocio("NOMBRE_CAMPO_DUPLICADO",
                        "Los campos de un catálogo deben tener nombres diferentes.", "fields[" + i + "].name",
                        nombreCampo);
            }
        }
    }

    /** Reglas 10 y E7: un catálogo nunca se elimina, se ofrece inactivarlo. */
    public static ErrorCatalogoException eliminacionNoPermitida() {
        return ErrorCatalogoException.eliminacionNoPermitida("Un catálogo no puede eliminarse, solo inactivarse.",
                "inactivarCatalogo");
    }

    /**
     * HU-ADM-01-06: reemplaza la lista completa de campos; el orden define la {@code posicion}
     * (desde 1). Que el catálogo no tenga registros (Regla 19) lo verifica quien lo invoca.
     */
    public void definirCampos(List<NuevoCampo> nuevos) {
        validarCampos(nuevos);
        campos.clear();
        for (int i = 0; i < nuevos.size(); i++) {
            campos.add(CampoDefinicion.nuevo(nuevos.get(i), i + 1));
        }
    }

    /**
     * HU-ADM-01-05: aplica los descriptores informados. Una {@code toDate} de hoy o pasada deja el
     * catálogo INACTIVE aunque se pida ACTIVE (Regla 14).
     */
    public void actualizarDescriptores(CambioDescriptores cambio) {
        if (cambio.informa(Descriptor.NOMBRE)) {
            nombre = cambio.nombre();
        }
        if (cambio.informa(Descriptor.PADRE)) {
            catalogoPadreCodigo = cambio.padre();
        }
        if (cambio.informa(Descriptor.FECHA_DESDE)) {
            fechaDesde = cambio.fechaDesde();
        }
        if (cambio.informa(Descriptor.FECHA_HASTA)) {
            fechaHasta = cambio.fechaHasta();
        }
        if (cambio.estado() != null) {
            estado = cambio.estado();
        }
        if (Vigencia.vencido(fechaHasta)) {
            estado = EstadoVigencia.INACTIVE;
        }
    }

    /** HU-ADM-01-07: ACTIVE → INACTIVE (Reglas 9, 14). */
    public void inactivar(LocalDate toDate) {
        fechaHasta = Vigencia.fechaInactivacion(toDate);
        estado = EstadoVigencia.INACTIVE;
    }

    public EstadoVigencia estadoEfectivo() {
        return Vigencia.estadoEfectivo(estado, fechaHasta);
    }

    public List<CampoDefinicion> camposOrdenados() {
        return campos.stream().sorted(CampoDefinicion.POR_POSICION).toList();
    }

    /** Campo KEY cuyo valor identifica al registro: el primero por posición (Regla 2 exige al menos uno). */
    public CampoDefinicion campoKey() {
        return camposOrdenados().stream()
                .filter(CampoDefinicion::isEsKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo " + codigo + " no tiene definido un campo KEY."));
    }

    public Optional<CampoDefinicion> buscarCampo(String nombreCampo) {
        return campos.stream().filter(campo -> campo.getNombre().equals(nombreCampo)).findFirst();
    }

    /** Un nombre de campo inexistente es CAMPO_INEXISTENTE, reportado sobre la propiedad {@code propiedad}. */
    public CampoDefinicion exigirCampo(String nombreCampo, String propiedad) {
        return buscarCampo(nombreCampo).orElseThrow(() -> ErrorCatalogoException.reglaNegocio(CAMPO_INEXISTENTE,
                MENSAJE_CAMPO_INEXISTENTE, propiedad, nombreCampo));
    }

    /**
     * HU-ADM-01-09 (Reglas 1, 8): valores de un registro nuevo por nombre de campo. Todo campo
     * informado debe existir y todo campo definido, incluido el KEY, debe venir.
     */
    public Map<String, String> valoresCompletos(List<ValorCampo> valores) {
        Map<String, String> porCampo = new HashMap<>();
        for (ValorCampo valor : valores) {
            exigirCampo(valor.campo(), "values");
            porCampo.put(valor.campo(), valor.valor());
        }
        for (CampoDefinicion campo : camposOrdenados()) {
            if (!porCampo.containsKey(campo.getNombre())) {
                throw ErrorCatalogoException.reglaNegocio("VALOR_CAMPO_FALTANTE",
                        "Falta el valor de un campo definido del catálogo.", "values", campo.getNombre());
            }
        }
        return porCampo;
    }

    /**
     * Reglas 8 y 23: en un catálogo con padre el registro padre es obligatorio; en uno sin padre
     * no se admite. Retorna el código del catálogo donde buscar el registro padre, o vacío si el
     * registro no lleva padre.
     */
    public Optional<String> catalogoDelRegistroPadre(String clavePadre) {
        if (catalogoPadreCodigo == null) {
            if (clavePadre != null) {
                throw ErrorCatalogoException.reglaNegocio(REGISTRO_PADRE_INEXISTENTE,
                        "El catálogo no tiene catálogo padre; no admite registro padre.", "parentRecord", clavePadre);
            }
            return Optional.empty();
        }
        if (clavePadre == null || clavePadre.isBlank()) {
            throw ErrorCatalogoException.reglaNegocio("REGISTRO_PADRE_REQUERIDO",
                    "El catálogo tiene padre; se requiere el registro padre.");
        }
        return Optional.of(catalogoPadreCodigo);
    }

    /** Regla 23: la clave indicada no corresponde a ningún registro del catálogo padre. */
    public static ErrorCatalogoException registroPadreInexistente(String clavePadre) {
        return ErrorCatalogoException.reglaNegocio(REGISTRO_PADRE_INEXISTENTE,
                "El registro padre indicado no existe en el catálogo padre.", "parentRecord", clavePadre);
    }

    /**
     * Reglas 4 y 5: los campos pedidos, en el orden pedido; sin lista, el primer campo no KEY según
     * {@code posicion}. Un nombre inexistente es E3.
     */
    public List<CampoDefinicion> camposProyectados(List<String> nombres) {
        List<String> solicitados = nombres == null ? List.of()
                : nombres.stream().map(String::trim).filter(nombreCampo -> !nombreCampo.isEmpty()).toList();
        if (solicitados.isEmpty()) {
            return camposOrdenados().stream().filter(campo -> !campo.isEsKey()).limit(1).toList();
        }
        return solicitados.stream().map(nombreCampo -> exigirCampo(nombreCampo, "fields")).toList();
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCatalogoPadreCodigo() {
        return catalogoPadreCodigo;
    }

    public EstadoVigencia getEstado() {
        return estado;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public List<CampoDefinicion> getCampos() {
        return List.copyOf(campos);
    }
}
