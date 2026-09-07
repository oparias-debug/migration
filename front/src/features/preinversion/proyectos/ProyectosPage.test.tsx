import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter } from 'react-router-dom';
import i18n from '../../../i18n/i18n';

const listarProyectos = vi.fn();
const eliminarProyecto = vi.fn();
const swalFire = vi.fn();

// El rol lo fija cada prueba: el escenario "no está disponible para otros
// actores" necesita justamente que NO sea Técnico URP.
let rolActual = 'TECNICO_URP';

vi.mock('sweetalert2', () => ({
  default: { fire: (...args: unknown[]) => swalFire(...args) },
}));

// Se conservan los exports reales (EstadoProyecto/IniciativaInversion los usa
// proyectoLabels); sólo se sustituye el cliente que sale a la red.
vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  preinversionApi: {
    listarProyectos: (...args: unknown[]) => listarProyectos(...args),
    eliminarProyecto: (...args: unknown[]) => eliminarProyecto(...args),
  },
}));

vi.mock('../../../auth/useAuth', () => ({
  useAuth: () => ({ hasRole: (rol: string) => rol === rolActual }),
}));

const { ProyectosPage } = await import('./ProyectosPage');

const respuestaVacia = {
  data: { contenido: [], paginacion: { pagina: 0, tamanio: 20, totalElementos: 0, totalPaginas: 0 } },
};

const unProyecto = {
  data: {
    contenido: [
      {
        idProyecto: 1,
        nombre: 'Equipamiento del hospital regional de Santa Ana',
        unidadEjecutora: { idUnidadEjecutora: 4501, nombre: 'MINSAL' },
        iniciativaInversion: 'PROYECTO',
        fechaIngreso: '2026-08-18T09:00:00Z',
        estado: 'ENVIADO_DGICP_REGISTRO',
      },
    ],
    paginacion: { pagina: 0, tamanio: 20, totalElementos: 1, totalPaginas: 1 },
  },
};

// "En Registro" = nunca solicitó el CUP, así que es el único eliminable (RN 4).
const proyectoEnRegistro = {
  data: {
    contenido: [
      {
        idProyecto: 7,
        nombre: 'Ampliación de la red de agua potable de Chalatenango',
        unidadEjecutora: { idUnidadEjecutora: 4501, nombre: 'ANDA' },
        iniciativaInversion: 'PROYECTO',
        fechaIngreso: '2026-09-01T09:00:00Z',
        estado: 'EN_REGISTRO',
      },
    ],
    paginacion: { pagina: 0, tamanio: 20, totalElementos: 1, totalPaginas: 1 },
  },
};

function fallo(status: number) {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('fallo', String(status), config, null, {
    status,
    statusText: '',
    headers: {},
    config,
    data: undefined,
  });
}

const montar = () =>
  render(
    <MemoryRouter>
      <ProyectosPage />
    </MemoryRouter>,
  );

