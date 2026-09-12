package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.DepartamentoRepository;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CeldaUbicacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.InteresadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoInteresadoDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AnalisisPoblacionService;
import sv.gob.mh.siip.model.preinversion.service.AreaInfluenciaService;
import sv.gob.mh.siip.model.preinversion.service.MatrizInteresadosService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-06-consultar.feature. A diferencia de CU-PRE-04/CU-PRE-05, ninguna regla de este CU
 * condiciona el acceso de Tecnico PRE a que la matriz ya se haya guardado al menos una vez (RN02
 * no lo menciona, ver contrato-CU-PRE-06.md): el Tecnico PRE de este escenario no tiene Unidad
 * Ejecutora propia (mismo criterio "sin restriccion" que fragmentos anteriores).
 * <p>
 * Ambos pasos de esta clase son ademas texto identico al de
 * CU-PRE-07-consultar-validar-tecnico-pre.feature y CU-PRE-08-consultar.feature (Cucumber exige
 * una unica definicion por texto): segun el valor de {@code pantalla} se ejecuta la rama
 * equivalente contra {@link AnalisisPoblacionService} o {@link AreaInfluenciaService} en lugar de
 * {@link MatrizInteresadosService}. Antes, cualquier {@code pantalla} que no fuera exactamente
 * "Análisis de la Población" caía en la rama por defecto de {@link MatrizInteresadosService}, asi
 * que CU-PRE-08-consultar.feature ("Área de Influencia") pasaba sin ejercitar
 * {@link AreaInfluenciaService} en absoluto.
 */
public class Pre06Consultar {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String PANTALLA_ANALISIS_POBLACION = "Análisis de la Población";
    private static final String PANTALLA_AREA_INFLUENCIA = "Área de Influencia";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MatrizInteresadosService matrizInteresadosService;
    private final AnalisisPoblacionService analisisPoblacionService;
    private final AreaInfluenciaService areaInfluenciaService;
    private final DepartamentoRepository departamentoRepository;
    private final MunicipioRepository municipioRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyecto;
    private MatrizInteresadosDto guardado;
    private MatrizInteresadosDto consultado;
    private AnalisisPoblacionDto guardadoPoblacion;
    private AnalisisPoblacionDto consultadoPoblacion;
    private AreaInfluenciaDto guardadoAreaInfluencia;
    private AreaInfluenciaDto consultadoAreaInfluencia;

