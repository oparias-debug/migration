package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP". Análogo de
 * {@link AvanceFinancieroPapServiceImpl} (CU-PRE-32) pero con valores porcentuales (0-100), y con el
 * ciclo de revisión ({@link sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap}) que aquí sí
 * vive: es la única aprobación unificada del avance del PAP (financiero y de metas físicas, decisión
 * funcional v1.2). El paso "Enviar a revisión DGICP" que sí existe en CU-PRE-31 no está documentado
 * aquí (ningún Flujo Básico/Subflujo lo narra) — no se modela, tal como señala el propio
 * CU-PRE-33.openapi.yaml.
 *
 * <p>El ciclo de revisión se expone en {@link AvanceMetasFisicasPapRevisionService}. La lógica de
 * este servicio vive en {@link AvanceMetasFisicasPapListado} (listado y reporte) y
 * {@link AvanceMetasFisicasPapDetalle} (consulta y registro del avance de un estudio); esta clase
 * mantiene el control transaccional.
 */
@Service
@Transactional
public class AvanceMetasFisicasPapServiceImpl implements AvanceMetasFisicasPapService {

    private final AvanceMetasFisicasPapListado listado;
    private final AvanceMetasFisicasPapDetalle detalle;

    public AvanceMetasFisicasPapServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository,
            AvanceCuatriMetaFisicaRepository avanceRepository,
            CalendarioEventoRepository calendarioEventoRepository, ActorContexto actorContexto) {
        this.listado = new AvanceMetasFisicasPapListado(actorContexto, etapaMetaRepository, progRepository,
                avanceRepository);
        this.detalle = new AvanceMetasFisicasPapDetalle(actorContexto, proyectoRepository,
                etapaPreinversionRepository, calendarioEventoRepository, etapaMetaRepository, progRepository,
                avanceRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public AvanceMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio) {
        return listado.listar(idUnidadEjecutora, anio, periodo, pagina, tamanio);
    }

    @Override
    @Transactional(readOnly = true)
    public AvanceMetasEstudioDto obtenerAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodo) {
        return detalle.obtenerAvanceMetasEstudio(cup, anio, periodo);
    }

    @Override
    public AvanceMetasEstudioDto guardarAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodoDto,
            GuardarAvanceMetasEstudioRequestDto request) {
        return detalle.guardarAvanceMetasEstudio(cup, anio, periodoDto, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource generarReporteAvanceMetas(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            String formato) {
        return listado.generarReporteAvanceMetas(idUnidadEjecutora, anio, periodo, formato);
    }
}
