package sv.gob.mh.siip.bdd.steps.preinversion;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.*;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.*;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AnalisisPoblacionService;
import sv.gob.mh.siip.model.preinversion.service.LocalizacionService;
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
public class Pre12ConsultarLocalizacion {

    private Proyecto proyecto;
    private LocalizacionDto ultimoResultadoDto;
    private LocalizacionDto localizacionPersistida;
    private SectorActividad sector;
    private EjeTematico ejeTematico;
    private final ContextoProyectoBdd contextoProyecto;
    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String DISTRITO = "Distrito BDD PRE08";


    private final LocalizacionService localizacionService;
    private final AnalisisPoblacionService analisisPoblacionService;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProyectoRepository proyectoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final MunicipioRepository municipioRepository;

    public Pre12ConsultarLocalizacion(ContextoProyectoBdd contextoProyecto, LocalizacionService localizacionService, AnalisisPoblacionService analisisPoblacionService, InstitucionRepository institucionRepository, UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository, MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository, ProyectoRepository proyectoRepository, DepartamentoRepository departamentoRepository, MunicipioRepository municipioRepository) {
        this.contextoProyecto = contextoProyecto;
        this.localizacionService = localizacionService;
        this.analisisPoblacionService = analisisPoblacionService;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.macroSectorRepository = macroSectorRepository;

        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.proyectoRepository = proyectoRepository;
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
    }

    @Cuando("el Técnico PRE accede a la pantalla {string} de cualquier Unidad Ejecutora localizacion")
    public void elTécnicoPREAccedeALaPantallaDeCualquierUnidadEjecutoraLocalizacion(String arg0) {


        //cremos contexto de entidades
        crearUsuarioContext();
        crearDistrito();

        //creamos la poblacion para alimentar areaInfluencia
        crearAnalisisPoblacionMuiltiple(this.proyecto);


        // Invocación del servicio para el autocompletado (FA-03 / RN03)
        LocalizacionDto resultadoAutocompletado = localizacionService.autocompletarLocalizacionDesdeAreaInfluencia(this.proyecto.getId());

        this.ultimoResultadoDto = resultadoAutocompletado;

        // 1. Suponiendo que tienes tu LocalizacionDto en 'ultimoResultadoDto'
        if (ultimoResultadoDto != null && ultimoResultadoDto.getFilas() != null && !ultimoResultadoDto.getFilas().isEmpty()) {

            // Tomamos la primera fila (o recorres con un for si son varias)
            FilaLocalizacionRequestDto fila = ultimoResultadoDto.getFilas().get(0);

            // 2. Llenamos los campos que estaban en null
            CoordenadasDto coords = new CoordenadasDto();
            coords.setLatitud(13.9941);  // Ejemplo de latitud
            coords.setLongitud(-89.5597); // Ejemplo de longitud
            fila.setCoordenadas(coords);

            fila.setRequiereAdquisicionTerreno(true);
            fila.setPropietario(TipoPropietarioDto.OTRA_INSTITUCION_PUBLICA); // O el enum correspondiente
            fila.setEspecifique("Ministerio de Obras Públicas");

            // 3. Construimos el LocalizacionRequestDto que espera tu controller o serviceImpl
            LocalizacionRequestDto requestDto = new LocalizacionRequestDto();
            requestDto.setFilas(ultimoResultadoDto.getFilas());

            // 4. Persistimos localizacion
            LocalizacionDto localizacionGuardada = localizacionService.guardarLocalizacion(ultimoResultadoDto.getIdProyecto(), requestDto);
            this.localizacionPersistida = localizacionGuardada;
            System.out.println("[BDD] Verificando: que persista localizacion." + this.localizacionPersistida);

        }
        assertThat(this.localizacionPersistida)
                .as("Localización Persistida")
                .isNotNull();

        LocalizacionDto localizacionEncontrada = localizacionService.obtenerLocalizacion(this.localizacionPersistida.getIdProyecto());
        assertThat(localizacionEncontrada)
                .as("Localización Encontrada")
                .isNotNull();
    }

    @Entonces("el sistema muestra la información registrada por el Técnico URP en modo solo lectura localizacion")
    public void elSistemaMuestraLaInformaciónRegistradaPorElTécnicoURPEnModoSoloLecturaLocalizacion() {
        LocalizacionDto localizacionEncontrada = localizacionService.obtenerLocalizacion(this.localizacionPersistida.getIdProyecto());
        assertThat(localizacionEncontrada)
                .as("Localización Encontrada")
                .isNotNull();
    }


    public void crearUsuarioContext(){
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-CUP-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-CUP-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.bdd.cup." + sufijo;
        usuarioRepository.save(Usuario.builder()
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
        sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-CUP-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto registrado", EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, ejeTematico));

        contextoProyecto.setProyectoActual(proyecto);

    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private CeldaUbicacionRequestDto nuevaCelda(int numeroPersonas) {
        return new CeldaUbicacionRequestDto().ubicacion(DISTRITO).numeroPersonas(numeroPersonas);
    }

    private void crearDistrito() {
        Departamento departamento = departamentoRepository.findAll().stream().findFirst().orElseGet(() ->
                departamentoRepository.save(Departamento.builder()
                        .codigo("D" + String.valueOf(System.nanoTime()).substring(0, 8)).nombre("Departamento BDD PRE08")
                        .region("Region BDD PRE08").build()));
        if (municipioRepository.findAllByOrderByNombreAsc().stream()
                .noneMatch(municipio -> DISTRITO.equals(municipio.getNombre()))) {
            municipioRepository.save(Municipio.builder()
                    .codigo("M" + String.valueOf(System.nanoTime()).substring(0, 8)).nombre(DISTRITO)
                    .departamento(departamento).build());
        }
    }

    public void crearAnalisisPoblacion(Proyecto proyecto){
        AnalisisPoblacionRequestDto request = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(100))))
                .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(80))))
                .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(30))));
        analisisPoblacionService.guardar(proyecto.getId(), request);
    }

    public void crearAnalisisPoblacionMuiltiple(Proyecto proyecto){
        List<AnalisisPoblacionRequestDto> requests = List.of(
                new AnalisisPoblacionRequestDto()
                        .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(100))))
                        .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(80))))
                        .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(30)))),

                new AnalisisPoblacionRequestDto()
                        .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(200))))
                        .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(160))))
                        .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(60)))),

                new AnalisisPoblacionRequestDto()
                        .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(300))))
                        .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(240))))
                        .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(90))))
        );

        for (AnalisisPoblacionRequestDto request : requests) {
            analisisPoblacionService.guardar(proyecto.getId(), request);
        }
    }


}
