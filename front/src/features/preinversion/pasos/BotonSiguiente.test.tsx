import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const navigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const { BotonSiguiente } = await import('./BotonSiguiente');

const montar = (props: Partial<Parameters<typeof BotonSiguiente>[0]> = {}) =>
  render(
    <MemoryRouter>
      <BotonSiguiente idProyecto={7} paso="riesgos" {...props} />
    </MemoryRouter>,
  );

beforeEach(() => navigate.mockReset());

describe('BotonSiguiente', () => {
  it('lleva al capítulo siguiente del árbol', () => {
    montar();
    fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/analisis-legal');
  });

  // El rótulo no nombra su destino: así no queda desactualizado al intercalar un capítulo.
  it('el rótulo es el mismo en cualquier capítulo', () => {
    montar({ paso: 'diagnostico' });
    expect(screen.getByRole('button', { name: 'Siguiente' })).toBeInTheDocument();
    expect(screen.queryByText(/Siguiente:/)).not.toBeInTheDocument();
  });

  it('no se dibuja en el último capítulo con pantalla', () => {
    montar({ paso: 'viabilidad' });
    expect(screen.queryByRole('button', { name: 'Siguiente' })).not.toBeInTheDocument();
  });

  it('no se dibuja para un paso que todavía no tiene pantalla', () => {
    montar({ paso: 'flujo-socioeconomico' });
    expect(screen.queryByRole('button', { name: 'Siguiente' })).not.toBeInTheDocument();
  });

  it('cuando el capítulo exige algo antes, sólo avanza si se cumple', async () => {
    const antesDeAvanzar = vi.fn().mockResolvedValue(false);
    montar({ antesDeAvanzar });
    fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
    await waitFor(() => expect(antesDeAvanzar).toHaveBeenCalled());
    expect(navigate).not.toHaveBeenCalled();
  });

  it('avanza cuando lo previo se cumple', async () => {
    const antesDeAvanzar = vi.fn().mockResolvedValue(true);
    montar({ antesDeAvanzar });
    fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/analisis-legal'));
  });

  it('deshabilitado no llama a nada', () => {
    montar({ deshabilitado: true });
    const boton = screen.getByRole('button', { name: 'Siguiente' });
    expect(boton).toBeDisabled();
    fireEvent.click(boton);
    expect(navigate).not.toHaveBeenCalled();
  });
});
