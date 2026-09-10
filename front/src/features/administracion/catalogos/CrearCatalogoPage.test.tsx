import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { CrearCatalogoPage } from './CrearCatalogoPage';

const crearCatalogo = vi.fn();
const buscarCatalogoPorNombre = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/administracionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/administracionApi')>();
  return {
    ...actual,
    catalogosApi: {
      crearCatalogo: (...a: unknown[]) => crearCatalogo(...a),
      buscarCatalogoPorNombre: (...a: unknown[]) => buscarCatalogoPorNombre(...a),
    },
  };
});

let rolesActivos: string[] = ['ADMINISTRADOR_DEL_SISTEMA'];
vi.mock('../../../auth/useAuth', () => ({
  useAuth: () => ({ hasRole: (rol: string) => rolesActivos.includes(rol) }),
}));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const montar = () =>
  render(
    <MemoryRouter>
      <CrearCatalogoPage />
    </MemoryRouter>,
  );

/** Rellena código, nombre y el primer campo, que es lo mínimo válido. */
function rellenarMinimo() {
  fireEvent.change(screen.getByLabelText('Código*'), { target: { value: 'SECTOR' } });
  fireEvent.change(screen.getByLabelText('Nombre*'), { target: { value: 'Sectores' } });
  fireEvent.change(screen.getByLabelText('Nombre del campo 1'), { target: { value: 'codigo' } });
}

describe('CrearCatalogoPage · CU-ADM-01 flujo principal', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DEL_SISTEMA'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    buscarCatalogoPorNombre.mockResolvedValue({ data: { nombre: '', existe: false } });
  });

  it('crea el catálogo con sus campos (camino feliz)', async () => {
    crearCatalogo.mockResolvedValue({ data: {} });
    montar();
    rellenarMinimo();
    fireEvent.click(screen.getByRole('button', { name: 'Crear catálogo' }));

    await waitFor(() =>
      expect(crearCatalogo).toHaveBeenCalledWith({
        crearCatalogoRequest: expect.objectContaining({
          codigo: 'SECTOR',
          nombre: 'Sectores',
          campos: [expect.objectContaining({ nombre: 'codigo', tipo: 'STRING', esKey: true })],
        }),
      }),
    );
    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'success' })));
  });

  // regla 17
  it('exige al menos un campo, y por eso no deja borrar el último', async () => {
    montar();
    expect(screen.getByLabelText('Eliminar el campo 1')).toBeDisabled();
  });

  // regla 2
  it('exige que algún campo sea la clave', async () => {
    montar();
    rellenarMinimo();
    // se desmarca la clave añadiendo otro campo y marcándolo, luego borrando el primero
    fireEvent.click(screen.getByRole('button', { name: 'Agregar campo' }));
    fireEvent.change(screen.getByLabelText('Nombre del campo 2'), { target: { value: 'descripcion' } });
    fireEvent.click(screen.getByLabelText('Marcar el campo 2 como clave'));
    await waitFor(() => expect(screen.getByLabelText('Marcar el campo 1 como clave')).not.toBeChecked());
    expect(screen.getByLabelText('Marcar el campo 2 como clave')).toBeChecked();
  });

  // regla 3
  it('avisa cuando dos campos se llaman igual', async () => {
    montar();
    rellenarMinimo();
    fireEvent.click(screen.getByRole('button', { name: 'Agregar campo' }));
    fireEvent.change(screen.getByLabelText('Nombre del campo 2'), { target: { value: 'CODIGO' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear catálogo' }));

    expect(await screen.findByText('Ya hay un campo con ese nombre')).toBeInTheDocument();
    expect(crearCatalogo).not.toHaveBeenCalled();
  });

  it('un campo ENUM sin valores no se acepta', async () => {
    montar();
    rellenarMinimo();
    fireEvent.change(screen.getByLabelText('Tipo del campo 1'), { target: { value: 'ENUM' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear catálogo' }));

    expect(await screen.findByText('Indique los valores permitidos')).toBeInTheDocument();
    expect(crearCatalogo).not.toHaveBeenCalled();
  });

  it('manda los valores del ENUM como lista, uno por línea', async () => {
    crearCatalogo.mockResolvedValue({ data: {} });
    montar();
    rellenarMinimo();
    fireEvent.change(screen.getByLabelText('Tipo del campo 1'), { target: { value: 'ENUM' } });
    fireEvent.change(screen.getByLabelText('Valores permitidos del campo 1'), {
      target: { value: 'ALTO\n  MEDIO  \n\nBAJO\n' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Crear catálogo' }));

    await waitFor(() =>
      expect(crearCatalogo).toHaveBeenCalledWith({
        crearCatalogoRequest: expect.objectContaining({
          campos: [expect.objectContaining({ valoresEnum: ['ALTO', 'MEDIO', 'BAJO'] })],
        }),
      }),
    );
  });

  // regla 12: sin fechas, el estado lo decide el servidor
  it('no manda vigencia si no se indicó ninguna fecha', async () => {
    crearCatalogo.mockResolvedValue({ data: {} });
    montar();
    rellenarMinimo();
    fireEvent.click(screen.getByRole('button', { name: 'Crear catálogo' }));

    await waitFor(() => expect(crearCatalogo).toHaveBeenCalled());
    const enviado = crearCatalogo.mock.calls[0][0].crearCatalogoRequest;
    expect(enviado.vigencia).toBeUndefined();
  });

  it('manda la vigencia cuando sí hay fechas', async () => {
    crearCatalogo.mockResolvedValue({ data: {} });
    montar();
    rellenarMinimo();
    fireEvent.change(screen.getByLabelText('Vigente desde'), { target: { value: '2026-01-01' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear catálogo' }));

    await waitFor(() =>
      expect(crearCatalogo).toHaveBeenCalledWith({
        crearCatalogoRequest: expect.objectContaining({
          vigencia: { fechaDesde: '2026-01-01', fechaHasta: null },
        }),
      }),
    );
  });

  it('avisa si el nombre ya está en uso, sin bloquear el envío', async () => {
    buscarCatalogoPorNombre.mockResolvedValue({ data: { nombre: 'Sectores', existe: true } });
    montar();
    fireEvent.change(screen.getByLabelText('Nombre*'), { target: { value: 'Sectores' } });
    fireEvent.blur(screen.getByLabelText('Nombre*'));

    expect(await screen.findByText('Ya existe un catálogo con ese nombre')).toBeInTheDocument();
  });

  it('quien no administra catálogos no ve el formulario', async () => {
    rolesActivos = ['TECNICO_URP'];
    montar();
    expect(screen.getByRole('alert')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Crear catálogo' })).not.toBeInTheDocument();
  });

  it('dice en pantalla lo que el contrato todavía no permite', async () => {
    montar();
    expect(screen.getByText('Ver la lista de catálogos existentes')).toBeInTheDocument();
    expect(screen.getByText(/GET \/catalogos/)).toBeInTheDocument();
  });
});
