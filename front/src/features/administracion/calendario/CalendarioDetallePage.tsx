import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import {
  calendariosApi,
  consultasCalendarioApi,
  type Calendario,
  type CalendarItem,
} from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { CLAVE_CALENDARIO as CLAVE, ROLES_CALENDARIO } from './CalendarioPage';

const DIAS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'] as const;
const MESES = [
  'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE',
  'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER',
] as const;

const diasDelMes = (texto: string) =>
  texto
    .split(',')
    .map((n) => Number(n.trim()))
    .filter((n) => Number.isInteger(n) && n >= 1 && n <= 31);

/** Cómo se repite un período, en una línea, para la tabla de la definición. */
function repeticionEnTexto(item: CalendarItem, t: (clave: string, datos?: Record<string, unknown>) => string): string {
  if (item.tipoItem === 'EXCEPCION') return t(`${CLAVE}.tiposExcepcion.${item.tipo}`);
  const r = item.recurrencia;
  if (r.tipo === 'SEMANAL') {
    return t(`${CLAVE}.resumenSemanal`, { dias: r.diasSemana.map((d) => t(`${CLAVE}.dias.${d}`)).join(', ') });
  }
  if (r.tipo === 'MENSUAL') {
    return t(`${CLAVE}.resumenMensual`, {
      dias: r.diasDelMes.join(', '),
      meses: r.meses.map((m) => t(`${CLAVE}.mesesNombre.${m}`)).join(', '),
    });
  }
  return t(`${CLAVE}.resumenUnaVez`, { desde: r.fechaInicio, hasta: r.fechaFin });
}

/**
 * Ficha de un calendario (CU-ADM-04): su definición —períodos laborales, no
 * laborales y excepciones— y las consultas de cálculo.
 *
 * Los datos del calendario (nombre, descripción y fechas) se muestran sin
 * editar: el contrato sólo permite cambiar su estado y su definición.
 */
