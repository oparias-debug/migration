import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import { catalogosApi, TipoCampo } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import {
  CAMPO_DEFAULT,
  CATALOGO_FORM_DEFAULTS,
  catalogoSchema,
  lineasAValores,
  type CatalogoFormValues,
} from './catalogoFormSchema';

const ROL_ADMIN = 'ADMINISTRADOR_CATALOGOS';

/**
 * Crear catálogo — Flujo Principal del CU-ADM-01 (§6).
 *
 * Sólo cubre ese flujo. Los alternativos (§7.1 a §7.8) empiezan todos con "el
 * Administrador selecciona un catálogo existente", y el contrato no expone
 * ninguna operación para listar catálogos ni para leer uno por su código: no
 * hay GET /catalogos ni GET /catalogos/{codigo}. Sin la definición de un
 * catálogo no se pueden dibujar ni su ficha ni el formulario de sus registros,
 * porque los campos los define el propio catálogo. Reportado; en cuanto existan
 * esas dos operaciones se puede continuar.
 */
export function CrearCatalogoPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const [guardando, setGuardando] = useState(false);
  const [nombreEnUso, setNombreEnUso] = useState(false);

  const puedeAdministrar = hasRole(ROL_ADMIN);

  const {
    control,
    register,
    handleSubmit,
    watch,
    setValue,
    reset,
    formState: { errors },
  } = useForm<CatalogoFormValues>({
    resolver: zodResolver(catalogoSchema),
    defaultValues: CATALOGO_FORM_DEFAULTS,
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'campos' });
  const campos = watch('campos');

  /**
   * Aviso de nombre repetido, al salir del campo. El contrato tiene
   * `buscarCatalogoPorNombre` justamente para esto; no bloquea el envío, porque
   * quien decide sobre la unicidad es el servidor.
   */
  const comprobarNombre = async (nombre: string) => {
    if (nombre.trim() === '') return setNombreEnUso(false);
    try {
      const { data } = await catalogosApi.buscarCatalogoPorNombre({ nombre: nombre.trim() });
      setNombreEnUso(data.existe);
    } catch {
      // Es una ayuda, no una validación: si falla la consulta no se molesta al
      // usuario, el servidor rechazará el alta si de verdad está repetido.
      setNombreEnUso(false);
    }
  };

  /** Sólo un campo puede ser la clave (regla 2: al menos uno; el CU trata KEY como único). */
  const marcarClave = (indice: number) => {
    campos.forEach((_, i) => setValue(`campos.${i}.esKey`, i === indice, { shouldDirty: true }));
  };

  const onSubmit = async (valores: CatalogoFormValues) => {
    setGuardando(true);
    try {
      await catalogosApi.crearCatalogo({
        crearCatalogoRequest: {
          codigo: valores.codigo.trim(),
          nombre: valores.nombre.trim(),
          catalogoPadreCodigo: valores.catalogoPadreCodigo.trim() || null,
          // La vigencia sólo se manda si el usuario puso alguna fecha: sin ella
          // el servidor aplica la regla 12 y deja el catálogo ACTIVE.
          vigencia:
            valores.fechaDesde || valores.fechaHasta
              ? { fechaDesde: valores.fechaDesde || null, fechaHasta: valores.fechaHasta || null }
              : undefined,
          campos: valores.campos.map((campo) => ({
            nombre: campo.nombre.trim(),
            tipo: campo.tipo,
            esKey: campo.esKey,
            valoresEnum: campo.tipo === TipoCampo.Enum ? lineasAValores(campo.valoresEnum) : undefined,
          })),
        },
      });
      await Swal.fire({ icon: 'success', text: t('administracion.catalogos.creado') });
      reset(CATALOGO_FORM_DEFAULTS);
      setNombreEnUso(false);
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  if (!puedeAdministrar) {
    return (
      <div className="aviso-error" role="alert">
        <p>{t('errores.permiso')}</p>
      </div>
    );
  }

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t('administracion.catalogos.tituloCrear')}</span>
      </div>
      <div className="formbody">
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="fr">
            <FormRow
              label={t('administracion.catalogos.codigo')}
              controlId="cat-codigo"
              required
              error={errors.codigo?.message}
            >
              <input id="cat-codigo" type="text" {...register('codigo')} />
            </FormRow>

            <FormRow
              label={t('administracion.catalogos.nombre')}
              controlId="cat-nombre"
              required
              error={errors.nombre?.message}
              ayuda={nombreEnUso ? t('administracion.catalogos.nombreEnUso') : undefined}
            >
              <input
                id="cat-nombre"
                type="text"
                {...register('nombre', { onBlur: (e) => comprobarNombre(e.target.value) })}
              />
            </FormRow>

            <FormRow label={t('administracion.catalogos.catalogoPadre')} controlId="cat-padre">
              <input id="cat-padre" type="text" {...register('catalogoPadreCodigo')} />
            </FormRow>

            <FormRow label={t('administracion.catalogos.vigenciaDesde')} controlId="cat-desde">
              <input id="cat-desde" type="date" {...register('fechaDesde')} />
            </FormRow>

            <FormRow label={t('administracion.catalogos.vigenciaHasta')} controlId="cat-hasta">
              <input id="cat-hasta" type="date" {...register('fechaHasta')} />
            </FormRow>
          </div>

          <fieldset className="campos-catalogo">
            <legend>{t('administracion.catalogos.campos')}</legend>

            {errors.campos?.message && (
              <p className="error" role="alert">
                {errors.campos.message}
              </p>
            )}

            <div className="tabla-cont">
              <table className="tabla">
                <thead>
                  <tr>
                    <th>{t('administracion.catalogos.campoNombre')}</th>
                    <th>{t('administracion.catalogos.campoTipo')}</th>
                    <th>{t('administracion.catalogos.campoClave')}</th>
                    <th>{t('administracion.catalogos.campoValores')}</th>
                    <th />
                  </tr>
                </thead>
                <tbody>
                  {fields.map((fila, indice) => (
                    <tr key={fila.id}>
                      <td>
                        <input
                          type="text"
                          aria-label={t('administracion.catalogos.campoNombreNumero', { numero: indice + 1 })}
                          className={errors.campos?.[indice]?.nombre ? 'malo' : undefined}
                          {...register(`campos.${indice}.nombre` as const)}
                        />
                        {errors.campos?.[indice]?.nombre && (
                          <span className="error">{errors.campos[indice]?.nombre?.message}</span>
                        )}
                      </td>
                      <td>
                        <select
                          aria-label={t('administracion.catalogos.campoTipoNumero', { numero: indice + 1 })}
                          {...register(`campos.${indice}.tipo` as const)}
                        >
                          {Object.values(TipoCampo).map((tipo) => (
                            <option key={tipo} value={tipo}>
                              {tipo}
                            </option>
                          ))}
                        </select>
                      </td>
                      <td>
                        <input
                          type="radio"
                          name="campo-clave"
                          aria-label={t('administracion.catalogos.campoClaveNumero', { numero: indice + 1 })}
                          checked={campos[indice]?.esKey ?? false}
                          onChange={() => marcarClave(indice)}
                        />
                      </td>
                      <td>
                        {campos[indice]?.tipo === TipoCampo.Enum ? (
                          <>
                            <textarea
                              rows={2}
                              aria-label={t('administracion.catalogos.campoValoresNumero', { numero: indice + 1 })}
                              placeholder={t('administracion.catalogos.campoValoresAyuda')}
                              className={errors.campos?.[indice]?.valoresEnum ? 'malo' : undefined}
                              {...register(`campos.${indice}.valoresEnum` as const)}
                            />
                            {errors.campos?.[indice]?.valoresEnum && (
                              <span className="error">{errors.campos[indice]?.valoresEnum?.message}</span>
                            )}
                          </>
                        ) : (
                          <span className="no-aplica">—</span>
                        )}
                      </td>
                      <td>
                        <button
                          type="button"
                          className="btn neutro"
                          disabled={fields.length === 1}
                          aria-label={t('administracion.catalogos.eliminarCampo', { numero: indice + 1 })}
                          onClick={() => remove(indice)}
                        >
                          ✕
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <button type="button" className="btn secundario" onClick={() => append({ ...CAMPO_DEFAULT })}>
              {t('administracion.catalogos.agregarCampo')}
            </button>
          </fieldset>

          <div className="acciones-form">
            <button type="submit" className="btn primario" disabled={guardando}>
              {guardando ? t('common.guardando') : t('administracion.catalogos.crear')}
            </button>
          </div>
        </form>

        {/* Lo que el contrato todavía no permite hacer desde aquí. Se dice en
            pantalla en vez de dibujar botones que no llevarían a ningún sitio. */}
        <div className="sin-respaldo">
          <b>{t('administracion.catalogos.sinRespaldoTitulo')}</b>
          <ul>
            <li>{t('administracion.catalogos.sinRespaldoListar')}</li>
            <li>{t('administracion.catalogos.sinRespaldoAbrir')}</li>
          </ul>
          <span>{t('administracion.catalogos.sinRespaldoNota')}</span>
        </div>
      </div>
    </div>
  );
}
