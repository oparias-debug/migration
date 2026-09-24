import { useTranslation } from 'react-i18next';
import type { TFunction } from 'i18next';
import { programacionMetasApi, type EstudioProgramacionMetas } from '../../../api/preinversionApi';
import type { ColumnaAnalisis } from '../analisis/TablaAnalisis';
import { FichaEstudioPAP } from './FichaEstudioPAP';
import { ROL_PAP, aMonto } from './fichaComun';
import { etiquetaEtapa } from './etiquetas';

const CLAVE = 'preinversion.programacionMetas';

const ENTREGABLES = ['ESTUDIO_DE_PERFIL', 'ESTUDIO_DE_PREFACTIBILIDAD', 'ESTUDIO_DE_FACTIBILIDAD', 'ESTUDIO_DE_DISENO'];

/** Una fila: la meta física de una etapa, en porcentaje. */
type Fila = {
  etapa: string;
  entregable: string;
  ejecutadoAniosAnteriores: string;
  montoCuatrimestre1: string;
  montoCuatrimestre2: string;
  montoCuatrimestre3: string;
  totalAnio: string;
  aniosPosteriores: string;
};

const conSigno = (valor: string) => (valor === '' ? '—' : `${valor} %`);

const columnas = (t: TFunction): ColumnaAnalisis<Fila>[] => [
  { clave: 'etapa', etiqueta: `${CLAVE}.columnaEtapa`, tipo: 'calculada', formato: etiquetaEtapa },
  // RN-B.a.1: en estudios de arrastre el entregable ya está decidido; el servidor rechaza cambiarlo.
  {
    clave: 'entregable',
    etiqueta: `${CLAVE}.columnaEntregable`,
    tipo: 'select',
    opciones: ENTREGABLES,
    formato: (v) => t(`preinversion.pap.entregables.${v}`, { defaultValue: v }),
  },
  {
    clave: 'ejecutadoAniosAnteriores',
    etiqueta: `${CLAVE}.columnaEjecutadoPrevio`,
    tipo: 'calculada',
    formato: conSigno,
  },
  { clave: 'montoCuatrimestre1', etiqueta: `${CLAVE}.columnaCuatrimestre1`, tipo: 'numero' },
  { clave: 'montoCuatrimestre2', etiqueta: `${CLAVE}.columnaCuatrimestre2`, tipo: 'numero' },
  { clave: 'montoCuatrimestre3', etiqueta: `${CLAVE}.columnaCuatrimestre3`, tipo: 'numero' },
  { clave: 'totalAnio', etiqueta: `${CLAVE}.columnaTotalAnio`, tipo: 'calculada', formato: conSigno },
  { clave: 'aniosPosteriores', etiqueta: `${CLAVE}.columnaPosteriores`, tipo: 'calculada', formato: conSigno },
];

const leer = (dato: EstudioProgramacionMetas) => ({
  nombre: dato.nombreProyecto,
  filas: (dato.etapas ?? []).map((etapa) => ({
    etapa: String(etapa.etapa ?? ''),
    entregable: String(etapa.entregable ?? ''),
    ejecutadoAniosAnteriores: String(etapa.ejecutadoAniosAnteriores ?? ''),
    montoCuatrimestre1: String(etapa.montoCuatrimestre1 ?? ''),
    montoCuatrimestre2: String(etapa.montoCuatrimestre2 ?? ''),
    montoCuatrimestre3: String(etapa.montoCuatrimestre3 ?? ''),
    totalAnio: String(etapa.totalAnio ?? ''),
    aniosPosteriores: String(etapa.aniosPosteriores ?? ''),
  })),
});

/**
 * Programación de metas físicas de un estudio (CU-PRE-31, Anexo A.2).
 *
 * La misma tabla que la financiera, pero en porcentaje de avance de la meta:
 * las etapas vienen dadas por la Ruta de Preinversión del proyecto, así que
 * aquí no se agregan ni se quitan filas.
 */
export function ProgramacionMetasEstudioPage() {
  const { t } = useTranslation();
  return (
    <FichaEstudioPAP<EstudioProgramacionMetas, Fila>
      clave={CLAVE}
      volverA="/programacion/pap/programacion-metas"
      columnas={columnas(t)}
      rolEditor={ROL_PAP}
      leer={leer}
      cargar={async ({ cup, anio }) => (await programacionMetasApi.obtenerProgramacionMetasEstudio({ cup, anio })).data}
      guardar={async ({ cup, anio }, filas) =>
        (
          await programacionMetasApi.guardarProgramacionMetasEstudio({
            cup,
            anio,
            guardarProgramacionMetasEstudioRequest: {
              etapas: filas.map((fila) => ({
                etapa: fila.etapa as never,
                entregable: (fila.entregable || undefined) as never,
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
