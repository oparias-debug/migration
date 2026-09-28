package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.ObjetivoEspecifico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.UnidadEjecutoraResumenDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;

/** Pruebas unitarias de {@link IdentificacionEnsamblador} (CU-PRE-04). */
class IdentificacionEnsambladorTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 9, 1, 10, 30);

    private final ProyectoMapper proyectoMapper = mock(ProyectoMapper.class);
    private final IdentificacionEnsamblador ensamblador = new IdentificacionEnsamblador(proyectoMapper);

    private final UnidadEjecutora unidad = UnidadEjecutora.builder().id(5L).build();
    private final Proyecto proyecto = Proyecto.builder().id(1L).nombre("Puente").cup("CUP-1")
            .unidadEjecutora(unidad).build();
    private final UnidadEjecutoraResumenDto unidadDto = new UnidadEjecutoraResumenDto();

    @Test
    void ahora_usaLaZonaDeElSalvador() {
        LocalDateTime antes = LocalDateTime.now(ZoneId.of("America/El_Salvador")).minusMinutes(1);

        assertThat(IdentificacionEnsamblador.ahora()).isAfter(antes);
    }

    @Test
    void aDto_sinRegistro_devuelveSoloDatosDelProyecto() {
        when(proyectoMapper.toResumen(unidad)).thenReturn(unidadDto);

        IdentificacionDto dto = ensamblador.aDto(proyecto, null);

        assertThat(dto.getIdProyecto()).isEqualTo(1L);
        assertThat(dto.getNombreProyecto()).isEqualTo("Puente");
        assertThat(dto.getCup()).isEqualTo("CUP-1");
        assertThat(dto.getUnidadEjecutora()).isSameAs(unidadDto);
        assertThat(dto.getObjetivosEspecificos()).isEmpty();
        assertThat(dto.getAntecedentes()).isNull();
        assertThat(dto.getFechaUltimoGuardado()).isNull();
    }

    @Test
    void aDto_conRegistro_ordenaObjetivosYMapeaArchivosYFechas() {
        Identificacion entidad = Identificacion.builder()
                .antecedentes("A").problemaCentral("P").objetivoGeneral("G")
                .nombreArchivoArbolProblemas("problemas.pdf").fechaCargaArbolProblemas(FECHA)
                .nombreArchivoArbolObjetivos("objetivos.pdf").fechaCargaArbolObjetivos(FECHA.plusDays(1))
                .fechaUltimoGuardado(FECHA.plusDays(2))
                .objetivosEspecificos(new ArrayList<>(List.of(
                        ObjetivoEspecifico.builder().descripcion("sin orden").build(),
                        ObjetivoEspecifico.builder().descripcion("segundo").orden(1).build(),
                        ObjetivoEspecifico.builder().descripcion("primero").orden(0).build())))
                .build();

        IdentificacionDto dto = ensamblador.aDto(proyecto, entidad);

        assertThat(dto.getAntecedentes()).isEqualTo("A");
        assertThat(dto.getProblemaCentral()).isEqualTo("P");
        assertThat(dto.getObjetivoGeneral()).isEqualTo("G");
        assertThat(dto.getObjetivosEspecificos()).containsExactly("primero", "segundo", "sin orden");
        assertThat(dto.getArchivoArbolProblemas().getNombreArchivo()).isEqualTo("problemas.pdf");
        assertThat(dto.getArchivoArbolProblemas().getFechaCarga().toLocalDateTime()).isEqualTo(FECHA);
        assertThat(dto.getArchivoArbolObjetivos().getNombreArchivo()).isEqualTo("objetivos.pdf");
        assertThat(dto.getFechaUltimoGuardado().toLocalDateTime()).isEqualTo(FECHA.plusDays(2));
    }

    @Test
    void aDto_conRegistroSinArchivosNiGuardado_dejaArchivosYFechaNulos() {
        IdentificacionDto dto = ensamblador.aDto(proyecto, Identificacion.builder().build());

        assertThat(dto.getArchivoArbolProblemas()).isNull();
        assertThat(dto.getArchivoArbolObjetivos()).isNull();
        assertThat(dto.getFechaUltimoGuardado()).isNull();
    }

    @Test
    void aplicar_copiaTextosReemplazaObjetivosYMarcaElGuardado() {
        Identificacion entidad = Identificacion.builder()
                .objetivosEspecificos(new ArrayList<>(List.of(ObjetivoEspecifico.builder().descripcion("viejo")
                        .build())))
                .build();
        IdentificacionRequestDto request = new IdentificacionRequestDto().antecedentes("A").problemaCentral("P")
                .objetivoGeneral("G").objetivosEspecificos(List.of("uno", "dos"));

        ensamblador.aplicar(entidad, request);

        assertThat(entidad.getAntecedentes()).isEqualTo("A");
        assertThat(entidad.getProblemaCentral()).isEqualTo("P");
        assertThat(entidad.getObjetivoGeneral()).isEqualTo("G");
        assertThat(entidad.getObjetivosEspecificos()).hasSize(2);
        assertThat(entidad.getObjetivosEspecificos().get(0).getDescripcion()).isEqualTo("uno");
        assertThat(entidad.getObjetivosEspecificos().get(1).getOrden()).isEqualTo(1);
        assertThat(entidad.getObjetivosEspecificos().get(1).getIdentificacion()).isSameAs(entidad);
        assertThat(entidad.getFechaUltimoGuardado()).isNotNull();
    }

    @Test
    void aplicar_sinObjetivos_dejaLaListaVacia() {
        Identificacion entidad = Identificacion.builder()
                .objetivosEspecificos(new ArrayList<>(List.of(ObjetivoEspecifico.builder().descripcion("viejo")
                        .build())))
                .build();

        ensamblador.aplicar(entidad, new IdentificacionRequestDto().objetivosEspecificos(null));

        assertThat(entidad.getObjetivosEspecificos()).isEmpty();
    }

    @Test
    void resumenArchivo_sinFecha_dejaFechaNula() {
        ArchivoAdjuntoResumenDto resumen = ensamblador.resumenArchivo("arbol.pdf", null);

        assertThat(resumen.getNombreArchivo()).isEqualTo("arbol.pdf");
        assertThat(resumen.getFechaCarga()).isNull();
    }
}
