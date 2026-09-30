package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.ObjetivoEspecifico;
import sv.gob.mh.siip.model.preinversion.dto.ApartadoOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.PantallaOrigenDto;
import sv.gob.mh.siip.model.preinversion.enums.ApartadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.FichaEmergenciaRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;

/**
 * Tabla "APARTADOS / COMENTARIOS DGICP / JUSTIFICACIÓN INSTITUCIÓN" de la pantalla de Opinión Técnica
 * (CU-PRE-26, Anexo A.1).
 *
 * <p>La columna "Apartados" es un visor (Anexo B.1): muestra el texto registrado en la Identificación
 * (CU-PRE-04) o, para un proyecto de emergencia, en su ficha (CU-PRE-3.5, RN13). El resto de apartados
 * reúne tablas y cálculos de otras pantallas; se muestra solo el enlace a ellas (RN16).
 */
@Component
@Transactional(readOnly = true)
public class ApartadosOpinionTecnica {

    private final IdentificacionRepository identificaciones;
    private final FichaEmergenciaRepository fichasEmergencia;

    public ApartadosOpinionTecnica(IdentificacionRepository identificaciones,
            FichaEmergenciaRepository fichasEmergencia) {
        this.identificaciones = identificaciones;
        this.fichasEmergencia = fichasEmergencia;
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param emergencia si el proyecto usa el formulario de emergencia (RN13)
     * @param comentarios comentarios DGICP de la gestión, por apartado
     * @return las filas de la tabla, en el orden de la pantalla
     */
    public List<ApartadoOpinionTecnicaDto> filas(Long idProyecto, boolean emergencia,
            Map<String, ComentarioOpinionTecnica> comentarios) {
        Map<ApartadoOpinionTecnica, String> contenidos = emergencia ? contenidoEmergencia(idProyecto)
                : contenidoIdentificacion(idProyecto);
        List<ApartadoOpinionTecnicaDto> filas = new ArrayList<>();
        for (ApartadoOpinionTecnica apartado : ApartadoOpinionTecnica.delFormulario(emergencia)) {
            PantallaOrigenDto origen = new PantallaOrigenDto(apartado.getCasoUso());
            if (apartado.getRuta() != null) {
                origen.setRuta("/preinversion/proyectos/" + idProyecto + "/" + apartado.getRuta());
            }
            ApartadoOpinionTecnicaDto fila = new ApartadoOpinionTecnicaDto(apartado.getCodigo(), apartado.getNombre(),
                    apartado.getSeccion().getEtiquetaUi(), origen);
            fila.setContenido(contenidos.get(apartado));
            ComentarioOpinionTecnica comentario = comentarios.get(apartado.getCodigo());
            if (comentario != null) {
                fila.setComentarioDgicp(comentario.getComentario());
                fila.setJustificacionInstitucion(comentario.getJustificacionInstitucion());
            }
            filas.add(fila);
        }
        return filas;
    }

    private Map<ApartadoOpinionTecnica, String> contenidoIdentificacion(Long idProyecto) {
        Map<ApartadoOpinionTecnica, String> contenidos = new EnumMap<>(ApartadoOpinionTecnica.class);
        identificaciones.findByProyectoId(idProyecto).ifPresent((Identificacion identificacion) -> {
            contenidos.put(ApartadoOpinionTecnica.ANTECEDENTES, identificacion.getAntecedentes());
            contenidos.put(ApartadoOpinionTecnica.PROBLEMA_CENTRAL, identificacion.getProblemaCentral());
            contenidos.put(ApartadoOpinionTecnica.OBJETIVO_GENERAL, identificacion.getObjetivoGeneral());
            contenidos.put(ApartadoOpinionTecnica.OBJETIVOS_ESPECIFICOS, objetivosEspecificos(identificacion));
        });
        return sinVacios(contenidos);
    }

    private Map<ApartadoOpinionTecnica, String> contenidoEmergencia(Long idProyecto) {
        Map<ApartadoOpinionTecnica, String> contenidos = new EnumMap<>(ApartadoOpinionTecnica.class);
        fichasEmergencia.findByProyectoId(idProyecto).ifPresent((FichaEmergencia ficha) -> {
            contenidos.put(ApartadoOpinionTecnica.EMERGENCIA_PROBLEMA, ficha.getPlanteamientoProblema());
            contenidos.put(ApartadoOpinionTecnica.EMERGENCIA_OBJETIVO_GENERAL, ficha.getObjetivoGeneral());
            contenidos.put(ApartadoOpinionTecnica.EMERGENCIA_DESCRIPCION, ficha.getDescripcionProyecto());
            contenidos.put(ApartadoOpinionTecnica.EMERGENCIA_PRODUCTOS, String.join(", ", ficha.getProductos()));
            contenidos.put(ApartadoOpinionTecnica.EMERGENCIA_LOCALIZACION, ficha.getDireccionEspecifica());
            contenidos.put(ApartadoOpinionTecnica.EMERGENCIA_POBLACION, ficha.getPoblacionObjetivo());
        });
        return sinVacios(contenidos);
    }

    /** Objetivos específicos en su orden, uno por línea. */
    private static String objetivosEspecificos(Identificacion identificacion) {
        return identificacion.getObjetivosEspecificos().stream()
                .sorted(Comparator.comparing(ObjetivoEspecifico::getOrden,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(ObjetivoEspecifico::getDescripcion)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
    }

    private static Map<ApartadoOpinionTecnica, String> sinVacios(Map<ApartadoOpinionTecnica, String> contenidos) {
        contenidos.values().removeIf(OpinionTecnicaContexto::esVacio);
        return contenidos;
    }
}
