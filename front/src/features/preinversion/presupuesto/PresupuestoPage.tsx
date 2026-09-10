import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import {
  catalogoInsumosApi,
  presupuestoApi,
  FuenteFinanciamiento,
} from '../../../api/preinversionApi';
import type {
  InsumoTipoResumen,
  Macroactividad,
  MacroactividadRequest,
  Presupuesto,
  ProductoPresupuesto,
} from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { DetalleMacroactividadModal } from './DetalleMacroactividadModal';
import { formatearMonto } from './presupuestoFormSchema';

const ROL_EDITA = 'TECNICO_URP';

/**
 * "Presupuesto del Proyecto" (Anexo A.1 del CU-PRE-17).
 *
 * Los productos vienen de CU-PRE-11 y son de sólo lectura aquí (RN16): no se
 * pueden añadir ni eliminar, sólo colgarles macroactividades.
 *
 * Casi todo el cálculo es del servidor y llega resuelto: los totales por
 * período (RN04, RN07), el redondeo de la inversión estimada (RN12) y el
 * resumen por componente (RN09). La pantalla no recalcula nada de eso; sólo lo
 * muestra. Los precios ajustados llegan en `null` cuando el actor no es interno
 * (RN10/RN11), y esa es la señal para no pintar la columna.
 */
