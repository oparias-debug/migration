import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../auth/useAuth';
import logoMH from '../assets/img/logoMH.png';

// Equivalente a fragments/topbar.html.
export function Topbar() {
  const { t } = useTranslation();
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="d-flex justify-content-between align-items-center mb-3 bg-light p-3 rounded shadow-sm">
      <h2 className="mb-0">{t('topbar.title')}</h2>
      <img src={logoMH} className="img-rounded" alt="Logo" width="20%" />
      <button className="btn btn-outline-danger" onClick={handleLogout}>
        {t('common.logout')}
      </button>
    </div>
  );
}
