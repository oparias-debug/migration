/**
 * ¿Pasa la puerta de calidad de SonarQube?
 *
 * `npm run sonar` sube el análisis y termina con éxito aunque el proyecto quede
 * en rojo: el resultado lo decide el servidor después. Este script pregunta por
 * él y devuelve un código de salida distinto de cero si falla, para poder
 * encadenarlo antes de subir código.
 *
 *   SONAR_TOKEN=... node scripts/comprobar-quality-gate.mjs
 *
 * Se pregunta por el análisis recién subido, no por el proyecto: el scanner deja
 * su identificador en .scannerwork/report-task.txt y aquí se espera a que el
 * servidor termine de procesarlo antes de leer el resultado. Preguntar por el
 * proyecto sin más devuelve el veredicto del análisis anterior mientras el nuevo
 * sigue en cola, y eso da por buena una puerta que acaba en rojo.
 */
import { readFileSync } from 'node:fs';

const leerPropiedades = (url) =>
  Object.fromEntries(
    readFileSync(url, 'utf8')
      .split('\n')
      .filter((l) => l.includes('=') && !l.trim().startsWith('#'))
      .map((l) => {
        const i = l.indexOf('=');
        return [l.slice(0, i).trim(), l.slice(i + 1).trim()];
      }),
  );

const propiedades = leerPropiedades(new URL('../sonar-project.properties', import.meta.url));
const host = process.env.SONAR_HOST_URL ?? propiedades['sonar.host.url'];
const proyecto = propiedades['sonar.projectKey'];
const token = process.env.SONAR_TOKEN;
if (!token) {
  console.error('Falta SONAR_TOKEN.');
  process.exit(2);
}

const cabeceras = { Authorization: `Basic ${Buffer.from(`${token}:`).toString('base64')}` };
const esperar = (ms) => new Promise((r) => { setTimeout(r, ms); });

const pedir = async (ruta) => {
  const r = await fetch(`${host}${ruta}`, { headers: cabeceras });
  if (!r.ok) {
    console.error(`SonarQube respondió ${r.status} a ${ruta}`);
    process.exit(2);
  }
  return r.json();
};

/** Identificador del análisis que acaba de subir el scanner. */
const tareaDelUltimoAnalisis = () => {
  try {
    return leerPropiedades(new URL('../.scannerwork/report-task.txt', import.meta.url)).ceTaskId;
  } catch {
    return null;
  }
};

/** Espera a que el servidor termine de procesar ese análisis y devuelve su id. */
const esperarAlAnalisis = async (ceTaskId) => {
  for (let intento = 0; intento < 40; intento += 1) {
    const { task } = await pedir(`/api/ce/task?id=${encodeURIComponent(ceTaskId)}`);
    if (task.status === 'SUCCESS') return task.analysisId;
    if (task.status === 'FAILED' || task.status === 'CANCELED') {
      console.error(`El análisis terminó en ${task.status}.`);
      process.exit(2);
    }
    await esperar(3000);
  }
  console.error('El servidor no terminó de procesar el análisis a tiempo.');
  process.exit(2);
  return null;
};

const ceTaskId = tareaDelUltimoAnalisis();
if (!ceTaskId) {
  console.error('No encuentro .scannerwork/report-task.txt: ¿se corrió el análisis antes que este script?');
  process.exit(2);
}

const analysisId = await esperarAlAnalisis(ceTaskId);
const { projectStatus: estado } = await pedir(`/api/qualitygates/project_status?analysisId=${encodeURIComponent(analysisId)}`);

for (const c of estado.conditions ?? []) {
  const marca = c.status === 'OK' ? '  ok  ' : ' FALLA';
  console.log(`${marca} ${c.metricKey.padEnd(30)} ${String(c.actualValue ?? '-').padStart(9)}  (umbral ${c.errorThreshold ?? '-'})`);
}

// Una puerta sin condiciones evaluadas no es una puerta aprobada: pasa en el
// primer análisis de un proyecto, cuando todavía no hay código nuevo con el que
// comparar, y conviene que se note en vez de leerse como un visto bueno.
if ((estado.conditions ?? []).length === 0) {
  console.log(`\nPuerta de calidad: ${estado.status}, pero sin condiciones evaluadas (proyecto ${proyecto}).`);
  process.exit(estado.status === 'OK' ? 0 : 1);
}

console.log(`\nPuerta de calidad: ${estado.status}`);
process.exit(estado.status === 'OK' ? 0 : 1);
