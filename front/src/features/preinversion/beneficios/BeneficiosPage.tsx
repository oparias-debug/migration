import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import {
  beneficiosApi,
  catalogoParametrosApi,
  TipoBeneficio,
  TipoBien,
  type Beneficio,
  type BeneficioRequest,
  type BeneficiosDelProyecto,
  type ParametroResumen,
} from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { aNumero, formatearMonto } from '../presupuesto/presupuestoFormSchema';
import { NuevoBeneficioModal } from './NuevoBeneficioModal';

const CLAVE = 'preinversion.beneficios';
/** Mismo actor que el resto de la formulación (x-roles del CU). */
const ROL_EDITA = 'TECNICO_URP';

const SECCIONES = [
  { tipo: TipoBeneficio.BeneficiosDirectos, lista: 'beneficiosDirectos' },
  { tipo: TipoBeneficio.BeneficiosIndirectos, lista: 'beneficiosIndirectos' },
  { tipo: TipoBeneficio.Externalidades, lista: 'externalidades' },
] as const;

const TIPOS_BIEN = [TipoBien.Equipos, TipoBien.Edificios, TipoBien.Terrenos, TipoBien.Vehiculos] as const;

/**
 * "Beneficios del Proyecto" (CU-PRE-20, Anexo A.1).
 *
 * Tres secciones —beneficios directos, indirectos y externalidades—, cada una
 * con sus beneficios y el monto de cada período. Debajo, el flujo de beneficios
 * del proyecto y el valor de rescate con su tipo de bien, que son los dos
 * únicos campos que se guardan desde esta pantalla.
 *
 * Los períodos salen de la vida útil que se configuró en CU-PRE-18 (RN05): si
 * allá no se configuró nada, aquí no hay columnas y se dice por qué, en vez de
 * mostrar una tabla vacía sin explicación.
 *
 * Los precios ajustados, el factor de corrección y el valor de rescate ajustado
 * sólo los manda el servidor a los usuarios internos (RN09, DN-01): al Técnico
 * URP le llegan nulos y esas filas no se pintan.
 */
