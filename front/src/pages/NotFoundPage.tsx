import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

export function NotFoundPage() {
  const { t } = useTranslation();

  return (
    <div className="pagina-aviso">
      <h1>404</h1>
      <p className="pagina-aviso-texto">{t('notFound.title')}</p>
      <Link className="btn primario" to="/">
        {t('placeholder.backHome')}
      </Link>
    </div>
  );
}
