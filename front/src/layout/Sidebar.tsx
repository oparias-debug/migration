import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../auth/useAuth';
import { ROLES_CON_ACCESO_REGISTRO_PROYECTO } from '../features/preinversion/proyectos/proyectoLabels';

// Equivalente a fragments/sidebar.html. Los data-bs-toggle="collapse" siguen
// funcionando porque bootstrap/dist/js/bootstrap.bundle.min.js se importa
// como side-effect global en main.tsx.
export function Sidebar() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const puedeVerRegistroProyecto = ROLES_CON_ACCESO_REGISTRO_PROYECTO.some(hasRole);

  return (
    <div className="bg-dark text-white p-3 vh-100">
      <h4>{t('sidebar.title')}</h4>
      <ul className="nav flex-column">
        <li className="nav-item">
          <a className="nav-link" role="button" data-bs-toggle="collapse" data-bs-target="#admin">
            {t('sidebar.administracion')}
          </a>
          <ul className="collapse nav flex-column ms-3" id="admin">
            <li className="nav-item">
              <a className="nav-link" role="button" data-bs-toggle="collapse" data-bs-target="#catalogos">
                {t('sidebar.catalogos')}
              </a>
              <ul className="collapse nav flex-column ms-3" id="catalogos">
                <li>
                  <Link className="nav-link" to="/catalogos-generales">
                    {t('sidebar.generales')}
                  </Link>
                </li>
                <li>
                  <Link className="nav-link" to="/tablas-rangos">
                    {t('sidebar.tablasDeRangos')}
                  </Link>
                </li>
              </ul>
            </li>
            <li>
              <Link className="nav-link" to="/usuarios">
                {t('sidebar.usuarios')}
              </Link>
            </li>
          </ul>
        </li>

        <li className="nav-item">
          <a className="nav-link" role="button" data-bs-toggle="collapse" data-bs-target="#preinversion">
            {t('sidebar.preinversion')}
          </a>
          <ul className="collapse nav flex-column ms-3" id="preinversion">
            {puedeVerRegistroProyecto && (
              <li>
                <Link className="nav-link" to="/preinversion/proyectos">
                  {t('sidebar.registroProyecto')}
                </Link>
              </li>
            )}
            <li>
              <Link className="nav-link" to="/programacion">
                {t('sidebar.programacion')}
              </Link>
            </li>
            <li>
              <Link className="nav-link" to="/seguimiento">
                {t('sidebar.seguimiento')}
              </Link>
            </li>
          </ul>
        </li>

        <li className="nav-item">
          <a className="nav-link" role="button" data-bs-toggle="collapse" data-bs-target="#programacionGroup">
            {t('sidebar.programacionGroup')}
          </a>
          <ul className="collapse nav flex-column ms-3" id="programacionGroup">
            <li>
              <Link className="nav-link" to="/ingreso">
                {t('sidebar.ingreso')}
              </Link>
            </li>
            <li>
              <Link className="nav-link" to="/pripme">
                {t('sidebar.pripme')}
              </Link>
            </li>
          </ul>
        </li>

        <li className="nav-item">
          <a className="nav-link" role="button" data-bs-toggle="collapse" data-bs-target="#seguimientoGroup">
            {t('sidebar.seguimientoGroup')}
          </a>
          <ul className="collapse nav flex-column ms-3" id="seguimientoGroup">
            <li>
              <Link className="nav-link" to="/financiero">
                {t('sidebar.financiero')}
              </Link>
            </li>
            <li>
              <Link className="nav-link" to="/geografico">
                {t('sidebar.geografico')}
              </Link>
            </li>
            <li>
              <Link className="nav-link" to="/fisico">
                {t('sidebar.fisico')}
              </Link>
            </li>
            <li>
              <Link className="nav-link" to="/procesos">
                {t('sidebar.procesos')}
              </Link>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  );
}
