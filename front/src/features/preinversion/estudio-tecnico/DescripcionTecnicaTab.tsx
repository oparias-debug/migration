import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { catalogoEtapasApi, catalogoUnidadesApi, descripcionTecnicaApi } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import type { DescripcionTecnica } from '../../../api/generated/preinversion-descripcion-tecnica';

export const DESCRIPCION_PRODUCTO_MAXLENGTH = 500;

const CLAVE = 'preinversion.estudioTecnico';

interface FilaTecnica {
  codigoProducto: string;
  componente: string;
  descripcionProducto: string;
  cantidad: string;
  unidadMedida: string;
}

interface Opcion {
  readonly valor: string;
  readonly texto: string;
}

const FILA_VACIA: FilaTecnica = {
  codigoProducto: '',
  componente: '',
  descripcionProducto: '',
  cantidad: '',
  unidadMedida: '',
};

interface Valores {
  descripcionProyecto: string;
  filas: FilaTecnica[];
}

const aValores = (datos: DescripcionTecnica): Valores => ({
  descripcionProyecto: datos.descripcionProyecto ?? '',
  filas: (datos.filas ?? []).map((f) => ({
    codigoProducto: f.producto?.codigoProducto ?? '',
    // En la lectura, componente y unidad de medida vienen resueltos a su entrada del catálogo.
    componente: f.componente?.codigo ?? '',
    descripcionProducto: f.descripcionProducto ?? '',
    cantidad: f.cantidad == null ? '' : String(f.cantidad),
    unidadMedida: f.unidadMedida?.unidadMedida ?? '',
  })),
});

const vacia = (fila: FilaTecnica) => Object.values(fila).every((v) => v.trim() === '');

/**
 * El contrato no exige ningún campo, pero el back sí: una fila sin componente
 * revienta con 500 (Componente.nombre es @NotBlank). Hasta que eso se corrija,
 * se avisa aquí en vez de dejar que el usuario reciba un error genérico.
 */
const sinComponente = (fila: FilaTecnica) => !vacia(fila) && fila.componente.trim() === '';

const conFilaInicial = (filas: FilaTecnica[], puedeEditar: boolean) =>
  filas.length === 0 && puedeEditar ? [{ ...FILA_VACIA }] : filas;

/** Un producto por código: el catálogo trae una entrada por producto e indicador. */
function productos(entradas: readonly { codigoProducto: string; producto: string }[]): Opcion[] {
  const porCodigo = new Map<string, Opcion>();
  for (const e of entradas) if (!porCodigo.has(e.codigoProducto)) porCodigo.set(e.codigoProducto, { valor: e.codigoProducto, texto: e.producto });
  return [...porCodigo.values()];
}

/**
 * Pestaña "Descripción técnica" (CU-PRE-11), primera del capítulo 1.3.2.2
 * "Estudio técnico" del árbol del sistema.
 *
 * La descripción del proyecto llega autocompletada del registro o de la última
 * opinión técnica (RN03) y se puede cambiar. En la tabla se elige producto,
 * componente y unidad de medida de sus catálogos; el guardado manda la tabla
 * completa (PUT de reemplazo).
 */
