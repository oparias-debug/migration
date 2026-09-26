package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioCampoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Viabilidad;
import sv.gob.mh.siip.model.preinversion.dto.CampoFichaViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.CargarDocumentoViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioCampoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.CampoFichaViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ViabilidadRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link ViabilidadServiceImpl} (CU-PRE-24). */
class ViabilidadServiceImplTest {

    private static final Long ID_PROYECTO = 7L;

    private ProyectoRepository proyectos;
    private RevisionViabilidadRepository revisiones;
    private ViabilidadRepository viabilidades;
    private UsuarioRepository usuarios;
    private NotificacionService notificaciones;
    private DocumentosViabilidad documentos;
    private FiltrosPosterioresViabilidad filtros;
    private FichaViabilidadEnsamblador ensamblador;
    private ActorContexto actor;
    private ViabilidadServiceImpl service;

    private UnidadEjecutora unidad;
    private Usuario tecnicoUrp;
    private Usuario viabilizador;
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        proyectos = mock(ProyectoRepository.class);
        revisiones = mock(RevisionViabilidadRepository.class);
        viabilidades = mock(ViabilidadRepository.class);
        usuarios = mock(UsuarioRepository.class);
        notificaciones = mock(NotificacionService.class);
        documentos = mock(DocumentosViabilidad.class);
        filtros = mock(FiltrosPosterioresViabilidad.class);
        ensamblador = mock(FichaViabilidadEnsamblador.class);
        actor = mock(ActorContexto.class);
        service = new ViabilidadServiceImpl(proyectos, revisiones, viabilidades, usuarios, notificaciones, documentos,
                filtros, ensamblador, actor);

