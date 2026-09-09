import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { IconoColor, IconoMascara } from '../components/Icono';
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
  { estado: EstadoProyecto.ObservadoDgicpRegistro, tono: 'aviso', icono: 'pend-observadas', tit: 'pend.observadas', det: 'pend.observadasDet' },
  { estado: EstadoProyecto.EnRegistro, tono: 'info', icono: 'pend-elaboracion', tit: 'pend.elaboracion', det: 'pend.elaboracionDet' },
  { estado: EstadoProyecto.CupAsignado, tono: 'ok', icono: 'pend-concup', tit: 'pend.conCup', det: 'pend.conCupDet' },
] as const;

/**
 * Resumen del ciclo de vida, con las cuatro casillas del diseño del 09/09/2026.
 *
 * Tres salen de un estado real del contrato. La cuarta, "En seguimiento", NO
 * corresponde a ninguno de los 18 valores de EstadoProyecto, así que se pinta
 * sin cifra en vez de colgarla de un estado que no es el que dice la etiqueta.
 * Es la misma regla que se aplica en el resto de la pantalla: antes que un
 * número inventado, ninguno. Pendiente de que el cliente diga qué cuenta ahí.
 */
const RESUMEN = [
  { estado: EstadoProyecto.EnFormulacion, icono: 'tile-formulacion', texto: 'ind.formulacion', color: 'var(--navy)' },
  { estado: EstadoProyecto.EnEjecucion, icono: 'tile-ejecucion', texto: 'ind.ejecucion', color: 'var(--ejec-txt)' },
  { estado: null, icono: 'tile-seguimiento', texto: 'ind.seguimiento', color: 'var(--preinv-txt)' },
  { estado: EstadoProyecto.Finalizado, icono: 'tile-completados', texto: 'ind.finalizados', color: 'var(--progra)' },
] as const;

// flatMap en vez de filter: descarta la casilla sin estado sin necesidad de un
// predicado de tipo, que aquí choca con el `as const` de RESUMEN.
const TODOS = [
  ...PENDIENTES.map((p) => p.estado),
  ...RESUMEN.flatMap((r) => (r.estado ? [r.estado] : [])),
];

export function HomePage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const base = import.meta.env.BASE_URL;
  const { conteos, cargando } = useConteosProyecto(TODOS);

  const irAlListado = (estado: EstadoProyecto) => navigate(`/preinversion/proyectos?estado=${estado}`);
  const cifra = (estado: EstadoProyecto) => (cargando ? '·' : (conteos[estado] ?? 0));

  return (
    <>
      {/* Hero del diseño del 09/09/2026: una sola línea con divisor. Ya no lleva
          "BIENVENIDO A" ni el párrafo de plataforma. */}
      <section className="hero">
        <div className="hero-marca">
          <h1>SIIP</h1>
          <div className="lema">{t('app.nombre')}</div>
        </div>
        <div className="marca">
          <img src={`${base}min-logo-blanco.png`} alt="Gobierno de El Salvador · Ministerio de Hacienda" />
        </div>
      </section>

      {/* El diseño pone los módulos ANTES de los pendientes. */}
      <h2 className="seccion">{t('inicio.modulos')}</h2>
      <div className="modulos">
        {MODULOS_TARJETA.map((m) => (
          <button key={m.clave} type="button" className="modulo" onClick={() => navigate(m.ruta)}>
            <IconoColor nombre={m.icono} />
            <h3 style={{ color: m.color }}>{t(m.texto)}</h3>
            <p>{t(m.desc)}</p>
            <div className="flecha" aria-hidden="true">
              →
            </div>
          </button>
        ))}
      </div>

      <h2 className="seccion">{t('inicio.pendientes')}</h2>
      <div className="pendientes">
        {PENDIENTES.map((p) => (
          <button key={p.estado} type="button" className={`pendiente ${p.tono}`} onClick={() => irAlListado(p.estado)}>
            <span className="caja-icono" aria-hidden="true">
              <IconoMascara nombre={p.icono} tam={26} />
            </span>
            <span className="texto">
              <span className="cifra">{cifra(p.estado)}</span>
              <span className="tit">{t(p.tit)}</span>
              <span className="det">{t(p.det)}</span>
            </span>
          </button>
        ))}
      </div>

      {/* Resumen: en el diseño es UNA tarjeta con cuatro columnas separadas por
          divisores, no cuatro tarjetas sueltas. */}
      <h2 className="seccion">{t('inicio.resumen')}</h2>
      <div className="indicadores">
        {RESUMEN.map((r) => {
          const contenido = (
            <>
              <IconoColor nombre={r.icono} />
              <span>
                <span className="cifra" style={{ color: r.color }}>
                  {r.estado ? cifra(r.estado) : '—'}
                </span>
                <span className="etiqueta">{t(r.texto)}</span>
              </span>
            </>
          );
          // La casilla sin estado en el contrato no navega a ningún sitio: no
          // hay listado que filtrar por "En seguimiento".
          return r.estado ? (
            <button key={r.texto} type="button" className="indicador" onClick={() => irAlListado(r.estado)}>
              {contenido}
            </button>
          ) : (
            <div key={r.texto} className="indicador sin-estado" title={t('inicio.sinEstadoContrato')}>
              {contenido}
            </div>
          );
        })}
      </div>
    </>
  );
}
