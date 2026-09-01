import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { IconoColor } from '../components/Icono';
import { EstadoProyecto } from '../api/preinversionApi';
import { useConteosProyecto } from './useConteosProyecto';

/**
 * Pantalla de inicio, según el diseño aprobado en solodevs.net: banda de
 * bienvenida, "Mis pendientes", tarjetas de módulo y resumen de proyectos.
 *
 * Las cifras salen de `paginacion.totalElementos` de listarProyectos, una
 * consulta por estado. No hay ningún número de ejemplo en pantalla.
 */
const MODULOS_TARJETA = [
  { clave: 'preinversion', icono: 'mod-preinversion', texto: 'menu.preinversion', desc: 'mod.preinversion.desc', color: 'var(--preinv-txt)', ruta: '/preinversion/proyectos' },
  { clave: 'programacion', icono: 'mod-programacion', texto: 'menu.programacion', desc: 'mod.programacion.desc', color: 'var(--progra)', ruta: '/programacion' },
  { clave: 'ejecucion', icono: 'mod-ejecucion', texto: 'menu.ejecucion', desc: 'mod.ejecucion.desc', color: 'var(--ejec-txt)', ruta: '/procesos' },
  { clave: 'seguimiento', icono: 'mod-seguimiento', texto: 'menu.seguimiento', desc: 'mod.seguimiento.desc', color: 'var(--segui)', ruta: '/seguimiento' },
] as const;

// Los tres estados de CU-PRE-01 sobre los que el Técnico URP tiene que actuar.
const PENDIENTES = [
  { estado: EstadoProyecto.ObservadoDgicpRegistro, tono: 'aviso', tit: 'pend.observadas', det: 'pend.observadasDet' },
  { estado: EstadoProyecto.EnRegistro, tono: 'info', tit: 'pend.elaboracion', det: 'pend.elaboracionDet' },
  { estado: EstadoProyecto.CupAsignado, tono: 'ok', tit: 'pend.conCup', det: 'pend.conCupDet' },
] as const;

// Resumen del ciclo de vida. Se rotula cada casilla con el estado que de verdad
// cuenta: el diseño decía "En seguimiento", que no corresponde a ningún estado
// del contrato (ver nota para el cliente).
const RESUMEN = [
  { estado: EstadoProyecto.EnFormulacion, icono: 'tile-formulacion', texto: 'ind.formulacion', color: 'var(--navy)' },
  { estado: EstadoProyecto.EnViabilidad, icono: 'tile-ejecucion', texto: 'ind.viabilidad', color: 'var(--preinv-txt)' },
  { estado: EstadoProyecto.EnEjecucion, icono: 'tile-seguimiento', texto: 'ind.ejecucion', color: 'var(--ejec-txt)' },
  { estado: EstadoProyecto.Finalizado, icono: 'tile-completados', texto: 'ind.finalizados', color: 'var(--progra)' },
] as const;

const TODOS = [...PENDIENTES.map((p) => p.estado), ...RESUMEN.map((r) => r.estado)];

export function HomePage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const base = import.meta.env.BASE_URL;
  const { conteos, cargando } = useConteosProyecto(TODOS);

  const irAlListado = (estado: EstadoProyecto) => navigate(`/preinversion/proyectos?estado=${estado}`);
  const cifra = (estado: EstadoProyecto) => (cargando ? '·' : (conteos[estado] ?? 0));

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

      <h2 className="seccion">{t('inicio.pendientes')}</h2>
      <p className="nota">{t('inicio.pendientesNota')}</p>
      <div className="pendientes">
        {PENDIENTES.map((p) => (
          <button key={p.estado} type="button" className={`pendiente ${p.tono}`} onClick={() => irAlListado(p.estado)}>
            <div className="cifra">{cifra(p.estado)}</div>
            <div className="tit">{t(p.tit)}</div>
            <div className="det">{t(p.det)}</div>
          </button>
        ))}
      </div>

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
        {RESUMEN.map((r) => (
          <button key={r.estado} type="button" className="indicador" onClick={() => irAlListado(r.estado)}>
            <IconoColor nombre={r.icono} />
            <span>
              <span className="cifra" style={{ color: r.color, display: 'block' }}>
                {cifra(r.estado)}
              </span>
              <span className="etiqueta">{t(r.texto)}</span>
            </span>
          </button>
        ))}
      </div>
    </>
  );
}
