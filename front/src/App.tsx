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
import { PasosProyectoLayout } from './features/preinversion/pasos/PasosProyectoLayout';
import { PresupuestoPage } from './features/preinversion/presupuesto/PresupuestoPage';
import { CrearCatalogoPage } from './features/administracion/catalogos/CrearCatalogoPage';
import { AlternativasSolucionPage } from './features/preinversion/alternativas-solucion/AlternativasSolucionPage';

// Módulos del sidebar aún no implementados en back — se muestran como
// "🚧 Página en Construcción". catalogosGenerales/tablasRangos estaban
// implementados contra el back viejo; se retiraron cuando back reemplazó ese
// layer de controllers/servicios por el nuevo modelo de dominio
// (administracion/convenios/ejecucion/oym/preinversion/programacion), que
// todavía no expone endpoints REST.
const PLACEHOLDER_PATHS = [
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

            <Route path="/catalogos-generales" element={<CrearCatalogoPage />} />
            <Route path="/preinversion/bandeja" element={<BandejaPage />} />
            <Route path="/preinversion/captura" element={<CapturaPage />} />
            <Route path="/preinversion/proyectos" element={<ProyectosPage />} />
            <Route path="/preinversion/bandeja" element={<BandejaPreinversionPage key="activas" />} />
            <Route path="/preinversion/bandeja/archivadas" element={<BandejaPreinversionPage key="archivadas" archivadas />} />
            <Route path="/preinversion/opinion-tecnica/:id" element={<OpinionTecnicaEntradaPage />} />
            <Route path="/preinversion/proyectos/nuevo" element={<ProyectoFormPage />} />
            <Route path="/preinversion/proyectos/:id" element={<ProyectoFormPage />} />
            {/* Pasos de un proyecto: comparten la barra de pasos (PasosProyectoLayout),
                que pinta cada pantalla en su <Outlet />. La ficha del proyecto y el alta
                quedan fuera: son anteriores al CUP, no pasos de la formulación. */}
            <Route element={<PasosProyectoLayout />}>
              <Route path="/preinversion/proyectos/:id/etapas" element={<EtapasPage />} />
              <Route path="/preinversion/proyectos/:id/ruta-preinversion" element={<RutaPreinversionPage />} />
              <Route path="/preinversion/proyectos/:id/ficha-informacion-general" element={<FichaInformacionGeneralPage />} />
              <Route path="/preinversion/proyectos/:id/ficha-emergencia" element={<FichaEmergenciaPage />} />
              <Route path="/preinversion/proyectos/:id/identificacion" element={<IdentificacionPage />} />
              <Route path="/preinversion/proyectos/:id/alternativas-solucion" element={<AlternativasSolucionPage />} />
              <Route path="/preinversion/proyectos/:id/presupuesto" element={<PresupuestoPage />} />
            </Route>

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
