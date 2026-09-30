import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { areaInfluenciaApi, catalogoEtapasApi } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import type { ErrorApi } from '../../../api/apiError';
import type { AreaInfluencia } from '../../../api/generated/preinversion-area-influencia';

export const UBICACION_ESPECIFICA_MAXLENGTH = 1000;

interface FilaArea {
  distrito: string;
  /** Los deriva el servidor del distrito; aquí sólo se muestran. */
  region: string;
  departamento: string;
  ubicacionEspecifica: string;
}

interface Ubicacion {
  readonly distrito: string;
  readonly departamento: string;
  readonly region: string;
}

const FILA_VACIA: FilaArea = { distrito: '', region: '', departamento: '', ubicacionEspecifica: '' };

const aFilas = (datos: AreaInfluencia): FilaArea[] =>
  (datos.filas ?? []).map((f) => ({
    distrito: f.distrito ?? '',
    region: f.region ?? '',
    departamento: f.departamento ?? '',
    ubicacionEspecifica: f.ubicacionEspecifica ?? '',
  }));

/**
 * El servidor numera las filas por su posición entre las enviadas y la tabla
 * muestra también las vacías, así que se traduce una numeración a la otra; sin
 * eso se sombrearía la fila equivocada.
 */
const filasDeLosDetalles = (error: ErrorApi, enviadas: FilaArea[], todas: FilaArea[]): Set<number> => {
  const posiciones = new Set<number>();
  for (const { campo } of error.detalles) {
    const indice = Number(/filas\[(\d+)\]/.exec(campo)?.[1]);
    const fila = enviadas[indice];
    if (fila) posiciones.add(todas.indexOf(fila));
  }
  return posiciones;
};

const conFilaInicial = (filas: FilaArea[], puedeEditar: boolean) =>
  filas.length === 0 && puedeEditar ? [{ ...FILA_VACIA }] : filas;

/**
 * Pestaña "Área de influencia" (CU-PRE-08), capítulo 1.3.2.1.
 *
 * Se elige el distrito; región y departamento los deriva el servidor y llegan
 * con la respuesta, así que se muestran vacíos hasta guardar. "Autocompletar"
 * trae las filas desde Población Objetivo (CU-PRE-07): las deja en la tabla,
 * pero no las guarda hasta pulsar Guardar.
 */
