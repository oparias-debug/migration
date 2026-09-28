package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorEvaluacionDto;
import sv.gob.mh.siip.model.preinversion.repository.DescripcionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;

/** Pruebas unitarias de {@link FichaViabilidadEnsamblador} (CU-PRE-24, Anexo B.1). */
class FichaViabilidadEnsambladorTest {

    private static final Long ID = 8L;

    private IdentificacionRepository identificaciones;
    private DescripcionTecnicaRepository descripciones;
    private FichaViabilidadProductos productos;
    private FichaViabilidadPresupuesto presupuesto;
    private FichaViabilidadIndicadores indicadores;
    private FichaViabilidadEnsamblador ensamblador;

    @BeforeEach
    void setUp() {
        identificaciones = mock(IdentificacionRepository.class);
        descripciones = mock(DescripcionTecnicaRepository.class);
        productos = mock(FichaViabilidadProductos.class);
        presupuesto = mock(FichaViabilidadPresupuesto.class);
        indicadores = mock(FichaViabilidadIndicadores.class);
        ensamblador = new FichaViabilidadEnsamblador(identificaciones, descripciones, productos, presupuesto,
                indicadores);
    }

    @Test
    void sinIdentificacionNiDescripcionEsosCamposQuedanVacios() {
        FichaViabilidadResponseDto ficha = new FichaViabilidadResponseDto();

        ensamblador.completarCamposDeConsulta(ficha, ID);

        assertThat(ficha.getObjetivoGeneral()).isNull();
        assertThat(ficha.getDescripcion()).isNull();
        verify(presupuesto).completar(ficha, ID);
    }

    @Test
    void tomaCadaCampoDeSuCasoDeUsoDeOrigen() {
        when(identificaciones.findByProyectoId(ID))
                .thenReturn(Optional.of(Identificacion.builder().objetivoGeneral("Objetivo").build()));
        when(descripciones.findByProyectoId(ID))
                .thenReturn(Optional.of(DescripcionTecnica.builder().descripcion("Descripción").build()));
        when(productos.productos(ID)).thenReturn(List.of("Carretera"));
        when(productos.poblacionObjetivo(ID)).thenReturn(150L);
        List<IndicadorEvaluacionDto> calculados = List.of(new IndicadorEvaluacionDto("VAN"));
        when(indicadores.indicadoresEvaluacion(ID)).thenReturn(calculados);
        FichaViabilidadResponseDto ficha = new FichaViabilidadResponseDto();

        ensamblador.completarCamposDeConsulta(ficha, ID);

        assertThat(ficha.getObjetivoGeneral()).isEqualTo("Objetivo");
        assertThat(ficha.getDescripcion()).isEqualTo("Descripción");
        assertThat(ficha.getProductos()).containsExactly("Carretera");
        assertThat(ficha.getPoblacionObjetivo()).isEqualTo(150L);
        assertThat(ficha.getIndicadoresEvaluacion()).isEqualTo(calculados);
        verify(presupuesto).completar(ficha, ID);
    }
}
