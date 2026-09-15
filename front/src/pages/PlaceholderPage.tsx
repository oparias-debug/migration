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
    <div className="text-center p-5">
      {ubicacion && <h2>{t(ubicacion.titulo)}</h2>}
      <p className="lead">{t('placeholder.title')}</p>
    </div>
  );
}
