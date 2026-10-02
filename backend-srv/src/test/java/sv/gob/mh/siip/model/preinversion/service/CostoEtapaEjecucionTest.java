package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.MontoPorPeriodoDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;

/** Pruebas unitarias de {@link CostoEtapaEjecucion} (CU-PRE-03.5, RN05/RN11/RN22). */
class CostoEtapaEjecucionTest {

    private static final Long ID_PROYECTO = 5L;

    private final EtapaPreinversionRepository etapas = mock(EtapaPreinversionRepository.class);
    private final PresupuestoProyectoRepository presupuestos = mock(PresupuestoProyectoRepository.class);
    private final PresupuestoInversionEnsamblador ensamblador = mock(PresupuestoInversionEnsamblador.class);
    private final CostoEtapaEjecucion costo = new CostoEtapaEjecucion(etapas, presupuestos, ensamblador);

    private final EtapaPreinversion ejecucion = EtapaPreinversion.builder()
            .tipoEtapa(TipoEtapaPreinversion.EJECUCION).costo(1d).build();

    @Test
    void actualizar_copiaElTotalAPreciosDeMercadoRedondeadoADosDecimales() {
        when(etapas.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.EJECUCION))
                .thenReturn(Optional.of(ejecucion));

        costo.actualizar(ID_PROYECTO, presupuesto(500000.005));

        assertThat(ejecucion.getCosto()).isEqualTo(500000.01);
        verify(etapas).save(ejecucion);
    }

    @Test
    void actualizar_sinEtapaDeEjecucion_noHaceNada() {
        when(etapas.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.EJECUCION))
                .thenReturn(Optional.empty());

        costo.actualizar(ID_PROYECTO, presupuesto(10d));

        verify(etapas, never()).save(any());
    }

    @Test
    void recalcular_conPresupuestoGuardado_usaSuTotal() {
        Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO).build();
        PresupuestoProyecto guardado = PresupuestoProyecto.builder().id(9L).proyecto(proyecto).build();
        when(presupuestos.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(guardado));
        when(ensamblador.dto(proyecto, guardado)).thenReturn(presupuesto(750000d));
        when(etapas.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.EJECUCION))
                .thenReturn(Optional.of(ejecucion));

        costo.recalcular(ID_PROYECTO);

        assertThat(ejecucion.getCosto()).isEqualTo(750000d);
    }

    @Test
    void recalcular_sinPresupuesto_dejaElCostoComoEsta() {
        when(presupuestos.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        costo.recalcular(ID_PROYECTO);

        assertThat(ejecucion.getCosto()).isEqualTo(1d);
        verify(etapas, never()).save(any());
    }

    private static PresupuestoDto presupuesto(double total) {
        return new PresupuestoDto(ID_PROYECTO, List.of(), new MontoPorPeriodoDto(List.of(total), total), List.of(),
                total);
    }
}
