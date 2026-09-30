package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * Registro de un {@link Catalogo}: el valor de su campo KEY ({@code clave}) y los del resto de
 * campos. Los valores se guardan como texto (el contrato los declara {@code additionalProperties:
 * true}, sin escenario de CU-ADM-01 que dependa de conservar el tipo original).
 */
public class Registro {

    private final Long id;
    private final Catalogo catalogo;
    private final String clave;
    private final RegistroPadre registroPadre;
    private EstadoVigencia estado;
    private final LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private final Map<String, String> valores;

    /** Reconstituye un registro ya persistido. */
    public Registro(Long id, Catalogo catalogo, String clave, RegistroPadre registroPadre, EstadoVigencia estado,
            LocalDate fechaDesde, LocalDate fechaHasta, Map<String, String> valores) {
        this.id = id;
        this.catalogo = catalogo;
        this.clave = clave;
        this.registroPadre = registroPadre;
        this.estado = estado;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.valores = new HashMap<>(valores);
    }

    /**
     * HU-ADM-01-09: {@code valores} ya validados con {@link Catalogo#valoresCompletos}; la clave
     * es el valor de su campo KEY. La unicidad de la clave la verifica quien lo crea.
     */
    public static Registro nuevo(Catalogo catalogo, Map<String, String> valores, RegistroPadre registroPadre,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        return new Registro(null, catalogo, clave(catalogo, valores), registroPadre,
                Vigencia.estadoInicial(null, fechaHasta), fechaDesde, fechaHasta, valores);
    }

    /** Valor del campo KEY dentro de {@code valores}. */
    public static String clave(Catalogo catalogo, Map<String, String> valores) {
        return valores.get(catalogo.campoKey().getNombre());
    }

    /** Reglas 11 y E7: un registro nunca se elimina, se ofrece inactivarlo. */
    public static ErrorCatalogoException eliminacionNoPermitida() {
        return ErrorCatalogoException.eliminacionNoPermitida(
                "Un registro de catálogo no puede eliminarse, solo inactivarse.", "inactivarRegistro");
    }

    /** HU-ADM-01-12: actualiza campos no KEY (Regla 16); se valida todo antes de aplicar nada. */
    public void actualizarValores(List<ValorCampo> nuevos) {
        for (int i = 0; i < nuevos.size(); i++) {
            String nombreCampo = nuevos.get(i).campo();
            if (catalogo.exigirCampo(nombreCampo, "values").isEsKey()) {
                throw ErrorCatalogoException.reglaNegocio("CAMPO_KEY_INMUTABLE",
                        "El campo KEY de un registro no puede actualizarse.", "values[" + i + "].field", nombreCampo);
            }
        }
        nuevos.forEach(valor -> valores.put(valor.campo(), valor.valor()));
    }

    /** HU-ADM-01-13: ACTIVE → INACTIVE (Reglas 9, 14). */
    public void inactivar(LocalDate toDate) {
        fechaHasta = Vigencia.fechaInactivacion(toDate);
        estado = EstadoVigencia.INACTIVE;
    }

    /** Regla 12: un registro de un catálogo INACTIVE es INACTIVE, cualquiera sea su propio estado. */
    public EstadoVigencia estadoEfectivo() {
        return catalogo.estadoEfectivo() == EstadoVigencia.INACTIVE ? EstadoVigencia.INACTIVE
                : Vigencia.estadoEfectivo(estado, fechaHasta);
    }

    /** Este registro como padre de un registro de un catálogo hijo (Regla 23). */
    public RegistroPadre comoPadre() {
        return new RegistroPadre(id, clave, catalogo.getCodigo());
    }

    public String valor(CampoDefinicion campo) {
        return valores.get(campo.getNombre());
    }

    public Long getId() {
        return id;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public String getClave() {
        return clave;
    }

    public RegistroPadre getRegistroPadre() {
        return registroPadre;
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

    public Map<String, String> getValores() {
        return Collections.unmodifiableMap(valores);
    }
}
