package sv.gob.mh.siip.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
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
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AnalisisAmbientalService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnalisisAmbientalControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired private MacroSectorRepository macroSectorRepository;
    @Autowired private SectorActividadRepository sectorActividadRepository;
    @Autowired private EjeTematicoRepository ejeTematicoRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private InstitucionRepository institucionRepository;
    @Autowired private AnalisisAmbientalController analisisAmbientalController;

    @MockitoBean
    private AnalisisAmbientalService analisisAmbientalService;

    private static final String HEADER_USUARIO = "X-Usuario";
    private Proyecto proyecto;
    Usuario usuarioAuthenticado;




    @Test
    @DisplayName("Debe retornar el análisis ambiental y el costo total calculado al consultar por ID de proyecto")
    void deberiaObtenerAnalisisAmbiental() throws Exception {
        Long idProyecto = 1L;
        AnalisisAmbientalDto mockDto = new AnalisisAmbientalDto();
        mockDto.setIdProyecto(idProyecto);
        mockDto.setTieneImpactosAmbientales(false);
        mockDto.setFilas(Collections.emptyList());
        mockDto.setTotalCostoMedidasGestion(0.0);

        given(analisisAmbientalService.obtenerAnalisisAmbiental(idProyecto)).willReturn(mockDto);

        mockMvc.perform(get("/proyectos/{idProyecto}/analisis-ambiental", idProyecto) // Ajusta tu ruta base real
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProyecto").value(idProyecto))
                .andExpect(jsonPath("$.tieneImpactosAmbientales").value(false))
                .andExpect(jsonPath("$.totalCostoMedidasGestion").value(0.0));
    }

    @Test
    @DisplayName("Debe guardar o actualizar el análisis ambiental correctamente mediante PUT")
    void deberiaGuardarOActualizarAnalisisAmbiental() {

        crearUsuarioYProyecto();

        // 1. Creamos las filas usando el método que pide todos los parámetros
        FilaImpactoAmbientalRequestDto impacto1 = crearFilaImpactoAmbiental(
                MedioDto.FISICO_AGUA,
                "Erosión y Sedimentación(por movimiento de tierras)",
                TipoImpactoDto.NEGATIVO,
                MagnitudDto.MODERADO,
                DuracionDto.CORTO_PLAZO,
                ReversibilidadDto.REVERSIBLE,
                "Control de escorrentías y regeneración de taludes",
                3200.00
        );

        FilaImpactoAmbientalRequestDto impacto2 = crearFilaImpactoAmbiental(
                MedioDto.FISICO_TIERRA,
                "Generacion de Residuos Sólidos",
                TipoImpactoDto.NEGATIVO,
                MagnitudDto.LEVE,
                DuracionDto.LARGO_PLAZO,
                ReversibilidadDto.IRREVERSIBLE,
                "Plan de Manejo de Residuos y disposición final autorizada",
                1800.50
        );

        FilaImpactoAmbientalRequestDto impacto3 = crearFilaImpactoAmbiental(
                MedioDto.BIOLOGICO_FLORA,
                "Pérdida de Vegetación(en la franja del río)",
                TipoImpactoDto.NEGATIVO,
                MagnitudDto.LEVE,
                DuracionDto.LARGO_PLAZO,
                ReversibilidadDto.IRREVERSIBLE,
                "Compensación con reforestación en zonas aledañas(especies nativas)",
                1800.50
        );


        AnalisisAmbientalRequestDto analisisAmbientalRequestDto = new AnalisisAmbientalRequestDto();
        analisisAmbientalRequestDto.setTieneImpactosAmbientales(true);
        analisisAmbientalRequestDto.setFilas(List.of(impacto1, impacto2, impacto3));

        ResponseEntity<AnalisisAmbientalDto> response = analisisAmbientalController.guardarAnalisisAmbiental(this.proyecto.getId(), analisisAmbientalRequestDto);

        assertThat(response).isNotNull();

    }

    /**
     * Helpers para datos
     */
    public void crearUsuarioYProyecto(){
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-CUP-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-CUP-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.bdd.cup." + sufijo;
        this.usuarioAuthenticado = usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioTecnico + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        autenticarComo(nombreUsuarioTecnico);

        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-CUP-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto registrado", EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, ejeTematico));



    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    public static AnalisisAmbientalRequestDto construirAnalisisAmbientalCompleto() {

        // 1. Llenamos la primera fila de impacto ambiental al 100%
        FilaImpactoAmbientalRequestDto impacto1 = new FilaImpactoAmbientalRequestDto();
        impacto1.setMedio(MedioDto.FISICO_AGUA); // Ajusta según los valores de tu enum MedioDto
        impacto1.setImpacto("Erosión y Sedimentación(por movimiento de tierras)");
        impacto1.setTipoImpacto(TipoImpactoDto.NEGATIVO);
        impacto1.setMagnitud(MagnitudDto.MODERADO);
        impacto1.setDuracion(DuracionDto.CORTO_PLAZO);
        impacto1.setReversibilidad(ReversibilidadDto.REVERSIBLE);
        impacto1.setMedidaGestion("Control de escorrentías y regeneración de taludes)");
        impacto1.setCostoMedidaGestion(3200.00);

        // 2. Llenamos una segunda fila para robustecer el DTO
        FilaImpactoAmbientalRequestDto impacto2 = new FilaImpactoAmbientalRequestDto();
        impacto2.setMedio(MedioDto.FISICO_TIERRA);
        impacto2.setImpacto("Generacion de Residuos Sólidos");
        impacto2.setTipoImpacto(TipoImpactoDto.NEGATIVO);
        impacto2.setMagnitud(MagnitudDto.LEVE);
        impacto2.setDuracion(DuracionDto.LARGO_PLAZO);
        impacto2.setReversibilidad(ReversibilidadDto.IRREVERSIBLE);
        impacto2.setMedidaGestion("Plan de Mamejo de Residuos y disposición final autorizada)");
        impacto2.setCostoMedidaGestion(1800.50);

        // 2. Llenamos una segunda fila para robustecer el DTO
        FilaImpactoAmbientalRequestDto impacto3 = new FilaImpactoAmbientalRequestDto();
        impacto3.setMedio(MedioDto.BIOLOGICO_FLORA);
        impacto3.setImpacto("Pérdida de Vegetación(en la franjadel río)");
        impacto3.setTipoImpacto(TipoImpactoDto.NEGATIVO);
        impacto3.setMagnitud(MagnitudDto.LEVE);
        impacto3.setDuracion(DuracionDto.LARGO_PLAZO);
        impacto3.setReversibilidad(ReversibilidadDto.IRREVERSIBLE);
        impacto3.setMedidaGestion("Compensación con reforstaci+on en zonas aledañas(especies nativas)");
        impacto3.setCostoMedidaGestion(1800.50);


        AnalisisAmbientalRequestDto analisisDto = new AnalisisAmbientalRequestDto();

        analisisDto.setTieneImpactosAmbientales(true);
        analisisDto.setFilas(List.of(impacto1, impacto2, impacto3));

        return analisisDto;
    }

    public static FilaImpactoAmbientalRequestDto crearFilaImpactoAmbiental(
            MedioDto medio,
            String impacto,
            TipoImpactoDto tipoImpacto,
            MagnitudDto magnitud,
            DuracionDto duracion,
            ReversibilidadDto reversibilidad,
            String medidaGestion,
            Double costoMedidaGestion) {

        FilaImpactoAmbientalRequestDto fila = new FilaImpactoAmbientalRequestDto();
        fila.setMedio(medio);
        fila.setImpacto(impacto);
        fila.setTipoImpacto(tipoImpacto);
        fila.setMagnitud(magnitud);
        fila.setDuracion(duracion);
        fila.setReversibilidad(reversibilidad);
        fila.setMedidaGestion(medidaGestion);
        fila.setCostoMedidaGestion(costoMedidaGestion);

        return fila;
    }


}
