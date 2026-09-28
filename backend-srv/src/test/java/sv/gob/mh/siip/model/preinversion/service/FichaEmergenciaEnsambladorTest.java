package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.ComponenteCostoEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ComponenteCostoDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;

class FichaEmergenciaEnsambladorTest {

    private final ProductoIndicadorCatalogoRepository catalogoRepository =
            mock(ProductoIndicadorCatalogoRepository.class);
    private final FichaEmergenciaEnsamblador ensamblador = new FichaEmergenciaEnsamblador(catalogoRepository);

    private final Proyecto proyecto = proyectoEmergencia();

    private static Proyecto proyectoEmergencia() {
        Proyecto proyecto = Proyecto.builder().id(1L).cup("CUP-1").nombre("Emergencia").build();
        proyecto.setNumeroDecretoLegislativo("DL-9");
        proyecto.setTipoEvento("Sismo");
        return proyecto;
    }

    @Test
    void construir_sinFicha_devuelveSoloDatosDelProyectoYEtapas() {
        FichaEmergenciaDto dto = ensamblador.construir(proyecto, null);

        assertThat(dto.getCup()).isEqualTo("CUP-1");
        assertThat(dto.getNombreProyecto()).isEqualTo("Emergencia");
        assertThat(dto.getEtapaActual()).isEqualTo(NombreEtapaDto.PERFIL);
        assertThat(dto.getEtapaFutura()).isEqualTo(NombreEtapaDto.EJECUCION);
        assertThat(dto.getNumeroDecretoLegislativo()).isEqualTo("DL-9");
        assertThat(dto.getTipoEvento()).isEqualTo("Sismo");
        assertThat(dto.getPlanteamientoProblema()).isNull();
    }

    @Test
    void construir_fichaCompleta_resuelveProductosYSumaComponentes() {
        ProductoIndicadorCatalogo catalogado = ProductoIndicadorCatalogo.builder().id(1L).codigoProducto("P-01")
                .producto("Puente").build();
        ProductoIndicadorCatalogo otro = ProductoIndicadorCatalogo.builder().id(2L).codigoProducto("P-03")
                .producto("Otro").build();
        when(catalogoRepository.findByCodigoProductoIn(List.of("P-01", "P-02"))).thenReturn(List.of(otro, catalogado));
        FichaEmergencia ficha = FichaEmergencia.builder()
                .planteamientoProblema("Problema").objetivoGeneral("Objetivo").descripcionProyecto("Descripción")
                .productos(List.of("P-01", "P-02")).distrito("Soyapango").latitud(1d).longitud(2d)
                .direccionEspecifica("Calle").poblacionObjetivo("Población").inversionEstimada(300d)
                .archivoPresupuestoUrl("p.pdf")
                .componentesCosto(List.of(new ComponenteCostoEmergencia("Obra", 100d),
                        new ComponenteCostoEmergencia("Supervisión", 50d)))
                .costosOperacion(5d).costosMantenimiento(6d)
                .fuentesFinanciamiento(List.of(FuenteFinanciamiento.DONACIONES))
                .fuenteRecursos("Cooperación").archivoProgramacionUrl("g.pdf")
                .build();

        FichaEmergenciaDto dto = ensamblador.construir(proyecto, ficha);

        assertThat(dto.getProductos()).extracting(ProductoSeleccionadoDto::getCodigoProducto,
                ProductoSeleccionadoDto::getProducto)
                .containsExactly(tuple("P-01", "Puente"),
                        tuple("P-02", null));
        assertThat(dto.getPlanteamientoProblema()).isEqualTo("Problema");
        assertThat(dto.getObjetivoGeneral()).isEqualTo("Objetivo");
        assertThat(dto.getDescripcionProyecto()).isEqualTo("Descripción");
        assertThat(dto.getDepartamento()).isNull();
        assertThat(dto.getDistrito()).isEqualTo("Soyapango");
        assertThat(dto.getLatitud()).isEqualTo(1d);
        assertThat(dto.getLongitud()).isEqualTo(2d);
        assertThat(dto.getDireccionEspecifica()).isEqualTo("Calle");
        assertThat(dto.getPoblacionObjetivo()).isEqualTo("Población");
        assertThat(dto.getInversionEstimada()).isEqualTo(300d);
        assertThat(dto.getArchivoPresupuestoUrl()).isEqualTo("p.pdf");
        assertThat(dto.getComponentesCosto()).extracting(ComponenteCostoDto::getTipoCosto)
                .containsExactly("Obra", "Supervisión");
        assertThat(dto.getTotalComponentesCosto()).isEqualTo(150d);
        assertThat(dto.getCostosOperacion()).isEqualTo(5d);
        assertThat(dto.getCostosMantenimiento()).isEqualTo(6d);
        assertThat(dto.getFuentesFinanciamiento()).containsExactly(FuenteFinanciamientoDto.DONACIONES);
        assertThat(dto.getFuenteRecursos()).isEqualTo("Cooperación");
        assertThat(dto.getArchivoProgramacionUrl()).isEqualTo("g.pdf");
    }

    @Test
    void construir_fichaSinProductosNiComponentes_noConsultaCatalogoNiCalculaTotal() {
        FichaEmergencia ficha = FichaEmergencia.builder().planteamientoProblema("Problema").build();

        FichaEmergenciaDto dto = ensamblador.construir(proyecto, ficha);

        assertThat(dto.getProductos()).isEmpty();
        assertThat(dto.getComponentesCosto()).isEmpty();
        assertThat(dto.getTotalComponentesCosto()).isNull();
        verify(catalogoRepository, never()).findByCodigoProductoIn(any());
    }
}
