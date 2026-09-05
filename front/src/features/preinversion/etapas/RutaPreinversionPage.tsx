import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import { etapasApi, preinversionApi, IniciativaInversion } from '../../../api/preinversionApi';
import type { CriteriosCalificacion, NombreEtapa, RutaPreinversionSugerida } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import {
  COMPLEJIDAD_OPCIONES,
  NOMBRE_ETAPA_OPCIONES,
  TAMANIO_PROYECTO_OPCIONES,
  TIPO_CAPITAL_OPCIONES,
  formatComplejidad,
  formatNombreEtapa,
  formatTamanioProyecto,
  formatTipoCapital,
} from './etapasLabels';
import {
  CRITERIOS_FORM_DEFAULTS,
  criteriosCalificacionSchema,
  type CriteriosCalificacionFormValues,
} from './etapasFormSchemas';

// Pantalla "Ruta de Preinversión" (Anexo A.2, CU-PRE-3.5-generar-aceptar-ruta.feature /
// CU-PRE-3.5-modificar-ruta.feature).
export function RutaPreinversionPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [esIniciativaProyecto, setEsIniciativaProyecto] = useState(false);
  const [etapasAceptadas, setEtapasAceptadas] = useState<NombreEtapa[]>([]);
  const [fueModificada, setFueModificada] = useState(false);
  const [sugerencia, setSugerencia] = useState<RutaPreinversionSugerida | null>(null);
  const [modificando, setModificando] = useState(false);
  const [etapasSeleccionadas, setEtapasSeleccionadas] = useState<NombreEtapa[]>([]);
  const [justificacion, setJustificacion] = useState('');
  const [errorJustificacion, setErrorJustificacion] = useState<string | undefined>();

  const puedeGestionar = hasRole('TECNICO_URP');

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CriteriosCalificacionFormValues>({
    resolver: zodResolver(criteriosCalificacionSchema),
    defaultValues: CRITERIOS_FORM_DEFAULTS as CriteriosCalificacionFormValues,
  });

  useEffect(() => {
    if (!idProyecto) return;
    Promise.all([preinversionApi.obtenerProyecto({ idProyecto }), etapasApi.obtenerRutaPreinversion({ idProyecto })])
      .then(([proyectoRes, rutaRes]) => {
        setEsIniciativaProyecto(proyectoRes.data.iniciativaInversion === IniciativaInversion.Proyecto);
        setEtapasAceptadas(rutaRes.data.etapasAceptadas);
        setFueModificada(rutaRes.data.fueModificada);
        setEtapasSeleccionadas(rutaRes.data.etapasAceptadas);
      })
      .catch((fallo) => setErrorCarga(mensajeDeError(toErrorApi(fallo), t)))
      .finally(() => setCargando(false));
  }, [idProyecto, t]);

  const generar = async (valores: CriteriosCalificacionFormValues) => {
    setGuardando(true);
    try {
      const { data } = await etapasApi.generarRutaPreinversion({
        idProyecto,
        criteriosCalificacion: valores as CriteriosCalificacion,
      });
      setSugerencia(data);
      setEtapasSeleccionadas(data.etapasSugeridas);
    } catch (fallo) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(fallo), t) });
    } finally {
      setGuardando(false);
    }
  };

  const aceptar = async () => {
    if (!sugerencia) return;
    setGuardando(true);
    try {
      const { data } = await etapasApi.aceptarRutaPreinversion({ idProyecto, criteriosCalificacion: sugerencia.criterios });
      setEtapasAceptadas(data.etapasAceptadas);
      setFueModificada(data.fueModificada);
      setSugerencia(null);
      await Swal.fire({ icon: 'success', text: t('preinversion.rutaPreinversion.rutaAceptada') });
      navigate(`/preinversion/proyectos/${idProyecto}/etapas`);
    } catch (fallo) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(fallo), t) });
    } finally {
      setGuardando(false);
    }
  };

  const alternarEtapaSeleccionada = (etapa: NombreEtapa) =>
    setEtapasSeleccionadas((actuales) =>
      actuales.includes(etapa) ? actuales.filter((e) => e !== etapa) : [...actuales, etapa],
    );

  const modificar = async () => {
    if (!justificacion.trim()) {
      setErrorJustificacion(t('preinversion.rutaPreinversion.justificacionObligatoria'));
      return;
    }
    if (etapasSeleccionadas.length === 0) {
      await Swal.fire({ icon: 'error', text: t('preinversion.rutaPreinversion.seleccioneAlMenosUnaEtapa') });
      return;
    }
    setErrorJustificacion(undefined);
    setGuardando(true);
    try {
      const { data } = await etapasApi.modificarRutaPreinversion({
        idProyecto,
        modificarRutaPreinversionRequest: { justificacion: justificacion.trim(), etapas: etapasSeleccionadas },
      });
      setEtapasAceptadas(data.etapasAceptadas);
      setFueModificada(data.fueModificada);
      setModificando(false);
      setSugerencia(null);
      await Swal.fire({ icon: 'success', text: t('preinversion.rutaPreinversion.rutaModificada') });
      navigate(`/preinversion/proyectos/${idProyecto}/etapas`);
    } catch (fallo) {
      const error = toErrorApi(fallo);
      if (error.clase === 'validacion') setErrorJustificacion(mensajeDeError(error, t));
      else await Swal.fire({ icon: 'error', text: mensajeDeError(error, t) });
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
        <span>{t('preinversion.rutaPreinversion.titulo')}</span>
      </div>
      <div className="formbody">
        {!esIniciativaProyecto && (
          // RN07/RN08: Programa y Estudios Generales no califican criterios; el botón
          // "Ruta de Preinversión" queda desactivado y la ruta ya viene fija.
          <p className="aviso-consulta">{t('preinversion.rutaPreinversion.rutaFijaProgramaEstudio')}</p>
        )}

        {esIniciativaProyecto && etapasAceptadas.length > 0 && !modificando && (
          <div className="fr">
            <FormRow label={t('preinversion.rutaPreinversion.etapasVigentes')} ancho>
              <p className="campo-asignado">{etapasAceptadas.map(formatNombreEtapa).join(', ')}</p>
            </FormRow>
            {fueModificada && <p className="aviso-consulta">{t('preinversion.rutaPreinversion.rutaFueModificada')}</p>}
          </div>
        )}

        {esIniciativaProyecto && !modificando && (
          <>
            {!sugerencia ? (
              puedeGestionar && (
                <form className="fr" onSubmit={handleSubmit(generar)} noValidate>
                  <FormRow controlId="tipoCapital" label={t('preinversion.rutaPreinversion.campoTipoCapital')} required error={errors.tipoCapital?.message}>
                    <select className={errors.tipoCapital ? 'malo' : undefined} id="tipoCapital" {...register('tipoCapital')}>
                      <option value="">{t('common.seleccione')}</option>
                      {TIPO_CAPITAL_OPCIONES.map((opcion) => (
                        <option key={opcion} value={opcion}>
                          {formatTipoCapital(opcion)}
                        </option>
                      ))}
                    </select>
                  </FormRow>
                  <FormRow controlId="tamanioProyecto" label={t('preinversion.rutaPreinversion.campoTamanioProyecto')} required error={errors.tamanioProyecto?.message}>
                    <select className={errors.tamanioProyecto ? 'malo' : undefined} id="tamanioProyecto" {...register('tamanioProyecto')}>
                      <option value="">{t('common.seleccione')}</option>
                      {TAMANIO_PROYECTO_OPCIONES.map((opcion) => (
                        <option key={opcion} value={opcion}>
                          {formatTamanioProyecto(opcion)}
                        </option>
                      ))}
                    </select>
                  </FormRow>
                  <FormRow controlId="complejidad" label={t('preinversion.rutaPreinversion.campoComplejidad')} required error={errors.complejidad?.message}>
                    <select className={errors.complejidad ? 'malo' : undefined} id="complejidad" {...register('complejidad')}>
                      <option value="">{t('common.seleccione')}</option>
                      {COMPLEJIDAD_OPCIONES.map((opcion) => (
                        <option key={opcion} value={opcion}>
                          {formatComplejidad(opcion)}
                        </option>
                      ))}
                    </select>
                  </FormRow>
                  <div className="acciones-form">
                    <button type="submit" className="btn primario" disabled={guardando}>
                      {t('preinversion.rutaPreinversion.botonGenerar')}
                    </button>
                    {etapasAceptadas.length > 0 && (
                      <button type="button" className="btn secundario" onClick={() => setModificando(true)} disabled={guardando}>
                        {t('preinversion.rutaPreinversion.botonModificar')}
                      </button>
                    )}
                  </div>
                </form>
              )
            ) : (
              <div className="fr">
                <FormRow label={t('preinversion.rutaPreinversion.etapasSugeridas')} ancho>
                  <p className="campo-asignado">{sugerencia.etapasSugeridas.map(formatNombreEtapa).join(', ')}</p>
                </FormRow>
                {puedeGestionar && (
                  <div className="acciones-form">
                    <button type="button" className="btn primario" onClick={aceptar} disabled={guardando}>
                      {t('preinversion.rutaPreinversion.botonAceptar')}
                    </button>
                    <button type="button" className="btn secundario" onClick={() => setModificando(true)} disabled={guardando}>
                      {t('preinversion.rutaPreinversion.botonModificar')}
                    </button>
                  </div>
                )}
              </div>
            )}
          </>
        )}

        {esIniciativaProyecto && modificando && puedeGestionar && (
          <div className="fr">
            <FormRow label={t('preinversion.rutaPreinversion.seleccioneEtapas')} ancho>
              <div className="radios">
                {NOMBRE_ETAPA_OPCIONES.map((etapa) => (
                  <label key={etapa} htmlFor={`etapa-${etapa}`}>
                    <input
                      type="checkbox"
                      id={`etapa-${etapa}`}
                      checked={etapasSeleccionadas.includes(etapa)}
                      onChange={() => alternarEtapaSeleccionada(etapa)}
                    />
                    {formatNombreEtapa(etapa)}
                  </label>
                ))}
              </div>
            </FormRow>
            <FormRow controlId="justificacion" label={t('preinversion.rutaPreinversion.campoJustificacion')} required ancho error={errorJustificacion}>
              <textarea
                id="justificacion"
                className={errorJustificacion ? 'malo' : undefined}
                rows={3}
                value={justificacion}
                onChange={(e) => setJustificacion(e.target.value)}
              />
            </FormRow>
            <div className="acciones-form">
              <button type="button" className="btn neutro" onClick={() => setModificando(false)} disabled={guardando}>
                {t('common.cancelar')}
              </button>
              <button type="button" className="btn primario" onClick={modificar} disabled={guardando}>
                {t('preinversion.rutaPreinversion.botonGuardarModificacion')}
              </button>
            </div>
          </div>
        )}

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}`)}>
            {t('preinversion.registro.botonRegresar')}
          </button>
        </div>
      </div>
    </div>
  );
}
