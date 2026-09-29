package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisMercado;
import sv.gob.mh.siip.model.preinversion.domain.FilaAnalisisMercado;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisMercadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AnalisisMercadoServiceImplTest {

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final AnalisisMercadoRepository analisisMercadoRepository = mock(AnalisisMercadoRepository.class);
    private final ProductoIndicadorCatalogoRepository productoRepository = mock(ProductoIndicadorCatalogoRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AnalisisMercadoServiceImpl service = new AnalisisMercadoServiceImpl(proyectoRepository,
            analisisMercadoRepository, productoRepository, actorContexto);

    private Proyecto proyectoDeUnidad(Long idUnidad) {
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(idUnidad).build();
        return Proyecto.builder().id(1L).unidadEjecutora(unidad).build();
    }

    /** Técnico URP sobre un proyecto nuevo; el catálogo contiene los productos indicados. */
    private void prepararGuardado(String... productosDelCatalogo) {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().unidadEjecutora(null).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoDeUnidad(5L)));
        when(analisisMercadoRepository.findByProyectoId(1L)).thenReturn(Optional.empty());
        when(analisisMercadoRepository.save(any(AnalisisMercado.class))).thenAnswer(inv -> inv.getArgument(0));
        List<ProductoIndicadorCatalogo> catalogo = Arrays.stream(productosDelCatalogo)
                .map(codigo -> ProductoIndicadorCatalogo.builder().codigoProducto(codigo)
                        .producto("Producto " + codigo).unidadMedida("m2").build())
                .toList();
        when(productoRepository.findByCodigoProductoIn(anyList())).thenAnswer(inv -> {
            List<String> pedidos = inv.getArgument(0);
            return catalogo.stream().filter(p -> pedidos.contains(p.getCodigoProducto())).toList();
        });
    }

    private static FilaAnalisisMercadoRequestDto filaCompleta(String codigo) {
        return new FilaAnalisisMercadoRequestDto().producto(new ProductoSeleccionadoDto(codigo))
                .demanda(100d).oferta(40d).aniosAProyectar(2).tasaDemanda(10d).tasaOferta(5d);
    }

    private static AnalisisMercadoRequestDto request(FilaAnalisisMercadoRequestDto... filas) {
        return new AnalisisMercadoRequestDto().filas(new ArrayList<>(Arrays.asList(filas)));
    }

    private static List<String> campos(ValidacionNegocioException ex) {
        return ex.getDetalles().stream().map(ErrorDetalleDto::getCampo).toList();
    }

    @Test
    void obtener_proyectoInexistente_lanzaRecursoNoEncontradoConCodigo() {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(1L))
                .isInstanceOfSatisfying(RecursoNoEncontradoException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo(ViabilidadAcceso.PROYECTO_NO_ENCONTRADO));
    }

    @Test
    void obtener_actorDeOtraUnidadEjecutora_lanzaAccesoDenegado() {
        UnidadEjecutora unidadActor = UnidadEjecutora.builder().id(99L).build();
        when(actorContexto.exigir()).thenReturn(Usuario.builder().unidadEjecutora(unidadActor).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoDeUnidad(5L)));

        assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void obtener_actorSinUnidadEjecutora_noRestringeYDevuelveFilasVaciasSiNoHayAnalisis() {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().unidadEjecutora(null).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoDeUnidad(5L)));
        when(analisisMercadoRepository.findByProyectoId(1L)).thenReturn(Optional.empty());

        AnalisisMercadoDto resultado = service.obtener(1L);

        assertThat(resultado.getIdProyecto()).isEqualTo(1L);
        assertThat(resultado.getFilas()).isEmpty();
    }

    @Test
    void obtener_conAnalisisExistente_calculaDeficitYPromediosYToleraDatosIncompletos() {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().unidadEjecutora(null).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoDeUnidad(5L)));
        FilaAnalisisMercado filaCompleta = FilaAnalisisMercado.builder()
                .codigoProducto("P-01").producto("Producto uno").unidadMedida("Unidad")
                .demanda(100d).oferta(40d).aniosAProyectar(2).tasaDemanda(10d).tasaOferta(5d)
                .build();
        FilaAnalisisMercado filaSinProyeccion = FilaAnalisisMercado.builder()
                .codigoProducto("P-02").producto("Producto dos")
                .demanda(50d).oferta(null).build();
        AnalisisMercado analisis = AnalisisMercado.builder().proyecto(proyectoDeUnidad(5L))
                .filas(List.of(filaCompleta, filaSinProyeccion)).build();
        when(analisisMercadoRepository.findByProyectoId(1L)).thenReturn(Optional.of(analisis));

        AnalisisMercadoDto resultado = service.obtener(1L);

        FilaAnalisisMercadoDto dtoCompleta = resultado.getFilas().get(0);
        assertThat(dtoCompleta.getDeficit()).isEqualTo(60d);
        // Anexo B.1: (100 + 110 + 121) / 3 y (40 + 42 + 44,1) / 3.
        assertThat(dtoCompleta.getPromedioDemanda()).isCloseTo(110.333333, within(0.000001));
        assertThat(dtoCompleta.getPromedioOferta()).isCloseTo(42.033333, within(0.000001));
        assertThat(dtoCompleta.getPromedioDeficit()).isCloseTo(68.3, within(0.000001));

        FilaAnalisisMercadoDto dtoIncompleta = resultado.getFilas().get(1);
        assertThat(dtoIncompleta.getDeficit()).isNull();
        assertThat(dtoIncompleta.getPromedioDemanda()).isNull();
        assertThat(dtoIncompleta.getPromedioDeficit()).isNull();
    }

    @Test
    void promedioProyectado_esElPromedioDelAnioBaseYLosProyectados_noElValorDelUltimoAnio() {
        // Demanda 500, tasa 15 %, 5 años: el promedio es 729,48; el valor del año 5 sería 1005,68.
        assertThat(AnalisisMercadoServiceImpl.promedioProyectado(500d, 15d, 5)).isCloseTo(729.48, within(0.01));
        assertThat(AnalisisMercadoServiceImpl.promedioProyectado(500d, 0d, 5)).isEqualTo(500d);
        assertThat(AnalisisMercadoServiceImpl.promedioProyectado(500d, 15d, null)).isNull();
    }

    @Test
    void guardar_sinNingunaFilaCompleta_lanzaValidacionNegocio() {
        prepararGuardado("P-01");

        assertThatThrownBy(() -> service.guardar(1L, request(
                new FilaAnalisisMercadoRequestDto().producto(new ProductoSeleccionadoDto("P-01")))))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo(AnalisisMercadoServiceImpl.CODIGO_SIN_FILA_COMPLETA);
                    assertThat(campos(ex)).containsExactly("filas");
                });
        verify(analisisMercadoRepository, never()).save(any());
    }

    @Test
    void guardar_conRequestNuloOSinFilas_lanzaValidacionNegocio() {
        prepararGuardado();

        assertThatThrownBy(() -> service.guardar(1L, null)).isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> service.guardar(1L, new AnalisisMercadoRequestDto()))
                .isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> service.guardar(1L, new AnalisisMercadoRequestDto().filas(null)))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void guardar_filasALasQueLesFaltaAlgunDato_noCuentanComoCompletas() {
        prepararGuardado("P-01");
        List<FilaAnalisisMercadoRequestDto> filas = new ArrayList<>();
        filas.add(null);
        filas.add(filaCompleta("P-01").producto(null));
        filas.add(filaCompleta(null));
        filas.add(filaCompleta("  "));
        filas.add(filaCompleta("P-01").demanda(null));
        filas.add(filaCompleta("P-01").oferta(null));
        filas.add(filaCompleta("P-01").aniosAProyectar(null));
        filas.add(filaCompleta("P-01").tasaDemanda(null));
        filas.add(filaCompleta("P-01").tasaOferta(null));

        assertThatThrownBy(() -> service.guardar(1L, new AnalisisMercadoRequestDto().filas(filas)))
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo(AnalisisMercadoServiceImpl.CODIGO_SIN_FILA_COMPLETA));
    }

    @Test
    void guardar_conFilaCompleta_tomaNombreYUnidadDelCatalogoEIgnoraElNombreEnviado() {
        prepararGuardado("P-01");

        AnalisisMercadoDto resultado = service.guardar(1L, request(
                filaCompleta("P-01").producto(new ProductoSeleccionadoDto("P-01").producto("Nombre enviado"))));

        FilaAnalisisMercadoDto dto = resultado.getFilas().get(0);
        assertThat(dto.getProducto().getProducto()).isEqualTo("Producto P-01");
        assertThat(dto.getUnidadMedida()).isEqualTo("m2");
    }

    @Test
    void guardar_productoQueNoEstaEnElCatalogo_rechazaSinGuardar() {
        prepararGuardado("P-01");

        assertThatThrownBy(() -> service.guardar(1L, request(filaCompleta("P-01"), filaCompleta("SIN-CATALOGO"))))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo(AnalisisMercadoServiceImpl.CODIGO_PRODUCTO_NO_EN_CATALOGO);
                    assertThat(campos(ex)).containsExactly("filas[1].producto");
                });
        verify(analisisMercadoRepository, never()).save(any());
    }

    @Test
    void guardar_valoresFueraDeRango_reportaCadaCampo() {
        prepararGuardado("P-01");

        assertThatThrownBy(() -> service.guardar(1L, request(
                filaCompleta("P-01").demanda(-1d).aniosAProyectar(0).tasaOferta(-100d))))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo(AnalisisMercadoServiceImpl.CODIGO_VALOR_FUERA_DE_RANGO);
                    assertThat(campos(ex)).containsExactly("filas[0].demanda", "filas[0].aniosAProyectar",
                            "filas[0].tasaOferta");
                });
    }

    @Test
    void guardar_tasaNegativaMayorQueMenos100_seAdmite() {
        prepararGuardado("P-01");

        AnalisisMercadoDto resultado = service.guardar(1L, request(filaCompleta("P-01").tasaDemanda(-5d)));

        assertThat(resultado.getFilas().get(0).getPromedioDemanda()).isLessThan(100d);
    }

    @Test
    void guardar_mismoProductoEnDosFilas_rechaza() {
        prepararGuardado("P-01");

        assertThatThrownBy(() -> service.guardar(1L, request(filaCompleta("P-01"), filaCompleta(" P-01 "))))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo(AnalisisMercadoServiceImpl.CODIGO_PRODUCTO_REPETIDO);
                    assertThat(campos(ex)).containsExactly("filas[1].producto");
                });
    }

    @Test
    void guardar_conAnalisisExistente_actualizaSusFilasEnLugarDeCrearUnoNuevo() {
        prepararGuardado("P-01");
        AnalisisMercado existente = AnalisisMercado.builder().id(9L).proyecto(proyectoDeUnidad(5L)).build();
        when(analisisMercadoRepository.findByProyectoId(1L)).thenReturn(Optional.of(existente));

        AnalisisMercadoDto resultado = service.guardar(1L, request(filaCompleta("P-01")));

        assertThat(resultado.getFilas()).hasSize(1);
        assertThat(existente.getFilas()).hasSize(1);
    }

    @Test
    void guardar_conFilaCompletaYFilasIncompletas_guardaLasIncompletasYDescartaLasVacias() {
        prepararGuardado("P-01");

        AnalisisMercadoDto resultado = service.guardar(1L, request(
                filaCompleta("P-01"),
                new FilaAnalisisMercadoRequestDto().demanda(10d).tasaDemanda(5d),
                new FilaAnalisisMercadoRequestDto().producto(new ProductoSeleccionadoDto(" ")),
                new FilaAnalisisMercadoRequestDto()));

        assertThat(resultado.getFilas()).hasSize(2);
        FilaAnalisisMercadoDto sinProducto = resultado.getFilas().get(1);
        assertThat(sinProducto.getProducto().getCodigoProducto()).isNull();
        assertThat(sinProducto.getProducto().getProducto()).isNull();
        assertThat(sinProducto.getDemanda()).isEqualTo(10d);
        assertThat(sinProducto.getPromedioDemanda()).isNull();
    }
}
