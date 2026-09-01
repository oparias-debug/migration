import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import { preinversionApi, catalogoPreinversionApi, IniciativaInversion, TipoMedidaCatalogo } from '../../../api/preinversionApi';
import type { ComentarioSolicitud, Proyecto, ProyectoRequest } from '../../../api/preinversionApi';
import { erroresPorCampo, mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { confirmDialog } from '../../../components/ConfirmDialog';
import { FormRow } from '../../../components/form/FormRow';
import { ESTADOS_EDITABLES, formatEstado } from './proyectoLabels';
import { MedidasCatalogoField } from './MedidasCatalogoField';
import { CategoriasCatalogoModal } from './CategoriasCatalogoModal';
import { useCatalogo } from './useCatalogo';
import { RevisionPre } from './RevisionPre';
import { proyectoFormSchema, PROYECTO_FORM_DEFAULTS, type ProyectoFormValues } from './proyectoFormSchema';

const INICIATIVA_OPCIONES = [
  { valor: IniciativaInversion.Programa, labelKey: 'preinversion.registro.opcionPrograma' },
  { valor: IniciativaInversion.Proyecto, labelKey: 'preinversion.registro.opcionProyecto' },
  { valor: IniciativaInversion.EstudioGeneral, labelKey: 'preinversion.registro.opcionEstudioGeneral' },
] as const;

function proyectoToFormValues(proyecto: Proyecto): ProyectoFormValues {
  return {
    iniciativaInversion: proyecto.iniciativaInversion,
    nombre: proyecto.nombre,
    montoEstimadoInversion: String(proyecto.montoEstimadoInversion),
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
    montoEstimadoInversion: Number(valores.montoEstimadoInversion),
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
      })
      .catch((fallo) => setErrorCarga(mensajeDeError(toErrorApi(fallo), t)))
      .finally(() => setCargando(false));
  }, [esNuevo, idProyecto, reset, t]);

  const esProyectoEmergencia = watch('esProyectoEmergencia');
  const medidasGrd = watch('medidasGrd');
  const medidasGrc = watch('medidasGrc');
  const medidasAcc = watch('medidasAcc');

  const sectores = useCatalogo(() => catalogoPreinversionApi.listarSectores());
  const ejesTematicos = useCatalogo(() => catalogoPreinversionApi.listarEjesTematicos());
  const ejesPlanGobierno = useCatalogo(() => catalogoPreinversionApi.listarEjesPlanGobierno());
  const planesSectoriales = useCatalogo(() => catalogoPreinversionApi.listarPlanesSectoriales());

  const puedeEditar = hasRole('TECNICO_URP') && (esNuevo || (estadoActual !== null && ESTADOS_EDITABLES.includes(estadoActual)));

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
      <div className="alert alert-danger" role="alert">
        <p className="mb-2">{errorCarga}</p>
        <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => navigate('/preinversion/proyectos')}>
          {t('preinversion.registro.botonRegresar')}
        </button>
      </div>
    );
  }

  return (
    <>
      <div className="d-flex align-items-center gap-3 mb-2">
        <h1 className="mb-0">{t(esNuevo ? 'preinversion.registro.tituloNuevo' : 'preinversion.registro.tituloEditar')}</h1>
        {estadoActual && (
          <span className="badge text-bg-secondary" aria-label={t('preinversion.registro.estadoActual')}>
            {formatEstado(estadoActual)}
          </span>
        )}
      </div>

      {/* Escenario "Solo consulta mientras el proyecto está Enviado a DGICP":
          los campos ya van deshabilitados, pero sin decir por qué el usuario
          solo ve una pantalla muerta. */}
      {!esNuevo && !puedeEditar && estadoActual && (
        <div className="alert alert-info" role="status">
          {t('preinversion.registro.soloConsulta', { estado: formatEstado(estadoActual) })}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <FormRow label={t('preinversion.registro.campoIniciativa')} required error={errors.iniciativaInversion?.message}>
          {INICIATIVA_OPCIONES.map((opcion) => (
            <div className="form-check form-check-inline" key={opcion.valor}>
              <input
                type="radio"
                className={`form-check-input${errors.iniciativaInversion ? ' is-invalid' : ''}`}
                id={`iniciativa-${opcion.valor}`}
                value={opcion.valor}
                disabled={!puedeEditar}
                {...register('iniciativaInversion')}
              />
              <label className="form-check-label" htmlFor={`iniciativa-${opcion.valor}`}>
                {t(opcion.labelKey)}
              </label>
            </div>
          ))}
        </FormRow>

        <FormRow controlId="nombre" label={t('preinversion.registro.campoNombre')} required error={errors.nombre?.message}>
          <input
            className={`form-control${errors.nombre ? ' is-invalid' : ''}`}
            disabled={!puedeEditar}
            id="nombre"
            {...register('nombre')}
          />
        </FormRow>

        <FormRow controlId="montoEstimadoInversion" label={t('preinversion.registro.campoMonto')} required error={errors.montoEstimadoInversion?.message}>
          <input
            type="number"
            step="0.01"
            min="0"
            className={`form-control${errors.montoEstimadoInversion ? ' is-invalid' : ''}`}
            disabled={!puedeEditar}
            id="montoEstimadoInversion"
            {...register('montoEstimadoInversion')}
          />
        </FormRow>

        <FormRow controlId="idSector" label={t('preinversion.registro.campoSector')} required error={errors.idSector?.message}>
          <select
            className={`form-select${errors.idSector ? ' is-invalid' : ''}`}
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

        <FormRow controlId="idEjeTematico" label={t('preinversion.registro.campoEjeTematico')} required error={errors.idEjeTematico?.message}>
          <select
            className={`form-select${errors.idEjeTematico ? ' is-invalid' : ''}`}
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
          <select className={`form-select${errors.idEjePlanGobierno ? ' is-invalid' : ''}`} disabled={!puedeEditar} id="idEjePlanGobierno"
            {...register('idEjePlanGobierno')}>
            <option value="">{t('common.seleccione')}</option>
            {ejesPlanGobierno.map((eje) => (
              <option key={eje.idEjePlanGobierno} value={eje.idEjePlanGobierno}>
                {eje.nombre}
              </option>
            ))}
          </select>
        </FormRow>

        <FormRow controlId="idPlanSectorialRegional" label={t('preinversion.registro.campoPlanSectorialRegional')} error={errors.idPlanSectorialRegional?.message}>
          <select className={`form-select${errors.idPlanSectorialRegional ? ' is-invalid' : ''}`} disabled={!puedeEditar} id="idPlanSectorialRegional"
            {...register('idPlanSectorialRegional')}>
            <option value="">{t('common.seleccione')}</option>
            {planesSectoriales.map((plan) => (
              <option key={plan.idPlanSectorialRegional} value={plan.idPlanSectorialRegional}>
                {plan.nombre}
              </option>
            ))}
          </select>
        </FormRow>

        <div className="row mb-3">
          <div className="col-md-2">
            <button type="button" className="btn btn-link p-0" onClick={() => setMostrarCategorias(true)}>
              {t('preinversion.registro.botonVerCategorias')}
            </button>
          </div>
          <div className="col-md-10">
            <div className="row">
              <div className="col-md-4">
                <MedidasCatalogoField
                  tipo={TipoMedidaCatalogo.Grd}
                  label={t('preinversion.registro.campoMedidasGrd')}
                  value={medidasGrd}
                  onChange={(valor) => setValue('medidasGrd', valor, { shouldDirty: true })}
                  disabled={!puedeEditar}
                />
                {errors.medidasGrd && <div className="invalid-feedback d-block">{errors.medidasGrd.message}</div>}
              </div>
              <div className="col-md-4">
                <MedidasCatalogoField
                  tipo={TipoMedidaCatalogo.Grc}
                  label={t('preinversion.registro.campoMedidasGrc')}
                  value={medidasGrc}
                  onChange={(valor) => setValue('medidasGrc', valor, { shouldDirty: true })}
                  disabled={!puedeEditar}
                />
                {errors.medidasGrc && <div className="invalid-feedback d-block">{errors.medidasGrc.message}</div>}
              </div>
              <div className="col-md-4">
                <MedidasCatalogoField
                  tipo={TipoMedidaCatalogo.Acc}
                  label={t('preinversion.registro.campoMedidasAcc')}
                  value={medidasAcc}
                  onChange={(valor) => setValue('medidasAcc', valor, { shouldDirty: true })}
                  disabled={!puedeEditar}
                />
                {errors.medidasAcc && <div className="invalid-feedback d-block">{errors.medidasAcc.message}</div>}
              </div>
            </div>
          </div>
        </div>

        <FormRow controlId="esProyectoEmergencia" label={t('preinversion.registro.campoEmergencia')} error={errors.esProyectoEmergencia?.message}>
          <input type="checkbox" className={`form-check-input${errors.esProyectoEmergencia ? ' is-invalid' : ''}`} disabled={!puedeEditar} id="esProyectoEmergencia"
            {...register('esProyectoEmergencia')} />
        </FormRow>

        {esProyectoEmergencia && (
          <>
            <FormRow controlId="tipoEvento" label={t('preinversion.registro.campoTipoEvento')} required error={errors.tipoEvento?.message}>
              <input
                className={`form-control${errors.tipoEvento ? ' is-invalid' : ''}`}
                disabled={!puedeEditar}
                id="tipoEvento"
            {...register('tipoEvento')}
              />
            </FormRow>
            <FormRow controlId="numeroDecretoLegislativo" label={t('preinversion.registro.campoNumeroDecreto')} required error={errors.numeroDecretoLegislativo?.message}>
              <input
                className={`form-control${errors.numeroDecretoLegislativo ? ' is-invalid' : ''}`}
                disabled={!puedeEditar}
                id="numeroDecretoLegislativo"
            {...register('numeroDecretoLegislativo')}
              />
            </FormRow>
          </>
        )}

        <FormRow controlId="descripcionProyecto" label={t('preinversion.registro.campoDescripcion')} required error={errors.descripcionProyecto?.message}>
          <textarea
            className={`form-control${errors.descripcionProyecto ? ' is-invalid' : ''}`}
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
            enviando={guardando}
            errorRespuesta={errorRespuesta}
            onEnviar={enviarRespuesta}
          />
        )}

        <div className="d-flex gap-2">
          <button type="button" className="btn btn-secondary" onClick={regresar} disabled={guardando}>
            {t('preinversion.registro.botonRegresar')}
          </button>
          {puedeEditar && (
            <button type="submit" className="btn btn-primary" disabled={guardando}>
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
              className="btn btn-success"
              onClick={solicitarCup}
              disabled={guardando || isDirty}
              title={isDirty ? t('preinversion.registro.guardarAntesDeSolicitar') : undefined}
            >
              {t('preinversion.registro.botonSolicitarCup')}
            </button>
          )}
        </div>
        {puedeEditar && !esNuevo && isDirty && (
          <p className="form-text mt-2">{t('preinversion.registro.guardarAntesDeSolicitar')}</p>
        )}
      </form>

      {mostrarCategorias && <CategoriasCatalogoModal onClose={() => setMostrarCategorias(false)} />}
    </>
  );
}

