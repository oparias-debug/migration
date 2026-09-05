package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-3.5-registrar-ficha-emergencia.feature. El clic en "Perfil"/"Guardar" comparte texto con
 * pasos ya definidos en Pre01RegistrarNuevoProyecto/Pre01ResponderObservaciones (no-op allí); la
 * acción real se dispara en el primer paso propio que sigue. "el sistema muestra el mensaje
 * {string}" también es compartido (definido en Pre01RegistrarNuevoProyecto): la excepción que
 * lanza {@code registrarFichaEmergencia} se guarda en {@link ContextoValidacionBdd} para que ese
 * paso la verifique.
 */
public class Pre35RegistrarFichaEmergencia {

    private static final String HEADER_USUARIO = "X-Usuario";

    private static final Map<String, String> PROPIEDAD_POR_CAMPO = Map.of(
            "Planteamiento del problema", "planteamientoProblema",
            "Producto", "productos",
            // "Departamento" se valida de forma transitiva a traves de "Distrito" (no existe como
            // campo propio en FichaEmergenciaRequest): ver CU-PRE-03.5.openapi.yaml.
            "Departamento", "distrito",
            "Distrito", "distrito",
            "Población objetivo", "poblacionObjetivo");

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final SeleccionYRegistroDeEtapasService service;
    private final ContextoValidacionBdd contextoValidacion;

    private Proyecto proyecto;
    private FichaEmergenciaDto fichaGuardada;

    public Pre35RegistrarFichaEmergencia(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            SeleccionYRegistroDeEtapasService service, ContextoValidacionBdd contextoValidacion) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("que el Técnico URP ingresa a \"Captura de Proyectos\" \\(UC-PRE-03) y da clic sobre el CUP de un proyecto de emergencia")
    public void ingresa_a_captura_de_proyectos_y_da_clic_sobre_el_cup_de_emergencia() {
        crearProyectoDeEmergenciaYAutenticar();
    }

    @Dado("el sistema muestra la pantalla del Anexo A.1 con únicamente la etapa \"Perfil\" disponible")
    public void el_sistema_muestra_unicamente_la_etapa_perfil_disponible() {
        List<EtapaDto> etapas = service.listarEtapas(proyecto.getId());
        assertThat(etapas).extracting(EtapaDto::getNombreEtapa).containsExactly(NombreEtapaDto.PERFIL);
    }

    @Entonces("el sistema muestra el formulario del Anexo A.4 con todos los campos habilitados")
    public void el_sistema_muestra_el_formulario_del_anexo_a4() {
        // UI pura: sin estado de backend que verificar en este paso.
    }

    @Cuando("el Técnico URP diligencia todos los campos obligatorios del formulario")
    public void el_tecnico_urp_diligencia_todos_los_campos_obligatorios() {
        // El envio real ocurre en el primer paso propio que sigue al clic en "Guardar" (no-op,
        // definido en Pre01ResponderObservaciones).
    }

    @Entonces("el sistema valida que todos los campos estén diligenciados")
    public void el_sistema_valida_que_todos_los_campos_esten_diligenciados() {
        fichaGuardada = service.registrarFichaEmergencia(proyecto.getId(), formularioValido());
        assertThat(fichaGuardada).isNotNull();
    }

    @Entonces("guarda la información")
    public void guarda_la_informacion() {
        FichaEmergenciaDto recargada = service.obtenerFichaEmergencia(proyecto.getId());
        assertThat(recargada.getPlanteamientoProblema()).isEqualTo(fichaGuardada.getPlanteamientoProblema());
    }

    @Entonces("remite el proyecto a \"Viabilidad\" \\(CU-PRE-24)")
    public void remite_el_proyecto_a_viabilidad() {
        Proyecto recargado = proyectoRepository.findById(proyecto.getId()).orElseThrow();
        assertThat(recargado.getEstado()).isEqualTo(EstadoProyecto.EN_VIABILIDAD);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber completado el campo {string}")
    public void el_tecnico_urp_hace_clic_sin_haber_completado_el_campo(String boton, String campo) {
        FichaEmergenciaRequestDto request = formularioValido();
        switch (campo) {
            case "Planteamiento del problema" -> request.setPlanteamientoProblema(null);
            case "Producto" -> request.setProductos(List.of());
            case "Departamento", "Distrito" -> request.setDistrito(null);
            case "Población objetivo" -> request.setPoblacionObjetivo(null);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
        try {
            service.registrarFichaEmergencia(proyecto.getId(), request);
        } catch (ValidacionNegocioException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("marca en rojo el contorno del campo {string}")
    public void marca_en_rojo_el_contorno_del_campo(String campo) {
        String propiedad = PROPIEDAD_POR_CAMPO.get(campo);
        ValidacionNegocioException excepcion = (ValidacionNegocioException) contextoValidacion.getUltimaExcepcion();
        assertThat(excepcion).isNotNull();
        assertThat(excepcion.getDetalles()).anyMatch(d -> propiedad.equals(d.getCampo()));
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema muestra únicamente la etapa \"Perfil\" en la sección \"Registro de Etapas\" para un proyecto de emergencia \\(RN09)")
    public void el_sistema_muestra_unicamente_perfil_en_registro_de_etapas() {
        List<EtapaDto> etapas = service.listarEtapas(proyecto.getId());
        assertThat(etapas).extracting(EtapaDto::getNombreEtapa).containsExactly(NombreEtapaDto.PERFIL);
        RequestContextHolder.resetRequestAttributes();
    }

    // -----------------------------------------------------------------------------------------

    private FichaEmergenciaRequestDto formularioValido() {
        return new FichaEmergenciaRequestDto()
                .planteamientoProblema("Deslizamientos de tierra afectan a la comunidad tras las lluvias.")
                .productos(List.of(new ProductoSeleccionadoDto().codigoProducto("PROD-1")))
                .distrito("San Salvador")
                .poblacionObjetivo("5,000 familias afectadas");
    }

    private void crearProyectoDeEmergenciaYAutenticar() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-35G-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-35G-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.35g." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-35G-" + sufijo, "Eje temático de prueba"));

        autenticarComo(nombreUsuario);

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto de emergencia CU-PRE-3.5 BDD",
                EstadoProyecto.CUP_ASIGNADO, unidadEjecutora, institucion, sector, ejeTematico));
        proyecto.setIniciativaInversion(IniciativaInversion.PROYECTO);
        proyecto.setEsProyectoEmergencia(true);
        proyecto = proyectoRepository.save(proyecto);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
