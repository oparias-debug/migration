import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
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
const consultarCatalogosHijos = vi.fn();
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
      consultarCatalogosHijos: (...a: unknown[]) => consultarCatalogosHijos(...a),
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
    consultarCatalogosHijos.mockResolvedValue({ data: [] });
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

  // El back no permite eliminar ni catálogos ni registros: sólo inactivarlos.
  it('no ofrece eliminar, sólo inactivar', async () => {
    montarFicha();
    await screen.findByText('Documento Único');
    expect(screen.queryByRole('button', { name: /Eliminar catálogo/ })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /Eliminar el registro/ })).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Inactivar el registro DUI' })).toBeInTheDocument();
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


/**
 * Un catálogo jerárquico se veía igual que uno suelto: la ficha guardaba el
 * código del padre pero no decía quiénes eran sus hijos, así que la jerarquía no
 * se veía por ninguna parte (observación del 30/09/2026).
 */
describe('la jerarquía de catálogos se ve', () => {
  const PADRE = { ...CATALOGO, code: 'PRUEBA', name: 'Catálogo de prueba' };
  const HIJO = { code: 'JERARQUICO', name: 'Catálogo jerárquico' };

  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DE_CATALOGOS'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    buscarListaRegistros.mockResolvedValue({ data: { content: [] } });
    consultarCatalogosHijos.mockResolvedValue({ data: [] });
    consultarCatalogo.mockResolvedValue({ data: PADRE });
    listarCatalogos.mockResolvedValue({ data: { content: [PADRE], totalPages: 1 } });
  });

  it('la ficha del padre lista sus hijos y los abre', async () => {
    consultarCatalogosHijos.mockResolvedValue({ data: [HIJO] });
    montarFicha();

    expect(await screen.findByText('Catálogo jerárquico')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Abrir el catálogo Catálogo jerárquico' }));
    expect(navigate).toHaveBeenCalledWith('/catalogos-generales/JERARQUICO');
  });

  it('un catálogo sin hijos lo dice', async () => {
    montarFicha();
    expect(await screen.findByText('Este catálogo no tiene catálogos hijos.')).toBeInTheDocument();
  });

  // La jerarquía se recorre en los dos sentidos.
  it('desde el hijo se sube al padre', async () => {
    consultarCatalogo.mockResolvedValue({ data: { ...CATALOGO, code: 'JERARQUICO', parent: 'PRUEBA' } });
    montarFicha();

    fireEvent.click(await screen.findByRole('button', { name: 'Abrir el catálogo padre PRUEBA' }));
    expect(navigate).toHaveBeenCalledWith('/catalogos-generales/PRUEBA');
  });

  it('sin padre no se ofrece subir', async () => {
    montarFicha();
    await screen.findByText('Este catálogo no tiene catálogos hijos.');
    expect(screen.queryByRole('button', { name: /Abrir el catálogo padre/ })).not.toBeInTheDocument();
  });

  it('la lista muestra de quién cuelga cada catálogo', async () => {
    listarCatalogos.mockResolvedValue({
      data: { content: [PADRE, { ...CATALOGO, code: 'JERARQUICO', name: 'Catálogo jerárquico', parent: 'PRUEBA' }], totalPages: 1 },
    });
    montarLista();

    const fila = (await screen.findByText('Catálogo jerárquico')).closest('tr') as HTMLElement;
    expect(fila).toHaveTextContent('PRUEBA');
  });

  // Los hijos son contexto: si no cargan, la ficha sigue sirviendo.
  it('si los hijos no cargan, la ficha funciona igual', async () => {
    consultarCatalogosHijos.mockRejectedValue(new Error('falla'));
    montarFicha();
    expect(await screen.findByText('Este catálogo no tiene catálogos hijos.')).toBeInTheDocument();
    expect(screen.queryByRole('alert')).not.toBeInTheDocument();
  });
});


/**
 * El código sólo estaba en la banda del título. En los datos se veía el código
 * del catálogo padre pero no el propio, así que parecía que la ficha no lo
 * trajera (observación del 30/09/2026).
 */
