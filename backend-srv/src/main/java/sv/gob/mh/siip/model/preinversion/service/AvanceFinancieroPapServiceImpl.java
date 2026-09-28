package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP". Análogo de
 * {@link ProgramacionFinancieraPapServiceImpl} (CU-PRE-30) pero sobre lo EJECUTADO por cuatrimestre
 * ({@link sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral}) contra lo programado por
 * esa misma fuente (RN-E). Sin flujo de aprobación propio: "Seguimiento de Metas" (SF-2) es pura
 * navegación a CU-PRE-33, donde vive la única aprobación unificada del avance del PAP.
 *
 * <p>La lógica vive en {@link AvanceFinancieroPapListado} (listado y reporte) y
 * {@link AvanceFinancieroPapDetalle} (consulta y registro del avance de un estudio); esta clase
 * mantiene el control transaccional.
 */
@Service
@Transactional
public class AvanceFinancieroPapServiceImpl implements AvanceFinancieroPapService {

    private final AvanceFinancieroPapListado listado;
    private final AvanceFinancieroPapDetalle detalle;

    public AvanceFinancieroPapServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository,
            CalendarioEventoRepository calendarioEventoRepository, RevisionAvancePapRepository revisionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, ActorContexto actorContexto) {
        this.listado = new AvanceFinancieroPapListado(actorContexto, fuenteRepository, progRepository,
                avanceRepository, unidadEjecutoraRepository, revisionRepository);
        this.detalle = new AvanceFinancieroPapDetalle(actorContexto, proyectoRepository, etapaPreinversionRepository,
                calendarioEventoRepository, fuenteRepository, progRepository, avanceRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public AvanceFinancieroPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio) {
        return listado.listar(idUnidadEjecutora, anio, periodo, pagina, tamanio);
    }

    @Override
    @Transactional(readOnly = true)
    public AvanceEstudioDto obtenerAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodo) {
        return detalle.obtenerAvanceEstudio(cup, anio, periodo);
    }

    @Override
    public AvanceEstudioDto guardarAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodoDto,
            GuardarAvanceEstudioRequestDto request) {
        return detalle.guardarAvanceEstudio(cup, anio, periodoDto, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource generarReporte(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo, String formato) {
        return listado.generarReporte(idUnidadEjecutora, anio, periodo, formato);
    }
}
