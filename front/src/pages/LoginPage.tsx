import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../auth/useAuth';
import minLogo from '../assets/img/min-logo-gob-el-salvador-negro.png';
import logoSiip from '../assets/img/logo-siip-negro.png';
import '../styles/login.css';

/**
 * Pantalla de inicio de sesión, según el diseño entregado por el cliente el
 * 30/08/2026 (`inicio-SESION-2-SIIP-08-26.jpg`): tarjeta blanca centrada sobre
 * la fotografía de San Salvador, con el logotipo del Ministerio y el del SIIP.
 *
 * Los logotipos son los archivos oficiales que envió el cliente, no recortes.
 *
 * Tres elementos del diseño quedan visibles pero inactivos porque hoy no tienen
 * respaldo en el backend; cada uno está marcado más abajo.
 */
export function LoginPage() {
  const { t, i18n } = useTranslation();
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
    <div className="login-fondo">
      {/* Selector de idioma. Sólo está cargado el español (ver i18n/i18n.ts):
          el botón EN queda deshabilitado hasta que exista la traducción. */}
      <div className="login-idioma">
        <button type="button" className="es-activo" aria-current="true" onClick={() => i18n.changeLanguage('es')}>
          ES
        </button>
        <span aria-hidden="true">|</span>
        <button type="button" disabled title={t('login.idiomaPendiente')}>
          EN
        </button>
        </div>

      <main className="login-tarjeta">
        <img className="login-marca" src={minLogo} alt="Gobierno de El Salvador · Ministerio de Hacienda" />
        <img className="login-siip" src={logoSiip} alt="SIIP · Sistema de Información de Inversión Pública" />

        <form onSubmit={handleSubmit} noValidate>
            {error && (
            <div className="alert alert-danger py-2" role="alert">
                {error}
              </div>
            )}

          <div className="login-campo">
            <label htmlFor="username" className="visually-hidden">
                  {t('login.username')}
                </label>
            <i className="bi bi-person-fill" aria-hidden="true" />
                <input
                  type="text"
                  id="username"
              autoComplete="username"
                  placeholder={t('login.username')}
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>

          <div className="login-campo">
            <label htmlFor="password" className="visually-hidden">
                  {t('login.password')}
                </label>
            <i className="bi bi-lock-fill" aria-hidden="true" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    id="password"
              autoComplete="current-password"
                    placeholder={t('login.password')}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
            <button
              type="button"
              className="login-ojo"
                    onClick={() => setShowPassword((prev) => !prev)}
              aria-label={t(showPassword ? 'login.ocultarPassword' : 'login.verPassword')}
              aria-pressed={showPassword}
            >
              <i className={showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'} aria-hidden="true" />
            </button>
                </div>

          {/* reCAPTCHA del diseño. No se integra todavía: requiere clave de sitio
              y verificación en el servidor, y no está en el contrato. Se deja el
              espacio maquetado para no cambiar la pantalla cuando exista. */}
          <div className="login-captcha" role="note" aria-label={t('login.captchaPendiente')}>
            <span className="login-captcha-caja" aria-hidden="true" />
            <span className="login-captcha-texto">{t('login.noSoyRobot')}</span>
            <span className="login-captcha-marca" aria-hidden="true">
              reCAPTCHA
            </span>
              </div>

          <button type="submit" className="login-enviar" disabled={submitting}>
            {submitting ? t('login.enviando') : t('login.submit')}
              </button>
            </form>

        {/* Recuperación de contraseña: necesita el flujo correspondiente en
            Keycloak. Sin endpoint todavía. */}
        <p className="login-aviso">{t('login.notice')}</p>

        <button type="button" className="login-olvido" disabled title={t('login.olvidoPendiente')}>
          {t('login.olvidoPassword')}
        </button>
      </main>
    </div>
  );
}
