import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { analisisMercadoApi, catalogoEtapasApi } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import type { AnalisisMercado, FilaAnalisisMercado } from '../../../api/generated/preinversion-mercado';

interface FilaMercado {
  codigoProducto: string;
  /** Lo calcula el servidor a partir del producto; aquí sólo se muestra. */
  unidadMedida: string;
  demanda: string;
  oferta: string;
  aniosAProyectar: string;
  tasaDemanda: string;
  tasaOferta: string;
  deficit: number | null;
  promedioDemanda: number | null;
  promedioOferta: number | null;
  promedioDeficit: number | null;
}

interface Producto {
  readonly codigoProducto: string;
  readonly producto: string;
}

const FILA_VACIA: FilaMercado = {
  codigoProducto: '',
  unidadMedida: '',
  demanda: '',
  oferta: '',
  aniosAProyectar: '',
  tasaDemanda: '',
  tasaOferta: '',
  deficit: null,
  promedioDemanda: null,
  promedioOferta: null,
  promedioDeficit: null,
};

/** Los campos que el usuario llena; los demás los calcula el servidor. */
const CAMPOS_EDITABLES = ['codigoProducto', 'demanda', 'oferta', 'aniosAProyectar', 'tasaDemanda', 'tasaOferta'] as const;
const NUMERICOS = ['demanda', 'oferta', 'aniosAProyectar', 'tasaDemanda', 'tasaOferta'] as const;

const texto = (valor: number | null | undefined) => (valor == null ? '' : String(valor));
const numero = (valor: string) => (valor.trim() === '' ? undefined : Number(valor));

const aFila = (fila: FilaAnalisisMercado): FilaMercado => ({
  codigoProducto: fila.producto?.codigoProducto ?? '',
  unidadMedida: fila.unidadMedida ?? '',
  demanda: texto(fila.demanda),
  oferta: texto(fila.oferta),
  aniosAProyectar: texto(fila.aniosAProyectar),
  tasaDemanda: texto(fila.tasaDemanda),
  tasaOferta: texto(fila.tasaOferta),
  deficit: fila.deficit ?? null,
  promedioDemanda: fila.promedioDemanda ?? null,
  promedioOferta: fila.promedioOferta ?? null,
  promedioDeficit: fila.promedioDeficit ?? null,
});

const aFilas = (datos: AnalisisMercado) => (datos.filas ?? []).map(aFila);

const vacia = (fila: FilaMercado) => CAMPOS_EDITABLES.every((c) => fila[c].trim() === '');
/** RN04: para guardar hace falta al menos una fila con sus seis campos llenos. */
const completa = (fila: FilaMercado) => CAMPOS_EDITABLES.every((c) => fila[c].trim() !== '');

const conFilaInicial = (filas: FilaMercado[], puedeEditar: boolean) =>
  filas.length === 0 && puedeEditar ? [{ ...FILA_VACIA }] : filas;

/** Un producto por código: el catálogo trae una entrada por producto e indicador. */
function productosDelCatalogo(entradas: readonly Producto[]): Producto[] {
  const porCodigo = new Map<string, Producto>();
  for (const e of entradas) if (!porCodigo.has(e.codigoProducto)) porCodigo.set(e.codigoProducto, e);
  return [...porCodigo.values()];
}

const moneda = (valor: number | null) =>
  valor == null ? '—' : valor.toLocaleString('es-SV', { maximumFractionDigits: 2 });

/**
 * Pestaña "Análisis de mercado" (CU-PRE-09, Anexo B.1), capítulo 1.3.2.1.
 *
 * Déficit, unidad de medida y los tres promedios los calcula el servidor: se
 * muestran, no se editan ni se envían, y se refrescan con la respuesta del
 * guardado. El guardado manda siempre la tabla completa (PUT de reemplazo).
 */
