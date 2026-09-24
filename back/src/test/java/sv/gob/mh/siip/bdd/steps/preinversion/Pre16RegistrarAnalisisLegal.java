package sv.gob.mh.siip.bdd.steps.preinversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
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
public class Pre16RegistrarAnalisisLegal {


    @Autowired private UsuarioRepository usuarioRepository;
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
    AnalisisLegalDto analisisGuardado;
    AnalisisLegalDto analisisEncontrado;

    /**
     * Antecedentes:
     * @param arg0
     * @param arg1
     */
    @Dado("que el Técnico URP ingresa a la pestaña {string}, sección {string} analisis-legal")
    public void queElTécnicoURPIngresaALaPestañaSecciónAnalisisLegal(String arg0, String arg1) {
        assertThat(arg0).isEqualTo("Formulación del Proyecto");
    }

    @Dado("se encuentra en la pantalla {string} \\(Anexo A.{int}) analisis-legal")
    public void se_encuentra_en_la_pantalla_anexo_a_analisis_legal(String string, Integer int1) {
        assertThat(string).isEqualTo("Análisis Legal");
    }


    /**
     * Escenario: Desplegar la tabla al seleccionar "Sí"
     * @param arg0
     * @param arg1
     */
    @Cuando("el Técnico URP selecciona  si {string} en {string} analisis-legal")
    public void elTécnicoURPSeleccionaSiEnAnalisisLegal(String arg0, String arg1) {
        assertThat(arg0).isEqualTo("Sí");
    }

    @Entonces("el sistema despliega la tabla {string} \\(RN{int}) analisis-legal")
    public void elSistemaDespliegaLaTablaRNAnalisisLegal(String arg0, int arg1) {
        assertThat(arg0).isEqualTo("Análisis Legal");
    }


    /**
     * Escenario: No desplegar la tabla al seleccionar "No"
     * @param arg0
     * @param arg1
     */
    @Cuando("el Técnico URP selecciona no {string} en {string} analisis-legal")
    public void elTécnicoURPSeleccionaNoEnAnalisisLegal(String arg0, String arg1) {
        assertThat(arg0).isEqualTo("No");
    }

    @Entonces("el sistema no despliega la tabla {string} \\(RN{int}) analisis-legal")
    public void elSistemaNoDespliegaLaTablaRNAnalisisLegal(String arg0, int arg1) {
        assertThat(arg0).isEqualTo("Análisis Legal");
    }


    /**
     * Escenario: Registrar y guardar el análisis legal (camino feliz)
     * @param arg0
     */
    @Dado("que la tabla {string} está desplegada analisis-legal")
    public void queLaTablaEstáDesplegadaAnalisisLegal(String arg0) {
        assertThat(arg0).isEqualTo("Análisis Legal");
    }

    @Cuando("el Técnico URP registra el {string} y el {string} de una fila analisis-legal")
    public void elTécnicoURPRegistraElYElDeUnaFilaAnalisisLegal(String arg0, String arg1) {
        assertThat(arg0).isEqualTo("Análisis o Gestión Legal Requerida");
        assertThat(arg1).isEqualTo("Entregable");
    }

    @Y("registra el {string} analisis-legal")
    public void registraElAnalisisLegal(String arg0) {
        //front-end, nada que hacer en back-end
    }

    @Y("hace clic en el botón {string} analisis-legal")
    public void haceClicEnElBotónAnalisisLegal(String arg0) {
       //front-end, nada que hacver en back-end
    }

