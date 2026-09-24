import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { TablaAnalisis, type ColumnaAnalisis } from './TablaAnalisis';

/** Quien registra los análisis del capítulo 1.3.2 (x-roles de los tres CU). */
export const ROL_ANALISIS = 'TECNICO_URP';

/** "$1,750.58" en pantalla; number en el contrato. */
export const aNumero = (texto: unknown): number | undefined => {
  const limpio = String(texto ?? '').replace(/[^0-9.-]/g, '');
  if (limpio === '') return undefined;
  const n = Number(limpio);
  return Number.isFinite(n) ? n : undefined;
};

export const formatearMonto = (valor: number | null | undefined): string =>
  valor == null ? '—' : valor.toLocaleString('es-SV', { style: 'currency', currency: 'USD' });

interface PantallaProps<D, F extends Record<string, unknown>> {
  /** Clave de i18n de la pantalla (título, rótulos propios). */
  readonly clave: string;
  readonly columnas: readonly ColumnaAnalisis<F>[];
  readonly filaVacia: () => F;
  readonly cargar: (idProyecto: number) => Promise<D>;
  readonly guardar: (idProyecto: number, aplica: boolean | undefined, filas: F[]) => Promise<D>;
  /** Cómo leer del dato del servidor lo que la pantalla necesita. */
  readonly leer: (dato: D) => { aplica?: boolean | null; filas: F[]; total?: number | null };
  /** Rótulo de la pregunta de sí/no; ausente si el CU no la tiene. */
  readonly pregunta?: string;
  /** Rótulo del total calculado por el servidor. */
  readonly total?: string;
  /**
   * Empezar con la pantalla vacía si la consulta falla.
   *
   * CU-PRE-14: el servidor responde 500 —una RuntimeException— cuando el
   * proyecto todavía no tiene análisis ambiental, que es el caso de toda
   * primera visita; guardar sí lo crea, y a partir de ahí la consulta
   * funciona. Con esto la pantalla se puede usar; pedido a Cristian que
   * devuelva un análisis vacío, como hace CU-PRE-16.
   */
  readonly vacioSiFalla?: boolean;
  /** Acción del botón "Siguiente", si el CU la define. */
  readonly siguiente?: { readonly etiqueta: string; readonly accion: (idProyecto: number) => Promise<void>; readonly destino: string };
}

/**
 * Las tres pantallas de análisis del capítulo 1.3.2 comparten estructura: una
 * pregunta de sí/no, una tabla que se guarda entera y un total que calcula el
 * servidor. Lo que cambia son las columnas y el servicio, que llegan por
 * parámetro; así los tres casos de uso no repiten la misma pantalla tres veces.
 */
export function PantallaAnalisis<D, F extends Record<string, unknown>>({
  clave,
  columnas,
  filaVacia,
  cargar,
  guardar,
  leer,
  pregunta,
  total,
  siguiente,
  vacioSiFalla,
}: PantallaProps<D, F>) {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);

  const [aplica, setAplica] = useState<boolean | undefined>(undefined);
  const [filas, setFilas] = useState<F[]>([]);
  const [totalServidor, setTotalServidor] = useState<number | null | undefined>(undefined);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  const puedeEditar = hasRole(ROL_ANALISIS);

  const volcar = (dato: D) => {
    const { aplica: valor, filas: nuevas, total: suma } = leer(dato);
    setAplica(valor ?? undefined);
    setFilas(nuevas);
    setTotalServidor(suma);
  };

  useEffect(() => {
    if (!idProyecto) return;
    cargar(idProyecto)
      .then(volcar)
      .catch((error_) => {
        if (vacioSiFalla) return;
        setErrorCarga(mensajeDeError(toErrorApi(error_), t));
      })
      .finally(() => setCargando(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idProyecto]);

  const alGuardar = async () => {
    setGuardando(true);
    try {
      volcar(await guardar(idProyecto, aplica, filas));
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  const alSiguiente = async () => {
    if (!siguiente) return;
    setGuardando(true);
    try {
      await siguiente.accion(idProyecto);
      navigate(`/preinversion/proyectos/${idProyecto}/${siguiente.destino}`);
    } catch (error_) {
      // RN06: el servidor rechaza avanzar si falta la acción de mitigación de
      // un riesgo alto; su mensaje dice cuál, así que se muestra tal cual.
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
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

  // RN04 de CU-PRE-14 y CU-PRE-16: con la respuesta en "No", la tabla no se despliega.
  const muestraTabla = pregunta === undefined || aplica !== false;

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t(`${clave}.titulo`)}</span>
      </div>
      <div className="formbody">
        {pregunta && (
          <div className="fr">
            <FormRow label={t(pregunta)} ancho>
              <div className="radios">
                <label htmlFor={`${clave}-si`}>
                  <input
                    type="radio"
                    id={`${clave}-si`}
                    checked={aplica === true}
                    disabled={!puedeEditar}
                    onChange={() => setAplica(true)}
                  />
                  {t('common.si')}
                </label>
                <label htmlFor={`${clave}-no`}>
                  <input
                    type="radio"
                    id={`${clave}-no`}
                    checked={aplica === false}
                    disabled={!puedeEditar}
                    onChange={() => setAplica(false)}
                  />
                  {t('common.no')}
                </label>
              </div>
            </FormRow>
          </div>
        )}

        {muestraTabla && (
          <TablaAnalisis
            columnas={columnas}
            filas={filas}
            filaVacia={filaVacia}
            onCambiar={setFilas}
            puedeEditar={puedeEditar}
            sinFilas={`${clave}.sinFilas`}
          />
        )}

        {muestraTabla && total && (
          <p className="nota-form">
            <b>{t(total)}:</b> {formatearMonto(totalServidor)}
          </p>
        )}

        <div className="acciones-form">
          <button
            type="button"
            className="btn neutro"
            onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/estudio-tecnico`)}
          >
            {t('preinversion.registro.botonRegresar')}
          </button>
          {puedeEditar && (
            <button type="button" className="btn primario" onClick={alGuardar} disabled={guardando}>
              {t('preinversion.registro.botonGuardar')}
            </button>
          )}
          {puedeEditar && siguiente && (
            <button type="button" className="btn secundario" onClick={alSiguiente} disabled={guardando}>
              {t(siguiente.etiqueta)}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
