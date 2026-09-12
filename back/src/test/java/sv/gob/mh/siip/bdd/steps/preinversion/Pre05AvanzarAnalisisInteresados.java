package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
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
 * CU-PRE-05-avanzar-analisis-interesados.feature. El clic en "Siguiente" reutiliza el paso
 * genérico "el Técnico URP hace clic en el botón {string}" (no-op) ya definido en
 * Pre01RegistrarNuevoProyecto; la accion real (RN2-4/RN2-3) se dispara en el primer paso propio
 * que sigue a ese clic — "el sistema avanza a la sección..." en el camino feliz, "el sistema
 * muestra la alerta..." en los dos casos de error (mismo texto en ambos escenarios negativos,
 * definido una unica vez aqui). "el sistema avanza a la sección {string} \(CU-PRE-{word})" es
 * texto identico al de CU-PRE-06-avanzar-poblacion-objetivo.feature (Cucumber exige una unica
 * definicion por texto, mismo criterio que Pre02Bandeja/Pre01ResponderObservaciones): se delega en
 * {@link Pre06AvanzarPoblacionObjetivo} o {@link Pre07AvanzarAreaInfluencia} cuando su respectivo
 * escenario esta activo.
 */
public class Pre05AvanzarAnalisisInteresados {

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

    // El paso "el sistema avanza a la sección {string} \(CU-PRE-{word})" es texto identico al de
    // CU-PRE-06-avanzar-poblacion-objetivo.feature; Cucumber no admite duplicarlo (ver javadoc de
    // la clase).
    @Autowired
    private Pre06AvanzarPoblacionObjetivo avanzarPoblacionObjetivo;
    @Autowired
    private Pre07AvanzarAreaInfluencia avanzarAreaInfluencia;

    public Pre05AvanzarAnalisisInteresados(InstitucionRepository institucionRepository,
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

    @Dado("que el Técnico URP se encuentra en la sección {string} de la pestaña {string}")
    public void que_el_tecnico_urp_se_encuentra_en_la_seccion_de_la_pestana(String seccion, String pestana) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE05B-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-PRE05B-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.pre05b." + sufijo;
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE05B-" + sufijo, "Eje temático de prueba"));

        Proyecto proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto avance BDD",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, sector, ejeTematico));
        contextoProyecto.setProyectoActual(proyecto);

        autenticarComo(nombreUsuario);
    }

    @Dado("que se ha registrado al menos una alternativa")
    public void que_se_ha_registrado_al_menos_una_alternativa() {
        alternativaSolucionService.guardar(contextoProyecto.getProyectoActual().getId(),
                new RegistroAlternativasRequestDto().alternativas(List.of(
                        new AlternativaSolucionRequestDto().nombreAlternativa("Alternativa 1 (BDD)")
                                .seleccionada(true),
                        new AlternativaSolucionRequestDto().nombreAlternativa("Alternativa 2 (BDD)"))));
    }

    @Dado("la Justificación está completa según corresponda")
    public void la_justificacion_esta_completa_segun_corresponda() {
        // RN2-3 solo exige Justificación cuando hay una unica alternativa; con dos registradas
        // (paso anterior) la condicion "segun corresponda" ya esta satisfecha sin necesidad de
        // completarla. Se verifica en el paso final del camino feliz.
    }

    @Dado("que no se ha registrado ninguna alternativa")
    public void que_no_se_ha_registrado_ninguna_alternativa() {
        // El proyecto recien creado en las Antecedentes no tiene alternativas guardadas todavia.
    }

    @Dado("que se ha registrado únicamente una alternativa")
    public void que_se_ha_registrado_unicamente_una_alternativa() {
        alternativaSolucionService.guardar(contextoProyecto.getProyectoActual().getId(),
                new RegistroAlternativasRequestDto().alternativas(
                        List.of(new AlternativaSolucionRequestDto().nombreAlternativa("Unica alternativa (BDD)")
                                .seleccionada(true))));
    }

    @Dado("el campo {string} no ha sido completado")
    public void el_campo_no_ha_sido_completado(String campo) {
        // La justificacion quedo vacia (null) en el guardado del paso anterior: nada que hacer.
    }

    // {word} en vez de {int} para el numero de CU: el tipo integrado {int} de Cucumber usa
    // Integer.decode (octal con cero inicial) y falla con valores como "08" (CU-PRE-08); el valor
    // no se usa en el cuerpo del paso, solo hace falta que capture sin error.
    @Entonces("el sistema avanza a la sección {string} \\(CU-PRE-{word})")
    public void el_sistema_avanza_a_la_seccion(String seccion, String numeroCu) {
        if (avanzarPoblacionObjetivo.esEscenarioAvanzarPoblacionObjetivo()) {
            avanzarPoblacionObjetivo.confirmarAvance();
            return;
        }
        if (avanzarAreaInfluencia.esEscenarioAvanzarAreaInfluencia()) {
            avanzarAreaInfluencia.confirmarAvance();
            return;
        }
        RegistroAlternativasDto resultado = alternativaSolucionService
                .avanzarAAnalisisInteresados(contextoProyecto.getProyectoActual().getId());
        assertThat(resultado).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema muestra la alerta {string} \\(RN2-{int})")
    public void el_sistema_muestra_la_alerta(String mensaje, Integer rn) {
        Long idProyecto = contextoProyecto.getProyectoActual().getId();
        assertThatThrownBy(() -> alternativaSolucionService.avanzarAAnalisisInteresados(idProyecto))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessage(mensaje);
    }

    @Entonces("no avanza a {string}")
    public void no_avanza_a(String seccion) {
        // Verificado en el paso anterior (la llamada lanzo la excepcion de validacion en vez de
        // devolver el registro actualizado).
        RequestContextHolder.resetRequestAttributes();
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
