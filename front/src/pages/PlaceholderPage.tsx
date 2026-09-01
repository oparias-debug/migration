import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

// Equivalente al fragmento "🚧 Página en Construcción" servido hoy por
// CustomErrorController para las rutas del sidebar aún no implementadas.
export function PlaceholderPage() {
  const { t } = useTranslation();

  return (
    <div className="text-center p-5">
      <h1 className="display-4 text-warning">{t('placeholder.title')}</h1>
      <p className="lead">{t('placeholder.text')}</p>
      <Link className="btn btn-primary mt-3" to="/">
        {t('placeholder.backHome')}
      </Link>
    </div>
  );
}
