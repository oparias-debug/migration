import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { catalogoEtapasApi, localizacionApi } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import type { FilaLocalizacionRequest, Localizacion } from '../../../api/generated/preinversion-localizacion';

export const ESPECIFIQUE_MAXLENGTH = 100;

const CLAVE = 'preinversion.localizacion';
/** Anexo B.1: con este distrito no hay punto que marcar en el mapa. */
const SIN_COORDENADAS = 'Nivel nacional';
/** Catálogo fijo del Anexo C.1. */
const PROPIETARIOS = [
  'INSTITUCION_PROPIETARIA_DEL_PROYECTO',
  'OTRA_INSTITUCION_PUBLICA',
  'LA_MUNICIPALIDAD',
  'COMODATO',
  'OTROS',
] as const;
/** RN07: todos menos el primero piden detalle en "Especifique". */
const PROPIETARIOS_CON_DETALLE = new Set<string>(PROPIETARIOS.slice(1));

interface FilaLocalizacion {
  departamento: string;
  distrito: string;
  direccionEspecifica: string;
  latitud: string;
  longitud: string;
  requiereAdquisicionTerreno: boolean;
  propietario: string;
  especifique: string;
}

interface Ubicacion {
  readonly distrito: string;
  readonly departamento: string;
}

const FILA_VACIA: FilaLocalizacion = {
  departamento: '',
  distrito: '',
  direccionEspecifica: '',
  latitud: '',
  longitud: '',
  requiereAdquisicionTerreno: false,
  propietario: '',
  especifique: '',
};

const texto = (valor: number | null | undefined) => (valor == null ? '' : String(valor));

const aFilas = (datos: Localizacion): FilaLocalizacion[] =>
  (datos.filas ?? []).map((f) => ({
    departamento: f.departamento ?? '',
    distrito: f.distrito ?? '',
    direccionEspecifica: f.direccionEspecifica ?? '',
    latitud: texto(f.coordenadas?.latitud),
    longitud: texto(f.coordenadas?.longitud),
    requiereAdquisicionTerreno: f.requiereAdquisicionTerreno ?? false,
    propietario: (f.propietario as string | undefined) ?? '',
    especifique: f.especifique ?? '',
  }));

const conFilaInicial = (filas: FilaLocalizacion[], puedeEditar: boolean) =>
  filas.length === 0 && puedeEditar ? [{ ...FILA_VACIA }] : filas;

const vacia = (fila: FilaLocalizacion) => fila.departamento === '' && fila.distrito === '';
/** Las coordenadas van completas o no van: el contrato exige latitud y longitud juntas. */
const coordenadasDe = (fila: FilaLocalizacion) =>
  fila.latitud.trim() !== '' && fila.longitud.trim() !== ''
    ? { latitud: Number(fila.latitud), longitud: Number(fila.longitud) }
    : undefined;

/**
 * Pestaña "Localización" (CU-PRE-12), segunda del capítulo 1.3.2.2 del árbol.
 *
 * Cada fila es una ubicación del proyecto: departamento y distrito del catálogo
 * geográfico, dirección, coordenadas, y si esa ubicación exige adquirir terreno,
 * de quién es y el detalle. El guardado manda la tabla completa (PUT de
 * reemplazo) y "Autocompletar" trae las filas del área de influencia (CU-PRE-08).
 *
 * Tres reglas condicionales del CU, que el servidor documenta pero no fuerza:
 * las coordenadas no aplican con distrito "Nivel nacional" (Anexo B.1), el
 * propietario sólo si la fila requiere terreno (RN06) y el detalle sólo para los
 * propietarios distintos de la institución del proyecto (RN07).
 */
