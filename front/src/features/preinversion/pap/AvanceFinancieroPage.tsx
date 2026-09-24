import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { avanceFinancieroApi, type EstudioFilaAvancePAP, type Cuatrimestre } from '../../../api/preinversionApi';
import { ListadoPAP, ejecutar, monto, porcentaje, type ColumnaPAP } from './papComun';

const CLAVE = 'preinversion.avanceFinanciero';

/**
 * "Avance Cuatrimestral Financiero del PAP" (CU-PRE-32, Anexo A.1).
 *
 * Lo ejecutado frente a lo programado en CU-PRE-30, un cuatrimestre a la vez.
 * La fila marcada con `alertaExcesoProgramado` es la que se pasó de lo
 * programado: se señala en la propia tabla (RN de la alerta), no en un aviso
 * aparte, para que se vea de qué etapa se trata.
 */
export function AvanceFinancieroPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const columnas: ColumnaPAP<EstudioFilaAvancePAP>[] = [
    { clave: 'cup', etiqueta: `${CLAVE}.columnaCup`, valor: (f) => f.cup },
    { clave: 'proyecto', etiqueta: `${CLAVE}.columnaProyecto`, valor: (f) => f.nombreProyecto },
    { clave: 'etapa', etiqueta: `${CLAVE}.columnaEtapa`, valor: (f) => String(f.etapa ?? '—') },
    { clave: 'fuente', etiqueta: `${CLAVE}.columnaFuente`, valor: (f) => String(f.fuenteFinanciamiento ?? '—') },
    { clave: 'costo', etiqueta: `${CLAVE}.columnaCosto`, valor: (f) => monto(f.costoEtapa), alineado: 'derecha' },
    {
      clave: 'anualProgramado',
      etiqueta: `${CLAVE}.columnaAnualProgramado`,
      valor: (f) => monto(f.avanceAnualProgramado),
      alineado: 'derecha',
    },
    {
      clave: 'anualEjecutado',
      etiqueta: `${CLAVE}.columnaAnualEjecutado`,
      valor: (f) => monto(f.avanceAnualEjecutadoMonto),
      alineado: 'derecha',
    },
    {
      clave: 'anualPorcentaje',
      etiqueta: `${CLAVE}.columnaAnualPorcentaje`,
      valor: (f) => porcentaje(f.avanceAnualEjecutadoPorcentaje),
      alineado: 'derecha',
    },
    {
      clave: 'cuatriProgramado',
      etiqueta: `${CLAVE}.columnaCuatriProgramado`,
      valor: (f) => monto(f.avanceDelCuatrimestreProgramado),
      alineado: 'derecha',
    },
    {
      clave: 'cuatriEjecutado',
      etiqueta: `${CLAVE}.columnaCuatriEjecutado`,
      valor: (f) => monto(f.avanceDelCuatrimestreEjecutadoMonto),
      alineado: 'derecha',
    },
    {
      clave: 'cuatriPorcentaje',
      etiqueta: `${CLAVE}.columnaCuatriPorcentaje`,
      valor: (f) => porcentaje(f.avanceDelCuatrimestrePorcentaje),
      alineado: 'derecha',
    },
    {
      clave: 'estado',
      etiqueta: `${CLAVE}.columnaAlerta`,
      valor: (f) =>
        f.alertaExcesoProgramado ? (
          <span className="marca-estado e-aviso">{t(`${CLAVE}.excesoProgramado`)}</span>
        ) : (
          '—'
        ),
    },
  ];

  const cargar = useCallback(
    async (p: { anio: number; periodo: string; pagina: number; tamanio: number }) => {
      const { data } = await avanceFinancieroApi.obtenerAvanceFinancieroPAP({
        anio: p.anio,
        periodo: p.periodo as Cuatrimestre,
        pagina: p.pagina,
        tamanio: p.tamanio,
      });
      return {
        contenido: data.contenido ?? [],
        totalPaginas: data.paginacion?.totalPaginas ?? 0,
        totalElementos: data.paginacion?.totalElementos ?? 0,
        pagina: data.paginacion?.pagina ?? p.pagina,
        idUnidadEjecutora: data.idUnidadEjecutora,
      };
    },
    [],
  );

  return (
    <ListadoPAP<EstudioFilaAvancePAP>
      clave={CLAVE}
      columnas={columnas}
      cargar={cargar}
      conBusqueda={false}
      conPeriodo
      alAbrir={(fila, contexto) =>
        navigate(`/programacion/pap/avance-financiero/${encodeURIComponent(fila.cup)}`, { state: contexto })
      }
      acciones={({ anio, periodo, idUnidadEjecutora }) => (
        <button
          type="button"
          className="btn secundario"
          onClick={() =>
            void ejecutar(
              () =>
                avanceFinancieroApi.generarReporteAvanceFinanciero({
                  anio,
                  periodo: periodo as Cuatrimestre,
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
      )}
    />
  );
}
