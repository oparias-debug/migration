import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter } from 'react-router-dom';
import i18n from '../../../i18n/i18n';

const listarSolicitudesActivas = vi.fn();
const listarSolicitudesArchivadas = vi.fn();
const asignarTecnicoPre = vi.fn();
const archivarSolicitud = vi.fn();
const listarTecnicosPre = vi.fn();
const swalFire = vi.fn().mockResolvedValue({ isConfirmed: true });
let esCoordinador = true;

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  bandejaApi: {
    listarSolicitudesActivas: (...a: unknown[]) => listarSolicitudesActivas(...a),
    listarSolicitudesArchivadas: (...a: unknown[]) => listarSolicitudesArchivadas(...a),
    asignarTecnicoPre: (...a: unknown[]) => asignarTecnicoPre(...a),
    archivarSolicitud: (...a: unknown[]) => archivarSolicitud(...a),
  },
  catalogoBandejaApi: { listarTecnicosPre: () => listarTecnicosPre() },
}));
vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: () => esCoordinador }) }));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const { BandejaPage } = await import('./BandejaPage');

const UNA = {
  idSolicitud: 1, idProyecto: 101,
  unidadEjecutora: { idUnidadEjecutora: 4501, nombre: 'MINSAL' },
  tipoSolicitud: 'CUP', cup: null,
  nombreProyecto: 'Equipamiento del hospital regional de Santa Ana',
  fechaSolicitud: '2026-08-18', estado: 'Enviado a DGICP', asignadoA: null,
};
const RESPUESTA = {
  data: {
    contenido: [UNA],
    paginacion: { pagina: 0, tamanio: 20, totalElementos: 1, totalPaginas: 1 },
    conteoPorTecnico: [
      { tecnico: { idUsuario: 11, nombreCompleto: 'Raquel González' }, cantidadCup: 2, cantidadOpinionTecnica: 1 },
    ],
  },
};

const montar = () => render(<MemoryRouter><BandejaPage /></MemoryRouter>);

describe('BandejaPage', () => {
  beforeEach(() => {
    esCoordinador = true;
    listarSolicitudesActivas.mockReset().mockResolvedValue(RESPUESTA);
    listarSolicitudesArchivadas.mockReset().mockResolvedValue({
      data: { contenido: [], paginacion: { pagina: 0, tamanio: 20, totalElementos: 0, totalPaginas: 0 } },
    });
    asignarTecnicoPre.mockReset().mockResolvedValue({});
    archivarSolicitud.mockReset().mockResolvedValue({});
    listarTecnicosPre.mockReset().mockResolvedValue({
      data: [{ idUsuario: 11, nombreCompleto: 'Raquel González' }, { idUsuario: 12, nombreCompleto: 'Jorge Ayala' }],
    });
    swalFire.mockClear().mockResolvedValue({ isConfirmed: true });
  });

  // Las siete columnas que enumera el escenario "Consultar la tabla de
  // solicitudes activas" (Anexo A.1).
  it('muestra la tabla con las columnas del caso de uso', async () => {
    montar();
    // "CUP" aparece dos veces —columna de la tabla y columna del conteo—, así
    // que se comprueban las cabeceras de la primera tabla, no de la pantalla.
    await screen.findByText(UNA.nombreProyecto);
    const cabeceras = [...document.querySelectorAll('table')][0].querySelectorAll('thead th');
    expect([...cabeceras].map((c) => c.textContent).filter(Boolean)).toEqual([
      'Unidad Ejecutora', 'Tipo de Solicitud', 'CUP', 'Nombre del Proyecto',
      'Fecha de Solicitud', 'Estado', 'Asignado a',
    ]);
    expect(await screen.findByText(UNA.nombreProyecto)).toBeInTheDocument();
  });

  it('filtra por tipo de solicitud contra el back', async () => {
    montar();
    await screen.findByText(UNA.nombreProyecto);
    fireEvent.change(screen.getByLabelText('Tipo de Solicitud'), { target: { value: 'OPINION_TECNICA' } });

    await waitFor(() =>
      expect(listarSolicitudesActivas).toHaveBeenLastCalledWith(
        expect.objectContaining({ tipoSolicitud: 'OPINION_TECNICA' }),
      ),
    );
  });

  // RN04: el conteo se contabiliza por separado para CUP y Opinión Técnica.
  it('muestra el conteo por técnico separado por tipo', async () => {
    montar();
    const fila = (await screen.findByText('Raquel González')).closest('tr')!;
    expect(fila).toHaveTextContent('2');
    expect(fila).toHaveTextContent('1');
    expect(fila).toHaveTextContent('3');
  });

  it('asigna un Técnico PRE y recarga el listado', async () => {
    montar();
    fireEvent.click(await screen.findByRole('button', { name: 'Asignar' }));
    fireEvent.change(await screen.findByRole('combobox', { name: '' }), { target: { value: '12' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(asignarTecnicoPre).toHaveBeenCalledWith({
        idSolicitud: 1,
        asignacionTecnicoPreRequest: { idTecnicoAsignado: 12 },
      }),
    );
  });

  it('archivar pide confirmación antes de llamar al back', async () => {
    swalFire.mockResolvedValue({ isConfirmed: false });
    montar();
    fireEvent.click(await screen.findByRole('button', { name: 'Archivar' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalled());
    expect(archivarSolicitud).not.toHaveBeenCalled();
  });

  // x-roles del contrato: asignar y archivar son sólo del Coordinador PRE.
  it('un Técnico PRE ve la tabla pero no las acciones de gestión', async () => {
    esCoordinador = false;
    montar();
    await screen.findByText(UNA.nombreProyecto);

    expect(screen.queryByRole('button', { name: 'Asignar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Archivar' })).not.toBeInTheDocument();
  });

  it('un fallo del back se ve como error, no como bandeja vacía', async () => {
    const config = { headers: new AxiosHeaders() };
    listarSolicitudesActivas.mockRejectedValue(
      new AxiosError('fallo', '500', config, null, { status: 500, statusText: '', headers: {}, config, data: undefined }),
    );
    montar();
    expect(await screen.findByRole('alert')).toHaveTextContent(i18n.t('errores.servidor'));
  });
});
