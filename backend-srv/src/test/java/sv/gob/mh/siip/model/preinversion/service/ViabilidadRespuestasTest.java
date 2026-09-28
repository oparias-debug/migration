package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioCampoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.CampoFichaViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioCampoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.DocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.CampoFichaViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;

/** Pruebas unitarias de {@link ViabilidadRespuestas} (CU-PRE-24). */
class ViabilidadRespuestasTest {

    private static final Usuario VIABILIZADOR = Usuario.builder().id(20L).rol(RolUsuario.VIABILIZADOR).build();

    private static Proyecto proyecto() {
        return Proyecto.builder().id(7L).cup("00123").nombre("Proyecto").estado(EstadoProyecto.EN_VIABILIDAD)
                .build();
    }

    @Test
    void laFichaLlevaElEncabezadoLosDocumentosLosComentariosYLasAcciones() {
        RevisionViabilidad revision = RevisionViabilidad.builder().estado(EstadoRevisionViabilidad.EN_CURSO)
                .observacionesGenerales("Cumple").comentarios(new ArrayList<>(List.of(
                        new ComentarioCampoViabilidad(CampoFichaViabilidad.PRODUCTOS, "Ok"))))
                .build();
        DocumentoViabilidad documento = DocumentoViabilidad.builder().id(1L)
                .tipoDocumento(TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION).nombreArchivo("p.pdf")
                .fechaCarga(LocalDateTime.of(2026, 1, 15, 10, 30)).build();
        ViabilidadContexto contexto = new ViabilidadContexto(VIABILIZADOR, proyecto(), revision, false, true);

        FichaViabilidadResponseDto ficha = ViabilidadRespuestas.ficha(contexto, List.of(documento));

        assertThat(ficha.getCup()).isEqualTo("00123");
        assertThat(ficha.getNombreProyecto()).isEqualTo("Proyecto");
        assertThat(ficha.getEstadoProyecto()).isEqualTo(EstadoProyecto.EN_VIABILIDAD.getEtiquetaUi());
        assertThat(ficha.getDocumentos()).singleElement()
                .satisfies(d -> assertThat(d.getNombreArchivo()).isEqualTo("p.pdf"));
        assertThat(ficha.getComentariosViabilizador())
                .containsExactly(new ComentarioCampoViabilidadDto(CampoFichaViabilidadDto.PRODUCTOS, "Ok"));
        assertThat(ficha.getObservacionesGeneralesJustificacion()).isEqualTo("Cumple");
        assertThat(ficha.getAccionesDisponibles().getGuardarComentarios()).isTrue();
    }

    @Test
    void sinRevisionNoHayComentariosNiObservaciones() {
        ViabilidadContexto contexto = new ViabilidadContexto(VIABILIZADOR, proyecto(), null, false, false);

        FichaViabilidadResponseDto ficha = ViabilidadRespuestas.ficha(contexto, List.of());

        assertThat(ficha.getDocumentos()).isEmpty();
        assertThat(ficha.getComentariosViabilizador()).isEmpty();
        assertThat(ficha.getObservacionesGeneralesJustificacion()).isNull();
    }

    @Test
    void elDocumentoSeExpresaEnLaZonaHorariaDeElSalvador() {
        DocumentoViabilidad documento = DocumentoViabilidad.builder().id(3L)
                .tipoDocumento(TipoDocumentoViabilidad.OTRO_DOCUMENTO).nombreArchivo("anexo.xlsx")
                .fechaCarga(LocalDateTime.of(2026, 1, 15, 10, 30)).build();

        DocumentoViabilidadDto dto = ViabilidadRespuestas.documento(documento);

        assertThat(dto.getDocumentoId()).isEqualTo(3L);
        assertThat(dto.getTipoDocumento()).isEqualTo(TipoDocumentoViabilidadDto.OTRO_DOCUMENTO);
        assertThat(dto.getFechaCarga().getOffset().getTotalSeconds()).isEqualTo(-6 * 3600);
    }
}
