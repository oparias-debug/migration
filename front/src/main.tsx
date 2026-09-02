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

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