export function AreaInfluenciaTab({
  idProyecto,
  puedeEditar,
}: {
  readonly idProyecto: number;
  readonly puedeEditar: boolean;
}) {
  const { t } = useTranslation();
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [ubicaciones, setUbicaciones] = useState<Ubicacion[]>([]);
  const [errorCatalogo, setErrorCatalogo] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [autocompletando, setAutocompletando] = useState(false);
  const [intentoGuardar, setIntentoGuardar] = useState(false);
  // Filas que el servidor devolvió como no aceptadas.
  const [filasSeñaladas, setFilasSeñaladas] = useState<Set<number>>(new Set());

  const { control, register, handleSubmit, watch, reset } = useForm<{ filas: FilaArea[] }>({
    defaultValues: { filas: [] },
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'filas' });
  const filas = watch('filas');

  useEffect(() => {
    Promise.allSettled([
      areaInfluenciaApi.obtenerAreaInfluencia({ idProyecto }),
      catalogoEtapasApi.listarUbicacionesGeograficas({}),
    ])
      .then(([area, catalogo]) => {
        if (area.status === 'fulfilled') reset({ filas: conFilaInicial(aFilas(area.value.data), puedeEditar) });
        else setErrorCarga(mensajeDeError(toErrorApi(area.reason), t));
        if (catalogo.status === 'fulfilled') setUbicaciones(catalogo.value.data as Ubicacion[]);
        else setErrorCatalogo(true);
      })
      .finally(() => setCargando(false));
  }, [idProyecto, puedeEditar, reset, t]);

  const autocompletar = async () => {
    setAutocompletando(true);
    try {
      const { data } = await areaInfluenciaApi.autocompletarAreaInfluenciaDesdePoblacionObjetivo({ idProyecto });
      const traidas = aFilas(data);
      if (traidas.length === 0) {
        await Swal.fire({ icon: 'info', text: t('preinversion.diagnostico.area.sinPoblacion') });
        return;
      }
      reset({ filas: traidas });
      await Swal.fire({ icon: 'success', text: t('preinversion.diagnostico.area.autocompletado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setAutocompletando(false);
    }
  };

  const onSubmit = async ({ filas: valores }: { filas: FilaArea[] }) => {
    setIntentoGuardar(true);
    setFilasSeñaladas(new Set());
    // Va todo lo que tenga algo escrito, incluidas las filas sin distrito: quien
    // decide si una fila sirve es el servidor, y así dice cuál falla en vez de
    // que la pantalla la descarte en silencio y avise de que guardó. Sólo se
    // deja fuera la fila en blanco que la tabla añade al final.
    const conContenido = valores.filter((f) => f.distrito.trim() !== '' || f.ubicacionEspecifica.trim() !== '');
    setGuardando(true);
    try {
      const { data } = await areaInfluenciaApi.guardarAreaInfluencia({
        idProyecto,
        areaInfluenciaRequest: {
          filas: conContenido.map((f) => ({
            distrito: f.distrito,
            ubicacionEspecifica: f.ubicacionEspecifica || undefined,
          })),
        },
      });
      reset({ filas: conFilaInicial(aFilas(data), puedeEditar) });
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      const api = toErrorApi(error_);
      // El servidor señala la fila que no acepta —sin distrito, un distrito que
      // no existe o una repetida— con su posición entre las que se enviaron.
      setFilasSeñaladas(filasDeLosDetalles(api, conContenido, valores));
      // El 404 de este guardado es ambiguo: puede ser el proyecto o un distrito
      // que el catálogo ya no tiene. No se afirma cuál de los dos.
      const texto = api.estadoHttp === 404 ? t('preinversion.diagnostico.area.distritoODesconocido') : mensajeDeError(api, t);
      await Swal.fire({ icon: 'error', text: texto });
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) return <p className="nota">{t('common.cargando')}</p>;
  if (errorCarga) return <p className="aviso-error">{errorCarga}</p>;

  const clave = 'preinversion.diagnostico.area';
  const faltaDistrito = (fila: FilaArea, indice: number) =>
    (intentoGuardar && puedeEditar && fila.distrito.trim() === '') || filasSeñaladas.has(indice);

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      {errorCatalogo && <p className="nota">{t(`${clave}.sinCatalogo`)}</p>}

      <div className="tabla-cont tabla-editable">
        <table>
          <thead>
            <tr>
              <th>{t(`${clave}.distrito`)}</th>
              <th>{t(`${clave}.departamento`)}</th>
              <th>{t(`${clave}.region`)}</th>
              <th>{t(`${clave}.ubicacionEspecifica`)}</th>
              {puedeEditar && <th>{t('common.acciones')}</th>}
            </tr>
          </thead>
          <tbody>
            {fields.map((campo, indice) => {
              const fila = filas?.[indice] ?? FILA_VACIA;
              const numero = indice + 1;
              return (
                <tr key={campo.id}>
                  <td>
                    <select
                      aria-label={t(`${clave}.distritoFila`, { numero })}
                      className={faltaDistrito(fila, indice) ? 'malo' : undefined}
                      disabled={!puedeEditar || errorCatalogo}
                      {...register(`filas.${indice}.distrito`)}
                    >
                      <option value="">{t('common.seleccione')}</option>
                      {ubicaciones.map((u) => (
                        <option key={u.distrito} value={u.distrito}>
                          {u.distrito}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td>{fila.departamento || '—'}</td>
                  <td>{fila.region || '—'}</td>
                  <td>
                    <input
                      type="text"
                      maxLength={UBICACION_ESPECIFICA_MAXLENGTH}
                      aria-label={t(`${clave}.ubicacionEspecificaFila`, { numero })}
                      readOnly={!puedeEditar}
                      {...register(`filas.${indice}.ubicacionEspecifica`)}
                    />
                  </td>
                  {puedeEditar && (
                    <td>
                      <button
                        type="button"
                        className="btn neutro"
                        onClick={() => remove(indice)}
                        aria-label={t(`${clave}.eliminarFila`, { numero })}
                      >
                        {t('common.eliminar')}
                      </button>
                    </td>
                  )}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {puedeEditar && (
        <div className="acciones-form">
          <button type="button" className="btn secundario" onClick={() => append({ ...FILA_VACIA })}>
            {t(`${clave}.agregar`)}
          </button>
          <button type="button" className="btn secundario" onClick={autocompletar} disabled={autocompletando}>
            {t(`${clave}.autocompletar`)}
          </button>
          <button type="submit" className="btn primario" disabled={guardando}>
            {t('common.guardar')}
          </button>
        </div>
      )}
    </form>
  );
}
