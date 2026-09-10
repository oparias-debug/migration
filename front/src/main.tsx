import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
// Bootstrap se conserva sólo como base de la retícula heredada; ninguna
// pantalla usa ya sus clases. Su JS se retiró: lo pedía el sidebar antiguo.
import 'bootstrap/dist/css/bootstrap.min.css';
import 'flatpickr/dist/flatpickr.min.css';
import './styles/estilos.css';
// Sistema de diseño: después de Bootstrap, para que sus valores prevalezcan.
import './styles/tokens.css';
import './styles/base.css';
import './i18n/i18n';
import { App } from './App';

/**
 * Se canjea el refresh token ANTES de montar React.
 *
 * Si se hiciera dentro de un componente habría una primera pasada sin sesión, y
 * el guardia de rutas mandaría al login antes de que llegara el token nuevo: el
 * usuario vería el login por un instante, o se quedaría en él. Al hacerlo aquí,
 * la aplicación arranca ya con la sesión renovada.
 *
 * Si el refresco falla —refresh vencido o revocado— se limpia el estado y la
 * aplicación arranca sin sesión, que es lo correcto.
 */
async function arrancar() {
  const { getAuthState, setAuthState, clearAuthState, stateFromTokens } = await import('./auth/tokenStore');
  const { accessToken, refreshToken } = getAuthState();

  if (!accessToken && refreshToken) {
    try {
      const { authApi } = await import('./api/authApi');
      const tokens = await authApi.refresh(refreshToken);
      setAuthState(stateFromTokens(tokens.access_token, tokens.refresh_token));
    } catch {
      clearAuthState();
    }
  }

  createRoot(document.getElementById('root')!).render(
    <StrictMode>
      <App />
    </StrictMode>,
  );
}

void arrancar();
