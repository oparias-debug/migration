import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { PasosProyectoLayout } from './PasosProyectoLayout';
import { ubicarPaso } from './pasosProyecto';

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

describe('PasosProyectoLayout', () => {
  beforeEach(() => {
    obtenerProyecto.mockReset();
    obtenerProyecto.mockResolvedValue({ data: { nombre: 'Hospital de Santa Ana', cup: '10001' } });
  });

  it('pinta la pantalla del paso y marca el paso actual', async () => {
    montar('/preinversion/proyectos/7/identificacion');
    expect(screen.getByText('pantalla identificación')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Identificación' })).toHaveAttribute('aria-current', 'step');
    await screen.findByText('Hospital de Santa Ana');
  });

  it('los pasos con pantalla enlazan a ese mismo proyecto', async () => {
    montar('/preinversion/proyectos/7/identificacion');
    expect(screen.getByRole('link', { name: 'Alternativas de solución' })).toHaveAttribute(
      'href',
      '/preinversion/proyectos/7/alternativas-solucion',
    );
    await screen.findByText('Hospital de Santa Ana');
  });

  it('navega al hacer clic en otro paso', async () => {
    montar('/preinversion/proyectos/7/identificacion');
    fireEvent.click(screen.getByRole('link', { name: 'Alternativas de solución' }));
    expect(await screen.findByText('pantalla alternativas')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Alternativas de solución' })).toHaveAttribute('aria-current', 'step');
  });

  // Se ve el recorrido completo, pero lo que no tiene pantalla no enlaza.
  it('los pasos sin pantalla se muestran pero no son enlaces', async () => {
    montar('/preinversion/proyectos/7/identificacion');
    expect(screen.getByText('Análisis de interesados')).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: /Análisis de interesados/ })).not.toBeInTheDocument();
    await screen.findByText('Hospital de Santa Ana');
  });

  it('Ruta de Preinversión pertenece al paso Registro de etapas', async () => {
    montar('/preinversion/proyectos/7/ruta-preinversion');
    expect(screen.getByRole('link', { name: 'Registro de etapas' })).toHaveAttribute('aria-current', 'step');
    await screen.findByText('Hospital de Santa Ana');
  });

  it('cambiar de grupo muestra sus pasos sin salir de la pantalla', async () => {
    montar('/preinversion/proyectos/7/identificacion');
    expect(screen.queryByRole('link', { name: 'Presupuesto de inversión' })).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: 'Presupuesto' }));

    expect(screen.getByRole('link', { name: 'Presupuesto de inversión' })).toBeInTheDocument();
    expect(screen.getByText('pantalla identificación')).toBeInTheDocument();
    await screen.findByText('Hospital de Santa Ana');
  });

  it('se abre en el grupo del paso actual y numera de forma correlativa', async () => {
    montar('/preinversion/proyectos/7/presupuesto');
    const enlace = screen.getByRole('link', { name: 'Presupuesto de inversión' });
    expect(enlace).toHaveAttribute('aria-current', 'step');
    // 7 pasos de Formulación + 5 de Análisis técnico: Presupuesto es el 13.
    expect(enlace).toHaveTextContent('13');
    await screen.findByText('Hospital de Santa Ana');
  });

  it('muestra el nombre y el CUP del proyecto', async () => {
    montar('/preinversion/proyectos/7/identificacion');
    expect(await screen.findByText('Hospital de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText(/CUP 10001/)).toBeInTheDocument();
  });

  it('si el proyecto no carga, la barra funciona igual', async () => {
    obtenerProyecto.mockRejectedValue(new Error('403'));
    montar('/preinversion/proyectos/7/identificacion');
    await waitFor(() => expect(obtenerProyecto).toHaveBeenCalled());
    expect(screen.getByRole('link', { name: 'Identificación' })).toBeInTheDocument();
    expect(screen.getByText('pantalla identificación')).toBeInTheDocument();
  });
});

describe('ubicarPaso', () => {
  it('reconoce las pantallas que forman un mismo paso', () => {
    expect(ubicarPaso('/preinversion/proyectos/7/ficha-emergencia')).toMatchObject({
      idProyecto: 7,
      paso: { clave: 'registro-etapas' },
    });
  });

  it('no trata como paso la ficha del proyecto ni el alta', () => {
    expect(ubicarPaso('/preinversion/proyectos/7')).toBeNull();
    expect(ubicarPaso('/preinversion/proyectos/nuevo')).toBeNull();
  });
});
