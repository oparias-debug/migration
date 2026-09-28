package sv.gob.mh.siip.model.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link CatalogoAltas} (CU-ADM-01, Reglas 2, 3 y 13). */
class CatalogoAltasTest {

    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final RegistroRepository registroRepository = mock(RegistroRepository.class);
    private final CatalogoAltas altas = new CatalogoAltas(actorContexto, catalogoRepository, registroRepository,
            new CatalogoBusqueda(catalogoRepository));

    private static CatalogFieldDto campo(String nombre, FieldQualifierDto calificador) {
        return new CatalogFieldDto().name(nombre).qualifier(calificador);
    }

    @BeforeEach
    void setUp() {
        when(catalogoRepository.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void crear_sinActive_calculaVigenciaYGuardaLosCampos() {
        when(catalogoRepository.findByCodigo("PADRE")).thenReturn(Optional.of(Catalogo.builder().build()));
        CatalogCreateRequestDto request = new CatalogCreateRequestDto().code("CAT").name("Catalogo")
                .parent("PADRE").fromDate(LocalDate.of(2020, 1, 1)).toDate(LocalDate.of(2020, 12, 31))
                .fields(List.of(campo("codigo", FieldQualifierDto.KEY), campo("nombre", FieldQualifierDto.FIELD)));

        CatalogDto dto = altas.crear(request);

        assertThat(dto.getCode()).isEqualTo("CAT");
        assertThat(dto.getName()).isEqualTo("Catalogo");
        assertThat(dto.getParent()).isEqualTo("PADRE");
        assertThat(dto.getActive()).isEqualTo(ActiveStatusDto.INACTIVE);
        assertThat(dto.getFields()).hasSize(2);
        verify(actorContexto).exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
    }

    @Test
    void crear_conActiveExplicitoYSinPadre_respetaElEstado() {
        CatalogCreateRequestDto request = new CatalogCreateRequestDto().code("CAT").name("Catalogo")
                .active(ActiveStatusDto.ACTIVE).toDate(LocalDate.of(2020, 12, 31))
                .fields(List.of(campo("codigo", FieldQualifierDto.KEY)));

        assertThat(altas.crear(request).getActive()).isEqualTo(ActiveStatusDto.ACTIVE);
    }

    @Test
    void crear_sinCampoKey_lanzaValidacionAntesDeConsultarElPadre() {
        CatalogCreateRequestDto request = new CatalogCreateRequestDto().code("CAT").name("Catalogo")
                .parent("PADRE").fields(List.of(campo("nombre", FieldQualifierDto.FIELD)));

        assertThatThrownBy(() -> altas.crear(request)).isInstanceOf(ValidacionNegocioException.class);
        verify(catalogoRepository, never()).findByCodigo(any());
        verify(catalogoRepository, never()).save(any());
    }

    @Test
    void crear_padreInexistente_lanzaRecursoNoEncontrado() {
        when(catalogoRepository.findByCodigo("PADRE")).thenReturn(Optional.empty());
        CatalogCreateRequestDto request = new CatalogCreateRequestDto().code("CAT").name("Catalogo")
                .parent("PADRE").fields(List.of(campo("codigo", FieldQualifierDto.KEY)));

        assertThatThrownBy(() -> altas.crear(request)).isInstanceOf(RecursoNoEncontradoException.class);
        verify(catalogoRepository, never()).save(any());
    }

    @Test
    void actualizarCampos_sinRegistros_reemplazaLaDefinicion() {
        Catalogo catalogo = Catalogo.builder().codigo("CAT").estado(EstadoVigencia.ACTIVE).build();
        catalogo.getCampos().add(CampoDefinicion.builder().nombre("viejo").build());
        when(registroRepository.existsByCatalogo_Codigo("CAT")).thenReturn(false);
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.of(catalogo));
        CatalogFieldsUpdateRequestDto request = new CatalogFieldsUpdateRequestDto()
                .fields(List.of(campo("codigo", FieldQualifierDto.KEY)));

        CatalogDto dto = altas.actualizarCampos("CAT", request);

        assertThat(dto.getFields()).extracting(CatalogFieldDto::getName).containsExactly("codigo");
        assertThat(catalogo.getCampos()).hasSize(1);
    }

    @Test
    void actualizarCampos_conRegistros_lanzaConflictoAntesDeBuscarElCatalogo() {
        when(registroRepository.existsByCatalogo_Codigo("CAT")).thenReturn(true);
        CatalogFieldsUpdateRequestDto request = new CatalogFieldsUpdateRequestDto()
                .fields(List.of(campo("codigo", FieldQualifierDto.KEY)));

        assertThatThrownBy(() -> altas.actualizarCampos("CAT", request))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessage("No se pueden modificar los campos de un catálogo que ya tiene registros.");
        verify(catalogoRepository, never()).findByCodigo(any());
    }

    @Test
    void actualizarCampos_catalogoInexistente_lanzaRecursoNoEncontradoDespuesDeRevisarRegistros() {
        when(registroRepository.existsByCatalogo_Codigo("CAT")).thenReturn(false);
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.empty());
        CatalogFieldsUpdateRequestDto request = new CatalogFieldsUpdateRequestDto()
                .fields(List.of(campo("codigo", FieldQualifierDto.KEY)));

        assertThatThrownBy(() -> altas.actualizarCampos("CAT", request))
                .isInstanceOf(RecursoNoEncontradoException.class);
        InOrder orden = inOrder(actorContexto, registroRepository, catalogoRepository);
        orden.verify(actorContexto).exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        orden.verify(registroRepository).existsByCatalogo_Codigo("CAT");
        orden.verify(catalogoRepository).findByCodigo("CAT");
    }
}
