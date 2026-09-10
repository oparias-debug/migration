import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import { identificacionApi } from '../../../api/preinversionApi';
import type { ArchivoAdjuntoResumen, Identificacion } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import {
  ANTECEDENTES_MAXLENGTH,
  IDENTIFICACION_FORM_DEFAULTS,
  OBJETIVO_ESPECIFICO_DEFAULT,
  OBJETIVO_ESPECIFICO_MAXLENGTH,
  OBJETIVO_GENERAL_MAXLENGTH,
  PROBLEMA_CENTRAL_MAXLENGTH,
  identificacionSchema,
  type IdentificacionFormValues,
} from './identificacionFormSchema';

/** Los dos árboles se manejan igual; sólo cambian los métodos del cliente. */
type Arbol = 'problemas' | 'objetivos';

function identificacionToFormValues(datos: Identificacion): IdentificacionFormValues {
  return {
    antecedentes: datos.antecedentes ?? '',
    problemaCentral: datos.problemaCentral ?? '',
    objetivoGeneral: datos.objetivoGeneral ?? '',
    objetivosEspecificos: (datos.objetivosEspecificos ?? []).map((texto) => ({ texto })),
  };
}

/**
 * Pestaña "Identificación del proyecto" (Anexo A.1 del CU-PRE-04).
 *
 * Tres partes: los campos que asigna el servidor y aquí sólo se muestran
 * (Unidad Ejecutora, Nombre del proyecto y CUP), el formulario editable, y los
 * dos árboles en PDF.
 *
 * Sobre el "Siguiente": el CU dice que avanza a Alternativas de Solución
 * (CU-PRE-05), y esa pantalla ya existe, así que se navega de verdad. El
 * contrato de CU-PRE-04 no expone ninguna operación de avance —a diferencia de
 * CU-PRE-05, que sí tiene `avanzarAAnalisisInteresados`—, de modo que aquí es
 * sólo navegación: no hay transición de estado que registrar en el servidor.
 */
