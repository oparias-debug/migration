import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import '../i18n/i18n';
import { PlaceholderPage } from './PlaceholderPage';

const montar = (ruta: string) =>
  render(
    <MemoryRouter initialEntries={[ruta]}>
      <PlaceholderPage />
    </MemoryRouter>,
  );

describe('PlaceholderPage', () => {
  it('muestra sólo el título de la opción y "En construcción"', () => {
    const { container } = montar('/programacion/priorizacion');
    expect(screen.getByRole('heading', { name: 'Priorización' })).toBeInTheDocument();
    expect(screen.getByText('En construcción')).toBeInTheDocument();
    expect(container.querySelectorAll('p')).toHaveLength(1);
    expect(screen.queryByRole('link')).not.toBeInTheDocument();
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });
});
