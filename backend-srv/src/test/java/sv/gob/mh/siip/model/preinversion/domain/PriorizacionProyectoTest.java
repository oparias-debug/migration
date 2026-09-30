package sv.gob.mh.siip.model.preinversion.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.enums.EstadoTramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;

class PriorizacionProyectoTest {

    @Test
    void tramo_recreaElEmbebidoQueHibernateDejaEnNulo() {
        PriorizacionProyecto priorizacion = PriorizacionProyecto.builder().tramoPre(null).tramoSymp(null).build();

        TramoCalificacionPriorizacion pre = priorizacion.tramo(TramoPriorizacion.PRE);
        TramoCalificacionPriorizacion symp = priorizacion.tramo(TramoPriorizacion.SYMP);

        assertThat(pre.getEstado()).isEqualTo(EstadoTramoPriorizacion.PENDIENTE);
        assertThat(pre.edicionHabilitada()).isTrue();
        assertThat(symp.fueRevisado()).isFalse();
        assertThat(priorizacion.tramo(TramoPriorizacion.PRE)).isSameAs(pre);
        assertThat(priorizacion.tramo(TramoPriorizacion.SYMP)).isSameAs(symp);
        assertThat(priorizacion.estaCompleta()).isFalse();
    }

    @Test
    void edicionHabilitada_soloPendienteOConAjustesHabilitados() {
        TramoCalificacionPriorizacion tramo = TramoCalificacionPriorizacion.builder()
                .estado(EstadoTramoPriorizacion.ENVIADA_A_REVISION).build();
        assertThat(tramo.edicionHabilitada()).isFalse();

        tramo.setAjustesHabilitados(true);
        tramo.setFechaRevision(LocalDateTime.now());
        assertThat(tramo.edicionHabilitada()).isTrue();
        assertThat(tramo.fueRevisado()).isTrue();
    }
}