export function DescripcionTecnicaTab({
  idProyecto,
  puedeEditar,
}: {
  readonly idProyecto: number;
  readonly puedeEditar: boolean;
}) {
  const { t } = useTranslation();
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [opcionesProducto, setOpcionesProducto] = useState<Opcion[]>([]);
  const [componentes, setComponentes] = useState<Opcion[]>([]);
  const [unidades, setUnidades] = useState<Opcion[]>([]);
  const [catalogosCaidos, setCatalogosCaidos] = useState<string[]>([]);
  const [guardando, setGuardando] = useState(false);
  const [intentoGuardar, setIntentoGuardar] = useState(false);

  const { control, register, handleSubmit, watch, reset } = useForm<Valores>({
    defaultValues: { descripcionProyecto: '', filas: [] },
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'filas' });
  const filas = watch('filas');

  useEffect(() => {
    // Cada catálogo por separado: si uno falla, la pantalla sigue y sólo se avisa
    // que ese desplegable no tiene de dónde elegir.
    Promise.allSettled([
      descripcionTecnicaApi.obtenerDescripcionTecnica({ idProyecto }),
      catalogoEtapasApi.listarProductosIndicadores(),
      catalogoEtapasApi.listarTiposCosto(),
      catalogoUnidadesApi.listarUnidadesMedida(),
    ])
      .then(([tecnica, prod, costos, medidas]) => {
        if (tecnica.status === 'fulfilled') {
          const valores = aValores(tecnica.value.data);
          reset({ ...valores, filas: conFilaInicial(valores.filas, puedeEditar) });
        } else setErrorCarga(mensajeDeError(toErrorApi(tecnica.reason), t));

        const caidos: string[] = [];
        if (prod.status === 'fulfilled') setOpcionesProducto(productos(prod.value.data as { codigoProducto: string; producto: string }[]));
        else caidos.push(t('preinversion.estudioTecnico.producto'));
        if (costos.status === 'fulfilled')
          setComponentes((costos.value.data as { codigo: string; nombre: string }[]).map((c) => ({ valor: c.codigo, texto: c.nombre })));
        else caidos.push(t('preinversion.estudioTecnico.componente'));
        if (medidas.status === 'fulfilled')
          setUnidades((medidas.value.data as { unidadMedida: string }[]).map((u) => ({ valor: u.unidadMedida, texto: u.unidadMedida })));
        else caidos.push(t('preinversion.estudioTecnico.unidadMedida'));
        setCatalogosCaidos(caidos);
      })
      .finally(() => setCargando(false));
  }, [idProyecto, puedeEditar, reset, t]);

  // RN07: al intentar guardar se sombrea en rojo lo que falte de una fila empezada.
  const pendiente = (fila: FilaTecnica, campo: keyof FilaTecnica) =>
    intentoGuardar && puedeEditar && !vacia(fila) && fila[campo].trim() === '';

  const onSubmit = async (valores: Valores) => {
    setIntentoGuardar(true);
    if (valores.filas.some(sinComponente)) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.componenteObligatorio`) });
      return;
    }
    setGuardando(true);
    try {
      const { data } = await descripcionTecnicaApi.guardarDescripcionTecnica({
        idProyecto,
        descripcionTecnicaRequest: {
          descripcionProyecto: valores.descripcionProyecto || undefined,
          filas: valores.filas
            .filter((f) => !vacia(f))
            .map((f) => ({
              producto: { codigoProducto: f.codigoProducto },
              componente: f.componente || undefined,
              descripcionProducto: f.descripcionProducto || undefined,
              cantidad: f.cantidad.trim() === '' ? undefined : Number(f.cantidad),
              unidadMedida: f.unidadMedida || undefined,
            })),
        },
      });
      const guardado = aValores(data);
      reset({ ...guardado, filas: conFilaInicial(guardado.filas, puedeEditar) });
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) return <p className="nota">{t('common.cargando')}</p>;
  if (errorCarga) return <p className="aviso-error">{errorCarga}</p>;

  const clave = CLAVE;
  const selects: { campo: 'codigoProducto' | 'componente' | 'unidadMedida'; etiqueta: string; opciones: Opcion[] }[] = [
    { campo: 'codigoProducto', etiqueta: 'productoFila', opciones: opcionesProducto },
    { campo: 'componente', etiqueta: 'componenteFila', opciones: componentes },
  ];

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      {catalogosCaidos.length > 0 && (
        <p className="nota">{t(`${clave}.sinCatalogos`, { catalogos: catalogosCaidos.join(', ') })}</p>
      )}

      <div className="f w">
        <label htmlFor="id-descripcion-proyecto">{t(`${clave}.descripcionProyecto`)}</label>
        <textarea
          id="id-descripcion-proyecto"
          rows={4}
          readOnly={!puedeEditar}
          {...register('descripcionProyecto')}
        />
      </div>

      <div className="tabla-cont tabla-editable">
        <table>
          <thead>
            <tr>
              <th>{t(`${clave}.producto`)}</th>
              <th>{t(`${clave}.componente`)}</th>
              <th>{t(`${clave}.descripcionProducto`)}</th>
              <th>{t(`${clave}.cantidad`)}</th>
              <th>{t(`${clave}.unidadMedida`)}</th>
              {puedeEditar && <th>{t('common.acciones')}</th>}
            </tr>
          </thead>
          <tbody>
            {fields.map((campo, indice) => {
              const fila = filas?.[indice] ?? FILA_VACIA;
              const numero = indice + 1;
              return (
                <tr key={campo.id}>
                  {selects.map(({ campo: nombre, etiqueta, opciones }) => (
                    <td key={nombre}>
                      <select
                        aria-label={t(`${clave}.${etiqueta}`, { numero })}
                        className={pendiente(fila, nombre) ? 'malo' : undefined}
                        disabled={!puedeEditar || opciones.length === 0}
                        {...register(`filas.${indice}.${nombre}`)}
                      >
                        <option value="">{t('common.seleccione')}</option>
                        {opciones.map((o) => (
                          <option key={o.valor} value={o.valor}>
                            {o.texto}
                          </option>
                        ))}
                      </select>
                    </td>
                  ))}
                  <td>
                    <input
                      type="text"
                      maxLength={DESCRIPCION_PRODUCTO_MAXLENGTH}
                      aria-label={t(`${clave}.descripcionProductoFila`, { numero })}
                      className={pendiente(fila, 'descripcionProducto') ? 'malo' : undefined}
                      readOnly={!puedeEditar}
                      {...register(`filas.${indice}.descripcionProducto`)}
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      step="any"
                      min={0}
                      aria-label={t(`${clave}.cantidadFila`, { numero })}
                      className={pendiente(fila, 'cantidad') ? 'malo' : undefined}
                      readOnly={!puedeEditar}
                      {...register(`filas.${indice}.cantidad`)}
                    />
                  </td>
                  <td>
                    <select
                      aria-label={t(`${clave}.unidadMedidaFila`, { numero })}
                      className={pendiente(fila, 'unidadMedida') ? 'malo' : undefined}
                      disabled={!puedeEditar || unidades.length === 0}
                      {...register(`filas.${indice}.unidadMedida`)}
                    >
                      <option value="">{t('common.seleccione')}</option>
                      {unidades.map((u) => (
                        <option key={u.valor} value={u.valor}>
                          {u.texto}
                        </option>
                      ))}
                    </select>
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
          <button type="submit" className="btn primario" disabled={guardando}>
            {t('common.guardar')}
          </button>
        </div>
      )}
    </form>
  );
}
