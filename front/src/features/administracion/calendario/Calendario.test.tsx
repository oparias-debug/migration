import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { CalendarioPage } from './CalendarioPage';
import { CalendarioDetallePage } from './CalendarioDetallePage';

const listarCalendarios = vi.fn();
const recuperarDefinicionCalendario = vi.fn();
const crearCalendario = vi.fn();
const agregarPeriodoLaboral = vi.fn();
const agregarPeriodoNoLaboral = vi.fn();
const registrarExcepcion = vi.fn();
const cambiarEstadoCalendario = vi.fn();
const editarDefinicionCalendario = vi.fn();
const consultarTipoDia = vi.fn();
const consultarDuracionPeriodo = vi.fn();
const consultarPertenenciaPeriodo = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/administracionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/administracionApi')>();
  return {
    ...actual,
    calendariosApi: {
      crearCalendario: (...a: unknown[]) => crearCalendario(...a),
      agregarPeriodoLaboral: (...a: unknown[]) => agregarPeriodoLaboral(...a),
      agregarPeriodoNoLaboral: (...a: unknown[]) => agregarPeriodoNoLaboral(...a),
      registrarExcepcion: (...a: unknown[]) => registrarExcepcion(...a),
      cambiarEstadoCalendario: (...a: unknown[]) => cambiarEstadoCalendario(...a),
      editarDefinicionCalendario: (...a: unknown[]) => editarDefinicionCalendario(...a),
    },
    consultasCalendarioApi: {
      listarCalendarios: (...a: unknown[]) => listarCalendarios(...a),
      recuperarDefinicionCalendario: (...a: unknown[]) => recuperarDefinicionCalendario(...a),
      consultarTipoDia: (...a: unknown[]) => consultarTipoDia(...a),
      consultarDuracionPeriodo: (...a: unknown[]) => consultarDuracionPeriodo(...a),
      consultarPertenenciaPeriodo: (...a: unknown[]) => consultarPertenenciaPeriodo(...a),
    },
  };
});

let rolesActivos: string[] = ['ADMINISTRADOR'];
vi.mock('../../../auth/useAuth', () => ({
  useAuth: () => ({ hasRole: (rol: string) => rolesActivos.includes(rol) }),
}));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const CALENDARIO = {
  id: 1,
  codigo: 'CAL-2026',
  nombre: 'Calendario 2026',
  descripcion: 'Del año',
  fechaInicio: '2026-01-01',
  fechaFin: '2026-12-31',
  estado: 'ACTIVO',
  items: [
    { tipoItem: 'NO_LABORAL', id: 10, codigo: 'FINDE', nombre: 'Fines de semana', estado: 'ACTIVO',
      recurrencia: { tipo: 'SEMANAL', fechaInicio: '2026-01-01', fechaFin: '2026-12-31', diasSemana: ['SATURDAY', 'SUNDAY'] } },
    { tipoItem: 'EXCEPCION', id: 11, fecha: '2026-12-25', tipo: 'DIA_NO_LABORAL', descripcion: 'Navidad', estado: 'ACTIVO' },
  ],
};

const montarLista = () => render(<MemoryRouter><CalendarioPage /></MemoryRouter>);
const montarFicha = () =>
  render(
    <MemoryRouter initialEntries={['/administracion/calendario/CAL-2026']}>
      <Routes>
        <Route path="/administracion/calendario/:codigo" element={<CalendarioDetallePage />} />
      </Routes>
    </MemoryRouter>,
  );

