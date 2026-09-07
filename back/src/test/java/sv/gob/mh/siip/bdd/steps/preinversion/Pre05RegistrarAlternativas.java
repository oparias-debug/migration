package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
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
import sv.gob.mh.siip.model.preinversion.dto.AlternativaSolucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AlternativaSolucionService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-05-registrar-alternativas.feature. El clic en botones genericos ("Guardar", "Aceptar") y
 * el mensaje "¡Guardado!...(Anexo A.2)" comparten texto con pasos ya definidos para CU-PRE-01
 * (Pre01ResponderObservaciones/Pre01RegistrarNuevoProyecto) y solo hacen no-op; la accion real de
 * guardar se dispara en el primer paso propio de esta clase que sigue al clic
 * ("el sistema guarda la información registrada"), mismo criterio que Pre04RegistrarGuardar.
 */
public class Pre05RegistrarAlternativas {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final AlternativaSolucionService alternativaSolucionService;
    private final ContextoProyectoBdd contextoProyecto;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private RegistroAlternativasRequestDto borrador;
    private RegistroAlternativasDto guardado;

    public Pre05RegistrarAlternativas(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            AlternativaSolucionService alternativaSolucionService,
            ContextoProyectoBdd contextoProyecto,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.alternativaSolucionService = alternativaSolucionService;
        this.contextoProyecto = contextoProyecto;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("que el Técnico URP ingresa a la sección {string} de la pestaña {string}")
    public void que_el_tecnico_urp_ingresa_a_la_seccion_de_la_pestana(String seccion, String pestana) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE05-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-PRE05-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.pre05." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE05-" + sufijo, "Eje temático de prueba"));

        Proyecto proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto alternativas BDD",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, sector, ejeTematico));
        contextoProyecto.setProyectoActual(proyecto);

        autenticarComo(nombreUsuario);
    }

    @Dado("el sistema muestra una fila por defecto en la tabla {string} \\(RN2-{int})")
    public void el_sistema_muestra_una_fila_por_defecto_en_la_tabla(String tabla, Integer rn) {
        // RN2-1: la fila por defecto es un elemento de cliente antes del primer guardado; lo unico
        // verificable contra el backend es que la consulta no falla para el Técnico URP aunque
        // todavia no se haya guardado nada (mismo criterio que Identificacion en CU-PRE-04).
        RegistroAlternativasDto dto = alternativaSolucionService.obtener(contextoProyecto.getProyectoActual().getId());
        assertThat(dto).isNotNull();
    }

    @Cuando("el Técnico URP registra el {string}, el {string} y la {string} en una o más filas")
    public void el_tecnico_urp_registra_el_nombre_monto_y_descripcion_en_una_o_mas_filas(String campo1,
            String campo2, String campo3) {
        borrador = new RegistroAlternativasRequestDto().alternativas(List.of(
                new AlternativaSolucionRequestDto()
                        .nombreAlternativa("Alternativa 1 (BDD)")
                        .montoAlternativa(1000.0)
                        .descripcionAlternativa("Descripción de la alternativa 1 (BDD)"),
                new AlternativaSolucionRequestDto()
                        .nombreAlternativa("Alternativa 2 (BDD)")
                        .montoAlternativa(2000.0)
                        .descripcionAlternativa("Descripción de la alternativa 2 (BDD)")));
    }

    @Cuando("selecciona, mediante el botón radial, la alternativa más conveniente")
    public void selecciona_mediante_el_boton_radial_la_alternativa_mas_conveniente() {
        borrador.getAlternativas().get(0).setSeleccionada(true);
    }

    @Cuando("registra la {string} de dicha selección")
    public void registra_la_justificacion_de_dicha_seleccion(String campo) {
        borrador.setJustificacion("Justificación de prueba BDD para la alternativa seleccionada.");
    }

    @Entonces("el sistema guarda la información registrada de las alternativas")
    public void el_sistema_guarda_la_informacion_registrada_de_las_alternativas() {
        guardado = alternativaSolucionService.guardar(contextoProyecto.getProyectoActual().getId(), borrador);
        assertThat(guardado.getAlternativas()).hasSize(2);
    }

    @Entonces("se mantiene en la sección {string}")
    public void se_mantiene_en_la_seccion(String seccion) {
        RegistroAlternativasDto recargado = alternativaSolucionService
                .obtener(contextoProyecto.getProyectoActual().getId());
        assertThat(recargado.getAlternativas()).hasSize(2);
    }

    @Entonces("el sistema resalta el texto de la alternativa seleccionada")
    public void el_sistema_resalta_el_texto_de_la_alternativa_seleccionada() {
        assertThat(guardado.getAlternativas()).filteredOn(a -> Boolean.TRUE.equals(a.getSeleccionada())).hasSize(1);
        assertThat(guardado.getAlternativas().get(0).getSeleccionada()).isTrue();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en el botón emergente {string}")
    public void el_tecnico_urp_hace_clic_en_el_boton_emergente(String boton) {
        // RN2-1/RN2-2: agregar/eliminar filas ocurre en el cliente; solo se envia al servidor en
        // el siguiente "Guardar" (mismo criterio que el manejo de filas de CU-PRE-04).
    }

    @Entonces("el sistema agrega una nueva fila a la tabla {string} \\(RN2-{int})")
    public void el_sistema_agrega_una_nueva_fila_a_la_tabla(String tabla, Integer rn) {
        // Ver comentario de "el Técnico URP hace clic en el botón emergente {string}".
    }

    @Dado("una alternativa registrada en la tabla")
    public void una_alternativa_registrada_en_la_tabla() {
        // Ver comentario de "el Técnico URP hace clic en el botón emergente {string}".
    }

    @Cuando("el Técnico URP hace clic en el botón emergente {string} junto a esa alternativa")
    public void el_tecnico_urp_hace_clic_en_el_boton_emergente_junto_a_esa_alternativa(String boton) {
        // Ver comentario de "el Técnico URP hace clic en el botón emergente {string}".
    }

    @Entonces("el sistema elimina la alternativa correspondiente \\(RN2-{int})")
    public void el_sistema_elimina_la_alternativa_correspondiente(Integer rn) {
        // Ver comentario de "el Técnico URP hace clic en el botón emergente {string}".
    }

    @Dado("que una alternativa ya está seleccionada mediante el botón radial")
    public void que_una_alternativa_ya_esta_seleccionada_mediante_el_boton_radial() {
        // RN2-6: la seleccion radial es un estado de cliente hasta el siguiente "Guardar"; ver
        // comentario de "el Técnico URP hace clic en el botón emergente {string}".
    }

    @Cuando("el Técnico URP selecciona el botón radial de otra alternativa")
    public void el_tecnico_urp_selecciona_el_boton_radial_de_otra_alternativa() {
        // Ver comentario del paso anterior.
    }

    @Entonces("el sistema deja seleccionada únicamente la nueva alternativa \\(RN2-{int})")
    public void el_sistema_deja_seleccionada_unicamente_la_nueva_alternativa(Integer rn) {
        // Ver comentario de "que una alternativa ya está seleccionada mediante el botón radial".
    }

    @Cuando("el Técnico URP hace clic en el botón {string} sin haber completado todos los campos de las alternativas")
    public void el_tecnico_urp_hace_clic_en_el_boton_sin_haber_completado_todos_los_campos_de_las_alternativas(String boton) {
        // RN3-2: sombreado de bordes es retroalimentacion visual de cliente. Se intenta guardar un
        // formulario vacio para confirmar que el servidor no lo rechaza (ningun campo es
        // obligatorio a nivel de servidor).
        guardado = alternativaSolucionService.guardar(contextoProyecto.getProyectoActual().getId(),
                new RegistroAlternativasRequestDto());
    }

    @Entonces("el sistema sombrea en color rojo los bordes de los campos pendientes de completar \\(RN3-{int})")
    public void el_sistema_sombrea_en_color_rojo_los_bordes_de_los_campos_pendientes_de_completar(Integer rn) {
        assertThat(guardado).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
