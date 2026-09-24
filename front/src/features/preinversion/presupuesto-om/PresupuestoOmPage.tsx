import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import {
  presupuestoOmApi,
  catalogoInsumosApi,
  TipoCostoOM,
  type ActividadOM,
  type InsumoTipoResumen,
  type PresupuestoOM,
  type TablaCostosOM,
} from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { formatearMonto } from '../presupuesto/presupuestoFormSchema';
import { NuevaActividadModal } from './NuevaActividadModal';

const CLAVE = 'preinversion.presupuestoOm';
/** Mismo actor que el presupuesto de inversión (CU-PRE-17). */
const ROL_EDITA = 'TECNICO_URP';

const TIPOS_COSTO = [TipoCostoOM.Operacion, TipoCostoOM.Mantenimiento, TipoCostoOM.OM, TipoCostoOM.NoAplica] as const;

/** Qué tablas se dibujan según el tipo de costo elegido (RN04). */
function tablasDe(presupuesto: PresupuestoOM): { clave: 'OPERACION' | 'MANTENIMIENTO'; tabla: TablaCostosOM }[] {
  const tablas: { clave: 'OPERACION' | 'MANTENIMIENTO'; tabla: TablaCostosOM }[] = [];
  if (presupuesto.costosOperacion) tablas.push({ clave: 'OPERACION', tabla: presupuesto.costosOperacion });
  if (presupuesto.costosMantenimiento) tablas.push({ clave: 'MANTENIMIENTO', tabla: presupuesto.costosMantenimiento });
  return tablas;
}

/**
 * "Presupuesto de Operación y Mantenimiento" (CU-PRE-18, Anexo A.1).
 *
 * Primero se configura qué se va a registrar —operación, mantenimiento, ambos o
 * ninguno—, con cuántos períodos y a qué tasa de crecimiento; con eso el
 * servidor genera las columnas. Después se registran actividades en cada tabla,
 * cada una con sus insumos.
 *
 * Los totales y los precios ajustados los calcula el servidor: aquí sólo se
 * muestran. Y los períodos 2 en adelante llegan vacíos a propósito —la fórmula
 * de RN06 está ilegible en el documento fuente y el contrato lo deja anotado
 * como hueco—, así que la pantalla lo dice en vez de inventar un número.
 */
