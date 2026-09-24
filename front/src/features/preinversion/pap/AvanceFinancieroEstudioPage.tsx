import { avanceFinancieroApi, type AvanceEstudio, type Cuatrimestre } from '../../../api/preinversionApi';
import { formatearMonto } from '../analisis/analisisComun';
import type { ColumnaAnalisis } from '../analisis/TablaAnalisis';
import { FichaEstudioPAP } from './FichaEstudioPAP';
import { ROL_PAP, aMonto, agruparPorEtapa } from './fichaComun';

const CLAVE = 'preinversion.avanceFinanciero';

/** Una fila: lo ejecutado en el cuatrimestre por una fuente de una etapa. */
type Fila = {
  idFuente: string;
  etapa: string;
  fuenteFinanciamiento: string;
  montoProgramadoCuatrimestre: string;
  montoEjecutadoCuatrimestre: string;
  porcentajeEjecutadoCuatrimestre: string;
  observacionesCuatrimestre: string;
};

const columnas: ColumnaAnalisis<Fila>[] = [
  { clave: 'etapa', etiqueta: `${CLAVE}.columnaEtapa`, tipo: 'calculada' },
  { clave: 'fuenteFinanciamiento', etiqueta: `${CLAVE}.columnaFuente`, tipo: 'calculada' },
  {
    clave: 'montoProgramadoCuatrimestre',
    etiqueta: `${CLAVE}.columnaCuatriProgramado`,
    tipo: 'calculada',
    formato: (v) => formatearMonto(Number(v)),
  },
  // Lo único que se escribe en esta pantalla, junto con la observación.
  { clave: 'montoEjecutadoCuatrimestre', etiqueta: `${CLAVE}.columnaCuatriEjecutado`, tipo: 'numero' },
  {
    clave: 'porcentajeEjecutadoCuatrimestre',
    etiqueta: `${CLAVE}.columnaCuatriPorcentaje`,
    tipo: 'calculada',
    formato: (v) => (v === '' ? '—' : `${v} %`),
  },
  { clave: 'observacionesCuatrimestre', etiqueta: `${CLAVE}.columnaObservaciones`, tipo: 'texto' },
];

const leer = (dato: AvanceEstudio) => ({
  nombre: dato.nombreProyecto,
  filas: (dato.etapas ?? []).flatMap((etapa) =>
    (etapa.fuentes ?? []).map((fuente) => ({
      idFuente: String(fuente.idFuente ?? ''),
      etapa: String(etapa.etapa ?? ''),
      fuenteFinanciamiento: String(fuente.fuenteFinanciamiento ?? ''),
      montoProgramadoCuatrimestre: String(fuente.montoProgramadoCuatrimestre ?? ''),
      montoEjecutadoCuatrimestre: String(fuente.montoEjecutadoCuatrimestre ?? ''),
      porcentajeEjecutadoCuatrimestre: String(fuente.porcentajeEjecutadoCuatrimestre ?? ''),
      observacionesCuatrimestre: String(fuente.observacionesCuatrimestre ?? ''),
    })),
  ),
});

/**
 * Avance financiero de un estudio en el cuatrimestre (CU-PRE-32, Anexo A.2).
 *
 * Se registra lo ejecutado por fuente contra lo que CU-PRE-30 programó; el
 * porcentaje y las alertas los calcula el servidor. Las filas vienen de la
 * programación, así que no se agregan ni se quitan.
 */
export function AvanceFinancieroEstudioPage() {
  return (
    <FichaEstudioPAP<AvanceEstudio, Fila>
      clave={CLAVE}
      volverA="/programacion/pap/avance-financiero"
      columnas={columnas}
      rolEditor={ROL_PAP}
      leer={leer}
      cargar={async ({ cup, anio, periodo }) =>
        (await avanceFinancieroApi.obtenerAvanceEstudio({ cup, anio, periodo: periodo as Cuatrimestre })).data
      }
      guardar={async ({ cup, anio, periodo }, filas) =>
        (
          await avanceFinancieroApi.guardarAvanceEstudio({
            cup,
            anio,
            periodo: periodo as Cuatrimestre,
            guardarAvanceEstudioRequest: {
              etapas: agruparPorEtapa(filas, (fila) => ({
                idFuente: Number(fila.idFuente),
                montoEjecutadoCuatrimestre: aMonto(fila.montoEjecutadoCuatrimestre),
                observacionesCuatrimestre: fila.observacionesCuatrimestre || undefined,
              })),
            },
          })
        ).data
      }
    />
  );
}
