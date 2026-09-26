package sv.gob.mh.siip.bdd.steps.preinversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.springframework.beans.factory.annotation.Autowired;
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
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisLegalRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AnalisisLegalService;
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
public class Pre16Consultar {


    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired private MacroSectorRepository macroSectorRepository;
    @Autowired private SectorActividadRepository sectorActividadRepository;
    @Autowired private EjeTematicoRepository ejeTematicoRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private InstitucionRepository institucionRepository;

    @Autowired
    private AnalisisLegalService analisisLegalService;

    private static final String HEADER_USUARIO = "X-Usuario";
    private Proyecto proyecto;

    AnalisisLegalDto analisisEncontrado;


    @Cuando("el Técnico PRE accede a la pantalla {string} de cualquier Unidad Ejecutora analisis-legal")
    public void elTécnicoPREAccedeALaPantallaDeCualquierUnidadEjecutoraAnalisisLegal(String arg0) {
        try {
            crearAnalisisLegal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Entonces("el sistema muestra la información registrada por el Técnico URP en modo solo lectura analisis-legal")
    public void elSistemaMuestraLaInformaciónRegistradaPorElTécnicoURPEnModoSoloLecturaAnalisisLegal() {
        analisisEncontrado = analisisLegalService.obtenerAnalisisLegal(proyecto.getId());
        assertThat(analisisEncontrado).isNotNull();
    }

    public void crearAnalisisLegal()  {
        crearUsuarioYProyecto();

        FilaAnalisisLegalRequestDto gestion1 = crearFilaAnalisisLegal(
                "Garantía de Buen Diseño",
                "Fianza de Cumplimiento de Buen Diseño(Documento emitido por una aseguradora, Central d Fianza o Banco).",
                null
        );

        FilaAnalisisLegalRequestDto gestion2 = crearFilaAnalisisLegal(
                "Grantia de Cumplimiento Contractual",
                "Fianza de Cumplimiento de Contrato y Calidad de Obra(Documento emitido por una aseguradora, Central d Fianza o Banco).",
                10000.00
        );

        FilaAnalisisLegalRequestDto gestion3 = crearFilaAnalisisLegal(
                "Gestión Ambiental para el Proyecto de Obras",
                "Resolución de Viabilidad Ambiental(Atestado del MARN que autoriza el estudio del impacto ambiental).",
                null
        );

        AnalisisLegalRequestDto requestDto = new AnalisisLegalRequestDto();
        requestDto.setFilas(List.of(gestion1, gestion2, gestion3));

        requestDto.setRequiereAnalisisLegal(true);
        analisisLegalService.guardarAnalisisLegal(proyecto.getId(), requestDto);

    }

    public void crearUsuarioYProyecto() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-LEGAL-" + sufijo, "Institución de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-LEGAL-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.legal." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Técnico URP Legal (BDD)")
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-LEGAL-" + sufijo, "Eje temático de prueba"));

        int cupRandom = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000, 50000);
        Proyecto proyectoTemporal = ProyectoFixtures.nuevoProyecto(
                "Proyecto de Análisis Legal",
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

    public static FilaAnalisisLegalRequestDto crearFilaAnalisisLegal(
            String analisisOgestionLegal,
            String entregable,
            Double costoEntregable) {

        FilaAnalisisLegalRequestDto fila = new FilaAnalisisLegalRequestDto();
        fila.setAnalisisGestionLegalRequerida(analisisOgestionLegal);
        fila.setEntregable(entregable);
        fila.setCostoEntregable(costoEntregable);

        return fila;
    }


}
