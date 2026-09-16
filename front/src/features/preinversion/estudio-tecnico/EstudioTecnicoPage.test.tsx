import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { EstudioTecnicoPage } from './EstudioTecnicoPage';

const obtenerDescripcionTecnica = vi.fn();
const guardarDescripcionTecnica = vi.fn();
const listarProductosIndicadores = vi.fn();
const listarTiposCosto = vi.fn();
const listarUnidadesMedida = vi.fn();
const obtenerProyecto = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    preinversionApi: { obtenerProyecto: (...a: unknown[]) => obtenerProyecto(...a) },
    descripcionTecnicaApi: {
      obtenerDescripcionTecnica: (...a: unknown[]) => obtenerDescripcionTecnica(...a),
      guardarDescripcionTecnica: (...a: unknown[]) => guardarDescripcionTecnica(...a),
    },
    catalogoEtapasApi: {
      listarProductosIndicadores: (...a: unknown[]) => listarProductosIndicadores(...a),
      listarTiposCosto: (...a: unknown[]) => listarTiposCosto(...a),
    },
    catalogoUnidadesApi: { listarUnidadesMedida: (...a: unknown[]) => listarUnidadesMedida(...a) },
  };
});

let rolesActivos: string[] = ['TECNICO_URP'];
vi.mock('../../../auth/useAuth', () => ({
  useAuth: () => ({ hasRole: (rol: string) => rolesActivos.includes(rol) }),
}));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const montar = () =>
  render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/estudio-tecnico']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/estudio-tecnico" element={<EstudioTecnicoPage />} />
      </Routes>
    </MemoryRouter>,
  );

describe('EstudioTecnicoPage · CU-PRE-11', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['TECNICO_URP'];
    obtenerProyecto.mockResolvedValue({ data: { nombre: 'Hospital de Santa Ana', cup: '10001' } });
    obtenerDescripcionTecnica.mockResolvedValue({ data: { idProyecto: 7, descripcionProyecto: null, filas: [] } });
    listarProductosIndicadores.mockResolvedValue({
      data: [{ codigoProducto: 'P1', producto: 'Aulas construidas' }, { codigoProducto: 'P1', producto: 'Aulas construidas' }],
    });
    listarTiposCosto.mockResolvedValue({ data: [{ codigo: 'C1', nombre: 'Obra civil' }] });
    listarUnidadesMedida.mockResolvedValue({ data: [{ tipo: 'LONGITUD', categoria: 'Métrico', unidadMedida: 'Metro' }] });
    swalFire.mockResolvedValue({ isConfirmed: true });
  });

  it('muestra las dos pestañas del capítulo, con Localización en construcción', async () => {
    montar();
    expect(await screen.findByRole('tab', { name: 'Descripción técnica' })).toHaveAttribute('aria-selected', 'true');
    fireEvent.click(screen.getByRole('tab', { name: 'Localización' }));
    expect(screen.getByText('En construcción')).toBeInTheDocument();
  });

  it('trae la descripción que ya venía y los catálogos', async () => {
    obtenerDescripcionTecnica.mockResolvedValue({
      data: {
        idProyecto: 7,
        descripcionProyecto: 'Ampliación del hospital',
        filas: [
          {
            producto: { codigoProducto: 'P1', producto: 'Aulas construidas' },
            componente: { codigo: 'C1', nombre: 'Obra civil' },
            descripcionProducto: 'Dos pabellones',
            cantidad: 2,
            unidadMedida: { tipo: 'LONGITUD', categoria: 'Métrico', unidadMedida: 'Metro' },
          },
        ],
      },
    });
    montar();
    expect(await screen.findByLabelText('Descripción del proyecto')).toHaveValue('Ampliación del hospital');
    expect(screen.getByLabelText('Producto de la fila 1')).toHaveValue('P1');
    expect(screen.getByLabelText('Componente de la fila 1')).toHaveValue('C1');
    expect(screen.getByLabelText('Unidad de medida de la fila 1')).toHaveValue('Metro');
    expect(screen.getByLabelText('Cantidad de la fila 1')).toHaveValue(2);
  });

  it('guarda los códigos que pide el contrato, no los objetos', async () => {
    guardarDescripcionTecnica.mockResolvedValue({ data: { idProyecto: 7, descripcionProyecto: 'Ampliación', filas: [] } });
    montar();
    await screen.findByLabelText('Producto de la fila 1');
    fireEvent.change(screen.getByLabelText('Descripción del proyecto'), { target: { value: 'Ampliación' } });
    fireEvent.change(screen.getByLabelText('Producto de la fila 1'), { target: { value: 'P1' } });
    fireEvent.change(screen.getByLabelText('Componente de la fila 1'), { target: { value: 'C1' } });
    fireEvent.change(screen.getByLabelText('Descripción del producto de la fila 1'), { target: { value: 'Dos pabellones' } });
    fireEvent.change(screen.getByLabelText('Cantidad de la fila 1'), { target: { value: '2' } });
    fireEvent.change(screen.getByLabelText('Unidad de medida de la fila 1'), { target: { value: 'Metro' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarDescripcionTecnica).toHaveBeenCalledWith({
        idProyecto: 7,
        descripcionTecnicaRequest: {
          descripcionProyecto: 'Ampliación',
          filas: [
            {
              producto: { codigoProducto: 'P1' },
              componente: 'C1',
              descripcionProducto: 'Dos pabellones',
              cantidad: 2,
              unidadMedida: 'Metro',
            },
          ],
        },
      }),
    );
  });

  // El back revienta con 500 si una fila va sin componente (Componente.nombre es @NotBlank),
  // aunque el contrato no lo exija. Se avisa antes de llamarlo.
  it('no manda una fila sin componente', async () => {
    montar();
    await screen.findByLabelText('Producto de la fila 1');
    fireEvent.change(screen.getByLabelText('Producto de la fila 1'), { target: { value: 'P1' } });
    fireEvent.change(screen.getByLabelText('Cantidad de la fila 1'), { target: { value: '3' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' })));
    expect(guardarDescripcionTecnica).not.toHaveBeenCalled();
  });

  it('limita la descripción del producto a 500 caracteres', async () => {
    montar();
    expect(await screen.findByLabelText('Descripción del producto de la fila 1')).toHaveAttribute('maxlength', '500');
  });

  it('avisa qué catálogo no responde y deja el resto usable', async () => {
    listarTiposCosto.mockRejectedValue(new Error('405'));
    montar();
    expect(await screen.findByText(/Estos catálogos no están disponibles: Componente/)).toBeInTheDocument();
    expect(screen.getByLabelText('Componente de la fila 1')).toBeDisabled();
    expect(screen.getByLabelText('Producto de la fila 1')).toBeEnabled();
  });

  it('el Técnico PRE consulta pero no edita', async () => {
    rolesActivos = ['TECNICO_PRE'];
    montar();
    expect(await screen.findByLabelText('Descripción del proyecto')).toHaveAttribute('readonly');
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });

  it('Regresar vuelve al diagnóstico', async () => {
    montar();
    await screen.findByLabelText('Descripción del proyecto');
    fireEvent.click(screen.getByRole('button', { name: 'Regresar' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/diagnostico');
  });
});
