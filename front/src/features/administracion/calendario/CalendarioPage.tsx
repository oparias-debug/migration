import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { calendariosApi, consultasCalendarioApi } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { FormRow } from '../../../components/form/FormRow';
import { Pestanas } from '../../../components/Pestanas';

const CLAVE = 'administracion.calendario';
const DIAS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'] as const;
const MESES = [
  'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE',
  'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER',
] as const;

const PESTANAS = [
  { clave: 'calendario', texto: `${CLAVE}.pestana.calendario` },
  { clave: 'periodos', texto: `${CLAVE}.pestana.periodos` },
  { clave: 'excepciones', texto: `${CLAVE}.pestana.excepciones` },
  { clave: 'consultas', texto: `${CLAVE}.pestana.consultas` },
] as const;
type Pestana = (typeof PESTANAS)[number]['clave'];

const numeros = (texto: string) =>
  texto
    .split(',')
    .map((n) => Number(n.trim()))
    .filter((n) => Number.isInteger(n) && n >= 1 && n <= 31);

/**
 * Pantalla "Calendario" (CU-ADM-04), bajo Administración, junto a Catálogos.
 *
 * Sirve para registrar los días festivos y las fechas especiales (cierres de
 * período y demás) que el back usa después para contar días hábiles entre dos
 * fechas.
 *
 * El contrato sólo expone altas y modificaciones: no hay ningún GET que liste
 * los calendarios ni que devuelva uno con sus períodos y excepciones. Por eso
 * la pantalla trabaja sobre el código de calendario que se indica arriba, y la
 * última pestaña usa las consultas de cálculo para comprobar lo cargado —
 * preguntar qué tipo de día es una fecha es hoy la única forma de verificarlo.
 */
export function CalendarioPage() {
  const { t } = useTranslation();
  const [pestana, setPestana] = useState<Pestana>('calendario');
  const [codigo, setCodigo] = useState('');

  const aviso = async (promesa: Promise<unknown>, exito: string) => {
    try {
      await promesa;
      await Swal.fire({ icon: 'success', text: t(exito) });
      return true;
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
      return false;
    }
  };

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t(`${CLAVE}.titulo`)}</span>
      </div>
      <div className="formbody">
        <p className="nota">{t(`${CLAVE}.intro`)}</p>

        <div className="f w">
          <label htmlFor="cal-codigo-activo">{t(`${CLAVE}.codigoActivo`)}</label>
          <input
            id="cal-codigo-activo"
            type="text"
            value={codigo}
            onChange={(e) => setCodigo(e.target.value)}
            placeholder={t(`${CLAVE}.codigoActivoAyuda`)}
          />
        </div>

        <Pestanas pestanas={PESTANAS} activa={pestana} onCambiar={setPestana} etiqueta={`${CLAVE}.pestanas`} />

        <div role="tabpanel" id={`panel-${pestana}`}>
          {pestana === 'calendario' && <FormularioCalendario alCrear={setCodigo} aviso={aviso} codigo={codigo} />}
          {pestana === 'periodos' && <FormularioPeriodo codigo={codigo} aviso={aviso} />}
          {pestana === 'excepciones' && <FormularioExcepcion codigo={codigo} aviso={aviso} />}
          {pestana === 'consultas' && <PanelConsultas codigo={codigo} />}
        </div>

        <p className="nota-form">{t(`${CLAVE}.sinConsultaDeCalendarios`)}</p>
      </div>
    </div>
  );
}

type Aviso = (promesa: Promise<unknown>, exito: string) => Promise<boolean>;

