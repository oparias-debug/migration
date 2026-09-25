import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import { RequireAuth } from './auth/RequireAuth';
import { AppLayout } from './layout/AppLayout';
import { LoginPage } from './pages/LoginPage';
import { HomePage } from './pages/HomePage';
import { PreinversionPage } from './pages/PreinversionPage';
import { BancoProyectosPage } from './features/preinversion/banco/BancoProyectosPage';
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
import { PresupuestoOmPage } from './features/preinversion/presupuesto-om/PresupuestoOmPage';
import { BeneficiosPage } from './features/preinversion/beneficios/BeneficiosPage';
import { CatalogosPage } from './features/administracion/catalogos/CatalogosPage';
import { CatalogoDetallePage } from './features/administracion/catalogos/CatalogoDetallePage';
import { CalendarioPage } from './features/administracion/calendario/CalendarioPage';
import { CalendarioDetallePage } from './features/administracion/calendario/CalendarioDetallePage';
import { AlternativasSolucionPage } from './features/preinversion/alternativas-solucion/AlternativasSolucionPage';
import { DiagnosticoPage } from './features/preinversion/diagnostico/DiagnosticoPage';
import { EstudioTecnicoPage } from './features/preinversion/estudio-tecnico/EstudioTecnicoPage';
import { AnalisisAmbientalPage } from './features/preinversion/analisis/AnalisisAmbientalPage';
import { AnalisisRiesgoPage } from './features/preinversion/analisis/AnalisisRiesgoPage';
import { AnalisisLegalPage } from './features/preinversion/analisis/AnalisisLegalPage';
import { PapPage } from './features/preinversion/pap/PapPage';
import { ProgramacionFinancieraPage } from './features/preinversion/pap/ProgramacionFinancieraPage';
import { ProgramacionFinancieraEstudioPage } from './features/preinversion/pap/ProgramacionFinancieraEstudioPage';
import { ProgramacionMetasPage } from './features/preinversion/pap/ProgramacionMetasPage';
import { ProgramacionMetasEstudioPage } from './features/preinversion/pap/ProgramacionMetasEstudioPage';
import { AvanceFinancieroPage } from './features/preinversion/pap/AvanceFinancieroPage';
import { AvanceFinancieroEstudioPage } from './features/preinversion/pap/AvanceFinancieroEstudioPage';
import { AvanceMetasPage } from './features/preinversion/pap/AvanceMetasPage';
import { AvanceMetasEstudioPage } from './features/preinversion/pap/AvanceMetasEstudioPage';

// Opciones del menú lateral (layout/navegacion.ts) que todavía no tienen
// pantalla: se muestran como "🚧 Página en Construcción".
const PLACEHOLDER_PATHS = [
  // Opciones del menú del árbol del sistema que todavía no tienen pantalla.
  'programacion/priorizacion',
  'programacion/pripme',
  'programacion/paip',
  'ejecucion/actualizaciones',
  'seguimiento/proyectos',
  'seguimiento/pap',
  'seguimiento/paip',
  'convenios/gestion',
  'reportes',
  'administracion/seguridad',
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
            <Route path="/preinversion" element={<PreinversionPage />} />
            <Route path="/banco-proyectos" element={<BancoProyectosPage />} />
            {/* PAP: CU-PRE-30 a CU-PRE-33, cada uno con su listado y su ficha por estudio. */}
            <Route path="/programacion/pap" element={<PapPage />} />
            <Route path="/programacion/pap/programacion-financiera" element={<ProgramacionFinancieraPage />} />
            <Route path="/programacion/pap/programacion-financiera/:cup" element={<ProgramacionFinancieraEstudioPage />} />
            <Route path="/programacion/pap/programacion-metas" element={<ProgramacionMetasPage />} />
            <Route path="/programacion/pap/programacion-metas/:cup" element={<ProgramacionMetasEstudioPage />} />
            <Route path="/programacion/pap/avance-financiero" element={<AvanceFinancieroPage />} />
            <Route path="/programacion/pap/avance-financiero/:cup" element={<AvanceFinancieroEstudioPage />} />
            <Route path="/programacion/pap/avance-metas" element={<AvanceMetasPage />} />
            <Route path="/programacion/pap/avance-metas/:cup" element={<AvanceMetasEstudioPage />} />

            <Route path="/catalogos-generales" element={<CatalogosPage />} />
            <Route path="/catalogos-generales/:codigo" element={<CatalogoDetallePage />} />
            <Route path="/administracion/calendario" element={<CalendarioPage />} />
            <Route path="/administracion/calendario/:codigo" element={<CalendarioDetallePage />} />
            <Route path="/preinversion/bandeja" element={<BandejaPage />} />
            <Route path="/preinversion/captura" element={<CapturaPage />} />
            {/* Procesos 1.2 a 1.5 del árbol: eligen un proyecto con CUP y entran a sus
                pasos. `key` remonta Captura al cambiar de proceso, para no arrastrar
                filtros ni página de uno a otro. */}
            <Route path="/preinversion/creacion-ruta" element={<CapturaPage key="creacion-ruta" proceso="creacion-ruta" />} />
            <Route path="/preinversion/formulacion" element={<CapturaPage key="formulacion" proceso="formulacion" />} />
            <Route path="/preinversion/programacion-proyecto" element={<CapturaPage key="programacion" proceso="programacion" />} />
            <Route path="/preinversion/gestion-proyecto" element={<CapturaPage key="gestion" proceso="gestion" />} />
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
              <Route path="/preinversion/proyectos/:id/diagnostico" element={<DiagnosticoPage />} />
              <Route path="/preinversion/proyectos/:id/estudio-tecnico" element={<EstudioTecnicoPage />} />
              <Route path="/preinversion/proyectos/:id/analisis-ambiental" element={<AnalisisAmbientalPage />} />
              <Route path="/preinversion/proyectos/:id/analisis-riesgo" element={<AnalisisRiesgoPage />} />
              <Route path="/preinversion/proyectos/:id/analisis-legal" element={<AnalisisLegalPage />} />
              <Route path="/preinversion/proyectos/:id/presupuesto" element={<PresupuestoPage />} />
              <Route path="/preinversion/proyectos/:id/presupuesto-om" element={<PresupuestoOmPage />} />
              <Route path="/preinversion/proyectos/:id/beneficios" element={<BeneficiosPage />} />
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
