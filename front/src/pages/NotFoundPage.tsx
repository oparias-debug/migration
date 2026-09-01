import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

export function NotFoundPage() {
  const { t } = useTranslation();

  return (
    <div className="text-center p-5">
      <h1>404</h1>
      <p className="lead">{t('notFound.title')}</p>
      <Link className="btn btn-primary mt-3" to="/">
        {t('placeholder.backHome')}
      </Link>
    </div>
  );
}
