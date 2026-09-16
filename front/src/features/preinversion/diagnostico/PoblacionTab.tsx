import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { catalogoEtapasApi, poblacionApi } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import type { AnalisisPoblacion, FilaPoblacion } from '../../../api/generated/preinversion-poblacion';

/** Las tres filas que el usuario llena. "Población en espera" la calcula el servidor. */
const FILAS_EDITABLES = ['poblacionReferencia', 'poblacionAfectada', 'poblacionObjetivo'] as const;
type ClaveFila = (typeof FILAS_EDITABLES)[number];

interface Celda {
  ubicacion: string;
  numeroPersonas: string;
}

interface FilaFormulario {
  descripcion: string;
  ubicaciones: Celda[];
}

type ValoresFormulario = Record<ClaveFila, FilaFormulario>;

interface Ubicacion {
  readonly distrito: string;
}

const CELDA_VACIA: Celda = { ubicacion: '', numeroPersonas: '' };
/** RN06: la descripción sólo aplica a Afectada y Objetivo. */
const CON_DESCRIPCION = new Set<ClaveFila>(['poblacionAfectada', 'poblacionObjetivo']);

const texto = (valor: number | null | undefined) => (valor == null ? '' : String(valor));

function aFilaFormulario(fila: FilaPoblacion | undefined, columnas: number): FilaFormulario {
  const ubicaciones = (fila?.ubicaciones ?? []).map((c) => ({
    ubicacion: c.ubicacion ?? '',
    numeroPersonas: texto(c.numeroPersonas),
  }));
  while (ubicaciones.length < columnas) ubicaciones.push({ ...CELDA_VACIA });
  return { descripcion: fila?.descripcion ?? '', ubicaciones };
}

/** Todas las filas se dibujan con la misma cantidad de columnas de ubicación. */
const numeroDeColumnas = (datos: AnalisisPoblacion) =>
  Math.max(1, ...FILAS_EDITABLES.map((c) => (datos[c]?.ubicaciones ?? []).length));

function aValores(datos: AnalisisPoblacion): ValoresFormulario {
  const columnas = numeroDeColumnas(datos);
  return {
    poblacionReferencia: aFilaFormulario(datos.poblacionReferencia, columnas),
    poblacionAfectada: aFilaFormulario(datos.poblacionAfectada, columnas),
    poblacionObjetivo: aFilaFormulario(datos.poblacionObjetivo, columnas),
  };
}

const aPeticion = (fila: FilaFormulario, conDescripcion: boolean) => ({
  descripcion: conDescripcion ? fila.descripcion || undefined : undefined,
  ubicaciones: fila.ubicaciones
    .filter((c) => c.ubicacion.trim() !== '' || c.numeroPersonas.trim() !== '')
    .map((c) => ({
      ubicacion: c.ubicacion || undefined,
      numeroPersonas: c.numeroPersonas.trim() === '' ? undefined : Number(c.numeroPersonas),
    })),
});

const cifra = (valor: number | null | undefined) =>
  valor == null ? '—' : valor.toLocaleString('es-SV', { maximumFractionDigits: 2 });
const porcentaje = (valor: number | null | undefined) => (valor == null ? '—' : `${cifra(valor)} %`);

/** Los dos 400 del contrato se distinguen por código, no por el texto del mensaje. */
const TEXTO_POR_CODIGO: Record<string, string> = {
  POBLACION_AFECTADA_MAYOR_QUE_REFERENCIA: 'preinversion.diagnostico.poblacion.afectadaMayor',
  POBLACION_OBJETIVO_MAYOR_QUE_AFECTADA: 'preinversion.diagnostico.poblacion.objetivoMayor',
};

/**
 * Pestaña "Análisis de población" (CU-PRE-07, Anexo A.1), capítulo 1.3.2.1.
 *
 * Cuatro filas de población por columnas de ubicación. Las tres primeras se
 * llenan; "Población en espera", los totales y los porcentajes los calcula el
 * servidor, así que se muestran de la última respuesta y se refrescan al
 * guardar. Si el guardado se rechaza (población afectada mayor que la de
 * referencia, u objetivo mayor que la afectada) no se guarda nada.
 */
