import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { programacionFinancieraApi, type EstudioFilaListaPAP } from '../../../api/preinversionApi';
import { ListadoPAP, ejecutar, monto, type ColumnaPAP } from './papComun';

const CLAVE = 'preinversion.programacionFinanciera';

/**
 * "Programación Financiera Cuatrimestral del PAP" (CU-PRE-30, Anexo A.1).
 *
 * Una fila por etapa y fuente de financiamiento, con lo programado en cada
 * cuatrimestre. Al abrir una fila se entra a la programación de ese estudio.
 */
export function ProgramacionFinancieraPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const columnas: ColumnaPAP<EstudioFilaListaPAP>[] = [
    { clave: 'cup', etiqueta: `${CLAVE}.columnaCup`, valor: (f) => f.cup },
    { clave: 'proyecto', etiqueta: `${CLAVE}.columnaProyecto`, valor: (f) => f.nombreProyecto },
    { clave: 'etapa', etiqueta: `${CLAVE}.columnaEtapa`, valor: (f) => String(f.etapa ?? '—') },
    { clave: 'fuente', etiqueta: `${CLAVE}.columnaFuente`, valor: (f) => String(f.fuenteFinanciamiento ?? '—') },
    { clave: 'costo', etiqueta: `${CLAVE}.columnaCosto`, valor: (f) => monto(f.costoEtapa), alineado: 'derecha' },
    { clave: 'previo', etiqueta: `${CLAVE}.columnaEjecutadoPrevio`, valor: (f) => monto(f.ejecutadoAniosAnteriores), alineado: 'derecha' },
    { clave: 'c1', etiqueta: `${CLAVE}.columnaCuatrimestre1`, valor: (f) => monto(f.montoCuatrimestre1), alineado: 'derecha' },
    { clave: 'c2', etiqueta: `${CLAVE}.columnaCuatrimestre2`, valor: (f) => monto(f.montoCuatrimestre2), alineado: 'derecha' },
    { clave: 'c3', etiqueta: `${CLAVE}.columnaCuatrimestre3`, valor: (f) => monto(f.montoCuatrimestre3), alineado: 'derecha' },
    { clave: 'total', etiqueta: `${CLAVE}.columnaTotalAnio`, valor: (f) => monto(f.totalProgramadoAnio), alineado: 'derecha' },
    { clave: 'posteriores', etiqueta: `${CLAVE}.columnaPosteriores`, valor: (f) => monto(f.aniosPosteriores), alineado: 'derecha' },
  ];

  const cargar = useCallback(async (p: { anio: number; busqueda?: string; pagina: number; tamanio: number }) => {
    const { data } = await programacionFinancieraApi.obtenerProgramacionFinancieraPAP(p);
    return {
      contenido: data.contenido ?? [],
      totalPaginas: data.paginacion?.totalPaginas ?? 0,
      totalElementos: data.paginacion?.totalElementos ?? 0,
      pagina: data.paginacion?.pagina ?? p.pagina,
      idUnidadEjecutora: data.idUnidadEjecutora,
    };
  }, []);

  return (
    <ListadoPAP<EstudioFilaListaPAP>
      clave={CLAVE}
      columnas={columnas}
      cargar={cargar}
      alAbrir={(fila, contexto) =>
        navigate(`/programacion/pap/programacion-financiera/${encodeURIComponent(fila.cup)}`, { state: contexto })
      }
      acciones={({ anio, idUnidadEjecutora, recargar }) => (
        <>
          <button
            type="button"
            className="btn secundario"
            onClick={() =>
              void ejecutar(
                () =>
                  programacionFinancieraApi.generarReporteProgramacionPAP({
                    anio,
                    idUnidadEjecutora: idUnidadEjecutora as number,
                    formato: 'EXCEL',
                  }),
                `${CLAVE}.reporteGenerado`,
                t,
              )
            }
          >
            {t('preinversion.pap.generarReporte')}
          </button>
          {/* RN: la DGICP habilita modificaciones fuera del plazo. */}
          <button
            type="button"
            className="btn neutro"
            onClick={() =>
              void ejecutar(
                () =>
                  programacionFinancieraApi.habilitarModificacionesFueraPlazo({
                    habilitarModificacionesFueraPlazoRequest: {
                      anio,
                      idUnidadEjecutora: idUnidadEjecutora as number,
                    } as never,
                  }),
                `${CLAVE}.plazoHabilitado`,
                t,
                recargar,
              )
            }
          >
            {t('preinversion.pap.habilitarPlazo')}
          </button>
        </>
      )}
    />
  );
}
