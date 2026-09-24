package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.HabilitacionModificacionMetasPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap;
import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.enums.EstadoPap;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionMetasPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionProgramacionPapRepository;
import sv.gob.mh.siip.security.ActorContexto;

class ProgramacionMetasFisicasPapServiceImplTest {

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final EtapaMetaFisicaPapRepository etapaMetaRepository = mock(EtapaMetaFisicaPapRepository.class);
    private final ProgCuatrimestralMetaFisicaRepository progRepository = mock(ProgCuatrimestralMetaFisicaRepository.class);
    private final HabilitacionModificacionMetasPapRepository habilitacionRepository = mock(
            HabilitacionModificacionMetasPapRepository.class);
    private final CalendarioEventoRepository calendarioEventoRepository = mock(CalendarioEventoRepository.class);
    private final RevisionProgramacionPapRepository revisionRepository = mock(RevisionProgramacionPapRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final NotificacionService notificacionService = mock(NotificacionService.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final ProgramacionMetasFisicasPapServiceImpl service = new ProgramacionMetasFisicasPapServiceImpl(
            proyectoRepository, etapaPreinversionRepository, etapaMetaRepository, progRepository,
            habilitacionRepository, calendarioEventoRepository, revisionRepository, usuarioRepository,
            notificacionService, actorContexto);

    private void mockActor(Usuario actor) {
        when(actorContexto.exigirRol(any(RolUsuario[].class))).thenReturn(actor);
    }

    @Test
    void listar_actorTecnicoUrp_ignoraElParametroYFiltraPorSuPropiaUnidadEjecutora() {
        UnidadEjecutora unidadDelActor = UnidadEjecutora.builder().id(7L).build();
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidadDelActor).build());
        when(etapaMetaRepository.buscar(eq(7L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        ProgramacionMetasFisicasPAPResponseDto respuesta = service.listar(999L, 2027, 0, 20);

        assertThat(respuesta.getIdUnidadEjecutora()).isEqualTo(7L);
        assertThat(respuesta.getContenido()).isEmpty();
    }

    private EtapaMetaFisicaPap filaConEtapa(Long idUnidadEjecutora) {
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(idUnidadEjecutora).build();
        Proyecto proyecto = Proyecto.builder().id(1L).cup("08040").nombre("Proyecto A").unidadEjecutora(unidad).build();
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(10L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        return EtapaMetaFisicaPap.builder().id(20L).etapaPreinversion(etapa).build();
    }

    @Test
    void listar_conActorInternoDgicp_incluyeComentariosDelReporte() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        EtapaMetaFisicaPap fila = filaConEtapa(5L);
        when(etapaMetaRepository.buscar(eq(5L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(fila)));
        when(progRepository.findByEtapaMetaFisicaId(fila.getId())).thenReturn(List.of());
        when(progRepository.findByEtapaMetaFisicaIdAndAnio(fila.getId(), 2027)).thenReturn(Optional.empty());
        when(revisionRepository.findByIdUnidadEjecutoraAndAnio(5L, 2027)).thenReturn(Optional.of(
                RevisionProgramacionPap.builder().comentariosReporteMetasFisicasDgicp("Revisar Total Año.").build()));

        ProgramacionMetasFisicasPAPResponseDto respuesta = service.listar(5L, 2027, 0, 20);

        assertThat(respuesta.getContenido().get(0).getComentariosReporteDgicp()).isEqualTo("Revisar Total Año.");
        assertThat(respuesta.getContenido().get(0).getMetaTotal()).isEqualTo(1d);
    }

    @Test
    void listar_conActorNoInternoDgicp_noIncluyeComentariosDelReporte() {
        UnidadEjecutora unidadDelActor = UnidadEjecutora.builder().id(5L).build();
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidadDelActor).build());
        EtapaMetaFisicaPap fila = filaConEtapa(5L);
        when(etapaMetaRepository.buscar(eq(5L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(fila)));
        when(progRepository.findByEtapaMetaFisicaId(fila.getId())).thenReturn(List.of());
        when(progRepository.findByEtapaMetaFisicaIdAndAnio(fila.getId(), 2027)).thenReturn(Optional.empty());

        ProgramacionMetasFisicasPAPResponseDto respuesta = service.listar(5L, 2027, 0, 20);

        assertThat(respuesta.getContenido().get(0).getComentariosReporteDgicp()).isNull();
    }

    private Proyecto proyectoConEstudio(String cup, Long idUnidadEjecutora) {
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(idUnidadEjecutora).build();
        Proyecto proyecto = Proyecto.builder().id(1L).cup(cup).nombre("Proyecto A").unidadEjecutora(unidad).build();
        when(proyectoRepository.findByCup(cup)).thenReturn(Optional.of(proyecto));
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(10L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        when(etapaPreinversionRepository.findByProyectoId(proyecto.getId())).thenReturn(List.of(etapa));
        when(etapaMetaRepository.findByEtapaPreinversionId(etapa.getId())).thenReturn(Optional.empty());
        when(etapaMetaRepository.findByEtapaPreinversionProyectoId(proyecto.getId())).thenReturn(List.of());
        return proyecto;
    }

    private GuardarProgramacionMetasEstudioRequestDto requestConPorcentajes(double c1, double c2, double c3) {
        return new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL)
                        .entregable(EntregableDto.ESTUDIO_DE_PERFIL)
                        .montoCuatrimestre1(c1).montoCuatrimestre2(c2).montoCuatrimestre3(c3));
    }

    private static ProgCuatrimestralMetaFisica progAnterior(double porcentaje) {
        return ProgCuatrimestralMetaFisica.builder().anio(2026).montoCuatrimestre1(BigDecimal.valueOf(porcentaje))
                .montoCuatrimestre2(BigDecimal.ZERO).montoCuatrimestre3(BigDecimal.ZERO).build();
    }

    /**
     * Proyecto mixto (mockup Anexo A.1): Perfil de arrastre (40% ejecutado en 2026) + Prefactibilidad
     * nueva (sin programación previa).
     */
    private Proyecto proyectoMixto() {
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(5L).build();
        Proyecto proyecto = Proyecto.builder().id(1L).cup("08041").nombre("Proyecto mixto").unidadEjecutora(unidad).build();
        when(proyectoRepository.findByCup("08041")).thenReturn(Optional.of(proyecto));
        EtapaPreinversion perfil = EtapaPreinversion.builder().id(10L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        EtapaPreinversion prefactibilidad = EtapaPreinversion.builder().id(11L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PREFACTIBILIDAD).build();
        when(etapaPreinversionRepository.findByProyectoId(1L)).thenReturn(List.of(perfil, prefactibilidad));
        EtapaMetaFisicaPap metaPerfil = EtapaMetaFisicaPap.builder().id(20L).etapaPreinversion(perfil)
                .entregable(Entregable.ESTUDIO_DE_PERFIL).build();
        when(etapaMetaRepository.findByEtapaPreinversionId(10L)).thenReturn(Optional.of(metaPerfil));
        when(etapaMetaRepository.findByEtapaPreinversionId(11L)).thenReturn(Optional.empty());
        when(etapaMetaRepository.findByEtapaPreinversionProyectoId(1L)).thenReturn(List.of(metaPerfil));
        when(progRepository.findByEtapaMetaFisicaId(20L)).thenReturn(List.of(progAnterior(40)));
        when(progRepository.findByEtapaMetaFisicaIdIn(List.of(20L))).thenReturn(List.of(progAnterior(40)));
        when(etapaMetaRepository.save(any(EtapaMetaFisicaPap.class))).thenAnswer(inv -> inv.getArgument(0));
        return proyecto;
    }

    /** Perfil (arrastre) sin "Entregable" en el request: el campo está deshabilitado (SF-1 paso 2). */
    private static GuardarProgramacionMetasEstudioRequestDto requestMixto(double perfilC1, double prefactibilidadC1,
            double prefactibilidadC2) {
        return new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL).montoCuatrimestre1(perfilC1))
                .addEtapasItem(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PREFACTIBILIDAD)
                        .entregable(EntregableDto.ESTUDIO_DE_PREFACTIBILIDAD)
                        .montoCuatrimestre1(prefactibilidadC1).montoCuatrimestre2(prefactibilidadC2));
    }