export function IdentificacionPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [intentoGuardar, setIntentoGuardar] = useState(false);
  // Cabecera de sólo lectura: la asigna el servidor, no se pide al usuario.
  const [cabecera, setCabecera] = useState<{ unidadEjecutora: string; nombreProyecto: string; cup: string }>({
    unidadEjecutora: '',
    nombreProyecto: '',
    cup: '',
  });
  const [adjuntos, setAdjuntos] = useState<Partial<Record<Arbol, ArchivoAdjuntoResumen>>>({});
  const [subiendo, setSubiendo] = useState<Arbol | null>(null);

  const entradaProblemas = useRef<HTMLInputElement>(null);
  const entradaObjetivos = useRef<HTMLInputElement>(null);
  const entradaDe = (arbol: Arbol) => (arbol === 'problemas' ? entradaProblemas : entradaObjetivos);

  const puedeEditar = hasRole('TECNICO_URP');

  const { control, register, handleSubmit, watch, reset } = useForm<IdentificacionFormValues>({
    resolver: zodResolver(identificacionSchema),
    defaultValues: IDENTIFICACION_FORM_DEFAULTS,
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'objetivosEspecificos' });
  const valores = watch();

  const aplicar = (datos: Identificacion) => {
    setCabecera({
      unidadEjecutora: datos.unidadEjecutora?.nombre ?? '',
      nombreProyecto: datos.nombreProyecto ?? '',
      cup: datos.cup ?? '',
    });
    // El contrato los declara nullable; el estado local usa `undefined` para
    // "no hay archivo", que es lo que entiende el renderizado.
    setAdjuntos({
      problemas: datos.archivoArbolProblemas ?? undefined,
      objetivos: datos.archivoArbolObjetivos ?? undefined,
    });
    reset(identificacionToFormValues(datos));
  };

  useEffect(() => {
    if (!idProyecto) return;
    identificacionApi
      .obtenerIdentificacion({ idProyecto })
      .then(({ data }) => {
        aplicar(data);
        // "Agregar una fila de Objetivo Específico": se arranca con una fila
        // para que haya dónde escribir sin tener que pulsar añadir primero.
        if ((data.objetivosEspecificos ?? []).length === 0 && hasRole('TECNICO_URP')) {
          append({ ...OBJETIVO_ESPECIFICO_DEFAULT });
        }
      })
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idProyecto, reset, t]);

  const onSubmit = async (v: IdentificacionFormValues) => {
    setIntentoGuardar(true);
    setGuardando(true);
    try {
      const { data } = await identificacionApi.guardarIdentificacion({
        idProyecto,
        identificacionRequest: {
          antecedentes: v.antecedentes || undefined,
          problemaCentral: v.problemaCentral || undefined,
          objetivoGeneral: v.objetivoGeneral || undefined,
          // Las filas vacías no se mandan: son andamiaje de la pantalla.
          objetivosEspecificos: v.objetivosEspecificos.map((o) => o.texto).filter((texto) => texto.trim() !== ''),
        },
      });
      aplicar(data);
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  const cargarArbol = async (arbol: Arbol, archivo: File) => {
    setSubiendo(arbol);
    try {
      const { data } =
        arbol === 'problemas'
          ? await identificacionApi.cargarArbolProblemas({ idProyecto, archivo })
          : await identificacionApi.cargarArbolObjetivos({ idProyecto, archivo });
      // Cargar sobre un árbol que ya tenía archivo lo reemplaza; el servidor
      // devuelve el resumen del que queda.
      setAdjuntos((previos) => ({ ...previos, [arbol]: data }));
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setSubiendo(null);
      // Se limpia para que volver a elegir el MISMO fichero dispare el change.
      const entrada = entradaDe(arbol).current;
      if (entrada) entrada.value = '';
    }
  };

  const descargarArbol = async (arbol: Arbol) => {
    try {
      const { data } =
        arbol === 'problemas'
          ? await identificacionApi.descargarArbolProblemas({ idProyecto }, { responseType: 'blob' })
          : await identificacionApi.descargarArbolObjetivos({ idProyecto }, { responseType: 'blob' });
      const url = URL.createObjectURL(data as unknown as Blob);
      const enlace = document.createElement('a');
      enlace.href = url;
      enlace.download = adjuntos[arbol]?.nombreArchivo ?? `arbol-${arbol}.pdf`;
      enlace.click();
      URL.revokeObjectURL(url);
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };

  const eliminarArbol = async (arbol: Arbol) => {
    const { isConfirmed } = await Swal.fire({
      text: t('preinversion.identificacion.confirmarEliminarArchivo'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (!isConfirmed) return;
    try {
      if (arbol === 'problemas') await identificacionApi.eliminarArbolProblemas({ idProyecto });
      else await identificacionApi.eliminarArbolObjetivos({ idProyecto });
      setAdjuntos((previos) => ({ ...previos, [arbol]: undefined }));
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
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

  // RNC-2: "sombrea en color rojo los bordes de los campos pendientes de
  // completar". Sólo tras intentar guardar, y sólo a quien puede editar.
  const pendiente = (valor: string) => intentoGuardar && puedeEditar && valor.trim() === '';

  const bloqueArbol = (arbol: Arbol) => {
    const adjunto = adjuntos[arbol];
    return (
      <div className="arbol-adjunto">
        <span className="arbol-titulo">{t(`preinversion.identificacion.arbol.${arbol}`)}</span>
        <input
          ref={entradaDe(arbol)}
          type="file"
          accept="application/pdf"
          hidden
          data-testid={`archivo-${arbol}`}
          onChange={(e) => {
            const archivo = e.target.files?.[0];
            if (archivo) void cargarArbol(arbol, archivo);
          }}
        />
        {adjunto ? (
          <span className="arbol-fichero">
            <button type="button" className="enlace-fila" onClick={() => descargarArbol(arbol)}>
              {adjunto.nombreArchivo}
            </button>
            {puedeEditar && (
              <>
                <button type="button" className="btn neutro" onClick={() => entradaDe(arbol).current?.click()}>
                  {t('preinversion.identificacion.reemplazar')}
                </button>
                <button type="button" className="btn neutro" onClick={() => eliminarArbol(arbol)}>
                  {t('common.eliminar')}
                </button>
              </>
            )}
          </span>
        ) : (
          <span className="arbol-fichero">
            <span className="sin-archivo">{t('preinversion.identificacion.sinArchivo')}</span>
            {puedeEditar && (
              <button
                type="button"
                className="btn secundario"
                disabled={subiendo === arbol}
                onClick={() => entradaDe(arbol).current?.click()}
              >
                {subiendo === arbol ? t('common.cargando') : t('preinversion.identificacion.agregar')}
              </button>
            )}
          </span>
        )}
      </div>
    );
  };

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t('preinversion.identificacion.titulo')}</span>
      </div>
      <div className="formbody">
        {/* Campos que asigna el servidor: se muestran, no se piden. */}
        <div className="fr">
          <FormRow label={t('preinversion.identificacion.unidadEjecutora')} controlId="id-unidad-ejecutora">
            <input id="id-unidad-ejecutora" type="text" value={cabecera.unidadEjecutora} readOnly />
          </FormRow>
          <FormRow label={t('preinversion.identificacion.nombreProyecto')} controlId="id-nombre-proyecto">
            <input id="id-nombre-proyecto" type="text" value={cabecera.nombreProyecto} readOnly />
          </FormRow>
          <FormRow label={t('preinversion.identificacion.cup')} controlId="id-cup">
            <input id="id-cup" type="text" value={cabecera.cup} readOnly />
          </FormRow>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <FormRow label={t('preinversion.identificacion.antecedentes')} controlId="id-antecedentes">
            <textarea
              rows={5}
              maxLength={ANTECEDENTES_MAXLENGTH}
              readOnly={!puedeEditar}
              aria-invalid={pendiente(valores.antecedentes)}
              className={pendiente(valores.antecedentes) ? 'malo' : undefined}
              id="id-antecedentes"
              {...register('antecedentes')}
            />
          </FormRow>

          <FormRow label={t('preinversion.identificacion.problemaCentral')} controlId="id-problema-central">
            <textarea
              rows={3}
              maxLength={PROBLEMA_CENTRAL_MAXLENGTH}
              readOnly={!puedeEditar}
              aria-invalid={pendiente(valores.problemaCentral)}
              className={pendiente(valores.problemaCentral) ? 'malo' : undefined}
              id="id-problema-central"
              {...register('problemaCentral')}
            />
          </FormRow>

          <FormRow label={t('preinversion.identificacion.objetivoGeneral')} controlId="id-objetivo-general">
            <textarea
              rows={3}
              maxLength={OBJETIVO_GENERAL_MAXLENGTH}
              readOnly={!puedeEditar}
              aria-invalid={pendiente(valores.objetivoGeneral)}
              className={pendiente(valores.objetivoGeneral) ? 'malo' : undefined}
              id="id-objetivo-general"
              {...register('objetivoGeneral')}
            />
          </FormRow>

          <fieldset className="objetivos-especificos">
            <legend>{t('preinversion.identificacion.objetivosEspecificos')}</legend>
            {fields.map((fila, indice) => (
              <div className="objetivo-fila" key={fila.id}>
                <input
                  type="text"
                  maxLength={OBJETIVO_ESPECIFICO_MAXLENGTH}
                  readOnly={!puedeEditar}
                  aria-label={t('preinversion.identificacion.objetivoNumero', { numero: indice + 1 })}
                  aria-invalid={pendiente(valores.objetivosEspecificos?.[indice]?.texto ?? '')}
                  className={pendiente(valores.objetivosEspecificos?.[indice]?.texto ?? '') ? 'malo' : undefined}
                  {...register(`objetivosEspecificos.${indice}.texto` as const)}
                />
                {puedeEditar && (
                  <button
                    type="button"
                    className="btn neutro"
                    aria-label={t('preinversion.identificacion.eliminarObjetivo', { numero: indice + 1 })}
                    onClick={() => remove(indice)}
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
                onClick={() => append({ ...OBJETIVO_ESPECIFICO_DEFAULT })}
              >
                {t('preinversion.identificacion.agregarObjetivo')}
              </button>
            )}
          </fieldset>

          <div className="arboles">{bloqueArbol('problemas')}{bloqueArbol('objetivos')}</div>

          <div className="acciones-form">
            <button
              type="button"
              className="btn neutro"
              onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/ruta-preinversion`)}
            >
              {t('common.regresar')}
            </button>
            {puedeEditar && (
              <button type="submit" className="btn primario" disabled={guardando}>
                {guardando ? t('common.guardando') : t('common.guardar')}
              </button>
            )}
            <button
              type="button"
              className="btn secundario"
              onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/alternativas-solucion`)}
            >
              {t('common.siguiente')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
