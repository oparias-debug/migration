import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import { RequireAuth } from './auth/RequireAuth';
import { AppLayout } from './layout/AppLayout';
import { LoginPage } from './pages/LoginPage';
import { HomePage } from './pages/HomePage';
import { PlaceholderPage } from './pages/PlaceholderPage';
import { NotFoundPage } from './pages/NotFoundPage';
import { ProyectosPage } from './features/preinversion/proyectos/ProyectosPage';
import { ProyectoFormPage } from './features/preinversion/proyectos/ProyectoFormPage';

// Módulos del sidebar aún no implementados en back — se muestran como
// "🚧 Página en Construcción". catalogosGenerales/tablasRangos estaban
// implementados contra el back viejo; se retiraron cuando back reemplazó ese
// layer de controllers/servicios por el nuevo modelo de dominio
// (administracion/convenios/ejecucion/oym/preinversion/programacion), que
// todavía no expone endpoints REST.
const PLACEHOLDER_PATHS = [
  'catalogos-generales',
  'tablas-rangos',
  'usuarios',
  'programacion',
  'seguimiento',
  'ingreso',
  'pripme',
  'financiero',
  'geografico',
  'fisico',
  'procesos',
];

export function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route
            element={
              <RequireAuth>
                <AppLayout />
              </RequireAuth>
            }
          >
            <Route path="/" element={<HomePage />} />

            <Route path="/preinversion/proyectos" element={<ProyectosPage />} />
            <Route path="/preinversion/proyectos/nuevo" element={<ProyectoFormPage />} />
            <Route path="/preinversion/proyectos/:id" element={<ProyectoFormPage />} />

            {PLACEHOLDER_PATHS.map((path) => (
              <Route key={path} path={`/${path}`} element={<PlaceholderPage />} />
            ))}

            <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
