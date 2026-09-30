package sv.gob.mh.application.handler.catalogo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.application.command.catalogo.ActualizarCamposCatalogoCommand;
import sv.gob.mh.application.command.catalogo.CrearCatalogoCommand;
import sv.gob.mh.application.command.catalogo.CrearRegistroCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.NuevoCampo;
import sv.gob.mh.domain.model.catalogo.ValorCampo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/** Reglas que verifican los handlers de creación de CU-ADM-01 antes de persistir. */
class CreacionCatalogoHandlersTest {

    private static final List<NuevoCampo> CAMPOS = List.of(new NuevoCampo("codigo", true),
            new NuevoCampo("nombre", false));

    private CatalogoRepository catalogos;
    private RegistroRepository registros;

    @BeforeEach
    void setUp() {
        catalogos = mock(CatalogoRepository.class);
        registros = mock(RegistroRepository.class);
    }

    @Test
    @DisplayName("HU-ADM-01-01: un código de catálogo repetido se rechaza sin guardar")
    void codigoDeCatalogoDuplicado() {
        when(catalogos.existeCodigo("PAISES")).thenReturn(true);
        CrearCatalogoHandler handler = new CrearCatalogoHandler(catalogos);
        CrearCatalogoCommand command = new CrearCatalogoCommand("PAISES", "Países", null, null, null, null, CAMPOS);

        assertThatThrownBy(() -> handler.handle(command)).hasFieldOrPropertyWithValue("codigo",
                "CODIGO_CATALOGO_DUPLICADO");
        verify(catalogos, never()).guardar(any());
    }

    @Test
    @DisplayName("Regla 23: el registro padre debe existir en el catálogo padre")
    void registroPadreInexistente() {
        Catalogo hijo = Catalogo.nuevo("MUNICIPIOS", "Municipios", "DEPARTAMENTOS", null, null, null, CAMPOS);
        when(catalogos.obtenerPorCodigo("MUNICIPIOS")).thenReturn(hijo);
        when(registros.buscarPorClave("DEPARTAMENTOS", "99")).thenReturn(Optional.empty());
        CrearRegistroHandler handler = new CrearRegistroHandler(catalogos, registros);
        CrearRegistroCommand command = new CrearRegistroCommand("MUNICIPIOS",
                List.of(new ValorCampo("codigo", "01"), new ValorCampo("nombre", "San Salvador")), "99", null, null);

        assertThatThrownBy(() -> handler.handle(command)).hasFieldOrPropertyWithValue("codigo",
                "REGISTRO_PADRE_INEXISTENTE");
        verify(registros, never()).guardar(any());
    }

    @Test
    @DisplayName("Un valor KEY que ya existe en el catálogo se rechaza sin guardar")
    void claveDeRegistroDuplicada() {
        Catalogo paises = Catalogo.nuevo("PAISES", "Países", null, null, null, null, CAMPOS);
        when(catalogos.obtenerPorCodigo("PAISES")).thenReturn(paises);
        when(registros.existeClave("PAISES", "SV")).thenReturn(true);
        CrearRegistroHandler handler = new CrearRegistroHandler(catalogos, registros);
        CrearRegistroCommand command = new CrearRegistroCommand("PAISES",
                List.of(new ValorCampo("codigo", "SV"), new ValorCampo("nombre", "El Salvador")), null, null, null);

        assertThatThrownBy(() -> handler.handle(command)).hasFieldOrPropertyWithValue("codigo",
                "CLAVE_REGISTRO_DUPLICADA");
        verify(registros, never()).guardar(any());
    }

    @Test
    @DisplayName("Los commands aceptan la lista de campos ausente")
    void losCommandsAceptanCamposAusentes() {
        assertThat(new CrearCatalogoCommand("A", "A", null, null, null, null, null).campos()).isNull();
        assertThat(new ActualizarCamposCatalogoCommand("A", null).campos()).isNull();
    }
}
