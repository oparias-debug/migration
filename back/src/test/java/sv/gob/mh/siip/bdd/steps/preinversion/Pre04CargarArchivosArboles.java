package sv.gob.mh.siip.bdd.steps.preinversion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ArchivoDescargado;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-04-cargar-archivos-arboles.feature. */
public class Pre04CargarArchivosArboles {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String ICONO_PROBLEMAS = "Agregar Árbol de problemas";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final IdentificacionService identificacionService;
    private final ContextoProyectoBdd contextoProyecto;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private String nombreUsuarioActual;
    private boolean esArbolProblemas;
    private byte[] contenidoCargado;
    private ArchivoAdjuntoResumenDto resumenCargado;

    public Pre04CargarArchivosArboles(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            IdentificacionService identificacionService,
            ContextoProyectoBdd contextoProyecto,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.identificacionService = identificacionService;
        this.contextoProyecto = contextoProyecto;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("que el Técnico URP se encuentra en la pestaña {string} con la edición de campos habilitada")
    public void que_el_tecnico_urp_se_encuentra_en_la_pestana_con_la_edicion_habilitada(String pestana) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE04A-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-PRE04A-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        nombreUsuarioActual = "tecnico.urp.bdd.pre04a." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioActual)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioActual + "@example.com")
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE04A-" + sufijo, "Eje temático de prueba"));

        Proyecto proyecto = ProyectoFixtures.nuevoProyecto("Proyecto arboles BDD", EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, ejeTematico);
        proyecto.setCup(ProyectoFixtures.nuevoCup());
        proyecto = proyectoRepository.save(proyecto);
        contextoProyecto.setProyectoActual(proyecto);

        autenticarComo(nombreUsuarioActual);
    }

    @Cuando("el Técnico URP hace clic en el ícono {string}")
    public void el_tecnico_urp_hace_clic_en_el_icono(String icono) {
        esArbolProblemas = ICONO_PROBLEMAS.equals(icono);
    }

    @Cuando("carga un archivo en formato PDF\\/A")
    public void carga_un_archivo_en_formato_pdf_a() {
        resumenCargado = cargarArchivo("%PDF-1.4 contenido de prueba BDD".getBytes(StandardCharsets.UTF_8));
    }

    @Entonces("el sistema almacena el archivo")
    public void el_sistema_almacena_el_archivo() {
        assertThat(resumenCargado).isNotNull();
        assertThat(resumenCargado.getNombreArchivo()).isNotBlank();
        assertThat(resumenCargado.getFechaCarga()).isNotNull();
    }

    @Entonces("el archivo cargado aparece junto al ícono, con la opción de descargar")
    public void el_archivo_cargado_aparece_junto_al_icono_con_la_opcion_de_descargar() {
        ArchivoDescargado descargado = descargarArchivo();
        assertThat(leer(descargado)).isEqualTo(contenidoCargado);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que ya existe un archivo cargado en el ícono {string}")
    public void que_ya_existe_un_archivo_cargado_en_el_icono(String icono) {
        esArbolProblemas = ICONO_PROBLEMAS.equals(icono);
        resumenCargado = cargarArchivo("%PDF-1.4 contenido original BDD".getBytes(StandardCharsets.UTF_8));
    }

    @Cuando("el Técnico URP carga un nuevo archivo en el mismo ícono")
    public void el_tecnico_urp_carga_un_nuevo_archivo_en_el_mismo_icono() {
        resumenCargado = cargarArchivo("%PDF-1.4 contenido reemplazado BDD".getBytes(StandardCharsets.UTF_8));
    }

    @Entonces("el sistema reemplaza el archivo anterior con el nuevo archivo cargado")
    public void el_sistema_reemplaza_el_archivo_anterior_con_el_nuevo_archivo_cargado() {
        ArchivoDescargado descargado = descargarArchivo();
        assertThat(leer(descargado)).isEqualTo(contenidoCargado);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en el ícono para eliminar el archivo")
    public void el_tecnico_urp_hace_clic_en_el_icono_para_eliminar_el_archivo() {
        Long idProyecto = contextoProyecto.getProyectoActual().getId();
        if (esArbolProblemas) {
            identificacionService.eliminarArbolProblemas(idProyecto);
        } else {
            identificacionService.eliminarArbolObjetivos(idProyecto);
        }
    }

    @Entonces("el sistema elimina el archivo cargado")
    public void el_sistema_elimina_el_archivo_cargado() {
        Long idProyecto = contextoProyecto.getProyectoActual().getId();
        assertThatThrownBy(esArbolProblemas
                ? () -> identificacionService.descargarArbolProblemas(idProyecto)
                : () -> identificacionService.descargarArbolObjetivos(idProyecto))
                .isInstanceOf(RecursoNoEncontradoException.class);
        RequestContextHolder.resetRequestAttributes();
    }

    private ArchivoAdjuntoResumenDto cargarArchivo(byte[] contenido) {
        contenidoCargado = contenido;
        Long idProyecto = contextoProyecto.getProyectoActual().getId();
        MockMultipartFile archivo = new MockMultipartFile("archivo", "arbol.pdf", "application/pdf", contenido);
        return esArbolProblemas ? identificacionService.cargarArbolProblemas(idProyecto, archivo)
                : identificacionService.cargarArbolObjetivos(idProyecto, archivo);
    }

    private ArchivoDescargado descargarArchivo() {
        Long idProyecto = contextoProyecto.getProyectoActual().getId();
        return esArbolProblemas ? identificacionService.descargarArbolProblemas(idProyecto)
                : identificacionService.descargarArbolObjetivos(idProyecto);
    }

    private byte[] leer(ArchivoDescargado descargado) {
        try {
            return descargado.recurso().getInputStream().readAllBytes();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
