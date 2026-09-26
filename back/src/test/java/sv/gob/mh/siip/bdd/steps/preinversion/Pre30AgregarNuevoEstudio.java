package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

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
import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto;
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

/** CU-PRE-30-agregar-nuevo-estudio.feature (SF-2, RN-B.b, RN-B.e). */
public class Pre30AgregarNuevoEstudio {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;

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
    private Institucion institucion;
    private SectorActividad sector;
    private EjeTematico ejeTematico;
    private Proyecto proyecto;
    private EtapaPreinversion etapaPerfil;
    private EstudioProgramacionPAPDto estudioAgregado;
    private EstudioProgramacionPAPDto estudioGuardado;
    private ValidacionNegocioException excepcionCapturada;
    private int fuentesAntesDeGuardar;

    public Pre30AgregarNuevoEstudio(InstitucionRepository institucionRepository,
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

    @Dado("que el Técnico URP se encuentra en la pantalla \"Programación Financiera Cuatrimestral del PAP\" \\(Anexo A.1) nuevo-estudio")
    public void que_el_tecnico_urp_se_encuentra_en_la_pantalla_anexo_a1() {
        crearInsumosYAutenticar();
        etapaPerfil = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 10000.0);
    }

    // "el Técnico URP hace clic en el botón "Agregar estudio"" ya está cubierto por el paso
    // genérico "el Técnico URP hace clic en el botón {string}" (no-op) definido en
    // Pre01RegistrarNuevoProyecto (Cucumber exige una única definición por texto); el registro
    // real ocurre al confirmar el Código, en el primer paso propio de esta clase que le sigue.

    @Entonces("el sistema muestra únicamente el campo \"Código\" y el botón \"Aceptar\"")
    public void el_sistema_muestra_unicamente_el_campo_codigo() {
        // Confirmacion de UI pura: sin estado de backend que verificar.
    }

    @Cuando("el Técnico URP registra el \"Código\" y hace clic en \"Aceptar\"")
    public void el_tecnico_urp_registra_el_codigo_y_hace_clic_en_aceptar() {
        estudioAgregado = service.agregarEstudio(new AgregarEstudioRequestDto(proyecto.getCup(), unidadEjecutora.getId(), ANIO));
    }

    @Entonces("el sistema valida la Ruta de Preinversión y las etapas finalizadas en períodos anteriores \\(RN-B.b, RN-B.e)")
    public void el_sistema_valida_la_ruta_y_las_etapas_finalizadas() {
        assertThat(estudioAgregado.getEtapas()).extracting(e -> e.getEtapa()).containsExactly(NombreEtapaDto.PERFIL);
    }

    @Y("muestra las etapas de la Ruta de Preinversión con \"Costo de la Etapa\" prediligenciado desde CU-PRE-03.5")
    public void muestra_las_etapas_con_costo_prediligenciado() {
        assertThat(estudioAgregado.getEtapas().get(0).getCostoEtapa()).isEqualTo(10000.0);
    }

    @Cuando("el Técnico URP selecciona la Fuente de Financiamiento, la Fuente de Recursos y el Convenio")
    public void el_tecnico_urp_selecciona_fuente_recursos_convenio() {
        // El armado del payload continua en el paso siguiente (mismo Cuando de negocio, RN-B.a).
    }

    @Y("registra los montos de cada cuatrimestre")
    public void registra_los_montos_de_cada_cuatrimestre() {
        // El envio real ocurre al hacer clic en "Guardar" (paso compartido con otras historias de
        // CU-PRE-30); la accion se dispara en el primer paso propio de esta clase que le sigue.
    }

