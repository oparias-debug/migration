/**
 * Los cuatro iconos de la pantalla de acceso, dibujados aquí.
 *
 * Van en el propio archivo y no en una hoja de iconos externa: la aplicación
 * se despliega en redes que pueden no tener salida a internet, y un icono que
 * viaja en el bundle se ve siempre. Toman el color del texto que los rodea.
 */
interface Props {
  readonly nombre: 'persona' | 'candado' | 'ojo' | 'ojo-tachado';
  readonly tam?: number;
}

export function IconoLogin({ nombre, tam = 16 }: Props) {
  return (
    <svg
      width={tam}
      height={tam}
      viewBox="0 0 16 16"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.4"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      focusable="false"
    >
      {nombre === 'persona' && (
        <>
          <circle cx="8" cy="5" r="2.6" />
          <path d="M2.8 13.6c0-2.6 2.3-4.2 5.2-4.2s5.2 1.6 5.2 4.2" />
        </>
      )}
      {nombre === 'candado' && (
        <>
          <rect x="3.2" y="7" width="9.6" height="6.6" rx="1.2" />
          <path d="M5.6 7V4.9a2.4 2.4 0 0 1 4.8 0V7" />
        </>
      )}
      {(nombre === 'ojo' || nombre === 'ojo-tachado') && (
        <>
          <path d="M1.4 8s2.6-4 6.6-4 6.6 4 6.6 4-2.6 4-6.6 4S1.4 8 1.4 8Z" />
          <circle cx="8" cy="8" r="1.8" />
        </>
      )}
      {nombre === 'ojo-tachado' && <path d="M2.6 2.6l10.8 10.8" />}
    </svg>
  );
}
