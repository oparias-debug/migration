import { render, screen, waitFor } from '@testing-library/react';
import { beforeAll, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const listarMedidasCatalogo = vi.fn();
vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  catalogoPreinversionApi: { listarMedidasCatalogo: (...a: unknown[]) => listarMedidasCatalogo(...a) },
}));

const { CategoriasCatalogoModal } = await import('./CategoriasCatalogoModal');

// jsdom no implementa <dialog>.showModal(); se sustituye para poder montarlo.
beforeAll(() => {
  HTMLDialogElement.prototype.showModal = function abrir() { this.open = true; };
  HTMLDialogElement.prototype.close = function cerrar() { this.open = false; };
});

describe('CategoriasCatalogoModal', () => {
  it('muestra las tres tablas del anexo con sus categorías', async () => {
    listarMedidasCatalogo.mockImplementation(({ tipo }: { tipo: string }) =>
      Promise.resolve({ data: [{ codigo: `${tipo}-1`, descripcion: `Categoría de ${tipo}` }] }),
    );

    render(<CategoriasCatalogoModal onClose={() => {}} />);

    await waitFor(() => expect(listarMedidasCatalogo).toHaveBeenCalledTimes(3));
    expect(await screen.findByText('Categoría de GRD')).toBeInTheDocument();
    expect(screen.getByText('Categoría de GRC')).toBeInTheDocument();
    expect(screen.getByText('Categoría de ACC')).toBeInTheDocument();
    // Tras retirar Bootstrap, la ventana usa estilos propios.
    expect(document.querySelector('.modal-categorias')).toBeInTheDocument();
    expect(document.querySelectorAll('.mc-grupo')).toHaveLength(3);
  });

  it('el botón de cerrar avisa a quien la abrió', async () => {
    listarMedidasCatalogo.mockResolvedValue({ data: [] });
    const onClose = vi.fn();
    render(<CategoriasCatalogoModal onClose={onClose} />);

    // Hay dos salidas con el mismo nombre accesible: la × de la cabecera y el
    // botón del pie. Se usa la del pie, que es la que pulsa el usuario.
    await screen.findAllByRole('button', { name: 'Cerrar' });
    (document.querySelector('.mc-pie button') as HTMLButtonElement).click();
    await waitFor(() => expect(onClose).toHaveBeenCalled());
  });
});
