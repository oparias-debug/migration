package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
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
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-30-registrar-estudio-arrastre.feature (SF-1, RN-B.a, RN-B.c, RN-B.d). */
public class Pre30RegistrarEstudioArrastre {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;
    private static final double COSTO_ETAPA = 10000d;
    private static final double EJECUTADO_ANIO_ANTERIOR = 4000d;
    private static final double MONTO_PENDIENTE = COSTO_ETAPA - EJECUTADO_ANIO_ANTERIOR;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProgramacionFinancieraPapService service;

    private UnidadEjecutora unidadEjecutora;
    private Proyecto proyecto;
    private EtapaPreinversion etapaPerfil;
    private FuenteFinanciamientoEtapaPap fuenteArrastre;
    private EstudioProgramacionPAPDto estudioConsultado;
    private EstudioProgramacionPAPDto estudioGuardado;
    private ValidacionNegocioException excepcionCapturada;

    public Pre30RegistrarEstudioArrastre(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            ProgramacionFinancieraPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Dado("que el Técnico URP selecciona su Unidad Ejecutora y el Año en la pantalla \"Programación Financiera Cuatrimestral del PAP\" \\(Anexo A.1)")
    public void que_selecciona_su_unidad_ejecutora_y_el_anio() {
        crearEstudioDeArrastreYAutenticar();
    }

    @Y("el sistema muestra el listado de estudios de arrastre con ejecución pendiente")
    public void el_sistema_muestra_el_listado_de_estudios_de_arrastre() {
        assertThat(service.listar(unidadEjecutora.getId(), ANIO, proyecto.getCup(), 0, 20).getContenido())
                .isNotEmpty();
    }

    @Cuando("el Técnico URP hace clic en el código de un proyecto")
    public void el_tecnico_urp_hace_clic_en_el_codigo_de_un_proyecto() {
        estudioConsultado = service.obtenerProgramacionEstudio(proyecto.getCup(), ANIO);
        assertThat(estudioConsultado.getCup()).isEqualTo(proyecto.getCup());
        assertThat(estudioConsultado.getEsArrastre()).isTrue();
    }

    // "el sistema muestra la pantalla "Programación Financiera por Etapa de Preinversión" (Anexo
    // A.2)" ya está cubierto por el paso genérico "el sistema muestra la pantalla {string} (Anexo
    // A.{int})" (no-op) definido en Pre01RegistrarNuevoProyecto (Cucumber exige una única
    // definición por texto); la verificación real ya ocurrió en el paso anterior.

    @Y("los campos \"Fuente de Financiamiento\", \"Fuente de Recursos\" y \"Convenio\" se muestran bloqueados con la información del ejercicio anterior")
    public void los_campos_de_fuente_se_muestran_bloqueados() {
        FilaFuenteProgramacionDto fila = estudioConsultado.getEtapas().get(0).getFuentes().get(0);
        assertThat(fila.getFuenteFinanciamiento()).isEqualTo(FuenteFinanciamientoDto.FONDO_GENERAL);
        assertThat(fila.getEjecutadoAniosAnteriores()).isEqualTo(EJECUTADO_ANIO_ANTERIOR);
    }

    @Cuando("el Técnico URP registra los montos en \"I Cuatrimestre\", \"II Cuatrimestre\" y \"III Cuatrimestre\"")
    public void el_tecnico_urp_registra_los_montos_en_los_cuatrimestres() {
        // El envio real ocurre al hacer clic en "Guardar" (paso compartido de CU-PRE-30); la
        // accion se dispara en el primer paso propio de esta clase que le sigue.
    }

    @Entonces("el sistema valida los datos según RN-B literal c.2")
    public void el_sistema_valida_los_datos_segun_rn_b_c2() {
        estudioGuardado = guardarMontos(2000, 2000, 2000, fuenteArrastre.getId());
        assertThat(estudioGuardado.getEtapas().get(0).getFuentes().get(0).getTotalProgramadoAnio())
                .isEqualTo(MONTO_PENDIENTE);
    }

    @Y("guarda automáticamente el registro")
    public void guarda_automaticamente_el_registro() {
        assertThat(progRepository.findByFuenteIdAndAnio(fuenteArrastre.getId(), ANIO)).isPresent();
    }

    @Y("traslada los valores de cada cuatrimestre a la pantalla del Anexo A.1")
    public void traslada_los_valores_a_la_pantalla_del_anexo_a1() {
        assertThat(service.listar(unidadEjecutora.getId(), ANIO, proyecto.getCup(), 0, 20).getContenido())
                .anySatisfy(fila -> assertThat(fila.getTotalProgramadoAnio()).isEqualTo(MONTO_PENDIENTE));
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema permite registrar los montos de cada cuatrimestre para esa fuente adicional")
    public void el_sistema_permite_registrar_montos_para_fuente_adicional() {
        GuardarProgramacionEstudioRequestDto request = new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(new FilaFuenteProgramacionRequestDto()
                                .fuenteFinanciamiento(FuenteFinanciamientoDto.PRESTAMOS_EXTERNOS)
                                .montoCuatrimestre1(5000d).montoCuatrimestre2(0d).montoCuatrimestre3(0d)));
        estudioGuardado = service.guardarProgramacionEstudio(proyecto.getCup(), ANIO, request);
        assertThat(fuenteRepository.findByEtapaPreinversionId(etapaPerfil.getId())).hasSize(2);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios")
    public void el_sistema_regresa_sin_guardar_los_cambios() {
        assertThat(progRepository.findByFuenteId(fuenteArrastre.getId())).hasSize(1);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que la suma de los cuatrimestres registrados es mayor al monto pendiente de ejecutar \\(Costo de la etapa menos lo Ejecutado en años anteriores)")
    public void que_la_suma_es_mayor_al_monto_pendiente() {
        Long idFuente = fuenteArrastre.getId();
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> guardarMontos(3000, 3000, 3000, idFuente));
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("MONTO_SUPERA_COSTO_ETAPA");
    }

    @Y("se mantiene en la pantalla del Anexo A.2")
    public void se_mantiene_en_la_pantalla_del_anexo_a2() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(progRepository.findByFuenteId(fuenteArrastre.getId())).hasSize(1);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que la suma de los cuatrimestres registrados es inferior al monto pendiente de ejecutar")
    public void que_la_suma_es_inferior_al_monto_pendiente() {
        estudioGuardado = guardarMontos(1000, 1000, 1000, fuenteArrastre.getId());
    }

    @Entonces("el sistema coloca el remanente en la columna \"Años posteriores\", calculado como Costo de la etapa menos Ejecutado años anteriores menos Total Año \\(RN-B.c)")
    public void el_sistema_coloca_el_remanente_en_anios_posteriores() {
        FilaFuenteProgramacionDto fila = estudioGuardado.getEtapas().get(0).getFuentes().get(0);
        assertThat(fila.getAniosPosteriores()).isEqualTo(COSTO_ETAPA - EJECUTADO_ANIO_ANTERIOR - 3000d);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que se registraron montos en los tres cuatrimestres")
    public void que_se_registraron_montos_en_los_tres_cuatrimestres() {
        estudioGuardado = guardarMontos(1000, 2000, 3000, fuenteArrastre.getId());
    }

    @Entonces("el sistema calcula el \"% Cuatrimestre\" de cada uno como \\(Monto Programado del cuatrimestre \\/ Total Programado Año) × 100 \\(RN-B.d)")
    public void el_sistema_calcula_el_porcentaje_de_cada_cuatrimestre() {
        FilaFuenteProgramacionDto fila = estudioGuardado.getEtapas().get(0).getFuentes().get(0);
        assertThat(fila.getPorcentajeCuatrimestre1()).isCloseTo(16.67, within(0.01));
        assertThat(fila.getPorcentajeCuatrimestre2()).isCloseTo(33.33, within(0.01));
        assertThat(fila.getPorcentajeCuatrimestre3()).isCloseTo(50.0, within(0.01));
    }

    @Y("la suma de los tres porcentajes es igual al 100%")
    public void la_suma_de_los_tres_porcentajes_es_100() {
        FilaFuenteProgramacionDto fila = estudioGuardado.getEtapas().get(0).getFuentes().get(0);
        double suma = fila.getPorcentajeCuatrimestre1() + fila.getPorcentajeCuatrimestre2() + fila.getPorcentajeCuatrimestre3();
        assertThat(suma).isCloseTo(100.0, within(0.01));
        RequestContextHolder.resetRequestAttributes();
    }

    private EstudioProgramacionPAPDto guardarMontos(double c1, double c2, double c3, Long idFuente) {
        GuardarProgramacionEstudioRequestDto request = new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(new FilaFuenteProgramacionRequestDto()
                                .idFuente(idFuente)
                                .montoCuatrimestre1(c1).montoCuatrimestre2(c2).montoCuatrimestre3(c3)));
        return service.guardarProgramacionEstudio(proyecto.getCup(), ANIO, request);
    }

    private void crearEstudioDeArrastreYAutenticar() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-30R2-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-30R2-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M30R2" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S30R2" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-30R2-" + sufijo, "Eje tematico de prueba"));

        String nombreUsuario = "urp.30r2." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        etapaPerfil = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, COSTO_ETAPA);
        fuenteArrastre = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapaPerfil)
                .fuenteFinanciamiento(FuenteFinanciamiento.FONDO_GENERAL)
                .fuenteRecursos("Fondo General")
                .build());
        progRepository.save(ProgCuatrimestralFinanciera.builder()
                .fuente(fuenteArrastre)
                .anio(ANIO - 1)
                .montoCuatrimestre1(BigDecimal.valueOf(EJECUTADO_ANIO_ANTERIOR))
                .montoCuatrimestre2(BigDecimal.ZERO)
                .montoCuatrimestre3(BigDecimal.ZERO)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
