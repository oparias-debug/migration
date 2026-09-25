import { useEffect, useState } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';
import { BandaRuta, type Tramo } from './BandaRuta';
import { ubicarEnMenu } from './navegacion';

/* Mismo punto de corte que la media query de base.css: por encima el menú está
   siempre a la vista; por debajo es un cajón. */
const ANCHO_MENU_FIJO = 1000;

/** Si el menú está fijo a la izquierda (pantalla ancha) o es un cajón. */
function usarMenuFijo() {
  const [fijo, setFijo] = useState(() => window.innerWidth > ANCHO_MENU_FIJO);
  useEffect(() => {
    const alCambiar = () => setFijo(window.innerWidth > ANCHO_MENU_FIJO);
    window.addEventListener('resize', alCambiar);
    return () => window.removeEventListener('resize', alCambiar);
  }, []);
  return fijo;
}

/** Armazón del diseño: menú lateral + barra superior + banda de ruta + contenido. */
export function AppLayout() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const { titulo, tramos, volverA } = describir(pathname, t);

  // Por debajo de 1000 px el menú es un cajón: se abre con la hamburguesa y se
  // cierra al navegar o al pulsar el velo.
  const [menuAbierto, setMenuAbierto] = useState(false);
  // "Contraer menú" del diseño: se recuerda entre pantallas, pero no entre
  // sesiones, porque es una preferencia de la vista y no un dato del usuario.
  const [menuContraido, setMenuContraido] = useState(false);
  const menuFijo = usarMenuFijo();

  // El botón ☰ hace lo que toca según el ancho: con el menú fijo lo contrae o lo
  // despliega (antes no hacía nada visible en pantalla ancha); con el menú en
  // cajón lo abre.
  const alPulsarMenu = () => (menuFijo ? setMenuContraido((v) => !v) : setMenuAbierto((v) => !v));
  useEffect(() => {
    setMenuAbierto(false);
    window.scrollTo(0, 0);
  }, [pathname]);

  return (
    <div className="app">
      <Sidebar
        abierto={menuAbierto}
        alNavegar={() => setMenuAbierto(false)}
        contraido={menuContraido}
        alContraer={() => setMenuContraido((v) => !v)}
      />
      {menuAbierto && (
        <button type="button" className="velo" aria-label={t('topbar.cerrarMenu')} onClick={() => setMenuAbierto(false)} />
      )}

      <div className="principal">
        <Topbar
          titulo={titulo}
          alPulsarMenu={alPulsarMenu}
          menuDesplegado={menuFijo ? !menuContraido : menuAbierto}
        />
        {/* El diseño no pinta banda de ruta en Inicio: sería "Inicio > Inicio". */}
        {pathname !== '/' && <BandaRuta tramos={tramos} />}
        <main className="contenido">
          {/* Vuelta al menú del macroproceso sin usar el "Atrás" del navegador
              (Rocío, 24/09/2026). La banda de ruta ya lleva al mismo sitio, pero
              pedía una acción visible, y esta sale sola en toda pantalla que
              cuelgue de un módulo con menú propio. */}
          {volverA && (
            <Link className="btn neutro volver-modulo" to={volverA.ruta}>
              ← {t('ruta.volverA', { modulo: t(volverA.texto) })}
            </Link>
          )}
          <Outlet />
        </main>
      </div>
    </div>
  );
}

/**
 * Título de la barra superior y tramos de la banda de ruta. Salen del mismo
 * resolvedor que marca el menú lateral (ubicarEnMenu), para que nunca discrepen.
 */
function describir(
  pathname: string,
  t: (clave: string) => string,
): { titulo: string; tramos: Tramo[]; volverA?: { ruta: string; texto: string } } {
  const ubicacion = ubicarEnMenu(pathname);
  if (!ubicacion) return { titulo: t('menu.inicio'), tramos: [{ texto: 'menu.inicio' }] };
  const { modulo } = ubicacion;
  // Sólo cuando el módulo tiene su propia pantalla de entrada y no estamos ya en ella.
  const volverA = modulo.ruta && modulo.ruta !== pathname ? { ruta: modulo.ruta, texto: modulo.texto } : undefined;
  return { titulo: t(ubicacion.titulo), tramos: [...ubicacion.tramos], volverA };
}
