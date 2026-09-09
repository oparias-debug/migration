import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { BandejaPreinversionPage, rutaCaso } from './BandejaPreinversionPage';

const mocks = vi.hoisted(() => ({ activas: vi.fn(), archivadas: vi.fn(), asignar: vi.fn(), archivar: vi.fn(), tecnicos: vi.fn(), confirmar: vi.fn(), rol: 'COORDINADOR_PRE' }));
vi.mock('../../../api/bandejaPreinversionApi', () => ({
  bandejaApi: { listarSolicitudesActivas: mocks.activas, listarSolicitudesArchivadas: mocks.archivadas,
    asignarTecnicoPre: mocks.asignar, archivarSolicitud: mocks.archivar },
  tecnicosPreApi: { listarTecnicosPre: mocks.tecnicos },
}));
vi.mock('../../../components/ConfirmDialog', () => ({ confirmDialog: mocks.confirmar }));
vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: (rol: string) => rol === mocks.rol }) }));

const tecnico = { idUsuario: 9, nombreCompleto: 'Ana Pérez' };
const fila = { idSolicitud: 2, idProyecto: 7, unidadEjecutora: { nombre: 'MAG' }, tipoSolicitud: 'CUP',
  nombreProyecto: 'Puente Lempa', fechaSolicitud: '2026-09-07T02:00:00Z', estado: 'ENVIADO_DGICP_REGISTRO', asignadoA: null };
