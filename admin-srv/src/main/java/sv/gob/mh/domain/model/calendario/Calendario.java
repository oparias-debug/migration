package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;
import sv.gob.mh.shared.exception.ErrorCalendarioException;

/**
 * Calendario laboral (CU-ADM-04): período maestro, estado, administrador responsable y sus
 * CalendarItems (períodos y excepciones). Raíz del agregado: sus períodos y excepciones solo cambian
 * a través de él, que valida que queden enmarcados en su rango (RN10) y sin códigos repetidos (RN15).
 * Nunca se elimina: la única transición es ACTIVO ⇄ INACTIVO, y sus CalendarItems heredan su estado
 * (RN20).
 */
public class Calendario {

    private static final String CODIGO_PERIODO_DUPLICADO = "CODIGO_PERIODO_DUPLICADO";
    private static final String PERIODO_INEXISTENTE = "PERIODO_INEXISTENTE";

    private final Long id;
    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private final RangoFechas rango;
    private EstadoCalendario estado;
    private final String administrador;
    private final List<Periodo> periodos;
    private final List<Excepcion> excepciones;

    /** Reconstituye un calendario ya persistido. */
    public Calendario(Long id, IdentificacionCalendario identificacion, RangoFechas rango, EstadoCalendario estado,
            String administrador, List<Periodo> periodos, List<Excepcion> excepciones) {
        this.id = id;
        this.codigo = identificacion.codigo();
        this.nombre = identificacion.nombre();
        this.descripcion = identificacion.descripcion();
        this.rango = rango;
        this.estado = estado;
        this.administrador = administrador;
        this.periodos = new ArrayList<>(periodos);
        this.excepciones = new ArrayList<>(excepciones);
    }

    /**
     * CU-ADM-04-01 (RN08): la unicidad del código (RN14) la verifica quien lo crea. El administrador
     * responsable es el actor autenticado (RN12).
     */
    public static Calendario nuevo(String codigo, String nombre, String descripcion, RangoFechas rango,
            EstadoCalendario estado, String administrador) {
        if (rango.invertido()) {
            throw ErrorCalendarioException.inconsistenciaFecha("CALENDARIO_RANGO_INVALIDO",
                    "La fecha de inicio del calendario es posterior a la fecha de fin.");
        }
        return new Calendario(null, new IdentificacionCalendario(codigo, nombre, descripcion), rango, estado,
                administrador, List.of(), List.of());
    }

    public static ErrorCalendarioException codigoDuplicado() {
        return ErrorCalendarioException.conflicto("CODIGO_CALENDARIO_DUPLICADO",
                "Ya existe un calendario con el código indicado.");
    }

    // ---------- Gestión ----------

    /** CU-ADM-04-02/03 (RN08, RN10, RN15). */
    public Periodo agregarPeriodo(String codigoPeriodo, String nombrePeriodo, TipoPeriodo tipo,
            Recurrencia recurrencia) {
        if (periodos.stream().anyMatch(periodo -> periodo.getCodigo().equals(codigoPeriodo))) {
            throw periodoDuplicado();
        }
        exigirRecurrenciaEnmarcada(recurrencia);
        var periodo = new Periodo(null, codigoPeriodo, nombrePeriodo, tipo, recurrencia);
        periodos.add(periodo);
        return periodo;
    }

    /** CU-ADM-04-04 (RN10): una excepción por fecha, dentro del rango del calendario. */
    public Excepcion registrarExcepcion(LocalDate fecha, TipoExcepcion tipo, String descripcionExcepcion) {
        exigirFechaDeExcepcion(fecha, null);
        var excepcion = new Excepcion(null, fecha, tipo, descripcionExcepcion);
        excepciones.add(excepcion);
        return excepcion;
    }

    /** CU-ADM-04-15 (RN20). */
    public void cambiarEstado(EstadoCalendario nuevoEstado) {
        estado = nuevoEstado;
    }

