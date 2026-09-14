import { useEffect, useState } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { preinversionApi } from '../../../api/preinversionApi';
import { GRUPOS_PASOS, NUMERO_DE_PASO, rutaDePaso, ubicarPaso } from './pasosProyecto';

/**
 * Barra de pasos de un proyecto, sobre cada pantalla que cuelga de él.
 *
 * Existe porque, sin ella, varias pantallas terminadas no se alcanzaban
 * haciendo clic: Identificación, Alternativas y Presupuesto sólo se abrían
 * escribiendo la URL, y el recorrido se cortaba en Etapas.
 *
 * Es una ruta de diseño sin path: envuelve las rutas de los pasos y pinta la
 * pantalla del paso en el <Outlet />. Las pantallas no se tocan, y como el
 * layout sigue montado al pasar de un paso a otro, el nombre del proyecto se
 * pide una sola vez.
 *
 * Los grupos son botones que sólo cambian qué pasos se ven, sin navegar; los
 * pasos son enlaces, con aria-current="step" en el actual.
 */
export function PasosProyectoLayout() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const ubicacion = ubicarPaso(pathname);
  const idProyecto = ubicacion?.idProyecto;
  const grupoActual = ubicacion?.grupo.clave;

  const [grupoVisible, setGrupoVisible] = useState(grupoActual ?? GRUPOS_PASOS[0].clave);
  // Al llegar a un paso de otro grupo, la barra se va a ese grupo.
  useEffect(() => {
    if (grupoActual) setGrupoVisible(grupoActual);
  }, [grupoActual]);

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
              {t(g.texto)}
            </button>
          ))}
        </div>

        <nav className="pasos-lista" aria-label={t('pasos.titulo')}>
          <ol>
            {grupo.pasos.map((paso) => {
              const destino = rutaDePaso(ubicacion.idProyecto, paso);
              const esActual = paso.clave === ubicacion.paso.clave;
              const numero = (
                <span className="pasos-numero" aria-hidden="true">
                  {NUMERO_DE_PASO.get(paso.clave)}
                </span>
              );
              return (
                <li key={paso.clave}>
                  {destino ? (
                    <Link
                      to={destino}
                      className={`pasos-paso${esActual ? ' actual' : ''}`}
                      aria-current={esActual ? 'step' : undefined}
                    >
                      {numero}
                      {t(paso.texto)}
                    </Link>
                  ) : (
                    <span className="pasos-paso sin-pantalla" title={t('pasos.sinPantalla')}>
                      {numero}
                      {t(paso.texto)}
                      <span className="sr-only"> ({t('pasos.sinPantalla')})</span>
                    </span>
                  )}
                </li>
              );
            })}
          </ol>
        </nav>
      </div>

      <Outlet />
    </>
  );
}
