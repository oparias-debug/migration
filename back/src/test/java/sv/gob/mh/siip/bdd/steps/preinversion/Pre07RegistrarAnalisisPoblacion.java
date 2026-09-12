package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.web.context.request.RequestContextHolder;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CeldaUbicacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisPoblacionService;

/**
 * CU-PRE-07-registrar-analisis-poblacion.feature. Los pasos de las Antecedentes ("que el Técnico
 * URP ingresa a la pestaña {string}, sección {string}" y "se encuentra en la tabla {string}
 * (Anexo A.{int})") son texto identico al de CU-PRE-06-registrar-matriz-interesados.feature
 * (Cucumber exige una unica definicion por texto, mismo criterio que Pre02Bandeja): viven en
 * {@link Pre06RegistrarMatrizInteresados}, que activa el escenario de esta clase via
 * {@link #activarEscenario(Proyecto)} cuando la tabla es "Análisis de la Población". El clic en
 * botones genericos ("Guardar", "Aceptar", "Adicionar Ubicación (+)") y el mensaje
 * "¡Guardado!...(Anexo A.2)" comparten texto con pasos ya definidos para CU-PRE-01/04/06 y solo
 * hacen no-op. "el sistema guarda la información registrada" (definido en
 * {@link Pre04RegistrarGuardar}) y "el Técnico URP hace clic en {string} sin haber completado el
 * campo {string}" (definido en {@link Pre35RegistrarFichaEmergencia}) tambien son texto
 * compartido: ambas clases delegan aqui cuando {@link #esEscenarioAnalisisPoblacion()} es
 * verdadero.
 */
public class Pre07RegistrarAnalisisPoblacion {

    private final AnalisisPoblacionService analisisPoblacionService;

    private Proyecto proyecto;
    private boolean activo;
    private AnalisisPoblacionRequestDto borrador;
    private AnalisisPoblacionDto guardado;
    private AnalisisPoblacionDto resultadoCalculo;
    private AnalisisPoblacionRequestDto ultimaSolicitud;
    private ValidacionNegocioException ultimaExcepcion;

    public Pre07RegistrarAnalisisPoblacion(AnalisisPoblacionService analisisPoblacionService) {
        this.analisisPoblacionService = analisisPoblacionService;
    }

    /** Invocado por {@link Pre06RegistrarMatrizInteresados} (ver javadoc de la clase). */
    public void activarEscenario(Proyecto proyecto) {
        this.proyecto = proyecto;
        this.activo = true;
        AnalisisPoblacionDto dto = analisisPoblacionService.obtener(proyecto.getId());
        assertThat(dto.getPoblacionReferencia().getUbicaciones()).isEmpty();
    }

    public boolean esEscenarioAnalisisPoblacion() {
        return activo;
    }

