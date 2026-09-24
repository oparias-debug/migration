package sv.gob.mh.siip.bdd.steps.preinversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.controller.AnalisisAmbientalController;
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
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre14Consultar {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired private MacroSectorRepository macroSectorRepository;
    @Autowired private SectorActividadRepository sectorActividadRepository;
    @Autowired private EjeTematicoRepository ejeTematicoRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private InstitucionRepository institucionRepository;
    @Autowired private AnalisisAmbientalController analisisAmbientalController;

    @Autowired
    private AnalisisAmbientalService analisisAmbientalService;

    private static final String HEADER_USUARIO = "X-Usuario";
    private Proyecto proyecto;
    Usuario usuarioAuthenticado;
    AnalisisAmbientalDto analisisEncontrado;


    @Cuando("el Técnico PRE accede a la pantalla {string} de cualquier Unidad Ejecutora analisis-ambiental")
    public void elTécnicoPREAccedeALaPantallaDeCualquierUnidadEjecutoraAnalisisAmbiental(String arg0) {
        crearAnalisisAmbiental();
        ResponseEntity<AnalisisAmbientalDto> response =  analisisAmbientalController.obtenerAnalisisAmbiental(this.proyecto.getId());
        assertThat(response).isNotNull();
        analisisEncontrado = response.getBody();
    }

    @Entonces("el sistema muestra la información registrada por el Técnico URP en modo solo lectura analisis-ambiental")
    public void elSistemaMuestraLaInformaciónRegistradaPorElTécnicoURPEnModoSoloLecturaAnalisisAmbiental() {
        assertThat(analisisEncontrado).isNotNull();
    }

    //helpers
    public void crearAnalisisAmbiental(){
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

        analisisAmbientalController.guardarAnalisisAmbiental(this.proyecto.getId(), analisisAmbientalRequestDto);
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


}
