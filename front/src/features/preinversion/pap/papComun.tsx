import { useCallback, useEffect, useState, type ReactNode } from "react";
import { useTranslation } from "react-i18next";
import Swal from "sweetalert2";
import { mensajeDeError, toErrorApi } from "../../../api/apiError";
import { Pagination } from "../../../components/table/Pagination";

export const TAMANIO_PAGINA = 20;

export const monto = (valor: number | null | undefined): string =>
  valor == null
    ? "—"
    : valor.toLocaleString("es-SV", { style: "currency", currency: "USD" });

export const numero = (valor: number | null | undefined): string =>
  valor == null ? "—" : valor.toLocaleString("es-SV");

export const porcentaje = (valor: number | null | undefined): string =>
  valor == null
    ? "—"
    : `${valor.toLocaleString("es-SV", { maximumFractionDigits: 2 })} %`;

/** Los tres cuatrimestres del PAP (CU-PRE-32 y CU-PRE-33 listan uno a la vez). */
export const CUATRIMESTRES = [
  "CUATRIMESTRE_I",
  "CUATRIMESTRE_II",
  "CUATRIMESTRE_III",
] as const;

export type Periodo = (typeof CUATRIMESTRES)[number];

/** Cuatrimestre en curso: enero-abril, mayo-agosto, septiembre-diciembre. */
export const cuatrimestreVigente = (hoy = new Date()): Periodo =>
  CUATRIMESTRES[Math.floor(hoy.getMonth() / 4)];

/** Una columna del listado del PAP. */
export interface ColumnaPAP<F> {
  readonly clave: string;
  readonly etiqueta: string;
  readonly valor: (fila: F) => ReactNode;
  readonly alineado?: "derecha";
}

interface ListadoProps<F> {
  /** Clave de i18n de la pantalla. */
  readonly clave: string;
  readonly columnas: readonly ColumnaPAP<F>[];
  readonly cargar: (parametros: {
    anio: number;
    periodo: Periodo;
    busqueda?: string;
    pagina: number;
    tamanio: number;
  }) => Promise<{
    contenido: F[];
    totalPaginas: number;
    totalElementos: number;
    pagina: number;
    estado?: string;
    /** El servidor resuelve el alcance y devuelve de qué unidad es el listado. */
    idUnidadEjecutora?: number;
  }>;
  /** Botones propios del caso de uso (reporte, revisión, plazos). */
  readonly acciones?: (contexto: {
    anio: number;
    periodo: Periodo;
    idUnidadEjecutora?: number;
    estado?: string;
    recargar: () => void;
  }) => ReactNode;
  /** Bloque propio del caso de uso bajo el listado (el panel de revisión). */
  readonly pie?: (contexto: {
    anio: number;
    periodo: Periodo;
    idUnidadEjecutora?: number;
    recargar: () => void;
  }) => ReactNode;
  /** Qué hacer al pulsar una fila; sin esto, la fila no es un enlace. */
  readonly alAbrir?: (fila: F, contexto: { anio: number; periodo: Periodo }) => void;
  /** Solo CU-PRE-30 acepta búsqueda por CUP o nombre en el contrato. */
  readonly conBusqueda?: boolean;
  /** CU-PRE-32 y CU-PRE-33 listan un cuatrimestre a la vez. */
  readonly conPeriodo?: boolean;
}

/**
 * Listado cuatrimestral del PAP (Anexo A.1 de CU-PRE-30, 31, 32 y 33).
 *
 * Los cuatro casos de uso tienen la misma pantalla de entrada: se elige el año,
 * se busca por CUP o nombre y se lista una fila por etapa —con sus montos o sus
 * metas, según el caso—, paginado por el servidor. Lo que cambia son las
 * columnas y las acciones, que llegan declaradas.
 */