export function CalendarioDetallePage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { codigo = '' } = useParams<{ codigo: string }>();
  const [calendario, setCalendario] = useState<Calendario | null>(null);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);

  const puedeAdministrar = ROLES_CALENDARIO.some(hasRole);

  const cargar = useCallback(() => {
    consultasCalendarioApi
      .recuperarDefinicionCalendario({ codigoCalendario: codigo })
      .then(({ data }) => {
        setCalendario(data);
        setErrorCarga(null);
      })
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)));
  }, [codigo, t]);

  useEffect(() => {
    if (puedeAdministrar) cargar();
  }, [cargar, puedeAdministrar]);

  const avisar = async (promesa: Promise<unknown>, exito: string) => {
    try {
      await promesa;
      await Swal.fire({ icon: 'success', text: t(exito) });
      cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };

  if (!puedeAdministrar) {
    return (
      <p className="aviso-error" role="alert">
        {t(`${CLAVE}.sinPermiso`)}
      </p>
    );
  }
  if (errorCarga) return <p className="aviso-error">{errorCarga}</p>;
  if (!calendario) return <p className="nota">{t('common.cargando')}</p>;

  const items = calendario.items ?? [];
  const cambiarEstado = (estado: 'ACTIVO' | 'INACTIVO') =>
    avisar(
      calendariosApi.cambiarEstadoCalendario({ codigoCalendario: codigo, cambiarEstadoCalendarioRequest: { estado } }),
      `${CLAVE}.estadoCambiado`,
    );

  const quitar = async (item: CalendarItem) => {
    const { isConfirmed } = await Swal.fire({
      icon: 'warning',
      text: t(`${CLAVE}.confirmarQuitar`),
      showCancelButton: true,
      confirmButtonText: t(`${CLAVE}.quitar`),
      cancelButtonText: t('common.cancelar'),
    });
    if (!isConfirmed) return;
    // La definición se guarda entera: se manda la lista sin el elemento quitado.
    const restantes = items.filter((i) => i.id !== item.id);
    await avisar(
      calendariosApi.editarDefinicionCalendario({
        codigoCalendario: codigo,
        editarDefinicionCalendarioRequest: { items: restantes as never },
      }),
      `${CLAVE}.definicionGuardada`,
    );
  };

  return (
    <div className="formcard">
      <div className="formhead">
        <span>
          {calendario.nombre} <span className="mono">· {calendario.codigo}</span>
        </span>
      </div>
      <div className="formbody">
        <section>
          <h2 className="seccion">{t(`${CLAVE}.datos`)}</h2>
          <div className="fr">
            <FormRow label={t(`${CLAVE}.descripcion`)} controlId="cal-desc-ver">
              <input id="cal-desc-ver" type="text" value={calendario.descripcion ?? ''} readOnly />
            </FormRow>
            <FormRow label={t(`${CLAVE}.estado`)} controlId="cal-estado-ver">
              <input id="cal-estado-ver" type="text" value={t(`${CLAVE}.estados.${calendario.estado}`)} readOnly />
            </FormRow>
            <FormRow label={t(`${CLAVE}.fechaInicio`)} controlId="cal-desde-ver">
              <input id="cal-desde-ver" type="text" value={calendario.fechaInicio} readOnly />
            </FormRow>
            <FormRow label={t(`${CLAVE}.fechaFin`)} controlId="cal-hasta-ver">
              <input id="cal-hasta-ver" type="text" value={calendario.fechaFin} readOnly />
            </FormRow>
          </div>
          <div className="acciones-form">
            <button
              type="button"
              className="btn secundario"
              disabled={calendario.estado === 'INACTIVO'}
              onClick={() => cambiarEstado('INACTIVO')}
            >
              {t(`${CLAVE}.inactivar`)}
            </button>
            <button
              type="button"
              className="btn secundario"
              disabled={calendario.estado === 'ACTIVO'}
              onClick={() => cambiarEstado('ACTIVO')}
            >
              {t(`${CLAVE}.activar`)}
            </button>
          </div>
        </section>

        <section>
          <h2 className="seccion">{t(`${CLAVE}.definicion`)}</h2>
          {items.length === 0 ? (
            <p className="nota">{t(`${CLAVE}.sinDefinicion`)}</p>
          ) : (
            <div className="tabla-cont tabla-editable">
              <table>
                <thead>
                  <tr>
                    <th>{t(`${CLAVE}.tipoItem`)}</th>
                    <th>{t(`${CLAVE}.codigo`)}</th>
                    <th>{t(`${CLAVE}.nombre`)}</th>
                    <th>{t(`${CLAVE}.repeticion`)}</th>
                    <th>{t('common.acciones')}</th>
                  </tr>
                </thead>
                <tbody>
                  {items.map((i) => (
                    <tr key={i.id}>
                      <td>{t(`${CLAVE}.tiposItem.${i.tipoItem}`)}</td>
                      <td className="mono">{i.tipoItem === 'EXCEPCION' ? i.fecha : i.codigo}</td>
                      <td>{i.tipoItem === 'EXCEPCION' ? (i.descripcion ?? '—') : i.nombre}</td>
                      <td>{repeticionEnTexto(i, t)}</td>
                      <td>
                        <button
                          type="button"
                          className="btn neutro"
                          aria-label={t(`${CLAVE}.quitarItem`, { codigo: i.tipoItem === 'EXCEPCION' ? i.fecha : i.codigo })}
                          onClick={() => quitar(i)}
                        >
                          {t(`${CLAVE}.quitar`)}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <NuevoPeriodo codigo={codigo} alGuardar={cargar} />
        <NuevaExcepcion codigo={codigo} alGuardar={cargar} />
        <Consultas codigo={codigo} />

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate('/administracion/calendario')}>
            {t('common.regresar')}
          </button>
        </div>
      </div>
    </div>
  );
}

/** Alta de un período, laboral o no laboral, con sus tres formas de repetición. */
function NuevoPeriodo({ codigo, alGuardar }: { readonly codigo: string; readonly alGuardar: () => void }) {
  const { t } = useTranslation();
  const [tipo, setTipo] = useState<'LABORAL' | 'NO_LABORAL'>('NO_LABORAL');
  const [codigoPeriodo, setCodigoPeriodo] = useState('');
  const [nombre, setNombre] = useState('');
  const [repeticion, setRepeticion] = useState<'UNA_VEZ' | 'SEMANAL' | 'MENSUAL'>('UNA_VEZ');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [dias, setDias] = useState<string[]>([]);
  const [diasMes, setDiasMes] = useState('');
  const [meses, setMeses] = useState<string[]>([]);
  const [guardando, setGuardando] = useState(false);

  const alternar = (lista: string[], valor: string) =>
    lista.includes(valor) ? lista.filter((v) => v !== valor) : [...lista, valor];

  const recurrencia = () => {
    if (repeticion === 'SEMANAL') return { tipo: 'SEMANAL', fechaInicio: desde, fechaFin: hasta, diasSemana: dias };
    if (repeticion === 'MENSUAL') return { tipo: 'MENSUAL', diasDelMes: diasDelMes(diasMes), meses };
    return { tipo: 'UNA_VEZ', fechaInicio: desde, fechaFin: hasta };
  };

  const guardar = async () => {
    if (!codigoPeriodo.trim() || !nombre.trim()) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.faltanDatosPeriodo`) });
      return;
    }
    setGuardando(true);
    // El cuerpo es PeriodoInput: sin `tipoItem`, que lo decide el propio endpoint.
    const cuerpo = {
      codigoCalendario: codigo,
      periodoInput: { codigo: codigoPeriodo.trim(), nombre: nombre.trim(), recurrencia: recurrencia() as never },
    };
    try {
      await (tipo === 'LABORAL' ? calendariosApi.agregarPeriodoLaboral(cuerpo) : calendariosApi.agregarPeriodoNoLaboral(cuerpo));
      await Swal.fire({ icon: 'success', text: t(`${CLAVE}.periodoAgregado`) });
      // El formulario se limpia entero: si no, el siguiente período hereda los
      // días o los meses del anterior sin que se note.
      setCodigoPeriodo('');
      setNombre('');
      setDias([]);
      setDiasMes('');
      setMeses([]);
      setDesde('');
      setHasta('');
      alGuardar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <section>
      <h2 className="seccion">{t(`${CLAVE}.nuevoPeriodo`)}</h2>
      <div className="fr">
        <FormRow label={t(`${CLAVE}.tipoPeriodo`)} controlId="per-tipo" required>
          <select id="per-tipo" value={tipo} onChange={(e) => setTipo(e.target.value as 'LABORAL' | 'NO_LABORAL')}>
            <option value="NO_LABORAL">{t(`${CLAVE}.tiposPeriodo.NO_LABORAL`)}</option>
            <option value="LABORAL">{t(`${CLAVE}.tiposPeriodo.LABORAL`)}</option>
          </select>
        </FormRow>
        <FormRow label={t(`${CLAVE}.codigoPeriodo`)} controlId="per-codigo" required>
          <input id="per-codigo" type="text" value={codigoPeriodo} onChange={(e) => setCodigoPeriodo(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.nombrePeriodo`)} controlId="per-nombre" required>
          <input id="per-nombre" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.repeticion`)} controlId="per-repeticion" required>
          <select
            id="per-repeticion"
            value={repeticion}
            onChange={(e) => setRepeticion(e.target.value as 'UNA_VEZ' | 'SEMANAL' | 'MENSUAL')}
          >
            <option value="UNA_VEZ">{t(`${CLAVE}.repeticiones.UNA_VEZ`)}</option>
            <option value="SEMANAL">{t(`${CLAVE}.repeticiones.SEMANAL`)}</option>
            <option value="MENSUAL">{t(`${CLAVE}.repeticiones.MENSUAL`)}</option>
          </select>
        </FormRow>
        {repeticion !== 'MENSUAL' && (
          <>
            <FormRow label={t(`${CLAVE}.fechaInicio`)} controlId="per-desde" required>
              <input id="per-desde" type="date" value={desde} onChange={(e) => setDesde(e.target.value)} />
            </FormRow>
            <FormRow label={t(`${CLAVE}.fechaFin`)} controlId="per-hasta" required>
              <input id="per-hasta" type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} />
            </FormRow>
          </>
        )}
        {repeticion === 'MENSUAL' && (
          <FormRow label={t(`${CLAVE}.diasDelMes`)} controlId="per-dias-mes" required>
            <input id="per-dias-mes" type="text" placeholder="1, 15, 30" value={diasMes} onChange={(e) => setDiasMes(e.target.value)} />
          </FormRow>
        )}
      </div>

      {repeticion === 'SEMANAL' && (
        <fieldset>
          <legend>{t(`${CLAVE}.diasDeLaSemana`)}</legend>
          {DIAS.map((d) => (
            <label key={d} className="casilla">
              <input type="checkbox" checked={dias.includes(d)} onChange={() => setDias((v) => alternar(v, d))} />{' '}
              {t(`${CLAVE}.dias.${d}`)}
            </label>
          ))}
        </fieldset>
      )}
      {repeticion === 'MENSUAL' && (
        <fieldset>
          <legend>{t(`${CLAVE}.meses`)}</legend>
          {MESES.map((m) => (
            <label key={m} className="casilla">
              <input type="checkbox" checked={meses.includes(m)} onChange={() => setMeses((v) => alternar(v, m))} />{' '}
              {t(`${CLAVE}.mesesNombre.${m}`)}
            </label>
          ))}
        </fieldset>
      )}

      <div className="acciones-form">
        <button type="button" className="btn primario" onClick={guardar} disabled={guardando}>
          {t(`${CLAVE}.agregarPeriodo`)}
        </button>
      </div>
    </section>
  );
}

/** Excepciones: un día suelto que rompe la regla del período. */
function NuevaExcepcion({ codigo, alGuardar }: { readonly codigo: string; readonly alGuardar: () => void }) {
  const { t } = useTranslation();
  const [fecha, setFecha] = useState('');
  const [tipo, setTipo] = useState<'DIA_LABORAL' | 'DIA_NO_LABORAL'>('DIA_NO_LABORAL');
  const [descripcion, setDescripcion] = useState('');
  const [guardando, setGuardando] = useState(false);

  const guardar = async () => {
    if (!fecha) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.faltaFecha`) });
      return;
    }
    setGuardando(true);
    try {
      await calendariosApi.registrarExcepcion({
        codigoCalendario: codigo,
        registrarExcepcionRequest: { fecha, tipo, descripcion: descripcion.trim() || undefined },
      });
      await Swal.fire({ icon: 'success', text: t(`${CLAVE}.excepcionRegistrada`) });
      setFecha('');
      setDescripcion('');
      setTipo('DIA_NO_LABORAL');
      alGuardar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <section>
      <h2 className="seccion">{t(`${CLAVE}.nuevaExcepcion`)}</h2>
      <div className="fr">
        <FormRow label={t(`${CLAVE}.fecha`)} controlId="exc-fecha" required>
          <input id="exc-fecha" type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.tipoExcepcion`)} controlId="exc-tipo" required>
          <select id="exc-tipo" value={tipo} onChange={(e) => setTipo(e.target.value as 'DIA_LABORAL' | 'DIA_NO_LABORAL')}>
            <option value="DIA_NO_LABORAL">{t(`${CLAVE}.tiposExcepcion.DIA_NO_LABORAL`)}</option>
            <option value="DIA_LABORAL">{t(`${CLAVE}.tiposExcepcion.DIA_LABORAL`)}</option>
          </select>
        </FormRow>
        <FormRow label={t(`${CLAVE}.descripcionExcepcion`)} controlId="exc-descripcion">
          <input id="exc-descripcion" type="text" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
        </FormRow>
      </div>
      <div className="acciones-form">
        <button type="button" className="btn primario" onClick={guardar} disabled={guardando}>
          {t(`${CLAVE}.registrarExcepcion`)}
        </button>
      </div>
    </section>
  );
}

/**
 * Consultas de cálculo: son las que usa el back para contar días hábiles, y
 * aquí sirven para comprobar que lo cargado se comporta como se espera.
 */
function Consultas({ codigo }: { readonly codigo: string }) {
  const { t } = useTranslation();
  const [fecha, setFecha] = useState('');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [respuesta, setRespuesta] = useState<string | null>(null);

  const consultar = async (promesa: Promise<{ data: unknown }>, formato: (d: never) => string) => {
    setRespuesta(null);
    try {
      const { data } = await promesa;
      setRespuesta(formato(data as never));
    } catch (error_) {
      setRespuesta(mensajeDeError(toErrorApi(error_), t));
    }
  };

  return (
    <section>
      <h2 className="seccion">{t(`${CLAVE}.comprobar`)}</h2>
      <div className="fr">
        <FormRow label={t(`${CLAVE}.fecha`)} controlId="con-fecha">
          <input id="con-fecha" type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.desde`)} controlId="con-desde">
          <input id="con-desde" type="date" value={desde} onChange={(e) => setDesde(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.hasta`)} controlId="con-hasta">
          <input id="con-hasta" type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} />
        </FormRow>
      </div>
      <div className="acciones-form">
        <button
          type="button"
          className="btn secundario"
          disabled={!fecha}
          onClick={() =>
            consultar(consultasCalendarioApi.consultarTipoDia({ codigoCalendario: codigo, fecha }), (d: { tipo?: string }) =>
              t(`${CLAVE}.respuestaTipoDia`, { tipo: d.tipo ? t(`${CLAVE}.tiposDia.${d.tipo}`) : '—' }),
            )
          }
        >
          {t(`${CLAVE}.consultarTipoDia`)}
        </button>
        <button
          type="button"
          className="btn secundario"
          disabled={!desde || !hasta}
          onClick={() =>
            consultar(
              consultasCalendarioApi.consultarDiasLaboralesEntreFechas({
                codigoCalendario: codigo,
                fechaInicio: desde,
                fechaFin: hasta,
              }),
              (d: { diasLaborales?: number }) => t(`${CLAVE}.respuestaDiasLaborales`, { dias: d.diasLaborales ?? '—' }),
            )
          }
        >
          {t(`${CLAVE}.consultarDiasLaborales`)}
        </button>
      </div>
      {respuesta && <p className="aviso-ok">{respuesta}</p>}
    </section>
  );
}
