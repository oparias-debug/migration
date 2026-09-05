import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import { etapasApi, preinversionApi } from '../../../api/preinversionApi';
import type { Etapa } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { formatNombreEtapa } from './etapasLabels';
import { actualizarEtapasSchema, conSeparadorDeMiles, sinSeparadorDeMiles, type ActualizarEtapasFormValues } from './etapasFormSchemas';

function etapasToFormValues(etapas: Etapa[]): ActualizarEtapasFormValues {
  return {
    etapas: etapas.map((etapa) => ({
      nombreEtapa: etapa.nombreEtapa,
      costo: etapa.costo != null ? conSeparadorDeMiles(String(etapa.costo)) : '',
      fechaInicio: etapa.fechaInicio ?? '',
      fechaFin: etapa.fechaFin ?? '',
    })),
  };
}

// Pantalla "Registro de Etapas" (Anexo A.1, CU-PRE-3.5-registrar-etapas.feature). Un solo botón
// "Guardar" para toda la tabla (RN04): el PUT actualizarEtapas recibe todas las filas a la vez.
export function EtapasPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [etapasOriginales, setEtapasOriginales] = useState<Etapa[]>([]);
  const [esProyectoEmergencia, setEsProyectoEmergencia] = useState(false);

  const puedeEditar = hasRole('TECNICO_URP');

  const {
    control,
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ActualizarEtapasFormValues>({
    resolver: zodResolver(actualizarEtapasSchema),
    defaultValues: { etapas: [] },
  });
  const { fields } = useFieldArray({ control, name: 'etapas' });

  useEffect(() => {
    if (!idProyecto) return;
    Promise.all([etapasApi.listarEtapas({ idProyecto }), preinversionApi.obtenerProyecto({ idProyecto })])
      .then(([etapasRes, proyectoRes]) => {
        setEtapasOriginales(etapasRes.data);
        setEsProyectoEmergencia(proyectoRes.data.esProyectoEmergencia ?? false);
        reset(etapasToFormValues(etapasRes.data));
      })
      .catch((fallo) => setErrorCarga(mensajeDeError(toErrorApi(fallo), t)))
      .finally(() => setCargando(false));
  }, [idProyecto, reset, t]);

  const onSubmit = async (valores: ActualizarEtapasFormValues) => {
    setGuardando(true);
    try {
      const { data } = await etapasApi.actualizarEtapas({
        idProyecto,
        actualizarEtapasRequest: {
          etapas: valores.etapas.map((fila) => ({
            nombreEtapa: fila.nombreEtapa,
            costo: Number(sinSeparadorDeMiles(fila.costo || '0')),
            fechaInicio: fila.fechaInicio,
            fechaFin: fila.fechaFin,
          })),
        },
      });
      setEtapasOriginales(data);
      reset(etapasToFormValues(data));
      await Swal.fire({ icon: 'success', text: t('preinversion.registroEtapas.mensajeGuardado') });
    } catch (fallo) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(fallo), t) });
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

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t('preinversion.registroEtapas.titulo')}</span>
      </div>
      <div className="formbody">
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="tabla-cont">
            <table className="table table-sm table-bordered">
              <thead>
                <tr>
                  <th>{t('preinversion.registroEtapas.columnaEtapa')}</th>
                  <th>{t('preinversion.registroEtapas.columnaCosto')}</th>
                  <th>{t('preinversion.registroEtapas.columnaFechaInicio')}</th>
                  <th>{t('preinversion.registroEtapas.columnaFechaFin')}</th>
                  <th>{t('preinversion.registroEtapas.columnaEstado')}</th>
                </tr>
              </thead>
              <tbody>
                {fields.map((fila, indice) => {
                  const original = etapasOriginales[indice];
                  const esEjecucion = fila.nombreEtapa === 'EJECUCION';
                  const bloqueada = original?.bloqueadaPorModificacion ?? false;
                  const deshabilitada = !puedeEditar || bloqueada;
                  return (
                    <tr key={fila.id}>
                      <td>{formatNombreEtapa(fila.nombreEtapa)}</td>
                      <td>
                        {esEjecucion ? (
                          <span title={t('preinversion.registroEtapas.notaCostoEjecucion')}>
                            {original?.costo != null ? conSeparadorDeMiles(String(original.costo)) : '—'}
                          </span>
                        ) : (
                          <>
                            <input
                              type="text"
                              inputMode="decimal"
                              className={errors.etapas?.[indice]?.costo ? 'malo' : undefined}
                              disabled={deshabilitada}
                              {...register(`etapas.${indice}.costo`)}
                              onChange={(e) => {
                                e.target.value = conSeparadorDeMiles(e.target.value);
                                return register(`etapas.${indice}.costo`).onChange(e);
                              }}
                            />
                            {errors.etapas?.[indice]?.costo && (
                              <span className="error">{errors.etapas[indice]?.costo?.message}</span>
                            )}
                          </>
                        )}
                      </td>
                      <td>
                        <input
                          type="text"
                          placeholder="dd/mm/aaaa"
                          className={errors.etapas?.[indice]?.fechaInicio ? 'malo' : undefined}
                          disabled={deshabilitada}
                          {...register(`etapas.${indice}.fechaInicio`)}
                        />
                        {errors.etapas?.[indice]?.fechaInicio && (
                          <span className="error">{errors.etapas[indice]?.fechaInicio?.message}</span>
                        )}
                      </td>
                      <td>
                        <input
                          type="text"
                          placeholder="dd/mm/aaaa"
                          className={errors.etapas?.[indice]?.fechaFin ? 'malo' : undefined}
                          disabled={deshabilitada}
                          {...register(`etapas.${indice}.fechaFin`)}
                        />
                        {errors.etapas?.[indice]?.fechaFin && (
                          <span className="error">{errors.etapas[indice]?.fechaFin?.message}</span>
                        )}
                      </td>
                      <td>
                        {original?.habilitadoParaRegistro && (
                          <span className="marca-estado e-ok">{t('preinversion.registroEtapas.estadoHabilitada')}</span>
                        )}
                        {original?.tieneOpinionTecnica && (
                          <span className="marca-estado e-info">{t('preinversion.registroEtapas.estadoConOpinionTecnica')}</span>
                        )}
                        {bloqueada && (
                          <span className="marca-estado e-aviso">{t('preinversion.registroEtapas.estadoBloqueada')}</span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {/* CU-PRE-04 a CU-PRE-26 (identificación/formulación/evaluación/programación) no están
              implementados en este frontend: no hay a dónde navegar aunque habilitadoParaRegistro
              sea true, así que no se dibuja un botón de navegación por etapa. */}
          <p className="nota-form">{t('preinversion.registroEtapas.notaSinNavegacion')}</p>

          <div className="acciones-form">
            <button type="button" className="btn neutro" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}`)}>
              {t('preinversion.registro.botonRegresar')}
            </button>
            <button type="button" className="btn secundario" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/ruta-preinversion`)}>
              {t('preinversion.registroEtapas.botonRutaPreinversion')}
            </button>
            <button type="button" className="btn secundario" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/ficha-informacion-general`)}>
              {t('preinversion.registroEtapas.botonFichaGeneral')}
            </button>
            {esProyectoEmergencia && (
              <button type="button" className="btn secundario" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/ficha-emergencia`)}>
                {t('preinversion.registroEtapas.botonFichaEmergencia')}
              </button>
            )}
            {puedeEditar && (
              <button type="submit" className="btn primario" disabled={guardando}>
                {t('preinversion.registro.botonGuardar')}
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