export function ListadoPAP<F>({
  clave,
  columnas,
  cargar,
  acciones,
  pie,
  alAbrir,
  conBusqueda = true,
  conPeriodo = false,
}: ListadoProps<F>) {
  const { t } = useTranslation();
  const anioActual = new Date().getFullYear();

  const [anio, setAnio] = useState(anioActual);
  const [periodo, setPeriodo] = useState<Periodo>(cuatrimestreVigente);
  const [busqueda, setBusqueda] = useState("");
  const [busquedaAplicada, setBusquedaAplicada] = useState("");
  const [filas, setFilas] = useState<F[]>([]);
  const [estado, setEstado] = useState<string | undefined>();
  const [idUnidadEjecutora, setIdUnidadEjecutora] = useState<
    number | undefined
  >();
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const pedir = useCallback(
    async (paginaSolicitada: number) => {
      setCargando(true);
      setError(null);
      try {
        const datos = await cargar({
          anio,
          periodo,
          busqueda: busquedaAplicada || undefined,
          pagina: paginaSolicitada,
          tamanio: TAMANIO_PAGINA,
        });
        setFilas(datos.contenido);
        setTotalPaginas(datos.totalPaginas);
        setTotalElementos(datos.totalElementos);
        setPagina(datos.pagina);
        setEstado(datos.estado);
        setIdUnidadEjecutora(datos.idUnidadEjecutora);
      } catch (error_) {
        setFilas([]);
        setTotalPaginas(0);
        setError(mensajeDeError(toErrorApi(error_), t));
      } finally {
        setCargando(false);
      }
    },
    [anio, periodo, busquedaAplicada, cargar, t],
  );

  useEffect(() => {
    void pedir(0);
  }, [pedir]);

  return (
    <>
      <div className="formcard">
        <div className="formhead">
          <span>{t(`${clave}.titulo`)}</span>
          {estado && (
            <span className="mono">
              · {t(`${clave}.estados.${estado}`, { defaultValue: estado })}
            </span>
          )}
        </div>
        <div className="formbody">
          {error && (
            <div className="aviso-error" role="alert">
              <span>{error}</span>
              <button
                type="button"
                className="btn neutro"
                onClick={() => void pedir(pagina)}
              >
                {t("errores.reintentar")}
              </button>
            </div>
          )}

          <div className="filtros">
            <div className="campo">
              <label htmlFor="pap-anio">{t("preinversion.pap.anio")}</label>
              <input
                id="pap-anio"
                type="number"
                min={2020}
                max={2100}
                value={anio}
                onChange={(e) => setAnio(Number(e.target.value) || anioActual)}
              />
            </div>
            {conPeriodo && (
              <div className="campo">
                <label htmlFor="pap-periodo">
                  {t("preinversion.pap.periodo")}
                </label>
                <select
                  id="pap-periodo"
                  value={periodo}
                  onChange={(e) => setPeriodo(e.target.value as Periodo)}
                >
                  {CUATRIMESTRES.map((c) => (
                    <option key={c} value={c}>
                      {t(`preinversion.pap.cuatrimestres.${c}`)}
                    </option>
                  ))}
                </select>
              </div>
            )}
            {conBusqueda && (
              <>
                <div className="campo crece">
                  <label htmlFor="pap-busqueda">
                    {t("preinversion.pap.busqueda")}
                  </label>
                  <input
                    id="pap-busqueda"
                    type="search"
                    placeholder={t("preinversion.pap.busquedaAyuda")}
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter")
                        setBusquedaAplicada(busqueda.trim());
                    }}
                  />
                </div>
                <div className="campo">
                  <button
                    type="button"
                    className="btn primario"
                    onClick={() => setBusquedaAplicada(busqueda.trim())}
                  >
                    {t("preinversion.pap.buscar")}
                  </button>
                </div>
              </>
            )}
          </div>

          {cargando && <p className="cargando">{t("common.cargando")}</p>}

          {!cargando && !error && (
            <div className="tabla-cont">
              <table>
                <thead>
                  <tr>
                    {columnas.map((c) => (
                      <th key={c.clave}>{t(c.etiqueta)}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filas.length === 0 && (
                    <tr>
                      <td className="vacio" colSpan={columnas.length}>
                        {t("preinversion.pap.sinFilas")}
                      </td>
                    </tr>
                  )}
                  {filas.map((fila, indice) => (
                    // eslint-disable-next-line react/no-array-index-key
                    <tr key={indice}>
                      {columnas.map((c, columna) => (
                        <td
                          key={c.clave}
                          style={
                            c.alineado === "derecha"
                              ? { textAlign: "right" }
                              : undefined
                          }
                        >
                          {columna === 0 && alAbrir ? (
                            <button
                              type="button"
                              className="enlace-fila"
                              onClick={() => alAbrir(fila, { anio, periodo })}
                            >
                              {c.valor(fila)}
                            </button>
                          ) : (
                            c.valor(fila)
                          )}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {!cargando && !error && filas.length > 0 && (
            <p className="conteo-listado">
              {t("preinversion.registro.conteo", {
                desde: pagina * TAMANIO_PAGINA + 1,
                hasta: pagina * TAMANIO_PAGINA + filas.length,
                total: totalElementos,
              })}
            </p>
          )}

          {!error && (
            <Pagination
              currentPage={pagina}
              totalPages={totalPaginas}
              first={pagina <= 0}
              last={pagina >= totalPaginas - 1}
              onPageChange={(p) => void pedir(p)}
            />
          )}

          {acciones && (
            <div className="acciones-form">
              {acciones({
                anio,
                periodo,
                idUnidadEjecutora,
                estado,
                recargar: () => void pedir(pagina),
              })}
            </div>
          )}
        </div>
      </div>
      {pie?.({
        anio,
        periodo,
        idUnidadEjecutora,
        recargar: () => void pedir(pagina),
      })}
    </>
  );
}

/** Acción del servidor con su aviso: se usa para reportes, envíos y plazos. */
export async function ejecutar(
  accion: () => Promise<unknown>,
  mensajeExito: string,
  t: (clave: string) => string,
  despues?: () => void,
) {
  try {
    await accion();
    await Swal.fire({ icon: "success", text: t(mensajeExito) });
    despues?.();
  } catch (error_) {
    await Swal.fire({
      icon: "error",
      text: mensajeDeError(toErrorApi(error_), t),
    });
  }
}
