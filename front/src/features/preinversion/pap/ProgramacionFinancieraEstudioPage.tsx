import { programacionFinancieraApi, type EstudioProgramacionPAP } from '../../../api/preinversionApi';
import { formatearMonto } from '../analisis/analisisComun';
import type { ColumnaAnalisis } from '../analisis/TablaAnalisis';
import { FichaEstudioPAP } from './FichaEstudioPAP';
import { ETAPAS, ROL_PAP, aMonto, agruparPorEtapa } from './fichaComun';

const CLAVE = 'preinversion.programacionFinanciera';

const FUENTES = [
  'SIN_FINANCIAMIENTO',
  'FONDO_GENERAL',
  'RECURSOS_PROPIOS',
  'PRESTAMOS_EXTERNOS',
  'PRESTAMOS_INTERNOS',
  'DONACIONES',
  'OTROS',
];

/** Una fila de la tabla: una fuente de financiamiento dentro de una etapa. */
type Fila = {
  idFuente: string;
  etapa: string;
  fuenteFinanciamiento: string;
  fuenteRecursos: string;
  montoCuatrimestre1: string;
  montoCuatrimestre2: string;
  montoCuatrimestre3: string;
  totalProgramadoAnio: string;
  aniosPosteriores: string;
};

const columnas: ColumnaAnalisis<Fila>[] = [
  { clave: 'etapa', etiqueta: `${CLAVE}.columnaEtapa`, tipo: 'select', opciones: ETAPAS },
  { clave: 'fuenteFinanciamiento', etiqueta: `${CLAVE}.columnaFuente`, tipo: 'select', opciones: FUENTES },
  { clave: 'fuenteRecursos', etiqueta: `${CLAVE}.columnaRecursos`, tipo: 'texto' },
  { clave: 'montoCuatrimestre1', etiqueta: `${CLAVE}.columnaCuatrimestre1`, tipo: 'numero' },
  { clave: 'montoCuatrimestre2', etiqueta: `${CLAVE}.columnaCuatrimestre2`, tipo: 'numero' },
  { clave: 'montoCuatrimestre3', etiqueta: `${CLAVE}.columnaCuatrimestre3`, tipo: 'numero' },
  // Las dos últimas las calcula el servidor (RN-B.c): aquí sólo se leen.
  {
    clave: 'totalProgramadoAnio',
    etiqueta: `${CLAVE}.columnaTotalAnio`,
    tipo: 'calculada',
    formato: (v) => formatearMonto(Number(v)),
  },
  {
    clave: 'aniosPosteriores',
    etiqueta: `${CLAVE}.columnaPosteriores`,
    tipo: 'calculada',
    formato: (v) => formatearMonto(Number(v)),
  },
];

const leer = (dato: EstudioProgramacionPAP) => ({
  nombre: dato.nombreProyecto,
  filas: (dato.etapas ?? []).flatMap((etapa) =>
    (etapa.fuentes ?? []).map((fuente) => ({
      idFuente: String(fuente.idFuente ?? ''),
      etapa: String(etapa.etapa ?? ''),
      fuenteFinanciamiento: String(fuente.fuenteFinanciamiento ?? ''),
      fuenteRecursos: String(fuente.fuenteRecursos ?? ''),
      montoCuatrimestre1: String(fuente.montoCuatrimestre1 ?? ''),
      montoCuatrimestre2: String(fuente.montoCuatrimestre2 ?? ''),
      montoCuatrimestre3: String(fuente.montoCuatrimestre3 ?? ''),
      totalProgramadoAnio: String(fuente.totalProgramadoAnio ?? ''),
      aniosPosteriores: String(fuente.aniosPosteriores ?? ''),
    })),
  ),
});

/**
 * Programación financiera de un estudio (CU-PRE-30, Anexo A.2).
 *
 * Una fila por fuente de financiamiento de cada etapa, con lo programado en
 * cada cuatrimestre; el total del año y lo que queda para años posteriores los
 * calcula el servidor al guardar.
 */
export function ProgramacionFinancieraEstudioPage() {
  return (
    <FichaEstudioPAP<EstudioProgramacionPAP, Fila>
      clave={CLAVE}
      volverA="/programacion/pap/programacion-financiera"
      columnas={columnas}
      rolEditor={ROL_PAP}
      leer={leer}
      filaVacia={(filas) => ({
        idFuente: '',
        // La etapa de la última fila: se agregan fuentes a la etapa que se está trabajando.
        etapa: filas.at(-1)?.etapa ?? '',
        fuenteFinanciamiento: '',
        fuenteRecursos: '',
        montoCuatrimestre1: '',
        montoCuatrimestre2: '',
        montoCuatrimestre3: '',
        totalProgramadoAnio: '',
        aniosPosteriores: '',
      })}
      cargar={async ({ cup, anio }) => (await programacionFinancieraApi.obtenerProgramacionEstudio({ cup, anio })).data}
      guardar={async ({ cup, anio }, filas) =>
        (
          await programacionFinancieraApi.guardarProgramacionEstudio({
            cup,
            anio,
            guardarProgramacionEstudioRequest: {
              etapas: agruparPorEtapa(filas, (fila) => ({
                idFuente: fila.idFuente ? Number(fila.idFuente) : undefined,
                fuenteFinanciamiento: (fila.fuenteFinanciamiento || undefined) as never,
                fuenteRecursos: fila.fuenteRecursos || undefined,
                montoCuatrimestre1: aMonto(fila.montoCuatrimestre1),
                montoCuatrimestre2: aMonto(fila.montoCuatrimestre2),
                montoCuatrimestre3: aMonto(fila.montoCuatrimestre3),
              })),
            },
          })
        ).data
      }
    />
  );
}
