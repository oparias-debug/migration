package sv.gob.mh.siip.model.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link CatalogoServiceImpl} (CU-ADM-01): delegación en sus colaboradores. */
class CatalogoServiceImplTest {

    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final RegistroRepository registroRepository = mock(RegistroRepository.class);
    private final CatalogoServiceImpl service = new CatalogoServiceImpl(catalogoRepository, registroRepository,
            mock(ActorContexto.class));

    private final Catalogo catalogo = Catalogo.builder().codigo("CAT").nombre("Catalogo")
            .estado(EstadoVigencia.ACTIVE).build();
    private final List<CatalogFieldDto> campos = List.of(
            new CatalogFieldDto().name("codigo").qualifier(FieldQualifierDto.KEY));

    @BeforeEach
    void setUp() {
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.of(catalogo));
        when(catalogoRepository.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void crear_delegaEnAltas() {
        CatalogCreateRequestDto request = new CatalogCreateRequestDto().code("NUEVO").name("Nuevo").fields(campos);

        assertThat(service.crear(request).getCode()).isEqualTo("NUEVO");
    }

    @Test
    void actualizarCampos_delegaEnAltas() {
        when(registroRepository.existsByCatalogo_Codigo("CAT")).thenReturn(true);
        CatalogFieldsUpdateRequestDto request = new CatalogFieldsUpdateRequestDto().fields(campos);

        assertThatThrownBy(() -> service.actualizarCampos("CAT", request))
                .isInstanceOf(ConflictoEstadoException.class);
    }

    @Test
    void consultas_delegaEnConsultas() {
        PageRequest pagina = PageRequest.of(0, 5);
        when(catalogoRepository.findAll(pagina)).thenReturn(new PageImpl<>(List.of(catalogo), pagina, 1));
        when(catalogoRepository.existsByNombreIgnoreCase("Catalogo")).thenReturn(true);
        when(catalogoRepository.findByCatalogoPadreCodigo("CAT")).thenReturn(List.of());

        assertThat(service.listar(pagina).getContent()).hasSize(1);
        assertThat(service.verificarExistencia("Catalogo").getExists()).isTrue();
        assertThat(service.consultar("CAT").getCode()).isEqualTo("CAT");
        assertThat(service.consultarHijos("CAT")).isEmpty();
    }

    @Test
    void edicion_delegaEnEdicion() {
        assertThat(service.actualizarDescriptores("CAT", new CatalogDescriptorsUpdateRequestDto().name("Otro"))
                .getName()).isEqualTo("Otro");
        assertThat(service.inactivar("CAT", null).getActive()).isEqualTo(ActiveStatusDto.INACTIVE);
        assertThatThrownBy(() -> service.eliminar("CAT")).isInstanceOf(ValidacionNegocioException.class);
    }
}
