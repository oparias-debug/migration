import { useEffect, useState } from 'react';
import { Link, Outlet, useLocation, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { preinversionApi } from '../../../api/preinversionApi';
import { GRUPOS_PASOS, esClaveGrupo, rutaDePaso, ubicarPaso, type PasoProyecto } from './pasosProyecto';

/**
 * Un capítulo en la barra: enlace si tiene pantalla, marca sin enlace si no.
 * Muestra el código del árbol del sistema y, como ayuda, sus pestañas.
 */
function PasoEnBarra({
  paso,
  destino,
  esActual,
}: {
  readonly paso: PasoProyecto;
  readonly destino: string | null;
  readonly esActual: boolean;
}) {
  const { t } = useTranslation();
  const lista = paso.pestanas?.map((p) => t(p)).join(', ');
  const ayuda = lista ? t('pasos.pestanas', { lista }) : undefined;
  const contenido = (
    <>
      <span className="pasos-numero" aria-hidden="true">
        {paso.codigo}
      </span>
      {t(paso.texto)}
    </>
  );
  if (destino) {
    return (
      <Link
        to={destino}
        className={`pasos-paso${esActual ? ' actual' : ''}`}
        aria-current={esActual ? 'step' : undefined}
        title={ayuda}
      >
        {contenido}
      </Link>
    );
  }
  const sinPantalla = t('pasos.sinPantalla');
  return (
    <span className="pasos-paso sin-pantalla" title={ayuda ? `${sinPantalla} · ${ayuda}` : sinPantalla}>
      {contenido}
      <span className="sr-only"> ({sinPantalla})</span>
    </span>
  );
}

/**
 * Barra de pasos de un proyecto, sobre cada pantalla que cuelga de él.
 *
 * Sigue el árbol del sistema: arriba los procesos de Preinversión (1.2 a 1.5),
 * debajo sus subprocesos y capítulos con su código. Es una ruta de diseño sin
 * path: envuelve las rutas de los pasos y pinta la pantalla en el <Outlet />, así
 * que el nombre del proyecto se pide una sola vez al moverse entre pasos.
 *
 * Los procesos son botones que sólo cambian qué capítulos se ven, sin navegar;
 * los capítulos son enlaces, con aria-current="step" en el actual.
 */
export function PasosProyectoLayout() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const [parametros] = useSearchParams();
  const ubicacion = ubicarPaso(pathname);
  const idProyecto = ubicacion?.idProyecto;
  const grupoActual = ubicacion?.grupo.clave;
  // ?grupo= lo pone la opción de menú de un proceso que todavía no tiene
  // pantallas: la barra se abre en ese proceso aunque la pantalla sea de otro.
  const pedido = parametros.get('grupo');
  const grupoPedido = esClaveGrupo(pedido) ? pedido : null;

  const [grupoVisible, setGrupoVisible] = useState(grupoPedido ?? grupoActual ?? GRUPOS_PASOS[0].clave);
  useEffect(() => {
    const destino = grupoPedido ?? grupoActual;
    if (destino) setGrupoVisible(destino);
  }, [grupoActual, grupoPedido]);

  const [cabecera, setCabecera] = useState<{ nombre: string; cup: string | null } | null>(null);
  useEffect(() => {
    if (!idProyecto) return undefined;
    let vigente = true;
    setCabecera(null);
    preinversionApi
      .obtenerProyecto({ idProyecto })
      .then(({ data }) => {
        if (vigente) setCabecera({ nombre: data.nombre, cup: data.cup ?? null });
      })
      .catch(() => {
        // La cabecera es contexto: si no carga, la barra funciona igual.
      });
    return () => {
      vigente = false;
    };
  }, [idProyecto]);

  if (!ubicacion) return <Outlet />;

  const grupo = GRUPOS_PASOS.find((g) => g.clave === grupoVisible) ?? ubicacion.grupo;

  return (
    <>
      <div className="pasos-proyecto">
        {cabecera && (
          <p className="pasos-cabecera">
            <b>{cabecera.nombre}</b>
            {cabecera.cup && <span className="mono"> · CUP {cabecera.cup}</span>}
          </p>
        )}

        <div className="pasos-grupos">
          {GRUPOS_PASOS.map((g) => (
            <button
              key={g.clave}
              type="button"
              className={`pasos-grupo${g.clave === grupo.clave ? ' activo' : ''}${g.clave === grupoActual ? ' actual' : ''}`}
              aria-pressed={g.clave === grupo.clave}
              onClick={() => setGrupoVisible(g.clave)}
            >
              <span className="pasos-codigo">{g.codigo}</span> {t(g.texto)}
            </button>
          ))}
        </div>

        <nav className="pasos-lista" aria-label={t('pasos.titulo')}>
          {grupo.secciones.map((seccion) => (
            <div className="pasos-seccion" key={seccion.codigo ?? grupo.clave}>
              {seccion.texto && (
                <p className="pasos-seccion-titulo">
                  {seccion.codigo} {t(seccion.texto)}
                </p>
              )}
              <ol>
                {seccion.pasos.map((paso) => (
                  <li key={paso.clave}>
                    <PasoEnBarra
                      paso={paso}
                      destino={rutaDePaso(ubicacion.idProyecto, paso)}
                      esActual={paso.clave === ubicacion.paso.clave}
                    />
                  </li>
                ))}
              </ol>
            </div>
          ))}
        </nav>
      </div>

      <Outlet />
    </>
  );
}