describe('CU-ADM-04 · calendario', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['ADMINISTRADOR'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    listarCalendarios.mockResolvedValue({ data: [{ codigo: 'CAL-2026', nombre: 'Calendario 2026', estado: 'ACTIVO' }] });
    recuperarDefinicionCalendario.mockResolvedValue({ data: CALENDARIO });
  });

  // RN22/RN18: la lista y las consultas están abiertas a cualquier usuario;
  // crear un calendario sigue siendo de los dos roles de administración (RN12).
  it('sin rol de administración lista los calendarios pero no ofrece crear', async () => {
    rolesActivos = ['TECNICO_URP'];
    montarLista();
    expect(await screen.findByText('Calendario 2026')).toBeInTheDocument();
    expect(listarCalendarios).toHaveBeenCalled();
    expect(screen.queryByRole('button', { name: 'Nuevo calendario' })).not.toBeInTheDocument();
  });

  it('lista los calendarios y abre uno', async () => {
    montarLista();
    expect(await screen.findByText('Calendario 2026')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Abrir el calendario Calendario 2026' }));
    expect(navigate).toHaveBeenCalledWith('/administracion/calendario/CAL-2026');
  });

  it('muestra la definición con su repetición en texto', async () => {
    montarFicha();
    expect(await screen.findByText('Fines de semana')).toBeInTheDocument();
    expect(screen.getByText('Cada semana: Sábado, Domingo')).toBeInTheDocument();
    expect(screen.getByText('Navidad')).toBeInTheDocument();
  });

  // El cuerpo de estos endpoints es PeriodoInput: sin tipoItem, que lo decide el endpoint.
  it('agrega un período semanal sin mandar tipoItem', async () => {
    agregarPeriodoNoLaboral.mockResolvedValue({ data: {} });
    montarFicha();
    await screen.findByText('Fines de semana');
    fireEvent.change(screen.getByLabelText('Código del período*'), { target: { value: 'FERIADOS' } });
    fireEvent.change(screen.getByLabelText('Nombre del período*'), { target: { value: 'Feriados' } });
    fireEvent.change(screen.getByLabelText('Se repite*'), { target: { value: 'SEMANAL' } });
    fireEvent.click(await screen.findByLabelText('Lunes'));
    fireEvent.change(screen.getByLabelText('Fecha de inicio*'), { target: { value: '2026-01-01' } });
    fireEvent.change(screen.getByLabelText('Fecha de fin*'), { target: { value: '2026-12-31' } });
    fireEvent.click(screen.getByRole('button', { name: 'Agregar período' }));

    await waitFor(() => expect(agregarPeriodoNoLaboral).toHaveBeenCalled());
    const enviado = agregarPeriodoNoLaboral.mock.calls[0][0];
    expect(enviado.codigoCalendario).toBe('CAL-2026');
    expect(enviado.periodoInput).toEqual({
      codigo: 'FERIADOS',
      nombre: 'Feriados',
      recurrencia: { tipo: 'SEMANAL', fechaInicio: '2026-01-01', fechaFin: '2026-12-31', diasSemana: ['MONDAY'] },
    });
  });

  // Si el formulario no se limpia, el siguiente período hereda los días del anterior.
  it('limpia el formulario tras guardar un período', async () => {
    agregarPeriodoNoLaboral.mockResolvedValue({ data: {} });
    montarFicha();
    await screen.findByText('Fines de semana');
    fireEvent.change(screen.getByLabelText('Código del período*'), { target: { value: 'FERIADOS' } });
    fireEvent.change(screen.getByLabelText('Nombre del período*'), { target: { value: 'Feriados' } });
    fireEvent.change(screen.getByLabelText('Se repite*'), { target: { value: 'SEMANAL' } });
    fireEvent.click(await screen.findByLabelText('Sábado'));
    fireEvent.click(screen.getByRole('button', { name: 'Agregar período' }));

    await waitFor(() => expect(agregarPeriodoNoLaboral).toHaveBeenCalled());
    await waitFor(() => expect(screen.getByLabelText('Código del período*')).toHaveValue(''));
    expect(screen.getByLabelText('Sábado')).not.toBeChecked();
  });

  it('registra una excepción', async () => {
    registrarExcepcion.mockResolvedValue({ data: {} });
    montarFicha();
    await screen.findByText('Fines de semana');
    // Los diálogos van en pestañas: primero se abre el de excepciones.
    fireEvent.click(screen.getByRole('tab', { name: 'Registrar excepción' }));
    fireEvent.change(screen.getByLabelText('Fecha*'), { target: { value: '2026-05-01' } });
    fireEvent.change(screen.getByLabelText('Descripción', { selector: '#exc-descripcion' }), { target: { value: 'Día del trabajo' } });
    fireEvent.click(screen.getByRole('button', { name: 'Registrar excepción' }));

    await waitFor(() =>
      expect(registrarExcepcion).toHaveBeenCalledWith({
        codigoCalendario: 'CAL-2026',
        registrarExcepcionRequest: { fecha: '2026-05-01', tipo: 'DIA_NO_LABORAL', descripcion: 'Día del trabajo' },
      }),
    );
  });

  // La definición se guarda entera: quitar un elemento manda la lista sin él.
  it('quitar un elemento reenvía la definición sin ese elemento', async () => {
    editarDefinicionCalendario.mockResolvedValue({ data: {} });
    montarFicha();
    fireEvent.click(await screen.findByRole('button', { name: 'Quitar FINDE de la definición' }));

    await waitFor(() => expect(editarDefinicionCalendario).toHaveBeenCalled());
    const items = editarDefinicionCalendario.mock.calls[0][0].editarDefinicionCalendarioRequest.items;
    expect(items).toHaveLength(1);
    expect(items[0].tipoItem).toBe('EXCEPCION');
  });

  it('cambia el estado del calendario', async () => {
    cambiarEstadoCalendario.mockResolvedValue({ data: {} });
    montarFicha();
    fireEvent.click(await screen.findByRole('button', { name: 'Inactivar' }));
    await waitFor(() =>
      expect(cambiarEstadoCalendario).toHaveBeenCalledWith({
        codigoCalendario: 'CAL-2026',
        cambiarEstadoCalendarioRequest: { estado: 'INACTIVO' },
      }),
    );
  });

  it('comprueba el tipo de día leyendo el campo tipo de la respuesta', async () => {
    consultarTipoDia.mockResolvedValue({ data: { fecha: '2026-12-25', tipo: 'NO_LABORAL' } });
    montarFicha();
    await screen.findByText('Fines de semana');
    fireEvent.click(screen.getByRole('tab', { name: 'Comprobar' }));
    fireEvent.change(screen.getByLabelText('Fecha', { selector: '#con-fecha' }), { target: { value: '2026-12-25' } });
    fireEvent.click(screen.getByRole('button', { name: '¿Qué tipo de día es?' }));
    expect(await screen.findByText('Ese día es: no laboral')).toBeInTheDocument();
  });

  // HU-ADM-04-22: editar un CalendarItem es mandar la definición entera con ese
  // ítem cambiado; el `id` es lo que distingue una edición de un alta.
  it('editar un período manda la definición completa conservando su id', async () => {
    editarDefinicionCalendario.mockResolvedValue({ data: {} });
    montarFicha();
    await screen.findByText('Fines de semana');
    fireEvent.click(screen.getByRole('button', { name: 'Editar FINDE' }));
    fireEvent.change(screen.getByLabelText('Nombre del período*'), { target: { value: 'Sábados y domingos' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar cambios' }));

    await waitFor(() => expect(editarDefinicionCalendario).toHaveBeenCalled());
    const enviado = editarDefinicionCalendario.mock.calls[0][0].editarDefinicionCalendarioRequest.items;
    expect(enviado).toHaveLength(2);
    expect(enviado[0]).toMatchObject({ id: 10, codigo: 'FINDE', nombre: 'Sábados y domingos' });
    // El resto de la definición viaja igual, y sin `estado`: lo hereda del calendario (RN20).
    expect(enviado[1]).toMatchObject({ id: 11, tipoItem: 'EXCEPCION' });
    expect(enviado[1].estado).toBeUndefined();
  });

  it('editar una excepción conserva su id y cambia sus datos', async () => {
    editarDefinicionCalendario.mockResolvedValue({ data: {} });
    montarFicha();
    await screen.findByText('Navidad');
    fireEvent.click(screen.getByRole('button', { name: 'Editar 2026-12-25' }));
    fireEvent.change(screen.getByLabelText('Descripción', { selector: '#exc-descripcion' }), {
      target: { value: 'Navidad (feriado nacional)' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar cambios' }));

    await waitFor(() => expect(editarDefinicionCalendario).toHaveBeenCalled());
    const enviado = editarDefinicionCalendario.mock.calls[0][0].editarDefinicionCalendarioRequest.items;
    expect(enviado.find((i: { id: number }) => i.id === 11)).toMatchObject({
      tipoItem: 'EXCEPCION',
      fecha: '2026-12-25',
      descripcion: 'Navidad (feriado nacional)',
    });
  });

  // Las consultas de los flujos 7.2 y 7.3 del CU.
  it('consulta la duración de un período elegido de la lista', async () => {
    consultarDuracionPeriodo.mockResolvedValue({ data: { duracionDias: 104 } });
    montarFicha();
    await screen.findByText('Fines de semana');
    fireEvent.click(screen.getByRole('tab', { name: 'Comprobar' }));
    fireEvent.change(screen.getByLabelText('Período'), { target: { value: 'FINDE' } });
    fireEvent.click(screen.getByRole('button', { name: '¿Cuánto dura el período?' }));

    await waitFor(() =>
      expect(consultarDuracionPeriodo).toHaveBeenCalledWith({ codigoCalendario: 'CAL-2026', codigoPeriodo: 'FINDE' }),
    );
    expect(await screen.findByText('El período dura 104 días.')).toBeInTheDocument();
  });

  it('una consulta que el back rechaza muestra su error, no un resultado', async () => {
    consultarPertenenciaPeriodo.mockRejectedValue(new Error('falla'));
    montarFicha();
    await screen.findByText('Fines de semana');
    fireEvent.click(screen.getByRole('tab', { name: 'Comprobar' }));
    fireEvent.change(screen.getByLabelText('Fecha', { selector: '#con-fecha' }), { target: { value: '2026-12-25' } });
    fireEvent.change(screen.getByLabelText('Período'), { target: { value: 'FINDE' } });
    fireEvent.click(screen.getByRole('button', { name: '¿La fecha está en ese período?' }));

    const aviso = await screen.findByText(/.+/, { selector: '.aviso-error' });
    expect(aviso).toBeInTheDocument();
  });

  // El alta de un calendario (CU-ADM-04, HU-01): sin ella no hay nada que probar.
  it('crea un calendario con su código, nombre y rango de fechas', async () => {
    crearCalendario.mockResolvedValue({ data: { codigo: 'INVERSION_2028' } });
    montarLista();

    fireEvent.click(await screen.findByRole('button', { name: 'Nuevo calendario' }));
    fireEvent.change(screen.getByLabelText(/Código/i), { target: { value: 'INVERSION_2028' } });
    fireEvent.change(screen.getByLabelText(/^Nombre/i), { target: { value: 'Calendario inversión 2028' } });
    fireEvent.change(screen.getByLabelText(/Fecha de inicio/i), { target: { value: '2028-01-01' } });
    fireEvent.change(screen.getByLabelText(/Fecha de fin/i), { target: { value: '2028-12-31' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear calendario' }));

    await waitFor(() => expect(crearCalendario).toHaveBeenCalled());
    const enviado = crearCalendario.mock.calls[0][0].crearCalendarioRequest;
    expect(enviado).toMatchObject({
      codigo: 'INVERSION_2028',
      nombre: 'Calendario inversión 2028',
      fechaInicio: '2028-01-01',
      fechaFin: '2028-12-31',
    });
    // Creado el calendario, se entra a su ficha para definir los períodos.
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/administracion/calendario/INVERSION_2028'));
  });

  // La rejilla y la ficha del día son lo que Álvaro pidió ver: se comprueban
  // juntas porque la ficha depende de qué día se pulse.
  it('pinta la rejilla y explica por qué un día quedó no laboral', async () => {
    montarFicha();
    await screen.findByText('Fines de semana');

    // Diciembre de 2026: el 25 tiene excepción y los fines de semana son rojos.
    expect(document.querySelector('.cal-rejilla')).toBeInTheDocument();
    expect(document.querySelectorAll('.cal-rejilla td.es-no-laboral').length).toBeGreaterThan(0);

    const dia = document.querySelector('.cal-rejilla td.es-no-laboral .dia') as HTMLButtonElement;
    fireEvent.click(dia);

    const ficha = document.querySelector('.cal-dia') as HTMLElement;
    expect(ficha).toBeInTheDocument();
    expect(ficha.textContent).toContain('Día no laboral');
    // Y dice qué período lo cubre, que es la explicación que pedía el cliente.
    expect(ficha.querySelector('.cal-porque')?.textContent).toContain('Fines de semana');
  });
});
