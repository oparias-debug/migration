package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.MedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoMedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.MedidaCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.MedidaCatalogoService;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01-registrar-nuevo-proyecto.feature. El clic en botones genericos ("Guardar",
 * "Regresar") se comparte via el paso "hace clic en el botón {string}" definido en
 * Pre01ResponderObservaciones (Cucumber exige una unica definicion por texto); aqui la accion de
 * negocio real (registrar/actualizar el proyecto) se dispara en el primer paso propio de esta
 * clase que sigue a ese clic. El ultimo paso de "Regresar sin guardar y cancelar" queda sin
 * implementar: es un texto compartido con CU-PRE-01-solicitar-cup.feature (navegacion de UI
 * pura, sin equivalente verificable en el backend), definido alli.
 */
public class Pre01RegistrarNuevoProyecto {

    private static final String HEADER_USUARIO = "X-Usuario";

    private static final Map<String, String> PROPIEDAD_POR_CAMPO = Map.of(
            "Iniciativa de inversión", "iniciativaInversion",
            "Nombre del proyecto", "nombre",
            "Monto Estimado de Inversión", "montoEstimadoInversion",
            "Sector", "idSector",
            "Eje temático", "idEjeTematico",
            "Descripción del proyecto", "descripcionProyecto");

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final ProyectoService proyectoService;
    private final MedidaCatalogoRepository medidaCatalogoRepository;
    private final MedidaCatalogoService medidaCatalogoService;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final Validator validator;

    private UnidadEjecutora unidadEjecutora;
    private Institucion institucion;
    private SectorActividad sector;
    private EjeTematico ejeTematico;
    private ProyectoRequestDto borrador;
    private ProyectoDto proyectoGuardado;
    private ProyectoDto proyectoObtenido;
    private Proyecto proyectoExistente;
    private Set<ConstraintViolation<ProyectoRequestDto>> violaciones;
    private long conteoProyectosAntes;
    private List<MedidaCatalogoDto> catalogoGrd;
    private List<MedidaCatalogoDto> catalogoGrc;
    private List<MedidaCatalogoDto> catalogoAcc;