/** Alta y modificación del calendario (HU-ADM-04-01). */
function FormularioCalendario({
  codigo,
  alCrear,
  aviso,
}: {
  readonly codigo: string;
  readonly alCrear: (codigo: string) => void;
  readonly aviso: Aviso;
}) {
  const { t } = useTranslation();
  const { register, handleSubmit, watch } = useForm({
    defaultValues: { codigo: '', nombre: '', descripcion: '', fechaInicio: '', fechaFin: '', estado: 'ACTIVO' },
  });
  const valores = watch();

  const crear = handleSubmit(async (v) => {
    const ok = await aviso(
      calendariosApi.crearCalendario({
        crearCalendarioRequest: {
          codigo: v.codigo,
          nombre: v.nombre,
          descripcion: v.descripcion || undefined,
          fechaInicio: v.fechaInicio,
          fechaFin: v.fechaFin,
          estado: v.estado as 'ACTIVO' | 'INACTIVO',
        },
      }),
      `${CLAVE}.creado`,
    );
    if (ok) alCrear(v.codigo);
  });

  const editar = async () => {
    await aviso(
      calendariosApi.editarCalendario({
        codigoCalendario: codigo,
        editarCalendarioRequest: {
          nombre: valores.nombre,
          descripcion: valores.descripcion || undefined,
          fechaInicio: valores.fechaInicio,
          fechaFin: valores.fechaFin,
        },
      }),
      `${CLAVE}.editado`,
    );
  };

  const eliminar = async () => {
    const { isConfirmed } = await Swal.fire({
      icon: 'warning',
      text: t(`${CLAVE}.confirmarEliminar`, { codigo }),
      showCancelButton: true,
      confirmButtonText: t('common.eliminar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (isConfirmed) await aviso(calendariosApi.eliminarCalendario({ codigoCalendario: codigo }), `${CLAVE}.eliminado`);
  };

  const cambiarEstado = async (estado: 'ACTIVO' | 'INACTIVO') => {
    await aviso(
      calendariosApi.cambiarEstadoCalendario({ codigoCalendario: codigo, cambiarEstadoCalendarioRequest: { estado } }),
      `${CLAVE}.estadoCambiado`,
    );
  };

  return (
    <form onSubmit={crear} noValidate>
      <div className="fr">
        <FormRow label={t(`${CLAVE}.codigo`)} controlId="cal-codigo" required>
          <input id="cal-codigo" type="text" {...register('codigo')} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.nombre`)} controlId="cal-nombre" required>
          <input id="cal-nombre" type="text" {...register('nombre')} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.descripcion`)} controlId="cal-descripcion">
          <input id="cal-descripcion" type="text" {...register('descripcion')} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.fechaInicio`)} controlId="cal-inicio" required>
          <input id="cal-inicio" type="date" {...register('fechaInicio')} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.fechaFin`)} controlId="cal-fin" required>
          <input id="cal-fin" type="date" {...register('fechaFin')} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.estado`)} controlId="cal-estado" required>
          <select id="cal-estado" {...register('estado')}>
            <option value="ACTIVO">{t(`${CLAVE}.estados.ACTIVO`)}</option>
            <option value="INACTIVO">{t(`${CLAVE}.estados.INACTIVO`)}</option>
          </select>
        </FormRow>
      </div>

      <div className="acciones-form">
        <button type="submit" className="btn primario">
          {t(`${CLAVE}.crear`)}
        </button>
        <button type="button" className="btn secundario" onClick={editar} disabled={!codigo}>
          {t(`${CLAVE}.editar`)}
        </button>
        <button type="button" className="btn secundario" onClick={() => cambiarEstado('INACTIVO')} disabled={!codigo}>
          {t(`${CLAVE}.inactivar`)}
        </button>
        <button type="button" className="btn neutro" onClick={eliminar} disabled={!codigo}>
          {t(`${CLAVE}.eliminar`)}
        </button>
      </div>
    </form>
  );
}

/** Períodos laborales y no laborales, con sus tres formas de repetición. */
function FormularioPeriodo({ codigo, aviso }: { readonly codigo: string; readonly aviso: Aviso }) {
  const { t } = useTranslation();
  const { register, handleSubmit, watch } = useForm({
    defaultValues: {
      tipo: 'NO_LABORAL',
      codigo: '',
      nombre: '',
      tipoRecurrencia: 'UNA_VEZ',
      fechaInicio: '',
      fechaFin: '',
      diasDeLaSemana: [] as string[],
      diasDelMes: '',
      meses: [] as string[],
    },
  });
  const v = watch();

  const recurrenciaDe = () => {
    if (v.tipoRecurrencia === 'SEMANAL') {
      return { tipoRecurrencia: 'SEMANAL', fechaInicio: v.fechaInicio, fechaFin: v.fechaFin, diasDeLaSemana: v.diasDeLaSemana };
    }
    if (v.tipoRecurrencia === 'MENSUAL') {
      return { tipoRecurrencia: 'MENSUAL', diasDelMes: numeros(v.diasDelMes), meses: v.meses };
    }
    return { tipoRecurrencia: 'UNA_VEZ', fechaInicio: v.fechaInicio, fechaFin: v.fechaFin };
  };

  const enviar = handleSubmit(async () => {
    const cuerpo = { codigo: v.codigo, nombre: v.nombre, recurrencia: recurrenciaDe() };
    const peticion =
      v.tipo === 'LABORAL'
        ? calendariosApi.agregarPeriodoLaboral({ codigoCalendario: codigo, periodoLaboralRequest: cuerpo as never })
        : calendariosApi.agregarPeriodoNoLaboral({ codigoCalendario: codigo, periodoNoLaboralRequest: cuerpo as never });
    await aviso(peticion, `${CLAVE}.periodoAgregado`);
  });

  return (
    <form onSubmit={enviar} noValidate>
      <div className="fr">
        <FormRow label={t(`${CLAVE}.tipoPeriodo`)} controlId="per-tipo" required>
          <select id="per-tipo" {...register('tipo')}>
            <option value="NO_LABORAL">{t(`${CLAVE}.tiposPeriodo.NO_LABORAL`)}</option>
            <option value="LABORAL">{t(`${CLAVE}.tiposPeriodo.LABORAL`)}</option>
          </select>
        </FormRow>
        <FormRow label={t(`${CLAVE}.codigoPeriodo`)} controlId="per-codigo" required>
          <input id="per-codigo" type="text" {...register('codigo')} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.nombrePeriodo`)} controlId="per-nombre" required>
          <input id="per-nombre" type="text" {...register('nombre')} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.repeticion`)} controlId="per-recurrencia" required>
          <select id="per-recurrencia" {...register('tipoRecurrencia')}>
            <option value="UNA_VEZ">{t(`${CLAVE}.repeticiones.UNA_VEZ`)}</option>
            <option value="SEMANAL">{t(`${CLAVE}.repeticiones.SEMANAL`)}</option>
            <option value="MENSUAL">{t(`${CLAVE}.repeticiones.MENSUAL`)}</option>
          </select>
        </FormRow>
      </div>

      {v.tipoRecurrencia !== 'MENSUAL' && (
        <div className="fr">
          <FormRow label={t(`${CLAVE}.fechaInicio`)} controlId="per-inicio" required>
            <input id="per-inicio" type="date" {...register('fechaInicio')} />
          </FormRow>
          <FormRow label={t(`${CLAVE}.fechaFin`)} controlId="per-fin" required>
            <input id="per-fin" type="date" {...register('fechaFin')} />
          </FormRow>
        </div>
      )}

      {v.tipoRecurrencia === 'SEMANAL' && (
        <fieldset>
          <legend>{t(`${CLAVE}.diasDeLaSemana`)}</legend>
          {DIAS.map((d) => (
            <label key={d} className="casilla">
              <input type="checkbox" value={d} {...register('diasDeLaSemana')} /> {t(`${CLAVE}.dias.${d}`)}
            </label>
          ))}
        </fieldset>
      )}

      {v.tipoRecurrencia === 'MENSUAL' && (
        <>
          <div className="fr">
            <FormRow label={t(`${CLAVE}.diasDelMes`)} controlId="per-dias-mes" required>
              <input id="per-dias-mes" type="text" placeholder="1, 15, 30" {...register('diasDelMes')} />
            </FormRow>
          </div>
          <fieldset>
            <legend>{t(`${CLAVE}.meses`)}</legend>
            {MESES.map((m) => (
              <label key={m} className="casilla">
                <input type="checkbox" value={m} {...register('meses')} /> {t(`${CLAVE}.mesesNombre.${m}`)}
              </label>
            ))}
          </fieldset>
        </>
      )}

      <div className="acciones-form">
        <button type="submit" className="btn primario" disabled={!codigo}>
          {t(`${CLAVE}.agregarPeriodo`)}
        </button>
      </div>
    </form>
  );
}

/** Excepciones: un día suelto que rompe la regla del período. */
function FormularioExcepcion({ codigo, aviso }: { readonly codigo: string; readonly aviso: Aviso }) {
  const { t } = useTranslation();
  const { register, handleSubmit } = useForm({
    defaultValues: { fecha: '', tipo: 'DIA_NO_LABORAL', descripcion: '' },
  });

  const enviar = handleSubmit(async (v) => {
    await aviso(
      calendariosApi.registrarExcepcion({
        codigoCalendario: codigo,
        excepcionRequest: { fecha: v.fecha, tipo: v.tipo as 'DIA_LABORAL' | 'DIA_NO_LABORAL', descripcion: v.descripcion },
      }),
      `${CLAVE}.excepcionRegistrada`,
    );
  });

  return (
    <form onSubmit={enviar} noValidate>
      <div className="fr">
        <FormRow label={t(`${CLAVE}.fecha`)} controlId="exc-fecha" required>
          <input id="exc-fecha" type="date" {...register('fecha')} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.tipoExcepcion`)} controlId="exc-tipo" required>
          <select id="exc-tipo" {...register('tipo')}>
            <option value="DIA_NO_LABORAL">{t(`${CLAVE}.tiposExcepcion.DIA_NO_LABORAL`)}</option>
            <option value="DIA_LABORAL">{t(`${CLAVE}.tiposExcepcion.DIA_LABORAL`)}</option>
          </select>
        </FormRow>
        <FormRow label={t(`${CLAVE}.descripcionExcepcion`)} controlId="exc-descripcion" required>
          <input id="exc-descripcion" type="text" {...register('descripcion')} />
        </FormRow>
      </div>

      <div className="acciones-form">
        <button type="submit" className="btn primario" disabled={!codigo}>
          {t(`${CLAVE}.registrarExcepcion`)}
        </button>
      </div>
    </form>
  );
}

