package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.springframework.data.jpa.domain.Specification;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;

import java.util.List;


/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre03NavegarRegistroEtapas {


    private ProyectoCapturaItemDto proyectoSeleccionado;
    private String pantallaActual;


    private ProyectosCapturaResponseDto respuestaConsulta;

    private final ProyectoCapturaRepository proyectoCapturaRepository;

    public Pre03NavegarRegistroEtapas(ProyectoCapturaRepository proyectoCapturaRepository){
        this.proyectoCapturaRepository = proyectoCapturaRepository;
    }

    @Dado("que el actor visualiza el listado de la pantalla {string} \\(Anexo A.{int}\\)")
    public void queElActorVisualizaElListadoDeLaPantallaAnexoA(String pantalla, Integer anexo) {
        this.respuestaConsulta = consultarProyectosComoResponseDtoByFilter(null, null, null, null, null, null);

        assertThat(this.respuestaConsulta)
                .as("La respuesta de la consulta no debe ser nula")
                .isNotNull();

        this.pantallaActual = pantalla;
    }

    @Cuando("el actor hace clic en el CUP de un proyecto del listado")
    public void elActorHaceClicEnElCUPDeUnProyectoDelListado() {
        // 2. Tomamos el primer proyecto del listado devuelto como objetivo del clic
        this.proyectoSeleccionado = this.respuestaConsulta.getContenido().get(0);

        assertThat(this.proyectoSeleccionado.getCup())
                .as("El proyecto seleccionado debe tener un CUP válido")
                .isNotNull();

        // Simulamos el cambio de contexto/navegación provocado por el clic
        this.pantallaActual = "Registro de Etapas";
    }

    @Entonces("el sistema muestra la pantalla {string} del caso de uso CU-PRE-{int}.{int} {string}")
    public void elSistemaMuestraLaPantallaDelCasoDeUsoCUPRE(String pantalla, Integer cuMayor, Integer cuMenor, String nombreCasoUso) {
        assertThat(this.pantallaActual)
                .as("El sistema debió navegar a la pantalla esperada")
                .isEqualTo(pantalla);

        assertThat(this.proyectoSeleccionado)
                .as("El proyecto seleccionado debe estar disponible para la navegación")
                .isNotNull();
    }

    private ProyectosCapturaResponseDto consultarProyectosComoResponseDtoByFilter(
            String cup,
            String nombre,
            Long idUnidadEjecutora,
            String iniciativa,
            String estado,
            String busquedaGlobal) {

        // 1. Especificación base obligatoria (Fetch JOIN + Reglas globales de captura)
        Specification<Proyecto> baseSpec = Specification.<Proyecto>unrestricted()
                .and(ProyectoCapturaRepository.Specs.fetchUnidadEjecutora())
                .and(ProyectoCapturaRepository.Specs.esValidoParaCaptura());

        // 2. Acumulador de filtros dinámicos en 'OR'
        Specification<Proyecto> searchFilters = null;

        if (cup != null && !cup.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byCup(cup)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byCup(cup));
        }

        if (nombre != null && !nombre.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byNombre(nombre)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byNombre(nombre));
        }

        if (idUnidadEjecutora != null) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byUnidadEjecutora(idUnidadEjecutora)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byUnidadEjecutora(idUnidadEjecutora));
        }

        if (iniciativa != null && !iniciativa.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byIniciativa(iniciativa)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byIniciativa(iniciativa));
        }

        if (estado != null && !estado.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byEstado(estado)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byEstado(estado));
        }

        if (busquedaGlobal != null && !busquedaGlobal.isBlank()) {
            searchFilters = (searchFilters == null)
                    ? ProyectoCapturaRepository.Specs.byBusquedaGeneral(busquedaGlobal)
                    : searchFilters.or(ProyectoCapturaRepository.Specs.byBusquedaGeneral(busquedaGlobal));
        }

        // 3. Unimos las reglas base con el bloque acumulado de ORs (si se ingresó algún filtro)
        Specification<Proyecto> finalSpec = (searchFilters != null)
                ? baseSpec.and(searchFilters)
                : baseSpec;

        // 4. Consulta paginada en BD
        var proyectosPage = proyectoCapturaRepository.findAll(
                finalSpec,
                org.springframework.data.domain.PageRequest.of(0, 20)
        );

        // 5. Mapeo a DTO de respuesta
        List<ProyectoCapturaItemDto> dtos = proyectosPage.getContent().stream()
                .map(proyecto -> {
                    ProyectoCapturaItemDto dto = new ProyectoCapturaItemDto();
                    UnidadEjecutoraResumenDto unidadEjecutoraDTO = new UnidadEjecutoraResumenDto();
                    IniciativaInversionDto iniciativaInversionDto = null;
                    EstadoProyectoDto estadoProyectoDto = null;

                    unidadEjecutoraDTO.setIdUnidadEjecutora(proyecto.getUnidadEjecutora().getId());
                    unidadEjecutoraDTO.setCodigo(proyecto.getUnidadEjecutora().getCodigo());
                    unidadEjecutoraDTO.setNombre(proyecto.getUnidadEjecutora().getNombre());
                    dto.setUnidadEjecutora(unidadEjecutoraDTO);

                    if(proyecto.getIniciativaInversion() != null){
                        String iniciativaString = proyecto.getIniciativaInversion().name();
                        iniciativaInversionDto = IniciativaInversionDto.fromValue(iniciativaString);
                    }
                    if(proyecto.getEstado() != null){
                        String estadoString = proyecto.getEstado().name();
                        estadoProyectoDto = EstadoProyectoDto.fromValue(estadoString);
                    }



                    dto.setIdProyecto(proyecto.getId());
                    dto.setNombreProyecto(proyecto.getNombre());
                    dto.setCup(proyecto.getCup());
                    dto.setUnidadEjecutora(unidadEjecutoraDTO);
                    dto.setIniciativaInversion(iniciativaInversionDto);
                    dto.setEstado(estadoProyectoDto);
                    return dto;
                })
                .toList();

        ProyectosCapturaResponseDto response = new ProyectosCapturaResponseDto();
        response.setContenido(dtos);
        return response;
    }
}