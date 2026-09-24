import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const obtenerProgramacionFinancieraPAP = vi.fn();
const generarReporteProgramacionPAP = vi.fn();
const obtenerProgramacionEstudio = vi.fn();
const guardarProgramacionEstudio = vi.fn();
const obtenerProgramacionMetasFisicasPAP = vi.fn();
const enviarProgramacionARevisionDgicp = vi.fn();
const registrarObservacionesDgicp = vi.fn();
const enviarObservacionesDgicp = vi.fn();
const registrarRespuestaInstitucion = vi.fn();
const finalizarRevision = vi.fn();
const obtenerProgramacionMetasEstudio = vi.fn();
const guardarProgramacionMetasEstudio = vi.fn();
const obtenerAvanceFinancieroPAP = vi.fn();
const obtenerAvanceMetasFisicasPAP = vi.fn();
const generarReporteAvanceMetas = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  programacionFinancieraApi: {
    obtenerProgramacionFinancieraPAP: (...a: unknown[]) => obtenerProgramacionFinancieraPAP(...a),
    generarReporteProgramacionPAP: (...a: unknown[]) => generarReporteProgramacionPAP(...a),
    habilitarModificacionesFueraPlazo: vi.fn(),
    obtenerProgramacionEstudio: (...a: unknown[]) => obtenerProgramacionEstudio(...a),
    guardarProgramacionEstudio: (...a: unknown[]) => guardarProgramacionEstudio(...a),
  },
  programacionMetasApi: {
    obtenerProgramacionMetasFisicasPAP: (...a: unknown[]) => obtenerProgramacionMetasFisicasPAP(...a),
    generarReporteMetasFisicas: vi.fn(),
    habilitarModificacionesMetasFueraPlazo: vi.fn(),
    enviarProgramacionARevisionDgicp: (...a: unknown[]) => enviarProgramacionARevisionDgicp(...a),
    registrarObservacionesDgicp: (...a: unknown[]) => registrarObservacionesDgicp(...a),
    enviarObservacionesDgicp: (...a: unknown[]) => enviarObservacionesDgicp(...a),
    registrarRespuestaInstitucion: (...a: unknown[]) => registrarRespuestaInstitucion(...a),
    enviarRespuestaInstitucion: vi.fn(),
    finalizarRevision: (...a: unknown[]) => finalizarRevision(...a),
    obtenerProgramacionMetasEstudio: (...a: unknown[]) => obtenerProgramacionMetasEstudio(...a),
    guardarProgramacionMetasEstudio: (...a: unknown[]) => guardarProgramacionMetasEstudio(...a),
  },
  avanceFinancieroApi: {
    obtenerAvanceFinancieroPAP: (...a: unknown[]) => obtenerAvanceFinancieroPAP(...a),
    generarReporteAvanceFinanciero: vi.fn(),
  },
  avanceMetasApi: {
    obtenerAvanceMetasFisicasPAP: (...a: unknown[]) => obtenerAvanceMetasFisicasPAP(...a),
    generarReporteAvanceMetas: (...a: unknown[]) => generarReporteAvanceMetas(...a),
    registrarObservacionesAvanceDgicp: vi.fn(),
    enviarObservacionesAvanceDgicp: vi.fn(),
    registrarRespuestaInstitucionAvance: vi.fn(),
    enviarRespuestaInstitucionAvance: vi.fn(),
    finalizarRevisionAvance: vi.fn(),
  },
}));

let rolesActivos: string[] = ['TECNICO_URP'];
vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: (r: string) => rolesActivos.includes(r) }) }));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const { PapPage } = await import('./PapPage');
const { ProgramacionFinancieraPage } = await import('./ProgramacionFinancieraPage');
const { ProgramacionFinancieraEstudioPage } = await import('./ProgramacionFinancieraEstudioPage');
const { ProgramacionMetasPage } = await import('./ProgramacionMetasPage');
const { ProgramacionMetasEstudioPage } = await import('./ProgramacionMetasEstudioPage');
const { AvanceFinancieroPage } = await import('./AvanceFinancieroPage');
const { AvanceMetasPage } = await import('./AvanceMetasPage');

