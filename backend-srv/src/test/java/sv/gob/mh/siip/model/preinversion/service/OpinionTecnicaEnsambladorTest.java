package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.PlazoComentariosOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.DocumentosAnexosOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoGestionOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ResumenOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoFormularioOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/** Pruebas unitarias de {@link OpinionTecnicaEnsamblador} (CU-PRE-26, Anexos A.1 y A1.5). */
class OpinionTecnicaEnsambladorTest {

    private static final LocalDateTime EMISION = LocalDateTime.of(2026, 9, 21, 10, 0);

    private ComentariosDgicpOpinionTecnica comentarios;
    private OpinionTecnicaRepository opiniones;
    private OpinionTecnicaEnsamblador ensamblador;
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        comentarios = mock(ComentariosDgicpOpinionTecnica.class);
        DocumentosOpinionTecnica notas = mock(DocumentosOpinionTecnica.class);
        ApartadosOpinionTecnica apartados = mock(ApartadosOpinionTecnica.class);
        DocumentosAnexosOpinionTecnica anexos = mock(DocumentosAnexosOpinionTecnica.class);
        opiniones = mock(OpinionTecnicaRepository.class);
        ensamblador = new OpinionTecnicaEnsamblador(comentarios, notas, apartados, anexos, opiniones);
        when(opiniones.contarDevoluciones(1L)).thenReturn(2L);
        when(notas.listar(anyLong())).thenReturn(List.of());
        when(apartados.filas(anyLong(), anyBoolean(), any())).thenReturn(List.of());
        when(anexos.seccion(anyLong(), any(), any())).thenReturn(new DocumentosAnexosOpinionTecnicaDto(List.of()));
        proyecto = Proyecto.builder().id(1L).cup("CUP-1").nombre("Proyecto").estado(EstadoProyecto.OBSERVADO)
                .unidadEjecutora(UnidadEjecutora.builder().id(2L).nombre("UE").build()).build();
    }

    private OpinionTecnicaResponseDto pantalla(OpinionTecnica gestion) {
        return ensamblador.pantalla(new OpinionTecnicaContexto(Usuario.builder().id(3L).rol(RolUsuario.TECNICO_PRE)
                .build(), proyecto, gestion, false));
    }

    @Test
    void laPrimeraGestionObservadaMuestraSusComentariosAElegibilidadYElPlazo() {
        OpinionTecnica gestion = OpinionTecnica.builder().id(4L).resultado(ResultadoOpinionTecnica.OBSERVADO)
                .fechaEmision(EMISION).plazoComentarios(new PlazoComentariosOpinionTecnica(LocalDate.of(2026, 9, 28)))
                .fechaSolicitud(EMISION.minusDays(3))
                .etapaActual(TipoEtapaPreinversion.PERFIL).build();
        when(comentarios.porApartado(gestion)).thenReturn(Map.of(ComentarioOpinionTecnica.ELEGIBILIDAD,
                ComentarioOpinionTecnica.builder().comentario("Precisar").justificacionInstitucion("Precisado").build()));

        OpinionTecnicaResponseDto dto = pantalla(gestion);

        assertThat(dto.getEstadoGestion()).isEqualTo(EstadoGestionOpinionTecnicaDto.OBSERVADA);
        assertThat(dto.getTipoFormulario()).isEqualTo(TipoFormularioOpinionTecnicaDto.ESTANDAR);
        assertThat(dto.getEncabezado().getUnidadEjecutora()).isEqualTo("UE");
        assertThat(dto.getEncabezado().getEtapaActual()).isEqualTo("Perfil");
        assertThat(dto.getEncabezado().getEtapaFutura()).isNull();
        assertThat(dto.getComentariosElegibilidad().getComentarioDgicpElegibilidad()).isEqualTo("Precisar");
        assertThat(dto.getFechaEnvioComentarios()).isEqualTo(EMISION.toLocalDate());
        assertThat(dto.getFechaEmisionOt()).isNull();
        assertThat(dto.getNotaOt()).isNull();
        assertThat(dto.getNumeroDevoluciones()).isEqualTo(2);
    }

    @Test
    void unaGestionPosteriorNoMuestraLaSeccionElegibilidad() {
        OpinionTecnica gestion = OpinionTecnica.builder().id(4L).primeraGestion(false).build();
        when(comentarios.porApartado(gestion)).thenReturn(Map.of());
        proyecto.setUnidadEjecutora(null);
        proyecto.setEsProyectoEmergencia(true);

        OpinionTecnicaResponseDto dto = pantalla(gestion);

        assertThat(dto.getComentariosElegibilidad()).isNull();
        assertThat(dto.getEncabezado().getUnidadEjecutora()).isNull();
        assertThat(dto.getTipoFormulario()).isEqualTo(TipoFormularioOpinionTecnicaDto.EMERGENCIA);
        assertThat(dto.getFechaSolicitud()).isNull();
        assertThat(dto.getEstadoGestion()).isEqualTo(EstadoGestionOpinionTecnicaDto.EN_CURSO);
    }

    @Test
    void laPrimeraGestionSinComentarioAElegibilidadMuestraLaSeccionVacia() {
        OpinionTecnica gestion = OpinionTecnica.builder().id(4L).build();
        when(comentarios.porApartado(gestion)).thenReturn(Map.of());

        assertThat(pantalla(gestion).getComentariosElegibilidad().getComentarioDgicpElegibilidad()).isNull();
    }

    @Test
    void elResumenDelHistoricoMuestraLaEmisionYElEstado() {
        OpinionTecnica favorable = OpinionTecnica.builder().id(4L).resultado(ResultadoOpinionTecnica.FAVORABLE)
                .fechaEmision(EMISION).etapaActual(TipoEtapaPreinversion.PERFIL)
                .etapaFutura(TipoEtapaPreinversion.PREFACTIBILIDAD).numeroNotaOt("OT-1")
                .inversionEstimada(new BigDecimal("1000000.00")).build();

        ResumenOpinionTecnicaDto resumen = ensamblador.resumen(favorable);

        assertThat(resumen.getEstadoGestion()).isEqualTo(EstadoGestionOpinionTecnicaDto.FAVORABLE);
        assertThat(resumen.getFechaEmisionOt()).isEqualTo(EMISION.toLocalDate());
        assertThat(resumen.getEtapaFutura()).isEqualTo("Prefactibilidad");
        assertThat(resumen.getNumeroNotaOt()).isEqualTo("OT-1");
        assertThat(resumen.getInversionEstimada()).isEqualByComparingTo("1000000");

        OpinionTecnica archivada = OpinionTecnica.builder().id(5L).resultado(ResultadoOpinionTecnica.OBSERVADO)
                .fechaEmision(EMISION).fechaArchivo(EMISION.plusDays(8)).build();
        assertThat(ensamblador.resumen(archivada).getEstadoGestion()).isEqualTo(EstadoGestionOpinionTecnicaDto.ARCHIVADA);
        assertThat(ensamblador.resumen(archivada).getFechaEmisionOt()).isNull();
    }
}
