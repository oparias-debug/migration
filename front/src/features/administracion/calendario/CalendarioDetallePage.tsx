import { useCallback, useEffect, useRef, useState } from 'react';
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
import { Pestanas } from '../../../components/Pestanas';
import { CLAVE_CALENDARIO as CLAVE, ROLES_CALENDARIO } from './CalendarioPage';
import { CalendarioVisual } from './CalendarioVisual';
import { aIso, clasificar, mesesDelCalendario } from './diasDelCalendario';

const DIAS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'] as const;
const MESES = [
  'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE',
  'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER',
] as const;

/** Las tres formas de repetición de un período (CU-ADM-04, sección 4). */
type Repeticion = 'UNA_VEZ' | 'SEMANAL' | 'MENSUAL';

const diasDelMes = (texto: string) =>
  texto
    .split(',')
    .map((n) => Number(n.trim()))
    .filter((n) => Number.isInteger(n) && n >= 1 && n <= 31);

/**
 * Un CalendarItem tal como lo espera `editarDefinicionCalendario`: el mismo
 * ítem sin `estado`, que no se manda porque lo hereda del calendario (RN20).
 * Conservar el `id` es lo que distingue editar de dar de alta.
 */
function aInput(item: CalendarItem) {
  const { estado: _estado, ...resto } = item as CalendarItem & { estado?: string };
  return resto;
}

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
  const [indiceMes, setIndiceMes] = useState(0);
  const [diaElegido, setDiaElegido] = useState<string | null>(null);
  const [panel, setPanel] = useState<'periodo' | 'excepcion' | 'comprobar'>('periodo');
  // Ítem de la definición que se está editando (RN23); null = alta.
  const [enEdicion, setEnEdicion] = useState<CalendarItem | null>(null);

  // Gestionar es de ADMINISTRADOR / ADMINISTRADOR_CALENDARIO (RN12); consultar
  // el calendario lo puede hacer cualquier usuario (RN18).
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
    cargar();
  }, [cargar]);

  // Al abrir un calendario se muestra el mes de hoy si cae dentro de su rango;
  // si no, el primero. Sólo al abrirlo: recargar tras guardar no debe devolver
  // al usuario al mes inicial mientras trabaja en otro.
  const calendarioSituado = useRef<string | null>(null);
  useEffect(() => {
    if (!calendario || calendarioSituado.current === calendario.codigo) return;
    calendarioSituado.current = calendario.codigo;
    const hoy = aIso(new Date());
    const meses = mesesDelCalendario(calendario.fechaInicio, calendario.fechaFin);
    const indiceDeHoy = meses.findIndex(
      (m) => m.anio === Number(hoy.slice(0, 4)) && m.mes === Number(hoy.slice(5, 7)) - 1,
    );
    setIndiceMes(Math.max(indiceDeHoy, 0));
  }, [calendario]);

  const avisar = async (promesa: Promise<unknown>, exito: string) => {
    try {
      await promesa;
      await Swal.fire({ icon: 'success', text: t(exito) });
      cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };

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
    const restantes = items.filter((i) => i.id !== item.id).map(aInput);
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
        {/* El calendario a la izquierda y los diálogos de su definición a la
            derecha, como lo planteó el cliente para este caso de uso. */}
        <div className="cal-layout">
          <div className="cal-izq">
            <CalendarioVisual
              calendario={calendario}
              indiceMes={indiceMes}
              alCambiarMes={setIndiceMes}
              diaElegido={diaElegido}
              alElegirDia={setDiaElegido}
            />
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
          {puedeAdministrar && <div className="acciones-form">
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
          </div>}
        </section>
          </div>
          <div className="cal-der">
        <DetalleDia
          calendario={calendario}
          fecha={diaElegido}
          puedeAdministrar={puedeAdministrar}
          alGuardar={cargar}
        />

        {/* Los diálogos de la definición, uno a la vez: puestos en fila, el
            panel medía tres pantallas de alto. Las consultas de cálculo no
            piden rol (RN18); agregar y registrar sí (RN12). */}
        <Pestanas
          etiqueta={`${CLAVE}.paneles`}
          activa={panel}
          onCambiar={setPanel}
          pestanas={[
            ...(puedeAdministrar
              ? ([
                  { clave: 'periodo' as const, texto: `${CLAVE}.nuevoPeriodo` },
                  { clave: 'excepcion' as const, texto: `${CLAVE}.nuevaExcepcion` },
                ])
              : []),
            { clave: 'comprobar' as const, texto: `${CLAVE}.comprobar` },
          ]}
        />
        {puedeAdministrar && panel === 'periodo' && (
          <NuevoPeriodo
            codigo={codigo}
            alGuardar={cargar}
            items={items}
            enEdicion={enEdicion?.tipoItem !== 'EXCEPCION' ? enEdicion : null}
            alSalirDeEdicion={() => setEnEdicion(null)}
          />
        )}
        {puedeAdministrar && panel === 'excepcion' && (
          <NuevaExcepcion
            codigo={codigo}
            alGuardar={cargar}
            fechaInicial={diaElegido}
            items={items}
            enEdicion={enEdicion?.tipoItem === 'EXCEPCION' ? enEdicion : null}
            alSalirDeEdicion={() => setEnEdicion(null)}
          />
        )}
        {panel === 'comprobar' && (
          <Consultas
            codigo={codigo}
            fechaInicial={diaElegido}
            periodos={items
              .filter((i) => i.tipoItem !== 'EXCEPCION')
              .map((i) => ({ codigo: (i as { codigo: string }).codigo, nombre: (i as { nombre: string }).nombre }))}
          />
        )}
          </div>
        </div>


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
                    {puedeAdministrar && <th>{t('common.acciones')}</th>}
                  </tr>
                </thead>
                <tbody>
                  {items.map((i) => (
                    <tr key={i.id}>
                      <td>{t(`${CLAVE}.tiposItem.${i.tipoItem}`)}</td>
                      <td className="mono">{i.tipoItem === 'EXCEPCION' ? i.fecha : i.codigo}</td>
                      <td>{i.tipoItem === 'EXCEPCION' ? (i.descripcion ?? '—') : i.nombre}</td>
                      <td>{repeticionEnTexto(i, t)}</td>
                      {puedeAdministrar && (
                        <td className="acciones-fila">
                          <button
                            type="button"
                            className="btn neutro"
                            aria-label={t(`${CLAVE}.editarItem`, { codigo: i.tipoItem === 'EXCEPCION' ? i.fecha : i.codigo })}
                            onClick={() => {
                              setEnEdicion(i);
                              setPanel(i.tipoItem === 'EXCEPCION' ? 'excepcion' : 'periodo');
                            }}
                          >
                            {t(`${CLAVE}.editar`)}
                          </button>
                          <button
                            type="button"
                            className="btn neutro"
                            aria-label={t(`${CLAVE}.quitarItem`, { codigo: i.tipoItem === 'EXCEPCION' ? i.fecha : i.codigo })}
                            onClick={() => quitar(i)}
                          >
                            {t(`${CLAVE}.quitar`)}
                          </button>
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate('/administracion/calendario')}>
            {t('common.regresar')}
          </button>
        </div>
      </div>
    </div>
  );
}

/**
 * El día elegido en la rejilla: qué tipo resulta, por qué —qué períodos o
 * excepción lo cubren— y, para quien administra, el atajo para reclasificarlo
 * con una excepción (HU-ADM-04-04) sin bajar al formulario.
 */
function DetalleDia({
  calendario,
  fecha,
  puedeAdministrar,
  alGuardar,
}: {
  readonly calendario: Calendario;
  readonly fecha: string | null;
  readonly puedeAdministrar: boolean;
  readonly alGuardar: () => void;
}) {
  const { t } = useTranslation();
  const [guardando, setGuardando] = useState(false);

  if (!fecha) return <p className="nota">{t(`${CLAVE}.elijaUnDia`)}</p>;

  const { tipo, cubren } = clasificar(calendario.items ?? [], fecha);

  const marcarDia = async (tipoExcepcion: 'DIA_LABORAL' | 'DIA_NO_LABORAL') => {
    const { isConfirmed, value } = await Swal.fire({
      icon: 'question',
      text: t(`${CLAVE}.motivoExcepcion`, { fecha }),
      input: 'text',
      inputPlaceholder: t(`${CLAVE}.descripcionExcepcion`),
      showCancelButton: true,
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (!isConfirmed) return;
    setGuardando(true);
    try {
      await calendariosApi.registrarExcepcion({
        codigoCalendario: calendario.codigo,
        registrarExcepcionRequest: { fecha, tipo: tipoExcepcion, descripcion: (value as string)?.trim() || undefined },
      });
      await Swal.fire({ icon: 'success', text: t(`${CLAVE}.excepcionRegistrada`) });
      alGuardar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <section className="cal-dia">
      <h2 className="seccion">{fecha}</h2>
      <p className={`cal-dia-tipo ${tipo === 'LABORAL' ? 'es-laboral' : ''} ${tipo === 'NO_LABORAL' ? 'es-no-laboral' : ''}`}>
        {t(`${CLAVE}.tiposDiaTitulo.${tipo ?? 'SIN_DEFINIR'}`)}
      </p>
      {cubren.length === 0 ? (
        <p className="nota">{t(`${CLAVE}.diaSinPeriodo`)}</p>
      ) : (
        <ul className="cal-porque">
          {cubren.map((item) => (
            <li key={item.id}>
              <b>{t(`${CLAVE}.tiposItem.${item.tipoItem}`)}</b>{' '}
              {item.tipoItem === 'EXCEPCION' ? (item.descripcion ?? item.fecha) : `${item.nombre} (${item.codigo})`}
            </li>
          ))}
        </ul>
      )}
      {puedeAdministrar && (
        <div className="acciones-form">
          <button type="button" className="btn secundario" disabled={guardando} onClick={() => marcarDia('DIA_NO_LABORAL')}>
            {t(`${CLAVE}.marcarNoLaboral`)}
          </button>
          <button type="button" className="btn secundario" disabled={guardando} onClick={() => marcarDia('DIA_LABORAL')}>
            {t(`${CLAVE}.marcarLaboral`)}
          </button>
        </div>
      )}
    </section>
  );
}

/** Alta de un período, laboral o no laboral, con sus tres formas de repetición. */
function NuevoPeriodo({
  codigo,
  alGuardar,
  items,
  enEdicion,
  alSalirDeEdicion,
}: {
  readonly codigo: string;
  readonly alGuardar: () => void;
  readonly items: readonly CalendarItem[];
  /** Período que se está editando (RN23); null = alta. */
  readonly enEdicion?: CalendarItem | null;
  readonly alSalirDeEdicion: () => void;
}) {
  const { t } = useTranslation();
  const [tipo, setTipo] = useState<'LABORAL' | 'NO_LABORAL'>('NO_LABORAL');
  const [codigoPeriodo, setCodigoPeriodo] = useState('');
  const [nombre, setNombre] = useState('');
  const [repeticion, setRepeticion] = useState<Repeticion>('UNA_VEZ');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [dias, setDias] = useState<string[]>([]);
  const [diasMes, setDiasMes] = useState('');
  const [meses, setMeses] = useState<string[]>([]);
  const [guardando, setGuardando] = useState(false);

  // Al elegir "Editar" en la tabla, el formulario se carga con ese período.
  useEffect(() => {
    if (!enEdicion || enEdicion.tipoItem === 'EXCEPCION') return;
    setTipo(enEdicion.tipoItem === 'LABORAL' ? 'LABORAL' : 'NO_LABORAL');
    setCodigoPeriodo(enEdicion.codigo);
    setNombre(enEdicion.nombre);
    const r = enEdicion.recurrencia;
    setRepeticion(r.tipo as Repeticion);
    setDesde(r.tipo === 'MENSUAL' ? '' : r.fechaInicio);
    setHasta(r.tipo === 'MENSUAL' ? '' : r.fechaFin);
    setDias(r.tipo === 'SEMANAL' ? [...r.diasSemana] : []);
    setDiasMes(r.tipo === 'MENSUAL' ? r.diasDelMes.join(', ') : '');
    setMeses(r.tipo === 'MENSUAL' ? [...r.meses] : []);
  }, [enEdicion]);

  const limpiar = () => {
    setCodigoPeriodo('');
    setNombre('');
    setDias([]);
    setDiasMes('');
    setMeses([]);
    setDesde('');
    setHasta('');
  };

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
      if (enEdicion) {
        // Editar un ítem es mandar la definición entera con ese ítem cambiado
        // (RN23): el `id` es lo que le dice al back que no es un alta.
        const actualizados = items.map((i) =>
          i.id === enEdicion.id
            ? { id: i.id, tipoItem: tipo, codigo: codigoPeriodo.trim(), nombre: nombre.trim(), recurrencia: recurrencia() }
            : aInput(i),
        );
        await calendariosApi.editarDefinicionCalendario({
          codigoCalendario: codigo,
          editarDefinicionCalendarioRequest: { items: actualizados as never },
        });
        await Swal.fire({ icon: 'success', text: t(`${CLAVE}.definicionGuardada`) });
        alSalirDeEdicion();
      } else {
        await (tipo === 'LABORAL' ? calendariosApi.agregarPeriodoLaboral(cuerpo) : calendariosApi.agregarPeriodoNoLaboral(cuerpo));
        await Swal.fire({ icon: 'success', text: t(`${CLAVE}.periodoAgregado`) });
      }
      // El formulario se limpia entero: si no, el siguiente período hereda los
      // días o los meses del anterior sin que se note.
      limpiar();
      alGuardar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <section>
      <h2 className="seccion">{t(enEdicion ? `${CLAVE}.editarPeriodo` : `${CLAVE}.nuevoPeriodo`)}</h2>
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
            onChange={(e) => setRepeticion(e.target.value as Repeticion)}
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
          {t(enEdicion ? `${CLAVE}.guardarCambios` : `${CLAVE}.agregarPeriodo`)}
        </button>
        {enEdicion && (
          <button
            type="button"
            className="btn neutro"
            disabled={guardando}
            onClick={() => {
              limpiar();
              alSalirDeEdicion();
            }}
          >
            {t('common.cancelar')}
          </button>
        )}
      </div>
    </section>
  );
}

/** Excepciones: un día suelto que rompe la regla del período. */
function NuevaExcepcion({
  codigo,
  alGuardar,
  fechaInicial,
  items,
  enEdicion,
  alSalirDeEdicion,
}: {
  readonly codigo: string;
  readonly alGuardar: () => void;
  readonly fechaInicial?: string | null;
  readonly items: readonly CalendarItem[];
  /** Excepción que se está editando (RN23); null = alta. */
  readonly enEdicion?: CalendarItem | null;
  readonly alSalirDeEdicion: () => void;
}) {
  const { t } = useTranslation();
  const [fecha, setFecha] = useState('');
  // Elegir un día en la rejilla rellena la fecha del formulario.
  useEffect(() => {
    if (fechaInicial) setFecha(fechaInicial);
  }, [fechaInicial]);
  const [tipo, setTipo] = useState<'DIA_LABORAL' | 'DIA_NO_LABORAL'>('DIA_NO_LABORAL');
  const [descripcion, setDescripcion] = useState('');
  const [guardando, setGuardando] = useState(false);

  // Al elegir "Editar" en la tabla, el formulario se carga con esa excepción.
  useEffect(() => {
    if (enEdicion?.tipoItem !== 'EXCEPCION') return;
    setFecha(enEdicion.fecha);
    setTipo(enEdicion.tipo);
    setDescripcion(enEdicion.descripcion ?? '');
  }, [enEdicion]);

  const guardar = async () => {
    if (!fecha) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.faltaFecha`) });
      return;
    }
    setGuardando(true);
    try {
      if (enEdicion) {
        // Igual que con los períodos: la edición manda la definición entera
        // con esta excepción cambiada, conservando su `id` (RN23).
        const actualizados = items.map((i) =>
          i.id === enEdicion.id
            ? { id: i.id, tipoItem: 'EXCEPCION', fecha, tipo, descripcion: descripcion.trim() || undefined }
            : aInput(i),
        );
        await calendariosApi.editarDefinicionCalendario({
          codigoCalendario: codigo,
          editarDefinicionCalendarioRequest: { items: actualizados as never },
        });
        await Swal.fire({ icon: 'success', text: t(`${CLAVE}.definicionGuardada`) });
        alSalirDeEdicion();
      } else {
        await calendariosApi.registrarExcepcion({
          codigoCalendario: codigo,
          registrarExcepcionRequest: { fecha, tipo, descripcion: descripcion.trim() || undefined },
        });
        await Swal.fire({ icon: 'success', text: t(`${CLAVE}.excepcionRegistrada`) });
      }
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
      <h2 className="seccion">{t(enEdicion ? `${CLAVE}.editarExcepcion` : `${CLAVE}.nuevaExcepcion`)}</h2>
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
          {t(enEdicion ? `${CLAVE}.guardarCambios` : `${CLAVE}.registrarExcepcion`)}
        </button>
        {enEdicion && (
          <button
            type="button"
            className="btn neutro"
            disabled={guardando}
            onClick={() => {
              setFecha('');
              setDescripcion('');
              setTipo('DIA_NO_LABORAL');
              alSalirDeEdicion();
            }}
          >
            {t('common.cancelar')}
          </button>
        )}
      </div>
    </section>
  );
}

/**
 * Las nueve consultas del CU-ADM-04 (secciones 7.1 a 7.9). No piden rol
 * (RN18): son las mismas que usa el resto del sistema para contar días
 * hábiles, y aquí sirven para comprobar que la definición cargada responde lo
 * que se espera.
 *
 * Cada consulta se pregunta sobre lo que necesita —una fecha, un período, un
 * número de días— y el servidor responde error cuando la pregunta no tiene
 * respuesta consistente (fecha fuera de todo período, código inexistente,
 * fechas incoherentes): ese error es la respuesta, y se muestra tal cual.
 */
function Consultas({
  codigo,
  fechaInicial,
  periodos,
}: {
  readonly codigo: string;
  readonly fechaInicial?: string | null;
  /** Códigos de período del calendario, para elegir en vez de teclearlos. */
  readonly periodos: readonly { codigo: string; nombre: string }[];
}) {
  const { t } = useTranslation();
  const [fecha, setFecha] = useState('');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [periodo, setPeriodo] = useState('');
  const [diasHabiles, setDiasHabiles] = useState('1');
  const [respuesta, setRespuesta] = useState<string | null>(null);
  const [fallo, setFallo] = useState(false);

  useEffect(() => {
    if (fechaInicial) setFecha(fechaInicial);
  }, [fechaInicial]);

  const consultar = async (promesa: Promise<{ data: unknown }>, formato: (d: never) => string) => {
    setRespuesta(null);
    try {
      const { data } = await promesa;
      setFallo(false);
      setRespuesta(formato(data as never));
    } catch (error_) {
      setFallo(true);
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
        <FormRow label={t(`${CLAVE}.periodo`)} controlId="con-periodo">
          <select id="con-periodo" value={periodo} onChange={(e) => setPeriodo(e.target.value)}>
            <option value="">{t('common.seleccione')}</option>
            {periodos.map((p) => (
              <option key={p.codigo} value={p.codigo}>
                {p.nombre} ({p.codigo})
              </option>
            ))}
          </select>
        </FormRow>
        <FormRow label={t(`${CLAVE}.desde`)} controlId="con-desde">
          <input id="con-desde" type="date" value={desde} onChange={(e) => setDesde(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.hasta`)} controlId="con-hasta">
          <input id="con-hasta" type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.diasHabiles`)} controlId="con-dias">
          <input
            id="con-dias"
            type="number"
            min={1}
            value={diasHabiles}
            onChange={(e) => setDiasHabiles(e.target.value)}
          />
        </FormRow>
      </div>

      <div className="acciones-form acciones-consultas">
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
          disabled={!fecha || !periodo}
          onClick={() =>
            consultar(
              consultasCalendarioApi.consultarPertenenciaPeriodo({ codigoCalendario: codigo, codigoPeriodo: periodo, fecha }),
              (d: { pertenece?: boolean }) =>
                t(d.pertenece ? `${CLAVE}.respuestaPerteneceSi` : `${CLAVE}.respuestaPerteneceNo`, { periodo }),
            )
          }
        >
          {t(`${CLAVE}.consultarPertenencia`)}
        </button>
        <button
          type="button"
          className="btn secundario"
          disabled={!periodo}
          onClick={() =>
            consultar(
              consultasCalendarioApi.consultarDuracionPeriodo({ codigoCalendario: codigo, codigoPeriodo: periodo }),
              (d: { duracionDias?: number }) => t(`${CLAVE}.respuestaDuracion`, { dias: d.duracionDias ?? '—' }),
            )
          }
        >
          {t(`${CLAVE}.consultarDuracion`)}
        </button>
        <button
          type="button"
          className="btn secundario"
          disabled={!fecha || !periodo}
          onClick={() =>
            consultar(
              consultasCalendarioApi.consultarDiasRestantesPeriodoLaboral({
                codigoCalendario: codigo,
                codigoPeriodo: periodo,
                fecha,
              }),
              (d: { diasRestantes?: number }) => t(`${CLAVE}.respuestaDiasRestantes`, { dias: d.diasRestantes ?? '—' }),
            )
          }
        >
          {t(`${CLAVE}.consultarDiasRestantes`)}
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
        <button
          type="button"
          className="btn secundario"
          disabled={!fecha || !diasHabiles}
          onClick={() =>
            consultar(
              consultasCalendarioApi.calcularFechaLaboralResultante({
                codigoCalendario: codigo,
                fecha,
                diasHabiles: Number(diasHabiles),
              }),
              (d: { fecha?: string }) => t(`${CLAVE}.respuestaFechaResultante`, { fecha: d.fecha ?? '—' }),
            )
          }
        >
          {t(`${CLAVE}.consultarFechaResultante`)}
        </button>
        <button
          type="button"
          className="btn secundario"
          onClick={() =>
            consultar(
              consultasCalendarioApi.consultarRangoFechasCalendario({ codigoCalendario: codigo }),
              (d: { fechaDesde?: string; fechaHasta?: string }) =>
                t(`${CLAVE}.respuestaRango`, { desde: d.fechaDesde ?? '—', hasta: d.fechaHasta ?? '—' }),
            )
          }
        >
          {t(`${CLAVE}.consultarRango`)}
        </button>
      </div>
      {respuesta && <p className={fallo ? 'aviso-error' : 'aviso-ok'}>{respuesta}</p>}
    </section>
  );
}
