import { useEffect, useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';
import { BandaRuta, type Tramo } from './BandaRuta';
import { MODULOS } from './navegacion';

/** Armazón del diseño: menú lateral + barra superior + banda de ruta + contenido. */
export function AppLayout() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const { titulo, tramos } = describir(pathname, t);

  // Por debajo de 1000 px el menú es un cajón: se abre con la hamburguesa y se
  // cierra al navegar o al pulsar el velo.
  const [menuAbierto, setMenuAbierto] = useState(false);
  useEffect(() => {
    setMenuAbierto(false);
    window.scrollTo(0, 0);
  }, [pathname]);

  return (
    <div className="app">
      <Sidebar abierto={menuAbierto} alNavegar={() => setMenuAbierto(false)} />
      {menuAbierto && (
        <button className="velo" aria-label={t('topbar.cerrarMenu')} onClick={() => setMenuAbierto(false)} />
      )}

      <div className="principal">
        <Topbar titulo={titulo} alAbrirMenu={() => setMenuAbierto(true)} />
        <BandaRuta tramos={tramos} />
        <main className="contenido">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

/**
 * Título de la barra superior y tramos de la banda de ruta, deducidos de la URL
 * y del mismo árbol que pinta el menú, para que nunca discrepen.
 */
function describir(pathname: string, t: (clave: string) => string): { titulo: string; tramos: Tramo[] } {
  if (pathname === '/') {
    return { titulo: t('menu.inicio'), tramos: [{ texto: 'menu.inicio' }] };
  }

  for (const modulo of MODULOS) {
    for (const sub of modulo.submenu ?? []) {
      if (pathname !== sub.ruta && !pathname.startsWith(`${sub.ruta}/`)) continue;

      const tramos: Tramo[] = [{ texto: modulo.texto }, { texto: sub.texto, ruta: sub.ruta }];
      if (pathname === sub.ruta) {
        return { titulo: t(sub.texto), tramos: [{ texto: modulo.texto }, { texto: sub.texto }] };
      }
      // Pantalla de detalle bajo el listado: alta o edición de un registro.
      const hoja = pathname.endsWith('/nuevo') ? t('ruta.nuevo') : t('ruta.detalle');
      return { titulo: t(sub.texto), tramos: [...tramos, { texto: hoja, literal: true }] };
    }
  }

  return { titulo: t('menu.inicio'), tramos: [{ texto: 'menu.inicio' }] };
}
