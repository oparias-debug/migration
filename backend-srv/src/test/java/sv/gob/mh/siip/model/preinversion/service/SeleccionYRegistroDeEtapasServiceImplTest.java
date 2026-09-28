package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;
import sv.gob.mh.siip.security.ActorContexto;

/** La lógica de cada pantalla se prueba en su colaborador; aquí, el rol exigido y la delegación. */
class SeleccionYRegistroDeEtapasServiceImplTest {

    private static final Long ID = 7L;

    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final SeleccionEtapasRuta ruta = mock(SeleccionEtapasRuta.class);
    private final SeleccionEtapasRegistro registro = mock(SeleccionEtapasRegistro.class);
    private final SeleccionEtapasFichaGeneral fichaGeneral = mock(SeleccionEtapasFichaGeneral.class);
    private final SeleccionEtapasFichaEmergencia fichaEmergencia = mock(SeleccionEtapasFichaEmergencia.class);
    private final SeleccionYRegistroDeEtapasServiceImpl service = new SeleccionYRegistroDeEtapasServiceImpl(
            actorContexto, ruta, registro, fichaGeneral, fichaEmergencia);

    @Test
    void obtenerRutaPreinversion_exigeActorYDelegaEnLaRuta() {
        RutaPreinversionDto dto = new RutaPreinversionDto();
        when(ruta.obtener(ID)).thenReturn(dto);

        assertThat(service.obtenerRutaPreinversion(ID)).isSameAs(dto);
        InOrder orden = inOrder(actorContexto, ruta);
        orden.verify(actorContexto).exigir();
        orden.verify(ruta).obtener(ID);
    }

    @Test
    void generarRutaPreinversion_exigeTecnicoUrp() {
        CriteriosCalificacionDto criterios = new CriteriosCalificacionDto();
        RutaPreinversionSugeridaDto dto = new RutaPreinversionSugeridaDto();
        when(ruta.generar(ID, criterios)).thenReturn(dto);

        assertThat(service.generarRutaPreinversion(ID, criterios)).isSameAs(dto);
        InOrder orden = inOrder(actorContexto, ruta);
        orden.verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP);
        orden.verify(ruta).generar(ID, criterios);
    }

    @Test
    void aceptarRutaPreinversion_exigeTecnicoUrp() {
        CriteriosCalificacionDto criterios = new CriteriosCalificacionDto();
        RutaPreinversionDto dto = new RutaPreinversionDto();
        when(ruta.aceptar(ID, criterios)).thenReturn(dto);

        assertThat(service.aceptarRutaPreinversion(ID, criterios)).isSameAs(dto);
        InOrder orden = inOrder(actorContexto, ruta);
        orden.verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP);
        orden.verify(ruta).aceptar(ID, criterios);
    }

    @Test
    void modificarRutaPreinversion_exigeTecnicoUrp() {
        ModificarRutaPreinversionRequestDto request = new ModificarRutaPreinversionRequestDto();
        RutaPreinversionDto dto = new RutaPreinversionDto();
        when(ruta.modificar(ID, request)).thenReturn(dto);

        assertThat(service.modificarRutaPreinversion(ID, request)).isSameAs(dto);
        InOrder orden = inOrder(actorContexto, ruta);
        orden.verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP);
        orden.verify(ruta).modificar(ID, request);
    }

    @Test
    void listarEtapas_exigeActorYDelegaEnElRegistro() {
        List<EtapaDto> etapas = List.of(new EtapaDto());
        when(registro.listar(ID)).thenReturn(etapas);

        assertThat(service.listarEtapas(ID)).isSameAs(etapas);
        InOrder orden = inOrder(actorContexto, registro);
        orden.verify(actorContexto).exigir();
        orden.verify(registro).listar(ID);
    }

    @Test
    void actualizarEtapas_exigeTecnicoUrp() {
        ActualizarEtapasRequestDto request = new ActualizarEtapasRequestDto();
        List<EtapaDto> etapas = List.of(new EtapaDto());
        when(registro.actualizar(ID, request)).thenReturn(etapas);

        assertThat(service.actualizarEtapas(ID, request)).isSameAs(etapas);
        InOrder orden = inOrder(actorContexto, registro);
        orden.verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP);
        orden.verify(registro).actualizar(ID, request);
    }

    @Test
    void obtenerFichaInformacionGeneral_exigeTecnicoUrpOCoordinadorSymp() {
        FichaInformacionGeneralDto dto = new FichaInformacionGeneralDto();
        when(fichaGeneral.obtener(ID)).thenReturn(dto);

        assertThat(service.obtenerFichaInformacionGeneral(ID)).isSameAs(dto);
        InOrder orden = inOrder(actorContexto, fichaGeneral);
        orden.verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP, RolUsuario.COORDINADOR_SYMP);
        orden.verify(fichaGeneral).obtener(ID);
    }

    @Test
    void seleccionarCoEjecutor_exigeCoordinadorSymp() {
        SeleccionCoEjecutorRequestDto request = new SeleccionCoEjecutorRequestDto();
        FichaInformacionGeneralDto dto = new FichaInformacionGeneralDto();
        when(fichaGeneral.seleccionarCoEjecutor(ID, request)).thenReturn(dto);

        assertThat(service.seleccionarCoEjecutor(ID, request)).isSameAs(dto);
        InOrder orden = inOrder(actorContexto, fichaGeneral);
        orden.verify(actorContexto).exigirRol(RolUsuario.COORDINADOR_SYMP);
        orden.verify(fichaGeneral).seleccionarCoEjecutor(ID, request);
    }

    @Test
    void obtenerFichaEmergencia_exigeTecnicoUrp() {
        FichaEmergenciaDto dto = new FichaEmergenciaDto();
        when(fichaEmergencia.obtener(ID)).thenReturn(dto);

        assertThat(service.obtenerFichaEmergencia(ID)).isSameAs(dto);
        InOrder orden = inOrder(actorContexto, fichaEmergencia);
        orden.verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP);
        orden.verify(fichaEmergencia).obtener(ID);
    }

    @Test
    void registrarFichaEmergencia_exigeTecnicoUrp() {
        FichaEmergenciaRequestDto request = new FichaEmergenciaRequestDto();
        FichaEmergenciaDto dto = new FichaEmergenciaDto();
        when(fichaEmergencia.registrar(ID, request)).thenReturn(dto);

        assertThat(service.registrarFichaEmergencia(ID, request)).isSameAs(dto);
        InOrder orden = inOrder(actorContexto, fichaEmergencia);
        orden.verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP);
        orden.verify(fichaEmergencia).registrar(ID, request);
    }

    @Test
    void registrarFichaEmergencia_rolNoPermitido_noLlegaAlColaborador() {
        when(actorContexto.exigirRol(any(RolUsuario.class))).thenThrow(new AccesoDenegadoException("Sin permiso"));
        FichaEmergenciaRequestDto request = new FichaEmergenciaRequestDto();

        assertThatThrownBy(() -> service.registrarFichaEmergencia(ID, request))
                .isInstanceOf(AccesoDenegadoException.class);
        verifyNoInteractions(fichaEmergencia);
    }
}