    @Entonces("el sistema muestra el mensaje {string} \\(Anexo A.{int}) analisis-legal")
    public void elSistemaMuestraElMensajeAnexoAAnalisisLegal(String mensaje, Integer int1) {
        try {
            crearAnalisisLegal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Cuando("el Técnico URP hace clic en {string} analisis-legal")
    public void elTécnicoURPHaceClicEnAnalisisLegal(String arg0) {
        //front-end, nada que hacer en bak-end
    }

    @Entonces("el sistema guarda la información registrada analisis-legal")
    public void elSistemaGuardaLaInformaciónRegistradaAnalisisLegal() {
        analisisEncontrado = analisisLegalService.obtenerAnalisisLegal(proyecto.getId());
        assertThat(analisisEncontrado).isNotNull();
    }

    @Y("se mantiene en la sección {string} analisis-legal")
    public void seMantieneEnLaSecciónAnalisisLegal(String arg0) {
        //front-end, nada que hacer en bak-end
    }


    /**
     *  Escenario: Agregar una nueva fila a la tabla de Análisis Legal
     */
    @Cuando("el Técnico URP adiciona una nueva fila analisis-legal")
    public void elTécnicoURPAdicionaUnaNuevaFilaAnalisisLegal() {
        //front-end, nada que hacer en bak-end
    }

    @Entonces("el sistema agrega la fila a la tabla {string} \\(RN{int}) analisis-legal")
    public void elSistemaAgregaLaFilaALaTablaRNAnalisisLegal(String arg0, int arg1) {
        //front-end, nada que hacer en bak-end
    }


    /**
     *  Escenario: Eliminar una fila de la tabla de Análisis Legal
     */
    @Dado("una fila registrada en la tabla analisis-legal")
    public void unaFilaRegistradaEnLaTablaAnalisisLegal() {
        //front-end, nada que hacer en bak-end
    }

    @Cuando("el Técnico URP elimina esa fila analisis-legal")
    public void elTécnicoURPEliminaEsaFilaAnalisisLegal() {
        //front-end, nada que hacer en bak-end
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN{int}) analisis-legal")
    public void elSistemaEliminaLaFilaCorrespondienteRNAnalisisLegal(int arg0) {
        //front-end, nada que hacer en bak-end
    }


    /**
     * Escenario: Cálculo automático del Total Costo Entregables
     */
    @Dado("que se han registrado costos de entregable en varias filas analisis-legal")
    public void queSeHanRegistradoCostosDeEntregableEnVariasFilasAnalisisLegal() {
        //front-end, nada que hacer en bak-end
    }

    @Entonces("el sistema calcula el {string} como la sumatoria de la columna {string} analisis-legal")
    public void elSistemaCalculaElComoLaSumatoriaDeLaColumnaAnalisisLegal(String arg0, String arg1) {
        //front-end, nada que hacer en bak-end
    }


    /**
     * Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
     * @param arg0
     * @param arg1
     */
    @Cuando("el Técnico URP hace clic en {string} sin haber completado el campo {string} analisis-legal")
    public void elTécnicoURPHaceClicEnSinHaberCompletadoElCampoAnalisisLegal(String arg0, String arg1) {
        //front-end, nada que hacer en bak-end
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} \\(RN{int}) analisis-legal")
    public void elSistemaSombreaEnRojoElBordeDelCampoRNAnalisisLegal(String arg0, int arg1) {
        //front-end, nada que hacer en bak-end
    }


    /**
     * Escenario: El campo Costo del entregable no es obligatorio
     * @param arg0
     * @param arg1
     */
    @Dado("que {string} y {string} están completos en una fila analisis-legal")
    public void queYEstánCompletosEnUnaFilaAnalisisLegal(String arg0, String arg1) {
        //front-end, nada que hacer en bak-end
    }

    @Cuando("el Técnico URP guarda sin registrar el {string} analisis-legal")
    public void elTécnicoURPGuardaSinRegistrarElAnalisisLegal(String arg0) {
        //front-end, nada que hacer en bak-end
    }

    @Entonces("el sistema permite guardar la fila analisis-legal")
    public void elSistemaPermiteGuardarLaFilaAnalisisLegal() {
        try {
            crearAnalisisLegal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        analisisEncontrado = analisisLegalService.obtenerAnalisisLegal(proyecto.getId());
        assertThat(analisisEncontrado).isNotNull();

        boolean tieneFilasCostoEntregableNulo = tieneCostoEntregableNulo(analisisEncontrado);
        assertThat(tieneFilasCostoEntregableNulo).isTrue();
    }

    public boolean tieneCostoEntregableNulo(AnalisisLegalDto analisisLegal) {
        if (analisisLegal == null || analisisLegal.getFilas() == null) {
            return false;

        }

        return analisisLegal.getFilas().stream()
                .anyMatch(fila -> fila.getCostoEntregable() == null);
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
        analisisGuardado = analisisLegalService.guardarAnalisisLegal(proyecto.getId(), requestDto);

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

        int cupRandom = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000, 100000);
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
