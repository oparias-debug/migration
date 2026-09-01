import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { IconoColor } from '../components/Icono';

/**
 * Pantalla de inicio, según el diseño aprobado (siip-INICIO-NUEVA-GRIS):
 * banda de bienvenida, tarjetas de módulo y resumen de proyectos.
 *
 * Las tarjetas llevan a los módulos ya enrutados. El resumen todavía no tiene
 * endpoint que lo alimente — listarProyectos sólo filtra por estado y no
 * devuelve totales — así que de momento sólo enlaza al listado con el filtro
 * puesto, sin cifras inventadas.
 */
const MODULOS_TARJETA = [
  { clave: 'preinversion', icono: 'mod-preinversion', texto: 'menu.preinversion', desc: 'mod.preinversion.desc', color: 'var(--preinv-txt)', ruta: '/preinversion/proyectos' },
  { clave: 'programacion', icono: 'mod-programacion', texto: 'menu.programacion', desc: 'mod.programacion.desc', color: 'var(--progra)', ruta: '/programacion' },
  { clave: 'ejecucion', icono: 'mod-ejecucion', texto: 'menu.ejecucion', desc: 'mod.ejecucion.desc', color: 'var(--ejec-txt)', ruta: '/procesos' },
  { clave: 'seguimiento', icono: 'mod-seguimiento', texto: 'menu.seguimiento', desc: 'mod.seguimiento.desc', color: 'var(--segui)', ruta: '/seguimiento' },
] as const;

const ACCESOS = [
  { icono: 'tile-formulacion', texto: 'ind.formulacion', color: 'var(--navy)', estado: 'EN_REGISTRO' },
  { icono: 'tile-ejecucion', texto: 'ind.ejecucion', color: 'var(--preinv-txt)', estado: 'ENVIADO_DGICP_REGISTRO' },
  { icono: 'tile-seguimiento', texto: 'ind.seguimiento', color: 'var(--ejec-txt)', estado: 'OBSERVADO_DGICP_REGISTRO' },
  { icono: 'tile-completados', texto: 'ind.completados', color: 'var(--progra)', estado: 'CUP_ASIGNADO' },
] as const;

export function HomePage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const base = import.meta.env.BASE_URL;

  return (
    <>
      <section className="hero">
        <div>
          <div className="bienvenido">{t('inicio.bienvenido')}</div>
          <h1>SIIP</h1>
          <div className="lema">{t('app.nombre')}</div>
          <p>{t('inicio.lema')}</p>
            </div>
        <div className="marca">
          <img src={`${base}min-logo-blanco.png`} alt="Gobierno de El Salvador · Ministerio de Hacienda" />
          </div>
      </section>

      <h2 className="seccion">{t('inicio.modulos')}</h2>
      <div className="modulos">
        {MODULOS_TARJETA.map((m) => (
          <button key={m.clave} type="button" className="modulo" onClick={() => navigate(m.ruta)}>
            <IconoColor nombre={m.icono} />
            <h3 style={{ color: m.color }}>{t(m.texto)}</h3>
            <p>{t(m.desc)}</p>
            <div className="flecha" style={{ color: m.color }} aria-hidden="true">
              →
        </div>
          </button>
        ))}
      </div>

      <h2 className="seccion">{t('inicio.resumen')}</h2>
      <p className="nota">{t('inicio.resumenNota')}</p>
      <div className="indicadores">
        {ACCESOS.map((a) => (
          <button
            key={a.texto}
            type="button"
            className="indicador"
            onClick={() => navigate(`/preinversion/proyectos?estado=${a.estado}`)}
          >
            <IconoColor nombre={a.icono} />
            <span>
              <span className="etiqueta">{t(a.texto)}</span>
            </span>
          </button>
        ))}
      </div>
    </>
  );
}