    @Test
    void guardarProgramacionMetasEstudio_proyectoMixto_etapaNuevaQueSuperaCien_usaCodigoNuevo() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoMixto();

        ValidacionNegocioException ex = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio("08041", 2027, requestMixto(60, 60, 50)));

        assertThat(ex.getCodigo()).isEqualTo("MONTO_SUPERA_100_NUEVO");
    }

    @Test
    void guardarProgramacionMetasEstudio_proyectoMixto_etapaArrastreQueSuperaPendiente_usaCodigoArrastre() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoMixto();

        ValidacionNegocioException ex = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio("08041", 2027, requestMixto(70, 80, 0)));

        assertThat(ex.getCodigo()).isEqualTo("PORCENTAJE_SUPERA_100_ARRASTRE");
    }

    @Test
    void obtenerProgramacionMetasEstudio_proyectoMixto_clasificaCadaEtapa() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoMixto();

        EstudioProgramacionMetasDto estudio = service.obtenerProgramacionMetasEstudio("08041", 2027);

        assertThat(estudio.getEsArrastre()).isTrue();
        assertThat(estudio.getEtapas()).extracting(EtapaMetaFisicaDto::getEsArrastre).containsExactly(true, false);
    }

    @Test
    void guardarProgramacionMetasEstudio_sinEntregableEnEtapaNueva_lanzaCampoObligatorio() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoConEstudio("08040", 5L);
        GuardarProgramacionMetasEstudioRequestDto request = new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL).montoCuatrimestre1(30d));

        ValidacionNegocioException ex = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio("08040", 2027, request));

        assertThat(ex.getCodigo()).isNull();
        assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo).containsExactly("PERFIL.entregable");
    }

    @Test
    void guardarProgramacionMetasEstudio_etapaArrastreSinEntregableEnRequest_usaElYaRegistrado() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoMixto();

        EstudioProgramacionMetasDto estudio = service.guardarProgramacionMetasEstudio("08041", 2027, requestMixto(20, 30, 0));

        assertThat(estudio.getEtapas().get(0).getEntregable()).isEqualTo(EntregableDto.ESTUDIO_DE_PERFIL);
    }

    @Test
    void guardarProgramacionMetasEstudio_sinNingunCuatrimestreRegistrado_lanzaValidacion() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoConEstudio("08040", 5L);

        ValidacionNegocioException ex = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio("08040", 2027, requestConPorcentajes(0, 0, 0)));

        assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo)
                .containsExactly("PERFIL.programacionCuatrimestral");
    }

    @Test
    void guardarProgramacionMetasEstudio_cuatrimestreFueraDeRango_lanzaValidacionPorCampo() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoConEstudio("08040", 5L);

        ValidacionNegocioException ex = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio("08040", 2027, requestConPorcentajes(-10, 20, 120)));

        assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo)
                .containsExactly("PERFIL.montoCuatrimestre1", "PERFIL.montoCuatrimestre3");
    }

    @Test
    void guardarProgramacionMetasEstudio_metaDesactivada_seReactivaAlGuardar() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoConEstudio("08040", 5L);
        EtapaMetaFisicaPap desactivada = EtapaMetaFisicaPap.builder().id(20L).activo(Boolean.FALSE).build();
        when(etapaMetaRepository.findByEtapaPreinversionId(10L)).thenReturn(Optional.of(desactivada));
        when(etapaMetaRepository.save(any(EtapaMetaFisicaPap.class))).thenAnswer(inv -> inv.getArgument(0));

        service.guardarProgramacionMetasEstudio("08040", 2027, requestConPorcentajes(30, 30, 30));

        assertThat(desactivada.getActivo()).isTrue();
    }

    private void periodoCerrado(Integer anio) {
        when(calendarioEventoRepository.findByTipoEventoAndAnioAndCuatrimestreIsNull(
                TipoEventoCalendario.PROGRAMACION_PAP, anio))
                .thenReturn(Optional.of(CalendarioEvento.builder().tipoEvento(TipoEventoCalendario.PROGRAMACION_PAP)
                        .anio(anio).estado(EstadoCalendarioEvento.CERRADO).build()));
    }

    @Test
    void finalizarRevision_fueraDelCalendario_lanzaPeriodoCerrado() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        periodoCerrado(2027);

        ConflictoEstadoException ex = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> service.finalizarRevision(new FinalizarRevisionRequestDto(5L, 2027)));

        assertThat(ex.getCodigo()).isEqualTo("PERIODO_CERRADO");
        verify(revisionRepository, never()).save(any());
    }

    @Test
    void finalizarRevision_fueraDelCalendarioConHabilitacion_permiteFinalizar() {
        mockActor(Usuario.builder().rol(RolUsuario.COORDINADOR_PRE).build());
        periodoCerrado(2027);
        when(habilitacionRepository.findByIdUnidadEjecutoraAndAnio(5L, 2027))
                .thenReturn(Optional.of(HabilitacionModificacionMetasPap.builder().idUnidadEjecutora(5L).anio(2027).build()));
        when(revisionRepository.save(any(RevisionProgramacionPap.class))).thenAnswer(inv -> inv.getArgument(0));

        RevisionProgramacionPAPDto respuesta = service.finalizarRevision(new FinalizarRevisionRequestDto(5L, 2027));

        assertThat(respuesta.getEstadoPap()).isEqualTo(EstadoPAPDto.PAP_REVISADO);
    }

    @Test
    void registrarRespuestaInstitucion_fueraDelCalendario_lanzaPeriodoCerrado() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        periodoCerrado(2027);

        ConflictoEstadoException ex = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> service.registrarRespuestaInstitucion(
                        new RegistrarRespuestaInstitucionRequestDto(5L, 2027, "Ajustes realizados.")));

        assertThat(ex.getCodigo()).isEqualTo("PERIODO_CERRADO");
        verify(revisionRepository, never()).save(any());
    }

    @Test
    void enviarRespuestaInstitucion_fueraDelCalendario_lanzaPeriodoCerradoYNoNotifica() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        periodoCerrado(2027);

        ConflictoEstadoException ex = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> service.enviarRespuestaInstitucion(new EnviarProgramacionARevisionDgicpRequestDto(5L, 2027)));

        assertThat(ex.getCodigo()).isEqualTo("PERIODO_CERRADO");
        verify(notificacionService, never()).notificarRespuestaInstitucion(any(), any(), any());
    }

    @Test
    void guardarProgramacionMetasEstudio_nuevoQueSuperaCien_lanzaValidacionConCodigoNuevo() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        proyectoConEstudio("08040", 5L);
        GuardarProgramacionMetasEstudioRequestDto request = requestConPorcentajes(50, 30, 30);

        ValidacionNegocioException ex = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio("08040", 2027, request));

        assertThat(ex.getCodigo()).isEqualTo("MONTO_SUPERA_100_NUEVO");
    }

    @Test
    void guardarProgramacionMetasEstudio_arrastreQueSuperaCien_lanzaValidacionConCodigoArrastre() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        Proyecto proyecto = proyectoConEstudio("08040", 5L);
        EtapaMetaFisicaPap etapaMeta = EtapaMetaFisicaPap.builder().id(20L).entregable(Entregable.ESTUDIO_DE_PERFIL).build();
        when(etapaMetaRepository.findByEtapaPreinversionProyectoId(proyecto.getId())).thenReturn(List.of(etapaMeta));
        when(progRepository.findByEtapaMetaFisicaIdIn(List.of(20L)))
                .thenReturn(List.of(sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica.builder()
                        .anio(2026).montoCuatrimestre1(java.math.BigDecimal.valueOf(40)).build()));
        when(etapaMetaRepository.findByEtapaPreinversionId(10L)).thenReturn(Optional.of(etapaMeta));
        when(progRepository.findByEtapaMetaFisicaId(20L))
                .thenReturn(List.of(sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica.builder()
                        .anio(2026).montoCuatrimestre1(java.math.BigDecimal.valueOf(40)).build()));
        GuardarProgramacionMetasEstudioRequestDto request = requestConPorcentajes(40, 20, 10);

        ValidacionNegocioException ex = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionMetasEstudio("08040", 2027, request));

        assertThat(ex.getCodigo()).isEqualTo("PORCENTAJE_SUPERA_100_ARRASTRE");
    }

    @Test
    void enviarProgramacionARevisionDgicp_actualizaEstadoYNotificaATecnicoPre() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(revisionRepository.findByIdUnidadEjecutoraAndAnio(5L, 2027)).thenReturn(Optional.empty());
        when(revisionRepository.save(any(RevisionProgramacionPap.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE)).thenReturn(List.of());

        RevisionProgramacionPAPDto respuesta = service
                .enviarProgramacionARevisionDgicp(new EnviarProgramacionARevisionDgicpRequestDto(5L, 2027));

        assertThat(respuesta.getEstadoPap()).isEqualTo(EstadoPAPDto.ENVIADO_A_REVISION_DGICP);
        verify(notificacionService).notificarProgramacionEnviadaARevision(eq(5L), eq(2027), any());
    }

    @Test
    void finalizarRevision_actualizaEstadoAPapRevisadoYGuardaComentarios() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        when(revisionRepository.findByIdUnidadEjecutoraAndAnio(5L, 2027))
                .thenReturn(Optional.of(RevisionProgramacionPap.builder().idUnidadEjecutora(5L).anio(2027)
                        .estadoPap(EstadoPap.ENVIADO_A_REVISION_DGICP).build()));
        when(revisionRepository.save(any(RevisionProgramacionPap.class))).thenAnswer(inv -> inv.getArgument(0));

        RevisionProgramacionPAPDto respuesta = service.finalizarRevision(new FinalizarRevisionRequestDto(5L, 2027)
                .comentariosReporteFinancieroDgicp("Sin observaciones.")
                .comentariosReporteMetasFisicasDgicp("Sin observaciones."));

        assertThat(respuesta.getEstadoPap()).isEqualTo(EstadoPAPDto.PAP_REVISADO);
        assertThat(respuesta.getComentariosReporteFinancieroDgicp()).isEqualTo("Sin observaciones.");
    }

    @Test
    void habilitarModificacionesMetasFueraPlazo_creaUnRegistroNuevoCuandoNoExiste() {
        mockActor(Usuario.builder().rol(RolUsuario.ADMINISTRADOR).build());
        when(habilitacionRepository.findByIdUnidadEjecutoraAndAnio(5L, 2027)).thenReturn(Optional.empty());

        service.habilitarModificacionesMetasFueraPlazo(new EnviarProgramacionARevisionDgicpRequestDto(5L, 2027));

        verify(habilitacionRepository).save(any(HabilitacionModificacionMetasPap.class));
    }
}
