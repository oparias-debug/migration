import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import { preinversionApi, catalogoPreinversionApi, revisionCupApi, IniciativaInversion, TipoMedidaCatalogo } from '../../../api/preinversionApi';
import type { ComentarioSolicitud, Proyecto, ProyectoRequest } from '../../../api/preinversionApi';
import { erroresPorCampo, mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { confirmDialog } from '../../../components/ConfirmDialog';
import { FormRow } from '../../../components/form/FormRow';
import { ESTADOS_EDITABLES, formatEstado } from './proyectoLabels';
import { ESTADOS_SIN_CUP } from '../etapas/etapasLabels';
import { MedidasCatalogoField } from './MedidasCatalogoField';
import { CategoriasCatalogoModal } from './CategoriasCatalogoModal';
import { useCatalogo } from '../useCatalogo';
import { RevisionPre } from './RevisionPre';
import {
  proyectoFormSchema,
  PROYECTO_FORM_DEFAULTS,
  conSeparadorDeMiles,
  sinSeparadorDeMiles,
  type ProyectoFormValues,
} from './proyectoFormSchema';

const INICIATIVA_OPCIONES = [
  { valor: IniciativaInversion.Programa, labelKey: 'preinversion.registro.opcionPrograma' },
  { valor: IniciativaInversion.Proyecto, labelKey: 'preinversion.registro.opcionProyecto' },
  { valor: IniciativaInversion.EstudioGeneral, labelKey: 'preinversion.registro.opcionEstudioGeneral' },
] as const;

function proyectoToFormValues(proyecto: Proyecto): ProyectoFormValues {
  return {
    iniciativaInversion: proyecto.iniciativaInversion,
    nombre: proyecto.nombre,
    montoEstimadoInversion: conSeparadorDeMiles(String(proyecto.montoEstimadoInversion)),
    idSector: String(proyecto.sector.idSector),
    idEjeTematico: String(proyecto.ejeTematico.idEjeTematico),
    medidasGrd: (proyecto.medidasGrd ?? []).map((m) => m.codigo),
    medidasGrc: (proyecto.medidasGrc ?? []).map((m) => m.codigo),
    medidasAcc: (proyecto.medidasAcc ?? []).map((m) => m.codigo),
    esProyectoEmergencia: proyecto.esProyectoEmergencia ?? false,
    tipoEvento: proyecto.tipoEvento ?? '',
    numeroDecretoLegislativo: proyecto.numeroDecretoLegislativo ?? '',
    idEjePlanGobierno: proyecto.ejePlanGobierno ? String(proyecto.ejePlanGobierno.idEjePlanGobierno) : '',
    idPlanSectorialRegional: proyecto.planSectorialRegional
      ? String(proyecto.planSectorialRegional.idPlanSectorialRegional)
      : '',
    descripcionProyecto: proyecto.descripcionProyecto,
  };
}

function formValuesToRequest(valores: ProyectoFormValues): ProyectoRequest {
  return {
    iniciativaInversion: valores.iniciativaInversion,
    nombre: valores.nombre,
    montoEstimadoInversion: Number(sinSeparadorDeMiles(valores.montoEstimadoInversion)),
    idSector: Number(valores.idSector),
    idEjeTematico: Number(valores.idEjeTematico),
    medidasGrd: valores.medidasGrd,
    medidasGrc: valores.medidasGrc,
    medidasAcc: valores.medidasAcc,
    esProyectoEmergencia: valores.esProyectoEmergencia,
    tipoEvento: valores.esProyectoEmergencia ? valores.tipoEvento : undefined,
    numeroDecretoLegislativo: valores.esProyectoEmergencia ? valores.numeroDecretoLegislativo : undefined,
    idEjePlanGobierno: valores.idEjePlanGobierno ? Number(valores.idEjePlanGobierno) : undefined,
    idPlanSectorialRegional: valores.idPlanSectorialRegional ? Number(valores.idPlanSectorialRegional) : undefined,
    descripcionProyecto: valores.descripcionProyecto,
  };
}

// Pantalla "Nuevo registro" (Anexo A.2): crea (SF-1/SF-1.1) o edita (SF-2) un
// proyecto, según haya o no un :id en la ruta.
/**
 * Campos que aparecen en el diseño del cliente pero no existen en
 * ProyectoRequest. Se muestran como aviso para que se definan en el contrato
 * o se retiren del diseño; hasta entonces no se pueden guardar.
 */
/**
 * Campos del diseño que siguen sin definición.
 *
 * De los cinco que se listaban, tres quedaron resueltos el 02/09/2026:
 * Responsable es el usuario de la sesión, Unidad formuladora es la Unidad
 * Ejecutora y Fecha solicitada es la fecha de radicación — los tres los asigna
 * el servidor y ahora se muestran de sólo lectura.
 *
 * Los dos que quedan sí hacen falta, pero para proyectos de emergencia y con
 * más alcance del que dibuja el diseño: el cliente pidió Fuente de
 * Financiamiento y Fuente de Recursos, ambas múltiples, más los documentos
 * soporte. Nada de eso existe todavía en ProyectoRequest.
 */
const CAMPOS_SIN_RESPALDO = [
  'Fuente de financiamiento (múltiple, en proyectos de emergencia)',
  'Fuente de recursos (múltiple, en proyectos de emergencia)',
  'Documentos adjuntos de soporte',
];

export function ProyectoFormPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = id ? Number(id) : undefined;
  const esNuevo = idProyecto === undefined;

  const [cargando, setCargando] = useState(!esNuevo);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [estadoActual, setEstadoActual] = useState<string | null>(null);
  const [revisionPre, setRevisionPre] = useState<ComentarioSolicitud[]>([]);
  /**
   * Campos que el CU-PRE-01 marca como "Seleccionable: No" (§B.1): los asigna
   * el servidor y la pantalla los muestra, no los pide. Por eso no están en
   * ProyectoRequest y se guardan aparte de los valores del formulario.
   */
  const [asignados, setAsignados] = useState<{
    institucion?: string;
    unidadEjecutora?: string;
    macrosector?: string;
    fechaIngreso?: string;
  }>({});
  const [errorRespuesta, setErrorRespuesta] = useState<string | undefined>();
  const [mostrarCategorias, setMostrarCategorias] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    reset,
    getValues,
    setValue,
    setError,
    setFocus,
    formState: { errors, isDirty },
  } = useForm<ProyectoFormValues>({
    resolver: zodResolver(proyectoFormSchema),
    defaultValues: PROYECTO_FORM_DEFAULTS as ProyectoFormValues,
  });

  useEffect(() => {
    if (esNuevo || idProyecto === undefined) return;
    // Sin catch, un 404 dejaba `cargando` en true y la pantalla colgada en
    // "Cargando..." indefinidamente.
    preinversionApi
      .obtenerProyecto({ idProyecto })
      .then(({ data }) => {
        reset(proyectoToFormValues(data));
        setEstadoActual(data.estado);
        setRevisionPre(data.revisionPre ?? []);
        setAsignados({
          institucion: data.institucion?.nombre,
          unidadEjecutora: data.unidadEjecutora?.nombre,
          macrosector: data.sector?.macrosector?.nombre,
          fechaIngreso: data.fechaIngreso,
        });
      })
      .catch((fallo) => setErrorCarga(mensajeDeError(toErrorApi(fallo), t)))
      .finally(() => setCargando(false));
  }, [esNuevo, idProyecto, reset, t]);

  // El monto se formatea mientras se escribe, así que se envuelve el onChange
  // que devuelve register en vez de pasarlo tal cual.
  const registroMonto = register('montoEstimadoInversion');

  const esProyectoEmergencia = watch('esProyectoEmergencia');
  const medidasGrd = watch('medidasGrd');
  const medidasGrc = watch('medidasGrc');
  const medidasAcc = watch('medidasAcc');

  const sectores = useCatalogo(() => catalogoPreinversionApi.listarSectores());
  const ejesTematicos = useCatalogo(() => catalogoPreinversionApi.listarEjesTematicos());
  const ejesPlanGobierno = useCatalogo(() => catalogoPreinversionApi.listarEjesPlanGobierno());
  const planesSectoriales = useCatalogo(() => catalogoPreinversionApi.listarPlanesSectoriales());

  const puedeEditar = hasRole('TECNICO_URP') && (esNuevo || (estadoActual !== null && ESTADOS_EDITABLES.includes(estadoActual)));
  // CU-PRE-01.5 (Antecedentes): el Técnico PRE revisa el mismo registro desde la Bandeja
  // Preinversión (CU-PRE-02) mientras está en ENVIADO_DGICP_REGISTRO; nunca edita los campos.
  const puedeRevisarPre = hasRole('TECNICO_PRE') && !esNuevo && estadoActual === 'ENVIADO_DGICP_REGISTRO';
  // CU-PRE-3.5 (Selección y Registro de Etapas): aplica una vez asignado el CUP. El punto de
  // entrada real que describe el propio CU es "Captura de Proyectos" (UC-PRE-03), que todavía no
  // existe en este frontend; mientras tanto se entra desde aquí.
  const puedeIrARegistroEtapas =
    (hasRole('TECNICO_URP') || hasRole('COORDINADOR_SYMP')) &&
    !esNuevo &&
    estadoActual !== null &&
    !ESTADOS_SIN_CUP.includes(estadoActual);

  const regresar = async () => {
    if (isDirty) {
      const confirmado = await confirmDialog(t('preinversion.registro.confirmarRegresar'), {
        confirmButtonText: t('common.aceptar'),
        cancelButtonText: t('common.cancelar'),
      });
      if (!confirmado) return;
    }
    navigate('/preinversion/proyectos');
  };

  /**
   * Vuelca la respuesta de error del back sobre la pantalla.
   *
   * Los 400 traen `detalles[].campo`: cada mensaje va a su campo, que es lo que
   * piden los escenarios "Intentar guardar con un campo obligatorio incompleto"
   * y "Solicitar el CUP con campos incompletos" (sombrear el campo y mostrar
   * ahí el mensaje del Anexo B.2). El resto de códigos —403 rol, 404
   * inexistente, 409 estado no editable— se muestran arriba con su propio texto.
   */
  const manejarErrorDelBack = async (fallo: unknown) => {
    const error = toErrorApi(fallo);
    const porCampo = erroresPorCampo(error);

    // Un mensaje sólo se considera "mostrado en su campo" si ese campo está
    // realmente en pantalla. tipoEvento y numeroDecretoLegislativo únicamente
    // se renderizan con "Proyecto de emergencia" marcado: si el back envía un
    // error sobre ellos estando oculto, marcar el campo no lo haría visible.
    const camposVisibles = Object.keys(PROYECTO_FORM_DEFAULTS).filter(
      (campo) =>
        getValues('esProyectoEmergencia') || !['tipoEvento', 'numeroDecretoLegislativo'].includes(campo),
    );
    const marcados = Object.entries(porCampo).filter(([campo]) => camposVisibles.includes(campo));

    marcados.forEach(([campo, mensaje]) => {
      setError(campo as keyof ProyectoFormValues, { type: 'server', message: mensaje });
    });
    if (marcados.length > 0) {
      setFocus(marcados[0][0] as keyof ProyectoFormValues);
    }

    // Lo que no pudo pintarse sobre un campo se muestra arriba, con su texto:
    // así un detalle sobre un campo oculto no se pierde en silencio.
    const sobrantes = Object.entries(porCampo).filter(([campo]) => !camposVisibles.includes(campo));
    if (marcados.length === 0 || sobrantes.length > 0) {
      const detalleSobrante = sobrantes.map(([, mensaje]) => mensaje).join(' ');
      const texto = [mensajeDeError(error, t), detalleSobrante].filter(Boolean).join(' ');
      await Swal.fire({ icon: 'error', text: texto });
    }
  };

  /**
   * Botón "Solicitar CUP" (SF-1.2 / FA-1). El back corre las validaciones del
   * Anexo B.2: si no hay inconsistencias el proyecto pasa a
   * ENVIADO_DGICP_REGISTRO y se vuelve al listado; si las hay responde 400 y se
   * cancela la acción, quedándose en esta pantalla con los campos marcados.
   */
  const solicitarCup = async () => {
    if (idProyecto === undefined) return;

    const confirmado = await confirmDialog(t('preinversion.registro.confirmarSolicitarCup'), {
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (!confirmado) return;

    setGuardando(true);
    try {
      const { data } = await preinversionApi.solicitarCup({ idProyecto });
      setEstadoActual(data.estado);
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.cupSolicitado') });
      navigate('/preinversion/proyectos');
    } catch (fallo) {
      await manejarErrorDelBack(fallo);
    } finally {
      setGuardando(false);
    }
  };

  /**
   * Botón "Enviar" de la sección Revisión PRE.
   *
   * El escenario dice que el Técnico URP "ajusta los campos correspondientes
   * y/o digita comentarios en Respuesta" y que al enviar el sistema "guarda los
   * datos registrados": por eso, si hay cambios en el formulario se persisten
   * antes (PUT /proyectos/{id}) y después se manda la respuesta
   * (POST /proyectos/{id}/respuestas-observacion). Son dos operaciones distintas
   * del contrato, no una.
   */
  const enviarRespuesta = async (respuesta: string) => {
    if (idProyecto === undefined) return;

    if (!respuesta.trim()) {
      setErrorRespuesta(t('preinversion.revisionPre.respuestaObligatoria'));
      return;
    }
    setErrorRespuesta(undefined);
    setGuardando(true);

    try {
      if (isDirty) {
        const valores = getValues();
        const { data } = await preinversionApi.actualizarProyecto({
          idProyecto,
          proyectoRequest: formValuesToRequest(valores),
        });
        reset(proyectoToFormValues(data));
      }

      const { data } = await preinversionApi.responderObservacionCup({
        idProyecto,
        respuestaObservacionRequest: { respuesta: respuesta.trim() },
      });

      setRevisionPre(data.revisionPre ?? []);
      setEstadoActual(data.estado);
      // "se pasa a la pantalla Nuevo registro": se sigue aquí, con el hilo ya
      // actualizado; no se navega a otro sitio.
      await Swal.fire({ icon: 'success', text: t('preinversion.revisionPre.enviada') });
    } catch (fallo) {
      const detalleRespuesta = erroresPorCampo(toErrorApi(fallo)).respuesta;
      if (detalleRespuesta) setErrorRespuesta(detalleRespuesta);
      else await manejarErrorDelBack(fallo);
    } finally {
      setGuardando(false);
    }
  };

  /**
   * Botón "Devolver" de la sección Revisión PRE (CU-PRE-01.5-devolver.feature).
   *
   * El comentario no es obligatorio (a diferencia de "Respuesta" del Técnico
   * URP): se envía sólo si el Técnico PRE escribió algo.
   */
  const devolverSolicitud = async (comentario: string) => {
    if (idProyecto === undefined) return;

    const confirmado = await confirmDialog(t('preinversion.revisionPre.confirmarDevolver'), {
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (!confirmado) return;

    setGuardando(true);
    try {
      const { data } = await revisionCupApi.devolverSolicitudCup({
        idProyecto,
        devolucionSolicitudRequest: comentario.trim() ? { comentario: comentario.trim() } : undefined,
      });
      setRevisionPre(data.revisionPre ?? []);
      setEstadoActual(data.estado);
      // "el sistema pasa a la pantalla Nuevo registro": se sigue aquí, igual que al responder.
      await Swal.fire({ icon: 'success', text: t('preinversion.revisionPre.devuelta') });
    } catch (fallo) {
      await manejarErrorDelBack(fallo);
    } finally {
      setGuardando(false);
    }
  };

  /** Botón "Emitir CUP" (CU-PRE-01.5-emitir-cup.feature): asigna el CUP y envía a la bandeja. */
  const emitirCup = async () => {
    if (idProyecto === undefined) return;

    const confirmado = await confirmDialog(t('preinversion.registro.confirmarEmitirCup'), {
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (!confirmado) return;

    setGuardando(true);
    try {
      const { data } = await revisionCupApi.emitirCup({ idProyecto });
      setEstadoActual(data.estado);
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.cupEmitido', { cup: data.cup ?? '' }) });
      // "el sistema envía el proyecto a la pantalla Captura de Proyectos (UC-PRE-03)": fuera
      // del alcance de este fragmento; se regresa al listado, como en "Solicitar CUP".
      navigate('/preinversion/proyectos');
    } catch (fallo) {
      await manejarErrorDelBack(fallo);
    } finally {
      setGuardando(false);
    }
  };

  const onSubmit = async (valores: ProyectoFormValues) => {
    setGuardando(true);
    try {
      const payload = formValuesToRequest(valores);
      if (esNuevo) {
        await preinversionApi.registrarProyecto({ proyectoRequest: payload });
      } else if (idProyecto !== undefined) {
        await preinversionApi.actualizarProyecto({ idProyecto, proyectoRequest: payload });
      }
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
      navigate('/preinversion/proyectos');
    } catch (fallo) {
      await manejarErrorDelBack(fallo);
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) {
    return <p>{t('common.cargando')}</p>;
  }

  if (errorCarga) {
    return (
      <div className="aviso-error" role="alert">
        <p>{errorCarga}</p>
        <button type="button" className="btn neutro" onClick={() => navigate('/preinversion/proyectos')}>
          {t('preinversion.registro.botonRegresar')}
        </button>
      </div>
    );
  }

  return (
    <>
      {/* Escenario "Solo consulta mientras el proyecto está Enviado a DGICP":
          los campos ya van deshabilitados, pero sin decir por qué el usuario
          solo ve una pantalla muerta. */}
      {!esNuevo && !puedeEditar && estadoActual && (
        <div className="aviso-consulta" role="status">
          {t('preinversion.registro.soloConsulta', { estado: formatEstado(estadoActual) })}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <div className="formcard">
          <div className="formhead">
            <span>{t(esNuevo ? 'preinversion.registro.tituloNuevo' : 'preinversion.registro.tituloEditar')}</span>
            {estadoActual && (
              <span className="marca-estado" aria-label={t('preinversion.registro.estadoActual')}>
                {formatEstado(estadoActual)}
              </span>
            )}
          </div>
          <div className="formbody">
            <div className="fr">
        {/* Los cuatro campos que el CU-PRE-01 marca "Seleccionable: No" (§B.1):
            el servidor los asigna y la pantalla sólo los muestra. En un
            registro nuevo todavía no existen, así que se indica que se
            asignarán al guardar en vez de dejar el hueco en blanco. */}
        <FormRow label={t('preinversion.registro.campoInstitucion')} ancho>
          <p className="campo-asignado">
            {asignados.institucion ?? t('preinversion.registro.seAsignaAlGuardar')}
          </p>
        </FormRow>

        <FormRow label={t('preinversion.registro.campoUnidadEjecutora')} ancho>
          <p className="campo-asignado">
            {asignados.unidadEjecutora ?? t('preinversion.registro.seAsignaAlGuardar')}
          </p>
        </FormRow>

        <FormRow label={t('preinversion.registro.campoIniciativa')} required ancho error={errors.iniciativaInversion?.message}>
          <div className="radios">
            {INICIATIVA_OPCIONES.map((opcion) => (
              <label key={opcion.valor} htmlFor={`iniciativa-${opcion.valor}`}>
                <input
                  type="radio"
                  className={errors.iniciativaInversion ? 'malo' : undefined}
                  id={`iniciativa-${opcion.valor}`}
                  value={opcion.valor}
                  disabled={!puedeEditar}
                  {...register('iniciativaInversion')}
                />
                {t(opcion.labelKey)}
              </label>
            ))}
          </div>
        </FormRow>

        <FormRow controlId="nombre" label={t('preinversion.registro.campoNombre')} required ancho error={errors.nombre?.message}>
          <input
            className={errors.nombre ? 'malo' : undefined}
            placeholder={t('preinversion.registro.phNombre')}
            disabled={!puedeEditar}
            id="nombre"
            {...register('nombre')}
          />
        </FormRow>

        <FormRow controlId="montoEstimadoInversion" label={t('preinversion.registro.campoMonto')} required error={errors.montoEstimadoInversion?.message}>
          {/* type="text" y no "number": un campo numérico nativo rechaza las
              comas, y el §B.2 pide separador de miles. inputMode deja el
              teclado numérico en móvil. El valor se limpia al convertir a
              ProyectoRequest. */}
          <input
            type="text"
            inputMode="decimal"
            className={errors.montoEstimadoInversion ? 'malo' : undefined}
            placeholder="0.00"
            disabled={!puedeEditar}
            id="montoEstimadoInversion"
            {...registroMonto}
            onChange={(e) => {
              e.target.value = conSeparadorDeMiles(e.target.value);
              return registroMonto.onChange(e);
            }}
          />
        </FormRow>

        <FormRow controlId="idSector" label={t('preinversion.registro.campoSector')} required error={errors.idSector?.message}>
          <select
            className={errors.idSector ? 'malo' : undefined}
            disabled={!puedeEditar}
            id="idSector"
            {...register('idSector')}
          >
            <option value="">{t('common.seleccione')}</option>
            {sectores.map((sector) => (
              <option key={sector.idSector} value={sector.idSector}>
                {sector.nombre}
              </option>
            ))}
          </select>
        </FormRow>

        {/* "Se asignará automáticamente por el Sistema de acuerdo con el Sector
            seleccionado" (§B.2). No se pide: se muestra. */}
        <FormRow label={t('preinversion.registro.campoMacrosector')}>
          <p className="campo-asignado">
            {asignados.macrosector ?? t('preinversion.registro.segunSector')}
          </p>
        </FormRow>

        <FormRow controlId="idEjeTematico" label={t('preinversion.registro.campoEjeTematico')} required ancho error={errors.idEjeTematico?.message}>
          <select
            className={errors.idEjeTematico ? 'malo' : undefined}
            disabled={!puedeEditar}
            id="idEjeTematico"
            {...register('idEjeTematico')}
          >
            <option value="">{t('common.seleccione')}</option>
            {ejesTematicos.map((eje) => (
              <option key={eje.idEjeTematico} value={eje.idEjeTematico}>
                {eje.nombre}
              </option>
            ))}
          </select>
        </FormRow>

        <FormRow controlId="idEjePlanGobierno" label={t('preinversion.registro.campoEjePlanGobierno')} error={errors.idEjePlanGobierno?.message}>
          <select className={errors.idEjePlanGobierno ? 'malo' : undefined} disabled={!puedeEditar} id="idEjePlanGobierno"
            {...register('idEjePlanGobierno')}>
            <option value="">{t('common.noAplica')}</option>
            {ejesPlanGobierno.map((eje) => (
              <option key={eje.idEjePlanGobierno} value={eje.idEjePlanGobierno}>
                {eje.nombre}
              </option>
            ))}
          </select>
        </FormRow>

        <FormRow controlId="idPlanSectorialRegional" label={t('preinversion.registro.campoPlanSectorialRegional')} error={errors.idPlanSectorialRegional?.message}>
          <select className={errors.idPlanSectorialRegional ? 'malo' : undefined} disabled={!puedeEditar} id="idPlanSectorialRegional"
            {...register('idPlanSectorialRegional')}>
            <option value="">{t('common.noAplica')}</option>
            {planesSectoriales.map((plan) => (
              <option key={plan.idPlanSectorialRegional} value={plan.idPlanSectorialRegional}>
                {plan.nombre}
              </option>
            ))}
          </select>
        </FormRow>

        <div className="f w">
          <label>
            {t('preinversion.registro.campoMedidas')}
            <span className="ayuda">{t('preinversion.registro.ayudaMedidas')}</span>
            <button type="button" className="enlace" onClick={() => setMostrarCategorias(true)}>
              {t('preinversion.registro.botonVerCategorias')}
            </button>
          </label>
          <div>
            <div className="medidas">
              <div>
                <MedidasCatalogoField
                  tipo={TipoMedidaCatalogo.Grd}
                  label={t('preinversion.registro.campoMedidasGrd')}
                  value={medidasGrd}
                  onChange={(valor) => setValue('medidasGrd', valor, { shouldDirty: true })}
                  disabled={!puedeEditar}
                />
                {errors.medidasGrd && <span className="error">{errors.medidasGrd.message}</span>}
              </div>
              <div>
                <MedidasCatalogoField
                  tipo={TipoMedidaCatalogo.Grc}
                  label={t('preinversion.registro.campoMedidasGrc')}
                  value={medidasGrc}
                  onChange={(valor) => setValue('medidasGrc', valor, { shouldDirty: true })}
                  disabled={!puedeEditar}
                />
                {errors.medidasGrc && <span className="error">{errors.medidasGrc.message}</span>}
              </div>
              <div>
                <MedidasCatalogoField
                  tipo={TipoMedidaCatalogo.Acc}
                  label={t('preinversion.registro.campoMedidasAcc')}
                  value={medidasAcc}
                  onChange={(valor) => setValue('medidasAcc', valor, { shouldDirty: true })}
                  disabled={!puedeEditar}
                />
                {errors.medidasAcc && <span className="error">{errors.medidasAcc.message}</span>}
              </div>
            </div>
          </div>
        </div>

        <FormRow label={t('preinversion.registro.campoEmergencia')} ancho error={errors.esProyectoEmergencia?.message}>
          <div className="radios">
            <label htmlFor="emergencia-si">
              <input type="radio" id="emergencia-si" disabled={!puedeEditar}
                checked={esProyectoEmergencia === true}
                onChange={() => setValue('esProyectoEmergencia', true, { shouldDirty: true })} />
              {t('common.si')}
            </label>
            <label htmlFor="emergencia-no">
              <input type="radio" id="emergencia-no" disabled={!puedeEditar}
                checked={esProyectoEmergencia !== true}
                onChange={() => setValue('esProyectoEmergencia', false, { shouldDirty: true })} />
              {t('common.no')}
            </label>
          </div>
        </FormRow>

        {esProyectoEmergencia && (
          <>
            <FormRow controlId="tipoEvento" label={t('preinversion.registro.campoTipoEvento')} required error={errors.tipoEvento?.message}>
              <input
                className={errors.tipoEvento ? 'malo' : undefined}
                disabled={!puedeEditar}
                id="tipoEvento"
            {...register('tipoEvento')}
              />
            </FormRow>
            <FormRow controlId="numeroDecretoLegislativo" label={t('preinversion.registro.campoNumeroDecreto')} required error={errors.numeroDecretoLegislativo?.message}>
              <input
                className={errors.numeroDecretoLegislativo ? 'malo' : undefined}
                disabled={!puedeEditar}
                id="numeroDecretoLegislativo"
            {...register('numeroDecretoLegislativo')}
              />
            </FormRow>
          </>
        )}

        {/* "La fecha de radicación no debe ser diligenciada manualmente por el
            usuario; el sistema la asigna al radicar" (cliente, 02/09/2026). */}
        <FormRow label={t('preinversion.registro.campoFechaRadicacion')}>
          <p className="campo-asignado">
            {asignados.fechaIngreso
              ? new Date(asignados.fechaIngreso).toLocaleDateString()
              : t('preinversion.registro.seAsignaAlGuardar')}
          </p>
        </FormRow>

        <FormRow controlId="descripcionProyecto" label={t('preinversion.registro.campoDescripcion')} required ancho error={errors.descripcionProyecto?.message}>
          <textarea
            className={errors.descripcionProyecto ? 'malo' : undefined}
            placeholder={t('preinversion.registro.phDescripcion')}
            rows={4}
            disabled={!puedeEditar}
            id="descripcionProyecto"
            {...register('descripcionProyecto')}
          />
        </FormRow>

        {!esNuevo && (
          <RevisionPre
            comentarios={revisionPre}
            puedeResponder={puedeEditar && estadoActual === 'OBSERVADO_DGICP_REGISTRO'}
            puedeDevolver={puedeRevisarPre}
            enviando={guardando}
            errorRespuesta={errorRespuesta}
            onEnviar={enviarRespuesta}
            onDevolver={devolverSolicitud}
          />
        )}

            </div>

            {/* Campos que están en el diseño pero no en el contrato del API.
                Se listan en vez de dibujarlos: si se dibujaran, el usuario los
                rellenaría y al guardar se perderían sin aviso. */}
            <div className="sin-respaldo">
              <b>{t('preinversion.registro.sinRespaldo')}</b>
              <ul>
                {CAMPOS_SIN_RESPALDO.map((campo) => (
                  <li key={campo}>{campo}</li>
                ))}
              </ul>
              <span>{t('preinversion.registro.sinRespaldoNota')}</span>
            </div>
          </div>

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={regresar} disabled={guardando}>
            {t('preinversion.registro.botonRegresar')}
          </button>
          <div className="esp" />
          {puedeEditar && (
            <button type="submit" className="btn primario" disabled={guardando}>
              {t('preinversion.registro.botonGuardar')}
            </button>
          )}
          {/* Solo sobre un registro ya guardado: el endpoint necesita idProyecto
              y el back valida lo persistido, no lo que hay en pantalla. Por eso
              se bloquea mientras haya cambios sin guardar, en vez de guardar
              por detrás sin que el usuario lo pida. */}
          {puedeEditar && !esNuevo && (
            <button
              type="button"
              className="btn secundario"
              onClick={solicitarCup}
              disabled={guardando || isDirty}
              title={isDirty ? t('preinversion.registro.guardarAntesDeSolicitar') : undefined}
            >
              {t('preinversion.registro.botonSolicitarCup')}
            </button>
          )}
          {puedeRevisarPre && (
            <button type="button" className="btn secundario" onClick={emitirCup} disabled={guardando}>
              {t('preinversion.registro.botonEmitirCup')}
            </button>
          )}
          {puedeIrARegistroEtapas && (
            <button
              type="button"
              className="btn secundario"
              onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/etapas`)}
            >
              {t('preinversion.registro.botonIrARegistroEtapas')}
            </button>
          )}
        </div>
        </div>
        {puedeEditar && !esNuevo && isDirty && (
          <p className="nota-form">{t('preinversion.registro.guardarAntesDeSolicitar')}</p>
        )}
      </form>

      {mostrarCategorias && <CategoriasCatalogoModal onClose={() => setMostrarCategorias(false)} />}
    </>
  );
}

