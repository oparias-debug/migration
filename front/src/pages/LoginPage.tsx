import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../auth/useAuth';
import mhLogo from '../assets/img/mh_nuevologo.png';
import '../styles/login.css';

// Equivalente a templates/login.html.
export function LoginPage() {
  const { t } = useTranslation();
  const { login } = useAuth();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(username, password);
      navigate('/');
    } catch {
      setError(t('login.invalidCredentials'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="login-page-body">
      <div className="login-container">
        <div className="w-50 login-info d-flex flex-column" style={{ height: '100vh' }}>
          <div className="flex-grow-1 d-flex flex-column justify-content-center align-items-center text-center px-4">
            <img src={mhLogo} alt="Logo" style={{ maxWidth: 400, maxHeight: 300 }} />
            <h2 className="mt-4">{t('login.appName')}</h2>
            <p>
              <b>{t('login.appDescription')}</b>
            </p>
            <p style={{ textAlign: 'center' }}>{t('login.notice')}</p>
          </div>
          <footer className="mt-auto">
            <small>{t('login.footer')}</small>
          </footer>
        </div>

        <div className="d-flex align-items-center" style={{ height: '100vh' }}>
          <div className="d-flex align-items-center justify-content-center" style={{ height: '100%', width: 20 }}>
            <div style={{ width: 2, height: '50%', backgroundColor: '#ccc' }} />
          </div>
        </div>

        <div className="w-50 login-form-container">
          <div className="login-box">
            <h4 className="mb-4 text-center">{t('login.title')}</h4>

            {error && (
              <div className="alert alert-danger" role="alert">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="mb-3 position-relative">
                <label htmlFor="username" className="form-label">
                  {t('login.username')}
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="username"
                  placeholder={t('login.username')}
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label htmlFor="password" className="form-label">
                  {t('login.password')}
                </label>
                <div className="position-relative d-flex align-items-center">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    className="form-control pe-5"
                    id="password"
                    placeholder={t('login.password')}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                  <i
                    className={`bi position-absolute end-0 me-3 ${showPassword ? 'bi-eye' : 'bi-eye-slash'}`}
                    style={{ cursor: 'pointer' }}
                    onClick={() => setShowPassword((prev) => !prev)}
                  />
                </div>
              </div>
              <button type="submit" className="btn btn-dark w-100" disabled={submitting}>
                {t('login.submit')}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
