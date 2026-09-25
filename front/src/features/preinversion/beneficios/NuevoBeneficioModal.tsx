import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { TipoIngreso, type BeneficioRequest, type ParametroResumen, type TipoBeneficio } from '../../../api/preinversionApi';
import { aNumero, formatearMonto } from '../presupuesto/presupuestoFormSchema';

const CLAVE = 'preinversion.beneficios';

interface Props {
  readonly tipoBeneficio: TipoBeneficio;
  readonly parametros: ParametroResumen[];
  /** Períodos de la vida útil (CU-PRE-18): cuántos montos se piden en modo Manual. */
  readonly periodos: number;
  readonly onGuardar: (datos: BeneficioRequest) => Promise<void>;
  readonly onCerrar: () => void;
}

/**
 * "Detalle del Beneficio" (CU-PRE-20, Anexo A.2).
 *
 * El tipo de ingreso decide qué se pide: en Automático basta el monto del
 * período 1 y la tasa de crecimiento, y el servidor proyecta el resto; en
 * Manual se escribe el monto de cada período de la vida útil.
 *
 * Las tres validaciones son las que el servidor rechaza con 400 (parámetro,
 * tipo de ingreso y, en Manual, al menos un monto): se comprueban aquí para
 * decirlo en el campo en vez de en un aviso de error del servidor.
 */
export function NuevoBeneficioModal({ tipoBeneficio, parametros, periodos, onGuardar, onCerrar }: Props) {
  const { t } = useTranslation();
  const dialogo = useRef<HTMLDialogElement>(null);

  const [nombre, setNombre] = useState('');
  const [parametro, setParametro] = useState('');
  const [tipoIngreso, setTipoIngreso] = useState<string>('');
  const [montoPeriodo1, setMontoPeriodo1] = useState('');
  const [tasa, setTasa] = useState('');
  const [montos, setMontos] = useState<Record<number, string>>({});
  const [error, setError] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  useEffect(() => {
    dialogo.current?.showModal();
  }, []);

  const esManual = tipoIngreso === TipoIngreso.Manual;
  const esAutomatico = tipoIngreso === TipoIngreso.Automatico;

  const guardar = async () => {
    if (!parametro) return setError(t(`${CLAVE}.parametroObligatorio`));
    if (!tipoIngreso) return setError(t(`${CLAVE}.tipoIngresoObligatorio`));

    const porPeriodo = Array.from({ length: periodos }, (_, i) => aNumero(montos[i] ?? ''));
    if (esManual && porPeriodo.every((m) => m == null)) return setError(t(`${CLAVE}.algunMontoObligatorio`));

    setError(null);
    setGuardando(true);
    try {
      await onGuardar({
        tipoBeneficio,
        nombreBeneficio: nombre.trim() || undefined,
        parametro,
        tipoIngreso: tipoIngreso as TipoIngreso,
        ...(esAutomatico
          ? {
              montoPeriodo1: aNumero(montoPeriodo1) ?? undefined,
              tasaCrecimientoProyectado: aNumero(tasa) ?? undefined,
            }
          : // El contrato admite huecos: un período sin monto viaja como null.
            { montosPrecioMercadoPorPeriodo: porPeriodo as number[] }),
      });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <dialog ref={dialogo} className="modal-detalle" onCancel={(e) => { e.preventDefault(); onCerrar(); }}>
      <form method="dialog" onSubmit={(e) => e.preventDefault()}>
        <h2>{t(`${CLAVE}.nuevoBeneficio`, { tipo: t(`${CLAVE}.tipos.${tipoBeneficio}`) })}</h2>

        <div className="fr">
          <div className="campo">
            <label htmlFor="ben-nombre">{t(`${CLAVE}.nombreBeneficio`)}</label>
            <input id="ben-nombre" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="ben-parametro">{t(`${CLAVE}.parametro`)}*</label>
            <select id="ben-parametro" value={parametro} onChange={(e) => setParametro(e.target.value)}>
              <option value="">{t('common.seleccione')}</option>
              {parametros.map((p) => (
                <option key={p.codigo} value={p.codigo}>
                  {p.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="campo">
            <label htmlFor="ben-tipo-ingreso">{t(`${CLAVE}.tipoIngreso`)}*</label>
            <select id="ben-tipo-ingreso" value={tipoIngreso} onChange={(e) => setTipoIngreso(e.target.value)}>
              <option value="">{t('common.seleccione')}</option>
              <option value={TipoIngreso.Automatico}>{t(`${CLAVE}.ingresos.AUTOMATICO`)}</option>
              <option value={TipoIngreso.Manual}>{t(`${CLAVE}.ingresos.MANUAL`)}</option>
            </select>
          </div>
        </div>

        {esAutomatico && (
          <div className="fr">
            <div className="campo">
              <label htmlFor="ben-monto1">{t(`${CLAVE}.montoPeriodo1`)}</label>
              <input id="ben-monto1" type="text" inputMode="decimal" value={montoPeriodo1} onChange={(e) => setMontoPeriodo1(e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="ben-tasa">{t(`${CLAVE}.tasaCrecimiento`)}</label>
              {/* Porcentaje, no factor: el contrato lo dice y el servidor lo
                  confirma (800 al 5 % da 840, 882...). Ojo, CU-PRE-18 usa la
                  convención contraria en su propia tasa. */}
              <input id="ben-tasa" type="text" inputMode="decimal" value={tasa} onChange={(e) => setTasa(e.target.value)} />
              <small className="ayuda">{t(`${CLAVE}.tasaAyuda`)}</small>
            </div>
          </div>
        )}

        {esManual && periodos === 0 && <p className="nota-form">{t(`${CLAVE}.sinVidaUtil`)}</p>}

        {esManual && periodos > 0 && (
          <div className="tabla-cont">
            <table className="tabla-datos">
              <thead>
                <tr>
                  <th>{t(`${CLAVE}.columnaPeriodo`, { numero: '' }).trim()}</th>
                  <th>{t(`${CLAVE}.montoPrecioMercado`)}</th>
                </tr>
              </thead>
              <tbody>
                {Array.from({ length: periodos }, (_, i) => (
                  <tr key={i}>
                    <td>{t(`${CLAVE}.columnaPeriodo`, { numero: i + 1 })}</td>
                    <td>
                      <input
                        type="text"
                        inputMode="decimal"
                        aria-label={t(`${CLAVE}.montoDelPeriodo`, { numero: i + 1 })}
                        value={montos[i] ?? ''}
                        onChange={(e) => setMontos((antes) => ({ ...antes, [i]: e.target.value }))}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot>
                <tr>
                  <th>{t(`${CLAVE}.totalBeneficio`)}</th>
                  <td style={{ textAlign: 'right' }}>
                    {formatearMonto(
                      Object.values(montos).reduce((suma, texto) => suma + (aNumero(texto) ?? 0), 0) || null,
                    )}
                  </td>
                </tr>
              </tfoot>
            </table>
          </div>
        )}

        {error && <p className="error" role="alert">{error}</p>}

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={onCerrar} disabled={guardando}>
            {t(`${CLAVE}.salir`)}
          </button>
          <button type="button" className="btn primario" onClick={guardar} disabled={guardando}>
            {t('preinversion.registro.botonGuardar')}
          </button>
        </div>
      </form>
    </dialog>
  );
}
