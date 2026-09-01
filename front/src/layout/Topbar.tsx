import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../auth/useAuth';
import { IconoColor } from '../components/Icono';

/**
 * Barra superior, según el diseño aprobado.
 *
 * Se conservan las dos funciones que tenía la barra anterior — título y cerrar
 * sesión — y se añaden los elementos del diseño. Los que aún no tienen soporte
 * en el API van visibles pero deshabilitados, para que nadie los dé por
 * implementados: buscador global, favoritos, notificaciones, configuración y el
 * idioma inglés (sólo existe el diccionario en español).
 */
export function Topbar({ titulo, alAbrirMenu }: { titulo: string; alAbrirMenu: () => void }) {
  const { t } = useTranslation();
  const { logout } = useAuth();
  const navigate = useNavigate();
  const base = import.meta.env.BASE_URL;

  const cerrarSesion = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="topbar">
      <button
        type="button"
        className="hamburguesa"
        onClick={alAbrirMenu}
        aria-label={t('topbar.abrirMenu')}
        aria-controls="menu-lateral"
      >
        ☰
      </button>

      <h1 className="titulo">{titulo}</h1>

      <div className="buscador">
        <label htmlFor="q-global" className="sr-only">
          {t('topbar.buscar')}
        </label>
        <input id="q-global" type="search" placeholder={t('topbar.buscar')} disabled title={t('topbar.pendiente')} />
        <img className="lupa" src={`${base}icons/ui-buscar.png`} alt="" width="17" height="17" />
      </div>

      <div className="topbar-acciones">
        <div className="idioma" role="group" aria-label="Idioma / Language">
          <button type="button" className="activo" aria-pressed="true">
            ES
          </button>
          <button type="button" disabled title={t('topbar.pendiente')}>
            EN
          </button>
        </div>

        <button type="button" className="icono-btn" disabled title={t('topbar.pendiente')} aria-label={t('topbar.favoritos')}>
          <IconoColor nombre="ui-favorito" style={{ width: 19, height: 19, objectFit: 'contain', display: 'block' }} />
        </button>

        <button type="button" className="icono-btn" disabled title={t('topbar.pendiente')} aria-label={t('topbar.notificaciones')}>
          <IconoColor nombre="ui-notif" style={{ width: 24, height: 'auto', marginTop: -1, display: 'block' }} />
        </button>

        <button type="button" className="icono-btn" disabled title={t('topbar.pendiente')} aria-label={t('topbar.configuracion')}>
          <IconoColor nombre="ui-config" style={{ width: 19, height: 19, objectFit: 'contain', display: 'block' }} />
        </button>

        <button type="button" className="topbar-salir" onClick={cerrarSesion}>
        {t('common.logout')}
      </button>
    </div>
    </header>
  );
}
