import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';
import '../i18n/i18n';

const navigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const { HomePage } = await import('./HomePage');

const montar = () => render(<MemoryRouter><HomePage /></MemoryRouter>);

/** Pantalla de inicio: la banda del ministerio y las tarjetas de los módulos. */
describe('HomePage', () => {
  it('muestra los módulos principales como tarjetas', () => {
    montar();
    expect(document.querySelector('.hero')).toBeInTheDocument();
    // "Preinversión" aparece en la tarjeta y en el listado de pendientes: se
    // busca la tarjeta, que es la que lleva al módulo.
    expect(document.querySelector('.modulo')).toBeInTheDocument();
    expect(document.querySelectorAll('.modulo').length).toBeGreaterThan(1);
  });

  it('la tarjeta de un módulo entra en su proceso', () => {
    navigate.mockReset();
    montar();
    fireEvent.click(document.querySelector('.modulo') as HTMLButtonElement);
    expect(navigate).toHaveBeenCalled();
    expect(String(navigate.mock.calls[0][0])).toContain('/preinversion');
  });
});
