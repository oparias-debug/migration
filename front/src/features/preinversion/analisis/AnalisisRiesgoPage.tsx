import { useTranslation } from 'react-i18next';
import {
  analisisRiesgoApi,
  ImpactoRiesgo,
  Probabilidad,
  type AnalisisRiesgo,
  type FilaRiesgoRequest,
} from '../../../api/preinversionApi';
import { PantallaAnalisis, aNumero } from './analisisComun';
import type { ColumnaAnalisis } from './TablaAnalisis';

const CLAVE = 'preinversion.analisisRiesgo';
type Fila = Record<string, unknown>;

/** Color de la calificación, según la leyenda del Anexo A.1. */
const TONO: Record<string, string> = {
  BAJO: 'e-ok',
  MEDIO: 'e-aviso',
  ALTO: 'e-aviso',
  MUY_ALTO: 'e-error',
};

/** Pantalla "Análisis de Riesgos" (CU-PRE-15, Anexo A.1), capítulo 1.3.2.4. */
export function AnalisisRiesgoPage() {
  const { t } = useTranslation();
  const opcion = (grupo: string) => (valor: string) => t(`${CLAVE}.${grupo}.${valor}`);

  const columnas: ColumnaAnalisis<Fila>[] = [
    { clave: 'descripcionRiesgo', etiqueta: `${CLAVE}.descripcion`, tipo: 'texto', ancho: '25%' },
    { clave: 'probabilidad', etiqueta: `${CLAVE}.probabilidad`, tipo: 'select', opciones: Object.values(Probabilidad), formato: opcion('probabilidades') },
    { clave: 'impactoRiesgo', etiqueta: `${CLAVE}.impacto`, tipo: 'select', opciones: Object.values(ImpactoRiesgo), formato: opcion('impactos') },
    // La calificación la calcula el servidor cruzando probabilidad e impacto
    // (Anexo C.1): aquí sólo se muestra, con el color de la leyenda.
    { clave: 'calificacionRiesgo', etiqueta: `${CLAVE}.calificacion`, tipo: 'calculada', formato: opcion('calificaciones'), clase: (v) => TONO[v] },
    { clave: 'accionMitigacion', etiqueta: `${CLAVE}.accionMitigacion`, tipo: 'texto', ancho: '25%' },
    { clave: 'costoAccionMitigacion', etiqueta: `${CLAVE}.costoAccion`, tipo: 'numero' },
  ];

  return (
    <PantallaAnalisis<AnalisisRiesgo, Fila>
      clave={CLAVE}
      columnas={columnas}
      filaVacia={() => ({})}
      pregunta={`${CLAVE}.pregunta`}
      total={`${CLAVE}.total`}
      // RN06: avanzar exige la acción de mitigación de los riesgos altos, y eso
      // lo valida el servidor; el botón sólo lo pide.
      siguiente={{
        etiqueta: `${CLAVE}.siguiente`,
        destino: 'analisis-legal',
        accion: async (idProyecto) => {
          await analisisRiesgoApi.avanzarAAnalisisLegal({ idProyecto });
        },
      }}
      cargar={async (idProyecto) => (await analisisRiesgoApi.obtenerAnalisisRiesgo({ idProyecto })).data}
      guardar={async (idProyecto, aplica, filas) =>
        (
          await analisisRiesgoApi.guardarAnalisisRiesgo({
            idProyecto,
            analisisRiesgoRequest: {
              tieneRiesgosDesastres: aplica,
              filas: filas.map((f) => ({
                descripcionRiesgo: f.descripcionRiesgo,
                probabilidad: f.probabilidad,
                impactoRiesgo: f.impactoRiesgo,
                accionMitigacion: f.accionMitigacion,
                costoAccionMitigacion: aNumero(f.costoAccionMitigacion),
              })) as FilaRiesgoRequest[],
            },
          })
        ).data
      }
      leer={(dato) => ({
        aplica: dato.tieneRiesgosDesastres,
        filas: (dato.filas ?? []) as Fila[],
        total: dato.totalAccionesMitigacion,
      })}
    />
  );
}