    public Pre01RegistrarNuevoProyecto(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            ProyectoService proyectoService,
            MedidaCatalogoRepository medidaCatalogoRepository,
            MedidaCatalogoService medidaCatalogoService,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository,
            Validator validator) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.proyectoService = proyectoService;
        this.medidaCatalogoRepository = medidaCatalogoRepository;
        this.medidaCatalogoService = medidaCatalogoService;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.validator = validator;
    }

    @Dado("que el Técnico URP ingresa a la pantalla {string} \\(Anexo A.{int})")
    public void que_el_tecnico_urp_ingresa_a_la_pantalla_anexo_a(String pantalla, Integer anexo) {
        autenticarComoTecnicoUrp();
    }

    @Cuando("el Técnico URP hace clic en el botón {string}")
    public void el_tecnico_urp_hace_clic_en_el_boton(String boton) {
        if ("Nuevo Registro".equals(boton)) {
            borrador = new ProyectoRequestDto();
        } else if ("Ver descripción de categorías".equals(boton)) {
            sembrarCatalogoDePrueba();
            catalogoGrd = medidaCatalogoService.listar(TipoMedidaCatalogoDto.GRD);
            catalogoGrc = medidaCatalogoService.listar(TipoMedidaCatalogoDto.GRC);
            catalogoAcc = medidaCatalogoService.listar(TipoMedidaCatalogoDto.ACC);
        }
        // Otros botones ("Solicitar CUP" en otra feature) son clics de navegacion sin efecto
        // propio que preparar aqui.
    }

    @Entonces("el sistema muestra en una ventana emergente las tablas descritas en los Anexos C.{int}, C.{double} y C.{int}")
    public void el_sistema_muestra_en_una_ventana_emergente_las_tablas_descritas_en_los_anexos_c_c_y_c(Integer int1,
            Double double1, Integer int2) {
        assertThat(catalogoGrd).isNotEmpty();
        assertThat(catalogoGrc).isNotEmpty();
        assertThat(catalogoAcc).isNotEmpty();
    }

    @Cuando("el sistema muestra la pantalla {string} \\(Anexo A.{int})")
    public void el_sistema_muestra_la_pantalla_anexo_a(String pantalla, Integer anexo) {
// Camino feliz: el clic en "Nuevo Registro" se resuelve en el paso anterior; la pantalla
        // mostrada es la misma que la de registro de proyecto, con campos vacios.
        assertThat(borrador).isNotNull();
    }

    @Cuando("el Técnico URP selecciona una de las opciones {string}, {string} o {string} en el campo {string}")
    public void el_tecnico_urp_selecciona_una_de_las_opciones_en_el_campo(String opcion1, String opcion2,
            String opcion3, String campo) {
        borrador.setIniciativaInversion(mapearIniciativa(opcion1));
    }

    @Cuando("registra la información de los campos obligatorios de la pantalla {string}")
    public void registra_la_informacion_de_los_campos_obligatorios_de_la_pantalla(String pantalla) {
        completarCamposObligatorios(borrador);
    }

    @Entonces("el sistema muestra el mensaje {string}")
    public void el_sistema_muestra_el_mensaje(String mensaje) {
        if ("*Campo obligatorio".equals(mensaje)) {
            // El backend no controla la copia exacta de UI "*Campo obligatorio" (responsabilidad
            // del frontend); se verifica que Bean Validation efectivamente marco el campo como
            // invalido, con un mensaje no vacio.
            assertThat(violaciones).isNotEmpty();
            violaciones.forEach(v -> assertThat(v.getMessage()).isNotBlank());
            return;
        }
        // Camino feliz: el clic en "Guardar" se resuelve en Pre01ResponderObservaciones (paso
        // compartido); la accion real de registrar se dispara aqui, en el primer paso propio de
        // esta clase que le sigue.
        proyectoGuardado = proyectoService.registrar(borrador);
        assertThat(mensaje).contains("Guardado");
    }

    @Entonces("el sistema almacena la información")
    public void el_sistema_almacena_la_informacion() {
        assertThat(proyectoGuardado).isNotNull();
        assertThat(proyectoRepository.findById(proyectoGuardado.getIdProyecto())).isPresent();
    }

    @Entonces("regresa a la pantalla {string} con el proyecto en estado {string}")
    public void regresa_a_la_pantalla_con_el_proyecto_en_estado(String pantalla, String estadoUi) {
        assertThat(proyectoGuardado.getEstado()).isEqualTo(EstadoProyectoDto.EN_REGISTRO);
        assertThat(estadoUi).isEqualTo("En Elaboración");
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el Técnico URP está registrando la información en la pantalla {string}")
    public void que_el_tecnico_urp_esta_registrando_la_informacion_en_la_pantalla(String pantalla) {
        borrador = borradorValido();
    }

    @Cuando("hace clic en el botón {string} sin haber completado el campo {string}")
    public void hace_clic_en_el_boton_sin_haber_completado_el_campo(String boton, String campo) {
        quitarCampo(borrador, campo);
        violaciones = validator.validate(borrador);
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string}")
    public void el_sistema_sombrea_en_rojo_el_borde_del_campo(String campo) {
        String propiedad = PROPIEDAD_POR_CAMPO.get(campo);
        assertThat(violaciones).anyMatch(v -> propiedad.equals(v.getPropertyPath().toString()));
    }

    @Dado("que el Técnico URP tiene datos sin guardar en la pantalla {string}")
    public void que_el_tecnico_urp_tiene_datos_sin_guardar_en_la_pantalla(String pantalla) {
        borrador = borradorValido();
        conteoProyectosAntes = proyectoRepository.count();
    }

    @Cuando("el sistema muestra el mensaje {string} \\(Anexo A.{double})")
    public void el_sistema_muestra_el_mensaje_anexo_a(String mensaje, Double anexo) {
        // Confirmacion de UI pura (Anexo A.2.1): no hay estado de backend que verificar aqui.
    }

    @Cuando("el Técnico URP hace clic en {string}")
    public void el_tecnico_urp_hace_clic_en(String opcion) {
        // "Cancelar"/"Aceptar" de la ventana de confirmacion: sin efecto propio en el backend,
        // mas alla de lo que ya se verifica en el paso final de cada escenario.
    }

    @Entonces("el sistema regresa a la pantalla {string} sin guardar los datos")
    public void el_sistema_regresa_a_la_pantalla_sin_guardar_los_datos(String pantalla) {
        assertThat(proyectoRepository.count()).isEqualTo(conteoProyectosAntes);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un proyecto en el listado de {string} con estado {string} o {string}")
    public void un_proyecto_en_el_listado_de_con_estado_o(String pantalla, String estado1, String estado2) {
        proyectoExistente = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto editable",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, sector, ejeTematico));
    }

    @Cuando("el Técnico URP hace clic en el nombre del proyecto")
    public void el_tecnico_urp_hace_clic_en_el_nombre_del_proyecto() {
        proyectoObtenido = proyectoService.obtener(proyectoExistente.getId());
    }

    @Entonces("el sistema habilita para edición los campos de la pantalla {string}")
    public void el_sistema_habilita_para_edicion_los_campos_de_la_pantalla(String pantalla) {
        // RN 1.c / RN 2.2.b: solo editable en estos dos estados.
        assertThat(proyectoObtenido.getEstado())
                .isIn(EstadoProyectoDto.EN_REGISTRO, EstadoProyectoDto.OBSERVADO_DGICP_REGISTRO);
    }

    @Entonces("el Técnico URP puede ajustar y\\/o actualizar los campos editados")
    public void el_tecnico_urp_puede_ajustar_y_o_actualizar_los_campos_editados() {
        ProyectoRequestDto cambios = borradorValido();
        cambios.setNombre("Proyecto editado por BDD");

        ProyectoDto actualizado = proyectoService.actualizar(proyectoExistente.getId(), cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Proyecto editado por BDD");
        RequestContextHolder.resetRequestAttributes();
    }

    private void autenticarComoTecnicoUrp() {
        // Sufijo unico por escenario: el filtro de tags de Cucumber (@rol:..., @CU-PRE-01) hace
        // que varios escenarios de esta feature corran dentro de la misma sesion de prueba, y
        // codigos/usuarios fijos chocan contra las restricciones UNIQUE si el rollback entre
        // escenarios no aisla completamente cada insercion.
        String sufijo = java.util.UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuarioTecnico = "tecnico.urp.bdd.registro." + sufijo;

        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-REG-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-BDD-REG-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo("tecnico.urp.bdd.registro." + sufijo + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-BDD-" + sufijo, "Eje temático de prueba"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuarioTecnico);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private ProyectoRequestDto borradorValido() {
        return completarCamposObligatorios(new ProyectoRequestDto().iniciativaInversion(IniciativaInversionDto.PROYECTO));
    }

    private ProyectoRequestDto completarCamposObligatorios(ProyectoRequestDto request) {
        return request.nombre("Proyecto de prueba BDD")
                .montoEstimadoInversion(1000.0)
                .idSector(sector.getId())
                .idEjeTematico(ejeTematico.getId())
                .descripcionProyecto("Descripcion de prueba BDD");
    }

    private void quitarCampo(ProyectoRequestDto request, String campoLabel) {
        switch (campoLabel) {
            case "Iniciativa de inversión" -> request.setIniciativaInversion(null);
            case "Nombre del proyecto" -> request.setNombre(null);
            case "Monto Estimado de Inversión" -> request.setMontoEstimadoInversion(null);
            case "Sector" -> request.setIdSector(null);
            case "Eje temático" -> request.setIdEjeTematico(null);
            case "Descripción del proyecto" -> request.setDescripcionProyecto(null);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campoLabel);
        }
    }

    private void sembrarCatalogoDePrueba() {
        // No hay valores oficiales de los Anexos C.1/C.1.5/C.2 disponibles en el repositorio:
        // se siembran entradas de prueba solo para verificar que el catalogo del backend
        // responde, sin inventar contenido oficial.
        medidaCatalogoRepository.save(MedidaCatalogo.builder().tipo(TipoMedidaCatalogo.GRD)
                .codigo("GRD-1").descripcion("Categoria de GRD de prueba BDD").build());
        medidaCatalogoRepository.save(MedidaCatalogo.builder().tipo(TipoMedidaCatalogo.GRC)
                .codigo("GRC-1").descripcion("Categoria de GRC de prueba BDD").build());
        medidaCatalogoRepository.save(MedidaCatalogo.builder().tipo(TipoMedidaCatalogo.ACC)
                .codigo("ACC-1").descripcion("Categoria de ACC de prueba BDD").build());
    }

    private IniciativaInversionDto mapearIniciativa(String etiqueta) {
        return switch (etiqueta) {
            case "Programa" -> IniciativaInversionDto.PROGRAMA;
            case "Proyecto" -> IniciativaInversionDto.PROYECTO;
            case "Estudios Generales" -> IniciativaInversionDto.ESTUDIO_GENERAL;
            default -> throw new IllegalArgumentException("Iniciativa no reconocida: " + etiqueta);
        };
    }
}
