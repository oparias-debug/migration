import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { CatalogosPage } from './CatalogosPage';
import { CatalogoDetallePage } from './CatalogoDetallePage';
import { problemaDeCampos } from './CamposEditor';

const buscarListarCatalogos = vi.fn();
const crearCatalogo = vi.fn();
const consultarCatalogo = vi.fn();
const actualizarDescriptores = vi.fn();
const consultarCatalogoHijo = vi.fn();
const listarRegistros = vi.fn();
const crearRegistro = vi.fn();
const actualizarRegistro = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/administracionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/administracionApi')>();
  return {
    ...actual,
    catalogosApi: {
      buscarListarCatalogos: (...a: unknown[]) => buscarListarCatalogos(...a),
      crearCatalogo: (...a: unknown[]) => crearCatalogo(...a),
      consultarCatalogo: (...a: unknown[]) => consultarCatalogo(...a),
      actualizarDescriptores: (...a: unknown[]) => actualizarDescriptores(...a),
      consultarCatalogoHijo: (...a: unknown[]) => consultarCatalogoHijo(...a),
    },
    registrosCatalogoApi: {
      listarRegistros: (...a: unknown[]) => listarRegistros(...a),
      crearRegistro: (...a: unknown[]) => crearRegistro(...a),
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
  codigo: 'TIPO-DOC',
  nombre: 'Tipos de documento',
  padre: null,
  hijo: null,
  estado: 'ACTIVE',
  vigencia: { desde: null, hasta: null },
  campos: [
    { nombre: 'descripcion', calificador: 'FIELD', posicion: 2 },
    { nombre: 'codigo', calificador: 'KEY', posicion: 1 },
  ],
};

/**
 * El listado de registros llega como tabla: `fieldSet` con los nombres de
 * columna y `resultSet` con una fila de valores en ese mismo orden. La clave no
 * viene aparte: va en la columna del campo KEY, que el servidor siempre incluye.
 */
const comoTabla = (codigoCatalogo: string, fieldSet: readonly string[], filas: readonly (readonly string[])[]) => ({
  data: {
    codigoCatalogo,
    fieldSet: [...fieldSet],
    resultSet: filas.map((valores) => ({ valores: [...valores], estado: 'ACTIVE' })),
  },
});

/** Las dos columnas del catálogo de ejemplo, en el orden en que las pide la ficha. */
const COLUMNAS = ['codigo', 'descripcion'] as const;

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
    consultarCatalogo.mockResolvedValue({ data: CATALOGO });
    listarRegistros.mockResolvedValue(comoTabla('TIPO-DOC', COLUMNAS, [['DUI', 'Documento Único']]));
    // La misma operación sirve para listar y para comprobar si un nombre ya está
    // en uso: con `nombre`, una lista vacía significa que no existe.
    buscarListarCatalogos.mockImplementation((peticion?: { nombre?: string }) =>
      Promise.resolve({ data: peticion?.nombre ? [] : [CATALOGO] }),
    );
    consultarCatalogoHijo.mockResolvedValue({ data: null });
  });

  it('sin el rol del CU no muestra nada del catálogo', () => {
    rolesActivos = ['ADMINISTRADOR'];
    montarLista();
    expect(screen.getByRole('alert')).toHaveTextContent(/Administrador de Catálogos/);
    expect(buscarListarCatalogos).not.toHaveBeenCalled();
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
    expect(crearCatalogo.mock.calls[0][0].catalogoCreacion.campos).toEqual([
      { nombre: 'codigo', calificador: 'KEY', posicion: 1 },
      { nombre: 'descripcion', calificador: 'FIELD', posicion: 2 },
    ]);
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/catalogos-generales/TIPO-DOC'));
  });

  it('avisa si el nombre ya existe', async () => {
    // Con `nombre`, una lista no vacía significa que ese nombre ya está tomado.
    buscarListarCatalogos.mockResolvedValue({ data: [CATALOGO] });
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
    crearRegistro.mockResolvedValue({ data: {} });
    montarFicha();
    await screen.findByText('Documento Único');
    fireEvent.change(screen.getByLabelText('codigo*'), { target: { value: 'NIT' } });
    fireEvent.change(screen.getByLabelText('descripcion'), { target: { value: 'Tributario' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar registro' }));

    await waitFor(() =>
      expect(crearRegistro).toHaveBeenCalledWith({
        codigo: 'TIPO-DOC',
        registroCreacion: { valores: { codigo: 'NIT', descripcion: 'Tributario' } },
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
        codigo: 'TIPO-DOC',
        llave: 'DUI',
        requestBody: { descripcion: 'Documento Único de Identidad' },
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
    expect(crearRegistro).not.toHaveBeenCalled();
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
  const PADRE = { ...CATALOGO, codigo: 'PRUEBA', nombre: 'Catálogo de prueba' };
  const HIJO = { codigo: 'JERARQUICO', nombre: 'Catálogo jerárquico' };

  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DE_CATALOGOS'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    listarRegistros.mockResolvedValue(comoTabla('TIPO-DOC', COLUMNAS, []));
    consultarCatalogoHijo.mockResolvedValue({ data: null });
    consultarCatalogo.mockResolvedValue({ data: PADRE });
    buscarListarCatalogos.mockResolvedValue({ data: [PADRE] });
  });

  it('la ficha del padre lista sus hijos y los abre', async () => {
    consultarCatalogoHijo.mockResolvedValue({ data: HIJO });
    montarFicha();

    expect(await screen.findByText('Catálogo jerárquico')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Abrir el catálogo Catálogo jerárquico' }));
    expect(navigate).toHaveBeenCalledWith('/catalogos-generales/JERARQUICO');
  });

  it('un catálogo sin hijos lo dice', async () => {
    montarFicha();
    expect(await screen.findByText('Este catálogo no tiene catálogo hijo.')).toBeInTheDocument();
  });

  // La jerarquía se recorre en los dos sentidos.
  it('desde el hijo se sube al padre', async () => {
    consultarCatalogo.mockResolvedValue({ data: { ...CATALOGO, codigo: 'JERARQUICO', padre: 'PRUEBA' } });
    montarFicha();

    fireEvent.click(await screen.findByRole('button', { name: 'Abrir el catálogo padre PRUEBA' }));
    expect(navigate).toHaveBeenCalledWith('/catalogos-generales/PRUEBA');
  });

  it('sin padre no se ofrece subir', async () => {
    montarFicha();
    await screen.findByText('Este catálogo no tiene catálogo hijo.');
    expect(screen.queryByRole('button', { name: /Abrir el catálogo padre/ })).not.toBeInTheDocument();
  });

  it('la lista muestra de quién cuelga cada catálogo', async () => {
    buscarListarCatalogos.mockResolvedValue({
      data: [PADRE, { ...CATALOGO, codigo: 'JERARQUICO', nombre: 'Catálogo jerárquico', padre: 'PRUEBA' }],
    });
    montarLista();

    const fila = (await screen.findByText('Catálogo jerárquico')).closest('tr') as HTMLElement;
    expect(fila).toHaveTextContent('PRUEBA');
  });

  // Los hijos son contexto: si no cargan, la ficha sigue sirviendo.
  it('si los hijos no cargan, la ficha funciona igual', async () => {
    consultarCatalogoHijo.mockRejectedValue(new Error('falla'));
    montarFicha();
    expect(await screen.findByText('Este catálogo no tiene catálogo hijo.')).toBeInTheDocument();
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
    listarRegistros.mockResolvedValue(comoTabla('TIPO-DOC', COLUMNAS, []));
    consultarCatalogoHijo.mockResolvedValue({ data: null });
    consultarCatalogo.mockResolvedValue({ data: { ...CATALOGO, codigo: 'JERARQUICO', padre: 'PRUEBA' } });
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

    actualizarDescriptores.mockResolvedValue({ data: {} });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar datos' }));
    await waitFor(() => expect(actualizarDescriptores).toHaveBeenCalled());
    const enviado = actualizarDescriptores.mock.calls[0][0].catalogoDescriptores;
    expect(enviado).not.toHaveProperty('codigo');
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
    listarRegistros.mockResolvedValue(comoTabla('TIPO-DOC', COLUMNAS, []));
    consultarCatalogoHijo.mockResolvedValue({ data: null });
  });

  it('nombra los catálogos por encima y deja volver a ellos', async () => {
    const arbol: Record<string, unknown> = {
      'TIPO-DOC': { ...CATALOGO, nombre: 'Distrito', padre: 'DEPARTAMENTO' },
      DEPARTAMENTO: { ...CATALOGO, codigo: 'DEPARTAMENTO', nombre: 'Departamento', padre: 'REGION' },
      REGION: { ...CATALOGO, codigo: 'REGION', nombre: 'Región', padre: null },
    };
    consultarCatalogo.mockImplementation(({ codigo }: { codigo: string }) => Promise.resolve({ data: arbol[codigo] }));
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
    listarRegistros.mockResolvedValue(comoTabla('TIPO-DOC', COLUMNAS, []));
    consultarCatalogoHijo.mockResolvedValue({ data: null });
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
    consultarCatalogoHijo.mockResolvedValue({ data: null });
    consultarCatalogo.mockResolvedValue({ data: CATALOGO });
    listarRegistros.mockResolvedValue(
      comoTabla(
        'TIPO-DOC',
        COLUMNAS,
        DEPTOS.map((d) => [d, `Departamento de ${d}`]),
      ),
    );
    buscarListarCatalogos.mockResolvedValue({
      data: [
        CATALOGO,
        { ...CATALOGO, codigo: 'DEPARTAMENTO', nombre: 'Departamentos', padre: 'REGION' },
        { ...CATALOGO, codigo: 'UNIDAD_MEDIDA', nombre: 'Unidades de medida', padre: null },
      ],
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


/**
 * Punto 6 del 30/09/2026: al crear un registro en un catálogo jerárquico hay que
 * poder decir de qué registro del padre cuelga, eligiéndolo de la lista o
 * buscándolo. El ejemplo del cliente: región → departamento → distrito; al dar
 * de alta Girardot se elige Cundinamarca como padre.
 */
describe('registro padre al crear en un catálogo jerárquico', () => {
  const DISTRITO = {
    ...CATALOGO,
    codigo: 'DISTRITO',
    nombre: 'Distritos',
    padre: 'DEPARTAMENTO',
    campos: [
      { nombre: 'codigo', calificador: 'KEY', posicion: 1 },
      { nombre: 'descripcion', calificador: 'FIELD', posicion: 2 },
    ],
  };
  const DEPTOS = ['Cundinamarca', 'Boyacá', 'Antioquia'];

  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR_DE_CATALOGOS'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    consultarCatalogoHijo.mockResolvedValue({ data: null });
    consultarCatalogo.mockResolvedValue({ data: DISTRITO });
    crearRegistro.mockResolvedValue({ data: {} });
    listarRegistros.mockImplementation(({ codigo }: { codigo: string }) =>
      Promise.resolve(
        codigo === 'DEPARTAMENTO'
          ? comoTabla(
              'DEPARTAMENTO',
              COLUMNAS,
              DEPTOS.map((d) => [d.slice(0, 3).toUpperCase(), d]),
            )
          : comoTabla(codigo, COLUMNAS, []),
      ),
    );
  });

  it('ofrece los registros del catálogo padre', async () => {
    montarFicha();
    const padre = (await screen.findByLabelText(/Registro padre en DEPARTAMENTO/)) as HTMLSelectElement;
    await waitFor(() => expect(within(padre).getByRole('option', { name: /Cundinamarca/ })).toBeInTheDocument());
    expect(within(padre).getByRole('option', { name: /Boyacá/ })).toBeInTheDocument();
  });

  it('se puede acotar la lista buscando', async () => {
    montarFicha();
    const inicial = (await screen.findByLabelText(/Registro padre en DEPARTAMENTO/)) as HTMLSelectElement;
    await waitFor(() => expect(within(inicial).getByRole('option', { name: /Boyacá/ })).toBeInTheDocument());

    fireEvent.change(screen.getByLabelText('Buscar el registro padre'), { target: { value: 'cund' } });

    const padre = screen.getByLabelText(/Registro padre en DEPARTAMENTO/) as HTMLSelectElement;
    expect(within(padre).getByRole('option', { name: /Cundinamarca/ })).toBeInTheDocument();
    expect(within(padre).queryByRole('option', { name: /Boyacá/ })).not.toBeInTheDocument();
  });

  it('crear el distrito manda el departamento elegido como registro padre', async () => {
    montarFicha();
    // El desplegable se pinta antes de que lleguen los registros del padre.
    const padre = (await screen.findByLabelText(/Registro padre en DEPARTAMENTO/)) as HTMLSelectElement;
    await waitFor(() => expect(within(padre).getByRole('option', { name: /Cundinamarca/ })).toBeInTheDocument());

    fireEvent.change(padre, { target: { value: 'CUN' } });
    fireEvent.change(screen.getByLabelText('codigo*'), { target: { value: 'GIR' } });
    fireEvent.change(screen.getByLabelText('descripcion'), { target: { value: 'Girardot' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar registro' }));


    await waitFor(() =>
      expect(crearRegistro).toHaveBeenCalledWith({
        // El catálogo sale de la URL de la ficha, no del objeto.
        codigo: 'TIPO-DOC',
        registroCreacion: {
          registroPadre: 'CUN',
          valores: { codigo: 'GIR', descripcion: 'Girardot' },
        },
      }),
    );
  });

  // El contrato lo exige cuando el catálogo cuelga de otro (Reglas 8 y 23).
  it('sin elegir padre no se manda nada', async () => {
    montarFicha();
    await screen.findByLabelText(/Registro padre en DEPARTAMENTO/);

    fireEvent.change(screen.getByLabelText('codigo*'), { target: { value: 'GIR' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar registro' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' })));
    expect(crearRegistro).not.toHaveBeenCalled();
  });

  /**
   * La tabla llevaba una columna con el registro padre de cada fila. Desde el
   * contrato del 05/10/2026 ninguna operación de lectura devuelve el
   * `registroPadre` de un registro: ni `listarRegistros`, ni
   * `buscarRegistroPorLlave`; sólo lo traen las respuestas de crear, actualizar
   * y cambiar estado. Mientras siga así no hay de dónde sacarlo para pintarlo, y
   * la columna no se muestra. Se elige igual al crear, que es lo que el contrato
   * sí admite, y eso lo cubren las pruebas de arriba.
   */
  it('la tabla no muestra el registro padre, porque el contrato ya no lo devuelve al leer', async () => {
    const departamentos = listarRegistros.getMockImplementation()!;
    listarRegistros.mockImplementation((peticion: { codigo: string }) =>
      peticion.codigo === 'TIPO-DOC'
        ? Promise.resolve(comoTabla('TIPO-DOC', COLUMNAS, [['GIR', 'Girardot']]))
        : departamentos(peticion),
    );
    montarFicha();

    expect(await screen.findByRole('cell', { name: 'Girardot' })).toBeInTheDocument();
    expect(screen.queryByRole('columnheader', { name: /Registro padre en/ })).not.toBeInTheDocument();
  });

  it('un catálogo sin padre no pide registro padre', async () => {
    consultarCatalogo.mockResolvedValue({ data: { ...CATALOGO, padre: null } });
    montarFicha();
    await screen.findByRole('button', { name: 'Guardar registro' });
    expect(screen.queryByLabelText(/Registro padre/)).not.toBeInTheDocument();
  });
});
