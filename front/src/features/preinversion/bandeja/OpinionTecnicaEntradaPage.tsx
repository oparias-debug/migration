import { Link, useParams } from 'react-router-dom';
import { useAuth } from '../../../auth/useAuth';

/** Punto de integración FA-02. La revisión completa se define en CU-PRE-26. */
export function OpinionTecnicaEntradaPage() {
  const { id } = useParams();
  const { hasRole } = useAuth();
  if (!hasRole('TECNICO_PRE')) return <p role="alert">No tiene permiso para abrir este caso.</p>;
  return <section><h1>Opinión Técnica</h1><p>Proyecto {id}</p>
    <p>La revisión de Opinión Técnica todavía no está disponible.</p>
    <Link to="/preinversion/bandeja">Regresar a Bandeja Preinversión</Link></section>;
}
