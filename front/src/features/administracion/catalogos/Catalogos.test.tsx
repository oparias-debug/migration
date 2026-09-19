import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { CatalogosPage } from './CatalogosPage';
import { CatalogoDetallePage } from './CatalogoDetallePage';
import { problemaDeCampos } from './CamposEditor';

const listarCatalogos = vi.fn();
const crearCatalogo = vi.fn();
const verificarExistenciaCatalogo = vi.fn();
const consultarCatalogo = vi.fn();
const actualizarDescriptoresCatalogo = vi.fn();
const eliminarCatalogo = vi.fn();
const buscarListaRegistros = vi.fn();
const crearRegistroCatalogo = vi.fn();
const actualizarRegistro = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/administracionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/administracionApi')>();
  return {
    ...actual,
    catalogosApi: {
      listarCatalogos: (...a: unknown[]) => listarCatalogos(...a),
      crearCatalogo: (...a: unknown[]) => crearCatalogo(...a),
      verificarExistenciaCatalogo: (...a: unknown[]) => verificarExistenciaCatalogo(...a),
      consultarCatalogo: (...a: unknown[]) => consultarCatalogo(...a),
      actualizarDescriptoresCatalogo: (...a: unknown[]) => actualizarDescriptoresCatalogo(...a),
      eliminarCatalogo: (...a: unknown[]) => eliminarCatalogo(...a),
    },
    registrosCatalogoApi: {
      buscarListaRegistros: (...a: unknown[]) => buscarListaRegistros(...a),
      crearRegistroCatalogo: (...a: unknown[]) => crearRegistroCatalogo(...a),
      actualizarRegistro: (...a: unknown[]) => actualizarRegistro(...a),
    },
  };
});

let rolesActivos: string[] = ['ADMINISTRADOR_DE_CATALOGOS'];
vi.mock('../../../auth/useAuth', () => ({
  useAuth: () => ({ hasRole: (rol: string) => rolesActivos.includes(rol) }),
}));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const CATALOGO = {
  code: 'TIPO-DOC',
  name: 'Tipos de documento',
  parent: null,
  active: 'ACTIVE',
  fromDate: null,
  toDate: null,
  fields: [
    { name: 'descripcion', qualifier: 'FIELD', position: 2 },
    { name: 'codigo', qualifier: 'KEY', position: 1 },
  ],
};

const montarLista = () =>
  render(
    <MemoryRouter>
      <CatalogosPage />
    </MemoryRouter>,
  );
const montarFicha = () =>
  render(
    <MemoryRouter initialEntries={['/catalogos-generales/TIPO-DOC']}>
      <Routes>
        <Route path="/catalogos-generales/:codigo" element={<CatalogoDetallePage />} />
      </Routes>
    </MemoryRouter>,
  );

