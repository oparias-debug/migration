package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void obtener_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(RecursoNoEncontradoException.class);
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
    void obtener_conAnalisisExistente_calculaDeficitYProyeccionYToleraDatosIncompletos() {
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

        assertThat(resultado.getFilas()).hasSize(2);
        FilaAnalisisMercadoDto dtoCompleta = resultado.getFilas().get(0);
        assertThat(dtoCompleta.getDeficit()).isEqualTo(60d);
        assertThat(dtoCompleta.getPromedioDemanda()).isEqualTo(100d * Math.pow(1.10, 2));
        assertThat(dtoCompleta.getPromedioOferta()).isEqualTo(40d * Math.pow(1.05, 2));
        assertThat(dtoCompleta.getPromedioDeficit()).isNotNull();

        FilaAnalisisMercadoDto dtoIncompleta = resultado.getFilas().get(1);
        assertThat(dtoIncompleta.getDeficit()).isNull();
        assertThat(dtoIncompleta.getPromedioDemanda()).isNull();
        assertThat(dtoIncompleta.getPromedioDeficit()).isNull();
    }

    @Test
    void guardar_sinNingunaFilaCompleta_lanzaValidacionNegocio() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().unidadEjecutora(null).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoDeUnidad(5L)));
        FilaAnalisisMercadoRequestDto filaIncompleta = new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto("P-01"));
        AnalisisMercadoRequestDto request = new AnalisisMercadoRequestDto().addFilasItem(filaIncompleta);

        ValidacionNegocioException ex = assertThrows(ValidacionNegocioException.class,
                () -> service.guardar(1L, request));
        assertThat(ex.getCodigo()).isEqualTo("ANALISIS_MERCADO_SIN_FILA_COMPLETA");
        verify(analisisMercadoRepository, never()).save(any());
    }

    @Test
    void guardar_conRequestSinFilas_lanzaValidacionNegocio() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().unidadEjecutora(null).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoDeUnidad(5L)));
        AnalisisMercadoRequestDto request = new AnalisisMercadoRequestDto();

        assertThatThrownBy(() -> service.guardar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void guardar_conFilaCompletaYProductoEnCatalogo_creaAnalisisNuevoUsandoDatosDelCatalogo() {
        Proyecto proyecto = proyectoDeUnidad(5L);
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().unidadEjecutora(null).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(analisisMercadoRepository.findByProyectoId(1L)).thenReturn(Optional.empty());
        ProductoIndicadorCatalogo productoCatalogo = ProductoIndicadorCatalogo.builder()
                .codigoProducto("P-01").producto("Producto del catálogo").unidadMedida("m2").build();
        when(productoRepository.findByCodigoProductoIn(List.of("P-01"))).thenReturn(List.of(productoCatalogo));
        when(analisisMercadoRepository.save(any(AnalisisMercado.class))).thenAnswer(inv -> inv.getArgument(0));

        FilaAnalisisMercadoRequestDto filaCompleta = new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto("P-01").producto("Nombre enviado por el usuario"))
                .demanda(100d).oferta(40d).aniosAProyectar(2).tasaDemanda(10d).tasaOferta(5d);
        AnalisisMercadoRequestDto request = new AnalisisMercadoRequestDto().addFilasItem(filaCompleta);

        AnalisisMercadoDto resultado = service.guardar(1L, request);

        assertThat(resultado.getFilas()).hasSize(1);
        FilaAnalisisMercadoDto dto = resultado.getFilas().get(0);
        assertThat(dto.getProducto().getProducto()).isEqualTo("Producto del catálogo");
        assertThat(dto.getUnidadMedida()).isEqualTo("m2");
    }

    @Test
    void guardar_conProductoNoRegistradoEnCatalogo_usaNombreEnviadoYUnidadMedidaNula() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().unidadEjecutora(null).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoDeUnidad(5L)));
        when(analisisMercadoRepository.findByProyectoId(1L)).thenReturn(Optional.empty());
        when(productoRepository.findByCodigoProductoIn(List.of("SIN-CATALOGO"))).thenReturn(List.of());
        when(analisisMercadoRepository.save(any(AnalisisMercado.class))).thenAnswer(inv -> inv.getArgument(0));

        FilaAnalisisMercadoRequestDto filaCompleta = new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto("SIN-CATALOGO").producto("Nombre enviado por el usuario"))
                .demanda(100d).oferta(40d).aniosAProyectar(2).tasaDemanda(10d).tasaOferta(5d);
        AnalisisMercadoRequestDto request = new AnalisisMercadoRequestDto().addFilasItem(filaCompleta);

        AnalisisMercadoDto resultado = service.guardar(1L, request);

        FilaAnalisisMercadoDto dto = resultado.getFilas().get(0);
        assertThat(dto.getProducto().getProducto()).isEqualTo("Nombre enviado por el usuario");
        assertThat(dto.getUnidadMedida()).isNull();
    }

    @Test
    void guardar_conAnalisisExistente_actualizaSusFilasEnLugarDeCrearUnoNuevo() {
        Proyecto proyecto = proyectoDeUnidad(5L);
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().unidadEjecutora(null).build());
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        AnalisisMercado existente = AnalisisMercado.builder().id(9L).proyecto(proyecto).build();
        when(analisisMercadoRepository.findByProyectoId(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.findByCodigoProductoIn(List.of("P-01"))).thenReturn(List.of());
        when(analisisMercadoRepository.save(any(AnalisisMercado.class))).thenAnswer(inv -> inv.getArgument(0));

        FilaAnalisisMercadoRequestDto filaCompleta = new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto("P-01"))
                .demanda(100d).oferta(40d).aniosAProyectar(2).tasaDemanda(10d).tasaOferta(5d);
        AnalisisMercadoRequestDto request = new AnalisisMercadoRequestDto().addFilasItem(filaCompleta);

        AnalisisMercadoDto resultado = service.guardar(1L, request);

        assertThat(resultado.getFilas()).hasSize(1);
        assertThat(existente.getFilas()).hasSize(1);
    }
}
