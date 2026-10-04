package sv.gob.mh.api.controller.catalogo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import sv.gob.mh.application.handler.catalogo.ActualizarCamposCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.ActualizarDescriptoresCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.ActualizarRegistroHandler;
import sv.gob.mh.application.handler.catalogo.CambiarEstadoCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.CambiarEstadoRegistroHandler;
import sv.gob.mh.application.handler.catalogo.CrearCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.CrearRegistroHandler;
import sv.gob.mh.application.handler.catalogo.EliminarCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.EliminarRegistroHandler;

/**
 * RN-13, RN-14 y E-24: el handler de eliminación siempre rechaza con 405. Si alguna vez no lo hiciera,
 * la respuesta sigue siendo 405 y nunca un 2xx que aparente haber borrado.
 */
class EliminacionesCatalogoTest {

    private static EscrituraCatalogos escrituraCatalogos() {
        return new EscrituraCatalogos(mock(CrearCatalogoHandler.class),
                mock(ActualizarDescriptoresCatalogoHandler.class), mock(ActualizarCamposCatalogoHandler.class),
                mock(CambiarEstadoCatalogoHandler.class), mock(EliminarCatalogoHandler.class));
    }

    private static EscrituraRegistros escrituraRegistros() {
        return new EscrituraRegistros(mock(CrearRegistroHandler.class), mock(ActualizarRegistroHandler.class),
                mock(CambiarEstadoRegistroHandler.class), mock(EliminarRegistroHandler.class));
    }

    @Test
    @DisplayName("Eliminar un catálogo nunca responde con éxito")
    void eliminarCatalogoResponde405() {
        assertThat(escrituraCatalogos().eliminar("PAISES").getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("Eliminar un registro nunca responde con éxito")
    void eliminarRegistroResponde405() {
        assertThat(escrituraRegistros().eliminar("PAISES", "SV").getStatusCode())
                .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("El controller devuelve la respuesta de la eliminación sin transformarla")
    void elControllerDelegaLasEliminaciones() {
        CatalogosAdministracionController controller = new CatalogosAdministracionController(escrituraCatalogos(),
                escrituraRegistros(), null);

        assertThat(controller.eliminarCatalogo("PAISES").getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(controller.eliminarRegistro("PAISES", "SV").getStatusCode())
                .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
