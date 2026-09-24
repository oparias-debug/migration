import { analisisLegalApi, type AnalisisLegal, type FilaAnalisisLegalRequest } from '../../../api/preinversionApi';
import { PantallaAnalisis, aNumero } from './analisisComun';
import type { ColumnaAnalisis } from './TablaAnalisis';

const CLAVE = 'preinversion.analisisLegal';
type Fila = Record<string, unknown>;

const COLUMNAS: ColumnaAnalisis<Fila>[] = [
  { clave: 'analisisGestionLegalRequerida', etiqueta: `${CLAVE}.analisisRequerido`, tipo: 'texto', ancho: '45%' },
  { clave: 'entregable', etiqueta: `${CLAVE}.entregable`, tipo: 'texto', ancho: '30%' },
  { clave: 'costoEntregable', etiqueta: `${CLAVE}.costoEntregable`, tipo: 'numero' },
];

/** Pantalla "Análisis Legal" (CU-PRE-16, Anexo A.1), capítulo 1.3.2.5. */
export function AnalisisLegalPage() {
  return (
    <PantallaAnalisis<AnalisisLegal, Fila>
      clave={CLAVE}
      columnas={COLUMNAS}
      filaVacia={() => ({})}
      pregunta={`${CLAVE}.pregunta`}
      total={`${CLAVE}.total`}
      cargar={async (idProyecto) => (await analisisLegalApi.obtenerAnalisisLegal({ idProyecto })).data}
      guardar={async (idProyecto, aplica, filas) =>
        (
          await analisisLegalApi.guardarAnalisisLegal({
            idProyecto,
            analisisLegalRequest: {
              requiereAnalisisLegal: aplica,
              filas: filas.map((f) => ({ ...f, costoEntregable: aNumero(f.costoEntregable) })) as FilaAnalisisLegalRequest[],
            },
          })
        ).data
      }
      leer={(dato) => ({
        aplica: dato.requiereAnalisisLegal,
        filas: (dato.filas ?? []) as Fila[],
        total: dato.totalCostoEntregables,
      })}
    />
  );
}