    @Entonces("el sistema valida los datos según RN-B literal c.1")
    public void el_sistema_valida_los_datos_segun_rn_b_c1() {
        GuardarProgramacionEstudioRequestDto request = new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(new FilaFuenteProgramacionRequestDto()
                                .fuenteFinanciamiento(FuenteFinanciamientoDto.FONDO_GENERAL)
                                .fuenteRecursos("Fondo General")
                                .montoCuatrimestre1(4000d).montoCuatrimestre2(3000d).montoCuatrimestre3(3000d)));
        estudioGuardado = service.guardarProgramacionEstudio(proyecto.getCup(), ANIO, request);
        assertThat(estudioGuardado.getEtapas().get(0).getFuentes().get(0).getTotalProgramadoAnio()).isEqualTo(10000d);
    }

    @Y("traslada la información a la tabla del Anexo A.1")
    public void traslada_la_informacion_a_la_tabla_del_anexo_a1() {
        var contenido = service.listar(unidadEjecutora.getId(), ANIO, proyecto.getCup(), 0, 20).getContenido();
        assertThat(contenido).isNotEmpty();
        // Anexo A.1: columnas "Programación I, II y III Cuatrimestre" trasladadas desde el Anexo A.2.
        assertThat(contenido.get(0).getMontoCuatrimestre1()).isEqualTo(4000d);
        assertThat(contenido.get(0).getMontoCuatrimestre2()).isEqualTo(3000d);
        assertThat(contenido.get(0).getMontoCuatrimestre3()).isEqualTo(3000d);
        assertThat(contenido.get(0).getTotalProgramadoAnio()).isEqualTo(10000d);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP intenta guardar sin haber registrado la programación de ninguna etapa")
    public void el_tecnico_urp_intenta_guardar_sin_ninguna_etapa() {
        String cup = proyecto.getCup();
        GuardarProgramacionEstudioRequestDto request = new GuardarProgramacionEstudioRequestDto();
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionEstudio(cup, ANIO, request));
    }

    @Entonces("el sistema no permite continuar, ya que es obligatorio programar al menos una etapa \\(RN-B.b)")
    public void el_sistema_no_permite_continuar_sin_ninguna_etapa() {
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("SIN_NINGUNA_ETAPA_PROGRAMADA");
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el Técnico URP intenta registrar programación de una etapa posterior sin haber registrado una etapa anterior incluida en la Ruta de Preinversión")
    public void que_intenta_registrar_una_etapa_posterior_sin_la_anterior() {
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PREFACTIBILIDAD, 20000.0);
    }

    @Cuando("intenta guardar")
    public void intenta_guardar() {
        GuardarProgramacionEstudioRequestDto request = new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PREFACTIBILIDAD)
                        .addFuentesItem(new FilaFuenteProgramacionRequestDto()
                                .montoCuatrimestre1(20000d).montoCuatrimestre2(0d).montoCuatrimestre3(0d)));
        String cup = proyecto.getCup();
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionEstudio(cup, ANIO, request));
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("RUTA_PREINVERSION_SALTEADA");
    }

    @Y("le indica que debe programar la etapa saltada o ajustar la Ruta de Preinversión en CU-PRE-03.5")
    public void le_indica_que_debe_programar_la_etapa_saltada() {
        assertThat(excepcionCapturada.getMessage())
                .contains("Las etapas no coinciden con las registradas en la Ruta de Preinversión");
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("los campos \"I Cuatrimestre\", \"II Cuatrimestre\" y \"III Cuatrimestre\" muestran \"$0.00\" por defecto")
    public void los_campos_de_cuatrimestre_muestran_0_por_defecto() {
        estudioAgregado = service.agregarEstudio(new AgregarEstudioRequestDto(proyecto.getCup(), unidadEjecutora.getId(), ANIO));
        var fila = estudioAgregado.getEtapas().get(0).getFuentes();
        assertThat(fila).isEmpty();
    }

    @Y("los campos \"Fuente de Financiamiento\", \"Fuente de Recursos\" y \"Convenio\" muestran \"Seleccione\" por defecto \\(RN-B.a)")
    public void los_campos_de_fuente_muestran_seleccione_por_defecto() {
        // RN-B.a describe los valores por defecto de una fila de fuente recién agregada (botón
        // "+"), antes de que el usuario seleccione o guarde nada; un intento de "Guardar" sin
        // ningún monto es, correctamente, rechazado por RN-B.b (SIN_NINGUNA_ETAPA_PROGRAMADA), asi
        // que se verifican los valores por defecto del propio DTO en lugar de un guardado real.
        FilaFuenteProgramacionRequestDto filaNueva = new FilaFuenteProgramacionRequestDto();
        assertThat(filaNueva.getFuenteFinanciamiento()).isNull();
        assertThat(filaNueva.getFuenteRecursos()).isNull();
        assertThat(filaNueva.getConvenios()).isEmpty();
        assertThat(filaNueva.getMontoCuatrimestre1()).isEqualTo(0d);
        assertThat(filaNueva.getMontoCuatrimestre2()).isEqualTo(0d);
        assertThat(filaNueva.getMontoCuatrimestre3()).isEqualTo(0d);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que una etapa de la preinversión ya fue finalizada física y financieramente en años anteriores")
    public void que_una_etapa_ya_fue_finalizada() {
        FuenteFinanciamientoEtapaPap fuente = fuenteRepository
                .save(FuenteFinanciamientoEtapaPap.builder().etapaPreinversion(etapaPerfil).build());
        progRepository.save(ProgCuatrimestralFinanciera.builder()
                .fuente(fuente)
                .anio(ANIO - 1)
                .montoCuatrimestre1(BigDecimal.valueOf(10000))
                .montoCuatrimestre2(BigDecimal.ZERO)
                .montoCuatrimestre3(BigDecimal.ZERO)
                .build());
    }

    @Entonces("el sistema no la muestra en el Anexo A.2 \\(RN-B.e)")
    public void el_sistema_no_la_muestra_en_el_anexo_a2() {
        estudioAgregado = service.agregarEstudio(new AgregarEstudioRequestDto(proyecto.getCup(), unidadEjecutora.getId(), ANIO));
        assertThat(estudioAgregado.getEtapas()).extracting(e -> e.getEtapa()).doesNotContain(NombreEtapaDto.PERFIL);
        RequestContextHolder.resetRequestAttributes();
    }

    // ---------------------------------------------------------------------------------------------
    // RN-B.c con varias fuentes de financiamiento (botón "+"): se valida el agregado de la etapa.

    @Cuando("el Técnico URP registra en la etapa \"Perfil\" una fuente de financiamiento con {int} y otra con {int} y hace clic en \"Guardar\"")
    public void registra_dos_fuentes_en_la_misma_solicitud(int montoPrimera, int montoSegunda) {
        EtapaProgramacionRequestDto etapa = new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                .addFuentesItem(filaNueva(FuenteFinanciamientoDto.FONDO_GENERAL, montoPrimera))
                .addFuentesItem(filaNueva(FuenteFinanciamientoDto.PRESTAMOS_EXTERNOS, montoSegunda));
        capturarRechazo(etapa);
    }

    @Dado("que la etapa \"Perfil\" ya tiene guardada una fuente de financiamiento con {int} programados en el año")
    public void que_la_etapa_ya_tiene_una_fuente_guardada(int monto) {
        estudioGuardado = service.guardarProgramacionEstudio(proyecto.getCup(), ANIO,
                new GuardarProgramacionEstudioRequestDto().addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(filaNueva(FuenteFinanciamientoDto.FONDO_GENERAL, monto))));
        assertThat(fuenteRepository.findByEtapaPreinversionId(etapaPerfil.getId())).hasSize(1);
    }

    @Dado("que la etapa \"Perfil\" tiene una fuente de financiamiento con {int} ejecutados en años anteriores")
    public void que_la_etapa_tiene_ejecucion_en_anios_anteriores(int monto) {
        FuenteFinanciamientoEtapaPap fuente = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapaPerfil)
                .fuenteFinanciamiento(FuenteFinanciamiento.FONDO_GENERAL)
                .build());
        progRepository.save(ProgCuatrimestralFinanciera.builder()
                .fuente(fuente)
                .anio(ANIO - 1)
                .montoCuatrimestre1(BigDecimal.valueOf(monto))
                .montoCuatrimestre2(BigDecimal.ZERO)
                .montoCuatrimestre3(BigDecimal.ZERO)
                .build());
    }

    @Cuando("el Técnico URP agrega con el botón \"+\" otra fuente de financiamiento con {int} y hace clic en \"Guardar\"")
    public void agrega_otra_fuente_y_guarda(int monto) {
        capturarRechazo(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                .addFuentesItem(filaNueva(FuenteFinanciamientoDto.PRESTAMOS_EXTERNOS, monto)));
    }

    @Cuando("el Técnico URP modifica esa fuente a {int} y agrega con el botón \"+\" otra fuente de financiamiento con {int}")
    public void modifica_la_fuente_guardada_y_agrega_otra(int montoEditado, int montoNueva) {
        Long idFuenteGuardada = estudioGuardado.getEtapas().get(0).getFuentes().get(0).getIdFuente();
        estudioGuardado = service.guardarProgramacionEstudio(proyecto.getCup(), ANIO,
                new GuardarProgramacionEstudioRequestDto().addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(filaNueva(FuenteFinanciamientoDto.FONDO_GENERAL, montoEditado)
                                .idFuente(idFuenteGuardada))
                        .addFuentesItem(filaNueva(FuenteFinanciamientoDto.PRESTAMOS_EXTERNOS, montoNueva))));
    }

    @Entonces("el sistema muestra el mensaje \"Monto Programado supera el costo de la etapa\" sin guardar ninguna fuente \\(Anexo A.3, RN-B.c)")
    public void el_sistema_rechaza_por_superar_el_costo_de_la_etapa() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("MONTO_SUPERA_COSTO_ETAPA");
        assertThat(excepcionCapturada.getMessage()).isEqualTo("Monto Programado supera el costo de la etapa.");
        // La validación ocurre antes de persistir: ninguna fuente adicional se guardó.
        assertThat(fuenteRepository.findByEtapaPreinversionId(etapaPerfil.getId())).hasSize(fuentesAntesDeGuardar);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema guarda ambas fuentes y el total programado de la etapa es igual a su \"Costo de la etapa\" \\(RN-B.c)")
    public void el_sistema_guarda_ambas_fuentes() {
        var fuentes = estudioGuardado.getEtapas().get(0).getFuentes();
        assertThat(fuentes).hasSize(2);
        assertThat(fuentes.stream().mapToDouble(f -> f.getTotalProgramadoAnio()).sum())
                .isEqualTo(estudioGuardado.getEtapas().get(0).getCostoEtapa());
        RequestContextHolder.resetRequestAttributes();
    }

    private static FilaFuenteProgramacionRequestDto filaNueva(FuenteFinanciamientoDto fuenteFinanciamiento, int monto) {
        return new FilaFuenteProgramacionRequestDto()
                .fuenteFinanciamiento(fuenteFinanciamiento)
                .montoCuatrimestre1((double) monto).montoCuatrimestre2(0d).montoCuatrimestre3(0d);
    }

    private void capturarRechazo(EtapaProgramacionRequestDto etapa) {
        String cup = proyecto.getCup();
        fuentesAntesDeGuardar = fuenteRepository.findByEtapaPreinversionId(etapaPerfil.getId()).size();
        GuardarProgramacionEstudioRequestDto request = new GuardarProgramacionEstudioRequestDto().addEtapasItem(etapa);
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ValidacionNegocioException.class,
                () -> service.guardarProgramacionEstudio(cup, ANIO, request));
    }

    private void crearInsumosYAutenticar() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("INS-30A-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-30A-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M30A" + sufijo, "Macrosector de prueba"));
        sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S30A" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-30A-" + sufijo, "Eje tematico de prueba"));

        String nombreUsuario = "urp.30a." + sufijo;
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

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
