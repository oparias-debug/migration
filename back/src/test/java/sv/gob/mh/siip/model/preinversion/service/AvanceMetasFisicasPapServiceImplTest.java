package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** CU-PRE-33: RN-C.b (tope "Programado en el Año"), RN-B.b, RN-B.d, RN-B.c y alcance del reporte (SF-4). */
class AvanceMetasFisicasPapServiceImplTest {

    private static final int ANIO = 2030;
    private static final String CUP = "08040";

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final EtapaMetaFisicaPapRepository etapaMetaRepository = mock(EtapaMetaFisicaPapRepository.class);
    private final ProgCuatrimestralMetaFisicaRepository progRepository = mock(ProgCuatrimestralMetaFisicaRepository.class);
    private final AvanceCuatriMetaFisicaRepository avanceRepository = mock(AvanceCuatriMetaFisicaRepository.class);
    private final CalendarioEventoRepository calendarioEventoRepository = mock(CalendarioEventoRepository.class);
    private final RevisionAvancePapRepository revisionRepository = mock(RevisionAvancePapRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final NotificacionService notificacionService = mock(NotificacionService.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AvanceMetasFisicasPapServiceImpl service = new AvanceMetasFisicasPapServiceImpl(proyectoRepository,
            etapaPreinversionRepository, etapaMetaRepository, progRepository, avanceRepository,
            calendarioEventoRepository, revisionRepository, usuarioRepository, notificacionService, actorContexto);

    private final List<AvanceCuatriMetaFisica> avances = new ArrayList<>();
    private EtapaMetaFisicaPap etapaMeta;

    @BeforeEach
    void configurarEstudio() {
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(7L).build();
        when(actorContexto.exigirRol(any(RolUsuario[].class)))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidad).build());

        Proyecto proyecto = Proyecto.builder().id(1L).cup(CUP).nombre("Estudio A").unidadEjecutora(unidad).build();
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(10L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        etapaMeta = EtapaMetaFisicaPap.builder().id(20L).etapaPreinversion(etapa).build();

        when(proyectoRepository.findByCup(CUP)).thenReturn(Optional.of(proyecto));
        when(etapaPreinversionRepository.findByProyectoId(1L)).thenReturn(List.of(etapa));
        when(etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(1L, TipoEtapaPreinversion.PERFIL))
                .thenReturn(Optional.of(etapa));
        when(etapaMetaRepository.findByEtapaPreinversionId(10L)).thenReturn(Optional.of(etapaMeta));
        when(avanceRepository.findByProgramacionMeta_EtapaMetaFisica_Id(20L)).thenReturn(avances);
        when(avanceRepository.findByProgramacionMetaIdAndCuatrimestre(anyLong(), any(Cuatrimestre.class)))
                .thenAnswer(inv -> avances.stream()
                        .filter(a -> a.getProgramacionMeta().getId().equals(inv.getArgument(0))
                                && a.getCuatrimestre() == inv.getArgument(1))
                        .findFirst());
    }

    private ProgCuatrimestralMetaFisica programar(long id, int anio, double c1, double c2, double c3) {
        ProgCuatrimestralMetaFisica prog = ProgCuatrimestralMetaFisica.builder().id(id).etapaMetaFisica(etapaMeta)
                .anio(anio).montoCuatrimestre1(BigDecimal.valueOf(c1)).montoCuatrimestre2(BigDecimal.valueOf(c2))
                .montoCuatrimestre3(BigDecimal.valueOf(c3)).build();
        when(progRepository.findByEtapaMetaFisicaIdAndAnio(20L, anio)).thenReturn(Optional.of(prog));
        return prog;
    }

    private void ejecutado(ProgCuatrimestralMetaFisica prog, Cuatrimestre periodo, double porcentaje) {
        avances.add(AvanceCuatriMetaFisica.builder().programacionMeta(prog).cuatrimestre(periodo)
                .avanceCuatrimestre(BigDecimal.valueOf(porcentaje)).build());
    }

    private void guardar(Cuatrimestre periodo, double avance) {
        GuardarAvanceMetasEstudioRequestDto request = new GuardarAvanceMetasEstudioRequestDto()
                .addEtapasItem(new EtapaAvanceMetasRequestDto(NombreEtapaDto.PERFIL, avance));
        service.guardarAvanceMetasEstudio(CUP, ANIO, CuatrimestreDto.valueOf(periodo.name()), request);
    }

    private EstadoAvanceMetasDto estado(Cuatrimestre periodo) {
        return service.obtenerAvanceMetasEstudio(CUP, ANIO, CuatrimestreDto.valueOf(periodo.name()))
                .getEtapas().get(0).getEstado();
    }

    // --- RN-C.b / RN-B.b / RN-B.d ---------------------------------------------------------------

    @Test
    void guardar_avanceQueSuperaElProgramadoAnualSinLlegarAl100_esRechazado() {
        programar(30L, ANIO, 25, 25, 25);

        assertThatThrownBy(() -> guardar(Cuatrimestre.CUATRIMESTRE_I, 80))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessage("El porcentaje total registrado supera el 100%")
                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getCodigo())
                        .isEqualTo("PORCENTAJE_SUPERA_PROGRAMADO_ANUAL"));
        verify(avanceRepository, never()).save(any());
    }

    @Test
    void guardar_acumuladoDelAnioQueSuperaElProgramadoAnual_esRechazado() {
        ProgCuatrimestralMetaFisica prog = programar(30L, ANIO, 25, 25, 25);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_I, 25);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_II, 25);

        // 25 + 25 + 30 = 80 > 75 ("Programado en el Año").
        assertThatThrownBy(() -> guardar(Cuatrimestre.CUATRIMESTRE_III, 30))
                .isInstanceOf(ValidacionNegocioException.class);
        verify(avanceRepository, never()).save(any());
    }

    @Test
    void guardar_avanceIgualAlProgramadoAnual_seAcepta() {
        programar(30L, ANIO, 25, 25, 25);

        guardar(Cuatrimestre.CUATRIMESTRE_I, 75);

        verify(avanceRepository).save(any(AvanceCuatriMetaFisica.class));
    }

    @Test
    void guardar_conProgramadoDelCuatrimestreIgualACero_noSeBloqueaPorElProgramadoAnual() {
        programar(30L, ANIO, 0, 50, 0);

        // RN-B.b: 60 > 50 del año, pero el cuatrimestre I no tiene programación.
        guardar(Cuatrimestre.CUATRIMESTRE_I, 60);

        verify(avanceRepository).save(any(AvanceCuatriMetaFisica.class));
    }

    @Test
    void guardar_conExcepcionRnBb_sigueAplicandoElTopeDel100() {
        ProgCuatrimestralMetaFisica anterior = programar(29L, ANIO - 1, 50, 0, 0);
        ejecutado(anterior, Cuatrimestre.CUATRIMESTRE_I, 50);
        programar(30L, ANIO, 0, 0, 0);

        assertThatThrownBy(() -> guardar(Cuatrimestre.CUATRIMESTRE_I, 60))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getCodigo())
                        .isEqualTo("PORCENTAJE_SUPERA_100"));
    }

    // --- RN-B.c -----------------------------------------------------------------------------------

    @Test
    void estado_mayorAlProgramadoAlCuatrimestreSinSobrepasarElAnual_esAdelantado() {
        ProgCuatrimestralMetaFisica prog = programar(30L, ANIO, 25, 25, 25);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_I, 40);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_II, 35);

        // 75 al cuatrimestre II: > 50 programado al cuatrimestre, = 75 "Programado en el Año".
        assertThat(estado(Cuatrimestre.CUATRIMESTRE_II)).isEqualTo(EstadoAvanceMetasDto.ADELANTADO);
    }

    @Test
    void estado_queSobrepasaElProgramadoEnElAnio_noEsAdelantado() {
        ProgCuatrimestralMetaFisica prog = programar(30L, ANIO, 25, 25, 25);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_I, 50);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_II, 30);

        assertThat(estado(Cuatrimestre.CUATRIMESTRE_II)).isNull();
    }

    @Test
    void estado_conTotalMetaEjecutadaAl100_esFinalizado() {
        ProgCuatrimestralMetaFisica anterior = programar(29L, ANIO - 1, 60, 0, 0);
        ejecutado(anterior, Cuatrimestre.CUATRIMESTRE_I, 60);
        ProgCuatrimestralMetaFisica prog = programar(30L, ANIO, 20, 20, 0);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_I, 20);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_II, 20);

        assertThat(estado(Cuatrimestre.CUATRIMESTRE_II)).isEqualTo(EstadoAvanceMetasDto.FINALIZADO);
    }

    @Test
    void estado_cumplirElProgramadoEnElAnioSinConcluirElEstudio_esATiempoYNoFinalizado() {
        ProgCuatrimestralMetaFisica prog = programar(30L, ANIO, 25, 25, 25);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_I, 25);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_II, 25);
        ejecutado(prog, Cuatrimestre.CUATRIMESTRE_III, 25);

        // Estudio plurianual: 75 = "Programado en el Año", pero "Total meta ejecutada" = 75 < 100.
        assertThat(estado(Cuatrimestre.CUATRIMESTRE_III)).isEqualTo(EstadoAvanceMetasDto.A_TIEMPO);
    }

    // --- SF-4: alcance del reporte ----------------------------------------------------------------

    @Test
    void generarReporte_actorTecnicoUrp_ignoraElParametroYUsaSuPropiaUnidadEjecutora() {
        service.generarReporteAvanceMetas(999L, ANIO, CuatrimestreDto.CUATRIMESTRE_I, "EXCEL");

        verify(etapaMetaRepository)
                .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(7L);
        verify(etapaMetaRepository, never())
                .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(999L);
    }

    @Test
    void generarReporte_actorTecnicoPre_respetaLaUnidadEjecutoraSolicitada() {
        when(actorContexto.exigirRol(any(RolUsuario[].class)))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());

        service.generarReporteAvanceMetas(999L, ANIO, CuatrimestreDto.CUATRIMESTRE_I, "PDF");

        verify(etapaMetaRepository)
                .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(999L);
    }
}