const paginacion = { pagina: 0, tamanio: 20, totalElementos: 1, totalPaginas: 1 };

const FINANCIERA = {
  data: {
    idUnidadEjecutora: 3,
    anio: 2026,
    contenido: [
      {
        cup: '10001',
        nombreProyecto: 'Hospital de Santa Ana',
        etapa: 'PERFIL',
        fuenteFinanciamiento: 'FONDO_GENERAL',
        costoEtapa: 120000,
        montoCuatrimestre1: 40000,
        montoCuatrimestre2: 40000,
        montoCuatrimestre3: 40000,
        totalProgramadoAnio: 120000,
      },
    ],
    paginacion,
  },
};

const METAS = {
  data: {
    idUnidadEjecutora: 3,
    anio: 2026,
    contenido: [
      {
        cup: '10001',
        nombreProyecto: 'Hospital de Santa Ana',
        etapa: 'PERFIL',
        metaTotal: 1,
        entregable: 'ESTUDIO_DE_PERFIL',
        ejecutadoAniosAnteriores: 0,
        totalAnio: 100,
        aniosPosteriores: 0,
      },
    ],
    paginacion,
  },
};

const AVANCE_FINANCIERO = {
  data: {
    idUnidadEjecutora: 3,
    anio: 2026,
    periodo: 'CUATRIMESTRE_I',
    contenido: [
      {
        cup: '10001',
        nombreProyecto: 'Hospital de Santa Ana',
        etapa: 'PERFIL',
        fuenteFinanciamiento: 'FONDO_GENERAL',
        avanceDelCuatrimestreEjecutadoMonto: 15000,
        avanceDelCuatrimestrePorcentaje: 37.5,
        alertaExcesoProgramado: true,
      },
    ],
    paginacion,
  },
};

const AVANCE_METAS = {
  data: {
    idUnidadEjecutora: 3,
    anio: 2026,
    periodo: 'CUATRIMESTRE_I',
    contenido: [
      {
        cup: '10001',
        nombreProyecto: 'Hospital de Santa Ana',
        etapa: 'PERFIL',
        meta: 1,
        entregable: 'ESTUDIO_DE_PERFIL',
        programadoDelCuatrimestre: 33,
        ejecutadoDelCuatrimestre: 20,
        totalMetaEjecutada: 20,
        estado: 'ATRASADO',
      },
    ],
    paginacion,
  },
};

const ESTUDIO_FINANCIERO = {
  data: {
    cup: '10001',
    nombreProyecto: 'Hospital de Santa Ana',
    esArrastre: false,
    etapas: [
      {
        etapa: 'PERFIL',
        costoEtapa: 120000,
        fuentes: [
          {
            idFuente: 9,
            fuenteFinanciamiento: 'FONDO_GENERAL',
            fuenteRecursos: 'GOES',
            montoCuatrimestre1: 40000,
            montoCuatrimestre2: 40000,
            montoCuatrimestre3: 40000,
            totalProgramadoAnio: 120000,
          },
        ],
      },
    ],
  },
};

const ESTUDIO_METAS = {
  data: {
    cup: '10001',
    nombreProyecto: 'Hospital de Santa Ana',
    esArrastre: false,
    etapas: [
      {
        etapa: 'PERFIL',
        entregable: 'ESTUDIO_DE_PERFIL',
        meta: 1,
        montoCuatrimestre1: 30,
        montoCuatrimestre2: 30,
        montoCuatrimestre3: 40,
        totalAnio: 100,
      },
    ],
  },
};

const montar = (Pantalla: () => JSX.Element) =>
  render(
    <MemoryRouter>
      <Pantalla />
    </MemoryRouter>,
  );