/**
 * Consultas de cálculo. Son las que usa el back para contar días hábiles, y
 * aquí sirven para comprobar que lo cargado se comporta como se espera: sin
 * ningún GET que devuelva el calendario, es la única forma de verificarlo.
 */
function PanelConsultas({ codigo }: { readonly codigo: string }) {
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
    <div>
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
          disabled={!codigo || !fecha}
          onClick={() =>
            consultar(consultasCalendarioApi.consultarTipoDia({ codigoCalendario: codigo, fecha }), (d: { tipoDia?: string }) =>
              t(`${CLAVE}.respuestaTipoDia`, { tipo: d.tipoDia ?? '—' }),
            )
          }
        >
          {t(`${CLAVE}.consultarTipoDia`)}
        </button>
        <button
          type="button"
          className="btn secundario"
          disabled={!codigo || !desde || !hasta}
          onClick={() =>
            consultar(
              consultasCalendarioApi.consultarDiasLaboralesEntreFechas({
                codigoCalendario: codigo,
                fechaInicial: desde,
                fechaFinal: hasta,
              }),
              (d: { diasLaborales?: number }) => t(`${CLAVE}.respuestaDiasLaborales`, { dias: d.diasLaborales ?? '—' }),
            )
          }
        >
          {t(`${CLAVE}.consultarDiasLaborales`)}
        </button>
      </div>

      {respuesta && <p className="aviso-ok">{respuesta}</p>}
    </div>
  );
}
