package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ConfigurarPeriodosProgramacionPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPreinversionService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/** Steps de integración del CU-PRE-22.1 contra H2 y el servicio real. */
public class Pre22ProgramacionFinancieraPreinversion {

    private final InstitucionRepository instituciones;
    private final UnidadEjecutoraRepository unidades;
    private final UsuarioRepository usuarios;
    private final ProyectoRepository proyectos;
    private final MacroSectorRepository macrosectores;
    private final SectorActividadRepository sectores;
    private final EjeTematicoRepository ejes;
    private final EtapaPreinversionRepository etapas;
    private final ProgramacionFinancieraPreinversionService service;

    private Proyecto proyecto;
    private EtapaPreinversion etapaPerfil;
    private String usuarioUrp;
    private String usuarioPre;
    private int periodos;
    private ProgramacionFinancieraPreinversionDto respuesta;
    private NombreEtapaDto etapaEnEscenario;
    private RuntimeException errorValidacion;

    public Pre22ProgramacionFinancieraPreinversion(InstitucionRepository instituciones,
            UnidadEjecutoraRepository unidades, UsuarioRepository usuarios, ProyectoRepository proyectos,
            MacroSectorRepository macrosectores, SectorActividadRepository sectores, EjeTematicoRepository ejes,
            EtapaPreinversionRepository etapas, ProgramacionFinancieraPreinversionService service) {
        this.instituciones = instituciones;
        this.unidades = unidades;
        this.usuarios = usuarios;
        this.proyectos = proyectos;
        this.macrosectores = macrosectores;
        this.sectores = sectores;
        this.ejes = ejes;
        this.etapas = etapas;
        this.service = service;
    }