export function PoblacionTab({ idProyecto, puedeEditar }: { readonly idProyecto: number; readonly puedeEditar: boolean }) {
  const { t } = useTranslation();
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [calculado, setCalculado] = useState<AnalisisPoblacion | null>(null);
  const [ubicaciones, setUbicaciones] = useState<Ubicacion[]>([]);
  const [errorCatalogo, setErrorCatalogo] = useState(false);
  const [columnas, setColumnas] = useState(1);
  const [guardando, setGuardando] = useState(false);

  const { register, handleSubmit, reset } = useForm<ValoresFormulario>();

  useEffect(() => {
    Promise.allSettled([
      poblacionApi.obtenerAnalisisPoblacion({ idProyecto }),
      catalogoEtapasApi.listarUbicacionesGeograficas({}),
    ])
      .then(([poblacion, catalogo]) => {
        if (poblacion.status === 'fulfilled') {
          setCalculado(poblacion.value.data);
          setColumnas(numeroDeColumnas(poblacion.value.data));
          reset(aValores(poblacion.value.data));
        } else setErrorCarga(mensajeDeError(toErrorApi(poblacion.reason), t));
        if (catalogo.status === 'fulfilled') setUbicaciones(catalogo.value.data as Ubicacion[]);
        else setErrorCatalogo(true);
      })
      .finally(() => setCargando(false));
  }, [idProyecto, reset, t]);

  const onSubmit = async (valores: ValoresFormulario) => {
    setGuardando(true);
    try {
      const { data } = await poblacionApi.guardarAnalisisPoblacion({
        idProyecto,
        analisisPoblacionRequest: {
          poblacionReferencia: aPeticion(valores.poblacionReferencia, false),
          poblacionAfectada: aPeticion(valores.poblacionAfectada, true),
          poblacionObjetivo: aPeticion(valores.poblacionObjetivo, true),
        },
      });
      setCalculado(data);
      setColumnas(numeroDeColumnas(data));
      reset(aValores(data));
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      const api = toErrorApi(error_);
      const clave = api.codigo ? TEXTO_POR_CODIGO[api.codigo] : undefined;
      await Swal.fire({ icon: 'error', text: clave ? t(clave) : mensajeDeError(api, t) });
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) return <p className="nota">{t('common.cargando')}</p>;
  if (errorCarga) return <p className="aviso-error">{errorCarga}</p>;

  const base = 'preinversion.diagnostico.poblacion';
  const indices = [...Array.from({ length: columnas }).keys()];

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      {errorCatalogo && <p className="nota">{t(`${base}.sinCatalogo`)}</p>}

      <div className="tabla-cont">
        <table>
          <thead>
            <tr>
              <th>{t(`${base}.fila`)}</th>
              <th>{t(`${base}.descripcion`)}</th>
              {indices.map((i) => (
                <th key={i} colSpan={2}>
                  {t(`${base}.ubicacionNumero`, { numero: i + 1 })}
                </th>
              ))}
              <th>{t(`${base}.totalPersonas`)}</th>
              <th>{t(`${base}.porcentaje`)}</th>
            </tr>
          </thead>
          <tbody>
            {FILAS_EDITABLES.map((clave) => {
              const conDescripcion = CON_DESCRIPCION.has(clave);
              const fila = calculado?.[clave];
              return (
                <tr key={clave}>
                  <th scope="row">{t(`${base}.${clave}`)}</th>
                  <td>
                    <input
                      type="text"
                      aria-label={t(`${base}.descripcionDe`, { fila: t(`${base}.${clave}`) })}
                      // RN06: en Población de Referencia la descripción no aplica.
                      disabled={!conDescripcion}
                      readOnly={!puedeEditar}
                      {...register(`${clave}.descripcion`)}
                    />
                  </td>
                  {indices.map((i) => (
                    <td key={i} colSpan={2} className="celda-ubicacion">
                      <select
                        aria-label={t(`${base}.ubicacionDe`, { fila: t(`${base}.${clave}`), numero: i + 1 })}
                        disabled={!puedeEditar || errorCatalogo}
                        {...register(`${clave}.ubicaciones.${i}.ubicacion`)}
                      >
                        <option value="">{t('common.seleccione')}</option>
                        {ubicaciones.map((u) => (
                          <option key={u.distrito} value={u.distrito}>
                            {u.distrito}
                          </option>
                        ))}
                      </select>
                      <input
                        type="number"
                        min={0}
                        aria-label={t(`${base}.personasDe`, { fila: t(`${base}.${clave}`), numero: i + 1 })}
                        readOnly={!puedeEditar}
                        {...register(`${clave}.ubicaciones.${i}.numeroPersonas`)}
                      />
                    </td>
                  ))}
                  <td className="mono">{cifra(fila?.totalNumeroPersonas)}</td>
                  {/* RN04: la población de referencia no lleva porcentaje. */}
                  <td className="mono">{porcentaje(fila?.totalPorcentaje)}</td>
                </tr>
              );
            })}
            <tr className="fila-calculada">
              <th scope="row">{t(`${base}.poblacionEnEspera`)}</th>
              <td colSpan={1 + columnas * 2} className="nota">
                {t(`${base}.enEsperaCalculada`)}
              </td>
              <td className="mono">{cifra(calculado?.poblacionEnEspera?.totalNumeroPersonas)}</td>
              <td className="mono">{porcentaje(calculado?.poblacionEnEspera?.totalPorcentaje)}</td>
            </tr>
          </tbody>
        </table>
      </div>

      {puedeEditar && (
        <div className="acciones-form">
          <button type="button" className="btn secundario" onClick={() => setColumnas((n) => n + 1)}>
            {t(`${base}.agregarUbicacion`)}
          </button>
          <button type="submit" className="btn primario" disabled={guardando}>
            {t('common.guardar')}
          </button>
        </div>
      )}
    </form>
  );
}
