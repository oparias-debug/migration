package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.Locale;
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
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-04-registrar-guardar.feature. El clic en botones genericos ("Guardar", "Aceptar",
 * "Regresar") y el mensaje "¡Guardado!...(Anexo A.2)" comparten texto con pasos ya definidos para
 * CU-PRE-01 (Pre01RegistrarNuevoProyecto/Pre01ResponderObservaciones: "el Técnico URP hace clic en
 * el botón {string}", "hace clic en el botón {string}", "el sistema muestra el mensaje {string}
 * \(Anexo A.{double})", "el Técnico URP hace clic en {string}") y solo hacen no-op; la accion real
 * de guardar se dispara en el primer paso propio de esta clase que sigue al clic
 * ("el sistema guarda la información registrada").
 */
public class Pre04RegistrarGuardar {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final IdentificacionService identificacionService;
    private final ContextoProyectoBdd contextoProyecto;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private IdentificacionRequestDto borrador;
    private IdentificacionDto guardado;
    private String campoActual;

    public Pre04RegistrarGuardar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            IdentificacionService identificacionService,
            ContextoProyectoBdd contextoProyecto,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.identificacionService = identificacionService;
        this.contextoProyecto = contextoProyecto;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("que el Técnico URP ingresa a la pestaña {string} \\(Anexo A.{int})")
    public void que_el_tecnico_urp_ingresa_a_la_pestana_anexo_a(String pestana, Integer anexo) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE04-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-PRE04-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.pre04." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        // MacroSector/SectorActividad.codigo son VARCHAR(10): sin margen para prefijo + sufijo de
        // 8 caracteres, solo 1 letra + sufijo (mismo criterio que el resto de steps de preinversion).
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE04-" + sufijo, "Eje temático de prueba"));

        Proyecto proyecto = ProyectoFixtures.nuevoProyecto("Proyecto identificacion BDD",
                EstadoProyecto.CUP_ASIGNADO, unidadEjecutora, institucion, sector, ejeTematico);
        // CU-PRE-04 opera sobre proyectos que ya tienen CUP (M-02, posterior a CU-PRE-01.5); el
        // valor exacto no importa aqui, solo que exista (campo no editable "CUP" de esta pantalla).
        proyecto.setCup(ProyectoFixtures.nuevoCup());
        proyecto = proyectoRepository.save(proyecto);
        contextoProyecto.setProyectoActual(proyecto);

        autenticarComo(nombreUsuario);
    }

    @Dado("el sistema muestra los campos no editables {string}, {string} y {string}")
    public void el_sistema_muestra_los_campos_no_editables(String campo1, String campo2, String campo3) {
        IdentificacionDto dto = identificacionService.obtener(contextoProyecto.getProyectoActual().getId());
        assertThat(dto.getUnidadEjecutora()).isNotNull();
        assertThat(dto.getNombreProyecto()).isNotBlank();
        assertThat(dto.getCup()).isNotBlank();
    }

    @Cuando("el Técnico URP registra información en los campos {string}, {string}, {string} y {string}")
    public void el_tecnico_urp_registra_informacion_en_los_campos(String campo1, String campo2, String campo3,
            String campo4) {
        borrador = new IdentificacionRequestDto();
        for (String campo : List.of(campo1, campo2, campo3, campo4)) {
            establecerCampo(borrador, campo, "Valor de prueba BDD para " + campo);
        }
    }

    @Entonces("el sistema guarda la información registrada")
    public void el_sistema_guarda_la_informacion_registrada() {
        guardado = identificacionService.guardar(contextoProyecto.getProyectoActual().getId(), borrador);
        assertThat(guardado.getAntecedentes()).isNotBlank();
        assertThat(guardado.getFechaUltimoGuardado()).isNotNull();
    }

    @Entonces("permanece en la sección {string}")
    public void permanece_en_la_seccion(String seccion) {
        assertThat(guardado).isNotNull();
        IdentificacionDto recargado = identificacionService.obtener(contextoProyecto.getProyectoActual().getId());
        assertThat(recargado.getAntecedentes()).isEqualTo(guardado.getAntecedentes());
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en el botón {string} sin haber completado todos los campos")
    public void el_tecnico_urp_hace_clic_en_el_boton_sin_haber_completado_todos_los_campos(String boton) {
        // RNC-2: sombreado de bordes es retroalimentacion visual de cliente. Se intenta guardar un
        // formulario vacio para confirmar que el servidor no lo rechaza (ninguno de los 4 campos es
        // obligatorio a nivel de servidor).
        guardado = identificacionService.guardar(contextoProyecto.getProyectoActual().getId(),
                new IdentificacionRequestDto());
    }

    @Entonces("el sistema sombrea en color rojo los bordes de los campos pendientes de completar \\(RNC-{int})")
    public void el_sistema_sombrea_en_color_rojo_los_bordes_de_los_campos_pendientes_de_completar(Integer rnc) {
        // Retroalimentacion visual de cliente (RNC-2): lo unico verificable contra el backend es
        // que el guardado con campos vacios no fue rechazado (ver paso anterior).
        assertThat(guardado).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP registra información en el campo {string}")
    public void el_tecnico_urp_registra_informacion_en_el_campo(String campo) {
        campoActual = campo;
    }

    @Entonces("el sistema permite hasta {string} caracteres para ese campo")
    public void el_sistema_permite_hasta_caracteres_para_ese_campo(String limite) {
        int longitud = Integer.parseInt(limite);
        String valor = "a".repeat(longitud);

        IdentificacionRequestDto request = new IdentificacionRequestDto();
        establecerCampo(request, campoActual, valor);

        IdentificacionDto resultado = identificacionService.guardar(contextoProyecto.getProyectoActual().getId(),
                request);
        assertThat(extraerCampo(resultado, campoActual)).hasSize(longitud);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en el botón para adicionar fila en la sección {string}")
    public void el_tecnico_urp_hace_clic_en_el_boton_para_adicionar_fila(String seccion) {
        // Manipulacion de filas de "Objetivos Específicos" en el cliente: solo se envia al servidor
        // en el siguiente "Guardar" (ver descripcion de objetivosEspecificos en el contrato), no hay
        // llamada propia que preparar aqui.
    }

    @Entonces("el sistema agrega una nueva fila para registrar un objetivo específico adicional")
    public void el_sistema_agrega_una_nueva_fila_para_registrar_un_objetivo_especifico_adicional() {
        // Ver comentario del paso anterior.
    }

    @Dado("una fila registrada en la sección {string}")
    public void una_fila_registrada_en_la_seccion(String seccion) {
        // Ver comentario de "el Técnico URP hace clic en el botón para adicionar fila...".
    }

    @Cuando("el Técnico URP hace clic en el ícono para eliminar esa fila")
    public void el_tecnico_urp_hace_clic_en_el_icono_para_eliminar_esa_fila() {
        // Ver comentario de "el Técnico URP hace clic en el botón para adicionar fila...".
    }

    @Entonces("el sistema elimina la fila correspondiente")
    public void el_sistema_elimina_la_fila_correspondiente() {
        // Ver comentario de "el Técnico URP hace clic en el botón para adicionar fila...".
    }

    @Entonces("el sistema navega a la pantalla {string}")
    public void el_sistema_navega_a_la_pantalla(String pantalla) {
        // Navegacion de UI pura hacia "Ruta de Preinversión", sin efecto propio en el backend.
        RequestContextHolder.resetRequestAttributes();
    }

    private void establecerCampo(IdentificacionRequestDto request, String campoLabel, String valor) {
        switch (campoLabel.toLowerCase(Locale.ROOT)) {
            case "antecedentes" -> request.setAntecedentes(valor);
            case "problema central" -> request.setProblemaCentral(valor);
            case "objetivo general" -> request.setObjetivoGeneral(valor);
            case "objetivos específicos" -> request.setObjetivosEspecificos(List.of(valor));
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campoLabel);
        }
    }

    private String extraerCampo(IdentificacionDto dto, String campoLabel) {
        return switch (campoLabel.toLowerCase(Locale.ROOT)) {
            case "antecedentes" -> dto.getAntecedentes();
            case "problema central" -> dto.getProblemaCentral();
            case "objetivo general" -> dto.getObjetivoGeneral();
            case "objetivos específicos" -> dto.getObjetivosEspecificos().get(0);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campoLabel);
        };
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
