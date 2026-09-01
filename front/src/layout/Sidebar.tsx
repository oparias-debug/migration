import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../auth/useAuth';
import { IconoMascara } from '../components/Icono';
import { MODULOS, type Modulo, type SubModulo } from './navegacion';

/**
 * Menú lateral, según el diseño aprobado (siip-INICIO-NUEVA-GRIS /
 * siip-asignacion-CUP-NUEVA-GRIS). Sustituye al sidebar de Bootstrap heredado
 * del front Java: se conservan TODAS sus rutas y su control de rol, cambia
 * únicamente el marcado y los estilos.
 *
 * Los cinco estados del ítem viven en base.css:
 *   reposo · hover (píldora #0073E3) · seleccionado (panel #D5E0F1)
 *   · foco de teclado · deshabilitado
 *
 * El azul vivo del mockup de Inicio es el HOVER, no el seleccionado; el estado
 * seleccionado con submenú desplegado es el panel claro de la pantalla de CUP.
 */
export function Sidebar({ abierto = false, alNavegar }: { abierto?: boolean; alNavegar?: () => void }) {
  const { t } = useTranslation();
  const { hasRole, username, roles } = useAuth();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const visible = (sub: SubModulo) => !sub.rolesRequeridos || sub.rolesRequeridos.some(hasRole);
  const submenuDe = (m: Modulo) => m.submenu?.filter(visible) ?? [];

  // Qué está seleccionado se deduce de la URL, no de un estado paralelo.
  const subActivo =
    MODULOS.flatMap(submenuDe).find(
      (s) => pathname === s.ruta || pathname.startsWith(`${s.ruta}/`),
    ) ?? null;
  const moduloConSub = subActivo
    ? (MODULOS.find((m) => submenuDe(m).some((s) => s.clave === subActivo.clave)) ?? null)
    : null;
  const claveActiva = moduloConSub?.clave ?? (pathname === '/' ? 'inicio' : null);

  // El despliegue sí es estado local: se puede plegar sin salir de la pantalla.
  const [desplegado, setDesplegado] = useState<string | null>(moduloConSub?.clave ?? null);
  useEffect(() => {
    if (moduloConSub) setDesplegado(moduloConSub.clave);
  }, [moduloConSub, pathname]);

  const ir = (ruta: string) => {
    navigate(ruta);
    alNavegar?.();
  };

  function pulsar(modulo: Modulo) {
    if (submenuDe(modulo).length > 0) {
      // Un clic abre, otro cierra, y cerrarlo no saca al usuario de su pantalla.
      setDesplegado((abierto) => (abierto === modulo.clave ? null : modulo.clave));
      return;
    }
    if (modulo.ruta) ir(modulo.ruta);
  }

  return (
    <aside className={`menu${abierto ? ' abierto' : ''}`} id="menu-lateral">
      <div className="menu-marca">
        <img src={`${import.meta.env.BASE_URL}escudo-solo-blanco.png`} alt="Gobierno de El Salvador" />
        <div className="ministerio">
          MINISTERIO
          <br />
          DE HACIENDA
        </div>
        <div className="sigla">SIIP</div>
        <div className="nombre">{t('app.nombre')}</div>
      </div>

      {/* Bloque de usuario del diseño, con los datos reales del token. */}
      <div className="menu-usuario">
        <div className="avatar" aria-hidden="true">{iniciales(username)}</div>
        <div>
          <div className="nom">{(username ?? '').toUpperCase()}</div>
          <div className="rol">{roles[0] ?? ''}</div>
        </div>
      </div>

      <nav className="menu-nav" aria-label={t('app.nombre')}>
        {MODULOS.map((modulo) => {
          const subs = submenuDe(modulo);
          const activo = claveActiva === modulo.clave;
          const abiertoAqui = subs.length > 0 && desplegado === modulo.clave;
          return (
            <div key={modulo.clave}>
              <button
                type="button"
                className={
                  `ni${activo ? ' activo' : ''}` +
                  `${activo && !abiertoAqui ? ' plegado' : ''}` +
                  `${activo && subs.length === 0 ? ' pildora' : ''}`
                }
                aria-expanded={subs.length > 0 ? abiertoAqui : undefined}
                aria-controls={subs.length > 0 ? `sub-${modulo.clave}` : undefined}
                aria-current={activo && subs.length === 0 ? 'page' : undefined}
                onClick={() => pulsar(modulo)}
              >
                <IconoMascara nombre={modulo.icono} />
                {t(modulo.texto)}
                {modulo.clave !== 'inicio' && (
                  <IconoMascara
                    nombre="ui-chevron"
                    tam={7}
                    className="chevron"
                    style={{
                      height: 11,
                      opacity: subs.length > 0 ? undefined : 0.6,
                      transform: abiertoAqui ? 'rotate(90deg)' : undefined,
                    }}
                  />
                )}
              </button>

              {subs.length > 0 && (
                <div
                  id={`sub-${modulo.clave}`}
                  className={`submenu${abiertoAqui ? '' : ' plegado'}`}
                  aria-hidden={!abiertoAqui}
                >
                  {subs.map((sub) => (
                    <button
                      key={sub.clave}
                      type="button"
                      tabIndex={abiertoAqui ? 0 : -1}
                      className={`sni${subActivo?.clave === sub.clave ? ' activo' : ''}`}
                      aria-current={subActivo?.clave === sub.clave ? 'page' : undefined}
                      onClick={() => ir(sub.ruta)}
                    >
                      {t(sub.texto)}
                    </button>
                  ))}
    </div>
              )}
            </div>
          );
        })}
      </nav>
    </aside>
  );
}

/** Iniciales para el avatar; dos letras como en el diseño. */
function iniciales(username: string | null): string {
  if (!username) return '··';
  const partes = username.split(/[.\s_-]+/).filter(Boolean);
  const letras = partes.length > 1 ? partes[0][0] + partes[1][0] : username.slice(0, 2);
  return letras.toUpperCase();
}
