import { useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { ubicarEnMenu } from '../layout/navegacion';

/**
 * Opciones del menú que todavía no se han desarrollado. Por pedido del cliente
 * muestra sólo el título de la opción y "En construcción", sin notas.
 */
export function PlaceholderPage() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const ubicacion = ubicarEnMenu(pathname);

  return (
    <div className="pagina-aviso">
      {ubicacion && <h2>{t(ubicacion.titulo)}</h2>}
      <p className="pagina-aviso-texto">{t('placeholder.title')}</p>
    </div>
  );
}
