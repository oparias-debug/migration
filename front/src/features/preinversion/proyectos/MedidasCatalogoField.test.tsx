import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { useState } from 'react';

const listarMedidasCatalogo = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  catalogoPreinversionApi: {
    listarMedidasCatalogo: (...a: unknown[]) => listarMedidasCatalogo(...a),
  },
}));

const { MedidasCatalogoField } = await import('./MedidasCatalogoField');
const { TipoMedidaCatalogo } = await import('../../../api/preinversionApi');

const OPCIONES = [
  { codigo: 'GRD-I', descripcion: 'i. Reducción del riesgo existente' },
  { codigo: 'GRD-II', descripcion: 'ii. Prevención de nuevos riesgos' },
  { codigo: 'GRD-III', descripcion: 'iii. Preparación ante desastres' },
];

// Envoltorio con estado: el componente es controlado, así que sin esto un
// clic no se reflejaría en la siguiente aserción.
function Anfitrion({ inicial = [] as string[] }) {
  const [valor, setValor] = useState<string[]>(inicial);
  return (
    <MedidasCatalogoField
      tipo={TipoMedidaCatalogo.Grd}
      label="Categorías de GRD"
      value={valor}
      onChange={setValor}
    />
  );
}

const categoria = () => screen.getByLabelText('Categorías de GRD') as HTMLInputElement;
const opciones = () => OPCIONES.map((o) => screen.getByLabelText(o.descripcion) as HTMLInputElement);

describe('MedidasCatalogoField', () => {
  beforeEach(() => {
    listarMedidasCatalogo.mockReset().mockResolvedValue({ data: OPCIONES });
  });

  it('marcar la categoría selecciona todas sus opciones', async () => {
    render(<Anfitrion />);
    await waitFor(() => expect(opciones()).toHaveLength(3));

    // Antes de activarla las opciones están deshabilitadas.
    expect(opciones().every((o) => o.disabled)).toBe(true);

    fireEvent.click(categoria());
    expect(opciones().every((o) => o.checked && !o.disabled)).toBe(true);
    expect(categoria().checked).toBe(true);
  });

  it('permite desmarcar una opción y deja la categoría en estado mixto', async () => {
    render(<Anfitrion />);
    await waitFor(() => expect(opciones()).toHaveLength(3));
    fireEvent.click(categoria());

    fireEvent.click(opciones()[1]);

    expect(opciones().map((o) => o.checked)).toEqual([true, false, true]);
    expect(categoria().checked).toBe(false);
    expect(categoria().indeterminate).toBe(true);
    expect(categoria()).toHaveAttribute('aria-checked', 'mixed');
  });

  // Es lo que evita el estado imposible "categoría marcada, cero medidas".
  it('al quitar la última opción la categoría vuelve a apagarse', async () => {
    render(<Anfitrion />);
    await waitFor(() => expect(opciones()).toHaveLength(3));
    fireEvent.click(categoria());

    opciones().forEach((o) => fireEvent.click(o));

    expect(categoria().checked).toBe(false);
    expect(categoria().indeterminate).toBe(false);
    expect(opciones().every((o) => o.disabled)).toBe(true);
  });

  it('pulsar la categoría en estado mixto vuelve a marcarlas todas', async () => {
    render(<Anfitrion />);
    await waitFor(() => expect(opciones()).toHaveLength(3));
    fireEvent.click(categoria());
    fireEvent.click(opciones()[0]);
    expect(categoria().indeterminate).toBe(true);

    fireEvent.click(categoria());

    expect(opciones().every((o) => o.checked)).toBe(true);
    expect(categoria().checked).toBe(true);
  });

  // Un proyecto guardado con medidas debe abrirse con su categoría activa, sin
  // que el usuario tenga que volver a marcarla.
  it('un valor inicial no vacío deja la categoría activa', async () => {
    render(<Anfitrion inicial={['GRD-I']} />);
    await waitFor(() => expect(opciones()).toHaveLength(3));

    expect(categoria().indeterminate).toBe(true);
    expect(opciones().every((o) => !o.disabled)).toBe(true);
  });
});
