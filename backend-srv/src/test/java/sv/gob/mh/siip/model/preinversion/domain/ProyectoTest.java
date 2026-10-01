package sv.gob.mh.siip.model.preinversion.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProyectoTest {

    @Test
    void recreaLasMedidasCuandoHibernateDejaElEmbebidoEnNull() {
        Proyecto proyecto = new Proyecto();
        // Hibernate deja el embebido en null cuando todas sus columnas son null.
        setField(proyecto, "medidas", null);

        proyecto.setMedidasGrd(List.of("GRD-1"));

        assertThat(proyecto.getMedidasGrd()).containsExactly("GRD-1");
        assertThat(proyecto.getMedidasGrc()).isEmpty();
    }
}
