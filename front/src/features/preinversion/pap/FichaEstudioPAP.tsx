import { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { TablaAnalisis, type ColumnaAnalisis } from '../analisis/TablaAnalisis';
import { cuatrimestreVigente, type Periodo } from './papComun';

/** El estudio y el período sobre los que se trabaja. */
export interface ContextoEstudio {
  readonly cup: string;
  readonly anio: number;
  readonly periodo: Periodo;
}

interface FichaProps<D, F extends Record<string, unknown>> {
  /** Clave de i18n de la pantalla. */
  readonly clave: string;
  /** Listado del que se viene y al que vuelve el botón. */
  readonly volverA: string;
  readonly columnas: readonly ColumnaAnalisis<F>[];
  readonly cargar: (contexto: ContextoEstudio) => Promise<D>;
  readonly guardar: (contexto: ContextoEstudio, filas: F[]) => Promise<D>;
  /** Qué mostrar del dato del servidor: el nombre del proyecto y las filas. */
  readonly leer: (dato: D) => { nombre: string; filas: F[] };
  /** Rol que puede escribir; los demás la ven en sólo lectura. */
  readonly rolEditor: string;
  /** Fila nueva, cuando el caso de uso deja agregarlas (sólo CU-PRE-30). */
  readonly filaVacia?: (filas: F[]) => F;
}

/**
 * Ficha de un estudio dentro del PAP: la pantalla a la que se entra al pulsar
 * una fila del listado (CU-PRE-30, 31, 32 y 33).
 *
 * Las cuatro son la misma pantalla —una tabla del estudio que se guarda
 * entera— sobre el mismo par de claves, el CUP y el año (más el cuatrimestre en
 * las de avance), así que lo común está aquí y cada caso de uso declara sus
 * columnas y su servicio. El año y el cuatrimestre llegan del listado por el
 * estado de la navegación; si se entra por la URL directamente, se toma el
 * período vigente, que es lo mismo que hace el servidor por omisión.
 */
export function FichaEstudioPAP<D, F extends Record<string, unknown>>({
  clave,
  volverA,
  columnas,
  cargar,
  guardar,
  leer,
  rolEditor,
  filaVacia,
}: FichaProps<D, F>) {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { cup = '' } = useParams<{ cup: string }>();
  const { state } = useLocation() as { state?: { anio?: number; periodo?: Periodo } };

  const contexto: ContextoEstudio = {
    cup,
    anio: state?.anio ?? new Date().getFullYear(),
    periodo: state?.periodo ?? cuatrimestreVigente(),
  };

  const [nombre, setNombre] = useState('');
  const [filas, setFilas] = useState<F[]>([]);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  const puedeEditar = hasRole(rolEditor);

  const volcar = (dato: D) => {
    const leido = leer(dato);
    setNombre(leido.nombre);
    setFilas(leido.filas);
  };

  useEffect(() => {
    if (!cup) return;
    cargar(contexto)
      .then(volcar)
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [cup, contexto.anio, contexto.periodo]);

  const alGuardar = async () => {
    setGuardando(true);
    try {
      volcar(await guardar(contexto, filas));
      await Swal.fire({ icon: 'success', text: t('preinversion.registro.mensajeGuardado') });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t(`${clave}.tituloFicha`)}</span>
        <span className="mono">
          · {cup} · {contexto.anio}
        </span>
      </div>
      <div className="formbody">
        {errorCarga && (
          <div className="aviso-error" role="alert">
            {errorCarga}
          </div>
        )}
        {cargando && <p className="cargando">{t('common.cargando')}</p>}

        {!cargando && !errorCarga && (
          <>
            <p className="nota">{nombre}</p>
            <TablaAnalisis<F>
              columnas={columnas}
              filas={filas}
              filaVacia={() => filaVacia?.(filas) as F}
              onCambiar={setFilas}
              puedeEditar={puedeEditar}
              permiteAgregar={Boolean(filaVacia)}
              sinFilas={`${clave}.sinFilas`}
            />
          </>
        )}

        <div className="acciones-form">
          <button type="button" className="btn neutro" onClick={() => navigate(volverA)}>
            {t('common.volver')}
          </button>
          {puedeEditar && (
            <button
              type="button"
              className="btn primario"
              disabled={cargando || guardando}
              onClick={() => void alGuardar()}
            >
              {t('common.guardar')}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
