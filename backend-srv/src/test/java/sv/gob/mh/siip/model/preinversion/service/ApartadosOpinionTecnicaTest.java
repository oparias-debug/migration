package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.ObjetivoEspecifico;
import sv.gob.mh.siip.model.preinversion.dto.ApartadoOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.repository.FichaEmergenciaRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;

/** Pruebas unitarias de {@link ApartadosOpinionTecnica} (CU-PRE-26, Anexo A.1, RN13, RN16). */
class ApartadosOpinionTecnicaTest {

    private static final Long ID_PROYECTO = 4L;

    private IdentificacionRepository identificaciones;
    private FichaEmergenciaRepository fichas;
    private ApartadosOpinionTecnica apartados;

    @BeforeEach
    void setUp() {
        identificaciones = mock(IdentificacionRepository.class);
        fichas = mock(FichaEmergenciaRepository.class);
        apartados = new ApartadosOpinionTecnica(identificaciones, fichas);
    }

    private static ApartadoOpinionTecnicaDto fila(List<ApartadoOpinionTecnicaDto> filas, String codigo) {
        return filas.stream().filter(f -> f.getApartadoCodigo().equals(codigo)).findFirst().orElseThrow();
    }

    @Test
    void elFormularioEstandarMuestraLaIdentificacionYLosComentarios() {
        List<ObjetivoEspecifico> objetivos = new ArrayList<>(List.of(
                ObjetivoEspecifico.builder().orden(2).descripcion("Segundo").build(),
                ObjetivoEspecifico.builder().orden(null).descripcion("Sin orden").build(),
                ObjetivoEspecifico.builder().orden(1).descripcion("Primero").build(),
                ObjetivoEspecifico.builder().orden(3).descripcion(null).build()));
        when(identificaciones.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(Identificacion.builder()
                .antecedentes("Antecedentes").problemaCentral("  ").objetivoGeneral("Objetivo")
                .objetivosEspecificos(objetivos).build()));
        ComentarioOpinionTecnica comentario = ComentarioOpinionTecnica.builder().apartado("1.1").comentario("Ampliar")
                .justificacionInstitucion("Ampliado").build();

        List<ApartadoOpinionTecnicaDto> filas = apartados.filas(ID_PROYECTO, false, Map.of("1.1", comentario));

        assertThat(fila(filas, "1.1").getContenido()).isEqualTo("Antecedentes");
        assertThat(fila(filas, "1.1").getComentarioDgicp()).isEqualTo("Ampliar");
        assertThat(fila(filas, "1.1").getJustificacionInstitucion()).isEqualTo("Ampliado");
        assertThat(fila(filas, "1.2").getContenido()).isNull();
        assertThat(fila(filas, "1.4").getContenido()).isEqualTo("Primero\nSegundo\nSin orden");
        assertThat(fila(filas, "1.1").getPantallaOrigen().getRuta()).isEqualTo("/preinversion/proyectos/4/identificacion");
        // Pantallas sin ruta propia en el cliente: solo se informa el caso de uso.
        assertThat(fila(filas, "3.2").getPantallaOrigen().getRuta()).isNull();
        assertThat(fila(filas, "3.2").getPantallaOrigen().getCasoUso()).isEqualTo("CU-PRE-21");
    }

    @Test
    void sinIdentificacionLosApartadosQuedanSinContenido() {
        when(identificaciones.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThat(apartados.filas(ID_PROYECTO, false, Map.of()))
                .allSatisfy(f -> assertThat(f.getContenido()).isNull());
    }

    @Test
    void elFormularioDeEmergenciaMuestraLaFichaDeCuPre35() {
        when(fichas.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(FichaEmergencia.builder()
                .planteamientoProblema("Deslave").objetivoGeneral("Reparar").descripcionProyecto("Muro")
                .productos(new ArrayList<>(List.of("P1", "P2"))).direccionEspecifica("Km 5").poblacionObjetivo("")
                .build()));

        List<ApartadoOpinionTecnicaDto> filas = apartados.filas(ID_PROYECTO, true, Map.of());

        assertThat(filas).allSatisfy(f -> assertThat(f.getPantallaOrigen().getCasoUso()).isEqualTo("CU-PRE-03.5"));
        assertThat(fila(filas, "E.1").getContenido()).isEqualTo("Deslave");
        assertThat(fila(filas, "E.4").getContenido()).isEqualTo("P1, P2");
        assertThat(fila(filas, "E.6").getContenido()).isNull();
    }

    @Test
    void sinFichaDeEmergenciaLosApartadosQuedanSinContenido() {
        when(fichas.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThat(apartados.filas(ID_PROYECTO, true, Map.of()))
                .allSatisfy(f -> assertThat(f.getContenido()).isNull());
    }
}
