package sv.gob.mh.siip.controller;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaRiesgoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ImpactoRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProbabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AnalisisRiesgoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Pruebas de integración del controlador para el módulo de Análisis de Riesgo (CU-PRE-15).
 * Simula peticiones HTTP reales sobre el contexto completo de Spring Boot.
 *
 * @author Luis Medrano
 * @version 1.0
 * @since 2026-09-20
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnalisisRiesgoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired private MacroSectorRepository macroSectorRepository;
    @Autowired private SectorActividadRepository sectorActividadRepository;
    @Autowired private EjeTematicoRepository ejeTematicoRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private InstitucionRepository institucionRepository;

    @Autowired
    private AnalisisRiesgoService analisisRiesgoService;

    private static final String HEADER_USUARIO = "X-Usuario";
    private Proyecto proyecto;
    private String nombreUsuarioTecnico;

    /**
     * Valida la consulta exitosa del análisis de riesgo y el costo total calculado por ID de proyecto.
     *
     * @throws Exception en caso de error en la ejecución del MockMvc.
     * @author Luis Medrano
     */
    @Test
    @DisplayName("Debe retornar el análisis de riesgo y el costo total de mitigación al consultar por ID de proyecto")
    void deberiaObtenerAnalisisRiesgo() throws Exception {
        crearUsuarioYProyecto();

        mockMvc.perform(get("/proyectos/{idProyecto}/analisis-riesgo", this.proyecto.getId())
                        .header(HEADER_USUARIO, this.nombreUsuarioTecnico)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProyecto").value(this.proyecto.getId()))
                .andExpect(jsonPath("$.tieneRiesgosDesastres").value(false))
                .andExpect(jsonPath("$.filas").isEmpty());
    }

    /**
     * RN01/RN02: un proyecto inexistente debe responder 404, no 200 ni 500
     * (regresión: antes se usaba getReferenceById, que nunca valida existencia).
     */
    @Test
    @DisplayName("Debe retornar 404 al consultar el análisis de riesgo de un proyecto inexistente")
    void deberiaRetornar404SiElProyectoNoExiste() throws Exception {
        crearUsuarioYProyecto();
        Long idProyectoInexistente = this.proyecto.getId() + 1_000_000L;

        mockMvc.perform(get("/proyectos/{idProyecto}/analisis-riesgo", idProyectoInexistente)
                        .header(HEADER_USUARIO, this.nombreUsuarioTecnico)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * RN01: sin actor autenticado (sin header X-Usuario) el sistema debe rechazar con 401,
     * no procesar la petición como si fuera anónima válida.
     */
    @Test
    @DisplayName("Debe retornar 401 al consultar el análisis de riesgo sin autenticación")
    void deberiaRetornar401SinAutenticacion() throws Exception {
        crearUsuarioYProyecto();

        mockMvc.perform(get("/proyectos/{idProyecto}/analisis-riesgo", this.proyecto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * RN01/RN02: un actor de una Unidad Ejecutora distinta a la del proyecto no debe poder
     * consultarlo, aunque tenga el rol correcto.
     */
    @Test
    @DisplayName("Debe retornar 403 si el actor no pertenece a la Unidad Ejecutora del proyecto")
    void deberiaRetornar403SiElActorNoPerteneceALaUnidadEjecutoraDelProyecto() throws Exception {
        crearUsuarioYProyecto();

        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion otraInstitucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-OTRA-" + sufijo, "Otra institución"));
        UnidadEjecutora otraUnidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-OTRA-" + sufijo, "Otra Unidad Ejecutora", otraInstitucion));
        String nombreUsuarioAjeno = "tecnico.urp.ajeno." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioAjeno)
                .nombreCompleto("Técnico URP de otra UE (BDD)")
                .correo(nombreUsuarioAjeno + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(otraUnidadEjecutora)
                .institucion(otraInstitucion)
                .activo(true)
                .build());

        mockMvc.perform(get("/proyectos/{idProyecto}/analisis-riesgo", this.proyecto.getId())
                        .header(HEADER_USUARIO, nombreUsuarioAjeno)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Valida el registro, guardado masivo y cálculo automático de matriz y costos mediante PUT (CU-PRE-15, FA-01).
     *
     * @throws Exception en caso de error en la ejecución del MockMvc.
     * @author Luis Medrano
     */
    @Test
    @DisplayName("Debe guardar o actualizar el análisis de riesgo correctamente mediante PUT")
    void deberiaGuardarOActualizarAnalisisRiesgo() throws Exception {
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

        mockMvc.perform(put("/proyectos/{idProyecto}/analisis-riesgo", this.proyecto.getId())
                        .header(HEADER_USUARIO, this.nombreUsuarioTecnico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filas", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$.totalAccionesMitigacion").value(600000.00))
                .andExpect(jsonPath("$.filas[0].calificacionRiesgo").value("MUY_ALTO"))
                .andExpect(jsonPath("$.filas[1].calificacionRiesgo").value("MEDIO"))
                .andExpect(jsonPath("$.filas[2].calificacionRiesgo").value("BAJO"));

        AnalisisRiesgoDto analisisRiesgoEncontrado = analisisRiesgoService.obtenerAnalisisRiesgo(this.proyecto.getId());

        org.assertj.core.api.Assertions.assertThat(analisisRiesgoEncontrado.getFilas()).hasSize(3);
        org.assertj.core.api.Assertions.assertThat(analisisRiesgoEncontrado.getTotalAccionesMitigacion()).isEqualTo(600000.00);
    }

    /**
     * Valida que el endpoint de avance (POST) rechace con HTTP 400 y el mensaje exacto de la RN06
     * si existen riesgos altos/muy altos sin acción o costo de mitigación completo.
     *
     * @throws Exception en caso de error en la ejecución del MockMvc.
     * @author Luis Medrano
     */
    @Test
    @DisplayName("Debe fallar con HTTP 400 y mensaje RN06 al intentar avanzar sin completar mitigaciones obligatorias")
    void deberiaFallarRN06AlAvanzarAnalisisLegal() throws Exception {
        crearUsuarioYProyecto();

        // Usamos la estructura de datos real del negocio, pero con acción y costo en null para detonar la RN06
        FilaRiesgoRequestDto riesgoIncompleto = crearFilaRiesgo(
                "Desbordamiento de Río Chilarna debido a la insuficiente capacidad del cauce y falta de dragado",
                ProbabilidadDto.CASI_SEGURO,
                ImpactoRiesgoDto.EXTREMO,
                null, // Acción de mitigación incompleta (obligatoria para Muy Alto)
                null  // Costo de mitigación incompleto (obligatorio para Muy Alto)
        );

        AnalisisRiesgoRequestDto requestDto = new AnalisisRiesgoRequestDto();
        requestDto.setTieneRiesgosDesastres(true);
        requestDto.setFilas(List.of(riesgoIncompleto));

        // 1. Guardamos el estado con el riesgo crítico incompleto
        mockMvc.perform(put("/proyectos/{idProyecto}/analisis-riesgo", this.proyecto.getId())
                        .header(HEADER_USUARIO, this.nombreUsuarioTecnico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        // 2. Intentamos avanzar a Análisis Legal, esperamos 400 con el mensaje literal de la RN06
        mockMvc.perform(post("/proyectos/{idProyecto}/analisis-riesgo/avance", this.proyecto.getId())
                        .header(HEADER_USUARIO, this.nombreUsuarioTecnico)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Se requiere completar los campos obligatorios"));
    }

    /**
     * Helpers para la creación de datos de prueba y contexto de seguridad.
     *
     * @author Luis Medrano
     */
    public void crearUsuarioYProyecto() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-RIESGOS-" + sufijo, "Institución de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-RIESGOS-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        this.nombreUsuarioTecnico = "tecnico.urp.riesgos." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Técnico URP Riesgos (BDD)")
                .correo(this.nombreUsuarioTecnico + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        autenticarComo(this.nombreUsuarioTecnico);

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-RIESGOS-" + sufijo, "Eje temático de prueba"));

        int cupRandom = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000, 100000);
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