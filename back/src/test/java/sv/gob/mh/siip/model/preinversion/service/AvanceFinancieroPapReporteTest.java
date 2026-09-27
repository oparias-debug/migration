package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;

class AvanceFinancieroPapReporteTest {

    private static final int ANIO = 2028;

    private final UnidadEjecutoraRepository unidadEjecutoraRepository = mock(UnidadEjecutoraRepository.class);
    private final RevisionAvancePapRepository revisionRepository = mock(RevisionAvancePapRepository.class);

    private final AvanceFinancieroPapReporte reporte =
            new AvanceFinancieroPapReporte(unidadEjecutoraRepository, revisionRepository);

    private static String titulo(Resource recurso) throws IOException {
        try (InputStream entrada = recurso.getInputStream(); XSSFWorkbook libro = new XSSFWorkbook(entrada)) {
            return libro.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
        }
    }

    @Test
    void generar_sinUnidadEjecutora_dejaVaciaLaInstitucionYNoMuestraComentariosAlTecnicoUrp() throws IOException {
        Usuario tecnicoUrp = Usuario.builder().rol(RolUsuario.TECNICO_URP).build();

        Resource recurso = reporte.generar(tecnicoUrp, null, ANIO, Cuatrimestre.CUATRIMESTRE_I, List.of(), "EXCEL");

        assertThat(titulo(recurso)).endsWith("Institución Ejecutora: ");
        verifyNoInteractions(unidadEjecutoraRepository, revisionRepository);
    }

    @Test
    void generar_actorDgicp_incluyeInstitucionYComentarioDeLaRevision() throws IOException {
        Usuario tecnicoPre = Usuario.builder().rol(RolUsuario.TECNICO_PRE).build();
        when(unidadEjecutoraRepository.findById(5L)).thenReturn(Optional.of(UnidadEjecutora.builder()
                .institucion(Institucion.builder().nombre("MINSAL").build()).build()));
        when(revisionRepository.findByIdUnidadEjecutoraAndAnioAndPeriodo(5L, ANIO, Cuatrimestre.CUATRIMESTRE_I))
                .thenReturn(Optional.of(RevisionAvancePap.builder().comentarioReporteFinancieroDgicp("Revisar")
                        .build()));

        Resource recurso = reporte.generar(tecnicoPre, 5L, ANIO, Cuatrimestre.CUATRIMESTRE_I, List.of(), "EXCEL");

        assertThat(titulo(recurso)).contains("Institución Ejecutora: MINSAL");
    }
}
