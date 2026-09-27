package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoRevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AvanceMetasFisicasPapRevisionServiceImplTest {

    private static final Long ID_UNIDAD = 5L;
    private static final int ANIO = 2028;

    private final RevisionAvancePapRepository revisionRepository = mock(RevisionAvancePapRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AvanceMetasFisicasPapRevisionServiceImpl service = new AvanceMetasFisicasPapRevisionServiceImpl(
            revisionRepository, mock(UsuarioRepository.class), mock(NotificacionService.class), actorContexto);

    private final RevisionAvancePap existente = RevisionAvancePap.builder()
            .idUnidadEjecutora(ID_UNIDAD).anio(ANIO).periodo(Cuatrimestre.CUATRIMESTRE_I)
            .comentarioReporteFinancieroDgicp("Financiero previo")
            .comentarioReporteMetasFisicasDgicp("Metas previo")
            .build();

    @BeforeEach
    void revisionExistenteYActorTecnicoPre() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        when(revisionRepository.findByIdUnidadEjecutoraAndAnioAndPeriodo(ID_UNIDAD, ANIO,
                Cuatrimestre.CUATRIMESTRE_I)).thenReturn(Optional.of(existente));
        when(revisionRepository.save(any(RevisionAvancePap.class))).then(returnsFirstArg());
    }

    @Test
    void finalizarRevisionAvance_sinComentarios_conservaLosComentariosRegistrados() {
        RevisionAvancePAPDto respuesta = service.finalizarRevisionAvance(
                new FinalizarRevisionAvanceRequestDto(ID_UNIDAD, ANIO, CuatrimestreDto.CUATRIMESTRE_I));

        assertThat(respuesta.getEstado()).isEqualTo(EstadoRevisionAvancePAPDto.REVISADO);
        assertThat(respuesta.getComentarioReporteFinancieroDgicp()).isEqualTo("Financiero previo");
        assertThat(respuesta.getComentarioReporteMetasFisicasDgicp()).isEqualTo("Metas previo");
    }

    @Test
    void finalizarRevisionAvance_conComentarios_reemplazaLosRegistrados() {
        RevisionAvancePAPDto respuesta = service.finalizarRevisionAvance(
                new FinalizarRevisionAvanceRequestDto(ID_UNIDAD, ANIO, CuatrimestreDto.CUATRIMESTRE_I)
                        .comentarioReporteFinancieroDgicp("Financiero nuevo")
                        .comentarioReporteMetasFisicasDgicp("Metas nuevo"));

        assertThat(respuesta.getComentarioReporteFinancieroDgicp()).isEqualTo("Financiero nuevo");
        assertThat(respuesta.getComentarioReporteMetasFisicasDgicp()).isEqualTo("Metas nuevo");
    }
}
