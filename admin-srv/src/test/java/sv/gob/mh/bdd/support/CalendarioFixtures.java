package sv.gob.mh.bdd.support;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.infrastructure.persistence.entity.calendario.CalendarioEntity;
import sv.gob.mh.infrastructure.persistence.entity.calendario.ExcepcionCalendarioEntity;
import sv.gob.mh.infrastructure.persistence.entity.calendario.PeriodoCalendarioEntity;
import sv.gob.mh.infrastructure.persistence.repository.calendario.CalendarioJpaRepository;
import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * Precondiciones y verificaciones de los steps BDD de CU-ADM-04 contra la base de datos, sin pasar
 * por la API, para que cada escenario solo ejercite por HTTP la operación bajo prueba. Cada método
 * corre en su propia transacción (los steps no tienen sesión de Hibernate abierta) y expone datos
 * planos, no colecciones lazy. Los escenarios usan códigos fijos ("CAL-2026"), así que
 * {@link #limpiar()} vacía los calendarios antes de cada uno.
 */
@Component
@Transactional
public class CalendarioFixtures {

    public static final LocalDate INICIO_CALENDARIO = LocalDate.of(2026, 1, 1);
    public static final LocalDate FIN_CALENDARIO = LocalDate.of(2026, 12, 31);

    private static final String ADMINISTRADOR = "admin.calendario.bdd";

    private final CalendarioJpaRepository calendarioRepository;

    public CalendarioFixtures(CalendarioJpaRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    /** Borra todos los calendarios; sus períodos y excepciones caen en cascada. */
    public void limpiar() {
        calendarioRepository.deleteAll(calendarioRepository.findAll());
    }

    // ---------- Calendarios ----------

    public void crearCalendario(String codigo, LocalDate fechaInicio, LocalDate fechaFin, EstadoCalendario estado) {
        CalendarioEntity calendario = new CalendarioEntity();
        calendario.setCodigo(codigo);
        calendario.setNombre("Calendario " + codigo);
        calendario.setDescripcion("Calendario de prueba BDD");
        calendario.setFechaInicio(fechaInicio);
        calendario.setFechaFin(fechaFin);
        calendario.setEstado(estado);
        calendario.setAdministrador(ADMINISTRADOR);
        calendarioRepository.save(calendario);
    }

    /** Calendario ACTIVO con el rango por defecto (2026). */
    public void crearCalendario(String codigo) {
        crearCalendario(codigo, INICIO_CALENDARIO, FIN_CALENDARIO, EstadoCalendario.ACTIVO);
    }

    /**
     * Calendario con el rango por defecto, un período LABORAL "LAB-01" (enero a junio), uno
     * NO_LABORAL "NOLAB-01" (julio) y una excepción DIA_NO_LABORAL el 1 de mayo.
     */
    public void crearCalendarioConItems(String codigo) {
        crearCalendario(codigo);
        agregarPeriodo(codigo, "LAB-01", TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30)));
        agregarPeriodo(codigo, "NOLAB-01", TipoPeriodo.NO_LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31)));
        agregarExcepcion(codigo, LocalDate.of(2026, 5, 1), TipoExcepcion.DIA_NO_LABORAL);
    }

    public boolean existeCalendario(String codigo) {
        return calendarioRepository.existsByCodigo(codigo);
    }

    public Optional<EstadoCalendario> estado(String codigo) {
        return calendarioRepository.findByCodigo(codigo).map(CalendarioEntity::getEstado);
    }

    public LocalDate fechaInicio(String codigo) {
        return exigir(codigo).getFechaInicio();
    }

    public void fijarEstado(String codigo, EstadoCalendario estado) {
        exigir(codigo).setEstado(estado);
    }

    // ---------- Períodos y excepciones ----------

    public void agregarPeriodo(String codigoCalendario, String codigoPeriodo, TipoPeriodo tipo,
            RecurrenciaBdd recurrencia) {
        CalendarioEntity calendario = exigir(codigoCalendario);
        PeriodoCalendarioEntity periodo = new PeriodoCalendarioEntity();
        periodo.setCalendario(calendario);
        periodo.setCodigo(codigoPeriodo);
        periodo.setNombre("Período " + codigoPeriodo);
        periodo.setTipo(tipo);
        periodo.setTipoRecurrencia(recurrencia.tipo());
        periodo.setFechaInicio(recurrencia.fechaInicio());
        periodo.setFechaFin(recurrencia.fechaFin());
        periodo.reemplazarDiasSemana(recurrencia.diasSemana());
        periodo.reemplazarDiasMes(recurrencia.diasMes());
        periodo.reemplazarMeses(recurrencia.meses());
        List<PeriodoCalendarioEntity> periodos = new ArrayList<>(calendario.getPeriodos());
        periodos.add(periodo);
        calendario.reemplazarPeriodos(periodos);
    }

    public void agregarExcepcion(String codigoCalendario, LocalDate fecha, TipoExcepcion tipo) {
        CalendarioEntity calendario = exigir(codigoCalendario);
        ExcepcionCalendarioEntity excepcion = new ExcepcionCalendarioEntity();
        excepcion.setCalendario(calendario);
        excepcion.setFecha(fecha);
        excepcion.setTipo(tipo);
        excepcion.setDescripcion("Excepción de prueba BDD");
        List<ExcepcionCalendarioEntity> excepciones = new ArrayList<>(calendario.getExcepciones());
        excepciones.add(excepcion);
        calendario.reemplazarExcepciones(excepciones);
    }

    public boolean existePeriodo(String codigoCalendario, String codigoPeriodo) {
        return periodo(codigoCalendario, codigoPeriodo).isPresent();
    }

    public Long idPeriodo(String codigoCalendario, String codigoPeriodo) {
        return periodo(codigoCalendario, codigoPeriodo).map(PeriodoCalendarioEntity::getId).orElseThrow(
                () -> new IllegalStateException("No existe el período " + codigoPeriodo + " en " + codigoCalendario));
    }

    private Optional<PeriodoCalendarioEntity> periodo(String codigoCalendario, String codigoPeriodo) {
        return calendarioRepository.findByCodigo(codigoCalendario).stream()
                .flatMap(calendario -> calendario.getPeriodos().stream())
                .filter(periodo -> periodo.getCodigo().equals(codigoPeriodo))
                .findFirst();
    }

    private CalendarioEntity exigir(String codigo) {
        return calendarioRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalStateException("No existe el calendario " + codigo));
    }
}
