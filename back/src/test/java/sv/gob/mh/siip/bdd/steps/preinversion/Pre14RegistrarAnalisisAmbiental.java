package sv.gob.mh.siip.bdd.steps.preinversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre14RegistrarAnalisisAmbiental {


    @Autowired private UsuarioRepository usuarioRepository;
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






    //Antecedentes:
    @Dado("que el Técnico URP ingresa a la pestaña {string}, sección {string} análisis-ambiental")
    public void queElTécnicoURPIngresaALaPestañaSecciónAnálisisAmbiental(String arg0, String arg1) {
        assertThat(arg0).isEqualTo("Formulación del Proyecto");
    }

    @Y("se encuentra en la pantalla {string} \\(Anexo A.{int}) análisis-ambiental")
    public void seEncuentraEnLaPantallaAnexoAAnálisisAmbiental(String arg0, int arg1) {
        assertThat(arg0).isEqualTo("Análisis Ambiental");
    }


    /**
     *  Escenario: Desplegar la tabla al seleccionar "Sí"
     * @param arg0
     * @param arg1
     */
    @Cuando("el Técnico URP selecciona si {string} en {string} análisis-ambiental")
    public void elTécnicoURPSeleccionaSiEnAnálisisAmbiental(String arg0, String arg1) {
        assertThat(arg1).isEqualTo("¿Existen impactos ambientales asociados al proyecto?");
    }

    @Entonces("el sistema despliega la tabla {string} \\(RN{int}) análisis-ambiental")
    public void elSistemaDespliegaLaTablaRNAnálisisAmbiental(String arg0, int arg1) {
        assertThat(arg0).isEqualTo("Análisis Ambiental");
    }



    /**
     *  Escenario: Desplegar la tabla al seleccionar "Sí"
     */
    @Cuando("el Técnico URP selecciona no {string} en {string} análisis-ambiental")
    public void elTécnicoURPSeleccionaNoEnAnálisisAmbiental(String arg0, String arg1) {
        assertThat(arg1).isEqualTo("¿Existen impactos ambientales asociados al proyecto?");
    }
    @Entonces("el sistema no despliega la tabla {string} \\(RN{int}) análisis-ambiental")
    public void elSistemaNoDespliegaLaTablaRNAnálisisAmbiental(String tabla, int arg1) {
        assertThat(tabla).isEqualTo("Análisis Ambiental");
    }



    /**
     * Escenario: Registrar y guardar la matriz de gestión ambiental (camino feliz)
     * @param arg0
     */
    @Dado("que la tabla {string} está desplegada análisis-ambiental")
    public void queLaTablaEstáDesplegadaAnálisisAmbiental(String tabla) {
        assertThat(tabla).isEqualTo("Análisis Ambiental");
    }

    @Cuando("el Técnico URP selecciona el {string}, el {string}, la {string}, la {string} y la {string} análisis-ambiental")
    public void elTécnicoURPSeleccionaElElLaLaYLaAnálisisAmbiental(String arg0, String arg1, String arg2, String arg3, String arg4) {
        //Front-end, nada que hacer en back-end
    }

    @Y("registra el {string} y la {string} análisis-ambiental")
    public void registraElYLaAnálisisAmbiental(String arg0, String arg1) {
        //Front-end, nada que hacer en back-end
    }

    @Y("registra el {string} análisis-ambiental")
    public void registraElAnálisisAmbiental(String arg0) {
        //Front-end, nada que hacer en back-end
    }

    @Y("hace clic en el botón {string} análisis-ambiental")
    public void haceClicEnElBotónAnálisisAmbiental(String arg0) {

        crearAnalisisAmbiental();
        ResponseEntity<AnalisisAmbientalDto> response =  analisisAmbientalController.obtenerAnalisisAmbiental(this.proyecto.getId());

        System.out.println(" resultado" + response);
    }

    @Entonces("el sistema muestra el mensaje {string} \\(Anexo A.{int}) análisis-ambiental")
    public void elSistemaMuestraElMensajeAnexoAAnálisisAmbiental(String arg0, int arg1) {
        assertThat(arg0).isEqualTo("Sus datos han sido guardados exitosamente. Recuerde que los costos derivados del análisis ambiental deben estar considerados dentro del presupuesto del proyecto.");
    }

    @Cuando("el Técnico URP hace clic en {string} análisis-ambiental")
    public void elTécnicoURPHaceClicEnAnálisisAmbiental(String arg0) {
        assertThat(arg0).isEqualTo("Aceptar");
    }

    @Entonces("el sistema guarda la información registrada análisis-ambiental")
    public void elSistemaGuardaLaInformaciónRegistradaAnálisisAmbiental() {
       // front-end, nada que hacer en back-end
    }

    @Y("se mantiene en la sección {string} análisis-ambiental")
    public void seMantieneEnLaSecciónAnálisisAmbiental(String arg0) {
        assertThat(arg0).isEqualTo("Análisis Ambiental");
    }


    /**
     * Escenario: Cálculo automático del Total Costo Medidas de Gestión
     */
    @Dado("que se han registrado costos de medida de gestión en varias filas análisis-ambiental")
    public void queSeHanRegistradoCostosDeMedidaDeGestiónEnVariasFilasAnálisisAmbiental() {
        // front-end, nada que hacer en back-end
    }

    @Entonces("el sistema calcula el {string} como la sumatoria de la columna {string} análisis-ambiental")
    public void elSistemaCalculaElComoLaSumatoriaDeLaColumnaAnálisisAmbiental(String arg0, String arg1) {
        // front-end, nada que hacer en back-end
    }


    /**
     * Escenario: Agregar una nueva fila a la matriz de gestión ambiental
     */
    @Cuando("el Técnico URP adiciona una nueva fila análisis-ambiental")
    public void elTécnicoURPAdicionaUnaNuevaFilaAnálisisAmbiental() {
        // front-end, nada que hacer en back-end
    }

    @Entonces("el sistema agrega la fila a la tabla {string} \\(RN{int}) análisis-ambiental")
    public void elSistemaAgregaLaFilaALaTablaRNAnálisisAmbiental(String arg0, int arg1) {
        // front-end, nada que hacer en back-end
    }


    /**
     * Escenario: Eliminar una fila de la matriz de gestión ambiental
     */
    @Dado("una fila registrada en la tabla análisis-ambiental")
    public void unaFilaRegistradaEnLaTablaAnálisisAmbiental() {
        // front-end, nada que hacer en back-end
    }

    @Cuando("el Técnico URP elimina esa fila análisis-ambiental")
    public void elTécnicoURPEliminaEsaFilaAnálisisAmbiental() {
        // front-end, nada que hacer en back-end
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN{int}) análisis-ambiental")
    public void elSistemaEliminaLaFilaCorrespondienteRNAnálisisAmbiental(int arg0) {
        // front-end, nada que hacer en back-end
    }


    /**
     * Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
     */
    @Cuando("el Técnico URP hace clic en {string} sin haber completado el campo {string} análisis-ambiental")
    public void elTécnicoURPHaceClicEnSinHaberCompletadoElCampoAnálisisAmbiental(String arg0, String arg1) {
        // front-end, nada que hacer en back-end
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} \\(RN{int}) análisis-ambiental")
    public void elSistemaSombreaEnRojoElBordeDelCampoRNAnálisisAmbiental(String arg0, int arg1) {
        // front-end, nada que hacer en back-end
    }

    /**
     * Escenario: Respetar el límite de caracteres de Impacto y Medida de gestión
     */
    @Cuando("el Técnico URP registra información en el campo {string} o en el campo {string} análisis-ambiental")
    public void elTécnicoURPRegistraInformaciónEnElCampoOEnElCampoAnálisisAmbiental(String arg0, String arg1) {
        // front-end, nada que hacer en back-end
    }

    @Entonces("el sistema permite hasta {int} caracteres para cada uno análisis-ambiental")
    public void elSistemaPermiteHastaCaracteresParaCadaUnoAnálisisAmbiental(int arg0) {
        // front-end, nada que hacer en back-end
    }

    /**
     * Escenario: El campo Costo de Medida de Gestión no es obligatorio
     */
    @Dado("que todos los demás campos obligatorios de una fila están completos análisis-ambiental")
    public void queTodosLosDemásCamposObligatoriosDeUnaFilaEstánCompletosAnálisisAmbiental() {
        // front-end, nada que hacer en back-end
    }

    @Cuando("el Técnico URP guarda sin registrar el {string} análisis-ambiental")
    public void elTécnicoURPGuardaSinRegistrarElAnálisisAmbiental(String arg0) {
        // front-end, nada que hacer en back-end
    }

    @Entonces("el sistema permite guardar la fila análisis-ambiental")
    public void elSistemaPermiteGuardarLaFilaAnálisisAmbiental() {
        // front-end, nada que hacer en back-end
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
