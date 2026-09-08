package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;


import java.util.*;


/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre03ConsultarFiltrar {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final ProyectoCapturaRepository proyectoCapturaRepository;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProyectoRepository proyectoRepository;

    // Listas estáticas con los nombres de los roles mapeados
    /**private static final List<String> ROLES_RN01 = List.of(
            "TECNICO_URP", "VIABILIZADOR", "USUARIOS INTERNOS/EXTERNOS"
    );

    private static final List<String> ROLES_RN02 = List.of(
            "TECNICO_PRE", "COORDINADOR_PRE"
    );
     **/

    private static final List<RolUsuario> ROLES_RN01 = List.of(
            RolUsuario.TECNICO_URP,
            RolUsuario.VIABILIZADOR,
            RolUsuario.TECNICO_ASYMP,
            RolUsuario.TECNICO_PROG,
            RolUsuario.TECNICO_SEG,
            RolUsuario.TECNICO_LEGAL,
            RolUsuario.TECNICO_OPE,
            RolUsuario.TECNICO_SIAF,
            RolUsuario.INGENIERO_DINAFI
    );

    private static final List<RolUsuario> ROLES_RN02 = List.of(
            RolUsuario.TECNICO_PRE,
            RolUsuario.COORDINADOR_PRE,
            RolUsuario.COORDINADOR_PROGRAMACION,
            RolUsuario.ADMINISTRADOR
    );

    // 1. ATRIBUTO DE CLASE (Estado del test)
    private ProyectosCapturaResponseDto respuestaConsulta;
    private ProyectosCapturaResponseDto proyectosResultadoFiltro;
    private Usuario usuarioAutenticado;


    private String terminoBusqueda;
    private String columnaFiltroSeleccionada;

    public Pre03ConsultarFiltrar(ProyectoCapturaRepository proyectoCapturaRepository,
                                 InstitucionRepository institucionRepository,
                                 UnidadEjecutoraRepository unidadEjecutoraRepository,
                                 UsuarioRepository usuarioRepository,
                                 MacroSectorRepository macroSectorRepository,
                                 SectorActividadRepository sectorActividadRepository,
                                 EjeTematicoRepository ejeTematicoRepository,
                                 ProyectoRepository proyectoRepository){
        this.proyectoCapturaRepository = proyectoCapturaRepository;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository  = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.proyectoRepository = proyectoRepository;

    }

    @Dado("que el Sistema ha registrado en {string} los proyectos con CUP")
    public void queElSistemaHaRegistradoEnLosProyectosConCUP(String pantalla) {
        // 1. Crear y guardar el proyecto asegurando saveAndFlush
        crearProyectoConCup();

        // 2. Validar la precondición
        long proyectosValidos = proyectoCapturaRepository.count(
                ProyectoCapturaRepository.Specs.esValidoParaCaptura()
        );

        assertThat(proyectosValidos)
                .withFailMessage("Precondición fallida: No existen proyectos activos con CUP registrados en la BD.")
                .isGreaterThan(0);
    }

    @Cuando("{string} accede a la pantalla {string} \\(Anexo A.{int}\\)")
    public void accedeALaPantallaAnexoA(String actor, String pantalla, int anexo) {
        // 1. Autenticación y asignación del rol
        // 1. Convertimos la cadena del actor al enum adecuado
        RolUsuario rolEnum = resolverRolDesdeActor(actor);

        // 2. IMPORTANTE: Sobrescribimos el contexto autenticando al nuevo usuario
        autenticarUsuarioSegunRol(rolEnum);
        this.respuestaConsulta = consultarProyectosComoResponseDto();
    }

    @Entonces("el sistema muestra el listado de proyectos con CUP con el siguiente alcance: {string}")
    public void elSistemaMuestraElListadoDeProyectosConCUPConElSiguienteAlcance(String alcance) {


        // 1. Verificamos que la respuesta del repositorio no sea nula
        assertThat(this.respuestaConsulta)
                .as("La respuesta de la consulta no debe ser nula")
                .isNotNull();

        List<ProyectoCapturaItemDto> proyectos = this.respuestaConsulta.getContenido();
        String rolUsuarioActual = this.usuarioAutenticado.getRol().name(); // O .getNombre() según tu Enum/Objeto

        // 2. Validación de visibilidad según la regla del Feature
        if (alcance.contains("RN01")) {
            // Validar que el rol corresponda a la regla RN01
            assertThat(ROLES_RN01)
                    .as("El rol [%s] no está registrado bajo la regla RN01", rolUsuarioActual)
                    .contains(RolUsuario.valueOf(rolUsuarioActual.toUpperCase()));

            // Aserción sobre el contenido devuelto bajo RN01
            assertThat(proyectos)
                    .as("El listado para alcance RN01 debe retornar proyectos filtrados")
                    .isNotNull();

        } else if (alcance.contains("RN02")) {
            // Validar que el rol corresponda a la regla RN02
            assertThat(ROLES_RN02)
                    .as("El rol [%s] no está registrado bajo la regla RN02", rolUsuarioActual)
                    .contains(RolUsuario.valueOf(rolUsuarioActual.toUpperCase()));

            // Aserción sobre el contenido devuelto bajo RN02
            assertThat(proyectos)
                    .as("El listado para alcance RN02 no debe estar vacío")
                    .isNotEmpty();
        }
    }

    @Cuando("el actor ingresa un término en el campo {string} \\(placeholder {string}\\)")
    public void elActorIngresaUnTérminoEnElCampoPlaceholder(String arg0, String arg1) {

        this.respuestaConsulta = consultarProyectosComoResponseDtoByFilter(null, null, null, null, null, null);

        assertThat(this.respuestaConsulta)
                .as("Debe existir una consulta previa para aplicar la búsqueda")
                .isNotNull();

        List<ProyectoCapturaItemDto> listaOriginal = this.respuestaConsulta.getContenido();

        this.terminoBusqueda = "Proyecto en revisión PRE";
        // Asumimos el término desde el estado del test o variable global
        String termino = this.terminoBusqueda != null ? this.terminoBusqueda.trim().toLowerCase() : "";

        List<ProyectoCapturaItemDto> resultados = listaOriginal.stream()
                .filter(p -> {
                    // 1. Evaluación por CUP
                    boolean matchCup = p.getCup() != null
                            && p.getCup().toLowerCase().contains(termino);

                    // 2. Evaluación por Nombre del Proyecto
                    boolean matchNombre = p.getNombreProyecto() != null
                            && p.getNombreProyecto().toLowerCase().contains(termino);

                    // 3. Evaluación por Unidad Ejecutora (Null-safe)
                    boolean matchUe = p.getUnidadEjecutora() != null
                            && p.getUnidadEjecutora().getNombre() != null
                            && p.getUnidadEjecutora().getNombre().toLowerCase().contains(termino);

                    // Cadena OR: Si coincide con cualquiera de los tres, pasa el filtro
                    return matchCup || matchNombre || matchUe;
                })
                .toList();

        // Actualizamos la lista filtrada para las aserciones en el @Entonces
        this.respuestaConsulta.setContenido(resultados);
        this.proyectosResultadoFiltro = this.respuestaConsulta;
    }

    @Entonces("el sistema muestra los proyectos cuyo CUP, Nombre o Unidad Ejecutora coincidan con el término ingresado")
    public void elSistemaMuestraLosProyectosCuyoCUPNombreOUnidadEjecutoraCoincidanConElTérminoIngresado() {
        // Validar que la respuesta del controlador exista y contenga datos
        assertThat(this.proyectosResultadoFiltro)
                .isNotNull();

        var proyectos = this.proyectosResultadoFiltro.getContenido();

        assertThat(proyectos)
                .isNotEmpty();



    }

    @Cuando("el actor hace clic en el ícono de filtro \\(▼\\) de la columna {string}")
    public void elActorHaceClicEnElÍconoDeFiltroDeLaColumna(String arg0) {

        this.respuestaConsulta = consultarProyectosComoResponseDto();
        this.columnaFiltroSeleccionada = arg0.trim();

    }

    @Y("aplica un valor de filtro")
    public void aplicaUnValorDeFiltro() {
        assertThat(this.respuestaConsulta)
                .as("Debe haber una lista de proyectos disponible para filtrar")
                .isNotNull();


        List<ProyectoCapturaItemDto> listaOriginal = this.respuestaConsulta.getContenido();

        List<ProyectoCapturaItemDto> filtrados = listaOriginal.stream()
                .filter(proyecto -> {
                    if (this.columnaFiltroSeleccionada == null) return true;

                    return switch (this.columnaFiltroSeleccionada.toUpperCase()) {
                        case "CUP" ->
                                proyecto.getCup() != null && proyecto.getCup().toLowerCase().contains(this.columnaFiltroSeleccionada);

                        case "NOMBRE", "NOMBRE DEL PROYECTO" ->
                                proyecto.getNombreProyecto() != null && proyecto.getNombreProyecto().toLowerCase().contains(this.columnaFiltroSeleccionada);

                        case "UNIDAD EJECUTORA" ->
                            // Mantenemos la evaluación null-safe
                                proyecto.getUnidadEjecutora() != null && proyecto.getUnidadEjecutora().getNombre() != null
                                        && proyecto.getUnidadEjecutora().getNombre().toLowerCase().contains(this.columnaFiltroSeleccionada);

                        default -> true;
                    };
                })
                .toList();

        // Guardamos el resultado filtrado
        this.respuestaConsulta.setContenido(filtrados);
        this.proyectosResultadoFiltro = this.respuestaConsulta;
    }

    @Entonces("el sistema muestra únicamente los proyectos que coinciden con el filtro aplicado en la columna {string}")
    public void elSistemaMuestraÚnicamenteLosProyectosQueCoincidenConElFiltroAplicadoEnLaColumna(String arg0) {
        List<ProyectoCapturaItemDto> proyectosFiltrados = this.respuestaConsulta.getContenido();

        assertThat(proyectosFiltrados)
                .as("La lista filtrada no debería ser nula")
                .isNotNull();

        // Verificamos que absolutamente todos los registros cumplan con el filtro
        assertThat(proyectosFiltrados).allMatch(p -> {
            switch (arg0.trim().toUpperCase()) {
                case "CUP":
                    return p.getCup() != null && p.getCup().toLowerCase().contains(this.columnaFiltroSeleccionada);
                case "NOMBRE", "NOMBRE DEL PROYECTO":
                    return p.getNombreProyecto() != null && p.getNombreProyecto().toLowerCase().contains(this.columnaFiltroSeleccionada);
                default:
                    return true;
            }
        });
    }



    private void autenticarUsuarioSegunRol(RolUsuario rol) {
        // Sufijo unico por escenario: el filtro de tags de Cucumber (@rol:..., @CU-PRE-01) hace
        // que varios escenarios de esta feature corran dentro de la misma sesion de prueba, y
        // codigos/usuarios fijos chocan contra las restricciones UNIQUE si el rollback entre
        // escenarios no aisla completamente cada insercion.
        String sufijo = java.util.UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuarioTecnico = "tecnico.urp.bdd.registro." + sufijo;

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-REG-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-BDD-REG-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        this.usuarioAutenticado =  usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo("tecnico.urp.bdd.registro." + sufijo + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-BDD-" + sufijo, "Eje temático de prueba"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuarioTecnico);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private Proyecto crearProyectoConCup() {
        String sufijo = java.util.UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-VER-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-BDD-VER-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        usuarioRepository.save(sv.gob.mh.siip.model.common.domain.Usuario.builder()
                .nombreUsuario("tecnico.bdd." + sufijo)
                .nombreCompleto("Técnico BDD")
                .correo("tecnico." + sufijo + "@example.com")
                .rol(sv.gob.mh.siip.model.common.enums.RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector ms = macroSectorRepository.save(
                ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector BDD"));

        SectorActividad sector = sectorActividadRepository.save(
                ProyectoFixtures.nuevoSector("S" + sufijo, "Sector BDD", ms));

        EjeTematico eje = ejeTematicoRepository.save(
                ProyectoFixtures.nuevoEjeTematico("EJE-BDD-" + sufijo, "Eje Temático BDD"));

        Proyecto proyecto = ProyectoFixtures.nuevoProyecto(
                "Proyecto BDD con CUP",
                sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, eje);


        int cupRandom = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000, 100000);
        proyecto.setCup(String.format("%05d", cupRandom));
        proyecto.setActivo(true);
        proyecto.setFechaCupAsignado(java.time.LocalDateTime.now());
        return proyectoRepository.save(proyecto);
    }

    private ProyectosCapturaResponseDto consultarProyectosComoResponseDto() {
        var proyectosPage = proyectoCapturaRepository.findAll(
                ProyectoCapturaRepository.Specs.esValidoParaCaptura(),
                org.springframework.data.domain.PageRequest.of(0, 20)
        );

        List<ProyectoCapturaItemDto> dtos = proyectosPage.getContent().stream()
                .map(proyecto -> {
                    ProyectoCapturaItemDto dto = new ProyectoCapturaItemDto();
                    dto.setIdProyecto(proyecto.getId());
                    dto.setNombreProyecto(proyecto.getNombre());
                    dto.setCup(proyecto.getCup());

                    return dto;
                })
                .toList();

        ProyectosCapturaResponseDto response = new ProyectosCapturaResponseDto();
        response.setContenido(dtos);
        return response;
    }

    private RolUsuario resolverRolDesdeActor(String actor) {
        return switch (actor.trim().toUpperCase()) {
            case "TÉCNICO URP", "TECNICO URP", "TECNICO_URP" -> RolUsuario.TECNICO_URP;
            case "VIABILIZADOR" -> RolUsuario.VIABILIZADOR;
            case "TÉCNICO PRE", "TECNICO PRE", "TECNICO_PRE" -> RolUsuario.TECNICO_PRE;
            case "COORDINADOR PRE", "COORDINADOR_PRE" -> RolUsuario.COORDINADOR_PRE;

            // Mapeo explícito para la etiqueta de la tabla del Feature
            case "USUARIOS INTERNOS/EXTERNOS", "USUARIOS_INTERNOS_EXTERNOS" -> RolUsuario.TECNICO_URP;

            default -> throw new IllegalArgumentException("Actor no soportado para el test: " + actor);
        };
    }

    private ProyectosCapturaResponseDto consultarProyectosComoResponseDtoByFilter(
            String cup,
            String nombre,
            Long idUnidadEjecutora,
            String iniciativa,
            String estado,
            String busquedaGlobal) {

        // 1. Especificación base obligatoria (Fetch JOIN + Reglas globales de captura)
        Specification<Proyecto> baseSpec = Specification.<Proyecto>unrestricted()
                .and(ProyectoCapturaRepository.Specs.fetchUnidadEjecutora())
                .and(ProyectoCapturaRepository.Specs.esValidoParaCaptura());

        // 2. Acumulador de filtros dinámicos en 'OR'
        Specification<Proyecto> searchFilters = null;

        if (cup != null && !cup.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byCup(cup)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byCup(cup));
        }

        if (nombre != null && !nombre.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byNombre(nombre)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byNombre(nombre));
        }

        if (idUnidadEjecutora != null) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byUnidadEjecutora(idUnidadEjecutora)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byUnidadEjecutora(idUnidadEjecutora));
        }

        if (iniciativa != null && !iniciativa.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byIniciativa(iniciativa)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byIniciativa(iniciativa));
        }

        if (estado != null && !estado.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byEstado(estado)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byEstado(estado));
        }

        if (busquedaGlobal != null && !busquedaGlobal.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byBusquedaGeneral(busquedaGlobal)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byBusquedaGeneral(busquedaGlobal));
        }

        // 3. Unimos las reglas base con el bloque acumulado de ORs (si se ingresó algún filtro)
        Specification<Proyecto> finalSpec = (searchFilters != null)
                ? baseSpec.and(searchFilters)
                : baseSpec;

        // 4. Consulta paginada en BD
        var proyectosPage = proyectoCapturaRepository.findAll(
                finalSpec,
                org.springframework.data.domain.PageRequest.of(0, 20)
        );

        // 5. Mapeo a DTO de respuesta
        List<ProyectoCapturaItemDto> dtos = proyectosPage.getContent().stream()
                .map(proyecto -> {
                    ProyectoCapturaItemDto dto = new ProyectoCapturaItemDto();
                    UnidadEjecutoraResumenDto unidadEjecutoraDTO = new UnidadEjecutoraResumenDto();
                    IniciativaInversionDto iniciativaInversionDto = null;
                    EstadoProyectoDto  estadoProyectoDto = null;

                    unidadEjecutoraDTO.setIdUnidadEjecutora(proyecto.getUnidadEjecutora().getId());
                    unidadEjecutoraDTO.setCodigo(proyecto.getUnidadEjecutora().getCodigo());
                    unidadEjecutoraDTO.setNombre(proyecto.getUnidadEjecutora().getNombre());
                    dto.setUnidadEjecutora(unidadEjecutoraDTO);

                    if(proyecto.getIniciativaInversion() != null){
                        String iniciativaString = proyecto.getIniciativaInversion().name();
                        iniciativaInversionDto = IniciativaInversionDto.fromValue(iniciativaString);
                    }
                    if(proyecto.getEstado() != null){
                        String estadoString = proyecto.getEstado().name();
                        estadoProyectoDto = EstadoProyectoDto.fromValue(estadoString);
                    }



                    dto.setIdProyecto(proyecto.getId());
                    dto.setNombreProyecto(proyecto.getNombre());
                    dto.setCup(proyecto.getCup());
                    dto.setUnidadEjecutora(unidadEjecutoraDTO);
                    dto.setIniciativaInversion(iniciativaInversionDto);
                    dto.setEstado(estadoProyectoDto);
                    return dto;
                })
                .toList();

        ProyectosCapturaResponseDto response = new ProyectosCapturaResponseDto();
        response.setContenido(dtos);
        return response;
    }
}