import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import '../i18n/i18n';
import { NotFoundPage } from './NotFoundPage';
import { PlaceholderPage } from './PlaceholderPage';

/**
 * Las dos pantallas de aviso. Se comprueban porque al retirar Bootstrap
 * perdieron sus clases de utilidad y hubo que darles estilo propio: la prueba
 * fija qué dicen y que sigan centradas en su contenedor.
 */
describe('pantallas de aviso', () => {
  it('la de dirección inexistente avisa y ofrece volver al inicio', () => {
    render(
      <MemoryRouter initialEntries={['/una-ruta-que-no-existe']}>
        <NotFoundPage />
      </MemoryRouter>,
    );
    expect(document.querySelector('.pagina-aviso')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /inicio/i })).toBeInTheDocument();
  });

  it('la de opción sin desarrollar muestra el título de la opción y "En construcción"', () => {
    render(
      <MemoryRouter initialEntries={['/reportes']}>
        <PlaceholderPage />
      </MemoryRouter>,
    );
    expect(document.querySelector('.pagina-aviso')).toBeInTheDocument();
    expect(screen.getByText(/construcción/i)).toBeInTheDocument();
  });
});
