import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../i18n/i18n';
import { AppLayout } from './AppLayout';

vi.mock('../auth/useAuth', () => ({
  useAuth: () => ({
    hasRole: () => true,
    logout: vi.fn(),
    username: 'tecnico.urp',
    roles: ['TECNICO_URP'],
  }),
}));

const anchoDe = (px: number) => {
  Object.defineProperty(window, 'innerWidth', { writable: true, configurable: true, value: px });
  fireEvent(window, new Event('resize'));
};

const montar = (ruta = '/') =>
  render(
    <MemoryRouter initialEntries={[ruta]}>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="*" element={<p>contenido</p>} />
        </Route>
      </Routes>
    </MemoryRouter>,
  );

const menu = () => document.querySelector('#menu-lateral')!;

describe('AppLayout · el botón ☰', () => {
  beforeEach(() => anchoDe(1440));

  // Antes no hacía nada visible en pantalla ancha: el menú ya estaba fijo.
  it('en pantalla ancha contrae y despliega el menú', () => {
    montar();
    const hamburguesa = () => document.querySelector('.hamburguesa')!;
    expect(menu()).not.toHaveClass('plegado');
    expect(hamburguesa()).toHaveAttribute('aria-label', 'Contraer menú');

    fireEvent.click(hamburguesa());
    expect(menu()).toHaveClass('plegado');
    expect(hamburguesa()).toHaveAttribute('aria-label', 'Expandir menú');

    fireEvent.click(hamburguesa());
    expect(menu()).not.toHaveClass('plegado');
  });

  it('dice con aria-expanded si el menú está desplegado', () => {
    montar();
    const boton = () => document.querySelector('.hamburguesa')!;
    expect(boton()).toHaveAttribute('aria-expanded', 'true');
    fireEvent.click(boton());
    expect(boton()).toHaveAttribute('aria-expanded', 'false');
  });

  it('en pantalla estrecha abre el cajón', () => {
    anchoDe(900);
    montar();
    expect(menu()).not.toHaveClass('abierto');

    fireEvent.click(document.querySelector('.hamburguesa')!);
    expect(menu()).toHaveClass('abierto');
    expect(menu()).not.toHaveClass('plegado');

    // El mismo botón lo cierra: su etiqueta dice "contraer", tiene que cumplirlo.
    fireEvent.click(document.querySelector('.hamburguesa')!);
    expect(menu()).not.toHaveClass('abierto');
  });

  it('el menú lateral conserva su propio botón de contraer', () => {
    montar();
    expect(screen.getAllByRole('button', { name: /Contraer menú/ }).length).toBeGreaterThan(1);
  });
});

/**
 * Rocío, 24/09/2026: desde cualquiera de los cinco subprocesos hay que poder
 * volver al menú de Preinversión sin usar el "Atrás" del navegador.
 */
describe('AppLayout · volver al menú del macroproceso', () => {
  beforeEach(() => anchoDe(1440));

  it.each([
    ['1.1 Asignación CUP', '/preinversion/proyectos'],
    ['1.2 Creación ruta de preinversión', '/preinversion/creacion-ruta'],
    ['1.3 Formulación y evaluación', '/preinversion/formulacion'],
    ['1.4 Programación del proyecto', '/preinversion/programacion-proyecto'],
    ['1.5 Gestión del proyecto', '/preinversion/gestion-proyecto'],
  ])('%s ofrece la vuelta a Preinversión', (_nombre, ruta) => {
    montar(ruta);
    expect(screen.getByRole('link', { name: /Volver a Preinversión/ })).toHaveAttribute('href', '/preinversion');
  });

  it('también desde un paso de un proyecto, que es donde uno se pierde', () => {
    montar('/preinversion/proyectos/7/identificacion');
    expect(screen.getByRole('link', { name: /Volver a Preinversión/ })).toBeInTheDocument();
  });

  it('la banda de ruta lleva al mismo sitio', () => {
    montar('/preinversion/creacion-ruta');
    expect(screen.getByRole('link', { name: 'Preinversión' })).toHaveAttribute('href', '/preinversion');
  });

  it('en el propio menú de Preinversión no se ofrece volver a sí mismo', () => {
    montar('/preinversion');
    expect(screen.queryByRole('link', { name: /Volver a/ })).not.toBeInTheDocument();
  });

  it('fuera de Preinversión no aparece', () => {
    montar('/programacion/pap/avance-metas');
    expect(screen.queryByRole('link', { name: /Volver a/ })).not.toBeInTheDocument();
  });
});