describe('el código del catálogo se ve en sus datos', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DE_CATALOGOS'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    buscarListaRegistros.mockResolvedValue({ data: { content: [] } });
    consultarCatalogosHijos.mockResolvedValue({ data: [] });
    consultarCatalogo.mockResolvedValue({ data: { ...CATALOGO, code: 'JERARQUICO', parent: 'PRUEBA' } });
  });

  it('lo muestra junto al nombre', async () => {
    montarFicha();
    expect(await screen.findByLabelText('Código')).toHaveValue('JERARQUICO');
  });

  // Regla 17: el código identifica al catálogo y el contrato lo deja fuera del
  // cuerpo de actualización, así que no se ofrece cambiarlo.
  it('no se puede cambiar, y guardar datos no lo manda', async () => {
    montarFicha();
    const codigo = await screen.findByLabelText('Código');
    expect(codigo).toHaveAttribute('readonly');

    actualizarDescriptoresCatalogo.mockResolvedValue({ data: {} });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar datos' }));
    await waitFor(() => expect(actualizarDescriptoresCatalogo).toHaveBeenCalled());
    const enviado = actualizarDescriptoresCatalogo.mock.calls[0][0].catalogDescriptorsUpdateRequest;
    expect(enviado).not.toHaveProperty('code');
  });
});


/**
 * Miga de pan dentro de la ficha: la banda de ruta de arriba sale de la URL y no
 * puede saber de quién cuelga un catálogo (indicación del 30/09/2026).
 */
describe('la miga de pan dice en qué catálogo se está', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DE_CATALOGOS'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    buscarListaRegistros.mockResolvedValue({ data: { content: [] } });
    consultarCatalogosHijos.mockResolvedValue({ data: [] });
  });

  it('nombra los catálogos por encima y deja volver a ellos', async () => {
    const arbol: Record<string, unknown> = {
      'TIPO-DOC': { ...CATALOGO, name: 'Distrito', parent: 'DEPARTAMENTO' },
      DEPARTAMENTO: { ...CATALOGO, code: 'DEPARTAMENTO', name: 'Departamento', parent: 'REGION' },
      REGION: { ...CATALOGO, code: 'REGION', name: 'Región', parent: null },
    };
    consultarCatalogo.mockImplementation(({ code }: { code: string }) => Promise.resolve({ data: arbol[code] }));
    montarFicha();

    const miga = await screen.findByRole('navigation', { name: /migas|ruta/i });
    expect(miga).toHaveTextContent('Región');
    expect(miga).toHaveTextContent('Departamento');
    expect(miga).toHaveTextContent('Distrito');
    expect(within(miga).getByRole('link', { name: 'Departamento' })).toHaveAttribute(
      'href',
      '/catalogos-generales/DEPARTAMENTO',
    );
  });

  it('un catálogo sin padre sólo se nombra a sí mismo', async () => {
    consultarCatalogo.mockResolvedValue({ data: CATALOGO });
    montarFicha();
    const miga = await screen.findByRole('navigation', { name: /migas|ruta/i });
    expect(within(miga).queryAllByRole('link')).toHaveLength(1);
    expect(miga).toHaveTextContent('Tipos de documento');
  });
});

/**
 * Un catálogo lleva una sola clave (indicación del 30/09/2026). El contrato
 * admite más de una, pero con una queda satisfecho igual.
 */
describe('la clave del catálogo es una sola', () => {
  it('marcar una desmarca la anterior', async () => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DE_CATALOGOS'];
    buscarListaRegistros.mockResolvedValue({ data: { content: [] } });
    consultarCatalogosHijos.mockResolvedValue({ data: [] });
    consultarCatalogo.mockResolvedValue({ data: CATALOGO });
    montarFicha();

    const primera = await screen.findByLabelText('El campo 1 es clave');
    const segunda = screen.getByLabelText('El campo 2 es clave');
    expect(primera).toBeChecked();

    fireEvent.click(segunda);
    expect(segunda).toBeChecked();
    expect(primera).not.toBeChecked();
  });

  it('dos claves no se pueden guardar', () => {
    expect(problemaDeCampos([{ nombre: 'a', clave: true }, { nombre: 'b', clave: true }])).toBe('variasClaves');
    expect(problemaDeCampos([{ nombre: 'a', clave: true }, { nombre: 'b', clave: false }])).toBeNull();
    expect(problemaDeCampos([{ nombre: 'a', clave: false }])).toBe('sinClave');
  });
});


