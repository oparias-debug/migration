import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { interesadosApi } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import type { InteresadoRequest, MatrizInteresados } from '../../../api/generated/preinversion-interesados';

/** Catálogo fijo del Anexo C.1: no es administrable, viaja en el propio contrato. */
const TIPOS = ['COOPERANTE', 'OPONENTE', 'BENEFICIARIO', 'PERJUDICADO'] as const;
const NIVELES = ['ALTO', 'BAJO'] as const;

interface FilaInteresado {
  nombreInteresado: string;
  tipo: string;
  nivelInfluencia: string;
  nivelInteres: string;
  estrategiaGestion: string;
}

const FILA_VACIA: FilaInteresado = {
  nombreInteresado: '',
  tipo: '',
  nivelInfluencia: '',
  nivelInteres: '',
  estrategiaGestion: '',
};

const aFilas = (matriz: MatrizInteresados): FilaInteresado[] =>
  (matriz.interesados ?? []).map((i) => ({
    nombreInteresado: i.nombreInteresado ?? '',
    tipo: i.tipo ?? '',
    nivelInfluencia: i.nivelInfluencia ?? '',
    nivelInteres: i.nivelInteres ?? '',
    estrategiaGestion: i.estrategiaGestion ?? '',
  }));

/** Siempre queda una fila en blanco donde escribir mientras se pueda editar (RN2-1). */
const conFilaInicial = (filas: FilaInteresado[], puedeEditar: boolean): FilaInteresado[] =>
  filas.length === 0 && puedeEditar ? [{ ...FILA_VACIA }] : filas;

const aPeticion = (fila: FilaInteresado): InteresadoRequest => ({
  nombreInteresado: fila.nombreInteresado || undefined,
  tipo: (fila.tipo || undefined) as InteresadoRequest['tipo'],
  nivelInfluencia: (fila.nivelInfluencia || undefined) as InteresadoRequest['nivelInfluencia'],
  nivelInteres: (fila.nivelInteres || undefined) as InteresadoRequest['nivelInteres'],
  estrategiaGestion: fila.estrategiaGestion || undefined,
});

const vacia = (fila: FilaInteresado) => Object.values(fila).every((v) => v.trim() === '');

/**
 * RN05: un mismo interesado puede repetirse si difiere en al menos una de las
 * cuatro columnas; si las cuatro coinciden, es un duplicado. El back no lo
 * valida, así que se avisa aquí antes de guardar.
 */
function indicesDuplicados(filas: readonly FilaInteresado[]): number[] {
  const vistas = new Map<string, number>();
  const repetidas: number[] = [];
  filas.forEach((fila, indice) => {
    if (vacia(fila)) return;
    const clave = [fila.nombreInteresado, fila.tipo, fila.nivelInfluencia, fila.nivelInteres]
      .map((v) => v.trim().toLocaleLowerCase())
      .join('|');
    const previa = vistas.get(clave);
    if (previa === undefined) vistas.set(clave, indice);
    else repetidas.push(indice);
  });
  return repetidas;
}

/**
 * Pestaña "Gestión de interesados" (CU-PRE-06, Anexo A.1), capítulo 1.3.2.1 del
 * árbol del sistema. Tabla de filas que se agregan y quitan en pantalla: el
 * guardado manda siempre la matriz completa (PUT de reemplazo).
 *
 * El Técnico PRE la consulta sin editar (x-roles del contrato).
 */