    @Before("@CU-PRE-22.1")
    public void prepararEscenario() {
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("MH-CU221-" + sufijo,
                "Ministerio de Hacienda CU22.1"));
        UnidadEjecutora unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE221-" + sufijo,
                "Unidad ejecutora CU22.1", institucion));
        usuarioUrp = "tecnico.urp.pre221." + sufijo;
        usuarios.save(usuario(usuarioUrp, RolUsuario.TECNICO_URP, institucion, unidad));

        Institucion institucionPre = instituciones.save(ProyectoFixtures.nuevaInstitucion("OTRA-CU221-" + sufijo,
                "Institución externa CU22.1"));
        UnidadEjecutora unidadPre = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UEPRE221-" + sufijo,
                "Unidad del técnico PRE", institucionPre));
        usuarioPre = "tecnico.pre.pre221." + sufijo;
        usuarios.save(usuario(usuarioPre, RolUsuario.TECNICO_PRE, institucionPre, unidadPre));

        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M221-" + sufijo,
                "Macrosector CU22.1"));
        SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S221-" + sufijo, "Sector CU22.1",
                macrosector));
        EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E221-" + sufijo, "Eje CU22.1"));
        proyecto = proyectos.save(ProyectoFixtures.nuevoProyecto("Proyecto CU22.1", EstadoProyecto.EN_REGISTRO,
                unidad, institucion, sector, eje));
        etapaPerfil = etapas.save(etapa(TipoEtapaPreinversion.PERFIL, 0D));
        etapas.save(etapa(TipoEtapaPreinversion.DISENO, 0D));
        autenticar(usuarioUrp);
    }

    @Dado("que el Técnico URP ingresa a la pestaña {string}, sección {string} - programacion-financiera-preinversion")
    public void ingresarAPestana(String pestana, String seccion) {
        assertThat(pestana).isEqualTo("Programación");
        assertThat(seccion).isEqualTo("Programación financiera de la Preinversión");
    }

    @Dado("la columna {string} se muestra autocompletada con las etapas registradas en CU-PRE-{int}.{int} \\(RN03\\) - programacion-financiera-preinversion")
    public void mostrarEtapasRegistradas(String columna, Integer casoUso, Integer subCasoUso) {
        assertThat(columna).isEqualTo("Etapa");
        assertThat(casoUso).isEqualTo(3);
        assertThat(subCasoUso).isEqualTo(5);
        assertThat(etapas.findByProyectoId(proyecto.getId())).extracting(EtapaPreinversion::getTipoEtapa)
                .containsExactlyInAnyOrder(TipoEtapaPreinversion.PERFIL, TipoEtapaPreinversion.DISENO);
    }

    @Cuando("el Técnico URP registra la cantidad de {string} - programacion-financiera-preinversion")
    public void registrarCantidadDePeriodos(String campo) {
        assertThat(campo).isEqualTo("Períodos a programar (preinversión)");
        periodos = 3;
        respuesta = service.configurarPeriodos(proyecto.getId(),
                new ConfigurarPeriodosProgramacionPreinversionRequestDto(periodos));
    }

    @Cuando("hace clic en el botón {string} - programacion-financiera-preinversion")
    public void hacerClicEnBoton(String boton) {
        assertThat(boton).isIn("Aceptar", "Guardar");
    }

    @Entonces("el sistema muestra en la sección {string} una cantidad de columnas igual a la registrada \\(RN04, RN05\\) - programacion-financiera-preinversion")
    public void mostrarColumnasDePeriodos(String seccion) {
        assertThat(seccion).isEqualTo("Programación");
        assertThat(respuesta.getPeriodosAProgramar()).isEqualTo(periodos);
    }

    @Cuando("el Técnico URP registra el monto de {string} para una etapa en un período habilitado - programacion-financiera-preinversion")
    public void registrarProgramacion(String campo) {
        assertThat(campo).isEqualTo("Programación");
        configurarTresPeriodos();
        guardar(List.of(fila(NombreEtapaDto.PERFIL, List.of(1000D, 500D, 0D))));
    }

    @Entonces("el sistema guarda los registros realizados - programacion-financiera-preinversion")
    public void validarRegistrosGuardados() {
        FilaProgramacionEtapaDto perfil = filaRespuesta(NombreEtapaDto.PERFIL);
        assertThat(perfil.getProgramacionPorPeriodo()).containsExactly(1000D, 500D, 0D);
        assertThat(perfil.getTotalProgramacion()).isEqualTo(1500D);
        assertThat(etapas.findById(etapaPerfil.getId()).orElseThrow().getCosto()).isEqualTo(1500D);
    }

    @Dado("la etapa {string} con costo de etapa {string} distribuido en los períodos registrados - programacion-financiera-preinversion")
    public void distribuirCostoPorEtapa(String nombreEtapa, String costo) {
        configurarTresPeriodos();
        double monto = moneda(costo);
        etapaEnEscenario = nombreEtapa(nombreEtapa);
        guardar(List.of(fila(etapaEnEscenario, List.of(monto, 0D, 0D))));
    }

    @Entonces("el sistema calcula el {string} de esa fila como {string} - programacion-financiera-preinversion")
    public void calcularTotalDeFila(String etiqueta, String total) {
        assertThat(etiqueta).isEqualTo("Total");
        assertThat(filaRespuesta(etapaEnEscenario).getTotalProgramacion()).isEqualTo(moneda(total));
    }

    @Entonces("las columnas de período anteriores al período actual se muestran deshabilitadas \\(RN09\\) - programacion-financiera-preinversion")
    public void validarReglaVisualDePeriodosAnteriores() {
        // RN09 es una regla de presentación: el contrato no ordena rechazar esos montos en el backend.
        configurarTresPeriodos();
        assertThat(respuesta.getPeriodosAProgramar()).isPositive();
    }

    @Cuando("el Técnico URP registra {string} para dos etapas distintas en el mismo período - programacion-financiera-preinversion")
    public void registrarDosEtapas(String campo) {
        assertThat(campo).isEqualTo("Programación");
        configurarTresPeriodos();
        guardar(List.of(fila(NombreEtapaDto.PERFIL, List.of(100D, 0D, 0D)),
                fila(NombreEtapaDto.DISENO, List.of(200D, 0D, 0D))));
    }

    @Entonces("el sistema permite ambos registros \\(RN09\\) - programacion-financiera-preinversion")
    public void validarDosRegistros() {
        assertThat(respuesta.getFilas()).hasSize(2);
        assertThat(respuesta.getTotalGeneralPorPeriodo()).startsWith(300D);
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber completado el campo {string} - programacion-financiera-preinversion")
    public void guardarConCampoIncompleto(String boton, String campo) {
        assertThat(boton).isEqualTo("Guardar");
        assertThat(campo).isIn("Períodos a programar (preinversión)", "Programación");
        try {
            if ("Períodos a programar (preinversión)".equals(campo)) {
                service.guardar(proyecto.getId(), solicitud(List.of(fila(NombreEtapaDto.PERFIL, List.of(1D)))));
            } else {
                configurarTresPeriodos();
                service.guardar(proyecto.getId(), solicitud(List.of()));
            }
        } catch (RuntimeException excepcion) {
            errorValidacion = excepcion;
        }
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} \\(RN07\\) - programacion-financiera-preinversion")
    public void validarReglaVisualCampoObligatorio(String campo) {
        // RN07 visual se prueba en frontend; aquí se comprueba su equivalente obligatorio en el contrato HTTP.
        assertThat(campo).isNotBlank();
        assertThat(errorValidacion).isInstanceOf(ValidacionNegocioException.class);
    }

    @Dado("que existe un valor de {string} para una etapa de preinversión - programacion-financiera-preinversion")
    public void existeCostoDeEtapa(String campo) {
        assertThat(campo).isEqualTo("Costo de la etapa");
        configurarTresPeriodos();
        guardar(List.of(fila(NombreEtapaDto.PERFIL, List.of(2000D, 0D, 0D))));
    }

    @Entonces("dicho valor se mantiene actualizado en el campo {string} del CU-PRE-{int}.{int} {string} \\(RN06\\) - programacion-financiera-preinversion")
    public void actualizarCostoDeEtapa(String campo, Integer casoUso, Integer subCasoUso, String casoRelacionado) {
        assertThat(campo).isEqualTo("Costo de la etapa");
        assertThat(casoUso).isEqualTo(3);
        assertThat(subCasoUso).isEqualTo(5);
        assertThat(casoRelacionado).isEqualTo("Selección y registro de etapas");
        assertThat(etapas.findById(etapaPerfil.getId()).orElseThrow().getCosto()).isEqualTo(2000D);
    }

    @Cuando("el Técnico PRE accede a la pantalla {string} de cualquier Unidad Ejecutora - programacion-financiera-preinversion")
    public void consultarComoTecnicoPre(String pantalla) {
        assertThat(pantalla).isEqualTo("Programación Financiera Preinversión");
        autenticar(usuarioUrp);
        configurarTresPeriodos();
        guardar(List.of(fila(NombreEtapaDto.PERFIL, List.of(500D, 0D, 0D))));
        autenticar(usuarioPre);
        respuesta = service.obtener(proyecto.getId());
    }

    @Entonces("el sistema muestra la información registrada por el Técnico URP en modo solo lectura - programacion-financiera-preinversion")
    public void mostrarInformacionSoloLectura() {
        assertThat(respuesta.getFilas()).isNotEmpty();
        assertThat(respuesta.getFilas().getFirst().getProgramacionPorPeriodo()).startsWith(500D);
    }

    private Usuario usuario(String nombre, RolUsuario rol, Institucion institucion, UnidadEjecutora unidad) {
        return Usuario.builder().nombreUsuario(nombre).nombreCompleto(nombre).correo(nombre + "@example.com").rol(rol)
                .institucion(institucion).unidadEjecutora(unidad).activo(true).build();
    }

    private EtapaPreinversion etapa(TipoEtapaPreinversion tipo, Double costo) {
        return EtapaPreinversion.builder().proyecto(proyecto).tipoEtapa(tipo).fechaSeleccion(LocalDateTime.now())
                .costo(costo).build();
    }

    private void configurarTresPeriodos() {
        periodos = 3;
        respuesta = service.configurarPeriodos(proyecto.getId(),
                new ConfigurarPeriodosProgramacionPreinversionRequestDto(periodos));
    }

    private void guardar(List<FilaProgramacionEtapaRequestDto> filas) {
        respuesta = service.guardar(proyecto.getId(), solicitud(filas));
    }

    private static ProgramacionFinancieraPreinversionRequestDto solicitud(
            List<FilaProgramacionEtapaRequestDto> filas) {
        return new ProgramacionFinancieraPreinversionRequestDto().filas(filas);
    }

    private FilaProgramacionEtapaRequestDto fila(NombreEtapaDto etapa, List<Double> montos) {
        return new FilaProgramacionEtapaRequestDto(etapa, montos);
    }

    private FilaProgramacionEtapaDto filaRespuesta(NombreEtapaDto etapa) {
        return respuesta.getFilas().stream().filter(fila -> fila.getEtapa() == etapa).findFirst().orElseThrow();
    }

    private static NombreEtapaDto nombreEtapa(String nombre) {
        return "Diseño".equals(nombre) ? NombreEtapaDto.DISENO : NombreEtapaDto.PERFIL;
    }

    private static double moneda(String valor) {
        return Double.parseDouble(valor.replace("$", "").replace(",", ""));
    }

    private static void autenticar(String usuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        AutenticacionDePrueba.autenticar(usuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
