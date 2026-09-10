import { useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import type { InsumoTipoResumen, Macroactividad, MacroactividadRequest } from '../../../api/preinversionApi';
import {
  aNumero,
  formatearMonto,
  macroactividadSchema,
  totalPeriodoAjustado,
  totalPeriodoMercado,
  type MacroactividadFormValues,
} from './presupuestoFormSchema';

interface Props {
  readonly abierto: boolean;
  readonly periodos: number;
  readonly insumosCatalogo: InsumoTipoResumen[];
  /** Macroactividad existente al editar; ausente al crear una nueva. */
  readonly macroactividad?: Macroactividad;
  /** Los precios ajustados sólo los ve el actor interno (RN11). */
  readonly muestraAjustados: boolean;
  readonly onGuardar: (datos: MacroactividadRequest) => Promise<void>;
  readonly onCerrar: () => void;
}

/**
 * "Detalle de Macroactividad" (Anexo A.2).
 *
 * La tabla se pinta con TODOS los insumos del catálogo (RN05), no sólo con los
 * que ya tienen costo: es el propio CU quien lo pide.
 *
 * Los totales que se ven mientras se escribe son un anticipo (RN04 y F1 paso
 * 1.2). El servidor los recalcula al guardar y su resultado es el que queda.
 */
export function DetalleMacroactividadModal({
  abierto,
  periodos,
  insumosCatalogo,
  macroactividad,
  muestraAjustados,
  onGuardar,
  onCerrar,
}: Props) {
  const { t } = useTranslation();
  const dialogo = useRef<HTMLDialogElement>(null);

  const valoresIniciales = (): MacroactividadFormValues => ({
    nombreMacroactividad: macroactividad?.nombreMacroactividad ?? '',
    insumos: insumosCatalogo.map((insumo) => {
      const yaRegistrado = macroactividad?.insumos?.find((i) => i.tipoInsumo?.codigo === insumo.codigo);
      return {
        tipoInsumo: insumo.codigo,
        nombre: insumo.nombre,
        factorCorreccion: insumo.factorCorreccion,
        costos: Array.from({ length: periodos }, (_, p) => {
          const valor = yaRegistrado?.costosPorPeriodo?.[p];
          return valor == null ? '' : String(valor);
        }),
      };
    }),
  });

  const {
    register,
    handleSubmit,
    watch,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<MacroactividadFormValues>({
    resolver: zodResolver(macroactividadSchema),
    defaultValues: valoresIniciales(),
  });
  const insumos = watch('insumos');

  useEffect(() => {
    const el = dialogo.current;
    if (!el) return;
    if (abierto) {
      reset(valoresIniciales());
      if (!el.open) el.showModal();
    } else if (el.open) {
      el.close();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [abierto, macroactividad, periodos, insumosCatalogo]);

  // "Salir": avisa de que los datos no se guardan (Anexo A.2).
  const salir = async () => {
    const { isConfirmed } = await Swal.fire({
      text: t('preinversion.presupuesto.salirSinGuardar'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (isConfirmed) onCerrar();
  };

  const onSubmit = async (valores: MacroactividadFormValues) => {
    await onGuardar({
      nombreMacroactividad: valores.nombreMacroactividad.trim(),
      insumos: valores.insumos
        // Sólo viajan las filas con algún dato: el resto son el catálogo en blanco.
        .filter((fila) => fila.costos.some((c) => c.trim() !== ''))
        .map((fila) => ({
          tipoInsumo: fila.tipoInsumo,
          costosPorPeriodo: fila.costos.map((c) => aNumero(c)),
        })),
    });
  };

  const columnas = Array.from({ length: periodos }, (_, i) => i);

  return (
    <dialog ref={dialogo} className="modal-detalle" onCancel={(e) => { e.preventDefault(); void salir(); }}>
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <h2>{t('preinversion.presupuesto.detalleTitulo')}</h2>

        <div className="campo crece">
          <label htmlFor="macro-nombre">{t('preinversion.presupuesto.nombreMacroactividad')}</label>
          <input
            id="macro-nombre"
            type="text"
            className={errors.nombreMacroactividad ? 'malo' : undefined}
            {...register('nombreMacroactividad')}
          />
          {errors.nombreMacroactividad && <span className="error">{errors.nombreMacroactividad.message}</span>}
        </div>

        <div className="tabla-cont">
          <table className="tabla">
            <thead>
              <tr>
                <th>{t('preinversion.presupuesto.insumoTipo')}</th>
                {columnas.map((p) => (
                  <th key={p}>{t('preinversion.presupuesto.periodo', { numero: p })}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {insumos.map((fila, indice) => (
                <tr key={fila.tipoInsumo}>
                  <td>{fila.nombre}</td>
                  {columnas.map((p) => (
                    <td key={p}>
                      <input
                        type="text"
                        inputMode="decimal"
                        aria-label={t('preinversion.presupuesto.costoInsumoPeriodo', {
                          insumo: fila.nombre,
                          numero: p,
                        })}
                        {...register(`insumos.${indice}.costos.${p}` as const)}
                      />
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <th>{t('preinversion.presupuesto.totalMercado')}</th>
                {columnas.map((p) => (
                  <td key={p}>{formatearMonto(totalPeriodoMercado(insumos, p))}</td>
                ))}
              </tr>
              {muestraAjustados && (
                <tr>
                  <th>{t('preinversion.presupuesto.totalAjustado')}</th>
                  {columnas.map((p) => (
                    <td key={p}>{formatearMonto(totalPeriodoAjustado(insumos, p))}</td>
                  ))}
                </tr>
              )}
            </tfoot>
          </table>
        </div>

        {errors.insumos?.message && (
          <p className="error" role="alert">
            {errors.insumos.message}
          </p>
        )}

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={salir}>
            {t('preinversion.presupuesto.salir')}
          </button>
          <button type="submit" className="btn primario" disabled={isSubmitting}>
            {isSubmitting ? t('common.guardando') : t('common.guardar')}
          </button>
        </div>
      </form>
    </dialog>
  );
}
