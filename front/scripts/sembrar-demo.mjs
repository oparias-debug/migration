/**
 * Datos de demostración del entorno de revisión.
 *
 * El back arranca con `hibernate.ddl-auto: create-drop`: cada vez que se
 * reconstruye o se reinicia, la base se recrea vacía y sólo quedan los datos
 * del sembrador de desarrollo. Todo lo que se haya cargado a mano —el
 * calendario de ejemplo, los catálogos, las solicitudes de prueba— desaparece.
 * Este script lo vuelve a dejar como estaba, en un solo paso.
 *
 *   node scripts/sembrar-demo.mjs [url]     (por defecto, el entorno de revisión)
 *
 * Usa la propia aplicación para autenticarse, así que no necesita credenciales
 * de base de datos ni tocar el back.
 */
import { chromium } from '/opt/alkimity/node_modules/playwright/index.mjs';

const base = process.argv[2] ?? 'http://187.124.51.133:8090';

const entrar = async (pagina, usuario, clave) => {
  await pagina.goto(`${base}/login`, { waitUntil: 'networkidle' });
  await pagina.fill('#username', usuario);
  await pagina.fill('#password', clave);
  await pagina.click('button[type=submit]');
  await pagina.waitForFunction(() => !!localStorage.getItem('siip.auth'), null, { timeout: 25000 });
  await pagina.waitForTimeout(500);
};

