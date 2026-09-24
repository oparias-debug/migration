import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { ActividadOMRequest, InsumoTipoResumen } from '../../../api/preinversionApi';
import { aNumero, formatearMonto } from '../presupuesto/presupuestoFormSchema';

const CLAVE = 'preinversion.presupuestoOm';

interface Props {
  readonly insumos: InsumoTipoResumen[];
  readonly tabla: 'OPERACION' | 'MANTENIMIENTO';
  readonly onGuardar: (datos: ActividadOMRequest) => Promise<void>;
  readonly onCerrar: () => void;
}

/**
 * Alta de una actividad de operación o mantenimiento (Anexo A.2).
 *
 * Sólo se registra el período 1 (RN06): los siguientes los proyecta el sistema,
 * y esa proyección está pendiente de que el negocio aporte la fórmula. La tabla
 * se pinta con el catálogo de insumos completo, como en CU-PRE-17, y se envían
 * únicamente las filas con costo.
 */
export function NuevaActividadModal({ insumos, tabla, onGuardar, onCerrar }: Props) {
  const { t } = useTranslation();
  const dialogo = useRef<HTMLDialogElement>(null);
  const [nombre, setNombre] = useState('');
  const [costos, setCostos] = useState<Record<string, string>>({});
  const [error, setError] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  useEffect(() => {
    dialogo.current?.showModal();
  }, []);

  const total = Object.values(costos).reduce((suma, texto) => suma + (aNumero(texto) ?? 0), 0);

  const guardar = async () => {
    if (!nombre.trim()) {
      setError(t(`${CLAVE}.nombreObligatorio`));
      return;
    }
    // Se manda lo que dice el contrato (`tipoInsumo`). Hoy el servidor rechaza
    // toda actividad —lee `insumoTipoCodigo`, una clave que su propio DTO nunca
    // produce—, así que el alta falla con su mensaje hasta que lo corrija;
    // mandar además la clave que espera no sirve: Jackson rechaza el campo
    // desconocido antes de llegar al servicio.
    const filas = insumos
      .map((insumo) => ({ tipoInsumo: insumo.codigo, costoPeriodo1PrecioMercado: aNumero(costos[insumo.codigo] ?? '') }))
      .filter((fila) => fila.costoPeriodo1PrecioMercado != null);
    if (filas.length === 0) {
      setError(t(`${CLAVE}.alMenosUnInsumo`));
      return;
    }
    setError(null);
    setGuardando(true);
    try {
      await onGuardar({ nombreActividad: nombre.trim(), insumos: filas as never });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <dialog ref={dialogo} className="modal-detalle" onCancel={(e) => { e.preventDefault(); onCerrar(); }}>
      <form method="dialog" onSubmit={(e) => e.preventDefault()}>
        <h2>{t(`${CLAVE}.nuevaActividad`, { tabla: t(`${CLAVE}.tabla.${tabla}`) })}</h2>

        <div className="campo crece">
          <label htmlFor="om-nombre">{t(`${CLAVE}.nombreActividad`)}*</label>
          <input id="om-nombre" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} />
        </div>

        <div className="tabla-cont">
          <table className="tabla-datos">
            <thead>
              <tr>
                <th>{t(`${CLAVE}.insumo`)}</th>
                <th>{t(`${CLAVE}.columnaPeriodo`, { numero: 1 })}</th>
              </tr>
            </thead>
            <tbody>
              {insumos.length === 0 && (
                <tr>
                  <td className="vacio" colSpan={2}>{t(`${CLAVE}.sinInsumos`)}</td>
                </tr>
              )}
              {insumos.map((insumo) => (
                <tr key={insumo.codigo}>
                  <td>{insumo.nombre}</td>
                  <td>
                    <input
                      type="text"
                      inputMode="decimal"
                      aria-label={t(`${CLAVE}.costoDe`, { insumo: insumo.nombre })}
                      value={costos[insumo.codigo] ?? ''}
                      onChange={(e) => setCostos((antes) => ({ ...antes, [insumo.codigo]: e.target.value }))}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <th>{t(`${CLAVE}.totalActividad`)}</th>
                <td style={{ textAlign: 'right' }}>{formatearMonto(total || null)}</td>
              </tr>
            </tfoot>
          </table>
        </div>

        {error && <p className="error" role="alert">{error}</p>}

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={onCerrar} disabled={guardando}>
            {t('common.cancelar')}
          </button>
          <button type="button" className="btn primario" onClick={guardar} disabled={guardando}>
            {t('preinversion.registro.botonGuardar')}
          </button>
        </div>
      </form>
    </dialog>
  );
}