const montarFicha = (Pantalla: () => JSX.Element, base: string) =>
  render(
    <MemoryRouter initialEntries={[{ pathname: `${base}/10001`, state: { anio: 2026, periodo: 'CUATRIMESTRE_II' } }]}>
      <Routes>
        <Route path={`${base}/:cup`} element={<Pantalla />} />
      </Routes>
    </MemoryRouter>,
  );

beforeEach(() => {
  rolesActivos = ['TECNICO_URP'];
  [
    swalFire,
    navigate,
    generarReporteProgramacionPAP,
    enviarProgramacionARevisionDgicp,
    registrarObservacionesDgicp,
    enviarObservacionesDgicp,
    registrarRespuestaInstitucion,
    finalizarRevision,
    generarReporteAvanceMetas,
    guardarProgramacionEstudio,
    guardarProgramacionMetasEstudio,
  ].forEach((m) => m.mockReset().mockResolvedValue({ data: {} }));
  swalFire.mockResolvedValue({ isConfirmed: true });
  obtenerProgramacionFinancieraPAP.mockReset().mockResolvedValue(FINANCIERA);
  obtenerProgramacionMetasFisicasPAP.mockReset().mockResolvedValue(METAS);
  obtenerAvanceFinancieroPAP.mockReset().mockResolvedValue(AVANCE_FINANCIERO);
  obtenerAvanceMetasFisicasPAP.mockReset().mockResolvedValue(AVANCE_METAS);
  obtenerProgramacionEstudio.mockReset().mockResolvedValue(ESTUDIO_FINANCIERO);
  obtenerProgramacionMetasEstudio.mockReset().mockResolvedValue(ESTUDIO_METAS);
  guardarProgramacionEstudio.mockResolvedValue(ESTUDIO_FINANCIERO);
  guardarProgramacionMetasEstudio.mockResolvedValue(ESTUDIO_METAS);
});

describe('PAP · entrada (CU-PRE-30 a CU-PRE-33)', () => {
  it('ofrece los cuatro documentos y entra a cada uno', () => {
    montar(PapPage);
    expect(screen.getByText('Programación Financiera Cuatrimestral')).toBeInTheDocument();
    expect(screen.getByText('Avance Cuatrimestral de Metas')).toBeInTheDocument();

    fireEvent.click(screen.getByText('Avance Cuatrimestral Financiero'));
    expect(navigate).toHaveBeenCalledWith('/programacion/pap/avance-financiero');
  });
});

describe('Programación Financiera del PAP (CU-PRE-30)', () => {
  it('lista los estudios del año con sus montos por cuatrimestre', async () => {
    montar(ProgramacionFinancieraPage);
    expect(await screen.findByText('Hospital de Santa Ana')).toBeInTheDocument();
    expect(screen.getAllByText(/40,000/).length).toBeGreaterThan(0);
  });

  // El PAP devuelve códigos de catálogo; en pantalla se leen como en el resto del sistema.
  it('las etapas y las fuentes se leen, no se muestran como código', async () => {
    montar(ProgramacionFinancieraPage);
    await screen.findByText('Hospital de Santa Ana');
    expect(screen.getByText('Perfil')).toBeInTheDocument();
    expect(screen.getByText('Fondo general (FGEN)')).toBeInTheDocument();
    expect(screen.queryByText('FONDO_GENERAL')).not.toBeInTheDocument();
  });

  it('la búsqueda por CUP la resuelve el servidor', async () => {
    montar(ProgramacionFinancieraPage);
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.change(screen.getByLabelText('Buscar'), { target: { value: '10001' } });
    fireEvent.click(screen.getByRole('button', { name: 'Buscar' }));

    await waitFor(() =>
      expect(obtenerProgramacionFinancieraPAP).toHaveBeenLastCalledWith(expect.objectContaining({ busqueda: '10001' })),
    );
  });

  // El reporte necesita la unidad ejecutora, y quien la resuelve es el servidor: viene del listado.
  it('el reporte se pide para la unidad ejecutora que devolvió el listado', async () => {
    montar(ProgramacionFinancieraPage);
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Generar reporte' }));

    await waitFor(() =>
      expect(generarReporteProgramacionPAP).toHaveBeenCalledWith({
        anio: expect.any(Number),
        idUnidadEjecutora: 3,
        formato: 'EXCEL',
      }),
    );
  });

  it('el CUP abre la ficha del estudio con el año del listado', async () => {
    montar(ProgramacionFinancieraPage);
    fireEvent.click(await screen.findByText('10001'));
    expect(navigate).toHaveBeenCalledWith(
      '/programacion/pap/programacion-financiera/10001',
      expect.objectContaining({ state: expect.objectContaining({ anio: expect.any(Number) }) }),
    );
  });

  it('un fallo del servidor se ve como error, no como listado vacío', async () => {
    obtenerProgramacionFinancieraPAP.mockRejectedValue(new Error('falla'));
    montar(ProgramacionFinancieraPage);
    expect(await screen.findByRole('alert')).toBeInTheDocument();
  });
});