export function BeneficiosPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [datos, setDatos] = useState<BeneficiosDelProyecto | null>(null);
  const [parametros, setParametros] = useState<ParametroResumen[]>([]);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [nuevoEn, setNuevoEn] = useState<TipoBeneficio | null>(null);

  const [valorRescate, setValorRescate] = useState('');
  const [tipoBien, setTipoBien] = useState('');

  const puedeEditar = hasRole(ROL_EDITA);

  const cargar = useCallback(async () => {
    const { data } = await beneficiosApi.obtenerBeneficiosProyecto({ idProyecto });
    setDatos(data);
    setValorRescate(data.valorRescate == null ? '' : String(data.valorRescate));
    setTipoBien(data.tipoBien ?? '');
  }, [idProyecto]);

  useEffect(() => {
    if (!idProyecto) return;
    Promise.all([cargar(), catalogoParametrosApi.listarParametrosBeneficio().then((r) => setParametros(r.data))])
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idProyecto]);

  const periodos = datos?.vidaUtil ?? 0;
  const columnas = Array.from({ length: periodos }, (_, i) => i);
  // El contrato pone en null lo que el actor no puede ver (RN09).
  const muestraAjustados = datos?.flujoBeneficiosPrecioAjustadoPorPeriodo != null;

  const guardarPantalla = async () => {
    setGuardando(true);
    try {
      await beneficiosApi.guardarBeneficiosProyecto({
        idProyecto,
        guardarBeneficiosProyectoRequest: {
          valorRescate: aNumero(valorRescate) ?? undefined,
          tipoBien: (tipoBien || undefined) as TipoBien | undefined,
        },
      });
      await cargar();
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  const quitar = async (beneficio: Beneficio) => {
    const confirma = await Swal.fire({
      icon: 'warning',
      text: t(`${CLAVE}.confirmarQuitar`),
      showCancelButton: true,
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (!confirma.isConfirmed) return;
    try {
      await beneficiosApi.eliminarBeneficio({ idProyecto, idBeneficio: beneficio.idBeneficio });
      await cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };

  const registrar = async (datosNuevos: BeneficioRequest) => {
    try {
      await beneficiosApi.registrarBeneficio({ idProyecto, beneficioRequest: datosNuevos });
      setNuevoEn(null);
      await cargar();
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };

  /** Nombre del parámetro, venga como objeto del catálogo o como texto. */
  const nombreParametro = (parametro: Beneficio['parametro']): string =>
    (typeof parametro === 'string' ? parametro : parametro?.nombre) || '—';

  /** Monto de un beneficio en un período; el servidor los devuelve por período. */
  const montoDe = (beneficio: Beneficio, indice: number) =>
    beneficio.montosPorPeriodo?.find((m) => m.periodo === indice + 1)?.montoPrecioMercado ?? null;

  return (
    <div className="formcard">
      <div className="formhead">{t(`${CLAVE}.titulo`)}</div>
      <div className="formbody">
        {errorCarga && (
          <div className="aviso-error" role="alert">
            {errorCarga}
          </div>
        )}
        {cargando && <p className="cargando">{t('common.cargando')}</p>}

        {!cargando && !errorCarga && (
          <>
            {periodos === 0 && <p className="aviso-consulta">{t(`${CLAVE}.sinVidaUtil`)}</p>}

            {SECCIONES.map(({ tipo, lista }) => (
              <section key={tipo}>
                <h2 className="seccion">{t(`${CLAVE}.tipos.${tipo}`)}</h2>
                <div className="tabla-cont">
                  <table className="tabla-datos">
                    <thead>
                      <tr>
                        <th>{t(`${CLAVE}.columnaBeneficio`)}</th>
                        <th>{t(`${CLAVE}.columnaParametro`)}</th>
                        <th>{t(`${CLAVE}.columnaTipoIngreso`)}</th>
                        {columnas.map((i) => (
                          <th key={i}>{t(`${CLAVE}.columnaPeriodo`, { numero: i + 1 })}</th>
                        ))}
                        {puedeEditar && <th>{t('common.acciones')}</th>}
                      </tr>
                    </thead>
                    <tbody>
                      {(datos?.[lista] ?? []).length === 0 && (
                        <tr>
                          <td className="vacio" colSpan={columnas.length + (puedeEditar ? 4 : 3)}>
                            {t(`${CLAVE}.sinBeneficios`)}
                          </td>
                        </tr>
                      )}
                      {(datos?.[lista] ?? []).map((beneficio) => (
                        <tr key={beneficio.idBeneficio}>
                          <td>{beneficio.nombreBeneficio ?? '—'}</td>
                          <td>{nombreParametro(beneficio.parametro)}</td>
                          <td>{t(`${CLAVE}.ingresos.${beneficio.tipoIngreso}`)}</td>
                          {columnas.map((i) => (
                            <td key={i} style={{ textAlign: 'right' }}>
                              {formatearMonto(montoDe(beneficio, i))}
                            </td>
                          ))}
                          {puedeEditar && (
                            <td>
                              <button type="button" className="btn neutro" onClick={() => void quitar(beneficio)}>
                                {t('common.quitar')}
                              </button>
                            </td>
                          )}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                {puedeEditar && (
                  <div className="acciones-form">
                    <button type="button" className="btn secundario" onClick={() => setNuevoEn(tipo)}>
                      {t(`${CLAVE}.agregarBeneficio`)}
                    </button>
                  </div>
                )}
              </section>
            ))}

            <section>
              <h2 className="seccion">{t(`${CLAVE}.flujo`)}</h2>
              <div className="tabla-cont">
                <table className="tabla-datos">
                  <thead>
                    <tr>
                      <th>{t(`${CLAVE}.concepto`)}</th>
                      {columnas.map((i) => (
                        <th key={i}>{t(`${CLAVE}.columnaPeriodo`, { numero: i + 1 })}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <th>{t(`${CLAVE}.flujoMercado`)}</th>
                      {columnas.map((i) => (
                        <td key={i} style={{ textAlign: 'right' }}>
                          {formatearMonto(datos?.flujoBeneficiosPrecioMercadoPorPeriodo?.[i] ?? null)}
                        </td>
                      ))}
                    </tr>
                    {muestraAjustados && (
                      <tr>
                        <th>{t(`${CLAVE}.flujoAjustado`)}</th>
                        {columnas.map((i) => (
                          <td key={i} style={{ textAlign: 'right' }}>
                            {formatearMonto(datos?.flujoBeneficiosPrecioAjustadoPorPeriodo?.[i] ?? null)}
                          </td>
                        ))}
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </section>

            <section>
              <h2 className="seccion">{t(`${CLAVE}.rescate`)}</h2>
              <div className="fr">
                <FormRow controlId="ben-rescate" label={t(`${CLAVE}.valorRescate`)}>
                  <input
                    id="ben-rescate"
                    type="text"
                    inputMode="decimal"
                    value={valorRescate}
                    disabled={!puedeEditar}
                    onChange={(e) => setValorRescate(e.target.value)}
                  />
                </FormRow>
                <FormRow controlId="ben-tipo-bien" label={t(`${CLAVE}.tipoBien`)}>
                  <select
                    id="ben-tipo-bien"
                    value={tipoBien}
                    disabled={!puedeEditar}
                    onChange={(e) => setTipoBien(e.target.value)}
                  >
                    <option value="">{t('common.seleccione')}</option>
                    {TIPOS_BIEN.map((bien) => (
                      <option key={bien} value={bien}>
                        {t(`${CLAVE}.bienes.${bien}`)}
                      </option>
                    ))}
                  </select>
                </FormRow>
                {muestraAjustados && (
                  <>
                    <FormRow controlId="ben-fc" label={t(`${CLAVE}.factorCorreccion`)}>
                      <output id="ben-fc">{datos?.fcTipoBien ?? '—'}</output>
                    </FormRow>
                    <FormRow controlId="ben-rescate-ajustado" label={t(`${CLAVE}.valorRescateAjustado`)}>
                      <output id="ben-rescate-ajustado">{formatearMonto(datos?.valorRescateAjustado ?? null)}</output>
                    </FormRow>
                  </>
                )}
              </div>
            </section>
          </>
        )}

        <div className="acciones-form">
          <button
            type="button"
            className="btn neutro"
            onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/presupuesto-om`)}
          >
            {t('preinversion.registro.botonRegresar')}
          </button>
          {puedeEditar && (
            <button type="button" className="btn primario" disabled={cargando || guardando} onClick={() => void guardarPantalla()}>
              {t('preinversion.registro.botonGuardar')}
            </button>
          )}
        </div>
      </div>

      {nuevoEn && (
        <NuevoBeneficioModal
          tipoBeneficio={nuevoEn}
          parametros={parametros}
          periodos={periodos}
          onCerrar={() => setNuevoEn(null)}
          onGuardar={registrar}
        />
      )}
    </div>
  );
}
