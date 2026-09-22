/**
 * ¿Pasa la puerta de calidad de SonarQube?
 *
 * `npm run sonar` sube el análisis y termina con éxito aunque el proyecto quede
 * en rojo: el resultado lo decide el servidor después. Este script pregunta por
 * él y devuelve un código de salida distinto de cero si falla, para poder
 * encadenarlo antes de subir código.
 *
 *   SONAR_TOKEN=... node scripts/comprobar-quality-gate.mjs
 */
import { readFileSync } from 'node:fs';

const propiedades = Object.fromEntries(
  readFileSync(new URL('../sonar-project.properties', import.meta.url), 'utf8')
    .split('\n')
    .filter((l) => l.includes('=') && !l.trim().startsWith('#'))
    .map((l) => {
      const i = l.indexOf('=');
      return [l.slice(0, i).trim(), l.slice(i + 1).trim()];
    }),
);

const host = process.env.SONAR_HOST_URL ?? propiedades['sonar.host.url'];
const proyecto = propiedades['sonar.projectKey'];
const token = process.env.SONAR_TOKEN;
if (!token) {
  console.error('Falta SONAR_TOKEN.');
  process.exit(2);
}

// El análisis se procesa en el servidor: puede tardar unos segundos en estar listo.
const esperar = (ms) => new Promise((r) => { setTimeout(r, ms); });
const cabeceras = { Authorization: `Basic ${Buffer.from(`${token}:`).toString('base64')}` };

let estado = null;
for (let intento = 0; intento < 10 && !estado; intento += 1) {
  const r = await fetch(`${host}/api/qualitygates/project_status?projectKey=${encodeURIComponent(proyecto)}`, { headers: cabeceras });
  if (!r.ok) {
    console.error(`SonarQube respondió ${r.status}`);
    process.exit(2);
  }
  const { projectStatus } = await r.json();
  if (projectStatus.status !== 'NONE') estado = projectStatus;
  else await esperar(3000);
}

if (!estado) {
  console.error('SonarQube no tiene todavía un resultado para este proyecto.');
  process.exit(2);
}

for (const c of estado.conditions ?? []) {
  const marca = c.status === 'OK' ? '  ok  ' : ' FALLA';
  console.log(`${marca} ${c.metricKey.padEnd(30)} ${String(c.actualValue ?? '-').padStart(9)}  (umbral ${c.errorThreshold ?? '-'})`);
}
console.log(`\nPuerta de calidad: ${estado.status}`);
process.exit(estado.status === 'OK' ? 0 : 1);