/** Ejecuta peticiones al API desde la propia página, con su token. */
const llamar = (pagina, peticiones) => pagina.evaluate(async (lista) => {
  const token = JSON.parse(localStorage.getItem('siip.auth')).accessToken;
  const cabeceras = { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` };
  const salida = [];
  for (const { metodo, url, cuerpo } of lista) {
    const r = await fetch(url, { method: metodo, headers: cabeceras, body: cuerpo ? JSON.stringify(cuerpo) : undefined });
    salida.push(`${r.status} ${metodo} ${url}`);
  }
  return salida;
}, peticiones);

const navegador = await chromium.launch();

// --- Calendario de inversión 2027 (el ejemplo de la nota de Álvaro) ---------
const admin = await (await navegador.newContext()).newPage();
await entrar(admin, 'admin', 'admin123');
const calendario = [
  { metodo: 'POST', url: '/back/calendarios', cuerpo: {
    codigo: 'INVERSION_2027', nombre: 'Calendario inversión 2027',
    descripcion: 'Períodos de cada etapa del proceso de inversión para el año 2027',
    fechaInicio: '2027-03-01', fechaFin: '2027-10-31', estado: 'ACTIVO' } },
  { metodo: 'POST', url: '/back/calendarios/INVERSION_2027/periodos-laborales', cuerpo: {
    codigo: 'HABILES', nombre: 'Días hábiles del proceso',
    recurrencia: { tipo: 'UNA_VEZ', fechaInicio: '2027-03-01', fechaFin: '2027-10-31' } } },
  { metodo: 'POST', url: '/back/calendarios/INVERSION_2027/periodos-no-laborales', cuerpo: {
    codigo: 'FIN_DE_SEMANA', nombre: 'Sábados y domingos',
    recurrencia: { tipo: 'SEMANAL', fechaInicio: '2027-03-01', fechaFin: '2027-10-31', diasSemana: ['SATURDAY', 'SUNDAY'] } } },
  { metodo: 'POST', url: '/back/calendarios/INVERSION_2027/periodos-no-laborales', cuerpo: {
    codigo: 'SEMANA_AGOSTINA', nombre: 'Semana Agostina',
    recurrencia: { tipo: 'UNA_VEZ', fechaInicio: '2027-08-01', fechaFin: '2027-08-06' } } },
  { metodo: 'POST', url: '/back/calendarios/INVERSION_2027/periodos-laborales', cuerpo: {
    codigo: 'REGISTRO_PROYECTOS', nombre: 'Período de registro de proyectos',
    recurrencia: { tipo: 'UNA_VEZ', fechaInicio: '2027-03-01', fechaFin: '2027-05-31' } } },
  { metodo: 'POST', url: '/back/calendarios/INVERSION_2027/excepciones', cuerpo: {
    fecha: '2027-05-01', tipo: 'DIA_NO_LABORAL', descripcion: 'Día del Trabajo' } },
  { metodo: 'POST', url: '/back/calendarios/INVERSION_2027/excepciones', cuerpo: {
    fecha: '2027-09-15', tipo: 'DIA_NO_LABORAL', descripcion: 'Día de la Independencia' } },
];
console.log('calendario:', (await llamar(admin, calendario)).filter((l) => !l.startsWith('2')).join(' | ') || 'todo creado');

// --- Catálogos --------------------------------------------------------------
const catalogos = await (await navegador.newContext()).newPage();
await entrar(catalogos, 'admin.catalogos', 'adminCatalogos123');
const peticionesCatalogo = [
  { metodo: 'POST', url: '/back/catalogos', cuerpo: {
    code: 'TIPO_DOCUMENTO', name: 'Tipos de documento de soporte', active: 'ACTIVE',
    fields: [{ name: 'codigo', qualifier: 'KEY', position: 1 }, { name: 'descripcion', qualifier: 'FIELD', position: 2 }] } },
  { metodo: 'POST', url: '/back/catalogos', cuerpo: {
    code: 'UNIDAD_MEDIDA', name: 'Unidades de medida', active: 'ACTIVE',
    fields: [{ name: 'codigo', qualifier: 'KEY', position: 1 }, { name: 'nombre', qualifier: 'FIELD', position: 2 }, { name: 'simbolo', qualifier: 'FIELD', position: 3 }] } },
  ...[['DL', 'Decreto Legislativo'], ['EST', 'Estudio de prefactibilidad'], ['PLA', 'Planos constructivos']]
    .map(([codigo, descripcion]) => ({ metodo: 'POST', url: '/back/catalogos/TIPO_DOCUMENTO/registros', cuerpo: { values: { codigo, descripcion } } })),
  ...[['KM', 'Kilómetro', 'km'], ['M2', 'Metro cuadrado', 'm²'], ['UN', 'Unidad', 'u']]
    .map(([codigo, nombre, simbolo]) => ({ metodo: 'POST', url: '/back/catalogos/UNIDAD_MEDIDA/registros', cuerpo: { values: { codigo, nombre, simbolo } } })),
];
console.log('catálogos:', (await llamar(catalogos, peticionesCatalogo)).filter((l) => !l.startsWith('2')).join(' | ') || 'todo creado');

// --- Solicitudes de ejemplo -------------------------------------------------
const urp = await (await navegador.newContext({ viewport: { width: 1440, height: 900 } })).newPage();
await entrar(urp, 'tecnico.urp', 'tecnicoUrp123');
const solicitudes = [
  ['Construcción del hospital nacional de Santa Ana', '12,500,000', 'Construcción de un hospital de segundo nivel con 120 camas para el departamento de Santa Ana.', true],
  ['Ampliación de la red de agua potable de Chalatenango', '3,200,000', 'Ampliación de la red de distribución de agua potable para 14 cantones del municipio de Chalatenango.', true],
  ['Mejoramiento de la carretera Longitudinal del Norte tramo III', '8,750,000', 'Rehabilitación y ampliación de 32 kilómetros de la carretera Longitudinal del Norte.', false],
];
for (const [nombre, monto, descripcion, enviar] of solicitudes) {
  await urp.goto(`${base}/preinversion/proyectos/nuevo`, { waitUntil: 'networkidle' });
  await urp.waitForTimeout(1200);
  await urp.click('#iniciativa-PROYECTO');
  await urp.fill('#nombre', nombre);
  await urp.fill('#montoEstimadoInversion', monto);
  const sectores = await urp.$$eval('#idSector option', (o) => o.map((x) => x.value).filter(Boolean));
  await urp.selectOption('#idSector', sectores[Math.floor(Math.random() * sectores.length)]);
  const ejes = await urp.$$eval('#idEjeTematico option', (o) => o.map((x) => x.value).filter(Boolean));
  await urp.selectOption('#idEjeTematico', ejes[0]);
  await urp.fill('#descripcionProyecto', descripcion);
  await urp.click('button[type=submit]');
  await urp.waitForTimeout(1400);
  await urp.click('.swal2-confirm').catch(() => {});
  await urp.waitForTimeout(1100);
  if (enviar) {
    await urp.click('button:has-text("Solicitar CUP")').catch(() => {});
    await urp.waitForTimeout(700);
    for (let i = 0; i < 2; i += 1) { await urp.click('.swal2-confirm').catch(() => {}); await urp.waitForTimeout(800); }
  }
  console.log('solicitud:', nombre);
}

await navegador.close();
console.log('\nDatos de demostración listos en', base);