function respuesta(contenido: unknown[] = [fila]) {
  return { data: { contenido, paginacion: { pagina: 0, tamanio: 20, totalPaginas: 1, totalElementos: contenido.length },
    conteoPorTecnico: [{ tecnico, cantidadCup: 3, cantidadOpinionTecnica: 2 }] } };
}
function abrir(archivadas = false) { render(<MemoryRouter><BandejaPreinversionPage archivadas={archivadas} /></MemoryRouter>); }
beforeEach(() => {
  vi.resetAllMocks(); mocks.rol = 'COORDINADOR_PRE';
  mocks.activas.mockResolvedValue(respuesta()); mocks.tecnicos.mockResolvedValue({ data: [tecnico] });
  mocks.archivadas.mockResolvedValue(respuesta([{ ...fila, estado: undefined, estadoSolicitud: 'ARCHIVADA', fechaArchivo: '2026-09-07T12:00:00-06:00' }].map(({ estado, ...s }) => s)));
  mocks.confirmar.mockResolvedValue(true); mocks.asignar.mockResolvedValue({ data: fila }); mocks.archivar.mockResolvedValue({ data: {} });
});
describe('CU-PRE-02 Bandeja Preinversión', () => {
  it('muestra columnas, estados, fecha local y conteos separados', async () => {
    abrir(); await screen.findByText('Puente Lempa');
    for (const nombre of ['Unidad Ejecutora', 'CUP', 'Nombre del Proyecto', 'Fecha de Solicitud', 'Estado', 'Asignado a'])
      expect(screen.getAllByRole('columnheader', { name: nombre }).length).toBeGreaterThan(0);
    expect(screen.getByText('Enviado a DGICP')).toBeInTheDocument();
    expect(screen.getByText('6/9/2026')).toBeInTheDocument();
    const tabla = screen.getByRole('table', { name: 'Conteo de casos por Técnico PRE' });
    expect(within(tabla).getByText('3')).toBeInTheDocument(); expect(within(tabla).getByText('2')).toBeInTheDocument();
  });
  it.each(['CUP', 'OPINION_TECNICA'])('filtra por %s desde página cero', async tipo => {
    abrir(); await screen.findByText('Puente Lempa'); fireEvent.change(screen.getByLabelText('Tipo de solicitud'), { target: { value: tipo } });
    await waitFor(() => expect(mocks.activas).toHaveBeenLastCalledWith({ tipoSolicitud: tipo, pagina: 0, tamanio: 20 }));
  });
  it('asigna después de confirmar y recarga la bandeja', async () => {
    abrir(); await screen.findByRole('option', { name: 'Ana Pérez' });
    fireEvent.change(screen.getByLabelText('Asignado a Puente Lempa'), { target: { value: '9' } }); fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(mocks.asignar).toHaveBeenCalledWith({ idSolicitud: 2, asignacionTecnicoPreRequest: { idTecnicoAsignado: 9 } }));
    expect(mocks.confirmar).toHaveBeenCalledWith('¿Está seguro de asignar esta solicitud?', { confirmButtonText: 'Aceptar', cancelButtonText: 'Cancelar' });
    await screen.findByText('Asignación guardada.');
  });
  it('cancelar no envía cambios y restaura la selección', async () => {
    mocks.confirmar.mockResolvedValue(false); abrir(); await screen.findByRole('option', { name: 'Ana Pérez' });
    fireEvent.change(screen.getByLabelText('Asignado a Puente Lempa'), { target: { value: '9' } }); fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(screen.getByLabelText('Asignado a Puente Lempa')).toHaveValue(''));
    expect(mocks.asignar).not.toHaveBeenCalled();
  });
  it('reasigna un caso observado', async () => {
    mocks.activas.mockResolvedValue(respuesta([{ ...fila, estado: 'OBSERVADO_DGICP_REGISTRO', asignadoA: { idUsuario: 8, nombreCompleto: 'Otro técnico' } }]));
    abrir(); await screen.findByRole('option', { name: 'Ana Pérez' }); expect(screen.getByText('Observado DGICP')).toBeInTheDocument();
    fireEvent.change(screen.getByLabelText('Asignado a Puente Lempa'), { target: { value: '9' } }); fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(mocks.asignar).toHaveBeenCalledTimes(1));
  });
  it('archiva tras confirmar y retira la fila de activas', async () => {
    abrir(); await screen.findByText('Puente Lempa'); mocks.activas.mockResolvedValue(respuesta([]));
    fireEvent.click(screen.getByRole('button', { name: 'Archivar Puente Lempa' }));
    await screen.findByText('Solicitud archivada.'); await screen.findByText('No hay solicitudes para mostrar.');
    expect(mocks.archivar).toHaveBeenCalledWith({ idSolicitud: 2 });
  });
  it('cancelar archivo conserva la fila', async () => {
    mocks.confirmar.mockResolvedValue(false); abrir(); await screen.findByText('Puente Lempa');
    fireEvent.click(screen.getByRole('button', { name: 'Archivar Puente Lempa' }));
    await waitFor(() => expect(screen.getByRole('button', { name: 'Archivar Puente Lempa' })).toBeEnabled());
    expect(mocks.archivar).not.toHaveBeenCalled(); expect(screen.getByText('Puente Lempa')).toBeInTheDocument();
  });
  it('muestra archivadas con fecha de archivo y sin asignación', async () => {
    abrir(true); await screen.findByText('Puente Lempa'); expect(screen.getByText('Archivado')).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: 'Fecha de Archivo' })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });
  it.each(['CUP', 'OPINION_TECNICA'] as const)('Técnico PRE abre %s sin acciones de coordinación', async tipoSolicitud => {
    mocks.rol = 'TECNICO_PRE'; mocks.activas.mockResolvedValue(respuesta([{ ...fila, tipoSolicitud, asignadoA: tecnico }]));
    abrir(); const enlace = await screen.findByRole('link', { name: 'Puente Lempa' });
    expect(enlace).toHaveAttribute('href', rutaCaso({ tipoSolicitud, idProyecto: 7 }));
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument(); expect(mocks.tecnicos).not.toHaveBeenCalled();
  });
  it('deniega reporte archivado al técnico sin consultar API', () => {
    mocks.rol = 'TECNICO_PRE'; abrir(true); expect(screen.getByRole('alert')).toHaveTextContent('No tiene permiso'); expect(mocks.archivadas).not.toHaveBeenCalled();
  });
  it('distingue error de lista vacía y permite reintentar', async () => {
    mocks.activas.mockRejectedValueOnce(new Error('500')); abrir(); await screen.findByRole('alert');
    expect(screen.queryByText('No hay solicitudes para mostrar.')).not.toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Reintentar' })); await screen.findByText('Puente Lempa');
  });
});
