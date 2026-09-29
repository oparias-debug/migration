package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/**
 * Lo que CU-PRE-25 "Elegibilidad" necesita que ocurra cuando el Técnico PRE da clic en "Enviar
 * comentarios" desde CU-PRE-26 "Opinión Técnica" (FB2 paso 2; RN09).
 *
 * <p>La ficha se vuelve a habilitar sola, porque {@link ElegibilidadAcceso} la deriva de la Opinión
 * Técnica "Observado" posterior a la última emisión. Falta avisar al Viabilizador: CU-PRE-26, dueño
 * de esa acción y aún sin implementar, debe invocar {@link #notificarComentarios(Proyecto)} una vez
 * registrados sus comentarios.
 */
@Component
public class ComentariosOtElegibilidad {

  private final UsuarioRepository usuarioRepository;
  private final NotificacionService notificacionService;

  public ComentariosOtElegibilidad(UsuarioRepository usuarioRepository, NotificacionService notificacionService) {
    this.usuarioRepository = usuarioRepository;
    this.notificacionService = notificacionService;
  }

  /**
   * Notifica a los Viabilizadores que la OT emitió comentarios a la Elegibilidad del proyecto y que
   * pueden ajustar la calificación de criterios (FB2 paso 2).
   *
   * @param proyecto proyecto devuelto a Elegibilidad
   */
  public void notificarComentarios(Proyecto proyecto) {
    notificacionService.notificarComentariosOtElegibilidad(proyecto,
        usuarioRepository.findByRolAndActivoTrue(RolUsuario.VIABILIZADOR));
  }
}
