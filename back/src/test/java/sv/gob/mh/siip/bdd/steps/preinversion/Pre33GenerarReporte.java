package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;

/** CU-PRE-33-generar-reporte.feature (SF-4). */
public class Pre33GenerarReporte {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2030;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvanceMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;
    private UnidadEjecutora otraUnidadEjecutora;
    private String formatoSeleccionado;
    private Resource reporteGenerado;

    public Pre33GenerarReporte(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            AvanceMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Cuando("el actor hace clic en el botón \"Generar Reporte\" avance-metas")
    public void el_actor_hace_clic_en_generar_reporte() {
        crearActorYAutenticar(RolUsuario.TECNICO_URP);
    }

    @Y("selecciona el formato {string} avance-metas")
    public void selecciona_el_formato(String formato) {
        formatoSeleccionado = "Excel".equals(formato) ? "EXCEL" : "PDF";
    }

    @Entonces("el sistema genera el reporte")
    public void el_sistema_genera_el_reporte() {
        reporteGenerado = service.generarReporteAvanceMetas(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I,
                formatoSeleccionado);
        assertThat(reporteGenerado).isNotNull();
        assertThat(reporteGenerado.exists() || reporteGenerado.isReadable() || contentLengthPositivo(reporteGenerado))
                .isTrue();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el Técnico URP pertenece a una unidad ejecutora y existe otra unidad ejecutora avance-metas")
    public void que_el_tecnico_urp_pertenece_a_una_unidad_y_existe_otra() {
        crearActorYAutenticar(RolUsuario.TECNICO_URP);
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion otraInstitucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-33F-" + sufijo, "Otra institucion de prueba"));
        otraUnidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33F-" + sufijo, "Otra UE de prueba", otraInstitucion));
    }

    @Cuando("el Técnico URP solicita el reporte indicando la otra unidad ejecutora avance-metas")
    public void el_tecnico_urp_solicita_el_reporte_de_otra_unidad() {
        reporteGenerado = service.generarReporteAvanceMetas(otraUnidadEjecutora.getId(), ANIO,
                CuatrimestreDto.CUATRIMESTRE_I, "EXCEL");
    }

    @Entonces("el sistema genera el reporte de la unidad ejecutora del Técnico URP, ignorando la solicitada avance-metas")
    public void el_sistema_genera_el_reporte_de_la_unidad_del_tecnico_urp() throws IOException {
        String textoReporte = textoExcel(reporteGenerado);
        assertThat(textoReporte).contains("Unidad Ejecutora " + unidadEjecutora.getId() + " -");
        assertThat(textoReporte).doesNotContain("Unidad Ejecutora " + otraUnidadEjecutora.getId() + " -");
        RequestContextHolder.resetRequestAttributes();
    }

    private static String textoExcel(Resource recurso) throws IOException {
        StringBuilder texto = new StringBuilder();
        try (InputStream entrada = recurso.getInputStream(); Workbook libro = new XSSFWorkbook(entrada)) {
            for (Row fila : libro.getSheetAt(0)) {
                for (Cell celda : fila) {
                    if (celda.getCellType() == CellType.STRING) {
                        texto.append(celda.getStringCellValue()).append('\n');
                    }
                }
            }
        }
        return texto.toString();
    }

    private boolean contentLengthPositivo(Resource recurso) {
        try {
            return recurso.contentLength() > 0;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void crearActorYAutenticar(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-33F-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33F-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "actor.33f." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de prueba (BDD)")
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
}
