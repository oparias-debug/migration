package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;

/** Pruebas unitarias de {@link NotaEmisionOpinionTecnica} (CU-PRE-26, FA01 paso 1.5, Anexo A1.5). */
class NotaEmisionOpinionTecnicaTest {

    private DocumentosOpinionTecnica documentos;
    private FichaViabilidadPresupuesto presupuesto;
    private NotaEmisionOpinionTecnica nota;

    private final Usuario tecnicoPre = Usuario.builder().id(1L).build();
    private final OpinionTecnica gestion = OpinionTecnica.builder().id(2L)
            .proyecto(Proyecto.builder().id(3L).build()).build();
    private final MockMultipartFile archivo = new MockMultipartFile("notaOt", "nota.pdf", "application/pdf",
            "x".getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        documentos = mock(DocumentosOpinionTecnica.class);
        presupuesto = mock(FichaViabilidadPresupuesto.class);
        nota = new NotaEmisionOpinionTecnica(documentos, presupuesto);
    }

    @Test
    void registraLaNotaSuNumeroYLaInversionDelMomento() {
        when(presupuesto.inversionEstimada(3L)).thenReturn(new BigDecimal("750000.00"));

        nota.registrar(gestion, archivo, "  MH.DGICP.DGI/001.070/2026 ", tecnicoPre);

        verify(documentos).cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_OT, archivo, tecnicoPre);
        assertThat(gestion.getNumeroNotaOt()).isEqualTo("MH.DGICP.DGI/001.070/2026");
        assertThat(gestion.getInversionEstimada()).isEqualByComparingTo("750000");
    }

    @Test
    void laNotaYSuNumeroSonObligatorios() {
        MockMultipartFile vacia = new MockMultipartFile("notaOt", new byte[0]);
        String largo = "N".repeat(NotaEmisionOpinionTecnica.LONGITUD_NUMERO + 1);

        assertThatThrownBy(() -> nota.registrar(gestion, vacia, "N-1", tecnicoPre))
                .isInstanceOf(ReglaNegocioException.class)
                .hasFieldOrPropertyWithValue("codigo", NotaEmisionOpinionTecnica.NOTA_OT_REQUERIDA);
        assertThatThrownBy(() -> nota.registrar(gestion, archivo, null, tecnicoPre))
                .hasFieldOrPropertyWithValue("codigo", NotaEmisionOpinionTecnica.NUMERO_NOTA_OT_REQUERIDO);
        assertThatThrownBy(() -> nota.registrar(gestion, archivo, "   ", tecnicoPre))
                .hasFieldOrPropertyWithValue("codigo", NotaEmisionOpinionTecnica.NUMERO_NOTA_OT_REQUERIDO);
        assertThatThrownBy(() -> nota.registrar(gestion, archivo, largo, tecnicoPre))
                .hasFieldOrPropertyWithValue("codigo", NotaEmisionOpinionTecnica.NUMERO_NOTA_OT_REQUERIDO);
        verify(documentos, never()).cargar(any(), any(), any(), any());
    }
}
