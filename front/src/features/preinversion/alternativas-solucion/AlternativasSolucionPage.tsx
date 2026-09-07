import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import { alternativasSolucionApi } from '../../../api/preinversionApi';
import type { RegistroAlternativas } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import {
  FILA_ALTERNATIVA_DEFAULT,
  JUSTIFICACION_MAXLENGTH,
  REGISTRO_ALTERNATIVAS_FORM_DEFAULTS,
  conSeparadorDeMiles,
  registroAlternativasSchema,
  sinSeparadorDeMiles,
  type RegistroAlternativasFormValues,
} from './alternativasSolucionFormSchema';

function registroToFormValues(registro: RegistroAlternativas): RegistroAlternativasFormValues {
  const alternativas = registro.alternativas ?? [];
  return {
    alternativas: alternativas.map((fila) => ({
      nombreAlternativa: fila.nombreAlternativa ?? '',
      montoAlternativa: fila.montoAlternativa != null ? conSeparadorDeMiles(String(fila.montoAlternativa)) : '',
      descripcionAlternativa: fila.descripcionAlternativa ?? '',
      seleccionada: fila.seleccionada ?? false,
    })),
    justificacion: registro.justificacion ?? '',
  };
}

// Pantalla "Registro de Alternativas de Solución" (Anexo A.1, CU-PRE-05-registrar-alternativas.feature).
// La pestaña "Identificación del proyecto" (CU-PRE-04) todavía no existe en este frontend, así que
// esta sección se accede directamente por URL, sin navegación de entrada ni de salida hacia
// "Análisis de interesados" (CU-PRE-06, tampoco implementado) — mismo criterio que EtapasPage con
// CU-PRE-04 a CU-PRE-26.
export function AlternativasSolucionPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [avanzando, setAvanzando] = useState(false);
  const [intentoGuardar, setIntentoGuardar] = useState(false);

  const puedeEditar = hasRole('TECNICO_URP');

  const {
    control,
    register,
    handleSubmit,
    watch,
    setValue,
    reset,
  } = useForm<RegistroAlternativasFormValues>({
    resolver: zodResolver(registroAlternativasSchema),
    defaultValues: REGISTRO_ALTERNATIVAS_FORM_DEFAULTS,
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'alternativas' });
  const alternativas = watch('alternativas');

  useEffect(() => {
    if (!idProyecto) return;
    alternativasSolucionApi
      .obtenerAlternativasSolucion({ idProyecto })
      .then(({ data }) => {
        const valores = registroToFormValues(data);
        // RN2-1: se muestra una fila por defecto en la tabla cuando todavía no hay ninguna.
        if (valores.alternativas.length === 0 && puedeEditar) {
          valores.alternativas = [{ ...FILA_ALTERNATIVA_DEFAULT }];
        }
        reset(valores);
      })
      .catch((fallo) => setErrorCarga(mensajeDeError(toErrorApi(fallo), t)))
      .finally(() => setCargando(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idProyecto, reset, t]);

  const seleccionarAlternativa = (indice: number) => {
    setValue(
      'alternativas',
      alternativas.map((fila, i) => ({ ...fila, seleccionada: i === indice })),
      { shouldDirty: true },
    );
  };

  const onSubmit = async (valores: RegistroAlternativasFormValues) => {
    setIntentoGuardar(true);
    setGuardando(true);
    try {
      const { data } = await alternativasSolucionApi.guardarAlternativasSolucion({
        idProyecto,
        registroAlternativasRequest: {
          alternativas: valores.alternativas.map((fila) => ({
            nombreAlternativa: fila.nombreAlternativa || undefined,
            montoAlternativa: fila.montoAlternativa ? Number(sinSeparadorDeMiles(fila.montoAlternativa)) : undefined,
            descripcionAlternativa: fila.descripcionAlternativa || undefined,
            seleccionada: fila.seleccionada,
          })),
          justificacion: valores.justificacion || undefined,
        },
      });
      reset(registroToFormValues(data));
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (fallo) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(fallo), t) });
    } finally {
      setGuardando(false);
    }
  };

  const onAvanzar = async () => {
    setAvanzando(true);
    try {
      await alternativasSolucionApi.avanzarAAnalisisInteresados({ idProyecto });
      await Swal.fire({ icon: 'success', text: t('preinversion.alternativasSolucion.mensajeAvanzado') });
    } catch (fallo) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(fallo), t) });
    } finally {
      setAvanzando(false);
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

  const campoVacio = (valor: string) => intentoGuardar && puedeEditar && valor.trim() === '';

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t('preinversion.alternativasSolucion.titulo')}</span>
      </div>
      <div className="formbody">
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="tabla-cont">
            <table className="table table-sm table-bordered">
              <thead>
                <tr>
                  <th>{t('preinversion.alternativasSolucion.columnaNombre')}</th>
                  <th>{t('preinversion.alternativasSolucion.columnaMonto')}</th>
                  <th>{t('preinversion.alternativasSolucion.columnaDescripcion')}</th>
                  <th>{t('preinversion.alternativasSolucion.columnaSeleccionada')}</th>
                  {puedeEditar && <th />}
                </tr>
              </thead>
              <tbody>
                {fields.map((fila, indice) => (
                  <tr key={fila.id} className={alternativas[indice]?.seleccionada ? 'fila-resaltada' : undefined}>
                    <td>
                      <input
                        type="text"
                        aria-label={t('preinversion.alternativasSolucion.columnaNombre')}
                        className={campoVacio(alternativas[indice]?.nombreAlternativa ?? '') ? 'malo' : undefined}
                        disabled={!puedeEditar}
                        {...register(`alternativas.${indice}.nombreAlternativa`)}
                      />
                    </td>
                    <td>
                      <input
                        type="text"
                        inputMode="decimal"
                        aria-label={t('preinversion.alternativasSolucion.columnaMonto')}
                        className={campoVacio(alternativas[indice]?.montoAlternativa ?? '') ? 'malo' : undefined}
                        disabled={!puedeEditar}
                        {...register(`alternativas.${indice}.montoAlternativa`)}
                        onChange={(e) => {
                          e.target.value = conSeparadorDeMiles(e.target.value);
                          return register(`alternativas.${indice}.montoAlternativa`).onChange(e);
                        }}
                      />
                    </td>
                    <td>
                      <textarea
                        rows={2}
                        aria-label={t('preinversion.alternativasSolucion.columnaDescripcion')}
                        className={campoVacio(alternativas[indice]?.descripcionAlternativa ?? '') ? 'malo' : undefined}
                        disabled={!puedeEditar}
                        {...register(`alternativas.${indice}.descripcionAlternativa`)}
                      />
                    </td>
                    <td>
                      <input
                        type="radio"
                        name="alternativa-seleccionada"
                        aria-label={t('preinversion.alternativasSolucion.columnaSeleccionada')}
                        checked={alternativas[indice]?.seleccionada ?? false}
                        onChange={() => seleccionarAlternativa(indice)}
                        disabled={!puedeEditar}
                      />
                    </td>
                    {puedeEditar && (
                      <td>
                        <button type="button" className="btn neutro" onClick={() => remove(indice)}>
                          {t('preinversion.alternativasSolucion.botonEliminarFila')}
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {puedeEditar && (
            <button type="button" className="enlace" onClick={() => append({ ...FILA_ALTERNATIVA_DEFAULT })}>
              {t('preinversion.alternativasSolucion.botonAgregarFila')}
            </button>
          )}

          <div className="fr">
            <FormRow controlId="justificacion" label={t('preinversion.alternativasSolucion.campoJustificacion')} ancho>
              <textarea
                id="justificacion"
                rows={3}
                maxLength={JUSTIFICACION_MAXLENGTH}
                className={campoVacio(watch('justificacion')) && alternativas.length === 1 ? 'malo' : undefined}
                disabled={!puedeEditar}
                {...register('justificacion')}
              />
            </FormRow>
          </div>

          <p className="nota-form">{t('preinversion.alternativasSolucion.notaSinNavegacionIdentificacion')}</p>

          <div className="acciones-form">
            <button type="button" className="btn neutro" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}`)}>
              {t('preinversion.registro.botonRegresar')}
            </button>
            {puedeEditar && (
              <>
                <button type="submit" className="btn primario" disabled={guardando}>
                  {t('preinversion.registro.botonGuardar')}
                </button>
                <button type="button" className="btn secundario" onClick={onAvanzar} disabled={avanzando}>
                  {t('preinversion.alternativasSolucion.botonSiguiente')}
                </button>
              </>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
