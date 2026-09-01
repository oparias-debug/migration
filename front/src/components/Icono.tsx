import type { CSSProperties } from 'react'

/**
 * Icono del diseño.
 *
 * Los archivos de public/icons son recortes del propio archivo del diseñador
 * (regla D-01/D-02: no se redibuja nada). Los del menú son siluetas blancas con
 * transparencia, así que se pintan como MÁSCARA y toman el color del estado en
 * el que estén: blanco en reposo, blanco sobre el hover azul, azul marino sobre
 * el panel claro del ítem seleccionado.
 *
 * Los iconos a color (módulos, indicadores, pasos) se muestran como imagen.
 */
interface IconoMascaraProps {
  nombre: string
  tam?: number
  className?: string
  style?: CSSProperties
}

export function IconoMascara({ nombre, tam = 22, className = '', style }: IconoMascaraProps) {
  const url = `${import.meta.env.BASE_URL}icons/${nombre}.png`
  return (
    <span
      aria-hidden="true"
      className={className}
      style={{
        width: tam, height: tam, flexShrink: 0, display: 'inline-block',
        background: 'currentColor',
        WebkitMask: `url(${url}) center/contain no-repeat`,
        mask: `url(${url}) center/contain no-repeat`,
        ...style,
      }}
    />
  )
}

interface IconoColorProps {
  nombre: string
  alt?: string
  className?: string
  style?: CSSProperties
}

export function IconoColor({ nombre, alt = '', className = '', style }: IconoColorProps) {
  return (
    <img
      src={`${import.meta.env.BASE_URL}icons/${nombre}.png`}
      alt={alt}
      aria-hidden={alt ? undefined : 'true'}
      className={className}
      style={style}
    />
  )
}
