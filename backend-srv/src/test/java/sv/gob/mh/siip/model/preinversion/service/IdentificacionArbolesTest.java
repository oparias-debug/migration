package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.FormatoArchivoNoSoportadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionArboles.TipoArbol;

/** Pruebas unitarias de {@link IdentificacionArboles} (CU-PRE-04, RNB-1/RNB-2). */
class IdentificacionArbolesTest {

    private static final Long ID_PROYECTO = 1L;
    private static final String RUTA = "/datos/identificacion/arbol.pdf";

    private final IdentificacionAcceso acceso = mock(IdentificacionAcceso.class);
    private final IdentificacionRepository identificacionRepository = mock(IdentificacionRepository.class);
    private final IdentificacionArchivos archivos = mock(IdentificacionArchivos.class);
    private final IdentificacionArboles arboles = new IdentificacionArboles(acceso, identificacionRepository,
            archivos, new IdentificacionEnsamblador(mock(ProyectoMapper.class)));

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO).build();
    private final MockMultipartFile pdf = new MockMultipartFile("archivo", "mi-arbol.pdf", "application/pdf",
            new byte[] {1});

    @BeforeEach
    void setUp() {
        when(acceso.proyectoEditable(ID_PROYECTO)).thenReturn(proyecto);
        when(acceso.proyectoConsultable(ID_PROYECTO)).thenReturn(proyecto);
    }

    private Identificacion guardada() {
        ArgumentCaptor<Identificacion> captor = ArgumentCaptor.forClass(Identificacion.class);
        verify(identificacionRepository).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void cargar_problemasSinRegistroPrevio_creaRegistroConElArchivo() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        when(archivos.guardar(ID_PROYECTO, "arbol-problemas.pdf", pdf)).thenReturn(RUTA);

        ArchivoAdjuntoResumenDto resumen = arboles.cargar(ID_PROYECTO, pdf, TipoArbol.PROBLEMAS);

        assertThat(resumen.getNombreArchivo()).isEqualTo("mi-arbol.pdf");
        assertThat(resumen.getFechaCarga()).isNotNull();
        Identificacion entidad = guardada();
        assertThat(entidad.getProyecto()).isSameAs(proyecto);
        assertThat(entidad.getNombreArchivoArbolProblemas()).isEqualTo("mi-arbol.pdf");
        assertThat(entidad.getRutaArchivoArbolProblemas()).isEqualTo(RUTA);
        assertThat(entidad.getFechaCargaArbolProblemas()).isNotNull();
        assertThat(entidad.getNombreArchivoArbolObjetivos()).isNull();
        InOrder orden = inOrder(acceso, archivos);
        orden.verify(acceso).proyectoEditable(ID_PROYECTO);
        orden.verify(archivos).validarFormatoPdf(pdf);
        orden.verify(archivos).guardar(ID_PROYECTO, "arbol-problemas.pdf", pdf);
    }

    @Test
    void cargar_objetivosSinNombreOriginal_usaElNombreEnDisco() {
        Identificacion existente = Identificacion.builder().proyecto(proyecto).build();
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(existente));
        MultipartFile sinNombre = mock(MultipartFile.class);
        when(archivos.guardar(ID_PROYECTO, "arbol-objetivos.pdf", sinNombre)).thenReturn(RUTA);

        ArchivoAdjuntoResumenDto resumen = arboles.cargar(ID_PROYECTO, sinNombre, TipoArbol.OBJETIVOS);

        assertThat(resumen.getNombreArchivo()).isEqualTo("arbol-objetivos.pdf");
        Identificacion entidad = guardada();
        assertThat(entidad).isSameAs(existente);
        assertThat(entidad.getNombreArchivoArbolObjetivos()).isEqualTo("arbol-objetivos.pdf");
        assertThat(entidad.getRutaArchivoArbolObjetivos()).isEqualTo(RUTA);
        assertThat(entidad.getFechaCargaArbolObjetivos()).isNotNull();
        assertThat(entidad.getNombreArchivoArbolProblemas()).isNull();
    }

    @Test
    void cargar_formatoInvalido_noGuardaNada() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        doThrow(new FormatoArchivoNoSoportadoException("El archivo debe estar en formato PDF/A."))
                .when(archivos).validarFormatoPdf(pdf);

        assertThatThrownBy(() -> arboles.cargar(ID_PROYECTO, pdf, TipoArbol.PROBLEMAS))
                .isInstanceOf(FormatoArchivoNoSoportadoException.class);
        verify(archivos, never()).guardar(any(), any(), any());
        verify(identificacionRepository, never()).save(any());
    }

    @Test
    void descargar_problemasCargado_devuelveRecursoYNombre() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(Identificacion.builder()
                .rutaArchivoArbolProblemas(RUTA).nombreArchivoArbolProblemas("problemas.pdf").build()));
        Resource recurso = new ByteArrayResource(new byte[] {1});
        when(archivos.abrir(RUTA)).thenReturn(Optional.of(recurso));

        ArchivoDescargado descargado = arboles.descargar(ID_PROYECTO, TipoArbol.PROBLEMAS);

        assertThat(descargado.recurso()).isSameAs(recurso);
        assertThat(descargado.nombreArchivo()).isEqualTo("problemas.pdf");
        verify(acceso).proyectoConsultable(ID_PROYECTO);
    }

    @Test
    void descargar_objetivosCargado_devuelveNombreDeObjetivos() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(Identificacion.builder()
                .rutaArchivoArbolObjetivos(RUTA).nombreArchivoArbolObjetivos("objetivos.pdf").build()));
        when(archivos.abrir(RUTA)).thenReturn(Optional.of(new ByteArrayResource(new byte[] {1})));

        assertThat(arboles.descargar(ID_PROYECTO, TipoArbol.OBJETIVOS).nombreArchivo()).isEqualTo("objetivos.pdf");
    }

    @Test
    void descargar_sinRegistro_lanzaRecursoNoEncontrado() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> arboles.descargar(ID_PROYECTO, TipoArbol.PROBLEMAS))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No hay ningun archivo cargado en el arbol de problemas.");
    }

    @Test
    void descargar_sinRuta_lanzaRecursoNoEncontrado() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO))
                .thenReturn(Optional.of(Identificacion.builder().rutaArchivoArbolProblemas(RUTA).build()));

        assertThatThrownBy(() -> arboles.descargar(ID_PROYECTO, TipoArbol.OBJETIVOS))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No hay ningun archivo cargado en el arbol de objetivos.");
    }

    @Test
    void descargar_archivoAusenteEnDisco_lanzaRecursoNoEncontrado() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO))
                .thenReturn(Optional.of(Identificacion.builder().rutaArchivoArbolProblemas(RUTA).build()));
        when(archivos.abrir(RUTA)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> arboles.descargar(ID_PROYECTO, TipoArbol.PROBLEMAS))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No hay ningun archivo cargado en el arbol de problemas.");
    }

    @Test
    void eliminar_problemas_borraArchivoYLimpiaReferencia() {
        Identificacion entidad = Identificacion.builder().rutaArchivoArbolProblemas(RUTA)
                .nombreArchivoArbolProblemas("problemas.pdf").rutaArchivoArbolObjetivos("/otra.pdf").build();
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(entidad));

        arboles.eliminar(ID_PROYECTO, TipoArbol.PROBLEMAS);

        verify(archivos).eliminar(RUTA);
        assertThat(guardada()).isSameAs(entidad);
        assertThat(entidad.getRutaArchivoArbolProblemas()).isNull();
        assertThat(entidad.getNombreArchivoArbolProblemas()).isNull();
        assertThat(entidad.getRutaArchivoArbolObjetivos()).isEqualTo("/otra.pdf");
    }

    @Test
    void eliminar_objetivos_borraArchivoYLimpiaReferencia() {
        Identificacion entidad = Identificacion.builder().rutaArchivoArbolObjetivos(RUTA)
                .nombreArchivoArbolObjetivos("objetivos.pdf").build();
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(entidad));

        arboles.eliminar(ID_PROYECTO, TipoArbol.OBJETIVOS);

        verify(archivos).eliminar(RUTA);
        assertThat(entidad.getRutaArchivoArbolObjetivos()).isNull();
        assertThat(entidad.getNombreArchivoArbolObjetivos()).isNull();
        assertThat(entidad.getFechaCargaArbolObjetivos()).isNull();
        verify(acceso).proyectoEditable(ID_PROYECTO);
    }

    @Test
    void eliminar_sinRegistro_lanzaRecursoNoEncontrado() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> arboles.eliminar(ID_PROYECTO, TipoArbol.OBJETIVOS))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No hay ningun archivo cargado en el arbol de objetivos para eliminar.");
    }

    @Test
    void eliminar_sinRuta_lanzaRecursoNoEncontradoSinTocarElDisco() {
        when(identificacionRepository.findByProyectoId(ID_PROYECTO))
                .thenReturn(Optional.of(Identificacion.builder().build()));

        assertThatThrownBy(() -> arboles.eliminar(ID_PROYECTO, TipoArbol.PROBLEMAS))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No hay ningun archivo cargado en el arbol de problemas para eliminar.");
        verify(archivos, never()).eliminar(any());
        verify(identificacionRepository, never()).save(any());
    }
}
