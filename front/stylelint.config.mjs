/**
 * Reglas de estilo para el CSS del front (CSS plano, sin SCSS ni CSS Modules:
 * src/styles/*.css + los .css de cada feature, importados desde los componentes).
 * La que importa de verdad es `no-descending-specificity`: es el equivalente local
 * de `css:S4664`, el hallazgo de impacto HIGH mas repetido en los frontends del
 * marco. Tenerla aqui significa verla al guardar y no dos dias despues en el
 * informe del ciclo.
 *
 * Lo demas viene del preset estandar de CSS, que se mantiene solo. Abajo estan los
 * unicos ajustes, y cada uno dice por que.
 *
 *   npm run lint:css
 */
const config = {
  extends: ['stylelint-config-standard'],
  ignoreFiles: ['**/node_modules/**', 'dist/**', 'coverage/**'],
  rules: {
    // css:S4664 — el selector generico va antes que el que lo sobrescribe. Se declara
    // explicito aunque el preset ya lo traiga, para que se vea que no es opcional.
    'no-descending-specificity': true,

    // Las clases propias van en kebab-case/BEM; las que se sobrescriben de Bootstrap,
    // flatpickr o sweetalert2 (`.form-control`, `.swal2-popup`...) ya cumplen ese patron.
    'selector-class-pattern': [
      '^[a-z][a-z0-9]*(-[a-z0-9]+)*(__[a-z0-9]+(-[a-z0-9]+)*)?(--[a-z0-9]+(-[a-z0-9]+)*)?$',
      { message: 'La clase va en kebab-case (o BEM: bloque__elemento--modificador)' },
    ],

    // Las tablas de variantes (`.top-right { top: 0; right: 0; }`) se leen mejor en una
    // linea que repartidas en tres. Es una preferencia de formato, no de correccion.
    'declaration-block-single-line-max-declarations': null,

    // Las dos siguientes se apagan por compatibilidad de navegador, no por comodidad.
    // El build no lleva autoprefixer ni postcss, asi que lo que se escriba aqui es
    // exactamente lo que llega al navegador: si se quitan los prefijos, se pierden.
    // `-webkit-user-select` hizo falta en Safari hasta la 16.4 y `-webkit-mask-image`
    // hasta la 15.4, y de esa mascara dependen los iconos del menu.
    'property-no-vendor-prefix': null,

    // `(width <= 1000px)` es sintaxis de rango: Chrome 104, Safari 16.4, Firefox 102.
    // Un navegador anterior no la entiende y descarta la consulta entera, con lo que
    // se pierde toda la maquetacion responsive en vez de degradarse. `max-width` la
    // entiende cualquiera.
    'media-feature-range-notation': null,

    // `clip` esta obsoleto, pero acompana a `clip-path` en el patron que oculta
    // contenido dejandolo disponible para lectores de pantalla: si el navegador no
    // entiende `clip-path` y no hay `clip`, el texto se ve. Es el unico sitio donde
    // se usa; la regla sigue activa para cualquier otra propiedad obsoleta.
    'property-no-deprecated': [true, { ignoreProperties: ['clip'] }],
  },
};

export default config;