        unidad = UnidadEjecutora.builder().id(3L).build();
        tecnicoUrp = Usuario.builder().id(10L).nombreUsuario("urp").rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidad)
                .build();
        viabilizador = Usuario.builder().id(20L).nombreUsuario("viab").rol(RolUsuario.VIABILIZADOR).build();
        proyecto = Proyecto.builder().id(ID_PROYECTO).cup("00123").nombre("Proyecto").estado(EstadoProyecto.EN_FORMULACION)
                .unidadEjecutora(unidad).build();
        when(proyectos.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(revisiones.findFirstByProyectoIdOrderByNumeroDesc(ID_PROYECTO)).thenReturn(Optional.empty());
    }

    private void comoTecnico() {
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.VIABILIZADOR)).thenReturn(tecnicoUrp);
    }

    private void comoViabilizador() {
        when(actor.exigirRol(RolUsuario.VIABILIZADOR)).thenReturn(viabilizador);
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.VIABILIZADOR)).thenReturn(viabilizador);
    }

    private RevisionViabilidad revision(EstadoRevisionViabilidad estado, String observaciones) {
        RevisionViabilidad revision = RevisionViabilidad.builder().id(50L).proyecto(proyecto).numero(1).estado(estado)
                .solicitante(tecnicoUrp).fechaSolicitud(LocalDateTime.now()).observacionesGenerales(observaciones)
                .comentarios(new ArrayList<>()).build();
        when(revisiones.findFirstByProyectoIdOrderByNumeroDesc(ID_PROYECTO)).thenReturn(Optional.of(revision));
        return revision;
    }

    private RevisionViabilidad emitida(boolean habilitaElegibilidad) {
        RevisionViabilidad revision = revision(EstadoRevisionViabilidad.EMITIDA, "Justificación");
        revision.setFechaCierre(LocalDateTime.now());
        revision.setHabilitaElegibilidad(habilitaElegibilidad);
        return revision;
    }

    private static GuardarComentariosViabilidadRequestDto request(String observaciones,
            ComentarioCampoViabilidadDto... comentarios) {
        GuardarComentariosViabilidadRequestDto request = new GuardarComentariosViabilidadRequestDto(new ArrayList<>(List.of(comentarios)));
        request.setObservacionesGeneralesJustificacion(observaciones);
        return request;
    }

    private static ComentarioCampoViabilidadDto comentario(CampoFichaViabilidadDto campo, String texto) {
        return new ComentarioCampoViabilidadDto(campo, texto);
    }

    @Nested
    class ConsultarFicha {

        @Test
        void tecnicoConDocumentoYSinSolicitudPuedeSolicitar() {
            comoTecnico();
            when(documentos.tieneDocumentoPreinversion(ID_PROYECTO)).thenReturn(true);
            DocumentoViabilidad documento = DocumentoViabilidad.builder().id(1L)
                    .tipoDocumento(TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION).nombreArchivo("p.pdf")
                    .fechaCarga(LocalDateTime.now()).build();
            when(documentos.listar(ID_PROYECTO)).thenReturn(List.of(documento));

            FichaViabilidadResponseDto ficha = service.consultarFicha(ID_PROYECTO);

            assertThat(ficha.getCup()).isEqualTo("00123");
            assertThat(ficha.getEstadoProyecto()).isEqualTo("En Formulación");
            assertThat(ficha.getDocumentos()).singleElement()
                    .satisfies(d -> assertThat(d.getTipoDocumento()).isEqualTo(TipoDocumentoViabilidadDto.DOCUMENTO_PREINVERSION));
            assertThat(ficha.getComentariosViabilizador()).isEmpty();
            assertThat(ficha.getObservacionesGeneralesJustificacion()).isNull();
            assertThat(ficha.getAccionesDisponibles().getSolicitarViabilidad()).isTrue();
            assertThat(ficha.getAccionesDisponibles().getEnviarComentarios()).isFalse();
            verify(ensamblador).completarCamposDeConsulta(ficha, ID_PROYECTO);
        }

        @Test
        void viabilizadorConRevisionEnCursoVeLosComentariosYSusAcciones() {
            comoViabilizador();
            RevisionViabilidad revision = revision(EstadoRevisionViabilidad.EN_CURSO, "Cumple");
            revision.getComentarios().add(new ComentarioCampoViabilidad(CampoFichaViabilidad.PRODUCTOS, "Ok"));

            FichaViabilidadResponseDto ficha = service.consultarFicha(ID_PROYECTO);

            assertThat(ficha.getComentariosViabilizador())
                    .containsExactly(comentario(CampoFichaViabilidadDto.PRODUCTOS, "Ok"));
            assertThat(ficha.getObservacionesGeneralesJustificacion()).isEqualTo("Cumple");
            assertThat(ficha.getAccionesDisponibles().getSolicitarViabilidad()).isFalse();
            assertThat(ficha.getAccionesDisponibles().getGuardarComentarios()).isTrue();
            assertThat(ficha.getAccionesDisponibles().getEnviarComentarios()).isTrue();
            assertThat(ficha.getAccionesDisponibles().getEmitirViabilidad()).isTrue();
            assertThat(ficha.getAccionesDisponibles().getIrAElegibilidad()).isFalse();
        }

        @Test
        void trasEmitirPorPrimeraVezSoloQuedaIrAElegibilidad() {
            comoViabilizador();
            emitida(true);

            FichaViabilidadResponseDto ficha = service.consultarFicha(ID_PROYECTO);

            assertThat(ficha.getAccionesDisponibles().getGuardarComentarios()).isFalse();
            assertThat(ficha.getAccionesDisponibles().getEmitirViabilidad()).isFalse();
            assertThat(ficha.getAccionesDisponibles().getIrAElegibilidad()).isTrue();
        }

        @Test
        void siLaOtDevolvioElProyectoLaFichaVuelveAAdmitirSolicitud() {
            comoTecnico();
            RevisionViabilidad revision = emitida(false);
            when(filtros.otDevolvioDespuesDe(ID_PROYECTO, revision.getFechaCierre())).thenReturn(true);
            when(documentos.tieneDocumentoPreinversion(ID_PROYECTO)).thenReturn(true);

            FichaViabilidadResponseDto ficha = service.consultarFicha(ID_PROYECTO);

            assertThat(ficha.getAccionesDisponibles().getSolicitarViabilidad()).isTrue();
            assertThat(ficha.getAccionesDisponibles().getIrAElegibilidad()).isFalse();
        }

        @Test
        void proyectoInexistenteRespondeProyectoNoEncontrado() {
            comoViabilizador();
            when(proyectos.findById(ID_PROYECTO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.consultarFicha(ID_PROYECTO))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .extracting(e -> ((RecursoNoEncontradoException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.PROYECTO_NO_ENCONTRADO);
        }

        @Test
        void tecnicoDeOtraUnidadEjecutoraNoAccede() {
            tecnicoUrp.setUnidadEjecutora(UnidadEjecutora.builder().id(99L).build());
            comoTecnico();

            assertThatThrownBy(() -> service.consultarFicha(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
        }

        @Test
        void proyectoSinUnidadEjecutoraNoEsAccesibleParaUnActorConUnidad() {
            proyecto.setUnidadEjecutora(null);
            comoTecnico();

            assertThatThrownBy(() -> service.consultarFicha(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
        }
    }

    @Nested
    class CargarDocumento {

        private final MockMultipartFile archivo = new MockMultipartFile("archivo", "p.pdf", "application/pdf", new byte[] {1});

        @Test
        void delegaLaCargaYDevuelveElDocumento() {
            comoTecnico();
            DocumentoViabilidad documento = DocumentoViabilidad.builder().id(1L)
                    .tipoDocumento(TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION).nombreArchivo("p.pdf")
                    .fechaCarga(LocalDateTime.now()).build();
            when(documentos.cargar(proyecto, TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION, archivo, tecnicoUrp))
                    .thenReturn(documento);
            when(documentos.tieneDocumentoPreinversion(ID_PROYECTO)).thenReturn(true);

            CargarDocumentoViabilidadResponseDto respuesta = service.cargarDocumento(ID_PROYECTO,
                    TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION, archivo);

            assertThat(respuesta.getDocumento().getDocumentoId()).isEqualTo(1L);
            assertThat(respuesta.getAccionesDisponibles().getSolicitarViabilidad()).isTrue();
        }

        @Test
        void conSolicitudEnCursoNoAdmiteCambios() {
            comoTecnico();
            revision(EstadoRevisionViabilidad.EN_CURSO, null);

            assertThatThrownBy(() -> service.cargarDocumento(ID_PROYECTO, TipoDocumentoViabilidad.OTRO_DOCUMENTO, archivo))
                    .isInstanceOf(ConflictoEstadoException.class)
                    .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.SOLICITUD_VIABILIDAD_EN_CURSO);
            verify(documentos, never()).cargar(any(), any(), any(), any());
        }
    }

    @Nested
    class SolicitarViabilidad {

        @Test
        void abreLaPrimeraRevisionBloqueaLaFormulacionYNotificaAlViabilizador() {
            comoTecnico();
            when(documentos.tieneDocumentoPreinversion(ID_PROYECTO)).thenReturn(true);
            when(usuarios.findByRolAndActivoTrue(RolUsuario.VIABILIZADOR)).thenReturn(List.of(viabilizador));

            service.solicitarViabilidad(ID_PROYECTO);

            ArgumentCaptor<RevisionViabilidad> captor = ArgumentCaptor.forClass(RevisionViabilidad.class);
            verify(revisiones).save(captor.capture());
            assertThat(captor.getValue().getNumero()).isEqualTo(1);
            assertThat(captor.getValue().getEstado()).isEqualTo(EstadoRevisionViabilidad.EN_CURSO);
            assertThat(captor.getValue().getSolicitante()).isEqualTo(tecnicoUrp);
            assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.EN_VIABILIDAD);
            assertThat(proyecto.getEstado().bloqueaFormulacion()).isTrue();
            verify(notificaciones).notificarSolicitudViabilidad(proyecto, List.of(viabilizador));
        }

        @Test
        void trasUnaDevolucionNumeraLaSiguienteRevision() {
            comoTecnico();
            revision(EstadoRevisionViabilidad.DEVUELTA, "Ajustar").setNumero(2);
            when(documentos.tieneDocumentoPreinversion(ID_PROYECTO)).thenReturn(true);

            service.solicitarViabilidad(ID_PROYECTO);

            ArgumentCaptor<RevisionViabilidad> captor = ArgumentCaptor.forClass(RevisionViabilidad.class);
            verify(revisiones).save(captor.capture());
            assertThat(captor.getValue().getNumero()).isEqualTo(3);
        }

        @Test
        void sinDocumentoDePreinversionSeRechaza() {
            comoTecnico();

            assertThatThrownBy(() -> service.solicitarViabilidad(ID_PROYECTO))
                    .isInstanceOf(ReglaNegocioException.class)
                    .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.DOCUMENTO_PREINVERSION_REQUERIDO);
            verify(revisiones, never()).save(any());
        }

        @Test
        void conComentariosDeOtSinResponderSeRechazaConElMensajeDeRn11() {
            comoTecnico();
            when(documentos.tieneDocumentoPreinversion(ID_PROYECTO)).thenReturn(true);
            when(filtros.tieneComentariosOtSinResponder(ID_PROYECTO)).thenReturn(true);

            assertThatThrownBy(() -> service.solicitarViabilidad(ID_PROYECTO))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessage(ViabilidadServiceImpl.MENSAJE_COMENTARIOS_OT_SIN_RESPONDER)
                    .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER);
        }

        @Test
        void conLaFichaDeshabilitadaSeRechaza() {
            comoTecnico();
            emitida(true);

            assertThatThrownBy(() -> service.solicitarViabilidad(ID_PROYECTO))
                    .isInstanceOf(ConflictoEstadoException.class)
                    .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.FICHA_VIABILIDAD_DESHABILITADA);
        }
    }

    @Nested
    class GuardarComentarios {

        @Test
        void reemplazaLosComentariosYDescartaLasCeldasVacias() {
            comoViabilizador();
            RevisionViabilidad revision = revision(EstadoRevisionViabilidad.EN_CURSO, null);
            revision.getComentarios().add(new ComentarioCampoViabilidad(CampoFichaViabilidad.DESCRIPCION, "Anterior"));

            GuardarComentariosViabilidadResponseDto respuesta = service.guardarComentarios(ID_PROYECTO,
                    request("  Justificación  ", comentario(CampoFichaViabilidadDto.OBJETIVO_GENERAL, "  Precisar  "),
                            comentario(CampoFichaViabilidadDto.PRODUCTOS, "   "),
                            comentario(CampoFichaViabilidadDto.COSTO_OPERACION, null)));

            assertThat(respuesta.getComentariosViabilizador())
                    .containsExactly(comentario(CampoFichaViabilidadDto.OBJETIVO_GENERAL, "Precisar"));
            assertThat(respuesta.getObservacionesGeneralesJustificacion()).isEqualTo("Justificación");
            assertThat(respuesta.getAccionesDisponibles().getEmitirViabilidad()).isTrue();
            verify(revisiones).save(revision);
        }

        @Test
        void observacionesEnBlancoQuedanSinRegistrarYNoHabilitanEmitir() {
            comoViabilizador();
            revision(EstadoRevisionViabilidad.EN_CURSO, "Anterior");
            GuardarComentariosViabilidadRequestDto sinComentarios = request("   ");
            sinComentarios.setComentariosViabilizador(null);

            GuardarComentariosViabilidadResponseDto respuesta = service.guardarComentarios(ID_PROYECTO, sinComentarios);

            assertThat(respuesta.getObservacionesGeneralesJustificacion()).isNull();
            assertThat(respuesta.getComentariosViabilizador()).isEmpty();
            assertThat(respuesta.getAccionesDisponibles().getEmitirViabilidad()).isFalse();
        }

        @Test
        void campoRepetidoEsSolicitudInvalida() {
            comoViabilizador();
            revision(EstadoRevisionViabilidad.EN_CURSO, null);

            GuardarComentariosViabilidadRequestDto repetido = request(null,
                    comentario(CampoFichaViabilidadDto.DESCRIPCION, "a"),
                    comentario(CampoFichaViabilidadDto.DESCRIPCION, "b"));

            assertThatThrownBy(() -> service.guardarComentarios(ID_PROYECTO, repetido))
                    .isInstanceOf(ValidacionNegocioException.class)
                    .hasMessageContaining("más de un comentario");
        }

        @Test
        void comentarioSinCampoEsSolicitudInvalida() {
            comoViabilizador();
            revision(EstadoRevisionViabilidad.EN_CURSO, null);

            GuardarComentariosViabilidadRequestDto sinCampo = request(null, comentario(null, "a"));

            assertThatThrownBy(() -> service.guardarComentarios(ID_PROYECTO, sinCampo))
                    .isInstanceOf(ValidacionNegocioException.class)
                    .hasMessageContaining("debe indicar el campo");
        }

        @Test
        void textosDemasiadoLargosSonSolicitudInvalida() {
            comoViabilizador();
            revision(EstadoRevisionViabilidad.EN_CURSO, null);
            String largo = "x".repeat(ViabilidadServiceImpl.LONGITUD_MAXIMA_TEXTO + 1);

            GuardarComentariosViabilidadRequestDto comentarioLargo = request(null,
                    comentario(CampoFichaViabilidadDto.DESCRIPCION, largo));
            GuardarComentariosViabilidadRequestDto observacionesLargas = request(largo);

            assertThatThrownBy(() -> service.guardarComentarios(ID_PROYECTO, comentarioLargo))
                    .isInstanceOf(ValidacionNegocioException.class)
                    .hasMessageContaining("supera");
            assertThatThrownBy(() -> service.guardarComentarios(ID_PROYECTO, observacionesLargas))
                    .isInstanceOf(ValidacionNegocioException.class)
                    .hasMessageContaining("observaciones generales");
        }

        @Test
        void sinRevisionEnCursoNoHayBorradorQueGuardar() {
            comoViabilizador();
            GuardarComentariosViabilidadRequestDto borrador = request("Obs");

            assertThatThrownBy(() -> service.guardarComentarios(ID_PROYECTO, borrador))
                    .isInstanceOf(ConflictoEstadoException.class)
                    .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.SOLICITUD_VIABILIDAD_NO_VIGENTE);
        }
    }

    @Nested
    class EnviarComentarios {

        @Test
        void devuelveElProyectoRegistraElResultadoYNotificaAlSolicitante() {
            comoViabilizador();
            RevisionViabilidad revision = revision(EstadoRevisionViabilidad.EN_CURSO, "Ajustar el presupuesto");

            EnviarComentariosViabilidadResponseDto respuesta = service.enviarComentarios(ID_PROYECTO);

            assertThat(respuesta.getEstadoProyecto()).isEqualTo("Observado");
            assertThat(revision.getEstado()).isEqualTo(EstadoRevisionViabilidad.DEVUELTA);
            assertThat(revision.getViabilizador()).isEqualTo(viabilizador);
            assertThat(revision.getFechaCierre()).isNotNull();
            assertThat(proyecto.getEstado().bloqueaFormulacion()).isFalse();
            ArgumentCaptor<Viabilidad> resultado = ArgumentCaptor.forClass(Viabilidad.class);
            verify(viabilidades).save(resultado.capture());
            assertThat(resultado.getValue().getResultado()).isEqualTo(ResultadoViabilidad.OBSERVADO);
            assertThat(resultado.getValue().getObservaciones()).isEqualTo("Ajustar el presupuesto");
            verify(notificaciones).notificarComentariosViabilidad(proyecto, tecnicoUrp);
        }

        @Test
        void sinSolicitudVigenteSeRechaza() {
            comoViabilizador();
            revision(EstadoRevisionViabilidad.DEVUELTA, null);

            assertThatThrownBy(() -> service.enviarComentarios(ID_PROYECTO))
                    .isInstanceOf(ConflictoEstadoException.class)
                    .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.SOLICITUD_VIABILIDAD_NO_VIGENTE);
        }
    }

    @Nested
    class EmitirViabilidad {

        @Test
        void laPrimeraEmisionHabilitaElegibilidad() {
            comoViabilizador();
            RevisionViabilidad revision = revision(EstadoRevisionViabilidad.EN_CURSO, "Cumple");

            EmitirViabilidadResponseDto respuesta = service.emitirViabilidad(ID_PROYECTO);

            assertThat(respuesta.getEstadoProyecto()).isEqualTo("Proyecto viable");
            assertThat(respuesta.getElegibilidadHabilitada()).isTrue();
            assertThat(revision.getEstado()).isEqualTo(EstadoRevisionViabilidad.EMITIDA);
            assertThat(revision.getHabilitaElegibilidad()).isTrue();
            assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.VIABLE);
            ArgumentCaptor<Viabilidad> resultado = ArgumentCaptor.forClass(Viabilidad.class);
            verify(viabilidades).save(resultado.capture());
            assertThat(resultado.getValue().getResultado()).isEqualTo(ResultadoViabilidad.VIABLE);
            assertThat(resultado.getValue().getEvaluador()).isEqualTo(viabilizador);
            verify(notificaciones).notificarEmisionViabilidad(proyecto, tecnicoUrp);
        }

        @Test
        void siYaPasoPorElegibilidadSeSaltaALaOt() {
            comoViabilizador();
            revision(EstadoRevisionViabilidad.EN_CURSO, "Cumple");
            when(filtros.yaPasoPorElegibilidad(ID_PROYECTO)).thenReturn(true);

            EmitirViabilidadResponseDto respuesta = service.emitirViabilidad(ID_PROYECTO);

            assertThat(respuesta.getElegibilidadHabilitada()).isFalse();
        }

        @Test
        void sinJustificacionSeRechaza() {
            comoViabilizador();
            revision(EstadoRevisionViabilidad.EN_CURSO, null);

            assertThatThrownBy(() -> service.emitirViabilidad(ID_PROYECTO))
                    .isInstanceOf(ReglaNegocioException.class)
                    .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.JUSTIFICACION_VIABILIDAD_REQUERIDA);
            verify(viabilidades, never()).save(any());
        }

        @Test
        void conLaViabilidadYaEmitidaSeRechaza() {
            comoViabilizador();
            emitida(true);
            when(filtros.otDevolvioDespuesDe(eq(ID_PROYECTO), any())).thenReturn(false);

            assertThatThrownBy(() -> service.emitirViabilidad(ID_PROYECTO))
                    .isInstanceOf(ConflictoEstadoException.class)
                    .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                    .isEqualTo(ViabilidadServiceImpl.FICHA_VIABILIDAD_DESHABILITADA);
            verify(proyectos, never()).save(any());
            verify(usuarios, never()).findById(anyLong());
        }
    }
}
