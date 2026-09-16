package sv.gob.mh.siip.bdd.steps.preinversion;



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
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.DescripcionTecnicaService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre11Consultar {

    private static final String HEADER_USUARIO = "X-Usuario";

    @Autowired
    private DescripcionTecnicaService descripcionTecnicaService;
    @Autowired
    private ComponenteRepository componenteRepository;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProyectoRepository proyectoRepository;
    private DescripcionTecnicaRequestDto requestDto = new DescripcionTecnicaRequestDto();
    private DescripcionTecnicaDto responseDto = new DescripcionTecnicaDto();
    DescripcionTecnicaDto descripcionTecnicaDto;

    public Pre11Consultar(InstitucionRepository institucionRepository, UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository, MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository, ProyectoRepository proyectoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.proyectoRepository = proyectoRepository;
    }


    @Cuando("el Técnico PRE accede a la pantalla {string} de cualquier Unidad Ejecutora desc-tecnica")
    public void elTécnicoPREAccedeALaPantallaDeCualquierUnidadEjecutoraDescTecnica(String arg0) {
        // El Técnico URP registra primero la información (guardarDescripcionTecnica exige TECNICO_URP)
        autenticarActor(RolUsuario.TECNICO_URP);
        this.responseDto = guardarFilaDescripcionTecnicaRequestDTO();

        // El Técnico PRE consulta en modo solo lectura (obtenerDescripcionTecnica admite TECNICO_URP/TECNICO_PRE)
        autenticarActor(RolUsuario.TECNICO_PRE);
        // Se ejecuta directamente sin try-catch para que la prueba falle si hay un error real
        this.descripcionTecnicaDto = this.descripcionTecnicaService.obtenerDescripcionTecnica(responseDto.getIdProyecto());

        assertThat(descripcionTecnicaDto)
                .as("se muestra resultado consulta")
                .isNotNull();
    }

    private void autenticarActor(RolUsuario rol) {
        String sufijo = java.util.UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuario = "actor.bdd.consultar." + sufijo;

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-CN-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-BDD-CN-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor BDD (" + rol + ")")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Entonces("el sistema muestra la información registrada por el Técnico URP en modo solo lectura desc-tecnica")
    public void elSistemaMuestraLaInformaciónRegistradaPorElTécnicoURPEnModoSoloLecturaDescTecnica() {
        // Validamos el resultado de la consulta
        assertThat(this.descripcionTecnicaDto)
                .as("Se muestra resultado de la consulta de descripción técnica")
                .isNotNull();
    }


    private Proyecto crearProyectoConCup() {
        String sufijo = java.util.UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-VER-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-BDD-VER-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        usuarioRepository.save(sv.gob.mh.siip.model.common.domain.Usuario.builder()
                .nombreUsuario("tecnico.bdd." + sufijo)
                .nombreCompleto("Técnico BDD")
                .correo("tecnico." + sufijo + "@example.com")
                .rol(sv.gob.mh.siip.model.common.enums.RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector ms = macroSectorRepository.save(
                ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector BDD"));

        SectorActividad sector = sectorActividadRepository.save(
                ProyectoFixtures.nuevoSector("S" + sufijo, "Sector BDD", ms));

        EjeTematico eje = ejeTematicoRepository.save(
                ProyectoFixtures.nuevoEjeTematico("EJE-BDD-" + sufijo, "Eje Temático BDD"));

        Proyecto proyecto = ProyectoFixtures.nuevoProyecto(
                "Proyecto BDD con CUP",
                sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, eje);


        int cupRandom = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000, 100000);
        proyecto.setCup(String.format("%05d", cupRandom));
        proyecto.setActivo(true);
        proyecto.setDescripcionProyecto("Descripcion de prueba CU-11");
        proyecto.setFechaCupAsignado(java.time.LocalDateTime.now());
        return proyectoRepository.save(proyecto);
    }

    public DescripcionTecnicaDto guardarFilaDescripcionTecnicaRequestDTO() {
        // 1. Crear el proyecto en H2 y asegurarnos de usar SU ID real
        Proyecto proyectoRegistrado = crearProyectoConCup();
        Long idProyecto = proyectoRegistrado.getId();



        Componente componente = new Componente();
        componente.setNombre("TC-EQUIPAMIENTO");
        componente.setDescripcion("Equipamiento e Infraestructura"); // <-- AQUÍ SE RESUELVE EL ERROR (RN07 / @NotBlank)
        componente.setProyecto(proyectoRegistrado);

        // 2. Persistir en H2 antes de invocar la lógica del servicio
        componenteRepository.save(componente);

        // 3. Inicializar el DTO de Petición
        this.requestDto = new DescripcionTecnicaRequestDto();
        this.requestDto.setDescripcionProyecto("Descripcion de prueba CU-11 precargada desde CU-PRE-01");

        ProductoSeleccionadoDto productoMercado = new ProductoSeleccionadoDto()
                .codigoProducto("PROD-CAT-C6-001")
                .producto("Paneles Solares Fotovoltaicos 500W");

        FilaDescripcionTecnicaRequestDto fila = new FilaDescripcionTecnicaRequestDto()
                .producto(productoMercado)
                .componente("TC-EQUIPAMIENTO") // Código del catálogo que procesa el servicio
                .descripcionProducto("Suministro e instalación de paneles solares monocristalinos 500W")
                .cantidad(150.00)
                .unidadMedida("UM-UNIDAD");

        this.requestDto.addFilasItem(fila);

        // 4. Invocar el servicio usando EL MISMO idProyecto que se creó en el paso 1
        return this.descripcionTecnicaService.guardarDescripcionTecnica(idProyecto, this.requestDto);
    }



}
