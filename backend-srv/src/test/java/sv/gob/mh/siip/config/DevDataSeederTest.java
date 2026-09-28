package sv.gob.mh.siip.config;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import org.mockito.InOrder;

import sv.gob.mh.siip.config.devseed.DevSeeder;

/** El orquestador solo debe ejecutar cada {@link DevSeeder} inyectado, en el orden recibido. */
class DevDataSeederTest {

    @Test
    void run_ejecutaCadaSeederInyectadoEnOrden() {
        DevSeeder primero = mock(DevSeeder.class);
        DevSeeder segundo = mock(DevSeeder.class);
        DevDataSeeder devDataSeeder = new DevDataSeeder(List.of(primero, segundo));

        devDataSeeder.run(null);

        InOrder orden = inOrder(primero, segundo);
        orden.verify(primero).seed();
        orden.verify(segundo).seed();
    }
}
