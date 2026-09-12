package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
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
import sv.gob.mh.siip.model.preinversion.dto.InteresadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NivelInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.NivelInteresDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoInteresadoDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.MatrizInteresadosService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-06-registrar-matriz-interesados.feature. El clic en botones genericos ("Guardar",
 * "Aceptar", "+") y el mensaje "¡Guardado!...(Anexo A.2)" comparten texto con pasos ya definidos
 * para CU-PRE-01/04 (Pre01RegistrarNuevoProyecto/Pre01ResponderObservaciones) y solo hacen no-op.
 * Dos pasos de esta historia comparten texto <b>identico</b> con pasos ya definidos en otras
 * clases de otros CU (Cucumber exige una unica definicion por texto, mismo criterio que
 * Pre02Bandeja/Pre01ResponderObservaciones): "el sistema guarda la información registrada"
 * (definido en {@link Pre04RegistrarGuardar}) y "el Técnico URP hace clic en {string} sin haber
 * completado el campo {string}" (definido en {@link Pre35RegistrarFichaEmergencia}). Ambas clases
 * delegan aqui cuando {@link #esEscenarioMatrizInteresados()} es verdadero (el Antecedentes de
 * esta historia establece {@code proyecto}, que solo esta poblado en escenarios de esta clase).
 * <p>
 * "se encuentra en la tabla {string} (Anexo A.{int})" es ademas texto identico al de
 * CU-PRE-07-registrar-analisis-poblacion.feature: cuando la tabla es "Análisis de la Población" se
 * delega en {@link Pre07RegistrarAnalisisPoblacion#activarEscenario}.
 */
public class Pre06RegistrarMatrizInteresados {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MatrizInteresadosService matrizInteresadosService;
    private final ContextoProyectoBdd contextoProyecto;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyecto;
    private InteresadoRequestDto filaActual;
    private MatrizInteresadosDto guardado;

    /**
     * Distinto de {@code proyecto != null}: el paso de entrada ("que el Técnico URP ingresa a la
     * pestaña...") fija {@code proyecto} para toda historia que reutilice ese texto compartido
     * (incluida CU-PRE-07), asi que no sirve por si solo para distinguir el escenario. Solo se
     * marca aqui cuando la tabla del escenario es realmente "Matriz de gestión de interesados".
     */
    private boolean escenarioMatrizInteresados;

    // El paso "se encuentra en la tabla {string} \(Anexo A.{int})" es texto identico al de
    // CU-PRE-07-registrar-analisis-poblacion.feature; Cucumber no admite duplicarlo (ver javadoc
    // de la clase).
    @Autowired
    private Pre07RegistrarAnalisisPoblacion analisisPoblacion;
    @Autowired
    private Pre08RegistrarAreaInfluencia areaInfluencia;

    public Pre06RegistrarMatrizInteresados(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            MatrizInteresadosService matrizInteresadosService,
            ContextoProyectoBdd contextoProyecto,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.matrizInteresadosService = matrizInteresadosService;
        this.contextoProyecto = contextoProyecto;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("que el Técnico URP ingresa a la pestaña {string}, sección {string}")
    public void que_el_tecnico_urp_ingresa_a_la_pestana_seccion(String pestana, String seccion) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE06-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-PRE06-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.pre06." + sufijo;
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE06-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto matriz interesados BDD",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, sector, ejeTematico));
        contextoProyecto.setProyectoActual(proyecto);

        filaActual = new InteresadoRequestDto();
        autenticarComo(nombreUsuario);
    }

    @Dado("se encuentra en la tabla {string} \\(Anexo A.{int})")
    public void se_encuentra_en_la_tabla_anexo_a(String tabla, Integer anexo) {
        if ("Análisis de la Población".equals(tabla)) {
            analisisPoblacion.activarEscenario(proyecto);
            return;
        }
        escenarioMatrizInteresados = true;
        MatrizInteresadosDto dto = matrizInteresadosService.obtener(proyecto.getId());
        assertThat(dto.getInteresados()).isEmpty();
    }

    @Cuando("el Técnico URP registra el {string}")
    public void el_tecnico_urp_registra_el(String campo) {
        if (!"Nombre del interesado".equals(campo)) {
            throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
        filaActual.setNombreInteresado("Interesado de prueba BDD");
    }

    @Cuando("selecciona el {string} \\(Cooperante, Oponente, Beneficiario o Perjudicado\\)")
    public void selecciona_el_tipo(String campo) {
        filaActual.setTipo(TipoInteresadoDto.COOPERANTE);
    }

    @Cuando("selecciona el {string} \\(Alto o Bajo\\)")
    public void selecciona_nivel(String campo) {
        switch (campo) {
            case "Nivel de influencia" -> filaActual.setNivelInfluencia(NivelInfluenciaDto.ALTO);
            case "Nivel de interés" -> filaActual.setNivelInteres(NivelInteresDto.ALTO);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
    }

    @Cuando("registra la {string}")
    public void registra_la(String campo) {
        if (!"Estrategia de gestión".equals(campo)) {
            throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
        filaActual.setEstrategiaGestion("Estrategia de gestión de prueba BDD");
    }

    @Entonces("se mantiene en la pantalla {string}")
    public void se_mantiene_en_la_pantalla(String pantalla) {
        if (areaInfluencia.esEscenarioAreaInfluencia()) {
            areaInfluencia.verificarGuardado(pantalla);
            return;
        }
        MatrizInteresadosDto recargado = matrizInteresadosService.obtener(proyecto.getId());
        assertThat(recargado.getInteresados()).hasSize(1);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema agrega una nueva fila a la tabla {string} \\(RN03\\)")
    public void el_sistema_agrega_una_nueva_fila_a_la_tabla_rn03(String tabla) {
        // RN03: agregar una fila ocurre en el cliente; solo se envia al servidor en el siguiente
        // "Guardar" (mismo criterio que el manejo de filas de CU-PRE-04/05).
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un interesado registrado en la tabla")
    public void un_interesado_registrado_en_la_tabla() {
        filaActual.setNombreInteresado("Interesado a eliminar (BDD)");
        guardado = matrizInteresadosService.guardar(proyecto.getId(),
                new MatrizInteresadosRequestDto().interesados(List.of(filaActual)));
        assertThat(guardado.getInteresados()).hasSize(1);
    }

    @Cuando("el Técnico URP hace clic en el botón {string} junto a ese interesado")
    public void el_tecnico_urp_hace_clic_en_el_boton_junto_a_ese_interesado(String boton) {
        // RN04: eliminar una fila ocurre en el cliente; se envia como lista vacia en el siguiente
        // "Guardar" (ver paso siguiente).
    }

    @Entonces("el sistema elimina la fila del interesado correspondiente \\(RN04\\)")
    public void el_sistema_elimina_la_fila_del_interesado_correspondiente_rn04() {
        MatrizInteresadosDto actualizado = matrizInteresadosService.guardar(proyecto.getId(),
                new MatrizInteresadosRequestDto().interesados(List.of()));
        assertThat(actualizado.getInteresados()).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un interesado ya registrado con un {string}, {string}, {string} y {string} específicos")
    public void un_interesado_ya_registrado_con_columnas_especificas(String tipo, String nivelInfluencia,
            String nivelInteres, String estrategia) {
        filaActual.setNombreInteresado("Interesado repetido (BDD)");
        filaActual.setTipo(TipoInteresadoDto.COOPERANTE);
        filaActual.setNivelInfluencia(NivelInfluenciaDto.ALTO);
        filaActual.setNivelInteres(NivelInteresDto.ALTO);
        filaActual.setEstrategiaGestion("Estrategia original (BDD)");
        guardado = matrizInteresadosService.guardar(proyecto.getId(),
                new MatrizInteresadosRequestDto().interesados(List.of(filaActual)));
    }

    @Cuando("el Técnico URP agrega una nueva fila con el mismo {string} pero un valor distinto en al menos una de las demás columnas")
    public void agrega_fila_con_mismo_nombre_pero_columna_distinta(String campo) {
        InteresadoRequestDto segundaFila = new InteresadoRequestDto()
                .nombreInteresado(filaActual.getNombreInteresado())
                .tipo(TipoInteresadoDto.OPONENTE)
                .nivelInfluencia(filaActual.getNivelInfluencia())
                .nivelInteres(filaActual.getNivelInteres())
                .estrategiaGestion(filaActual.getEstrategiaGestion());
        guardado = matrizInteresadosService.guardar(proyecto.getId(),
                new MatrizInteresadosRequestDto().interesados(List.of(filaActual, segundaFila)));
    }

    @Entonces("el sistema permite el nuevo registro \\(RN05\\)")
    public void el_sistema_permite_el_nuevo_registro_rn05() {
        assertThat(guardado.getInteresados()).hasSize(2);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} \\(RN06\\)")
    public void el_sistema_sombrea_en_rojo_el_borde_del_campo_rn06(String campo) {
        assertThat(guardado).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    // -----------------------------------------------------------------------------------------
    // Puntos de extension usados por otras clases de steps para los pasos de texto compartido
    // (ver javadoc de la clase).

    public boolean esEscenarioMatrizInteresados() {
        return escenarioMatrizInteresados;
    }

    /** Invocado por {@link Pre04RegistrarGuardar} para "el sistema guarda la información registrada". */
    public void guardarInformacionRegistrada() {
        guardado = matrizInteresadosService.guardar(proyecto.getId(),
                new MatrizInteresadosRequestDto().interesados(List.of(filaActual)));
        assertThat(guardado.getInteresados()).hasSize(1);
    }

    /**
     * Invocado por {@link Pre35RegistrarFichaEmergencia} para "el Técnico URP hace clic en
     * {string} sin haber completado el campo {string}" (RN06): ningun campo es obligatorio a
     * nivel de servidor, asi que se guarda una fila completa salvo el campo indicado, para
     * confirmar que el servidor no la rechaza.
     */
    public void guardarSinCompletarCampo(String campo) {
        InteresadoRequestDto fila = new InteresadoRequestDto();
        if (!"Nombre del interesado".equals(campo)) {
            fila.setNombreInteresado("Interesado de prueba BDD");
        }
        if (!"Tipo".equals(campo)) {
            fila.setTipo(TipoInteresadoDto.COOPERANTE);
        }
        if (!"Nivel de influencia".equals(campo)) {
            fila.setNivelInfluencia(NivelInfluenciaDto.ALTO);
        }
        if (!"Nivel de interés".equals(campo)) {
            fila.setNivelInteres(NivelInteresDto.ALTO);
        }
        if (!"Estrategia de gestión".equals(campo)) {
            fila.setEstrategiaGestion("Estrategia de gestión de prueba BDD");
        }
        guardado = matrizInteresadosService.guardar(proyecto.getId(),
                new MatrizInteresadosRequestDto().interesados(List.of(fila)));
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
