package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.EscalaCalificacionSubcriterio;
import sv.gob.mh.siip.model.preinversion.domain.RangoInterpretacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;
import sv.gob.mh.siip.model.preinversion.repository.CriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.EscalaCalificacionSubcriterioRepository;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SubcriterioPriorizacionRepository;
import sv.gob.mh.siip.security.ActorContexto;

class CatalogoPriorizacionServiceImplTest {

    private final CriterioPriorizacionRepository criterioPriorizacionRepository = mock(
            CriterioPriorizacionRepository.class);
    private final SubcriterioPriorizacionRepository subcriterioPriorizacionRepository = mock(
            SubcriterioPriorizacionRepository.class);
    private final EscalaCalificacionSubcriterioRepository escalaCalificacionSubcriterioRepository = mock(
            EscalaCalificacionSubcriterioRepository.class);
    private final RangoInterpretacionPriorizacionRepository rangoInterpretacionPriorizacionRepository = mock(
            RangoInterpretacionPriorizacionRepository.class);
    private final CatalogosAdministracionMapper mapper = Mappers.getMapper(CatalogosAdministracionMapper.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final CatalogoPriorizacionServiceImpl service = new CatalogoPriorizacionServiceImpl(
            criterioPriorizacionRepository, subcriterioPriorizacionRepository,
            escalaCalificacionSubcriterioRepository, rangoInterpretacionPriorizacionRepository, mapper,
            actorContexto);

    {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.TECNICO_SYMP, RolUsuario.COORDINADOR_PRE,
                RolUsuario.COORDINADOR_SYMP, RolUsuario.SUBJEFE_DGI, RolUsuario.JEFE_DGI))
                .thenReturn(Usuario.builder().rol(RolUsuario.COORDINADOR_PRE).build());
    }

    @Test
    void listarCriteriosPriorizacion_devuelveCriteriosConSusSubcriterios() {
        CriterioPriorizacion criterio = CriterioPriorizacion.builder().id(1L).codigo("CRIT-1").numeroCriterio(1)
                .nombreCriterio("Alineación estratégica").ponderacionCriterio(30.0)
                .subcriterios(List.of(SubcriterioPriorizacion.builder().id(1L).codigo("SUB-1.1").numero("1.1")
                        .nombre("Contribución al Plan Cuscatlán").ponderacionSubcriterio(15.0).build()))
                .build();
        when(criterioPriorizacionRepository.findAllByOrderByNumeroCriterioAsc()).thenReturn(List.of(criterio));

        var resultado = service.listarCriteriosPriorizacion();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("CRIT-1");
        assertThat(resultado.get(0).getSubcriterios()).extracting("codigo").containsExactly("SUB-1.1");
    }

    @Test
    void listarEscalaCalificacionSubcriterio_subcriterioExistente_devuelveLaEscala() {
        when(subcriterioPriorizacionRepository.findByCodigo("SUB-1.1"))
                .thenReturn(Optional.of(mock(SubcriterioPriorizacion.class)));
        when(escalaCalificacionSubcriterioRepository.findByCodigoSubcriterio("SUB-1.1")).thenReturn(List.of(
                EscalaCalificacionSubcriterio.builder().id(1L).codigoSubcriterio("SUB-1.1")
                        .valor(ValorCalificacion.CINCO).descripcion("Cumple plenamente.").build()));

        var resultado = service.listarEscalaCalificacionSubcriterio("SUB-1.1");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDescripcion()).isEqualTo("Cumple plenamente.");
    }

    @Test
    void listarEscalaCalificacionSubcriterio_subcriterioInexistente_lanzaRecursoNoEncontrado() {
        when(subcriterioPriorizacionRepository.findByCodigo("NO-EXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listarEscalaCalificacionSubcriterio("NO-EXISTE"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void listarRangosInterpretacionPriorizacion_devuelveLosRangosOrdenados() {
        when(rangoInterpretacionPriorizacionRepository.findAllByOrderByPuntajeMinimoAsc()).thenReturn(List.of(
                RangoInterpretacionPriorizacion.builder().id(1L).puntajeMinimo(0.0).puntajeMaximo(25.0)
                        .categoria("Baja prioridad").implicacion("Revisión sustancial.").build()));

        var resultado = service.listarRangosInterpretacionPriorizacion();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo("Baja prioridad");
    }
}
