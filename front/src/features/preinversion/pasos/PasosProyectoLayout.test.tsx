import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { PasosProyectoLayout } from './PasosProyectoLayout';
import { GRUPOS_PASOS, entradaDeGrupo, pasosDe, ubicarPaso } from './pasosProyecto';

const obtenerProyecto = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return { ...actual, preinversionApi: { obtenerProyecto: (...a: unknown[]) => obtenerProyecto(...a) } };
});

const montar = (ruta: string) =>
  render(
    <MemoryRouter initialEntries={[ruta]}>
      <Routes>
        <Route element={<PasosProyectoLayout />}>
          <Route path="/preinversion/proyectos/:id/identificacion" element={<p>pantalla identificación</p>} />
          <Route path="/preinversion/proyectos/:id/ruta-preinversion" element={<p>pantalla ruta</p>} />
          <Route path="/preinversion/proyectos/:id/presupuesto" element={<p>pantalla presupuesto</p>} />
          <Route path="/preinversion/proyectos/:id/alternativas-solucion" element={<p>pantalla alternativas</p>} />
        </Route>
      </Routes>
    </MemoryRouter>,
  );

const IDENTIFICACION = '/preinversion/proyectos/7/identificacion';

describe('PasosProyectoLayout · árbol del sistema', () => {
  beforeEach(() => {
    obtenerProyecto.mockReset();
    obtenerProyecto.mockResolvedValue({ data: { nombre: 'Hospital de Santa Ana', cup: '10001' } });
  });

  it('pinta la pantalla del paso y marca el paso actual', async () => {
    montar(IDENTIFICACION);
    expect(screen.getByText('pantalla identificación')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Identificación' })).toHaveAttribute('aria-current', 'step');
    await screen.findByText('Hospital de Santa Ana');
  });

  it('muestra los procesos de Preinversión con su código, sin Priorización', async () => {
    montar(IDENTIFICACION);
    for (const nombre of [
      '1.2 Creación ruta de preinversión',
      '1.3 Formulación y evaluación',
      '1.4 Programación del proyecto',
      '1.5 Gestión del proyecto',
    ]) {
      expect(screen.getByRole('button', { name: nombre })).toBeInTheDocument();
    }
    expect(screen.queryByText(/Priorización/)).not.toBeInTheDocument();
    await screen.findByText('Hospital de Santa Ana');
  });

  it('agrupa los capítulos de Formulación por subproceso', async () => {
    montar(IDENTIFICACION);
    expect(screen.getByText('1.3.1 Registrar identificación')).toBeInTheDocument();
    expect(screen.getByText('1.3.2 Registrar formulación')).toBeInTheDocument();
    expect(screen.getByText('1.3.3 Evaluación ex ante')).toBeInTheDocument();
    await screen.findByText('Hospital de Santa Ana');
  });

  it('los pasos con pantalla enlazan a ese mismo proyecto', async () => {
    montar(IDENTIFICACION);
    expect(screen.getByRole('link', { name: 'Alternativas de solución' })).toHaveAttribute(
      'href',
      '/preinversion/proyectos/7/alternativas-solucion',
    );
    await screen.findByText('Hospital de Santa Ana');
  });

  it('navega al hacer clic en otro paso', async () => {
    montar(IDENTIFICACION);
    fireEvent.click(screen.getByRole('link', { name: 'Alternativas de solución' }));
    expect(await screen.findByText('pantalla alternativas')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Alternativas de solución' })).toHaveAttribute('aria-current', 'step');
  });

  it('un capítulo sin pantalla se ve en su sitio pero no enlaza', async () => {
    montar(IDENTIFICACION);
    expect(screen.getByText('Diagnóstico de la situación actual')).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: /Diagnóstico/ })).not.toBeInTheDocument();
    await screen.findByText('Hospital de Santa Ana');
  });

  it('un capítulo con pestañas las anuncia', async () => {
    montar(IDENTIFICACION);
    expect(screen.getByText('Diagnóstico de la situación actual')).toHaveAttribute(
      'title',
      expect.stringContaining('Gestión de interesados'),
    );
    await screen.findByText('Hospital de Santa Ana');
  });

  it('Ruta de Preinversión pertenece a Selección de la etapa (1.2)', async () => {
    montar('/preinversion/proyectos/7/ruta-preinversion');
    expect(screen.getByRole('link', { name: 'Selección de la etapa de preinversión' })).toHaveAttribute(
      'aria-current',
      'step',
    );
    expect(screen.getByRole('button', { name: '1.2 Creación ruta de preinversión' })).toHaveAttribute(
      'aria-pressed',
      'true',
    );
    await screen.findByText('Hospital de Santa Ana');
  });

  it('muestra el código del árbol en el paso', async () => {
    montar('/preinversion/proyectos/7/presupuesto');
    const enlace = screen.getByRole('link', { name: 'Presupuesto de inversión' });
    expect(enlace).toHaveAttribute('aria-current', 'step');
    expect(enlace).toHaveTextContent('1.3.2.6');
    await screen.findByText('Hospital de Santa Ana');
  });

  it('cambiar de proceso muestra sus capítulos sin salir de la pantalla', async () => {
    montar(IDENTIFICACION);
    fireEvent.click(screen.getByRole('button', { name: '1.5 Gestión del proyecto' }));
    expect(screen.getByText('Viabilidad')).toBeInTheDocument();
    expect(screen.getByText('pantalla identificación')).toBeInTheDocument();
    await screen.findByText('Hospital de Santa Ana');
  });

  it('?grupo= abre la barra en ese proceso', async () => {
    montar('/preinversion/proyectos/7/ruta-preinversion?grupo=programacion');
    expect(screen.getByRole('button', { name: '1.4 Programación del proyecto' })).toHaveAttribute(
      'aria-pressed',
      'true',
    );
    expect(screen.getByText('Indicadores del proyecto')).toBeInTheDocument();
    await screen.findByText('Hospital de Santa Ana');
  });

  it('muestra el nombre y el CUP del proyecto', async () => {
    montar(IDENTIFICACION);
    expect(await screen.findByText('Hospital de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText(/CUP 10001/)).toBeInTheDocument();
  });

  it('si el proyecto no carga, la barra funciona igual', async () => {
    obtenerProyecto.mockRejectedValue(new Error('403'));
    montar(IDENTIFICACION);
    await waitFor(() => expect(obtenerProyecto).toHaveBeenCalled());
    expect(screen.getByRole('link', { name: 'Identificación' })).toBeInTheDocument();
    expect(screen.getByText('pantalla identificación')).toBeInTheDocument();
  });
});

describe('pasosProyecto', () => {
  it('reconoce las pantallas que forman un mismo capítulo', () => {
    expect(ubicarPaso('/preinversion/proyectos/7/ficha-emergencia')).toMatchObject({
      idProyecto: 7,
      grupo: { clave: 'creacion-ruta' },
      paso: { clave: 'seleccion-etapa' },
    });
  });

  it('no trata como paso la ficha del proyecto ni el alta', () => {
    expect(ubicarPaso('/preinversion/proyectos/7')).toBeNull();
    expect(ubicarPaso('/preinversion/proyectos/nuevo')).toBeNull();
  });

  // Rocío: "Registrar formulación abarca 9 capítulos… te ubicas por la codificación".
  it('Formulación tiene los 9 capítulos de identificación y formulación, más la evaluación ex ante', () => {
    const formulacion = GRUPOS_PASOS.find((g) => g.clave === 'formulacion');
    expect(formulacion).toBeDefined();
    const [identificacion, registrar] = formulacion!.secciones;
    expect(identificacion.pasos.length + registrar.pasos.length).toBe(9);
    expect(pasosDe(formulacion!)).toHaveLength(12);
  });

  it('la entrada de cada proceso lleva a su primer capítulo con pantalla', () => {
    expect(entradaDeGrupo(7, 'creacion-ruta')).toBe('/preinversion/proyectos/7/etapas');
    expect(entradaDeGrupo(7, 'formulacion')).toBe('/preinversion/proyectos/7/identificacion');
  });

  it('un proceso sin pantallas abre la barra en ese proceso', () => {
    expect(entradaDeGrupo(7, 'programacion')).toBe('/preinversion/proyectos/7/etapas?grupo=programacion');
    expect(entradaDeGrupo(7, 'gestion')).toBe('/preinversion/proyectos/7/etapas?grupo=gestion');
  });

  it('Priorización ya no es un paso del proyecto', () => {
    expect(GRUPOS_PASOS.flatMap(pasosDe).some((p) => p.cu === 'CU-PRE-26.5')).toBe(false);
  });
});
