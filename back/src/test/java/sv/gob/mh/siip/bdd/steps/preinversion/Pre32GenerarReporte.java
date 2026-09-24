package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;
import sv.gob.mh.siip.model.preinversion.service.AvanceFinancieroPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-32-generar-reporte.feature (SF-3, Anexo A.6). */
public class Pre32GenerarReporte {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final int ANIO = 2028;
    private static final String NOMBRE_INSTITUCION = "Institucion Reporte 32 (BDD)";
    private static final String COMENTARIO_DGICP = "Comentario DGICP de prueba (BDD)";
    private static final String ETIQUETA_COMENTARIOS = "Comentarios al reporte financiero DGICP:";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final AvanceFinancieroCuatrimestralRepository avanceRepository;
    private final RevisionAvancePapRepository revisionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final AvanceFinancieroPapService service;

    private Institucion institucion;
    private UnidadEjecutora unidadEjecutora;
    private Proyecto proyecto;
    private String formatoSeleccionado;
    private Resource reporteGenerado;
    private List<List<String>> celdas;

    public Pre32GenerarReporte(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository, RevisionAvancePapRepository revisionRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, AvanceFinancieroPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
        this.revisionRepository = revisionRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Cuando("el actor hace clic en el botón \"Generar Reporte\" avance-financiero")
    public void el_actor_hace_clic_en_generar_reporte() {
        crearUnidadEjecutora();
        autenticar(RolUsuario.TECNICO_URP);
    }

    @Y("selecciona el formato {string} avance-financiero")
    public void selecciona_el_formato(String formato) {
        formatoSeleccionado = "Excel".equals(formato) ? "EXCEL" : "PDF";
    }

    @Entonces("el sistema genera el reporte \\(Anexo A.6)")
    public void el_sistema_genera_el_reporte() {
        reporteGenerado = service.generarReporte(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I,
                formatoSeleccionado);
        assertThat(reporteGenerado).isNotNull();
        assertThat(reporteGenerado.exists() || reporteGenerado.isReadable() || contentLengthPositivo(reporteGenerado))
                .isTrue();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que existe avance financiero registrado y \"Comentarios al reporte financiero DGICP\" para el período")
    public void que_existe_avance_y_comentarios_dgicp() {
        crearUnidadEjecutora();
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M32E" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S32E" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-32E-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        EtapaPreinversion etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto,
                TipoEtapaPreinversion.PERFIL, 10000.0);
        FuenteFinanciamientoEtapaPap fuente = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapa).fuenteFinanciamiento(FuenteFinanciamiento.FONDO_GENERAL).build());
        ProgCuatrimestralFinanciera prog = progRepository.save(ProgCuatrimestralFinanciera.builder().fuente(fuente)
                .anio(ANIO).montoCuatrimestre1(BigDecimal.valueOf(1000)).montoCuatrimestre2(BigDecimal.valueOf(2000))
                .montoCuatrimestre3(BigDecimal.valueOf(3000)).build());
        avanceRepository.save(AvanceFinancieroCuatrimestral.builder().programacion(prog)
                .cuatrimestre(Cuatrimestre.CUATRIMESTRE_I).montoEjecutado(BigDecimal.valueOf(500))
                .observaciones("Observacion del cuatrimestre (BDD)").fechaRegistro(LocalDateTime.now(ZONA)).build());
        revisionRepository.save(RevisionAvancePap.builder()
                .idUnidadEjecutora(unidadEjecutora.getId())
                .anio(ANIO)
                .periodo(Cuatrimestre.CUATRIMESTRE_I)
                .comentarioReporteFinancieroDgicp(COMENTARIO_DGICP)
                .build());
    }

    @Cuando("el {string} genera el reporte en Excel avance-financiero")
    public void el_actor_genera_el_reporte_en_excel(String rol) throws IOException {
        autenticar(RolUsuario.valueOf(rol));
        reporteGenerado = service.generarReporte(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, "EXCEL");
        celdas = leerCeldas(reporteGenerado);
    }

    @Entonces("el reporte muestra la \"Institución Ejecutora\" y las columnas del Anexo A.1 con su fila \"TOTAL\" \\(Anexo A.6)")
    public void el_reporte_muestra_institucion_columnas_y_total() {
        assertThat(celdas.get(0).get(0)).contains("Institución Ejecutora: " + NOMBRE_INSTITUCION);
        assertThat(celdas.get(2)).contains("Avance Anual %", "Avance al Cuatrimestre Programado",
                "Avance al Cuatrimestre Ejecutado", "Avance al Cuatrimestre %", "Observaciones del Cuatrimestre");
        List<String> fila = celdas.get(3);
        assertThat(fila.get(0)).isEqualTo(proyecto.getCup());
        assertThat(fila).contains("Observacion del cuatrimestre (BDD)");
        assertThat(celdas.get(4).get(1)).isEqualTo("TOTAL");
    }

    @Y("el campo \"Comentarios al reporte financiero DGICP\" {string} en el reporte \\(solo actores internos DGICP)")
    public void el_campo_comentarios_dgicp_en_el_reporte(String visibilidad) {
        boolean contieneComentarios = celdas.stream().anyMatch(fila -> fila.contains(ETIQUETA_COMENTARIOS));
        if ("se muestra".equals(visibilidad)) {
            assertThat(contieneComentarios).isTrue();
            assertThat(celdas.stream().anyMatch(fila -> fila.contains(COMENTARIO_DGICP))).isTrue();
        } else {
            assertThat(contieneComentarios).isFalse();
            assertThat(celdas.stream().noneMatch(fila -> fila.contains(COMENTARIO_DGICP))).isTrue();
        }
        RequestContextHolder.resetRequestAttributes();
    }

    private static List<List<String>> leerCeldas(Resource recurso) throws IOException {
        List<List<String>> resultado = new ArrayList<>();
        try (InputStream entrada = recurso.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(entrada)) {
            XSSFSheet hoja = workbook.getSheetAt(0);
            for (int i = 0; i <= hoja.getLastRowNum(); i++) {
                Row row = hoja.getRow(i);
                List<String> valores = new ArrayList<>();
                if (row != null) {
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell celda = row.getCell(j);
                        valores.add(celda == null ? ""
                                : celda.getCellType() == CellType.STRING ? celda.getStringCellValue()
                                        : String.valueOf(celda.getNumericCellValue()));
                    }
                }
                resultado.add(valores);
            }
        }
        return resultado;
    }

    private boolean contentLengthPositivo(Resource recurso) {
        try {
            return recurso.contentLength() > 0;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void crearUnidadEjecutora() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-32E-" + sufijo, NOMBRE_INSTITUCION));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-32E-" + sufijo, "UE de prueba", institucion));
    }

    private void autenticar(RolUsuario rol) {
        String nombreUsuario = "actor.32e." + UUID.randomUUID().toString().substring(0, 8);
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
