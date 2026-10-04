package sv.gob.mh.application.handler.catalogo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.application.command.catalogo.ActualizarCamposCatalogoCommand;
import sv.gob.mh.application.command.catalogo.CrearCatalogoCommand;
import sv.gob.mh.application.command.catalogo.CrearRegistroCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.DefinicionTipo;
import sv.gob.mh.domain.model.catalogo.NuevoCampo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.ValorCampo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/** Reglas que verifican los handlers de creación de CU-ADM-01 antes de persistir. */
class CreacionCatalogoHandlersTest {

    private static final List<NuevoCampo> CAMPOS = List.of(
            new NuevoCampo("codigo", true, 1, DefinicionTipo.texto(3)),
            new NuevoCampo("nombre", false, 2, DefinicionTipo.texto(60)));
    private static final List<ValorCampo> VALORES = List.of(new ValorCampo("codigo", "ANT"),
            new ValorCampo("nombre", "Antioquia"));

    private CatalogoRepository catalogos;
    private RegistroRepository registros;
    private JerarquiaCatalogos jerarquia;

    @BeforeEach
    void setUp() {
        catalogos = mock(CatalogoRepository.class);
        registros = mock(RegistroRepository.class);
        jerarquia = new JerarquiaCatalogos(catalogos, registros);
    }

    @Test
    @DisplayName("E-01: un código de catálogo repetido se rechaza sin guardar")
    void codigoDeCatalogoDuplicado() {
        when(catalogos.existeCodigo("PAIS")).thenReturn(true);
        CrearCatalogoHandler handler = new CrearCatalogoHandler(catalogos, jerarquia);
        CrearCatalogoCommand command = new CrearCatalogoCommand("PAIS", "Países", null, null, null, null, CAMPOS);

        assertThatThrownBy(() -> handler.handle(command)).hasFieldOrPropertyWithValue("codigo", "E-01");
        verify(catalogos, never()).guardar(any());
    }

    @Test
    @DisplayName("E-07: el catálogo padre ya tiene otro hijo")
    void padreConHijo() {
        Catalogo pais = Catalogo.nuevo("PAIS", "Países", null, null, null, null, CAMPOS);
        Catalogo depto = Catalogo.nuevo("DEPTO", "Departamentos", "PAIS", null, null, null, CAMPOS);
        when(catalogos.buscarPorCodigo("PAIS")).thenReturn(Optional.of(pais));
        when(catalogos.buscarHijo("PAIS")).thenReturn(Optional.of(depto));
        CrearCatalogoHandler handler = new CrearCatalogoHandler(catalogos, jerarquia);
        CrearCatalogoCommand command = new CrearCatalogoCommand("REGION", "Regiones", "PAIS", null, null, null, CAMPOS);

        assertThatThrownBy(() -> handler.handle(command))
                .hasMessage("El catálogo PAIS ya tiene un catálogo hijo (DEPTO).");
        verify(catalogos, never()).guardar(any());
    }

    @Test
    @DisplayName("E-18: el registro padre debe existir y estar activo en el catálogo padre")
    void registroPadreInvalido() {
        Catalogo pais = Catalogo.nuevo("PAIS", "Países", null, null, null, null, CAMPOS);
        Catalogo depto = Catalogo.nuevo("DEPTO", "Departamentos", "PAIS", null, null, null, CAMPOS);
        Registro ecuador = Registro.nuevo(pais, Map.of("codigo", "ECU", "nombre", "Ecuador"), null, null, null);
        ecuador.inactivar(null);
        when(catalogos.obtenerPorCodigo("DEPTO")).thenReturn(depto);
        when(registros.buscarPorClave("PAIS", "ZZZ")).thenReturn(Optional.empty());
        when(registros.buscarPorClave("PAIS", "ECU")).thenReturn(Optional.of(ecuador));
        CrearRegistroHandler handler = new CrearRegistroHandler(catalogos, registros);

        assertThatThrownBy(() -> handler.handle(new CrearRegistroCommand("DEPTO", VALORES, "ZZZ", null, null)))
                .hasMessage("Registro padre ZZZ inválido en el catálogo PAIS.");
        assertThatThrownBy(() -> handler.handle(new CrearRegistroCommand("DEPTO", VALORES, "ECU", null, null)))
                .hasMessage("Registro padre ECU inválido en el catálogo PAIS.");
        assertThatThrownBy(() -> handler.handle(new CrearRegistroCommand("DEPTO", VALORES, null, null, null)))
                .hasMessage("Registro padre (no informado) inválido en el catálogo PAIS.");
        verify(registros, never()).guardar(any());
    }

    @Test
    @DisplayName("E-17 y E-13: llave duplicada y catálogo inactivo se rechazan sin guardar")
    void claveDuplicadaYCatalogoInactivo() {
        Catalogo pais = Catalogo.nuevo("PAIS", "Países", null, null, null, null, CAMPOS);
        when(catalogos.obtenerPorCodigo("PAIS")).thenReturn(pais);
        when(registros.existeClave("PAIS", "ANT")).thenReturn(true);
        CrearRegistroHandler handler = new CrearRegistroHandler(catalogos, registros);
        CrearRegistroCommand command = new CrearRegistroCommand("PAIS", VALORES, null, null, null);

        assertThatThrownBy(() -> handler.handle(command)).hasMessage("Ya existe un registro con la llave ANT.");
        pais.inactivar(null);
        assertThatThrownBy(() -> handler.handle(command)).hasFieldOrPropertyWithValue("codigo", "E-13");
        verify(registros, never()).guardar(any());
    }

    @Test
    @DisplayName("Los commands aceptan la lista de campos ausente")
    void losCommandsAceptanCamposAusentes() {
        assertThat(new CrearCatalogoCommand("A", "A", null, null, null, null, null).campos()).isNull();
        assertThat(new ActualizarCamposCatalogoCommand("A", null).campos()).isNull();
    }
}
