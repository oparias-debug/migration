package sv.gob.mh.siip.model.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link CatalogoEdicion} (CU-ADM-01, Reglas 9 y 14). */
class CatalogoEdicionTest {

    private static final LocalDate HOY = LocalDate.now(ZoneId.of("America/El_Salvador"));

    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final CatalogoEdicion edicion = new CatalogoEdicion(actorContexto, catalogoRepository,
            new CatalogoBusqueda(catalogoRepository));

    private final Catalogo catalogo = Catalogo.builder().codigo("CAT").nombre("Viejo")
            .estado(EstadoVigencia.ACTIVE).build();

    @BeforeEach
    void setUp() {
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.of(catalogo));
        when(catalogoRepository.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void actualizarDescriptores_todos_actualizaCadaDescriptor() {
        when(catalogoRepository.findByCodigo("PADRE")).thenReturn(Optional.of(Catalogo.builder().build()));
        CatalogDescriptorsUpdateRequestDto request = new CatalogDescriptorsUpdateRequestDto().name("Nuevo")
                .parent("PADRE").active(ActiveStatusDto.INACTIVE).fromDate(LocalDate.of(2026, 1, 1))
                .toDate(LocalDate.of(2026, 12, 31));

        CatalogDto dto = edicion.actualizarDescriptores("CAT", request);

        assertThat(dto.getName()).isEqualTo("Nuevo");
        assertThat(dto.getParent()).isEqualTo("PADRE");
        assertThat(dto.getActive()).isEqualTo(ActiveStatusDto.INACTIVE);
        assertThat(dto.getFromDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(dto.getToDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        verify(actorContexto).exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
    }

    @Test
    void actualizarDescriptores_soloNombre_conservaElResto() {
        CatalogDto dto = edicion.actualizarDescriptores("CAT", new CatalogDescriptorsUpdateRequestDto().name("Nuevo"));

        assertThat(dto.getName()).isEqualTo("Nuevo");
        assertThat(dto.getActive()).isEqualTo(ActiveStatusDto.ACTIVE);
        assertThat(dto.getParent()).isNull();
    }

    @Test
    void actualizarDescriptores_sinDescriptores_lanzaDescriptorRequerido() {
        CatalogDescriptorsUpdateRequestDto request = new CatalogDescriptorsUpdateRequestDto();

        assertThatThrownBy(() -> edicion.actualizarDescriptores("CAT", request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo("DESCRIPTOR_REQUERIDO"));
        verify(catalogoRepository, never()).save(any());
    }

    @Test
    void actualizarDescriptores_padreInexistente_lanzaRecursoNoEncontrado() {
        when(catalogoRepository.findByCodigo("PADRE")).thenReturn(Optional.empty());
        CatalogDescriptorsUpdateRequestDto request = new CatalogDescriptorsUpdateRequestDto().parent("PADRE");

        assertThatThrownBy(() -> edicion.actualizarDescriptores("CAT", request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El catálogo padre indicado no existe.");
    }

    @Test
    void actualizarDescriptores_catalogoInexistente_lanzaRecursoNoEncontrado() {
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.empty());
        CatalogDescriptorsUpdateRequestDto request = new CatalogDescriptorsUpdateRequestDto();

        assertThatThrownBy(() -> edicion.actualizarDescriptores("CAT", request))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminar_siempreLanzaEliminacionNoPermitida() {
        assertThatThrownBy(edicion::eliminar)
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo("ELIMINACION_NO_PERMITIDA"));
        verify(actorContexto).exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
    }

    @Test
    void inactivar_sinRequest_inactivaConFechaDeHoy() {
        CatalogDto dto = edicion.inactivar("CAT", null);

        assertThat(dto.getActive()).isEqualTo(ActiveStatusDto.INACTIVE);
        assertThat(dto.getToDate()).isEqualTo(HOY);
    }

    @Test
    void inactivar_conToDatePasada_laUsa() {
        LocalDate ayer = HOY.minusDays(1);

        CatalogDto dto = edicion.inactivar("CAT", new InactivationRequestDto().toDate(ayer));

        assertThat(dto.getToDate()).isEqualTo(ayer);
        assertThat(catalogo.getEstado()).isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    void inactivar_conToDateFutura_lanzaValidacion() {
        InactivationRequestDto request = new InactivationRequestDto().toDate(HOY.plusDays(1));

        assertThatThrownBy(() -> edicion.inactivar("CAT", request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo("TO_DATE_FUTURA"));
        verify(catalogoRepository, never()).save(any());
    }
}
