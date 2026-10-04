package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Registro de un {@link Catalogo}: el valor de su campo KEY ({@code clave}), los del resto de
 * campos en versión STRING (RN-11), su registro padre si el catálogo tiene padre (RN-05), su
 * estado y su vigencia.
 */
public class Registro {

    private final Long id;
    private final Catalogo catalogo;
    private final String clave;
    private RegistroPadre registroPadre;
    private EstadoVigencia estado;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private final Map<String, String> valores;

    /** Reconstituye un registro ya persistido. */
    public Registro(Long id, Catalogo catalogo, String clave, RegistroPadre registroPadre, Periodo vigencia,
            Map<String, String> valores) {
        this.id = id;
        this.catalogo = catalogo;
        this.clave = clave;
        this.registroPadre = registroPadre;
        this.estado = vigencia.estado();
        this.fechaDesde = vigencia.desde();
        this.fechaHasta = vigencia.hasta();
        this.valores = new HashMap<>(valores);
    }

    /**
     * SF-07: {@code valores} ya validados con {@link Catalogo#valoresParaRegistroNuevo}; la clave
     * es el valor de su campo KEY. Sin fechas queda ACTIVE (RN-15); con TO DATE hoy o pasada,
     * INACTIVE (RN-16). La unicidad de la clave la verifica quien lo crea.
     */
    public static Registro nuevo(Catalogo catalogo, Map<String, String> valores, RegistroPadre registroPadre,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        Vigencia.validarRango(fechaDesde, fechaHasta);
        EstadoVigencia estado = Vigencia.vencido(fechaHasta) ? EstadoVigencia.INACTIVE : EstadoVigencia.ACTIVE;
        return new Registro(null, catalogo, valores.get(catalogo.campoKey().getNombre()), registroPadre,
                new Periodo(estado, fechaDesde, fechaHasta), valores);
    }

    /**
     * SF-08 (RN-18): actualiza campos no KEY. Se valida toda la solicitud antes de aplicar cambios:
     * campos definidos (E-21), sin el KEY (E-19) y valores válidos (E-14).
     */
    public void actualizarValores(List<ValorCampo> nuevos) {
        nuevos.forEach(valor -> catalogo.exigirCampo(valor.campo()));
        if (nuevos.stream().anyMatch(valor -> catalogo.exigirCampo(valor.campo()).isEsKey())) {
            throw ErroresCatalogo.campoKeyInmutable();
        }
        nuevos.forEach(valor -> catalogo.validarValor(catalogo.exigirCampo(valor.campo()), valor.valor()));
        nuevos.forEach(valor -> valores.put(valor.campo(), valor.valor()));
    }

    /**
     * S-04 (modelo de dominio v4.0): al asignar un padre nuevo a su catálogo, el registro se enlaza
     * al registro padre indicado, ya validado por quien lo invoca; {@code null} lo deja sin padre.
     */
    public void asignarRegistroPadre(RegistroPadre nuevoPadre) {
        registroPadre = nuevoPadre;
    }

    /**
     * SF-09: ACTIVE → INACTIVE. Con una TO DATE actual o pasada la conserva (RN-12b); si no, la
     * TO DATE pasa a ser la fecha actual (RN-12a). La cascada a los registros hijos (RN-14) la
     * aplica quien lo invoca.
     */
    public void inactivar(LocalDate toDate) {
        LocalDate fecha = Vigencia.fechaInactivacion(toDate);
        Vigencia.validarRango(fechaDesde, fecha);
        fechaHasta = fecha;
        estado = EstadoVigencia.INACTIVE;
    }

    /** Nueva TO DATE sin cambiar el estado (E-09 si queda antes de la FROM DATE). */
    public void cambiarFechaHasta(LocalDate hasta) {
        Vigencia.validarRango(fechaDesde, hasta);
        fechaHasta = hasta;
    }

    /** RN-14, SF-14: inactivado porque se inactivó su registro padre, o porque venció. */
    public void inactivarEnCascada(LocalDate fecha) {
        fechaHasta = Vigencia.fechaHastaEnCascada(fechaHasta, fecha);
        estado = EstadoVigencia.INACTIVE;
    }

    /**
     * SF-09 paso 4: INACTIVE → ACTIVE con la TO DATE indicada, vacía o futura (E-16, S-02). Que
     * el catálogo y el registro padre estén activos (E-20) lo verifica quien lo invoca; los
     * registros hijos conservan su estado (S-03).
     */
    public void reactivar(LocalDate hasta) {
        Vigencia.validarFechaReactivacion(hasta);
        Vigencia.validarRango(fechaDesde, hasta);
        fechaHasta = hasta;
        estado = EstadoVigencia.ACTIVE;
    }

    /** RN-06: un registro de un catálogo INACTIVE es INACTIVE, cualquiera sea su propio estado. */
    public EstadoVigencia estadoEfectivo() {
        return catalogo.estaActivo() ? Vigencia.estadoEfectivo(estado, fechaHasta) : EstadoVigencia.INACTIVE;
    }

    public boolean estaActivo() {
        return estadoEfectivo() == EstadoVigencia.ACTIVE;
    }

    /** Este registro como padre de un registro de su catálogo hijo (RN-05). */
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
