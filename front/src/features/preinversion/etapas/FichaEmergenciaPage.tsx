import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useFieldArray, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Swal from 'sweetalert2';
import { etapasApi, catalogoEtapasApi } from '../../../api/preinversionApi';
import type { FichaEmergencia, FuenteFinanciamiento, ProductoIndicador, TipoCostoResumen, UbicacionGeografica } from '../../../api/preinversionApi';
import { erroresPorCampo, mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { useCatalogo } from '../useCatalogo';
import { FUENTE_FINANCIAMIENTO_OPCIONES, formatFuenteFinanciamiento, formatNombreEtapa } from './etapasLabels';
import {
  FICHA_EMERGENCIA_FORM_DEFAULTS,
  conSeparadorDeMiles,
  fichaEmergenciaSchema,
  sinSeparadorDeMiles,
  type FichaEmergenciaFormValues,
} from './etapasFormSchemas';

function fichaToFormValues(ficha: FichaEmergencia): FichaEmergenciaFormValues {
  return {
    planteamientoProblema: ficha.planteamientoProblema ?? '',
    objetivoGeneral: ficha.objetivoGeneral ?? '',
    descripcionProyecto: ficha.descripcionProyecto ?? '',
    productos: (ficha.productos ?? []).map((p) => ({ codigoProducto: p.codigoProducto })),
    distrito: ficha.distrito ?? '',
    latitud: ficha.latitud != null ? String(ficha.latitud) : '',
    longitud: ficha.longitud != null ? String(ficha.longitud) : '',
    direccionEspecifica: ficha.direccionEspecifica ?? '',
    poblacionObjetivo: ficha.poblacionObjetivo ?? '',
    inversionEstimada: ficha.inversionEstimada != null ? conSeparadorDeMiles(String(ficha.inversionEstimada)) : '',
    componentesCosto: (ficha.componentesCosto ?? []).map((c) => ({ tipoCosto: c.tipoCosto, costo: conSeparadorDeMiles(String(c.costo)) })),
    costosOperacion: ficha.costosOperacion != null ? conSeparadorDeMiles(String(ficha.costosOperacion)) : '',
    costosMantenimiento: ficha.costosMantenimiento != null ? conSeparadorDeMiles(String(ficha.costosMantenimiento)) : '',
    fuentesFinanciamiento: ficha.fuentesFinanciamiento ?? [],
    fuenteRecursos: ficha.fuenteRecursos ?? '',
  };
}

// Pantalla "Ficha de proyectos de emergencia" (Anexo A.4, CU-PRE-3.5-registrar-ficha-emergencia.feature).
export function FichaEmergenciaPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [encabezado, setEncabezado] = useState<{ cup: string; nombreProyecto: string; etapaActual: string; etapaFutura: string } | null>(null);
  const [departamentoFiltro, setDepartamentoFiltro] = useState('');

  const puedeEditar = hasRole('TECNICO_URP');

  const productos: ProductoIndicador[] = useCatalogo(() => catalogoEtapasApi.listarProductosIndicadores());
  const tiposCosto: TipoCostoResumen[] = useCatalogo(() => catalogoEtapasApi.listarTiposCosto());
  const ubicaciones: UbicacionGeografica[] = useCatalogo(() => catalogoEtapasApi.listarUbicacionesGeograficas());
  const departamentos = Array.from(new Set(ubicaciones.map((u) => u.departamento))).sort();
  const distritosVisibles = departamentoFiltro ? ubicaciones.filter((u) => u.departamento === departamentoFiltro) : ubicaciones;

  const {
    register,
    control,
    handleSubmit,
    watch,
    setValue,
    setError,
    reset,
    formState: { errors },
  } = useForm<FichaEmergenciaFormValues>({
    resolver: zodResolver(fichaEmergenciaSchema),
    defaultValues: FICHA_EMERGENCIA_FORM_DEFAULTS,
  });
  const { fields: filasCosto, append: agregarFilaCosto, remove: quitarFilaCosto } = useFieldArray({ control, name: 'componentesCosto' });

  const productosSeleccionados = watch('productos');
  const fuentesSeleccionadas = watch('fuentesFinanciamiento');
  const distritoSeleccionado = watch('distrito');
  const esNivelNacional = distritoSeleccionado === 'Nivel nacional';

  useEffect(() => {
    if (!idProyecto) return;
    etapasApi
      .obtenerFichaEmergencia({ idProyecto })
      .then(({ data }) => {
        setEncabezado({
          cup: data.cup,
          nombreProyecto: data.nombreProyecto,
          etapaActual: data.etapaActual,
          etapaFutura: data.etapaFutura,
        });
        reset(fichaToFormValues(data));
      })
      .catch((fallo) => setErrorCarga(mensajeDeError(toErrorApi(fallo), t)))
      .finally(() => setCargando(false));
  }, [idProyecto, reset, t]);

  const alternarProducto = (codigoProducto: string) => {
    const yaSeleccionado = productosSeleccionados.some((p) => p.codigoProducto === codigoProducto);
    setValue(
      'productos',
      yaSeleccionado
        ? productosSeleccionados.filter((p) => p.codigoProducto !== codigoProducto)
        : [...productosSeleccionados, { codigoProducto }],
      { shouldDirty: true },
    );
  };

  const alternarFuente = (fuente: string) => {
    setValue(
      'fuentesFinanciamiento',
      fuentesSeleccionadas.includes(fuente)
        ? fuentesSeleccionadas.filter((f) => f !== fuente)
        : [...fuentesSeleccionadas, fuente],
      { shouldDirty: true },
    );
  };

  const onSubmit = async (valores: FichaEmergenciaFormValues) => {
    setGuardando(true);
    try {
      const { data } = await etapasApi.registrarFichaEmergencia({
        idProyecto,
        fichaEmergenciaRequest: {
          planteamientoProblema: valores.planteamientoProblema,
          objetivoGeneral: valores.objetivoGeneral || undefined,
          descripcionProyecto: valores.descripcionProyecto || undefined,
          productos: valores.productos,
          distrito: valores.distrito,
          latitud: valores.latitud ? Number(valores.latitud) : undefined,
          longitud: valores.longitud ? Number(valores.longitud) : undefined,
          direccionEspecifica: valores.direccionEspecifica || undefined,
          poblacionObjetivo: valores.poblacionObjetivo,
          inversionEstimada: valores.inversionEstimada ? Number(sinSeparadorDeMiles(valores.inversionEstimada)) : undefined,
          componentesCosto: valores.componentesCosto.map((c) => ({ tipoCosto: c.tipoCosto, costo: Number(sinSeparadorDeMiles(c.costo)) })),
          costosOperacion: valores.costosOperacion ? Number(sinSeparadorDeMiles(valores.costosOperacion)) : undefined,
          costosMantenimiento: valores.costosMantenimiento ? Number(sinSeparadorDeMiles(valores.costosMantenimiento)) : undefined,
          fuentesFinanciamiento: valores.fuentesFinanciamiento as FuenteFinanciamiento[],
          fuenteRecursos: valores.fuenteRecursos || undefined,
        },
      });
      reset(fichaToFormValues(data));
      await Swal.fire({ icon: 'success', text: t('preinversion.fichaEmergencia.mensajeGuardado') });
      navigate(`/preinversion/proyectos/${idProyecto}/etapas`);
    } catch (fallo) {
      const error = toErrorApi(fallo);
      const porCampo = erroresPorCampo(error);
      Object.entries(porCampo).forEach(([campo, mensaje]) => {
        setError(campo as keyof FichaEmergenciaFormValues, { type: 'server', message: mensaje });
      });
      if (Object.keys(porCampo).length === 0) {
        await Swal.fire({ icon: 'error', text: mensajeDeError(error, t) });
      }
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) return <p>{t('common.cargando')}</p>;

  if (errorCarga || !encabezado) {
    return (
      <div className="aviso-error" role="alert">
        <p>{errorCarga}</p>
      </div>
    );
  }

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t('preinversion.fichaEmergencia.titulo')}</span>
      </div>
      <div className="formbody">
        <div className="fr">
          <FormRow label={t('preinversion.fichaEmergencia.campoCup')}>
            <p className="campo-asignado">{encabezado.cup}</p>
          </FormRow>
          <FormRow label={t('preinversion.registro.campoNombre')}>
            <p className="campo-asignado">{encabezado.nombreProyecto}</p>
          </FormRow>
          <FormRow label={t('preinversion.fichaEmergencia.campoEtapaActual')}>
            <p className="campo-asignado">{formatNombreEtapa(encabezado.etapaActual)}</p>
          </FormRow>
          <FormRow label={t('preinversion.fichaEmergencia.campoEtapaFutura')}>
            <p className="campo-asignado">{formatNombreEtapa(encabezado.etapaFutura)}</p>
          </FormRow>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="fr">
            <FormRow controlId="planteamientoProblema" label={t('preinversion.fichaEmergencia.campoPlanteamiento')} required ancho error={errors.planteamientoProblema?.message}>
              <textarea
                id="planteamientoProblema"
                className={errors.planteamientoProblema ? 'malo' : undefined}
                rows={3}
                disabled={!puedeEditar}
                {...register('planteamientoProblema')}
              />
            </FormRow>

            <FormRow controlId="objetivoGeneral" label={t('preinversion.fichaEmergencia.campoObjetivoGeneral')} ancho>
              <textarea id="objetivoGeneral" rows={2} disabled={!puedeEditar} {...register('objetivoGeneral')} />
            </FormRow>

            <FormRow controlId="descripcionProyecto" label={t('preinversion.registro.campoDescripcion')} ancho>
              <textarea id="descripcionProyecto" rows={3} disabled={!puedeEditar} {...register('descripcionProyecto')} />
            </FormRow>

            <FormRow label={t('preinversion.fichaEmergencia.campoProductos')} required ancho error={errors.productos?.message}>
              <div className="medida-lista">
                {productos.map((producto) => (
                  <label key={producto.codigoProducto} htmlFor={`producto-${producto.codigoProducto}`}>
                    <input
                      type="checkbox"
                      id={`producto-${producto.codigoProducto}`}
                      checked={productosSeleccionados.some((p) => p.codigoProducto === producto.codigoProducto)}
                      onChange={() => alternarProducto(producto.codigoProducto)}
                      disabled={!puedeEditar}
                    />
                    {producto.producto}
                  </label>
                ))}
              </div>
            </FormRow>

            <FormRow controlId="departamentoFiltro" label={t('preinversion.fichaEmergencia.campoDepartamento')}>
              <select id="departamentoFiltro" value={departamentoFiltro} onChange={(e) => setDepartamentoFiltro(e.target.value)} disabled={!puedeEditar}>
                <option value="">{t('preinversion.registro.filtroTodos')}</option>
                {departamentos.map((departamento) => (
                  <option key={departamento} value={departamento}>
                    {departamento}
                  </option>
                ))}
              </select>
            </FormRow>

            <FormRow controlId="distrito" label={t('preinversion.fichaEmergencia.campoDistrito')} required error={errors.distrito?.message}>
              <select id="distrito" className={errors.distrito ? 'malo' : undefined} disabled={!puedeEditar} {...register('distrito')}>
                <option value="">{t('common.seleccione')}</option>
                {distritosVisibles.map((ubicacion) => (
                  <option key={ubicacion.distrito} value={ubicacion.distrito}>
                    {ubicacion.distrito}
                  </option>
                ))}
              </select>
            </FormRow>

            <FormRow controlId="latitud" label={t('preinversion.fichaEmergencia.campoLatitud')}>
              <input type="text" inputMode="decimal" id="latitud" disabled={!puedeEditar || esNivelNacional} {...register('latitud')} />
            </FormRow>
            <FormRow controlId="longitud" label={t('preinversion.fichaEmergencia.campoLongitud')}>
              <input type="text" inputMode="decimal" id="longitud" disabled={!puedeEditar || esNivelNacional} {...register('longitud')} />
            </FormRow>
            <FormRow controlId="direccionEspecifica" label={t('preinversion.fichaEmergencia.campoDireccion')} ancho>
              <input type="text" id="direccionEspecifica" disabled={!puedeEditar} {...register('direccionEspecifica')} />
            </FormRow>

            <FormRow controlId="poblacionObjetivo" label={t('preinversion.fichaEmergencia.campoPoblacionObjetivo')} required error={errors.poblacionObjetivo?.message}>
              <input
                type="text"
                id="poblacionObjetivo"
                className={errors.poblacionObjetivo ? 'malo' : undefined}
                disabled={!puedeEditar}
                {...register('poblacionObjetivo')}
              />
            </FormRow>

            <FormRow controlId="inversionEstimada" label={t('preinversion.fichaEmergencia.campoInversionEstimada')}>
              <input
                type="text"
                inputMode="decimal"
                id="inversionEstimada"
                disabled={!puedeEditar}
                {...register('inversionEstimada')}
                onChange={(e) => {
                  e.target.value = conSeparadorDeMiles(e.target.value);
                  return register('inversionEstimada').onChange(e);
                }}
              />
            </FormRow>

            <div className="f w">
              <label>{t('preinversion.fichaEmergencia.campoComponentesCosto')}</label>
              <div>
                {filasCosto.map((fila, indice) => (
                  <div key={fila.id} className="radios">
                    <select disabled={!puedeEditar} {...register(`componentesCosto.${indice}.tipoCosto`)}>
                      <option value="">{t('common.seleccione')}</option>
                      {tiposCosto.map((tipo) => (
                        <option key={tipo.codigo} value={tipo.codigo}>
                          {tipo.nombre}
                        </option>
                      ))}
                    </select>
                    <input
                      type="text"
                      inputMode="decimal"
                      disabled={!puedeEditar}
                      {...register(`componentesCosto.${indice}.costo`)}
                      onChange={(e) => {
                        e.target.value = conSeparadorDeMiles(e.target.value);
                        return register(`componentesCosto.${indice}.costo`).onChange(e);
                      }}
                    />
                    {puedeEditar && (
                      <button type="button" className="btn neutro" onClick={() => quitarFilaCosto(indice)}>
                        {t('common.cancelar')}
                      </button>
                    )}
                  </div>
                ))}
                {puedeEditar && (
                  <button type="button" className="enlace" onClick={() => agregarFilaCosto({ tipoCosto: '', costo: '' })}>
                    {t('preinversion.fichaEmergencia.botonAgregarComponente')}
                  </button>
                )}
              </div>
            </div>

            <FormRow controlId="costosOperacion" label={t('preinversion.fichaEmergencia.campoCostosOperacion')}>
              <input
                type="text"
                inputMode="decimal"
                id="costosOperacion"
                disabled={!puedeEditar}
                {...register('costosOperacion')}
                onChange={(e) => {
                  e.target.value = conSeparadorDeMiles(e.target.value);
                  return register('costosOperacion').onChange(e);
                }}
              />
            </FormRow>
            <FormRow controlId="costosMantenimiento" label={t('preinversion.fichaEmergencia.campoCostosMantenimiento')}>
              <input
                type="text"
                inputMode="decimal"
                id="costosMantenimiento"
                disabled={!puedeEditar}
                {...register('costosMantenimiento')}
                onChange={(e) => {
                  e.target.value = conSeparadorDeMiles(e.target.value);
                  return register('costosMantenimiento').onChange(e);
                }}
              />
            </FormRow>

            <FormRow label={t('preinversion.fichaEmergencia.campoFuentesFinanciamiento')} ancho>
              <div className="medida-lista">
                {FUENTE_FINANCIAMIENTO_OPCIONES.map((fuente) => (
                  <label key={fuente} htmlFor={`fuente-${fuente}`}>
                    <input
                      type="checkbox"
                      id={`fuente-${fuente}`}
                      checked={fuentesSeleccionadas.includes(fuente)}
                      onChange={() => alternarFuente(fuente)}
                      disabled={!puedeEditar}
                    />
                    {formatFuenteFinanciamiento(fuente)}
                  </label>
                ))}
              </div>
            </FormRow>

            <FormRow controlId="fuenteRecursos" label={t('preinversion.fichaEmergencia.campoFuenteRecursos')} ancho>
              <input type="text" id="fuenteRecursos" disabled={!puedeEditar} {...register('fuenteRecursos')} />
            </FormRow>
          </div>

          <div className="acciones-form">
            <button type="button" className="btn neutro" onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/etapas`)} disabled={guardando}>
              {t('preinversion.registro.botonRegresar')}
            </button>
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
