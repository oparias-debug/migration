package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import sv.gob.mh.siip.model.administracion.dto.TipoCatalogoEspecificarDto;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.EntradaCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;
import sv.gob.mh.siip.model.preinversion.repository.CriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EntradaCatalogoEspecificarRepository;
import sv.gob.mh.siip.security.ActorContexto;

class CatalogoElegibilidadServiceImplTest {

    private final CriterioElegibilidadRepository criterioElegibilidadRepository = mock(
            CriterioElegibilidadRepository.class);
    private final EntradaCatalogoEspecificarRepository entradaCatalogoEspecificarRepository = mock(
            EntradaCatalogoEspecificarRepository.class);
    private final CatalogosAdministracionMapper mapper = Mappers.getMapper(CatalogosAdministracionMapper.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final CatalogoElegibilidadServiceImpl service = new CatalogoElegibilidadServiceImpl(
            criterioElegibilidadRepository, entradaCatalogoEspecificarRepository, mapper, actorContexto);

    {
        when(actorContexto.exigirRol(RolUsuario.VIABILIZADOR))
                .thenReturn(Usuario.builder().rol(RolUsuario.VIABILIZADOR).build());
    }

    @Test
    void listarCriteriosElegibilidad_devuelveElCatalogoMapeado() {
        when(criterioElegibilidadRepository.findAllByOrderByCodigoAsc()).thenReturn(List.of(
                CriterioElegibilidad.builder().id(1L).codigo("ELEG-01").dimension("Alineación estratégica")
                        .criterio("¿Contribuye a algún ODS?").tipoEspecificar(TipoEspecificar.CATALOGO)
                        .catalogoEspecificar(TipoCatalogoEspecificar.ODS).permiteSeleccionMultiple(true).build()));

        var resultado = service.listarCriteriosElegibilidad();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("ELEG-01");
        assertThat(resultado.get(0).getPermiteSeleccionMultiple()).isTrue();
    }

    @Test
    void listarCatalogoEspecificarElegibilidad_filtraPorTipo() {
        when(entradaCatalogoEspecificarRepository.findByTipoOrderByCodigoAsc(TipoCatalogoEspecificar.ODS))
                .thenReturn(List.of(EntradaCatalogoEspecificar.builder().id(1L).tipo(TipoCatalogoEspecificar.ODS)
                        .codigo("ODS-01").nombre("ODS 1: Fin de la pobreza").build()));

        var resultado = service.listarCatalogoEspecificarElegibilidad(TipoCatalogoEspecificarDto.ODS);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("ODS-01");
    }
}