export function MercadoTab({ idProyecto, puedeEditar }: { readonly idProyecto: number; readonly puedeEditar: boolean }) {
  const { t } = useTranslation();
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [errorCatalogo, setErrorCatalogo] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [intentoGuardar, setIntentoGuardar] = useState(false);

  const { control, register, handleSubmit, watch, reset } = useForm<{ filas: FilaMercado[] }>({
    defaultValues: { filas: [] },
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'filas' });
  const filas = watch('filas');

  useEffect(() => {
    // El catálogo de productos y el análisis se piden a la vez: si el catálogo
    // falla, la tabla se sigue viendo y sólo se avisa que no hay productos.
    Promise.allSettled([
      analisisMercadoApi.obtenerAnalisisMercado({ idProyecto }),
      catalogoEtapasApi.listarProductosIndicadores(),
    ])
      .then(([mercado, catalogo]) => {
        if (mercado.status === 'fulfilled') reset({ filas: conFilaInicial(aFilas(mercado.value.data), puedeEditar) });
        else setErrorCarga(mensajeDeError(toErrorApi(mercado.reason), t));
        if (catalogo.status === 'fulfilled') setProductos(productosDelCatalogo(catalogo.value.data as Producto[]));
        else setErrorCatalogo(true);
      })
      .finally(() => setCargando(false));
  }, [idProyecto, puedeEditar, reset, t]);

  // RN05: al intentar guardar se sombrean en rojo los campos que falten de una fila empezada.
  const pendiente = (fila: FilaMercado, campo: (typeof CAMPOS_EDITABLES)[number]) =>
    intentoGuardar && puedeEditar && !vacia(fila) && fila[campo].trim() === '';

  const onSubmit = async ({ filas: valores }: { filas: FilaMercado[] }) => {
    setIntentoGuardar(true);
    // RN04: el back rechaza el guardado entero si ninguna fila está completa; se avisa antes.
    if (!valores.some(completa)) {
      await Swal.fire({ icon: 'error', text: t('preinversion.diagnostico.mercado.filaIncompleta') });
      return;
    }
    setGuardando(true);
    try {
      const { data } = await analisisMercadoApi.guardarAnalisisMercado({
        idProyecto,
        analisisMercadoRequest: {
          filas: valores
            .filter((f) => !vacia(f))
            .map((f) => ({
              producto: { codigoProducto: f.codigoProducto },
              demanda: numero(f.demanda),
              oferta: numero(f.oferta),
              aniosAProyectar: numero(f.aniosAProyectar),
              tasaDemanda: numero(f.tasaDemanda),
              tasaOferta: numero(f.tasaOferta),
            })),
        },
      });
      reset({ filas: conFilaInicial(aFilas(data), puedeEditar) });
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) return <p className="nota">{t('common.cargando')}</p>;
  if (errorCarga) return <p className="aviso-error">{errorCarga}</p>;

  const clave = 'preinversion.diagnostico.mercado';

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      {errorCatalogo && <p className="nota">{t(`${clave}.sinCatalogo`)}</p>}

      <div className="tabla-cont tabla-editable">
        <table>
          <thead>
            <tr>
              <th>{t(`${clave}.producto`)}</th>
              <th>{t(`${clave}.unidadMedida`)}</th>
              {/* El orden sigue al de los campos editables (NUMERICOS) y luego los calculados. */}
              <th>{t(`${clave}.demanda`)}</th>
              <th>{t(`${clave}.oferta`)}</th>
              <th>{t(`${clave}.anios`)}</th>
              <th>{t(`${clave}.tasaDemanda`)}</th>
              <th>{t(`${clave}.tasaOferta`)}</th>
              <th>{t(`${clave}.deficit`)}</th>
              <th>{t(`${clave}.promedioDemanda`)}</th>
              <th>{t(`${clave}.promedioOferta`)}</th>
              <th>{t(`${clave}.promedioDeficit`)}</th>
              {puedeEditar && <th>{t('common.acciones')}</th>}
            </tr>
          </thead>
          <tbody>
            {fields.map((campo, indice) => {
              const fila = filas?.[indice] ?? FILA_VACIA;
              const numeroFila = indice + 1;
              return (
                <tr key={campo.id}>
                  <td>
                    <select
                      aria-label={t(`${clave}.productoFila`, { numero: numeroFila })}
                      className={pendiente(fila, 'codigoProducto') ? 'malo' : undefined}
                      disabled={!puedeEditar || errorCatalogo}
                      {...register(`filas.${indice}.codigoProducto`)}
                    >
                      <option value="">{t('common.seleccione')}</option>
                      {productos.map((p) => (
                        <option key={p.codigoProducto} value={p.codigoProducto}>
                          {p.producto}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td className="mono">{fila.unidadMedida || '—'}</td>
                  {NUMERICOS.map((n) => (
                    <td key={n}>
                      <input
                        type="number"
                        step="any"
                        min={n === 'demanda' || n === 'oferta' ? 0 : undefined}
                        aria-label={t(`${clave}.${n}Fila`, { numero: numeroFila })}
                        className={pendiente(fila, n) ? 'malo' : undefined}
                        readOnly={!puedeEditar}
                        {...register(`filas.${indice}.${n}`)}
                      />
                    </td>
                  ))}
                  {/* Calculados por el servidor; se reordenan aquí para leer la tabla como el Anexo B.1. */}
                  <td className="mono">{moneda(fila.deficit)}</td>
                  <td className="mono">{moneda(fila.promedioDemanda)}</td>
                  <td className="mono">{moneda(fila.promedioOferta)}</td>
                  <td className="mono">{moneda(fila.promedioDeficit)}</td>
                  {puedeEditar && (
                    <td>
                      <button
                        type="button"
                        className="btn neutro"
                        onClick={() => remove(indice)}
                        aria-label={t(`${clave}.eliminarFila`, { numero: numeroFila })}
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
          <button type="submit" className="btn primario" disabled={guardando}>
            {t('common.guardar')}
          </button>
        </div>
      )}
    </form>
  );
}