export function PresupuestoPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [presupuesto, setPresupuesto] = useState<Presupuesto | null>(null);
  const [insumos, setInsumos] = useState<InsumoTipoResumen[]>([]);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [periodosBorrador, setPeriodosBorrador] = useState('');
  const [fuentes, setFuentes] = useState<FuenteFinanciamiento[]>([]);
  const [fuenteRecursos, setFuenteRecursos] = useState('');
  const [detalle, setDetalle] = useState<{ producto: ProductoPresupuesto; macro?: Macroactividad } | null>(null);

  const puedeEditar = hasRole(ROL_EDITA);
  const periodos = presupuesto?.periodosEstimados ?? 0;
  // El contrato pone en null los ajustados cuando el actor no los puede ver.
  const muestraAjustados = presupuesto?.inversionEstimadaPreciosAjustados != null;

  const cargar = async () => {
    setCargando(true);
    setErrorCarga(null);
    try {
      const [pres, cat, fue] = await Promise.all([
        presupuestoApi.obtenerPresupuesto({ idProyecto }),
        catalogoInsumosApi.listarInsumosTipo(),
        presupuestoApi.obtenerFuentesFinanciamiento({ idProyecto }),
      ]);
      setPresupuesto(pres.data);
      setInsumos(cat.data);
      setFuentes(fue.data.fuentesFinanciamiento ?? []);
      setFuenteRecursos(fue.data.fuenteRecursos ?? '');
      setPeriodosBorrador(pres.data.periodosEstimados != null ? String(pres.data.periodosEstimados) : '');
    } catch (error_) {
      setErrorCarga(mensajeDeError(toErrorApi(error_), t));
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    if (idProyecto) void cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idProyecto]);

  // "Generar las columnas de período" (RN06).
  const aceptarPeriodos = async () => {
    const cantidad = Number(periodosBorrador);
    if (!Number.isInteger(cantidad) || cantidad <= 0) return;
    try {
      const { data } = await presupuestoApi.configurarPeriodosEjecucion({
        idProyecto,
        configurarPeriodosEjecucionRequest: { periodosEstimados: cantidad },
      });
      setPresupuesto(data);
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };

  const guardarMacroactividad = async (datos: MacroactividadRequest) => {
    if (!detalle) return;
    try {
      await presupuestoApi.registrarMacroactividad({
        idProyecto,
        idProducto: detalle.producto.numero,
        macroactividadRequest: datos,
      });
      setDetalle(null);
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
      // Los totales se trasladan al presupuesto (RN07): los recalcula el
      // servidor, así que se relee en vez de recomponerlos aquí.
      await cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };

  const guardarTodo = async () => {
    // "cada producto debe contar con al menos una macroactividad registrada"
    const sinMacro = (presupuesto?.productos ?? []).filter((p) => (p.macroactividades ?? []).length === 0);
    if (sinMacro.length > 0) {
      await Swal.fire({
        icon: 'warning',
        text: t('preinversion.presupuesto.faltaMacroactividad', {
          productos: sinMacro.map((p) => p.numero).join(', '),
        }),
      });
      return;
    }
    setGuardando(true);
    try {
      await presupuestoApi.guardarFuentesFinanciamiento({
        idProyecto,
        fuentesFinanciamientoRequest: { fuentesFinanciamiento: fuentes, fuenteRecursos: fuenteRecursos || undefined },
      });
      await presupuestoApi.guardarPresupuesto({ idProyecto });
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

  const columnas = Array.from({ length: periodos }, (_, i) => i);

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t('preinversion.presupuesto.titulo')}</span>
      </div>
      <div className="formbody">
        {/* Períodos estimados para la ejecución (RN06). */}
        <div className="filtros">
          <div className="campo">
            <label htmlFor="periodos">{t('preinversion.presupuesto.periodosEstimados')}</label>
            <input
              id="periodos"
              type="number"
              min={1}
              value={periodosBorrador}
              readOnly={!puedeEditar}
              onChange={(e) => setPeriodosBorrador(e.target.value)}
            />
          </div>
          {puedeEditar && (
            <div className="campo">
              <button type="button" className="btn primario" onClick={aceptarPeriodos}>
                {t('common.aceptar')}
              </button>
            </div>
          )}
        </div>

        {periodos === 0 ? (
          <p className="nota">{t('preinversion.presupuesto.sinPeriodos')}</p>
        ) : (
          <div className="tabla-cont">
            <table className="tabla">
              <thead>
                <tr>
                  <th>{t('preinversion.presupuesto.colNumero')}</th>
                  <th>{t('preinversion.presupuesto.colProducto')}</th>
                  {columnas.map((p) => (
                    <th key={p}>{t('preinversion.presupuesto.periodo', { numero: p })}</th>
                  ))}
                  <th>{t('preinversion.presupuesto.colTotal')}</th>
                  {puedeEditar && <th />}
                </tr>
              </thead>
              <tbody>
                {(presupuesto?.productos ?? []).map((producto) => (
                  <>
                    <tr key={`p-${producto.numero}`} className="fila-producto">
                      <td>{producto.numero}</td>
                      {/* RN16: el producto no se edita aquí. */}
                      <td>{producto.producto?.producto ?? producto.producto?.codigoProducto ?? '—'}</td>
                      {columnas.map((p) => (
                        <td key={p}>{formatearMonto(producto.costoProductoPorPeriodo?.[p])}</td>
                      ))}
                      <td>{formatearMonto(producto.costoProductoTotal)}</td>
                      {puedeEditar && (
                        <td>
                          <button
                            type="button"
                            className="btn secundario"
                            onClick={() => setDetalle({ producto })}
                          >
                            {t('preinversion.presupuesto.agregarMacroactividad')}
                          </button>
                        </td>
                      )}
                    </tr>
                    {(producto.macroactividades ?? []).map((macro) => (
                      <tr key={`m-${macro.idMacroactividad}`} className="fila-macro">
                        <td>{macro.numero}</td>
                        <td>{macro.nombreMacroactividad}</td>
                        {columnas.map((p) => (
                          <td key={p}>{formatearMonto(macro.totalPeriodoPrecioMercado?.[p])}</td>
                        ))}
                        <td />
                        {puedeEditar && (
                          <td>
                            <button
                              type="button"
                              className="btn neutro"
                              onClick={() => setDetalle({ producto, macro })}
                            >
                              {t('common.editar')}
                            </button>
                          </td>
                        )}
                      </tr>
                    ))}
                  </>
                ))}
              </tbody>
              <tfoot>
                <tr>
                  <th colSpan={2}>{t('preinversion.presupuesto.inversionMercado')}</th>
                  {columnas.map((p) => (
                    <td key={p}>{formatearMonto(presupuesto?.inversionEstimadaPreciosMercado?.porPeriodo?.[p])}</td>
                  ))}
                  <td>{formatearMonto(presupuesto?.inversionEstimadaPreciosMercado?.total)}</td>
                  {puedeEditar && <td />}
                </tr>
                {muestraAjustados && (
                  <tr>
                    <th colSpan={2}>{t('preinversion.presupuesto.inversionAjustada')}</th>
                    {columnas.map((p) => (
                      <td key={p}>{formatearMonto(presupuesto?.inversionEstimadaPreciosAjustados?.porPeriodo?.[p])}</td>
                    ))}
                    <td>{formatearMonto(presupuesto?.inversionEstimadaPreciosAjustados?.total)}</td>
                    {puedeEditar && <td />}
                  </tr>
                )}
              </tfoot>
            </table>
          </div>
        )}

        {/* Resumen presupuesto por componente (Anexo A.4, RN09). Lo calcula el
            servidor; aquí sólo se muestra. */}
        <h3 className="seccion">{t('preinversion.presupuesto.resumenComponente')}</h3>
        <div className="tabla-cont">
          <table className="tabla">
            <thead>
              <tr>
                <th>{t('preinversion.presupuesto.colComponente')}</th>
                <th>{t('preinversion.presupuesto.colCosto')}</th>
              </tr>
            </thead>
            <tbody>
              {(presupuesto?.resumenPorComponente ?? []).map((fila) => (
                <tr key={fila.componente?.codigo ?? fila.componente?.nombre}>
                  <td>{fila.componente?.nombre ?? '—'}</td>
                  <td>{formatearMonto(fila.costo)}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <th>{t('preinversion.presupuesto.total')}</th>
                <td>{formatearMonto(presupuesto?.totalResumenPorComponente)}</td>
              </tr>
            </tfoot>
          </table>
        </div>

        {/* Fuente de financiamiento y de recursos (Anexo A.5, RN14). */}
        <h3 className="seccion">{t('preinversion.presupuesto.fuentes')}</h3>
        {fuentes.map((fuente, indice) => (
          <div className="fuente-fila" key={`${fuente}-${indice}`}>
            <select
              aria-label={t('preinversion.presupuesto.fuenteNumero', { numero: indice + 1 })}
              value={fuente}
              disabled={!puedeEditar}
              onChange={(e) =>
                setFuentes(fuentes.map((f, i) => (i === indice ? (e.target.value as FuenteFinanciamiento) : f)))
              }
            >
              {Object.values(FuenteFinanciamiento).map((valor) => (
                <option key={valor} value={valor}>
                  {t(`preinversion.presupuesto.fuente.${valor}`)}
                </option>
              ))}
            </select>
            {puedeEditar && (
              <button
                type="button"
                className="btn neutro"
                aria-label={t('preinversion.presupuesto.quitarFuente', { numero: indice + 1 })}
                onClick={() => setFuentes(fuentes.filter((_, i) => i !== indice))}
              >
                ✕
              </button>
            )}
          </div>
        ))}
        {puedeEditar && (
          <button
            type="button"
            className="btn secundario"
            onClick={() => setFuentes([...fuentes, FuenteFinanciamiento.FondoGeneral])}
          >
            {t('preinversion.presupuesto.agregarFuente')}
          </button>
        )}

        <div className="campo crece">
          <label htmlFor="fuente-recursos">{t('preinversion.presupuesto.fuenteRecursos')}</label>
          <input
            id="fuente-recursos"
            type="text"
            value={fuenteRecursos}
            readOnly={!puedeEditar}
            onChange={(e) => setFuenteRecursos(e.target.value)}
          />
        </div>
        {/* El contrato modela la fuente de recursos como texto libre a propósito:
            está pendiente de decidir a qué catálogo enlaza. No se inventa. */}
        <p className="nota-form">{t('preinversion.presupuesto.notaFuenteRecursos')}</p>

        <div className="acciones-form">
          <button
            type="button"
            className="btn neutro"
            onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/ruta-preinversion`)}
          >
            {t('common.regresar')}
          </button>
          {puedeEditar && (
            <button type="button" className="btn primario" disabled={guardando} onClick={guardarTodo}>
              {guardando ? t('common.guardando') : t('common.guardar')}
            </button>
          )}
        </div>
      </div>

      {detalle && (
        <DetalleMacroactividadModal
          abierto
          periodos={periodos}
          insumosCatalogo={insumos}
          macroactividad={detalle.macro}
          muestraAjustados={muestraAjustados}
          onGuardar={guardarMacroactividad}
          onCerrar={() => setDetalle(null)}
        />
      )}
    </div>
  );
}