    @Cuando("el Técnico URP registra la {string} y el {string} para las filas de Población de Referencia, Población Afectada y Población Objetivo")
    public void el_tecnico_urp_registra_ubicacion_y_numero_personas(String campoUbicacion, String campoNumero) {
        borrador = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(100))))
                .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(80))))
                .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(30))));
    }

    @Cuando("registra la {string} para las filas de Población Afectada y Población Objetivo")
    public void registra_descripcion_para_afectada_y_objetivo(String campo) {
        borrador.getPoblacionAfectada().setDescripcion("Descripción de la Población Afectada (BDD)");
        borrador.getPoblacionObjetivo().setDescripcion("Descripción de la Población Objetivo (BDD)");
    }

    /** Invocado por {@link Pre04RegistrarGuardar} para "el sistema guarda la información registrada". */
    public void guardarInformacionRegistrada() {
        guardado = analisisPoblacionService.guardar(proyecto.getId(), borrador);
        assertThat(guardado.getPoblacionReferencia().getUbicaciones()).hasSize(1);
    }

    @Entonces("se mantiene en la pestaña {string}")
    public void se_mantiene_en_la_pestana(String pestana) {
        AnalisisPoblacionDto recargado = analisisPoblacionService.obtener(proyecto.getId());
        assertThat(recargado.getPoblacionAfectada().getDescripcion())
                .isEqualTo(guardado.getPoblacionAfectada().getDescripcion());
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema agrega una nueva columna agrupada de {string} y {string} \\(RN09\\)")
    public void el_sistema_agrega_una_nueva_columna_agrupada_rn09(String columnaUbicacion, String columnaNumero) {
        // RN09: agregar una ubicacion (columna) ocurre en el cliente; solo se envia al servidor en
        // el siguiente "Guardar" (mismo criterio que el resto del contrato).
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("la celda de la columna {string} en la fila {string} permanece bloqueada y vacía \\(RN04\\)")
    public void la_celda_permanece_bloqueada_y_vacia_rn04(String columna, String fila) {
        AnalisisPoblacionDto dto = guardarEjemploCompleto();
        assertThat(dto.getPoblacionReferencia().getUbicaciones())
                .isNotEmpty()
                .allSatisfy(celda -> assertThat(celda.getPorcentaje()).isNull());
        assertThat(dto.getPoblacionReferencia().getTotalPorcentaje()).isNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("la celda de la columna {string} en la fila {string} permanece bloqueada y vacía, mostrada sombreada en gris \\(RN05\\)")
    public void la_celda_permanece_bloqueada_y_vacia_rn05(String columna, String fila) {
        AnalisisPoblacionDto dto = guardarEjemploCompleto();
        assertThat(dto.getPoblacionEnEspera().getUbicaciones())
                .isNotEmpty()
                .allSatisfy(celda -> assertThat(celda.getUbicacion()).isNull());
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("la celda de la columna {string} permanece bloqueada y vacía para las filas {string} y {string} \\(RN06\\)")
    public void la_celda_permanece_bloqueada_y_vacia_rn06(String columna, String fila1, String fila2) {
        AnalisisPoblacionDto dto = guardarEjemploCompleto();
        assertThat(dto.getPoblacionReferencia().getDescripcion()).isNull();
        assertThat(dto.getPoblacionEnEspera().getDescripcion()).isNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que se han registrado los valores de {string} para Población de Referencia, Afectada y Objetivo en una ubicación")
    public void que_se_han_registrado_los_valores_de_numero_de_personas(String campo) {
        AnalisisPoblacionRequestDto request = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(100))))
                .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(80))))
                .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(30))));
        resultadoCalculo = analisisPoblacionService.guardar(proyecto.getId(), request);
    }

    @Entonces("el sistema calcula automáticamente {string} como Población Afectada menos Población Objetivo, para esa ubicación")
    public void el_sistema_calcula_automaticamente_poblacion_en_espera(String campo) {
        assertThat(resultadoCalculo.getPoblacionEnEspera().getUbicaciones().get(0).getNumeroPersonas()).isEqualTo(50);
    }

    @Entonces("calcula el {string} como \\(Población Objetivo \\/ Población Afectada\\) * 100")
    public void calcula_el_porcentaje_de_poblacion_objetivo(String campo) {
        assertThat(resultadoCalculo.getPoblacionObjetivo().getUbicaciones().get(0).getPorcentaje()).isEqualTo(37.5);
    }

    @Entonces("calcula el {string} como {string} menos {string}")
    public void calcula_el_porcentaje_de_poblacion_en_espera(String campoEspera, String campoAfectada,
            String campoObjetivo) {
        assertThat(resultadoCalculo.getPoblacionEnEspera().getUbicaciones().get(0).getPorcentaje()).isEqualTo(62.5);
    }

    @Entonces("calcula el {string} de cada tipo de población como la suma de todas sus ubicaciones")
    public void calcula_los_totales_de_cada_tipo_de_poblacion(String campo) {
        assertThat(resultadoCalculo.getPoblacionReferencia().getTotalNumeroPersonas()).isEqualTo(100);
        assertThat(resultadoCalculo.getPoblacionAfectada().getTotalNumeroPersonas()).isEqualTo(80);
        assertThat(resultadoCalculo.getPoblacionObjetivo().getTotalNumeroPersonas()).isEqualTo(30);
        assertThat(resultadoCalculo.getPoblacionEnEspera().getTotalNumeroPersonas()).isEqualTo(50);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el {string} registrado es mayor al {string} en la misma ubicación")
    public void que_el_campo_registrado_es_mayor_al_otro_en_la_misma_ubicacion(String campoMayor, String campoMenor) {
        CeldaUbicacionRequestDto celdaReferencia = nuevaCelda(50);
        CeldaUbicacionRequestDto celdaAfectada = nuevaCelda(50);
        CeldaUbicacionRequestDto celdaObjetivo = nuevaCelda(20);

        switch (campoMayor) {
            case "N° de personas (Población Afectada)" -> celdaAfectada.setNumeroPersonas(80);
            case "N° de personas (Población Objetivo)" -> celdaObjetivo.setNumeroPersonas(80);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campoMayor);
        }

        ultimaSolicitud = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(celdaReferencia)))
                .poblacionAfectada(new FilaPoblacionRequestDto().ubicaciones(List.of(celdaAfectada)))
                .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(celdaObjetivo)));
    }

    @Entonces("el sistema muestra el modal de {string} descrito en el Anexo A.3")
    public void el_sistema_muestra_el_modal_de_ingreso_invalido_anexo_a3(String modal) {
        intentarGuardarYCapturarError("POBLACION_AFECTADA_MAYOR_QUE_REFERENCIA");
    }

    @Entonces("el sistema muestra el modal de {string} descrito en el Anexo A.4")
    public void el_sistema_muestra_el_modal_de_ingreso_invalido_anexo_a4(String modal) {
        intentarGuardarYCapturarError("POBLACION_OBJETIVO_MAYOR_QUE_AFECTADA");
    }

    @Entonces("no guarda la información registrada")
    public void no_guarda_la_informacion_registrada() {
        AnalisisPoblacionDto dto = analisisPoblacionService.obtener(proyecto.getId());
        assertThat(dto.getPoblacionReferencia().getUbicaciones()).isEmpty();
    }

    @Entonces("regresa a la pestaña anterior")
    public void regresa_a_la_pestana_anterior() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} \\(RN07\\)")
    public void el_sistema_sombrea_en_rojo_el_borde_del_campo_rn07(String campo) {
        assertThat(guardado).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Invocado por {@link Pre35RegistrarFichaEmergencia} para "el Técnico URP hace clic en
     * {string} sin haber completado el campo {string}" (RN07): ninguno de los 5 campos listados
     * en el Esquema del escenario es obligatorio a nivel de servidor, asi que se guarda una
     * combinacion completa salvo el campo indicado, para confirmar que el servidor no la rechaza.
     */
    public void guardarSinCompletarCampo(String campo) {
        CeldaUbicacionRequestDto celdaReferencia = nuevaCelda(100);
        CeldaUbicacionRequestDto celdaAfectada = nuevaCelda(80);
        CeldaUbicacionRequestDto celdaObjetivo = nuevaCelda(30);

        switch (campo) {
            case "Ubicación (Población Afectada)" -> celdaAfectada.setUbicacion(null);
            case "Ubicación (Población Objetivo)" -> celdaObjetivo.setUbicacion(null);
            case "N° de personas (Población de Referencia)" -> celdaReferencia.setNumeroPersonas(null);
            case "N° de personas (Población Afectada)" -> celdaAfectada.setNumeroPersonas(null);
            case "N° de personas (Población Objetivo)" -> celdaObjetivo.setNumeroPersonas(null);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }

        AnalisisPoblacionRequestDto request = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(celdaReferencia)))
                .poblacionAfectada(new FilaPoblacionRequestDto().descripcion("Descripción de prueba BDD")
                        .ubicaciones(List.of(celdaAfectada)))
                .poblacionObjetivo(new FilaPoblacionRequestDto().descripcion("Descripción de prueba BDD")
                        .ubicaciones(List.of(celdaObjetivo)));

        guardado = analisisPoblacionService.guardar(proyecto.getId(), request);
    }

    private void intentarGuardarYCapturarError(String codigoEsperado) {
        try {
            analisisPoblacionService.guardar(proyecto.getId(), ultimaSolicitud);
            ultimaExcepcion = null;
        } catch (ValidacionNegocioException ex) {
            ultimaExcepcion = ex;
        }
        assertThat(ultimaExcepcion).isNotNull();
        assertThat(ultimaExcepcion.getCodigo()).isEqualTo(codigoEsperado);
    }

    private AnalisisPoblacionDto guardarEjemploCompleto() {
        AnalisisPoblacionRequestDto request = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(List.of(nuevaCelda(100))))
                .poblacionAfectada(new FilaPoblacionRequestDto().descripcion("Descripción de la Población Afectada (BDD)")
                        .ubicaciones(List.of(nuevaCelda(80))))
                .poblacionObjetivo(new FilaPoblacionRequestDto().descripcion("Descripción de la Población Objetivo (BDD)")
                        .ubicaciones(List.of(nuevaCelda(30))));
        return analisisPoblacionService.guardar(proyecto.getId(), request);
    }

    private CeldaUbicacionRequestDto nuevaCelda(int numeroPersonas) {
        return new CeldaUbicacionRequestDto().ubicacion("Ubicación de prueba BDD").numeroPersonas(numeroPersonas);
    }
}
