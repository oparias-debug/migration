package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.util.List;
import java.util.UUID;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorResultado;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorResultadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/** Steps de integración de CU-PRE-23 contra H2 y el servicio real. */
public class Pre23IndicadoresProyecto {
    private final InstitucionRepository instituciones;
    private final UnidadEjecutoraRepository unidades;
    private final UsuarioRepository usuarios;
    private final ProyectoRepository proyectos;
    private final MacroSectorRepository macrosectores;
    private final SectorActividadRepository sectores;
    private final EjeTematicoRepository ejes;
    private final ComponenteRepository componentes;
    private final IndicadorResultadoRepository resultados;
    private final ProductoIndicadorCatalogoRepository catalogoProductos;
    private final IndicadoresProyectoService service;
    private Proyecto proyecto;
    private Componente componente;
    private RuntimeException error;
    private String sufijo;

    public Pre23IndicadoresProyecto(InstitucionRepository instituciones, UnidadEjecutoraRepository unidades,
            UsuarioRepository usuarios, ProyectoRepository proyectos, MacroSectorRepository macrosectores,
            SectorActividadRepository sectores, EjeTematicoRepository ejes, ComponenteRepository componentes,
            IndicadorResultadoRepository resultados, ProductoIndicadorCatalogoRepository catalogoProductos,
            IndicadoresProyectoService service) {
        this.instituciones = instituciones;
        this.unidades = unidades;
        this.usuarios = usuarios;
        this.proyectos = proyectos;
        this.macrosectores = macrosectores;
        this.sectores = sectores;
        this.ejes = ejes;
        this.componentes = componentes;
        this.resultados = resultados;
        this.catalogoProductos = catalogoProductos;
        this.service = service;
    }

    @Before("@CU-PRE-23")
    public void prepararEscenario() {
        sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("MH-CU23-" + sufijo,
                "Ministerio de Hacienda CU-PRE-23"));
        UnidadEjecutora unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE23-" + sufijo,
                "Unidad ejecutora CU-PRE-23", institucion));
        String usuario = "tecnico.urp.pre23." + sufijo;
        usuarios.save(Usuario.builder().nombreUsuario(usuario).nombreCompleto(usuario).correo(usuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP).institucion(institucion).unidadEjecutora(unidad).activo(true).build());
        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M23-" + sufijo, "Macro CU23"));
        SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S23-" + sufijo, "Sector CU23", macrosector));
        EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E23-" + sufijo, "Eje CU23"));
        proyecto = proyectos.save(ProyectoFixtures.nuevoProyecto("Proyecto CU-PRE-23", EstadoProyecto.EN_REGISTRO,
                unidad, institucion, sector, eje));
        autenticar(usuario);
    }

    @Dado("que existe un producto de CU-PRE-11 para indicadores -cu-pre-23")
    public void crearProductoConCantidad() {
        componente = componentes.save(Componente.builder().proyecto(proyecto).nombre("Componente")
                .descripcion("Producto de prueba").codigoProducto("P-23-" + sufijo).cantidad(70D).unidadMedida("Unidad").build());
        catalogoProductos.save(ProductoIndicadorCatalogo.builder().codigoProducto("P-23-" + sufijo).producto("Producto de prueba")
                .codigoIndicador("IP-23-" + sufijo).indicador("Indicador principal").unidadMedida("Unidad")
                .esIndicadorPrincipal(true).build());
    }

    @Cuando("el Técnico URP registra una meta global de 70 con períodos 25, 30 y 10 -cu-pre-23")
    public void registrarMetaInconsistente() {
        try {
            service.registrarProducto(proyecto.getId(), componente.getId(),
                    new IndicadorProductoRequestDto("IP-23-" + sufijo, 70D, true).metasPorPeriodo(List.of(25D, 30D, 10D)));
        } catch (RuntimeException excepcion) {
            error = excepcion;
        }
    }

    @Entonces("el sistema rechaza la suma distinta de la meta global -cu-pre-23")
    public void validarSuma() {
        assertThat(error).isInstanceOf(ValidacionNegocioException.class).hasMessageContaining("suma de los períodos");
    }

    @Dado("que existe un producto de CU-PRE-11 y un indicador de resultado -cu-pre-23")
    public void crearProductoYResultado() {
        crearProductoConCantidad();
        resultados.save(IndicadorResultado.builder().codigo("IR-23-" + sufijo).nombre("Resultado de prueba")
                .unidadMedida("Unidad").build());
        service.registrarResultado(proyecto.getId(), new IndicadorResultadoRequestDto("IR-23-" + sufijo, 1D));
    }

    @Cuando("el Técnico URP confirma los indicadores del proyecto -cu-pre-23")
    public void confirmarIndicadores() {
        try {
            service.guardar(proyecto.getId());
        } catch (RuntimeException excepcion) {
            error = excepcion;
        }
    }

    @Entonces("el sistema crea el indicador principal del catálogo C.1 -cu-pre-23")
    public void validarPrincipal() {
        assertThat(error).isNull();
        assertThat(service.obtener(proyecto.getId()).getProductos()).singleElement().satisfies(producto -> {
            assertThat(producto.getIndicadores()).singleElement().satisfies(indicador -> {
                assertThat(indicador.getEsIndicadorPrincipal()).isTrue();
                assertThat(indicador.getMetaGlobal()).isEqualTo(70D);
            });
        });
    }

    @Dado("que existe un producto de CU-PRE-11 sin cantidad -cu-pre-23")
    public void crearProductoIncompleto() {
        componente = componentes.save(Componente.builder().proyecto(proyecto).nombre("Componente")
                .descripcion("Producto incompleto").codigoProducto("P-23-" + sufijo).unidadMedida("Unidad").build());
        resultados.save(IndicadorResultado.builder().codigo("IR-23-" + sufijo).nombre("Resultado de prueba")
                .unidadMedida("Unidad").build());
        service.registrarResultado(proyecto.getId(), new IndicadorResultadoRequestDto("IR-23-" + sufijo, 1D));
    }

    @Entonces("el sistema exige completar código y cantidad del producto -cu-pre-23")
    public void validarProductoIncompleto() {
        assertThat(error).isInstanceOf(ValidacionNegocioException.class).hasMessageContaining("código y cantidad");
    }

    private static void autenticar(String usuario) {
        AutenticacionDePrueba.autenticar(usuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }
}
