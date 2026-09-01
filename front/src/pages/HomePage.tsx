import { useTranslation } from 'react-i18next';

// Equivalente a templates/index.html (la vista real de "/", con layout).
export function HomePage() {
  const { t } = useTranslation();

  return (
    <>
      <h1>{t('home.title')}</h1>
      <div className="row">
        <div className="col-md-12">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">{t('home.panelTitle')}</h5>
              <p className="card-text">{t('home.panelText')}</p>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
