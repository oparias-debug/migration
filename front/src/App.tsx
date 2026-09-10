import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import { RequireAuth } from './auth/RequireAuth';
import { AppLayout } from './layout/AppLayout';
import { LoginPage } from './pages/LoginPage';
import { HomePage } from './pages/HomePage';
import { PlaceholderPage } from './pages/PlaceholderPage';
import { NotFoundPage } from './pages/NotFoundPage';
import { ProyectosPage } from './features/preinversion/proyectos/ProyectosPage';
import { BandejaPage } from './features/preinversion/bandeja/BandejaPage';
import { CapturaPage } from './features/preinversion/captura/CapturaPage';
import { ProyectoFormPage } from './features/preinversion/proyectos/ProyectoFormPage';
import { BandejaPreinversionPage } from './features/preinversion/bandeja/BandejaPreinversionPage';
import { OpinionTecnicaEntradaPage } from './features/preinversion/bandeja/OpinionTecnicaEntradaPage';
import { RutaPreinversionPage } from './features/preinversion/etapas/RutaPreinversionPage';
import { EtapasPage } from './features/preinversion/etapas/EtapasPage';
import { FichaInformacionGeneralPage } from './features/preinversion/etapas/FichaInformacionGeneralPage';
import { FichaEmergenciaPage } from './features/preinversion/etapas/FichaEmergenciaPage';
import { IdentificacionPage } from './features/preinversion/identificacion/IdentificacionPage';
import { AlternativasSolucionPage } from './features/preinversion/alternativas-solucion/AlternativasSolucionPage';

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

            <Route path="/preinversion/bandeja" element={<BandejaPage />} />
            <Route path="/preinversion/captura" element={<CapturaPage />} />
            <Route path="/preinversion/proyectos" element={<ProyectosPage />} />
            <Route path="/preinversion/bandeja" element={<BandejaPreinversionPage key="activas" />} />
            <Route path="/preinversion/bandeja/archivadas" element={<BandejaPreinversionPage key="archivadas" archivadas />} />
            <Route path="/preinversion/opinion-tecnica/:id" element={<OpinionTecnicaEntradaPage />} />
            <Route path="/preinversion/proyectos/nuevo" element={<ProyectoFormPage />} />
            <Route path="/preinversion/proyectos/:id" element={<ProyectoFormPage />} />

            {/* CU-PRE-3.5 "Selección y Registro de Etapas": pantallas de detalle de un proyecto
                puntual, sin entrada propia en el sidebar (mismo criterio que CU-PRE-01.5). */}
            <Route path="/preinversion/proyectos/:id/ruta-preinversion" element={<RutaPreinversionPage />} />
            <Route path="/preinversion/proyectos/:id/etapas" element={<EtapasPage />} />
            <Route path="/preinversion/proyectos/:id/ficha-informacion-general" element={<FichaInformacionGeneralPage />} />
            <Route path="/preinversion/proyectos/:id/ficha-emergencia" element={<FichaEmergenciaPage />} />

            {/* CU-PRE-05 "Alternativas de Solución": sin entrada propia en el sidebar, mismo
                criterio que CU-PRE-3.5 — la pestaña "Identificación del proyecto" (CU-PRE-04) que
                enlazaría aquí todavía no existe en este frontend (ver AlternativasSolucionPage). */}
            <Route path="/preinversion/proyectos/:id/identificacion" element={<IdentificacionPage />} />
            <Route path="/preinversion/proyectos/:id/alternativas-solucion" element={<AlternativasSolucionPage />} />

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
