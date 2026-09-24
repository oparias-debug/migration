import { avanceMetasApi, type AvanceMetasEstudio, type Cuatrimestre } from '../../../api/preinversionApi';
import type { ColumnaAnalisis } from '../analisis/TablaAnalisis';
import { FichaEstudioPAP } from './FichaEstudioPAP';
import { ROL_PAP, aMonto } from './fichaComun';

const CLAVE = 'preinversion.avanceMetas';

/** Una fila: el avance físico de una etapa en el cuatrimestre, en porcentaje. */
type Fila = {
  etapa: string;
  entregable: string;
  programadoDelCuatrimestre: string;
  avanceCuatrimestre: string;
  totalMetaEjecutada: string;
  observacionesCuatrimestre: string;
};

const conSigno = (valor: string) => (valor === '' ? '—' : `${valor} %`);

const columnas: ColumnaAnalisis<Fila>[] = [
  { clave: 'etapa', etiqueta: `${CLAVE}.columnaEtapa`, tipo: 'calculada' },
  { clave: 'entregable', etiqueta: `${CLAVE}.columnaEntregable`, tipo: 'calculada' },
  {
    clave: 'programadoDelCuatrimestre',
    etiqueta: `${CLAVE}.columnaProgramadoCuatri`,
    tipo: 'calculada',
    formato: conSigno,
  },
  { clave: 'avanceCuatrimestre', etiqueta: `${CLAVE}.columnaEjecutadoCuatri`, tipo: 'numero' },
  { clave: 'totalMetaEjecutada', etiqueta: `${CLAVE}.columnaTotalEjecutado`, tipo: 'calculada', formato: conSigno },
  { clave: 'observacionesCuatrimestre', etiqueta: `${CLAVE}.columnaObservaciones`, tipo: 'texto' },
];

const leer = (dato: AvanceMetasEstudio) => ({
  nombre: dato.nombreProyecto,
  filas: (dato.etapas ?? []).map((etapa) => ({
    etapa: String(etapa.etapa ?? ''),
    entregable: String(etapa.entregable ?? ''),
    programadoDelCuatrimestre: String(etapa.programadoDelCuatrimestre ?? ''),
    avanceCuatrimestre: String(etapa.avanceCuatrimestre ?? ''),
    totalMetaEjecutada: String(etapa.totalMetaEjecutada ?? ''),
    observacionesCuatrimestre: '',
  })),
});

/**
 * Avance de metas físicas de un estudio en el cuatrimestre (CU-PRE-33, Anexo A.2).
 *
 * Se escribe el avance del cuatrimestre por etapa; el total ejecutado y el
 * estado (en tiempo, en riesgo, retrasado) los calcula el servidor. La
 * observación del cuatrimestre se manda con el avance y no vuelve en la
 * lectura, así que el campo arranca vacío en cada visita.
 */
export function AvanceMetasEstudioPage() {
  return (
    <FichaEstudioPAP<AvanceMetasEstudio, Fila>
      clave={CLAVE}
      volverA="/programacion/pap/avance-metas"
      columnas={columnas}
      rolEditor={ROL_PAP}
      leer={leer}
      cargar={async ({ cup, anio, periodo }) =>
        (await avanceMetasApi.obtenerAvanceMetasEstudio({ cup, anio, periodo: periodo as Cuatrimestre })).data
      }
      guardar={async ({ cup, anio, periodo }, filas) =>
        (
          await avanceMetasApi.guardarAvanceMetasEstudio({
            cup,
            anio,
            periodo: periodo as Cuatrimestre,
            guardarAvanceMetasEstudioRequest: {
              etapas: filas.map((fila) => ({
                etapa: fila.etapa as never,
                avanceCuatrimestre: aMonto(fila.avanceCuatrimestre),
                observacionesCuatrimestre: fila.observacionesCuatrimestre || undefined,
              })),
            },
          })
        ).data
      }
    />
  );
}