    /**
     * CU-ADM-04-14 (RN23): {@code items} es el conjunto final de CalendarItems. Con {@code id} se
     * edita el existente, sin {@code id} se da de alta uno nuevo y los existentes omitidos se
     * eliminan. Cada ítem se valida contra el estado del calendario al aplicarlo, así que el código
     * de un período nuevo no puede repetir el de uno existente aunque este se omita (RN15).
     */
    public void editarDefinicion(Iterable<ItemDefinicion> items) {
        List<Periodo> periodosConservados = new ArrayList<>();
        List<Excepcion> excepcionesConservadas = new ArrayList<>();
        for (ItemDefinicion item : items) {
            switch (item) {
                case ItemDefinicion.DePeriodo periodo -> periodosConservados.add(aplicar(periodo));
                case ItemDefinicion.DeExcepcion excepcion -> excepcionesConservadas.add(aplicar(excepcion));
            }
        }
        // Por identidad: un CalendarItem conservado es la misma instancia del agregado.
        periodos.removeIf((Periodo periodo) -> periodosConservados.stream()
                .noneMatch((Periodo conservado) -> conservado == periodo));
        excepciones.removeIf((Excepcion excepcion) -> excepcionesConservadas.stream()
                .noneMatch((Excepcion conservada) -> conservada == excepcion));
    }

    private Periodo aplicar(ItemDefinicion.DePeriodo item) {
        Periodo periodo = item.id() == null ? null
                : periodos.stream().filter(p -> item.id().equals(p.getId())).findFirst()
                        .orElseThrow(() -> ErrorCalendarioException.noEncontrado(PERIODO_INEXISTENTE,
                                "No existe ningún período con el id indicado dentro de ese calendario."));
        var editado = periodo;
        if (periodos.stream().anyMatch(otro -> otro != editado && otro.getCodigo().equals(item.codigo()))) {
            throw periodoDuplicado();
        }
        exigirRecurrenciaEnmarcada(item.recurrencia());
        if (periodo == null) {
            periodo = new Periodo(null, item.codigo(), item.nombre(), item.tipo(), item.recurrencia());
            periodos.add(periodo);
        } else {
            periodo.redefinir(item.codigo(), item.nombre(), item.tipo(), item.recurrencia());
        }
        return periodo;
    }

    private Excepcion aplicar(ItemDefinicion.DeExcepcion item) {
        Excepcion excepcion = item.id() == null ? null
                : excepciones.stream().filter(e -> item.id().equals(e.getId())).findFirst()
                        .orElseThrow(() -> ErrorCalendarioException.noEncontrado("EXCEPCION_INEXISTENTE",
                                "No existe ninguna excepción con el id indicado dentro de ese calendario."));
        exigirFechaDeExcepcion(item.fecha(), excepcion);
        if (excepcion == null) {
            excepcion = new Excepcion(null, item.fecha(), item.tipo(), item.descripcion());
            excepciones.add(excepcion);
        } else {
            excepcion.redefinir(item.fecha(), item.tipo(), item.descripcion());
        }
        return excepcion;
    }

    /** RN08 y RN10: el rango de la recurrencia (si declara uno) es válido y cae dentro del calendario. */
    private void exigirRecurrenciaEnmarcada(Recurrencia recurrencia) {
        Optional<RangoFechas> rangoPeriodo = recurrencia.rango();
        if (rangoPeriodo.isEmpty()) {
            return;
        }
        if (rangoPeriodo.get().invertido()) {
            throw ErrorCalendarioException.inconsistenciaFecha("PERIODO_RANGO_INVALIDO",
                    "La fecha de inicio del período es posterior a la fecha de fin.");
        }
        if (!rango.contiene(rangoPeriodo.get())) {
            throw ErrorCalendarioException.inconsistenciaFecha("PERIODO_FUERA_DE_RANGO",
                    "El período no está enmarcado dentro del rango del calendario.");
        }
    }