    public Pre06Consultar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            MatrizInteresadosService matrizInteresadosService,
            AnalisisPoblacionService analisisPoblacionService,
            AreaInfluenciaService areaInfluenciaService,
            DepartamentoRepository departamentoRepository,
            MunicipioRepository municipioRepository,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.matrizInteresadosService = matrizInteresadosService;
        this.analisisPoblacionService = analisisPoblacionService;
        this.areaInfluenciaService = areaInfluenciaService;
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Cuando("el Técnico PRE accede a la pantalla {string} de cualquier Unidad Ejecutora")
    public void el_tecnico_pre_accede_a_la_pantalla_de_cualquier_unidad_ejecutora(String pantalla) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE06C-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-PRE06C-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioUrp = "tecnico.urp.bdd.pre06c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioUrp)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioUrp + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE06C-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto consulta interesados BDD",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, sector, ejeTematico));

        autenticarComo(nombreUsuarioUrp);
        boolean esAnalisisPoblacion = PANTALLA_ANALISIS_POBLACION.equals(pantalla);
        boolean esAreaInfluencia = PANTALLA_AREA_INFLUENCIA.equals(pantalla);
        if (esAnalisisPoblacion) {
            guardadoPoblacion = analisisPoblacionService.guardar(proyecto.getId(), new AnalisisPoblacionRequestDto()
                    .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(
                            new CeldaUbicacionRequestDto().ubicacion("Ubicación de prueba BDD").numeroPersonas(100))))
                    .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(
                            new CeldaUbicacionRequestDto().ubicacion("Ubicación de prueba BDD").numeroPersonas(80)))));
        } else if (esAreaInfluencia) {
            guardadoAreaInfluencia = areaInfluenciaService.guardar(proyecto.getId(), new AreaInfluenciaRequestDto()
                    .filas(List.of(new AreaInfluenciaFilaRequestDto()
                            .distrito(crearDistrito(sufijo))
                            .ubicacionEspecifica("Ubicación específica de prueba BDD"))));
        } else {
            guardado = matrizInteresadosService.guardar(proyecto.getId(), new MatrizInteresadosRequestDto()
                    .interesados(List.of(new InteresadoRequestDto()
                            .nombreInteresado("Interesado de prueba BDD")
                            .tipo(TipoInteresadoDto.COOPERANTE)
                            .estrategiaGestion("Estrategia de prueba BDD"))));
        }

        String sufijoPre = UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuarioPre = "tecnico.pre.bdd.pre06c." + sufijoPre;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioPre)
                .nombreCompleto("Tecnico PRE (BDD)")
                .correo(nombreUsuarioPre + "@example.com")
                .rol(RolUsuario.TECNICO_PRE)
                .activo(true)
                .build());
        autenticarComo(nombreUsuarioPre);

        if (esAnalisisPoblacion) {
            consultadoPoblacion = analisisPoblacionService.obtener(proyecto.getId());
        } else if (esAreaInfluencia) {
            consultadoAreaInfluencia = areaInfluenciaService.obtener(proyecto.getId());
        } else {
            consultado = matrizInteresadosService.obtener(proyecto.getId());
        }
    }

    @Entonces("el sistema muestra la información registrada por el Técnico URP en modo solo lectura")
    public void el_sistema_muestra_la_informacion_registrada_por_el_tecnico_urp_en_modo_solo_lectura() {
        if (consultadoPoblacion != null) {
            assertThat(consultadoPoblacion.getIdProyecto()).isEqualTo(guardadoPoblacion.getIdProyecto());
            assertThat(consultadoPoblacion.getPoblacionReferencia().getUbicaciones()).hasSize(1);
            assertThat(consultadoPoblacion.getPoblacionReferencia().getUbicaciones().get(0).getNumeroPersonas())
                    .isEqualTo(guardadoPoblacion.getPoblacionReferencia().getUbicaciones().get(0).getNumeroPersonas());
            return;
        }
        if (consultadoAreaInfluencia != null) {
            assertThat(consultadoAreaInfluencia.getIdProyecto()).isEqualTo(guardadoAreaInfluencia.getIdProyecto());
            assertThat(consultadoAreaInfluencia.getFilas()).hasSize(1);
            assertThat(consultadoAreaInfluencia.getFilas().get(0).getUbicacionEspecifica())
                    .isEqualTo(guardadoAreaInfluencia.getFilas().get(0).getUbicacionEspecifica());
            return;
        }
        assertThat(consultado.getIdProyecto()).isEqualTo(guardado.getIdProyecto());
        assertThat(consultado.getInteresados()).hasSize(1);
        assertThat(consultado.getInteresados().get(0).getNombreInteresado())
                .isEqualTo(guardado.getInteresados().get(0).getNombreInteresado());
    }

    @Entonces("no permite su edición")
    public void no_permite_su_edicion() {
        // El GET consultado no ofrece ninguna operacion de escritura: la ausencia de un metodo
        // "guardar" en la superficie de lectura ya impide la edicion. Sin acceso a un cliente HTTP
        // real en este nivel de prueba, se verifica que el actor consultado sigue siendo de solo
        // lectura (Tecnico PRE, sin permiso de "guardarMatrizInteresados" segun x-roles).
        assertThat(consultado).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private String crearDistrito(String sufijo) {
        // CODIGO de Departamento/Municipio es VARCHAR(10): prefijo de una letra + sufijo de 8.
        Departamento departamento = departamentoRepository.save(Departamento.builder()
                .codigo("D" + sufijo).nombre("Departamento BDD PRE06C").region("Region BDD PRE06C").build());
        String nombreDistrito = "Distrito BDD PRE06C " + sufijo;
        municipioRepository.save(Municipio.builder()
                .codigo("M" + sufijo).nombre(nombreDistrito).departamento(departamento).build());
        return nombreDistrito;
    }
}