export function InteresadosTab({ idProyecto, puedeEditar }: { readonly idProyecto: number; readonly puedeEditar: boolean }) {
  const { t } = useTranslation();
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [intentoGuardar, setIntentoGuardar] = useState(false);

  const { control, register, handleSubmit, watch, reset } = useForm<{ interesados: FilaInteresado[] }>({
    defaultValues: { interesados: [] },
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'interesados' });
  const filas = watch('interesados');

  useEffect(() => {
    interesadosApi
      .obtenerMatrizInteresados({ idProyecto })
      .then(({ data }) => {
        reset({ interesados: conFilaInicial(aFilas(data), puedeEditar) });
      })
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
  }, [idProyecto, puedeEditar, reset, t]);

  // RN06: al intentar guardar, los campos que falten se sombrean en rojo.
  const pendiente = (valor: string) => intentoGuardar && puedeEditar && valor.trim() === '';
  const clase = (valor: string) => (pendiente(valor) ? 'malo' : undefined);
  const duplicadas = intentoGuardar ? indicesDuplicados(filas ?? []) : [];

  const onSubmit = async ({ interesados }: { interesados: FilaInteresado[] }) => {
    setIntentoGuardar(true);
    if (indicesDuplicados(interesados).length > 0) {
      await Swal.fire({ icon: 'error', text: t('preinversion.diagnostico.interesados.duplicado') });
      return;
    }
    setGuardando(true);
    try {
      // Las filas en blanco son andamiaje de la pantalla: no se mandan.
      const { data } = await interesadosApi.guardarMatrizInteresados({
        idProyecto,
        matrizInteresadosRequest: { interesados: interesados.filter((f) => !vacia(f)).map(aPeticion) },
      });
      reset({ interesados: conFilaInicial(aFilas(data), puedeEditar) });
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) return <p className="nota">{t('common.cargando')}</p>;
  if (errorCarga) return <p className="aviso-error">{errorCarga}</p>;

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <div className="tabla-cont">
        <table>
          <thead>
            <tr>
              <th>{t('preinversion.diagnostico.interesados.nombre')}</th>
              <th>{t('preinversion.diagnostico.interesados.tipo')}</th>
              <th>{t('preinversion.diagnostico.interesados.influencia')}</th>
              <th>{t('preinversion.diagnostico.interesados.interes')}</th>
              <th>{t('preinversion.diagnostico.interesados.estrategia')}</th>
              {puedeEditar && <th aria-label={t('common.acciones')} />}
            </tr>
          </thead>
          <tbody>
            {fields.map((campo, indice) => {
              const fila = filas?.[indice] ?? FILA_VACIA;
              const numero = indice + 1;
              return (
                <tr key={campo.id} className={duplicadas.includes(indice) ? 'fila-duplicada' : undefined}>
                  <td>
                    <input
                      type="text"
                      aria-label={t('preinversion.diagnostico.interesados.nombreFila', { numero })}
                      className={clase(fila.nombreInteresado)}
                      readOnly={!puedeEditar}
                      {...register(`interesados.${indice}.nombreInteresado`)}
                    />
                  </td>
                  <td>
                    <select
                      aria-label={t('preinversion.diagnostico.interesados.tipoFila', { numero })}
                      className={clase(fila.tipo)}
                      disabled={!puedeEditar}
                      {...register(`interesados.${indice}.tipo`)}
                    >
                      <option value="">{t('common.seleccione')}</option>
                      {TIPOS.map((v) => (
                        <option key={v} value={v}>
                          {t(`preinversion.diagnostico.interesados.tipos.${v}`)}
                        </option>
                      ))}
                    </select>
                  </td>
                  {(['nivelInfluencia', 'nivelInteres'] as const).map((campoNivel) => (
                    <td key={campoNivel}>
                      <select
                        aria-label={t(`preinversion.diagnostico.interesados.${campoNivel}Fila`, { numero })}
                        className={clase(fila[campoNivel])}
                        disabled={!puedeEditar}
                        {...register(`interesados.${indice}.${campoNivel}`)}
                      >
                        <option value="">{t('common.seleccione')}</option>
                        {NIVELES.map((v) => (
                          <option key={v} value={v}>
                            {t(`preinversion.diagnostico.interesados.niveles.${v}`)}
                          </option>
                        ))}
                      </select>
                    </td>
                  ))}
                  <td>
                    <input
                      type="text"
                      aria-label={t('preinversion.diagnostico.interesados.estrategiaFila', { numero })}
                      className={clase(fila.estrategiaGestion)}
                      readOnly={!puedeEditar}
                      {...register(`interesados.${indice}.estrategiaGestion`)}
                    />
                  </td>
                  {puedeEditar && (
                    <td>
                      <button
                        type="button"
                        className="btn neutro"
                        onClick={() => remove(indice)}
                        aria-label={t('preinversion.diagnostico.interesados.eliminarFila', { numero })}
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

      {fields.length === 0 && <p className="nota">{t('preinversion.diagnostico.interesados.sinFilas')}</p>}

      {puedeEditar && (
        <div className="acciones-form">
          <button type="button" className="btn secundario" onClick={() => append({ ...FILA_VACIA })}>
            {t('preinversion.diagnostico.interesados.agregar')}
          </button>
          <button type="submit" className="btn primario" disabled={guardando}>
            {t('common.guardar')}
          </button>
        </div>
      )}
    </form>
  );
}
