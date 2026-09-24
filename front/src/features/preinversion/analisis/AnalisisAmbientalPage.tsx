import { useTranslation } from 'react-i18next';
import {
  analisisAmbientalApi,
  Duracion,
  Magnitud,
  Medio,
  Reversibilidad,
  TipoImpacto,
  type AnalisisAmbiental,
  type FilaImpactoAmbientalRequest,
} from '../../../api/preinversionApi';
import { PantallaAnalisis, aNumero } from './analisisComun';
import type { ColumnaAnalisis } from './TablaAnalisis';

const CLAVE = 'preinversion.analisisAmbiental';
type Fila = Record<string, unknown>;

/** Pantalla "Análisis Ambiental" (CU-PRE-14, Anexo A.1), capítulo 1.3.2.3. */
export function AnalisisAmbientalPage() {
  const { t } = useTranslation();
  const opcion = (grupo: string) => (valor: string) => t(`${CLAVE}.${grupo}.${valor}`);

  const columnas: ColumnaAnalisis<Fila>[] = [
    { clave: 'medio', etiqueta: `${CLAVE}.medio`, tipo: 'select', opciones: Object.values(Medio), formato: opcion('medios') },
    { clave: 'impacto', etiqueta: `${CLAVE}.impacto`, tipo: 'texto' },
    { clave: 'tipoImpacto', etiqueta: `${CLAVE}.tipoImpacto`, tipo: 'select', opciones: Object.values(TipoImpacto), formato: opcion('tiposImpacto') },
    { clave: 'magnitud', etiqueta: `${CLAVE}.magnitud`, tipo: 'select', opciones: Object.values(Magnitud), formato: opcion('magnitudes') },
    { clave: 'duracion', etiqueta: `${CLAVE}.duracion`, tipo: 'select', opciones: Object.values(Duracion), formato: opcion('duraciones') },
    { clave: 'reversibilidad', etiqueta: `${CLAVE}.reversibilidad`, tipo: 'select', opciones: Object.values(Reversibilidad), formato: opcion('reversibilidades') },
    { clave: 'medidaGestion', etiqueta: `${CLAVE}.medidaGestion`, tipo: 'texto' },
    { clave: 'costoMedidaGestion', etiqueta: `${CLAVE}.costoMedidaGestion`, tipo: 'numero' },
  ];

  return (
    <PantallaAnalisis<AnalisisAmbiental, Fila>
      clave={CLAVE}
      columnas={columnas}
      filaVacia={() => ({})}
      pregunta={`${CLAVE}.pregunta`}
      // El back devuelve 500 mientras el proyecto no tenga análisis; guardar lo
      // crea. Ver la nota de `vacioSiFalla` en analisisComun.
      vacioSiFalla
      total={`${CLAVE}.total`}
      cargar={async (idProyecto) => (await analisisAmbientalApi.obtenerAnalisisAmbiental({ idProyecto })).data}
      guardar={async (idProyecto, aplica, filas) =>
        (
          await analisisAmbientalApi.guardarAnalisisAmbiental({
            idProyecto,
            analisisAmbientalRequest: {
              tieneImpactosAmbientales: aplica,
              filas: filas.map((f) => ({ ...f, costoMedidaGestion: aNumero(f.costoMedidaGestion) })) as FilaImpactoAmbientalRequest[],
            },
          })
        ).data
      }
      leer={(dato) => ({
        aplica: dato.tieneImpactosAmbientales,
        filas: (dato.filas ?? []) as Fila[],
        total: dato.totalCostoMedidasGestion,
      })}
    />
  );
}
