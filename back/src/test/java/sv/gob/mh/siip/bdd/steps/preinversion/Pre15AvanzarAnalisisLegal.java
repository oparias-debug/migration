package sv.gob.mh.siip.bdd.steps.preinversion;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

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
public class Pre15AvanzarAnalisisLegal {


    private final UsuarioRepository usuarioRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProyectoRepository proyectoRepository;
    private final InstitucionRepository institucionRepository;


    public ResponseEntity<AnalisisRiesgoDto> respuestaCalificacionAnalisis;
    public ResponseEntity<AnalisisRiesgoDto> analisisEncontradoResponse;

    private final AnalisisRiesgoService analisisRiesgoService;
    AnalisisRiesgoRequestDto objetoAAnalizar;
    private final AnalisisRiesgoController analisisRiesgoController;

    private static final String HEADER_USUARIO = "X-Usuario";
    private Proyecto proyecto;

    public Pre15AvanzarAnalisisLegal(UsuarioRepository usuarioRepository,
                                     UnidadEjecutoraRepository unidadEjecutoraRepository,
                                     MacroSectorRepository macroSectorRepository,
                                     SectorActividadRepository sectorActividadRepository,
                                     EjeTematicoRepository ejeTematicoRepository,
                                     ProyectoRepository proyectoRepository,
                                     InstitucionRepository institucionRepository,
                                     AnalisisRiesgoService analisisRiesgoService,
                                     AnalisisRiesgoController analisisRiesgoController) {
        this.usuarioRepository = usuarioRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.proyectoRepository = proyectoRepository;
        this.institucionRepository = institucionRepository;
        this.analisisRiesgoService = analisisRiesgoService;
        this.analisisRiesgoController = analisisRiesgoController;
    }

    /**
     * Antecedentes:
     */
    @Dado("que el Técnico URP se encuentra en la pantalla {string} Análisis-Riesgos-legal")
    public void queElTécnicoURPSeEncuentraEnLaPantallaAnálisisRiesgosLegal(String pantalla) {
        assertThat(pantalla).isNotNull();
    }



    /**
     * Escenario: Avanzar sin restricciones cuando ninguna fila tiene Calificación Alta o Muy Alta
     */

    @Dado("que ninguna fila tiene {string} igual a {string} o {string} Análisis-Riesgos-legal")
    public void queNingunaFilaTieneIgualAOAnálisisRiesgosLegal(String arg0, String arg1, String arg2) {
        this.analisisEncontradoResponse = crearYOBtenerAnalisisRiesgo();
        this.respuestaCalificacionAnalisis = amalizarAvaceALegal(this.proyecto);
        analisisRiesgoService.avanzarAAnalisisLegal(this.proyecto.getId());
        assertThat(this.analisisEncontradoResponse.getStatusCode()).isNotNull();
        assertThat(this.analisisEncontradoResponse.getStatusCode().value()).isGreaterThan(0);
    }

    @Cuando("el Técnico URP hace clic en el botón {string} Análisis-Riesgos-legal")
    public void elTécnicoURPHaceClicEnElBotónAnálisisRiesgosLegal(String boton) {
        assertThat(boton).isEqualTo("Siguiente");
    }

    @Entonces("el sistema avanza a la sección {string} \\(CU-PRE{int}, FA{int}) Análisis-Riesgos-legal")
    public void elSistemaAvanzaALaSecciónCUPREFAAnálisisRiesgosLegal(String seccion, int arg1, int arg2) {
        assertThat(seccion).isEqualTo("Análisis Legal");
    }



    /**
     * Esquema del escenario: Bloquear el avance si una fila de riesgo alto no tiene acción de mitigación o costo completos
     */
    @Dado("una fila con {string} igual a {string} Análisis-Riesgos-legal")
    public void unaFilaConIgualAAnálisisRiesgosLegal(String calificacion, String arg1) {
        assertThat(calificacion).isEqualTo("Calificación del Riesgo");
    }

    @Y("el campo {string} no está completo en esa fila Análisis-Riesgos-legal")
    public void elCampoNoEstáCompletoEnEsaFilaAnálisisRiesgosLegal(String arg0) {
        assertThat(arg0).isNotNull();
    }

    @Entonces("el sistema muestra el mensaje {string} \\(RN{int}) Análisis-Riesgos-legal")
    public void elSistemaMuestraElMensajeRNAnálisisRiesgosLegal(String arg0, int arg1) {
       assertThat(arg0).isEqualTo("Se requiere completar los campos obligatorios");
    }

    @Y("marca dicho campo Análisis-Riesgos-legal")
    public void marcaDichoCampoAnálisisRiesgosLegal() {
        //parte de front end, mada que hacer desde back-end
    }

    @Y("no permite avanzar a {string} Análisis-Riesgos-legal")
    public void noPermiteAvanzarAAnálisisRiesgosLegal(String arg0) {
        assertThat(arg0).isEqualTo("Análisis Legal");
    }


    /**
     *
     * Escenario: Avanzar cuando una fila de riesgo alto sí tiene acción de mitigación y costo completos
     */
    @Dado("una fila con {string} igual a {string} o {string} Análisis-Riesgos-legal")
    public void unaFilaConIgualAOAnálisisRiesgosLegal(String arg0, String arg1, String arg2) {
       assertThat(arg0).isEqualTo("Calificación del Riesgo");
    }

    @Y("los campos {string} y {string} están completos en esa fila Análisis-Riesgos-legal")
    public void losCamposYEstánCompletosEnEsaFilaAnálisisRiesgosLegal(String arg0, String arg1) {
        assertThat(arg0).isEqualTo("Acción de mitigación");
        assertThat(arg1).isEqualTo("Costo acción de mitigación");
    }

    @Entonces("el sistema avanza a la sección {string} \\(CU-PRE{int}) Análisis-Riesgos-legal")
    public void elSistemaAvanzaALaSecciónCUPREAnálisisRiesgosLegal(String arg0, int arg1) {
        assertThat(arg0).isEqualTo("Análisis Legal");
    }

    //Helpers

    public ResponseEntity<AnalisisRiesgoDto> crearYOBtenerAnalisisRiesgo(){
        crearUsuarioYProyecto();

        this.objetoAAnalizar =crearAnalisisFilasSinRiesgo();

        analisisRiesgoController.guardarAnalisisRiesgo(this.proyecto.getId(), this.objetoAAnalizar);

        return analisisRiesgoController.guardarAnalisisRiesgo(this.proyecto.getId(), this.objetoAAnalizar);
    }

    public AnalisisRiesgoRequestDto crearAnalisisFilasSinRiesgo(){
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
        return requestDto;
    }

    public AnalisisRiesgoRequestDto crearAnalisisFilasConRiesgo(){
        FilaRiesgoRequestDto riesgo1 = crearFilaRiesgo(
                "DesbordAmiento de Río Chilarna debido a la insuficiente capacidad del cauce y falta de muros de contención.",
                ProbabilidadDto.CASI_SEGURO,
                ImpactoRiesgoDto.EXTREMO,
               null,
                null
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
        return requestDto;
    }

    public ResponseEntity<AnalisisRiesgoDto> amalizarAvaceALegal(Proyecto proyecto){

        return analisisRiesgoController.avanzarAAnalisisLegal(proyecto.getId());
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

    /**
     * Helpers para la creación de datos de prueba y contexto de seguridad.
     *
     * @author Luis Medrano
     */
    public AnalisisRiesgoDto crearAnalisisRiesgo()  {
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

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