describe('ProyectosPage', () => {
  beforeEach(() => {
    listarProyectos.mockReset();
    eliminarProyecto.mockReset();
    swalFire.mockReset();
    swalFire.mockResolvedValue({ isConfirmed: true });
    rolActual = 'TECNICO_URP';
  });

  it('pide la primera página con el tamaño del contrato (base 0, 20 por página)', async () => {
    listarProyectos.mockResolvedValue(respuestaVacia);
    montar();
    await waitFor(() => expect(listarProyectos).toHaveBeenCalledWith({ pagina: 0, tamanio: 20 }));
  });

  it('muestra los proyectos que devuelve el back', async () => {
    listarProyectos.mockResolvedValue(unProyecto);
    montar();
    expect(await screen.findByText('Equipamiento del hospital regional de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText('MINSAL')).toBeInTheDocument();
  });

  // El defecto que se corrige: antes, un 500 dejaba la tabla en "no hay
  // registros", indistinguible de un listado legítimamente vacío.
  it('un fallo del servidor se ve como error, no como listado vacío', async () => {
    listarProyectos.mockRejectedValue(fallo(500));
    montar();
    const aviso = await screen.findByRole('alert');
    expect(aviso).toHaveTextContent(i18n.t('errores.servidor'));
    expect(screen.queryByText(/no hay registros/i)).not.toBeInTheDocument();
  });

  it('distingue un fallo de red de un error del servidor', async () => {
    listarProyectos.mockRejectedValue(new AxiosError('Network Error'));
    montar();
    expect(await screen.findByRole('alert')).toHaveTextContent(i18n.t('errores.red'));
  });

  it('permite reintentar tras un fallo', async () => {
    listarProyectos.mockRejectedValueOnce(fallo(500)).mockResolvedValueOnce(unProyecto);
    montar();
    await screen.findByRole('alert');
    fireEvent.click(screen.getByRole('button', { name: 'Reintentar' }));
    expect(await screen.findByText('Equipamiento del hospital regional de Santa Ana')).toBeInTheDocument();
    expect(screen.queryByRole('alert')).not.toBeInTheDocument();
  });

  // CU-PRE-01-eliminar-registro.feature
  describe('eliminar un registro antes de la primera solicitud de CUP', () => {
    it('el Técnico URP elimina un registro que nunca solicitó CUP y deja de aparecer', async () => {
      listarProyectos
        .mockResolvedValueOnce(proyectoEnRegistro)
        .mockResolvedValueOnce(respuestaVacia);
      eliminarProyecto.mockResolvedValue({});
      montar();

      fireEvent.click(await screen.findByRole('button', { name: 'Eliminar' }));

      await waitFor(() => expect(eliminarProyecto).toHaveBeenCalledWith({ idProyecto: 7 }));
      // "el registro deja de aparecer en la pantalla Registro de Proyecto"
      await waitFor(() =>
        expect(
          screen.queryByText('Ampliación de la red de agua potable de Chalatenango'),
        ).not.toBeInTheDocument(),
      );
    });

    it('no elimina nada si el usuario cancela la confirmación', async () => {
      listarProyectos.mockResolvedValue(proyectoEnRegistro);
      swalFire.mockResolvedValue({ isConfirmed: false });
      montar();

      fireEvent.click(await screen.findByRole('button', { name: 'Eliminar' }));

      await waitFor(() => expect(swalFire).toHaveBeenCalled());
      expect(eliminarProyecto).not.toHaveBeenCalled();
    });

    // "el Sistema deniega la eliminación": el 409 es RN 4 y se explica como tal,
    // no con el texto genérico de conflicto.
    it('explica la RN 4 cuando el back responde 409', async () => {
      listarProyectos.mockResolvedValue(proyectoEnRegistro);
      eliminarProyecto.mockRejectedValue(fallo(409));
      montar();

      fireEvent.click(await screen.findByRole('button', { name: 'Eliminar' }));

      await waitFor(() =>
        expect(swalFire).toHaveBeenCalledWith(
          expect.objectContaining({
            icon: 'error',
            text: i18n.t('preinversion.registro.eliminarNoPermitido'),
          }),
        ),
      );
    });

    // "No es posible eliminar un registro que ya solicitó el CUP": ni se ofrece.
    it('no ofrece eliminar un proyecto que ya solicitó el CUP', async () => {
      listarProyectos.mockResolvedValue(unProyecto); // ENVIADO_DGICP_REGISTRO
      montar();

      await screen.findByText('Equipamiento del hospital regional de Santa Ana');
      expect(screen.queryByRole('button', { name: 'Eliminar' })).not.toBeInTheDocument();
    });

    // "El botón/acción de eliminar no está disponible para otros actores".
    it('no ofrece eliminar a un actor que no es Técnico URP', async () => {
      rolActual = 'TECNICO_DGICP';
      listarProyectos.mockResolvedValue(proyectoEnRegistro);
      montar();

      await screen.findByText('Ampliación de la red de agua potable de Chalatenango');
      expect(screen.queryByRole('button', { name: 'Eliminar' })).not.toBeInTheDocument();
    });
  });
});
