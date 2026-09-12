package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoCapturaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaFiltro;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Ejercita {@link ProyectoCapturaService} real (antes esta clase reconstruía su propia
 * consulta/mapeo DTO en paralelo al servicio, así que nunca probaba el código de producción).
 *
 * @author Luis Medrano
 * @version 2.0
 */
public class Pre03ConsultarFiltrar {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final ProyectoCapturaService proyectoCapturaService;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProyectoRepository proyectoRepository;

    private ProyectosCapturaResponseDto respuestaConsulta;
    private Usuario usuarioAutenticado;
    private Proyecto proyectoObjetivo;
    private Proyecto proyectoDecoy;
    private String columnaFiltroSeleccionada;

    public Pre03ConsultarFiltrar(ProyectoCapturaService proyectoCapturaService,
            InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository,
            ProyectoRepository proyectoRepository) {
        this.proyectoCapturaService = proyectoCapturaService;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Dado("que el Sistema ha registrado en {string} los proyectos con CUP")
    public void queElSistemaHaRegistradoEnLosProyectosConCUP(String pantalla) {
        // Dos proyectos con nombre/CUP/estado/iniciativa/Unidad Ejecutora distintos entre sí: el
        // "objetivo" es el que búsqueda/filtro deben encontrar, el "decoy" prueba que el filtro
        // realmente excluye lo que no coincide (una lista vacía no debería bastar para pasar
        // ninguno de los escenarios de este Feature).
        this.proyectoObjetivo = crearProyectoDeListado("Proyecto Captura Objetivo",
                EstadoProyecto.CUP_ASIGNADO, IniciativaInversion.PROYECTO);
        this.proyectoDecoy = crearProyectoDeListado("Otro Proyecto Distinto",
                EstadoProyecto.EN_FORMULACION, IniciativaInversion.PROGRAMA);

        assertThat(proyectoObjetivo.getId())
                .as("Precondición fallida: el proyecto objetivo no quedó registrado con CUP.")
                .isNotNull();
        assertThat(proyectoDecoy.getId())
                .as("Precondición fallida: el proyecto decoy no quedó registrado con CUP.")
                .isNotNull();

        // Actor por defecto para los escenarios que no autentican uno propio (Buscar/Filtrar):
        // el servicio real exige un actor autenticado. Coordinador PRE no tiene restricción de
        // Unidad Ejecutora (RN02), así que no interfiere con esos escenarios.
        autenticarUsuarioSegunRol(RolUsuario.COORDINADOR_PRE, null);
    }

    @Cuando("{string} accede a la pantalla {string} \\(Anexo A.{int}\\)")
    public void accedeALaPantallaAnexoA(String actor, String pantalla, int anexo) {
        RolUsuario rol = resolverRolDesdeActor(actor);
        // RN01/RN02 (decisión funcional del usuario, 11/09/2026): el Técnico URP ve únicamente
        // los proyectos de su propia Unidad Ejecutora; el resto de los actores ve todos, sin
        // importar la suya. Para poder probar la restricción de verdad, al actor URP se le asigna
        // la MISMA Unidad Ejecutora que proyectoObjetivo (así debe ver el objetivo pero no el
        // decoy, que pertenece a otra); a cualquier otro rol se le asigna una unidad ajena a
        // ambos (así ver los dos confirma que no hay restricción).
        UnidadEjecutora unidadEjecutora = rol == RolUsuario.TECNICO_URP
                ? proyectoObjetivo.getUnidadEjecutora() : null;
        autenticarUsuarioSegunRol(rol, unidadEjecutora);
        this.respuestaConsulta = consultar(new ProyectoCapturaFiltro(null, null, null, null, null, null));
    }

    @Entonces("el sistema muestra el listado de proyectos con CUP con el siguiente alcance: {string}")
    public void elSistemaMuestraElListadoDeProyectosConCUPConElSiguienteAlcance(String alcance) {
        assertThat(this.respuestaConsulta).as("La respuesta de la consulta no debe ser nula").isNotNull();

        var idsVisibles = this.respuestaConsulta.getContenido().stream()
                .map(ProyectoCapturaItemDto::getIdProyecto)
                .toList();

        if (this.usuarioAutenticado.getRol() == RolUsuario.TECNICO_URP) {
            assertThat(idsVisibles)
                    .as("RN01: el Técnico URP solo debería ver proyectos de su propia Unidad Ejecutora")
                    .contains(this.proyectoObjetivo.getId())
                    .doesNotContain(this.proyectoDecoy.getId());
        } else {
            assertThat(idsVisibles)
                    .as("RN02: el rol [%s] debería ver proyectos de cualquier Unidad Ejecutora",
                            this.usuarioAutenticado.getRol())
                    .contains(this.proyectoObjetivo.getId(), this.proyectoDecoy.getId());
        }
    }

    @Cuando("el actor ingresa un término en el campo {string} \\(placeholder {string}\\)")
    public void elActorIngresaUnTerminoEnElCampo(String campo, String placeholder) {
        // RN03: término que solo coincide con el proyecto objetivo, no con el decoy — así
        // "Buscar" prueba que filtra, no solo que la búsqueda por CUP/Nombre/UE no rompe nada.
        // El clic en "BUSCAR" lo cubre el paso genérico "hace clic en el botón {string}"
        // (Pre01ResponderObservaciones), sin efecto propio: la búsqueda ya se aplicó aquí.
        String termino = this.proyectoObjetivo.getNombre();
        this.respuestaConsulta = consultar(new ProyectoCapturaFiltro(termino, null, null, null, null, null));
    }

    @Entonces("el sistema muestra los proyectos cuyo CUP, Nombre o Unidad Ejecutora coincidan con el término ingresado")
    public void elSistemaMuestraLosProyectosQueCoincidenConElTerminoIngresado() {
        List<ProyectoCapturaItemDto> proyectos = this.respuestaConsulta.getContenido();

        assertThat(proyectos)
                .as("Debe encontrar al menos el proyecto objetivo")
                .isNotEmpty()
                .extracting(ProyectoCapturaItemDto::getIdProyecto)
                .contains(this.proyectoObjetivo.getId())
                .doesNotContain(this.proyectoDecoy.getId());
    }

    @Cuando("el actor hace clic en el ícono de filtro \\(▼\\) de la columna {string}")
    public void elActorHaceClicEnElIconoDeFiltroDeLaColumna(String columna) {
        this.columnaFiltroSeleccionada = columna.trim();
    }

    @Y("aplica un valor de filtro")
    public void aplicaUnValorDeFiltro() {
        // El valor de filtro aplicado es siempre el que corresponde al proyecto objetivo en esa
        // columna, para poder verificar después que el decoy (con un valor distinto en la misma
        // columna) queda excluido del resultado.
        ProyectoCapturaFiltro filtro = switch (columnaFiltroSeleccionada.toUpperCase()) {
            case "CUP" -> new ProyectoCapturaFiltro(null, proyectoObjetivo.getCup(), null, null, null, null);
            case "NOMBRE DEL PROYECTO" ->
                    new ProyectoCapturaFiltro(null, null, proyectoObjetivo.getNombre(), null, null, null);
            case "INICIATIVA DE INVERSIÓN" -> new ProyectoCapturaFiltro(null, null, null,
                    IniciativaInversionDto.fromValue(proyectoObjetivo.getIniciativaInversion().name()), null, null);
            case "ESTADO" -> new ProyectoCapturaFiltro(null, null, null, null,
                    EstadoProyectoDto.fromValue(proyectoObjetivo.getEstado().name()), null);
            case "UNIDAD EJECUTORA" -> new ProyectoCapturaFiltro(
                    null, null, null, null, null, proyectoObjetivo.getUnidadEjecutora().getId());
            default -> throw new IllegalArgumentException("Columna no soportada: " + columnaFiltroSeleccionada);
        };

        this.respuestaConsulta = consultar(filtro);
    }

    @Entonces("el sistema muestra únicamente los proyectos que coinciden con el filtro aplicado en la columna {string}")
    public void elSistemaMuestraUnicamenteLosProyectosQueCoincidenConElFiltroAplicadoEnLaColumna(String columna) {
        List<ProyectoCapturaItemDto> proyectos = this.respuestaConsulta.getContenido();

        assertThat(proyectos)
                .as("El filtro por columna '%s' no debería vaciar el listado", columna)
                .isNotEmpty()
                .extracting(ProyectoCapturaItemDto::getIdProyecto)
                .contains(this.proyectoObjetivo.getId())
                .doesNotContain(this.proyectoDecoy.getId());
    }

    private ProyectosCapturaResponseDto consultar(ProyectoCapturaFiltro filtro) {
        return proyectoCapturaService.listarProyectosCaptura(filtro, 0, 20);
    }

    /**
     * @param unidadEjecutoraForzada si no es {@code null}, el actor queda asignado exactamente a
     *        esa Unidad Ejecutora (necesario para probar RN01 contra proyectoObjetivo); si es
     *        {@code null}, se le crea una unidad nueva y ajena a los proyectos de prueba.
     */
    private void autenticarUsuarioSegunRol(RolUsuario rol, UnidadEjecutora unidadEjecutoraForzada) {
        // Sufijo unico por escenario: el filtro de tags de Cucumber (@rol:..., @UC-PRE-03) hace
        // que varios escenarios de esta feature corran dentro de la misma sesion de prueba, y
        // codigos/usuarios fijos chocan contra las restricciones UNIQUE si el rollback entre
        // escenarios no aisla completamente cada insercion.
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        UnidadEjecutora unidadEjecutora = unidadEjecutoraForzada;
        Institucion institucion;
        if (unidadEjecutora != null) {
            institucion = unidadEjecutora.getInstitucion();
        } else {
            institucion = institucionRepository
                    .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-A-" + sufijo, "Institucion de prueba"));
            unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                    .nuevaUnidadEjecutora("UE-BDD-A-" + sufijo, "Unidad Ejecutora de prueba", institucion));
        }

        this.usuarioAutenticado = usuarioRepository.save(Usuario.builder()
                .nombreUsuario("actor.bdd." + sufijo)
                .nombreCompleto("Actor BDD")
                .correo("actor.bdd." + sufijo + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, this.usuarioAutenticado.getNombreUsuario());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private Proyecto crearProyectoDeListado(String nombre, EstadoProyecto estado, IniciativaInversion iniciativa) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-BDD-" + sufijo, "Unidad Ejecutora " + sufijo, institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector BDD"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector BDD", macrosector));
        EjeTematico eje = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-BDD-" + sufijo, "Eje Temático BDD"));

        Proyecto proyecto = ProyectoFixtures.nuevoProyecto(
                nombre + " " + sufijo, estado, unidadEjecutora, institucion, sector, eje);
        proyecto.setIniciativaInversion(iniciativa);
        proyecto.setCup(String.format("%05d", ThreadLocalRandom.current().nextInt(10000, 100000)));
        proyecto.setActivo(true);
        proyecto.setFechaCupAsignado(LocalDateTime.now(ZoneId.of("America/El_Salvador")));

        return proyectoRepository.save(proyecto);
    }

    private RolUsuario resolverRolDesdeActor(String actor) {
        return switch (actor.trim().toUpperCase()) {
            case "TÉCNICO URP", "TECNICO URP", "TECNICO_URP" -> RolUsuario.TECNICO_URP;
            case "VIABILIZADOR" -> RolUsuario.VIABILIZADOR;
            case "TÉCNICO PRE", "TECNICO PRE", "TECNICO_PRE" -> RolUsuario.TECNICO_PRE;
            case "COORDINADOR PRE", "COORDINADOR_PRE" -> RolUsuario.COORDINADOR_PRE;
            default -> throw new IllegalArgumentException("Actor no soportado para el test: " + actor);
        };
    }
}
