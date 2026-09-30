package sv.gob.mh.api.controller.catalogo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import sv.gob.mh.application.handler.catalogo.ActualizarCamposCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.ActualizarDescriptoresCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.ActualizarRegistroHandler;
import sv.gob.mh.application.handler.catalogo.CrearCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.CrearRegistroHandler;
import sv.gob.mh.application.handler.catalogo.EliminarCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.EliminarRegistroHandler;
import sv.gob.mh.application.handler.catalogo.InactivarCatalogoHandler;
import sv.gob.mh.application.handler.catalogo.InactivarRegistroHandler;

/**
 * Reglas 10, 11 y E7: el handler de eliminación siempre rechaza con 405. Si alguna vez no lo hiciera,
 * la respuesta sigue siendo 405 y nunca un 2xx que aparente haber borrado.
 */
class EliminacionesCatalogoTest {

    @Test
    @DisplayName("Eliminar un catálogo nunca responde con éxito")
    void eliminarCatalogoResponde405() {
        EscrituraCatalogos catalogos = new EscrituraCatalogos(mock(CrearCatalogoHandler.class),
                mock(ActualizarDescriptoresCatalogoHandler.class), mock(ActualizarCamposCatalogoHandler.class),
                mock(InactivarCatalogoHandler.class), mock(EliminarCatalogoHandler.class));

        assertThat(catalogos.eliminar("PAISES").getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("Eliminar un registro nunca responde con éxito")
    void eliminarRegistroResponde405() {
        EscrituraRegistros registros = new EscrituraRegistros(mock(CrearRegistroHandler.class),
                mock(ActualizarRegistroHandler.class), mock(InactivarRegistroHandler.class),
                mock(EliminarRegistroHandler.class));

        assertThat(registros.eliminar("PAISES", "SV").getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("El controller devuelve la respuesta de la eliminación sin transformarla")
    void elControllerDelegaLasEliminaciones() {
        CatalogosAdministracionController controller = new CatalogosAdministracionController(
                new EscrituraCatalogos(mock(CrearCatalogoHandler.class),
                        mock(ActualizarDescriptoresCatalogoHandler.class),
                        mock(ActualizarCamposCatalogoHandler.class), mock(InactivarCatalogoHandler.class),
                        mock(EliminarCatalogoHandler.class)),
                new EscrituraRegistros(mock(CrearRegistroHandler.class), mock(ActualizarRegistroHandler.class),
                        mock(InactivarRegistroHandler.class), mock(EliminarRegistroHandler.class)),
                null);

        assertThat(controller.eliminarCatalogo("PAISES").getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(controller.eliminarRegistro("PAISES", "SV").getStatusCode())
                .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
