import { useEffect, useRef, useState } from 'react';
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
  const { logout, username, roles } = useAuth();
  const navigate = useNavigate();
  const base = import.meta.env.BASE_URL;

  const [abierto, setAbierto] = useState(false);
  const caja = useRef<HTMLDivElement>(null);

  // Un clic fuera cierra el desplegable, como espera cualquier menú de usuario.
  useEffect(() => {
    if (!abierto) return;
    const fuera = (e: MouseEvent) => {
      if (caja.current && !caja.current.contains(e.target as Node)) setAbierto(false);
    };
    document.addEventListener('mousedown', fuera);
    return () => document.removeEventListener('mousedown', fuera);
  }, [abierto]);

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
        <fieldset className="idioma" aria-label="Idioma / Language">
          <button type="button" className="activo" aria-pressed="true">
            ES
          </button>
          <button type="button" disabled title={t('topbar.pendiente')}>
            EN
          </button>
        </fieldset>

        <button type="button" className="icono-btn" disabled title={t('topbar.pendiente')} aria-label={t('topbar.favoritos')}>
          <IconoColor nombre="ui-favorito" style={{ width: 19, height: 19, objectFit: 'contain', display: 'block' }} />
        </button>

        <button type="button" className="icono-btn" disabled title={t('topbar.pendiente')} aria-label={t('topbar.notificaciones')}>
          <IconoColor nombre="ui-notif" style={{ width: 24, height: 'auto', marginTop: -1, display: 'block' }} />
        </button>

        {/* "Mi Cuenta" del diseño del 09/09/2026: el avatar lleva las iniciales
            del usuario real del token. En el diseño esta píldora es un selector
            de rol de demostración; aquí el rol lo fija el token y no se elige,
            así que el nombre y el rol se muestran dentro del desplegable. */}
        <div className="usuario-menu" ref={caja}>
          <button
            type="button"
            className="mi-cuenta"
            aria-haspopup="menu"
            aria-expanded={abierto}
            onClick={() => setAbierto((v) => !v)}
          >
            <span className="avatar" aria-hidden="true">{iniciales(username)}</span>
            {t('topbar.miCuenta')}
            <span className="flecha" aria-hidden="true">▾</span>
          </button>
          {abierto && (
            <div className="usuario-desplegable" role="menu">
              <div className="quien">
                <b>{username ?? ''}</b>
                {roles[0] ? <span>{roles[0]}</span> : null}
              </div>
              <button type="button" role="menuitem" onClick={cerrarSesion}>
                {t('common.logout')}
              </button>
            </div>
          )}
        </div>

        <button type="button" className="icono-btn" disabled title={t('topbar.pendiente')} aria-label={t('topbar.configuracion')}>
          <IconoColor nombre="ui-config" style={{ width: 19, height: 19, objectFit: 'contain', display: 'block' }} />
        </button>
    </div>
    </header>
  );
}

/** Iniciales para el avatar de la barra superior; dos letras, como en el menú. */
function iniciales(username: string | null): string {
  if (!username) return '··';
  const partes = username.split(/[.\s_-]+/).filter(Boolean);
  const letras = partes.length > 1 ? partes[0][0] + partes[1][0] : username.slice(0, 2);
  return letras.toUpperCase();
}
