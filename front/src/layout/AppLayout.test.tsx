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

const montar = () =>
  render(
    <MemoryRouter initialEntries={['/']}>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<p>contenido</p>} />
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
