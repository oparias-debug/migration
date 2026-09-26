package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaListaPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": armado de cada fila del
 * listado (Anexo A.1) y del reporte a partir de una fuente de financiamiento de una etapa.
 */
final class ProgramacionFinancieraPapListadoAssembler {

    private final ProgramacionFinancieraPapCalculos calculos;

    ProgramacionFinancieraPapListadoAssembler(FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository) {
        this.calculos = new ProgramacionFinancieraPapCalculos(fuenteRepository, progRepository);
    }

    EstudioFilaListaPAPDto construirFilaListaDto(FuenteFinanciamientoEtapaPap fuente, Integer anio) {
        EtapaPreinversion etapa = fuente.getEtapaPreinversion();
        Proyecto proyecto = etapa.getProyecto();
        ProgramacionFinancieraPapMontos montos = calculos.montos(fuente.getId(), anio);

        return new EstudioFilaListaPAPDto(proyecto.getCup(), proyecto.getNombre(),
                NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()))
                .fuenteFinanciamiento(ProgramacionFinancieraPapEstudioAssembler.fuenteFinanciamientoDto(fuente))
                .costoEtapa(etapa.getCosto())
                .ejecutadoAniosAnteriores(ProgramacionPapSoporte.positivoONulo(montos.ejecutadoAniosAnteriores()))
                // Anexo A.1: columnas "Programación I, II y III Cuatrimestre" (monto de cada cuatrimestre
                // del Anexo A.2).
                .montoCuatrimestre1(montos.cuatrimestre1().doubleValue())
                .montoCuatrimestre2(montos.cuatrimestre2().doubleValue())
                .montoCuatrimestre3(montos.cuatrimestre3().doubleValue())
                .totalProgramadoAnio(montos.total().doubleValue())
                .aniosPosteriores(ProgramacionPapSoporte.positivoONulo(montos.aniosPosteriores(etapa.getCosto())));
    }
}