/**
 * Búsqueda por fracciones de palabra: "Cund" tiene que encontrar "Cundinamarca"
 * (indicación del 30/09/2026). El contrato no tiene parámetro de búsqueda, así
 * que se filtra sobre lo cargado y se avisa cuando no está todo.
 */
describe('búsqueda en catálogos y registros', () => {
  const DEPTOS = ['Cundinamarca', 'Boyacá', 'Antioquia', 'Santander'];

  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DE_CATALOGOS'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    consultarCatalogosHijos.mockResolvedValue({ data: [] });
    consultarCatalogo.mockResolvedValue({ data: CATALOGO });
    buscarListaRegistros.mockResolvedValue({
      data: {
        content: DEPTOS.map((d) => ({ key: d, values: { codigo: d, descripcion: `Departamento de ${d}` }, active: 'ACTIVE' })),
        totalElements: DEPTOS.length,
      },
    });
    listarCatalogos.mockResolvedValue({
      data: {
        content: [
          CATALOGO,
          { ...CATALOGO, code: 'DEPARTAMENTO', name: 'Departamentos', parent: 'REGION' },
          { ...CATALOGO, code: 'UNIDAD_MEDIDA', name: 'Unidades de medida', parent: null },
        ],
        totalPages: 1,
      },
    });
  });

  it('en un catálogo, "Cund" encuentra "Cundinamarca"', async () => {
    montarFicha();
    expect(await screen.findByText('Cundinamarca')).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText('Buscar registro'), { target: { value: 'Cund' } });

    expect(screen.getByText('Cundinamarca')).toBeInTheDocument();
    expect(screen.queryByText('Boyacá')).not.toBeInTheDocument();
    expect(screen.queryByText('Antioquia')).not.toBeInTheDocument();
  });

  it('no distingue tildes ni mayúsculas, y busca en cualquier columna', async () => {
    montarFicha();
    await screen.findByText('Boyacá');

    fireEvent.change(screen.getByLabelText('Buscar registro'), { target: { value: 'BOYACA' } });
    expect(screen.getByText('Boyacá')).toBeInTheDocument();

    // "Departamento de ..." sólo está en la segunda columna.
    fireEvent.change(screen.getByLabelText('Buscar registro'), { target: { value: 'departamento de anti' } });
    expect(screen.getByText('Antioquia')).toBeInTheDocument();
    expect(screen.queryByText('Boyacá')).not.toBeInTheDocument();
  });

  it('si no hay coincidencias lo dice, y al borrar vuelven todos', async () => {
    montarFicha();
    await screen.findByText('Cundinamarca');

    fireEvent.change(screen.getByLabelText('Buscar registro'), { target: { value: 'zzz' } });
    expect(screen.getByText(/Ningún registro contiene "zzz"/)).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText('Buscar registro'), { target: { value: '' } });
    expect(screen.getByText('Cundinamarca')).toBeInTheDocument();
  });

  // El contrato no deja buscar en el servidor: hay que decir que no se mira todo.
  it('avisa cuando el catálogo tiene más registros de los traídos', async () => {
    buscarListaRegistros.mockResolvedValue({
      data: { content: [{ key: 'A', values: { codigo: 'A' }, active: 'ACTIVE' }], totalElements: 5000 },
    });
    montarFicha();
    expect(await screen.findByText(/Se muestran 1 de 5000 registros/)).toBeInTheDocument();
  });

  it('la lista de catálogos también se busca', async () => {
    montarLista();
    expect(await screen.findByText('Departamentos')).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText('Buscar catálogo'), { target: { value: 'medida' } });
    expect(screen.getByText('Unidades de medida')).toBeInTheDocument();
    expect(screen.queryByText('Departamentos')).not.toBeInTheDocument();

    // También por código.
    fireEvent.change(screen.getByLabelText('Buscar catálogo'), { target: { value: 'DEPARTA' } });
    expect(screen.getByText('Departamentos')).toBeInTheDocument();
  });
});
