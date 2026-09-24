import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { IconoMascara } from '../components/Icono';
import { useAuth } from '../auth/useAuth';
import { MODULOS, destinoDe, type SubModulo } from '../layout/navegacion';

/** Descripción de cada subproceso, para que el botón diga a qué lleva. */
const DESCRIPCION: Record<string, string> = {
  'asignacion-cup': 'preinversionInicio.asignacionCup',
  'creacion-ruta': 'preinversionInicio.creacionRuta',
  formulacion: 'preinversionInicio.formulacion',
  'programacion-proyecto': 'preinversionInicio.programacionProyecto',
  'gestion-proyecto': 'preinversionInicio.gestionProyecto',
};

/**
 * Entrada del macroproceso Preinversión.
 *
 * El botón grande de Inicio llevaba directamente al listado de solicitudes, y
 * desde ahí no se veía en qué parte del proceso estaba uno ni cómo seguir el
 * orden (Rocío, 24/09/2026). Ahora lleva aquí: los subprocesos 1.1 a 1.5 del
 * árbol del sistema, en su orden, cada uno con su número y su descripción.
 *
 * Los subprocesos salen del mismo modelo que pinta el menú lateral, así que un
 * usuario sólo ve los que su rol puede abrir, y el destino de cada uno es el
 * mismo al que lleva el menú.
 */
export function PreinversionPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();

  const preinversion = MODULOS.find((m) => m.clave === 'preinversion');
  const visible = (sub: SubModulo) => !sub.rolesRequeridos || sub.rolesRequeridos.some(hasRole);
  const subprocesos = (preinversion?.submenu ?? []).filter(visible);

  return (
    <>
      <h2 className="seccion">{t('preinversionInicio.titulo')}</h2>
      <p className="nota">{t('preinversionInicio.intro')}</p>

      <div className="modulos">
        {subprocesos.map((sub, indice) => (
          <button
            key={sub.clave}
            type="button"
            className="modulo"
            onClick={() => navigate(destinoDe(sub, hasRole))}
          >
            <IconoMascara nombre="menu-preinversion" tam={40} style={{ color: 'var(--preinv-txt)' }} />
            <h3 style={{ color: 'var(--preinv-txt)' }}>
              <span className="mono">1.{indice + 1}</span> {t(sub.texto)}
            </h3>
            <p>{t(DESCRIPCION[sub.clave] ?? 'preinversionInicio.generico')}</p>
            <div className="flecha" aria-hidden="true">
              →
            </div>
          </button>
        ))}
      </div>

      {subprocesos.length === 0 && <p className="nota">{t('preinversionInicio.sinAcceso')}</p>}
    </>
  );
}
