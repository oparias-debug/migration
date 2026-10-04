package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Catálogo del catalogMaster (CU-ADM-01): código, nombre, padre e hijo opcionales (por código de
 * negocio), estado, vigencia y sus campos (FIELD/KEY). Raíz del agregado: sus campos solo cambian a
 * través de él. Las reglas que necesitan otros catálogos o los registros (unicidad del código,
 * jerarquía, cascadas) las aplican los casos de uso.
 */
public class Catalogo {

    private final Long id;
    private final String codigo;
    private String nombre;
    private String catalogoPadreCodigo;
    /** RN-05: el catálogo padre conoce a su hijo (a lo sumo uno). Solo informativo: lo deriva la persistencia. */
    private final String catalogoHijoCodigo;
    private EstadoVigencia estado;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private final List<CampoDefinicion> campos;

    /** Reconstituye un catálogo ya persistido. */
    public Catalogo(Long id, String codigo, String nombre, String catalogoPadreCodigo, String catalogoHijoCodigo,
            Periodo vigencia, List<CampoDefinicion> campos) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.catalogoPadreCodigo = catalogoPadreCodigo;
        this.catalogoHijoCodigo = catalogoHijoCodigo;
        this.estado = vigencia.estado();
        this.fechaDesde = vigencia.desde();
        this.fechaHasta = vigencia.hasta();
        this.campos = new ArrayList<>(campos);
    }

    /**
     * SF-01. Sin fechas queda ACTIVE (RN-15); con TO DATE hoy o pasada, INACTIVE (RN-16); pedido
     * INACTIVE sin TO DATE vencida, la TO DATE pasa a ser la fecha actual (RN-12a).
     */
    public static Catalogo nuevo(String codigo, String nombre, String catalogoPadreCodigo,
            EstadoVigencia estadoSolicitado, LocalDate fechaDesde, LocalDate fechaHasta, List<NuevoCampo> campos) {
        validarCampos(campos);
        Vigencia.validarRango(fechaDesde, fechaHasta);
        EstadoVigencia estado = EstadoVigencia.ACTIVE;
        LocalDate hasta = fechaHasta;
        if (Vigencia.vencido(fechaHasta)) {
            estado = EstadoVigencia.INACTIVE;
        } else if (estadoSolicitado == EstadoVigencia.INACTIVE) {
            estado = EstadoVigencia.INACTIVE;
            hasta = Vigencia.hoy();
        }
        var catalogo = new Catalogo(null, codigo, nombre, catalogoPadreCodigo, null,
                new Periodo(estado, fechaDesde, hasta), List.of());
        catalogo.definirCampos(campos);
        return catalogo;
    }

    /**
     * RN-20 (al menos un campo, E-02), RN-03 (exactamente un KEY, E-03 y E-23), RN-04 (nombres
     * únicos, E-04; tipo y restricción consistentes, E-05) y S-05 (posiciones enteras positivas y
     * únicas).
     */
    public static void validarCampos(List<NuevoCampo> campos) {
        if (campos == null || campos.isEmpty()) {
            throw ErroresCatalogo.sinCampos();
        }
        long keys = campos.stream().filter(NuevoCampo::esKey).count();
        if (keys == 0) {
            throw ErroresCatalogo.sinCampoKey();
        }
        if (keys > 1) {
            throw ErroresCatalogo.multiplesCamposKey();
        }
        Set<String> nombres = new HashSet<>();
        Set<Integer> posiciones = new HashSet<>();
        for (NuevoCampo campo : campos) {
            if (!nombres.add(campo.nombre())) {
                throw ErroresCatalogo.nombreCampoDuplicado(campo.nombre());
            }
            if (campo.posicion() == null || campo.posicion() <= 0) {
                throw ErroresCatalogo.posicionNoPositiva(campo.posicion());
            }
            if (!posiciones.add(campo.posicion())) {
                throw ErroresCatalogo.posicionRepetida(campo.posicion());
            }
        }
        for (NuevoCampo campo : campos) {
            if (campo.definicion() == null || !campo.definicion().esValida(campo.esKey())) {
                throw ErroresCatalogo.definicionTipoInvalida(campo.nombre());
            }
        }
    }

    /**
     * SF-04 paso 5: reemplaza la lista completa de campos. Un campo que conserva su nombre conserva
     * su identidad. Que el catálogo no tenga registros (RN-21) lo verifica quien lo invoca.
     */
    public void definirCampos(List<NuevoCampo> nuevos) {
        validarCampos(nuevos);
        Map<String, Long> idsPorNombre = new HashMap<>();
        campos.forEach(campo -> idsPorNombre.put(campo.getNombre(), campo.getId()));
        campos.clear();
        nuevos.forEach(campo -> campos.add(CampoDefinicion.desde(campo, idsPorNombre.get(campo.nombre()))));
    }

    // ---------- Descriptores (RN-19) ----------

    public void cambiarNombre(String nuevoNombre) {
        nombre = nuevoNombre;
    }

    /** RN-05: la existencia y el estado del padre, y que no genere ciclos, los verifica quien lo invoca. */
    public void asignarPadre(String codigoPadre) {
        catalogoPadreCodigo = codigoPadre;
    }

    /** RN-19: cambia la vigencia sin cambiar el estado (E-09 si el rango está invertido). */
    public void cambiarVigencia(LocalDate desde, LocalDate hasta) {
        Vigencia.validarRango(desde, hasta);
        fechaDesde = desde;
        fechaHasta = hasta;
    }

    // ---------- Estado (RN-06, RN-12, RN-14, RN-16) ----------

    public EstadoVigencia estadoEfectivo() {
        return Vigencia.estadoEfectivo(estado, fechaHasta);
    }

    public boolean estaActivo() {
        return estadoEfectivo() == EstadoVigencia.ACTIVE;
    }

    /**
     * SF-05: ACTIVE → INACTIVE. Con una TO DATE actual o pasada la conserva (RN-12b); si no, la
     * TO DATE pasa a ser la fecha actual (RN-12a). La cascada a registros y catálogo hijo (RN-06)
     * la aplica quien lo invoca.
     */
    public void inactivar(LocalDate toDate) {
        LocalDate fecha = Vigencia.fechaInactivacion(toDate);
        Vigencia.validarRango(fechaDesde, fecha);
        fechaHasta = fecha;
        estado = EstadoVigencia.INACTIVE;
    }

    /** RN-06: inactivado porque se inactivó su catálogo padre, o porque venció (SF-14). */
    public void inactivarEnCascada(LocalDate fecha) {
        fechaHasta = Vigencia.fechaHastaEnCascada(fechaHasta, fecha);
        estado = EstadoVigencia.INACTIVE;
    }

    /**
     * SF-06: INACTIVE → ACTIVE con la TO DATE indicada, que debe quedar vacía o futura (E-16, S-02).
     * Que el padre esté activo (E-15) lo verifica quien lo invoca; registros e hijo conservan su
     * estado (S-03).
     */
    public void reactivar(LocalDate desde, LocalDate hasta) {
        Vigencia.validarFechaReactivacion(hasta);
        Vigencia.validarRango(desde, hasta);
        fechaDesde = desde;
        fechaHasta = hasta;
        estado = EstadoVigencia.ACTIVE;
    }

    // ---------- Campos y registros ----------

    public List<CampoDefinicion> camposOrdenados() {
        return campos.stream().sorted(CampoDefinicion.POR_POSICION).toList();
    }

    /** RN-03: el único campo KEY, cuyo valor identifica al registro. */
    public CampoDefinicion campoKey() {
        return camposOrdenados().stream()
                .filter(CampoDefinicion::isEsKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El catálogo " + codigo + " no tiene campo KEY."));
    }

    public Optional<CampoDefinicion> buscarCampo(String nombreCampo) {
        return campos.stream().filter(campo -> campo.getNombre().equals(nombreCampo)).findFirst();
    }

    /** E-21 si el campo no está definido en el catálogo. */
    public CampoDefinicion exigirCampo(String nombreCampo) {
        return buscarCampo(nombreCampo).orElseThrow(() -> ErroresCatalogo.campoInexistente(nombreCampo, codigo));
    }

    /** E-14 si el valor no cumple el tipo y la restricción del campo (RN-01, RN-11). */
    public void validarValor(CampoDefinicion campo, String valor) {
        if (!campo.getDefinicion().admite(valor)) {
            throw ErroresCatalogo.valorInvalido(valor, campo);
        }
    }

    /**
     * SF-07 (RN-01, RN-11): valores de un registro nuevo por nombre de campo, en el orden de los
     * campos. Cada campo informado debe existir (E-21) y cada campo definido, incluido el KEY, debe
     * venir con un valor válido (E-14).
     */
    public Map<String, String> valoresParaRegistroNuevo(List<ValorCampo> valores) {
        Map<String, String> porCampo = new HashMap<>();
        for (ValorCampo valor : valores) {
            exigirCampo(valor.campo());
            porCampo.put(valor.campo(), valor.valor());
        }
        Map<String, String> ordenados = new LinkedHashMap<>();
        for (CampoDefinicion campo : camposOrdenados()) {
            String valor = porCampo.get(campo.getNombre());
            validarValor(campo, valor);
            ordenados.put(campo.getNombre(), valor);
        }
        return ordenados;
    }

    /**
     * RN-05, RN-11: en un catálogo con padre el registro padre es obligatorio; en uno plano no se
     * admite (E-18). Retorna el código del catálogo donde buscar el registro padre, o vacío si el
     * registro no lleva padre.
     */
    public Optional<String> catalogoDelRegistroPadre(String clavePadre) {
        if (catalogoPadreCodigo == null) {
            if (clavePadre != null) {
                throw ErroresCatalogo.registroPadreEnCatalogoPlano(clavePadre, codigo);
            }
            return Optional.empty();
        }
        if (clavePadre == null || clavePadre.isBlank()) {
            throw ErroresCatalogo.registroPadreInvalido(null, catalogoPadreCodigo);
        }
        return Optional.of(catalogoPadreCodigo);
    }

    /**
     * Algoritmo 4.1 del CU (RN-07 a): Field Set de una búsqueda. Sin campos pedidos, el KEY y el
     * primer campo no KEY de menor posición; si los pedidos no incluyen el KEY, se antepone; un
     * campo no definido es E-21.
     */
    public List<CampoDefinicion> conjuntoDeCampos(List<String> nombres) {
        List<String> solicitados = nombres == null ? List.of()
                : nombres.stream()
                        .filter(nombreCampo -> nombreCampo != null)
                        .map(String::trim)
                        .filter(nombreCampo -> !nombreCampo.isEmpty())
                        .toList();
        CampoDefinicion key = campoKey();
        List<CampoDefinicion> conjunto = new ArrayList<>();
        if (solicitados.isEmpty()) {
            conjunto.add(key);
            camposOrdenados().stream().filter(campo -> !campo.isEsKey()).findFirst().ifPresent(conjunto::add);
            return conjunto;
        }
        solicitados.forEach(nombreCampo -> conjunto.add(exigirCampo(nombreCampo)));
        if (!conjunto.contains(key)) {
            conjunto.add(0, key);
        }
        return conjunto;
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

    public String getCatalogoHijoCodigo() {
        return catalogoHijoCodigo;
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
