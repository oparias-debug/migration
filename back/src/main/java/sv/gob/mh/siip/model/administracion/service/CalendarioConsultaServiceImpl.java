package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioResumenDto;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.FechaLaboralResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RangoFechasCalendarioResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;

/**
 * CU-ADM-04 (Gestion de Calendarios): consultas de solo lectura sobre calendarios (RN18). La logica vive en
 * {@link CalendarioConsultasDefinicion} (definicion del calendario y de sus periodos) y
 * {@link CalendarioConsultasDias} (tipo de dia y conteo de dias); esta clase mantiene el control
 * transaccional. La gestion esta en {@link CalendarioServiceImpl}.
 */
@Service
@Transactional
public class CalendarioConsultaServiceImpl implements CalendarioConsultaService {

    private final CalendarioConsultasDefinicion definicion;
    private final CalendarioConsultasDias dias;

    public CalendarioConsultaServiceImpl(CalendarioRepository calendarioRepository,
            PeriodoRepository periodoRepository) {
        this.definicion = new CalendarioConsultasDefinicion(calendarioRepository, periodoRepository);
        this.dias = new CalendarioConsultasDias(calendarioRepository, periodoRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarioResumenDto> listar() {
        return definicion.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarioDto recuperarDefinicion(String codigoCalendario) {
        return definicion.recuperarDefinicion(codigoCalendario);
    }

    @Override
    @Transactional(readOnly = true)
    public RangoFechasCalendarioResponseDto consultarRangoFechasCalendario(String codigoCalendario) {
        return definicion.consultarRangoFechasCalendario(codigoCalendario);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDiaResponseDto consultarTipoDia(String codigoCalendario, LocalDate fecha) {
        return dias.consultarTipoDia(codigoCalendario, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public PertenenciaPeriodoResponseDto consultarPertenenciaPeriodo(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha) {
        return definicion.consultarPertenenciaPeriodo(codigoCalendario, codigoPeriodo, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public DuracionPeriodoResponseDto consultarDuracionPeriodo(String codigoCalendario, String codigoPeriodo) {
        return definicion.consultarDuracionPeriodo(codigoCalendario, codigoPeriodo);
    }

    @Override
    @Transactional(readOnly = true)
    public DiasRestantesResponseDto consultarDiasRestantesPeriodoLaboral(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        return dias.consultarDiasRestantesPeriodoLaboral(codigoCalendario, codigoPeriodo, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public DiasLaboralesEntreFechasResponseDto consultarDiasLaboralesEntreFechas(String codigoCalendario,
            LocalDate fechaInicio, LocalDate fechaFin) {
        return dias.consultarDiasLaboralesEntreFechas(codigoCalendario, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public FechaLaboralResultanteResponseDto calcularFechaLaboralResultante(String codigoCalendario, LocalDate fecha,
            Integer diasHabiles) {
        return dias.calcularFechaLaboralResultante(codigoCalendario, fecha, diasHabiles);
    }
}