describe('CU-ADM-01 · catálogos', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DE_CATALOGOS'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    listarCatalogos.mockResolvedValue({ data: { content: [CATALOGO], totalPages: 1 } });
    consultarCatalogo.mockResolvedValue({ data: CATALOGO });
    buscarListaRegistros.mockResolvedValue({
      data: { content: [{ key: 'DUI', values: { codigo: 'DUI', descripcion: 'Documento Único' }, active: 'ACTIVE' }] },
    });
    verificarExistenciaCatalogo.mockResolvedValue({ data: { exists: false } });
  });

  it('sin el rol del CU no muestra nada del catálogo', () => {
    rolesActivos = ['ADMINISTRADOR'];
    montarLista();
    expect(screen.getByRole('alert')).toHaveTextContent(/Administrador de Catálogos/);
    expect(listarCatalogos).not.toHaveBeenCalled();
  });

  it('lista los catálogos y abre uno', async () => {
    montarLista();
    expect(await screen.findByText('Tipos de documento')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Abrir el catálogo Tipos de documento' }));
    expect(navigate).toHaveBeenCalledWith('/catalogos-generales/TIPO-DOC');
  });

  it('crea un catálogo con sus campos, en orden y con su calificador', async () => {
    crearCatalogo.mockResolvedValue({ data: {} });
    montarLista();
    fireEvent.click(await screen.findByRole('button', { name: 'Nuevo catálogo' }));
    fireEvent.change(screen.getByLabelText('Código*'), { target: { value: 'TIPO-DOC' } });
    fireEvent.change(screen.getByLabelText('Nombre*'), { target: { value: 'Tipos de documento' } });
    fireEvent.change(screen.getByLabelText('Nombre del campo 1'), { target: { value: 'codigo' } });
    fireEvent.click(screen.getByRole('button', { name: 'Agregar campo' }));
    fireEvent.change(screen.getByLabelText('Nombre del campo 2'), { target: { value: 'descripcion' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear catálogo' }));

    await waitFor(() => expect(crearCatalogo).toHaveBeenCalled());
    expect(crearCatalogo.mock.calls[0][0].catalogCreateRequest.fields).toEqual([
      { name: 'codigo', qualifier: 'KEY', position: 1 },
      { name: 'descripcion', qualifier: 'FIELD', position: 2 },
    ]);
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/catalogos-generales/TIPO-DOC'));
  });

  it('avisa si el nombre ya existe', async () => {
    verificarExistenciaCatalogo.mockResolvedValue({ data: { exists: true } });
    montarLista();
    fireEvent.click(await screen.findByRole('button', { name: 'Nuevo catálogo' }));
    fireEvent.change(screen.getByLabelText('Nombre*'), { target: { value: 'Sectores' } });
    fireEvent.blur(screen.getByLabelText('Nombre*'));
    expect(await screen.findByText('Ya existe un catálogo con ese nombre.')).toBeInTheDocument();
  });

  it('la ficha ordena los campos por posición y muestra los registros', async () => {
    montarFicha();
    expect(await screen.findByLabelText('Nombre del campo 1')).toHaveValue('codigo');
    expect(screen.getByLabelText('Nombre del campo 2')).toHaveValue('descripcion');
    expect(await screen.findByText('Documento Único')).toBeInTheDocument();
  });

  it('crea un registro con los valores de todos los campos', async () => {
    crearRegistroCatalogo.mockResolvedValue({ data: {} });
    montarFicha();
    await screen.findByText('Documento Único');
    fireEvent.change(screen.getByLabelText('codigo*'), { target: { value: 'NIT' } });
    fireEvent.change(screen.getByLabelText('descripcion'), { target: { value: 'Tributario' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar registro' }));

    await waitFor(() =>
      expect(crearRegistroCatalogo).toHaveBeenCalledWith({
        code: 'TIPO-DOC',
        catalogRecordCreateRequest: { values: { codigo: 'NIT', descripcion: 'Tributario' } },
      }),
    );
  });

  // El back rechaza la actualización si el cuerpo trae un campo KEY, aunque sea el mismo valor.
  it('al editar un registro no manda la clave, que va en la ruta', async () => {
    actualizarRegistro.mockResolvedValue({ data: {} });
    montarFicha();
    fireEvent.click(await screen.findByRole('button', { name: 'Editar el registro DUI' }));
    expect(screen.getByLabelText('codigo*')).toHaveAttribute('readonly');
    fireEvent.change(screen.getByLabelText('descripcion'), { target: { value: 'Documento Único de Identidad' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar registro' }));

    await waitFor(() =>
      expect(actualizarRegistro).toHaveBeenCalledWith({
        code: 'TIPO-DOC',
        key: 'DUI',
        catalogRecordUpdateRequest: { values: { descripcion: 'Documento Único de Identidad' } },
      }),
    );
  });

  it('no deja guardar un registro sin la clave', async () => {
    montarFicha();
    await screen.findByText('Documento Único');
    fireEvent.change(screen.getByLabelText('descripcion'), { target: { value: 'Sin clave' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar registro' }));
    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' })));
    expect(crearRegistroCatalogo).not.toHaveBeenCalled();
  });
});

describe('problemaDeCampos', () => {
  it('pide al menos una clave, nombres y sin repetir', () => {
    expect(problemaDeCampos([])).toBe('sinCampos');
    expect(problemaDeCampos([{ nombre: '', clave: true }])).toBe('campoSinNombre');
    expect(problemaDeCampos([{ nombre: 'a', clave: true }, { nombre: 'A', clave: false }])).toBe('camposRepetidos');
    expect(problemaDeCampos([{ nombre: 'a', clave: false }])).toBe('sinClave');
    expect(problemaDeCampos([{ nombre: 'a', clave: true }])).toBeNull();
  });
});