export function LocalizacionTab({
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

  const { control, register, handleSubmit, watch, reset } = useForm<{ filas: FilaLocalizacion[] }>({
    defaultValues: { filas: [] },
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'filas' });
  const filas = watch('filas');

  useEffect(() => {
    Promise.allSettled([
      localizacionApi.obtenerLocalizacion({ idProyecto }),
      catalogoEtapasApi.listarUbicacionesGeograficas({}),
    ])
      .then(([localizacion, catalogo]) => {
        if (localizacion.status === 'fulfilled') {
          reset({ filas: conFilaInicial(aFilas(localizacion.value.data), puedeEditar) });
        } else setErrorCarga(mensajeDeError(toErrorApi(localizacion.reason), t));
        if (catalogo.status === 'fulfilled') setUbicaciones(catalogo.value.data as Ubicacion[]);
        else setErrorCatalogo(true);
      })
      .finally(() => setCargando(false));
  }, [idProyecto, puedeEditar, reset, t]);

  const autocompletar = async () => {
    setAutocompletando(true);
    try {
      const { data } = await localizacionApi.autocompletarLocalizacionDesdeAreaInfluencia({ idProyecto });
      const traidas = aFilas(data);
      if (traidas.length === 0) {
        await Swal.fire({ icon: 'info', text: t('preinversion.localizacion.sinAreaInfluencia') });
        return;
      }
      reset({ filas: traidas });
      await Swal.fire({ icon: 'success', text: t('preinversion.localizacion.autocompletado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setAutocompletando(false);
    }
  };

  const onSubmit = async ({ filas: valores }: { filas: FilaLocalizacion[] }) => {
    setGuardando(true);
    try {
      const { data } = await localizacionApi.guardarLocalizacion({
        idProyecto,
        localizacionRequest: {
          filas: valores
            .filter((f) => !vacia(f))
            .map((f) => ({
              departamento: f.departamento || undefined,
              distrito: f.distrito || undefined,
              direccionEspecifica: f.direccionEspecifica || undefined,
              coordenadas: f.distrito === SIN_COORDENADAS ? undefined : coordenadasDe(f),
              requiereAdquisicionTerreno: f.requiereAdquisicionTerreno,
              // RN06 y RN07: lo que no aplica no se manda, aunque haya quedado escrito.
              propietario: f.requiereAdquisicionTerreno
                ? ((f.propietario || undefined) as FilaLocalizacionRequest['propietario'])
                : undefined,
              especifique:
                f.requiereAdquisicionTerreno && PROPIETARIOS_CON_DETALLE.has(f.propietario)
                  ? f.especifique || undefined
                  : undefined,
            })),
        },
      });
      reset({ filas: conFilaInicial(aFilas(data), puedeEditar) });
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      const api = toErrorApi(error_);
      // Como en CU-PRE-08: el 404 de este guardado puede ser el proyecto o un
      // distrito que el catálogo geográfico no reconoce. No se afirma cuál.
      const texto = api.estadoHttp === 404 ? t(`${CLAVE}.distritoODesconocido`) : mensajeDeError(api, t);
      await Swal.fire({ icon: 'error', text: texto });
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) return <p className="nota">{t('common.cargando')}</p>;
  if (errorCarga) return <p className="aviso-error">{errorCarga}</p>;

  const clave = CLAVE;
  const departamentos = [...new Set(ubicaciones.map((u) => u.departamento))];
  const distritosDe = (departamento: string) =>
    ubicaciones.filter((u) => !departamento || u.departamento === departamento).map((u) => u.distrito);

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      {errorCatalogo && <p className="nota">{t(`${clave}.sinCatalogo`)}</p>}

      <div className="tabla-cont">
        <table>
          <thead>
            <tr>
              <th>{t(`${clave}.departamento`)}</th>
              <th>{t(`${clave}.distrito`)}</th>
              <th>{t(`${clave}.direccion`)}</th>
              <th>{t(`${clave}.latitud`)}</th>
              <th>{t(`${clave}.longitud`)}</th>
              <th>{t(`${clave}.requiereTerreno`)}</th>
              <th>{t(`${clave}.propietario`)}</th>
              <th>{t(`${clave}.especifique`)}</th>
              {puedeEditar && <th>{t('common.acciones')}</th>}
            </tr>
          </thead>
          <tbody>
            {fields.map((campo, indice) => {
              const fila = filas?.[indice] ?? FILA_VACIA;
              const numero = indice + 1;
              const sinCoordenadas = fila.distrito === SIN_COORDENADAS;
              const conPropietario = fila.requiereAdquisicionTerreno;
              const conDetalle = conPropietario && PROPIETARIOS_CON_DETALLE.has(fila.propietario);
              return (
                <tr key={campo.id}>
                  <td>
                    <select
                      aria-label={t(`${clave}.departamentoFila`, { numero })}
                      disabled={!puedeEditar || errorCatalogo}
                      {...register(`filas.${indice}.departamento`)}
                    >
                      <option value="">{t('common.seleccione')}</option>
                      {departamentos.map((d) => (
                        <option key={d} value={d}>
                          {d}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td>
                    <select
                      aria-label={t(`${clave}.distritoFila`, { numero })}
                      disabled={!puedeEditar || errorCatalogo}
                      {...register(`filas.${indice}.distrito`)}
                    >
                      <option value="">{t('common.seleccione')}</option>
                      {distritosDe(fila.departamento).map((d) => (
                        <option key={d} value={d}>
                          {d}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td>
                    <input
                      type="text"
                      aria-label={t(`${clave}.direccionFila`, { numero })}
                      readOnly={!puedeEditar}
                      {...register(`filas.${indice}.direccionEspecifica`)}
                    />
                  </td>
                  {(['latitud', 'longitud'] as const).map((eje) => (
                    <td key={eje}>
                      <input
                        type="number"
                        step="any"
                        aria-label={t(`${clave}.${eje}Fila`, { numero })}
                        /* Anexo B.1: a nivel nacional no hay punto que marcar. */
                        disabled={!puedeEditar || sinCoordenadas}
                        {...register(`filas.${indice}.${eje}`)}
                      />
                    </td>
                  ))}
                  <td>
                    <input
                      type="checkbox"
                      aria-label={t(`${clave}.requiereTerrenoFila`, { numero })}
                      disabled={!puedeEditar}
                      {...register(`filas.${indice}.requiereAdquisicionTerreno`)}
                    />
                  </td>
                  <td>
                    <select
                      aria-label={t(`${clave}.propietarioFila`, { numero })}
                      /* RN06: sólo aplica si esta fila requiere adquirir terreno. */
                      disabled={!puedeEditar || !conPropietario}
                      {...register(`filas.${indice}.propietario`)}
                    >
                      <option value="">{t('common.seleccione')}</option>
                      {PROPIETARIOS.map((v) => (
                        <option key={v} value={v}>
                          {t(`${clave}.propietarios.${v}`)}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td>
                    <input
                      type="text"
                      maxLength={ESPECIFIQUE_MAXLENGTH}
                      aria-label={t(`${clave}.especifiqueFila`, { numero })}
                      /* RN07: sólo para propietarios distintos de la institución del proyecto. */
                      readOnly={!puedeEditar}
                      disabled={!conDetalle}
                      {...register(`filas.${indice}.especifique`)}
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
