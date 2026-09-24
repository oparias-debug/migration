package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-33-calcular-estado-avance.feature (RN-B.c, RN-F). */
public class Pre33CalcularEstadoAvance {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final int ANIO = 2030;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final AvanceCuatriMetaFisicaRepository avanceRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final AvanceMetasFisicasPapService service;

    public Pre33CalcularEstadoAvance(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository, ProgCuatrimestralMetaFisicaRepository progRepository,
            AvanceCuatriMetaFisicaRepository avanceRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            AvanceMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    private EtapaAvanceMetasDto etapaConsultada;

    @Dado("un estudio con una ejecución {string} respecto a lo programado al cuatrimestre")
    public void un_estudio_con_una_ejecucion(String condicion) {
        double ejecutadoAniosAnteriores = 0;
        double ejecutadoCi;
        double ejecutadoCii;
        if (condicion.contains("igual al porcentaje programado")) {
            ejecutadoCi = 25;
            ejecutadoCii = 25;
        } else if (condicion.contains("menor al porcentaje programado")) {
            ejecutadoCi = 10;
            ejecutadoCii = 10;
        } else if (condicion.startsWith("mayor al Programado en el Año")) {
            // Ejecutado al cuatrimestre II = 80 > "Programado en el Año" = 75 (sin concluir el estudio).
            ejecutadoCi = 50;
            ejecutadoCii = 30;
        } else if (condicion.contains("mayor al programado")) {
            // Ejecutado al cuatrimestre II = 60: mayor al programado al cuatrimestre (50) sin sobrepasar
            // el "Programado en el Año" (75), RN-B.c.
            ejecutadoCi = 30;
            ejecutadoCii = 30;
        } else {
            // "Total meta ejecutada" = 50 + 25 + 25 = 100% (meta del estudio alcanzada, RN-B.d).
            ejecutadoAniosAnteriores = 50;
            ejecutadoCi = 25;
            ejecutadoCii = 25;
        }
        etapaConsultada = consultarEtapa(ejecutadoAniosAnteriores, ejecutadoCi, ejecutadoCii);
    }

    @Entonces("el sistema le asigna el estado {string} \\(RN-B.c)")
    public void el_sistema_le_asigna_el_estado(String estadoTexto) {
        EstadoAvanceMetasDto esperado = switch (estadoTexto) {
            case "A tiempo" -> EstadoAvanceMetasDto.A_TIEMPO;
            case "Atrasado" -> EstadoAvanceMetasDto.ATRASADO;
            case "Adelantado" -> EstadoAvanceMetasDto.ADELANTADO;
            case "Finalizado" -> EstadoAvanceMetasDto.FINALIZADO;
            // Fuera del catálogo de RN-B.c: el campo "estado" queda vacío.
            default -> null;
        };
        assertThat(etapaConsultada.getEstado()).isEqualTo(esperado);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema calcula el \"Avance Anual Programado\\/Ejecutado\" y el \"Avance Programado\\/Ejecutado al Cuatrimestre\" para cada cuatrimestre, según las fórmulas acumulativas de RN-F")
    public void el_sistema_calcula_los_acumulados() {
        EtapaAvanceMetasDto etapa = consultarEtapa(10, 20, 15);
        assertThat(etapa.getProgramadoEnElAnio()).isEqualTo(75d);
        assertThat(etapa.getEjecutadoEnElAnio()).isEqualTo(35d);
        assertThat(etapa.getProgramadoAlCuatrimestre()).isEqualTo(50d);
        assertThat(etapa.getEjecutadoAlCuatrimestre()).isEqualTo(35d);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema calcula \"Total Meta Ejecutada\" como Ejecutado años anteriores más Avance Anual Ejecutado en el año \\(RN-F)")
    public void el_sistema_calcula_total_meta_ejecutada() {
        EtapaAvanceMetasDto etapa = consultarEtapa(10, 20, 15);
        assertThat(etapa.getTotalMetaEjecutada()).isEqualTo(45d);
        RequestContextHolder.resetRequestAttributes();
    }

    private EtapaAvanceMetasDto consultarEtapa(double ejecutadoAniosAnteriores, double ejecutadoCi, double ejecutadoCii) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-33C-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33C-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.33c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M33C" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S33C" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-33C-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        Proyecto proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        EtapaPreinversion etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 100000.0);
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository.save(EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).build());

        if (ejecutadoAniosAnteriores > 0) {
            ProgCuatrimestralMetaFisica progAnterior = progRepository.save(ProgCuatrimestralMetaFisica.builder()
                    .etapaMetaFisica(etapaMeta).anio(ANIO - 1)
                    .montoCuatrimestre1(BigDecimal.valueOf(ejecutadoAniosAnteriores)).build());
            avanceRepository.save(AvanceCuatriMetaFisica.builder().programacionMeta(progAnterior)
                    .cuatrimestre(Cuatrimestre.CUATRIMESTRE_I).avanceCuatrimestre(BigDecimal.valueOf(ejecutadoAniosAnteriores))
                    .fechaRegistro(LocalDateTime.now(ZONA)).build());
        }

        ProgCuatrimestralMetaFisica prog = progRepository.save(ProgCuatrimestralMetaFisica.builder()
                .etapaMetaFisica(etapaMeta).anio(ANIO)
                .montoCuatrimestre1(BigDecimal.valueOf(25)).montoCuatrimestre2(BigDecimal.valueOf(25))
                .montoCuatrimestre3(BigDecimal.valueOf(25)).build());
        avanceRepository.save(AvanceCuatriMetaFisica.builder().programacionMeta(prog)
                .cuatrimestre(Cuatrimestre.CUATRIMESTRE_I).avanceCuatrimestre(BigDecimal.valueOf(ejecutadoCi))
                .fechaRegistro(LocalDateTime.now(ZONA)).build());
        avanceRepository.save(AvanceCuatriMetaFisica.builder().programacionMeta(prog)
                .cuatrimestre(Cuatrimestre.CUATRIMESTRE_II).avanceCuatrimestre(BigDecimal.valueOf(ejecutadoCii))
                .fechaRegistro(LocalDateTime.now(ZONA)).build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        AvanceMetasEstudioDto estudio = service.obtenerAvanceMetasEstudio(proyecto.getCup(), ANIO, CuatrimestreDto.CUATRIMESTRE_II);
        return estudio.getEtapas().get(0);
    }
}
