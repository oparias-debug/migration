package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.ConclusionesOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.Localizacion;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorEvaluacionDto;
import sv.gob.mh.siip.model.preinversion.dto.InformeOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.LocalizacionRepository;

/** Pruebas unitarias de {@link InformeOpinionTecnicaEnsamblador} (CU-PRE-26, Anexo A.6). */
class InformeOpinionTecnicaEnsambladorTest {

    private static final LocalDateTime EMISION = LocalDateTime.of(2026, 6, 26, 10, 0);

    private FichaViabilidadEnsamblador ficha;
    private IdentificacionRepository identificaciones;
    private LocalizacionRepository localizaciones;
    private InformeOpinionTecnicaEnsamblador ensamblador;

    private final Usuario actor = Usuario.builder().id(1L).rol(RolUsuario.TECNICO_PRE).build();
    private final Proyecto proyecto = Proyecto.builder().id(2L).cup("8803").nombre("Unidad de Salud")
            .unidadEjecutora(UnidadEjecutora.builder().id(3L).nombre("Ministerio de Salud").build()).build();

    @BeforeEach
    void setUp() {
        ficha = mock(FichaViabilidadEnsamblador.class);
        identificaciones = mock(IdentificacionRepository.class);
        localizaciones = mock(LocalizacionRepository.class);
        ensamblador = new InformeOpinionTecnicaEnsamblador(ficha, identificaciones, localizaciones);
    }

    @Test
    void soloHayInformeDeUnaOtEmitida() {
        OpinionTecnicaContexto enCurso = new OpinionTecnicaContexto(actor, proyecto,
                OpinionTecnica.builder().id(4L).build(), false);

        assertThatThrownBy(() -> ensamblador.informe(enCurso))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasFieldOrPropertyWithValue("codigo", InformeOpinionTecnicaEnsamblador.OPINION_TECNICA_NO_EMITIDA);
    }

    @Test
    void reuneLosDatosDelProyectoYDeLaEmision() {
        OpinionTecnica emitida = OpinionTecnica.builder().id(4L).resultado(ResultadoOpinionTecnica.FAVORABLE)
                .fechaSolicitud(EMISION.minusDays(16)).fechaAjustes(EMISION.minusDays(3)).fechaEmision(EMISION)
                .etapaActual(TipoEtapaPreinversion.PERFIL).etapaFutura(TipoEtapaPreinversion.EJECUCION)
                .numeroNotaOt("MH.DGICP.DGI/001.070/2025")
                .revisionConclusiones(new ConclusionesOpinionTecnica("Se considera pertinente")).build();
        doAnswer(invocacion -> {
            FichaViabilidadResponseDto datos = invocacion.getArgument(0);
            datos.setObjetivoGeneral("Contar con infraestructura adecuada");
            datos.setDescripcion("Construcción de un nivel");
            datos.setProductos(List.of("Unidad de salud construida"));
            datos.setPoblacionObjetivo(10_500L);
            datos.setInversionEstimada(new BigDecimal("750000"));
            datos.setCostoOperacion(new BigDecimal("35000"));
            datos.setCostoMantenimiento(new BigDecimal("15000"));
            datos.setIndicadoresEvaluacion(List.of(new IndicadorEvaluacionDto("VAN").valor(BigDecimal.TEN)));
            return null;
        }).when(ficha).completarCamposDeConsulta(any(), any());
        when(identificaciones.findByProyectoId(2L)).thenReturn(Optional.of(Identificacion.builder()
                .problemaCentral("El inmueble no es adecuado").build()));
        when(localizaciones.findAllByProyectoId(2L)).thenReturn(List.of(
                Localizacion.builder().departamento(Departamento.builder().nombre("Morazán").build())
                        .municipio(Municipio.builder().nombre("Perquín").build()).direccion("Rancho Quemado").build(),
                Localizacion.builder().build()));

        InformeOpinionTecnicaResponseDto informe = ensamblador.informe(
                new OpinionTecnicaContexto(actor, proyecto, emitida, false));

        assertThat(informe.getOpinionTecnica()).isEqualTo("FAVORABLE");
        assertThat(informe.getEncabezado().getUnidadEjecutora()).isEqualTo("Ministerio de Salud");
        assertThat(informe.getEncabezado().getEtapaFutura()).isEqualTo("Ejecución");
        assertThat(informe.getProblemaCentral()).isEqualTo("El inmueble no es adecuado");
        assertThat(informe.getProductos()).containsExactly("Unidad de salud construida");
        assertThat(informe.getPoblacionObjetivo()).isEqualTo(10_500L);
        assertThat(informe.getInversionEstimada()).isEqualByComparingTo("750000");
        assertThat(informe.getIndicadoresEvaluacion()).singleElement()
                .satisfies(i -> assertThat(i.getNombre()).isEqualTo("VAN"));
        assertThat(informe.getLocalizacion().get(0).getMunicipio()).isEqualTo("Perquín");
        assertThat(informe.getLocalizacion().get(1).getDepartamento()).isNull();
        assertThat(informe.getFechaAjustes()).isEqualTo(EMISION.minusDays(3).toLocalDate());
        assertThat(informe.getFechaEmisionOt()).isEqualTo(EMISION.toLocalDate());
        assertThat(informe.getNumeroNotaOt()).isEqualTo("MH.DGICP.DGI/001.070/2025");
        assertThat(informe.getConclusiones()).isEqualTo("Se considera pertinente");
        assertThat(informe.getTamano()).isNull();
        assertThat(informe.getCostoEstudio()).isNull();
    }
}