export function PresupuestoOmPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [presupuesto, setPresupuesto] = useState<PresupuestoOM | null>(null);
  const [insumos, setInsumos] = useState<InsumoTipoResumen[]>([]);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [nuevaEn, setNuevaEn] = useState<'OPERACION' | 'MANTENIMIENTO' | null>(null);

  // Configuración en edición, antes de pulsar "Aceptar".
  const [tipoCosto, setTipoCosto] = useState<string>('');
  const [vidaUtil, setVidaUtil] = useState('');
  const [tasa, setTasa] = useState('');

  const puedeEditar = hasRole(ROL_EDITA);
  // El contrato pone en null los ajustados cuando el actor no los puede ver (RN09).
  const muestraAjustados = presupuesto?.costosOperacion?.totalPorPeriodoPrecioAjustado != null
    || presupuesto?.costosMantenimiento?.totalPorPeriodoPrecioAjustado != null;

  const cargar = async () => {
    setCargando(true);
    try {
      const { data } = await presupuestoOmApi.obtenerPresupuestoOM({ idProyecto });
      setPresupuesto(data);
      setTipoCosto(data.tipoCosto ?? '');
      setVidaUtil(data.vidaUtil != null ? String(data.vidaUtil) : '');
      setTasa(data.tasaCrecimientoCostos != null ? String(data.tasaCrecimientoCostos) : '');
      setErrorCarga(null);
    } catch (error_) {
      setErrorCarga(mensajeDeError(toErrorApi(error_), t));
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    if (!idProyecto) return;
    void cargar();
    // El catálogo de insumos es el mismo de CU-PRE-17; si falla, el alta de
    // actividades queda sin insumos y se avisa ahí, no aquí.
    catalogoInsumosApi.listarInsumosTipo().then(({ data }: { data: InsumoTipoResumen[] }) => setInsumos(data)).catch(() => setInsumos([]));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idProyecto]);

  const aceptarConfiguracion = async () => {
    if (!tipoCosto) {
      await Swal.fire({ icon: 'warning', text: t(`${CLAVE}.elijaTipoCosto`) });
      return;
    }
    const noAplica = tipoCosto === TipoCostoOM.NoAplica;
    setGuardando(true);
    try {
      await presupuestoOmApi.configurarPresupuestoOM({
        idProyecto,
        configurarPresupuestoOMRequest: {
          tipoCosto: tipoCosto as PresupuestoOM['tipoCosto'],
          vidaUtil: noAplica || !vidaUtil ? undefined : Number(vidaUtil),
          tasaCrecimientoCostos: noAplica || tasa === '' ? undefined : Number(tasa),
        } as never,
      });
      await cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  const quitarActividad = async (tabla: 'OPERACION' | 'MANTENIMIENTO', actividad: ActividadOM) => {
    const { isConfirmed } = await Swal.fire({
      icon: 'warning',
      text: t(`${CLAVE}.confirmarQuitar`, { nombre: actividad.nombreActividad ?? actividad.numero }),
      showCancelButton: true,
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (!isConfirmed) return;
    try {
      await presupuestoOmApi.eliminarActividad({ idProyecto, tipoCostoTabla: tabla as never, idActividad: actividad.idActividad });
      await cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };

  const guardarTodo = async () => {
    setGuardando(true);
    try {
      await presupuestoOmApi.guardarPresupuestoOM({ idProyecto });
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
      await cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) return <p>{t('common.cargando')}</p>;
  if (errorCarga) {
    return (
      <div className="aviso-error" role="alert">
        <p>{errorCarga}</p>
      </div>
    );
  }
  if (!presupuesto) return null;

  const periodos = presupuesto.vidaUtil ?? 0;
  const columnas = Array.from({ length: periodos }, (_, i) => i);
  const tablas = tablasDe(presupuesto);
  const noAplica = presupuesto.tipoCosto === TipoCostoOM.NoAplica;

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t(`${CLAVE}.titulo`)}</span>
      </div>
      <div className="formbody">
        {/* RN04-RN06: qué se registra, cuántos períodos y a qué tasa. */}
        <section>
          <h2 className="seccion">{t(`${CLAVE}.configuracion`)}</h2>
          <div className="fr">
            <FormRow controlId="om-tipo" label={t(`${CLAVE}.tipoCosto`)} required>
              <select id="om-tipo" value={tipoCosto} disabled={!puedeEditar} onChange={(e) => setTipoCosto(e.target.value)}>
                <option value="">{t('common.seleccione')}</option>
                {TIPOS_COSTO.map((tipo) => (
                  <option key={tipo} value={tipo}>
                    {t(`${CLAVE}.tiposCosto.${tipo}`)}
                  </option>
                ))}
              </select>
            </FormRow>
            <FormRow controlId="om-vida" label={t(`${CLAVE}.vidaUtil`)}>
              <input
                id="om-vida"
                type="number"
                min={1}
                value={vidaUtil}
                disabled={!puedeEditar || tipoCosto === TipoCostoOM.NoAplica}
                onChange={(e) => setVidaUtil(e.target.value)}
              />
            </FormRow>
            <FormRow controlId="om-tasa" label={t(`${CLAVE}.tasaCrecimiento`)} ayuda={t(`${CLAVE}.tasaAyuda`)}>
              <input
                id="om-tasa"
                type="number"
                min={0}
                max={3}
                step="0.01"
                value={tasa}
                disabled={!puedeEditar || tipoCosto === TipoCostoOM.NoAplica}
                onChange={(e) => setTasa(e.target.value)}
              />
            </FormRow>
          </div>
          {puedeEditar && (
            <div className="acciones-form">
              <button type="button" className="btn primario" onClick={aceptarConfiguracion} disabled={guardando}>
                {t('common.aceptar')}
              </button>
            </div>
          )}
        </section>

        {noAplica && <p className="aviso-consulta">{t(`${CLAVE}.noAplica`)}</p>}

        {tablas.map(({ clave, tabla }) => (
          <section key={clave}>
            <h2 className="seccion">{t(`${CLAVE}.tabla.${clave}`)}</h2>
            <div className="tabla-cont">
              <table className="tabla-datos">
                <thead>
                  <tr>
                    <th>{t(`${CLAVE}.columnaNumero`)}</th>
                    <th>{t(`${CLAVE}.columnaActividad`)}</th>
                    {columnas.map((i) => (
                      <th key={i}>{t(`${CLAVE}.columnaPeriodo`, { numero: i + 1 })}</th>
                    ))}
                    {puedeEditar && <th>{t('common.acciones')}</th>}
                  </tr>
                </thead>
                <tbody>
                  {tabla.actividades.length === 0 && (
                    <tr>
                      <td className="vacio" colSpan={columnas.length + (puedeEditar ? 3 : 2)}>
                        {t(`${CLAVE}.sinActividades`)}
                      </td>
                    </tr>
                  )}
                  {tabla.actividades.map((actividad) => (
                    <tr key={actividad.idActividad}>
                      <td>{actividad.numero}</td>
                      <td>{actividad.nombreActividad ?? '—'}</td>
                      {columnas.map((i) => (
                        <td key={i} style={{ textAlign: 'right' }}>
                          {i === 0
                            ? formatearMonto(actividad.totalPeriodo1PrecioMercado)
                            : formatearMonto(actividad.costosProyectadosPeriodo2EnAdelante?.[i - 1] ?? null)}
                        </td>
                      ))}
                      {puedeEditar && (
                        <td>
                          <button type="button" className="btn neutro" onClick={() => quitarActividad(clave, actividad)}>
                            {t(`${CLAVE}.quitar`)}
                          </button>
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
                <tfoot>
                  <tr>
                    <th colSpan={2}>{t(`${CLAVE}.totalMercado`)}</th>
                    {columnas.map((i) => (
                      <td key={i} style={{ textAlign: 'right' }}>
                        {formatearMonto(tabla.totalPorPeriodoPrecioMercado?.[`periodo${i + 1}` as never] ?? null)}
                      </td>
                    ))}
                    {puedeEditar && <td />}
                  </tr>
                  {muestraAjustados && (
                    <tr>
                      <th colSpan={2}>{t(`${CLAVE}.totalAjustado`)}</th>
                      {columnas.map((i) => (
                        <td key={i} style={{ textAlign: 'right' }}>
                          {formatearMonto(tabla.totalPorPeriodoPrecioAjustado?.[`periodo${i + 1}` as never] ?? null)}
                        </td>
                      ))}
                      {puedeEditar && <td />}
                    </tr>
                  )}
                </tfoot>
              </table>
            </div>
            {puedeEditar && (
              <div className="acciones-form">
                <button type="button" className="btn secundario" onClick={() => setNuevaEn(clave)}>
                  {t(`${CLAVE}.agregarActividad`)}
                </button>
              </div>
            )}
          </section>
        ))}

        {periodos > 1 && tablas.length > 0 && (
          // El contrato marca este hueco: la fórmula de proyección de RN06 es
          // ilegible en el documento fuente y no se inventó ningún cálculo.
          <p className="nota-form">{t(`${CLAVE}.periodosPendientes`)}</p>
        )}

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/presupuesto`)}>
            {t('preinversion.registro.botonRegresar')}
          </button>
          {puedeEditar && tablas.length > 0 && (
            <button type="button" className="btn primario" onClick={guardarTodo} disabled={guardando}>
              {t('preinversion.registro.botonGuardar')}
            </button>
          )}
        </div>
      </div>

      {nuevaEn && (
        <NuevaActividadModal
          insumos={insumos}
          tabla={nuevaEn}
          onCerrar={() => setNuevaEn(null)}
          onGuardar={async (datos) => {
            await presupuestoOmApi.registrarActividad({
              idProyecto,
              tipoCostoTabla: nuevaEn as never,
              actividadRequest: datos as never,
            });
            setNuevaEn(null);
            await cargar();
          }}
        />
      )}
    </div>
  );
}
