package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión". Un {@code idFuente}
 * ({@link sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap}) persiste a través de
 * los años; el monto programado por cuatrimestre de cada año vive en
 * {@link sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera} (RN-B.a). "Ejecutado
 * años anteriores" (RN-B.c) se calcula como la suma histórica de los años previos al consultado, no se
 * almacena: no existe todavía un módulo de ejecución financiera real que lo alimente.
 *
 * <p>La lógica de este servicio vive en {@link ProgramacionFinancieraPapListado} (listado y reporte) y
 * {@link ProgramacionFinancieraPapEstudio} (agregar, consultar y guardar un estudio); esta clase mantiene
 * el control transaccional. Las bajas (desactivar un estudio, eliminar una etapa o fuente) y la
 * habilitación fuera de plazo están en {@link ProgramacionFinancieraPapAjusteServiceImpl}.
 */
@Service
@Transactional
public class ProgramacionFinancieraPapServiceImpl implements ProgramacionFinancieraPapService {

    private final ProgramacionFinancieraPapListado listado;
    private final ProgramacionFinancieraPapEstudio estudio;

    public ProgramacionFinancieraPapServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            HabilitacionModificacionPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository, ActorContexto actorContexto) {
        this.listado = new ProgramacionFinancieraPapListado(actorContexto, fuenteRepository, progRepository);
        this.estudio = new ProgramacionFinancieraPapEstudio(actorContexto, proyectoRepository,
                etapaPreinversionRepository, fuenteRepository, progRepository, habilitacionRepository,
                calendarioEventoRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramacionFinancieraPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, String busqueda,
            Integer pagina, Integer tamanio) {
        return listado.listar(idUnidadEjecutora, anio, busqueda, pagina, tamanio);
    }

    @Override
    public EstudioProgramacionPAPDto agregarEstudio(AgregarEstudioRequestDto request) {
        return estudio.agregarEstudio(request);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudioProgramacionPAPDto obtenerProgramacionEstudio(String cup, Integer anio) {
        return estudio.obtenerProgramacionEstudio(cup, anio);
    }

    @Override
    public EstudioProgramacionPAPDto guardarProgramacionEstudio(String cup, Integer anio,
            GuardarProgramacionEstudioRequestDto request) {
        return estudio.guardarProgramacionEstudio(cup, anio, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource generarReporte(Long idUnidadEjecutora, Integer anio, String formato) {
        return listado.generarReporte(idUnidadEjecutora, anio, formato);
    }
}
