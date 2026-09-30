import { createContext, useContext, useEffect, useState } from 'react';
import { catalogoEtapasApi } from '../../../api/preinversionApi';
import type { ContenidoIniciativaResumen } from '../../../api/generated/administracion-catalogos';
import type { IniciativaInversion } from '../../../api/generated/preinversion';
import type { PasoProyecto } from './pasosProyecto';

/**
 * Qué capítulos se formulan según la iniciativa, con el "Anexo F — Contenido de
 * Iniciativas de Proyecto" (RN20) que sirve el servidor en
 * /catalogos/contenido-iniciativas-proyecto.
 *
 * Un programa no formula alternativas de solución, ni ambiental, ni legal, ni
 * beneficios; un estudio general tampoco riesgos ni O&M. Antes se veían los
 * capítulos de un proyecto para las tres iniciativas, y había que entrar a cada
 * uno para descubrir que no aplicaba (observación del 21/09/2026: "allí solo se
 * deben mostrar los siguientes formularios").
 *
 * La matriz NO se copia aquí: es un catálogo del servidor y se lee de allí, para
 * que un cambio del anexo no obligue a publicar el frontend.
 *
 * El anexo distingue además las cuatro etapas de un proyecto (Perfil,
 * Prefactibilidad, Factibilidad, Diseño). Para una iniciativa PROYECTO se toma
 * la unión de las cuatro: cuál se está formulando es parte del flujo que
 * todavía no existe, y hasta entonces es mejor mostrar un capítulo de más que
 * esconder uno que hace falta.
 */

/** 'CU-PRE-22.1' y 'CUPRE-22.1' son el mismo caso de uso escrito de dos maneras. */
const claveCu = (cu: string) => cu.replaceAll('-', '').toUpperCase();

const aplicaAIniciativa = (fila: ContenidoIniciativaResumen, iniciativa: IniciativaInversion) => {
  if (iniciativa === 'PROGRAMA') return fila.aplicaPrograma;
  if (iniciativa === 'ESTUDIO_GENERAL') return fila.aplicaEstudioGeneral;
  return fila.aplicaPerfil || fila.aplicaPrefactibilidad || fila.aplicaFactibilidad || fila.aplicaDiseno;
};

/**
 * ¿Se formula este caso de uso en esta iniciativa?
 *
 * Sin catálogo todavía, sin iniciativa conocida, o con un caso de uso que el
 * catálogo no menciona, la respuesta es que sí. Esconder un capítulo es quitarle
 * trabajo a quien formula, así que sólo se esconde cuando el anexo lo dice; el
 * catálogo que sirve el servidor hoy no trae todas las filas del anexo, y los
 * casos de uso que le faltan no pueden desaparecer de la pantalla por eso.
 */
export function contenidoAplica(
  filas: readonly ContenidoIniciativaResumen[] | null,
  iniciativa: IniciativaInversion | null,
  cu: string,
): boolean {
  if (!filas || !iniciativa) return true;
  const clave = claveCu(cu);
  const propias = filas.filter((f) => f.ubicacionCasoUso && claveCu(f.ubicacionCasoUso) === clave);
  if (propias.length === 0) return true;
  // Un caso de uso puede ocupar varias filas del anexo (CU-PRE-17 es
  // "Presupuesto de Inversión" y "Fuentes de Financiamiento"): basta una.
  return propias.some((f) => aplicaAIniciativa(f, iniciativa));
}

/** Los casos de uso del anexo que cubre un capítulo. */
export const casosDeUsoDe = (paso: PasoProyecto): readonly string[] => paso.cus ?? [paso.cu];

/** ¿Tiene el capítulo algún contenido que se formule en esta iniciativa? */
export const pasoAplica = (paso: PasoProyecto, aplica: (cu: string) => boolean) =>
  casosDeUsoDe(paso).some((cu) => aplica(cu));

/**
 * El anexo, pedido una sola vez por sesión: son 26 filas que no cambian
 * mientras se trabaja. Un fallo no se guarda, para que el siguiente intento
 * vuelva a preguntar.
 */
let pedido: Promise<ContenidoIniciativaResumen[]> | null = null;
export function catalogoContenidos(): Promise<ContenidoIniciativaResumen[]> {
  pedido ??= catalogoEtapasApi
    .listarContenidoIniciativasProyecto()
    .then(({ data }) => data)
    .catch((error: unknown) => {
      pedido = null;
      throw error;
    });
  return pedido;
}

/** Sólo para las pruebas: olvida el anexo ya pedido. */
export const olvidarCatalogoContenidos = () => {
  pedido = null;
};

export function useCatalogoContenidos(): ContenidoIniciativaResumen[] | null {
  const [filas, setFilas] = useState<ContenidoIniciativaResumen[] | null>(null);
  useEffect(() => {
    let vigente = true;
    catalogoContenidos()
      .then((datos) => {
        if (vigente) setFilas(datos);
      })
      .catch(() => {
        // Sin el anexo se muestran todos los capítulos: ver contenidoAplica.
      });
    return () => {
      vigente = false;
    };
  }, []);
  return filas;
}

/**
 * La respuesta que da la barra de pasos a las pantallas que cuelgan de ella, para
 * que "Siguiente" salte los capítulos que no se formulan en esta iniciativa sin
 * volver a pedir ni el proyecto ni el anexo.
 *
 * Sin barra alrededor —una pantalla abierta sola— aplica todo.
 */
export const ContextoContenido = createContext<(cu: string) => boolean>(() => true);

export const useAplicaContenido = () => useContext(ContextoContenido);
