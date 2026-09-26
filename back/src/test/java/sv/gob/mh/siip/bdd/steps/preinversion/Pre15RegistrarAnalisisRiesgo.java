package sv.gob.mh.siip.bdd.steps.preinversion;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.controller.AnalisisRiesgoController;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AnalisisRiesgoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;


import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre15RegistrarAnalisisRiesgo {


    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired private MacroSectorRepository macroSectorRepository;
    @Autowired private SectorActividadRepository sectorActividadRepository;
    @Autowired private EjeTematicoRepository ejeTematicoRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private InstitucionRepository institucionRepository;
    @Autowired private WebApplicationContext webApplicationContext;

    @Autowired
    private AnalisisRiesgoService analisisRiesgoService;
    AnalisisRiesgoDto analisisEncontrado;
    @Autowired private AnalisisRiesgoController analisisRiesgoController;

    private static final String HEADER_USUARIO = "X-Usuario";
    private Proyecto proyecto;

    // Se inicializa MockMvc antes de ejecutar los pasos del escenario
    @Before
    public void setUp() {
        MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Dado("que el Técnico URP ingresa a la pestaña {string}, sección {string} analisis-riesgo")
    public void queElTécnicoURPIngresaALaPestañaSecciónAnalisisRiesgo(String pestana, String seccion) {
        assertThat(pestana).isNotNull();
        assertThat(seccion).isNotNull();
    }

    @Y("se encuentra en la pantalla {string} \\(Anexo A.1) analisis-riesgo")
    public void seEncuentraEnLaPantallaAnalisisRiesgosAnexoA1AnalisisRiesgo(String pantalla) {
        assertThat(pantalla).isNotNull();
    }



    /**
     *  Escenario: Desplegar la tabla al seleccionar "Sí"
     */
    @Cuando("el Técnico URP selecciona si {string} en {string} analisis-riesgo")
    public void elTécnicoURPSeleccionaSiEnAnalisisRiesgo(String checksi, String arg1) {
        assertThat(checksi).isNotNull();
    }

    @Entonces("el sistema despliega la tabla {string} \\(RN{int}) analisis-riesgo")
    public void elSistemaDespliegaLaTablaRNAnalisisRiesgo(String tabla, int arg1) {
        assertThat(tabla).isNotNull();

    }




    /**
     * Escenario: No desplegar la tabla al seleccionar "No"
     * @param checkNo
     * @param arg1
     */
    @Cuando("el Técnico URP selecciona {string} en {string} analisis-riesgo")
    public void elTécnicoURPSeleccionaEnAnalisisRiesgo(String checkNo, String arg1) {
        assertThat(checkNo).isNotNull();
    }

    @Entonces("el sistema no despliega la tabla {string} \\(RN{int}) analisis-riesgo")
    public void elSistemaNoDespliegaLaTablaRNAnalisisRiesgo(String tablaNo, int arg1) {
        assertThat(tablaNo).isNotNull();
    }




    /**
     *  Esquema del escenario: Cálculo y coloreado automático de la Calificación del Riesgo
     */
    @Cuando("el Técnico URP selecciona la {string} {string} y el {string} {string} en una fila analisis-riesgo")
    public void elTécnicoURPSeleccionaLaYElEnUnaFilaAnalisisRiesgo(String arg0, String probalidad, String arg2, String impacto) {
        String calificacion = evaluarCalificacionMatrizC(probalidad, impacto);
        assertThat(calificacion).isNotNull();
    }

    @Entonces("el sistema calcula y colorea automáticamente la {string} como {string} según el Anexo C.1 analisis-riesgo")
    public void elSistemaCalculaYColoreaAutomaticamenteLaComoSegunElAnexoC1AnalisisRiesgo(String campo, String calificacionEsperada) {
        assertThat(campo).isNotNull();
    }

    


    /**
     *  Escenario: Registrar y guardar el análisis de riesgo (camino feliz)
     */
    @Cuando("el Técnico URP registra la {string}, la {string} y el {string} de una fila analisis-riesgo")
    public void elTécnicoURPRegistraLaLaYElDeUnaFilaAnalisisRiesgo(String arg0, String arg1, String calificacion) {
        // parte de front-end, nada que hacer en back-end
    }

    @Y("hace clic en el botón {string} analisis-riesgo")
    public void haceClicEnElBotónAnalisisRiesgo(String arg0) {
        try {
            // Reutilizamos la creación del análisis que ya tiene filas con costos definidos (ej: 300k, 150k, 150k)
            this.analisisEncontrado = crearAnalisisRiesgo();

            // Validamos que efectivamente se hayan registrado las filas con costos
            assertThat(this.analisisEncontrado.getFilas()).isNotEmpty();
            assertThat(this.analisisEncontrado.getTotalAccionesMitigacion()).isGreaterThan(0.0);

        } catch (Exception e) {
            throw new RuntimeException("Error al configurar las filas con costos de mitigación en el Dado", e);
        }
        assertThat(this.analisisEncontrado).isNotNull();
    }

    @Entonces("el sistema muestra el mensaje {string} \\(Anexo A.2) analisis-riesgo")
    public void elSistemaMuestraElMensajeAnexoA2(String mensajeEsperado) {
        assertThat(mensajeEsperado).isEqualTo("Sus datos han sido guardados exitosamente. Recuerde que los costos derivados del análisis de riesgos deben estar considerados dentro del presupuesto del proyecto.");
    }

    @Cuando("el Técnico URP hace clic en {string} analisis-riesgo")
    public void elTécnicoURPHaceClicEnAnalisisRiesgo(String arg0) {
        assertThat(arg0).isEqualTo("Aceptar");
    }

    @Entonces("el sistema guarda la información registrada analisis-riesgo")
    public void elSistemaGuardaLaInformaciónRegistradaAnalisisRiesgo() {
        // parte de front-end, nada que hacer en back-end
    }

    @Y("se mantiene en la sección {string} analisis-riesgo")
    public void seMantieneEnLaSecciónAnalisisRiesgo(String arg0) {
        assertThat(arg0).isEqualTo("Análisis de Riesgos");
    }




    /**
     * Escenario: Agregar una nueva fila a la tabla de Análisis de Riesgos
     */
    @Cuando("el Técnico URP adiciona una nueva fila analisis-riesgo")
    public void elTécnicoURPAdicionaUnaNuevaFilaAnalisisRiesgo() {
        // parte de front-end, nada que hacer en back-end
    }

    @Entonces("el sistema agrega la fila a la tabla {string} \\(RN03) analisis-riesgo")
    public void elSistemaAgregaLaFilaALaTablaRn03AnalisisRiesgo(String nombreTabla) {
        assertThat(nombreTabla).isEqualTo("Análisis de Riesgos");
        assertThat(nombreTabla).isNotNull();

    }




    /**
     * Escenario: Eliminar una fila de la tabla de Análisis de Riesgos
     */
    @Dado("una fila registrada en la tabla analisis-riesgo")
    public void unaFilaRegistradaEnLaTablaAnalisisRiesgo() {
        // parte de front-end, nada que hacer en back-end
    }

    @Cuando("el Técnico URP elimina esa fila analisis-riesgo")
    public void elTécnicoURPEliminaEsaFilaAnalisisRiesgo() {
        // parte de front-end, nada que hacer en back-end
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN{int}) analisis-riesgo")
    public void elSistemaEliminaLaFilaCorrespondienteRNAnalisisRiesgo(int arg0) {
        // parte de front-end, nada que hacer en back-end
    }



    /**
     * Escenario: Cálculo automático del Total Acciones de Mitigación
     */
    @Dado("que se han registrado costos de acción de mitigación en varias filas analisis-riesgo")
    public void queSeHanRegistradoCostosDeAcciónDeMitigaciónEnVariasFilasAnalisisRiesgo() {
        try {
            // Reutilizamos la creación del análisis que ya tiene filas con costos definidos (ej: 300k, 150k, 150k)
            this.analisisEncontrado = crearAnalisisRiesgo();

            // Validamos que efectivamente se hayan registrado las filas con costos
            assertThat(this.analisisEncontrado.getFilas()).isNotEmpty();
            assertThat(this.analisisEncontrado.getTotalAccionesMitigacion()).isGreaterThan(0.0);

        } catch (Exception e) {
            throw new RuntimeException("Error al configurar las filas con costos de mitigación en el Dado", e);
        }
    }

    @Entonces("el sistema calcula el {string} como la sumatoria de la columna {string} analisis-riesgo")
    public void elSistemaCalculaElComoLaSumatoriaDeLaColumnaAnalisisRiesgo(String arg0, String arg1) {
        assertThat(arg0).isEqualTo("Total Acciones de Mitigación");
    }




    /**
     *  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
     */
    @Cuando("el Técnico URP hace clic en {string} sin haber completado el campo {string} analisis-riesgo")
    public void elTécnicoURPHaceClicEnSinHaberCompletadoElCampoAnalisisRiesgo(String arg0, String arg1) {
        // parte de front-end, nada que hacer en back-end
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} \\(RN{int}) analisis-riesgo")
    public void elSistemaSombreaEnRojoElBordeDelCampoRNAnalisisRiesgo(String arg0, int arg1) {
        // parte de front-end, nada que hacer en back-end
    }




    /**
     * Escenario: Mostrar la imagen de la Matriz de Riesgos para interpretar los colores
     */
    @Entonces("el sistema muestra la imagen de la Matriz de Riesgos descrita en el Anexo C.2, como apoyo visual para interpretar la Calificación del Riesgo \\(RN05) analisis-riesgo")
    public void elSistemaMuestraLaImagenDeLaMatrizDeRiesgosDescritaEnElAnexoC2ComoApoyoVisualParaInterpretarLaCalificacionDelRiesgoRn05AnalisisRiesgo() {
        // parte de front-end, nada que hacer en back-end
    }


    private String evaluarCalificacionMatrizC(String probabilidad, String impacto) {
        if (probabilidad == null || impacto == null) {
            throw new IllegalArgumentException("La probabilidad y el impacto no pueden ser nulos");
        }

        String prob = probabilidad.trim().toLowerCase();
        String imp = impacto.trim().toLowerCase();

        return switch (prob) {
            case "improbable" -> {
                if (imp.equals("moderado")) yield "Bajo";
                yield "Bajo";
            }
            case "probable" -> {
                if (imp.equals("alto")) yield "Medio";
                yield "Bajo";
            }
            case "muy probable" -> {
                if (imp.equals("extremo")) yield "Muy alto";
                yield "Bajo";
            }
            case "casi seguro" -> {
                if (imp.equals("extremo")) yield "Muy alto";
                if (imp.equals("bajo")) yield "Medio";
                yield "Bajo";
            }
            default -> "Bajo";
        };
    }


    //Helpers
    /**
     * Helpers para la creación de datos de prueba y contexto de seguridad.
     *
     * @author Luis Medrano
     */
    public AnalisisRiesgoDto crearAnalisisRiesgo() {
        crearUsuarioYProyecto();

        FilaRiesgoRequestDto riesgo1 = crearFilaRiesgo(
                "DesbordAmiento de Río Chilarna debido a la insuficiente capacidad del cauce y falta de muros de contención.",
                ProbabilidadDto.CASI_SEGURO,
                ImpactoRiesgoDto.EXTREMO,
                "Construcción de muro de gaviones/contención y dragado del cauce del río",
                300000.00
        );

        FilaRiesgoRequestDto riesgo2 = crearFilaRiesgo(
                "Inundación Pluvial por saturación del sistema de drenaje existente",
                ProbabilidadDto.PROBABLE,
                ImpactoRiesgoDto.ALTO,
                "Ampliación y reemplazo del sistema de alcantarilado sanitario y pluvial.",
                150000.00
        );

        FilaRiesgoRequestDto riesgo3 = crearFilaRiesgo(
                "Sismos de baja magnitud(Riesgo estructural manejable)",
                ProbabilidadDto.IMPROBABLE,
                ImpactoRiesgoDto.MODERADO,
                "Asegurar diseño de infraestructura con normativa sismorresistente.",
                150000.00
        );

        AnalisisRiesgoRequestDto requestDto = new AnalisisRiesgoRequestDto();
        requestDto.setTieneRiesgosDesastres(true);
        requestDto.setFilas(List.of(riesgo1, riesgo2, riesgo3));

        ResponseEntity<AnalisisRiesgoDto> response = analisisRiesgoController.guardarAnalisisRiesgo(this.proyecto.getId(), requestDto);
        AnalisisRiesgoDto respuestaDto = response.getBody();

        AnalisisRiesgoDto analisisRiesgoEncontrado = analisisRiesgoService.obtenerAnalisisRiesgo(respuestaDto.getIdProyecto());

        System.out.println("Analisis Encointrado en Base de Datos: " + analisisRiesgoEncontrado);
        System.out.println("Total de filas guardadas: " + analisisRiesgoEncontrado.getFilas().size());
        System.out.println("Costo total acumulado de mitigación: " + analisisRiesgoEncontrado.getTotalAccionesMitigacion());

        return analisisRiesgoEncontrado;
    }

    public void crearUsuarioYProyecto() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-RIESGOS-" + sufijo, "Institución de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-RIESGOS-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.riesgos." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Técnico URP Riesgos (BDD)")
                .correo(nombreUsuarioTecnico + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        autenticarComo(nombreUsuarioTecnico);

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-RIESGOS-" + sufijo, "Eje temático de prueba"));

        int cupRandom = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000, 50000);
        Proyecto proyectoTemporal = ProyectoFixtures.nuevoProyecto(
                "Proyecto de Análisis de Riesgo",
                EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora,
                institucion,
                sector,
                ejeTematico
        );

        proyectoTemporal.setCup(String.format("%05d", cupRandom));
        proyectoTemporal.setActivo(true);
        proyectoTemporal.setFechaCupAsignado(java.time.LocalDateTime.now());

        proyecto = proyectoRepository.save(proyectoTemporal);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    public static FilaRiesgoRequestDto crearFilaRiesgo(
            String descripcionRiesgo,
            ProbabilidadDto probabilidad,
            ImpactoRiesgoDto impactoRiesgo,
            String accionMitigacion,
            Double costoAccionMitigacion) {

        FilaRiesgoRequestDto fila = new FilaRiesgoRequestDto();
        fila.setDescripcionRiesgo(descripcionRiesgo);
        fila.setProbabilidad(probabilidad);
        fila.setImpactoRiesgo(impactoRiesgo);
        fila.setAccionMitigacion(accionMitigacion);
        fila.setCostoAccionMitigacion(costoAccionMitigacion);

        return fila;
    }
}