describe('Ficha financiera de un estudio (CU-PRE-30)', () => {
  it('guarda las fuentes agrupadas por etapa y con el año del listado', async () => {
    montarFicha(ProgramacionFinancieraEstudioPage, '/programacion/pap/programacion-financiera');
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.change(screen.getByLabelText('I cuatrimestre 1'), { target: { value: '50000' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarProgramacionEstudio).toHaveBeenCalledWith(
        expect.objectContaining({
          cup: '10001',
          anio: 2026,
          guardarProgramacionEstudioRequest: {
            etapas: [
              {
                etapa: 'PERFIL',
                fuentes: [
                  expect.objectContaining({ idFuente: 9, montoCuatrimestre1: 50000, montoCuatrimestre3: 40000 }),
                ],
              },
            ],
          },
        }),
      ),
    );
  });

  it('quien no registra el PAP la ve sin poder guardar', async () => {
    rolesActivos = ['TECNICO_PRE'];
    montarFicha(ProgramacionFinancieraEstudioPage, '/programacion/pap/programacion-financiera');
    await screen.findByText('Hospital de Santa Ana');
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });
});

describe('Programación de Metas Físicas del PAP (CU-PRE-31)', () => {
  it('lista los entregables y el total del año en porcentaje', async () => {
    montar(ProgramacionMetasPage);
    expect(await screen.findByText('Hospital de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText('100 %')).toBeInTheDocument();
    expect(screen.getByText('Estudio de perfil')).toBeInTheDocument();
  });

  // Decisión funcional v1.2: el envío a revisión es uno solo para CU-PRE-30 y CU-PRE-31.
  it('envía la programación del PAP a revisión de la DGICP', async () => {
    montar(ProgramacionMetasPage);
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Enviar a revisión DGICP' }));

    await waitFor(() =>
      expect(enviarProgramacionARevisionDgicp).toHaveBeenCalledWith({
        enviarProgramacionARevisionDgicpRequest: { anio: expect.any(Number), idUnidadEjecutora: 3 },
      }),
    );
  });

  it('la institución responde las observaciones y no las escribe', async () => {
    montar(ProgramacionMetasPage);
    await screen.findByText('Hospital de Santa Ana');

    expect(screen.queryByLabelText('Observaciones de la DGICP')).not.toBeInTheDocument();
    fireEvent.change(screen.getByLabelText('Respuesta de la institución'), { target: { value: 'Corregido' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(registrarRespuestaInstitucion).toHaveBeenCalledWith({
        registrarRespuestaInstitucionRequest: {
          anio: expect.any(Number),
          idUnidadEjecutora: 3,
          respuestaInstitucion: 'Corregido',
        },
      }),
    );
  });

  it('la DGICP escribe las observaciones, las envía y finaliza la revisión', async () => {
    rolesActivos = ['COORDINADOR_PRE'];
    montar(ProgramacionMetasPage);
    await screen.findByText('Hospital de Santa Ana');

    expect(screen.queryByLabelText('Respuesta de la institución')).not.toBeInTheDocument();
    fireEvent.change(screen.getByLabelText('Observaciones de la DGICP'), {
      target: { value: 'Falta el III cuatrimestre' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(registrarObservacionesDgicp).toHaveBeenCalled());

    fireEvent.click(screen.getByRole('button', { name: 'Enviar observaciones' }));
    await waitFor(() => expect(enviarObservacionesDgicp).toHaveBeenCalled());

    fireEvent.click(screen.getByRole('button', { name: 'Finalizar revisión' }));
    await waitFor(() => expect(finalizarRevision).toHaveBeenCalled());
  });
});

describe('Ficha de metas físicas de un estudio (CU-PRE-31)', () => {
  it('las etapas vienen de la ruta del proyecto: no se agregan ni se quitan filas', async () => {
    montarFicha(ProgramacionMetasEstudioPage, '/programacion/pap/programacion-metas');
    await screen.findByText('Hospital de Santa Ana');
    expect(screen.queryByRole('button', { name: 'Agregar fila' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Quitar' })).not.toBeInTheDocument();
  });

  it('guarda el porcentaje de cada cuatrimestre', async () => {
    montarFicha(ProgramacionMetasEstudioPage, '/programacion/pap/programacion-metas');
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.change(screen.getByLabelText('II cuatrimestre 1'), { target: { value: '35' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarProgramacionMetasEstudio).toHaveBeenCalledWith(
        expect.objectContaining({
          guardarProgramacionMetasEstudioRequest: {
            etapas: [
              expect.objectContaining({ etapa: 'PERFIL', entregable: 'ESTUDIO_DE_PERFIL', montoCuatrimestre2: 35 }),
            ],
          },
        }),
      ),
    );
  });
});

describe('Avance Financiero del PAP (CU-PRE-32)', () => {
  it('lista el cuatrimestre vigente y avisa de lo que excede lo programado', async () => {
    montar(AvanceFinancieroPage);
    expect(await screen.findByText('Hospital de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText('Excede lo programado')).toBeInTheDocument();
  });

  it('cambiar de cuatrimestre vuelve a pedir el listado', async () => {
    montar(AvanceFinancieroPage);
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.change(screen.getByLabelText('Cuatrimestre'), { target: { value: 'CUATRIMESTRE_III' } });

    await waitFor(() =>
      expect(obtenerAvanceFinancieroPAP).toHaveBeenLastCalledWith(
        expect.objectContaining({ periodo: 'CUATRIMESTRE_III' }),
      ),
    );
  });

  // CU-PRE-32 no tiene búsqueda en el contrato; no se inventa un filtro que el servidor ignoraría.
  it('no ofrece búsqueda por CUP', async () => {
    montar(AvanceFinancieroPage);
    await screen.findByText('Hospital de Santa Ana');
    expect(screen.queryByLabelText('Buscar')).not.toBeInTheDocument();
  });
});

describe('Avance de Metas Físicas del PAP (CU-PRE-33)', () => {
  it('muestra el estado del avance de cada etapa', async () => {
    montar(AvanceMetasPage);
    expect(await screen.findByText('Hospital de Santa Ana')).toBeInTheDocument();
    // Los cuatro valores del contrato son A_TIEMPO, ATRASADO, ADELANTADO y FINALIZADO.
    expect(screen.getByText('Atrasado')).toHaveClass('marca-estado', 'e-error');
  });

  it('el reporte se pide para el año y el cuatrimestre en pantalla', async () => {
    montar(AvanceMetasPage);
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Generar reporte' }));

    await waitFor(() =>
      expect(generarReporteAvanceMetas).toHaveBeenCalledWith(
        expect.objectContaining({
          idUnidadEjecutora: 3,
          formato: 'EXCEL',
          periodo: expect.stringContaining('CUATRIMESTRE'),
        }),
      ),
    );
  });
});

describe('PAP · casos de borde comunes', () => {
  it('un año sin programación se dice, no se deja la tabla muda', async () => {
    obtenerProgramacionFinancieraPAP.mockResolvedValue({
      data: { contenido: [], paginacion: { ...paginacion, totalElementos: 0, totalPaginas: 0 } },
    });
    montar(ProgramacionFinancieraPage);
    expect(await screen.findByText('No hay estudios que mostrar para ese año.')).toBeInTheDocument();
  });

  it('quien no participa en la revisión no ve el panel', async () => {
    rolesActivos = ['ADMINISTRADOR'];
    montar(ProgramacionMetasPage);
    await screen.findByText('Hospital de Santa Ana');
    expect(screen.queryByText('Revisión de la DGICP')).not.toBeInTheDocument();
  });

  it('si la acción falla, se avisa del error del servidor', async () => {
    enviarProgramacionARevisionDgicp.mockRejectedValue(new Error('falla'));
    montar(ProgramacionMetasPage);
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Enviar a revisión DGICP' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' })));
  });

  it('agregar una fuente la deja en la etapa que se está trabajando', async () => {
    montarFicha(ProgramacionFinancieraEstudioPage, '/programacion/pap/programacion-financiera');
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Agregar fila' }));

    expect(screen.getByLabelText('Etapa 2')).toHaveValue('PERFIL');
  });

  it('si no se puede leer el estudio, la ficha lo dice', async () => {
    obtenerProgramacionEstudio.mockRejectedValue(new Error('falla'));
    montarFicha(ProgramacionFinancieraEstudioPage, '/programacion/pap/programacion-financiera');
    expect(await screen.findByRole('alert')).toBeInTheDocument();
  });

  it('si el guardado falla, el aviso es de error y la pantalla no se vacía', async () => {
    guardarProgramacionMetasEstudio.mockRejectedValue(new Error('falla'));
    montarFicha(ProgramacionMetasEstudioPage, '/programacion/pap/programacion-metas');
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' })));
    expect(screen.getByText('Hospital de Santa Ana')).toBeInTheDocument();
  });

  it('el año se puede cambiar y el listado se vuelve a pedir', async () => {
    montar(ProgramacionFinancieraPage);
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.change(screen.getByLabelText('Año'), { target: { value: '2025' } });

    await waitFor(() =>
      expect(obtenerProgramacionFinancieraPAP).toHaveBeenLastCalledWith(expect.objectContaining({ anio: 2025 })),
    );
  });
});

// Los datos del PAP se van llenando a lo largo del año: casi toda columna puede
// llegar vacía, y una celda en blanco no se distingue de un fallo de carga.
describe('PAP · celdas sin dato', () => {
  const soloElCup = (extra: Record<string, unknown> = {}) => ({
    data: {
      idUnidadEjecutora: 3,
      contenido: [{ cup: '10002', nombreProyecto: 'Carretera Longitudinal', ...extra }],
      paginacion,
    },
  });

  it.each([
    ['financiera', () => obtenerProgramacionFinancieraPAP.mockResolvedValue(soloElCup()), ProgramacionFinancieraPage],
    ['metas', () => obtenerProgramacionMetasFisicasPAP.mockResolvedValue(soloElCup()), ProgramacionMetasPage],
    ['avance financiero', () => obtenerAvanceFinancieroPAP.mockResolvedValue(soloElCup()), AvanceFinancieroPage],
    ['avance de metas', () => obtenerAvanceMetasFisicasPAP.mockResolvedValue(soloElCup()), AvanceMetasPage],
  ])('%s marca con raya lo que todavía no tiene dato', async (_nombre, preparar, Pantalla) => {
    preparar();
    montar(Pantalla as () => JSX.Element);
    expect(await screen.findByText('Carretera Longitudinal')).toBeInTheDocument();
    expect(screen.getAllByText('—').length).toBeGreaterThan(3);
  });
});
