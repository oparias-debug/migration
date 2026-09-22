import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeAll, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const swalFire = vi.fn().mockResolvedValue({ isConfirmed: true });
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const { DetalleMacroactividadModal } = await import('./DetalleMacroactividadModal');

// jsdom no implementa <dialog>.
beforeAll(() => {
  HTMLDialogElement.prototype.showModal = function abrir() { this.open = true; };
  HTMLDialogElement.prototype.close = function cerrar() { this.open = false; };
});

const INSUMOS = [
  { codigo: 'MO', nombre: 'Mano de obra' },
  { codigo: 'MAT', nombre: 'Materiales' },
];

const montar = (props: Partial<Parameters<typeof DetalleMacroactividadModal>[0]> = {}) =>
  render(
    <DetalleMacroactividadModal
      abierto
      periodos={2}
      insumosCatalogo={INSUMOS as never}
      muestraAjustados={false}
      onGuardar={props.onGuardar ?? vi.fn().mockResolvedValue(undefined)}
      onCerrar={props.onCerrar ?? vi.fn()}
      macroactividad={props.macroactividad}
    />,
  );

describe('DetalleMacroactividadModal (Anexo A.2)', () => {
  it('pide el nombre y un costo por insumo y período', () => {
    montar();
    expect(screen.getByLabelText(/Nombre/i)).toBeInTheDocument();
    INSUMOS.forEach((i) => expect(screen.getByText(i.nombre)).toBeInTheDocument());
    // Dos insumos × dos períodos: una casilla de costo para cada combinación.
    expect(screen.getAllByRole('textbox').length).toBeGreaterThanOrEqual(INSUMOS.length * 2);
  });

  it('no guarda si falta el nombre de la macroactividad', async () => {
    const onGuardar = vi.fn().mockResolvedValue(undefined);
    montar({ onGuardar });

    fireEvent.click(screen.getByRole('button', { name: /Guardar/i }));

    await waitFor(() => expect(screen.getByRole('button', { name: /Guardar/i })).toBeEnabled());
    expect(onGuardar).not.toHaveBeenCalled();
  });

  it('carga los datos de una macroactividad existente al editarla', () => {
    montar({
      macroactividad: {
        idMacroactividad: 3,
        nombreMacroactividad: 'Construcción de aulas',
        insumos: [],
      } as never,
    });
    expect(screen.getByDisplayValue('Construcción de aulas')).toBeInTheDocument();
  });
});
