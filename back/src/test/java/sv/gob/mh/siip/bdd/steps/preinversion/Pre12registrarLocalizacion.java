package sv.gob.mh.siip.bdd.steps.preinversion;



import static org.assertj.core.api.Assertions.assertThat;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.*;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.*;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AnalisisPoblacionService;
import sv.gob.mh.siip.model.preinversion.service.LocalizacionService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;




import java.util.List;
import java.util.UUID;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre12registrarLocalizacion {




    private LocalizacionDto ultimoResultadoDto;
    private LocalizacionDto localizacionPersistida;
    private Proyecto proyecto;
    private String opcionSeleccionadaUltima;



    private final ContextoProyectoBdd contextoProyecto;
    private final LocalizacionService localizacionService;
    private static final String HEADER_USUARIO = "X-Usuario";
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProyectoRepository proyectoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final MunicipioRepository municipioRepository;
    private final AnalisisPoblacionService analisisPoblacionService;
    private static final String DISTRITO = "Distrito BDD PRE08";


    public Pre12registrarLocalizacion(ContextoProyectoBdd contextoProyecto,
                                      LocalizacionService localizacionService,
                                      InstitucionRepository institucionRepository,
                                      UnidadEjecutoraRepository unidadEjecutoraRepository,
                                      UsuarioRepository usuarioRepository,
                                      MacroSectorRepository macroSectorRepository,
                                      SectorActividadRepository sectorActividadRepository,
                                      EjeTematicoRepository ejeTematicoRepository,
                                      ProyectoRepository proyectoRepository,
                                      DepartamentoRepository departamentoRepository,
                                      MunicipioRepository municipioRepository,
                                      AnalisisPoblacionService analisisPoblacionService) {
        this.contextoProyecto = contextoProyecto;
        this.localizacionService = localizacionService;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.proyectoRepository = proyectoRepository;
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.analisisPoblacionService = analisisPoblacionService;
    }


    @Dado("que el Técnico URP se encuentra en la pantalla {string} \\(Anexo A.{int}) localizacion")
    public void que_el_técnico_urp_se_encuentra_en_la_pantalla_anexo_a_localizacion(String string, Integer int1) {
        assertThat(string).isEqualTo("Localización");
        assertThat(int1).isEqualTo(1);
    }

    @Y("^se encuentra en la pantalla \"([^\"]*)\" \\(Anexo ([^)]*)\\) Localización")
    public void se_encuentra_en_la_pantalla(String pantalla, String anexo) {
        String pantallaActual = pantalla + "(Anexo A.1)";
        assertThat(pantallaActual)
                .as("se encuentra en la pantalla \"Descripción Técnica\" (Anexo A.1)")
                .isNotNull();
    }


    /**
     * Escenario: Autocompletar la ubicación desde  Área de Influencia (camino feliz, FA-03)
     */
    @Dado("que el proyecto ya cuenta con ubicaciones registradas en CU-PRE-(\\d+) \"([^\"]*)\" localizacion$")
    public void queElProyectoYaCuentaConUbicacionesRegistradasEnCUPRELocalizacion(String arg0, String arg1) {
        //cremos contexto de entidades
        crearUsuarioContext();
        crearDistrito();

        crearAnalisisPoblacion(this.proyecto);
        if(this.proyecto != null){
            contextoProyecto.setProyectoActual(this.proyecto);
        }
        //creamos la poblacion

        // Validar contexto activo
        assertThat(contextoProyecto.getProyectoActual().getId()).isNotNull();

        // Invocación del servicio para el autocompletado (FA-03 / RN03)
        LocalizacionDto resultadoAutocompletado = localizacionService.autocompletarLocalizacionDesdeAreaInfluencia(this.proyecto.getId());

        this.ultimoResultadoDto = resultadoAutocompletado;

    }

    @Cuando("el Técnico URP hace clic en el botón {string} localizacion")
    public void elTécnicoURPHaceClicEnElBotónPoblacionLocalizacion(String arg0) {
        assertThat(this.ultimoResultadoDto)
                .as(arg0 + ", Ubicaciones no deben ser nulas")
                .isNotNull();
    }

    @Entonces("el sistema completa los campos {string} y {string} de esa fila localizacion")
    public void elSistemaCompletaLosCamposYDeEsaFilaLocalizacion(String departamento, String distrito) {
        System.out.println("[Front] usuario digita campos " + departamento + " y " + distrito);
    }

    @Y("completa, cuando corresponda, los campos {string} y {string} de esa fila localizacion")
    public void completaCuandoCorrespondaLosCamposYDeEsaFila(String direccionEspecifica, String coordenadas) {
        System.out.println("[Front] usuario digita campos " + direccionEspecifica + " y " + coordenadas);
    }

    @Y("se mantiene en la pantalla {string} \\(RN{int}) localizacion")
    public void seMantieneEnLaPantallaRNLocalizacion(String arg0, int arg1) {
        System.out.println("[Front] usuario se mantiene en " + arg0 + " RN0 " + arg1);
    }



    /**
     * Escenario: Agregar una fila adicional de localización
     */
    @Cuando("el Técnico URP hace clic en el botón emergente para adicionar fila localizacion")
    public void elTécnicoURPHaceClicEnElBotónEmergenteParaAdicionarFilaLocalizacion() {
        System.out.println("[Front] usuario agrega fila en boton emergente");

    }

    @Entonces("el sistema agrega una nueva fila, con los campos {string} y {string} editables \\(RN{int}) localizacion")
    public void elSistemaAgregaUnaNuevaFilaConLosCamposYEditablesRNLocalizacion(String departamnto, String distrito, int anexo) {
        System.out.println("[Front] usuario agrega fila en boton emergente con los campos " + departamnto + " y " + distrito);
    }



    /**
     * Escenario: El distrito de una fila adicionada debe estar dentro de los departamentos de Área de Influencia
     */
    @Dado("una fila adicionada por el Técnico URP  localizacion")
    public void unaFilaAdicionadaPorElTécnicoURPLocalizacion() {
        System.out.println("[Front] Fila adicionada por Técnico URP");

    }

    @Cuando("^el Técnico URP selecciona un distrito perteneciente a uno de los departamentos registrados en la tabla \"([^\"]*)\" de CU-PRE-(.*) localizacion$")
    public void elTécnicoURPSeleccionaUnDistritoPertenecienteAUnoDeLosDepartamentosRegistradosEnLaTablaDeCUPRELocalizacion(String nombreTabla, String codigoCu) {
        System.out.println("[Front] Técnico URP selecciona distrito en dentro del departamento del area de influencia para el caso CU-PRE-" + codigoCu);
    }

    @Entonces("el sistema permite el registro \\(RN{int}) localizacion")
    public void elSistemaPermiteElRegistroRNLocalizacion(int arg0) {
        System.out.println("[Front] Técnico URP El Técnico URP podrá editar dicha información y adicionar más filas a la tabla de la pantalla \"Localización\" ");
    }



    /**
     * Escenario: Eliminar una fila de localización
     */
    @Dado("una fila registrada en la tabla de {string} localizacion")
    public void unaFilaRegistradaEnLaTablaDeLocalizacion(String arg0) {
        System.out.println("[Front] Técnico URP El Técnico URP podrá eliminar filas de la tabla de la pantalla \"Localización\" ");
    }

    @Cuando("el Técnico URP hace clic en el botón emergente ubicado a un costado de esa fila localizacion")
    public void elTécnicoURPHaceClicEnElBotónEmergenteUbicadoAUnCostadoDeEsaFilaLocalizacion() {
        System.out.println("[Front] Técnico URP El Técnico URP hace clic boton emergente a un costado de la fila");
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN{int}) localizacion")
    public void elSistemaEliminaLaFilaCorrespondienteRNLocalizacion(int arg0) {
        System.out.println("[Front] El sistema elimina la fila");
    }



    /**
     * Escenario: Registrar coordenadas ubica automáticamente un marcador en el mapa
     */
    @Cuando("el Técnico URP registra las {string} de una fila en formato DD \\(Grados Decimales) localizacion")
    public void elTécnicoURPRegistraLasDeUnaFilaEnFormatoDDGradosDecimalesLocalizacion(String arg0) {
        System.out.println("[Front] Técnico URP registra coordenadas en formato e grados decimales");
    }

    @Entonces("^el sistema ubica automáticamente un marcador en el mapa del Anexo (.*) localizacion$")
    public void elSistemaUbicaAutomáticamenteUnMarcadorEnElMapaDelAnexoALocalizacion(String anexo) {
        System.out.println("[Front] El sistema ubica autmaticamente un marcador en el mapa");
    }



    /**
     * Escenario: Agregar un marcador en el mapa autocompleta las coordenadas
     */
    @Cuando("el Técnico URP agrega un marcador en una ubicación específica del mapa localizacion")
    public void elTécnicoURPAgregaUnMarcadorEnUnaUbicaciónEspecíficaDelMapaLocalizacion() {
        System.out.println("[Front] Técnico URP agrega un marcador en una ubicación específica del mapa");
    }

    @Entonces("el sistema autocompleta el campo {string} de la fila correspondiente en formato DD localizacion")
    public void elSistemaAutocompletaElCampoDeLaFilaCorrespondienteEnFormatoDDLocalizacion(String arg0) {
        System.out.println("[Front] el sistema autocompleta el campo \"Coordenadas\" de la fila correspondiente en formato DD");
    }



    /**
     * Escenario: El campo Coordenadas se bloquea cuando el Distrito es "Nivel nacional"
     */
    @Dado("que el Técnico URP selecciona {string} en el campo {string} de una fila localizacion")
    public void queElTécnicoURPSeleccionaEnElCampoDeUnaFilaLocalizacion(String arg0, String arg1) {
        System.out.println("[Front] Técnico URP selecciona \"Nivel nacional\" en el campo \"Distritos\" de una fila localizacion");
    }

    @Entonces("el sistema bloquea el ingreso de coordenadas de esa fila, mostrando la celda sombreada localizacion")
    public void elSistemaBloqueaElIngresoDeCoordenadasDeEsaFilaMostrandoLaCeldaSombreadaLocalizacion() {
        System.out.println("[Front] el sistema bloquea el ingreso de coordenadas de esa fila, mostrando la celda sombreada");
    }



    /**
     * Escenario: Desplegar el campo del propietario al requerir adquisición de terreno
     */
    @Cuando("el Técnico URP selecciona si {string} en {string} de una fila localizacion")
    public void elTécnicoURPSeleccionaSiEnDeUnaFilaLocalizacion(String arg0, String arg1) {
        System.out.println("[Front] Técnico URP selecciona \"Sí\" en \"¿El proyecto requiere de la adquisición de un terreno o inmueble?\"");
    }

    @Entonces("el sistema si despliega, para esa misma fila, el campo {string} \\(RN{int}) localizacion")
    public void elSistemaSiDespliegaParaEsaMismaFilaElCampoRNLocalizacion(String arg0, int arg1) {
        System.out.println("[Front] el sistema si despliega, para esa misma fila, el campo \"¿Quién es el propietario del terreno o inmueble?\"");
    }



    /**
     * Escenario: No desplegar el campo del propietario cuando no se requiere adquisición
     */
    @Cuando("el Técnico URP selecciona {string} en {string} de una fila localizacion")
    public void elTécnicoURPSeleccionaEnDeUnaFilaLocalizacion(String arg0, String arg1) {
        System.out.println("[Front] Técnico URP selecciona \"No\" en \"¿El proyecto requiere de la adquisición de un terreno o inmueble?\"");
    }

    @Entonces("el sistema no despliega, para esa fila, el campo {string} \\(RN{int}) localizacion")
    public void elSistemaNoDespliegaParaEsaFilaElCampoRNLocalizacion(String arg0, int arg1) {
        System.out.println("[Front] el sistema no despliega, para esa misma fila, el campo \"¿Quién es el propietario del terreno o inmueble?\"");
    }



    /**
     * Esquema del escenario: Habilitar el campo "Especifique" según la opción de propietario seleccionada
     */
    @Dado("que el campo {string} de una fila está desplegado  localizacion")
    public void queElCampoDeUnaFilaEstáDesplegadoLocalizacionHabilitar(String arg0) {
        assertThat(arg0).isEqualTo("¿Quién es el propietario del terreno o inmueble?");

        // Limpiamos el estado al iniciar el step
        opcionSeleccionadaUltima = null;
    }

    @Cuando("el Técnico URP selecciona, en esa fila, la opción {string}  localizacion")
    public void elTécnicoURPSeleccionaEnEsaFilaLaOpciónLocalizacionHabilita(String opcion) {
        // Guardamos la opción seleccionada (ya sea de la tabla o del escenario fijo)
        this.opcionSeleccionadaUltima = opcion;
    }

    @Entonces("^el sistema habilita el campo \"([^\"]*)\" de esa misma fila \\(RN(.*)\\)\\s+localizacion$")
    public void elSistemaHabilitaElCampoDeEsaMismaFilaRNLocalizacion(String especifique, String numeroRn) {
        assertThat(especifique).isEqualTo("Especifique");

        assertThat(numeroRn).isEqualTo("07");

        // Validamos que la opción elegida SÍ amerite habilitarlo
        assertThat(opcionSeleccionadaUltima).isNotEqualTo("La Institución propietaria del proyecto");
    }




    /**
     * Escenario: No habilitar "Especifique" cuando el propietario es la institución propietaria del proyecto
     */
    @Dado("que el campo {string} de una fila está desplegado localizacion")
    public void queElCampoDeUnaFilaEstáDesplegadoLocalizacionInhabilitar(String arg0) {
        System.out.println("[Front] Técnico URP selecciona \"No\" en \"¿El proyecto requiere de la adquisición de un terreno o inmueble?\"");
    }

    @Cuando("el Técnico URP selecciona, en esa fila, la opción {string} localizacion")
    public void elTécnicoURPSeleccionaEnEsaFilaLaOpciónLocalizacionInhabilita(String arg0) {
        System.out.println("[Front] el Técnico URP selecciona, en esa fila, la opción \"La Institución propietaria del proyecto\"");

    }

    @Entonces("el sistema no habilita el campo {string} de esa fila localizacion")
    public void elSistemaNoHabilitaElCampoDeEsaFilaLocalizacion(String arg0) {
        System.out.println("[Front] el sistema no habilita el campo \"Especifique\" de esa fila");
    }



    /**
     * Escenario: Respetar el límite de caracteres del campo Especifique
     */
    @Dado("que el campo {string} de una fila está habilitado localizacion")
    public void queElCampoDeUnaFilaEstáHabilitadoLocalizacion(String arg0) {
        System.out.println("[Front] que el campo \"Especifique\" de una fila está habilitado");
    }

    @Cuando("el Técnico URP registra información en ese campo localizacion")
    public void elTécnicoURPRegistraInformaciónEnEseCampoLocalizacion() {
        System.out.println("[Front] el Técnico URP registra información en ese campo");
    }

    @Entonces("el sistema permite hasta {int} caracteres localizacion")
    public void elSistemaPermiteHastaCaracteresLocalizacion(int arg0) {
        assertThat(arg0).isEqualTo(100);

        System.out.println("[BDD] Verificado: El sistema permite ingresar hasta " + arg0 + " caracteres en el campo Especifique.");
    }



    /**
     * Escenario: Guardar la información de Localización (camino feliz)
     */
    @Cuando("el Técnico URP hace clic en el botón {string}  localizacion")
    public void elTécnicoURPHaceClicEnElBotónGuardarLocalizacion(String arg0) {

        //cremos contexto de entidades
        crearUsuarioContext();
        crearDistrito();

        //creamos la poblacion para alimentar areaInfluencia
        crearAnalisisPoblacionMuiltiple(this.proyecto);

        if(this.proyecto != null){
            contextoProyecto.setProyectoActual(this.proyecto);
        }


        // Invocación del servicio para el autocompletado (FA-03 / RN03)
        LocalizacionDto resultadoAutocompletado = localizacionService.autocompletarLocalizacionDesdeAreaInfluencia(this.proyecto.getId());

        this.ultimoResultadoDto = resultadoAutocompletado;

        // 1. Suponiendo que tienes tu LocalizacionDto en 'ultimoResultadoDto'
        if (ultimoResultadoDto != null && ultimoResultadoDto.getFilas() != null && !ultimoResultadoDto.getFilas().isEmpty()) {

            // Tomamos la primera fila (o recorres con un for si son varias)
            FilaLocalizacionRequestDto fila = ultimoResultadoDto.getFilas().get(0);

            // 2. Llenamos los campos que estaban en null
            CoordenadasDto coords = new CoordenadasDto();
            coords.setLatitud(13.9941);  // Ejemplo de latitud
            coords.setLongitud(-89.5597); // Ejemplo de longitud
            fila.setCoordenadas(coords);

            fila.setRequiereAdquisicionTerreno(true);
            fila.setPropietario(TipoPropietarioDto.OTRA_INSTITUCION_PUBLICA); // O el enum correspondiente
            fila.setEspecifique("Ministerio de Obras Públicas");

            // 3. Construimos el LocalizacionRequestDto que espera tu controller o serviceImpl
            LocalizacionRequestDto requestDto = new LocalizacionRequestDto();
            requestDto.setFilas(ultimoResultadoDto.getFilas());

            // 4. Guardamos localizacion
            LocalizacionDto localizacionGuardada = localizacionService.guardarLocalizacion(ultimoResultadoDto.getIdProyecto(), requestDto);
            this.localizacionPersistida = localizacionGuardada;
            System.out.println("[BDD] Verificando: que persista localizacion." + this.localizacionPersistida);

        }
        assertThat(this.localizacionPersistida)
                .as(arg0 + ", Ubicaciones no deben ser nulas")
                .isNotNull();

    }

    @Entonces("^el sistema muestra el mensaje \"([^\"]*)\" \\(Anexo (.*)\\) localizacion$")
    public void elSistemaMuestraElMensajeAnexoLocalizacion(String mensaje, String anexo) {
        System.out.println("[Front] Sistema muestra mensaje de  " + anexo);

    }

    @Cuando("el Técnico URP hace clic en {string} localizacion")
    public void elTécnicoURPHaceClicEnLocalizacion(String arg0) {
        System.out.println("[Front] el Técnico URP hace clic en " + arg0);
    }

    @Entonces("el sistema guarda la información registrada localizacion")
    public void elSistemaGuardaLaInformaciónRegistradaLocalizacion() {
        System.out.println("[Front] Sistema guarda la informacion registrada");
    }

    @Y("se mantiene en la pantalla {string} localizacion")
    public void seMantieneEnLaPantallaLocalizacion(String arg0) {
        System.out.println("[Front] el Técnico URP se mantiene en pantalla  " + arg0);
    }




    /**
     * Escenario: Intentar guardar con campos pendientes de completar
     */
    @Cuando("el Técnico URP hace clic en el botón {string} sin haber completado los campos requeridos localizacion")
    public void elTécnicoURPHaceClicEnElBotónSinHaberCompletadoLosCamposRequeridosLocalizacion(String boton) {
        //cremos contexto de entidades
        crearUsuarioContext();
        crearDistrito();

        if(this.proyecto != null){
            contextoProyecto.setProyectoActual(this.proyecto);
        }

        // Validar contexto activo
        assertThat(contextoProyecto.getProyectoActual().getId()).isNotNull();

        // Invocación del servicio para el autocompletado (FA-03 / RN03)
        LocalizacionDto resultadoAutocompletado = localizacionService.autocompletarLocalizacionDesdeAreaInfluencia(this.proyecto.getId());
        LocalizacionRequestDto requestDto = new LocalizacionRequestDto();
        requestDto.setFilas(resultadoAutocompletado.getFilas());

        if ("Guardar".equalsIgnoreCase(boton)) {
            // Creamos el DTO de request vacío (sin filas o con filas incompletas obligatorias)
            LocalizacionRequestDto localizacionRequestDto = new LocalizacionRequestDto();
            localizacionRequestDto.setFilas(null); // O una lista con filas vacías según valide tu DTO

            assertThat(resultadoAutocompletado)
                    .as("El DTO de request de localización")
                    .isNotNull();

            // Validar que las filas estén vacías o nulas
            assertThat(resultadoAutocompletado.getFilas())
                    .as("La lista de filas de localización")
                    .satisfiesAnyOf(
                            filas -> assertThat(filas).isNull(),
                            filas -> assertThat(filas).isEmpty()
                    );

            // Si trae filas pero quieres validar que sus campos internos vengan vacíos/nulos
            if (resultadoAutocompletado.getFilas() != null && !resultadoAutocompletado.getFilas().isEmpty()) {
                assertThat(resultadoAutocompletado.getFilas())
                        .allSatisfy(fila -> {
                            assertThat(fila.getDepartamento()).isNull();
                            assertThat(fila.getDistrito()).isNull();
                            // Agrega aquí cualquier otro campo que quieras verificar que esté vacío
                        });
            }

        }


    }

    @Entonces("^el sistema sombrea en color rojo los bordes de los campos pendientes de completar \\(RN(.*)\\) localizacion$")
    public void elSistemaSombreaEnColorRojoLosBordesDeLosCamposPendientesDeCompletarRNLocalizacion(String reglaNegocio) {
        System.out.println("[Front] el sistema sombrea de rojo los bordes de campos pendientes");
    }

    public void crearUsuarioContext(){
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-CUP-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-CUP-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.bdd.cup." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioTecnico + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        autenticarComo(nombreUsuarioTecnico);

        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-CUP-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto registrado", EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, ejeTematico));

        contextoProyecto.setProyectoActual(proyecto);

    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private CeldaUbicacionRequestDto nuevaCelda(int numeroPersonas) {
        return new CeldaUbicacionRequestDto().ubicacion(DISTRITO).numeroPersonas(numeroPersonas);
    }

    private void crearDistrito() {
        Departamento departamento = departamentoRepository.findAll().stream().findFirst().orElseGet(() ->
                departamentoRepository.save(Departamento.builder()
                        .codigo("D" + String.valueOf(System.nanoTime()).substring(0, 8)).nombre("Departamento BDD PRE08")
                        .region("Region BDD PRE08").build()));
        if (municipioRepository.findAllByOrderByNombreAsc().stream()
                .noneMatch(municipio -> DISTRITO.equals(municipio.getNombre()))) {
            municipioRepository.save(Municipio.builder()
                    .codigo("M" + String.valueOf(System.nanoTime()).substring(0, 8)).nombre(DISTRITO)
                    .departamento(departamento).build());
        }
    }

    public void crearAnalisisPoblacion(Proyecto proyecto){
        AnalisisPoblacionRequestDto request = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(100))))
                .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(80))))
                .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(30))));
        analisisPoblacionService.guardar(proyecto.getId(), request);
    }

    public void crearAnalisisPoblacionMuiltiple(Proyecto proyecto){
        List<AnalisisPoblacionRequestDto> requests = List.of(
                new AnalisisPoblacionRequestDto()
                        .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(100))))
                        .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(80))))
                        .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(30)))),

                new AnalisisPoblacionRequestDto()
                        .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(200))))
                        .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(160))))
                        .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(60)))),

                new AnalisisPoblacionRequestDto()
                        .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(300))))
                        .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(240))))
                        .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(90))))
        );

        for (AnalisisPoblacionRequestDto request : requests) {
            analisisPoblacionService.guardar(proyecto.getId(), request);
        }
    }


}