    /** RN10 y una sola excepción por fecha ({@code propia} es la que se edita, o {@code null}). */
    private void exigirFechaDeExcepcion(LocalDate fecha, Excepcion propia) {
        if (!rango.contiene(fecha)) {
            throw ErrorCalendarioException.inconsistenciaFecha("EXCEPCION_FUERA_DE_RANGO",
                    "La fecha de la excepción no está enmarcada dentro del rango del calendario.");
        }
        if (excepciones.stream().anyMatch(otra -> otra != propia && otra.getFecha().equals(fecha))) {
            throw ErrorCalendarioException.conflicto("EXCEPCION_DUPLICADA",
                    "Ya existe una excepción para esa fecha dentro del calendario.");
        }
    }

    private static ErrorCalendarioException periodoDuplicado() {
        return ErrorCalendarioException.conflicto(CODIGO_PERIODO_DUPLICADO,
                "Ya existe un período con ese código dentro del calendario.");
    }

    // ---------- Clasificación de fechas ----------

    /**
     * Una excepción sobre la fecha tiene prioridad (RN01); en su ausencia, la intersección
     * LABORAL+NO_LABORAL se resuelve como NO_LABORAL (RN02). Vacío si la fecha no cae en ninguna
     * excepción ni período (RN16).
     */
    public Optional<TipoPeriodo> clasificar(LocalDate fecha) {
        Optional<Excepcion> excepcion = excepciones.stream().filter(e -> e.getFecha().equals(fecha)).findFirst();
        if (excepcion.isPresent()) {
            return Optional.of(excepcion.get().getTipo() == TipoExcepcion.DIA_LABORAL ? TipoPeriodo.LABORAL
                    : TipoPeriodo.NO_LABORAL);
        }
        if (enAlgunPeriodo(TipoPeriodo.NO_LABORAL, fecha)) {
            return Optional.of(TipoPeriodo.NO_LABORAL);
        }
        return enAlgunPeriodo(TipoPeriodo.LABORAL, fecha) ? Optional.of(TipoPeriodo.LABORAL) : Optional.empty();
    }

    /** RN19: para efectos de conteo, una fecha sin período definido tampoco cuenta como LABORAL. */
    public boolean esDiaLaboral(LocalDate fecha) {
        return clasificar(fecha).map(tipo -> tipo == TipoPeriodo.LABORAL).orElse(false);
    }

    public boolean enAlgunPeriodo(TipoPeriodo tipo, LocalDate fecha) {
        return periodos.stream().filter(periodo -> periodo.getTipo() == tipo)
                .anyMatch(periodo -> periodo.incluye(fecha));
    }

    public boolean contiene(LocalDate fecha) {
        return rango.contiene(fecha);
    }

    /** RN17: el período con ese código dentro del calendario. */
    public Periodo exigirPeriodo(String codigoPeriodo) {
        return periodos.stream().filter(periodo -> periodo.getCodigo().equals(codigoPeriodo)).findFirst()
                .orElseThrow(() -> ErrorCalendarioException.noEncontrado(PERIODO_INEXISTENTE,
                        "No existe ningún período con el código indicado dentro de ese calendario."));
    }

    public Periodo exigirPeriodoLaboral(String codigoPeriodo) {
        var periodo = exigirPeriodo(codigoPeriodo);
        if (!periodo.esLaboral()) {
            throw ErrorCalendarioException.noEncontrado(PERIODO_INEXISTENTE,
                    "No existe ningún período LABORAL con el código indicado dentro de ese calendario.");
        }
        return periodo;
    }

    /** Rango sobre el que se recorre un período: el suyo, o el del calendario si no declara uno (MENSUAL). */
    public RangoFechas rangoDe(Periodo periodo) {
        return periodo.getRecurrencia().rango().orElse(rango);
    }

    // ---------- Lectura ----------

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public RangoFechas getRango() {
        return rango;
    }

    public EstadoCalendario getEstado() {
        return estado;
    }

    public String getAdministrador() {
        return administrador;
    }

    public List<Periodo> getPeriodos() {
        return Collections.unmodifiableList(periodos);
    }

    public List<Excepcion> getExcepciones() {
        return excepciones.stream().sorted((a, b) -> a.getFecha().compareTo(b.getFecha())).toList();
    }
}
